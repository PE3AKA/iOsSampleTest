package com.ios.testhelper.demo.kpi;


import com.ios.testhelper.demo.MainConstants;
import com.ios.testhelper.demo.PropertiesManager;
import com.ios.testhelper.demo.TestManager;
import com.ios.testhelper.demo.enums.ConfigurationParametersEnum;
import com.ios.testhelper.demo.enums.ProductTypeEnum;
import com.ios.testhelper.demo.helpers.ITest;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;

import java.util.ArrayList;
import java.util.Date;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by ashynkevich on 10/14/14.
 */

public class DeferredSignInKPI extends SignInKpi {

    public DeferredSignInKPI(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
        super(iosTestHelper, propertiesManager, testManager);
    }

    @Override
    public boolean execute(ProductTypeEnum productTypeEnum) {
        long timeout = propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.SAMPLE_DOWNLOAD_TIMEOUT.name());

        if (!deferredLogin(timeout)) return false;
//        if (!deferredOpenLibrary(timeout)) return false;
        if (!deferredDownloadSample(timeout)) return false;
        if (!deferredOpenSample(timeout, "Back to library", -1))
            return false;

        return true;
    }

    public boolean deferredLogin(long timeout) {
        Element exploreAppBtn = null;
        if (iosTestHelper.isIPad()) {
            exploreAppBtn = iosTestHelper.waitForElementByNameVisible("explore the app", timeout, 1, true, null, 2);
        }
        if (iosTestHelper.isIphone()) {
            exploreAppBtn = iosTestHelper.waitForElementByNameVisible("explore the app", timeout, 0, true, null, 2);
        }

        iosTestHelper.sleep(2000);

        chooseCountry();

        if (exploreAppBtn == null) {
            finishReturn("Button 'Explore the App' is null.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.SING_IN_TEST_ACTION);
            return false;
        }

        iosTestHelper.saveClickOnElement(exploreAppBtn);
        iosTestHelper.sleep(2000);

        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FIRST_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));

        iosTestHelper.waitForElementByNameExists("Network connection in progress", 15000, 0, true, null, 3);

        if (!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout * 10, 0, true, null, 3)) {
            finishReturn("Network connection in progress is exist 5 min.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.LIBRARY_FIRST_SYNC);
            return true;
        }
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        while (iosTestHelper.waitForElementByNameExists("Network connection in progress", 3000, 0, true, null, 3) != null) {
            if (!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout * 10, 0, true, null, 3)) {
                finishReturn("Network connection in progress is exist 5 min.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.LIBRARY_FULL_SYNC);
                return true;
            }
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        }
        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FULL_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));

        return true;
    }

    private boolean deferredOpenLibrary(long timeout) {
        iosTestHelper.openMenu();

        Element library = iosTestHelper.waitForElementByNameVisible("Library", 10000, 0, true, null, 3);
        iosTestHelper.saveClickOnElement(library);
        iosTestHelper.setStartTime();
//
        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
        long startTime = System.currentTimeMillis();

        Element collection;
        while (true) {
            if (System.currentTimeMillis() - startTime > timeout * 2) {
                 finishReturn(" 'Collection' is null.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.LIBRARY_FIRST_SYNC);
                return true;
            }

            if ((collection = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionView, 1, 0, null, 2)) != null) {
                if (iosTestHelper.getElementChildren(collection).size() > 0)
                    break;
            }

            if (iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionView, 10000, 0, null, 2) != null) {
                break;
            }
        }

        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FIRST_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));

        iosTestHelper.waitForElementByNameExists("Network connection in progress", 15000, 0, true, null, 3);

        if (!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout * 10, 0, true, null, 3)) {
             finishReturn("Network connection in progress is exist 5 min.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.LIBRARY_FULL_SYNC);
            return true;
        }
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        while (iosTestHelper.waitForElementByNameExists("Network connection in progress", 3000, 0, true, null, 3) != null) {
            if (!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout * 10, 0, true, null, 3)) {
                 finishReturn("Network connection in progress is exist 5 min.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.LIBRARY_FULL_SYNC);
                return true;
            }
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        }
        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FULL_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
        return true;
    }

    private boolean deferredDownloadSample(long downloadTimeout) {
        Element collection = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionView, 10000, 0, null, 2);
        if (collection == null) {
            iosTestHelper.setEndTime();
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_LOAD_SAMPLE, "");
            return false;
        }

        ArrayList<Element> elements = iosTestHelper.getElementChildren(collection);
        i("Count collection cell:" + elements.size());

        Element element = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionCell, 10000, 0, collection, 1);
        iosTestHelper.longClickOnElementByXY(element, 0.5, 0.5, 4);

        element = iosTestHelper.waitForElementByNameVisible("Download", 10000, 0, true, null, 3);
        if (element == null) {
            TestManager.setStartTime(0);
            TestManager.setEndTime(0);
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, "");
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            return false;
        }
        iosTestHelper.saveClickOnElement(element);
        iosTestHelper.setStartTime();
        element = iosTestHelper.waitForElementByNameVisible("Read", downloadTimeout, 0, true, null, 3);
        iosTestHelper.setEndTime();
        if (element == null) {
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, "");
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            return false;
        }
        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, "");
        return true;
    }

    private boolean deferredOpenSample(long timeout,  String backButton, int toolbarIndex){
        Element element = iosTestHelper.waitForElementByNameVisible("Read", timeout, 0, true, null, 3);
        iosTestHelper.saveClickOnElement(element);
        iosTestHelper.setStartTime();

        if(toolbarIndex > -1) {
            element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 60000, toolbarIndex, null, 2);

            if (element == null) {
                iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, "");
                return false;
            }
        }

        element = iosTestHelper.waitForElementByNameVisible(backButton, 60000, 0, true, toolbarIndex > -1 ? element : null, toolbarIndex > -1 ? 1 : 3);
        iosTestHelper.setEndTime();
        if(element == null) {
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, "");
            return false;
        }

        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, "");
        iosTestHelper.saveClickOnElement(element);

        iosTestHelper.sleep(3000);
        return true;
    }
}

