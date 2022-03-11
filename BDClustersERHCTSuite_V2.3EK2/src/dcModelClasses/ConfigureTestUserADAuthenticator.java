package dcModelClasses;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ConfigureTestUserADAuthenticator extends Authenticator {
	private String krbUserName = "wa00336"; // your AD account name...wa00336...
	private String krbUserPassword = "bnhgui89"; // your password for your AD account...bnhjui89...bnhgui89

	public PasswordAuthentication getPasswordAuthentication() {
	      // I haven't checked getRequestingScheme() here, since for NTLM
	      // and Negotiate, the usrname and password are all the same.
	      System.err.println(" --- Feeding AD Krb username and password for " + getRequestingScheme());
	      return (new PasswordAuthentication(krbUserName, krbUserPassword.toCharArray()));
	}

	public String getKrbUserName() {
		return krbUserName;
	}

	public void setKrbUserName(String krbUserName) {
		this.krbUserName = krbUserName;
	}

	public String getKrbUserPassword() {
		return krbUserPassword;
	}

	public void setKrbUserPassword(String krbUserPassword) {
		this.krbUserPassword = krbUserPassword;
	}	
	
}
