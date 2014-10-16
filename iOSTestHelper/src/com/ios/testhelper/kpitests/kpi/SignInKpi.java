package com.ios.testhelper.kpitests.kpi;

import com.ios.testhelper.kpitests.MainConstants;
import com.ios.testhelper.kpitests.enums.ConfigurationParametersEnum;
import com.ios.testhelper.kpitests.PropertiesManager;
import com.ios.testhelper.kpitests.TestManager;
import com.ios.testhelper.kpitests.enums.ProductTypeEnum;
import com.ios.testhelper.kpitests.helpers.ITest;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;
import net.bugs.testhelper.ios.item.ResponseItem;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by avsupport on 10/9/14.
 */
public class SignInKpi extends KpiTest {

    public SignInKpi(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
        super(iosTestHelper, propertiesManager, testManager);
    }

    public boolean execute(ProductTypeEnum productTypeEnum) {
        long timeout = propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.TIMEOUT.name());

        Element btnSignIn = iosTestHelper.waitForElementByNameVisible(iosTestHelper.isIPad() ? "explore the app" : "signIn", timeout, 0, true, null, 2);

        iosTestHelper.sleep(2000);

        chooseCountry();

        if(btnSignIn == null) {
            finishReturn("Button 'Sign in' is null.", MainConstants.SING_IN_TEST_NAME, MainConstants.SING_IN_TEST_ACTION);
            return false;
        }

        btnSignIn = iosTestHelper.waitForElementByNameVisible(iosTestHelper.isIPad() ? "explore the app" : "signIn", timeout, 0, true, null, 2);

//        iosTestHelper.takeScreenShot();
        iosTestHelper.saveClickOnElement(btnSignIn);

//        Element btnSignInDialog = iosTestHelper.waitForElementByNameExists("Sign In", timeout, 2, true, null, 3);
        Element btnSignInDialog = iosTestHelper.waitForElementByNameExists("Sign In", timeout, 0, true, null, 2);
        if(btnSignInDialog == null) {
            finishReturn("Dialog button 'Sign In' is null.", MainConstants.SING_IN_TEST_NAME, MainConstants.SING_IN_TEST_ACTION);
            return false;
        }

        Element textFieldEmail = iosTestHelper.waitForElementByClassExists(UIAElementType.UIATextField, 5000, 0, null, 2);
        ResponseItem responseItem = iosTestHelper.getResponseItem();
        i("Response type:" + responseItem.getResponseType().name() + "; value:" + responseItem.getReturnValue());

        i("element: " + responseItem.getElement());

        iosTestHelper.saveClickOnElement(textFieldEmail);
        Element btnClear = iosTestHelper.getElementByName("Clear text", 0, true, textFieldEmail, 1);
        if(btnClear != null)
            iosTestHelper.saveClickOnElement(btnClear);
        iosTestHelper.inputText(propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()), textFieldEmail);

        iosTestHelper.sleep(1000);

        Element textFieldPassword = iosTestHelper.waitForElementByClassExists(UIAElementType.UIASecureTextField, 5000, 0, null, 2);
        iosTestHelper.saveClickOnElement(textFieldPassword);
        btnClear = iosTestHelper.getElementByName("Clear text", 0, true, textFieldPassword, 1);
        if(btnClear != null)
            iosTestHelper.saveClickOnElement(btnClear);
        iosTestHelper.inputText(propertiesManager.getProperty(ConfigurationParametersEnum.PASSWORD.name()) + "\n", textFieldPassword);

        if(iosTestHelper.isIphone()) {
            iosTestHelper.sleep(1000);
//            btnSignInDialog = iosTestHelper.waitForElementByNameExists("Sign In", timeout, 0, true, null, 2);
            iosTestHelper.saveClickOnElement(btnSignInDialog);
        }

        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());

//        iosTestHelper.takeScreenShot();


        long startTime = System.currentTimeMillis();
        Element collection;
        while (true) {
            if(System.currentTimeMillis() - startTime > timeout*2){
                finishReturn("Library is not loaded.", MainConstants.LIBRARY_TEST_NAME, MainConstants.LIBRARY_FIRST_SYNC);
                return false;
            }

            if((collection = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionView, 1, 0, null, 2)) != null){
                if(iosTestHelper.getElementChildren(collection).size() > 0)
                    break;
//                org.json.JSONException: JSONObject["isVisible"] is not a number. todo
//                at org.json.JSONObject.getDouble(JSONObject.java:543)
//                at org.json.JSONObject.getInt(JSONObject.java:560)
//                at net.bugs.testhelper.ios.item.Element.<init>(Element.java:48)
//                at net.bugs.testhelper.ios.item.ResponseItem.<init>(ResponseItem.java:53)
//                at net.bugs.testhelper.ios.exec.CommandsExecutor.waitAnswer(CommandsExecutor.java:118)
//                at net.bugs.testhelper.ios.exec.CommandsExecutor.getResponseItem(CommandsExecutor.java:331)
//                at net.bugs.testhelper.ios.exec.CommandsExecutor.getElementChildren(CommandsExecutor.java:763)
//                at com.ios.testhelper.kpitests.kpi.SignInKpi.execute(SignInKpi.java:89)
//                at com.ios.testhelper.kpitests.Main.startTest(Main.java:160)
//                at com.ios.testhelper.kpitests.Main.startTest(Main.java:150)
//                at com.ios.testhelper.kpitests.Main.mainLogic(Main.java:122)
//                at com.ios.testhelper.kpitests.Main.main(Main.java:77)
//                at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//                at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
//                at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//                at java.lang.reflect.Method.invoke(Method.java:606)
//                at com.intellij.rt.execution.application.AppMain.main(AppMain.java:120)

            }

            if(iosTestHelper.waitForElementByNameExists("Free Sample", 1, 0, true, null, 5) != null){
                break;
            }
        }

        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());

        iosTestHelper.passKpi(MainConstants.LIBRARY_TEST_NAME, MainConstants.LIBRARY_FIRST_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));

        iosTestHelper.waitForElementByNameExists("Network connection in progress", 15000, 0, true, null, 3);

        if(!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout*10, 0, true, null, 3)) {
            finishReturn("Network connection in progress is exist 5 min.", MainConstants.LIBRARY_TEST_NAME, "FullSync");
            return false;
        }
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        while (iosTestHelper.waitForElementByNameExists("Network connection in progress", 3000, 0, true, null, 3) != null) {
            if(!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout*10, 0, true, null, 3)) {
                finishReturn("Network connection in progress is exist 5 min.", MainConstants.LIBRARY_TEST_NAME, "FullSync");
                return false;
            }
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        }
        iosTestHelper.passKpi(MainConstants.LIBRARY_TEST_NAME, MainConstants.LIBRARY_FULL_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
        return true;
    }

    protected void chooseCountry() {
        Element element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAPickerWheel, 10000, 0, null, 3);
        if(element != null && element.getValue().equals("United States. 2 of 2")) {
            return;
        }

        Element chooseCountry = iosTestHelper.waitForElementByNameVisible("select country", 10000, 0, true, null, 2);
        iosTestHelper.sleep(2000);

        iosTestHelper.clickOnElement(chooseCountry);

        chooseCountry.getValue();
        Element picker = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAPicker, 10000, 0, null, 2);
        iosTestHelper.sleep(1000);

        int x = picker.getX() + picker.getWidth() / 2;
        int y = picker.getY() + picker.getHeight() - 10;

        iosTestHelper.clickByXY(x, y);
    }

    protected void finishReturn(String msgLog, String testName, String testAction){
        i(msgLog);
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        iosTestHelper.failKpi(testName, testAction, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
    }
}
