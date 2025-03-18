package org.swdc.rmdisk.views.common;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DateUIUtil {

    static final Map<Locale, String[]> WEEK_DAYS = new HashMap<>();

    static final Map<Locale, String[]> MONTHS = new HashMap<>();

    static  {

        WEEK_DAYS.put(Locale.ENGLISH, new String[]{
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        });

        String[] CNDays = new String[]{
                "一", "二", "三", "四", "五", "六", "日"
        };
        WEEK_DAYS.put(Locale.SIMPLIFIED_CHINESE, CNDays);
        WEEK_DAYS.put(Locale.TRADITIONAL_CHINESE, CNDays);
        WEEK_DAYS.put(Locale.CHINESE, CNDays);

        String[] CN = new String[]{
                "一月", "二月", "三月", "四月", "五月", "六月",
                "七月", "八月", "九月", "十月", "十一月", "十二月"
        };

        MONTHS.put(Locale.CHINESE, CN);
        MONTHS.put(Locale.SIMPLIFIED_CHINESE,CN);
        MONTHS.put(Locale.TRADITIONAL_CHINESE, CN);

        MONTHS.put(Locale.ENGLISH, new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        });


    }


    /**
     * 根据星期几的数字获取对应的星期标签。
     *
     * @param weekDay 星期几的数字，0代表星期一，1代表星期二，以此类推。
     * @return 返回对应的星期标签，标签的语言根据系统默认语言环境或默认为英语。
     */
    public static String getWeekDayLabel(int weekDay) {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        String[] labelWeekDays = DateUIUtil.WEEK_DAYS.get(locale);
        return labelWeekDays[weekDay];
    }


    /**
     * 根据月份的数字获取对应的月份标签。
     *
     * @param month 月份的数字，范围从1到12。
     * @return 返回对应的月份标签，标签的语言根据系统默认语言环境或默认为英语。
     */
    public static String getMonthLabel(int month) {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        String[] monthLabels = DateUIUtil.MONTHS.get(locale);
        return monthLabels[month - 1];
    }

    /**
     * 根据年份和月份获取该月有多少天
     *
     * @param year 年份
     * @param month 月份
     * @return 返回该月份的天数
     */
    public static int getMonthDates(int year, int month) {
        List<Integer> largeMonths = List.of(1, 3, 5, 7, 8, 10, 12);
        if (largeMonths.contains(month)) {
            return 31;
        } else if (month == 2) {
            if (isLeapYear(year)) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 30;
        }
    }


    /**
     * 判断某一年是否为闰年
     *
     * @param year 要判断的年份
     * @return 如果该年份是闰年则返回true，否则返回false
     */
    public static boolean isLeapYear(int year) {
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)  {
            return true;
        }
        return false;
    }


    /**
     * 获取指定日期是星期几
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @return 返回，0代表星期一，1代表星期二，2代表星期三，3代表星期四，4代表星期五，5代表星期六，6代表星期日
     */
    public static int getDayOfWeek(int year, int month, int day) {

        if(month < 3) {
            year--;
            month += 12;
        }

        int rst = (day + 2 * month + 3 * (month + 1) / 5 + year + year / 4 - year / 100 + year / 400);
        if (rst < 0) {
            rst = rst + 7;
        }
        return rst % 7;
    }


}
