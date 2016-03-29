package com.soft2t.imk2tbaseframework.util.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import org.apache.http.conn.util.InetAddressUtils;

import com.soft2t.imk2tbaseframework.R;
import com.soft2t.imk2tbaseframework.base.BaseApplication;
import com.soft2t.imk2tbaseframework.util.LogUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.TextView;

/**
 * 检查网络连接
 * 
 * @author imknown
 */
public class NetworkUtil {

	/** 网络类型未知 */
	private static final int NETWORKTYPE_UNKNOWN = -44;

	/** 没有网络 */
	private static final int NETWORKTYPE_INVALID = 0;

	/** WAP 网络 */
	private static final int NETWORKTYPE_WAP = 1;

	/** 2G 网络 */
	private static final int NETWORKTYPE_2G = 2;

	/** 3G 以上(含 3G)的网络, 统称为快速网络 */
	private static final int NETWORKTYPE_3G_OR_HIGHER = 3;

	/** WIFI 网络 */
	private static final int NETWORKTYPE_WIFI = 4;

	// 网络判断 ==========================================

	/** Gps是否打开 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/** WIFI 是否已经连接 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = mgrConn.getActiveNetworkInfo();

		if (activeNetInfo != null && activeNetInfo.getState() == NetworkInfo.State.CONNECTED) {
			return true;
		}

		return false;
	}

	/** 判断当前网络是否是wifi网络 */
	public static boolean isWifiNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}

		return false;
	}

	/** 判断当前网络是否移动网络 */
	public static boolean isMobileNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}

		return false;
	}

	/** 判断是否连接了网络, 不能检测是否有网络访问权限 */
	public static boolean hasConnectionsForNetwork(Context context) {
		try {
			ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo[] networkInfos = con.getAllNetworkInfo();
			// NetworkInfo activeNetworkInfo = con.getActiveNetworkInfo();

			for (NetworkInfo networkInfo : networkInfos) {
				if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
					if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return false;
	}

	/** 判断 是否是 快连接 */
	public static boolean isFastConnections(Context context) {
		ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo[] networkInfos = con.getAllNetworkInfo();

		for (NetworkInfo networkInfo : networkInfos) {
			int type = networkInfo.getType();
			int subType = networkInfo.getSubtype();

			LogUtil.i("网络类型的名字: " + networkInfo.getTypeName() + " .");

			if (type == ConnectivityManager.TYPE_WIFI) {
				return true;

			} else if (type == ConnectivityManager.TYPE_MOBILE) {
				switch (subType) {

				case TelephonyManager.NETWORK_TYPE_1xRTT: // ~ 50-100 kbps
					return false;
				case TelephonyManager.NETWORK_TYPE_CDMA: // ~ 14-64 kbps
					return false;
				case TelephonyManager.NETWORK_TYPE_EDGE: // ~ 50-100 kbps
					return false;
				case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
					return true;
				case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
					return true;
				case TelephonyManager.NETWORK_TYPE_GPRS: // ~ 100 kbps
					return false;

				case TelephonyManager.NETWORK_TYPE_HSDPA: // ~ 2-14 Mbps
					return true;
				case TelephonyManager.NETWORK_TYPE_HSPA: // ~ 700-1700 kbps
					return true;
				case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps
					return true;
				case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 kbps
					return true;

				// NOT AVAILABLE YET IN API LEVEL 7*
				case TelephonyManager.NETWORK_TYPE_EHRPD: // ~ 1-2 Mbps
					return true;
				case TelephonyManager.NETWORK_TYPE_EVDO_B: // ~ 5 Mbps
					return true;
				case TelephonyManager.NETWORK_TYPE_HSPAP: // ~ 10-20 Mbps
					return true;
				case TelephonyManager.NETWORK_TYPE_IDEN: // ~25 kbps
					return false;
				case TelephonyManager.NETWORK_TYPE_LTE: // ~ 10+ Mbps
					return true;

				// Unknown
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
					return false;

				default:
					return false;

				}
			} else {

				return false;
			}
		}

		return false;
	}

	/**
	 * 获取网络状态，wifi,wap,2g,3g.
	 * 
	 * @return int 网络状态<br>
	 *         {@link #NETWORKTYPE_2G}<br>
	 *         {@link #NETWORKTYPE_3G_OR_HIGHER}<br>
	 *         {@link #NETWORKTYPE_INVALID}<br>
	 *         {@link #NETWORKTYPE_WAP}<br>
	 *         {@link #NETWORKTYPE_WIFI}<br>
	 */
	public static int getNetworkType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		int mNetWorkType = NETWORKTYPE_UNKNOWN;

		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();

			if (type.equalsIgnoreCase("WIFI")) {
				mNetWorkType = NETWORKTYPE_WIFI;
			} else if (type.equalsIgnoreCase("MOBILE")) {
				@SuppressWarnings("deprecation")
				String proxyHost = android.net.Proxy.getDefaultHost();

				mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastConnections(context) ? NETWORKTYPE_3G_OR_HIGHER : NETWORKTYPE_2G) : NETWORKTYPE_WAP;
			}
		} else {
			mNetWorkType = NETWORKTYPE_INVALID;
		}

		return mNetWorkType;
	}

	// 打开设置 ==========================================

	/** 打开网络设置界面 */
	public static void openSetting_M1(Context context) {
		if (android.os.Build.VERSION.SDK_INT > 10) {
			context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
		} else {
			context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
	}

	/** 打开网络设置界面 */
	public static void openSetting_M2(Activity activity) {
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction(Intent.ACTION_VIEW);// intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}

	/**
	 * Mac 地址
	 * 
	 * @author imknown
	 *
	 */
	public static class MacAddressUtil {
		/**
		 * 根据Wifi信息 获取 本地Mac地址<br/>
		 * 错误返回12个00:00:00:00:00:00
		 */
		public static String getLocalMacAddressFromWifiInfo(Context context) {
			String macAddress = "00:00:00:00:00:00";

			try {
				WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());

				if (null != info) {
					macAddress = info.getMacAddress();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return macAddress;
		}

		/** 根据IP 获取 本地Mac地址 */
		public static String getLocalMacAddressFromIp() {
			String mac_s = "";

			try {
				byte[] mac;
				NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(IpAddressUtil.getLocalIpAddress_M1()));
				mac = ne.getHardwareAddress();
				mac_s = byte2hex(mac);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return mac_s;
		}

		/** 二进制转十六进制 */
		private static String byte2hex(byte[] b) {
			StringBuffer buffer = new StringBuffer(b.length);
			String stmp = "";
			int len = b.length;

			for (int i = 0; i < len; i++) {
				if (i != 0) {
					buffer.append(':');
				}

				stmp = Integer.toHexString(b[i] & 0xFF);
				buffer.append(stmp.length() == 1 ? "0" : "");
				buffer.append(stmp);
			}

			return buffer.toString().toUpperCase(Locale.getDefault());
		}

		/** 根据 busybox 获取 本地Mac地址 */
		public static String getLocalMacAddressFromBusybox() {
			String result = "";

			result = callCmd("busybox ifconfig", "HWaddr");

			if (result != null) {
				// 对该行数据进行解析
				// 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
				if (result.length() > 0 && result.contains("HWaddr") == true) {
					result = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
				}
			}

			return result;
		}

		/** 执行命令 */
		private static String callCmd(String cmd, String filter) {
			String result = "";

			try {
				Process proc = Runtime.getRuntime().exec(cmd);
				InputStreamReader is = new InputStreamReader(proc.getInputStream());
				BufferedReader br = new BufferedReader(is);

				String line = "";

				while ((line = br.readLine()) != null && line.contains(filter) == false) {
					result += line;
				}

				result = line;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}
	}

	/**
	 * IP 地址
	 * 
	 * @author imknown
	 *
	 */
	public static class IpAddressUtil {
		/**
		 * 获取 第一个 非本地接口 的 IP
		 * 
		 * @param ipv4
		 *            [true] ipv4, [false] ipv6
		 * @return 地址 或者 空字符串
		 */
		public static String getIPAddress(boolean useIPv4) {
			try {
				List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

				for (NetworkInterface intf : interfaces) {
					List<InetAddress> addrs = Collections.list(intf.getInetAddresses());

					for (InetAddress addr : addrs) {
						if (!addr.isLoopbackAddress()) {
							String sAddr = addr.getHostAddress().toUpperCase(Locale.getDefault());
							boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);

							if (useIPv4) {
								if (isIPv4) {
									return sAddr;
								}
							} else {
								if (!isIPv4) {
									int delim = sAddr.indexOf('%'); // drop ip6 port suffix

									return delim < 0 ? sAddr : sAddr.substring(0, delim);
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return "";
		}

		/** 获取 内网IP */
		public static String getLocalIpAddress_M1() {
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();

					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();

						if (!inetAddress.isLoopbackAddress()) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			} catch (SocketException ex) {
				ex.printStackTrace();
			}

			return "";
		}

		/** 获取 内网IP */
		@Deprecated
		public static String getLocalIpAddress_M2(Context context) {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifiManager.getConnectionInfo();
			String ip = Formatter.formatIpAddress(info.getIpAddress());
			return ip;
		}

		/**
		 * 获取 外网IP<br>
		 * http://pv.sohu.com/cityjson?ie=utf-8
		 */
		public static String getWebIpAddress_M1(String websiteAddr) {
			URL infoUrl = null;
			InputStream inStream = null;

			try {
				infoUrl = new URL(websiteAddr);
				URLConnection connection = infoUrl.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;

				int responseCode = httpConnection.getResponseCode();

				if (responseCode == HttpURLConnection.HTTP_OK) {
					inStream = httpConnection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
					StringBuilder strber = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						strber.append(line);
					}

					reader.close();
					inStream.close();

					return strber.toString();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return "";
		}

		/**
		 * 获取 外网IP <br>
		 * http://www.baidu.com
		 */
		@Deprecated
		public static String getWebIpAddress_M2(String websiteAddr) {
			String ip = "";

			Socket socket = null;
			try {
				socket = new Socket(websiteAddr, 80);
				ip += socket.getLocalAddress().toString();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return ip;
		}
	}

	// 网速相关 ==========================================
	public class SpeedUtil {
		private static final String SPEEDSTRING_DEFAULT = "无";
		private String speedString = SPEEDSTRING_DEFAULT;

		private static final int INTERVAL_DEFAULT = 1000;
		private int mInterval = INTERVAL_DEFAULT;

		private TextView mTv;

		public void startWatchNetworkSpeed(TextView tv) {
			startWatchNetworkSpeed(INTERVAL_DEFAULT, tv);
		}

		public void startWatchNetworkSpeed(int interval, TextView tv) {
			mInterval = interval;
			mTv = tv;
			mHandler.postDelayed(updateCurrentNetWorkSpeed, mInterval);
		}

		public void stopWatchNetworkSpeed() {
			mHandler.removeCallbacks(updateCurrentNetWorkSpeed);
		}

		private android.os.Handler mHandler = new android.os.Handler();
		private long lastTimeSpeed;
		private Runnable updateCurrentNetWorkSpeed = new Runnable() {
			public void run() {
				long getDataFlow = getNetworkSpeed(BaseApplication.mApplicationContext);

				if (lastTimeSpeed == 0) {
					lastTimeSpeed = getDataFlow;
				}

				long showSpeed = getDataFlow - lastTimeSpeed;
				lastTimeSpeed = getDataFlow;

				if (showSpeed > 1024 * 1024) {
					speedString = (showSpeed / 1024 / 1024 + " M/S");
				} else if (showSpeed > 1024) {
					speedString = (showSpeed / 1024 + " K/S");
				} else if (showSpeed > 0) {
					speedString = (showSpeed + " B/S");
				} else {
					speedString = SPEEDSTRING_DEFAULT;
				}

				mTv.setText(BaseApplication.mApplicationContext.getString(R.string.network_speed, speedString));

				mHandler.postDelayed(updateCurrentNetWorkSpeed, mInterval);
			}
		};

		/** 获取网络速度 */
		public long getNetworkSpeed(Context context) {
			// ProcessBuilder cmd;
			// long readBytes = 0;
			// BufferedReader bufferReader = null;
			// try {
			// String[] args = { "/system/bin/cat", "/proc/net/dev" };
			// cmd = new ProcessBuilder(args);
			// Process process = cmd.start();
			// bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			// String line;
			// while ((line = bufferReader.readLine()) != null) {
			// if (line.contains("wlan0") && NetworkUtil.isWifi(context)) {
			// String[] delim = line.split(":");
			// if (delim.length >= 2) {
			// String values = delim[1].trim();
			// values = nSpace2one(values);
			// String[] numbers = values.split(",");// 提取数据
			// readBytes = Long.parseLong(numbers[0].trim());
			// readBytes += Long.parseLong(numbers[8].trim());
			// break;
			// }
			// }
			//
			// if (line.contains("eth0") && NetworkUtil.checkNetworkAvailability(context)) {
			// String[] delim = line.split(":");
			// if (delim.length >= 2) {
			// String values = delim[1].trim();
			// values = nSpace2one(values);
			// String[] numbers = values.split(",");
			// readBytes = Long.parseLong(numbers[0].trim());
			// readBytes += Long.parseLong(numbers[8].trim());
			// break;
			// }
			// }
			// }
			// bufferReader.close();
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// } finally {
			// if (bufferReader != null) {
			// try {
			// bufferReader.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
			// }
			// return readBytes;

			return new SpeedHelper().refresh();
		}

		// /** 处理字符串数据的 */
		// private String nSpace2one(String s) {
		// String regEx = "[' ']+"; // 一个或多个空格
		// Pattern p = Pattern.compile(regEx);
		// Matcher m = p.matcher(s);
		// String ret = m.replaceAll(",").trim();
		// return ret;
		// }
	}
}
