package BaiTapServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ClientMK {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Socket clientsocket = new Socket("Localhost", 8001);
			//Tao dataoutputstream de gui du lieu tu client den server
			Scanner key = new Scanner(System.in);
			String a,b;
			
			System.out.println("Nhap a");
			a =key.nextLine();
			System.out.println("Nhap b");
			b=key.nextLine();
			DataOutputStream outputClient=new DataOutputStream(clientsocket.getOutputStream());
			outputClient.writeBytes(a+"\n");
			outputClient.writeBytes(b+"\n");
			DataInputStream inputClient =new DataInputStream(clientsocket.getInputStream());
			String tong=inputClient.readLine();
			System.out.println("Tong la:"+tong);
			inputClient.close();
			outputClient.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
