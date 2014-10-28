package com.ios.testhelper.kpitests.helpers;

import com.ios.testhelper.kpitests.TestManager;
import net.bugs.testhelper.IOSTestHelper;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;
import net.bugs.testhelper.ios.item.ElementForWait;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by avsupport on 10/9/14.
 */
public class ITest extends IOSTestHelper {
    public static final int EXTRA_SMALL_FONT = 0;
    public static final int SMALL_FONT = 1;
    public static final int MEDIUM_SMALL_FONT = 2;
    public static final int MEDIUM_LARGE_FONT = 3;
    public static final int LARGE_FONT = 4;
    public static final int EXTRA_LARGE_FONT = 5;

    public static final int SMALL_MARGIN = 0;
    public static final int MEDIUM_MARGIN = 1;
    public static final int LARGE_MARGIN = 2;

    public static final int SINGLE_LINE_SPACING = 0;
    public static final int ONE_AND_HALF_LINES_SPACING = 1;
    public static final int MULTIPLE_LINE_SPACING = 2;

    public static final int SWIPE_UP = 0;
    public static final int SWIPE_DOWN = 1;

    public ITest(String pathToApplicationOrBundle, String pathToResultsFolder, String deviceUUID) {
        super(pathToApplicationOrBundle, pathToResultsFolder, deviceUUID);
    }

    public int generateRandom(int min, int max){
        Random random = new Random();
        int randomIndex = random.nextInt((max - min)+1) + min;
        return randomIndex;
    }

    public void randomSwipe(Element element, int swipeSide) {
        int randomIndex = generateRandom(1, 5);
        switch (swipeSide){
            case SWIPE_UP:
                while(randomIndex != 0) {
                    super.scrollUpInsideElement(element, element.getHeight(), 1);
                    randomIndex--;
                }
                break;
            case SWIPE_DOWN:
                while(randomIndex != 0) {
                    super.scrollDownInsideElement(element, element.getHeight(), 1);
                    randomIndex--;
                }
                break;
        }
    }

    public boolean saveClickAndWaitElement(Element element, ElementForWait parent, ElementForWait elementForWait){
        if(element == null) {
            i("can not click to element, because element is null");
            return false;
        }

        if(!element.isVisible())
            return super.clickOnElementAndWaitElement(element, parent, elementForWait) != null;
        else
            return super.clickOnElementByXYAndWaitElement(element, 0.5, 0.5, parent, elementForWait) != null;
    }

    public void saveLongClickOnElement(Element element){
        if(element == null) {
            i("can not long click to element, because element is null");
            return;
        }
        if(element.isVisible())
            super.longClickOnElement(element, 4);
        else
            super.longClickOnElementByXY(element, 0.5, 0.5, 4);
    }

    public void saveClickOnElement(Element element){
        if(element == null) {
            i("can not click to element, because element is null");
            return;
        }
        if(element.isVisible())
            super.clickOnElement(element);
        else
            super.clickOnElementByXY(element, 0.5, 0.5);
    }

    public void hideKeyboard() {
        Element keyboard = waitForElementByClassExists(UIAElementType.UIAKeyboard, 1, 0, null, 2);

        Element element = waitForElementByNameExists("Hide keyboard", 1000, 0, true, keyboard, 2);
            if(element != null){
                saveClickOnElement(element);
            }
//        if(element != null){
//            int[] screenSize = getScreenSize();
//            clickByXY(screenSize[0] / 3, screenSize[1] / 3);
//        }
    }

    public boolean openMenu() {
        return clickToolbarButtonByIndex(0);
    }

    private boolean clickToolbarButtonByIndex(int index) {
        Element element = waitForElementByClassExists(UIAElementType.UIAToolBar, 10000, 0, null, 2);
        if(element == null) {
            i("Element UIAToolBar was not found!");
            return false;
        }
        ArrayList<Element> buttons = getElementChildrenByType(element, UIAElementType.UIAButton);
        i("Toolbar was found");
        if(buttons.size() < 1) {
            return false;
        }
        Element button = buttons.get(index);
        saveClickOnElement(button);
        return true;
    }

    public void stopInstruments(){
        stopTest();
    }

    public void assertNotNull(Object object) {
        if(object == null) {
            i("ERROR: ELEMENT is null");
            stopInstruments();
            sleep(2000);
            System.exit(0);
        }
    }

    public boolean goToLibrary() {
        if(waitForElementByClassExists(UIAElementType.UIASearchBar, 5000, 0, null, 3) != null)
            return true;
        if(!openMenu())
            return false;
        Element library = waitForElementByNameVisible("Library", 10000, 0, true, null, 3);
        saveClickOnElement(library);
        return waitForElementByClassExists(UIAElementType.UIASearchBar, 10000, 0, null, 3) != null;
    }

    public void setStartTime() {
        TestManager.setStartTime(getResponseItem().getEndTime());
    }

    public void setEndTime() {
        TestManager.setEndTime(getResponseItem().getEndTime());
    }

    public void passKpi(String testName, String kpiName, String testData){
        TestManager.write(TestManager.addLogParams(new Date(), testName, kpiName, testData, true));
    }

    public void failKpi(String testName, String kpiName, String testData) {
        TestManager.write(TestManager.addLogParams(new Date(), testName, kpiName, testData, false));
    }

    public void reportStress(String testName, String testAction, String testData, String testType, String testCycle, boolean testResult) {
        TestManager.writeStress(TestManager.addLogParams(new Date(), testName, testAction, testData, testType, testCycle, testResult));
    }
}
