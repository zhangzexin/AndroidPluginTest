package com.zzx.pluglib;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;

import com.zzx.pluglib.proxys.InstrumentationProxy;
import com.zzx.pluglib.proxys.PackageManagerProxy;
import com.zzx.pluglib.stub.StubCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class HookInstarumentation {


    public static void hookInstrumentation(Context context) {
        Class<StubCompatActivity> stubCompatActivityClass = StubCompatActivity.class;
        try {
            Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
            Field mMainThreadField = contextImplClass.getDeclaredField("mMainThread");
            mMainThreadField.setAccessible(true);
            Object mMainThread = mMainThreadField.get(context);
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(mMainThread);
            hookPackageManager(context, stubCompatActivityClass);
            mInstrumentationField.set(mMainThread, new InstrumentationProxy(mInstrumentation, context.getPackageManager(), stubCompatActivityClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void hookPackageManager(Context context, Class<StubCompatActivity> stubCompatActivityClass) {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            //这只是确保hook的时候已有人调用过，真实情况是hook时已经被调用过该方法了
            Method getPackageManagerMethod = activityThreadClass.getDeclaredMethod("getPackageManager");
            getPackageManagerMethod.setAccessible(true);
            getPackageManagerMethod.invoke(null);


            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object activityThread = currentActivityThreadMethod.invoke(null);
            Object packagemanager = sPackageManagerField.get(activityThread);
            Class<?> iPackageManagerClass = Class.forName("android.content.pm.IPackageManager");
            Object packageManagerProxy = Proxy.newProxyInstance(packagemanager.getClass().getClassLoader(),
                    new Class[]{iPackageManagerClass},
                    new PackageManagerProxy(packagemanager, stubCompatActivityClass, context.getApplicationContext().getPackageName()));
            sPackageManagerField.set(activityThread, packageManagerProxy);
            PackageManager packageManager = context.getPackageManager();
            Field mPMField = packageManager.getClass().getDeclaredField("mPM");
            mPMField.setAccessible(true);
            mPMField.set(packageManager, packageManagerProxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
