package com.hotato.udp.sdk.udp;

import com.hotato.udp.sdk.util.AddressUtil;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeSelector {
    private List<InetSocketAddress> inetSockets;
    private int number = -1;
    private int length;

    public NodeSelector(List<InetSocketAddress> inetSockets) {
        this.inetSockets = inetSockets;
        length = inetSockets.size();
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        final NodeSelector selector = new NodeSelector(AddressUtil.getAddresses("localhost:8192,localhost:8191"));
        final int threadCount = 4;
        ExecutorService service = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        selector.select();
                    }
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        service.shutdown();
        System.out.println("cost:" + (System.currentTimeMillis() - start));
    }

    public InetSocketAddress select() {
        number = (number + 1) % length;
        InetSocketAddress address = inetSockets.get(number);
        return address;
    }

}
