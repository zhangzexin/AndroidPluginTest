package com.zzx.plugextend;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.lang.reflect.Field;

public class PlugBaseActivity extends AppCompatActivity {
   protected Context mContext = null;
   protected Boolean pluginInHostRunning;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Class hostAppClazz = null;
      try {
         hostAppClazz = Class.forName("com.zzx.hooktest.MyAppAplication");
      } catch (Throwable e) {
      }
      pluginInHostRunning = hostAppClazz != null;
      if (pluginInHostRunning) {
         injectPlugResource();
      }
   }

   private void injectPlugResource() {
      String plugName = "pluginapk-debug.apk";
      Resources resource = PluginResourceUtils.getResource(getApplicationContext(), plugName);
      if (resource == null) {
         throw new NullPointerException("plug resouce is null");
      }
      mContext = new ContextThemeWrapper(getBaseContext(), 0);
      Class<? extends Context> contextWrapperClass = mContext.getClass();
      try {
         Field mResourcesField = contextWrapperClass.getDeclaredField("mResources");
         mResourcesField.setAccessible(true);
         mResourcesField.set(mContext,resource);
         Class rClazz = Class.forName("com.google.android.material.R$style");
         Field themeField = rClazz.getDeclaredField("Theme_MaterialComponents_DayNight");
         themeField.setAccessible(true);
         // release 编译时, 需要在 在gradle.properties中加入 android.enableR8.fullMode=false
         Object themeObj = themeField.get(null);
         if (themeObj != null) {
            int theme = (int)themeObj;
            Field mThemeResourceField = contextWrapperClass.getDeclaredField("mThemeResource");
            mThemeResourceField.setAccessible(true);
            mThemeResourceField.set(mContext,theme);
         }
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }


   }
}
