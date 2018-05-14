package BaiTapServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMayChu {
	 

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*try {
			// tạo cổng port phía server
			ServerSocket server  = new ServerSocket(8001); 
			System.out.println("Server start");
			//tạo Socket
			Socket clienSocket = server.accept();
			System.out.println("Waiting");
			//tạo DataInputStream để nhận dữ liệu từ Client
			DataInputStream inputServer = new DataInputStream(ServerSocket.GetInputStream());
			String so1 = inputServer.readLine();
			String so2 = inputServer.readLine();
			int a = Integer.parseInt(so1);
			int b = Integer.parseInt(so2);
			int tong = a+b;
			
			//tạo Data ontput Stream để gửi tổng đi
			DataOutputStream outputStream = new DataOutputStream(clienSocket);
			outputServer.w
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		*/
		try {
			//tao cong port phia server
			ServerSocket server = new ServerSocket(8001);
			System.out.println("srever start");
			//Tao socket
			Socket serversocket = server.accept();    
			System.out.println("Wailting");
			//tao data inputstream de nhan du lieu tu client
			DataInputStream inputServer = new DataInputStream(serversocket.getInputStream());
			//(...)toi lay du lieu gan cho inputserver
			String so1=inputServer.readLine();
			String so2=inputServer.readLine();
			int a=Integer.parseInt(so1);
			int b=Integer.parseInt(so2);
			int tong =a+b;
			//tao data outputstream de gui tong
			DataOutputStream outputserver =new DataOutputStream(serversocket.getOutputStream());
			outputserver.writeBytes(String.valueOf(tong));
			inputServer.close();
			outputserver.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}