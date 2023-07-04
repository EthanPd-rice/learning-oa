package com.imooc.oa.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class Md5UnitsTest {

    @Test
    public void md5Digest() {
        String md5 = Md5Units.md5Digest("123456");
        System.out.println(md5);
    }
}