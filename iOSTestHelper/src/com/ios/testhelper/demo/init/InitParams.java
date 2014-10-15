package com.ios.testhelper.demo.init;

import com.ios.testhelper.demo.PropertiesManager;
import com.ios.testhelper.demo.enums.ConfigurationParametersEnum;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by avsupport on 10/9/14.
 */
public class InitParams {
    private String buildPath = null;
    private String resultPath = null;
    private String device = null;
    private String testName = null;
    private PropertiesManager propertiesManager = null;

    public InitParams(String[] args, PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
        if(args.length%2 != 0) {
            i("incorrect params.\n\n" +
                    "usage:\n" +
                    "java -jar test.jar" + Params.BUILD + " app_bungle_or_path_to_build_for_simulator " +
                    Params.RESULT + " RESULT "+
                    Params.DEVICE + " \"iPad Retina - Simulator - iOS 7.1\" " +
                    Params.TEST + " signIn");
            System.exit(0);
        }

        String param = "";
        for(int currentParamIndex = 0; currentParamIndex < args.length; currentParamIndex ++){
            param = args[currentParamIndex];
            currentParamIndex ++;
            if(param.equals(Params.BUILD)) {
                buildPath = args[currentParamIndex];
            } else if(param.equals(Params.DEVICE)) {
                device = args[currentParamIndex];
            } else if(param.equals(Params.RESULT)) {
                resultPath = args[currentParamIndex];
            } else if(param.equals(Params.TEST)) {
                testName = args[currentParamIndex];
            }
        }
    }

    public String getBuildPath() {
        if (buildPath == null)
            buildPath = propertiesManager.getProperty(ConfigurationParametersEnum.BUILD_PATH.name());
        return buildPath;
    }

    public String getResultPath() {
        if (resultPath == null)
            resultPath = propertiesManager.getProperty(ConfigurationParametersEnum.PATH_RESULT_FOLDER.name());
        return resultPath;
    }

    public String getDevice() {
        if (device == null)
            device = propertiesManager.getProperty(ConfigurationParametersEnum.DEVICE_ID.name());
        return device;
    }

    public String getTestName() {
        return testName;
    }
}
