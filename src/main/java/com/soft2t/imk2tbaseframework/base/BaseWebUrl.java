package com.soft2t.imk2tbaseframework.base;

import java.util.List;

import org.apache.http.NameValuePair;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.soft2t.imk2tbaseframework.util.LogUtil;
import com.soft2t.imk2tbaseframework.util.device.soft.DeviceUtil;
import com.soft2t.imk2tbaseframework.util.web.NetworkUtil;

@SuppressWarnings("deprecation")
public class BaseWebUrl {
	/** 请求服务器 的方法名 */
	public static final String REQUEST_ACTION = "action";

	/** 请求服务器 成功, 返回的 code 的 key */
	public static final String RESPONSE_CODE = "code";

	/** 请求服务器 成功, 并且没有异常 */
	public static final String RESPONSE_OK = "ok";

	/** 服务器 反馈的错误信息 */
	public static final String RESPONSE_MESS = "mess";

	/** 服务器 反馈的错误信息 */
	public static final String RESPONSE_DEBUG = "debug";

	/**
	 * 发送默认地址的请求
	 * 
	 * @param actionName
	 *            方法名, 用来 打 log
	 * @param requestParams
	 *            请求参数
	 * @param requestCallBack
	 *            回调
	 */
	public static <T> HttpHandler<T> makeCall(String actionName, RequestParams requestParams, RequestCallBack<T> requestCallBack) {
		return makeCall(null, actionName, requestParams, requestCallBack);
	}

	/**
	 * 发送自定义地址的请求
	 * 
	 * @param url
	 *            自定义地址, 例如: <br/>
	 *            {@code Constant.Net.SCHEMA + "static.cnyanglao.com" + ":" + Constant.Net.TOMCAT_PORT + Constant.SLASH + "PeopleDaily.aspx";}
	 * @param actionName
	 *            方法名, 用来 打 log
	 * @param requestParams
	 *            请求参数
	 * @param requestCallBack
	 *            回调
	 */
	public static <T> HttpHandler<T> makeCall(String url, String actionName, RequestParams requestParams, RequestCallBack<T> requestCallBack) {
		// 添加通用头部 ==========================
		// DeviceUtil d = DeviceUtil.getOSParameters(BaseApplication.mApplicationContext);
		// requestParams.addQueryStringParameter("__DeviceSerial__", android.os.Build.SERIAL);// 4f8456asd
		// requestParams.addQueryStringParameter("__DeviceModel__", android.os.Build.PRODUCT);// TCL M3G
		// requestParams.addQueryStringParameter("__OS__", "android " + d.getDeviceNameReleaseVersion());// andorid 5.1.1
		// requestParams.addQueryStringParameter("__RomName__", android.os.Build.DISPLAY);// sudamod 省略若干...
		// requestParams.addQueryStringParameter("__AppVersionCode__", "" + DeviceUtil.getAppVersionCode(BaseApplication.mApplicationContext));// 22
		// requestParams.addQueryStringParameter("__AppVersionName__", DeviceUtil.getAppVersionName(BaseApplication.mApplicationContext));// 0.5_r484
		fillDeviceAndRomInfo(requestParams);

		requestParams.addQueryStringParameter(REQUEST_ACTION, actionName);

		String realUrl = TextUtils.isEmpty(url) ? Constant.Net.getConnectHost() : url;

		// 打 log ==========================
		String logParams = "";
		String logFullUrl = realUrl;
		List<NameValuePair> requestParamsList = requestParams.getQueryStringParams();
		int size = requestParamsList.size();

		for (int i = 0; i < size; i++) {
			NameValuePair nvPair = requestParams.getQueryStringParams().get(i);

			String nvPairString = nvPair.getName() + "=" + nvPair.getValue();

			logParams = logParams.concat(nvPairString);

			if (i < size - 1) {
				logParams += ", ";
			}

			String concatString = ((i == 0) ? "?" : "&");
			logFullUrl = logFullUrl.concat(concatString + nvPair.getName() + "=" + nvPair.getValue());
		}

		LogUtil.i("■ 完整请求 ■", actionName + "【" + logFullUrl + "】");
		// LogUtil.e("■ 请求参数 ■", "方法名=" + actionName + ", 请求参数=【" + logParams + "】");

		// 发送请求 ==========================
		HttpUtils http = new HttpUtils();
		HttpHandler<T> httpHandler = http.send(HttpRequest.HttpMethod.POST, realUrl, requestParams, requestCallBack);
		return httpHandler;
	}

	public static void fillDeviceAndRomInfo(@NonNull RequestParams requestParams) {
		DeviceUtil d = DeviceUtil.getOSParameters(BaseApplication.mApplicationContext);

		String serial = android.os.Build.SERIAL;
		if (serial.equals(DeviceUtil.FACTORY_SERIAL)) {
			serial = NetworkUtil.MacAddressUtil.getLocalMacAddressFromWifiInfo(BaseApplication.mApplicationContext); // mac地址
		}
		requestParams.addQueryStringParameter("__DeviceSerial__", serial);// 4f8456asd
		requestParams.addQueryStringParameter("__DeviceModel__", android.os.Build.PRODUCT);// TCL M3G
		requestParams.addQueryStringParameter("__OS__", "android " + d.getDeviceNameReleaseVersion());// andorid 5.1.1
		requestParams.addQueryStringParameter("__RomName__", android.os.Build.DISPLAY);// sudamod 省略若干...
		requestParams.addQueryStringParameter("__AppVersionCode__", "" + DeviceUtil.getAppVersionCode(BaseApplication.mApplicationContext));// 22
		requestParams.addQueryStringParameter("__AppVersionName__", DeviceUtil.getAppVersionName(BaseApplication.mApplicationContext));// 0.5_r484
	}

	/**
	 * 检查 {@link #RESPONSE_CODE} 是否成功
	 * 
	 * @param fastjsonObject
	 *            需要检查的Json
	 * @return 是否成功
	 * @throws JSONException
	 *             不存在 {@link #RESPONSE_CODE} key
	 */
	public static boolean checkCode(String actionName, String jsonString) throws JSONException {
		// {"code":"ok","mess":"","data":{},"debug":""}
		// {"code":"00001","mess":"请求异常","data":null,"debug":"未将对象引用设置到对象的实例。"}

		JSONObject fastjsonObject = JSON.parseObject(jsonString);

		String code = fastjsonObject.getString(BaseWebUrl.RESPONSE_CODE);
		String mess = fastjsonObject.getString(BaseWebUrl.RESPONSE_MESS);

		boolean result = code.equals(BaseWebUrl.RESPONSE_OK) /* && TextUtils.isEmpty(mess) */;

		return result;
	}
}
