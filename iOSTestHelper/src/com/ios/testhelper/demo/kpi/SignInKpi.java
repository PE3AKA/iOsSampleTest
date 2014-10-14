package com.ios.testhelper.demo.kpi;

import com.ios.testhelper.demo.enums.ConfigurationParametersEnum;
import com.ios.testhelper.demo.PropertiesManager;
import com.ios.testhelper.demo.TestManager;
import com.ios.testhelper.demo.enums.ProductTypeEnum;
import com.ios.testhelper.demo.helpers.ITest;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;
import net.bugs.testhelper.ios.item.ResponseItem;

import java.util.ArrayList;
import java.util.Date;

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
            finishReturn("Button 'Sign in' is null.", "click on sign in");
            return false;
        }

        btnSignIn = iosTestHelper.waitForElementByNameVisible(iosTestHelper.isIPad() ? "explore the app" : "signIn", timeout, 0, true, null, 2);

//        iosTestHelper.takeScreenShot();
        iosTestHelper.saveClickOnElement(btnSignIn);

        Element btnSignInDialog = iosTestHelper.waitForElementByNameExists("Sign In", timeout, 2, true, null, 3);
        if(btnSignInDialog == null) {
            finishReturn("Dialog button 'Sign In' is null.", "click on sign in");
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
            iosTestHelper.saveClickOnElement(btnSignInDialog);
        }

        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());

//        iosTestHelper.takeScreenShot();


        long startTime = System.currentTimeMillis();
        Element collection;
        while (true) {
            if(System.currentTimeMillis() - startTime > timeout*2){
                finishReturn("Button 'Free Sample' is null.", "FirstSync");
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
//                at com.ios.testhelper.demo.kpi.SignInKpi.execute(SignInKpi.java:89)
//                at com.ios.testhelper.demo.Main.startTest(Main.java:160)
//                at com.ios.testhelper.demo.Main.startTest(Main.java:150)
//                at com.ios.testhelper.demo.Main.mainLogic(Main.java:122)
//                at com.ios.testhelper.demo.Main.main(Main.java:77)
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

    protected void finishReturn(String msgLog, String testAction){
        i(msgLog);
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        testManager.write(testManager.addLogParams(new Date(), testAction, "", false));
    }
}
