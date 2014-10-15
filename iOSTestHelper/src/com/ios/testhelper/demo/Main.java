package com.ios.testhelper.demo;

import com.ios.testhelper.demo.enums.ProductTypeEnum;
import com.ios.testhelper.demo.helpers.ITest;
import com.ios.testhelper.demo.init.InitParams;
import com.ios.testhelper.demo.init.Params;
import com.ios.testhelper.demo.kpi.*;
import net.bugs.testhelper.ios.alert.AlertCondition;
import net.bugs.testhelper.ios.alert.AlertHandler;
import net.bugs.testhelper.ios.alert.AlertItem;

import java.io.File;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by nikolai on 01.08.2014.
 */
public class Main {

    private static final String LOG_TAG = "iOSTestHelper : ";
    private static String iOSDeviceUUID = "";
    private static String pathToiOSApp = "";
    private static String pathToFolderResults = "";
    private static String testName = "";
    private static ITest iosTestHelper;
    private static TestManager testManager;
    private static PropertiesManager propertiesManager;

    private static void init(String[] args){
        InitParams initParams = new InitParams(args);

//        System.out.println("Args length:" + args.length);
        pathToiOSApp = initParams.getBuildPath();
        if(pathToiOSApp == null) {
            i("Looks like you forgot setup path to iOs build or app bundle\n" +
                    "usage: " + Params.BUILD + " app_bungle_or_path_to_build_for_simulator");
            System.exit(0);
        }
        pathToFolderResults = initParams.getResultPath();
        if(pathToFolderResults == null) {
            i("Looks like you forgot setup path to results folder\n" +
                    "usage: " + Params.RESULT + " RESULT");
            System.exit(0);
        }
        iOSDeviceUUID = initParams.getDevice();
        if(iOSDeviceUUID == null) {
            i("Looks like you forgot setup device id\n" +
                    "usage: " + Params.DEVICE + " \"iPad Retina - Simulator - iOS 7.1\"\n\n" +
                    "Device list you can get using following command:\n" +
                    "instruments -w help");
            System.exit(0);
        }
        testName = initParams.getTestName();
        if(testName == null) {
            i("Looks like you forgot setup test name\n" +
                    "usage: " + Params.TEST + " signIn\n\n" +
                    "test list:\n" +
                     Params.getTestList());
            System.exit(0);
        }

        propertiesManager = new PropertiesManager();
    }

    private static void log(String msg){
        System.out.println(LOG_TAG + msg);
    }

    public static void main(String[] args) {

        File file = new File("config.properties");
        i(file.getAbsolutePath() + " test " + file.exists());
        init(args);

        setUpIOsHelper();

        testManager = TestManager.getInstance(iosTestHelper, "","","",iOSDeviceUUID,"","");

        mainLogic();
        iosTestHelper.sleep(2000);
        iosTestHelper.stopInstruments();

    }

    private static void setUpIOsHelper() {
        iosTestHelper = new ITest(pathToiOSApp, pathToFolderResults, iOSDeviceUUID);

        final AlertHandler alertHandler = new AlertHandler();

        alertHandler.logMessage("Alert appeared");
        alertHandler.takeScreenShot("alert " + System.currentTimeMillis());
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

        iosTestHelper.launchServer();
    }

    private static void mainLogic() {
        startTest();
    }

    private static void startTest() {
        KpiTest kpiTest = null;
        ProductTypeEnum productTypeEnum = ProductTypeEnum.SIGN_IN_OUT;
        if(testName.equals(Params.TEST_SIGN_IN)) {
            kpiTest = new SignInKpi(iosTestHelper, propertiesManager, testManager);
        } else if(testName.equals(Params.TEST_SIGN_OUT)) {
            kpiTest = new SignOutKpi(iosTestHelper, propertiesManager, testManager);
        } else if(testName.equals(Params.TEST_DEFERREF_SIGN_IN)) {
            kpiTest = new DeferredSignInKPI(iosTestHelper, propertiesManager, testManager);
        } else if(testName.equals(Params.TEST_OPEN_BOOK)) {
            kpiTest = new TestOpenItemKpi(iosTestHelper, propertiesManager, testManager);
            productTypeEnum = ProductTypeEnum.BOOK;
        } else if(testName.equals(Params.TEST_OPEN_MAGAZINES)) {
            kpiTest = new TestOpenItemKpi(iosTestHelper, propertiesManager, testManager);
            productTypeEnum = ProductTypeEnum.MAGAZINE;
        } else if(testName.equals(Params.TEST_OPEN_PDF)) {
            kpiTest = new TestOpenItemKpi(iosTestHelper, propertiesManager, testManager);
            productTypeEnum = ProductTypeEnum.PDF;
        } else if(testName.equals(Params.TEST_OPEN_COMICS)) {
            kpiTest = new TestOpenItemKpi(iosTestHelper, propertiesManager, testManager);
            productTypeEnum = ProductTypeEnum.COMICS;
        } else if(testName.equals(Params.TEST_OPEN_NEWSPAPER)) {
            kpiTest = new TestOpenItemKpi(iosTestHelper, propertiesManager, testManager);
            productTypeEnum = ProductTypeEnum.NEWSPAPER;
        } else if(testName.equals(Params.TEST_ALL_KPI)) {
            for (String test : Params.ALL_KPI_TESTS){
                testName = test;
                startTest();
            }
            return;
        }
        if(kpiTest == null) {
            i("\"" +testName + "\" incorrect test name!\n\n" +
                    "valid test names:\n" + Params.getTestList());
            iosTestHelper.stopInstruments();
            System.exit(0);
        }
        kpiTest.execute(productTypeEnum);
    }
}