package com.ios.testhelper.demo;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nikolai on 12.02.14.
 */
public class ItemLog {
    private String date="";
    private String time="";
    private String build="";
    private String hw="";
    private String os="";
    private String net="";
    private String deviceId="";
    private String slaveId="";
    private String testType="";
    private String testId="";
    private String testName="";
    private String testAction="";
    private String testData="";
    private String usedMemory="";
    private long startTime=0;
    private long endTime=0;
    private long totalTime=0;
    private boolean testResult=false;
    private String testCycle="";

    String dateFieldName = null;
    String timeFieldName = null;
    String buildFieldName = null;
    String hwFieldName = null;
    String osFieldName = null;
    String netFieldName = null;
    String deviceIdFieldName = null;
    String slaveIdFieldName = null;
    String testTypeFieldName = null;
    String testIdFieldName = null;
    String testNameFieldName = null;
    String testActionFieldName = null;
    String testDataFieldName = null;
    String usedMemoryFieldName = null;
    String startTimeFieldName = null;
    String endTimeFieldName = null;
    String totalTimeFieldName = null;
    String testResultFieldName = null;
    String testCycleFieldName = null;

    private PropertiesManager propertiesManager;

    public ItemLog(PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
        dateFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.DATE_FIELD_NAME.name());
        dateFieldName = dateFieldName == null ? "Date=" : dateFieldName.trim();

        timeFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.TIME_FIELD_NAME.name());
        timeFieldName = timeFieldName == null ? "Time=" : timeFieldName.trim();

        buildFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.BUILD_FIELD_NAME.name());
        buildFieldName = buildFieldName == null ? "Build=" : buildFieldName.trim();

        hwFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.HW_FIELD_NAME.name());
        hwFieldName = hwFieldName == null ? "HW=" : hwFieldName.trim();

        osFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.OS_FIELD_NAME.name());
        osFieldName = osFieldName == null ? "OS=" : osFieldName.trim();

        netFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.NET_FIELD_NAME.name());
        netFieldName = netFieldName == null ? "Net=" : netFieldName.trim();

        deviceIdFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.DEVICE_ID_FIELD_NAME.name());
        deviceIdFieldName = deviceIdFieldName == null ? "DeviceID=" : deviceIdFieldName.trim();

        slaveIdFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.SLAVE_ID_FIELD_NAME.name());
        slaveIdFieldName = slaveIdFieldName == null ? "SlaveID=" : slaveIdFieldName.trim();

        testTypeFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.TEST_TYPE_FIELD_NAME.name());
        testTypeFieldName = testTypeFieldName == null ? "TestType=" : testTypeFieldName.trim();

        testIdFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.TEST_ID_FIELD_NAME.name());
        testIdFieldName = testIdFieldName == null ? "TestID=" : testIdFieldName.trim();

        testNameFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.TEST_NAME_FIELD_NAME.name());
        testNameFieldName = testNameFieldName == null ? "TestName=" : testNameFieldName.trim();

        testActionFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.TEST_ACTION_FIELD_NAME.name());
        testActionFieldName = testActionFieldName == null ? "TestAction=" : testActionFieldName.trim();

        testDataFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.TEST_DATA_FIELD_NAME.name());
        testDataFieldName = testDataFieldName == null ? "TestData=" : testDataFieldName.trim();

        usedMemoryFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.USED_MEMORY_FIELD_NAME.name());
        usedMemoryFieldName = usedMemoryFieldName == null ? "UsedMemory=" : usedMemoryFieldName.trim();

        startTimeFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.START_TIME_FIELD_NAME.name());
        startTimeFieldName = startTimeFieldName == null ? "StartTime=" : startTimeFieldName.trim();

        endTimeFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.END_TIME_FIELD_NAME.name());
        endTimeFieldName = endTimeFieldName == null ? "EndTime=" : endTimeFieldName.trim();

        totalTimeFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.TOTAL_TIME_FIELD_NAME.name());
        totalTimeFieldName = totalTimeFieldName == null ? "TotalTime=" : totalTimeFieldName.trim();

        testResultFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.TEST_RESULT_FIELD_NAME.name());
        testResultFieldName = testResultFieldName == null ? "TestResult=" : testResultFieldName.trim();

        testCycleFieldName = propertiesManager.getProperty(ConfigurationParametersEnum.TEST_CYCLE_FIELD_NAME.name());
        testCycleFieldName = testCycleFieldName == null ? "TestCycle=" : testCycleFieldName.trim();


    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getBuild() {
        return build;
    }

    public String getHw() {
        return hw;
    }

    public String getOs() {
        return os;
    }

    public String getNet() {
        return net;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getSlaveId() {
        return slaveId;
    }

    public String getTestType() {
        if(testType == null || testType.isEmpty())
            return MainConstants.TEST_TYPE;
        return testType;
    }

    public String getTestId() {
        return testId;
    }

    public String getTestName() {
        if(testName == null || testName.isEmpty())
            return MainConstants.TEST_NAME;
        return testName;
    }

    public String getTestAction() {
        return testAction;
    }

    public String getTestData() {
        return testData;
    }

    public String getUsedMemory() {
        return usedMemory;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public boolean getTestResult() {
        return testResult;
    }

    public String getTestCycle() {
        return testCycle;
    }

    public void setDate(Date d, String format) {
        SimpleDateFormat simpleDateFormat;
        if(format != null && !format.isEmpty()){
            simpleDateFormat = new SimpleDateFormat(format);
            this.date = simpleDateFormat.format(d);
        }else{
            simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            this.date = simpleDateFormat.format(d);
        }
    }

    public void setTime(Date t, String format) {
        SimpleDateFormat simpleTimeFormat;
        if(format != null && !format.isEmpty()){
            simpleTimeFormat = new SimpleDateFormat(format);
            this.time = simpleTimeFormat.format(t);
        }else{
            simpleTimeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
            this.time = simpleTimeFormat.format(t);
        }
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public void setHw(String hw) {
        this.hw = hw;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setSlaveId(String slaveId) {
        this.slaveId = slaveId;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setTestAction(String testAction) {
        this.testAction = testAction;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public void setUsedMemory(String usedMemory) {
        this.usedMemory = usedMemory;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime, long accuracy) {
        this.endTime = endTime;
        long time = this.endTime - this.startTime - accuracy/2;
        if(time > 0)
            this.totalTime = time;
        else
            this.totalTime = this.endTime - this.startTime;
    }

    public void setTestResult(boolean testResult) {
        this.testResult = testResult;
    }

    public void setTestCycle(String testCycle) {
        this.testCycle = testCycle;
    }

    @Override
    public String toString() {
        return  dateFieldName + getDate() +
                ", " + timeFieldName + getTime() +
                ", " + buildFieldName + getBuild() +
                ", " + hwFieldName + getHw() +
                ", " + osFieldName + getOs() +
                ", " + netFieldName + getNet() +
                ", " + deviceIdFieldName + getDeviceId() +
                ", " + slaveIdFieldName + getSlaveId() +
                ", " + testTypeFieldName + getTestType() +
                ", " + testIdFieldName + getTestId() +
                ", " + testNameFieldName + getTestName() +
                ", " + testActionFieldName + getTestAction() +
                ", " + testDataFieldName + getTestData() +
                ", " + usedMemoryFieldName + getUsedMemory() +
                ", " + startTimeFieldName + getStartTime() +
                ", " + endTimeFieldName + getEndTime() +
                ", " + totalTimeFieldName + getTotalTime() +
                ", " + testResultFieldName + (getTestResult() ? "pass" : "fail") +
                ", " + testCycleFieldName + getTestCycle();
    }
}
