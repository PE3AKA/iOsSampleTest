package com.ios.testhelper.demo;

import com.ios.testhelper.demo.helpers.ITest;

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
    private static String mNet = "";
    private static String mArgTimeout = "";
    public static int mTimeout = 0;

    public static PropertiesManager propertiesManager;
    private static long mStartTime = 0;
    private static long mEndTime = 0;
    private static ITest iosTestHelper;
    private static String mOs;
    private static String mSlaveId;
    private static String mTestId;
    private static String mTestName;

    private TestManager(ITest iosTestHelper){
        this.iosTestHelper = iosTestHelper;
        fileWorker = new FileWorker("ios.csv", iosTestHelper);
        propertiesManager = new PropertiesManager();
    }

    public PropertiesManager getPropertiesManager(){
        return propertiesManager;
    }

    public static TestManager getInstance(ITest $iosTestHelper,
                                          final String buildId,
                                          final String login,
                                          final String password,
                                          final String deviceId,
                                          final String hwDevice,
                                          final String timeout){
        iosTestHelper = $iosTestHelper;
        if(instance == null) {
            synchronized (TestManager.class) {
                if (instance == null)
                    instance = new TestManager(iosTestHelper);
            }
        }
        if(mDeviceId.length() == 0) {
            mArgTimeout = timeout;
            mDeviceId = deviceId;
            mHwDevice = iosTestHelper.getIOsDeviceModel();
            mBuildId = propertiesManager.getProperty("BUILD");
            mNet = propertiesManager.getProperty("NET");
            mOs = iosTestHelper.getIOsDeviceFullVersion();
            mSlaveId = iosTestHelper.getOsFullName();
            mTestId = System.currentTimeMillis() + "";
            mTestName = "DemoKpiTest";
            mLogin = login;
            mPassword = password;
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
        itemLog.setNet(mNet);
        itemLog.setHw(mHwDevice);
        itemLog.setOs(mOs);
        itemLog.setSlaveId(mSlaveId);
        itemLog.setDate(date, "");
        itemLog.setTime(date, "");
        itemLog.setStartTime(mStartTime);
        itemLog.setEndTime(mEndTime, 0);
        itemLog.setTestId(mTestId);
        itemLog.setTestName(mTestName);
        itemLog.setTestAction(testAction);
        itemLog.setTestData(testData);
        itemLog.setTestResult(testResult);
        return itemLog;
    }

    public static void write(ItemLog itemLog){
            fileWorker.writeLog(itemLog);
    }

    public ITest getTestHelper() {
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
