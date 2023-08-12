/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.widget.wheel;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;

import java.util.Calendar;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter extends AbstractWheelTextAdapter {

    /**
     * The default min value
     */
    public static final int DEFAULT_MAX_VALUE = 9;

    /**
     * The default max value
     */
    private static final int DEFAULT_MIN_VALUE = 0;

    // Values
    private int minValue;
    private int maxValue;

    // format
    private String format;
    //unit
    private String unit;
    private int currentYear, currentMonth, currentDay;


    /**
     * Constructor
     *
     * @param context the current context
     */
    public NumericWheelAdapter(Context context) {
        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     *
     * @param context  the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue) {
        this(context, minValue, maxValue, null);
    }

    /**
     * Constructor
     *
     * @param context  the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format   the format string
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format) {
        this(context, minValue, maxValue, format, null);
    }

    /**
     * Constructor
     *
     * @param context  the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format   the format string
     * @param unit     the wheel unit value
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format, String unit) {
        super(context);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
        this.unit = unit;
    }

    /**
     * Constructor
     *
     * @param context  the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format   the format string
     * @param unit     the wheel unit value
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format, String unit, int currentYear, int currentMonth, int currentDay) {
        super(context);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
        this.unit = unit;
        this.currentYear = currentYear;
        this.currentMonth = currentMonth;
        this.currentDay = currentDay;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            String text = !TextUtils.isEmpty(format) ? String.format(format, value) : Integer.toString(value);
            text = TextUtils.isEmpty(unit) ? text : text + unit;
            boolean isCurrentDate = false;
            if (unit.equals(DefaultConfig.DAY)) {
                Calendar currentSelectDate = getDate(currentYear, currentMonth);
                Calendar currentCalendar = Calendar.getInstance();
//                LogUtils.d(String.format("选择原始日期%s-%s-%s", currentYear, currentMonth, currentDay), String.format("选择的日期%s-%s", currentSelectDate.get(Calendar.YEAR), currentSelectDate.get(Calendar.MONTH) + 1), String.format("当前时间%s-%s-%s", currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH) + 1, currentCalendar.get(Calendar.DATE)));
                if (String.format("%s-%s", currentSelectDate.get(Calendar.YEAR), currentSelectDate.get(Calendar.MONTH) + 1).equals(String.format("%s-%s", currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH) + 1))) {
                    isCurrentDate = true;
                }
//                LogUtils.d(text, getCurrentDay(), isCurrentDate);
                if (text.equals(getCurrentDay()) && isCurrentDate)
                    return Html.fromHtml("<font color='#2E7FFF'>今日</font>");
            }
            return text;
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }

    public Calendar getDate(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        return calendar;
    }

    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int value = calendar.get(Calendar.DATE);
        String text = !TextUtils.isEmpty(format) ? String.format(format, value) : Integer.toString(value);
        text = TextUtils.isEmpty(unit) ? text : text + unit;
        return text;
    }
}
