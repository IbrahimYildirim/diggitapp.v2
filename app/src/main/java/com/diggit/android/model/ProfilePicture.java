package com.diggit.android.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.diggit.android.ModelFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by TOKB on 16-03-2015.
 */
public class ProfilePicture implements ModelObject {
   public static final String KEY = "PROFILE_PICTURE";
   public String profilePicture;
   boolean hasBeenLocked;
   private final String TAG = ProfilePicture.class.getSimpleName();


   public ProfilePicture updateProfilePicture(JSONObject json) {
      try {
         profilePicture = getString(json,"profilePicture");
         int start = profilePicture.indexOf(",");
         if (start != -1) {
            profilePicture = profilePicture.substring(start);
         }
         Log.d(TAG, "Saving image: " + profilePicture);
         hasBeenLocked = json.getBoolean("hasBeenLocked");
      } catch (JSONException e) {
         throw new RuntimeException(e);
      }
      return this;
   }

   @Override
   public String getKey() {
      return KEY;
   }

   @Override
   public void save(Context context) {
      ModelFactory.save(context, this);
   }

   public void saveImage(Bitmap image, Context context) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      image.compress(Bitmap.CompressFormat.PNG, 100, baos);
      profilePicture = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
      save(context);
   }

   public Bitmap getImage() {
      byte[] decodedString = Base64.decode(profilePicture, Base64.DEFAULT);
      return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
   }

   private String getString(JSONObject jsonObject, String name) throws JSONException {
      String value = jsonObject.getString(name);
      if(value == null ||  value.equals("null")){
         return "";
      }
      return value;
   }
}
