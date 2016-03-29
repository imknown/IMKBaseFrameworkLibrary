package com.soft2t.imk2tbaseframework.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
	/**
	 * 获取某一时间 的 指定格式的 字符串
	 * 
	 * @param date
	 *            时间
	 * @param newDatePattern
	 *            期望的 字符串格式
	 * @return
	 */
	public static String convertDate2String(Date date, String newDatePattern) {
		DateFormat df = new SimpleDateFormat(newDatePattern, Locale.getDefault());
		String returnValue = df.format(date);

		return returnValue;
	}

	/**
	 * 根据 指定格式的 字符串 获取某一时间
	 * 
	 * @param datePattern
	 *            时间格式
	 * @param dateString
	 *            指定的时间
	 * @return
	 * @throws ParseException
	 */
	public static Date convertString2Date(String datePattern, String dateString) throws ParseException {
		DateFormat df = new SimpleDateFormat(datePattern, Locale.getDefault());
		Date date = df.parse(dateString);

		return date;
	}
}
