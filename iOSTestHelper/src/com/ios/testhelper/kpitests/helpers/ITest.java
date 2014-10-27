package com.ios.testhelper.kpitests.helpers;

import com.ios.testhelper.kpitests.TestManager;
import net.bugs.testhelper.IOSTestHelper;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;
import net.bugs.testhelper.ios.item.ElementForWait;

import java.util.ArrayList;
import java.util.Date;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by avsupport on 10/9/14.
 */
public class ITest extends IOSTestHelper {
    public ITest(String pathToApplicationOrBundle, String pathToResultsFolder, String deviceUUID) {
        super(pathToApplicationOrBundle, pathToResultsFolder, deviceUUID);
    }

    public boolean saveClickAndWaitElement(Element element, ElementForWait parent, ElementForWait elementForWait){
        if(element == null) {
            i("can not click to element, because element is null");
            return false;
        }

        if(!element.isVisible())
            return super.clickOnElementAndWaitElement(element, parent, elementForWait);
        else
            return super.clickOnElementByXYAndWaitElement(element, 0.5, 0.5, parent, elementForWait);
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
