package com.ios.testhelper.demo;

import net.bugs.testhelper.IOSTestHelper;
import net.bugs.testhelper.ios.alert.AlertCondition;
import net.bugs.testhelper.ios.alert.AlertHandler;
import net.bugs.testhelper.ios.alert.AlertItem;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;
import net.bugs.testhelper.ios.item.ResponseItem;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by nikolai on 01.08.2014.
 */
public class Main {

    private static final String LOG_TAG = "iOSTestHelper : ";
    private static String iOSDeviceUUID = "";
    private static String pathToiOSApp = "";
    private static String pathToFolderResults = "";
    private static IOSTestHelper iosTestHelper;
    private static TestManager testManager;
    private static PropertiesManager propertiesManager;

    private static void init(String[] args){
        System.out.println("Args length:" + args.length);
        pathToiOSApp = args[0];
//        pathToiOSApp = "/Users/nikolai/Downloads/BNeReader-Universal-SIM-Release-build-4121.app";
        pathToFolderResults = args[1];
//        pathToFolderResults = "results";
        iOSDeviceUUID = args[2];
//        iOSDeviceUUID = "iPad - Simulator - iOS 7.1";
        propertiesManager = new PropertiesManager();
    }

//    private static void cleanFolder(String pathToFolder){
//        File dir = new File(pathToFolder);
//        try {
//            if(dir.exists())
//                FileUtils.cleanDirectory(dir);
//            else dir.mkdir();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private static void waitiOsDevice(){
//        File dir = new File(pathToFolderResults);
//        while(dir.listFiles().length == 0){
//            sleep(1000);
//        }
//    }

    private static void stopInstruments(){
        if(iosTestHelper != null) {
            iosTestHelper.stopTest();
        }
    }

    private static void log(String msg){
        System.out.println(LOG_TAG + msg);
    }

    private static void finishReturn(String msgLog, String testAction){
        log(msgLog);
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        testManager.write(testManager.addLogParams(new Date(), testAction, "", false));
    }

    private static void clickOnElement(Element element){
        if(element.isVisible())
            iosTestHelper.clickOnElement(element);
        else
            iosTestHelper.clickOnElementByXY(element, 0.5, 0.5);
    }

    public static void main(String[] args) {
        init(args);
//        cleanFolder(pathToFolderResults);

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

        log("pathToiOSApp:" + pathToiOSApp + "; pathToFolderResults:" + pathToFolderResults + "; iOSDeviceUUID:" + iOSDeviceUUID);
        iosTestHelper = new IOSTestHelper(pathToiOSApp, pathToFolderResults, iOSDeviceUUID);

        alertHandler.push(iosTestHelper);
        iosTestHelper.launchServer();

//        System.out.println("Waiting... Launch application on ios device");
//        waitiOsDevice();
        testManager = TestManager.getInstance(iosTestHelper, "","","","","","");

        mainLogic();

        sleep(2000);

        stopInstruments();
    }

    private static void mainLogic() {
        long timeout = 5000;
//        long timeout = propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.TIMEOUT.name());
        if(!signIn(timeout)) {
            i("signOut");
            singOut();
            return;
        }

        openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.DRP_COMICS.name()),
                "Library",
                1,
                propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.COMICS_DOWNLOAD_TIMEOUT.name()));

        openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.BOOK.name()),
                "Library",
                1,
                propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.BOOK_DOWNLOAD_TIMEOUT.name()));

        openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.WOODWIN_MAGAZINE.name()),
                "Back to library",
                -1,
                propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.WOODWIN_TIMEOUT.name()));

