package com.incture.bupa.exceptions;

public class FlowAlreadyExistsException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String msg;

    public FlowAlreadyExistsException() {
        super();
    }

    public FlowAlreadyExistsException(String message) {
        this.msg = message;
    }

    @Override
    public String getMessage() {
        return msg;
    }

}
