package com.zzx.pluglib.utils;

import android.content.Context;

import java.io.File;

public class FileUtils {

   public static String getFileName(String pathandname) {
      int start=pathandname.lastIndexOf("/");
      int end=pathandname.lastIndexOf(".");
      if (start!=-1 && end!=-1)
         return pathandname.substring(start+1, end);

      else
         return null;
   }

   public static File getFilePath(Context context,String dirName) {
      File filesDir = context.getFilesDir();
      File dirFile = new File(filesDir + File.separator + dirName);
      if (!dirFile.exists()) {
         dirFile.mkdir();
      }
      return dirFile;
   }

   public static File getApkFilePath(Context context, String apkName) {
      File pluginDir = getFilePath(context, "pluginApk");
      File fileStreamPath = new File( pluginDir+ File.separator + apkName);
      return fileStreamPath;
   }

}
