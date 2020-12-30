package com.yskim.ex.thread;

public class TcpMultiLib {
   public static String sPacketDelimiter = ":";
   public static String sPacketCmdLogin = "/LOGIN";   // "/LOGIN:ID:PWD"
   public static String sPacketResult = "/RESULT";    // "/RESULT:OK:msg", "/RESULT:ERR:msg"
   public static String sPacketResultOk = "OK";
   public static String sPacketResultError = "ERROR";
   public static String sPacketCmdMsg = "/MSG";       // "/MSG:*:msg"
   public static String sPacketCmdMsgToAll = "*";     // "/MSG:receiver:msg"
   public static String sPacketCmdMsg2 = "/RMSG";     // "/RMSG:sender:msg"

   public static String makePacket(String sPacketCmd1, String sParam1, String sParam2) {
      return sPacketCmd1 + sPacketDelimiter 
            + sParam1 + sPacketDelimiter + sParam2;
   }

   public static String makeLoginPacket(String sUserId, String sPwd) {
      return makePacket(sPacketCmdLogin, sUserId, sPwd);
   }

   public static String makeMsgPacket4All(String sMsg) {
      return makePacket(sPacketCmdMsg, sPacketCmdMsgToAll, sMsg);
   }

   public static String makeMsgPacket4SomeOne(String sUserId, String sMsg) {
      return makePacket(sPacketCmdMsg, sUserId, sMsg);
   }

   public static String makeMsg2Packet(String sUserId, String sMsg) {
      return makePacket(sPacketCmdMsg2, sUserId, sMsg);
   }

}
