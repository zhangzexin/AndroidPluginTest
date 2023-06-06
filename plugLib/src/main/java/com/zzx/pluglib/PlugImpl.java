package com.zzx.pluglib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import com.zzx.pluglib.enums.HookActivityMode;
import com.zzx.pluglib.mode.ApkLoadInfo;
import com.zzx.pluglib.utils.PluginUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlugImpl {

   static ExecutorService mSingleThreadExecutor= Executors.newSingleThreadExecutor();


   public static void init(Context context, HookActivityMode mode) {
      switch (mode) {
         case HandlerMode:
            break;
         case InstrumentationMode:
         default:
            HookInstarumentation.hookInstrumentation(context);
            break;
      }
      mSingleThreadExecutor.execute(new Runnable() {
         @Override
         public void run() {
            initLoadAssetsApk(context);
         }
      });
   }

   /**
    * 用于加载Assets下的apk资源
    * @param context
    */
   private static void initLoadAssetsApk(Context context) {
      try {
         String[] apks = context.getAssets().list("apk");
         ArrayList<ApkLoadInfo> unOptApks = new ArrayList<>();
         ArrayList<ApkLoadInfo> optApks = new ArrayList<>();
         for (String apk : apks) {
            InputStream fileInputStream = context.getAssets().open("apk/" + apk);
            ApkLoadInfo apkLoadInfo = loadAssetsApk(context, fileInputStream, apk);
            if (apkLoadInfo != null) {
               if (apkLoadInfo.isFirstLoad) {
                  unOptApks.add(apkLoadInfo);
               } else {
                  optApks.add(apkLoadInfo);
               }
            }
         }
         loadOptPluginApk(context,optApks);
         loadUnOptPluginApk(context,unOptApks);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static void loadOptPluginApk(Context context, ArrayList<ApkLoadInfo> optApks) {
      if (optApks != null && !optApks.isEmpty()) {
         PlugLoadClassLoaderHelper.attachOptPluginClass(context, optApks);
      }
   }

   private static void loadUnOptPluginApk(Context context, ArrayList<ApkLoadInfo> unOptApks) {
      if (unOptApks != null && !unOptApks.isEmpty()) {
         PlugLoadClassLoaderHelper.attachUnOptPluginClass(context, unOptApks);
      }
   }

   private static ApkLoadInfo loadAssetsApk(Context context, InputStream fileInputStream, String apkName) {

      return PluginUtils.copyToFiles(context, fileInputStream,apkName);
   }


//   private static synchronized void loadPluginApk(Context context, Boolean firstMode) {
//
//      File fileplugPath = context.getFileStreamPath(FILE_PLUG_APK);
//      File filepluDexPath = context.getFileStreamPath(FILE_PLUG_DEX);
//      if (firstMode) {
//
//      } else {
//         PlugLoadClassLoaderHelper.patchClassLoader(context.getClassLoader(), context, filepluDexPath,"");
//      }
//   }
}
