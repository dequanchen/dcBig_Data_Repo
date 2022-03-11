package HoldFiles;


/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 05/31/2014; 2/14/2017 
*/ 

public class ConfigureULWResources {	
	private String puttyExeFileInstalledPathAndName = "InstalledFullPath\\putty.exe";
	private String plinkExeFileInstalledPathAndName = "InstalledFullPath\\plink.exe";
	private String pscpExeFileInstalledPathAndName = "InstalledFullPath\\pscp.exe";
	
	private String[] bdKnoxNode01RootConParameters = {"LinuxOSVersionServicePackInfo", "kx01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdKnoxNode02RootConParameters = {"LinuxOSVersionServicePackInfo", "kx02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdKnoxNode03RootConParameters = {"LinuxOSVersionServicePackInfo", "kx03-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdKnoxNode04RootConParameters = {"LinuxOSVersionServicePackInfo", "kx04-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};	
	
	private String[] bdKnoxNode01ConParameters = {"LinuxOSVersionServicePackInfo", "kx01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdKnoxNode02ConParameters = {"LinuxOSVersionServicePackInfo", "kx02-FQDN", "LoginUNxxxxxx", "pwxxxxxx???"};
	private String[] bdKnoxNode03ConParameters = {"LinuxOSVersionServicePackInfo", "kx03-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdKnoxNode04ConParameters = {"LinuxOSVersionServicePackInfo", "kx04-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	
	
	private String[] bdDevAllNodesRootConParameters = {"LinuxOSVersionServicePackInfo", "", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdDevAllNodesNonRootConParameters = {"LinuxOSVersionServicePackInfo", "", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdIntAllNodesRootConParameters = {"LinuxOSVersionServicePackInfo", "", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdIntAllNodesNonRootConParameters = {"LinuxOSVersionServicePackInfo", "", "LoginUNxxxxxx", "pwxxxxxx"};
	
	private String[] bdProdAllNodesRootConParameters = {"LinuxOSVersionServicePackInfo", "", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdAllNodesNonRootConParameters = {"LinuxOSVersionServicePackInfo", "", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdSdbxAllNodesRootConParameters = {"LinuxOSVersionServicePackInfo", "", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxAllNodesNonRootConParameters = {"LinuxOSVersionServicePackInfo", "", "LoginUNxxxxxx", "pwxxxxxx"};
	
	private String[] bdDevEN01RootConParameters = {"LinuxOSVersionServicePackInfo", "deven01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdDevMN01RootConParameters = {"LinuxOSVersionServicePackInfo", "devmn01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdDevDN01RootConParameters = {"LinuxOSVersionServicePackInfo", "devdn01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdDevDN02RootConParameters = {"LinuxOSVersionServicePackInfo", "devdn02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdDevDN03RootConParameters = {"LinuxOSVersionServicePackInfo", "devdn03-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdDevEN02RootConParameters = {"LinuxOSVersionServicePackInfo", "deven02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdDevMN02RootConParameters = {"LinuxOSVersionServicePackInfo", "devmn02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdDevDN04RootConParameters = {"LinuxOSVersionServicePackInfo", "devdn04-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdDevDN05RootConParameters = {"LinuxOSVersionServicePackInfo", "devdn05-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	
	private String[] bdDevEN01ConParameters = {"LinuxOSVersionServicePackInfo", "deven01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdDevMN01ConParameters = {"LinuxOSVersionServicePackInfo", "devmn01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdDevDN01ConParameters = {"LinuxOSVersionServicePackInfo", "devdn01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdDevDN02ConParameters = {"LinuxOSVersionServicePackInfo", "devdn02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdDevDN03ConParameters = {"LinuxOSVersionServicePackInfo", "devdn03-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdDevEN02ConParameters = {"LinuxOSVersionServicePackInfo", "deven02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdDevMN02ConParameters = {"LinuxOSVersionServicePackInfo", "devmn02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdDevDN04ConParameters = {"LinuxOSVersionServicePackInfo", "devdn04-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdDevDN05ConParameters = {"LinuxOSVersionServicePackInfo", "devdn05-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	 	
	private String[] bdIntEN01RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2en01-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"}; 
	private String[] bdIntEN02RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2en02-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};	
	private String[] bdIntEN03RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2en03-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntEN04RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2en04-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntEN05RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2en05-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntEN06RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2en06-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};	
	private String[] bdIntMN01RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2mn01-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntMN02RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2mn02-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};	
	private String[] bdIntDN01RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn01-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntDN02RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn02-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntDN03RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn03-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntDN04RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn04-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntDN05RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn05-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntDN06RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn06-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntDN07RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn07-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntDN08RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn08-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntDN09RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn09-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	private String[] bdIntDN10RootConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn10-FQDN", "rootUNxxxxxxx", "pwxxxxxx,"};
	
	private String[] bdIntEN01ConParameters = {"LinuxOSVersionServicePackInfo", "prod2en01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntEN02ConParameters = {"LinuxOSVersionServicePackInfo", "prod2en02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdIntEN03ConParameters = {"LinuxOSVersionServicePackInfo", "prod2en03-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntEN04ConParameters = {"LinuxOSVersionServicePackInfo", "prod2en04-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntEN05ConParameters = {"LinuxOSVersionServicePackInfo", "prod2en05-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntEN06ConParameters = {"LinuxOSVersionServicePackInfo", "prod2en06-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdIntMN01ConParameters = {"LinuxOSVersionServicePackInfo", "prod2mn01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntMN02ConParameters = {"LinuxOSVersionServicePackInfo", "prod2mn02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdIntDN01ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntDN02ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntDN03ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn03-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntDN04ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn04-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntDN05ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn05-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntDN06ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn06-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntDN07ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn07-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntDN08ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn08-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntDN09ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn09-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdIntDN10ConParameters = {"LinuxOSVersionServicePackInfo", "prod2dn10-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	

	private String[] bdProdEN01RootConParameters = {"LinuxOSVersionServicePackInfo", "test2en01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"}; //56rtdfxc..pwxxxxxx
	private String[] bdProdEN02RootConParameters = {"LinuxOSVersionServicePackInfo", "test2en02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdProdEN03RootConParameters = {"LinuxOSVersionServicePackInfo", "test2en03-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdEN04RootConParameters = {"LinuxOSVersionServicePackInfo", "test2en04-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdProdEN05RootConParameters = {"LinuxOSVersionServicePackInfo", "test2en05-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdProdMN01RootConParameters = {"LinuxOSVersionServicePackInfo", "test2mn01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdMN02RootConParameters = {"LinuxOSVersionServicePackInfo", "test2mn02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdProdDN01RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN02RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN03RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn03-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN04RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn04-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN05RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn05-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN06RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn06-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN07RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn07-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN08RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn08-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN09RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn09-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN10RootConParameters = {"LinuxOSVersionServicePackInfo", "test2dn10-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	
	private String[] bdProdEN01ConParameters = {"LinuxOSVersionServicePackInfo", "test2en01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdEN02ConParameters = {"LinuxOSVersionServicePackInfo", "test2en02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdProdEN03ConParameters = {"LinuxOSVersionServicePackInfo", "test2en03-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdEN04ConParameters = {"LinuxOSVersionServicePackInfo", "test2en04-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdProdEN05ConParameters = {"LinuxOSVersionServicePackInfo", "test2en05-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdProdMN01ConParameters = {"LinuxOSVersionServicePackInfo", "test2mn01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdMN02ConParameters = {"LinuxOSVersionServicePackInfo", "test2mn02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdProdDN01ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN02ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN03ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn03-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN04ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn04-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN05ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn05-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN06ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn06-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN07ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn07-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN08ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn08-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN09ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn09-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdProdDN10ConParameters = {"LinuxOSVersionServicePackInfo", "test2dn10-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	

	private String[] bdSdbxEN01RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxen01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"}; 
	private String[] bdSdbxEN02RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxen02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdSdbxEN03RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxen03-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxEN04RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxen04-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdSdbxMN01RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxmn01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxMN02RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxmn02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};	
	private String[] bdSdbxDN01RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn01-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN02RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn02-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN03RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn03-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN04RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn04-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN05RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn05-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN06RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn06-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN07RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn07-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN08RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn08-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN09RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn09-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN10RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn10-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN11RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn11-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN12RootConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn12-FQDN", "rootUNxxxxxxx", "pwxxxxxx"};
	
	private String[] bdSdbxEN01ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxen01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"}; 
	private String[] bdSdbxEN02ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxen02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdSdbxEN03ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxen03-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxEN04ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxen04-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdSdbxMN01ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxmn01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxMN02ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxmn02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
	private String[] bdSdbxDN01ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn01-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN02ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn02-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN03ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn03-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN04ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn04-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN05ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn05-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN06ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn06-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN07ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn07-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN08ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn08-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN09ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn09-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN10ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn10-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN11ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn11-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};
	private String[] bdSdbxDN12ConParameters = {"LinuxOSVersionServicePackInfo", "sandboxdn12-FQDN", "LoginUNxxxxxx", "pwxxxxxx"};	
 
//Default Constructor
  public ConfigureULWResources() {
     // ...
	 //code to get all the properties components from a secured silo or database
  }//end Default Constructor 

  
  
//-----------------------------------------------------------------------------------------
  //The following are getters and setters 
  
public String getPuttyExeFileInstalledPathAndName() {
	return puttyExeFileInstalledPathAndName;
}

public void setPuttyExeFileInstalledPathAndName(
		String puttyExeFileInstalledPathAndName) {
	this.puttyExeFileInstalledPathAndName = puttyExeFileInstalledPathAndName;
}

public String getPlinkExeFileInstalledPathAndName() {
	return plinkExeFileInstalledPathAndName;
}

public void setPlinkExeFileInstalledPathAndName(
		String plinkExeFileInstalledPathAndName) {
	this.plinkExeFileInstalledPathAndName = plinkExeFileInstalledPathAndName;
}

public String getPscpExeFileInstalledPathAndName() {
	return pscpExeFileInstalledPathAndName;
}

public void setPscpExeFileInstalledPathAndName(
		String pscpExeFileInstalledPathAndName) {
	this.pscpExeFileInstalledPathAndName = pscpExeFileInstalledPathAndName;
}

public String[] getBdDevEN01ConParameters() {
	return bdDevEN01ConParameters;
}

public void setBdDevEN01ConParameters(String[] bdDevEN01ConParameters) {
	this.bdDevEN01ConParameters = bdDevEN01ConParameters;
}

public String[] getBdDevMN01ConParameters() {
	return bdDevMN01ConParameters;
}

public void setBdDevMN01ConParameters(String[] bdDevMN01ConParameters) {
	this.bdDevMN01ConParameters = bdDevMN01ConParameters;
}

public String[] getBdDevDN01ConParameters() {
	return bdDevDN01ConParameters;
}


public String[] getBdDevDN04ConParameters() {
	return bdDevDN04ConParameters;
}



public void setBdDevDN04ConParameters(String[] bdDevDN04ConParameters) {
	this.bdDevDN04ConParameters = bdDevDN04ConParameters;
}



public String[] getBdDevDN05ConParameters() {
	return bdDevDN05ConParameters;
}



public void setBdDevDN05ConParameters(String[] bdDevDN05ConParameters) {
	this.bdDevDN05ConParameters = bdDevDN05ConParameters;
}



public void setBdDevDN01ConParameters(String[] bdDevDN01ConParameters) {
	this.bdDevDN01ConParameters = bdDevDN01ConParameters;
}

public String[] getBdDevDN02ConParameters() {
	return bdDevDN02ConParameters;
}

public void setBdDevDN02ConParameters(String[] bdDevDN02ConParameters) {
	this.bdDevDN02ConParameters = bdDevDN02ConParameters;
}

public String[] getBdDevDN03ConParameters() {
	return bdDevDN03ConParameters;
}

public void setBdDevDN03ConParameters(String[] bdDevDN03ConParameters) {
	this.bdDevDN03ConParameters = bdDevDN03ConParameters;
}

public String[] getBdIntEN01ConParameters() {
	return bdIntEN01ConParameters;
}

public void setBdIntEN01ConParameters(String[] bdIntEN01ConParameters) {
	this.bdIntEN01ConParameters = bdIntEN01ConParameters;
}

public String[] getBdIntEN02ConParameters() {
	return bdIntEN02ConParameters;
}

public void setBdIntEN02ConParameters(String[] bdIntEN02ConParameters) {
	this.bdIntEN02ConParameters = bdIntEN02ConParameters;
}

public String[] getBdIntEN03ConParameters() {
	return bdIntEN03ConParameters;
}

public void setBdIntEN03ConParameters(String[] bdIntEN03ConParameters) {
	this.bdIntEN03ConParameters = bdIntEN03ConParameters;
}

public String[] getBdIntEN04ConParameters() {
	return bdIntEN04ConParameters;
}

public void setBdIntEN04ConParameters(String[] bdIntEN04ConParameters) {
	this.bdIntEN04ConParameters = bdIntEN04ConParameters;
}

public String[] getBdIntEN05ConParameters() {
	return bdIntEN05ConParameters;
}

public void setBdIntEN05ConParameters(String[] bdIntEN05ConParameters) {
	this.bdIntEN05ConParameters = bdIntEN05ConParameters;
}

public String[] getBdIntEN06ConParameters() {
	return bdIntEN06ConParameters;
}

public void setBdIntEN06ConParameters(String[] bdIntEN06ConParameters) {
	this.bdIntEN06ConParameters = bdIntEN06ConParameters;
}

public String[] getBdIntMN01ConParameters() {
	return bdIntMN01ConParameters;
}

public void setBdIntMN01ConParameters(String[] bdIntMN01ConParameters) {
	this.bdIntMN01ConParameters = bdIntMN01ConParameters;
}

public String[] getBdIntMN02ConParameters() {
	return bdIntMN02ConParameters;
}

public void setBdIntMN02ConParameters(String[] bdIntMN02ConParameters) {
	this.bdIntMN02ConParameters = bdIntMN02ConParameters;
}

public String[] getBdIntDN01ConParameters() {
	return bdIntDN01ConParameters;
}

public void setBdIntDN01ConParameters(String[] bdIntDN01ConParameters) {
	this.bdIntDN01ConParameters = bdIntDN01ConParameters;
}

public String[] getBdIntDN02ConParameters() {
	return bdIntDN02ConParameters;
}

public void setBdIntDN02ConParameters(String[] bdIntDN02ConParameters) {
	this.bdIntDN02ConParameters = bdIntDN02ConParameters;
}

public String[] getBdIntDN03ConParameters() {
	return bdIntDN03ConParameters;
}

public void setBdIntDN03ConParameters(String[] bdIntDN03ConParameters) {
	this.bdIntDN03ConParameters = bdIntDN03ConParameters;
}

public String[] getBdIntDN04ConParameters() {
	return bdIntDN04ConParameters;
}

public void setBdIntDN04ConParameters(String[] bdIntDN04ConParameters) {
	this.bdIntDN04ConParameters = bdIntDN04ConParameters;
}

public String[] getBdIntDN05ConParameters() {
	return bdIntDN05ConParameters;
}

public void setBdIntDN05ConParameters(String[] bdIntDN05ConParameters) {
	this.bdIntDN05ConParameters = bdIntDN05ConParameters;
}

public String[] getBdIntDN06ConParameters() {
	return bdIntDN06ConParameters;
}

public void setBdIntDN06ConParameters(String[] bdIntDN06ConParameters) {
	this.bdIntDN06ConParameters = bdIntDN06ConParameters;
}

public String[] getBdIntDN07ConParameters() {
	return bdIntDN07ConParameters;
}

public void setBdIntDN07ConParameters(String[] bdIntDN07ConParameters) {
	this.bdIntDN07ConParameters = bdIntDN07ConParameters;
}

public String[] getBdIntDN08ConParameters() {
	return bdIntDN08ConParameters;
}

public void setBdIntDN08ConParameters(String[] bdIntDN08ConParameters) {
	this.bdIntDN08ConParameters = bdIntDN08ConParameters;
}

public String[] getBdIntDN09ConParameters() {
	return bdIntDN09ConParameters;
}

public void setBdIntDN09ConParameters(String[] bdIntDN09ConParameters) {
	this.bdIntDN09ConParameters = bdIntDN09ConParameters;
}

public String[] getBdIntDN10ConParameters() {
	return bdIntDN10ConParameters;
}

public void setBdIntDN10ConParameters(String[] bdIntDN10ConParameters) {
	this.bdIntDN10ConParameters = bdIntDN10ConParameters;
}

public String[] getBdProdEN01ConParameters() {
	return bdProdEN01ConParameters;
}

public void setBdProdEN01ConParameters(String[] bdProdEN01ConParameters) {
	this.bdProdEN01ConParameters = bdProdEN01ConParameters;
}

public String[] getBdProdEN02ConParameters() {
	return bdProdEN02ConParameters;
}

public void setBdProdEN02ConParameters(String[] bdProdEN02ConParameters) {
	this.bdProdEN02ConParameters = bdProdEN02ConParameters;
}

public String[] getBdProdEN03ConParameters() {
	return bdProdEN03ConParameters;
}

public void setBdProdEN03ConParameters(String[] bdProdEN03ConParameters) {
	this.bdProdEN03ConParameters = bdProdEN03ConParameters;
}

public String[] getBdProdEN04ConParameters() {
	return bdProdEN04ConParameters;
}

public void setBdProdEN04ConParameters(String[] bdProdEN04ConParameters) {
	this.bdProdEN04ConParameters = bdProdEN04ConParameters;
}

public String[] getBdProdEN05ConParameters() {
	return bdProdEN05ConParameters;
}

public void setBdProdEN05ConParameters(String[] bdProdEN05ConParameters) {
	this.bdProdEN05ConParameters = bdProdEN05ConParameters;
}

public String[] getBdProdMN01ConParameters() {
	return bdProdMN01ConParameters;
}

public void setBdProdMN01ConParameters(String[] bdProdMN01ConParameters) {
	this.bdProdMN01ConParameters = bdProdMN01ConParameters;
}

public String[] getBdProdMN02ConParameters() {
	return bdProdMN02ConParameters;
}

public void setBdProdMN02ConParameters(String[] bdProdMN02ConParameters) {
	this.bdProdMN02ConParameters = bdProdMN02ConParameters;
}

public String[] getBdProdDN01ConParameters() {
	return bdProdDN01ConParameters;
}

public void setBdProdDN01ConParameters(String[] bdProdDN01ConParameters) {
	this.bdProdDN01ConParameters = bdProdDN01ConParameters;
}

public String[] getBdProdDN02ConParameters() {
	return bdProdDN02ConParameters;
}

public void setBdProdDN02ConParameters(String[] bdProdDN02ConParameters) {
	this.bdProdDN02ConParameters = bdProdDN02ConParameters;
}

public String[] getBdProdDN03ConParameters() {
	return bdProdDN03ConParameters;
}

public void setBdProdDN03ConParameters(String[] bdProdDN03ConParameters) {
	this.bdProdDN03ConParameters = bdProdDN03ConParameters;
}

public String[] getBdProdDN04ConParameters() {
	return bdProdDN04ConParameters;
}

public void setBdProdDN04ConParameters(String[] bdProdDN04ConParameters) {
	this.bdProdDN04ConParameters = bdProdDN04ConParameters;
}

public String[] getBdProdDN05ConParameters() {
	return bdProdDN05ConParameters;
}

public void setBdProdDN05ConParameters(String[] bdProdDN05ConParameters) {
	this.bdProdDN05ConParameters = bdProdDN05ConParameters;
}

public String[] getBdProdDN06ConParameters() {
	return bdProdDN06ConParameters;
}

public void setBdProdDN06ConParameters(String[] bdProdDN06ConParameters) {
	this.bdProdDN06ConParameters = bdProdDN06ConParameters;
}

public String[] getBdProdDN07ConParameters() {
	return bdProdDN07ConParameters;
}

public void setBdProdDN07ConParameters(String[] bdProdDN07ConParameters) {
	this.bdProdDN07ConParameters = bdProdDN07ConParameters;
}

public String[] getBdProdDN08ConParameters() {
	return bdProdDN08ConParameters;
}

public void setBdProdDN08ConParameters(String[] bdProdDN08ConParameters) {
	this.bdProdDN08ConParameters = bdProdDN08ConParameters;
}

public String[] getBdProdDN09ConParameters() {
	return bdProdDN09ConParameters;
}

public void setBdProdDN09ConParameters(String[] bdProdDN09ConParameters) {
	this.bdProdDN09ConParameters = bdProdDN09ConParameters;
}

public String[] getBdProdDN10ConParameters() {
	return bdProdDN10ConParameters;
}

public void setBdProdDN10ConParameters(String[] bdProdDN10ConParameters) {
	this.bdProdDN10ConParameters = bdProdDN10ConParameters;
}

public String[] getBdSdbxEN01ConParameters() {
	return bdSdbxEN01ConParameters;
}

public void setBdSdbxEN01ConParameters(String[] bdSdbxEN01ConParameters) {
	this.bdSdbxEN01ConParameters = bdSdbxEN01ConParameters;
}

public String[] getBdSdbxEN02ConParameters() {
	return bdSdbxEN02ConParameters;
}

public void setBdSdbxEN02ConParameters(String[] bdSdbxEN02ConParameters) {
	this.bdSdbxEN02ConParameters = bdSdbxEN02ConParameters;
}

public String[] getBdSdbxEN03ConParameters() {
	return bdSdbxEN03ConParameters;
}

public void setBdSdbxEN03ConParameters(String[] bdSdbxEN03ConParameters) {
	this.bdSdbxEN03ConParameters = bdSdbxEN03ConParameters;
}

public String[] getBdSdbxEN04ConParameters() {
	return bdSdbxEN04ConParameters;
}

public void setBdSdbxEN04ConParameters(String[] bdSdbxEN04ConParameters) {
	this.bdSdbxEN04ConParameters = bdSdbxEN04ConParameters;
}

public String[] getBdSdbxMN01ConParameters() {
	return bdSdbxMN01ConParameters;
}

public void setBdSdbxMN01ConParameters(String[] bdSdbxMN01ConParameters) {
	this.bdSdbxMN01ConParameters = bdSdbxMN01ConParameters;
}

public String[] getBdSdbxMN02ConParameters() {
	return bdSdbxMN02ConParameters;
}

public void setBdSdbxMN02ConParameters(String[] bdSdbxMN02ConParameters) {
	this.bdSdbxMN02ConParameters = bdSdbxMN02ConParameters;
}

public String[] getBdSdbxDN01ConParameters() {
	return bdSdbxDN01ConParameters;
}

public void setBdSdbxDN01ConParameters(String[] bdSdbxDN01ConParameters) {
	this.bdSdbxDN01ConParameters = bdSdbxDN01ConParameters;
}

public String[] getBdSdbxDN02ConParameters() {
	return bdSdbxDN02ConParameters;
}

public void setBdSdbxDN02ConParameters(String[] bdSdbxDN02ConParameters) {
	this.bdSdbxDN02ConParameters = bdSdbxDN02ConParameters;
}

public String[] getBdSdbxDN03ConParameters() {
	return bdSdbxDN03ConParameters;
}

public void setBdSdbxDN03ConParameters(String[] bdSdbxDN03ConParameters) {
	this.bdSdbxDN03ConParameters = bdSdbxDN03ConParameters;
}

public String[] getBdSdbxDN04ConParameters() {
	return bdSdbxDN04ConParameters;
}

public void setBdSdbxDN04ConParameters(String[] bdSdbxDN04ConParameters) {
	this.bdSdbxDN04ConParameters = bdSdbxDN04ConParameters;
}

public String[] getBdSdbxDN05ConParameters() {
	return bdSdbxDN05ConParameters;
}

public void setBdSdbxDN05ConParameters(String[] bdSdbxDN05ConParameters) {
	this.bdSdbxDN05ConParameters = bdSdbxDN05ConParameters;
}

public String[] getBdSdbxDN06ConParameters() {
	return bdSdbxDN06ConParameters;
}

public void setBdSdbxDN06ConParameters(String[] bdSdbxDN06ConParameters) {
	this.bdSdbxDN06ConParameters = bdSdbxDN06ConParameters;
}

public String[] getBdSdbxDN07ConParameters() {
	return bdSdbxDN07ConParameters;
}

public void setBdSdbxDN07ConParameters(String[] bdSdbxDN07ConParameters) {
	this.bdSdbxDN07ConParameters = bdSdbxDN07ConParameters;
}

public String[] getBdSdbxDN08ConParameters() {
	return bdSdbxDN08ConParameters;
}

public void setBdSdbxDN08ConParameters(String[] bdSdbxDN08ConParameters) {
	this.bdSdbxDN08ConParameters = bdSdbxDN08ConParameters;
}

public String[] getBdSdbxDN09ConParameters() {
	return bdSdbxDN09ConParameters;
}

public void setBdSdbxDN09ConParameters(String[] bdSdbxDN09ConParameters) {
	this.bdSdbxDN09ConParameters = bdSdbxDN09ConParameters;
}

public String[] getBdSdbxDN10ConParameters() {
	return bdSdbxDN10ConParameters;
}

public void setBdSdbxDN10ConParameters(String[] bdSdbxDN10ConParameters) {
	this.bdSdbxDN10ConParameters = bdSdbxDN10ConParameters;
}

public String[] getBdSdbxDN11ConParameters() {
	return bdSdbxDN11ConParameters;
}

public void setBdSdbxDN11ConParameters(String[] bdSdbxDN11ConParameters) {
	this.bdSdbxDN11ConParameters = bdSdbxDN11ConParameters;
}

public String[] getBdSdbxDN12ConParameters() {
	return bdSdbxDN12ConParameters;
}

public void setBdSdbxDN12ConParameters(String[] bdSdbxDN12ConParameters) {
	this.bdSdbxDN12ConParameters = bdSdbxDN12ConParameters;
}

public String[] getBdDevEN02ConParameters() {
	return bdDevEN02ConParameters;
}

public void setBdDevEN02ConParameters(String[] bdDevEN02ConParameters) {
	this.bdDevEN02ConParameters = bdDevEN02ConParameters;
}

public String[] getBdDevMN02ConParameters() {
	return bdDevMN02ConParameters;
}

public void setBdDevMN02ConParameters(String[] bdDevMN02ConParameters) {
	this.bdDevMN02ConParameters = bdDevMN02ConParameters;
}

public String[] getBdKnoxNode01ConParameters() {
	return bdKnoxNode01ConParameters;
}

public void setBdKnoxNode01ConParameters(String[] bdKnoxNode01ConParameters) {
	this.bdKnoxNode01ConParameters = bdKnoxNode01ConParameters;
}

public String[] getBdKnoxNode03ConParameters() {
	return bdKnoxNode03ConParameters;
}

public void setBdKnoxNode03ConParameters(String[] bdKnoxNode03ConParameters) {
	this.bdKnoxNode03ConParameters = bdKnoxNode03ConParameters;
}

public String[] getBdKnoxNode04ConParameters() {
	return bdKnoxNode04ConParameters;
}

public void setBdKnoxNode04ConParameters(String[] bdKnoxNode04ConParameters) {
	this.bdKnoxNode04ConParameters = bdKnoxNode04ConParameters;
}

public String[] getBdKnoxNode02ConParameters() {
	return bdKnoxNode02ConParameters;
}

public void setBdKnoxNode02ConParameters(String[] bdKnoxNode02ConParameters) {
	this.bdKnoxNode02ConParameters = bdKnoxNode02ConParameters;
}

public String[] getBdDevEN01RootConParameters() {
	return bdDevEN01RootConParameters;
}

public void setBdDevEN01RootConParameters(String[] bdDevEN01RootConParameters) {
	this.bdDevEN01RootConParameters = bdDevEN01RootConParameters;
}

public String[] getBdDevMN01RootConParameters() {
	return bdDevMN01RootConParameters;
}

public void setBdDevMN01RootConParameters(String[] bdDevMN01RootConParameters) {
	this.bdDevMN01RootConParameters = bdDevMN01RootConParameters;
}

public String[] getBdDevDN01RootConParameters() {
	return bdDevDN01RootConParameters;
}

public void setBdDevDN01RootConParameters(String[] bdDevDN01RootConParameters) {
	this.bdDevDN01RootConParameters = bdDevDN01RootConParameters;
}

public String[] getBdDevDN02RootConParameters() {
	return bdDevDN02RootConParameters;
}

public void setBdDevDN02RootConParameters(String[] bdDevDN02RootConParameters) {
	this.bdDevDN02RootConParameters = bdDevDN02RootConParameters;
}

public String[] getBdDevDN03RootConParameters() {
	return bdDevDN03RootConParameters;
}

public void setBdDevDN03RootConParameters(String[] bdDevDN03RootConParameters) {
	this.bdDevDN03RootConParameters = bdDevDN03RootConParameters;
}

public String[] getBdDevEN02RootConParameters() {
	return bdDevEN02RootConParameters;
}

public void setBdDevEN02RootConParameters(String[] bdDevEN02RootConParameters) {
	this.bdDevEN02RootConParameters = bdDevEN02RootConParameters;
}

public String[] getBdDevMN02RootConParameters() {
	return bdDevMN02RootConParameters;
}

public void setBdDevMN02RootConParameters(String[] bdDevMN02RootConParameters) {
	this.bdDevMN02RootConParameters = bdDevMN02RootConParameters;
}

public String[] getBdDevDN04RootConParameters() {
	return bdDevDN04RootConParameters;
}

public void setBdDevDN04RootConParameters(String[] bdDevDN04RootConParameters) {
	this.bdDevDN04RootConParameters = bdDevDN04RootConParameters;
}

public String[] getBdDevDN05RootConParameters() {
	return bdDevDN05RootConParameters;
}

public void setBdDevDN05RootConParameters(String[] bdDevDN05RootConParameters) {
	this.bdDevDN05RootConParameters = bdDevDN05RootConParameters;
}

public String[] getBdKnoxNode01RootConParameters() {
	return bdKnoxNode01RootConParameters;
}

public void setBdKnoxNode01RootConParameters(String[] bdKnoxNode01RootConParameters) {
	this.bdKnoxNode01RootConParameters = bdKnoxNode01RootConParameters;
}

public String[] getBdKnoxNode03RootConParameters() {
	return bdKnoxNode03RootConParameters;
}

public void setBdKnoxNode03RootConParameters(String[] bdKnoxNode03RootConParameters) {
	this.bdKnoxNode03RootConParameters = bdKnoxNode03RootConParameters;
}

public String[] getBdKnoxNode04RootConParameters() {
	return bdKnoxNode04RootConParameters;
}

public void setBdKnoxNode04RootConParameters(String[] bdKnoxNode04RootConParameters) {
	this.bdKnoxNode04RootConParameters = bdKnoxNode04RootConParameters;
}

public String[] getBdKnoxNode02RootConParameters() {
	return bdKnoxNode02RootConParameters;
}

public void setBdKnoxNode02RootConParameters(String[] bdKnoxNode02RootConParameters) {
	this.bdKnoxNode02RootConParameters = bdKnoxNode02RootConParameters;
}

public String[] getBdIntEN01RootConParameters() {
	return bdIntEN01RootConParameters;
}

public void setBdIntEN01RootConParameters(String[] bdIntEN01RootConParameters) {
	this.bdIntEN01RootConParameters = bdIntEN01RootConParameters;
}

public String[] getBdIntEN02RootConParameters() {
	return bdIntEN02RootConParameters;
}

public void setBdIntEN02RootConParameters(String[] bdIntEN02RootConParameters) {
	this.bdIntEN02RootConParameters = bdIntEN02RootConParameters;
}

public String[] getBdIntEN03RootConParameters() {
	return bdIntEN03RootConParameters;
}

public void setBdIntEN03RootConParameters(String[] bdIntEN03RootConParameters) {
	this.bdIntEN03RootConParameters = bdIntEN03RootConParameters;
}

public String[] getBdIntEN04RootConParameters() {
	return bdIntEN04RootConParameters;
}

public void setBdIntEN04RootConParameters(String[] bdIntEN04RootConParameters) {
	this.bdIntEN04RootConParameters = bdIntEN04RootConParameters;
}

public String[] getBdIntEN05RootConParameters() {
	return bdIntEN05RootConParameters;
}

public void setBdIntEN05RootConParameters(String[] bdIntEN05RootConParameters) {
	this.bdIntEN05RootConParameters = bdIntEN05RootConParameters;
}

public String[] getBdIntEN06RootConParameters() {
	return bdIntEN06RootConParameters;
}

public void setBdIntEN06RootConParameters(String[] bdIntEN06RootConParameters) {
	this.bdIntEN06RootConParameters = bdIntEN06RootConParameters;
}

public String[] getBdIntMN01RootConParameters() {
	return bdIntMN01RootConParameters;
}

public void setBdIntMN01RootConParameters(String[] bdIntMN01RootConParameters) {
	this.bdIntMN01RootConParameters = bdIntMN01RootConParameters;
}

public String[] getBdIntMN02RootConParameters() {
	return bdIntMN02RootConParameters;
}

public void setBdIntMN02RootConParameters(String[] bdIntMN02RootConParameters) {
	this.bdIntMN02RootConParameters = bdIntMN02RootConParameters;
}

public String[] getBdIntDN01RootConParameters() {
	return bdIntDN01RootConParameters;
}

public void setBdIntDN01RootConParameters(String[] bdIntDN01RootConParameters) {
	this.bdIntDN01RootConParameters = bdIntDN01RootConParameters;
}

public String[] getBdIntDN02RootConParameters() {
	return bdIntDN02RootConParameters;
}

public void setBdIntDN02RootConParameters(String[] bdIntDN02RootConParameters) {
	this.bdIntDN02RootConParameters = bdIntDN02RootConParameters;
}

public String[] getBdIntDN03RootConParameters() {
	return bdIntDN03RootConParameters;
}

public void setBdIntDN03RootConParameters(String[] bdIntDN03RootConParameters) {
	this.bdIntDN03RootConParameters = bdIntDN03RootConParameters;
}

public String[] getBdIntDN04RootConParameters() {
	return bdIntDN04RootConParameters;
}

public void setBdIntDN04RootConParameters(String[] bdIntDN04RootConParameters) {
	this.bdIntDN04RootConParameters = bdIntDN04RootConParameters;
}

public String[] getBdIntDN05RootConParameters() {
	return bdIntDN05RootConParameters;
}

public void setBdIntDN05RootConParameters(String[] bdIntDN05RootConParameters) {
	this.bdIntDN05RootConParameters = bdIntDN05RootConParameters;
}

public String[] getBdIntDN06RootConParameters() {
	return bdIntDN06RootConParameters;
}

public void setBdIntDN06RootConParameters(String[] bdIntDN06RootConParameters) {
	this.bdIntDN06RootConParameters = bdIntDN06RootConParameters;
}

public String[] getBdIntDN07RootConParameters() {
	return bdIntDN07RootConParameters;
}

public void setBdIntDN07RootConParameters(String[] bdIntDN07RootConParameters) {
	this.bdIntDN07RootConParameters = bdIntDN07RootConParameters;
}

public String[] getBdIntDN08RootConParameters() {
	return bdIntDN08RootConParameters;
}

public void setBdIntDN08RootConParameters(String[] bdIntDN08RootConParameters) {
	this.bdIntDN08RootConParameters = bdIntDN08RootConParameters;
}

public String[] getBdIntDN09RootConParameters() {
	return bdIntDN09RootConParameters;
}

public void setBdIntDN09RootConParameters(String[] bdIntDN09RootConParameters) {
	this.bdIntDN09RootConParameters = bdIntDN09RootConParameters;
}

public String[] getBdIntDN10RootConParameters() {
	return bdIntDN10RootConParameters;
}

public void setBdIntDN10RootConParameters(String[] bdIntDN10RootConParameters) {
	this.bdIntDN10RootConParameters = bdIntDN10RootConParameters;
}

public String[] getBdProdEN01RootConParameters() {
	return bdProdEN01RootConParameters;
}

public void setBdProdEN01RootConParameters(String[] bdProdEN01RootConParameters) {
	this.bdProdEN01RootConParameters = bdProdEN01RootConParameters;
}

public String[] getBdProdEN02RootConParameters() {
	return bdProdEN02RootConParameters;
}

public void setBdProdEN02RootConParameters(String[] bdProdEN02RootConParameters) {
	this.bdProdEN02RootConParameters = bdProdEN02RootConParameters;
}

public String[] getBdProdEN03RootConParameters() {
	return bdProdEN03RootConParameters;
}

public void setBdProdEN03RootConParameters(String[] bdProdEN03RootConParameters) {
	this.bdProdEN03RootConParameters = bdProdEN03RootConParameters;
}

public String[] getBdProdEN04RootConParameters() {
	return bdProdEN04RootConParameters;
}

public void setBdProdEN04RootConParameters(String[] bdProdEN04RootConParameters) {
	this.bdProdEN04RootConParameters = bdProdEN04RootConParameters;
}

public String[] getBdProdEN05RootConParameters() {
	return bdProdEN05RootConParameters;
}

public void setBdProdEN05RootConParameters(String[] bdProdEN05RootConParameters) {
	this.bdProdEN05RootConParameters = bdProdEN05RootConParameters;
}

public String[] getBdProdMN01RootConParameters() {
	return bdProdMN01RootConParameters;
}

public void setBdProdMN01RootConParameters(String[] bdProdMN01RootConParameters) {
	this.bdProdMN01RootConParameters = bdProdMN01RootConParameters;
}

public String[] getBdProdMN02RootConParameters() {
	return bdProdMN02RootConParameters;
}

public void setBdProdMN02RootConParameters(String[] bdProdMN02RootConParameters) {
	this.bdProdMN02RootConParameters = bdProdMN02RootConParameters;
}

public String[] getBdProdDN01RootConParameters() {
	return bdProdDN01RootConParameters;
}

public void setBdProdDN01RootConParameters(String[] bdProdDN01RootConParameters) {
	this.bdProdDN01RootConParameters = bdProdDN01RootConParameters;
}

public String[] getBdProdDN02RootConParameters() {
	return bdProdDN02RootConParameters;
}

public void setBdProdDN02RootConParameters(String[] bdProdDN02RootConParameters) {
	this.bdProdDN02RootConParameters = bdProdDN02RootConParameters;
}

public String[] getBdProdDN03RootConParameters() {
	return bdProdDN03RootConParameters;
}

public void setBdProdDN03RootConParameters(String[] bdProdDN03RootConParameters) {
	this.bdProdDN03RootConParameters = bdProdDN03RootConParameters;
}

public String[] getBdProdDN04RootConParameters() {
	return bdProdDN04RootConParameters;
}

public void setBdProdDN04RootConParameters(String[] bdProdDN04RootConParameters) {
	this.bdProdDN04RootConParameters = bdProdDN04RootConParameters;
}

public String[] getBdProdDN05RootConParameters() {
	return bdProdDN05RootConParameters;
}

public void setBdProdDN05RootConParameters(String[] bdProdDN05RootConParameters) {
	this.bdProdDN05RootConParameters = bdProdDN05RootConParameters;
}

public String[] getBdProdDN06RootConParameters() {
	return bdProdDN06RootConParameters;
}

public void setBdProdDN06RootConParameters(String[] bdProdDN06RootConParameters) {
	this.bdProdDN06RootConParameters = bdProdDN06RootConParameters;
}

public String[] getBdProdDN07RootConParameters() {
	return bdProdDN07RootConParameters;
}

public void setBdProdDN07RootConParameters(String[] bdProdDN07RootConParameters) {
	this.bdProdDN07RootConParameters = bdProdDN07RootConParameters;
}

public String[] getBdProdDN08RootConParameters() {
	return bdProdDN08RootConParameters;
}

public void setBdProdDN08RootConParameters(String[] bdProdDN08RootConParameters) {
	this.bdProdDN08RootConParameters = bdProdDN08RootConParameters;
}

public String[] getBdProdDN09RootConParameters() {
	return bdProdDN09RootConParameters;
}

public void setBdProdDN09RootConParameters(String[] bdProdDN09RootConParameters) {
	this.bdProdDN09RootConParameters = bdProdDN09RootConParameters;
}

public String[] getBdProdDN10RootConParameters() {
	return bdProdDN10RootConParameters;
}

public void setBdProdDN10RootConParameters(String[] bdProdDN10RootConParameters) {
	this.bdProdDN10RootConParameters = bdProdDN10RootConParameters;
}

public String[] getBdSdbxEN01RootConParameters() {
	return bdSdbxEN01RootConParameters;
}

public void setBdSdbxEN01RootConParameters(String[] bdSdbxEN01RootConParameters) {
	this.bdSdbxEN01RootConParameters = bdSdbxEN01RootConParameters;
}

public String[] getBdSdbxEN02RootConParameters() {
	return bdSdbxEN02RootConParameters;
}

public void setBdSdbxEN02RootConParameters(String[] bdSdbxEN02RootConParameters) {
	this.bdSdbxEN02RootConParameters = bdSdbxEN02RootConParameters;
}

public String[] getBdSdbxEN03RootConParameters() {
	return bdSdbxEN03RootConParameters;
}

public void setBdSdbxEN03RootConParameters(String[] bdSdbxEN03RootConParameters) {
	this.bdSdbxEN03RootConParameters = bdSdbxEN03RootConParameters;
}

public String[] getBdSdbxEN04RootConParameters() {
	return bdSdbxEN04RootConParameters;
}

public void setBdSdbxEN04RootConParameters(String[] bdSdbxEN04RootConParameters) {
	this.bdSdbxEN04RootConParameters = bdSdbxEN04RootConParameters;
}

public String[] getBdSdbxMN01RootConParameters() {
	return bdSdbxMN01RootConParameters;
}

public void setBdSdbxMN01RootConParameters(String[] bdSdbxMN01RootConParameters) {
	this.bdSdbxMN01RootConParameters = bdSdbxMN01RootConParameters;
}

public String[] getBdSdbxMN02RootConParameters() {
	return bdSdbxMN02RootConParameters;
}

public void setBdSdbxMN02RootConParameters(String[] bdSdbxMN02RootConParameters) {
	this.bdSdbxMN02RootConParameters = bdSdbxMN02RootConParameters;
}

public String[] getBdSdbxDN01RootConParameters() {
	return bdSdbxDN01RootConParameters;
}

public void setBdSdbxDN01RootConParameters(String[] bdSdbxDN01RootConParameters) {
	this.bdSdbxDN01RootConParameters = bdSdbxDN01RootConParameters;
}

public String[] getBdSdbxDN02RootConParameters() {
	return bdSdbxDN02RootConParameters;
}

public void setBdSdbxDN02RootConParameters(String[] bdSdbxDN02RootConParameters) {
	this.bdSdbxDN02RootConParameters = bdSdbxDN02RootConParameters;
}

public String[] getBdSdbxDN03RootConParameters() {
	return bdSdbxDN03RootConParameters;
}

public void setBdSdbxDN03RootConParameters(String[] bdSdbxDN03RootConParameters) {
	this.bdSdbxDN03RootConParameters = bdSdbxDN03RootConParameters;
}

public String[] getBdSdbxDN04RootConParameters() {
	return bdSdbxDN04RootConParameters;
}

public void setBdSdbxDN04RootConParameters(String[] bdSdbxDN04RootConParameters) {
	this.bdSdbxDN04RootConParameters = bdSdbxDN04RootConParameters;
}

public String[] getBdSdbxDN05RootConParameters() {
	return bdSdbxDN05RootConParameters;
}

public void setBdSdbxDN05RootConParameters(String[] bdSdbxDN05RootConParameters) {
	this.bdSdbxDN05RootConParameters = bdSdbxDN05RootConParameters;
}

public String[] getBdSdbxDN06RootConParameters() {
	return bdSdbxDN06RootConParameters;
}

public void setBdSdbxDN06RootConParameters(String[] bdSdbxDN06RootConParameters) {
	this.bdSdbxDN06RootConParameters = bdSdbxDN06RootConParameters;
}

public String[] getBdSdbxDN07RootConParameters() {
	return bdSdbxDN07RootConParameters;
}

public void setBdSdbxDN07RootConParameters(String[] bdSdbxDN07RootConParameters) {
	this.bdSdbxDN07RootConParameters = bdSdbxDN07RootConParameters;
}

public String[] getBdSdbxDN08RootConParameters() {
	return bdSdbxDN08RootConParameters;
}

public void setBdSdbxDN08RootConParameters(String[] bdSdbxDN08RootConParameters) {
	this.bdSdbxDN08RootConParameters = bdSdbxDN08RootConParameters;
}

public String[] getBdSdbxDN09RootConParameters() {
	return bdSdbxDN09RootConParameters;
}

public void setBdSdbxDN09RootConParameters(String[] bdSdbxDN09RootConParameters) {
	this.bdSdbxDN09RootConParameters = bdSdbxDN09RootConParameters;
}

public String[] getBdSdbxDN10RootConParameters() {
	return bdSdbxDN10RootConParameters;
}

public void setBdSdbxDN10RootConParameters(String[] bdSdbxDN10RootConParameters) {
	this.bdSdbxDN10RootConParameters = bdSdbxDN10RootConParameters;
}

public String[] getBdSdbxDN11RootConParameters() {
	return bdSdbxDN11RootConParameters;
}

public void setBdSdbxDN11RootConParameters(String[] bdSdbxDN11RootConParameters) {
	this.bdSdbxDN11RootConParameters = bdSdbxDN11RootConParameters;
}

public String[] getBdSdbxDN12RootConParameters() {
	return bdSdbxDN12RootConParameters;
}

public void setBdSdbxDN12RootConParameters(String[] bdSdbxDN12RootConParameters) {
	this.bdSdbxDN12RootConParameters = bdSdbxDN12RootConParameters;
}

public String[] getBdDevAllNodesRootConParameters() {
	return bdDevAllNodesRootConParameters;
}

public void setBdDevAllNodesRootConParameters(
		String[] bdDevAllNodesRootConParameters) {
	this.bdDevAllNodesRootConParameters = bdDevAllNodesRootConParameters;
}

public String[] getBdDevAllNodesNonRootConParameters() {
	return bdDevAllNodesNonRootConParameters;
}

public void setBdDevAllNodesNonRootConParameters(
		String[] bdDevAllNodesNonRootConParameters) {
	this.bdDevAllNodesNonRootConParameters = bdDevAllNodesNonRootConParameters;
}

public String[] getBdIntAllNodesRootConParameters() {
	return bdIntAllNodesRootConParameters;
}

public void setBdIntAllNodesRootConParameters(
		String[] bdIntAllNodesRootConParameters) {
	this.bdIntAllNodesRootConParameters = bdIntAllNodesRootConParameters;
}

public String[] getBdIntAllNodesNonRootConParameters() {
	return bdIntAllNodesNonRootConParameters;
}

public void setBdIntAllNodesNonRootConParameters(
		String[] bdIntAllNodesNonRootConParameters) {
	this.bdIntAllNodesNonRootConParameters = bdIntAllNodesNonRootConParameters;
}

public String[] getBdProdAllNodesRootConParameters() {
	return bdProdAllNodesRootConParameters;
}

public void setBdProdAllNodesRootConParameters(
		String[] bdProdAllNodesRootConParameters) {
	this.bdProdAllNodesRootConParameters = bdProdAllNodesRootConParameters;
}

public String[] getBdProdAllNodesNonRootConParameters() {
	return bdProdAllNodesNonRootConParameters;
}

public void setBdProdAllNodesNonRootConParameters(
		String[] bdProdAllNodesNonRootConParameters) {
	this.bdProdAllNodesNonRootConParameters = bdProdAllNodesNonRootConParameters;
}

public String[] getBdSdbxAllNodesRootConParameters() {
	return bdSdbxAllNodesRootConParameters;
}

public void setBdSdbxAllNodesRootConParameters(
		String[] bdSdbxAllNodesRootConParameters) {
	this.bdSdbxAllNodesRootConParameters = bdSdbxAllNodesRootConParameters;
}

public String[] getBdSdbxAllNodesNonRootConParameters() {
	return bdSdbxAllNodesNonRootConParameters;
}

public void setBdSdbxAllNodesNonRootConParameters(
		String[] bdSdbxAllNodesNonRootConParameters) {
	this.bdSdbxAllNodesNonRootConParameters = bdSdbxAllNodesNonRootConParameters;
}

//public String[] getHwSdbxNodeConParameters() {
//	return hwSdbxNodeConParameters;
//}
//
//public void setHwSdbxNodeConParameters(String[] hwSdbxNodeConParameters) {
//	this.hwSdbxNodeConParameters = hwSdbxNodeConParameters;
//}


}//end class