package DAY12_29test1;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseEvent;

public class UPloadGUL extends JFrame {

	private JPanel contentPane;
	private javax.swing.JTextField textField;
	private javax.swing.JTextField textField_1;
	private javax.swing.JButton btnNewButton_1;
	private javax.swing.JLabel lb;
	Socket s1;
	ObjectOutputStream oos;
	FileInputStream fin;
	File file;
	PrintWriter pw;
	int portno;
	public UPloadGUL(int portno1) {
		this.portno=portno1;
		
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UPloadGUL frame = new UPloadGUL();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	JFileChooser fileDial = new JFileChooser("C:/MyJava/");//기준 경로명을 정해야한다.
	
	class SenderThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String serverip= textField.getText();
			int port=8905; //포트 넘버를 받아와서 넣어야 한다.
			if (serverip == null || serverip.trim().isEmpty()) {
				JOptionPane.showMessageDialog(lb, "서버의 IP 주소를 입력하세요");
				textField.requestFocus();
				return;
			}//run if-end
			//소켓을 생성 해야 한다.
			try {
				s1 = new Socket(serverip, port);
				//서버와 연결 결과를 출력 합니다.
				System.out.println("서버와 접속 되었습니다.");
				//socket 출력 스트림=>object아웃풋으로 필터링
				//파일은 object인풋으로 파일을 보내야 합니다.
				oos = new ObjectOutputStream(s1.getOutputStream());
				file = fileDial.getSelectedFile();
				fin = new FileInputStream(file);
				//이렇게 하면 파일명을 서버에 전송한다.
				String fname = file.getName();
				pw = new PrintWriter(s1.getOutputStream(), true);
				oos.writeUTF(fname);
				oos.flush();//설명은 아래
//				출력 스트림과 버퍼된 출력 바이트를 강제로 쓰게 한다.
				int input = 0, count = 0;
				byte[]data = new byte[1024];
				while ((input = fin.read(data)) != -1) {
					oos.write(data,0,input);;
					oos.flush();
					System.out.println(count + "바이트 전송중....");
				}
				if(pw !=null)
					pw.close();
				if (oos != null) 
					oos.close();
				if(fin != null)
					fin.close();
				if(s1 != null)
					s1.close();
				JOptionPane.showMessageDialog(lb, "업로드 완료!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public UPloadGUL() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new javax.swing.JTextField();
		textField.setBounds(14, 12, 301, 52);
		textField.setText("localhost");
		textField.setBorder(BorderFactory.createTitledBorder("업로드 할 ip주소"));
		contentPane.add(textField);
		textField.setColumns(10);
		
		javax.swing.JButton btnNewButton = new javax.swing.JButton("ip접속");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				SenderThread tr=new SenderThread();
				tr.start();
			}
		});
		btnNewButton.setFont(new Font("한컴 고딕", Font.ITALIC, 20));
		btnNewButton.setBounds(375, 12, 143, 52);
		contentPane.add(btnNewButton);
		
		textField_1 = new javax.swing.JTextField();
		textField_1.setText("보낼파일을 찾아봐");
		textField_1.setColumns(10);
		textField_1.setBorder(BorderFactory.createTitledBorder("업로드할 파일"));
		textField_1.setBounds(14, 88, 301, 52);
		contentPane.add(textField_1);
		
		btnNewButton_1 = new javax.swing.JButton("파일찾기");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				fileDial.showOpenDialog(btnNewButton_1);
				File selFile = fileDial.getSelectedFile();
				textField_1.setText(selFile.getAbsolutePath());
				String filename = selFile.getName();
			}
		});
		btnNewButton_1.setFont(new Font("한컴 고딕", Font.ITALIC, 20));
		btnNewButton_1.setBounds(375, 88, 143, 52);
		contentPane.add(btnNewButton_1);
		
		lb = new javax.swing.JLabel("New label");
		lb.setBounds(14, 171, 504, 370);
		contentPane.add(lb);
	}
}
