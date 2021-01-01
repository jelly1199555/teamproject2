package DAY12_29test1;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Color;

public class TcpMultiClientGUI2 extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TcpMultiClientGUI2 frame = new TcpMultiClientGUI2();
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
	public TcpMultiClientGUI2() {
		
		Color color1 = new Color(90,135,221); //밝은 파랑
	    Color color2 = new Color(10,31,64); //남색
	    
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.white);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JButton btnNewButton = new JButton("닫기");
		btnNewButton.setBounds(149, 194, 129, 29);
		btnNewButton.setBackground(color1);
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setFont(new Font("맑은 고딕 Semilight", Font.BOLD, 15));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			setVisible(false);
			}
		});
		contentPane.setLayout(null);
		contentPane.add(btnNewButton);
		JLabel lblw = new JLabel("/w상대방아이디 보낼메세지");
		lblw.setBounds(53, 64, 321, 56);
		lblw.setFont(new Font("맑은 고딕", Font.BOLD, 25));
		lblw.setForeground(color1);
		contentPane.add(lblw);
		
		JLabel lblNewLabel_2 = new JLabel("위와 같이 메세지 전송시");
		lblNewLabel_2.setBounds(53, 123, 173, 21);
		lblNewLabel_2.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_2_1 = new JLabel("상대방에게 '비밀메시지'가 보내집니다");
		lblNewLabel_2_1.setBounds(53, 146, 262, 21);
		lblNewLabel_2_1.setBackground(new Color(240, 240, 240));
		lblNewLabel_2_1.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		contentPane.add(lblNewLabel_2_1);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(190, 0, 48, 92);
		lblNewLabel.setIcon(new ImageIcon(Login.class.getResource("/img/message3.png")));
		
		contentPane.add(lblNewLabel);
	}
}
