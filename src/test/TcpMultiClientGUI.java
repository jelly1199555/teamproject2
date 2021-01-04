package test;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class TcpMultiClientGUI extends JFrame {

   private JPanel contentPane;
   private JTextField textField;
   private JTextArea txtLog;
   
   private DataInputStream dis;
   private DataOutputStream dos;
   private String sUserId;
   private JButton btnFile;

   /**
    * Launch the application.
    */
   public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               TcpMultiClientGUI frame = new TcpMultiClientGUI(null, null, "yskim");
               frame.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
   }

   /**
    * Create the frame.
    */
//   public TcpMultiClientGUI() {
//      // TODO Auto-generated constructor stub
//   }

   public TcpMultiClientGUI(DataInputStream dis, DataOutputStream dos, String sUserId) {
      this.dis = dis;
      this.dos = dos;
      this.sUserId = sUserId;
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 0, 800, 600);
      contentPane = new JPanel();
      contentPane.setBackground(Color.BLACK);
      contentPane.setForeground(Color.WHITE);
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);
      
      JScrollPane scrollPane = new JScrollPane();
      scrollPane.setBounds(0, 50, 781, 457);
      contentPane.add(scrollPane);
      
      JLabel lblTitle = new JLabel("그룹채팅 (1조 - 김용수, 손주희, 조문진, 최현경)");
      lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
      lblTitle.setFont(new Font("굴림", Font.PLAIN, 32));
      lblTitle.setBackground(Color.BLACK);
      lblTitle.setForeground(Color.WHITE);
      lblTitle.setBounds(20, 0, 776, 50);
      contentPane.add(lblTitle);
      
      txtLog = new JTextArea();
      scrollPane.setViewportView(txtLog);
      txtLog.setBackground(Color.DARK_GRAY);
      txtLog.setForeground(Color.WHITE);
      txtLog.setFont(new Font("굴림", Font.PLAIN, 30));
      
      textField = new JTextField();
      textField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            sendMsg(e);
         }
      });
      
      textField.setForeground(Color.WHITE);
      textField.setBackground(Color.BLACK);
      textField.setFont(new Font("굴림", Font.PLAIN, 30));
      textField.setBounds(0, 508, 630, 50);
      contentPane.add(textField);
      textField.setColumns(10);      
      
      btnFile = new JButton("파일");
      btnFile.setForeground(Color.WHITE);
      btnFile.setBackground(Color.BLACK);
      btnFile.setFont(new Font("굴림", Font.PLAIN, 30));
      btnFile.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            JFileChooser fc = new JFileChooser("C:/TMP");
            if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
               File fiSel = fc.getSelectedFile();
               String sFile = fiSel.getPath();
               textField.setText("/f " + sFile);
               
            }
         }
      });
      btnFile.setBounds(633, 508, 147, 50);
      contentPane.add(btnFile);
      
      addText("[귓속말 보내는 방법]");
      addText(" 채팅창에");
      addText("  /w아이디 메시지");
      addText(" 를 입력하면 '아이디' 접속자에게 '메시지'가 전송됩니다.");
      addText("");
      addText("[파일 보내는 방법]");
      addText(" 채팅창에");
      addText("  /f아이디 파일명");
      addText(" 를 입력하면 '아이디' 접속자에게 '파일명'의 파일이 전송됩니다.");
      addText(" 아이디를 생략하면 모든 접속자에게 '파일명'의 파일이 전송됩니다.");
      addText(" 수신된 파일은 " + TcpMultiLib.sDownloadFolder + "아이디/에 보관됩니다.");
      addText("");
      
      setVisible(true);

      Thread thread = new Thread(new TcpMultiClient(this, dis, dos, sUserId));
      thread.start();
   }
   
   public void addText(String msg) {
      txtLog.append("\n" + msg);
      
      int txtLen = txtLog.getText().length();
      txtLog.setCaretPosition(txtLen);
   }

   private void sendMsg(KeyEvent e) {
      try {
         if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String sMsg = textField.getText();
            String sPacket = null;
            String sFile = null;
            File fi = null;
            
            textField.setText("");
            
            if(sMsg.startsWith("/w")) {
               int spPos = sMsg.indexOf(' ');
               
               if(spPos >= 3) {
                  String sUserId = sMsg.substring(2, spPos).trim();
                  String sMsg2 = sMsg.substring(spPos + 1);
                  
                  if(sUserId.length() >= 1) {
                     sPacket = TcpMultiLib.makeMsgPacket4SomeOne(sUserId, sMsg2);
                     addText("(귓속말)" + sUserId + ": " + sMsg2);   
                     System.out.println("+++ " + sPacket);
                  }
               }
            }
            else if(sMsg.startsWith("/f")) {
               int spPos = sMsg.indexOf(' ');
               
               if(spPos >= 2) {
                  String sUserId = sMsg.substring(2, spPos).trim();
                  sFile = sMsg.substring(spPos + 1);
                  //sFile = "C:\\TMP\\shutup\\images\\복근\\기능성 전신 운동 터키쉬 겟업\\Turkish Getup.jpg";
                  fi = new File(sFile);
                  
                  if(fi.exists()) {
                     if(sUserId.length() == 0) {
                        sUserId = TcpMultiLib.sPacketCmdToAll;
                     }
                     
                     String sFile2 = fi.getName();
                     sPacket = TcpMultiLib.makeFilePacket4SomeOne(sUserId, sFile2);
                     addText("(파일)" + sUserId + ": " + sFile2);   
                     System.out.println("+++ " + sPacket);
                  }
                  else {
                     addText(sFile + "이(가) 존재하지 않습니다.");
                     fi = null;
                  }
               }
            }
            else {
               sPacket = TcpMultiLib.makeMsgPacket4All(sMsg);
            }
            
            if(sPacket != null) {
               dos.writeUTF(sPacket);
            }
            
            if(fi != null) {
               FileInputStream fis = new FileInputStream(fi);
               DataInputStream dis = new DataInputStream(fis);
   //            DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
               
               int fileSize = (int)fi.length();
               byte[] abyBuff = new byte[fileSize];
   
               dis.readFully(abyBuff);
               dos.writeInt(abyBuff.length);
               dos.write(abyBuff);            
               System.out.println(sFile + " 전송 OK!");
            }
         }
      } catch (IOException ex) {
         // ex.printStackTrace();
      }
   }
}

