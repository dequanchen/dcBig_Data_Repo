package dcModelClasses;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 05/31/2014; 5/5/2017 
*/ 

public class ULServerCommandFactory {
	private String serverOS = "";
	private String serverURI = "";
	private String username = "";
	private String passwordLessPuttyKeyFile = "";
	private String rootPassword = "";
	private String bdClusterName = "";
	private String bdClusterIdName = "";
	private String bdClusterF5ConnStr = "";

	//Constructor#1a
	public ULServerCommandFactory(String aServerOS, String aServerURI, String aUsername, String aPasswordLessPuttyKeyFileOrRootPassword) {
		this.serverOS = aServerOS.toUpperCase();
		this.serverURI = aServerURI.toLowerCase();
		this.username = aUsername;
		this.passwordLessPuttyKeyFile = aPasswordLessPuttyKeyFileOrRootPassword;
		this.rootPassword = aPasswordLessPuttyKeyFileOrRootPassword;
		if (this.serverURI.contains("hdpr03")){
			this.bdClusterName = "BDDev1";
			this.bdClusterIdName = "MAYOHADOOPDEV1";
			this.bdClusterF5ConnStr = "https://bigdataknox-dev.mayo.edu/gateway/MAYOHADOOPDEV1";
		}
		if (this.serverURI.contains("hdpr05")){
			this.bdClusterName = "BDDev3";
			this.bdClusterIdName = "MAYOHADOOPDEV3";
			this.bdClusterF5ConnStr = "https://bigdata.mayo.edu/hdp/DEV3/knox";
		}
		
		
		if (this.serverURI.contains("hdpr02")){
			//this.bdClusterName = "BDInt"; //Prior to cluster conversion: BDInt or BDTest1 ==> BDProd2
			this.bdClusterName = "BDProd2"; //Post cluster conversion: BDInt or BDTest1 ==> BDProd2
			//this.bdClusterKnoxIdName = "MAYOHADOOPTEST1";	//Prior to cluster conversion: BDInt or BDTest1 ==> BDProd2
			this.bdClusterIdName = "MAYOHADOOPPROD2";
			this.bdClusterF5ConnStr = "https://bigdataknox.mayo.edu/gateway/MAYOHADOOPPROD2";
		}		
		if (this.serverURI.contains("hdpr07")){			
			this.bdClusterName = "BDProd3"; 
			this.bdClusterIdName = "MAYOHADOOPPROD3";
			this.bdClusterF5ConnStr = "https://bigdata.mayo.edu/hdp/PROD3/knox";
		}
		
		
		if (this.serverURI.contains("hdpr01")){
			//this.bdClusterName = "BDTest2"; //Prior to cluster conversion: BDProd1 ==> BDTest2 or BDInt2
			this.bdClusterName = "BDTest2";   //Post cluster conversion: BDProd1 ==> BDTest2 or BDInt2
			this.bdClusterIdName = "MAYOHADOOPTEST2";
			this.bdClusterF5ConnStr = "https://bigdataknox.mayo.edu/gateway/MAYOHADOOPTEST2";
		}
		if (this.serverURI.contains("hdpr06")){
			this.bdClusterName = "BDTest3";
			this.bdClusterIdName = "MAYOHADOOPTEST3";
			this.bdClusterF5ConnStr = "https://bigdata.mayo.edu/hdp/TEST3/knox";
		}
		
		
		
		if (this.serverURI.contains("hdpr04")){
			this.bdClusterName = "BDSdbx";
			this.bdClusterIdName = "MAYOHADOOPSNB1";
			this.bdClusterF5ConnStr = "https://bigdataknox-dev.mayo.edu/gateway/MAYOHADOOPSNB1";
		}		
		
	}
	
			
	//Constructor#2	
	public ULServerCommandFactory(String[] serverConnParamenters){
		this.serverOS = serverConnParamenters[0].toUpperCase();
		this.serverURI = serverConnParamenters[1].toLowerCase();
		this.username = serverConnParamenters[2];
		this.passwordLessPuttyKeyFile = serverConnParamenters[3];	
		this.rootPassword = serverConnParamenters[3];	
		if (this.serverURI.contains("hdpr03")){
			this.bdClusterName = "BDDev1";
			this.bdClusterIdName = "MAYOHADOOPDEV1";
			this.bdClusterF5ConnStr = "https://bigdataknox-dev.mayo.edu/gateway/MAYOHADOOPDEV1";
		}
		if (this.serverURI.contains("hdpr05")){
			this.bdClusterName = "BDDev3";
			this.bdClusterIdName = "MAYOHADOOPDEV3";
			this.bdClusterF5ConnStr = "https://bigdata.mayo.edu/hdp/DEV3/knox";
		}
		
		
		if (this.serverURI.contains("hdpr02")){
			//this.bdClusterName = "BDInt"; //Prior to cluster conversion: BDInt or BDTest1 ==> BDProd2
			this.bdClusterName = "BDProd2"; //Post cluster conversion: BDInt or BDTest1 ==> BDProd2
			//this.bdClusterKnoxIdName = "MAYOHADOOPTEST1";	//Prior to cluster conversion: BDInt or BDTest1 ==> BDProd2
			this.bdClusterIdName = "MAYOHADOOPPROD2";
			this.bdClusterF5ConnStr = "https://bigdataknox.mayo.edu/gateway/MAYOHADOOPPROD2";
		}		
		if (this.serverURI.contains("hdpr07")){			
			this.bdClusterName = "BDProd3"; 
			this.bdClusterIdName = "MAYOHADOOPPROD3";
			this.bdClusterF5ConnStr = "https://bigdata.mayo.edu/hdp/PROD3/knox";
		}
		
		
		if (this.serverURI.contains("hdpr01")){
			//this.bdClusterName = "BDTest2"; //Prior to cluster conversion: BDProd1 ==> BDTest2 or BDInt2
			this.bdClusterName = "BDTest2";   //Post cluster conversion: BDProd1 ==> BDTest2 or BDInt2
			this.bdClusterIdName = "MAYOHADOOPTEST2";
			this.bdClusterF5ConnStr = "https://bigdataknox.mayo.edu/gateway/MAYOHADOOPTEST2";
		}
		if (this.serverURI.contains("hdpr06")){
			this.bdClusterName = "BDTest3";
			this.bdClusterIdName = "MAYOHADOOPTEST3";
			this.bdClusterF5ConnStr = "https://bigdata.mayo.edu/hdp/TEST3/knox";
		}
		
		
		
		if (this.serverURI.contains("hdpr04")){
			this.bdClusterName = "BDSdbx";
			this.bdClusterIdName = "MAYOHADOOPSNB1";
			this.bdClusterF5ConnStr = "https://bigdataknox-dev.mayo.edu/gateway/MAYOHADOOPSNB1";
		}		
				
	}
	
