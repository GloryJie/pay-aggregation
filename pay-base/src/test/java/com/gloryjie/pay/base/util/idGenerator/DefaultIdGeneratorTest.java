package com.gloryjie.pay.base.util.idGenerator;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Jie
 * @since
 */
public class DefaultIdGeneratorTest {

    @Test
    public void generatorId() {
        IdGenerator generator = new DefaultIdGenerator(0, 0);

        // 一百万是否包含重复
        List<Long> list = new ArrayList<>(1000000);
        for (int i = 0; i < 1000000; i++) {
            list.add(generator.generatorId());
        }
        long result = list.stream().distinct().count();
        Assert.assertEquals(result, 1000000);
    }
}