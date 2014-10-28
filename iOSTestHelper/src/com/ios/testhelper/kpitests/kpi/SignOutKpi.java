package com.ios.testhelper.kpitests.kpi;

import com.ios.testhelper.kpitests.MainConstants;
import com.ios.testhelper.kpitests.PropertiesManager;
import com.ios.testhelper.kpitests.TestManager;
import com.ios.testhelper.kpitests.enums.ProductTypeEnum;
import com.ios.testhelper.kpitests.helpers.ITest;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;

import java.util.ArrayList;

/**
 * Created by avsupport on 10/9/14.
 */
public class SignOutKpi extends KpiTest {

    public SignOutKpi(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
        super(iosTestHelper, propertiesManager, testManager);
    }

    @Override
    public boolean execute(ProductTypeEnum productTypeEnum) {
        Element element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIASearchBar, 5000, 0, null, 3);
        ArrayList<Element> buttons = iosTestHelper.getElementChildrenByType(element, UIAElementType.UIAButton);
        if(buttons.size() > 0) {
            iosTestHelper.saveClickOnElement(buttons.get(0));
        }

        if(iosTestHelper.isIphone()){
            Element cancelBtn = iosTestHelper.waitForElementByNameExists("Cancel", 1000, 0, true, null, 3);
            iosTestHelper.saveClickOnElement(cancelBtn);
        }

//        iosTestHelper.hideKeyboard();
        iosTestHelper.openMenu();

        iosTestHelper.setStartTime();
        element = iosTestHelper.waitForElementByNameVisible("Settings", 5000, 0, true, null, 3);
        iosTestHelper.setEndTime();
        if(element == null) iosTestHelper.failKpi(MainConstants.SIGN_OUT_TEST, MainConstants.SIGN_OUT_ACTION, "");
        iosTestHelper.assertNotNull(element);
        iosTestHelper.clickOnElement(element);

        int [] deviceSizes = iosTestHelper.getScreenSize();
        iosTestHelper.setStartTime();

        if(deviceSizes[1] == 480){
            element = iosTestHelper.waitForElementByClassExists("UIATableView", 10000, 0, null, 2);
            if(element != null) {
                ArrayList<Element> elements = iosTestHelper.getElementChildren(element);
                element = elements.get(elements.size() - 1);
            }
        }else {
            element = iosTestHelper.waitForElementByNameVisible("logout", 10000, 0, true, null, 3);
        }

        iosTestHelper.setEndTime();
        if(element == null) iosTestHelper.failKpi(MainConstants.SIGN_OUT_TEST, MainConstants.SIGN_OUT_ACTION, "");
        iosTestHelper.assertNotNull(element);
        iosTestHelper.clickOnElement(element);

        iosTestHelper.setStartTime();
        iosTestHelper.sleep(1000);
        iosTestHelper.waitForElementByNameVisible(iosTestHelper.isIPad() ? "explore the app" : "signIn", 30000, 0, true, null, 2);
        iosTestHelper.setEndTime();
        if(element == null) iosTestHelper.passKpi(MainConstants.SIGN_OUT_TEST, MainConstants.SIGN_OUT_ACTION, "");
        return true;
    }
}
