package dcBDAppliance_InstallJDBCDrivers;

import dcModelClasses.ConfigureULWResources;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 05/31/2014 
*/ 

public class ULServerCommandFactory {
	private String serverOS = "";
	private String serverURI = "";
	private String username = "";
	private String password = "";
	private String bdClusterName = "";

	//Constructor#1
	public ULServerCommandFactory(String aServerOS, String aServerURI, String aUsername, String aPassword) {
		this.serverOS = aServerOS.toUpperCase();
		this.serverURI = aServerURI.toLowerCase();
		this.username = aUsername;
		this.password = aPassword;
		if (this.serverURI.contains("hdpr03")){
			this.bdClusterName = "BDDev";
		}
		if (this.serverURI.contains("hdpr01")){
			this.bdClusterName = "BDProd";
		}
		if (this.serverURI.contains("hdpr02")){
			this.bdClusterName = "BDInt";
		}
		if (this.serverURI.contains("hdpr04")){
			this.bdClusterName = "BDSdbx";
		}
	}
	
	//Constructor#2	
	public ULServerCommandFactory(String[] serverConnParamenters){
		this.serverOS = serverConnParamenters[0].toUpperCase();
		this.serverURI = serverConnParamenters[1].toLowerCase();
		this.username = serverConnParamenters[2];
		this.password = serverConnParamenters[3];	
		if (this.serverURI.contains("hdpr03")){
			this.bdClusterName = "BDDev";
		}
		if (this.serverURI.contains("hdpr01")){
			this.bdClusterName = "BDProd";
		}
		if (this.serverURI.contains("hdpr02")){
			this.bdClusterName = "BDInt";
		}
		if (this.serverURI.contains("hdpr04")){
			this.bdClusterName = "BDSdbx";
		}
	}
	
	public String getPuttyScriptCommandString(String puttyScriptFilePathAndName) {
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String puttyExeFilePath = currConfigureBDResource.getPuttyExeFileInstalledPathAndName();
		
		//String puttyCommand_L = "cmd /c cmd.exe /K \"\"" + puttyExeFilePath + "\" -ssh " + this.serverURI + " -l " + this.username + " -pw " + this.password;
		
		String puttyCommand_L = "cmd /c cmd.exe /C \"\"" + puttyExeFilePath + "\" -ssh " + this.serverURI + " -l " + this.username + " -pw " + this.password;
		String puttyCommand_M  = " -m \"" + puttyScriptFilePathAndName + "\"";
		String puttyCommand_R = " && exit && exit\"\"";
		String fullPuttyCmd = puttyCommand_L + puttyCommand_M + puttyCommand_R;
		
		return fullPuttyCmd;			    
	}//end getPuttyScriptCommandString
	
	public String getPlinkSingleCommandString(String singleComandStr, String reDirectingToOutput) {
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String plinkExeFilePath = currConfigureBDResource.getPlinkExeFileInstalledPathAndName();
		
		String plinkCommand_L = "cmd /c cmd.exe /C \"\"" + plinkExeFilePath + "\" -ssh " + this.serverURI + " -l " + this.username + " -pw " + this.password;
		//String plinkCommand_L = "runas /profile /user:Administrator /savecred \"cmd /c cmd.exe \"" + plinkExeFilePath + "\" -ssh " + this.serverURI + " -l " + this.username + " -pw " + this.password;
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
		
		String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -pw " + this.password;
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
	
	public String getPscpFileCopyToLocalCommandString(String serverFilePathAndName, String winLocalFilePathAndName) {
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String pscpExeFilePath = currConfigureBDResource.getPscpExeFileInstalledPathAndName();
				
		String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -pw " + this.password;
		
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
		ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
		String pscpExeFilePath = currConfigureBDResource.getPscpExeFileInstalledPathAndName();
				
		String pscpCommand_L = "cmd /c cmd.exe /C \"\"" + pscpExeFilePath + "\" -pw " + this.password;
		
		String pscpCommand_M  = "";
		if (localFolderPathAndName.contains("&")){
			localFolderPathAndName = localFolderPathAndName.replaceAll("&", "^&");
			pscpCommand_M  = " -r " + this.username + "@" + this.serverURI + ":\"" + serverFolderPathAndName + "\" " + localFolderPathAndName + "";
			
		} else {
			pscpCommand_M  = " -r " + this.username + "@" + this.serverURI + ":\"" + serverFolderPathAndName + "\" \"" + localFolderPathAndName + "\"";
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}	
	
}//end class
