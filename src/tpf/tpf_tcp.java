package tpf;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Logger;

/**
 * Created by kang on 2015-07-04.
 */
public class tpf_tcp {
    private static final int sizeBuf = 50;

    public static void main(String[] args) throws IOException {
        int port = 8888;
        ServerSocket serverSock = new ServerSocket(port);
        Logger logger = Logger.getLogger("tpf_test");

        while (true) {
            Socket clientSock = serverSock.accept();
            SocketAddress clientAddress = clientSock.getRemoteSocketAddress();
            Thread thread = new Thread(new TpfThread(clientSock, clientAddress, logger));
            thread.start();
            if (clientSock.isConnected())
                System.out.println("Connected! Client IP : " + clientAddress);
        }
    }
}
