package com.ios.testhelper.kpitests.init;

/**
 * Created by avsupport on 10/9/14.
 */
public class Params {
    public static final String BUILD = "-b";
    public static final String RESULT = "-r";
    public static final String DEVICE = "-d";
    public static final String TEST = "-t";
    public static final String INSTALL = "-i";

    public static final String TEST_SIGN_IN = "signIn";
    public static final String TEST_SIGN_IN_JS = "signInJs";
    public static final String TEST_DEFERREF_SIGN_IN = "deferredSignIn";
    public static final String TEST_SIGN_OUT = "signOut";
    public static final String TEST_SIGN_OUT_JS = "signOutJs";
    public static final String TEST_OPEN_BOOK = "openBook";
    public static final String TEST_OPEN_BOOK_JS = "openBookJs";
    public static final String TEST_OPEN_MAGAZINES = "openMagazine";
    public static final String TEST_OPEN_MAGAZINES_JS = "openMagazineJs";
    public static final String TEST_OPEN_PDF = "openPdf";
    public static final String TEST_OPEN_PDF_JS = "openPdfJs";
    public static final String TEST_OPEN_COMICS = "openComics";
    public static final String TEST_OPEN_COMICS_JS = "openComicsJs";
    public static final String TEST_OPEN_NEWSPAPER = "openNewspaper";
    public static final String TEST_OPEN_NEWSPAPER_JS = "openNewspaperJs";
    public static final String TEST_ALL_KPI = "allKpiTests";
    public static final String TEST_ALL_KPI_JS = "allKpiTestsJs";

    public static final String[] ALL_KPI_TESTS = new String[]{
            Params.TEST_SIGN_IN,
            Params.TEST_OPEN_BOOK,
            Params.TEST_OPEN_MAGAZINES,
            Params.TEST_OPEN_PDF,
            Params.TEST_OPEN_COMICS,
            Params.TEST_SIGN_OUT
    };

    public static final String TEST_OPEN_RANDOM_CONTENT = "openRandomContent";

    public static String getTestList(){
        return TEST_SIGN_IN + "\n" +
                TEST_SIGN_OUT + "\n" +
                TEST_OPEN_BOOK + "\n" +
                TEST_OPEN_MAGAZINES + "\n" +
                TEST_OPEN_PDF + "\n" +
                TEST_OPEN_COMICS + "\n" +
                TEST_OPEN_NEWSPAPER + "\n" +
                TEST_ALL_KPI + "\n" +
                TEST_DEFERREF_SIGN_IN + "\n" +
                TEST_OPEN_RANDOM_CONTENT + "\n" +
                TEST_SIGN_IN_JS + "\n" +
                TEST_SIGN_OUT_JS + "\n" +
                TEST_OPEN_BOOK + "\n" +
                TEST_OPEN_MAGAZINES_JS + "\n" +
                TEST_OPEN_PDF_JS + "\n" +
                TEST_OPEN_COMICS_JS + "\n" +
                TEST_OPEN_NEWSPAPER_JS + "\n" +
                TEST_ALL_KPI_JS;
    }
}
