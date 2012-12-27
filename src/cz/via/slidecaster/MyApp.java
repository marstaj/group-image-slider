package cz.via.slidecaster;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

public class MyApp extends Application {

	private String hash;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		hash = getsaltedHash(imei);
	}

	public static String getsaltedHash(String imei) {
		String salt = "Random$SaltValue#WithSpecialCharacters12@$@4&#%^$*";
		return md5(imei + salt);
	}

	public static String md5(String input) {
		String md5 = null;

		if (null == input)
			return null;
		try {
			// Create MessageDigest object for MD5
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// Update input string in message digest
			digest.update(input.getBytes(), 0, input.length());
			// Converts message digest value in base 16 (hex)
			md5 = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5;
	}

	public String getDeviceId() {
		return hash;
		//return "id";
	}

}