package ilrp.net;

import ilrp.db.DbAccess;
import ilrp.db.User;
import ilrp.db.UserManage;
import ilrp.protocol.common.Request;
import ilrp.protocol.common.Response;
import ilrp.protocol.packet.AuthRequest;
import ilrp.protocol.packet.AuthResponse;
import ilrp.protocol.packet.ExpenseRequest;
import ilrp.protocol.packet.ExpenseResponse;
import ilrp.protocol.packet.IncomeRequest;
import ilrp.protocol.packet.IncomeResponse;
import ilrp.protocol.packet.MissPacketRequest;
import ilrp.protocol.packet.MissPacketResponse;
import ilrp.util.DateUtil;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class IlrpSession extends Thread {
	private static enum ClientStatus {
		CONNECTED,		// not authorized
		AUTHORIZED,		// authorized
		CLOSED,			// authorization failed or other failures
	}
	
	private Socket socket;
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private ClientStatus status;
	// authorized session always have a valid user code
	private String userCode;
	
	public IlrpSession(Socket client) {
		this.socket = client;
	}
	
	@Override
	public void run() {
		try {
			this.os = new ObjectOutputStream(socket.getOutputStream());
			this.is = new ObjectInputStream(socket.getInputStream());
			this.status = ClientStatus.CONNECTED;
		} catch (IOException e) {
			this.status = ClientStatus.CLOSED;
			System.out.println("[SOCKET] Can not obtain input/output stream from the client socket: " + e.getMessage());
			e.printStackTrace();
		}
		while (status != ClientStatus.CLOSED) {
			try {
				Request req = (Request) is.readObject();
				System.out.println("[SERVER][" + userCode + "] Request: " + req.toString());
				Response res = handle(req);
				System.out.println("[SERVER][" + userCode + "] Response: " + res.toString());
				os.writeObject(res);
				os.flush();
				System.out.println("[SERVER][" + userCode + "] OK.");
			} catch (EOFException e) {
				System.out.println("[SOCKET][" + userCode + "] Closed from the client.");
				status = ClientStatus.CLOSED;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				System.out.println("[SOCKET][" + userCode + "] Error reading request: " + e.getMessage());
				status = ClientStatus.CLOSED;
			} catch (RuntimeException e) {
				System.out.println("[SERVER][" + userCode + "] RuntimeException: " + e.getMessage());
				status = ClientStatus.CLOSED;
			}
		}
		try {
			socket.close();
			System.out.println("[SOCKET][" + userCode + "] Socket closed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理主流程
	 * @param req
	 * @return
	 */
	private Response handle(Request req) {
		if (req instanceof AuthRequest) {
			return handleAuthRequest((AuthRequest) req);
		} else if (req instanceof IncomeRequest) {
			return handleIncomeRequest((IncomeRequest) req);
		} else if (req instanceof ExpenseRequest) {
			return handleExpenseRequest((ExpenseRequest) req);
		} else if (req instanceof MissPacketRequest) {
			return handleMissPacketRequest((MissPacketRequest) req);
		} else {
			throw new RuntimeException("unknown request type: " + req.getClass().getName());
		}
	}

	private Response handleMissPacketRequest(MissPacketRequest req) {
		if (userCode == null) {
			return new MissPacketResponse(MissPacketResponse.ERROR);
		} else {
			String msg = " " + String.format("%4d.%02d", 0, 0) + "\t |" + req.toString();
			DbAccess.appendLog(userCode, msg);
			return new MissPacketResponse(MissPacketResponse.STATUS_OK);
		}
	}

	private Response handleExpenseRequest(ExpenseRequest req) {
		if (userCode == null) {
			return new ExpenseResponse(ExpenseResponse.ERROR);
		} else {
			int delta = req.totalAmount - req.savedAmount;
			String msg = "-" + String.format("%4d.%02d", (delta / 100), (delta % 100)) + "\t |" + req.toString();
			DbAccess.appendLog(userCode, msg);
			return new ExpenseResponse(ExpenseResponse.STATUS_OK);
		}
	}

	private Response handleIncomeRequest(IncomeRequest req) {
		if (userCode == null) {
			return new IncomeResponse(IncomeResponse.ERROR);
		} else {
			int delta = req.amount;
			String msg = "+" + String.format("%4d.%02d", (delta / 100), (delta % 100)) + "\t |" + req.toString();
			DbAccess.appendLog(userCode, msg);
			return new IncomeResponse(IncomeResponse.STATUS_OK);
		}
	}

	private Response handleAuthRequest(AuthRequest req) {
		try {
			UserManage um = DbAccess.loadUsers();
			User user = um.find(req.code);
			if (userCode != null) {
				// duplicated auth in the same session
				status = ClientStatus.CLOSED;
				return new AuthResponse(AuthResponse.DUP_AUTH);
			} else if (user == null) {
				// does not exist
				status = ClientStatus.CLOSED;
				return new AuthResponse(AuthResponse.LICENSE_NOT_EXISTS);
			} else if (!user.enable) {
				status = ClientStatus.CLOSED;
				return new AuthResponse(AuthResponse.LICENSE_DISABLED);
			} else {
				// save user code
				userCode = req.code;
				// check expiration
				Date expiration = DateUtil.calcEndOfDay(user.expiration);
				Date now = DateUtil.getCSTCalendar().getTime();
				if (expiration.before(now)) {
					// expired
					status = ClientStatus.CLOSED;
					return new AuthResponse(AuthResponse.LICENSE_EXPIRED);
				} else {
					// ok
					return new AuthResponse(AuthResponse.LICENSE_OK);
				}
			}
		} catch (RuntimeException e) {
			System.out.println("[SERVER] Authorization exception: " + e.getMessage());
			return new AuthResponse(AuthResponse.SERVER_ERROR);
		}
	}
}
