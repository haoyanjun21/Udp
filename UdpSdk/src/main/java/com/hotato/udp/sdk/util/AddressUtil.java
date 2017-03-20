package com.hotato.udp.sdk.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public final class AddressUtil {

    public static List<InetSocketAddress> getAddresses(String s) {
        ArrayList<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();
        if (SdkUtil.isEmpty(s)) {
            return addrs;
        }
        for (String hostStuff : s.split("(\\s|,|;)+")) {
            if (hostStuff.equals("")) {
                continue;
            }

            int finalColon = hostStuff.lastIndexOf(':');
            if (finalColon < 1) {
                throw new IllegalArgumentException("Invalid server ``" + hostStuff + "'' in list:  " + s);
            }
            String hostPart = hostStuff.substring(0, finalColon);
            String portNum = hostStuff.substring(finalColon + 1);

            addrs.add(new InetSocketAddress(hostPart, Integer.parseInt(portNum)));
        }
        return addrs;
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        List<InetSocketAddress> addresses = AddressUtil.getAddresses(
                "localhost:1234,localhost:1243");
        for (InetSocketAddress address : addresses) {
            System.err.println(address.getHostString() + ":" + address.getPort());
        }
        System.out.println("cost:" + (System.currentTimeMillis() - start));
    }

}
