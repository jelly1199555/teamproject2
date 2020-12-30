package com.yskim.ex.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TcpMultiServerMain {
   private ArrayList<ThreadServerClass> lstThread = new ArrayList<>();
   private Socket sk;
   DataOutputStream dos;
   
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
            TcpMultiLib.sPacketCmdMsgToAll, sMsg);
      System.out.println(">>> " + lstThread.size() + "- " + sPacket);
      
      for(int i = 0; i < lstThread.size(); i++) {
         lstThread.get(i).dos.writeUTF(sPacket);
      }
   }
   
   public void analyzePacket(String sPacket1, ThreadServerClass tc1) throws IOException {
      if(sPacket1 != null && sPacket1.length() >= 1) {
         String[] asSplit = sPacket1.split(TcpMultiLib.sPacketDelimiter);
         
         if(asSplit.length >= 3) {
            String cmd = asSplit[0];
            
            // 메시지
            if(cmd.equals(TcpMultiLib.sPacketCmdMsg)) {
               String sSenderId = tc1.getUserId();
               String sPacket2;
               
               if(asSplit[1].equals(TcpMultiLib.sPacketCmdMsgToAll)) {
                  sPacket2 = TcpMultiLib.makeMsg2Packet(sSenderId, asSplit[2]);
                  
                  for(int i = 0; i < lstThread.size(); i++) {
                     ThreadServerClass tc2 = lstThread.get(i);
                     System.out.println("<<< " + sSenderId + " " + sPacket1);
                     tc2.dos.writeUTF(sPacket2);
                  }
               }
               else {
                  sPacket2 = TcpMultiLib.makeMsg2Packet(sSenderId, "(귀속말)" + asSplit[2]);
                  
                  for(int i = 0; i < lstThread.size(); i++) {
                     ThreadServerClass tc2 = lstThread.get(i);
                     
                     if(tc2.checkUserId(asSplit[1])) {
                        System.out.println(asSplit[1] + " <<< " + sSenderId + " " + sPacket2);
                        tc2.dos.writeUTF(sPacket2);
                     }
                  }
               }
            }
            else if(cmd.equals(TcpMultiLib.sPacketCmdLogin)) {
               String sResult1;
               String sMsg;
               String userId = asSplit[1];
               String pwd = asSplit[2];
               boolean loginResult = chkLogin(userId, userId, tc1);
               
               if(loginResult) {
                  sResult1 = TcpMultiLib.sPacketResultOk;
                  sMsg = userId + "(으)로 로긴했습니다.";
               }
               else {
                  sResult1 = TcpMultiLib.sPacketResultError;
                  sMsg = userId + "(으)로 로긴할 수 없습니다.";
               }
               
               String sResult2 = TcpMultiLib.makePacket(TcpMultiLib.sPacketResult, sResult1, sMsg);
               tc1.dos.writeUTF(sResult2);
               
               if(loginResult) {
                  sendChat(userId + "님 입장!!!");
               }
            }
         }
      }
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
