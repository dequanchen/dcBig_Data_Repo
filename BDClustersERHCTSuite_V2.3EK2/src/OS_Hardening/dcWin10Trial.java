package OS_Hardening;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import dcModelClasses.ConfigureULWResources;
import dcModelClasses.ULServerCommandFactory;

public class dcWin10Trial {

	public static void main(String[] args) {
		String bdClusterName = "BDDev1";
		try {
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = null; //new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); //getBdProd2EN02ConParameters..getBdTest2EN02ConParameters...getBdProd2EN01ConParameters()
			
			if (bdClusterName.equalsIgnoreCase("BDTest2")
							|| bdClusterName.equalsIgnoreCase("BDPrd")){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDProd2")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDSbx")
					|| bdClusterName.equalsIgnoreCase("BDSdbx")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN01ConParameters()); 
			}
			
			//System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());
			//"http://hdpr03mn01.mayo.edu:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus"
			String nn1StatusQueryURI = "https://" + bdENCommFactory.getServerURI() + ":50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
			
			//nn1StatusQueryURI = "http://dotnetprod/magicra/prestige/";
			System.out.println("\n *** nn1StatusQueryURI: " + nn1StatusQueryURI);
			
			
			URL aWebSiteURL = new URL(nn1StatusQueryURI);	     
	        URLConnection myConn = aWebSiteURL.openConnection();
	        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
	        String inputLine;	        
	        while ((inputLine = bReader.readLine()) != null){
	        	System.out.println(inputLine.trim());
	            if (inputLine.contains("lblAccountID") ){
	            	//System.out.println(inputLine.trim());
	            	//inputLine = inputLine.replace("\"State\" :", "");
	            	//nn1ServiceState = inputLine.replace("\"State\" :", "").replace("\"", "").replace(",", "").trim();
	            }
	        }
	        bReader.close();	        
		} catch (Exception e) {				
			e.printStackTrace();
		}//end try

	}

}
