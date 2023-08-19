package ChatRoomTCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	private int port;
	public static ArrayList<Socket> listSK;
	public Server(int port) {
		this.port = port;
	}
	
	private void execute() throws IOException{
		try (ServerSocket server = new ServerSocket(port)) {
			WriteServer write = new WriteServer();
			write.start();
			System.out.print("Server is listening ...");
			while (true) {
				Socket socket = server.accept();
				System.out.print("Da ket noi voi: " + socket);
				Server.listSK.add(socket);
				ReadServer read = new ReadServer(socket);
				read.start();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		Server.listSK = new ArrayList<>();
		Server server = new Server(15797);
		server.execute();
	}
}

class ReadServer extends Thread{
	private Socket socket;

	public ReadServer(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		System.out.println("Processing: " + socket);
		DataInputStream dis = null;
        try {
        	dis = new DataInputStream(socket.getInputStream());
            while (true) {
                String sms = dis.readUTF();
                for (Socket item : Server.listSK) {
                	if (item.getPort() != socket.getPort()) {
                		DataOutputStream dos = new DataOutputStream(item.getOutputStream());
    					dos.writeUTF("Da nhan tu A (port "+ item.getPort()+")"+" noi dung sau: "+sms);
					}			
				}
                System.out.print(sms);
            }
            
        }catch (Exception e) {
        	try {
        		dis.close();
        		socket.close();
			} catch (IOException ex) {
				System.err.println("Ngắt kết nối Server");
			}  
        }
        System.out.println("Hoàn thành quá trình xử lý: " + socket);
	}
}

class WriteServer extends Thread{
	
	@Override
	public void run() {
		DataOutputStream dos = null;
		try (Scanner sc = new Scanner(System.in)) {
			while (true) {
				String sms = sc.nextLine();	
				try {
					for (Socket item : Server.listSK) {
						dos = new DataOutputStream(item.getOutputStream());
						dos.writeUTF(sms);
					}
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}