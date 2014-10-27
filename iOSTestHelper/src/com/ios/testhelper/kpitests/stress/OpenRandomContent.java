package com.ios.testhelper.kpitests.stress;

import com.ios.testhelper.kpitests.MainConstants;
import com.ios.testhelper.kpitests.PropertiesManager;
import com.ios.testhelper.kpitests.TestManager;
import com.ios.testhelper.kpitests.enums.ProductTypeEnum;
import com.ios.testhelper.kpitests.helpers.ITest;
import com.ios.testhelper.kpitests.kpi.KpiTest;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;
import net.bugs.testhelper.ios.item.ElementForWait;

import java.util.ArrayList;
import java.util.Random;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by nikolai on 10/27/14.
 */
public class OpenRandomContent extends KpiTest {
    private enum State{SUCCESS, ERROR}
    private State currentState = State.SUCCESS;

    private static final int DRP_READER = 1;
    private static final int EPUB_READER = 0;

    private String testName = "";
    private String testType = "stress";
    private int testCycle = 0;

    private String bookName = "";

    /**
     * value = 0 - without timeout
     */
    private long timeoutTestAction = 0;

    protected static final int SWIPE_UP = 0;
    protected static final int SWIPE_DOWN = 1;

    public OpenRandomContent(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
        super(iosTestHelper, propertiesManager, testManager);
    }

    protected int generateRandom(int min, int max){
        Random random = new Random();
        int randomIndex = random.nextInt((max-min)+1) + min;
        return randomIndex;
    }

    protected void randomSwipe(Element element, int swipeSide) {
        int randomIndex = generateRandom(1, 5);
        switch (swipeSide){
            case SWIPE_UP:
                while(randomIndex != 0) {
                    iosTestHelper.scrollUpInsideElement(element, element.getHeight(), 1);
                    randomIndex--;
                }
                break;
            case SWIPE_DOWN:
                while(randomIndex != 0) {
                    iosTestHelper.scrollDownInsideElement(element, element.getHeight(), 1);
                    randomIndex--;
                }
                break;
        }
    }

    protected Element waitForFillContainer(UIAElementType className, long timeoutMs, int instance, Element parentElement, int maxLevel) {
        Element collection = null;
        long startTime = System.currentTimeMillis();

        while (true) {
            if(timeoutMs != 0 && System.currentTimeMillis() - startTime > timeoutMs) {
                return null;
            }

            if((collection = iosTestHelper.waitForElementByClassExists(className, 1, instance, parentElement, maxLevel)) != null){
                if(iosTestHelper.getElementChildren(collection).size() > 0)
                    break;

            }
        }
        return collection;
    }

    protected Element getRandomElementByContainer(Element container){
        ArrayList<Element> children = iosTestHelper.getElementChildren(container);
        int index = generateRandom(0, children.size());

        i("Random index " + index);
        return iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionCell, 1, index, container, 1);
    }

    protected boolean downloadItem(long downloadTimeout) {
        Element element = iosTestHelper.waitForElementByNameVisible("Download", 10000, 0, true, null, 3);

        bookName = getBookName();

        if(element == null && iosTestHelper.waitForElementByNameVisible("Read", 10000, 0, true, null, 3) != null) {
            currentState = State.SUCCESS;
            return false;
        }

        if(element == null) {
            report("Download", bookName == null ? "" : bookName, false);
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            currentState = State.ERROR;
            return false;
        }

        ElementForWait readBtn = new ElementForWait(ElementForWait.QueryType.NAME, "Read", downloadTimeout, 0, 3, true);
        if (!iosTestHelper.saveClickAndWaitElement(element, null, readBtn)) {
            report("Download", bookName == null ? "" : bookName, false);
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            currentState = State.ERROR;
            return false;
        }

        return true;
    }

    /**
     *
     * @return index reader
     */
    protected int openItem() {
        // swipe up if iphone
        if(iosTestHelper.isIphone()) {
            Element scrollView = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAScrollView, 1000, 0, null, 3);
            iosTestHelper.scrollUpInsideElement(scrollView, scrollView.getHeight()*3, 3);
        }

        Element element = iosTestHelper.waitForElementByNameVisible("Read", 10000, 0, true, null, 3);
        iosTestHelper.saveClickOnElement(element);
        if(iosTestHelper.waitForElementByNameExists("Opening eBook...", 10000, 0, true, null, 3) != null) {
            if(!iosTestHelper.waitForElementByNameGone("Opening eBook...", 60000, 0, true, null, 3)){
                return -1;
            }
        }

        Element parentAddBookmark = iosTestHelper.waitForElementByClassExists("UIAScrollView", 1, 0, null, 3);
