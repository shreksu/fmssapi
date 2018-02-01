package fmssapi.util;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author suyuanyang
 * @create 2017-12-11 上午9:27
 */
public class StringUtil {


    /**
     *  组一个编码
     * @param value 值
     * @param len 编码长度
     * @param preStr 前缀 可以为null
     * @return
     */
    public static String getCode(int value,int len,String preStr){
        String val = String.valueOf(value);
        while(val.length()<len){
            val = "0"+val;
        }
        if(preStr!=null){
            val = preStr + val;
        }
        return val;
    }

    /**
     * 日期转字符串
     *
     * @param time
     * @param option
     * @return
     */
    public static String dateToString(Date time, int option) {
        if (time != null) {
            SimpleDateFormat formatter;
            if (option == 0) {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
            } else if (option == 1) {
                formatter = new SimpleDateFormat("yyyy年MM月dd日");
            } else if (option == 2) {
                formatter = new SimpleDateFormat("yyyy.MM.dd");
            } else if (option == 3) {
                formatter = new SimpleDateFormat("yyyy\\MM\\dd");
            } else if (option == 4) {
                formatter = new SimpleDateFormat("dd/MM/yyyy");
            } else {
                formatter = new SimpleDateFormat("yyyyMMdd");
            }

            String ctime = formatter.format(time);

            return ctime;
        } else {
            return null;
        }
    }

    /**
     * 字符串转换为java.util.Date<br>
     * yyyy-MM-dd 如 '2002-1-1 '<br>
     * yyyy年MM月dd日 如 '2002年1月1日 '<br>
     * yyyy.MM.dd 如 '2002.1.1 '<br>
     * yyyy\\MM\\dd 如 '2002\\1\\1 '<br>
     * dd/MM/yyyy 如 '01/01/2002 '<br>
     * yyyyMMdd 如 '20020101 '<br>
     *
     * @param time String 字符串<br>
     * @return Date 日期<br>
     */
    public static Date stringToDate(String time) {
        if (time != null && !"".equals(time)) {
            SimpleDateFormat formatter;
            time = time.trim();
            if ((time.indexOf("-") > -1)) {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
            } else if ((time.indexOf("年") > -1)) {
                formatter = new SimpleDateFormat("yyyy年MM月dd日");
            } else if ((time.indexOf(".") > -1)) {
                formatter = new SimpleDateFormat("yyyy.MM.dd");
            } else if ((time.indexOf("\\") > -1)) {
                formatter = new SimpleDateFormat("yyyy\\MM\\dd");
            } else if ((time.indexOf("/") > -1)) {
                formatter = new SimpleDateFormat("dd/MM/yyyy");
            } else {
                formatter = new SimpleDateFormat("yyyyMMdd");
            }
            ParsePosition pos = new ParsePosition(0);
            java.util.Date ctime = formatter.parse(time, pos);

            return ctime;
        } else {
            return null;
        }

    }


    /**
     * 根据日期返回月份
     *
     * @param date
     * @return
     */
    public static String getMonth(Date date) {
        String str = StringUtil.dateToString(date, 0);
        String[] arr = str.split("-");
        return arr[0] + "-" + arr[1];
    }

