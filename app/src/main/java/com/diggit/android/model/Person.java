package com.diggit.android.model;

import android.content.Context;
import android.util.Log;
import com.diggit.android.ModelFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.diggit.android.model.InternalStorage.*;

/**
 * Created by TOKB on 16-03-2015.
 */
public class Person implements Serializable, ModelObject {
   private String Navn;
   private String Brugerid;
   private String Fornavn;
   private String Efternavn;
   private String SkolekomNavn;
   private String Mailadresse;
   private String Instnr;
   private String Funktionsmarkering;
   private String Foedselsdag;
   private Date creationTime;
   public static final String KEY = "PERSON";


   @Override
   public String getKey() {
      return KEY;
   }

   public void save(Context context) {
      ModelFactory.save(context,this);

   }

   public Person updatePerson(JSONObject jsonObject) {
      try {
         Navn = jsonObject.getString("Navn");
         Brugerid = jsonObject.getString("Brugerid");
         Fornavn = jsonObject.getString("Fornavn");
         Efternavn = jsonObject.getString("Efternavn");
         SkolekomNavn = jsonObject.getString("SkolekomNavn");
         Mailadresse = jsonObject.getString("Mailadresse");
         Instnr = jsonObject.getString("Instnr");
         Funktionsmarkering = jsonObject.getString("Funktionsmarkering");
         Foedselsdag = jsonObject.getString("Foedselsdag");

         creationTime = new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("creationTime"));
      } catch (JSONException e) {
         throw new RuntimeException(e);
      } catch (ParseException e) {
         throw new RuntimeException(e);
      }
      return this;
   }

   public String getNavn() {
      return Navn;
   }

   public String getBrugerid() {
      return Brugerid;
   }

   public String getFornavn() {
      return Fornavn;
   }

   public String getEfternavn() {
      return Efternavn;
   }

   public String getSkolekomNavn() {
      return SkolekomNavn;
   }

   public String getMailadresse() {
      return Mailadresse;
   }

   public String getInstnr() {
      return Instnr;
   }

   public String getFunktionsmarkering() {
      return Funktionsmarkering;
   }

   public String getFoedselsdag() {
      return Foedselsdag;
   }

   public Date getCreationTime() {
      return creationTime;
   }
}
