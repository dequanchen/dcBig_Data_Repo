package dcBDAppliance_DownloadRootPermittedFiles;

import org.jasypt.util.text.StrongTextEncryptor;

public class dcTestJaSypt {
	//Java7 Unlimited JCE: http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html

	public static void main(String[] args) {
		String myText = "m041785";
		String myEncryptionPassword = "20170313";		
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword(myEncryptionPassword);
		String myEncryptedText = textEncryptor.encrypt(myText);
		
		//myEncryptedText="1pnlEH6wngAfRChTh87jhw==";
		String plainText = textEncryptor.decrypt(myEncryptedText);
		
		System.out.println("*** myEncryptedText is: " + myEncryptedText);
		System.out.println("*** plainText is: " + plainText);

	}

}