	public String getPuttyScriptCommandString(String puttyScriptFilePathAndName) {
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String puttyExeFilePath = currConfigureBDResource.getPuttyExeFileInstalledPathAndName();
		
		// ... cmd /c cmd.exe /C ""C:\Program Files (x86)\PuTTY\plink.exe" -ssh hdpr02en01.mayo.edu -l root -pw root4int "su - hdfs; hadoop fs -rmr -skipTrash /data/amalgahistory/cnote/2009/5/2""
		//String puttyCommand_L = "cmd /c cmd.exe /K \"\"" + puttyExeFilePath + "\" -ssh " + this.serverURI + " -l " + this.username + " -pw " + this.password;
		
		// ... cmd /c cmd.exe /C ""C:\Program Files (x86)\PuTTY\plink.exe" -ssh hdpr05en01.mayo.edu -l wa00336 -i "C:\BD\wa00336Key\id_rsa_wa00336.ppk" "ls -la /data/home/wa00336""
		String puttyCommand_L = "cmd /c cmd.exe /C \"\"" + puttyExeFilePath + "\" -ssh " + this.serverURI + " -l " + this.username + " -i \"" + this.passwordLessPuttyKeyFile + "\"";
		String puttyCommand_M  = " -m \"" + puttyScriptFilePathAndName + "\"";
		String puttyCommand_R = " && exit && exit\"\"";
		String fullPuttyCmd = puttyCommand_L + puttyCommand_M + puttyCommand_R;
		
		return fullPuttyCmd;			    
	}//end getPuttyScriptCommandString
	
