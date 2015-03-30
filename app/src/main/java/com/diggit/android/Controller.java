package com.diggit.android;

import android.content.Context;
import android.content.Intent;
import com.diggit.android.activities.BowntyActivity;
import com.diggit.android.activities.LoginActivity;
import com.diggit.android.activities.SelectImageActivity;
import com.diggit.android.activities.StudentCardActivity;
import com.diggit.android.model.Institution;
import com.diggit.android.model.Person;
import com.diggit.android.model.ProfilePicture;

import java.io.File;

/**
 * Created by tokb on 30-03-2015.
 */
public class Controller  {

   public static void showLoginScreen(Context context){
      context.startActivity(new Intent(context, LoginActivity.class));
   }

   public static void showSelectImageScreen(Context context){
      context.startActivity(new Intent(context, SelectImageActivity.class));
   }

   public static void showStudentCardScreen(Context context){
      context.startActivity(new Intent(context, StudentCardActivity.class));
   }

   public static void showBowntyScreen(Context context){
      context.startActivity(new Intent(context, BowntyActivity.class));
   }

   public static void resetSavedInformation(Context context){
      ModelFactory.delete(context, Person.KEY);
      ModelFactory.delete(context, Institution.KEY);
      ModelFactory.delete(context, ProfilePicture.KEY);
      File file = new File(SelectImageActivity.SELFIE_PATH);
      if (file.exists()) {
         file.delete();
      }
      Controller.showStudentCardScreen(context);

   }
}
