package com.hotato.udp.server.handler;

import com.hotato.udp.sdk.common.SdkConstants;
import com.hotato.udp.sdk.udp.UdpSdk;
import com.hotato.udp.sdk.util.SdkUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Executor {
    private ExecutorService executorService;
    private UdpSdk sdk;

    public Executor() {
        executorService = Executors.newFixedThreadPool(SdkConstants.availableProcessors);
        sdk = new UdpSdk();
    }

    public void execute(final byte[] bytes, final InetSocketAddress address) {
        final String[] args = SdkUtil.decodeBytes(bytes);
        executorService.execute(() -> sdk.send(SdkUtil.encodeBytes(args[0],
                String.valueOf(ThreadLocalRandom.current().nextInt())), address));

    }
}
