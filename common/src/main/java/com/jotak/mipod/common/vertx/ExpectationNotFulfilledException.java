package com.jotak.mipod.common.vertx;

/**
 * @author Joel Takvorian <joel.takvorian@qaraywa.net>
 */
public class ExpectationNotFulfilledException extends Exception {
    public ExpectationNotFulfilledException(final String msg) {
        super(msg);
    }
    public ExpectationNotFulfilledException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
