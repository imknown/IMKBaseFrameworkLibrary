package com.soft2t.imk2tbaseframework.base;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.soft2t.imk2tbaseframework.util.LogUtil;

public abstract class BaseRequestCallBack extends RequestCallBack<String> {

	public abstract void onBaseSuccess(ResponseInfo<String> responseInfo);

	public abstract void onBaseFailure(Exception error, String msg);

	private String actionName;
	private boolean serverResultResponsedIsJson = true;

	public BaseRequestCallBack(String actionName) {
		this.actionName = actionName;
	}

	public BaseRequestCallBack(String actionName, boolean serverResultResponsedIsJson) {
		this.actionName = actionName;
		this.serverResultResponsedIsJson = serverResultResponsedIsJson;
	}

	@Override
	public void onSuccess(ResponseInfo<String> responseInfo) {
		if (serverResultResponsedIsJson) {
			parseJsonFromServer(responseInfo);
		} else {
			parseDataFromServer(responseInfo);
		}
	}

	private void parseJsonFromServer(ResponseInfo<String> responseInfo) {
		String jsonString = "";

		try {
			jsonString = responseInfo.result;

			// =======================================
			// 用来防止 进入 ParseFailException
			// =======================================
			JSONObject fastjsonObject = JSON.parseObject(jsonString);
			// String code = fastjsonObject.getString("code");
			String mess = fastjsonObject.getString("mess");
			String data = fastjsonObject.getString("data");
			// String debug = fastjsonObject.getString("debug");

			if (TextUtils.isEmpty(data)) {
				LogUtil.e("服务器 data 空数据: " + mess + "【" + jsonString + "】");
			}

			// =======================================
			BaseEntity be;

			try {
				be = JSON.parseObject(jsonString, BaseEntity.class);
			} catch (Exception e) {
				throw new ParseFailException(e.getMessage());
			}

			// =======================================

			if (BaseWebUrl.checkCode(actionName, jsonString)) {
				LogUtil.i("■ 响应成功 ■", actionName + "【" + jsonString + "】");

				onBaseSuccess(responseInfo);
			} else {
				// String msgMess = fastjsonObject.getString(BaseWebUrl.RESPONSE_MESS);
				// String msgDebug = fastjsonObject.getString(BaseWebUrl.RESPONSE_DEBUG);
				String msgMess = be.getMess();
				String msgDebug = be.getDebug();

				String msg = BaseWebUrl.RESPONSE_MESS + "=" + msgMess + ", " + BaseWebUrl.RESPONSE_DEBUG + "=" + msgDebug;

				throw new ServerFailException(msg);
			}
		} catch (ParseFailException parseFailException) {
			showErrorThenGotoFail("■ 客户端解析异常 ■", jsonString, parseFailException);
		} catch (ServerFailException serverFailException) {
			showErrorThenGotoFail("■ 服务器异常 ■", jsonString, serverFailException);
		} catch (Exception e) {
			if (Constant.Debug.SHOW_DEVELOP_LOG) {
				e.printStackTrace();
			}

			showErrorThenGotoFail("■ 程序员自己的异常 ■", jsonString, e);
		}
	}

	private void parseDataFromServer(ResponseInfo<String> responseInfo) {
		String data = responseInfo.result;

		try {
			if (TextUtils.isEmpty(data)) {
				LogUtil.e("【服务器 data 空数据】");
			}

			// =======================================

			LogUtil.i("■ 响应成功 ■", actionName + "【" + data + "】");

			onBaseSuccess(responseInfo);
		} catch (Exception e) {
			if (Constant.Debug.SHOW_DEVELOP_LOG) {
				e.printStackTrace();
			}

			showErrorThenGotoFail("■ 程序员自己的异常 ■", data, e);
		}
	}

	@Override
	public void onFailure(HttpException he, String msg) {
		showErrorThenGotoFail("■ 调用失败 ■", msg, he);
	}

	private void showErrorThenGotoFail(String tag, String jsonString, Exception e) {
		String msg = "";

		if (!(e instanceof ParseFailException)//
				&& !(e instanceof ServerFailException)//
				&& !(e instanceof HttpException)) {
			msg = "类=" + e.getClass().getCanonicalName() + ", 信息=";
		}

		msg += e.getMessage();

		LogUtil.e(tag, actionName + "【" + msg + "】");

		try {
			onBaseFailure(e, jsonString);
		} catch (Exception yourE) {
			LogUtil.e("■ 程序员玩坏了这个程序 ■", actionName + "【" + yourE.getMessage() + "】");

			if (Constant.Debug.SHOW_DEVELOP_LOG) {
				yourE.printStackTrace();
			}
		}
	}

	// /** 服务器 data 为空, 失败 */
	// public class ServerEmptyDataException extends Exception {
	// public ServerEmptyDataException(String msg) {
	// super(msg);
	// }
	//
	// private static final long serialVersionUID = 673087369921742540L;
	// }

	/** 服务器 报错, 失败 */
	public class ServerFailException extends Exception {
		public ServerFailException(String msg) {
			super(msg);
		}

		private static final long serialVersionUID = 673087369921742540L;
	}

	/** 解析 服务器 信息 失败 */
	public class ParseFailException extends Exception {
		public ParseFailException(String msg) {
			super(msg);
		}

		private static final long serialVersionUID = 673087369921742540L;
	}
}