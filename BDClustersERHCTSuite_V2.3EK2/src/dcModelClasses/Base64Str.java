package dcModelClasses;

import org.apache.commons.codec.binary.Base64;

public class Base64Str {

	public Base64Str() {		
	}//default constructor
	
	public static String getDecodedString(String encodedString) {
		return new String(Base64.decodeBase64(encodedString.getBytes()));		
	}
	
	public static String getEncodedString(String decodedString) {
		return new String(Base64.encodeBase64(decodedString.getBytes()));		
	}	
}//end class
