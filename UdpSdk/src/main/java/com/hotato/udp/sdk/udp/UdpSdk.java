package com.hotato.udp.sdk.udp;

import com.hotato.udp.sdk.common.SdkConstants;
import com.hotato.udp.sdk.util.AddressUtil;
import com.hotato.udp.sdk.util.SdkUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

public class UdpSdk {
    public static int timeout = 200;
    public static volatile NodeSelector selector;
    private static int retries = 1;
    private static ThreadLocal<UdpSdk> threadLocal = new ThreadLocal();
    private DatagramSocket localSocket;
    private int receiveBufSize = SdkConstants.SDK_MAX_PACKET_SIZE;
    private byte[] receiveBuf = new byte[this.receiveBufSize];
    private String errMsg;

    /**
     * 发送并接收数据，为避免接收到其他线程发送数据的返回结果，需要使用线程内单例
     */
    public static UdpSdk getLocalSdk() {
        if (null == threadLocal.get()) {
            UdpSdk sdk = new UdpSdk();
            sdk.init();
            threadLocal.set(sdk);
        }
        return threadLocal.get();
    }

    public static void updateSelector(String nodeAddresses) {
        if (!SdkUtil.isEmpty(nodeAddresses)) {
            List<InetSocketAddress> inetSockets = AddressUtil.getAddresses(nodeAddresses);
            selector = new NodeSelector(inetSockets);
        }
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void init() {
        checkSocket();
    }

    void checkSocket() {
        try {
            if (null == this.localSocket) {
                this.localSocket = new DatagramSocket();
                if (this.timeout > 0) {
                    this.localSocket.setSoTimeout(this.timeout);
                }
                System.out.println("localSocketPort: " + localSocket.getLocalPort());
            }
        } catch (SocketException e) {
            closeSocket();
            System.out.println(e.getMessage());
        }
    }

    private void closeSocket() {
        if (this.localSocket != null) {
            localSocket.close();
            this.localSocket = null;
        }
    }

    public String[] request(String key) {
        for (int i = 0; i <= retries && i < 3; i++) {
            try {
                checkSocket();
                byte[] msg = SdkUtil.encodeBytes(key);
                DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, selector.select());
                this.localSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(this.receiveBuf, this.receiveBufSize);
                this.localSocket.receive(receivePacket);
                String[] results = SdkUtil.decodeBytes(Arrays.copyOf(this.receiveBuf, receivePacket.getLength()));
                if (results != null) {
                    if (key.equals(results[0])) {
                        return results;
                    } else {
                        closeSocket();
                        this.errMsg = "key != return key";
                    }
                }
            } catch (Exception e) {
                this.errMsg = e.getMessage();
            }
        }
        return null;
    }

    public void send(byte[] bytes, InetSocketAddress address) {
        try {
            checkSocket();
            DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, address);
            this.localSocket.send(sendPacket);
        } catch (Exception e) {
            this.errMsg = e.getMessage();
        }
    }

    public void send(byte[] bytes) {
        send(bytes, selector.select());
    }

}

