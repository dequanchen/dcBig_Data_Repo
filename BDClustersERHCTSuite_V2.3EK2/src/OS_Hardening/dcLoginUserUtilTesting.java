package OS_Hardening;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdNode;

public class dcLoginUserUtilTesting {
	private static String bdClusterName = "BDDev";
	private static String enName = "EN01";

	public static void main(String[] args) throws IOException {
		String winLocalScriptFilesFolder = "C:\\BD\\BD_UAT\\ScriptFiles_BDDev\\temp\\";
		prepareFolder(winLocalScriptFilesFolder, "Folder for Local Experimental Testing Script Files");
		
		BdNode aBDNode = new BdNode(enName, bdClusterName);
		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
		ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
		System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
		System.out.println(" *** bdENRootCmdFactory.getServerURI(): " + bdENRootCmdFactory.getServerURI());
		
		String loginUserName = bdENCmdFactory.getUsername();
		String loginUserPwLessPuttyKeyFile = bdENCmdFactory.getPasswordLessPuttyKeyFile();		
		String rootUserName = bdENRootCmdFactory.getUsername();
		String rootPw = bdENRootCmdFactory.getRootPassword();		
		System.out.println(" *** loginUserName: " + loginUserName);		
		System.out.println(" *** loginUserPwLessPuttyKeyFile: " + loginUserPwLessPuttyKeyFile);
		System.out.println(" *** rootUserName: " + rootUserName);
		System.out.println(" *** rootPw: " + rootPw);
		
		String enServerRootUserFileFolderSingleOpsCmd1 = "echo 'tdc' | su - root -c 'mkdir -p /data/home/.kafka'";
		String enServerRootUserFileFolderSingleOpsCmd2 = "echo 'tdc' | su - root -c 'chmod 750 -R /data/home/.kafka'";
		String enServerRootUserFileFolderSingleOpsCmd3 = "echo 'tdc' | su - root -c 'chown kafka:users -R /data/home/.kafka'";
		String enServerRootUserFileFolderSingleOpsCmd4 = "echo 'tdc' | su - root -c 'rm -r /data/home/.kafka'";
		
		StringBuilder sb = new StringBuilder();
		sb.append(enServerRootUserFileFolderSingleOpsCmd4 + ";\n");
		sb.append(enServerRootUserFileFolderSingleOpsCmd1 + ";\n");
		sb.append(enServerRootUserFileFolderSingleOpsCmd2 + ";\n");
		sb.append(enServerRootUserFileFolderSingleOpsCmd3 + ";\n");
		
		
//		LoginUserUtil.performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(enServerRootUserFileFolderSingleOpsCmd, bdENCmdFactory, rootPw);
//		System.out.print("\n\n*** Done - Running Cmd - " + enServerRootUserFileFolderSingleOpsCmd);
		
		
		String loginUserHomeFolderPathName = "/data/home/m041785/";
		String enServerScriptFileDirectory = loginUserHomeFolderPathName + "test/";
//		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
//		
//		
//		StringBuilder sb = new StringBuilder();
//
//		//sb.append("ssh -t hdpr03en01.mayo.edu -A \"echo \"" + rootPw + "\" | sudo -s echo && sudo su - root\";\n");
//		//sb.append("echo \"" + rootPw + "\" | sudo -s echo && sudo -i;\n");
//		//sb.append("echo \"" + rootPw + "\" | sudo -s echo && sudo bash;\n");
//		//sb.append("sleep 5;\n");
//		//sb.append("echo \"" + rootPw + "\" | sudo -S echo && sudo su -root <<HERE");
////		sb.append("#!/bin/bash\n");
////		sb.append("cd " + enServerScriptFileDirectory + ";\n");
////		sb.append("echo 'new running now...' >" + enServerScriptFileDirectory + "output.txt; \n");	
////		sb.append("{ echo -ne 'Old user is:'; whoami; } >>output.txt; \n");			
////		sb.append("{ echo -ne 'Old pwd is:'; pwd; } >>" + enServerScriptFileDirectory + "output.txt; \n");
////		sb.append("echo ' !!! ' >>" + enServerScriptFileDirectory + "output.txt; \n");
////		
////		sb.append("echo \"tdc\" | sudo -S echo && sudo bash -c \"");
////		sb.append("{ echo -ne 'New user is:' & whoami; } >>" + enServerScriptFileDirectory + "output.txt; \n");
////		sb.append("cd /root;\n");
////		sb.append("{ echo -ne 'New pwd is:' & pwd; } >>" + enServerScriptFileDirectory + "output.txt; \n");
////		sb.append("{ echo -ne 'No sleeling 10 seconds:' & sleep 10; } >>" + enServerScriptFileDirectory + "output.txt\"\n");
//		
//		sb.append("#!/bin/bash\n");
//		sb.append("cd " + enServerScriptFileDirectory + ";\n");
//		sb.append("echo 'new running now...'; \n");	
//		sb.append("echo -ne 'Old user is:'; whoami;\n");			
//		sb.append("echo -ne 'Old pwd is:'; pwd; \n");
//		sb.append("echo ' !!! '; \n");
//		
//		sb.append("echo \"tdc\" | sudo -S echo && sudo su - root;\n");
//		sb.append("echo -ne 'New user is:' & whoami;\n");
//		sb.append("cd /root;\n");
//		sb.append("echo -ne 'New pwd is:' & pwd;\n");
//		sb.append("echo -ne 'Now sleeping 10 seconds:' & sleep 10; \n");
//		
//		
//		//sb.append("echo \"tdc\" | sudo -S echo && sudo bash -c \"echo -ne 'Present user is:'; whoami >>" + enServerScriptFileDirectory + "output.txt; \"\n");	
//		
////		sb.append("sleep 15;\" >" + enServerScriptFileDirectory + "output.txt; \n");	
////		sb.append("HERE\n;");
////		sb.append("exit; \n");
////		sb.append("cat output.txt;");
////		sb.append("sleep 15;\n");
//
//		
		String winLocalScriptFileFullPathAndName = winLocalScriptFilesFolder + "dcTempScript1.sh";
		prepareFile (winLocalScriptFileFullPathAndName,  "Script File For Experimental Testing");
		
		String experimentalTestingCmds = sb.toString();
		writeDataToAFile(winLocalScriptFileFullPathAndName, experimentalTestingCmds, false);		
		sb.setLength(0);
		
			
		LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster (winLocalScriptFileFullPathAndName, 
				winLocalScriptFilesFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
		System.out.print("\n\n*** Done - Running Script for the Cmds - " + enServerRootUserFileFolderSingleOpsCmd1 + "..."); 
		
		//String winLocalSrcFolderName = "C:\\BD\\BD_UAT\\ScriptFiles_BDDev\\pig\\";
		//String winLocalSrcFileName  = "dcTestPig_RelationCreationAndStoringScriptFile_No1.sh";
		//LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerWorkingFolderFullPathAndName, bdENCmdFactory);
		//LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(winLocalSrcFileName,	winLocalSrcFolderName, enServerWorkingFolderFullPathAndName, bdENCmdFactory);
		//String winLocalTgtFolderName = "C:\\BD\\BD_UAT\\ScriptFiles_BDDev\\temp\\";
		//LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster(winLocalSrcFileName, enServerWorkingFolderFullPathAndName, winLocalTgtFolderName, bdENCmdFactory);
					
//		String workingFileFullPathAndName = enServerWorkingFolderFullPathAndName + "/" + "dcTestFile.txt";
//		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerWorkingFolderFullPathAndName, bdENCmdFactory);
//		String loginUserCmd = "touch " + workingFileFullPathAndName;
//		LoginUserUtil.performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserCmd, bdENCmdFactory);
//		
//		LoginUserUtil.activateAnyFile4ExecutionByLoginUser_OnEntryNodeLocal_OnBDCluster(workingFileFullPathAndName, bdENCmdFactory);
//		LoginUserUtil.changeFileOrFolderPermissionInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerWorkingFolderFullPathAndName, "-R 777", bdENCmdFactory);
//		LoginUserUtil.changeFileOrFolderOwnershipInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(workingFileFullPathAndName, "m041785:bduser", bdENCmdFactory);
//		LoginUserUtil.changeFileOrFolderOwnershipInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(workingFileFullPathAndName, "m041785:users", bdENCmdFactory);
//		LoginUserUtil.changeFileOrFolderOwnershipInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerWorkingFolderFullPathAndName, "-R m041785:bduser", bdENCmdFactory);
//		LoginUserUtil.removeFileOrFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(workingFileFullPathAndName, bdENCmdFactory);
//		String loginUserCmd = "mkdir -p " + enServerWorkingFolderFullPathAndName;
//		LoginUserUtil.performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserCmd, bdENCmdFactory);
//		
//		loginUserCmd = "touch " + workingFileFullPathAndName;
//		LoginUserUtil.performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserCmd, bdENCmdFactory);
//		
//		loginUserCmd = "rm -r -f " + workingFileFullPathAndName;
//		LoginUserUtil.performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserCmd, bdENCmdFactory); 
//		
//		loginUserCmd = "rm -r -f " + enServerWorkingFolderFullPathAndName;
//		LoginUserUtil.performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserCmd, bdENCmdFactory);
		

		//Failed  in testing of root user operations through putty
//		String rootUserCmd = "mkdir -p " + enServerWorkingFolderFullPathAndName;
//		LoginUserUtil.performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(rootUserCmd, bdENCmdFactory, rootPw);
		

	}//end main
	
	private static void writeDataToAFile (String recordingFile, String recordInfo, boolean AppendingStatus) throws IOException{
		FileWriter outStream;
		if (AppendingStatus == true){
			outStream = new FileWriter(recordingFile, true);
		} else {
			outStream = new FileWriter(recordingFile);
		}		
		
	    PrintWriter output = new PrintWriter (outStream);
	    output.println(recordInfo);
	    System.out.println(recordInfo);
	    output.close();
	    outStream.close();	    
		
	}//end writeDataToAFile

	private static void prepareFile (String localFilePathAndName, String fileNoticeInfo){
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
	
	private static void prepareFolder(String aNewFolderPathAndName, String purposeInfo){
		File aFolderFile = new File (aNewFolderPathAndName);		
		if (!aFolderFile.exists()){
			aFolderFile.mkdirs();			
			System.out.println("\n .. Created folder for " + purposeInfo + ": \n" + aNewFolderPathAndName); 
		}		
	}//end prepareFolder

}//end class
