package com.diggit.android.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.diggit.android.Controller;
import com.diggit.android.ModelFactory;
import com.diggit.android.model.Person;
import com.diggit.android.model.ProfilePicture;

/**
 * Created by tokb on 30-03-2015.
 */
public class LaunchActivity extends Activity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      Controller.resetSavedInformation(this);

      final Person person = ModelFactory.getPerson(this);
      ProfilePicture profilePicture = ModelFactory.getProfilePicture(this);

      boolean needLogin = person == null || person.getBrugerid() == null;
      boolean needPicture = profilePicture == null || profilePicture.profilePicture == null;

      if (needLogin) {
         Controller.showLoginScreen(this);
      } else if (needPicture) {
         Controller.showSelectImageScreen(this);
      } else {
         Controller.showStudentCardScreen(this);
      }
   }
}
