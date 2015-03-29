package com.diggit.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.diggit.android.model.Institution;
import com.diggit.android.model.Person;
import com.diggit.android.model.ProfilePicture;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {
   private static final String TAG = "Main";
   int TAKE_PHOTO_CODE = 0;
   private String picturePath;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);

      //here,we are making a folder named picFolder to store pics taken by the camera using this application
      picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/selfie.jpg";

      renderActivity();
   }

   private void renderActivity() {
      final Person person = ModelFactory.getPerson(getApplicationContext());
      Institution institution = ModelFactory.getInstitution(getApplicationContext());
      ProfilePicture profilePicture = ModelFactory.getProfilePicture(getApplicationContext());

      boolean userHasLoggedIn = person.getBrugerid() != null;
      if (userHasLoggedIn) {
         renderStudentCard();
      } else {
         renderLogin();
      }
   }

   private void renderStudentCard() {
      setContentView(R.layout.studentcard);

      updateImageView();

      { //TODO Delete after testing
         final Button resetButton = (Button) findViewById(R.id.reset);
         resetButton.setOnClickListener(
               new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                     ModelFactory.delete(getApplicationContext(), Person.KEY);
                     ModelFactory.delete(getApplicationContext(), Institution.KEY);
                     ModelFactory.delete(getApplicationContext(), ProfilePicture.KEY);
                     File file = new File(picturePath);
                     if (file.exists()) {
                        file.delete();
                     }
                     renderActivity();
                  }
               });
      }

      Button takePicture = (Button) findViewById(R.id.takePicture);
      takePicture.setOnClickListener(
            new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  // here,counter will be incremented each time,and the picture taken by camera will be stored as 1.jpg,2.jpg and likewise.
                  File file = new File(picturePath);
                  if(file.exists()){
                     file.delete();
                     file = new File(picturePath);
                  }
                  Uri outputFileUri = Uri.fromFile(file);

                  Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                  cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                  startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
               }
            });

       Button isValidButton = (Button) findViewById(R.id.isActive);
       isValidButton.setOnClickListener(
               new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       boolean isValid = ModelFactory.isValid();
                       Button isValidButton = (Button) findViewById(R.id.isActive);
                       isValidButton.setText("Valid: " + isValid);
                   }
               });

       //Set icons for button - Font Awesome
       Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
       takePicture.setTypeface(tf);

       Button btnNextPage = (Button) findViewById(R.id.btn_back);
       btnNextPage.setTypeface(tf);

       Button btnDeals = (Button) findViewById(R.id.btn_deals);
       btnDeals.setTypeface(tf);

   }

   private void renderLogin() {
      setContentView(R.layout.login);


      final Button loginButton = (Button) findViewById(R.id.loginButton);
      loginButton.setOnClickListener(
            new View.OnClickListener() {
               @Override
               public void onClick(View clickedView) {
                  EditText userName = (EditText) findViewById(R.id.username);
                  EditText password = (EditText) findViewById(R.id.password);

                  String brugerId = userName.getText().toString();
                  ModelFactory.LoginResult loginResult = ModelFactory.postToUNILogin(
                        getApplicationContext(),
                        brugerId,
                        password.getText().toString());
                  if (loginResult.wasSuccessful) {
                     renderActivity();
                  } else {
                     TextView loginResultView = (TextView) findViewById(R.id.loginButtonResult);
                     loginResultView.setText("Fejl ved login: " + loginResult.errorMessage);
                  }
               }
            });

       //Set custom font to "Diggit"R Text
       String fontPath = "fonts/timeburner_regular.ttf";
       TextView lblDiggit = (TextView)findViewById(R.id.lblDiggitLogin);
       Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
       lblDiggit.setTypeface(tf);


   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
         Log.d("CameraDemo", "Pic saved");

         updateImageView();
      }
   }

   private void updateImageView() {
      File file = new File(picturePath);
      if (!file.exists()) {
         Log.i(TAG,"Image file was not found.");
         return;
      }
      Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
      ExifInterface ei = null;
      try {
         ei = new ExifInterface(picturePath);
      } catch (IOException e) {

         int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
         switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
               RotateBitmap(bitmap, 90);
               break;
            case ExifInterface.ORIENTATION_ROTATE_180:
               RotateBitmap(bitmap, 180);
               break;
            // etc.
         }
         throw new RuntimeException(e);
      }


      ImageView imageView = (ImageView) findViewById(R.id.imageView);
      imageView.setImageBitmap(bitmap);
   }

   public static Bitmap RotateBitmap(Bitmap source, float angle) {
      Matrix matrix = new Matrix();
      matrix.postRotate(angle);
      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
   }
}
