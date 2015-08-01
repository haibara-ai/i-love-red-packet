package ilrp.net;

import ilrp.protocol.common.Callback;
import ilrp.protocol.common.Request;
import ilrp.protocol.common.Response;
import ilrp.protocol.packet.AuthResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class IlrpClient {
	private static class Pair<F, S> {
		public Pair(F f, S s) {
			this.first = f;
			this.second = s;
		}
		
		public F first;
		public S second;
	}
	
	private Socket socket;
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private ClientStatus status;
	private Queue<Pair<Request, Callback>> pending = new LinkedList<>();
	
	public static enum ClientStatus {
		DISCONNECTED,	// not connected
		UNAUTHORIZED, 	// connected but not authorized
		AUTHORIZED,		// authorized
		CLOSED,			// closed
	}

	private static class ResponseWaiter extends Thread {
		public IlrpClient client;
		
		public ResponseWaiter(IlrpClient client) {
			this.client = client;
		}
		
		public void run() {
			while (client.status != ClientStatus.CLOSED) {
				Pair<Request, Callback> pair = null;
				synchronized (client.pending) {
					if (client.pending.size() > 0)
						pair = client.pending.poll();
				}
				if (pair != null) {
					try {
						Request req = pair.first;
						Response res = client.sendMessage(req);
						pair.second.invoke(Callback.SUCCESS, req, res);
					} catch (ClassNotFoundException | IOException e) {
						pair.second.invoke(Callback.ERROR, pair.first, null);
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public IlrpClient() {
		status = ClientStatus.DISCONNECTED;
	}
	
	public void openConnection(String host, int port) throws UnknownHostException, IOException {
	    socket = new Socket(host, port);
	    socket.setSoTimeout(20000);	// wait 20s for response
	    status = ClientStatus.UNAUTHORIZED;
	    os = new ObjectOutputStream(socket.getOutputStream());
	    is = new ObjectInputStream(socket.getInputStream());
	    // start the daemon
	    new ResponseWaiter(this).start();
	}
	
	public void closeConnection() throws IOException {
		// close directly
		status = ClientStatus.CLOSED;
		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	    synchronized (pending) {
			pending.clear();
		}
	}

	/**
	 * send authorization request
	 * @param request
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Response authorize(Request request) throws ClassNotFoundException, IOException {
		if (status == ClientStatus.UNAUTHORIZED) {
			Response res = sendMessage(request);
			if (res instanceof AuthResponse) {
				AuthResponse ar = (AuthResponse) res;
				if (ar.status == AuthResponse.LICENSE_OK) {
					status = ClientStatus.AUTHORIZED;
				}
			}
			return res;
		} else {
			throw new IllegalStateException("can not authorize in " + status + " status.");
		}
	}

	/**
	 * Send a message and wait for response
	 * @param msg
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private Response sendMessage(Request request) throws IOException, ClassNotFoundException {
		os.writeObject(request);
		os.flush();
		return (Response) is.readObject();
	}
	
	/**
	 * Send a message and return
	 * Once there is a response, the callback will be called
	 * @param msg
	 * @param callback
	 */
	public void sendMessage(Request request, Callback callback) {
		if (status == ClientStatus.AUTHORIZED) {
			synchronized (pending) {
				pending.add(new Pair<Request, Callback>(request, callback));
			}
		} else {
			throw new IllegalStateException("can not send request in " + status + " status.");
		}
	}
}
