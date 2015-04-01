package com.diggit.app.model;

import android.content.Context;
import com.diggit.app.ModelFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TOKB on 16-03-2015.
 */
public class Institution implements ModelObject{
   public static final String KEY = "INSTITUTION";

   private String Navn;
   private String Adresse;
   private String Bynavn;
   private Integer Postnr;
   private Integer Telefonnr;
   private String Mailadresse;
   private String Kommune;
   private boolean subscribingInstitution;
   private String cvr;

   public Institution() {

   }
   public Institution updateInstitution(JSONObject json) {
      try {
         Navn = json.getString("Navn");
         Adresse = json.getString("Adresse");
         Bynavn = json.getString("Bynavn");
         Postnr = json.getInt("Postnr");
         Telefonnr = json.getInt("Telefonnr");
         Mailadresse = json.getString("Mailadresse");
         Kommune = json.getString("Kommune");
         subscribingInstitution = json.getBoolean("subscribingInstitution");
         cvr = json.getString("cvr");
      } catch (JSONException e) {
         throw new RuntimeException(e);
      }
      return this;
   }

   public String getNavn() {
      return Navn;
   }

   public String getAdresse() {
      return Adresse;
   }

   public String getBynavn() {
      return Bynavn;
   }

   public Integer getPostnr() {
      return Postnr;
   }

   public Integer getTelefonnr() {
      return Telefonnr;
   }

   public String getMailadresse() {
      return Mailadresse;
   }

   public String getKommune() {
      return Kommune;
   }

   public boolean isSubscribingInstitution() {
      return subscribingInstitution;
   }

   public String getCvr() {
      return cvr;
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
