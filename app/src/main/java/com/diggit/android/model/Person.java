package com.diggit.android.model;

import android.content.Context;
import com.diggit.android.ModelFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TOKB on 16-03-2015.
 */
public class Person implements Serializable, ModelObject {
   private String Navn;
   private String Brugerid;
   private String Instnr;
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
         Navn = getString(jsonObject, "Navn");
         Brugerid = getString(jsonObject,"Brugerid");
         Instnr = getString(jsonObject, "Instnr");
         Foedselsdag = getString(jsonObject, "Foedselsdag");

         creationTime = new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("creationTime"));
      } catch (JSONException e) {
         throw new RuntimeException(e);
      } catch (ParseException e) {
         throw new RuntimeException(e);
      }
      return this;
   }

   private String getString(JSONObject jsonObject, String name) throws JSONException {
      String value = jsonObject.getString(name);
      if(value == null ||  value.equals("null")){
         return "";
      }
      return value;
   }

   public String getNavn() {
      return Navn;
   }

   public String getBrugerid() {
      return Brugerid;
   }

   public String getInstnr() {
      return Instnr;
   }

   public String getFoedselsdag() {
      return Foedselsdag;
   }

   public Date getCreationTime() {
      return creationTime;
   }
}
