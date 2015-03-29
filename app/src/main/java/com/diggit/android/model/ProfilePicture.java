package com.diggit.android.model;

import android.content.Context;
import com.diggit.android.ModelFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TOKB on 16-03-2015.
 */
public class ProfilePicture implements ModelObject{
   public static final String KEY = "PROFILE_PICTURE";
   String profilePicture;
   boolean hasBeenLocked;


   public ProfilePicture updateProfilePicture(JSONObject json) {
      try {
         profilePicture = json.getString("profilePicture");
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
      ModelFactory.save(context,this);
   }
}
