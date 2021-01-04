package DAY12_29test1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpMultiClientMain {
	Socket s1;
	DataInputStream dis;
	DataOutputStream dos;

	public TcpMultiClientMain(String ip, Integer portNo, String nickname) throws UnknownHostException, IOException {
		int a = 0;
		try {
			s1 = new Socket(ip, portNo);
			System.out.println("Server에 연결...");
			dis = new DataInputStream(s1.getInputStream());
			dos = new DataOutputStream(s1.getOutputStream());
			dos.writeUTF(TcpMultiLib.makeLoginPacket(nickname, nickname));
		} catch (IOException e) {
			a++;
		}
		if (a != 0) {
			dis.close();
			dos.close();
			System.exit(0);
		} else {
			new TcpMultiClientGUI(dis, dos, nickname) {
				public void closeGUI() throws IOException {
					dis.close();
					dos.close();
					System.exit(0);
				}
			};
		}

	}

}