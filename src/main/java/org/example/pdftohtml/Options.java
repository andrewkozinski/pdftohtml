package org.example.pdftohtml;

import java.io.File;

public class Options {

    private File outputFolder;

    private String hFontSize;

    private String hFontFamily;

    private String hPosition;

    private String pFontSize;

    private String pFontFamily;

    private String pPosition;

    public Options() {
        outputFolder = null;
        hFontSize = "Null";
        hFontFamily = "Null";
        hPosition = "left";
        pFontSize = "Null";
        pFontFamily = "Null";
        pPosition = "left";
    }

    public Options(File outputFolder, String hFontSize, String hFontFamily, String hPosition, String pFontSize, String pFontFamily, String pPosition) {
        this.outputFolder = outputFolder;
        this.hFontSize = hFontSize;
        this.hFontFamily = hFontFamily;
        this.hPosition = hPosition;
        this.pFontSize = pFontSize;
        this.pFontFamily = pFontFamily;
        this.pPosition = pPosition;
    }

    public String gethPosition() {
        return hPosition;
    }

    public void sethPosition(String hPosition) {
        this.hPosition = hPosition;
    }

    public String getpFontSize() {
        return pFontSize;
    }

    public void setpFontSize(String pFontSize) {
        this.pFontSize = pFontSize;
    }

    public String getpFontFamily() {
        return pFontFamily;
    }

    public void setpFontFamily(String pFontFamily) {
        this.pFontFamily = pFontFamily;
    }

    public String getpPosition() {
        return pPosition;
    }

    public void setpPosition(String pPosition) {
        this.pPosition = pPosition;
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String gethFontSize() {
        return hFontSize;
    }

    public void sethFontSize(String hFontSize) {
        this.hFontSize = hFontSize;
    }

    public String gethFontFamily() {
        return hFontFamily;
    }

    public void sethFontFamily(String hFontFamily) {
        this.hFontFamily = hFontFamily;
    }
}
