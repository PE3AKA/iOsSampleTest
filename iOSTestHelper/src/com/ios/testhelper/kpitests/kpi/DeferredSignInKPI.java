package com.ios.testhelper.kpitests.kpi;


import com.ios.testhelper.kpitests.Main;
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
 * Created by ashynkevich on 10/14/14.
 */

public class DeferredSignInKPI extends SignInKpi {

    public DeferredSignInKPI(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
        super(iosTestHelper, propertiesManager, testManager);
    }

    @Override
    public boolean execute(ProductTypeEnum productTypeEnum) {
        long testStartTime = System.currentTimeMillis();
        long timeout = propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.SAMPLE_DOWNLOAD_TIMEOUT.name());

        if (!deferredLogin(timeout)) return false;
//        if (!deferredOpenLibrary(timeout)) return false;

        if(!openSearch("cars", timeout - (System.currentTimeMillis() - testStartTime))) {
            return false;
        }

        iosTestHelper.stopInstruments();
        iosTestHelper.sleep(5000
        );
        Main.setUpIOsHelper(false);

        if (!deferredLogin(timeout)) return false;

        Element collection = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionView, 1, 0, null, 2);
        Element collectionCell = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionCell, 1, 4, collection, 1);

        if(collectionCell != null) {
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_CHECK_SAMPLE, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
        } else {
            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_CHECK_SAMPLE, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
        }

//        if (!deferredDownloadSample(timeout)) return false;
//        if (!deferredOpenSample(timeout, "Back to library", -1))
//            return false;

        return true;
    }

    private boolean openSearch(String query, long timeout) {
        if(timeout < 0) {
            i("Test failed by timeout");
            iosTestHelper.stopInstruments();
            System.exit(0);
        }

        Element element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIASearchBar, 100000, 0, null, 3);
        iosTestHelper.saveClickOnElement(element);
        ArrayList<Element> buttons = iosTestHelper.getElementChildrenByType(element, UIAElementType.UIAButton);
        if (buttons.size() > 0) {
            iosTestHelper.saveClickOnElement(buttons.get(0));
        }

//        ElementForWait parentElement = new ElementForWait(ElementForWait.QueryType.CLASS, UIAElementType.UIACollectionView.name(), 1, 1, 2, false);
        ElementForWait elementForWait = new ElementForWait(ElementForWait.QueryType.CLASS, UIAElementType.UIACollectionCell.name(), timeout, 5, 1, false);

//        iosTestHelper.inputTextAndWaitElement(query + "\n" ,element, parentElement, elementForWait);
        iosTestHelper.inputText(query, element);

        Element keyboard = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAKeyboard, 1000, 0, null, 3);
        Element searchBtn = iosTestHelper.getElementByName("Search", 0, true, keyboard, 5);
        if(searchBtn == null) {
            i("Can not click on search button");
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_LOAD_SAMPLE, query);
            return false;
        }

        ElementForWait parentForWaitElement = new ElementForWait(ElementForWait.QueryType.CLASS, "UIACollectionView", 1, 0, 2, false);
        if(iosTestHelper.clickOnElementAndWaitElement(searchBtn, parentForWaitElement, elementForWait) == null) {
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_LOAD_SAMPLE, query);
            return false;
        }else{
            TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
            i("search timeout: " + iosTestHelper.getResponseItem().getEndTime());
            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_LOAD_SAMPLE, query);
        }

        Element collection = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionView, timeout, 0, null, 2);

        ArrayList<Element> elements = iosTestHelper.getElementChildren(collection);

        Element necessaryElement = null;

        Element button = null;
        for(Element currentElement : elements) {
            button = iosTestHelper.waitForElementByNameExists("Sample", 1, 0, true, currentElement, 1);
            if(button != null) {
                necessaryElement = currentElement;
                break;
            }
        }

        if(button == null) {
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, "");
            return false;
        }

        ElementForWait parent = new ElementForWait(necessaryElement);
        elementForWait = new ElementForWait(ElementForWait.QueryType.NAME, "Read", timeout, 0, 1, true);

        Element readButton = iosTestHelper.clickOnElementAndWaitElement(button, parent, elementForWait);
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
        if(readButton == null) {
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, query);
            return false;
        } else {
            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, query);
        }



        elementForWait = new ElementForWait(ElementForWait.QueryType.NAME, new String[]{"Back to library", "Library", "library"}, timeout, new int[]{0}, 3, new boolean[]{true}, true);

        Element backToLibrary = iosTestHelper.clickOnElementAndWaitElement(button, null, elementForWait);

        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());

        if(backToLibrary == null) {
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, query);
            return false;
        } else {
            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, query);
        }

        iosTestHelper.clickOnElement(backToLibrary);

        return true;

