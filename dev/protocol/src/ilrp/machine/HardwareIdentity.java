package ilrp.machine;

import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 当前机器对应的标识
 * 
 * HardwareIdentity.getInstance().getCode()
 * 
 * @author aleck
 *
 */
public class HardwareIdentity {
	private static final String GLOBAL_PASSWORD = "roses all the way";

	private static HardwareIdentity instance;

	static {
		try {
			Process process = Runtime.getRuntime().exec(
					new String[] { "wmic", "cpu", "get", "ProcessorId" });
			process.getOutputStream().close();
			Scanner sc = new Scanner(process.getInputStream());
			sc.next();					// property name
			String serial = sc.next();	// serial number
			sc.close();
			if (serial == null || serial.length() < 8) {
				serial = System.getProperty("user.name")
						+ System.getProperty("user.dir");
			}
			instance = new HardwareIdentity(serial);
		} catch (Exception e) {
			instance = null;
		}
	}

	public static HardwareIdentity getInstance() {
		return instance;
	}

	private String code;

	/**
	 * this is used for generating license only
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	private HardwareIdentity(String serial) {
		this.code = compute6digits(serial);
	}

	private String compute6digits(byte[] serial) {
		int numeric = 0;
		for (int i = 0; i < serial.length; i++) {
			numeric = (numeric * 13 + numeric / 10 + serial[i]) % 1000000;
		}
		if (numeric < 0) {
			numeric += 1000000;
		}
		return String.format("%06d", numeric);
	}

	private String compute6digits(String serial) {
		return compute6digits(serial.getBytes());
	}

	/**
	 * 返回当前的机器码
	 * @return
	 */
	public String getCode() {
		return code;
	}

	public String toString() {
		return getCode();
	}

	public String generatePassword(String code) {
		try {
			byte[] datasource = code.getBytes();
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(GLOBAL_PASSWORD.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			byte[] result = cipher.doFinal(datasource);
			return compute6digits(result);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public String generatePassword() {
		return generatePassword(code);
	}

	public static void main(String[] args) {
		System.out.println("code for this machine: " + HardwareIdentity.getInstance().getCode());
		System.out.println("password computer:");
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.print("code:");
			String code = sc.next();
			if (code.trim().length() == 0)
				break;
			HardwareIdentity identity = new HardwareIdentity("");
			identity.setCode(code);
			System.out.println("license:" + identity.generatePassword());
		}
		sc.close();
	}
}
