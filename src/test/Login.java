package test;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JFrame {
	BufferedImage img = null;
	String Ip;
	int portno;
	Socket s1;
	String nickname;

	// 메인
	public static void main(String[] args) {
		new Login();
	}

	// 생성자
	public Login() {
		setTitle("IP, PORTNUMBER");
		setSize(830, 850);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("IP");
		lblNewLabel_1.setForeground(Color.GREEN);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("굴림", Font.BOLD | Font.ITALIC, 25));
		lblNewLabel_1.setBounds(342, 294, 106, 50);
		getContentPane().add(lblNewLabel_1);

		JTextPane textPane = new JTextPane();
		textPane.setBounds(273, 342, 257, 34);
		getContentPane().add(textPane);

		JLabel lblNewLabel_2 = new JLabel("PORTNUMBER\r\n");
		lblNewLabel_2.setForeground(Color.GREEN);
		lblNewLabel_2.setFont(new Font("돋움체", Font.BOLD | Font.ITALIC, 25));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(301, 388, 186, 50);
		getContentPane().add(lblNewLabel_2);

		JTextPane textPane_1 = new JTextPane();
		textPane_1.setBounds(273, 439, 257, 34);
		getContentPane().add(textPane_1);

		JTextPane textPane_1_1 = new JTextPane();
		textPane_1_1.setBounds(273, 529, 257, 34);
		getContentPane().add(textPane_1_1);

		JButton btnNewButton = new JButton("접속");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Ip = textPane.getText();
				portno = Integer.parseInt(textPane_1.getText());
				nickname = textPane_1_1.getText();
				try {
					if (Ip != null && portno != 0)
						new TcpMultiClientMain(Ip, portno, nickname);

					dispose();
					setVisible(false);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setFont(new Font("돋움체", Font.BOLD | Font.ITALIC, 25));
		btnNewButton.setForeground(Color.BLUE);
		btnNewButton.setBounds(316, 602, 186, 50);
		getContentPane().add(btnNewButton);

		JLabel lblNewLabel_3 = new JLabel("닉네임\r\n");
		lblNewLabel_3.setFont(new Font("한컴 고딕", Font.BOLD | Font.ITALIC, 20));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setForeground(Color.GREEN);
		lblNewLabel_3.setBounds(342, 485, 116, 50);
		getContentPane().add(lblNewLabel_3);

      String logo = "/resources/images/logo.png";
      System.out.println("Logo Resource: " + logo);
      System.out.println("Logo 파일 위치: " + Login.class.getResource(logo).getPath());
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(Login.class.getResource(logo)));
		lblNewLabel.setBounds(0, 12, 800, 800);
		getContentPane().add(lblNewLabel);
		setVisible(true);

		MyPanel panel = new MyPanel();
		panel.setBounds(0, 0, 800, 800);

	}

	class MyPanel extends JPanel {
		public void paint(Graphics g) {
			System.out.println("1212312");
			g.drawImage(img, 0, 0, null);
		}
	}
}
