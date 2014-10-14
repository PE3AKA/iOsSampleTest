package com.ios.testhelper.demo.kpi;

import com.ios.testhelper.demo.PropertiesManager;
import com.ios.testhelper.demo.TestManager;
import com.ios.testhelper.demo.enums.ProductTypeEnum;
import com.ios.testhelper.demo.helpers.ITest;
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
        iosTestHelper.hideKeyboard();
        iosTestHelper.openMenu();

        iosTestHelper.setStartTime();
        element = iosTestHelper.waitForElementByNameVisible("Settings", 5000, 0, true, null, 3);
        iosTestHelper.setEndTime();
        if(element == null) iosTestHelper.failKpi("SignOut");
        iosTestHelper.assertNotNull(element);
        iosTestHelper.clickOnElement(element);

        iosTestHelper.setStartTime();
        element = iosTestHelper.waitForElementByNameVisible("Log out", 5000, 0, true, null, 3);
        iosTestHelper.setEndTime();
        if(element == null) iosTestHelper.failKpi("SignOut");
        iosTestHelper.assertNotNull(element);
        iosTestHelper.clickOnElement(element);

        iosTestHelper.setStartTime();
        iosTestHelper.sleep(1000);
        iosTestHelper.waitForElementByNameVisible(iosTestHelper.isIPad() ? "explore the app" : "signIn", 30000, 0, true, null, 2);
        iosTestHelper.setEndTime();
        if(element == null) iosTestHelper.passKpi("SignOut");
        return true;
    }
}
