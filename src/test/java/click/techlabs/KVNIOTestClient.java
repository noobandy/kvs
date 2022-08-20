package click.techlabs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class KVNIOTestClient {

    private static void printUsageAndExit() {
        System.out.println("Usage: GET <Key>");
        System.out.println("Usage: SET <Key> <Value>");
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            printUsageAndExit();
        }

        String cmd = args[0];
        String key = args[1];
        ByteBuffer data = null;
        switch (cmd) {
            case "GET":
                data = prepareGetCommandData(key);
                break;
            case "SET":
                if (args.length < 3) {
                    printUsageAndExit();
                }
                data = prepareSetCommandData(key, args[2]);
                break;
            default:
                printUsageAndExit();

        }

        send("localhost", 3000, data);
    }

    private static ByteBuffer prepareSetCommandData(String key, String value) {
        ByteBuffer cmdBuffer = ByteBuffer.allocate(1 + 4 + key.length() + 4 + value.length());
        cmdBuffer.put((byte) 2);
        cmdBuffer.putInt(key.length());
        cmdBuffer.put(key.getBytes());
        cmdBuffer.putInt(value.length());
        cmdBuffer.put(value.getBytes());
        return cmdBuffer;
    }

    private static ByteBuffer prepareGetCommandData(String key) {
        ByteBuffer cmdBuffer = ByteBuffer.allocate(1 + 4 + key.length());
        cmdBuffer.put((byte) 1);
        cmdBuffer.putInt(key.length());
        cmdBuffer.put(key.getBytes());
        return cmdBuffer;
    }

    private static void send(String host, int port, ByteBuffer data) throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress(host, port);
        try (SocketChannel sc = SocketChannel.open(socketAddress)) {
            data.flip();
            while (data.hasRemaining()) {
                sc.write(data);
            }
            int responseSize = readInt(sc);
            String response = readString(sc, responseSize);
            System.out.println(response);
        }

    }

    private static int readInt(SocketChannel sc) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        read(sc, buffer);
        buffer.flip();
        return buffer.getInt();
    }


    private static void read(SocketChannel sc, ByteBuffer buffer) throws IOException {
        buffer.clear();
        int capacity = buffer.capacity();
        int read = 0;
        while (read < capacity) {
            int part = sc.read(buffer);
            if (part == -1) {
                throw new IOException("Invalid data stream");
            }
            read = read + part;
        }
    }

    private static String readString(SocketChannel sc, int sizeInBytes) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(sizeInBytes);
        read(sc, buffer);
        buffer.flip();
        byte[] bytes = new byte[sizeInBytes];
        buffer.get(bytes);
        return new String(bytes);

    }

}
