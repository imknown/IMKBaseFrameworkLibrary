package com.soft2t.imk2tbaseframework.base.debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.soft2t.imk2tbaseframework.R;
import com.soft2t.imk2tbaseframework.base.BaseApplication;
import com.soft2t.imk2tbaseframework.base.BaseApplication.MyBattery;
import com.soft2t.imk2tbaseframework.base.BaseApplication.MyBattery.BatteryInfos;
import com.soft2t.imk2tbaseframework.base.Constant;
import com.soft2t.imk2tbaseframework.util.LogUtil;
import com.soft2t.imk2tbaseframework.util.date.DateUtil;
import com.soft2t.imk2tbaseframework.util.web.NetworkUtil;
import com.soft2t.imk2tbaseframework.util.web.NetworkUtil.SpeedUtil;
import com.soft2t.imk2tbaseframework.util.web.WifiReceiver;
import com.soft2t.imk2tbaseframework.util.web.WifiReceiver.IWifiReceiverCallback;

import java.util.Date;

public class MyDebugManager {
    private static MyDebugManager myDebugManager;
    private Context cxt = BaseApplication.mApplicationContext;
    private SpeedUtil speedUtil = new NetworkUtil().new SpeedUtil();
    private boolean isShowing;
    private View floatView;
    private TextView close_tv;
    private TextView debug_service_connected_tv;

    // =================================================
    private TextView debug_network_speed_tv;
    private TextView debug_battery_infos_tv;
    private TextView debug_datetimes_startup_timestamp_tv;
    private TextView debug_connected_wifi_tv;

    // =================================================
    private BroadcastReceiver batteryReceiver;
    private MediaPlayer mediaPlayer;

    private MyDebugManager() {
    }

    public static MyDebugManager getInstance() {
        if (myDebugManager == null) {
            myDebugManager = new MyDebugManager();
        }

        return myDebugManager;
    }

    public void init() {
        createView();
    }

    public void destroy() {
        speedUtil.stopWatchNetworkSpeed();

        try {
            cxt.unregisterReceiver(batteryReceiver);
        } catch (Exception e) {
            LogUtil.i("tag", "广播已经被解除了?");
        }

        dismiss();
    }

    public void toggle() {
        if (isShowing) {
            dismiss();
        } else {
            show();
        }
    }

    // private TextView debug_connencted_wifi_level_tv;
    // private TextView debug_connected_wifi_name_tv;
    // private TextView debug_connencted_wifi_rssi_tv;

    public void show() {
        isShowing = true;
        FloatInject.getInstance().bindViews(floatView);
    }

    public void dismiss() {
        isShowing = false;
        FloatInject.getInstance().unbindViews(floatView);
    }

    // =================================================

