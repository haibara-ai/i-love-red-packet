package ilrp.assist;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainFrame {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	
	private Driver driver = null;
	private Thread driverThread = null;
	
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
		JButton button_1 = new JButton("\u505C\u6B62\u62A2\u7EA2\u5305");
		JButton button_2 = new JButton("\u8FDE\u63A5\u670D\u52A1\u5668");
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (driver == null) {
					driver = new Driver();
				}
				driverThread = new Thread(driver);
				driverThread.start();
				button.setEnabled(false);
				button_1.setEnabled(true);
			}
		});
		button.setBounds(28, 21, 111, 33);
		frame.getContentPane().add(button);
		
		button_1.setEnabled(false);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				driverThread.interrupt();
				button.setEnabled(true);
				button_1.setEnabled(false);
			}
		});
		button_1.setBounds(168, 21, 111, 33);
		frame.getContentPane().add(button_1);
		
		button_2.setBounds(28, 71, 111, 33);
		frame.getContentPane().add(button_2);
	}
}
