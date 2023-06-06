package com.zzx.plugextend;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PluginResourceUtils {
   private static Resources mResources = null;
   public static Resources getResource(Context context, String pluginName) {
      if (mResources != null) {
         return mResources;
      }
      mResources = loadResource(context,pluginName);
      if (mResources == null) {
         throw new NullPointerException("plugin resource is null");
      }
      return mResources;
   }

   private static Resources loadResource(Context context, String pluginName) {
      File apkFilePath = FileUtils.getApkFilePath(context, pluginName);
      try {
         AssetManager assetManager = AssetManager.class.newInstance();
         Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
         addAssetPathMethod.setAccessible(true);
         int returnAddAssetPath = (int) addAssetPathMethod.invoke(assetManager, apkFilePath.getAbsolutePath());
         Log.d("ResourcesHook", "returnAddAssetPath: "+returnAddAssetPath);
         Resources resources = context.getResources();
         return new Resources(assetManager, resources.getDisplayMetrics(),resources.getConfiguration());

      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      }
      return null;
   }
}
