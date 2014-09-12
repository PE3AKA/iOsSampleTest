package com.ios.testhelper.demo;

import net.bugs.testhelper.IOSTestHelper;

import java.util.Date;

/**
 * Created by nikolai on 10.01.14.
 */
public class TestManager {
    private static volatile TestManager instance;

    private static FileWorker fileWorker;
    private static String mDeviceId = "";
    private static String mBuildId = "";
    private static String mLogin = "";
    private static String mPassword = "";
    private static String mHwDevice = "";
    private static String mArgTimeout = "";
    public static int mTimeout = 0;

    public static PropertiesManager propertiesManager;
    private static long mStartTime = 0;
    private static long mEndTime = 0;
    private static IOSTestHelper iosTestHelper;

    private TestManager(IOSTestHelper iosTestHelper){
        this.iosTestHelper = iosTestHelper;
        fileWorker = new FileWorker("ios.csv", iosTestHelper);
        propertiesManager = new PropertiesManager();
    }

    public PropertiesManager getPropertiesManager(){
        return propertiesManager;
    }

    public static TestManager getInstance(IOSTestHelper $iosTestHelper,
                                          final String buildId,
                                          final String login,
                                          final String password,
                                          final String deviceId,
                                          final String hwDevice,
                                          final String timeout){
        iosTestHelper = $iosTestHelper;
        mArgTimeout = timeout;
        mDeviceId = deviceId;
        mHwDevice = hwDevice;
        mBuildId = buildId;
        mLogin = login;
        mPassword = password;
        if(instance == null)
            synchronized (TestManager.class) {
                if(instance == null)
                    instance = new TestManager(iosTestHelper);
            }
        return instance;
    }

    public static TestManager getInstance() {
        return getInstance(iosTestHelper, mBuildId, mLogin, mPassword, mDeviceId, mHwDevice, mArgTimeout);
    }

    public static ItemLog addLogParams(Date date, String testAction, String testData, boolean testResult){
        ItemLog itemLog = new ItemLog(propertiesManager);
        itemLog.setBuild(mBuildId);
        itemLog.setDeviceId(mDeviceId);
        itemLog.setNet("");
        itemLog.setHw(mHwDevice);
        itemLog.setOs("");
        itemLog.setSlaveId("");
        itemLog.setDate(date, "");
        itemLog.setTime(date, "");
        itemLog.setStartTime(mStartTime);
        itemLog.setEndTime(mEndTime, 0);
        itemLog.setTestId("kpi");
        itemLog.setTestAction(testAction);
        itemLog.setTestData(testData);
        itemLog.setTestResult(testResult);
        return itemLog;
    }

    public static void write(ItemLog itemLog){
            fileWorker.writeLog(itemLog);
    }

    public IOSTestHelper getTestHelper() {
        return iosTestHelper;
    }

    private static void timer(boolean isStart){
        long time = System.currentTimeMillis();
        if(isStart)
            mStartTime = time;
        else{
            mEndTime = time;
        }
    }

    public static void setStartTime(long startTime){
        mStartTime = startTime;
    }

    public static void setEndTime(long endTime){
        mEndTime = endTime;
    }

    public static void startTimer(){
        timer(true);
    }

    public static void stopTimer(boolean accuracy){
        timer(false);
    }

    @Deprecated
    public static void stopTimer(){
        timer(false);
    }
}
