package com.ios.testhelper.kpitests;

import com.ios.testhelper.kpitests.helpers.ITest;
import net.bugs.testhelper.IOSTestHelper;

import java.io.*;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by nikolai on 14.01.14.
 */
public class FileWorker {

    private String fileName;
    private File currentFile;
    private String testName = null;
    private ITest iosTestHelper;

    public FileWorker(String fileName, ITest iosTestHelper) {
        this.fileName = fileName;
        this.iosTestHelper = iosTestHelper;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void writeLog(ItemLog itemLog){
        writeToFile(itemLog.toString());
        String name = itemLog.getDate() + "_" + itemLog.getTime() + "_" + (itemLog.getTestResult() ? "pass" : "fail") + "_" + itemLog.getTestName() + "_" + itemLog.getTestAction();
        iosTestHelper.takeScreenShot(name);
    }

    public void writeToFile(String log) {
        BufferedWriter bw = null;
        currentFile = new File(fileName);
        try {
            if(!currentFile.exists()){
                currentFile.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(currentFile.getAbsoluteFile(), true));
            int number = getLineNumberFile();
            number++;
            i("KPI: " + log);
            bw.write(log);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }

    private int getLineNumberFile() throws IOException {
        LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(fileName)));
        lnr.skip(Long.MAX_VALUE);
        return lnr.getLineNumber();
    }
}
