package com.baidu.spark.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DateUtils {
	
	public final static int DAY = 24 * 3600 * 1000;
	
	/*lucene搜索使用的,不需要使用中杠*/
	public static final DateFormat DATE_TIME_PATTERN_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");         
	public static final DateFormat DATE_PATTERN_FORMAT = new SimpleDateFormat("yyyy-MM-dd");         
	public static final DateFormat DATE_TIME_SHORT_PATTERN_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");       

	/**
	 * 判读两个日期是否相同
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDate(Date date1,Date date2){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		if(date1!=null&&date2!=null){
			if(cal1.get(Calendar.YEAR)==cal2.get(Calendar.YEAR)){
				if(cal1.get(Calendar.MONTH)==cal2.get(Calendar.MONTH)){
					if(cal1.get(Calendar.DAY_OF_MONTH)==cal2.get(Calendar.DAY_OF_MONTH)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 求两个日期之间的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int diffDate(Date date1, java.util.Date date2) {
		if (date1 != null && date2 != null) {
			Date temp1 = clearTimeOfDate(date1);
			Date temp2 = clearTimeOfDate(date2);
			return Math
					.abs((int) ((temp1.getTime() - temp2.getTime()) / (24 * 3600 * 1000)));
		}
		return 0;
	}
	
	public static int diffDateWithTime(Date date1, java.util.Date date2) {
		if (date1 != null && date2 != null) {
			return Math
					.abs((int) ((date1.getTime() - date2.getTime()) / (24 * 3600 * 1000)));
		}
		return 0;
	}

	/**
	 * 获得指定日期的上个月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDateOfLastMonth(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(GregorianCalendar.MONTH, -1);// 上个月的日子
		String datestr = getMonthBegin(cal.getTime());
		return parseFormatDate(datestr);
	}
	
	/**
	 * 获得指定日期当月第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDateOfThisMonth(Date date) {
		String datestr = getMonthBegin(date);
		return parseFormatDate(datestr);
	}

	/**
	 * 获得指定日期的上个月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDateOfLastMonth(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(GregorianCalendar.MONTH, -1);// 上个月的日子
		String datestr = getMonthEnd(cal.getTime());
		return parseFormatDate(datestr);

	}

	/**
	 * 取得指定月份的第一天
	 * 
	 * @param strdate
	 *            String
	 * @return String
	 */
	public static String getMonthBegin(Date date) {
		return formatDate(date, "yyyy-MM") + "-01";
	}

	/**
	 * 取得指定月份的最后一天
	 * 
	 * @param strdate
	 *            String
	 * @return String
	 */
	public static String getMonthEnd(Date date) {
		date = parseFormatDate(getMonthBegin(date));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return formatDate(calendar.getTime());
	}

	public static int getLastDayOfMonth(Date date)
	{
		String lastDate = getMonthEnd(date);
		String lastDay = lastDate.substring(lastDate.lastIndexOf("-")+1,lastDate.length()); 
		return Integer.parseInt(lastDay);
	}
	
	/**
	 * 以指定的格式来格式化日期
	 * 
	 * @param date
	 *            Date
	 * @param format
	 *            String
	 * @return String
	 */
	public static String formatDate(java.util.Date date, String format) {
		String result = "";
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				result = sdf.format(date);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 常用的格式化日期
	 * 
	 * @param date
	 *            Date
	 * @return String
	 */
	public static String formatDate(java.util.Date date) {
		if (date != null)
			return formatDate(date, "yyyy-MM-dd");
		return "";
	}
	
	/**
	 * 常用的格式化日期
	 * 
	 * @param date
	 *            Date
	 * @return String
	 */
	public static String formatDatetime(java.util.Date date) {
		if (date != null)
			return formatDate(date, "yyyy-MM-dd HH:mm:ss");
		return "";
	}

	/**
	 * 以指定的格式来格式化日期
	 * 
	 * @param date
	 *            Date
	 * @param format
	 *            String
	 * @return String
	 */
	public static Date parseFormatDate(String str) {
		Date date = null;
		try {
			date = DATE_PATTERN_FORMAT.parse(str);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return date;
	}
	/**
	 * 返回指定日期对应的毫秒数
	 * @param date 指定的日期
	 * @param isStartOfDate 返回该日期开始的毫秒数，或是结束的毫秒数
	 * @return
	 */
	public static long parseDate2long(Date date,boolean isStartOfDate){
		if(isStartOfDate){
			return date.getTime();
		}else{
			return date.getTime()+86400000-1;
		}
		
	}

	/**
	 * 清除日期中有关时间的信息
	 * @param date
	 * @return
	 */
	public static Date clearTimeOfDate(Date date){
		return parseFormatDate(formatDate(date,"yyyy-MM-dd"));
	}
	/**
	 * 获得当前的年份
	 * @return
	 */
	public static int getYear(Date date){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	/**
	 * 获得当前季度
	 * @param date
	 * @return
	 */
	public static int getQuarter(Date date){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		return month/3+1;
		
	}
	
	public static int getMonth(Date date)
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH)+1;
		return month;
	}
	/**
	 * 根据年月日获得日期对象
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date getDate(int year,int month,int day){
		Calendar cld=Calendar.getInstance( );
        cld.set(year,month-1,day);
        Date d=cld.getTime();
        return d;
	}
	/**
	 * 获得当前时间所在季度的倒数第n天
	 * @param date
	 * @return
	 */
	public static Date getLastNDayOfQuarter(Date date,int n){
		int qua = getQuarter(date);
		int month = qua*3;
		Date firstDateOfNextMonth = getDate(getYear(date),month,1);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstDateOfNextMonth);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_YEAR, 0-n);
		return calendar.getTime();
	}
	
	public static Date getYesterday(Date date){
		return new Date(date.getTime() - DAY);
	}
	
	public static Date getTomorrow(Date date) {
		return new Date(date.getTime() + DAY);
	}
	
	/**
	 * 获得当前时间的下个月当天(如果日期不存在,如3月31日的下月当天,会被认为是4月30日)
	 * @param date
	 * @return
	 */
	public static Date getNextMonth(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}
	
	/**
	 * 获得当前时间的上个月当天(如果日期不存在,如5月31日的上月当天,会被认为是4月30日)
	 * @param date
	 * @return
	 */
	public static Date getLastMonth(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		return calendar.getTime();
	}
	
	
	
	public static Date getThisQuarterBegin(Date date){
		int qtr = getQuarter(date);
		int month = (qtr-1)*3+1;
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		Date currentQtrBegin = getDate(cal1.get(Calendar.YEAR), month, 1);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(currentQtrBegin);
		
		return clearTimeOfDate(cal.getTime());
	}
	
	public static Date getThisQuarterEnd(Date date){
		return getYesterday(getThisQuarterBegin(getNextQuarter(date)));
	}
	
	/**
	 * 获得
	 * @param date
	 * @return
	 */
	public static Date getLastNDayOfDate(Date date,int n){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, 0-n);
		;
		return clearTimeOfDate(calendar.getTime());
	}

	
	public static Date getLastQuarter(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -3);
		return calendar.getTime();
	}
	
	public static Date getNextQuarter(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, +3);
		return calendar.getTime();
	}
	
	public static Date getLastQuarterBegin(Date date){
		return getThisQuarterBegin(getLastQuarter(date));
	}

	public static Date getLastQuarterEnd(Date date) {
		return getYesterday(getThisQuarterBegin(date));
	}
	
	public static Date getDaysBefore(Date date, int day){
		return new Date(date.getTime() - DAY * day);
	}
	
	public static void main(String[] arg) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date1 = sdf.parse("2008-1-30 03:20");
		System.out.println(formatDate(getThisQuarterEnd(date1)));
//		System.out.println(dh.formatDate(dh.getNextMonth(date1)));
//		System.out.println(dh.getFirstDateOfThisMonth(DateHelper.getDate(2008, 6, 2)));
//		dh.log.debug("" + dh.diffDate(date1, date2));
		//date1 = dh.clearTimeOfDate(date1);
//		System.out.println(dh.formatDateByFormat(DateHelper.getLastNDayOfDate(new Date(), 2),"yyyy-MM-dd HH:mm:ss"));
		
//		log.debug("" + DateHelper.formatDate(DateHelper.getLastNDayOfQuarter(date1,5)));
//		CommonMailSender sender = new CommonMailSender("测试","内容","zhangjing_pe@baidu.com");
//		sender.start();
//		Date date = new Date();
//		Calendar cal1 = Calendar.getInstance();
//		cal1.setTime(date);
//		int day = cal1.get(Calendar.DAY_OF_WEEK);
//		System.out.println("day of week:"+day);
	}
}
