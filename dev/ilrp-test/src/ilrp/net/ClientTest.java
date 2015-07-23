package ilrp.net;

import ilrp.protocol.common.Callback;
import ilrp.protocol.common.Request;
import ilrp.protocol.common.Response;
import ilrp.protocol.packet.AuthRequest;
import ilrp.protocol.packet.ExpenseRequest;
import ilrp.protocol.packet.IncomeRequest;
import ilrp.protocol.packet.MissPacketRequest;

import java.io.IOException;
import java.util.Calendar;

public class ClientTest {
	public static class EchoCallback implements Callback {
		@Override
		public void invoke(byte status, Request req, Response res) {
			System.out.println("[Callback] " + req);
			System.out.println("[Callback] " + res);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		IlrpServer server = new IlrpServer();
		server.start();

		EchoCallback echo = new EchoCallback();
		IlrpClient client = new IlrpClient();
		client.openConnection();
		
		AuthRequest r1 = new AuthRequest("000001", Calendar.getInstance().getTime());
		client.authorize(r1);
		
		IncomeRequest r2 = new IncomeRequest(
				IncomeRequest.RECEIVE_PUBLIC_RED_PACKET, 
				10000, 
				"alice", 
				"public-group", 
				Calendar.getInstance().getTime()
				);
		client.sendMessage(r2, echo);

		IncomeRequest r3 = new IncomeRequest(
				IncomeRequest.RECEIVE_PUBLIC_RED_PACKET, 
				12300, 
				"bob", 
				"public-group", 
				Calendar.getInstance().getTime()
				);
		client.sendMessage(r3, echo);
		
		ExpenseRequest r4 = new ExpenseRequest(
				ExpenseRequest.SEND_RED_PACKET_TO_PUBLIC,
				40000, 
				25600, 
				"private-group", 
				"public-group", 
				Calendar.getInstance().getTime()
				);
		client.sendMessage(r4, echo);
		
		MissPacketRequest r5 = new MissPacketRequest(
				Calendar.getInstance().getTime()
				);
		client.sendMessage(r5, echo);
		
	}

}
