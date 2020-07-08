package com.hife.util;

import cn.izern.sequence.Sequence;

public class GuidUtil {

    private GuidUtil() {
    }

    private static Sequence sequence = new Sequence();

    public static String generateGuid() {

        return String.valueOf(sequence.nextId());
    }

    public static long generateLongGuid() {

        return sequence.nextId();
    }

}
