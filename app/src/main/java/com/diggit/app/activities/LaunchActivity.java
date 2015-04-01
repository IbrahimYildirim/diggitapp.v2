package com.diggit.app.activities;

import android.app.Activity;
import android.os.Bundle;
import com.diggit.app.Controller;

/**
 * Created by tokb on 30-03-2015.
 */
public class LaunchActivity extends Activity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

//      Controller.resetSavedInformation(this);


      boolean needLogin = Controller.needLogin(this);
      boolean needPicture = Controller.needPicture(this);

      if (needLogin) {
         Controller.showLoginScreen(this);
      } else if (needPicture) {
         Controller.showSelectImageScreen(this);
      } else {
         Controller.showStudentCardScreen(this);
      }
   }
}
