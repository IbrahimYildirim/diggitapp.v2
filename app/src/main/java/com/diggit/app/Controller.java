package com.diggit.app;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.diggit.app.activities.BowntyActivity;
import com.diggit.app.activities.LoginActivity;
import com.diggit.app.activities.SelectImageActivity;
import com.diggit.app.activities.StudentCardActivity;
import com.diggit.app.model.Institution;
import com.diggit.app.model.Person;
import com.diggit.app.model.ProfilePicture;

import java.io.File;

/**
 * Created by tokb on 30-03-2015.
 */
public class Controller {

   public static void showLoginScreen(Context context) {
      context.startActivity(new Intent(context, LoginActivity.class));
   }

   public static void showSelectImageScreen(Context context) {
      context.startActivity(new Intent(context, SelectImageActivity.class));
   }

   public static void showStudentCardScreen(Context context) {
      context.startActivity(new Intent(context, StudentCardActivity.class));
   }

   public static void showBowntyScreen(Context context) {
      context.startActivity(new Intent(context, BowntyActivity.class));
   }

   public static void resetSavedInformation(Context context) {
      ModelFactory.delete(context, Person.KEY);
      ModelFactory.delete(context, Institution.KEY);
      ModelFactory.delete(context, ProfilePicture.KEY);
      File file = new File(SelectImageActivity.SELFIE_PATH);
      if (file.exists()) {
         file.delete();
      }
      Controller.showLoginScreen(context);
   }

   public static void fadeViewin(View v, int delay) {
      if (v.getVisibility() == View.INVISIBLE) {
         v.setVisibility(View.VISIBLE);
      }
      ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(v, "alpha", 0.0f, 1.0f);
      fadeAnim.setDuration(400);
      fadeAnim.setInterpolator(new LinearInterpolator());
      fadeAnim.setStartDelay(delay);
      fadeAnim.start();
   }

   public static void fadeViewOut(final View v, int delay) {
      ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(v, "alpha", 0.0f);
      fadeAnim.setDuration(400);
      fadeAnim.setInterpolator(new LinearInterpolator());
      fadeAnim.setStartDelay(delay);
      fadeAnim.addListener(
            new Animator.AnimatorListener() {
               @Override
               public void onAnimationStart(Animator animator) {
               }

               @Override
               public void onAnimationEnd(Animator animator) {
                  v.setVisibility(View.INVISIBLE);
               }

               @Override
               public void onAnimationCancel(Animator animator) {
               }

               @Override
               public void onAnimationRepeat(Animator animator) {
               }
            });
      fadeAnim.start();
   }

   public static boolean needPicture(Context context) {
      ProfilePicture profilePicture = ModelFactory.getProfilePicture(context);
      return profilePicture == null || profilePicture.profilePicture == null || profilePicture.profilePicture.trim().isEmpty();
   }

   public static boolean needLogin(Context context) {
      final Person person = ModelFactory.getPerson(context);
      return person == null || person.getBrugerid() == null;
   }
}
