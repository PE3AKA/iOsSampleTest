package com.ios.testhelper.demo;

/**
 * Created by nikolai on 27.03.14.
 */
public class Timer {

    public static final long MINUTE = 60000;

    public static long getTimeout() {
        long diff = (System.currentTimeMillis() - MainConstants.TIME_START_TEST);
        if(diff < 0)
            return TestManager.mTimeout;
        return TestManager.mTimeout - diff;
    }

}
