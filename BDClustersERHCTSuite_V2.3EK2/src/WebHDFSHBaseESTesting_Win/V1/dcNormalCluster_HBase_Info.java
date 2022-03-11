package WebHDFSHBaseESTesting_Win.V1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import WebHDFSHBaseESTesting_Win.McCluster;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 5/13/2017
*/ 

public class dcNormalCluster_HBase_Info {
	private static String bdClusterName = "BDDev"; //BDDev...BDSdbx...BDTest2...BDProd2
	private static String curlExeFilePath = "C:\\Program Files\\curl-7.53.0-win64-mingw\\bin\\curl.exe";
	private static String userName = "wa00336";
	private static String userPW = "bnhjui89";
	private static String userKeytabFilePathAndName = "C:\\m041785.keytab";
	private static String userPrincipalName = userName.toUpperCase() + "@MFAD.MFROOT.ORG";
	private static String tgtResult = "Joe";
	
	public static void main(String[] args) {		
		String curlCmdEceTempResultFilePathAndName = curlExeFilePath.replaceAll("curl.exe", "dcTempCurlExeResult.txt");
		prepareFile_keepOld (curlCmdEceTempResultFilePathAndName, "Preparing file for temp curl cmd execution");
		
		//String curlCmd_R = "-k -u " + userName + ":" + userPW + " https://hdpr01kx03.mayo.edu:8442/gateway/MAYOHADOOPTEST2/hbase/employee_knox_webhbase1/101/cfs:firstName > \"" + curlCmdEceTempResultFilePathAndName + "\"";
		
		McCluster presCluster  = new McCluster (bdClusterName);
		String bdClusterIdName = presCluster.getBdClusterIdName();	
		String f5BalancerIPAddressAndPort = presCluster.getF5BalancerIPAddressAndPort();
		
		String firstHBaseMasterIPAddressAndPort = presCluster.getFirstHBaseMasterIPAddressAndPort();
		String secondHBaseMasterIPAddressAndPort = presCluster.getSecondHBaseMasterIPAddressAndPort();
		System.out.println("\n--- firstHBaseMasterIPAddressAndPort: " + firstHBaseMasterIPAddressAndPort);
		System.out.println("\n--- secondHBaseMasterIPAddressAndPort: " + secondHBaseMasterIPAddressAndPort);
		
//		String curlCmd_L_F5 = "\"" + curlExeFilePath + "\" ";
//		String curlCmd_M_F5 = "-k -u " + userName + ":" + userPW + " " + f5BalancerIPAddressAndPort + "/gateway/" + bdClusterIdName + "/hbase/";
//		String curlCmd_R_F5 =  "employee_knox_webhbase1/101/cfs:firstName > \"" + curlCmdEceTempResultFilePathAndName + "\"";
//		
//		
//		String curlCmd_Full_F5 = curlCmd_L_F5 + curlCmd_M_F5 + curlCmd_R_F5;
//		System.out.println("\n--- curlCmd_Full_F5: " + curlCmd_Full_F5);		
//		
//		String winRunCurlCmd_F5 = "cmd /c start cmd.exe /C \"" + curlCmd_Full_F5 + "\"";
//		String winRunCurlCmdExeAndStatus_F5 = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_F5, curlCmdEceTempResultFilePathAndName, tgtResult);
//		System.out.println("\n--1-- WebHbase Testing Result on the F5 Balancer: \n\t" + winRunCurlCmdExeAndStatus_F5);
//		
//		String curlCmd_R_F5_ns = "namespaces > \"" + curlCmdEceTempResultFilePathAndName + "\"";
//		String winRunCurlCmd_F5_ns = winRunCurlCmd_F5.replace(curlCmd_R_F5, curlCmd_R_F5_ns);
//		System.out.println("\n*** : \n\t" + winRunCurlCmd_F5_ns);
//		runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_F5_ns, curlCmdEceTempResultFilePathAndName, tgtResult);
//		try {
//			Desktop.getDesktop().open(new File(curlCmdEceTempResultFilePathAndName));
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}
		
		//hdpr03mn01.mayo.edu:8084/employee1/schema
		//https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/hbase/employee1/schema
		
		//kinit -k -t "C:\m041785.keytab" M041785@MFAD.MFROOT.ORG && curl -k --negotiate -u : -X GET -H "Accept: text/xml" "http://hdpr03mn01.mayo.edu:8084/namespaces/"

		
		
