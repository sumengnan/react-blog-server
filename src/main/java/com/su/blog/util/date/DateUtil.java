package com.su.blog.util.date;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @description: 时间格式转换工具类
 * @author: Wangzy
 * @create: 2018-12-17 10:13
 **/
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    //创建日期处理线程
    private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal();
    //加锁对象（线程安全）
    private static final Object object = new Object();

    private static final SimpleDateFormat datetimeFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat datetimeShortFormat = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    private static final SimpleDateFormat timeZoneFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS Z");


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd");
    private static final SimpleDateFormat dateShortFormat = new SimpleDateFormat(
            "yyyyMMdd");
    private static final SimpleDateFormat dateSpotFormat = new SimpleDateFormat(
            "yyyy.MM.dd");
    private static final SimpleDateFormat dateWithOutYearFormat = new SimpleDateFormat(
            "MM-dd");
    private static final SimpleDateFormat dateMonthFormat = new SimpleDateFormat(
            "yyyy-MM");

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
            "HH:mm:ss");
    private static final SimpleDateFormat timeFormat2 = new SimpleDateFormat(
            "HH:mm");
    private static final SimpleDateFormat timeShortFormat = new SimpleDateFormat(
            "HHmmss");

    private DateUtil() {
    }

    /**
     * 获取SimpleDateFormat（线程安全）
     *
     * @param pattern 日期格式
     * @return java.text.SimpleDateFormat
     * @Create Wangzy 2018/12/17 13:26
     * @Update Wangzy 2018/12/17 13:26
     */
    private static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {
        SimpleDateFormat dateFormat = threadLocal.get();
        if (dateFormat == null) {
            synchronized (object) {
                if (dateFormat == null) {
                    dateFormat = new SimpleDateFormat(pattern);
                    dateFormat.setLenient(false);
                    threadLocal.set(dateFormat);
                }
            }
        }
        dateFormat.applyPattern(pattern);
        return dateFormat;
    }


    /**
     * 获取日期中的某数值，如获取月份
     *
     * @param date     日期
     * @param dateType 日期类型
     * @return int
     * @Create Wangzy 2018/12/17 13:29
     * @Update Wangzy 2018/12/17 13:29
     */
    public static int getInteger(Date date, int dateType) {
        int num = 0;
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
            num = calendar.get(dateType);
        }
        return num;
    }

    /**
     * 获取精确的日期
     *
     * @param timestamps
     * @return java.util.Date
     * @Create Wangzy 2018/12/17 14:31
     * @Update Wangzy 2018/12/17 14:31
     */
    public static Date getAccurateDate(List<Long> timestamps) {
        Date date = null;
        long timestamp = 0;
        Map<Long, long[]> map = new HashMap();
        List<Long> absoluteValues = new ArrayList();

        if (timestamps != null && timestamps.size() > 0) {
            if (timestamps.size() > 1) {
                for (int i = 0; i < timestamps.size(); i++) {
                    for (int j = i + 1; j < timestamps.size(); j++) {
                        long absoluteValue = Math.abs(timestamps.get(i)
                                - timestamps.get(j));
                        absoluteValues.add(absoluteValue);
                        long[] timestampTmp = {timestamps.get(i),
                                timestamps.get(j)};
                        map.put(absoluteValue, timestampTmp);
                    }
                }

                // 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的。此时minAbsoluteValue为0
                // 因此不能将minAbsoluteValue取默认值0
                long minAbsoluteValue = -1;
                if (!absoluteValues.isEmpty()) {
                    minAbsoluteValue = absoluteValues.get(0);
                    for (int i = 1; i < absoluteValues.size(); i++) {
                        if (minAbsoluteValue > absoluteValues.get(i)) {
                            minAbsoluteValue = absoluteValues.get(i);
                        }
                    }
                }

                if (minAbsoluteValue != -1) {
                    long[] timestampsLastTmp = map.get(minAbsoluteValue);

                    long dateOne = timestampsLastTmp[0];
                    long dateTwo = timestampsLastTmp[1];
                    if (absoluteValues.size() > 1) {
                        timestamp = Math.abs(dateOne) > Math.abs(dateTwo) ? dateOne
                                : dateTwo;
                    }
                }
            } else {
                timestamp = timestamps.get(0);
            }
        }

        if (timestamp != 0) {
            date = new Date(timestamp);
        }
        return date;
    }

    /**
     * 增加日期中某类型的某数值，如增加日
     *
     * @param date     日期
     * @param dateType 增加的类型
     * @param amount   增加的数值
     * @return java.util.Date
     * @Create Wangzy 2018/12/17 13:33
     * @Update Wangzy 2018/12/17 13:33
     */
    public static Date addInteger(Date date, int dateType, int amount) {
        Date myDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(dateType, amount);
            myDate = calendar.getTime();
        }
        return myDate;
    }

    public static String addInteger(String date, int dateType, int amount) {
        String dateString = null;
        DateStyle dateStyle = getDateStyle(date);
        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            myDate = addInteger(myDate, dateType, amount);
            dateString = DateToString(myDate, dateStyle);
        }
        return dateString;
    }

    /**
     * 获取日期字符串的日期风格。失敗返回null。
     *
     * @param date 日期字符串
     * @return 日期风格
     */
    public static DateStyle getDateStyle(String date) {
        DateStyle dateStyle = null;
        Map<Long, DateStyle> map = new HashMap<Long, DateStyle>();
        List<Long> timestamps = new ArrayList<Long>();
        for (DateStyle style : DateStyle.values()) {
            if (style.isShowOnly()) {
                continue;
            }
            Date dateTmp = null;
            if (date != null) {
                try {
                    ParsePosition pos = new ParsePosition(0);
                    dateTmp = getDateFormat(style.getValue()).parse(date, pos);
                    if (pos.getIndex() != date.length()) {
                        dateTmp = null;
                    }
                } catch (Exception e) {
                }
            }
            if (dateTmp != null) {
                timestamps.add(dateTmp.getTime());
                map.put(dateTmp.getTime(), style);
            }
        }
        Date accurateDate = getAccurateDate(timestamps);
        if (accurateDate != null) {
            dateStyle = map.get(accurateDate.getTime());
        }
        return dateStyle;
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date    日期字符串
     * @param pattern 日期格式
     * @return 日期
     */
    public static Date StringToDate(String date, String pattern) {
        Date myDate = null;
        if (date != null) {
            try {
                myDate = getDateFormat(pattern).parse(date);
            } catch (Exception e) {
            }
        }
        return myDate;
    }

    public static Date StringToDate(String date, DateStyle dateStyle) {
        Date myDate = null;
        if (dateStyle != null) {
            myDate = StringToDate(date, dateStyle.getValue());
        }
        return myDate;
    }

    public static Date StringToDate(String date) {
        DateStyle dateStyle = getDateStyle(date);
        return StringToDate(date, dateStyle);
    }

    /**
     * 将日期转化为日期字符串,失败返回null。
     *
     * @param date
     * @param pattern
     * @return java.lang.String
     * @Create Wangzy 2018/12/17 14:58
     * @Update Wangzy 2018/12/17 14:58
     */
    public static String DateToString(Date date, String pattern) {
        String dateString = null;
        if (date != null) {
            try {
                dateString = getDateFormat(pattern).format(date);
            } catch (Exception e) {
            }
        }
        return dateString;
    }

    public static String DateToString(Date date, DateStyle dateStyle) {
        String dateString = null;
        if (dateStyle != null) {
            dateString = DateToString(date, dateStyle.getValue());
        }
        return dateString;
    }

    /**
     * 获取今日日期
     *
     * @return
     */
    public static String currentDate() {
        return dateFormat.format(new Date());
    }

    /**
     * 获得当前月份
     */
    public static String currentMonth() {
        return dateMonthFormat.format(now());
    }

    /**
     * 获取明天日期
     *
     * @return
     */
    public static String tomorrowDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        Date d = cal.getTime();
        return dateFormat.format(d);
    }

    /**
     * 获取昨天日期
     *
     * @return
     */
    public static String yesturdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date d = cal.getTime();
        return dateFormat.format(d);
    }

    /**
     * 根据时间获取星期
     *
     * @param pTime
     * @return
     */
    public static int dayForWeek(String pTime) {
        SimpleDateFormat dateFormat = getDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormat.parse(pTime));
        } catch (Exception e) {
        }
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    /**
     * 获得当前日期时间
     * <p>
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String currentDatetime() {
        return datetimeFormat.format(now());
    }

    /**
     * 格式化日期时间
     * <p>
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String formatDatetime(Date date) {
        return datetimeFormat.format(date);
    }

    /**
     * 格式化日期时间
     *
     * @param date
     * @param pattern 格式化模式，详见{@link SimpleDateFormat}构造器
     *                <code>SimpleDateFormat(String pattern)</code>
     * @return
     */
    public static String formatDatetime(Date date, String pattern) {
        SimpleDateFormat customFormat = (SimpleDateFormat) datetimeFormat
                .clone();
        customFormat.applyPattern(pattern);
        return customFormat.format(date);
    }

    /**
     * 获得当前日期 (短)
     * <p>
     * 日期格式yyyy-MM-dd
     *
     * @return
     */
    public static String currentShortDate() {
        return dateShortFormat.format(now());
    }


    /**
     * 格式化日期
     * <p>
     * 日期格式yyyy-MM-dd
     *
     * @return
     */
    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    /**
     * 获得当前时间
     * <p>
     * 时间格式HH:mm:ss
     *
     * @return
     */
    public static String currentTime() {
        return timeFormat.format(now());
    }


    /**
     * 获得当前时间 (短)
     * <p>
     * 时间格式HH:mm:ss
     *
     * @return
     */
    public static String currentShortTime() {
        return timeShortFormat.format(now());
    }


    /**
     * 获得当前日期时间 (短)
     * <p>
     * 日期格式yyyy-MM-dd
     *
     * @return
     */
    public static String currentShortDateTime() {
        return datetimeShortFormat.format(now());
    }

    /**
     * 格式化时间
     * <p>
     * 时间格式HH:mm:ss
     *
     * @return
     */
    public static String formatTime(Date date) {
        return timeFormat.format(date);
    }


    /**
     * 格式化时间
     * <p>
     * 时间格式HH:mm:ss
     *
     * @return
     */
    public static String formatTime2(Date date) {
        return timeFormat2.format(date);
    }

    /**
     * 获得当前时间的<code>java.util.Date</code>对象
     *
     * @return
     */
    public static Date now() {
        return new Date();
    }

    public static Calendar calendar() {
        Calendar cal = GregorianCalendar.getInstance(Locale.CHINESE);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return cal;
    }

    /**
     * 获得当前时间的毫秒数
     * <p>
     * 详见{@link System#currentTimeMillis()}
     *
     * @return
     */
    public static long millis() {
        return System.currentTimeMillis();
    }

    /**
     * 获得当前Chinese月份
     *
     * @return
     */
    public static int month() {
        return calendar().get(Calendar.MONTH) + 1;
    }

    /**
     * 获得月份中的第几天
     *
     * @return
     */
    public static int dayOfMonth() {
        return calendar().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 今天是星期的第几天
     *
     * @return
     */
    public static int dayOfWeek() {
        return calendar().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 今天是年中的第几天
     *
     * @return
     */
    public static int dayOfYear() {
        return calendar().get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 判断原日期是否在目标日期之前
     *
     * @param src
     * @param dst
     * @return
     */
    public static boolean isBefore(Date src, Date dst) {
        return src.before(dst);
    }

    /**
     * 判断原日期是否在目标日期之后
     *
     * @param src
     * @param dst
     * @return
     */
    public static boolean isAfter(Date src, Date dst) {
        return src.after(dst);
    }

    /**
     * 判断两日期是否相同
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isEqual(Date date1, Date date2) {
        return date1.compareTo(date2) == 0;
    }

    /**
     * 判断某个日期是否在某个日期范围
     *
     * @param beginDate 日期范围开始
     * @param endDate   日期范围结束
     * @param src       需要判断的日期
     * @return
     */
    public static boolean between(Date beginDate, Date endDate, Date src) {
        return beginDate.before(src) && endDate.after(src);
    }

    /**
     * 获得当前月的最后一天
     * <p>
     * HH:mm:ss为0，毫秒为999
     *
     * @return
     */
    public static Date lastDayOfMonth() {
        //获取前月的最后一天
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return ca.getTime();

    }

    /**
     * 获得当前月的第一天
     * <p>
     * HH:mm:ss SS为零
     *
     * @return
     */
    public static Date firstDayOfMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        return c.getTime();
    }

    /**
     * 获取本周一日期
     *
     * @return
     */
    public static Date firstDayOfWeek() {
        Calendar c = Calendar.getInstance();
        int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayofweek == 0) {
            dayofweek = 7;
        }
        c.add(Calendar.DATE, -dayofweek + 1);
        return c.getTime();
    }


    private static Date weekDay(int week) {
        Calendar cal = calendar();
        cal.set(Calendar.DAY_OF_WEEK, week);
        return cal.getTime();
    }

    /**
     * 获得周五日期
     * <p>
     * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday
     *
     * @return
     */
    public static Date friday() {
        return weekDay(Calendar.FRIDAY);
    }

    /**
     * 获得周六日期
     * <p>
     * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday
     *
     * @return
     */
    public static Date saturday() {
        return weekDay(Calendar.SATURDAY);
    }

    /**
     * 获得周日日期
     * <p>
     * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday
     *
     * @return
     */
    public static Date sunday() {
        return weekDay(Calendar.SUNDAY);
    }

    /**
     * 将字符串日期时间转换成java.util.Date类型
     * <p>
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     *
     * @param datetime
     * @return
     */
    public static Date parseDatetime(String datetime) {
        try {
            return datetimeFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将格式数据转成date
     *
     * @param datetime
     * @return
     * @throws ParseException
     */
    public static Date parseZonetime(String datetime) throws ParseException {
        return timeZoneFormat.parse(datetime);
    }

    /**
     * 将字符串日期转换成java.util.Date类型
     * <p>
     * 日期时间格式yyyy-MM-dd
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串日期转换成java.util.Date类型
     * <p>
     * 时间格式 HH:mm:ss
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date parseTime(String time) throws ParseException {
        return timeFormat.parse(time);
    }

    /**
     * 将字符串日期转换成java.util.Date类型
     * <p>
     * 时间格式 HH:mm:ss
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date parseTime2(String time) throws ParseException {
        return timeFormat2.parse(time);
    }

    /**
     * 根据自定义pattern将字符串日期转换成java.util.Date类型
     *
     * @param datetime
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parseDatetime(String datetime, String pattern)
            throws ParseException {
        SimpleDateFormat format = (SimpleDateFormat) datetimeFormat.clone();
        format.applyPattern(pattern);
        return format.parse(datetime);
    }

    /**
     * 传入日期，之前或之后的日期
     *
     * @param date 日期
     * @param day  之后、之前的天数
     * @return yyyy-MM-dd
     */
    public static Date getDateAfterOrBefore(Date date, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.DAY_OF_MONTH, day);
        return now.getTime();
    }


    public static String getDateAfter(String date, int day) {
        Calendar now = Calendar.getInstance();


        try {
            now.setTime(dateShortFormat.parse(date));
        } catch (ParseException e) {
            logger.error("获取时间失败", e);
        }
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        dateShortFormat.format(now.getTime());
        return dateShortFormat.format(now.getTime());
    }

    /**
     * @throws Exception
     * @reason:计算两个日期差几天，也可比较两个日期谁在前，谁在后
     * @param:只支持yyyyMMdd格式
     * @return：int 如果firstDate在secondDate之前，返回一个负整数；反之返回正整数
     */
    public static int getDiffBetweenTwoDate(String firstDate, String secondDate) {
        //计算两天之差
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyyMMdd");
        Date date1 = null;
        Date date2 = null;
        int cha = 0;
        try {
            //起始日期
            date1 = myFormatter.parse(firstDate);
            //终止日期
            date2 = myFormatter.parse(secondDate);
            //起始日期-终止日期=毫秒
            long seconds = date1.getTime() - date2.getTime();
            //再除以每天多少毫秒(24*60*60*1000) ＝差几天
            cha = (int) (seconds / (24 * 60 * 60 * 1000));
        } catch (Exception e) {
            logger.error("获取时间失败", e);
        }
        return cha;
    }

    /**
     * 获取两个日期之间的天数  yyyy-MM-dd
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 日期set
     */
    public static List<String> getSetBetweenDate(Date beginDate, Date endDate) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        List<String> dateList = new ArrayList<>();
        while (calendar.getTime().getTime() <= endDate.getTime()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(calendar.getTime());
            dateList.add(dateStr);
            //进行当前日期月份加1
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dateList;
    }


    /**
     * 排列排列周的次序(1,2,3,4,5,6,7)
     *
     * @param weekNum
     * @return 按升序排列
     */
    public static String sortWeek(String weekNum) {

        // 使用正则表达式进行分割
        String[] strs = weekNum.split(",");
        int[] is = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            // 遍历String数组，赋值给int数组
            is[i] = Integer.parseInt(strs[i]);
        }

        // 使用数组工具类进行排序，也可以自己使用冒泡或选择排序来进行排序
        Arrays.sort(is);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < is.length; i++) {
            // 遍历进行拼接
            if (i == is.length - 1) {
                sb.append(is[i]);
            } else {
                sb.append(is[i] + ",");
            }
        }
        return sb.toString();
    }


    /**
     * 设置一天起始时间
     *
     * @param beginDate
     * @return
     */
    public static String dateAddBegin(String beginDate) {
        try {
            Date date = dateFormat.parse(beginDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            String dateStr = formatDatetime(calendar.getTime());

            return dateStr;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentDate();
    }

    /**
     * 设置一天结束时间
     *
     * @param endDate
     * @return
     */
    public static String dateAddEnd(String endDate) {
        try {
            Date date = dateFormat.parse(endDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 24);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            String dateStr = formatDatetime(calendar.getTime());

            return dateStr;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentDate();
    }

    /**
     * 根据日期获取当天是周几
     *
     * @param datetime 日期
     * @return 周几
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = sdf.parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDays[w];
    }

    /**
     * 获取上一月的年月
     */
    public static String getOnYearMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        return format.format(m);
    }

    /**
     * 当前时间所在一周的周一和周日时间
     *
     * @return
     */
    public static Map<String, String> getWeekDate() {
        Map<String, String> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayWeek == 1) {
            dayWeek = 8;
        }

        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);
        Date mondayDate = cal.getTime();
        String weekBegin = sdf.format(mondayDate);

        cal.add(Calendar.DATE, 4);
        Date fridayDate = cal.getTime();
        String friday = sdf.format(fridayDate);

        cal.add(Calendar.DATE, 1);
        Date saturdayDate = cal.getTime();
        String saturday = sdf.format(saturdayDate);

        cal.add(Calendar.DATE, 1);
        Date sundayDate = cal.getTime();
        String weekEnd = sdf.format(sundayDate);

        map.put("mondayDate", weekBegin);
        map.put("sundayDate", weekEnd);
        map.put("fridayDate", friday);
        map.put("saturdayDate", saturday);
        return map;
    }

    /**
     * 获取当前月，以及每一个周六日
     *
     * @return
     */
    public static Map<String, Object> getMonthDate() {

        Map<String, Object> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //获取当前日期
        Calendar cal = Calendar.getInstance();
        String today = sdf.format(cal.getTime());

        //当月一号
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String firstDay = sdf.format(cal.getTime());

        //获取周六日

        List sundayList = new LinkedList();
        List saturdayList = new LinkedList();
        int month = DateUtil.getInteger(new Date(), Calendar.MONTH);
        int i = 1;
        int year = DateUtil.getInteger(new Date(), Calendar.YEAR);
        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.MONTH) < month + 1 && calendar.get(Calendar.YEAR) == year) {
            calendar.set(Calendar.WEEK_OF_MONTH, i++);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            if (calendar.get(Calendar.MONTH) == month) {
                if (calendar.before(Calendar.getInstance()) || calendar.equals(Calendar.getInstance())) {
                    sundayList.add(sdf.format(calendar.getTime()));
                }

            }
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            if (calendar.get(Calendar.MONTH) == month) {
                if (calendar.before(Calendar.getInstance()) || calendar.equals(Calendar.getInstance())) {
                    saturdayList.add(sdf.format(calendar.getTime()));
                }
            }
        }


        map.put("firstDay", firstDay);
        map.put("today", today);
        map.put("saturdayList", saturdayList);
        map.put("sundayList", sundayList);
        return map;
    }

    /**
     * zone 数据转换成 HH:MM 格式数据
     *
     * @param time
     * @return
     */
    public static String zoneToHH_MM(String time) {
        if (time != null && time.contains("Z")) {
            try {
                time = DateToString(parseZonetime(time.replace("Z", " UTC")), DateStyle.HH_MM);
            } catch (ParseException e) {
                logger.error("【zoneToHH_MM】转换失败。");
                e.printStackTrace();

            }
        }
        return time;
    }

    /**
     * "yyyy-MM-dd" 转 "yyyy.MM.dd"
     *
     * @param date
     * @return
     */
    public static String dateToSportFormat(String date) {
        try {
            return dateSpotFormat.format(dateFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取某天是周几
     *
     * @param date 是为则默认今天日期、可自行设置“2013-06-03”格式的日期
     * @return 返回7是星期日、1是星期一、2是星期二、3是星期三、4是星期四、5是星期五、6是星期六
     */
    public static int getDayOfWeek(String date) {
        Calendar cal = calendar();
        if ("".equals(date)) {
            cal.setTime(new Date());
        } else {
            cal.setTime(new Date(parseDate(date).getTime()));
        }

        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
        } else {
            day--;
        }
        return day;
    }

    /**
     * 时间加减
     */
    public static String findDate(String date, int days) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, days);
        Date d = c.getTime();
        return dateWithOutYearFormat.format(d);
    }

    /**
     * 获取当前的上一天日期
     */
    public static String getLastDay() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        date = calendar.getTime();
        return dateFormat.format(date);
    }

    /**
     * 获取指定日期的上一天日期
     */
    public static String getLastDay(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, -1);
        Date d = calendar.getTime();
        return dateFormat.format(d);
    }

    // 获得当前日期与本周日相差的天数
    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

    /**
     * 过去七天
     */
    public static String lastWeekDate(String date) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, -7);
        Date d = c.getTime();
        return dateFormat.format(d);
    }

    /**
     * 获取上个月月份
     *
     * @return
     */
    public static String getLastMonth() {
        LocalDate today = LocalDate.now();
        today = today.minusMonths(1);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM");
        return formatters.format(today);
    }

    /**
     * 获取指定月的上个月月份
     *
     * @return
     */
    public static String getLastMonth(String month) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateMonthFormat.parse(month));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        return dateMonthFormat.format(m);
    }

    /**
     * 获取某月天数
     */
    public static int getDaysOfMonth(String source) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDate.parse(source));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//根据年月 获取月份天数

    }

}

