package com.zzx.pluglib.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.FileUtils;

import com.zzx.pluglib.mode.ApkLoadInfo;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PluginUtils {

   public static File getPluginOptDexDir(Context context, String packageName) {
      return enforceDirExists(new File(getPluginBaseDir(context,packageName), "odex"));
   }

   private static File getPluginBaseDir(Context context, String packageName) {
      File plugin = enforceDirExists(context.getFileStreamPath("plugin"));
      return enforceDirExists(plugin);
   }

   public static File enforceDirExists(File file) {
      if (!file.exists()) {
         boolean mkdir = file.mkdir();
         if (!mkdir) {
            throw new RuntimeException("create dir " + file + " failed");
         }
      }
      return file;
   }

   public static void extractAssets(Context context, String fileName) {
      AssetManager assets = context.getAssets();
      InputStream inputStream = null;
      FileOutputStream fileOutputStream = null;
      try {
         inputStream = assets.open(fileName);
         File fileStreamPath = context.getFileStreamPath(fileName);
         fileOutputStream = new FileOutputStream(fileStreamPath);
         byte[] bytes = new byte[1024];
         int count;
         while ((count = inputStream.read(bytes)) > 0) {
            fileOutputStream.write(bytes, 0, count);
         }
         fileOutputStream.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }finally {
         closeSilently(inputStream);
         closeSilently(fileOutputStream);
      }
   }

   private static void closeSilently(Closeable closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public static ApkLoadInfo copyToFiles(Context context, InputStream fileInputStream, String apkName) {
      FileOutputStream fileOutputStream = null;
      try {

         File pluginDir = com.zzx.pluglib.utils.FileUtils.getFilePath(context, "pluginApk");
         File pluginDexDir = com.zzx.pluglib.utils.FileUtils.getFilePath(context, "pluginDex");

         File fileStreamPath = new File( pluginDir+ File.separator + apkName);
//         File fileOutDexPath = new File( pluginDexDir+ File.separator + com.zzx.pluglib.utils.FileUtils.getFileName(apkName) + ".dex");
//         if (fileStreamPath.exists()) {
//            return ApkLoadInfo.build(false,apkName,fileStreamPath,null);
//         }
         fileOutputStream = new FileOutputStream(fileStreamPath);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.copy(fileInputStream,fileOutputStream);
         } else {
            byte[] bytes = new byte[1024];
            int count;
            while ((count = fileInputStream.read(bytes)) > 0) {
               fileOutputStream.write(bytes, 0, count);
            }
            fileOutputStream.flush();
         }
         return ApkLoadInfo.build(true, apkName, fileStreamPath,null);
      } catch (IOException e) {
         e.printStackTrace();
      }finally {
         closeSilently(fileInputStream);
         closeSilently(fileOutputStream);
      }
      return null;
   }
}
