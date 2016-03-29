package com.soft2t.imk2tbaseframework.util.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.soft2t.imk2tbaseframework.base.BaseApplication.MyToastManager;

public class SpeedHelper {
	/** 系统流量文件 */
	public final String DEV_FILE = "/proc/self/net/dev";

	/** 流量数据 */
	private String[] ethData = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };
	private String[] gprsData = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };
	private String[] wifiData = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };

	// 用来存储前一个时间点的数据
	private String[] data = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };

	/** 以太网 */
	private final String ETHLINE = "eth0";
	/** wifi */
	private final String WIFILINE = "wlan0";
	/** gprs */
	private final String GPRSLINE = "rmnet0";

	/** 读取系统流量文件 */
	private void readDev() {
		FileReader fr = null;
		try {
			fr = new FileReader(DEV_FILE);
		} catch (Exception e) {
			e.printStackTrace();
			MyToastManager.showToast("系统流量文件读取失败");
			return;
		}

		BufferedReader bufr = new BufferedReader(fr, 500);
		String line;
		String[] data_temp;
		String[] netData;
		int k;
		int j;

		// 读取文件，并对读取到的文件进行操作
		try {
			while ((line = bufr.readLine()) != null) {
				data_temp = line.trim().split(":");

				if (line.contains(ETHLINE)) {
					netData = data_temp[1].trim().split(" ");
					for (k = 0, j = 0; k < netData.length; k++) {
						if (netData[k].length() > 0) {
							ethData[j] = netData[k];
							j++;
						}
					}
				} else if (line.contains(GPRSLINE)) {
					netData = data_temp[1].trim().split(" ");
					for (k = 0, j = 0; k < netData.length; k++) {
						if (netData[k].length() > 0) {
							gprsData[j] = netData[k];
							j++;
						}
					}
				} else if (line.contains(WIFILINE)) {
					netData = data_temp[1].trim().split(" ");
					for (k = 0, j = 0; k < netData.length; k++) {
						if (j < wifiData.length) {
							try {
								Integer.parseInt(netData[k]);
								wifiData[j] = netData[k];
							} catch (Exception ex) {
								wifiData[j] = "0";
							}
						}
						j++;
					}
				}
			}

			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 实时读取系统流量文件，更新 */
	public long refresh() {
		// 读取系统流量文件
		readDev();

		// 计算增量
		long[] delta = new long[12];
		delta[0] = Long.parseLong(ethData[0]) - Long.parseLong(data[0]);
		delta[1] = Long.parseLong(ethData[1]) - Long.parseLong(data[1]);
		delta[2] = Long.parseLong(ethData[8]) - Long.parseLong(data[2]);
		delta[3] = Long.parseLong(ethData[9]) - Long.parseLong(data[3]);

		delta[4] = Long.parseLong(gprsData[0]) - Long.parseLong(data[4]);
		delta[5] = Long.parseLong(gprsData[1]) - Long.parseLong(data[5]);
		delta[6] = Long.parseLong(gprsData[8]) - Long.parseLong(data[6]);
		delta[7] = Long.parseLong(gprsData[9]) - Long.parseLong(data[7]);

		delta[8] = Long.parseLong(wifiData[0]) - Long.parseLong(data[8]);
		delta[9] = Long.parseLong(wifiData[1]) - Long.parseLong(data[9]);
		delta[10] = Long.parseLong(wifiData[8]) - Long.parseLong(data[10]);
		delta[11] = Long.parseLong(wifiData[9]) - Long.parseLong(data[11]);

		data[0] = ethData[0];
		data[1] = ethData[1];
		data[2] = ethData[8];
		data[3] = ethData[9];

		data[4] = gprsData[0];
		data[5] = gprsData[1];
		data[6] = gprsData[8];
		data[7] = gprsData[9];

		data[8] = wifiData[0];
		data[9] = wifiData[1];
		data[10] = wifiData[8];
		data[11] = wifiData[9];

		// 每秒下载的字节数
		long traffic_data = delta[0] + delta[4] + delta[8];

		return traffic_data;
	}
}