package com.ios.testhelper.demo.helpers;

import com.ios.testhelper.demo.TestManager;
import net.bugs.testhelper.IOSTestHelper;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by avsupport on 10/9/14.
 */
public class ITest extends IOSTestHelper {
    public ITest(String pathToApplicationOrBundle, String pathToResultsFolder, String deviceUUID) {
        super(pathToApplicationOrBundle, pathToResultsFolder, deviceUUID);
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
        Element element = waitForElementByClassExists(UIAElementType.UIAKeyboard, 1, 0, null, 2);
        if(element != null){
            int[] screenSize = getScreenSize();
            clickByXY(screenSize[0] / 3, screenSize[1] / 3);
        }
    }

    public boolean openMenu() {
        return clickToolbarButtonByIndex(0);
    }

    private boolean clickToolbarButtonByIndex(int index) {
        Element element = waitForElementByClassExists(UIAElementType.UIAToolBar, 10000, 0, null, 2);
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
        if(waitForElementByClassExists(UIAElementType.UIASearchBar, 5000, 0, null, 3) != null) return true;
        openMenu();
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

    public void passKpi(String kpiName){
        TestManager.write(TestManager.addLogParams(new Date(), kpiName, "", true));
    }

    public void failKpi(String kpiName){
        TestManager.write(TestManager.addLogParams(new Date(), kpiName, "", false));
    }
}
