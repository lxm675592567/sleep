package com.hife.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    private DateUtil() {
    }

    /**
     * 日期格式
     */
    public static final String DATE_FMT = "yyyy-MM-dd";

    /**
     * 时间格式
     */
    public static final String DATE_TIME_FMT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时区
     */
    public static final String TIME_ZONE = "GMT+8";

    public static String getTodayStr(String fmt) {
        return getDateStr(new Date(), fmt);
    }

    public static String getDateStr(Date date, String fmt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
        return simpleDateFormat.format(date);
    }

    /**
     * 获得详细年龄
     *
     * @param birthdayStr 生日str
     * @param format      格式str
     * @return 详细年龄
     */
    public static Age getAge(String birthdayStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return getAge(simpleDateFormat.parse(birthdayStr));
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获得详细年龄
     *
     * @param birthday 生日
     * @return 详细年龄
     */
    public static Age getAge(Date birthday) {
        Calendar now = Calendar.getInstance(), bir = Calendar.getInstance();
        bir.setTime(birthday);

        int day = now.get(Calendar.DAY_OF_MONTH) - bir.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH) - bir.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR) - bir.get(Calendar.YEAR);
        // 按照减法原理，先day相减，不够向month借；然后month相减，不够向year借；最后year相减。
        if (day < 0) {
            month -= 1;
            now.add(Calendar.MONTH, -1);
            day = day + now.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if (month < 0) {
            month = (month + 12) % 12;
            year--;
        }


        return new Age(year, month, day);
    }

    /**
     * 获取某一天 前几天，后几天的日期
     *
     * @param date 某一天
     * @param value 修正值
     * @return 修正后的日期
     */
    public static Date getCorrectDate(Date date, int value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, value);
        return cal.getTime();
    }

    /**
     * 获取当前日 前后几天的日期
     *
     * @param value 修正值
     * @return 修正后的日期
     */
    public static Date getCorrectDate(int value) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, value);
        return cal.getTime();
    }

    public static JSONObject parseDateRange(JSONArray daterange) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FMT);
        JSONObject jsonObject = new JSONObject();
        String startStr = daterange.getString(0);
        String endStr = daterange.getString(1);
        try {
            jsonObject.fluentPut("$gte", simpleDateFormat.parse(startStr))
                    .fluentPut("$lte", simpleDateFormat.parse(endStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject parseDateRanges(JSONArray daterange) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FMT);
        JSONObject jsonObject = new JSONObject();
        String startStr = daterange.getString(0);
        String endStr = daterange.getString(1);
        jsonObject.fluentPut("$gte", startStr)
                .fluentPut("$lte", endStr);
        return jsonObject;
    }

    /**
     * 年龄详情类
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Age {

        /**
         * 岁
         */
        private Integer year;

        /**
         * 月
         */
        private Integer month;

        /**
         * 天
         */
        private Integer day;

        /**
         * 获得月龄
         *
         * @return 月龄
         */
        public double getMonthAge() {
            Calendar now = Calendar.getInstance();
            double monthAge = this.year * 12 + month + day * 1.0 / now.getActualMaximum(Calendar.DAY_OF_MONTH);
            return Math.round(monthAge * 10) / 10.0;
        }

        public int getMonthAgeInt() {
            Calendar now = Calendar.getInstance();
            // double monthAge = this.year * 12 + month + day * 1.0 / now.getActualMaximum(Calendar.DAY_OF_MONTH);
            return this.year * 12 + month;
        }

        /**
         * 获得年龄 str
         *
         * @return 年龄 str
         */
        public String getAgeDetail() {

            if (year > 0) {
                return year + "岁" + month + "月" + day + "天";
            }

            if (month > 0) {
                return month + "月" + day + "天";
            }

            return day + "天";
        }
    }
}
