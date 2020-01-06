package net.ys.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 计算请假时长，2019
 * User: NMY
 * Date: 2020-1-3
 * Time: 15:35
 */
public class LeaveDuration {

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static SimpleDateFormat md = new SimpleDateFormat("MM-dd");

    static List<String> festival;//节日

    static List<String> work;//法定上班日

    static {
        //2019
        //        String holiday = "01-01,02-04,02-05,02-06,02-07,02-08,02-09,02-10,04-05,04-06,04-07,05-01,05-02,05-03,05-04,06-07,06-08,06-09,09-13,09-14,09-15,10-01,10-02,10-03,10-04,10-05,10-06,10-07";//2019年全年节假日
        //        String overDay = "02-02,02-03,04-28,05-05,09-29,10-12";//2019年全年加班日

        //2020
        String holiday = "01-01,01-24,01-25,01-26,01-27,01-28,01-29,01-30,04-04,04-05,04-06,05-01,05-02,05-03,05-04,05-05,06-25,06-26,06-27,10-01,10-02,10-03,10-04,10-05,10-06,10-07,10-08";//2020年全年节假日
        String overDay = "01-19,02-01,04-26,05-09,06-28,09-27,10-10";//2020年全年加班日
        festival = Arrays.asList(holiday.split(","));
        work = Arrays.asList(overDay.split(","));
    }

    public static void main(String[] args) throws ParseException, IOException, InvalidFormatException {
        float duration = calLeaveDuration("2020-03-25 08:00:00", "2020-03-29 17:30:00");
        System.out.println(duration);
    }

    /**
     * 计算请假时长
     * 规则：正常上班时间 早上 9点到12点，下午2点到6点，小于等4小时算半天，大于4小时算一天
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return 请假天数 leave
     */
    private static float calLeaveDuration(String start, String end) throws ParseException {

        String temp;
        if (start.compareTo(end) > 0) {//确保结束时间大于起始时间
            temp = start;
            start = end;
            end = temp;
        }

        Calendar startTime = Calendar.getInstance();
        startTime.setTime(format.parse(start));
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(format.parse(end));

        //1、判断是否在同一天内
        if (startTime.get(Calendar.DATE) == endTime.get(Calendar.DATE)) {
            return calEveryDayLeaveDuration(startTime, endTime);
        } else {//跨天
            //计算真实请假起始结束时间
            if (startTime.get(Calendar.HOUR_OF_DAY) >= 18) {
                startTime.add(Calendar.DATE, 1);
                startTime.set(Calendar.HOUR_OF_DAY, 9);
            }

            if (endTime.get(Calendar.HOUR_OF_DAY) <= 9) {
                endTime.add(Calendar.DATE, -1);
                endTime.set(Calendar.HOUR_OF_DAY, 18);
            }

            Calendar startTimeT = Calendar.getInstance();
            startTimeT.setTime(startTime.getTime());
            Calendar endTimeT = Calendar.getInstance();
            endTimeT.setTime(endTime.getTime());

            int day = 0;
            while (startTimeT.before(endTimeT)) {
                startTimeT.add(Calendar.DATE, 1);
                day++;
            }

            Calendar startTimeTemp;
            Calendar endTimeTemp;
            if (day == 1) {//只隔天的话不考虑 节假日，否则为脏数据
                startTimeTemp = Calendar.getInstance();
                endTimeTemp = Calendar.getInstance();
                startTimeTemp.setTime(startTime.getTime());
                endTimeTemp.setTime(startTime.getTime());
                endTimeTemp.set(Calendar.HOUR_OF_DAY, 18);

                float time1 = calEveryDayLeaveDuration(startTimeTemp, endTimeTemp);

                startTimeTemp = Calendar.getInstance();
                startTimeTemp.setTime(endTime.getTime());
                startTimeTemp.set(Calendar.HOUR_OF_DAY, 9);
                endTimeTemp = Calendar.getInstance();
                endTimeTemp.setTime(endTime.getTime());
                float time2 = calEveryDayLeaveDuration(startTimeTemp, endTimeTemp);

                return time1 + time2;
            } else {// 大于一天，需要考虑是否是工作日了
                startTimeTemp = Calendar.getInstance();
                startTimeTemp.setTime(startTime.getTime());
                endTimeTemp = Calendar.getInstance();
                endTimeTemp.setTime(startTime.getTime());
                endTimeTemp.set(Calendar.HOUR_OF_DAY, 18);

                float sum = calEveryDayLeaveDuration(startTimeTemp, endTimeTemp);

                for (int i = 1; i < day - 1; i++) {
                    startTimeTemp.add(Calendar.DATE, 1);

                    boolean isWorkDay = validWorkDay(startTimeTemp);
                    if (!isWorkDay) {
                        continue;
                    }
                    sum++;
                }

                //最后一天也认为是工作日
                startTimeTemp = Calendar.getInstance();
                startTimeTemp.setTime(endTime.getTime());
                startTimeTemp.set(Calendar.HOUR_OF_DAY, 9);
                endTimeTemp = Calendar.getInstance();
                endTimeTemp.setTime(endTime.getTime());

                sum += calEveryDayLeaveDuration(startTimeTemp, endTimeTemp);
                return sum;
            }
        }
    }

    /**
     * 判断是否是工作日
     *
     * @param startTimeTemp
     * @return
     */
    private static boolean validWorkDay(Calendar startTimeTemp) {
        String date = md.format(startTimeTemp.getTime());

        if (festival.contains(date)) {
            return false;
        }

        if (work.contains(date)) {
            return true;
        }

        int week = startTimeTemp.get(Calendar.DAY_OF_WEEK) - 1;//周几
        if (week == 6 || week == 0) {//周六、日
            return false;
        } else {
            return true;
        }
    }

    /**
     * 计算每天的请假时长
     *
     * @param startTime
     * @param endTime
     * @return
     */
    static float calEveryDayLeaveDuration(Calendar startTime, Calendar endTime) {

        boolean isWorkDay = validWorkDay(startTime);
        if (!isWorkDay) {
            return 0;
        }
        int startHour = startTime.get(Calendar.HOUR_OF_DAY);
        int endHour = endTime.get(Calendar.HOUR_OF_DAY);
        if (startHour == endHour) {
            return 0;
        }

        if (startHour < 9) {
            startHour = 9;
        } else if (startHour >= 9 && startHour <= 12) {
        } else if (startHour > 12 && startHour < 14) {
            startHour = 14;
        } else if (startHour >= 14 && startHour <= 18) {
        } else if (startHour > 18) {
            return 0;
        }

        if (endHour < 9) {
            return 0;
        } else if (endHour >= 9 && endHour <= 12) {
        } else if (endHour > 12 && endHour < 14) {
            endHour = 12;
        } else if (endHour >= 14 && endHour <= 18) {
        } else if (endHour > 18) {
            endHour = 18;
        }

        if (endHour <= 12) {
            return 0.5f;
        }

        if (startHour < 12 && endHour > 14) {
            int d = (12 - startHour) + (endHour - 14);
            if (d <= 4) {
                return 0.5f;
            } else {
                return 1;
            }
        }

        if (startHour >= 14) {
            int d = endHour - startHour;
            if (d <= 4) {
                return 0.5f;
            } else {
                return 1;
            }
        }

        return 0;
    }
}

