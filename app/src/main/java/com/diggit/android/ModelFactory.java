package com.diggit.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import com.diggit.android.model.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.diggit.android.model.InternalStorage.writeObject;

/**
 * Created by TOKB on 13-03-2015.
 */
public class ModelFactory {

   private static final String TAG = "ModelFactory";

   private static String WS_PASSWORD = "IMcOYZnhmCSTBnGqt9MLgR0WkE8=";
   private static String PROJEKT = "diggit";
   private static String KONTEKST = "A03855";
   private static String WS_ID = "wsdiggitap";

   private static String hmacSha1(String value, String key) {
      try {
         String type = "HmacSHA1";
         SecretKeySpec secret = null;
         Mac mac;
         byte[] keyBytes = key.getBytes("UTF-8");
         System.out.println("Key bytes -> "+ Arrays.toString(keyBytes));
         secret = new SecretKeySpec(keyBytes, type);
         mac = Mac.getInstance(type);
         mac.init(secret);

         byte[] bytes = mac.doFinal(value.getBytes("UTF-8"));
         System.out.println("final bytes -> " + Arrays.toString(bytes));
         return bytesToHex(bytes);
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      } catch (InvalidKeyException e) {
         throw new RuntimeException(e);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }
   }

   public static String bytesToHex(byte[] raw) {
      final char[] kDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f' };

      int length = raw.length;
      char[] hex = new char[length * 2];
      for (int i = 0; i < length; i++) {
         int value = (raw[i] + 256) % 256;
         int highIndex = value >> 4;
         int lowIndex = value & 0x0f;
         hex[i * 2 + 0] = kDigits[highIndex];
         hex[i * 2 + 1] = kDigits[lowIndex];
      }
      return String.valueOf(hex);
   }


