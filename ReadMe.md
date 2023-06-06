## 动态Activity分析
先写一个未申明的Activity，我们来看看运行流程(注意这里建议使用虚拟机来查看)
```
已注册的页面初始化时添加空指针
Process: com.zzx.hooktest, PID: 14434
    java.lang.RuntimeException: Unable to start activity ComponentInfo{com.zzx.hooktest/com.zzx.hooktest.MainActivity}: java.lang.NullPointerException
        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3645)
        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3782)
        at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:101)
        at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:135)
        at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:95)
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2307)
        at android.os.Handler.dispatchMessage(Handler.java:106)
        at android.os.Looper.loopOnce(Looper.java:201)
        at android.os.Looper.loop(Looper.java:288)
        at android.app.ActivityThread.main(ActivityThread.java:7872)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:548)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:936)
    Caused by: java.lang.NullPointerException
        at com.zzx.hooktest.MainActivity.onCreate(MainActivity.kt:29)
        at android.app.Activity.performCreate(Activity.java:8305)
        at android.app.Activity.performCreate(Activity.java:8284)
        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1417)
        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3626)
        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3782)
        at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:101)
        at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:135)
        at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:95)
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2307)
        at android.os.Handler.dispatchMessage(Handler.java:106)
        at android.os.Looper.loopOnce(Looper.java:201)
        at android.os.Looper.loop(Looper.java:288)
        at android.app.ActivityThread.main(ActivityThread.java:7872)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:548)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:936)
```
```
click打开一个未注册的activity
FATAL EXCEPTION: main
Process: com.zzx.hooktest, PID: 2463
android.content.ActivityNotFoundException: Unable to find explicit activity class {com.zzx.hooktest/com.zzx.hooktest.PluginTestActivity}; have you declared this activity in your AndroidManifest.xml, or does your intent not match its declared <intent-filter>?
    at android.app.Instrumentation.checkStartActivityResult(Instrumentation.java:2197)
    at android.app.Instrumentation.execStartActivity(Instrumentation.java:1839)
    at android.app.Activity.startActivityForResult(Activity.java:5471)
    at androidx.activity.ComponentActivity.startActivityForResult(ComponentActivity.java:712)
    at android.app.Activity.startActivityForResult(Activity.java:5429)
    at androidx.activity.ComponentActivity.startActivityForResult(ComponentActivity.java:693)
    at android.app.Activity.startActivity(Activity.java:5927)
    at android.app.Activity.startActivity(Activity.java:5894)
    at com.zzx.hooktest.MainActivity.onCreate$lambda$0(MainActivity.kt:39)
    at com.zzx.hooktest.MainActivity.$r8$lambda$Jytl-rgsCek_4W6Ci1rToFKZJNY(Unknown Source:0)
    at com.zzx.hooktest.MainActivity$$ExternalSyntheticLambda0.onClick(Unknown Source:2)
    at android.view.View.performClick(View.java:7506)
    at android.view.View.performClickInternal(View.java:7483)
    at android.view.View.-$$Nest$mperformClickInternal(Unknown Source:0)
    at android.view.View$PerformClick.run(View.java:29334)
    at android.os.Handler.handleCallback(Handler.java:942)
    at android.os.Handler.dispatchMessage(Handler.java:99)
    at android.os.Looper.loopOnce(Looper.java:201)
    at android.os.Looper.loop(Looper.java:288)
    at android.app.ActivityThread.main(ActivityThread.java:7872)
    at java.lang.reflect.Method.invoke(Native Method)
    at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:548)
    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:936)
```
看上面崩溃日志，最后会调到execStartActivity方法，然后会通过ActivityTaskManager.getService().startActivity()，而checkStartActivityResult()中抛出的异常。然后我们去看源码。
```
|-Instrumentation.execStartActivity
| |-ActivityTaskManagerService.startActivity() -> startActivityAsUser()
|   |-ActivityStarter.execute() -> startActivityMayWait() -> startActivity() -> ... -> startActivityUnchecked() -> mRootActivityContainer.resumeFocusedStacksTopActivities()
|     |-RootActivityContainer.resumeFocusedStacksTopActivities() -> targetStack.resumeTopActivityUncheckedLocked()
|      |-ActivityStack.resumeTopActivityUncheckedLocked() -> resumeTopActivityInnerLocked() -> mStackSupervisor.startSpecificActivityLocked()
|       |-ActivityStackSupervisor.startSpecificActivityLocked() -> 判断是否已启动进程,没启动的话向AMS发送消息创建新的进程,已启动的话 -> realStartActivityLocked() -> mService.getLifecycleManager().scheduleTransaction(clientTransaction)        
|        |-ClientLifecycleManager.scheduleTransaction() -> transaction.schedule()
|         |-ClientTransaction.schedule() -> IApplicationThread mClient.scheduleTransaction()
|          |-ApplicationThread.scheduleTransaction() -> ActivityThread.this.scheduleTransaction()
|           |-ActivityThread.scheduleTransaction() -> ClientTransactionHandler.scheduleTransaction()
|            |-ClientTransactionHandler.scheduleTransaction -> sendMessage(ActivityThread.H.EXECUTE_TRANSACTION, transaction); -> 发送到mH的handler中
|           |-ActivityThread H mH.handlerMessage() -> case:EXECUTE_TRANSACTION -> mTransactionExecutor.execute(transaction)
|            |-TransactionExecutor.execute() -> executeCallbacks() -> item.execute(mTransactionHandler, token, mPendingActions) 实际上这里的item对应的是LaunchActivityItem
|             |-LaunchActivityItem.execute() -> client.handleLaunchActivity()
|           |-ActivityThread.handleLaunchActivity() -> performLaunchActivity() -> createBaseContextForActivity ContextImpl创建流程 ->mInstrumentation.newActivity() 返回的Activity对象 -> Application app = r.packageInfo.makeApplication Application创建流程 -> mInstrumentation.callActivityOnCreate 调用activity的onCreate方法
|-Instrumentation.newActivity 通过反射真正初始化activity的地方
```
通过这段流程，和错误堆栈，了解到了整个activity的启动流程。\
那么我们应该怎么去替换对应的activity呢？  
通过第二段未注册启动的崩溃中得出，他会运行在Instrumentation.execStartActivity后的方法开始判断intent启动的控制是否在Manifest中.\
那么我们是不是能先让他通过已有注册的Activity进行判断，然后将需要跳转的Intent存储起来，那么查看流程，发现Instrumentation.execStartActivity后才触发intent检测的，那么我们是不是可以尝试hook住这段代码即可  
- [x]hook Activity资源检测并替换

