package com.soft2t.imk2tbaseframework.util.date;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import com.soft2t.imk2tbaseframework.util.string.StringUtils;

public class DateUtil {
	private static String datePattern = "yyyy-MM-dd";
	private static String datePattern2 = "yyyyMMdd";
	// private static String timePattern = datePattern + " HH:MM a";
	public static final String STR_DATEFORMATE = "yyyy-MM-dd HH:mm:ss";
	public static TimeZone TIMEZONE = new SimpleTimeZone(8 * 60 * 60 * 1000, "BEIJING");

	public static int convertStr2UTC(String str) {
		long millionSeconds = 0;

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(STR_DATEFORMATE, Locale.getDefault());
			sdf.setTimeZone(TIMEZONE);
			millionSeconds = sdf.parse(str).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return (int) (millionSeconds / 1000);
	}

	/**
	 * 获取format格式日期
	 * 
	 * @param format
	 *            :可单独获取年:yyyy, 月:MM, 日:dd, 时:HH/hh, 分:mm, 秒:ss
	 * @return
	 */
	public static String convertUTC2Str(int utc, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		sdf.setTimeZone(TIMEZONE);
		Date date = new Date(Long.parseLong(String.valueOf(utc) + "000"));

		return sdf.format(date);
	}

	/**
	 * 通过UTC秒数获取指定格式的时间
	 * 
	 * @param timeInSec
	 * @param newDatePattern
	 * @return
	 */
	public static final String getDateByTimeInSec(long timeInSec, String newDatePattern) {
		return getDateByTimeInMillis(timeInSec * 1000, newDatePattern);
	}