   private static String SHA1(String text) {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         md.update(text.getBytes("iso-8859-1"), 0, text.length());
         return Base64.encodeToString(md.digest(), Base64.DEFAULT);
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }
   }

   public static void delete(Context context, String key) {
      try {
         InternalStorage.writeObject(context,key,null);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

   public static class LoginResult {
      boolean wasSuccessful;
      String errorMessage;

      public LoginResult(boolean wasSuccessful, String errorMessage) {
         this.wasSuccessful = wasSuccessful;
         this.errorMessage = errorMessage;
      }
   }

   static LoginResult postToUNILogin(Context context, String brugerId, String password) {


      String utcTime = String.valueOf(new Date().getTime() / 1000);

      String m0 = utcTime + KONTEKST + PROJEKT + brugerId;
      String k0 = (WS_PASSWORD.concat(ModelFactory.SHA1(password))).replace("\n","").replace("\r","");

      String auth = hmacSha1(m0, k0);

      List<NameValuePair> pairs = new ArrayList<NameValuePair>();
      pairs.add(new BasicNameValuePair("wsBrugerid", WS_ID));
      pairs.add(new BasicNameValuePair("UTCtime", utcTime));
      pairs.add(new BasicNameValuePair("kontekst", KONTEKST));
      pairs.add(new BasicNameValuePair("projekt", PROJEKT));
      pairs.add(new BasicNameValuePair("BrugerId", brugerId));
      pairs.add(new BasicNameValuePair("Auth", auth));

      for (NameValuePair pair : pairs) {
         Log.i("VALUE",pair.getName()+" -> "+pair.getValue());
      }


      try {
         JSONObject jsonObject = post(pairs, "https://auth.emu.dk/mauth/");

         int valid = jsonObject.getInt("VALID");
         String digest = jsonObject.getString("DIGEST");
         boolean hasError = jsonObject.has("error");
         if(hasError){
            String error = jsonObject.getString("error");
            return new LoginResult(false,error);
         }
         JSONObject result = ModelFactory.postToOwnServer(brugerId, digest, valid, auth);
         hasError = result.has("error");
         if (hasError) {
            String error = jsonObject.getString("error");
            return new LoginResult(false,error);
         } else {
            getPerson(context).updatePerson(result.getJSONObject("person")).save(context);
            getInstitution(context).updateInstitution(result.getJSONObject("institution")).save(context);
            getProfilePicture(context).updateProfilePicture(result.getJSONObject("profilePicture")).save(context);
            return new LoginResult(true,"Alt OK");
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   static JSONObject postToOwnServer(
         String brugerId,
         String digest,
         int valid,
         String auth) throws IOException, JSONException {

      String deviceName = android.os.Build.MODEL;

      List<NameValuePair> pairs = new ArrayList<NameValuePair>();
      pairs.add(new BasicNameValuePair("brugerId", brugerId));
      pairs.add(new BasicNameValuePair("auth", auth));
      pairs.add(new BasicNameValuePair("digest", digest));
      pairs.add(new BasicNameValuePair("validity", String.valueOf(valid)));
      pairs.add(new BasicNameValuePair("deviceName", deviceName));

      return post(pairs, "http://diggitapp.dk/php/unilogin-v2.php");
   }



   private static JSONObject getJSONObjectFromResponse(HttpResponse response) throws IOException, JSONException {
      Reader in = new BufferedReader(
            new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
      StringBuilder builder = new StringBuilder();
      char[] buf = new char[1000];
      int l = 0;
      while (l >= 0) {
         builder.append(buf, 0, l);
         l = in.read(buf);
      }
      Log.i("JSON", builder.toString());
      return new JSONObject(builder.toString());
   }

   public static boolean sendPicture(Bitmap image) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      image.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
      byte[] byteArray = byteArrayOutputStream .toByteArray();

      String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

      ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
      data.add(new BasicNameValuePair("brugerId", "toke0969"));
      data.add(new BasicNameValuePair("imageBase64",encodedImage));
      try {
         JSONObject result = post(data, "http://diggitapp.dk/php/saveimage-v2.php");
         boolean status = result.getBoolean("status");
         return status;
      } catch (IOException e) {
         throw new RuntimeException(e);
      } catch (JSONException e) {
         throw new RuntimeException(e);
      }
   }


   public static boolean isValid() {
      ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
      params.add(new BasicNameValuePair("brugerId","magn3155"));
      params.add(new BasicNameValuePair("institutionId","751402"));

      DefaultHttpClient httpClient = new DefaultHttpClient();
      String uri = "http://diggitapp.dk/php/isvalid.php?" + URLEncodedUtils.format(params, "utf-8");
      HttpGet request = new HttpGet(uri);
      Log.i("GET",uri);
      try {
         JSONObject response = getJSONObjectFromResponse(httpClient.execute(request));
         return response.getBoolean("isValid");
      } catch (IOException e) {
         throw new RuntimeException(e);
      } catch (JSONException e) {
         throw new RuntimeException(e);
      }
   }

   private static JSONObject post(List<NameValuePair> data, String uri) throws IOException, JSONException {
      Log.i("POST", data.toString());

      HttpPost post = new HttpPost(uri);
      post.setEntity(new UrlEncodedFormEntity(data));
      post.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));

      HttpClient httpclient = new DefaultHttpClient();
      return getJSONObjectFromResponse(httpclient.execute(post));
   }

   public static Person getPerson(Context applicationContext) {
      Person person = null;
      try {
         person = (Person) InternalStorage.readObject(applicationContext, Person.KEY);
      } catch (Exception e) {
         Log.e(TAG, e.getMessage());
      }
      if(person == null){
         person = new Person();
      }
      return person;
   }

   public static Institution getInstitution(Context applicationContext) {
      Institution insitution = null;
      try {
         insitution = (Institution) InternalStorage.readObject(applicationContext, Institution.KEY);
      } catch (Exception e) {
         Log.e(TAG, e.getMessage());
      }
      if(insitution == null){
         insitution = new Institution();
      }
      return insitution;
   }

   public static void save(Context context, ModelObject modelObject) {
      try {
         // Save the list of entries to internal storage
         writeObject(context, modelObject.getKey(), modelObject);
      } catch (IOException e) {
         Log.e(TAG, e.getMessage());
      }
   }

   public static ProfilePicture getProfilePicture(Context context) {
      ProfilePicture profilePicture = null;
      try {
         profilePicture = (ProfilePicture) InternalStorage.readObject(context, ProfilePicture.KEY);
      } catch (Exception e) {
         Log.e(TAG, e.getMessage());
      }
      if(profilePicture == null){
         profilePicture = new ProfilePicture();
      }
      return profilePicture;
   }
}