//        openItemKpi("Is For Stupid", "backButton", -1);
        singOut();
    }

    private static void singOut() {
        Element element = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIASearchBar, 5000, 0, null, 3);
        ArrayList<Element> buttons = iosTestHelper.getElementChildrenByType(element, UIAElementType.UIAButton);
        if(buttons.size() > 0) {
            clickOnElement(buttons.get(0));
        }
        hideKeyboard();
        openMenu();
        element = iosTestHelper.waitForElementByNameVisible("Settings", 5000, 0, true, null, 3);
        if(element != null) {
            iosTestHelper.clickOnElement(element);
        }
        element = iosTestHelper.waitForElementByNameVisible("Logout", 5000, 0, true, null, 3);
        if(element != null) {
            iosTestHelper.clickOnElement(element);
        }
        sleep(1000);
        iosTestHelper.waitForElementByNameVisible("signIn", 30000, 0, true, null, 2);
    }

    private static void hideKeyboard() {
        Element element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAKeyboard, 1, 0, null, 2);
        if(element != null){
            int[] screenSize = iosTestHelper.getScreenSize();
            iosTestHelper.clickByXY(screenSize[0]/3, screenSize[1]/3);
        }
    }

    private static void openItemKpi(String name, String backButton, int toolbarIndex, int downloadTimeout) {
        goToLibrary();
        Element element = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIASearchBar, 100000, 0, null, 3);
        clickOnElement(element);
        ArrayList<Element> buttons = iosTestHelper.getElementChildrenByType(element, UIAElementType.UIAButton);
        if(buttons.size() > 0) {
            clickOnElement(buttons.get(0));
        }
        iosTestHelper.inputText(name + "\n", element);
        hideKeyboard();
        setStartTime();
        element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionView, 10000, 0, null, 2);
        if(element == null) {
            setEndTime();
            failKpi("search " + name);
            return;
        }
        ArrayList<Element> elements = iosTestHelper.getElementChildren(element);
        i("Count collection cell:" + elements.size());

        element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionCell, 10000, 0, element, 1);
        setEndTime();
        if(element == null) {
            failKpi("search " + name);
            return;
        } else {
            passKpi("search " + name);
        }

        i("element is visible?: " + element.isVisible());
        iosTestHelper.longClickInsideElement(element, 4, 0.5, 0.5);

        element = iosTestHelper.waitForElementByNameVisible("Download", 10000, 0, true, null, 2);
        if(element == null) {
            TestManager.setStartTime(0);
            TestManager.setEndTime(0);
            failKpi("download " + name);
            return;
        }
        clickOnElement(element);
        setStartTime();
        element = iosTestHelper.waitForElementByNameVisible("Read", downloadTimeout, 0, true, null, 2);
        setEndTime();
        if(element == null) {
            failKpi("download " + name);
            Element closeBtn = iosTestHelper.getElementByName("Close", 0, true, null, 2);
            iosTestHelper.clickOnElement(closeBtn);
            //todo logic to close dialog
            return;
        }
        passKpi("download " + name);
        clickOnElement(element);
        setStartTime();

        if(toolbarIndex > -1) {
            element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 60000, toolbarIndex, null, 2);

            if (element == null) {
                failKpi("open " + name);
                return;
            }
        }

        element = iosTestHelper.waitForElementByNameExists(backButton, 60000, 0, true, toolbarIndex > -1 ? element : null, toolbarIndex > -1 ? 1 : 3);
        setEndTime();
        if(element == null) {
            failKpi("open " + name);
            return;
        }

        passKpi("open " + name);
        clickOnElement(element);
    }

    private static void setEndTime() {
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
    }

    private static void setStartTime() {
        TestManager.setStartTime(iosTestHelper.getResponseItem().getEndTime());
    }

    private static void passKpi(String kpiName){
        TestManager.write(TestManager.addLogParams(new Date(), kpiName, "", true));
    }

    private static void failKpi(String kpiName){
        TestManager.write(TestManager.addLogParams(new Date(), kpiName, "", false));
    }

    private static boolean goToLibrary() {
        if(iosTestHelper.waitForElementByClassVisible(UIAElementType.UIASearchBar, 5000, 0, null, 3) != null) return true;
        openMenu();
        Element library = iosTestHelper.waitForElementByNameVisible("Library", 10000, 0, true, null, 3);
        clickOnElement(library);
        return iosTestHelper.waitForElementByClassVisible(UIAElementType.UIASearchBar, 10000, 0, null, 3) != null;
    }

    private static boolean openMenu() {
        Element element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 10000, 0, null, 2);
        ArrayList<Element> buttons = iosTestHelper.getElementChildrenByType(element, UIAElementType.UIAButton);
        i("Toolbar was found");
        if(buttons.size() < 1) {
            return false;
        }
        Element button = buttons.get(0);
        clickOnElement(button);
        return true;
    }

    private static boolean signIn(long timeout) {
        Element btnSignIn = iosTestHelper.waitForElementByNameVisible("signIn", timeout, 0, true, null, 2);
        if(btnSignIn == null) {
            finishReturn("Button 'Sign in' is null.", "click on sign in");
            return false;
        }
        iosTestHelper.takeScreenShot();
        clickOnElement(btnSignIn);

        Element btnSignInDialog = iosTestHelper.waitForElementByNameExists("Sign In", timeout, 2, true, null, 3);
        if(btnSignInDialog == null) {
            finishReturn("Dialog button 'Sign In' is null.", "click on sign in");
            return false;
        }

        Element textFieldEmail = iosTestHelper.waitForElementByClassExists(UIAElementType.UIATextField, 5000, 0, null, 2);
        ResponseItem responseItem = iosTestHelper.getResponseItem();
        log("Response type:" + responseItem.getResponseType().name() + "; value:" + responseItem.getReturnValue());

        log("element: " + responseItem.getElement());

        clickOnElement(textFieldEmail);
        Element btnClear = iosTestHelper.getElementByName("Clear text", 0, true, textFieldEmail, 1);
        if(btnClear != null)
            clickOnElement(btnClear);
        iosTestHelper.inputText(propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()), textFieldEmail);

        Element textFieldPassword = iosTestHelper.waitForElementByClassExists(UIAElementType.UIASecureTextField, 5000, 0, null, 2);
        clickOnElement(textFieldPassword);
        btnClear = iosTestHelper.getElementByName("Clear text", 0, true, textFieldPassword, 1);
        if(btnClear != null)
            clickOnElement(btnClear);
        iosTestHelper.inputText(propertiesManager.getProperty(ConfigurationParametersEnum.PASSWORD.name()), textFieldPassword);

        iosTestHelper.takeScreenShot();

        clickOnElement(btnSignInDialog);

        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
        Element btnFreeSample = iosTestHelper.waitForElementByNameExists("Free Sample", timeout*2, 0, true, null, 5);
        if(btnFreeSample == null){
            finishReturn("Button 'Free Sample' is null.", "FirstSync");
            return false;
        }

        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        TestManager.write(TestManager.addLogParams(new Date(), "FirstSync", "", true));

        iosTestHelper.waitForElementByNameExists("Network connection in progress", 15000, 0, true, null, 3);

        if(!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout*10, 0, true, null, 3)) {
            finishReturn("Network connection in progress is exist 5 min.", "FullSync");
            return false;
        }
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        while (iosTestHelper.waitForElementByNameExists("Network connection in progress", 3000, 0, true, null, 3) != null) {
            if(!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout*10, 0, true, null, 3)) {
                finishReturn("Network connection in progress is exist 5 min.", "FullSync");
                return false;
            }
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        }
        TestManager.write(TestManager.addLogParams(new Date(), "FullSync", "", true));
        return true;
    }
}


/*org.json.JSONException: JSONObject["response"] is not a JSONArray.
	at org.json.JSONObject.getJSONArray(JSONObject.java:578)
	at net.bugs.testhelper.ios.item.ResponseItem.<init>(ResponseItem.java:50)
	at net.bugs.testhelper.ios.exec.CommandsExecutor.waitAnswer(CommandsExecutor.java:92)
	at net.bugs.testhelper.ios.exec.CommandsExecutor.getResponseItem(CommandsExecutor.java:240)
	at net.bugs.testhelper.ios.exec.CommandsExecutor.getElementChildren(CommandsExecutor.java:609)
	at com.ios.testhelper.demo.Main.main(Main.java:119)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:120)
Exception in thread "main" java.lang.NullPointerException
	at net.bugs.testhelper.ios.item.ResponseItem.equalsResponseType(ResponseItem.java:133)
	at net.bugs.testhelper.ios.exec.CommandsExecutor.getElementChildren(CommandsExecutor.java:611)
	at com.ios.testhelper.demo.Main.main(Main.java:119)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:120)
*/