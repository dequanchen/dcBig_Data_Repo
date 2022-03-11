package WebHDFSHBaseESTesting_Win.V1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import WebHDFSHBaseESTesting_Win.McCluster;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 3/1/2017
*/ 

public class dcTestWebHDFS_v1 {
	private static String bdClusterName = "BDDev";//BDDev...BDSdbx...BDTest2...BDProd2
	private static String curlExeFilePath = "C:\\Program Files\\curl-7.53.0-win64-mingw\\bin\\curl.exe";
	private static String userName = "m041785";
	private static String userPW = "feeGoo17";
	private static String tgtResult = "104,Brian,Williams,120000,M,NewYork";
	
	public static void main(String[] args) {		
		String curlCmdEceTempResultFilePathAndName = curlExeFilePath.replaceAll("curl.exe", "dcTempCurlExeResult.txt");
		prepareFile_keepOld (curlCmdEceTempResultFilePathAndName, "Preparing file for temp curl cmd execution");
				
		//String curlCmd_R = "-k -u " + userName + ":" + userPW + " https://hdpr01kx03.mayo.edu:8442/gateway/MAYOHADOOPTEST2/hbase/employee_knox_webhbase1/101/cfs:firstName > \"" + curlCmdEceTempResultFilePathAndName + "\"";
		
		McCluster presCluster  = new McCluster (bdClusterName);
		String bdClusterIdName = presCluster.getBdClusterIdName();	
		String f5BalancerIPAddressAndPort = presCluster.getF5BalancerIPAddressAndPort();
		String firstKnoxNodeIPAddressAndPort = presCluster.getFirstKnoxNodeIPAddressAndPort();
		String secondKnoxNodeIPAddressAndPort = presCluster.getSecondKnoxNodeIPAddressAndPort();		
		
		//curl -k -u xxxxxx:yyyyyyy -X GET -L "https://bigdataknox.mayo.edu:8442/gateway/MAYOHADOOPTEST2/webhdfs/v1/user/m041785/test/Knox/employee_webhdfs_curl.txt?op=OPEN"
		String curlCmd_R_F5 = "-k -u " + userName + ":" + userPW + " -X GET -L \"" + f5BalancerIPAddressAndPort + "/gateway/" + bdClusterIdName + "/webhdfs/v1/user/m041785/test/Knox/employee_webhdfs_curl.txt?op=OPEN\" > \"" + curlCmdEceTempResultFilePathAndName + "\"";
		
		String curlCmd_Full_F5 = "\"" + curlExeFilePath + "\" " + curlCmd_R_F5;
		System.out.println("\n--- curlCmd_Full_F5: " + curlCmd_Full_F5);		
		
		
		String winRunCurlCmd_F5 = "cmd /c start cmd.exe /C \"" + curlCmd_Full_F5 + "\"";
		String winRunCurlCmdExeAndStatus_F5 = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_F5, curlCmdEceTempResultFilePathAndName, tgtResult);
		System.out.println("\n--1-- WebHDFS Testing on the F5 Balancer Testing Status: \n\t" + winRunCurlCmdExeAndStatus_F5);
		
		
		String winRunCurlCmd_1stKnox = winRunCurlCmd_F5.replaceAll(f5BalancerIPAddressAndPort, firstKnoxNodeIPAddressAndPort);
		String winRunCurlCmdExeAndStatus_1stKnox = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_1stKnox, curlCmdEceTempResultFilePathAndName, tgtResult);
		System.out.println("\n--2-- WebHDFS Testing on the First Knox Node Testing Status: \n\t" + winRunCurlCmdExeAndStatus_1stKnox);
		
		
		String winRunCurlCmd_2ndKnox = winRunCurlCmd_F5.replaceAll(f5BalancerIPAddressAndPort, secondKnoxNodeIPAddressAndPort);
		String winRunCurlCmdExeAndStatus_2ndKnox = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_2ndKnox, curlCmdEceTempResultFilePathAndName, tgtResult);
		System.out.println("\n--3-- WebHDFS Testing on the Second Knox Node Testing Status: \n\t" + winRunCurlCmdExeAndStatus_2ndKnox);
		

		
	

	}//end main
	
	private static String runCurlCmdAndGetCurlTestStatus (String winRunCurlCmd, String curlCmdEceTempResultFilePathAndName, String tgtResult){		
		Runtime rt = Runtime.getRuntime();
		@SuppressWarnings("unused")
		int exitVal = 10000;
		
		String curlTestStatus = "Failed - Result: Not Contains the Target --- " + tgtResult;
		try {
			Process proc = rt.exec(winRunCurlCmd);
			
			exitVal = proc.waitFor();
//			System.out.println("\n--- winRunCurlCmd: " + winRunCurlCmd);
//			System.out.println("--- Executing winRunCurlCmd exitVal is: " + exitVal);
			
			//Desktop.getDesktop().open(new File(curlCmdEceTempResultFilePathAndName));
			try {
				Thread.sleep(1*1000);
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
			String curlCmdResult = findTargetLineInALocalFile (curlCmdEceTempResultFilePathAndName, tgtResult);
			//System.out.println("\n--- curlCmdResult: " + curlCmdResult);
			
			if (curlCmdResult.contains(tgtResult)){
				curlTestStatus = "Succeeded - Result: Contains the Target --- " + tgtResult;
			}

					            
		} catch (IOException e1) {			
			e1.printStackTrace();
		} catch (InterruptedException e2) {			
			e2.printStackTrace();
		}
		
		return curlTestStatus;		
	}//end runCurlCmdAndGetCurlTestStatus
	
	private static String findTargetLineInALocalFile (String localFilePathAndName, String target){
		String foundLine = "";
		try {
			FileReader aFileReader = new FileReader(localFilePathAndName);
			BufferedReader br = new BufferedReader(aFileReader);
			String line = "";
						
			while ((line = br.readLine()) != null ) {
				if (line.contains(target)){
					foundLine = line;
					break;
				} 
			}//end while			
			br.close();
			aFileReader.close();			
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		return foundLine;
	}//end findTargetLineInALocalFile
	
	private static void prepareFile_keepOld (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
//			if (aFile.exists()){
//				aFile.delete();
//				System.out.println("\n .. Deleted file for dump_Backloading: \n" + localFilePathAndName);
//				aFile.createNewFile();
//				System.out.println("\n .. Created file for dump_Backloading: \n" + localFilePathAndName);
//			}
			
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile

}