//        ElementForWait parentForWait = new ElementForWait(ElementForWait.QueryType.CLASS, "UIAScrollView", 1, 0, 3, true);
//        ElementForWait elementForWait = new ElementForWait(ElementForWait.QueryType.NAME, "Add bookmark", 30000, 0, 2, true); // todo

        if(iosTestHelper.waitForElementByNameExists("Back to library", 5000, 0, true, null, 3) != null ||
                iosTestHelper.waitForElementByNameExists("Back to Library", 5000, 0, true, null, 3) != null) {
            return EPUB_READER;
        } else if(iosTestHelper.waitForElementByNameExists("Add bookmark", 10000, 0, true, parentAddBookmark, 2) != null) {
            return DRP_READER;
        }
        else return -1;
    }

    protected boolean closeBook(int iReader, String backButton) {
        Element toolbar = null;
        if(iReader == DRP_READER) {
            toolbar = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 10000, 0, null, 2);
        }

        Element element = iosTestHelper.waitForElementByNameVisible(backButton, 10000, 0, true, iReader == DRP_READER ? toolbar : null, iReader == DRP_READER ? 1 : 3);
        if(element == null) {
            iosTestHelper.clickOnScreenCenter(0);
            element = iosTestHelper.waitForElementByNameVisible(backButton, 3000, 0, true, iReader == DRP_READER ? toolbar : null, iReader == DRP_READER ? 1 : 3);
        }

        if(element == null && iReader == DRP_READER) {
            return false;
        } else if(element == null && iReader == EPUB_READER) { // PDF_READER
            element = iosTestHelper.waitForElementByNameVisible("Back to Library", 10000, 0, true, iReader == DRP_READER ? toolbar : null, iReader == DRP_READER ? 1 : 3);
            if(element == null) {
                iosTestHelper.clickOnScreenCenter(0);
                element = iosTestHelper.waitForElementByNameVisible("Back to Library", 3000, 0, true, iReader == DRP_READER ? toolbar : null, iReader == DRP_READER ? 1 : 3);
            }
        }

        if(element == null)
            return false;

        iosTestHelper.saveClickOnElement(element);

        Element collection = waitForFillContainer(UIAElementType.UIACollectionView, 30000, 0, null, 2);
        if (collection == null) {
            i("Collection is null");
            return false;
        }

        return true;
    }

    protected boolean randomOpenBook() {
        Element collection = waitForFillContainer(UIAElementType.UIACollectionView, timeoutTestAction, 0, null, 2);
        if (collection == null) {
            i("Collection is null");
            report("RandomOpenBook", "", false);
            return false;
        }

        Element element = getRandomElementByContainer(collection);
        if (element == null) {
            i("Element is null");
            report("RandomOpenBook", "", false);
            return false;
        }

        iosTestHelper.longClickOnElement(element, 4);

        downloadItem(500000);
        if(currentState == State.ERROR)
            return false;

        int iReader = openItem();
        switch (iReader) {
            case EPUB_READER:
                closeBook(EPUB_READER, "Back to library");
                break;
            case DRP_READER:
                closeBook(DRP_READER, iosTestHelper.isIPad() ? "Library" : "library");
                break;
            default:
                report("Open", bookName, false);
                return false;
        }

        return true;
    }

    protected String getBookName() {
        Element navigationBar = iosTestHelper.waitForElementByClassExists("UIANavigationBar", 10000, 0, null, 2);
        if(navigationBar == null)
            return "";

        Element uiaStaticText = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAStaticText, 10000, 0, navigationBar, 1);
        if(uiaStaticText == null)
            return "";

        return uiaStaticText.getName();
    }

    @Override
    public boolean execute(ProductTypeEnum productTypeEnum) {
        testName = "OpenRandomContent";

        for(int indexCycle = 0; indexCycle < 100; indexCycle++) {
            testCycle = indexCycle;
            if(!randomOpenBook()) {
                reLaunchApp();
            }
        }
        return false;
    }

    protected void report(String testAction, String testData, boolean testResult) {
        iosTestHelper.reportStress(testName, testAction, testData, testType, testCycle+"", testResult);
    }

    protected void reLaunchApp() {
        iosTestHelper.deactivateAppForDuration(2000);
        iosTestHelper.stopInstruments();
        iosTestHelper.launchServer(false, true, 0);

    }
}
