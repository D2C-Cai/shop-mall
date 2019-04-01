package com.d2c.shop.service.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Cai
 */
public class ExecutorUtil {

    //
    public static ExecutorService cachedPool = Executors.newCachedThreadPool();
    //
    public static ExecutorService fixedPool = Executors.newFixedThreadPool(50);

}