	/**
	 * 通过UTC毫秒数获取指定格式的时间
	 * 
	 * @param timeInMillis
	 * @param newDatePattern
	 * @return
	 */
	public static final String getDateByTimeInMillis(long timeInMillis, String newDatePattern) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		return getDate(c, newDatePattern);
	}

	/**
	 * 获取预定格式的日期字符串
	 * 
	 * @param aDate
	 * @param newDatePattern
	 *            要得到的日期格式,可单独获取年:yyyy, 月:MM, 日:dd, 时:HH/hh, 分:mm, 秒:ss
	 * @return
	 */
	public static final String getDate(Date aDate, String newDatePattern) {
		SimpleDateFormat df = new SimpleDateFormat(newDatePattern, Locale.getDefault());
		String returnValue = df.format(aDate);

		return returnValue;
	}

	/**
	 * 获取预定格式的日期字符串
	 * 
	 * @param newDatePattern
	 *            :可单独获取年:yyyy, 月:MM, 日:dd, 时:HH/hh, 分:mm, 秒:ss
	 * @param newDatePattern
	 * @return
	 */
	public static final String getDate(Calendar cal, String newDatePattern) {
		return getDate(cal.getTime(), newDatePattern);
	}

	/**
	 * 获取预定格式的日期字符串
	 * 
	 * @param date
	 *            格式: yyyy-MM-dd HH:mm:ss
	 * @param newDatePattern
	 *            要得到的日期格式,可单独获取年:yyyy, 月:MM, 日:dd, 时:HH/hh, 分:mm, 秒:ss
	 * @return
	 */
	public static final String getDateFromStr(String date, String newDatePattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(newDatePattern, Locale.getDefault());
		String strDate = "";

		try {
			Date newDate = sdf.parse(date);
			strDate = getDate(newDate, newDatePattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return strDate;
	}

	/**
	 * 将long型装换成预定格式的日期字符串
	 * 
	 * @param ymd
	 *            格式: yyyy-MM-dd HH:mm:ss
	 * @param newDatePattern
	 *            要得到的日期格式
	 * @return
	 */
	public static final String getYear_Month_Day(long ymd, String newDatePattern) {
		int year;
		int month;
		int day;

		year = (int) ymd / 10000;
		month = (int) (ymd / 100) % 100;
		day = (int) ymd % 100;

		Calendar c = getCalendar(year, month, day, 0, 0);

		return getDate(c, newDatePattern);
	}

	public static final String getHour_Minute(long hm, String newDatePattern) {
		int hour;
		int minute;

		hour = (int) hm / 100;
		minute = (int) hm % 100;

		Calendar c = getCalendar(0, 0, 0, hour, minute);

		return getDate(c, newDatePattern);
	}

	/**
	 * 获取指定某天的凌晨0点0分0秒的UTC 时间 (毫秒)
	 * 
	 * @param c
	 * @return
	 */
	public static final long get0HourOfSomeday(Calendar c) {
		long result = 0;
		int curYear = Integer.parseInt(getDate(c, "yyyy"));
		int curMonth = Integer.parseInt(getDate(c, "MM"));
		int curDay = Integer.parseInt(getDate(c, "dd"));

		Calendar theDay = Calendar.getInstance();
		// 设置为当天的0时0分0秒
		theDay.set(curYear, curMonth - 1, curDay, 0, 0, 0);
		result = theDay.getTimeInMillis();

		return result;
	}

	public static final Date convertStringToDate(String aMask, String strDate) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(aMask, Locale.getDefault());
		Date date = df.parse(strDate);

		return date;
	}

	/**
	 * 通过指定的年月日小时分生成Calendar
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 * @return @throws ParseException
	 */
	public static Calendar getCalendar(int year, int month, int date, int hrs, int min) {

		Calendar cal = new GregorianCalendar();
		cal.set(year, month - 1, date, hrs, min);

		return cal;
	}

	public static Calendar getCalendar(long timeInMillis) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		return c;
	}

	/**
	 * 通过指定的年月日小时分生成Calendar
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 * @return @throws ParseException
	 */
	public static Calendar getCalendar(int year, int month, int date, int hrs, int min, int sec) {

		Calendar cal = new GregorianCalendar();
		cal.set(year, month - 1, date, hrs, min, sec);

		return cal;
	}

	/**
	 * 举例：传入"2004-05-06-12-23-55"与"-"
	 * <p>
	 * 表示时间为2004年05月06日12点23分55秒,分隔符为"-" <br>
	 * 
	 * @param time
	 * @param delim
	 * @return
	 */
	public static Calendar getCalendar(String time, String delim) {
		String[] ts = StringUtils.delimitedListToStringArray(time, delim);
		int[] ti = { 0, 0, 0, 0, 0, 0 };
		ti[0] = Integer.parseInt(ts[0]);
		ti[1] = Integer.parseInt(ts[1]);
		ti[2] = Integer.parseInt(ts[2]);
		ti[3] = Integer.parseInt(ts[3]);
		ti[4] = Integer.parseInt(ts[4]);
		ti[5] = Integer.parseInt(ts[5]);

		return getCalendar(ti[0], ti[1] - 1, ti[2], ti[3], ti[4], ti[5]);
	}

	public static long getLongDate(Calendar c, String datePattern) {
		return Long.parseLong(getDate(c, datePattern));
	}

	/**
	 * This method converts a String to a date using the datePattern
	 * 
	 * @param strDate
	 *            the date to convert (in format MM/dd/yyyy)
	 * @return a date object
	 * @throws ParseException
	 */
	public static Date convertStringToDate(String strDate) throws ParseException {
		Date aDate = null;
		try {
			aDate = convertStringToDate(datePattern, strDate);
		} catch (ParseException pe) {
			pe.printStackTrace();
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}
		return aDate;
	}

	public static Date convertStringToDate2(String yyyyMMdd) throws ParseException {
		Date aDate = null;
		try {
			aDate = convertStringToDate(datePattern2, yyyyMMdd);
		} catch (ParseException pe) {
			pe.printStackTrace();
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}
		return aDate;
	}

	/**
	 * 求某年下的第几个星期的日期 返回java.uilt.Date 类型日期 时间time为当前机器时间
	 * 
	 * @param year
	 *            要获得的年
	 * @param week
	 *            第几个星期
	 * @param flag
	 *            是否是第一天还是最后一天,当为true时返回第一天,false则返回最后一天
	 * @return java.uilt.Date 类型日期
	 * @例如 getDayByWeek(2002,2,true) 返回Tue Jan 08 14:11:57 CST 2002
	 */
	public static Date getDayByWeek(int year, int week, boolean flag) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, week);
		if (!flag)
			cal.setTimeInMillis(cal.getTimeInMillis() + 6 * 24 * 60 * 60 * 1000);
		return cal.getTime();
	}

	/**
	 * 计算出给定日期所在周的周一和周日
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Week getWeekByDate(Date date) throws ParseException {
		Date _date = org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Calendar c = Calendar.getInstance();
		c.setTime(_date);
		// 星期：1，2，3，4，5，6，7
		int iWeek = c.get(Calendar.DAY_OF_WEEK);
		// 时期日->星期八
		if (iWeek == 1) {
			iWeek = 8;
		}
		Week week = new Week(_date);
		// 算出离星期一（2）的距离
		week.setStart(org.apache.commons.lang3.time.DateUtils.addDays(_date, 2 - iWeek));
		// 算出离星期日（8）的距离
		week.setEnd(org.apache.commons.lang3.time.DateUtils.addDays(_date, 8 - iWeek));
		return week;
	}

	/**
	 * 获得指定日期星期几
	 * 
	 * @param yyyy_MM_dd
	 *            (YYYY-MM-DD)
	 * @throws Exception
	 */
	public static String getDayOfWeekStr(String yyyy_MM_dd) throws Exception {
		String str = "";
		Date date = convertStringToDate(yyyy_MM_dd);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case 1:
			str = "日";
			break;
		case 2:
			str = "一";
			break;
		case 3:
			str = "二";
			break;
		case 4:
			str = "三";
			break;
		case 5:
			str = "四";
			break;
		case 6:
			str = "五";
			break;
		case 7:
			str = "六";
			break;
		}
		return str;
	}

	/**
	 * 获得指定日期星期几
	 * 
	 * @param yyyy_MM_dd
	 *            (YYYY-MM-DD)
	 * @throws Exception
	 */
	public static int getDayOfWeekInt(String yyyyMMdd) throws Exception {
		Date date = convertStringToDate2(yyyyMMdd);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}

	/**
	 * @param date
	 * @return
	 */
	public int getYearByStrDate(String date) {
		if (date == null || "".equals(date) || "null".equals(date)) {
			return 0;
		}
		String[] strs = date.split("-");
		String year = strs[0];
		return Integer.parseInt(year);
	}

	/**
	 * @param date
	 * @return
	 */
	public int getMonthByStrDate(String date) {
		if (date == null || "".equals(date) || "null".equals(date)) {
			return 0;
		}
		String[] strs = date.split("-");
		String month = strs[1];
		return Integer.parseInt(month);
	}

	/**
	 * 获得当前周的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekFirstDate(String date) {
		String rtnStr = "";
		try {
			Date d = DateUtil.convertStringToDate(date);
			Week week = DateUtil.getWeekByDate(d);
			rtnStr = getSystemTimeFormat(week.getStart(), "yyyy-MM-dd") + " 00:00:00";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public static String getWeekFirstDate2(String yyyymmdd) {
		String rtnStr = "";
		try {
			Date d = DateUtil.convertStringToDate2(yyyymmdd);
			Week week = DateUtil.getWeekByDate(d);
			rtnStr = getSystemTimeFormat(week.getStart(), "yyyyMMdd");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	/**
	 * 获得当前周的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekLastDate(String date) {
		String rtnStr = "";
		try {
			Date d = DateUtil.convertStringToDate(date);
			Week week = DateUtil.getWeekByDate(d);
			rtnStr = getSystemTimeFormat(week.getEnd(), "yyyy-MM-dd") + " 23:59:59";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public static String[] getWeekFirst2LastDay(String date) throws Exception {
		String startDate = getWeekFirstDate(date);
		String endDate = getWeekLastDate(startDate);
		return new String[] { startDate, endDate };
	}

	public static int[] getWeekFirst2LastDayUTC(String date) throws Exception {
		String startDate = getWeekFirstDate(date);
		String endDate = getWeekLastDate(startDate);
		int sUTC = convertStr2UTC(startDate);
		int eUTC = convertStr2UTC(endDate);
		return new int[] { sUTC, eUTC };
	}

	/**
	 * 取得指定月份的第一天
	 * 
	 * @param strdate
	 *            String
	 * @return String
	 * @throws ParseException
	 */
	public String getMonthBegin(int year, int month) throws ParseException {
		SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		String strDate = String.valueOf(year) + "-" + String.valueOf(month) + "-" + "01";
		cal.setTime(datef.parse(strDate));

		// 当前月的第一天
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
		Date beginTime = cal.getTime();
		String beginTime1 = datef.format(beginTime) + " 00:00:00";

		return beginTime1;
	}

	/**
	 * 获得本月首日到现在的时间数组（yyyymmdd~yyyymmdd）
	 * 
	 * @param now
	 *            (yyyy-mm-dd)
	 * @return
	 */
	public String[] getMonthFirstDay2Now(String now) {
		String[] times = null;
		if (now == null || "".equals(now)) {
			return times;
		} else {
			times = new String[2];
		}
		try {
			int year = new DateUtil().getYearByStrDate(now);
			int month = new DateUtil().getMonthByStrDate(now);
			String startTime = this.getMonthBegin(year, month);// 月的第一天
			times[0] = startTime.replace("-", "").substring(0, 8);
			times[1] = now.replace("-", "").substring(0, 8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return times;
	}

	/**
	 * 获得本月首日到现在的UTC时间数组
	 * 
	 * @param now
	 *            (yyyy-mm-dd)
	 * @return
	 */
	public int[] getMonthFirstDay2NowUTC(String now) {
		int[] times = null;
		if (now == null || "".equals(now)) {
			return times;
		} else {
			times = new int[2];
		}

		try {
			int year = new DateUtil().getYearByStrDate(now);
			int month = new DateUtil().getMonthByStrDate(now);
			String start = this.getMonthBegin(year, month);// 月的第一天
			String end = now + " 23:59:59";
			times[0] = convertStr2UTC(start);
			times[1] = convertStr2UTC(end);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return times;
	}

	/**
	 * 获得本月首日到末日的日期数组
	 * 
	 * @param now
	 *            (yyyy-mm-dd)
	 * @return
	 * @throws Exception
	 */
	public String[] getMonthFirstDay2EndDay(String now) throws Exception {
		// int[] times = new int[2];
		int year = getYearByStrDate(now);
		int month = getMonthByStrDate(now);
		String startTime = getMonthBegin(year, month);// 页面默认时间所在月的第一天
		String endTime = getMonthEnd(year, month);// 月最后一天
		return new String[] { startTime, endTime };
	}

	/**
	 * 获得本月首日到末日的UTC时间数组
	 * 
	 * @param now
	 *            (yyyy-mm-dd)
	 * @return
	 * @throws Exception
	 */
	public int[] getMonthFirstDay2EndDayUTC(String now) throws Exception {
		int[] times = new int[2];
		String strs[] = getMonthFirstDay2EndDay(now);
		times[0] = convertStr2UTC(strs[0]);// 页面默认时间所在月的第一天
		times[1] = convertStr2UTC(strs[1]);// 月最后一天
		return times;
	}

	/**
	 * 取得指定月份的最后一天
	 * 
	 * @param strdate
	 *            String
	 * @return String
	 * @throws ParseException
	 */
	public String getMonthEnd(int year, int month) throws ParseException {
		SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		String strDate = String.valueOf(year) + "-" + String.valueOf(month) + "-" + "01";
		cal.setTime(datef.parse(strDate));

		// 当前月的最后一天
		cal.set(Calendar.DATE, 1);
		cal.roll(Calendar.DATE, -1);
		Date endTime = cal.getTime();
		String endTime1 = datef.format(endTime) + " 23:59:59";
		return endTime1;
	}

	/**
	 * 返回每周7天的开始和结束UTC时间数组List集合
	 * 
	 * @param queryDate
	 * @return
	 * @throws ParseException
	 */
	public static List<int[]> getWeekPerDayUTCList(String queryDate) throws ParseException {
		List<int[]> list = new ArrayList<int[]>();
		String[] days = getWeekPerDay(queryDate);
		for (String day : days) {
			int[] times = new int[2];
			times[0] = convertStr2UTC(day + " 00:00:00");
			times[1] = convertStr2UTC(day + " 23:59:59");
			list.add(times);
		}
		return list;
	}

	public static String[] getWeekPerDay(String queryDate) throws ParseException {
		Date _date = org.apache.commons.lang3.time.DateUtils.truncate(convertStringToDate(queryDate), Calendar.DAY_OF_MONTH);
		Calendar c = Calendar.getInstance();
		c.setTime(_date);
		// 星期：1，2，3，4，5，6，7
		int iWeek = c.get(Calendar.DAY_OF_WEEK);
		// 时期日->星期八
		if (iWeek == 1) {
			iWeek = 8;
		}

		String[] weekDay = new String[7];
		for (int i = 0; i < 7; i++) {
			Date d = org.apache.commons.lang3.time.DateUtils.addDays(_date, i + 2 - iWeek);
			weekDay[i] = DateUtil.getDate(d, "yyyy-MM-dd");
		}

		Week week = new Week(_date);
		// 算出离星期一（2）的距离
		week.setStart(org.apache.commons.lang3.time.DateUtils.addDays(_date, 2 - iWeek));
		// 算出离星期日（8）的距离
		week.setEnd(org.apache.commons.lang3.time.DateUtils.addDays(_date, 8 - iWeek));
		return weekDay;
	}

	// ====================

	/**
	 * 取得当前日期秒数
	 * 
	 * @return String
	 */
	public static int getNowUTC() {
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TIMEZONE);
		c.setTime(d);
		int utc = (int) (c.getTimeInMillis() / 1000);
		return utc;
	}

	public static String getNowTimeStr() {
		SimpleDateFormat sdf = new SimpleDateFormat(STR_DATEFORMATE, Locale.getDefault());
		sdf.setTimeZone(TIMEZONE);
		Long time = (long) getNowUTC() * 1000;
		Date d = new Date(time);
		return sdf.format(d);
	}

	public static String getNowTimeStr(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		sdf.setTimeZone(TIMEZONE);
		Long time = (long) getNowUTC() * 1000;
		Date d = new Date(time);
		return sdf.format(d);
	}

	// public static int getYear(int utc) {
	// String str = convertUTC2Str(utc, "yyyy");
	// return Integer.parseInt(str);
	// }

	// public static int getMonth(int utc) {
	// String str = convertUTC2Str(utc, "MM");
	// return Integer.parseInt(str);
	// }
	//
	// public static int getDay(int utc) {
	// String str = convertUTC2Str(utc, "dd");
	// return Integer.parseInt(str);
	// }
	//
	// public static int getHour(int utc) {
	// String str = convertUTC2Str(utc, "HH");
	// return Integer.parseInt(str);
	// }
	//
	// public static int getMinute(int utc) {
	// String str = convertUTC2Str(utc, "mm");
	// return Integer.parseInt(str);
	// }
	//
	// public static int getSecond(int utc) {
	// String str = convertUTC2Str(utc, "ss");
	// return Integer.parseInt(str);
	// }

	/**
	 * 根据参数utc时间，当前统计时间
	 * 
	 * @param offset
	 * @return 返回时间字符串 年月日（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getYYYYMMDDhhmmss(int offset) {
		Calendar cal = Calendar.getInstance(TIMEZONE);
		cal.add(Calendar.DATE, offset);
		long utc = cal.getTimeInMillis();
		return convertUTC2Str((int) (utc / 1000), STR_DATEFORMATE);
	}

	/**
	 * 当前统计时间后offset_day天的日期
	 * 
	 * @param offset_day
	 * @return 返回时间字符串 年月日（yyyyMMdd）
	 */
	public static String getYYYYMMDD(int offset_day) {
		String time = getYYYYMMDDhhmmss(offset_day);
		// return getYearStr(time) + getMonthStr(time) + getDayStr(time);
		return getDateFromStr(time, "yyyy") + getDateFromStr(time, "MM") + getDateFromStr(time, "dd");
	}

	/**
	 * 某一日期前i天的日期
	 * 
	 * @param i
	 *            format:日期格式
	 * @return 返回时间字符串 年月日
	 */
	public static String getDateTime(int i, String time, String format) {
		Calendar c = Calendar.getInstance();

		// String s = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
		Date d = null;
		try {
			d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(d);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - i);
		String dayBefore = new SimpleDateFormat(format, Locale.getDefault()).format(c.getTime());

		return dayBefore;
	}

	/**
	 * 当前时间前i个月的日期
	 * 
	 * @param i
	 * @return 返回时间字符串 年月日（yyyy-MM）
	 */
	public static String getDateTimeMM(int i) {
		Calendar c = Calendar.getInstance();

		String s = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
		Date d = null;
		try {
			d = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(d);
		int day = c.get(Calendar.MONTH);
		c.set(Calendar.MONTH, day - i);
		String dayBefore = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(c.getTime());

		return dayBefore;
	}

	/**
	 * 当前统计时间下个月的日期
	 * 
	 * @param
	 * @return 返回时间字符串 年月日（yyyy-MM）
	 */
	public static String getNextMouthTime() {
		// SimpleDateFormat sdf = new SimpleDateFormat(STR_DATEFORMATE, Locale.getDefault());
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 1);

		long nextUTC = c.getTimeInMillis();
		String next_dateStr = convertUTC2Str((int) (nextUTC / 1000), "yyyy-MM ");
		// String nextTime = next_dateStr + " "+wc.getHour()+":"+wc.getMinute()+":"+wc.getSecond();
		// log.info("**** nextTimeSTR = "+nextTime);
		// rtnVal = convertStr2UTC(nextTime);

		// System.out.println(next_dateStr);
		// return sdf.format(c.getTime());
		return next_dateStr;
	}

	public static String getMonthFirstDateStr(String yyyymmdd) {
		return yyyymmdd.substring(0, 6) + "01";
	}

	// 计算后一天
	public static String getlastDay(String before, int daycount) throws ParseException {
		Calendar c = Calendar.getInstance();
		Date date = null;
		date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(before);
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + daycount);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(c.getTime());
		return dayBefore;
	}

	// ================================
	public static String[] getYear() {
		String dateStr = new java.sql.Date(new java.util.Date().getTime()).toString();
		int date = Integer.parseInt(dateStr.substring(0, 4));
		String[] newDate = new String[15];
		for (int i = 0; i < 15; i++) {
			newDate[i] = date - 8 + i + "";
		}
		return newDate;
	}

	public static String[] getDay(String yearStr, String mounth) {
		String[] day = null;
		if (mounth.equals("2")) {
			int year = Integer.parseInt(yearStr);
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
				day = new String[] { "01", "02", "3", "04", "05", "06", "07", "08", "09", "10", //
						"11", "12", "13", "14", "15", "16", "17", "18", "19", "20", //
						"21", "22", "23", "24", "25", "26", "27", "28", "29" };
			} else {
				day = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", //
						"11", "12", "13", "14", "15", "16", "17", "18", "19", "20", //
						"21", "22", "23", "24", "25", "26", "27", "28" };
			}
		} else if (mounth.equals("4") || mounth.equals("6") || mounth.equals("9") || mounth.equals("11")) {
			day = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", //
					"11", "12", "13", "14", "15", "16", "17", "18", "19", "20", //
					"21", "22", "23", "24", "25", "26", "27", "28", "29", "30" };
		} else {
			day = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", //
					"11", "12", "13", "14", "15", "16", "17", "18", "19", "20", //
					"21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };
		}
		return day;
	}

	public static String getDate() {
		String dateStr = new java.sql.Date(new java.util.Date().getTime()).toString();
		return dateStr;
	}

	/**
	 * 将指定时间，按指定的格式转化为字符串
	 * 
	 * @param date
	 *            时间
	 * @param format
	 *            时间格式，如：yyyy-MM-dd HH:mm:ss，yyyyMMddHHmmss等
	 * @return 字符串
	 */
	public static String getSystemTimeFormat(Date date, String format) {
		String sysTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		sysTime = sdf.format(date);
		return sysTime;
	}

	// ================================

	private static final long MILLIS_IN_A_SECOND = 1000;

	private static final long SECONDS_IN_A_MINUTE = 60;

	private static final long MINUTES_IN_AN_HOUR = 60;

	private static final long HOURS_IN_A_DAY = 24;

	private static final int DAYS_IN_A_WEEK = 7;

	private static final int MONTHS_IN_A_YEAR = 12;

	// private static final int[] daysInMonth = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	/**
	 * 最小日期，设定为1000年1月1日
	 */
	public static final Date MIN_DATE = date(1000, 1, 1);

	/**
	 * 最大日期，设定为8888年1月1日
	 */
	public static final Date MAX_DATE = date(8888, 1, 1);

	/**
	 * 根据年月日构建日期对象。注意月份是从1开始计数的，即month为1代表1月份。
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月。注意1代表1月份，依此类推。
	 * @param day
	 *            日
	 * @return
	 */
	public static Date date(int year, int month, int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, date, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 计算两个日期（不包括时间）之间相差的周年数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getYearDiff(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new InvalidParameterException("date1 and date2 cannot be null!");
		}
		if (date1.after(date2)) {
			throw new InvalidParameterException("date1 cannot be after date2!");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		int year1 = calendar.get(Calendar.YEAR);
		int month1 = calendar.get(Calendar.MONTH);
		int day1 = calendar.get(Calendar.DATE);

		calendar.setTime(date2);
		int year2 = calendar.get(Calendar.YEAR);
		int month2 = calendar.get(Calendar.MONTH);
		int day2 = calendar.get(Calendar.DATE);

		int result = year2 - year1;
		if (month2 < month1) {
			result--;
		} else if (month2 == month1 && day2 < day1) {
			result--;
		}
		return result;
	}

	/**
	 * 计算两个日期（不包括时间）之间相差的整月数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getMonthDiff(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new InvalidParameterException("date1 and date2 cannot be null!");
		}
		if (date1.after(date2)) {
			throw new InvalidParameterException("date1 cannot be after date2!");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		int year1 = calendar.get(Calendar.YEAR);
		int month1 = calendar.get(Calendar.MONTH);
		int day1 = calendar.get(Calendar.DATE);

		calendar.setTime(date2);
		int year2 = calendar.get(Calendar.YEAR);
		int month2 = calendar.get(Calendar.MONTH);
		int day2 = calendar.get(Calendar.DATE);

		int months = 0;
		if (day2 >= day1) {
			months = month2 - month1;
		} else {
			months = month2 - month1 - 1;
		}
		return (year2 - year1) * MONTHS_IN_A_YEAR + months;
	}

	/**
	 * 统计两个日期之间包含的天数。包含date1，但不包含date2
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getDayDiff(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new InvalidParameterException("date1 and date2 cannot be null!");
		}
		Date startDate = org.apache.commons.lang3.time.DateUtils.truncate(date1, Calendar.DATE);
		Date endDate = org.apache.commons.lang3.time.DateUtils.truncate(date2, Calendar.DATE);
		if (startDate.after(endDate)) {
			throw new InvalidParameterException("date1 cannot be after date2!");
		}
		long millSecondsInOneDay = HOURS_IN_A_DAY * MINUTES_IN_AN_HOUR * SECONDS_IN_A_MINUTE * MILLIS_IN_A_SECOND;
		return (int) ((endDate.getTime() - startDate.getTime()) / millSecondsInOneDay);
	}

	/**
	 * 计算time2比time1晚多少分钟，忽略日期部分
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static int getMinuteDiffByTime(Date time1, Date time2) {
		long startMil = 0;
		long endMil = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time1);
		calendar.set(1900, 1, 1);
		startMil = calendar.getTimeInMillis();
		calendar.setTime(time2);
		calendar.set(1900, 1, 1);
		endMil = calendar.getTimeInMillis();
		return (int) ((endMil - startMil) / MILLIS_IN_A_SECOND / SECONDS_IN_A_MINUTE);
	}

	/**
	 * 计算指定日期的前一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getPrevDay(Date date) {
		return org.apache.commons.lang3.time.DateUtils.addDays(date, -1);
	}

	/**
	 * 计算指定日期的后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextDay(Date date) {
		return org.apache.commons.lang3.time.DateUtils.addDays(date, 1);
	}

	/**
	 * 判断date1是否在date2之后，忽略时间部分
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isDateAfter(Date date1, Date date2) {
		Date theDate1 = org.apache.commons.lang3.time.DateUtils.truncate(date1, Calendar.DATE);
		Date theDate2 = org.apache.commons.lang3.time.DateUtils.truncate(date2, Calendar.DATE);
		return theDate1.after(theDate2);
	}

	/**
	 * 判断date1是否在date2之前，忽略时间部分
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isDateBefore(Date date1, Date date2) {
		return isDateAfter(date2, date1);
	}

	/**
	 * 判断time1是否在time2之后，忽略日期部分
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isTimeAfter(Date time1, Date time2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(time1);
		calendar1.set(1900, 1, 1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(time2);
		calendar2.set(1900, 1, 1);
		return calendar1.after(calendar2);
	}

	/**
	 * 判断time1是否在time2之后
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isAfter(Date time1, Date time2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(time1);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(time2);

		return calendar1.after(calendar2);
	}

	/**
	 * 判断time1是否在time2之前，忽略日期部分
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isTimeBefore(Date time1, Date time2) {
		return isTimeAfter(time2, time1);
	}

	/**
	 * 判断两个日期是否同一天（忽略时间部分）
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		return org.apache.commons.lang3.time.DateUtils.isSameDay(date1, date2);
	}

	/**
	 * 判断两个日历天是否同一天（忽略时间部分）
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Calendar date1, Calendar date2) {
		return org.apache.commons.lang3.time.DateUtils.isSameDay(date1, date2);
	}

	/**
	 * 将字符串形式的日期表示解析为日期对象
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date parseDate(String dateString) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(dateString, new String[] { "yyyy-MM-dd", "yyyy-M-d", "yyyy-MM-d", "yyyy-M-dd" });
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 将字符串形式的时间表示解析为日期时间对象
	 * 
	 * @param timeString
	 * @return
	 */
	public static Date parseTime(String timeString) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(timeString, new String[] { "HH:mm:ss", "H:m:s", "HH:mm", "H:m" });
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 将字符串形式的日期时间表示解析为时间对象
	 * 
	 * @param timeString
	 * @return
	 */
	public static Date parseDateTime(String timeString) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(timeString, new String[] { "yyyy-MM-dd HH:mm:ss", "yyyy-M-d H:m:s", "yyyy-MM-dd H:m:s", "yyyy-M-d HH:mm:ss" });
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 计算两个日期之间包含的星期X的天数。
	 * 
	 * @param fromDate
	 *            起始日期
	 * @param toDate
	 *            结束日期
	 * @param dayOfWeek
	 *            星期，例如星期三，星期四
	 * @return
	 */
	public static int getWeekDaysBetween(Date fromDate, Date toDate, int dayOfWeek) {
		int result = 0;
		Date firstDate = getFirstWeekdayBetween(fromDate, toDate, dayOfWeek);
		if (firstDate == null) {
			return 0;
		}
		Calendar aDay = Calendar.getInstance();
		aDay.setTime(firstDate);
		while (aDay.getTime().before(toDate)) {
			result++;
			aDay.add(Calendar.DATE, DAYS_IN_A_WEEK);
		}
		return result;
	}

	/**
	 * 获取在两个日期之间的第一个星期X
	 * 
	 * @param fromDate
	 *            起始日期
	 * @param toDate
	 *            结束日期
	 * @param dayOfWeek
	 *            星期，例如星期三，星期四
	 * @return
	 */
	public static Date getFirstWeekdayBetween(Date fromDate, Date toDate, int dayOfWeek) {
		Calendar aDay = Calendar.getInstance();
		aDay.setTime(fromDate);
		while (aDay.getTime().before(toDate)) {
			if (aDay.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
				return aDay.getTime();
			}
			aDay.add(Calendar.DATE, 1);
		}
		return null;
	}

	/**
	 * 取得参数year指定的年份的总天数
	 * 
	 * @param year
	 * @return
	 */
	public static int getDaysInYear(int year) {
		Calendar aDay = Calendar.getInstance();
		aDay.set(year, 1, 1);
		Date from = aDay.getTime();
		aDay.set(year + 1, 1, 1);
		Date to = aDay.getTime();
		return getDayDiff(from, to);
	}

	/**
	 * 取得指定年月的总天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDaysInMonth(int year, int month) {
		Calendar aDay = Calendar.getInstance();
		aDay.set(year, month, 1);
		Date from = aDay.getTime();
		if (month == Calendar.DECEMBER) {
			aDay.set(year + 1, Calendar.JANUARY, 1);
		} else {
			aDay.set(year, month + 1, 1);
		}
		Date to = aDay.getTime();
		return getDayDiff(from, to);
	}

	/**
	 * 获得指定日期的年份
	 * 
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		return getFieldValue(date, Calendar.YEAR);
	}

	/**
	 * 获得指定日期的月份
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		return getFieldValue(date, Calendar.MONTH) + 1;
	}

	/**
	 * 获得指定日期是当年的第几天
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfYear(Date date) {
		return getFieldValue(date, Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获得指定日期是当月的第几天
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfMonth(Date date) {
		return getFieldValue(date, Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获得指定日期是当周的第几天
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
		return getFieldValue(date, Calendar.DAY_OF_WEEK);
	}

	private static int getFieldValue(Date date, int field) {
		if (date == null) {
			throw new InvalidParameterException("date cannot be null!");
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(field);
	}

	/**
	 * 获得指定日期之后一段时期的日期。例如某日期之后3天的日期等。
	 * 
	 * @param origDate
	 *            基准日期
	 * @param amount
	 *            时间数量
	 * @param timeUnit
	 *            时间单位，如年、月、日等。用Calendar中的常量代表
	 * @return
	 */
	public static final Date dateAfter(Date origDate, int amount, int timeUnit) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(origDate);
		calendar.add(timeUnit, amount);
		return calendar.getTime();
	}

	/**
	 * 获得指定日期之前一段时期的日期。例如某日期之前3天的日期等。
	 * 
	 * @param origDate
	 *            基准日期
	 * @param amount
	 *            时间数量
	 * @param timeUnit
	 *            时间单位，如年、月、日等。用Calendar中的常量代表
	 * @return
	 */
	public static final Date dateBefore(Date origDate, int amount, int timeUnit) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(origDate);
		calendar.add(timeUnit, -amount);
		return calendar.getTime();
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	 */
	public static Date getNowDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(8);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回短时间格式 yyyy-MM-dd
	 */
	public static Date getNowDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(8);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 */
	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取时间 小时:分;秒 HH:mm:ss
	 * 
	 * @return
	 */
	public static String getTimeShort() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDateLong(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateDate
	 * @return
	 */
	public static String dateToStrLong(java.util.Date dateDate) {

		String dateString = getDate(dateDate, "yyyy-MM-dd HH:mm:ss");
		return dateString;
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 得到现在时间
	 * 
	 * @return
	 */
	public static Date getNow() {
		Date currentTime = new Date();
		return currentTime;
	}

	/**
	 * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
	 * 
	 * @param sformat
	 *            yyyyMMddhhmmss
	 * @return
	 */
	public static String getUserDate(String sformat) {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(sformat, Locale.getDefault());
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
	 */
	public static String getTwoHour(String st1, String st2) {
		String[] kk = null;
		String[] jj = null;
		kk = st1.split(":");
		jj = st2.split(":");
		if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
			return "0";
		else {
			double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
			double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
			if ((y - u) > 0)
				return y - u + "";
			else
				return "0";
		}
	}

	/**
	 * 时间前推或后推分钟,其中JJ表示分钟.
	 */
	public static String getPreTime(String sj1, String jj) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		String mydate1 = "";
		try {
			Date date1 = format.parse(sj1);
			long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
			date1.setTime(Time * 1000);
			mydate1 = format.format(date1);
		} catch (Exception e) {
		}
		return mydate1;
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 */
	public static String getNextDay(String nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			String mdate = "";
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 判断是否润年
	 * 
	 * @param ddate
	 * @return
	 */
	public static boolean isLeapYear(String ddate) {

		/**
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年 3.能被4整除同时能被100整除则不是闰年
		 */
		Date d = strToDate(ddate);
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(d);
		int year = gc.get(Calendar.YEAR);
		if ((year % 400) == 0)
			return true;
		else if ((year % 4) == 0) {
			if ((year % 100) == 0)
				return false;
			else
				return true;
		} else
			return false;
	}

	/**
	 * 返回美国时间格式 26 Apr 2006
	 * 
	 * @param str
	 * @return
	 */
	public static String getEDate(String str) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(str, pos);
		String j = strtodate.toString();
		String[] k = j.split(" ");
		return k[2] + k[1].toUpperCase(Locale.getDefault()) + k[5].substring(2, 4);
	}

	/**
	 * 获取一个月的最后一天
	 * 
	 * @param dat
	 * @return
	 */
	public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
		String str = dat.substring(0, 8);
		String month = dat.substring(5, 7);
		int mon = Integer.parseInt(month);
		if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
			str += "31";
		} else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
			str += "30";
		} else {
			if (isLeapYear(dat)) {
				str += "29";
			} else {
				str += "28";
			}
		}
		return str;
	}

	/**
	 * 提取一个月中的最后一天
	 * 
	 * @param day
	 * @return
	 */
	public static Date getLastDate(long day) {
		Date date = new Date();
		long date_3_hm = date.getTime() - 3600000 * 34 * day;
		Date date_3_hm_date = new Date(date_3_hm);
		return date_3_hm_date;
	}

	/**
	 * 判断二个时间是否在同一个周
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameWeekDates(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (0 == subYear) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
			// 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	/**
	 * 产生周序列,即得到当前时间所在的年度是第几周
	 * 
	 * @return
	 */
	public static String getSeqWeek() {
		Calendar c = Calendar.getInstance(Locale.getDefault());
		String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
		if (week.length() == 1)
			week = "0" + week;
		String year = Integer.toString(c.get(Calendar.YEAR));
		return year + week;
	}

	/**
	 * 两个时间之间的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDays(String date1, String date2) {
		if (date1 == null || date1.equals(""))
			return 0;
		if (date2 == null || date2.equals(""))
			return 0;
		// 转换为标准时间
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		java.util.Date date = null;
		java.util.Date mydate = null;
		try {
			date = myFormatter.parse(date1);
			mydate = myFormatter.parse(date2);
		} catch (Exception e) {
		}
		long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}

	/**
	 * 取得数据库主键 生成格式为yyyymmddhhmmss+k位随机数
	 * 
	 * @param k
	 *            表示是取几位随机数，可以自己定
	 */
	public static String getNo(int k) {
		return getUserDate("yyyyMMddhhmmss") + getRandom(k);
	}

	/**
	 * 返回一个随机数
	 * 
	 * @param i
	 * @return
	 */
	public static String getRandom(int i) {
		Random jjj = new Random();
		// int suiJiShu = jjj.nextInt(9);
		if (i == 0)
			return "";
		String jj = "";
		for (int k = 0; k < i; k++) {
			jj = jj + jjj.nextInt(9);
		}
		return jj;
	}

	// ================================

	/**
	 * 两个时间的 间隔
	 * 
	 * @param dateString1
	 *            时间1
	 * @param dateString2
	 *            时间2
	 * @return
	 * @throws ParseException
	 */
	public static String formatDuring(String dateString1, String dateString2, DateFormat format) throws ParseException {
		if (format == null) {
			format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
		}

		Date date1 = format.parse(dateString1);
		Date date2 = format.parse(dateString2);

		long time1 = date1.getTime();
		long time2 = date2.getTime();

		long mss = Math.abs(time2 - time1);

		return formatDuring(mss);
	}

	/**
	 * // * 两个时间的 间隔
	 * 
	 * @param mss
	 *            间隔的毫秒
	 * @return
	 */
	public static String formatDuring(long mss) {
		String time = "";

		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;

		if (seconds != 0) {
			time = seconds + "秒";
		}

		if (minutes != 0) {
			time = minutes + "分" + time;
		}

		if (hours != 0) {
			time = hours + "小时" + time;
		}

		if (days != 0) {
			time = days + "天" + time;
		}

		return time;
	}

	/**
	 * 截止到现在的间隔
	 * 
	 * @param time
	 * @return
	 */
	public static String formatDuringFromNow(String time) {
		String result = time;

		try {
			java.util.Date begin = new Date();

			SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			java.util.Date end = dfs.parse(time);

			long between = (begin.getTime() - end.getTime()) / 1000;// 除以1000是为了转换成秒
			long month = between / (30 * 24 * 3600);
			long day = between / (24 * 3600);
			long hour = between % (24 * 3600) / 3600;
			long minute = between % 3600 / 60;
			long second = between % 60 / 60;

			if (month > 0) {
				result = month + "月前";
			} else if (day > 0) {
				result = day + "天前";
			} else if (hour > 0) {
				result = hour + "小时前";
			} else if (minute > 0) {
				result = minute + "分钟前";
			} else if (second > 0) {
				result = second + "秒前";
			} else {
				result = "刚刚";
			}
		} catch (Exception e) {

		}

		return result;
	}

	/**
	 * 将yyyy-MM-dd HH:mm:ss格式的时间转换成MM月dd日 HH:mm格式的
	 * 
	 * @param dt
	 *            yyyy-MM-dd HH:mm:ss格式的日期
	 * @return HH:mm格式的日期
	 */
	public static String convertDatetimeStringFormat(String dt) {
		SimpleDateFormat orignalFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date orignalDate = new Date();

		try {
			orignalDate = orignalFmt.parse(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat myFmt = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
		String iWannaString = myFmt.format(orignalDate);
		return iWannaString;
	}

	// ================================

}

class Week {
	private Date date; // 日期
	private Date start; // date每周的第一天（星期一）
	private Date end; // date每周的最后一天（星期日）

	public Week(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
}
