package com.ios.testhelper.demo.kpi;

import com.ios.testhelper.demo.PropertiesManager;
import com.ios.testhelper.demo.TestManager;
import com.ios.testhelper.demo.enums.ProductTypeEnum;
import com.ios.testhelper.demo.helpers.ITest;

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
}
