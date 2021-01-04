package test;

public class TcpMultiLib {
   public static String sPacketDelimiter = ":";
   public static String sPacketCmdLogin = "/LOGIN";   // "/LOGIN:ID:PWD"
   public static String sPacketResult = "/RESULT";    // "/RESULT:OK:msg", "/RESULT:ERR:msg"
   public static String sPacketResultOk = "OK";
   public static String sPacketResultError = "ERROR";
   public static String sPacketCmdMsg = "/MSG";       // "/MSG:*:msg"
   public static String sPacketCmdToAll = "*";        // "/MSG:receiver:msg"
   public static String sPacketCmdMsg2 = "/RMSG";     // "/RMSG:sender:msg"
   public static String sPacketCmdSendFile = "/SFILE";// "/SFILE:receiver:fileName"
   public static String sPacketCmdRecvFile = "/RFILE";// "/RFILE:receiver:fileName"

   public static String sDownloadFolder = "C:/TMP/";  // 수신 파일이 보관될 Folder.

   public static String makePacket(String sPacketCmd1, String sParam1, String sParam2) {
      return sPacketCmd1 + sPacketDelimiter 
            + sParam1 + sPacketDelimiter + sParam2;
   }

   public static String makeLoginPacket(String sUserId, String sPwd) {
      return makePacket(sPacketCmdLogin, sUserId, sPwd);
   }

   public static String makeMsgPacket4All(String sMsg) {
      return makePacket(sPacketCmdMsg, sPacketCmdToAll, sMsg);
   }

   public static String makeMsgPacket4SomeOne(String sUserId, String sMsg) {
      return makePacket(sPacketCmdMsg, sUserId, sMsg);
   }

   public static String makeMsg2Packet(String sUserId, String sMsg) {
      return makePacket(sPacketCmdMsg2, sUserId, sMsg);
   }

   public static String makeFilePacket4SomeOne(String sUserId, String sFile) {
      return makePacket(sPacketCmdSendFile, sUserId, sFile);
   }

   public static String makeFilePacket2SomeOne(String sUserId, String sFile) {
      return makePacket(sPacketCmdRecvFile, sUserId, sFile);
   }

}
