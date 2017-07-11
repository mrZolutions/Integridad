package com.mrzolution.integridad.app.exceptions;

public class BadRequestException extends RuntimeException {
    /**
     * Constructor
     *
     * @param message the message
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message the message
     * @param cause   the cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean getMessageShown() {
        return messageShown;
    }

    public boolean isMessageShown() {
        return messageShown;
    }

    public void setMessageShown(boolean messageShown) {
        this.messageShown = messageShown;
    }

    private int status = 400;
    private boolean messageShown = true;
}
