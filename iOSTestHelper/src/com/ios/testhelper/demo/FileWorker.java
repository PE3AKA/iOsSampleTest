package com.ios.testhelper.demo;

import net.bugs.testhelper.IOSTestHelper;

import java.io.*;

/**
 * Created by nikolai on 14.01.14.
 */
public class FileWorker {

    private String fileName;
    private File currentFile;
    private String testName = null;
    private IOSTestHelper iosTestHelper;

    public FileWorker(String fileName, IOSTestHelper iosTestHelper) {
        this.fileName = fileName;
        this.iosTestHelper = iosTestHelper;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void writeLog(ItemLog itemLog){
        BufferedWriter bw = null;
        currentFile = new File(fileName);
        try {
            if(!currentFile.exists()){
                currentFile.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(currentFile.getAbsoluteFile(), true));
            int number = getLineNumberFile();
            number++;
            String log = itemLog.toString();
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
        String name = itemLog.getDate() + "_" + itemLog.getTime() + "_" + (itemLog.getTestResult() ? "pass" : "fail") + "_" + itemLog.getTestName() + "_" + itemLog.getTestAction();
        iosTestHelper.takeScreenShot();
    }

//    public Date write (String text) {
//        BufferedWriter bw = null;
//        currentFile = new File(fileName);
//        Date date = new Date();
//        try {
//            if(!currentFile.exists()){
//                currentFile.createNewFile();
//            }
//
//            bw = new BufferedWriter(new FileWriter(currentFile.getAbsoluteFile(), true));
//
//            int number = getLineNumberFile();
//            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
//            String log = String.format(MainConstants.RANDOM_TEST_LOG, ++number, simpleTimeFormat.format(date), simpleDateFormat.format(date), (testName==null ? "" : testName), text);
//
//            bw.write(log);
//            bw.newLine();
//            bw.flush();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        } finally {
//            if (bw != null) try {
//                bw.close();
//            } catch (IOException ioe2) {
//                ioe2.printStackTrace();
//            }
//        }
//        return date;
//    }

    private int getLineNumberFile() throws IOException {
        LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(fileName)));
        lnr.skip(Long.MAX_VALUE);
        return lnr.getLineNumber();
    }
}
