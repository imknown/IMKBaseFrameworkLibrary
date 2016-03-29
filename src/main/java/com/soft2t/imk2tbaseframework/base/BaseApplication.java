package com.soft2t.imk2tbaseframework.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.test.suitebuilder.annotation.SmallTest;
import android.text.TextUtils;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.soft2t.imk2tbaseframework.R;
import com.soft2t.imk2tbaseframework.base.Constant.Debug;
import com.soft2t.imk2tbaseframework.base.debug.MyDebugManager;
import com.soft2t.imk2tbaseframework.base.uncaught.CrashHandler;
import com.soft2t.imk2tbaseframework.util.LogUtil;
import com.soft2t.imk2tbaseframework.util.file.DatabaseUtil;

import java.util.LinkedList;
import java.util.List;

import im.fir.sdk.FIR;

public class BaseApplication extends Application {

    // region DB versions
    public final static int DB_VERSION_1 = 1;
    public final static int DB_VERSION_2 = 2;

    //  region 通用 对话框
    public final static int DB_VERSION_3 = 3;
    public final static int DB_VERSION_4 = 4;
    public final static int DB_VERSION_5 = 5;
    public final static int DB_VERSION_6 = 6;
    public final static int DB_VERSION_7 = 7;
    public final static int DB_VERSION_8 = 8;
    public final static int DB_VERSION_9 = 9;
    public final static int DB_VERSION_10 = 10;

    // endregion

    //  region 数据库
    public static BaseApplication mBaseApplication;
    /**
     * 这个 需要在 子项目中 实现
     */
    public static Context mApplicationContext;

    // endregion
    /**
     * 是否可以退出
     */
    public static boolean canExit;
    private static ProgressDialog progressDialog;
    private static SQLiteDatabase instanceOfSQLiteDatabase;

    public static void showWait(Context context) {
        showWait(context, R.string.tips_waiting, false);
    }

    public static void showWait(Context context, boolean cancelable) {
        showWait(context, R.string.tips_waiting, cancelable);
    }

    public static void showWait(Context context, int resId) {
        showWait(context, context.getResources().getString(resId), false);
    }

    public static void showWait(Context context, int resId, boolean cancelable) {
        showWait(context, context.getResources().getString(resId), cancelable);
    }

    @Deprecated
    public static void showWait(Context context, String waitMsg) {
        showWait(context, waitMsg, false);
    }

    @Deprecated
    public static void showWait(Context context, String waitMsg, boolean cancelable) {
        // dismissDialog();

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(cancelable);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.setMessage(waitMsg);
        progressDialog.show();
    }

    public static void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static SQLiteDatabase getSQLiteDatabaseInstance() {
        if (instanceOfSQLiteDatabase == null || !instanceOfSQLiteDatabase.isOpen()) {
            instanceOfSQLiteDatabase = mApplicationContext.openOrCreateDatabase(DatabaseUtil.DB_NAME, Context.MODE_PRIVATE, null);
        }

        return instanceOfSQLiteDatabase;
    }

