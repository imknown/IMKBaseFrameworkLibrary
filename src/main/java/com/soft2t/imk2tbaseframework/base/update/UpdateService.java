package com.soft2t.imk2tbaseframework.base.update;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.soft2t.imk2tbaseframework.R;
import com.soft2t.imk2tbaseframework.base.BaseWebUrl;
import com.soft2t.imk2tbaseframework.base.Constant;
import com.soft2t.imk2tbaseframework.util.device.soft.PackageUtils;
import com.soft2t.imk2tbaseframework.util.device.soft.ShellUtils;
import com.soft2t.imk2tbaseframework.util.file.DeleteFileUtil;
import com.soft2t.imk2tbaseframework.util.image.BitmapAndDrawableUtil;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class UpdateService extends Service {

    public static final String APP_NAME = "app_name";
    public static final String ICON = "icon";
    public static final String ICON_SILHOUETTE = "icon_silhouette";
    public static final String URL = "download_url";
    public static final String FILE_NAME_ONLY = "file_name_only";

    public static final int APK_UPDATE_DOWNLOAD_ID = 0;
    private static final int DOWNLOAD_STATUS_BEGIN = 0;
    private static final int DOWNLOAD_STATUS_FINISH = 100;

    private int appName = R.string.my_app_name;
    private int icon = R.mipmap.eclipse_adt_ic_launcher;
    private int icon_silhouette = R.mipmap.eclipse_adt_ic_launcher;

    private String downloadUrl;
    private String fullSaveDir;
    private String fileNameOnly = Calendar.getInstance().getTimeInMillis() + ".download";
    private String fullAppSavePath;

    private int downloadCurrent;
    private int downloadTotal;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initNotify();

        if (intent != null) {
            initIntent(intent);

            downFile();
        } else {
            downloadFail();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initIntent(Intent intent) {
        appName = intent.getIntExtra(APP_NAME, R.string.my_app_name);
        icon = intent.getIntExtra(ICON, R.mipmap.eclipse_adt_ic_launcher);
        icon_silhouette = intent.getIntExtra(ICON_SILHOUETTE, R.drawable.icon_silhouette);
        downloadUrl = intent.getStringExtra(URL);
        fullSaveDir = Constant.Db.SELF_UPDATE_DOWNLOAD_PATH;
//        fileNameOnly = intent.getStringExtra(FILE_NAME_ONLY);
        fullAppSavePath = fullSaveDir + fileNameOnly;
    }

    private void initNotify() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);

        builder = new NotificationCompat.Builder(this);
        builder.setProgress(DOWNLOAD_STATUS_FINISH, DOWNLOAD_STATUS_BEGIN, false);// 设置进度条, false表示是进度条, true表示是个走马灯
        builder.setTicker(getString(R.string.update_downloading)); // 通知首次出现在通知栏时的提示文字
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(getString(appName));// 设置内容
        builder.setContentText(getString(R.string.update_downloading));// 左侧 小 文本
        // builder.setSubText("SubText");
        // builder.setContentInfo("ContentInfo");// 右侧 小文本
        // .setFullScreenIntent(p, true)// 全屏拉下后显示 Only for use with extremely high-priority notifications demanding the user's immediate attention, such as an incoming phone call or alarm clock that the user has explicitly set to a particular time.
        // .setDeleteIntent(null)// 通知消失时的动作
        // .setLights(new Color().BLACK, 1000, 1000)//俺的手机不支持..无反应
        // .setNumber(22)//
        // .setPriority(2)//优先级
        builder.setAutoCancel(true);// 点击消失
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), icon, BitmapAndDrawableUtil.getDefaultOriginalOptions()));
        builder.setSmallIcon(getNotificationIcon());
        // builder.setColor(Color.BLUE);// 小图标 背景色 或者 进度条背景色
        builder.setContentIntent(pendingIntent);// 这句和点击消失那句是“Notification点击消失但不会跳转”的必须条件，如果只有点击消失那句，这个功能是不能实现的
        // builder.setPriority(Notification.PRIORITY_DEFAULT);//通知的优先级
        // builder.setDefaults(Notification.DEFAULT_VIBRATE);//为通知添加声音,闪灯和振动效果等效果
        builder.setOngoing(true);// true 表示不允许 被 滑动删除
    }

    private int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? icon_silhouette : icon;
    }

    public void downFile() {
        DeleteFileUtil.delAllFile(fullSaveDir);

        HttpUtils http = new HttpUtils();

        RequestParams requestParams = new RequestParams();
        BaseWebUrl.fillDeviceAndRomInfo(requestParams);

        @SuppressWarnings("unused")
        HttpHandler<File> handler = http.download(//
                downloadUrl, //
                fullAppSavePath, //
                requestParams, //
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {
                    private String getPercent(int progress) {
                        return getString(R.string.update_downloading) + ", " + progress + "%";
                    }

                    @Override
                    public void onStart() {
                        builder.setContentText(getPercent(DOWNLOAD_STATUS_BEGIN));// 左侧 小 文本
                        mNotificationManager.notify(APK_UPDATE_DOWNLOAD_ID, builder.build());
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        int percent = (int) ((float) current / total * 100);

                        downloadTotal = (int) total;
                        downloadCurrent = (int) current;

                        builder.setProgress(downloadTotal, downloadCurrent, false);
                        builder.setContentText(getPercent(percent));
                        mNotificationManager.notify(APK_UPDATE_DOWNLOAD_ID, builder.build());
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        builder.setOngoing(false);
                        builder.setProgress(DOWNLOAD_STATUS_FINISH, DOWNLOAD_STATUS_FINISH, false);
                        builder.setContentText(getPercent(DOWNLOAD_STATUS_FINISH));
                        mNotificationManager.notify(APK_UPDATE_DOWNLOAD_ID, builder.build());

                        fileNameOnly = responseInfo.result.getName();
                        fullAppSavePath = fullSaveDir + fileNameOnly;

                        readyToInstall();
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        // if (!msg.equals("maybe the file has downloaded completely")) {
                        // // 之前已经下载完成, 但是 不清楚 xUtils 自己是否有 CRC32校验(存在的, 和 要下载的, 是否一模一样)
                        // }

                        downloadFail();
                    }
                }
        );
    }

    private void downloadFail() {
        builder.setOngoing(false);
        builder.setProgress(downloadTotal, downloadCurrent, false);
        builder.setContentText(getString(R.string.update_download_fail));
        mNotificationManager.notify(APK_UPDATE_DOWNLOAD_ID, builder.build());

        stopSelf();

        // fullSaveDir 需要保存到文件, 不然 Activity 挂掉, 对象会变 null
        // DeleteFileUtil.delAllFile(fullSaveDir);
    }

    private void readyToInstall() {
        // 提升目录/文件权限 ======================================
        chmod("777", fullAppSavePath);

        install();

        stopSelf();
    }

//    private void install() {
//        Intent installIntent = new Intent(Intent.ACTION_VIEW);
//        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        installIntent.setDataAndType(Uri.fromFile(new File(fullAppSavePath)), "application/vnd.android.package-archive");
//
//        // 允许通知栏被点击 ======================================
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, installIntent, 0);
//        builder.setContentTitle(getString(R.string.my_app_name));
//        builder.setContentText(getString(R.string.update_download_success));
//        builder.setContentIntent(pendingIntent);
//        mNotificationManager.notify(APK_UPDATE_DOWNLOAD_ID, builder.build());
//
//        startActivity(installIntent);
//    }

    public void install() {

        boolean checkRootPermission = ShellUtils.checkRootPermission();

        if (checkRootPermission) {
            PackageUtils.installSilent(this, fullAppSavePath);
        } else {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.setDataAndType(Uri.fromFile(new File(fullAppSavePath)), "application/vnd.android.package-archive");
            startActivity(installIntent);
        }

    }


    private void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
