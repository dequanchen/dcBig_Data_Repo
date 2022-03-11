package Kerberos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

import dcBDAppliance_InstallJDBCDrivers.ULServerCommandFactory;
import dcModelClasses.ConfigureULWResources;
import dcModelClasses.HdfsUtil;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ConfigureTestUserADAuthenticator;





public class dcTrial_NewHTTPKrbAuthenticationCodeChange {
//	static final String kuser = "wa00336"; // your account name
//	static final String kpass = "bnhjui89"; // your password for the account
//	 
//	static class MyAuthenticator extends Authenticator {
//	    public PasswordAuthentication getPasswordAuthentication() {
//	      // I haven't checked getRequestingScheme() here, since for NTLM
//	      // and Negotiate, the usrname and password are all the same.
//	      System.err.println("Feeding username and password for "
//	          + getRequestingScheme());
//	      return (new PasswordAuthentication(kuser, kpass.toCharArray()));
//	    }
//	}

	public static void main(String[] args) {
		String bdClusterName = "BDProd3"; //BDProd3...BDTest3...BDDev3..BDDev1..BDProd2..BDTest2
		BdCluster currBdCluster = new BdCluster(bdClusterName);	
		
		String bdClusterActiveRMIPAddressAndPort = currBdCluster.getBdClusterActiveRMIPAddressAndPort();
		System.out.println("\n *** bdClusterActiveRMIPAddressAndPort: " + bdClusterActiveRMIPAddressAndPort);
	
//		ConfigureTestUserADAuthenticator krbTestUserAuthenticator = new ConfigureTestUserADAuthenticator();
//		
//		String nn1ServiceState = "standby";
//		String rm1ServiceState = "standby";			
//		
//		try {
//			Authenticator.setDefault(krbTestUserAuthenticator);
//			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
//			ULServerCommandFactory bdENCommFactory = null; //new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); //getBdProd2EN02ConParameters..getBdTest2EN02ConParameters...getBdProd2EN01ConParameters()
//			
//			if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1MN01ConParameters()); 
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev3")) {					
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3MN01ConParameters()); 
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDProd2")){
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN01ConParameters()); 
//			}
//			if (bdClusterName.equalsIgnoreCase("BDProd3")){
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3MN01ConParameters()); 
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDTest2")
//							//|| bdClusterName.equalsIgnoreCase("BDPrd")
//							){						
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN01ConParameters()); 
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest3")
//							){						
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3MN01ConParameters()); 
//			}
//			
//			
//			if (bdClusterName.equalsIgnoreCase("BDSbx")
//					|| bdClusterName.equalsIgnoreCase("BDSdbx")){
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN01ConParameters()); 
//			}
//			
//			//System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());
//			//"http://hdpr03mn01.mayo.edu:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus"
//			String nn1StatusQueryURI = "http://" + bdENCommFactory.getServerURI() + ":50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
//			System.out.println("\n *** nn1StatusQueryURI: " + nn1StatusQueryURI);
//			
//			URL aWebSiteURL = new URL(nn1StatusQueryURI);	     
//	        URLConnection myConn = aWebSiteURL.openConnection();
//	        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
//	        String inputLine;	        
//	        while ((inputLine = bReader.readLine()) != null){	        	
//	            if (inputLine.contains("\"State\" :") ){
//	            	//System.out.println(inputLine.trim());
//	            	inputLine = inputLine.replace("\"State\" :", "");
//	            	nn1ServiceState = inputLine.replace("\"State\" :", "").replace("\"", "").replace(",", "").trim();
//	            }
//	        }
//	        bReader.close();
//			System.out.println("\n*** nn1ServiceState: " + nn1ServiceState );
//			
//			if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1MN01ConParameters()); 
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev3")) {					
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3MN03ConParameters()); 
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDProd2")){
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN01ConParameters()); 
//			}
//			if (bdClusterName.equalsIgnoreCase("BDProd3")){
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3MN03ConParameters()); 
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDTest2")
//							//|| bdClusterName.equalsIgnoreCase("BDPrd")
//							){						
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN01ConParameters()); 
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest3")
//							){						
//				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3MN03ConParameters()); 
//			}
//			
//			String rm1StatusQueryURI = "http://" + bdENCommFactory.getServerURI() + ":8088/ws/v1/cluster/info";
//			System.out.println("\n *** rm1StatusQueryURI: " + rm1StatusQueryURI);
//			
//			URL bWebSiteURL = new URL(rm1StatusQueryURI);	     
//			URLConnection myConn_b = bWebSiteURL.openConnection();
//			BufferedReader bReader_b = new BufferedReader(new InputStreamReader(myConn_b.getInputStream()));
//	        String inputLine_b;	        
//	        while ((inputLine_b = bReader_b.readLine()) != null){	        	
//	        	if (inputLine_b.contains("\"haState\":\"") ){
//	            	//System.out.println(inputLine_b.trim());
//	            	String[] lineSplit = inputLine_b.trim().split("\"haState\":\"");
//	            	String tgtStr = lineSplit[1].trim();
//	            	if (tgtStr.contains("\",\"")){
//	            		String[] tgtStrSplit = tgtStr.split("\",\"");
//	            		rm1ServiceState = tgtStrSplit[0].toLowerCase();
//	            	}             	
//	            }
//	        }
//		} catch (Exception e) {				
//			e.printStackTrace();
//		}//end try		
//		System.out.println("\n*** rm1ServiceState: " + rm1ServiceState );
//		
//		try {
//			Authenticator.setDefault(new MyAuthenticator());
//			String nn1StatusQueryURI ="http://hdpr03mn01.mayo.edu:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
//			System.out.println("\n *** nn1StatusQueryURI: " + nn1StatusQueryURI);
//			
//			URL aWebSiteURL = new URL(nn1StatusQueryURI);	     
//	        URLConnection myConn = aWebSiteURL.openConnection();
//	        
//	        
//	        
//	        
////	        myConn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
////	        myConn.setRequestProperty("Accept","*/*");
////	        myConn.setDoInput(true);
////	        
////	        String authStr = "m041785" + ":" + "feeGoo17";
////	        String authStringEnc = Base64.encodeBytes(authStr.getBytes());
////	        
////	        URLConnection myConn = aWebSiteURL.openConnection();
////	        myConn.setRequestProperty("Authorization", String.format("Basic %s", encoding));
//	        
//	        //String authHeader = "Negotiate " + getKrbToken("HTTP/web.springsource.com");
//	        /////e.g.,The target SPN is HTTP/web.springsource.com
//	        //String authHeader = "Negotiate " + getKrbToken("HTTP/hdpr05mn01.mayo.edu");
//	        
//	        
//	        //http://alvinalexander.com/java/jwarehouse/openjdk-8/jdk/test/sun/net/www/protocol/http/spnegoReadme.shtml
//	        //http://seenukarthi.com/security/2014/09/07/kerberos-thick-client-for-rest-service/#waffle-dependency
//	        //http://jianmingli.com/wp/?p=6716
//	        	
////	        HttpHeaders headers = new HttpHeaders () {
////	            {
////	                byte[] encodedAuth = Base64.encode(getToken());
////	                //The target SPN is HTTP/web.springsource.com
////	                String authHeader = "Negotiate " + getKrbToken("HTTP/web.springsource.com");
////	                set("Authorization", authHeader);
////	            }
////
////				private void set(String string, String authHeader) {
////					// TODO Auto-generated method stub
////					
////				}
////	        };
//			
////	        String username = "m041785";
////	        String password = "feeGoo17";	             
////	        String encodedAuthorizedUser = getAuthantication(username, password);
//	        //System.out.println("encodedAuthorizedUser: " + encodedAuthorizedUser);
//	        
//	        //URLConnection  myConn = aWebSiteURL.openConnection();
//	       // myConn.setRequestProperty ("Authorization", "Basic " + encodedAuthorizedUser );
//			
//			//System.out.println(((HttpURLConnection) myConn).getResponseCode());
//	        
//	       
//	    
//	        
//	        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
//	        String inputLine;	        
//	        while ((inputLine = bReader.readLine()) != null){	        	
//	            if (inputLine.contains("\"State\" :") ){
//	            	System.out.println(inputLine.trim());
//	            	inputLine = inputLine.replace("\"State\" :", "");
//	            	nn1ServiceState = inputLine.replace("\"State\" :", "").replace("\"", "").replace(",", "").trim();
//	            }
//	        }
//	        bReader.close();	        
//		} catch (Exception e) {				
//			e.printStackTrace();
//		}//end try
//		
//		System.out.println("\n*** nn1ServiceState: " + nn1ServiceState );
//		System.out.println("\n*** rm1ServiceState: " + rm1ServiceState );
//		
			        
		
	}
	


}
