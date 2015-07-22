package ilrp.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IlrpServer extends Thread {
	private static final int PORT_NUM = 11307;
	
	public void run() {
		try {
			work();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void work() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT_NUM)) {
            System.out.println("[DAEMON] Starting on port " + PORT_NUM);
        	while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[DAEMON] New connection from: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                
                IlrpSession server = new IlrpSession(clientSocket);
                server.start();
        	}
        } catch (IOException e) {
            System.out.println("[DAEMON] Can not start server on port " + PORT_NUM);
            System.out.println(e.getMessage());
        }
    }
}
