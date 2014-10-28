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

    private enum State{DOWNLOAD_SUCCESS, DOWNLOAD_ERROR, DOWNLOAD_MISSING}
    private State currentState = State.DOWNLOAD_SUCCESS;

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

    public OpenRandomContent(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
        super(iosTestHelper, propertiesManager, testManager);
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
        int index = iosTestHelper.generateRandom(0, children.size());

        i("Random index " + index);
        return iosTestHelper.waitForElementByClassExists(UIAElementType.UIACollectionCell, 1, index, container, 1);
    }

    protected boolean downloadItem(long downloadTimeout) {
        Element element = iosTestHelper.waitForElementByNameVisible("Download", 10000, 0, true, null, 3);

        bookName = getBookName();

        if(element == null && iosTestHelper.waitForElementByNameVisible("Read", 10000, 0, true, null, 3) != null) {
            currentState = State.DOWNLOAD_MISSING;
            return false;
        }

        if(element == null) {
            report("Download", bookName == null ? "" : bookName, false);
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            currentState = State.DOWNLOAD_ERROR;
            return false;
        }

        ElementForWait readBtn = new ElementForWait(ElementForWait.QueryType.NAME, "Read", downloadTimeout, 0, 3, true);
        if (!iosTestHelper.saveClickAndWaitElement(element, null, readBtn)) {
            report("Download", bookName == null ? "" : bookName, false);
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            currentState = State.DOWNLOAD_ERROR;
            return false;
        }else{
            report("Download", bookName == null ? "" : bookName, true);
            currentState = State.DOWNLOAD_SUCCESS;
        }

        return true;
    }

    /**
     *
     * @return index reader
     */
    protected int openItem() {
        if(iosTestHelper.isIphone() && currentState != State.DOWNLOAD_MISSING) {
            Element scrollView = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAScrollView, 1000, 0, null, 3);
            iosTestHelper.scrollUpInsideElement(scrollView, scrollView.getHeight()*3, 3);
        }

        Element element = iosTestHelper.waitForElementByNameVisible("Read", 10000, 0, true, null, 3);
        iosTestHelper.saveClickOnElement(element);
        if(iosTestHelper.waitForElementByNameExists("Opening eBook...", 10000, 0, true, null, 3) != null) {
            if(!iosTestHelper.waitForElementByNameGone("Opening eBook...", 60000, 0, true, null, 3)){
                report("Open", bookName == null ? "" : bookName, false);
                return -1;
            }
        }

        Element parentAddBookmark = iosTestHelper.waitForElementByClassExists("UIAScrollView", 1, 0, null, 3);
        if(iosTestHelper.waitForElementByNameExists("Back to library", 5000, 0, true, null, 3) != null ||
                iosTestHelper.waitForElementByNameExists("Back to Library", 5000, 0, true, null, 3) != null) {
            report("Open", bookName == null ? "" : bookName, true);
            return EPUB_READER;
        } else if(iosTestHelper.waitForElementByNameExists("Add bookmark", 10000, 0, true, parentAddBookmark, 4) != null) {
            report("Open", bookName == null ? "" : bookName, true);
            return DRP_READER;
        }
        else
            return -1;
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

        if(element == null) {
            report("CloseBook", bookName == null ? "" : bookName, false);
            return false;
        }

        iosTestHelper.saveClickOnElement(element);

        Element collection = waitForFillContainer(UIAElementType.UIACollectionView, 30000, 0, null, 2);
        if (collection == null) {
            i("Collection is null");
            report("CloseBook", bookName == null ? "" : bookName, false);
            return false;
        }else {
            report("CloseBook", bookName == null ? "" : bookName, true);
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
        if(currentState == State.DOWNLOAD_ERROR)
            return false;

        int iReader = openItem();
        switch (iReader) {
            case EPUB_READER:
                changeEpubReaderState();
                if(!closeBook(EPUB_READER, "Back to library"))
                    return false;
                break;
            case DRP_READER:
                changeDrpReaderState();
                if(!closeBook(DRP_READER, iosTestHelper.isIPad() ? "Library" : "library"))
                    return false;
                break;
            default:
                report("Open", bookName, false);
                return false;
        }

        return true;
    }

    private void changeOrientation() {
        switch (iosTestHelper.generateRandom(0, 2)){
            case 0:
                if(iosTestHelper.setOrientationLandscapeLeft()) {
                    report("ChangeOrientation", "landscape", true);
                } else
                    report("ChangeOrientation", "landscape", false);
                break;
            case 1:
                if(iosTestHelper.setOrientationPortrait()) {
                    report("ChangeOrientation", "portrait", true);
                } else
                    report("ChangeOrientation", "portrait", false);
                break;
        }

    }

    private void changeEpubReaderState() {
        int index = iosTestHelper.generateRandom(0, 2);
        switch (index) {
            case 0:
                changeTextOptions();
                break;
            case 1:
                pageTurn();
                break;
        }
    }

    private void changeDrpReaderState() {
        int index = iosTestHelper.generateRandom(0, 2);
        switch (index){
            case 0:
                addBookmark();
                break;
            case 1:
                pageTurn();
                break;
        }
    }

    private void pageTurn(){
        int index = iosTestHelper.generateRandom(0, 2);
        Element uiaScrollView = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAScrollView, 1, 0, null, 3);

        if(uiaScrollView == null) {
            report("PageTurn", bookName == null ? "" : bookName, false);
            return;
        }

        switch (index) {
            case 0:
                iosTestHelper.scrollLeftInsideElement(uiaScrollView, uiaScrollView.getWidth(), 0.5);
                report("PageTurnLeft", bookName == null ? "" : bookName, true);
                break;
            case 1:
                iosTestHelper.scrollRightInsideElement(uiaScrollView, uiaScrollView.getWidth(), 0.5);
                report("PageTurnRight", bookName == null ? "" : bookName, true);
                break;
        }
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

    protected boolean changeTextOptions() {
        addBookmark();
        Element btnTextOptions = iosTestHelper.waitForElementByNameExists("Text options", 1, 0, true, null, 2);
        if (btnTextOptions == null) {
            iosTestHelper.clickOnScreenCenter(0);
            btnTextOptions = iosTestHelper.waitForElementByNameExists("Text options", 1, 0, true, null, 2);
        }

        if (btnTextOptions == null) {
            report("ChangeTextOptions", "", false);
            return false;
        }

        iosTestHelper.saveClickOnElement(btnTextOptions);

        if (iosTestHelper.waitForElementByNameExists("Text options", 10000, 0, true, null, 2) == null) {
            report("ChangeTextOptions", "", false);
            return false;
        }

        switch (iosTestHelper.generateRandom(0, 3)) {
            case 0:
                changeSize(0, true);
                break;
            case 1:
                changeLineSpacing(0, true);
                break;
            case 2:
                changeMargins(0, true);
                break;
        }

        return true;
    }

    protected boolean changeLineSpacing(int index, boolean isRandom) {
        if(isRandom) {
            index = iosTestHelper.generateRandom(0, 3);
        }

        Element element = null;
        String testData = "";
        switch (index) {
            case ITest.SINGLE_LINE_SPACING:
                element = iosTestHelper.waitForElementByNameExists("SingleLineSpacing", 1, 0, true, null, 2);
                testData = "SingleLineSpacing";
                break;
            case ITest.ONE_AND_HALF_LINES_SPACING:
                element = iosTestHelper.waitForElementByNameExists("OneAndHalfLinesSpacing", 1, 0, true, null, 2);
                testData = "OneAndHalfLinesSpacing";
                break;
            case ITest.MULTIPLE_LINE_SPACING:
                element = iosTestHelper.waitForElementByNameExists("MultipleLinesSpacing", 1, 0, true, null, 2);
                testData = "MultipleLinesSpacing";
                break;
        }

        iosTestHelper.saveClickOnElement(element);

        if(iosTestHelper.waitForElementByNameExists("Applying settings...", 10000, 0, true, null, 3) != null) {
            if(!iosTestHelper.waitForElementByNameGone("Applying settings...", 60000, 0, true, null, 3)) {
                report("ChangeTextLineSpacing", (bookName == null ? "" : bookName) + "&" + testData, false);
                return false;
            } else {
                report("ChangeTextLineSpacing", (bookName == null ? "" : bookName) + "&" + testData, true);
            }
        }else {
            Element closeElement = iosTestHelper.waitForElementByNameExists("Close", 1, 0, true, null, 2);
            iosTestHelper.saveClickOnElement(closeElement);
            return false;
        }

        return true;
    }

    protected boolean changeMargins(int index, boolean isRandom) {
        if(isRandom) {
            index = iosTestHelper.generateRandom(0, 3);
        }

        Element element = null;
        String testData = "";
        switch (index) {
            case ITest.SMALL_MARGIN:
                element = iosTestHelper.waitForElementByNameExists("SmallMargin", 1, 0, true, null, 2);
                testData = "SmallMargin";
                break;
            case ITest.MEDIUM_MARGIN:
                element = iosTestHelper.waitForElementByNameExists("MediumMargin", 1, 0, true, null, 2);
                testData = "MediumMargin";
                break;
            case ITest.LARGE_MARGIN:
                element = iosTestHelper.waitForElementByNameExists("LargeMargin", 1, 0, true, null, 2);
                testData = "LargeMargin";
                break;
        }

        iosTestHelper.saveClickOnElement(element);

        if(iosTestHelper.waitForElementByNameExists("Applying settings...", 10000, 0, true, null, 3) != null) {
            if(!iosTestHelper.waitForElementByNameGone("Applying settings...", 60000, 0, true, null, 3)) {
                report("ChangeTextMargin", (bookName == null ? "" : bookName) + "&" + testData, false);
                return false;
            } else {
                report("ChangeTextMargin", (bookName == null ? "" : bookName) + "&" + testData, true);
            }
        }else {
            Element closeElement = iosTestHelper.waitForElementByNameExists("Close", 1, 0, true, null, 2);
            iosTestHelper.saveClickOnElement(closeElement);
            return false;
        }

        return false;
    }

    protected boolean addBookmark() {
        Element uiaToolBar = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIAToolBar, 1, 0, null, 2);
        if(uiaToolBar != null) {
            iosTestHelper.clickOnScreenCenter(0);
        }

        Element btnAddBookmark = iosTestHelper.waitForElementByNameExists("Add bookmark", 1, 0, true, null, 4);
        if(btnAddBookmark == null) {
            iosTestHelper.clickOnScreenCenter(0);
            btnAddBookmark = iosTestHelper.waitForElementByNameExists("Add bookmark", 1, 0, true, null, 4);
        }

        if(btnAddBookmark == null) {
            report("AddBookmark", (bookName == null ? "" : bookName), false);
            return false;
        }

        iosTestHelper.saveClickOnElement(btnAddBookmark);
        report("AddBookmark", (bookName == null ? "" : bookName), true);

        return true;
    }

    public boolean changeSize(int index, boolean isRandom) {
        if(isRandom) {
            index = iosTestHelper.generateRandom(0, 5);
        }

        Element element = null;
        String testData = "";

        switch (index) {
            case ITest.EXTRA_SMALL_FONT:
                element = iosTestHelper.waitForElementByNameExists("extraSmallFont", 1, 0, true, null, 2);
                testData = "extraSmallFont";
                break;
            case ITest.SMALL_FONT:
                element = iosTestHelper.waitForElementByNameExists("smallFont", 1, 0, true, null, 2);
                testData = "smallFont";
                break;
            case ITest.MEDIUM_SMALL_FONT:
                element = iosTestHelper.waitForElementByNameExists("mediumSmallFont", 1, 0, true, null, 2);
                testData = "mediumSmallFont";
                break;
            case ITest.MEDIUM_LARGE_FONT:
                element = iosTestHelper.waitForElementByNameExists("mediumLargeFont", 1, 0, true, null, 2);
                testData = "mediumLargeFont";
                break;
            case ITest.LARGE_FONT:
                element = iosTestHelper.waitForElementByNameExists("largeFont", 1, 0, true, null, 2);
                testData = "largeFont";
                break;
            case ITest.EXTRA_LARGE_FONT:
                element = iosTestHelper.waitForElementByNameExists("extraLargeFont", 1, 0, true, null, 2);
                testData = "extraLargeFont";
                break;
        }

        if(element == null) {
            report("ChangeTextSize", (bookName == null ? "" : bookName)+ "&" + testData, false);
            return false;
        }

        iosTestHelper.saveClickOnElement(element);

        if(iosTestHelper.waitForElementByNameExists("Applying settings...", 10000, 0, true, null, 3) != null) {
            if(!iosTestHelper.waitForElementByNameGone("Applying settings...", 60000, 0, true, null, 3)){
                report("ChangeTextSize", (bookName == null ? "" : bookName)+ "&" + testData, false);
                return false;
            } else {
                report("ChangeTextSize", (bookName == null ? "" : bookName)+ "&" + testData, true);
            }
        }else {
            Element closeElement = iosTestHelper.waitForElementByNameExists("Close", 1, 0, true, null, 2);
            iosTestHelper.saveClickOnElement(closeElement);
            return false;
        }

        return true;
    }

    @Override 
    public boolean execute(ProductTypeEnum productTypeEnum) {
        testName = "OpenRandomContent";

        for(int indexCycle = 0; indexCycle < 100; indexCycle++) {

            testCycle = indexCycle;
            currentState = State.DOWNLOAD_SUCCESS;

            changeOrientation();
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
        iosTestHelper.stopInstruments();
        TestManager.setUpIOsHelper(false);
    }
}
