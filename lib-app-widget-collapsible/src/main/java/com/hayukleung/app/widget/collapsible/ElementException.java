package com.hayukleung.app.widget.collapsible;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * TODO
 * 
 * @author HayukLeung
 * 
 */
public class ElementException extends RuntimeException {

    /**
   * 
   */
    private static final long serialVersionUID = 4255166407571792798L;

    public ElementException() {
    }

    public ElementException(String detailMessage) {
        super(detailMessage);
    }

    public ElementException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ElementException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public Throwable fillInStackTrace() {
        // TODO Auto-generated method stub
        return super.fillInStackTrace();
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        // TODO Auto-generated method stub
        return super.getLocalizedMessage();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        // TODO Auto-generated method stub
        return super.getStackTrace();
    }

    @Override
    public void setStackTrace(StackTraceElement[] trace) {
        // TODO Auto-generated method stub
        super.setStackTrace(trace);
    }

    @Override
    public void printStackTrace() {
        // TODO Auto-generated method stub
        super.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream err) {
        // TODO Auto-generated method stub
        super.printStackTrace(err);
    }

    @Override
    public void printStackTrace(PrintWriter err) {
        // TODO Auto-generated method stub
        super.printStackTrace(err);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

    @Override
    public Throwable initCause(Throwable throwable) {
        // TODO Auto-generated method stub
        return super.initCause(throwable);
    }

    @Override
    public Throwable getCause() {
        // TODO Auto-generated method stub
        return super.getCause();
    }

}
