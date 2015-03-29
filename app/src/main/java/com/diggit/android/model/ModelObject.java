package com.diggit.android.model;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by TOKB on 24-03-2015.
 */
public interface ModelObject extends Serializable{
   String getKey();
   void save(Context context);
}
