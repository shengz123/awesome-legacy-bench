package com.alphaentropy.common.utils;

import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateTimeUtil {
    private static final String[] FORMATS = new String[]{
            "yyyy-MM-dd", "yyyyMMdd"
    };

    public static List<Date> getDatesBetween(Date start, Date end) {
        List<Date> ret = new ArrayList<>();
        Calendar runDate = Calendar.getInstance();
        runDate.setTime(start);

        while (!runDate.getTime().after(end)) {
            skipWeekends(runDate);
            ret.add(new Date(runDate.getTime().getTime()));
            runDate.add(Calendar.DAY_OF_YEAR, 1);
        }
        return ret;
    }

    private static void skipWeekends(Calendar runDate) {
        if (runDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            runDate.add(Calendar.DATE, 1);
        } else if (runDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            runDate.add(Calendar.DATE, 1);
        }
    }

    public static String convertDate(String inputDate, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = DateUtils.parseDate(inputDate, FORMATS);
        return simpleDateFormat.format(date);
    }

    public static int compareDates(String benchmark, String toCompare) throws ParseException {
        Date date1 = DateUtils.parseDate(benchmark, FORMATS);
        Date date2 = DateUtils.parseDate(toCompare, FORMATS);
        if (date1.before(date2)) {
            return -1;
        } else if (date1.after(date2)) {
            return 1;
        } else {
            return 0;
        }
    }

    public static java.sql.Date createDate(String yyyyMMddStr) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return new java.sql.Date(format.parse(yyyyMMddStr).getTime());
    }

    public static int getYearAfter(int nYear) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int thisear = cal.get(Calendar.YEAR);
        return thisear + nYear;
    }

    public static int dateDiff(long date1, long date2) {
        return (int) ((date1 - date2) / 1000 / 60 / 60 / 24);
    }

    public static long getNextDay(long dateTS) {
        return dateTS + 24L * 3600L * 1000L;
    }

    public static boolean isUnleasedOfferDatesLinked(Date offerDate,
                                                     Date unleasedDate) {
        Calendar offerCal = Calendar.getInstance();
        offerCal.setTime(offerDate);

        Calendar unleasedCal = Calendar.getInstance();
        unleasedCal.setTime(unleasedDate);

        int offerYear = offerCal.get(Calendar.YEAR);
        int unleasedYear = unleasedCal.get(Calendar.YEAR);

        if (Math.abs(offerYear - unleasedYear) <= 3) {
            unleasedCal.set(Calendar.YEAR, offerYear);
            long diff = unleasedCal.getTimeInMillis()
                    - offerCal.getTimeInMillis();
            // Recent 2 weeks
            if (Math.abs(diff) <= 24 * 60 * 60 * 1000 * 14) {
                return true;
            }
        }

        return false;
    }

    public static String getQ(Date date) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String mmdd = format.format(date).substring(4);
        if (mmdd.equals("0331")) {
            return "Q1";
        } else if (mmdd.equals("0630")) {
            return "Q2";
        } else if (mmdd.equals("0930")) {
            return "Q3";
        } else if (mmdd.equals("1231")) {
            return "Q4";
        }
        return null;
    }

    public static List<String> getReportDatesSince(Date day1, String dateFmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFmt);
        Set<String> result = new TreeSet<>();
        Date endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(day1);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        int[] months = new int[] {2,5,8,11};
        int[] days = new int[] {31,30,30,31};
        int currMon = cal.get(Calendar.MONTH);
        int monthIdx = 0;
        for (int month : months) {
            if (month >= currMon) {
                break;
            }
            monthIdx++;
        }
        while (!cal.after(endCal)) {
            cal.set(Calendar.MONTH, months[monthIdx]);
            cal.set(Calendar.DAY_OF_MONTH, days[monthIdx]);
            if (!cal.after(endCal)) {
                result.add(sdf.format(cal.getTime()));
            }
            monthIdx++;
            if (monthIdx >= months.length) {
                cal.add(Calendar.YEAR, 1);
                monthIdx = 0;
                cal.set(Calendar.MONTH, months[monthIdx]);
                cal.set(Calendar.DAY_OF_MONTH, days[monthIdx]);
            }
        }
        return new ArrayList<>(result);
    }


    public static List<Date> getReportDatesSince(Date day1) {
        Set<Date> result = new TreeSet<>();
        Date endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(day1);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        int[] months = new int[] {2,5,8,11};
        int[] days = new int[] {31,30,30,31};
        int currMon = cal.get(Calendar.MONTH);
        int monthIdx = 0;
        for (int month : months) {
            if (month >= currMon) {
                break;
            }
            monthIdx++;
        }
        while (!cal.after(endCal)) {
            cal.set(Calendar.MONTH, months[monthIdx]);
            cal.set(Calendar.DAY_OF_MONTH, days[monthIdx]);
            if (!cal.after(endCal)) {
                result.add(cal.getTime());
            }
            monthIdx++;
            if (monthIdx >= months.length) {
                cal.add(Calendar.YEAR, 1);
                monthIdx = 0;
                cal.set(Calendar.MONTH, months[monthIdx]);
                cal.set(Calendar.DAY_OF_MONTH, days[monthIdx]);
            }
        }
        return new ArrayList<>(result);
    }

    public static List<String> getWeekDaysSince(Date day1, String dateFmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFmt);
        List<String> result = new ArrayList<>();
        Date endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(day1);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        while (!cal.after(endCal)) {
            int day = cal.get(Calendar.DAY_OF_WEEK);
            if (day != Calendar.SATURDAY && day != Calendar.SUNDAY) {
                result.add(sdf.format(cal.getTime()));
            }
            cal.add(Calendar.DATE, 1);
        }
        return result;
    }

    public static List<Date> getWeekDaysSince(Date day1) {
        List<Date> result = new ArrayList<>();
        Date endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(day1);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        while (!cal.after(endCal)) {
            int day = cal.get(Calendar.DAY_OF_WEEK);
            if (day != Calendar.SATURDAY && day != Calendar.SUNDAY) {
                result.add(cal.getTime());
            }
            cal.add(Calendar.DATE, 1);
        }
        return result;
    }

    public static String[] getWeekStartEndDates(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        Date endDate = cal.getTime();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        return new String[] { format.format(startDate), format.format(endDate) };
    }

    public static String[] getMonthStartEndDates(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        return new String[] { format.format(startDate), format.format(endDate) };
    }

    public static boolean isWeekend() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.FRIDAY || day == Calendar.SATURDAY
                || day == Calendar.SUNDAY) {
            return true;
        }
        return false;
    }

    public static Date getSameDayLastYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, year - 1);
        return cal.getTime();
    }

    public static boolean isYearEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        if (day == 31 && month == 11) {
            return true;
        }
        return false;
    }

    public static boolean isSwitchMonth(Date prevTD, Date thisTD) {
        int thisMon = getMonthPart(thisTD);
        int prevMon = getMonthPart(prevTD);
        if (thisMon > prevMon) {
            return true;
        }
        return false;
    }

    public static boolean isSwitchYear(Date prevTD, Date thisTD) {
        int thisYear = getYearPart(thisTD);
        int prevYear = getYearPart(prevTD);
        if (thisYear > prevYear) {
            return true;
        }
        return false;
    }

    public static Date getPrevYearEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int thisYear = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, thisYear - 1);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        return cal.getTime();
    }

    public static int getReportDays(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        // 1231
        if (month == 11) {
            return 360;
        } else if (month == 8) {
            cal.set(Calendar.MONTH, 5);
            cal.set(Calendar.DAY_OF_MONTH, 30);
            return 270;
        } else if (month == 5) {
            cal.set(Calendar.MONTH, 2);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            return 180;
        } else if (month == 2) {
            int thisYear = cal.get(Calendar.YEAR);
            cal.set(Calendar.YEAR, thisYear - 1);
            cal.set(Calendar.MONTH, 11);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            return 90;
        }
        return 0;
    }

    public static boolean isQ1(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        return month == 2;
    }

    public static boolean isQuarterEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if ((month == 11 && day == 31) || (month == 8 && day == 30)
                || (month == 5 && day == 30) || (month == 2 && day == 31)) {
            return true;
        }
        return false;
    }

    public static Date getPrevQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        switch(month) {
            // month 10, 11, 12 -> 9/30
            case 11:
            case 10:
            case 9: {
                cal.set(Calendar.MONTH, 8);
                cal.set(Calendar.DAY_OF_MONTH, 30);
                return cal.getTime();
            }
            // month 7, 8, 9 -> 6/30
            case 8:
            case 7:
            case 6: {
                cal.set(Calendar.MONTH, 5);
                cal.set(Calendar.DAY_OF_MONTH, 30);
                return cal.getTime();
            }
            // month 4, 5, 6 -> 3/31
            case 5:
            case 4:
            case 3: {
                cal.set(Calendar.MONTH, 2);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                return cal.getTime();
            }
            // month 1, 2, 3 -> 12/31 in prev year
            case 2:
            case 1:
            case 0: {
                int thisYear = cal.get(Calendar.YEAR);
                cal.set(Calendar.YEAR, thisYear - 1);
                cal.set(Calendar.MONTH, 11);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                return cal.getTime();
            }
            default:
                return null;
        }
    }

    public static Date getPrevMonthStartDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getMonthAgo(cal.getTime());
    }

    public static Date getMonthLater(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        if (month == 12) {
            cal.set(Calendar.MONTH, 1);
            cal.set(Calendar.YEAR, year + 1);
        } else {
            cal.set(Calendar.MONTH, month + 1);
        }
        return cal.getTime();
    }

    public static String getYearMonString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        return String.valueOf(year) + "-" + String.valueOf(month + 1);
    }

    public static Date getMonthAgo(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        if (month == 1) {
            cal.set(Calendar.MONTH, 12);
            cal.set(Calendar.YEAR, year - 1);
        } else {
            cal.set(Calendar.MONTH, month - 1);
        }
        return cal.getTime();
    }

    public static Date getYearStartDate() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.parse("20140101");
    }

    public static Date getDaysAgo(int days) {
        return getDaysAgo(days, new Date());
    }

    public static Date getDaysAgo(int days, Date base) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day > days) {
            cal.set(Calendar.DAY_OF_MONTH, day - days);
        } else {
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DAY_OF_MONTH, day + 28 - days);
        }
        return cal.getTime();
    }

    public static int getMonthPart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getYearPart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static String getThisYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public static Date getYearAgo(Date date, int nYear) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, year - nYear);
        return cal.getTime();
    }

    public static String getHalfYearAgo() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int month = cal.get(Calendar.MONTH);
        if (month < 6) {
            int year = cal.get(Calendar.YEAR);
            cal.set(Calendar.YEAR, year - 1);
            cal.set(Calendar.MONTH, month + 6);
        } else {
            cal.set(Calendar.MONTH, month - 6);
        }
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(cal.getTime());
    }

    public static String getYearAgo(int nYear) {
        Date yearAgo = getYearAgo(new Date(), nYear);
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(yearAgo);
    }

    public static Date getMonthAgo() {
        return getMonthAgo(new Date());
    }

    public static String[] getReportPeriod(String todayString) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = sdf.parse(todayString);
        String[] result = null;
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int monthFrom0 = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        // today between 04-01 and 05-10, return Q1 of this year and Q4 of
        // previous year
        if (monthFrom0 == 3 || (monthFrom0 == 4 && day <= 10)) {
            Integer prevYear = year - 1;
            Calendar q4 = Calendar.getInstance();
            q4.set(prevYear, 11, 31);
            String q4String = sdf.format(q4.getTime());
            Calendar q1 = Calendar.getInstance();
            q1.set(year, 2, 31);
            String q1String = sdf.format(q1.getTime());
            result = new String[] { q4String, q1String };
        } else if (monthFrom0 == 6 || monthFrom0 == 7
                || (monthFrom0 == 8 && day <= 10)) {
            // today between 07-01 and 09-10, return Q2 of this year
            Calendar q2 = Calendar.getInstance();
            q2.set(year, 5, 30);
            String q2String = sdf.format(q2.getTime());
            result = new String[] { q2String };
        } else if (monthFrom0 == 9 || (monthFrom0 == 10 && day <= 10)) {
            // today between 10-01 and 11-10, return Q3 of this year
            Calendar q3 = Calendar.getInstance();
            q3.set(year, 8, 30);
            String q3String = sdf.format(q3.getTime());
            result = new String[] { q3String };
        } else if (monthFrom0 <= 2) {
            // today between 01-01 and 03-31, return Q4 of prev year
            Integer prevYear = year - 1;
            Calendar q4 = Calendar.getInstance();
            q4.set(prevYear, 11, 31);
            String q4String = sdf.format(q4.getTime());
            result = new String[] { q4String };
        }

        if (result == null) {
            return null;
        }

        String[] tmp = getPast1YearPeriod(result[0]);
        String[] returnResult = new String[result.length + tmp.length];
        for (int i = tmp.length; i < returnResult.length; i++) {
            returnResult[i] = result[i - tmp.length];
        }
        for (int i = 0; i < tmp.length; i++) {
            returnResult[i] = tmp[i];
        }
        return returnResult;
    }

    public static String[] getReportPeriod() throws Exception {
        return getReportPeriod(new Date(), false);
    }

    // 03-31: today between 04-01 and 05-10
    // 06-30: today between 07-01 and 09-10
    // 09-30: today between 10-01 and 11-10
    // 12-31: today between 01-01 and 05-10
    public static String[] getReportPeriod(Date today, boolean getFullCoverage)
            throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] result = null;

        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int monthFrom0 = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        // today between 04-01 and 05-10, return Q1 of this year and Q4 of
        // previous year
        if (monthFrom0 == 3 || monthFrom0 == 4 || monthFrom0 == 5
                || (getFullCoverage && monthFrom0 >= 3 && monthFrom0 <= 5)) {
            Integer prevYear = year - 1;
            Calendar q4 = Calendar.getInstance();
            q4.set(prevYear, 11, 31);
            String q4String = sdf.format(q4.getTime());
            Calendar q1 = Calendar.getInstance();
            q1.set(year, 2, 31);
            String q1String = sdf.format(q1.getTime());
            if (getFullCoverage) {
                Calendar q3 = Calendar.getInstance();
                q3.set(prevYear, 8, 30);
                String q3String = sdf.format(q3.getTime());
                result = new String[] { q3String, q4String, q1String };
            } else {
                result = new String[] { q4String, q1String };
            }
        } else if (monthFrom0 == 6 || monthFrom0 == 7
                || (monthFrom0 == 8 && day <= 10)
                || (getFullCoverage && monthFrom0 >= 6 && monthFrom0 <= 8)) {
            // today between 07-01 and 09-10, return Q2 of this year
            Calendar q2 = Calendar.getInstance();
            q2.set(year, 5, 30);
            String q2String = sdf.format(q2.getTime());
            if (getFullCoverage) {
                Calendar q1 = Calendar.getInstance();
                q1.set(year, 2, 31);
                String q1String = sdf.format(q1.getTime());
                result = new String[] { q1String, q2String };
            } else {
                result = new String[] { q2String };
            }
        } else if (monthFrom0 >= 9 && monthFrom0 <= 10
                || (getFullCoverage && monthFrom0 >= 9 && monthFrom0 <= 11)) {
            // today between 10-01 and 11-10, return Q3 of this year
            Calendar q3 = Calendar.getInstance();
            q3.set(year, 8, 30);
            String q3String = sdf.format(q3.getTime());
            if (getFullCoverage) {
                Calendar q2 = Calendar.getInstance();
                q2.set(year, 5, 30);
                String q2String = sdf.format(q2.getTime());
                result = new String[] { q2String, q3String };
            } else {
                result = new String[] { q3String };
            }
        } else if (monthFrom0 <= 2
                || (getFullCoverage && monthFrom0 >= 0 && monthFrom0 <= 2)) {
            // today between 01-01 and 03-31, return Q4 of prev year
            Integer prevYear = year - 1;
            Calendar q4 = Calendar.getInstance();
            q4.set(prevYear, 11, 31);
            String q4String = sdf.format(q4.getTime());
            if (getFullCoverage) {
                Calendar q3 = Calendar.getInstance();
                q3.set(prevYear, 8, 30);
                String q3String = sdf.format(q3.getTime());
                result = new String[] { q3String, q4String };
            } else {
                result = new String[] { q4String };
            }
        }

        if (result == null) {
            return null;
        }

        if (getFullCoverage) {
            return result;
        }
        String[] tmp = getPast1YearPeriod(result[0]);
        String[] returnResult = new String[result.length + tmp.length];
        for (int i = tmp.length; i < returnResult.length; i++) {
            returnResult[i] = result[i - tmp.length];
        }
        for (int i = 0; i < tmp.length; i++) {
            returnResult[i] = tmp[i];
        }
        return returnResult;
    }

    private static String[] getPast1YearPeriod(String reportDate)
            throws Exception {
        String[] result = new String[4];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date rd = sdf.parse(reportDate);
        Calendar c = Calendar.getInstance();
        c.setTime(rd);
        int monthFrom0 = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        if (monthFrom0 == 2) {
            c.set(year - 1, 2, 31);
            result[0] = sdf.format(c.getTime());
            c.set(year - 1, 5, 30);
            result[1] = sdf.format(c.getTime());
            c.set(year - 1, 8, 30);
            result[2] = sdf.format(c.getTime());
            c.set(year - 1, 11, 31);
            result[3] = sdf.format(c.getTime());
        } else if (monthFrom0 == 5) {
            c.set(year - 1, 5, 30);
            result[0] = sdf.format(c.getTime());
            c.set(year - 1, 8, 30);
            result[1] = sdf.format(c.getTime());
            c.set(year - 1, 11, 31);
            result[2] = sdf.format(c.getTime());
            c.set(year, 2, 31);
            result[3] = sdf.format(c.getTime());
        } else if (monthFrom0 == 8) {
            c.set(year - 1, 8, 30);
            result[0] = sdf.format(c.getTime());
            c.set(year - 1, 11, 31);
            result[1] = sdf.format(c.getTime());
            c.set(year, 2, 31);
            result[2] = sdf.format(c.getTime());
            c.set(year, 5, 30);
            result[3] = sdf.format(c.getTime());
        } else if (monthFrom0 == 11) {
            c.set(year - 1, 11, 31);
            result[0] = sdf.format(c.getTime());
            c.set(year, 2, 31);
            result[1] = sdf.format(c.getTime());
            c.set(year, 5, 30);
            result[2] = sdf.format(c.getTime());
            c.set(year, 8, 30);
            result[3] = sdf.format(c.getTime());
        }
        return result;
    }

    public static String[] getForecastPeriod() throws Exception {
        return getForecastPeriod(new Date());
    }

    // For one today, it may have 2 forecast period:
    // 1. For the symbol already released the report, then the report period
    // must be the next period
    // 2. For the symbol not released the report, then the report period is this
    // period
    public static String[] getForecastPeriod(Date today) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] result = null;
        String[] currentReportPeriod = getReportPeriod(today, false);

        if (currentReportPeriod != null) {
            if (currentReportPeriod.length == 6) {
                result = new String[3];
                result[0] = currentReportPeriod[4];
                result[1] = currentReportPeriod[5];
            } else if (currentReportPeriod.length == 5) {
                result = new String[2];
                result[0] = currentReportPeriod[4];
            }
        } else {
            result = new String[1];
        }

        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int year = c.get(Calendar.YEAR);
        int monthFrom0 = c.get(Calendar.MONTH);
        if (monthFrom0 <= 2) {
            c.set(year, 2, 31);
        } else if (monthFrom0 <= 5) {
            c.set(year, 5, 30);
        } else if (monthFrom0 <= 8) {
            c.set(year, 8, 30);
        } else if (monthFrom0 <= 11) {
            c.set(year, 11, 31);
        }
        String q2String = sdf.format(c.getTime());
        result[result.length - 1] = q2String;

        return result;
    }

    public static Date getNextReportPeriod(Date thisPerid) throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(thisPerid);
        int year = c.get(Calendar.YEAR);
        int monthFrom0 = c.get(Calendar.MONTH);
        if (monthFrom0 == 2) {
            c.set(year, 5, 30);
        } else if (monthFrom0 <= 5) {
            c.set(year, 8, 30);
        } else if (monthFrom0 == 8) {
            c.set(year, 11, 31);
        } else if (monthFrom0 == 11) {
            c.set(year + 1, 2, 31);
        }
        return c.getTime();
    }

    public static boolean isTimestampToday(long timestamp) throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Date date = new Date(timestamp);
        c.setTime(date);
        int otherYear = c.get(Calendar.YEAR);
        int otherMonth = c.get(Calendar.MONTH);
        int otherDay = c.get(Calendar.DAY_OF_MONTH);

        return (year == otherYear) && (month == otherMonth)
                && (day == otherDay);
    }

    public static BigDecimal getFactorByReportDate(Date reportDate)
            throws Exception {
        BigDecimal result = null;
        Calendar c = Calendar.getInstance();
        c.setTime(reportDate);
        int monthFrom0 = c.get(Calendar.MONTH);
        if (monthFrom0 == 2) {
            result = new BigDecimal(4);
        } else if (monthFrom0 == 5) {
            result = new BigDecimal(2);
        } else if (monthFrom0 == 8) {
            result = new BigDecimal(1.33333);
        } else if (monthFrom0 == 11) {
            result = new BigDecimal(1);
        }
        return result;
    }
}