    /**
     * 双击退出程序之前的准备
     */
    public static void readyToExit() {
        if (!canExit) {
            canExit = true;

            MyToastManager.showToast(R.string.press_twice_to_exit);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    canExit = false;
                }
            }, Constant.App.HOW_MANY_MILLISECOND_BETWEEN_DOUBLE_CLICK_FOR_EXIT);

        } else {
            exitClient();
        }
    }

    /**
     * 退出应用，关闭所有Activity
     */
    public static void exitClient() {
        if (instanceOfSQLiteDatabase != null && instanceOfSQLiteDatabase.isOpen()) {
            instanceOfSQLiteDatabase.close();
            instanceOfSQLiteDatabase = null;
        }

        MyDebugManager.getInstance().destroy();

//        NotificationManager notificationManager = (NotificationManager) mApplicationContext.getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.cancel(UpdateService.APK_UPDATE_DOWNLOAD_ID);

        MyActivityManager.finishAllActivity();

//        android.os.Process.killprocess(android.os.process.mypid());
//        system.exit(0);
    }

    /**
     * 子项目 重写规则 <br />
     * <code>
     * <pre>
     * &#64;Override
     * public void onCreate() {
     *     super.onCreate();
     *     子项目的 initConstant();
     *     boolean isMain = super.initBase();
     *
     *     其他子项目的初始化();
     * }
     * </pre>
     * </code>
     */
    @Override
    public void onCreate() {
        super.onCreate();

        mBaseApplication = this;
    }

    public boolean initBase() {
        boolean isMain = false;

        if (!ProcessChecker.checkProcessIsMain(mBaseApplication)) {
            String processName = ProcessChecker.getCurProcessName(mBaseApplication);
            LogUtil.e(processName + " 非主进程, 已返回");
            return isMain;
        }

        initCrashHandler();

        initStrictMode();

        UIL.initImageLoader();

        initDbUtils();

        // initWiFiConnect();

        // initBluetooth();

        MyDebugManager.getInstance().init();

        FIR.init(this);

        isMain = true;
        return isMain;
    }

    @Override
    public void onTerminate() {
        // unregisterReceiver(batteryReceiver);

        MyDebugManager.getInstance().dismiss();

        super.onTerminate();
    }

    public void initCrashHandler() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(mApplicationContext);
    }

    public void initStrictMode() {
        if (Debug.ENABLE_STRICT_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            // StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            // StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() // 构造StrictMode
                    .detectDiskReads() // 当发生磁盘读操作时输出
                    .detectDiskWrites() // 当发生磁盘写操作时输出
                    // .detectNetwork() // 访问网络时输出
                    .detectAll() // 磁盘读写和网络I/O
                    .penaltyLog() // 以日志的方式输出
                    // .penaltyDeath() //
                    .penaltyDialog()// 以对话框的方式输出
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder() // 构造StrictMode
                    .detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                    .detectAll() // 磁盘读写和网络I/O
                    .penaltyLog() // 以日志的方式输出
                    // .penaltyDeath() //
                    .build());
        }
    }

    private void initDbUtils() {
        String temp_db_name = "temp_db_name";
        DatabaseUtil.DATABASE_PATH = mApplicationContext.getDatabasePath(temp_db_name).getAbsolutePath().replace(temp_db_name, "");
        DatabaseUtil.DB_NAME = Constant.Db.DB_NAME;
        DatabaseUtil.DB_VERSION = Constant.Db.DB_VERSION;

        // File dir = new File(DatabaseUtil.DATABASE_PATH);
        //
        // if (dir.exists() || dir.mkdirs()) {
        // File dbFile = new File(DatabaseUtil.DATABASE_PATH, DatabaseUtil.DB_NAME);
        // instanceOfSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        // }
    }
    // endregion

    public static class MyR {
        private static Resources mRes;

        public static Resources getR() {
            if (mRes == null) {
                mRes = mBaseApplication.getResources();
            }

            return mRes;
        }
    }

    public static class ProcessChecker {
        public static boolean checkProcessIsMain(Context context) {
            boolean isMain = false;

            String processName = getCurProcessName(context);
            final String packageName = context.getPackageName();
            LogUtil.i("processName: " + processName);
            LogUtil.i("packageName: " + packageName);

            if (!TextUtils.isEmpty(processName)) {
                if (processName.equals(packageName)) {
                    isMain = true;
                }
            }

            return isMain;
        }

        private static String getCurProcessName(Context context) {
            int pid = android.os.Process.myPid();

            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }

            return "";
        }

        // /**
        // * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
        // *
        // * @param pID
        // * @return
        // */
        // private static String getAppName(int pID, Context context) {
        // String processName = null;
        //
        // ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //
        // List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
        //
        // Iterator<RunningAppProcessInfo> i = l.iterator();
        //
        // PackageManager pm = context.getPackageManager();
        //
        // while (i.hasNext()) {
        // RunningAppProcessInfo info = (RunningAppProcessInfo) (i.next());
        //
        // try {
        // if (info.pid == pID) {
        // CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
        // Log.d("Process", "Id: " + info.pid + " ProcessName: " + info.processName + " Label: " + c.toString());
        // // processName = c.toString();
        //
        // processName = info.processName;
        //
        // return processName;
        // }
        // } catch (Exception e) {
        // Log.e("Process", e.getMessage());
        // }
        // }
        //
        // return processName;
        // }
    }

    public static class UIL {

        private static void initImageLoader() {
            // This configuration tuning is custom. You can tune every option, you may tune some of them,
            // or you can create default configuration by
            // ImageLoaderConfiguration.createDefault(this);
            // method.
            ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(mApplicationContext);

            config.threadPriority(Thread.NORM_PRIORITY - 2);
            config.denyCacheImageMultipleSizesInMemory();
            config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
            config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
            config.tasksProcessingOrder(QueueProcessingType.LIFO);

            if (Debug.SHOW_DEVELOP_LOG) {
                // Remove for release app
                config.writeDebugLogs();
            }

            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config.build());

            // ************** How to use **************
            // "http://site.com/image.png" // from Web
            // "file:///mnt/sdcard/image.png" // from SD card
            // "file:///mnt/sdcard/video.mp4" // from SD card (video thumbnail)
            // "content://media/external/images/media/13" // from content provider
            // "content://media/external/video/media/13" // from content provider (video thumbnail)
            // "assets://image.png" // from assets
            // "drawable://" + R.drawable.img // from drawables (non-9patch images)
            //
            // ImageLoader.getInstance().displayImage("uri", (ImageAware) null);
        }

        /**
         * 获取 一个 独立的新实例
         */
        public static DisplayImageOptions getUILNewInstanceOptions() {
            DisplayImageOptions options = new DisplayImageOptions.Builder()//
                    // .showImageOnLoading(R.drawable.ic_launcher)// 加载中
                    // .showImageForEmptyUri(R.drawable.ic_launcher)// 空地址
                    // .showImageOnFail(R.drawable.ic_launcher)// 失败的图片
                    .cacheInMemory(true)// 内存缓存
                    .cacheOnDisk(true)// 磁盘缓存
                    .resetViewBeforeLoading(false)// 重置 控件 原有的 src
                    .considerExifParams(true)// 是否考虑JPEG图像EXIF参数(旋转，翻转等)
                    .bitmapConfig(Bitmap.Config.RGB_565)//
                    // .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
                    // .resetViewBeforeLoading(true)// 设置图片在下载前, 是否 重置/复位
                    // .decodingOptions(android.graphics.BitmapFactory.Options decodingOptions)// 设置图片的解码配置
                    // .delayBeforeLoading(100)// 你设置的下载前的延迟时间
                    // .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
                    // .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                    .build();

            return options;
        }
    }

    public static class MyBattery {
        private final static String DEFAULT_CHARGING_STATUS = "未知的充电状态";
        private final static String DEFAULT_HEALTH_STATUS = "未知的健康状态";

        /**
         * 在使用电池的标志
         */
        private final static int BATTERY_ON_DEVICE = 0;

        private final static int DEFAULT = -1;

        private final static int EMPTY = 0;
        private final static int FULL = 100;

        // /** 电池当前使用状态的图标 */
        // public static int batteryResId = DEFAULT;
        //
        // /** 电池当前使用状态的图标 */
        // public static int levelRate = DEFAULT;

        // public static BatterHolder batterHolder;
        //
        // public static void initBatteryStatus() {
        // if (batterHolder != null && batterHolder.homepage_battery_img != null && batterHolder.homepage_battery_rate != null) {
        // batterHolder.homepage_battery_img.setImageResource(BaseApplication.MyBattery.batteryResId);
        // batterHolder.homepage_battery_rate.setText(BaseApplication.MyBattery.levelRate + "%");
        // }
        // }

        // public static void initBatteryReceiver() {
        // batteryReceiver = new BroadcastReceiver() {
        // @Override
        // public void onReceive(Context context, Intent intent) {
        // doBatteryReceiver(intent);
        // }
        // };
        //
        // mApplicationContext.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // }

        public static BatteryInfos doBatteryReceiver(Intent intent) {

            // 当前剩余电量
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, DEFAULT);
            // 电量最大值
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, DEFAULT);
            // 电池剩余容量
            int levelRate = (int) (level / (float) scale * (FULL - EMPTY));

            // 电池当前使用状态的图标
            int batteryResId = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, DEFAULT);

            // 电池是否处于充电状态 /
            boolean isCharging = false;

            // 充电状态
            String chargingStatus = DEFAULT_CHARGING_STATUS;
            switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS, DEFAULT)) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    int pluggedByWhat = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, DEFAULT);

                    if (pluggedByWhat != BATTERY_ON_DEVICE && pluggedByWhat != DEFAULT) {
                        isCharging = true;
                    }

                    if (pluggedByWhat == BatteryManager.BATTERY_PLUGGED_AC) {
                        chargingStatus = "使用电源适配器(充电器)充电中";
                    } else if (pluggedByWhat == BatteryManager.BATTERY_PLUGGED_USB) {
                        chargingStatus = "使用USB数据线充电中";
                    } else if (pluggedByWhat == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                        chargingStatus = "使用无线充电中";
                    } else {
                        chargingStatus = "使用未知方式充电中";
                    }
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    chargingStatus = "放电中";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    chargingStatus = "已充满";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    chargingStatus = "未充满";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    chargingStatus = "未知";
                    break;
            }

            // 电池状态
            String batteryHealthStatus = DEFAULT_HEALTH_STATUS;
            switch (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, DEFAULT)) {
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    batteryHealthStatus = "电池已损坏！";
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    batteryHealthStatus = "健康";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    batteryHealthStatus = "电压过高";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    batteryHealthStatus = "温度过高";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    batteryHealthStatus = "未知";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    batteryHealthStatus = "未知故障";
                    break;
            }

            // 电池电压
            String batteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, DEFAULT) + "mV";

            // 电池温度
            String batteryTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 1) / 10.0 + "℃";

            // 电池类型
            String batteryTech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

            // initBatteryStatus();

            BatteryInfos batteryInfos = new BatteryInfos();
            batteryInfos.batteryChargingStatus = chargingStatus;
            batteryInfos.batteryHealthStatus = batteryHealthStatus;
            batteryInfos.batteryResId = batteryResId;
            batteryInfos.levelRate = levelRate;
            batteryInfos.batteryVoltage = batteryVoltage;
            batteryInfos.batteryTemperature = batteryTemperature;
            batteryInfos.batteryTech = batteryTech;
            batteryInfos.isCharging = isCharging;
            return batteryInfos;
        }

        public static class BatteryInfos {
            /**
             * 电池当前使用状态
             */
            public String batteryChargingStatus = DEFAULT_CHARGING_STATUS;

            /**
             * 电池当前健康状态
             */
            public String batteryHealthStatus = DEFAULT_HEALTH_STATUS;

            /**
             * 电池当前使用状态的图标
             */
            public int batteryResId = DEFAULT;

            /**
             * 电池当前使用状态的图标
             */
            public int levelRate = DEFAULT;

            /**
             * 电池是否处于充电状态
             */
            public boolean isCharging;

            /**
             * 电压
             */
            public String batteryVoltage = EMPTY + "mV";

            /**
             * 温度
             */
            public String batteryTemperature = EMPTY + "℃";

            /**
             * 类型
             */
            public String batteryTech = "未知技术";
        }
    }

    /**
     * 如果 出现 Looper 和 Handler , Thread 等 奇怪的一样, 可以尝试<br>
     * <p/>
     * <code>
     * <pre>
     * if (Looper.myLooper() == null) {
     * 	Looper.prepare();
     * }
     *
     * MyToastManager.showToast("奇怪的错误");
     *
     * Looper.loop();
     * </pre>
     * </code>
     */
    public static class MyToastManager {
        @Deprecated
        public static void showToast(String text) {
            showToast(mApplicationContext, text);
        }

        public static void showToast(int resId) {
            showToast(mApplicationContext, resId);
        }

        public static void showToast(Context context, int resId) {
            showToast(context, context.getString(resId));
        }

        public static void showToast(Context context, int resId, Object[] args) {
            showToast(context, context.getString(resId, args));
        }

        @Deprecated
        public static void showToast(final Context context, final String text) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    toast.show();

                    // LayoutInflater inflater = LayoutInflater.from(context);
                    // LinearLayout viewLayout = (LinearLayout) inflater.inflate(R.layout.toast_show_msg, (ViewGroup) null, false);
                    //
                    // TextView message_tv = (TextView) viewLayout.findViewById(R.id.message_tv);
                    // ImageView app_icon_imgv = (ImageView) viewLayout.findViewById(R.id.app_icon_imgv);
                    //
                    // Toast toast = new Toast(mApplicationContext);
                    // // toast.setGravity(Gravity.CENTER, 0, 0);
                    // toast.setDuration(Toast.LENGTH_SHORT);
                    //
                    // PackageInfo pi = DeviceUtil.getAppVersion(context);
                    // ApplicationInfo ai = pi.applicationInfo;
                    //
                    // int icon = ai.icon;
                    // // int logo = ai.logo;
                    // // Drawable appIcon = ai.loadIcon(context.getPackageManager());
                    // // Drawable appStoreLogo = ai.loadLogo(context.getPackageManager());
                    //
                    // message_tv.setText(text);
                    // app_icon_imgv.setImageResource(icon);
                    //
                    // toast.setView(viewLayout);
                    //
                    // toast.show();
                }
            });
        }
    }

    public static class MyActivityManager {
        private static List<Activity> activityList = new LinkedList<>();

        /**
         * 添加 Activity
         *
         * @param activity 需要添加的 Activity
         */
        public static void addActivity(Activity activity) {
            activityList.add(activity);
        }

        /**
         * 关闭 所有的 Activity
         */
        public static void finishAllActivity() {
            int size = activityList.size();

            for (int i = size - 1; i >= 0; i--) {
                Activity a = activityList.get(i);

                activityList.remove(a);

                if (null != a) {
                    a.finish();
                }
            }

            // SharePre.setLoginState(mApplicationContext, false);
        }

        /**
         * 关闭 其他的 Activity
         *
         * @param activity 不需要关闭的 Activity
         */
        public static void finishOtherActivity(Activity activity) {
            int size = activityList.size();

            for (int i = size - 1; i >= 0; i--) {
                Activity a = activityList.get(i);

                if (null != a && !activity.getClass().toString().equals(a.getClass().toString())) {
                    activityList.remove(a);

                    a.finish();
                }
            }
        }

        /**
         * 关闭 当前 Activity
         *
         * @param activity 当前 Activity
         */
        public static void finishThisActivity(Activity activity) {
            int size = activityList.size();

            for (int i = size - 1; i >= 0; i--) {
                Activity a = activityList.get(i);

                if (null != a && activity.getClass().toString().equals(a.getClass().toString())) {
                    activityList.remove(a);

                    a.finish();
                }
            }
        }

        /**
         * 获取 当前 Activity
         */
        @SmallTest
        public static Activity getCurrentActivity() {
            Activity activity = null;

            try {
                int size = activityList.size();

                if (size > 1) {
                    int lastSize = size - 1;
                    activity = activityList.get(lastSize);
                }
            } catch (Exception e) {
                LogUtil.w("getCurrentActivity() Fail", e.getMessage());
            }

            return activity;
        }

        /**
         * 获取 上一个 Activity
         */
        public static Activity getLastActivity() {
            int size = activityList.size();

            Activity last = null;

            if (size > 1) {
                last = activityList.get(size - 2);
            }

            return last;
        }
    }
}