//
//        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
//        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.SEARCH_SAMPLES, query);

    }

    public boolean deferredLogin(long timeout) {
        Element exploreAppBtn = null;
        if (iosTestHelper.isIPad()) {
            exploreAppBtn = iosTestHelper.waitForElementByNameVisible("explore the app", timeout, 1, true, null, 2);
        } else {
            exploreAppBtn = iosTestHelper.waitForElementByNameVisible("explore the app", timeout, 0, true, null, 2);
        }

        iosTestHelper.sleep(2000);

//        chooseCountry();

        if (exploreAppBtn == null) {
            finishReturn("Button 'Explore the App' is null.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.SING_IN_TEST_ACTION);
            return false;
        }

        ElementForWait parentElement = new ElementForWait(ElementForWait.QueryType.CLASS, UIAElementType.UIACollectionView.name(), 1, 0, 2, false);
        ElementForWait elementForWait = new ElementForWait(ElementForWait.QueryType.CLASS, UIAElementType.UIACollectionCell.name(), timeout, 3, 1, false);

        Element element = iosTestHelper.clickOnElementAndWaitElement(exploreAppBtn, parentElement, elementForWait);


        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FIRST_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
//
//        iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionCell.name(), timeout, 3, null, 3);
//        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
        if(element != null) {
            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FULL_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
        } else {
            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FULL_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
        }
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
        // swipe up if iphone
        if(iosTestHelper.isIphone()){
            Element scrollView = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAScrollView, 1000, 0, null, 3);
            if(scrollView != null)
                iosTestHelper.scrollUpInsideElement(scrollView, scrollView.getHeight()*3, 3);
        }

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











//
//
//
//package com.ios.testhelper.kpitests.kpi;
//
//
//        import com.ios.testhelper.kpitests.Main;
//        import com.ios.testhelper.kpitests.MainConstants;
//        import com.ios.testhelper.kpitests.PropertiesManager;
//        import com.ios.testhelper.kpitests.TestManager;
//        import com.ios.testhelper.kpitests.enums.ConfigurationParametersEnum;
//        import com.ios.testhelper.kpitests.enums.ProductTypeEnum;
//        import com.ios.testhelper.kpitests.helpers.ITest;
//        import net.bugs.testhelper.ios.enums.UIAElementType;
//        import net.bugs.testhelper.ios.item.Element;
//        import net.bugs.testhelper.ios.item.ElementForWait;
//
//        import java.util.ArrayList;
//
//        import static net.bugs.testhelper.helpers.LoggerUtil.i;
//
///**
// * Created by ashynkevich on 10/14/14.
// */

//public class DeferredSignInKPI extends SignInKpi {
//
//    public DeferredSignInKPI(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
//        super(iosTestHelper, propertiesManager, testManager);
//    }
//
//    @Override
//    public boolean execute(ProductTypeEnum productTypeEnum) {
//        long testStartTime = System.currentTimeMillis();
//        long timeout = propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.SAMPLE_DOWNLOAD_TIMEOUT.name());
//
//        if (!deferredLogin(timeout)) return false;
////        if (!deferredOpenLibrary(timeout)) return false;
//
//        if(!openSearch("cars", timeout - (System.currentTimeMillis() - testStartTime))) {
//            return false;
//        }
//
//        iosTestHelper.stopInstruments();
//        Main.setUpIOsHelper(false);
//
//        if (!deferredLogin(timeout)) return false;
//
//        Element collection = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionView, 1, 0, null, 2);
//        Element collectionCell = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionCell, 1, 4, collection, 1);
//
//        if(collectionCell != null) {
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_CHECK_SAMPLE, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
//        } else {
//            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_CHECK_SAMPLE, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
//        }
//
////        if (!deferredDownloadSample(timeout)) return false;
////        if (!deferredOpenSample(timeout, "Back to library", -1))
////            return false;
//
//        return true;
//    }
//
//    private boolean openSearch(String query, long timeout) {
//        if(timeout < 0) {
//            i("Test failed by timeout");
//            iosTestHelper.stopInstruments();
//            System.exit(0);
//        }
//
//        Element element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIASearchBar, 100000, 0, null, 3);
//        iosTestHelper.saveClickOnElement(element);
//        ArrayList<Element> buttons = iosTestHelper.getElementChildrenByType(element, UIAElementType.UIAButton);
//        if (buttons.size() > 0) {
//            iosTestHelper.saveClickOnElement(buttons.get(0));
//        }
//
//        iosTestHelper.inputText(query, element);
//
//        Element keyboard = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAKeyboard, 1000, 0, null, 3);
//        Element searchBtn = iosTestHelper.getElementByName("Search", 0, true, keyboard, 5);
//        if(searchBtn == null) {
//            i("Can not click on search button");
//            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_LOAD_SAMPLE, query);
//            return false;
//        }
//
//        ElementForWait parentForWaitElement = new ElementForWait(ElementForWait.QueryType.CLASS, "UIACollectionView", 1, 0, 2, false);
//        ElementForWait elementForWait = new ElementForWait(ElementForWait.QueryType.NAME, "Sample", timeout, 0, 2, true, true);
//
//        Element sample = null;
//        if((sample = iosTestHelper.clickOnElementAndWaitElement(searchBtn, parentForWaitElement, elementForWait)) == null) {
//            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_LOAD_SAMPLE, query);
//            return false;
//        }else{
//            TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
//            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//            i("search timeout: " + iosTestHelper.getResponseItem().getEndTime());
//            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_LOAD_SAMPLE, query);
//        }
//
//        elementForWait = new ElementForWait(ElementForWait.QueryType.NAME, "Read", timeout, 0, 1, true);
//
//        iosTestHelper.sleep(2000);
//
//        Element readButton = iosTestHelper.clickOnElementAndWaitElement(sample, parentForWaitElement, elementForWait);
//        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
//        if(readButton == null) {
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, query);
//            return false;
//        } else {
//            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, query);
//        }
//
//        iosTestHelper.sleep(2000);
//
//        elementForWait = new ElementForWait(ElementForWait.QueryType.NAME, new String[]{"Back to library", "Library", "library"}, timeout, new int[]{0}, 3, new boolean[]{true}, true);
//
//        Element backToLibrary = iosTestHelper.clickOnElementAndWaitElement(readButton, null, elementForWait);
//
//        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
//
//        if(backToLibrary == null) {
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, query);
//            return false;
//        } else {
//            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, query);
//        }
//
//        iosTestHelper.clickOnElement(backToLibrary);
//
//        return true;
//    }
//
//    public boolean deferredLogin(long timeout) {
//        Element exploreAppBtn = null;
//        if (iosTestHelper.isIPad()) {
//            exploreAppBtn = iosTestHelper.waitForElementByNameVisible("explore the app", timeout, 1, true, null, 2);
//        } else {
//            exploreAppBtn = iosTestHelper.waitForElementByNameVisible("explore the app", timeout, 0, true, null, 2);
//        }
//
//        iosTestHelper.sleep(2000);
//
////        chooseCountry();
//
//        if (exploreAppBtn == null) {
//            finishReturn("Button 'Explore the App' is null.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.SING_IN_TEST_ACTION);
//            return false;
//        }
//
//        ElementForWait parentElement = new ElementForWait(ElementForWait.QueryType.CLASS, UIAElementType.UIACollectionView.name(), 1, 0, 2, false);
//        ElementForWait elementForWait = new ElementForWait(ElementForWait.QueryType.CLASS, UIAElementType.UIACollectionCell.name(), timeout, 3, 1, false);
//
//        Element element = iosTestHelper.clickOnElementAndWaitElement(exploreAppBtn, parentElement, elementForWait);
//
//
//        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
//        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
////        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FIRST_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
////
////        iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionCell.name(), timeout, 3, null, 3);
////        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//        if(element != null) {
//            iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FULL_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
//        } else {
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FULL_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
//        }
//        return true;
//    }
//
//    private boolean deferredOpenLibrary(long timeout) {
//        iosTestHelper.openMenu();
//
//        Element library = iosTestHelper.waitForElementByNameVisible("Library", 10000, 0, true, null, 3);
//        iosTestHelper.saveClickOnElement(library);
//        iosTestHelper.setStartTime();
////
//        TestManager.setStartTime(iosTestHelper.getResponseItem().getStartTime());
//        long startTime = System.currentTimeMillis();
//
//        Element collection;
//        while (true) {
//            if (System.currentTimeMillis() - startTime > timeout * 2) {
//                finishReturn(" 'Collection' is null.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.LIBRARY_FIRST_SYNC);
//                return true;
//            }
//
//            if ((collection = iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionView, 1, 0, null, 2)) != null) {
//                if (iosTestHelper.getElementChildren(collection).size() > 0)
//                    break;
//            }
//
//            if (iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionView, 10000, 0, null, 2) != null) {
//                break;
//            }
//        }
//
//        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FIRST_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
//
//        iosTestHelper.waitForElementByNameExists("Network connection in progress", 15000, 0, true, null, 3);
//
//        if (!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout * 10, 0, true, null, 3)) {
//            finishReturn("Network connection in progress is exist 5 min.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.LIBRARY_FULL_SYNC);
//            return true;
//        }
//        TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//        while (iosTestHelper.waitForElementByNameExists("Network connection in progress", 3000, 0, true, null, 3) != null) {
//            if (!iosTestHelper.waitForElementByNameGone("Network connection in progress", timeout * 10, 0, true, null, 3)) {
//                finishReturn("Network connection in progress is exist 5 min.", MainConstants.DEFERRED_SIGN_IN_NAME, MainConstants.LIBRARY_FULL_SYNC);
//                return true;
//            }
//            TestManager.setEndTime(iosTestHelper.getResponseItem().getEndTime());
//        }
//        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.LIBRARY_FULL_SYNC, propertiesManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
//        return true;
//    }
//
//    private boolean deferredDownloadSample(long downloadTimeout) {
//        Element collection = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionView, 10000, 0, null, 2);
//        if (collection == null) {
//            iosTestHelper.setEndTime();
////            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_LOAD_SAMPLE, "");
//            return false;
//        }
//
//        ArrayList<Element> elements = iosTestHelper.getElementChildren(collection);
//        i("Count collection cell:" + elements.size());
//
//        Element element = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionCell, 10000, 0, collection, 1);
//        iosTestHelper.longClickOnElementByXY(element, 0.5, 0.5, 4);
//
//        element = iosTestHelper.waitForElementByNameVisible("Download", 10000, 0, true, null, 3);
//        if (element == null) {
//            TestManager.setStartTime(0);
//            TestManager.setEndTime(0);
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, "");
//            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
//            iosTestHelper.clickOnElement(closeBtn);
//            return false;
//        }
//        iosTestHelper.saveClickOnElement(element);
//        iosTestHelper.setStartTime();
//        element = iosTestHelper.waitForElementByNameVisible("Read", downloadTimeout, 0, true, null, 3);
//        iosTestHelper.setEndTime();
//        if (element == null) {
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, "");
//            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
//            iosTestHelper.clickOnElement(closeBtn);
//            return false;
//        }
//        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_DOWNLOAD_SAMPLE, "");
//        return true;
//    }
//
//    private boolean deferredOpenSample(long timeout,  String backButton, int toolbarIndex){
//        // swipe up if iphone
//        if(iosTestHelper.isIphone()){
//            Element scrollView = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAScrollView, 1000, 0, null, 3);
//            if(scrollView != null)
//                iosTestHelper.scrollUpInsideElement(scrollView, scrollView.getHeight()*3, 3);
//        }
//
//        Element element = iosTestHelper.waitForElementByNameVisible("Read", timeout, 0, true, null, 3);
//        iosTestHelper.saveClickOnElement(element);
//        iosTestHelper.setStartTime();
//
//        if(toolbarIndex > -1) {
//            element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 60000, toolbarIndex, null, 2);
//
//            if (element == null) {
//                iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, "");
//                return false;
//            }
//        }
//
//        element = iosTestHelper.waitForElementByNameVisible(backButton, 60000, 0, true, toolbarIndex > -1 ? element : null, toolbarIndex > -1 ? 1 : 3);
//        iosTestHelper.setEndTime();
//        if(element == null) {
//            iosTestHelper.failKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, "");
//            return false;
//        }
//
//        iosTestHelper.passKpi(MainConstants.DEFERRED_LIBRARY_TEST_NAME, MainConstants.DEFERRED_LIBRARY_OPEN_SAMPLE, "");
//        iosTestHelper.saveClickOnElement(element);
//
//        iosTestHelper.sleep(3000);
//        return true;
//    }
//}


