package com.gloryjie.pay.base.util;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * @author Jie
 * @since
 */
public class DateTimeUtilTest {

    @Test
    public void parse() {
        System.out.println(DateTimeUtil.parse(LocalDateTime.now()));
    }

    @Test
    public void now() {
        System.out.println(DateTimeUtil.now());
    }

    @Test
    public void currentTimeMillis() {
        System.out.println(DateTimeUtil.currentTimeMillis());
    }
}