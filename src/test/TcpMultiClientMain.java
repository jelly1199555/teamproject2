package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpMultiClientMain {
   public TcpMultiClientMain(String ip, Integer portNo, String id) throws UnknownHostException, IOException {
      Socket sk = new Socket(ip, portNo);
      System.out.println("Server에 연결...");

      DataInputStream dis = new DataInputStream(sk.getInputStream());
      DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
      dos.writeUTF(TcpMultiLib.makeLoginPacket(id, id));

      new TcpMultiClientGUI(dis, dos, id) {
         public void closeGUI() throws IOException {
            dis.close();
            dos.close();
            System.exit(0);
         }
      };
   }

   public static void main(String[] args) throws UnknownHostException, IOException {
      String ip = "127.0.0.1";
      int portNo = 19999;
      String id = "yskim";

      if (args.length != 3) {
         System.out.println("Usage: java PackageName.TcpMultiClient Ip_Address PortNo NickName");
      } else {
         ip = args[0];
         portNo = Integer.valueOf(args[1]);
         id = args[2];
      }

      new TcpMultiClientMain(ip, portNo, id);
   }
}
