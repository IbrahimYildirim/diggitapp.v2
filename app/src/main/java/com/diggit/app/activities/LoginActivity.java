package com.diggit.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.diggit.app.Controller;
import com.diggit.app.ModelFactory;
import com.diggit.app.R;

public class LoginActivity extends Activity {
   private static final String TAG = LoginActivity.class.getSimpleName();
   ProgressBar mProgressBar;

   /**
    * Called when the activity is first created.
    */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.login);

      mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

      final Button loginButton = (Button) findViewById(R.id.loginButton);
      loginButton.setOnClickListener(
            new View.OnClickListener() {
               @Override
               public void onClick(View clickedView) {
                  EditText userNameNox = (EditText) findViewById(R.id.username);
                  EditText passwordBox = (EditText) findViewById(R.id.password);

                  String brugerId = userNameNox.getText().toString();
                  String password = passwordBox.getText().toString();

                  if (brugerId.isEmpty() || password.isEmpty()) {
                     new AlertDialog.Builder(LoginActivity.this).
                           setTitle("Ups").
                           setMessage("Udfyldt venligst brugernavn og kode").
                           setPositiveButton(
                                 "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                 }).show();
                  } else {
                     mProgressBar.setVisibility(View.VISIBLE);
                     new PerformLoginTask().execute(brugerId, password);
                  }
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

            boolean needPicture = Controller.needPicture(LoginActivity.this);

            if (needPicture) {
               Controller.showSelectImageScreen(LoginActivity.this);
            } else {
               Controller.showStudentCardScreen(LoginActivity.this);
            }
         } else {
            Toast.makeText(
                  LoginActivity.this,
                  "Fejl ved login: " + loginResult.errorMessage,
                  Toast.LENGTH_SHORT).show();
         }
         mProgressBar.setVisibility(View.INVISIBLE);
      }
   }
}
