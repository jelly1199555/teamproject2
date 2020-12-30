package com.yskim.ex.thread;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class TcpMultiClientGUI extends JFrame implements Runnable, ActionListener {
   private DataInputStream dis;
   private DataOutputStream dos;
   private String sUserId;

   JLabel lbl1 = new JLabel("그룹채팅");
   TextArea txtArea1 = new TextArea();
   TextField txtField1 = new TextField();
   JScrollPane scrollPane1 = new JScrollPane(txtArea1);

   public TcpMultiClientGUI(DataInputStream dis, DataOutputStream dos, String sUserId) {
      this.dis = dis;
      this.dos = dos;
      this.sUserId = sUserId;
      
      setLayout(new BorderLayout());
      lbl1.setFont(new Font("굴림", Font.BOLD, 22));
      add("North", lbl1);
      
      txtArea1.setBackground(Color.yellow);
      txtArea1.setForeground(Color.blue);
      txtArea1.setFont(new Font("굴림", Font.BOLD, 22));
      txtArea1.setEditable(false);
      txtArea1.setText("귀속말 보내는 방법: 채팅창에\n/w아이디 메시지\n를 입력하면 '아이디' 사용자에게 '메시지'가 전송됩니다.\n");
      add("Center", scrollPane1);
      
      txtField1.setBackground(Color.white);
      txtField1.setForeground(Color.black);
      txtField1.setFont(new Font("굴림", Font.BOLD, 25));
      add("South", txtField1);
      txtField1.addActionListener(this);
      
      setSize(800, 800);
      setVisible(true);

      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            dispose();
            setVisible(false);
            //dis.close();
            //dos.close();
            // sk.close();
            System.exit(0); // 강제 종료. socket을 닫음
         }
      });
      
      Thread thread = new Thread(this);
      thread.start();
   }
   
   // OS 제어용 Toolkit
   Toolkit tk = Toolkit.getDefaultToolkit();

   int prevNo = -1;
   String prevId = "";
   
   @Override
   public void run() {
      try {
         while(true) {
            String sPacket = dis.readUTF();
            System.out.println("<<< " + sPacket);
            
            if(sPacket == null) {
               txtArea1.append("\n종료");
               break;
            }

            String[] asSplit = sPacket.split(":");
            String cmd = asSplit[0];
            
            // 메시지
            if(cmd.equals(TcpMultiLib.sPacketCmdMsg2)) {
               txtArea1.append("\n" + asSplit[1] + ": " + asSplit[2]);
            }
            else if(cmd.equals(TcpMultiLib.sPacketResult)) {
               txtArea1.append("\n" + "longin " + asSplit[1] 
                     + "\n" + asSplit[2]);
            }
            
            int txtLen = txtArea1.getText().length();
            txtArea1.setCaretPosition(txtLen);
            tk.beep();
         }
      }
      catch(Exception e) {
         txtArea1.append("\n" + e.getMessage());
      }
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == txtField1) {
         try {
            String sMsg = txtField1.getText();
            String sPacket = null;
            
            if(sMsg.startsWith("/w")) {
               int spPos = sMsg.indexOf(' ');
               
               if(spPos >= 3) {
                  String sUserId = sMsg.substring(2, spPos).trim();
                  String sMsg2 = sMsg.substring(spPos + 1);
                  
                  if(sUserId.length() >= 1) {
                     sPacket = TcpMultiLib.makeMsgPacket4SomeOne(sUserId, sMsg2);
                     txtArea1.append("\n(귀속말)" + sUserId + ": " + sMsg2);   
                     System.out.println("+++ " + sPacket);
                  }
               }
            }
            else {
               sPacket = TcpMultiLib.makeMsgPacket4All(sMsg);
            }
            
            if(sPacket != null) {
               for(int i = 0; i < 1; i++) {
                  dos.writeUTF(sPacket);
               }
            }
         } catch (IOException ex) {
            // ex.printStackTrace();
         }

         txtField1.setText("");
      }
   }
}
