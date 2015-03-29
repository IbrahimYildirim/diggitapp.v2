package com.diggit.android.model;

import android.content.Context;
import com.diggit.android.ModelFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TOKB on 16-03-2015.
 */
public class Institution implements ModelObject{
   public static final String KEY = "INSTITUTION";

   private Integer Instnr;
   private String Navn;
   private Integer Type;
   private String Typenavn;
   private String Adresse;
   private String Bynavn;
   private Integer Postnr;
   private Integer Telefonnr;
   private String Faxnr;
   private String Mailadresse;
   private String Www;
   private Integer Kommunenr;
   private String Kommune;
   private String Admkommunenr;
   private String Admkommune;
   private Integer Regionsnr;
   private String Region;
   private boolean subscribingInstitution;
   private String cvr;

   public Institution() {

   }
   public Institution updateInstitution(JSONObject json) {
      try {
         Instnr = json.getInt("Instnr");
         Navn = json.getString("Navn");
         Type = json.getInt("Type");
         Typenavn = json.getString("Typenavn");
         Adresse = json.getString("Adresse");
         Bynavn = json.getString("Bynavn");
         Postnr = json.getInt("Postnr");
         Telefonnr = json.getInt("Telefonnr");
         Faxnr = json.getString("Faxnr");
         Mailadresse = json.getString("Mailadresse");
         Www = json.getString("Www");
         Kommunenr = json.getInt("Kommunenr");
         Kommune = json.getString("Kommune");
         Admkommunenr = json.getString("Admkommunenr");
         Admkommune = json.getString("Admkommune");
         Regionsnr = json.getInt("Regionsnr");
         Region = json.getString("Region");
         subscribingInstitution = json.getBoolean("subscribingInstitution");
         cvr = json.getString("cvr");
      } catch (JSONException e) {
         throw new RuntimeException(e);
      }
      return this;
   }

   public Integer getInstnr() {
      return Instnr;
   }

   public String getNavn() {
      return Navn;
   }

   public Integer getType() {
      return Type;
   }

   public String getTypenavn() {
      return Typenavn;
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

   public String getFaxnr() {
      return Faxnr;
   }

   public String getMailadresse() {
      return Mailadresse;
   }

   public String getWww() {
      return Www;
   }

   public Integer getKommunenr() {
      return Kommunenr;
   }

   public String getKommune() {
      return Kommune;
   }

   public String getAdmkommunenr() {
      return Admkommunenr;
   }

   public String getAdmkommune() {
      return Admkommune;
   }

   public Integer getRegionsnr() {
      return Regionsnr;
   }

   public String getRegion() {
      return Region;
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
