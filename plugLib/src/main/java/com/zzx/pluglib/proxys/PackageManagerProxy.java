package com.zzx.pluglib.proxys;

import android.content.ComponentName;
import android.util.Log;

import com.zzx.pluglib.stub.StubCompatActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PackageManagerProxy implements InvocationHandler {


    private final Class stubCompatActivityClass;
    private Object packagemanager;
    private String mAppPackageName;

    public PackageManagerProxy(Object packagemanager, Class<StubCompatActivity> stubCompatActivityClass,String mAppPackageName) {
        this.stubCompatActivityClass = stubCompatActivityClass;
        this.packagemanager = packagemanager;
        this.mAppPackageName = mAppPackageName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getActivityInfo".equals(method.getName()) && args != null && args.length > 0) {
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Integer) {
                    Log.d("PackageManagerProxy", "invoke: Integer:" + args[i].toString());
                } else if (args[i] instanceof ComponentName) {
                    Log.d("PackageManagerProxy", "invoke: ComponentName:" + args[i].toString());
                    index = i;
                    break;
                }
            }
            ComponentName componentName = new ComponentName(mAppPackageName,stubCompatActivityClass.getName());
            args[index] = componentName;
        }
        return method.invoke(packagemanager, args);
    }
}