不会再报错了，那么下一步最终替换成我们自己未注册的activity该怎么办？  
我们来看第一段错误日志，根据代码追踪activity创建是在performLaunchActivity中通过反射创建，通过调用Instrumentation.newActivity()创建，啊~~~一下子通了，那么这时候担心的是里面是不是会判断intent的合法性(未注册)，
但是实际翻代码，发现没有对intent做二次判断，那么开心了，我们就通过hook newActivity()方法，在被调用的时候将原先实际需要的Intent取回来。\
- [x]hook 将占位activity替换成真实的activity

现在前面两个都想好了，那么我们现在怎么去替换Instrumentation的实例呢  
通过上面的启动路径发现可以通过activity去获取Instrumentation对象，这是没问题的，但是这样hook的方式是不方便的，那我们还有其他启动activity的方式么，这么一说你是不是想起来了，我们还可以通过Context去启动activity
ContextImpl是Context具体实现类，那么context是否也有对应的Instrumentation呢，实际还真有，ContextImpl中的startActivity()中mMainThread.getInstrumentation().execStartActivity，啊~原来要麻烦一点，得
先获取mMainThread才能获取其中的Instrumentation实例。
- [x]在Application的时候hook住Instrumentation
## 动态Activity方案
1. 通过HookInstarumentation.hookInstrumentation()在Application启动时对ActivityThread中的Instrumentation进行替换
2. InstrumentationProxy继承Instrumentation通过静态代理的方式重写execStartActivity()和newActivity()
+  1. execStartActivity()用于将未注册的替换成占位页面，用于通过内部的intent检测机制.
   2. newActivity()用于还原需要真正使用的页面.

## PlugLoadClassLoaderHelper
负责替换插件中的加载类，可以用于热更新修复bug等

### DexPatchList
发现Element[]可以用makeDexElements(List<File> files, File optimizedDirectory,List<IOException> suppressedExceptions, ClassLoader loader)生成。\
所以新添插件可以通过这个方法生成自己对应的Element

### 总结
通过替换掉原先DexPathList中的Elements，来实现java层面的动态添加插件，以及修复BUG

## InstrumentationProxy
默认情况下performLaunchActivity会使用站位StubActivity的ApplicationInfo，通过代理类最后再newActivity()中替换成真实的Intent对象实现。