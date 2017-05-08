package com.abooc.dmr.dmr;

/**
 * Created by dayu on 2017/5/5.
 */

public class A {
    private static final A ourInstance = new A();

    public static A getInstance() {
        return ourInstance;
    }

    private A() {
    }
}
