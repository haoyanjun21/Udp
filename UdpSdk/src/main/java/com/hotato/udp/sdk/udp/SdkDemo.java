package com.hotato.udp.sdk.udp;


import com.hotato.udp.sdk.common.SdkConstants;
import com.hotato.udp.sdk.util.SdkUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class SdkDemo {
    public static void main(String[] args) throws InterruptedException {
        long start1 = System.currentTimeMillis();
        String nodeAddresses = "localhost:8192";
        final int send = 100000;
        final int totalKeys = 100000;
        int threadCount = SdkConstants.availableProcessors;
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        UdpSdk.updateSelector(nodeAddresses);
        Runnable command = () -> {
            try {
                long receive = 0;
                long start = System.currentTimeMillis();
                for (int i = 1; i <= send; i++) {
                    String key = SdkUtil.testKey(ThreadLocalRandom.current().nextInt(totalKeys));
                    String[] results = UdpSdk.getLocalSdk().request(key);
                    if (results != null) {
                        receive++;
                    }
                }
                long cost = System.currentTimeMillis() - start;
                System.out.println("total send " + send + " receive " + receive + " cost " + cost + " mean cost " + (cost * 1.0 / send));
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(command);
        }
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("total " + (threadCount * send) + " cost " + (System.currentTimeMillis() - start1));
    }

}