    private void createView() {
        LayoutInflater lf = LayoutInflater.from(cxt);
        floatView = lf.inflate(R.layout.float_layout, null, false);

        close_tv = (TextView) find(R.id.close_tv);
        close_tv.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });

        // 主服务器 ===============================
        debug_service_connected_tv = (TextView) find(R.id.debug_service_connected_tv);
        String connectHost = Constant.Net.getConnectHost();
        debug_service_connected_tv.setText(cxt.getString(R.string.service_connected, connectHost));

        // WIFI 信息================================
        debug_connected_wifi_tv = (TextView) find(R.id.debug_connected_wifi_tv);
        // debug_connected_wifi_name_tv = (TextView) find(R.id.debug_connencted_wifi_name_tv);
        // debug_connencted_wifi_level_tv = (TextView) find(R.id.debug_connencted_wifi_level_tv);
        // debug_connencted_wifi_rssi_tv = (TextView) find(R.id.debug_connencted_wifi_rssi_tv);

        // 实时网络 ===============================
        debug_network_speed_tv = (TextView) find(R.id.debug_network_speed_tv);
        speedUtil.startWatchNetworkSpeed(debug_network_speed_tv);

        initWifiStateChange();

        // 电池状态 ===============================
        debug_battery_infos_tv = (TextView) find(R.id.debug_battery_infos_tv);
        initBatteryStatus();

        // 事件相关 ===============================
        debug_datetimes_startup_timestamp_tv = (TextView) find(R.id.debug_datetimes_startup_timestamp_tv);
        String nowString = DateUtil.getDate(new Date(), "yyyy-MM-dd hh:mm:ss aa");
        debug_datetimes_startup_timestamp_tv.setText(cxt.getString(R.string.datetimes_startup_timestamp, nowString));
    }

    private View find(int id) {
        return floatView.findViewById(id);
    }

    // =================================================

    private void initBatteryStatus() {
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BatteryInfos batteryInfos = MyBattery.doBatteryReceiver(intent);

                String levelRate = batteryInfos.levelRate + "%";
                boolean isCharging = batteryInfos.isCharging;
                String batteryChargingStatus = batteryInfos.batteryChargingStatus;
                Object[] formatArgs = {levelRate, isCharging, batteryChargingStatus};

                // 电量 = %1$s, 充电 = %2$s, 状态 = %3$s
                debug_battery_infos_tv.setText(cxt.getString(R.string.battery_infos, formatArgs));
            }
        };

        cxt.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void initWifiStateChange() {
        WifiReceiver wifiReceiver = new WifiReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        cxt.registerReceiver(wifiReceiver, intentFilter);

        wifiReceiver.setiWifiReceiverCallback(new IWifiReceiverCallback() {
            private static final int MIN_RSSI = -100;
            private static final int MAX_RSSI = -55;

            private int calculateSignalLevel(int rssi, int numLevels) {
                if (rssi <= MIN_RSSI) {
                    return 0;
                } else if (rssi >= MAX_RSSI) {
                    return numLevels - 1;
                } else {
                    float inputRange = (MAX_RSSI - MIN_RSSI);
                    float outputRange = (numLevels - 1);
                    return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
                }
            }

            @Override
            public void onWifiEnabled() {
            }

            @Override
            public void onRSSIChange() {
                WifiManager wifiManager = (WifiManager) cxt.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                if (wifiInfo != null) {
                    int wifiRSSI = wifiInfo.getRssi();

                    int level;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 101);
                    } else {
                        level = this.calculateSignalLevel(wifiInfo.getRssi(), 101);
                    }

                    Object[] objs = {wifiInfo.getSSID(), level + "%", wifiRSSI};
                    String debug_connected_wifi = cxt.getString(R.string.network_wifi, objs);
                    debug_connected_wifi_tv.setText(debug_connected_wifi);
                }
            }

            @Override
            public void onApSsidConnected() {
                WifiManager wifiManager = (WifiManager) cxt.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                if (wifiInfo != null) {
                    int wifiRSSI = wifiInfo.getRssi();

                    int level;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 101);
                    } else {
                        level = this.calculateSignalLevel(wifiInfo.getRssi(), 101);
                    }

                    Object[] objs = {wifiInfo.getSSID(), level + "%", wifiRSSI};
                    String debug_connected_wifi = cxt.getString(R.string.network_wifi, objs);
                    debug_connected_wifi_tv.setText(debug_connected_wifi);
                }
            }

            @Override
            public void onApSsidDisConnected() {

                // Vibrator vibrator = (Vibrator) cxt.getSystemService(Service.VIBRATOR_SERVICE);
                // 第一个参数为等待指定时间后开始震动, 震动时间为第二个参数. 后边的参数依次为等待震动和震动的时间.
                // 第二个参数为重复次数, -1 为不重复, 0 为一直震动
                // vibrator.vibrate(1000);

                // FIXME 坤哥说不让开！！！
                // if (mediaPlayer != null) {
                // if (!mediaPlayer.isPlaying()) {
                // mediaPlayer.start();
                // }
                // } else {
                // mediaPlayer = MediaPlayer.create(cxt, R.raw.wifi);
                // if (!mediaPlayer.isPlaying()) {
                // mediaPlayer.start();
                // }
                // }

                Object[] objs = {"未连接", "未连接", "未连接"};
                String debug_connected_wifi = cxt.getString(R.string.network_wifi, objs);
                debug_connected_wifi_tv.setText(debug_connected_wifi);
            }
        });
    }
}
