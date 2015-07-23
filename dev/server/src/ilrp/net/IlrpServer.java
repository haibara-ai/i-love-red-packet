package ilrp.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IlrpServer extends Thread {
	private int port;
	
	public IlrpServer(int port) {
		this.port = port;
	}

	public void run() {
		try {
			work();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void work() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[DAEMON] Starting on port " + port);
        	while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[DAEMON] New connection from: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                
                IlrpSession session = new IlrpSession(clientSocket);
                session.start();
        	}
        } catch (IOException e) {
            System.out.println("[DAEMON] Can not start server on port " + port);
            System.out.println(e.getMessage());
        }
    }
	
	public static void main(String[] args) {
		try {
			int port = Integer.parseInt(args[1]);
			IlrpServer server = new IlrpServer(port);
			server.start();
		} catch (Exception e) {
			System.out.println("Userage: IlrpServer PORT_NUM");
		}
	}
}
