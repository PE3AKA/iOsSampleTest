/**
 Authors:

 Maxim Sushkevich (msushkevich@gmail.com) and Yahor Paulavets (paulavets.pride@gmail.com)

 This file is part of Gobrotium project (https://github.com/a-a-a-CBEI-I-IEE-M9ICO/GoBrotium.git)

 Gobrotium project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Gobrotium is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Gobrotium project.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ios.testhelper.demo;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesManager {
    private static Properties properties;

    public PropertiesManager(){
        init();
    }

    public void init() {
        try {
            loadProperties();
        } catch (IOException e) {
            closeApp("Unable to load configuration file: " + MainConstants.CONFIGURATION_FILE_PATH);
        }

    }

    public int getPropertyTimeout(String propertyKey){
        Enumeration enuKeys = properties.keys();
        int timeout = 0;
        while (enuKeys.hasMoreElements()) {
            String key = (String) enuKeys.nextElement();

            if(!key.equalsIgnoreCase(propertyKey)) continue;

            String value = properties.getProperty(key);
            try{
                timeout = Integer.parseInt(value);
                return timeout;
            }catch (Exception ex){
                return MainConstants.DEFAULT_TIMEOUT;
            }
        }

        return MainConstants.DEFAULT_TIMEOUT;
    }

    public String getProperty(String propertyKey) {
        Enumeration enuKeys = properties.keys();
        while (enuKeys.hasMoreElements()) {
            String key = (String) enuKeys.nextElement();

            if(!key.equalsIgnoreCase(propertyKey)) continue;

            String value = properties.getProperty(key);

            return value;
        }

        return null;
    }

    private void loadProperties() throws IOException {
        File configProperties = new File(MainConstants.CONFIGURATION_FILE_PATH);
        FileInputStream fileInput = new FileInputStream(configProperties);
        Properties properties = new Properties();
        properties.load(fileInput);
        fileInput.close();

        setProperties(properties);
    }

    public void setProperties(Properties properties) {
        PropertiesManager.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void closeApp(String s) {
        System.exit(0);
    }

    public void closeAppOnTrue(boolean condition, String message) {
        if(condition) {
            closeApp(message);
        }
    }
}
