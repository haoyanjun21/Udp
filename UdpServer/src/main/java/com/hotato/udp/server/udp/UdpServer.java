package com.hotato.udp.server.udp;

import com.hotato.udp.sdk.common.SdkConstants;
import com.hotato.udp.server.handler.Executor;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UdpServer {
    public static final int SERVER_RCV_BUFFER_SIZE = 1 * 1024 * 1024 * 1024;
    private final int port;
    private Executor executor;

    public UdpServer(int port) {
        this.port = port;
        executor = new Executor();
    }

    public static void main(String[] args) throws IOException {
        int port = 8192;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        System.out.println("udp server start at " + port + " !");
        new UdpServer(port).handleMessage();
    }

    public void handleMessage() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        DatagramSocket socket = channel.socket();
        socket.setReceiveBufferSize(SERVER_RCV_BUFFER_SIZE);
        socket.bind(new InetSocketAddress(port));
        ByteBuffer buffer = ByteBuffer.allocateDirect(SdkConstants.SDK_MAX_PACKET_SIZE);
        while (true) {
            InetSocketAddress address = (InetSocketAddress) channel.receive(buffer);
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            try {
                executor.execute(bytes, address);
            } catch (Exception e) {
                e.printStackTrace();
            }
            buffer.clear();
        }
    }
}
