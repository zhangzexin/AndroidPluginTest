package com.zzx.pluglib;

import android.content.Context;
import android.os.Build;
import com.zzx.pluglib.utils.FileUtils;
import android.sax.Element;

import com.zzx.pluglib.mode.ApkLoadInfo;
import com.zzx.pluglib.utils.PluginUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;

class PlugLoadClassLoaderHelper {


   public static void attachUnOptPluginClass(Context context, ArrayList<ApkLoadInfo> unOptApks) {
      Class<? super PathClassLoader> baseDexClassLoader = PathClassLoader.class.getSuperclass();
      try {
         Field pathListField = baseDexClassLoader.getDeclaredField("pathList");
         pathListField.setAccessible(true);
         ClassLoader classLoader = context.getClassLoader();
         Object pathList = pathListField.get(classLoader);
         Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
         dexElementsField.setAccessible(true);
         Object[] dexElements = (Object[]) dexElementsField.get(pathList);
         Class<?> componentType = dexElements.getClass().getComponentType();
         Object[] newElements = (Object[]) Array.newInstance(componentType, dexElements.length + unOptApks.size());
         Object[] pluginElements = loadDexFile(context, pathList, unOptApks);
         System.arraycopy(dexElements,0,newElements,0,dexElements.length);
         System.arraycopy(pluginElements,0,newElements,dexElements.length,pluginElements.length);
         dexElementsField.set(pathList,newElements);
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }

   }

   private static Object[] loadDexFile(Context context,Object pathList, ArrayList<ApkLoadInfo> apkLoadInfo) {
      try {
         ArrayList<File> files = new ArrayList<>();
         for (ApkLoadInfo loadInfo : apkLoadInfo) {
            files.add(loadInfo.apkfilePath);
         }
         File pluginDexDir = FileUtils.getFilePath(context, "pluginDex");
         ArrayList<IOException> ioExceptions = new ArrayList<>();
         int sdkInt = Build.VERSION.SDK_INT;
         if (sdkInt >= 26) {
            Method makeDexElementsMethod = pathList.getClass().getDeclaredMethod("makeDexElements", List.class, File.class, List.class, ClassLoader.class, boolean.class);
            makeDexElementsMethod.setAccessible(true);
            return (Object[]) makeDexElementsMethod.invoke(null, files, pluginDexDir, ioExceptions, context.getClassLoader(), false);
         } else if (sdkInt >= 19) {
            Method makeDexElementsMethod = pathList.getClass().getDeclaredMethod("makeDexElements", List.class, File.class, List.class);
            makeDexElementsMethod.setAccessible(true);
            return (Object[]) makeDexElementsMethod.invoke(null, files, pluginDexDir, ioExceptions);
         } else {
            Method makeDexElementsMethod = pathList.getClass().getDeclaredMethod("makeDexElements", ArrayList.class, File.class);
            makeDexElementsMethod.setAccessible(true);
            return (Object[]) makeDexElementsMethod.invoke(null, files, pluginDexDir);
         }
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }
      return null;
   }

   public static void attachOptPluginClass(Context context, ArrayList<ApkLoadInfo> optApks) {
      Class<? super PathClassLoader> baseDexClassLoader = PathClassLoader.class.getSuperclass();
      try {
         Field pathListField = baseDexClassLoader.getDeclaredField("pathList");
         pathListField.setAccessible(true);
         ClassLoader classLoader = context.getClassLoader();
         Object pathList = pathListField.get(classLoader);
         Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
         dexElementsField.setAccessible(true);
         Object[] dexElements = (Object[]) dexElementsField.get(pathList);
         Class<?> componentType = dexElements.getClass().getComponentType();
         Object[] newElements = (Object[]) Array.newInstance(componentType, dexElements.length + optApks.size());
         Object[] pluginElements = loadDexFile(context, pathList, optApks);
         System.arraycopy(dexElements,0,newElements,0,dexElements.length);
         if (pluginElements != null) {
            System.arraycopy(pluginElements, 0, newElements, dexElements.length, pluginElements.length);
         }
         dexElementsField.set(pathList,newElements);
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }
}
