package com.soft2t.imk2tbaseframework.util.web;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.soft2t.imk2tbaseframework.util.LogUtil;

import java.util.List;

/**
 * Wifi 操作相关<br>
 * <p/>
 * How to Use: <br>
 * <br>
 * {@link #startScan()} <br>
 * <br>
 * {@code WifiOpenHelper wifiOpenHelper = new WifiOpenHelper(null); } <br>
 * {@code wifiOpenHelper.openWifi(); } <br>
 * {@code WifiConfiguration wc = wifiOpenHelper.createWifiInfo("2TSOFT_WORK", "2222tttt", PWD_TYPE_3); } <br>
 * {@code wifiOpenHelper.addNetwork(wc); } <br>
 *
 * @author imknown
 */
public class WifiOpenHelper {

    /**
     * 定义WifiManager对象
     */
    private WifiManager mWifiManager;

    /**
     * 定义WifiInfo对象
     */
    private WifiInfo mWifiInfo;

    /**
     * 网络连接列表
     */
    private List<WifiConfiguration> mWifiConfiguration;

    /**
     * 定义一个WifiLock
     */
    private WifiLock mWifiLock;

    /**
     * 构造器
     */
    public WifiOpenHelper(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 打开WIFI
     */
    public void openWifi() {
        if (!isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WIFI
     */
    public void closeWifi() {
        if (isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 检查当前WIFI状态
     */
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // ========================================

    /**
     * 创建一个WifiLock
     */
    public void creatWifiLock(String tag) {
        mWifiLock = mWifiManager.createWifiLock(tag);
    }

    /**
     * 锁定WifiLock
     */
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    /**
     * 解锁WifiLock
     */
    public void releaseWifiLock() {
        // 判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    // ========================================

    /**
     * 得到配置好的网络
     */
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    /**
     * 指定配置好的网络进行连接
     */
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }

        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }

    /**
     * 开始扫描, 得到配置好的网络连接 {@link #mWifiConfiguration}
     */
    public void startScan() {
        mWifiManager.startScan();

        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    /**
     * 得到网络列表
     */
    public List<ScanResult> getWifiList() {
        return mWifiManager.getScanResults();
    }

    /**
     * 得到MAC地址
     */
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    /**
     * 得到接入点的BSSID
     */
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    /**
     * 得到IP地址
     */
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    /**
     * 得到连接的ID
     */
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 得到WifiInfo的所有信息包
     */
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    /**
     * 添加一个网络并连接
     */
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);

        LogUtil.i("Added WifiConfiguration ID = " + wcgID);
        LogUtil.i("enableNetwork result = " + b);
    }

    /**
     * 断开指定ID的网络
     */
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_PSK = 1;
    public static final int SECURITY_EAP_OR_IEEE8021X = 23;
    public static final int SECURITY_WEP = 3;

    public static int getSecurity(WifiConfiguration config) {
        if (config.wepKeys[0] != null) {
            return SECURITY_WEP;
        }

        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }

        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP_OR_IEEE8021X;
        }

        return SECURITY_NONE;
    }

    public static int getSecurity(ScanResult result) {
        String capabilities = result.capabilities;

        if (capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (capabilities.contains("EAP")) {
            return SECURITY_EAP_OR_IEEE8021X;
        }

        return SECURITY_NONE;
    }

    public WifiConfiguration createWifiConfiguration(String SSID, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.isExsits(SSID);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (type == SECURITY_NONE) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == SECURITY_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == SECURITY_PSK) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    /**
     * 断开当前网络
     */
    public void disconnectAP() {
        if (!isWifiEnabled()) {
            mWifiManager.disconnect();
        }
    }

    /**
     * 检查wifi列表中是否有以输入参数为名的wifi热点, <br>
     * 如果存在, 则在 {@link #createWifiConfiguration(String SSID, String password, int type)} 方法开始配置wifi网络之前 将其移除, 以避免ssid的重复
     */
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }

        return null;
    }
}