	public String getPlinkSingleCommandString(String singleComandStr, String reDirectingToOutput) {
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String plinkExeFilePath = currConfigureBDResource.getPlinkExeFileInstalledPathAndName();
		
		//String plinkCommand_L = "cmd /c cmd.exe /C \"\"" + plinkExeFilePath + "\" -ssh " + this.serverURI + " -l " + this.username + " -pw " + this.password;
		//String plinkCommand_L = "runas /profile /user:Administrator /savecred \"cmd /c cmd.exe \"" + plinkExeFilePath + "\" -ssh " + this.serverURI + " -l " + this.username + " -pw " + this.password;
		
		String plinkCommand_L = "cmd /c cmd.exe /C \"\"" + plinkExeFilePath + "\" -ssh " + this.serverURI + " -l " + this.username + " -i \"" + this.passwordLessPuttyKeyFile + "\"";
		String plinkCommand_M  = " \"" + singleComandStr;
		String plinkCommand_R = "";
		if (reDirectingToOutput.isEmpty()){
			plinkCommand_R = " && exit && exit\"\"";
			//plinkCommand_R = " && exit && exit\"\"\"";
		} else {
			plinkCommand_R = " " + reDirectingToOutput +"\"\"";
			//plinkCommand_R = " " + reDirectingToOutput +"\"\"\"";
		}
	
		String fullplinkCmd = plinkCommand_L + plinkCommand_M + plinkCommand_R;
		
		return fullplinkCmd;			    
	}//end getPlinkSingleCommandString
		
	public String getWindowsLocalFileCopyCommandString(String winLocalElseFilePathAndName, String winLocalFilePathAndName) {		
		String pscpCommand_L = "cmd /c cmd.exe /C \"";
		String pscpCommand_M  = "COPY \"" + winLocalElseFilePathAndName + "\" \"" + winLocalFilePathAndName + "\"";
				
		String pscpCommand_R = " && exit && exit\"";
		String fullPscpCmd = pscpCommand_L + pscpCommand_M + pscpCommand_R;
		
		return fullPscpCmd;			    
	}//end getWindowsLocalFileCopyCommandString
	
	public String getPscpFileCopyToServerCommandString(String winLocalFilePathAndName, String serverFilePathAndName) {		
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String pscpExeFilePath = currConfigureBDResource.getPscpExeFileInstalledPathAndName();
		
		//String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -pw " + this.password;
		String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -i \"" + this.passwordLessPuttyKeyFile + "\"";
		String pscpCommand_M  = "";
		if (winLocalFilePathAndName.contains("&")){
			winLocalFilePathAndName = winLocalFilePathAndName.replaceAll("&", "^&");
			pscpCommand_M  = " " + winLocalFilePathAndName + " " + this.username + "@" + this.serverURI + ":\"" + serverFilePathAndName + "\"";
			
		} else {
			pscpCommand_M  = " \"" + winLocalFilePathAndName + "\" " + this.username + "@" + this.serverURI + ":\"" + serverFilePathAndName + "\"";
		}		
		
		String pscpCommand_R = " && exit && exit\"";
		String fullPscpCmd = pscpCommand_L + pscpCommand_M + pscpCommand_R;
		
		return fullPscpCmd;			    
	}//end getPscpFileCopyToServerCommandString
	
