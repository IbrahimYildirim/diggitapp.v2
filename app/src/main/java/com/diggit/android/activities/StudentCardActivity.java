package com.diggit.android.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.diggit.android.Controller;
import com.diggit.android.ModelFactory;
import com.diggit.android.R;
import com.diggit.android.model.ProfilePicture;


/**
 * Created by tokb on 30-03-2015.
 */
public class StudentCardActivity extends Activity {
   private static final String TAG = StudentCardActivity.class.getSimpleName();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);


      //here,we are making a folder named picFolder to store pics taken by the camera using this application

      setContentView(R.layout.studentcard);


      ProfilePicture profilePicture = ModelFactory.getProfilePicture(getApplicationContext());
      ImageView imageView = (ImageView) findViewById(R.id.imageView);
      imageView.setImageBitmap(profilePicture.getImage());


      Button isValidButton = (Button) findViewById(R.id.isActive);
      isValidButton.setOnClickListener(
            new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  new IsValidTask().execute();
               }
            });

      //Set icons for button - Font Awesome
      Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");


      Button btnNextPage = (Button) findViewById(R.id.btn_back);
      btnNextPage.setTypeface(tf);

      Button btnDeals = (Button) findViewById(R.id.btn_deals);
      btnDeals.setTypeface(tf);
      btnDeals.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                        Controller.showBowntyScreen(StudentCardActivity.this);
                                     }
                                  });
   }

   private class IsValidTask extends AsyncTask<Void,Void,Boolean> {
      @Override
      protected Boolean doInBackground(Void... params) {
         return ModelFactory.isValid(StudentCardActivity.this);
      }

      @Override
      protected void onPostExecute(Boolean isValid) {
         super.onPostExecute(isValid);

         Button isValidButton = (Button) findViewById(R.id.isActive);
         isValidButton.setText("Valid: " + isValid);
      }
   }


}
