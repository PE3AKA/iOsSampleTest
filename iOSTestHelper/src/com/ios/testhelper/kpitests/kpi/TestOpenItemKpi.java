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
            Element toolbar = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 10000, 1, null, 2);
            if (toolbar != null) {
                iosTestHelper.saveClickOnElement(iosTestHelper.waitForElementByNameVisible("Search", 1, 0, true, toolbar, 1));
                iosTestHelper.sleep(1000);
            }
        }
        Element element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIASearchBar, 100000, 0, null, 3);
        iosTestHelper.saveClickOnElement(element);
        ArrayList<Element> buttons = iosTestHelper.getElementChildrenByType(element, UIAElementType.UIAButton);
        if (buttons.size() > 0) {
            iosTestHelper.saveClickOnElement(buttons.get(0));
        }
        iosTestHelper.inputText(name, element);

        Element searchBtn = iosTestHelper.getElementByName("Search", 0, true, null, 5);
        if(searchBtn == null)
            i("Can not click on search button");

        //        ElementForWait elementForWait = new ElementForWait(ElementForWait.QueryType.NAME, "Library Results (", 10000, 0, 4, false);
        //        iosTestHelper.setStartTime();

        ElementForWait parentForWaitElement = new ElementForWait(ElementForWait.QueryType.CLASS, "UIACollectionView", 1, 0, 2, false);
        ElementForWait elementForWait = new ElementForWait(ElementForWait.QueryType.CLASS, "UIACollectionCell", 20000, 0, 1, false);
        if(!iosTestHelper.clickOnElementAndWaitElement(searchBtn, parentForWaitElement, elementForWait)){
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            failSearch(name);
            return;
        }else{
            TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            i("search timeout: " + iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.passKpi(this.testName, MainConstants.OPEN_ITEM_SEARCH, name);
        }

//        iosTestHelper.hideKeyboard();
//        iosTestHelper.setStartTime();
        Element collection = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionView, 10000, 0, null, 2);
//        if (collection == null) {
//            iosTestHelper.setEndTime();
//            failSearch(name);
//            return;
//        }
//        ArrayList<Element> elements = iosTestHelper.getElementChildren(collection);
//        i("Count collection cell:" + elements.size());
//
//        element = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionCell, 10000, 0, collection, 1);
//        iosTestHelper.setEndTime();
//        if (element == null) {
//            failSearch(name);
//            return;
//        } else {
//            iosTestHelper.passKpi(this.testName, MainConstants.OPEN_ITEM_SEARCH, name);
//        }

        i("element is visible?: " + element.isVisible());
        iosTestHelper.sleep(3000);
        element = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionCell, 10000, 0, collection, 1);
//        iosTestHelper.longClickOnElement(element, 0.5, 0.5, 4);
        iosTestHelper.longClickOnElementByXY(element, 0.5, 0.5, 4);

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
//        iosTestHelper.saveClickOnElement(element);
//        iosTestHelper.setStartTime();
        ElementForWait readBtn = new ElementForWait(ElementForWait.QueryType.NAME, "Read", downloadTimeout, 0, 3, true);
        if(!iosTestHelper.clickOnElementByXYAndWaitElement(element, 0.5, 0.5, null, readBtn)){
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

        element = iosTestHelper.waitForElementByNameVisible("Read", downloadTimeout, 0, true, null, 3);
//        iosTestHelper.setEndTime();
//        if (element == null) {
//            iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_DOWNLOAD, name);
//            TestManager.setStartTime(0);
//            TestManager.setEndTime(0);
//            iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
//            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
//            iosTestHelper.clickOnElement(closeBtn);
//            return;
//        }

//        iosTestHelper.saveClickOnElement(element);
//        iosTestHelper.setStartTime();
        ElementForWait toolbarParent = null;
//        Element toolbar = null;
        if (toolbarIndex > -1) {
//            toolbar = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 60000, toolbarIndex, null, 2);
            toolbarParent = new ElementForWait(ElementForWait.QueryType.CLASS, "UIAToolBar", 1, toolbarIndex, 2, false);

//            if (toolbar == null) {
//                iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
//                return;
//            }
        }

        ElementForWait backBtnWaitElement = new ElementForWait(ElementForWait.QueryType.NAME, backButton, 60000, 0, toolbarIndex > -1 ? 1 : 3, true);
        if(!iosTestHelper.clickOnElementByXYAndWaitElement(element, 0.5, 0.5, toolbarIndex > -1 ? toolbarParent : null, backBtnWaitElement)){
            TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
            return;
        }else{
            TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.passKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
        }

        Element toolbar = null;
        if(toolbarIndex > -1) {
            toolbar = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 60000, toolbarIndex, null, 2);
        }
        element = iosTestHelper.waitForElementByNameVisible(backButton, 60000, 0, true, toolbarIndex > -1 ? toolbar : null, toolbarIndex > -1 ? 1 : 3);
//        iosTestHelper.setEndTime();
//        if (element == null) {
//            iosTestHelper.failKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);
//            return;
//        }
//        iosTestHelper.passKpi(this.testName, MainConstants.OPEN_ITEM_OPEN, name);



        if (toolbarIndex == 0 && (iosTestHelper.waitForElementByNameVisible(backButton, 0, 0, true, toolbar, 1)==null)) {
            i("NOT VISIBLE");
            iosTestHelper.clickOnScreenCenter(0);
            iosTestHelper.sleep(1000);
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
