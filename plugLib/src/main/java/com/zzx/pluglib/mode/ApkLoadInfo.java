package com.zzx.pluglib.mode;

import java.io.File;

public class ApkLoadInfo {
   public boolean isFirstLoad;
   public String name;
   public File apkfilePath;
   public File fileOutDexPath;
   private ApkLoadInfo(boolean isFirstLoad, String name, File apkfilePath, File fileOutDexPath) {
      this.isFirstLoad = isFirstLoad;
      this.name = name;
      this.apkfilePath =  apkfilePath;
      this.fileOutDexPath = fileOutDexPath;
   }

   public static ApkLoadInfo build(boolean isFirstLoad, String name, File apkfilePath, File fileOutDexPath) {
      return new ApkLoadInfo(isFirstLoad,name,apkfilePath,fileOutDexPath);
   }
}
