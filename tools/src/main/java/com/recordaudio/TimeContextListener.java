package com.recordaudio;


public interface TimeContextListener {
    /**
     * @param startRecordTime 录制开始时间
     * @param recordTime      录制时长
     * @param number          录制时长
     */
    void onTimeContextListener(String startRecordTime, String recordTime, int number);
}
