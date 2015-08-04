package ilrp.assist;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class MainFrame {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	
	private Driver driver = null;
	private Thread driverThread = null;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
		this.driver = new Driver();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton button = new JButton("\u5F00\u59CB\u62A2\u7EA2\u5305");
		JButton button_2 = new JButton("\u5F00\u59CB\u53D1\u7EA2\u5305");
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (driver == null) {
					driver = new Driver();
				}
//				driverThread = new Thread(driver);
//				driverThread.start();
//				driver.clickRedPacket(driver.wx1);
				button.setEnabled(false);
			}
		});
		button.setBounds(10, 219, 111, 33);
		frame.getContentPane().add(button);
		
		button_2.setBounds(131, 219, 111, 33);
		frame.getContentPane().add(button_2);
		
		JButton button_3 = new JButton("\u505C\u6B62");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				driver.postRedPacket(driver.wx1, textField.getText(), textField_1.getText());
			}
		});
		button_3.setBounds(252, 219, 157, 33);
		frame.getContentPane().add(button_3);
		
		JLabel lblNewLabel = new JLabel("\u8F6C\u5305\u6A21\u5F0F");
		lblNewLabel.setBounds(25, 30, 54, 15);
		frame.getContentPane().add(lblNewLabel);
		
		textField = new JTextField();
		textField.setText("1");
		textField.setBounds(288, 132, 121, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel label = new JLabel("\u7EA2\u5305\u91D1\u989D");
		label.setBounds(211, 99, 54, 15);
		frame.getContentPane().add(label);
		
		textField_1 = new JTextField();
		textField_1.setText("0.01");
		textField_1.setColumns(10);
		textField_1.setBounds(288, 96, 121, 21);
		frame.getContentPane().add(textField_1);
		
		JLabel label_1 = new JLabel("\u5185\u90E8\u7FA4\u540D\u79F0");
		label_1.setBounds(211, 30, 80, 15);
		frame.getContentPane().add(label_1);
		
		textField_2 = new JTextField();
		textField_2.setText("0.01");
		textField_2.setColumns(10);
		textField_2.setBounds(288, 27, 121, 21);
		frame.getContentPane().add(textField_2);
		
		JLabel label_2 = new JLabel("\u5916\u90E8\u7FA4\u540D\u79F0");
		label_2.setBounds(211, 63, 80, 15);
		frame.getContentPane().add(label_2);
		
		textField_3 = new JTextField();
		textField_3.setText("0.01");
		textField_3.setColumns(10);
		textField_3.setBounds(288, 60, 121, 21);
		frame.getContentPane().add(textField_3);
		
		JLabel label_3 = new JLabel("\u62BD\u6C34\u91D1\u989D");
		label_3.setBounds(211, 135, 54, 15);
		frame.getContentPane().add(label_3);
		
		JLabel label_4 = new JLabel("\u7EA2\u5305\u4EFD\u6570");
		label_4.setBounds(211, 171, 54, 15);
		frame.getContentPane().add(label_4);
		
		textField_4 = new JTextField();
		textField_4.setText("1");
		textField_4.setColumns(10);
		textField_4.setBounds(288, 168, 121, 21);
		frame.getContentPane().add(textField_4);
		
		JCheckBox chckbxAb = new JCheckBox("A+B");
		chckbxAb.setBounds(20, 72, 54, 23);
		frame.getContentPane().add(chckbxAb);
		
		JCheckBox chckbxA = new JCheckBox("A");
		chckbxA.setBounds(20, 108, 54, 23);
		frame.getContentPane().add(chckbxA);
		
		JCheckBox chckbxB = new JCheckBox("B");
		chckbxB.setBounds(20, 143, 54, 23);
		frame.getContentPane().add(chckbxB);
		
		textField_5 = new JTextField();
		textField_5.setText("0.01");
		textField_5.setColumns(10);
		textField_5.setBounds(74, 73, 60, 21);
		frame.getContentPane().add(textField_5);
		
		textField_6 = new JTextField();
		textField_6.setText("0.01");
		textField_6.setColumns(10);
		textField_6.setBounds(74, 109, 60, 21);
		frame.getContentPane().add(textField_6);
		
		textField_7 = new JTextField();
		textField_7.setText("0.01");
		textField_7.setColumns(10);
		textField_7.setBounds(74, 145, 60, 21);
		frame.getContentPane().add(textField_7);
		
		JLabel lblNewLabel_1 = new JLabel("------------------------------------------------------------------------");
		lblNewLabel_1.setBounds(0, 194, 434, 15);
		frame.getContentPane().add(lblNewLabel_1);
		
	}
}