		//kinit -k -t "C:\m041785.keytab" M041785@MFAD.MFROOT.ORG && curl -k --negotiate -u : -H "Accept: text/xml" -X GET  "http://hdpr03mn01.mayo.edu:8084/"		
		//kinit -k -t "C:\m041785.keytab" M041785@MFAD.MFROOT.ORG && curl -k --negotiate -u : -X PUT -H "Accept: text/xml" -H "Content-Type: text/xml" "hdpr03mn01.mayo.edu:8084/employee1/scanner/"
		//curl -k -u m041785:feeGoo17 https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/hbase
		//curl -k -u m041785:feeGoo17 https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/hbase/employee1/schema
		//curl -k -u m041785:feeGoo17 -H "Accept: text/xml" https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/hbase/employee1/schema

	
		
//		String firstKnoxNodeIPAddressAndPort = presCluster.getFirstKnoxNodeIPAddressAndPort();
//		String secondKnoxNodeIPAddressAndPort = presCluster.getSecondKnoxNodeIPAddressAndPort();
//		
//		String winRunCurlCmd_1stKnox = winRunCurlCmd_F5.replaceAll(firstKnoxNodeIPAddressAndPort, secondKnoxNodeIPAddressAndPort);
//		String winRunCurlCmdExeAndStatus_1stKnox = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_1stKnox, curlCmdEceTempResultFilePathAndName, tgtResult);
//		System.out.println("\n--2-- WebHbase Testing Result on the First Knox Node: \n\t" + winRunCurlCmdExeAndStatus_1stKnox);
//		
//		
//		String winRunCurlCmd_2ndKnox = winRunCurlCmd_F5.replaceAll(firstKnoxNodeIPAddressAndPort, f5BalancerIPAddressAndPort);
//		String winRunCurlCmdExeAndStatus_2ndKnox = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_2ndKnox, curlCmdEceTempResultFilePathAndName, tgtResult);
//		System.out.println("\n--3-- WebHbase Testing Result on the Second Knox Node: \n\t" + winRunCurlCmdExeAndStatus_2ndKnox);
//		
//		
//		String firstHBaseMasterIPAddressAndPort = presCluster.getFirstHBaseMasterIPAddressAndPort();
//		String secondHBaseMasterIPAddressAndPort = presCluster.getSecondHBaseMasterIPAddressAndPort();
//		
//		//System.out.println("\n--- firstHBaseMasterIPAddressAndPort: " + firstHBaseMasterIPAddressAndPort);
//		//System.out.println("\n--- secondHBaseMasterIPAddressAndPort: " + secondHBaseMasterIPAddressAndPort);
//		
//		//kinit -k -t C:\m041785.keytab M041785@MFAD.MFROOT.ORG && curl -s --negotiate -u :  http://hdpr04mn01.mayo.edu:8084/employee_webhbase1/101/cfs:firstName
//		String keytabAuthenCmd = "kinit -k -t \"" + userKeytabFilePathAndName + "\" " + userPrincipalName;
//		//System.out.println("\n--- keytabAuthenCmd: " + keytabAuthenCmd);
//		String curlCmd_R_1stStargateHBase = " -s --negotiate -u :  " + firstHBaseMasterIPAddressAndPort + "/employee_knox_webhbase1/101/cfs:firstName > \"" + curlCmdEceTempResultFilePathAndName + "\"";
//		
//		String curlCmd_Full_1stStargateHBase = keytabAuthenCmd + " && \"" + curlExeFilePath + "\" " + curlCmd_R_1stStargateHBase;
//		//System.out.println("\n--- curlCmd_Full_1stStargateHBase: " + curlCmd_Full_1stStargateHBase);		
//		
//		String winRunCurlCmd_1stStargateHBase = "cmd /c start cmd.exe /C \"" + curlCmd_Full_1stStargateHBase + "\"";
//		String winRunCurlCmdExeAndStatus_1stStargateHBase = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_1stStargateHBase, curlCmdEceTempResultFilePathAndName, tgtResult);
//		System.out.println("\n--4-- WebHbase Testing Result on the First Stargate HBase Rest API: \n\t" + winRunCurlCmdExeAndStatus_1stStargateHBase);
//		
//		
//		String winRunCurlCmd_2ndStargateHBase = winRunCurlCmd_1stStargateHBase.replaceAll(firstHBaseMasterIPAddressAndPort, secondHBaseMasterIPAddressAndPort);
//		String winRunCurlCmdExeAndStatus_2ndStargateHBase = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_2ndStargateHBase, curlCmdEceTempResultFilePathAndName, tgtResult);
//		System.out.println("\n--5-- WebHbase Testing Result on the Second Stargate HBase Rest API: \n\t" + winRunCurlCmdExeAndStatus_2ndStargateHBase);
//	

	}//end main
	
	private static String runCurlCmdAndGetCurlTestStatus (String winRunCurlCmd, String curlCmdEceTempResultFilePathAndName, String tgtResult){		
		Runtime rt = Runtime.getRuntime();
		@SuppressWarnings("unused")
		int exitVal = 10000;
		
		String curlTestStatus = "Failed - Result: Not " + tgtResult;
		try {
			Process proc = rt.exec(winRunCurlCmd);
			
			exitVal = proc.waitFor();
			//System.out.println("\n--- winRunCurlCmd: " + winRunCurlCmd);
			//System.out.println("--- Executing winRunCurlCmd exitVal is: " + exitVal);
			
			//Desktop.getDesktop().open(new File(curlCmdEceTempResultFilePathAndName));
			try {
				Thread.sleep(1*1000);
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
			String curlCmdResult = findTargetLineInALocalFile (curlCmdEceTempResultFilePathAndName, tgtResult);
			//System.out.println("\n--- curlCmdResult: " + curlCmdResult);
			
			if (curlCmdResult.contains(tgtResult)){
				curlTestStatus = "Succeeded - Result: " + tgtResult;
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
	
////Methods for creating user keytab file
//kinit -k -t C:\m041785.keytab M041785@MFAD.MFROOT.ORG && curl -s --negotiate -u :  http://hdpr04mn01.mayo.edu:8084/employee_webhbase1/101/cfs:firstName
//
//1. ON BDDev EN01: Create User Keytab file
//ktutil
//ktutil:  addent -password -p M041785@MFAD.MFROOT.ORG -k 1 -e rc4-hmac
//Password for M041785@MFAD.MFROOT.ORG: [enter your password]
//ktutil:  addent -password -p M041785@MFAD.MFROOT.ORG -k 1 -e aes256-cts
//Password for M041785@MFAD.MFROOT.ORG: [enter your password]
//ktutil:  wkt m041785.keytab
//ktutil:  quit
//
//2.Download m041785.keytab from Hadoop Cluster EN01 to Windows as C:\m041785.keytab

}
