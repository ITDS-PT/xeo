/*Enconding=UTF-8*/
package netgest.bo.presentation.render;

import java.util.ArrayList;

import netgest.bo.boConfig;
import netgest.bo.runtime.boRuntimeException;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class PageController {
    private static final String FUNCTION = "f";
    private static final String IMPORT = "i";
    private static final String SCRIPT = "s";
    private ArrayList controller;
    private int producingForBrowser = -1;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public PageController() {
        controller = new ArrayList();
        producingForBrowser = boConfig.getBrowserCode();
    }

    public PageController(int browser) {
        controller = new ArrayList();
        producingForBrowser = browser;
    }

    public int getBrowserCode() {
        return producingForBrowser;
    }

    public String getBrowserName() throws boRuntimeException {
        return Browser.getBrowserName(producingForBrowser);
    }

    private boolean canWrite(String name) {
        if (controller.contains(name)) {
            return false;
        }

        return true;
    }

    public boolean canWriteFunction(int code) {
        return canWrite(FUNCTION + code);
    }

    public boolean canWriteImport(int code) {
        return canWrite(IMPORT + code);
    }

    public boolean canWriteScript(int code) {
        return canWrite(SCRIPT + code);
    }

    public void markWriteImport(int code) {
        controller.add(IMPORT + code);
    }

    public void markWriteScript(int code) {
        controller.add(SCRIPT + code);
    }

    public void markWriteFunction(int code) {
        controller.add(FUNCTION + code);
    }
}