class TcpMultiClient implements Runnable {
   private TcpMultiClientGUI gui;
   private DataInputStream dis;
   private DataOutputStream dos;
   private String sUserId;
   private Toolkit tk;

   public TcpMultiClient(TcpMultiClientGUI gui, DataInputStream dis, DataOutputStream dos, String sUserId) {
      this.gui = gui;
      this.dis = dis;
      this.dos = dos;
      this.sUserId = sUserId;
      
      // OS 제어용 Toolkit
      tk = Toolkit.getDefaultToolkit();      
   }
   
   @Override
   public void run() {
      try {
         while(true) {
            String sPacket = dis.readUTF();
            System.out.println("<<< " + sPacket);
            
            if(sPacket == null) {
               gui.addText("종료");
               break;
            }

            String[] asSplit = sPacket.split(":");
            String cmd = asSplit[0];
            
            // 메시지
            if(cmd.equals(TcpMultiLib.sPacketCmdMsg2)) {
               gui.addText(asSplit[1] + ": " + asSplit[2]);
            }
            // File 수신
            else if(cmd.equals(TcpMultiLib.sPacketCmdRecvFile)) {
               String sSender = asSplit[1];
               String sFile = asSplit[2];
               File fi = new File(sFile);

               String sUserBaseFolder = TcpMultiLib.sDownloadFolder + sUserId + "/";
               File fiBaseFolder = new File(sUserBaseFolder);
               fiBaseFolder.mkdirs();
               
               String sFile2 = sUserBaseFolder + fi.getName();
               gui.addText(sSender + ": (파일) " + sFile2);
               
               int fileLen = dis.readInt();
               byte[] abyBuff = new byte[fileLen];
               dis.readFully(abyBuff);
               
               FileOutputStream fosFile = new FileOutputStream(sFile2);
               fosFile.write(abyBuff);
               fosFile.close();
               gui.addText("파일수신 완료");
            }

            // login 결과
            else if(cmd.equals(TcpMultiLib.sPacketResult)) {
               gui.addText("longin " + asSplit[1]);
               gui.addText(asSplit[2]);
            }
            
            tk.beep();
         }
      }
      catch(Exception e) {
         gui.addText(e.getMessage());
      }
   }
}