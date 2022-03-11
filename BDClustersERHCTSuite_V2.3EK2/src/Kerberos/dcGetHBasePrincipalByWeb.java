package Kerberos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;

public class dcGetHBasePrincipalByWeb {

	public static void main(String[] args) throws IOException {
		//http://www.avajava.com/tutorials/lessons/how-do-i-connect-to-a-url-using-basic-authentication.html
		
		//Not working:
		////ConfigureTestUserADAuthenticator krbTestUserAuthenticator = new ConfigureTestUserADAuthenticator();
		//// Authenticator.setDefault(krbTestUserAuthenticator);
		
		String name = "wa00336";
		String password = "bnhgui89";

		String authString = name + ":" + password;
		System.out.println("auth string: " + authString);
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		System.out.println("Base64 encoded auth string: " + authStringEnc);
		
		String clusterHBaseAllConfigVersionURL = "https://hdpr03mn01.mayo.edu:8081/api/v1/clusters/MAYOHADOOPDEV1/configurations/service_config_versions?service_name=HBASE";
		System.out.println("\n *** clusterHBaseAllConfigVersionURL: " + clusterHBaseAllConfigVersionURL);
		
		URL aWebSiteURL_all = new URL(clusterHBaseAllConfigVersionURL);	     
        URLConnection myConn_all = aWebSiteURL_all.openConnection();       
        //HttpsURLConnection myConn = (HttpsURLConnection) aWebSiteURL.openConnection();
        myConn_all.setRequestProperty("Authorization", "Basic " + authStringEnc);
        BufferedReader bReader_all = new BufferedReader(new InputStreamReader(myConn_all.getInputStream()));
        String line;
        String clusterHbaseLatestVersionConfigURL = "";
        
        while ((line = bReader_all.readLine()) != null){	        	
            if (line.contains("\"href\" :") ){
            	//System.out.println(line.trim());
            	clusterHbaseLatestVersionConfigURL=line.replace("\"href\" :", "").replace("\"", "").replace(",", "").trim();
            }
        }
        bReader_all.close();
        
        System.out.println("\n*** clusterHbaseLatestVersionConfigURL: " + clusterHbaseLatestVersionConfigURL );
		
		//https://hdpr03mn01.mayo.edu:8081/api/v1/clusters/MAYOHADOOPDEV1/configurations/service_config_versions?service_name=HBASE&service_config_version=97
		//String clusterHBaseConfigVersionURL = "https://hdpr03mn01.mayo.edu:8081/api/v1/clusters/MAYOHADOOPDEV1/configurations/service_config_versions?service_name=HBASE&service_config_version=97";
        String clusterHBaseConfigVersionURL = clusterHbaseLatestVersionConfigURL;
        System.out.println("\n *** clusterHBaseConfigVersionURL: " + clusterHBaseConfigVersionURL);
		
		URL aWebSiteURL = new URL(clusterHBaseConfigVersionURL);	     
        URLConnection myConn = aWebSiteURL.openConnection();
        myConn.setRequestProperty("Authorization", "Basic " + authStringEnc);
        //HttpsURLConnection myConn = (HttpsURLConnection) aWebSiteURL.openConnection();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
        String inputLine;
        String clusterHBasePrincipalName = "";
        
        while ((inputLine = bReader.readLine()) != null){	        	
            if (inputLine.contains("\"hbase_principal_name\" :") ){
            	//System.out.println(inputLine.trim());
            	inputLine = inputLine.replace("\"hbase_principal_name\" :", "");
            	clusterHBasePrincipalName = inputLine.replace("\"hbase_principal_name\" :", "").replace("\"", "").replace(",", "").trim();
            }
        }
        bReader.close();
        
        System.out.println("\n*** clusterHBasePrincipalName: " + clusterHBasePrincipalName );
	}

}
