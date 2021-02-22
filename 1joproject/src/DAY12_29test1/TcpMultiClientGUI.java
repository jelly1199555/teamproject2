package DAY12_29test1;

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
import javax.swing.JButton;
import java.awt.SystemColor;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TcpMultiClientGUI extends JFrame implements Runnable, ActionListener {
   private DataInputStream dis;
   private DataOutputStream dos;
   private String sUserId;
   
   Color color1 = new Color(90,135,221); //밝은 파랑
   Color color2 = new Color(18,30,63); //남색

   JLabel lbl1 = new JLabel("그룹채팅");
   TextField txtField1 = new TextField();


   public TcpMultiClientGUI(DataInputStream dis, DataOutputStream dos, String sUserId) {
   	getContentPane().setBackground(new Color(231, 235, 240));
      this.dis = dis;
      this.dos = dos;
      this.sUserId = sUserId;
      getContentPane().setLayout(null);
      lbl1.setBounds(25, 25, 612, 26);
      lbl1.setFont(new Font("맑은 고딕", Font.BOLD, 20));
      lbl1.setForeground(color1);
      getContentPane().add(lbl1);
      

      
      
      txtField1.setBounds(25, 555, 565, 142);
      txtField1.setBackground(Color.white);
      txtField1.setForeground(Color.black);
      txtField1.setFont(new Font("굴림", Font.BOLD, 25));
      getContentPane().add(txtField1);
      

      txtArea.setBackground(Color.WHITE);
      txtArea.setForeground(new Color(0, 0, 0));
      txtArea.setFont(new Font("맑은 고딕", Font.BOLD, 20));
      txtArea.setEditable(false);
/*      txtArea.setText("\n"+"귓속말 보내는 방법: 채팅창에\n/w아이디 메시지\n를 입력하면 '아이디' 사용자에게 '메시지'가 전송됩니다.\n\n");
*/      
      JButton btnExit = new JButton("EXIT");
      btnExit.setBackground(new Color(255, 255, 255));
     
      btnExit.setBounds(612, 126, 149, 54);
      btnExit.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent arg0) {
      		System.exit(0);
      	}
      });

      btnExit.setForeground(color1);
      btnExit.setFont(new Font("맑은 고딕", Font.BOLD, 20));
      getContentPane().add(btnExit);
      btnSend.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		new UPloadGUL().setVisible(true);
      	}
      });
      
      btnSend.setBounds(612, 555, 149, 54);
      btnSend.setBackground(color1);
      btnSend.setForeground(Color.white);
      btnSend.setFont(new Font("맑은 고딕", Font.BOLD, 20));
      
      getContentPane().add(btnSend);
      btnTip.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      	}
      });
      btnTip.addMouseListener(new MouseAdapter() {
      	@Override
      	public void mouseClicked(MouseEvent arg0) {
      		new TcpMultiClientGUI2().setVisible(true);
      	}
      });
      
      btnTip.setBounds(612, 66, 149, 54);
      btnTip.setFont(new Font("맑은 고딕", Font.BOLD, 20));
      btnTip.setBackground(new Color(255, 255, 255));
      btnTip.setForeground(color1);
      
      getContentPane().add(btnTip);
      
      JScrollPane scrollPane = new JScrollPane();
      scrollPane.setBounds(25, 66, 565, 459);
      getContentPane().add(scrollPane);
      
      scrollPane.setViewportView(txtArea);
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
   private final JButton btnSend = new JButton("FILE");
   private final JButton btnTip = new JButton("TIP");
   private final JTextArea txtArea = new JTextArea();
   
   @Override
   public void run() {
      try {
         while(true) {
            String sPacket = dis.readUTF();
            System.out.println("<<< " + sPacket);
            
            if(sPacket == null) {
               txtArea.append("\n종료");
               break;
            }

            String[] asSplit = sPacket.split(":");
            String cmd = asSplit[0];
            
            // 메시지
            if(cmd.equals(TcpMultiLib.sPacketCmdMsg2)) {
            	txtArea.append("\n" + asSplit[1] + ": " + asSplit[2]);
            }
            else if(cmd.equals(TcpMultiLib.sPacketResult)) {
            	txtArea.append("\n" + "longin " + asSplit[1] 
                     + "\n" + asSplit[2]);
            }
            
            int txtLen = txtArea.getText().length();
            txtArea.setCaretPosition(txtLen);
            tk.beep();
         }
      }
      catch(Exception e) {
    	  txtArea.append("\n" + e.getMessage());
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
                     txtArea.append("\n(r귓속말)" + sUserId + ": " + sMsg2);   
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
