package com.ios.testhelper.demo.kpi;

import com.ios.testhelper.demo.PropertiesManager;
import com.ios.testhelper.demo.TestManager;
import com.ios.testhelper.demo.enums.ProductTypeEnum;
import com.ios.testhelper.demo.helpers.ITest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by avsupport on 10/9/14.
 */
public abstract class KpiTest {

    public abstract boolean execute(ProductTypeEnum productTypeEnum);

    protected ITest iosTestHelper = null;
    protected PropertiesManager propertiesManager = null;
    protected TestManager testManager = null;

    public KpiTest(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
        this.iosTestHelper = iosTestHelper;
        this.propertiesManager = propertiesManager;
        this.testManager = testManager;
    }

    protected void takeScreenShot(String testName, String testAction, boolean isSuccess) {
        String fileName = getDate() + "_" + getTime() + "_" + (isSuccess ? "pass" : "fail") + "_" + testName + "_" + testAction;
        iosTestHelper.takeScreenShot(fileName);
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(new Date());
    }

    public String getTime() {
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH-mm-ss");
        return simpleTimeFormat.format(new Date());
    }
}
