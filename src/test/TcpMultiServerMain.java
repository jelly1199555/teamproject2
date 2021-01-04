package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TcpMultiServerMain {
   private ArrayList<ThreadServerClass> lstThread = new ArrayList<>();
   private Socket sk;
   //DataOutputStream dos;
   
   public TcpMultiServerMain(int portNo) throws IOException {
      Socket sk1 = null;
      ServerSocket ss = new ServerSocket(portNo);
      System.out.println("서버 가동.... Port 번호: " + portNo + " 접속 대기중...");
      
      while(true) {
         sk1 = ss.accept();
         System.out.println("접속주소: " + sk1.getInetAddress() 
            + ", 접속 Port: " + sk1.getPort());
         
         ThreadServerClass tsc = new ThreadServerClass(sk1);
         lstThread.add(tsc);
         System.out.println("접속자 수: " + lstThread.size());
         tsc.start();
      }
   }
   
   public void sendChat(String sMsg) throws IOException {
      String sPacket = TcpMultiLib.makePacket(TcpMultiLib.sPacketCmdMsg, 
            TcpMultiLib.sPacketCmdToAll, sMsg);
      System.out.println(">>> " + lstThread.size() + "- " + sPacket);
      
      for(int i = 0; i < lstThread.size(); i++) {
         lstThread.get(i).dos.writeUTF(sPacket);
      }
   }
   
   public void analyzePacket(String sPacket1, ThreadServerClass tc1) throws IOException {
      if(sPacket1 != null && sPacket1.length() >= 1) {
         String[] asSplit = sPacket1.split(TcpMultiLib.sPacketDelimiter);
         System.out.println(sPacket1);
         
         if(asSplit.length >= 3) {
            String cmd = asSplit[0];
            String sReceiver = asSplit[1];
            String sExt = asSplit[2];
            String sSenderId = tc1.getUserId();
            String sPacket2;
            String sMsg;
            
            // 메시지
            if(cmd.equals(TcpMultiLib.sPacketCmdMsg)) {
               if(sReceiver.equals(TcpMultiLib.sPacketCmdToAll)) {
                  sPacket2 = TcpMultiLib.makeMsg2Packet(sSenderId, sExt);
                  
                  for(int i = 0; i < lstThread.size(); i++) {
                     ThreadServerClass tc2 = lstThread.get(i);
                     System.out.println("<<< " + sSenderId + " " + sPacket1);
                     tc2.dos.writeUTF(sPacket2);
                  }
               }
               else {
                  sPacket2 = TcpMultiLib.makeMsg2Packet(sSenderId, "(귓속말)" + sExt);
                  
                  for(int i = 0; i < lstThread.size(); i++) {
                     ThreadServerClass tc2 = lstThread.get(i);
                     
                     if(tc2.checkUserId(sReceiver)) {
                        System.out.println(sReceiver + " <<< " + sSenderId + " " + sPacket2);
                        tc2.dos.writeUTF(sPacket2);
                     }
                  }
               }
            }
            // file
            if(cmd.equals(TcpMultiLib.sPacketCmdSendFile)) {
               System.out.println(sExt);
               sPacket2 = TcpMultiLib.makeFilePacket2SomeOne(sSenderId, sExt);
               
               if(sReceiver.equals(TcpMultiLib.sPacketCmdToAll)) {
                  for(int i = 0; i < lstThread.size(); i++) {
                     ThreadServerClass tc2 = lstThread.get(i);
                     
                     if(!tc2.checkUserId(sSenderId)) {
                        System.out.println("<<< " + sSenderId + " " + sPacket1);
                        tc2.dos.writeUTF(sPacket2);
                     }
                  }
               }
               else {
                  for(int i = 0; i < lstThread.size(); i++) {
                     ThreadServerClass tc2 = lstThread.get(i);
                     
                     if(tc2.checkUserId(sReceiver)) {
                        System.out.println(sReceiver + " <<< " + sSenderId + " " + sPacket2);
                        tc2.dos.writeUTF(sPacket2);
                     }
                  }
               }
               
               int fileLen = tc1.dis.readInt();
               byte[] abyBuff = new byte[fileLen];
               tc1.dis.readFully(abyBuff);

               if(sReceiver.equals(TcpMultiLib.sPacketCmdToAll)) {
                  for(int i = 0; i < lstThread.size(); i++) {
                     ThreadServerClass tc2 = lstThread.get(i);

                     if(!tc2.checkUserId(sSenderId)) {
                        tc2.dos.writeInt(abyBuff.length);
                        tc2.dos.write(abyBuff);    
                     }
                  }
               }
               else {
                  for(int i = 0; i < lstThread.size(); i++) {
                     ThreadServerClass tc2 = lstThread.get(i);
                     
                     if(tc2.checkUserId(sReceiver)) {
                        tc2.dos.writeInt(abyBuff.length);
                        tc2.dos.write(abyBuff);    
                     }
                  }
               }
            }
            // login
            else if(cmd.equals(TcpMultiLib.sPacketCmdLogin)) {
               String sResult1;
               boolean loginResult = chkLogin(sReceiver, sExt, tc1);
               
               if(loginResult) {
                  String sLastIfo = addLoginInfo(sReceiver);
                  sResult1 = TcpMultiLib.sPacketResultOk;
                  sMsg = "최근 Login 시간\n" + sLastIfo + "\n"
                       + sReceiver + "(으)로 로긴했습니다.";
               }
               else {
                  sResult1 = TcpMultiLib.sPacketResultError;
                  sMsg = sReceiver + "(으)로 로긴할 수 없습니다.";
               }
               
               String sResult2 = TcpMultiLib.makePacket(TcpMultiLib.sPacketResult, sResult1, sMsg);
               tc1.dos.writeUTF(sResult2);
               
               if(loginResult) {
                  sendChat(sReceiver + "님 입장!!!");
               }
            }
         }
      }
   }
   
   private String addLoginInfo(String sUserId) {
      Connection conn = null;
      String sResult = "<없음>";
      String driverName = "oracle.jdbc.driver.OracleDriver";
      String url = "jdbc:oracle:thin:@localhost:1521:xe"; // localhost대신 ip주소가 들어갈수도
      String id = "hr";
      String pw = "hr";
      
      try {
         // 2. JDBC 드라이버 로딩
         Class.forName(driverName);

         // 3. 접속
         // - Connection 객체 생성 + 접속 작업.
         conn = DriverManager.getConnection(url, id, pw);
         
         String sql = "INSERT INTO login_table"
                    + " (user_id)"
                    + " VALUES"
                    + " (?)";

         PreparedStatement st = conn.prepareStatement(sql);
         st.setString(1, sUserId);
         int insResult = st.executeUpdate();
         
         if(insResult == 1 ) {
            st.close();
            
            sql = "SELECT *"
                + " FROM  (" 
                + "         SELECT DISTINCT user_id, TO_CHAR(login_dt, 'YYYY-MM-DD') as login_dt"
                + "         FROM   login_table"
                + "         WHERE  user_id = ?"
                + "         ORDER  BY login_dt DESC" 
                + "       )"               
                + " WHERE rownum <= 3";
               
            System.out.println(sql);
            st = conn.prepareStatement(sql);
            st.setString(1, sUserId);
            ResultSet rs = st.executeQuery();
            StringBuilder sb = new StringBuilder();
            
            while(rs.next()) {
               System.out.println(1);
               String sUserId2 = rs.getString("user_id");
               String sDate = rs.getString("login_dt");
               System.out.println(sUserId2 + ", " + sDate);
               System.out.println(2);
               sb.append(sUserId2 + ", " + sDate + "에 로긴했었음.\n");
            }
            
            sResult = sb.toString();
            System.out.println(sResult);
         }
         else {
            sResult = "login 정보 저장 오류";
         }

         conn.close();
      } 
      catch (Exception ex) {
         ex.printStackTrace();
         sResult = "DB 오류";
      }
      
      return sResult;   
   }

   private boolean chkLogin(String id, String pwd, ThreadServerClass tc) {
      boolean result = true;
      
      if(result) {
         tc.userId = id;
      }
      
      return result;
   }

   class ThreadServerClass extends Thread {
      private Socket sk;
      private DataInputStream dis;
      private DataOutputStream dos;
      private String userId;
     
      public String getUserId() {
         return userId;
      }

      public Socket getSk() {
         return sk;
      }

      public DataInputStream getDis() {
         return dis;
      }

      public DataOutputStream getDos() {
         return dos;
      }

      public ThreadServerClass(Socket sk) throws IOException {
         this.sk = sk;
         dis = new DataInputStream(sk.getInputStream());
         dos = new DataOutputStream(sk.getOutputStream());
      }
      
      public boolean checkUserId(String userId) {
         return this.userId.compareToIgnoreCase(userId) == 0;
      }

      @Override
      public void run() {
         try {
//            if(dis != null) {
//               String sPacket = dis.readUTF();
//               analyzePacket(sPacket, this);
//            }
            while(dis != null) {
               String sPacket = dis.readUTF();
               analyzePacket(sPacket, this);
               //sendChat(sPacket);
            }
         }
         catch (IOException e) {
            e.printStackTrace();
         }
         finally {
            for(int i = 0; i < lstThread.size(); i++) {
               if(sk.equals(lstThread.get(i).sk)) {
                  lstThread.remove(i);
                  
                  try {
                     sendChat(userId + "님 퇴장!!!");
                  }
                  catch(IOException e) {
                     e.printStackTrace();
                  }
               }
            }
         }
         
         System.out.println("접속자 수: " + lstThread.size() + "명");
         
      }
   }

   public static void main(String[] args) throws NumberFormatException, IOException {
      int portNo = 19999;
      
      if(args.length != 1) {
         System.out.println("Usage: java PackageName.TcpMultiServerMain PortNo");
      }
      else {
         portNo = Integer.valueOf(args[0]);
      }
      
      new TcpMultiServerMain(portNo);
   }

}
