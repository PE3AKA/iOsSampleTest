package com.ios.testhelper.kpitests.kpi;

import com.ios.testhelper.kpitests.MainConstants;
import com.ios.testhelper.kpitests.PropertiesManager;
import com.ios.testhelper.kpitests.TestManager;
import com.ios.testhelper.kpitests.enums.ConfigurationParametersEnum;
import com.ios.testhelper.kpitests.enums.ProductTypeEnum;
import com.ios.testhelper.kpitests.helpers.ITest;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;
import net.bugs.testhelper.ios.item.ElementForWait;

import java.util.ArrayList;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by avsupport on 10/9/14.
 */
public class TestOpenItemKpi extends KpiTest {

    public TestOpenItemKpi(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
        super(iosTestHelper, propertiesManager, testManager);
    }

    private String testName = "";

    @Override
    public boolean execute(ProductTypeEnum productTypeEnum) {
        switch (productTypeEnum) {
            case BOOK:
                testName = MainConstants.OPEN_BOOK_TEST_NAME;
                openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.BOOK.name()),
                        "Back to library",
                        -1,
                        propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.BOOK_DOWNLOAD_TIMEOUT.name()));
                break;
            case MAGAZINE:
                testName = MainConstants.OPEN_MAGAZINE_TEST_NAME;
                openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.WOODWIN_MAGAZINE.name()),
                        iosTestHelper.isIPad() ? "Library" : "library",
                        0,
                        propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.WOODWIN_DOWNLOAD_TIMEOUT.name()));
                break;
            case NEWSPAPER:
                testName = MainConstants.OPEN_NEWSPAPERS_TEST_NAME;
                break;
            case PDF:
                testName = MainConstants.OPEN_PDF_TEST_NAME;
                openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.PDF.name()),
                        "Back to Library",
                        -1,
                        propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.PDF_DOWNLOAD_TIMEOUT.name()));
                break;
            case COMICS:
                testName = MainConstants.OPEN_COMICS_TEST_NAME;
                openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.DRP_COMICS.name()),
                        iosTestHelper.isIPad() ? "Library" : "library",
                        0,
                        propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.COMICS_DOWNLOAD_TIMEOUT.name()));
                break;
        }
        return true;
    }

    private void openItemKpi(String name, String backButton, int toolbarIndex, int downloadTimeout) {
        iosTestHelper.goToLibrary();
        if (iosTestHelper.isIphone()) {
            Element toolbar = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 10000, 0, null, 2);
            if (toolbar != null) {
                while(iosTestHelper.waitForElementByNameExists("Network connection in progress", 3000, 0, true, null, 3) != null) {
                    if(!iosTestHelper.waitForElementByNameGone("Network connection in progress", 1000, 0, true, null, 3)) {
                        break;
                    }
                }
                iosTestHelper.saveClickOnElement(iosTestHelper.waitForElementByNameVisible("Search", 1, 0, true, toolbar, 1));
                iosTestHelper.sleep(1000);
            }
        }

        //---------------------
        //Search event
        //---------------------
        Element element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIASearchBar, 100000, 0, null, 3);
        iosTestHelper.saveClickOnElement(element);
        ArrayList<Element> buttons = iosTestHelper.getElementChildrenByType(element, UIAElementType.UIAButton);
        if (buttons.size() > 0) {
            iosTestHelper.saveClickOnElement(buttons.get(0));
        }
        iosTestHelper.inputText(name, element);

        Element keyboard = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAKeyboard, 1000, 0, null, 3);
        Element searchBtn = iosTestHelper.getElementByName("Search", 0, true, keyboard, 5);
        if(searchBtn == null) {
            i("Can not click on search button");
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            failSearch(name);
            return;
        }

        ElementForWait parentForWaitElement = new ElementForWait(ElementForWait.QueryType.CLASS, "UIACollectionView", 1, 0, 2, false);
        ElementForWait elementForWait = new ElementForWait(ElementForWait.QueryType.CLASS, "UIACollectionCell", 20000, 0, 1, false);
        if(!iosTestHelper.clickOnElementAndWaitElement(searchBtn, parentForWaitElement, elementForWait)) {
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            failSearch(name);
            return;
        }else{
            TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            i("search timeout: " + iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.passKpi(this.testName, MainConstants.OPEN_ITEM_SEARCH, name);
        }

        Element collection = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionView, 10000, 0, null, 2);

        i("element is visible?: " + element.isVisible());
        iosTestHelper.sleep(3000);
        element = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionCell, 10000, 0, collection, 1);
        iosTestHelper.longClickOnElementByXY(element, 0.5, 0.5, 4);

        //---------------------
        //Download event
        //---------------------
        element = iosTestHelper.waitForElementByNameVisible("Download", 10000, 0, true, null, 3);
        if (element == null) {
            TestManager.setStartTime(0);
            TestManager.setEndTime(0);
            iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_DOWNLOAD, name);
            iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            return;
        }

        ElementForWait readBtn = new ElementForWait(ElementForWait.QueryType.NAME, "Read", downloadTimeout, 0, 3, true);
        if(!iosTestHelper.saveClickAndWaitElement(element, null, readBtn)) {
            iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_DOWNLOAD, name);
            TestManager.setStartTime(0);
            TestManager.setEndTime(0);
            iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            return;
        }else{
            TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.passKpi(this.testName, MainConstants.OPEN_ITEM_DOWNLOAD, name);
        }

        // swipe up if iphone
        if(iosTestHelper.isIphone()){
            Element scrollView = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAScrollView, 1000, 0, null, 3);
            iosTestHelper.scrollUpInsideElement(scrollView, scrollView.getHeight()*3, 3);
        }

        //---------------------
        //Read event
        //---------------------
        element = iosTestHelper.waitForElementByNameVisible("Read", downloadTimeout, 0, true, null, 3);
        ElementForWait toolbarParent = null;
        if (toolbarIndex > -1) {
            toolbarParent = new ElementForWait(ElementForWait.QueryType.CLASS, "UIAToolBar", 1, toolbarIndex, 2, false);
        }

        ElementForWait backBtnWaitElement = new ElementForWait(ElementForWait.QueryType.NAME, backButton, 60000, 0, toolbarIndex > -1 ? 1 : 3, true);
        if(!iosTestHelper.saveClickAndWaitElement(element, toolbarIndex > -1 ? toolbarParent : null, backBtnWaitElement)) {
            TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
            return;
        }else{
            TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.passKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
        }

        //---------------------
        //Click on back button
        //---------------------
        Element toolbar = null;
        if(toolbarIndex > -1) {
            toolbar = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 6000, toolbarIndex, null, 2);
        }

        element = iosTestHelper.waitForElementByNameVisible(backButton, 6000, 0, true, toolbarIndex > -1 ? toolbar : null, toolbarIndex > -1 ? 1 : 3);
        if(element == null) {
            i("NOT VISIBLE");
            iosTestHelper.clickOnScreenCenter(0);
            element = iosTestHelper.waitForElementByNameVisible(backButton, 3000, 0, true, toolbarIndex > -1 ? toolbar : null, toolbarIndex > -1 ? 1 : 3);
        }

        iosTestHelper.saveClickOnElement(element);
        iosTestHelper.sleep(3000);
    }

    private void failSearch(String name) {
        iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_SEARCH, name);
        TestManager.setStartTime(0);
        TestManager.setEndTime(0);
        iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_DOWNLOAD, name);
        iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
    }

}
