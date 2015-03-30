package com.diggit.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.diggit.android.Controller;
import com.diggit.android.ModelFactory;
import com.diggit.android.R;
import com.diggit.android.model.Institution;
import com.diggit.android.model.Person;
import com.diggit.android.model.ProfilePicture;

import java.io.File;
import java.io.IOException;

public class LoginActivity extends Activity {
   private static final String TAG = LoginActivity.class.getSimpleName();


   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.login);

      final Button loginButton = (Button) findViewById(R.id.loginButton);
      loginButton.setOnClickListener(
            new View.OnClickListener() {
               @Override
               public void onClick(View clickedView) {
                  EditText userNameNox = (EditText) findViewById(R.id.username);
                  EditText passwordBox = (EditText) findViewById(R.id.password);

                  String brugerId = userNameNox.getText().toString();
                  String password = passwordBox.getText().toString();

                  new PerformLoginTask().execute(brugerId, password);
               }
            });

      //Set custom font to "Diggit"R Text
      String fontPath = "fonts/timeburner_regular.ttf";
      TextView lblDiggit = (TextView) findViewById(R.id.lblDiggitLogin);
      Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
      lblDiggit.setTypeface(tf);
   }

   private class PerformLoginTask extends AsyncTask<String, Void, ModelFactory.LoginResult> {
      @Override
      protected ModelFactory.LoginResult doInBackground(String[] params) {
         return ModelFactory.postToUNILogin(
               getApplicationContext(),
               params[0],
               params[1]);
      }

      @Override
      protected void onPostExecute(ModelFactory.LoginResult loginResult) {
         if (loginResult.wasSuccessful) {
            Controller.showSelectImageScreen(LoginActivity.this);
         } else {
            TextView loginResultView = (TextView) findViewById(R.id.loginButtonResult);
            loginResultView.setText("Fejl ved login: " + loginResult.errorMessage);
         }
      }
   }
}
