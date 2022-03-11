package Kerberos;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import com.sun.security.auth.callback.TextCallbackHandler;

public class FileCountReading2_BDDev_EK {
	//http://appcrawler.com/wordpress/2015/02/03/jaas-with-clientserver-socket-example/
	
	public static void main(final String[] args) throws IOException, FileNotFoundException, InterruptedException{
		System.setProperty( "sun.security.krb5.debug", "true");
		System.setProperty("java.security.krb5.realm", "MFAD.MFROOT.ORG");
		System.setProperty("java.security.kdc", "mfad.mfroot.org");
		System.setProperty( "javax.security.auth.useSubjectCredsOnly", "true");
		System.setProperty("java.security.auth.login.config", "C:\\Windows\\dcKerberos\\jaas.conf");
		
		String username = "m041785"; //"m041785@MFAD.MFROOT.ORG";
	    String password = "";

	    LoginContext lc = null;
	    try {	    	
            lc = new LoginContext(username, new TextCallbackHandler()); //"SampleClient"
            // attempt authentication
            lc.login();
	    } catch (LoginException le) {
	    	le.printStackTrace();
	    }

	     // Now try to execute ClientAction as the authenticated Subject

	     Subject mySubject = lc.getSubject();
	     System.out.println("\n**** mySubject: " + mySubject.getPrincipals());
	    
	    // LoginContext loginCtx = null;
	    // "Client" references the JAAS configuration in the jaas.conf file.
//	    loginCtx = new LoginContext( "Client", new Subject( username, password));
//	    System.out.println("trying to login");
//	    loginCtx.login();
//	    this.subject = loginCtx.getSubject();
	    
	}

}
