package dcBDAppliance_DownloadRootPermittedFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import WebHDFSHBaseESTesting_Win.McCluster;

public class dcRunCurlTrial {
	//https://ss64.com/nt/cmd.html
	
	private static String bdClusterName = "BDTest2";
	private static String curlExeFilePath = "C:\\Program Files\\curl-7.53.0-win64-mingw\\bin\\curl.exe";
	private static String userName = "m041785";
	private static String userPW = "deehoo17";
	
	public static void main(String[] args) {		
		String curlCmdEceTempResultFilePathAndName = curlExeFilePath.replaceAll("curl.exe", "dcTempCurlExeResult.txt");
		prepareFile_keepOld (curlCmdEceTempResultFilePathAndName, "Preparing file for temp curl cmd execution");
		
		//String curlCmd_R = "-k -u " + userName + ":" + userPW + " https://hdpr01kx03.mayo.edu:8442/gateway/MAYOHADOOPTEST2/hbase/employee_knox_webhbase1/101/cfs:firstName > \"" + curlCmdEceTempResultFilePathAndName + "\"";
		
		McCluster presCluster  = new McCluster (bdClusterName);
		String bdClusterIdName = presCluster.getBdClusterIdName();		
		String firstKnoxNodeIPAddressAndPort = presCluster.getSecondKnoxNodeIPAddressAndPort();		
		String curlCmd_R = "-k -u " + userName + ":" + userPW + " " + firstKnoxNodeIPAddressAndPort + "/gateway/" + bdClusterIdName + "/hbase/employee_knox_webhbase1/101/cfs:firstName > \"" + curlCmdEceTempResultFilePathAndName + "\"";
		
		String curlCmd_Full = "\"" + curlExeFilePath + "\" " + curlCmd_R;
		System.out.println("\n--- curlCmd_Full: " + curlCmd_Full);
		
		
		String winRunCurlCmd = "cmd /c start cmd.exe /C \"" + curlCmd_Full + "\"";
		System.out.println("\n--- winRunCurlCmd: " + winRunCurlCmd);
		
		Runtime rt = Runtime.getRuntime();
		int exitVal = 10000;
		
		try {
			Process proc = rt.exec(winRunCurlCmd);
			
			exitVal = proc.waitFor();
			System.out.println("\n--- current fullPuttyCmd exitVal is: " + exitVal);
			
			//Desktop.getDesktop().open(new File(curlCmdEceTempResultFilePathAndName));
			String curlCmdResult = findTargetLineInALocalFile (curlCmdEceTempResultFilePathAndName, "Joe");
			System.out.println("\n--- curlCmdResult: " + curlCmdResult);

					            
		} catch (IOException e1) {			
			e1.printStackTrace();
		} catch (InterruptedException e2) {			
			e2.printStackTrace();
		}
	

	}//end main
	
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
	}
	
	private static void prepareFile_keepOld (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
			//if (aFile.exists()){
			//	aFile.delete();
			//	System.out.println("\n .. Deleted file for dump_Backloading: \n" + localFilePathAndName);
			//	aFile.createNewFile();
			//	System.out.println("\n .. Created file for dump_Backloading: \n" + localFilePathAndName);
			//}
			
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile

}
