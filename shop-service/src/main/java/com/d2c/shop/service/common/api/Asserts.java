package com.d2c.shop.service.common.api;

import com.baomidou.mybatisplus.extension.api.Assert;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;

import java.math.BigDecimal;

/**
 * @author Cai
 */
public class Asserts extends Assert {

    public static void isNull(String msg, Object... conditions) {
        for (Object obj : conditions) {
            if (obj != null) {
                throw new ApiException(msg);
            }
        }
    }

    public static void notNull(String msg, Object... conditions) {
        for (Object obj : conditions) {
            if (obj == null) {
                throw new ApiException(msg);
            }
        }
    }

    public static void eq(Object obj1, Object obj2, String msg) {
        if (obj1 == null || !obj1.equals(obj2)) {
            throw new ApiException(msg);
        }
    }

    public static void gt(Integer num1, Integer num2, String msg) {
        if (num1 == null || num2 == null || num1 <= num2) {
            throw new ApiException(msg);
        }
    }

    public static void ge(Integer num1, Integer num2, String msg) {
        if (num1 == null || num2 == null || num1 < num2) {
            throw new ApiException(msg);
        }
    }

    public static void gt(BigDecimal num1, BigDecimal num2, String msg) {
        if (num1 == null || num2 == null || num1.compareTo(num2) <= 0) {
            throw new ApiException(msg);
        }
    }

    public static void ge(BigDecimal num1, BigDecimal num2, String msg) {
        if (num1 == null || num2 == null || num1.compareTo(num2) < 0) {
            throw new ApiException(msg);
        }
    }

}
