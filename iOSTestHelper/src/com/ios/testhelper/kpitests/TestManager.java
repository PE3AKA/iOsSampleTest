package com.ios.testhelper.kpitests;

import com.ios.testhelper.kpitests.helpers.ITest;
import net.bugs.testhelper.IOSTestHelper;
import net.bugs.testhelper.ios.alert.AlertCondition;
import net.bugs.testhelper.ios.alert.AlertHandler;
import net.bugs.testhelper.ios.alert.AlertItem;

import java.io.File;
import java.util.Date;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by nikolai on 10.01.14.
 */
public class TestManager {
    private static volatile TestManager instance;

    private static FileWorker fileWorker;
    private static FileWorker fileWorkerStress;
    private static String mDeviceId = "";
    private static String mBuildId = "";
    private static String mLogin = "";
    private static String mPassword = "";
    private static String mHwDevice = "";
    private static String mNet = "";
    private static String mArgTimeout = "";
    public static int mTimeout = 0;

    /**
     * params
     */
    private static String mPathToiOSApp = "";
    private static String mPathToFolderResults = "";

    public static PropertiesManager propertiesManager;
    private static long mStartTime = 0;
    private static long mEndTime = 0;
    private static ITest iosTestHelper;
    private static String mOs;
    private static String mSlaveId;
    private static String mTestId = System.currentTimeMillis() + "";
    private static String mTestName;

    private TestManager(ITest iosTestHelper1) {
        iosTestHelper = iosTestHelper1;
        File logs = new File("logs");
        if(!logs.exists()) logs.mkdirs();
        fileWorker = new FileWorker(logs.getAbsolutePath() + "/kpi.txt", iosTestHelper);
        iosTestHelper.setJsKpiListener(new IOSTestHelper.JsKpiListener() {
            @Override
            public void kpiCompleted(String line) {
                i("###################### KpiCompleted: " + line);
                try {
                    fileWorker.writeToFile(String.format(line, mBuildId, mNet, mDeviceId, mSlaveId, mTestId));
                } catch (Exception ex) {
                    i("###################### kpiCompleted: ERROR");
                    ex.printStackTrace();
                }
            }
        });
        fileWorkerStress = new FileWorker(logs.getAbsolutePath() + "/stress.txt", iosTestHelper);
        propertiesManager = new PropertiesManager();
    }

    public PropertiesManager getPropertiesManager(){
        return propertiesManager;
    }

    public static TestManager getInstance(ITest $iosTestHelper,
                                          final String pathToiOSApp,
                                          final String login,
                                          final String password,
                                          final String deviceId,
                                          final String pathToFolderResults,
                                          final String timeout){
        iosTestHelper = $iosTestHelper;
        mPathToiOSApp = pathToiOSApp;
        mPathToFolderResults = pathToFolderResults;
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
//            mTestId = System.currentTimeMillis() + "";
            mTestName = "DemoKpiTest";
            mLogin = login;
            mPassword = password;
        }
        return instance;
    }

    public static void setUpIOsHelper(boolean installApp) {
        iosTestHelper = new ITest(mPathToiOSApp, mPathToFolderResults, mDeviceId);

        final AlertHandler alertHandler = new AlertHandler();

        alertHandler.logMessage("Alert appeared");
//        alertHandler.takeScreenShot(new AlertStep.IScreenShot() {
//            @Override
//            public Object[] getScreenShotName() {
//                return new Object[] {alertHandler.getDate("-"), "_", alertHandler.getTime("-"), "_", "alert"};
//            }
//
//            @Override
//            public boolean takeScreenShotViaInstrumentsOnly() {
//                return true;
//            }
//        });
        AlertItem result = alertHandler.waitForElementByNameVisible("logging out will", 1, 0, false, null, 4);
        alertHandler.createElementNotNullCondition(result, new AlertCondition.ConditionResults() {
            @Override
            public void positiveResult() {
                final AlertItem buttonOk = alertHandler.waitForElementByNameVisible("OK", 1, 0, false, null, 4);
                alertHandler.createElementNotNullCondition(buttonOk, new AlertCondition.ConditionResults() {
                    @Override
                    public void positiveResult() {
                        alertHandler.clickOnElementByXY(buttonOk, 0.5, 0.5);
                        alertHandler.returnBoolean(true);
                    }

                    @Override
                    public void negativeResult() {

                    }
                });
            }

            @Override
            public void negativeResult() {

            }
        });

        alertHandler.returnBoolean(false);

        alertHandler.push(iosTestHelper);

        iosTestHelper.cleanResultsFolder();

        iosTestHelper.launchServer(installApp, true, 0);
    }

    public static TestManager getInstance() {
        return getInstance(iosTestHelper, mBuildId, mLogin, mPassword, mDeviceId, mHwDevice, mArgTimeout);
    }

    public static ItemLog addLogParams(Date date, String testName, String testAction, String testData, String testType, String testCycle, boolean testResult){
        ItemLog itemLog = addLogParams(date, testName, testAction, testData, testResult);
        itemLog.setTestType(testType);
        itemLog.setTestCycle(testCycle);
        return itemLog;
    }

    public static ItemLog addLogParams(Date date, String testName, String testAction, String testData, boolean testResult){
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
        itemLog.setTestName(testName);
        itemLog.setTestAction(testAction);
        itemLog.setTestData(testData);
        itemLog.setTestResult(testResult);
        return itemLog;
    }

    public static void write(ItemLog itemLog){
            fileWorker.writeLog(itemLog);
    }

    public static void writeStress(ItemLog itemLog){
        fileWorkerStress.writeLog(itemLog);
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
