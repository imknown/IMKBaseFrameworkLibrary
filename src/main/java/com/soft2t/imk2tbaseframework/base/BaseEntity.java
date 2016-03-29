package com.soft2t.imk2tbaseframework.base;

import java.io.Serializable;

/** 服务器 的 基础信息 */
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = -248991456731368187L;

	private String code;

	private String mess;

	// private JSONObject data;

	private String debug;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	// public JSONObject getData() {
	// return data;
	// }
	//
	// public void setData(JSONObject data) {
	// this.data = data;
	// }

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