    /**
     * 获得一个月的第一天和最后一天
     * @param date
     * @return
     */
    public static Date[] getDatesByMonth(Date date){
        //获取前一个月第一天
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        calendar1.set(Calendar.DAY_OF_MONTH,1);
        //获取前一个月最后一天
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);
        calendar2.add(Calendar.MONTH, 1);
        calendar2.set(Calendar.DAY_OF_MONTH, 0);
        Date[] dates = new Date[2];
        dates[0] = calendar1.getTime();
        dates[1] = calendar2.getTime();
        return dates;
    }

    public static String getPreMonth(String monthStr) {
        String[] arr = monthStr.split("-");
        Integer year = Integer.valueOf(arr[0]);
        Integer month = Integer.valueOf(arr[1]);
        String month_str;
        if(month!=1){
            if(--month < 10){
                month_str="0"+month;
            }else{
                month_str=""+month;
            }
        }else{
            month_str = "12";
            year--;
        }
        return year + "-" +month_str;
    }

    /**
     * 获得该月第一天
     * @param year
     * @param month
     * @return
     */
    public static Date getFirstDayOfMonth(int year,int month){
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return cal.getTime();
    }
    /**
     * 获得该月第一天
     * @param monthStr
     * @return
     */
    public static Date getFirstDayOfMonth(String monthStr){
        String[] arr = monthStr.split("-");
        Integer year = Integer.valueOf(arr[0]);
        Integer month = Integer.valueOf(arr[1]);
        return getFirstDayOfMonth(year, month);
    }

    /**
     * 获得该月最后一天
     * @param year
     * @param month
     * @return
     */
    public static Date getLastDayOfMonth(int year,int month){
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return cal.getTime();
    }
    /**
     * 获得该月最后一天
     * @param monthStr
     * @return
     */
    public static Date getLastDayOfMonth(String monthStr){
        String[] arr = monthStr.split("-");
        Integer year = Integer.valueOf(arr[0]);
        Integer month = Integer.valueOf(arr[1]);
        return getLastDayOfMonth(year, month);
    }

    /**
     * 已分为单位的金额转成元
     * @param value
     * @return
     */
    public static Double getDouble(Long value){
        if(value!=null){
            DecimalFormat df = new DecimalFormat("#.00");
            return Double.valueOf(df.format(value/100d));
        }
        return null;
    }

    /**
     * 保留两位小数
     * @param d
     * @return
     */
    public static Double round2(Double d){
        if(d == null) return d;
        return Math.round(d*100)/100d;
    }

    /**
     * 获得两个double的和
     * @param d1
     * @param d2
     * @return
     */
    public static Double getSum(Double d1,Double d2){
        Double result = 0d;
        result += d1==null?0d:d1;
        result += d2==null?0d:d2;
        return result==0?null:result;
    }
    /**
     * 获得两个long的和
     * @param l1
     * @param l2
     * @return
     */
    public static Long getSum(Long l1, Long l2) {
        Long result = 0l;
        result += l1==null?0l:l1;
        result += l2==null?0l:l2;
        return result==0?null:result;
    }

    /**
     * 获得两个double的差
     * @param d1
     * @param d2
     * @return
     */
    public static Double getCha(Double d1,Double d2){
        Double result = 0d;
        result += d1==null?0d:d1;
        result -= d2==null?0d:d2;
        return result;
    }

    public static Long getCha(Long l1, Long l2) {
        Long result = 0l;
        result += l1==null?0l:l1;
        result -= l2==null?0l:l2;
        return result;
    }

    public static String getPreYearMonth(String month) {
        String[] arr = month.split("-");
        Integer year = Integer.valueOf(arr[0]);
        return year-1 + "-12";
    }
    //避免科学计数法显示
    public static String formatNumber(Double value) {
        if(value != null){
            if(value.doubleValue() != 0){
                java.text.DecimalFormat df = new java.text.DecimalFormat("########.00");
                return df.format(value.doubleValue());
            }else{
                return "";
            }
        }
        return "";
    }


/***
    public static void main(String[] args){
//        ScriptEngineManager manager = new ScriptEngineManager();
//        ScriptEngine se = manager.getEngineByName("js");
//        Double amount = null;
//        try {
//            amount = (Double)se.eval("45-5");
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }
//        System.out.println(amount);
//        String s = "${brandName}-${styleName}";
//        String reg = "\\$\\{\\w+\\}";
//        Pattern pattern = Pattern.compile(reg);
//        Matcher matcher = pattern.matcher(s);
//        while(matcher.find()) {
//            String matchWord = matcher.group(0);
//            String property = matchWord.substring(2, matchWord.length() - 1);
//            s = s.replace(matchWord, property);
//        }
//        Double a1 = 2553423.52;
//        Double a2 =2452893.42;
//                Double a3 =219477.53;
//                Double a4 =-118947.43;
//                System.out.println(a1==StringUtil.round2(a2+a3+a4).doubleValue());
    }
    ****/

}
