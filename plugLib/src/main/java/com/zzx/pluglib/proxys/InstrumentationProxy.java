package com.zzx.pluglib.proxys;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.zzx.pluglib.stub.StubCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static android.content.pm.PackageManager.*;

public class InstrumentationProxy extends Instrumentation {
    private static final String TARGET_INTENT_CLASS = "target_intent_class";
    Instrumentation mInstrumentation;
    PackageManager packageManager;
    Class<StubCompatActivity> stubCompatActivityClass;

    public InstrumentationProxy(Instrumentation mInstrumentation, PackageManager packageManager, Class<StubCompatActivity> stubCompatActivityClass) {
        this.mInstrumentation = mInstrumentation;
        this.packageManager = packageManager;
        this.stubCompatActivityClass = stubCompatActivityClass;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) {
        List<ResolveInfo> resolveInfoList = null;
        int flags = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            flags = MATCH_ALL;
        }
        if (Build.VERSION.SDK_INT >= 33) {
            resolveInfoList = packageManager.queryIntentActivities(intent, ResolveInfoFlags.of(flags));
        } else {
            resolveInfoList = packageManager.queryIntentActivities(intent, flags);
        }
        Intent finalIntent = intent;
        if (resolveInfoList == null || resolveInfoList.isEmpty()) {
            finalIntent = new Intent(who, stubCompatActivityClass);
            finalIntent.putExtra(TARGET_INTENT_CLASS, intent);
        }

        try {
            Method execStartActivity = mInstrumentation.getClass().getDeclaredMethod("execStartActivity", Context.class,
                    IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
            execStartActivity.setAccessible(true);
            return (ActivityResult) execStartActivity.invoke(mInstrumentation, who, contextThread, token, target, finalIntent, requestCode, options);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ActivityResult  execStartActivities(Context who, IBinder contextThread,
                                               IBinder token, Activity target, Intent intent, Bundle options) {
        if (Build.VERSION.SDK_INT != 15) return null;
        List<ResolveInfo> resolveInfoList = null;
        int flags = 0;
        if (Build.VERSION.SDK_INT >= 33) {
            resolveInfoList = packageManager.queryIntentActivities(intent, ResolveInfoFlags.of(flags));
        } else {
            resolveInfoList = packageManager.queryIntentActivities(intent,flags);
        }
        Intent finalIntent = intent;
        if (resolveInfoList != null && resolveInfoList.isEmpty()) {
            finalIntent = new Intent(who,stubCompatActivityClass);
            finalIntent.putExtra(TARGET_INTENT_CLASS, intent);
        }
        try {
            Method execStartActivityMethod = mInstrumentation.getClass().getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, Build.class);
            execStartActivityMethod.setAccessible(true);
            return (ActivityResult) execStartActivityMethod.invoke(mInstrumentation, who,contextThread,token,target,finalIntent,options);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Activity newActivity(ClassLoader cl, String className,
                                Intent intent) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Intent plugIntent;
        if (Build.VERSION.SDK_INT >= 33) {
            plugIntent = intent.getParcelableExtra(TARGET_INTENT_CLASS, Intent.class);
        } else {
            plugIntent = intent.getParcelableExtra(TARGET_INTENT_CLASS);
        }
        boolean plugIntentClassExist =  (plugIntent != null && !TextUtils.isEmpty(plugIntent.getComponent().getClassName()));
        String finalClassName = plugIntentClassExist? plugIntent.getComponent().getClassName() : className;
        Intent finalIntent = plugIntentClassExist?plugIntent:intent;

        if (Build.VERSION.SDK_INT >= 28) {
            return mInstrumentation.newActivity(cl,finalClassName,finalIntent);
        }
        return super.newActivity(cl,finalClassName,finalIntent);
    }


}
