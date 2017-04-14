package com.terry.interview.emc;

import java.util.Random;

/**
 * Created by Xianghe on 2017/4/14.
 */
public class Common {

    /**
     * Sleep a period of time
     * @param v Milli-seconds need to sleep
     */
    public static void sleepInMilliseconds(long v) {
        try {
            Thread.sleep(v);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