	public String getPscpFolderCopyToServerCommandString(String localFolderPathAndName, String serverFolderPathAndName) {
		localFolderPathAndName = localFolderPathAndName.replace("/", "\\");
		serverFolderPathAndName = serverFolderPathAndName.replace("\\", "/");
		if (!localFolderPathAndName.endsWith("\\")){
			localFolderPathAndName += "\\";
		}
		if (localFolderPathAndName.endsWith("\\")){
			localFolderPathAndName += "*";
		}
				
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String pscpExeFilePath = currConfigureBDResource.getPscpExeFileInstalledPathAndName();
		
		//cmd.exe ... C:\\Windows\\System32\\cmd.exe	
		//String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -r -scp -pw " + this.password;
		String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -r -scp -i \"" + this.passwordLessPuttyKeyFile + "\"";
		
		String pscpCommand_M  = "";
		if (localFolderPathAndName.contains("&")){
			localFolderPathAndName = localFolderPathAndName.replaceAll("&", "^&");
			pscpCommand_M  = " " + localFolderPathAndName + " " + this.username + "@" + this.serverURI + ":\"" + serverFolderPathAndName + "\"";
		} else {
			pscpCommand_M  = " \"" + localFolderPathAndName + "\" " + this.username + "@" + this.serverURI + ":\"" + serverFolderPathAndName + "\"";
		}
		
		String pscpCommand_R = " && exit && exit\"";
		String fullPscpCmd = pscpCommand_L + pscpCommand_M + pscpCommand_R;
		
		return fullPscpCmd;			    
	}//end getPscpFolderCopyToServerCommandString
	
	
	public String getPscpFileCopyToLocalCommandString(String serverFilePathAndName, String winLocalFilePathAndName) {
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String pscpExeFilePath = currConfigureBDResource.getPscpExeFileInstalledPathAndName();
		
		//String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -pw " + this.password;		
		String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -i \"" + this.passwordLessPuttyKeyFile + "\"";
		
		String pscpCommand_M  = "";
		if (winLocalFilePathAndName.contains("&")){
			winLocalFilePathAndName = winLocalFilePathAndName.replaceAll("&", "^&");
			pscpCommand_M  = " " + this.username + "@" + this.serverURI + ":\"" + serverFilePathAndName + "\" " + winLocalFilePathAndName + "";
			
		} else {
			pscpCommand_M  = " " + this.username + "@" + this.serverURI + ":\"" + serverFilePathAndName + "\" \"" + winLocalFilePathAndName + "\"";
		}
		
		String pscpCommand_R = " && exit && exit\"";
		String fullPscpCmd = pscpCommand_L + pscpCommand_M + pscpCommand_R;
		
		return fullPscpCmd;			    
	}//end getPscpFileCopyToLocalCommandString
	
	public String getPscpFolderCopyToLocalCommandString(String serverFolderPathAndName, String localFolderPathAndName) {
		localFolderPathAndName = localFolderPathAndName.replace("/", "\\");
		serverFolderPathAndName = serverFolderPathAndName.replace("\\", "/");
		if (!localFolderPathAndName.endsWith("\\")){
			localFolderPathAndName += "\\";
		}
		if (localFolderPathAndName.endsWith("\\")){
			localFolderPathAndName += ".";
		}
				
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String pscpExeFilePath = currConfigureBDResource.getPscpExeFileInstalledPathAndName();
		
		//cmd.exe ... C:\\Windows\\System32\\cmd.exe
		//String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -r -scp -pw " + this.password;
		String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -r -scp -i \"" + this.passwordLessPuttyKeyFile + "\"";
		
		String pscpCommand_M  = "";
		if (localFolderPathAndName.contains("&")){
			localFolderPathAndName = localFolderPathAndName.replaceAll("&", "^&");
			pscpCommand_M  = " " + this.username + "@" + this.serverURI + ":\"" + serverFolderPathAndName + "\" " + localFolderPathAndName + "";
			
		} else {
			pscpCommand_M  = " " + this.username + "@" + this.serverURI + ":\"" + serverFolderPathAndName + "\" \"" + localFolderPathAndName + "\"";
			
		}
		
		String pscpCommand_R = " && exit && exit\"";
		String fullPscpCmd = pscpCommand_L + pscpCommand_M + pscpCommand_R;
		
		return fullPscpCmd;			    
	}//end getPscpFileCopyToLocalCommandString
	
	
	public String getServerURI() {
		return serverURI;
	}

	public void setServerURI(String serverURI) {
		this.serverURI = serverURI;
	}
	
	public String getServerOS() {
		return serverOS;
	}

	public void setServerOS(String serverOS) {
		this.serverOS = serverOS;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordLessPuttyKeyFile() {
		return passwordLessPuttyKeyFile;
	}

	public void setPasswordLessPuttyKeyFile(String passwordLessPuttyKeyFile) {
		this.passwordLessPuttyKeyFile = passwordLessPuttyKeyFile;
	}

	public String getRootPassword() {
		return rootPassword;
	}

	public void setRootPassword(String rootPassword) {
		this.rootPassword = rootPassword;
	}
	
	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public String getBdClusterIdName() {
		return bdClusterIdName;
	}

	public void setBdClusterIdName(String bdClusterIdName) {
		this.bdClusterIdName = bdClusterIdName;
	}

	public String getBdClusterF5ConnStr() {
		return bdClusterF5ConnStr;
	}

	public void setBdClusterF5ConnStr(String bdClusterF5ConnStr) {
		this.bdClusterF5ConnStr = bdClusterF5ConnStr;
	}
	
	
}//end class
