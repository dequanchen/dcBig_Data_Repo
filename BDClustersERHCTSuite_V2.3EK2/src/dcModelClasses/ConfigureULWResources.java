package dcModelClasses;


/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 05/31/2014; 5/5/2017 
*/ 

public class ConfigureULWResources {
	private String puttyExeFileInstalledPathAndName = "C:\\Program Files (x86)\\PuTTY\\putty.exe";
	//private String puttyExeFileInstalledPathAndName = "C:\\Program Files (x86)\\PuTTY\\PAGEANT.EXE";	
	private String plinkExeFileInstalledPathAndName = "C:\\Program Files (x86)\\PuTTY\\plink.exe";
	private String pscpExeFileInstalledPathAndName = "C:\\Program Files (x86)\\PuTTY\\pscp.exe";
	
	
	String testUserAccountName = "wa00336";  //wa00336...m041785
	String testUserPasswordLessPuttyKeyFilePathAndName = "C:\\Program Files (x86)\\PuTTY\\wa00336\\id_rsa_wa00336.ppk";
	//String testUserNodeLoginPassWd = "changeme1";   //changeme1...xxxxx
	//String bdDev1TestUserNodeLoginPassWd = "changeme1";   
	//String bdDev3TestUserNodeLoginPassWd = "changeme1"; 
	//String bdProd2TestUserNodeLoginPassWd = "changeme1"; 
	//String bdProd3TestUserNodeLoginPassWd = "changeme1"; 
	//String bdTest2TestUserNodeLoginPassWd = "changeme1"; 
	//String bdTest3TestUserNodeLoginPassWd = "changeme1"; 
	//String bdSdbxTestUserNodeLoginPassWd = "changeme1";
	
	String bdDev1RootNodeLoginPassWd = "tdc";   
	String bdDev3RootNodeLoginPassWd = "tdc"; 
	String bdProd2RootNodeLoginPassWd = "23wesdxc"; 
	String bdProd3RootNodeLoginPassWd = "tdc"; 
	String bdTest2RootNodeLoginPassWd = "tdcd1"; 
	String bdTest3RootNodeLoginPassWd = "tdc"; 
	String bdSdbxRootNodeLoginPassWd = "tdc"; 
	
	private String[] bdKnoxNode01RootConParameters = {"SLES11(SP3)", "hdpr01kx01.mayo.edu", "root", bdDev1RootNodeLoginPassWd};
	private String[] bdKnoxNode02RootConParameters = {"SLES11(SP3)", "hdpr01kx02.mayo.edu", "root", bdDev1RootNodeLoginPassWd};
	private String[] bdKnoxNode03RootConParameters = {"SLES11(SP3)", "hdpr01kx03.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdKnoxNode04RootConParameters = {"SLES11(SP3)", "hdpr01kx04.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	
	private String[] bdKnoxNode01ConParameters = {"SLES11(SP3)", "hdpr01kx01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdKnoxNode02ConParameters = {"SLES11(SP3)", "hdpr01kx02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdKnoxNode03ConParameters = {"SLES11(SP3)", "hdpr01kx03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdKnoxNode04ConParameters = {"SLES11(SP3)", "hdpr01kx04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	
	
	private String[] bdDev1AllNodesRootConParameters = {"SLES11(SP3)", "", "root", bdDev1RootNodeLoginPassWd};
	private String[] bdDev1AllNodesNonRootConParameters = {"SLES11(SP3)", "", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd2AllNodesRootConParameters = {"SLES11(SP3)", "", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2AllNodesNonRootConParameters = {"SLES11(SP3)", "", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest2AllNodesRootConParameters = {"SLES11(SP3)", "", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2AllNodesNonRootConParameters = {"SLES11(SP3)", "", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdSdbxAllNodesRootConParameters = {"SLES11(SP3)", "", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxAllNodesNonRootConParameters = {"SLES11(SP3)", "", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	//
	private String[] bdDev3AllNodesRootConParameters = {"SLES11(SP3)", "", "root", bdDev3RootNodeLoginPassWd};
	private String[] bdDev3AllNodesNonRootConParameters = {"SLES11(SP3)", "", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd3AllNodesRootConParameters = {"SLES11(SP3)", "", "root", bdProd3RootNodeLoginPassWd};
	private String[] bdProd3AllNodesNonRootConParameters = {"SLES11(SP3)", "", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest3AllNodesRootConParameters = {"SLES11(SP3)", "", "root", bdTest3RootNodeLoginPassWd};
	private String[] bdTest3AllNodesNonRootConParameters = {"SLES11(SP3)", "", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	
	private String[] bdDev1EN01RootConParameters = {"SLES11(SP3)", "hdpr03en01.mayo.edu", "root", bdDev1RootNodeLoginPassWd};	
	private String[] bdDev1MN01RootConParameters = {"SLES11(SP3)", "hdpr03mn01.mayo.edu", "root", bdDev1RootNodeLoginPassWd};	
	private String[] bdDev1DN01RootConParameters = {"SLES11(SP3)", "hdpr03dn01.mayo.edu", "root", bdDev1RootNodeLoginPassWd};	
	private String[] bdDev1DN02RootConParameters = {"SLES11(SP3)", "hdpr03dn02.mayo.edu", "root", bdDev1RootNodeLoginPassWd};	
	private String[] bdDev1DN03RootConParameters = {"SLES11(SP3)", "hdpr03dn03.mayo.edu", "root", bdDev1RootNodeLoginPassWd};	
	private String[] bdDev1EN02RootConParameters = {"SLES11(SP3)", "hdpr03en02.mayo.edu", "root", bdDev1RootNodeLoginPassWd};	
	private String[] bdDev1MN02RootConParameters = {"SLES11(SP3)", "hdpr03mn02.mayo.edu", "root", bdDev1RootNodeLoginPassWd};
	private String[] bdDev1DN04RootConParameters = {"SLES11(SP3)", "hdpr03dn04.mayo.edu", "root", bdDev1RootNodeLoginPassWd};
	private String[] bdDev1DN05RootConParameters = {"SLES11(SP3)", "hdpr03dn05.mayo.edu", "root", bdDev1RootNodeLoginPassWd};
	private String[] bdDev1MN03RootConParameters = {"SLES11(SP3)", "hdpr03mn03.mayo.edu", "root", bdDev1RootNodeLoginPassWd};
	//
	private String[] bdDev3EN01RootConParameters = {"SLES11(SP3)", "hdpr05en01.mayo.edu", "root", bdDev3RootNodeLoginPassWd};	
	private String[] bdDev3EN02RootConParameters = {"SLES11(SP3)", "hdpr05en02.mayo.edu", "root", bdDev3RootNodeLoginPassWd};
	private String[] bdDev3EN03RootConParameters = {"SLES11(SP3)", "hdpr05en03.mayo.edu", "root", bdDev3RootNodeLoginPassWd};	
	private String[] bdDev3EN04RootConParameters = {"SLES11(SP3)", "hdpr05en04.mayo.edu", "root", bdDev3RootNodeLoginPassWd};
	private String[] bdDev3EN05RootConParameters = {"SLES11(SP3)", "hdpr05en05.mayo.edu", "root", bdDev3RootNodeLoginPassWd};	
	private String[] bdDev3MN01RootConParameters = {"SLES11(SP3)", "hdpr05mn01.mayo.edu", "root", bdDev3RootNodeLoginPassWd};		
	private String[] bdDev3MN02RootConParameters = {"SLES11(SP3)", "hdpr05mn02.mayo.edu", "root", bdDev3RootNodeLoginPassWd};
	private String[] bdDev3MN03RootConParameters = {"SLES11(SP3)", "hdpr05mn03.mayo.edu", "root", bdDev3RootNodeLoginPassWd};	
	private String[] bdDev3DN01RootConParameters = {"SLES11(SP3)", "hdpr05dn01.mayo.edu", "root", bdDev3RootNodeLoginPassWd};	
	private String[] bdDev3DN02RootConParameters = {"SLES11(SP3)", "hdpr05dn02.mayo.edu", "root", bdDev3RootNodeLoginPassWd};	
	private String[] bdDev3DN03RootConParameters = {"SLES11(SP3)", "hdpr05dn03.mayo.edu", "root", bdDev3RootNodeLoginPassWd};
	private String[] bdDev3DN04RootConParameters = {"SLES11(SP3)", "hdpr05dn04.mayo.edu", "root", bdDev3RootNodeLoginPassWd};
	private String[] bdDev3DN05RootConParameters = {"SLES11(SP3)", "hdpr05dn05.mayo.edu", "root", bdDev3RootNodeLoginPassWd};
	
	private String[] bdDev1EN01ConParameters = {"SLES11(SP3)", "hdpr03en01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev1MN01ConParameters = {"SLES11(SP3)", "hdpr03mn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev1DN01ConParameters = {"SLES11(SP3)", "hdpr03dn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev1DN02ConParameters = {"SLES11(SP3)", "hdpr03dn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev1DN03ConParameters = {"SLES11(SP3)", "hdpr03dn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev1EN02ConParameters = {"SLES11(SP3)", "hdpr03en02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev1MN02ConParameters = {"SLES11(SP3)", "hdpr03mn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdDev1DN04ConParameters = {"SLES11(SP3)", "hdpr03dn04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdDev1DN05ConParameters = {"SLES11(SP3)", "hdpr03dn05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdDev1MN03ConParameters = {"SLES11(SP3)", "hdpr03mn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	//
	private String[] bdDev3EN01ConParameters = {"SLES11(SP3)", "hdpr05en01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev3EN02ConParameters = {"SLES11(SP3)", "hdpr05en02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdDev3EN03ConParameters = {"SLES11(SP3)", "hdpr05en03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev3EN04ConParameters = {"SLES11(SP3)", "hdpr05en04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdDev3EN05ConParameters = {"SLES11(SP3)", "hdpr05en05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev3MN01ConParameters = {"SLES11(SP3)", "hdpr05mn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};		
	private String[] bdDev3MN02ConParameters = {"SLES11(SP3)", "hdpr05mn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdDev3MN03ConParameters = {"SLES11(SP3)", "hdpr05mn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev3DN01ConParameters = {"SLES11(SP3)", "hdpr05dn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev3DN02ConParameters = {"SLES11(SP3)", "hdpr05dn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdDev3DN03ConParameters = {"SLES11(SP3)", "hdpr05dn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdDev3DN04ConParameters = {"SLES11(SP3)", "hdpr05dn04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdDev3DN05ConParameters = {"SLES11(SP3)", "hdpr05dn05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	 	
	private String[] bdProd2EN01RootConParameters = {"SLES11(SP3)", "hdpr02en01.mayo.edu", "root", bdProd2RootNodeLoginPassWd}; //23wesdxc,..tdc..23wesdxc..23wesdxc
	private String[] bdProd2EN02RootConParameters = {"SLES11(SP3)", "hdpr02en02.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	private String[] bdProd2EN03RootConParameters = {"SLES11(SP3)", "hdpr02en03.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2EN04RootConParameters = {"SLES11(SP3)", "hdpr02en04.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2EN05RootConParameters = {"SLES11(SP3)", "hdpr02en05.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2EN06RootConParameters = {"SLES11(SP3)", "hdpr02en06.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	private String[] bdProd2EN07RootConParameters = {"SLES11(SP3)", "hdpr02en07.mayo.edu", "root", bdProd2RootNodeLoginPassWd}; 
	private String[] bdProd2EN08RootConParameters = {"SLES11(SP3)", "hdpr02en08.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	private String[] bdProd2EN09RootConParameters = {"SLES11(SP3)", "hdpr02en09.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2EN10RootConParameters = {"SLES11(SP3)", "hdpr02en10.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2EN11RootConParameters = {"SLES11(SP3)", "hdpr02en11.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	private String[] bdProd2MN01RootConParameters = {"SLES11(SP3)", "hdpr02mn01.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2MN02RootConParameters = {"SLES11(SP3)", "hdpr02mn02.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	private String[] bdProd2DN01RootConParameters = {"SLES11(SP3)", "hdpr02dn01.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN02RootConParameters = {"SLES11(SP3)", "hdpr02dn02.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN03RootConParameters = {"SLES11(SP3)", "hdpr02dn03.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN04RootConParameters = {"SLES11(SP3)", "hdpr02dn04.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN05RootConParameters = {"SLES11(SP3)", "hdpr02dn05.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN06RootConParameters = {"SLES11(SP3)", "hdpr02dn06.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN07RootConParameters = {"SLES11(SP3)", "hdpr02dn07.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN08RootConParameters = {"SLES11(SP3)", "hdpr02dn08.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN09RootConParameters = {"SLES11(SP3)", "hdpr02dn09.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN10RootConParameters = {"SLES11(SP3)", "hdpr02dn10.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2MN03RootConParameters = {"SLES11(SP3)", "hdpr02mn03.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	private String[] bdProd2EN12RootConParameters = {"SLES11(SP3)", "hdpr02en12.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	private String[] bdProd2EN13RootConParameters = {"SLES11(SP3)", "hdpr02en13.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2EN14RootConParameters = {"SLES11(SP3)", "hdpr02en14.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2EN15RootConParameters = {"SLES11(SP3)", "hdpr02en15.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2EN16RootConParameters = {"SLES11(SP3)", "hdpr02en16.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	private String[] bdProd2EN17RootConParameters = {"SLES11(SP3)", "hdpr02en17.mayo.edu", "root", bdProd2RootNodeLoginPassWd}; 
	private String[] bdProd2EN18RootConParameters = {"SLES11(SP3)", "hdpr02en18.mayo.edu", "root", bdProd2RootNodeLoginPassWd};	
	private String[] bdProd2EN19RootConParameters = {"SLES11(SP3)", "hdpr02en19.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2EN20RootConParameters = {"SLES11(SP3)", "hdpr02en20.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN11RootConParameters = {"SLES11(SP3)", "hdpr02dn11.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN12RootConParameters = {"SLES11(SP3)", "hdpr02dn12.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN13RootConParameters = {"SLES11(SP3)", "hdpr02dn13.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN14RootConParameters = {"SLES11(SP3)", "hdpr02dn14.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN15RootConParameters = {"SLES11(SP3)", "hdpr02dn15.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN16RootConParameters = {"SLES11(SP3)", "hdpr02dn16.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN17RootConParameters = {"SLES11(SP3)", "hdpr02dn17.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN18RootConParameters = {"SLES11(SP3)", "hdpr02dn18.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN19RootConParameters = {"SLES11(SP3)", "hdpr02dn19.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN20RootConParameters = {"SLES11(SP3)", "hdpr02dn20.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN21RootConParameters = {"SLES11(SP3)", "hdpr02dn21.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN22RootConParameters = {"SLES11(SP3)", "hdpr02dn22.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN23RootConParameters = {"SLES11(SP3)", "hdpr02dn23.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN24RootConParameters = {"SLES11(SP3)", "hdpr02dn24.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN25RootConParameters = {"SLES11(SP3)", "hdpr02dn25.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN26RootConParameters = {"SLES11(SP3)", "hdpr02dn26.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN27RootConParameters = {"SLES11(SP3)", "hdpr02dn27.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN28RootConParameters = {"SLES11(SP3)", "hdpr02dn28.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN29RootConParameters = {"SLES11(SP3)", "hdpr02dn29.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN30RootConParameters = {"SLES11(SP3)", "hdpr02dn30.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN31RootConParameters = {"SLES11(SP3)", "hdpr02dn31.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	private String[] bdProd2DN32RootConParameters = {"SLES11(SP3)", "hdpr02dn32.mayo.edu", "root", bdProd2RootNodeLoginPassWd};
	//
	private String[] bdProd3EN01RootConParameters = {"SLES11(SP3)", "hdpr07en01.mayo.edu", "root", bdProd3RootNodeLoginPassWd};	
	private String[] bdProd3EN02RootConParameters = {"SLES11(SP3)", "hdpr07en02.mayo.edu", "root", bdProd3RootNodeLoginPassWd};
	private String[] bdProd3EN03RootConParameters = {"SLES11(SP3)", "hdpr07en03.mayo.edu", "root", bdProd3RootNodeLoginPassWd};	
	private String[] bdProd3EN04RootConParameters = {"SLES11(SP3)", "hdpr07en04.mayo.edu", "root", bdProd3RootNodeLoginPassWd};
	private String[] bdProd3EN05RootConParameters = {"SLES11(SP3)", "hdpr07en05.mayo.edu", "root", bdProd3RootNodeLoginPassWd};	
	private String[] bdProd3EN06RootConParameters = {"SLES11(SP3)", "hdpr07en06.mayo.edu", "root", bdProd3RootNodeLoginPassWd};	
	private String[] bdProd3EN07RootConParameters = {"SLES11(SP3)", "hdpr07en07.mayo.edu", "root", bdProd3RootNodeLoginPassWd};
	private String[] bdProd3EN08RootConParameters = {"SLES11(SP3)", "hdpr07en08.mayo.edu", "root", bdProd3RootNodeLoginPassWd};	
	private String[] bdProd3MN01RootConParameters = {"SLES11(SP3)", "hdpr07mn01.mayo.edu", "root", bdProd3RootNodeLoginPassWd};	
	private String[] bdProd3MN02RootConParameters = {"SLES11(SP3)", "hdpr07mn02.mayo.edu", "root", bdProd3RootNodeLoginPassWd};
	private String[] bdProd3MN03RootConParameters = {"SLES11(SP3)", "hdpr07mn03.mayo.edu", "root", bdProd3RootNodeLoginPassWd};	
	private String[] bdProd3DN01RootConParameters = {"SLES11(SP3)", "hdpr07dn01.mayo.edu", "root", bdProd3RootNodeLoginPassWd};	
	private String[] bdProd3DN02RootConParameters = {"SLES11(SP3)", "hdpr07dn02.mayo.edu", "root", bdProd3RootNodeLoginPassWd};	
	private String[] bdProd3DN03RootConParameters = {"SLES11(SP3)", "hdpr07dn03.mayo.edu", "root", bdProd3RootNodeLoginPassWd};
	private String[] bdProd3DN04RootConParameters = {"SLES11(SP3)", "hdpr07dn04.mayo.edu", "root", bdProd3RootNodeLoginPassWd};
	private String[] bdProd3DN05RootConParameters = {"SLES11(SP3)", "hdpr07dn05.mayo.edu", "root", bdProd3RootNodeLoginPassWd};
	//...
	private String[] bdProd3DN32RootConParameters = {"SLES11(SP3)", "hdpr07dn32.mayo.edu", "root", bdProd3RootNodeLoginPassWd};
	
	private String[] bdProd2EN01ConParameters = {"SLES11(SP3)", "hdpr02en01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN02ConParameters = {"SLES11(SP3)", "hdpr02en02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd2EN03ConParameters = {"SLES11(SP3)", "hdpr02en03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN04ConParameters = {"SLES11(SP3)", "hdpr02en04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN05ConParameters = {"SLES11(SP3)", "hdpr02en05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN06ConParameters = {"SLES11(SP3)", "hdpr02en06.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd2EN07ConParameters = {"SLES11(SP3)", "hdpr02en07.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN08ConParameters = {"SLES11(SP3)", "hdpr02en08.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd2EN09ConParameters = {"SLES11(SP3)", "hdpr02en09.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN10ConParameters = {"SLES11(SP3)", "hdpr02en10.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN11ConParameters = {"SLES11(SP3)", "hdpr02en11.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd2MN01ConParameters = {"SLES11(SP3)", "hdpr02mn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2MN02ConParameters = {"SLES11(SP3)", "hdpr02mn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd2DN01ConParameters = {"SLES11(SP3)", "hdpr02dn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN02ConParameters = {"SLES11(SP3)", "hdpr02dn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN03ConParameters = {"SLES11(SP3)", "hdpr02dn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN04ConParameters = {"SLES11(SP3)", "hdpr02dn04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN05ConParameters = {"SLES11(SP3)", "hdpr02dn05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN06ConParameters = {"SLES11(SP3)", "hdpr02dn06.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN07ConParameters = {"SLES11(SP3)", "hdpr02dn07.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN08ConParameters = {"SLES11(SP3)", "hdpr02dn08.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN09ConParameters = {"SLES11(SP3)", "hdpr02dn09.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN10ConParameters = {"SLES11(SP3)", "hdpr02dn10.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2MN03ConParameters = {"SLES11(SP3)", "hdpr02mn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN12ConParameters = {"SLES11(SP3)", "hdpr02en12.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd2EN13ConParameters = {"SLES11(SP3)", "hdpr02en13.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN14ConParameters = {"SLES11(SP3)", "hdpr02en14.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN15ConParameters = {"SLES11(SP3)", "hdpr02en15.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN16ConParameters = {"SLES11(SP3)", "hdpr02en16.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd2EN17ConParameters = {"SLES11(SP3)", "hdpr02en17.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName}; 
	private String[] bdProd2EN18ConParameters = {"SLES11(SP3)", "hdpr02en18.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd2EN19ConParameters = {"SLES11(SP3)", "hdpr02en19.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2EN20ConParameters = {"SLES11(SP3)", "hdpr02en20.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN11ConParameters = {"SLES11(SP3)", "hdpr02dn11.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN12ConParameters = {"SLES11(SP3)", "hdpr02dn12.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN13ConParameters = {"SLES11(SP3)", "hdpr02dn13.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN14ConParameters = {"SLES11(SP3)", "hdpr02dn14.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN15ConParameters = {"SLES11(SP3)", "hdpr02dn15.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN16ConParameters = {"SLES11(SP3)", "hdpr02dn16.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN17ConParameters = {"SLES11(SP3)", "hdpr02dn17.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN18ConParameters = {"SLES11(SP3)", "hdpr02dn18.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN19ConParameters = {"SLES11(SP3)", "hdpr02dn19.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN20ConParameters = {"SLES11(SP3)", "hdpr02dn20.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN21ConParameters = {"SLES11(SP3)", "hdpr02dn21.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN22ConParameters = {"SLES11(SP3)", "hdpr02dn22.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN23ConParameters = {"SLES11(SP3)", "hdpr02dn23.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN24ConParameters = {"SLES11(SP3)", "hdpr02dn24.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN25ConParameters = {"SLES11(SP3)", "hdpr02dn25.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN26ConParameters = {"SLES11(SP3)", "hdpr02dn26.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN27ConParameters = {"SLES11(SP3)", "hdpr02dn27.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN28ConParameters = {"SLES11(SP3)", "hdpr02dn28.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN29ConParameters = {"SLES11(SP3)", "hdpr02dn29.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN30ConParameters = {"SLES11(SP3)", "hdpr02dn30.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN31ConParameters = {"SLES11(SP3)", "hdpr02dn31.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd2DN32ConParameters = {"SLES11(SP3)", "hdpr02dn32.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	//
	private String[] bdProd3EN01ConParameters = {"SLES11(SP3)", "hdpr07en01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd3EN02ConParameters = {"SLES11(SP3)", "hdpr07en02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd3EN03ConParameters = {"SLES11(SP3)", "hdpr07en03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd3EN04ConParameters = {"SLES11(SP3)", "hdpr07en04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd3EN05ConParameters = {"SLES11(SP3)", "hdpr07en05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd3EN06ConParameters = {"SLES11(SP3)", "hdpr07en06.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd3EN07ConParameters = {"SLES11(SP3)", "hdpr07en07.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd3EN08ConParameters = {"SLES11(SP3)", "hdpr07en08.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd3MN01ConParameters = {"SLES11(SP3)", "hdpr07mn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};		
	private String[] bdProd3MN02ConParameters = {"SLES11(SP3)", "hdpr07mn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd3MN03ConParameters = {"SLES11(SP3)", "hdpr07mn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd3DN01ConParameters = {"SLES11(SP3)", "hdpr07dn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd3DN02ConParameters = {"SLES11(SP3)", "hdpr07dn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdProd3DN03ConParameters = {"SLES11(SP3)", "hdpr07dn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd3DN04ConParameters = {"SLES11(SP3)", "hdpr07dn04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdProd3DN05ConParameters = {"SLES11(SP3)", "hdpr07dn05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	//...
	private String[] bdProd3DN32ConParameters = {"SLES11(SP3)", "hdpr07dn32.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	

	private String[] bdTest2EN01RootConParameters = {"SLES11(SP3)", "hdpr01en01.mayo.edu", "root", bdTest2RootNodeLoginPassWd}; //56rtdfxc..tdcd1
	private String[] bdTest2EN02RootConParameters = {"SLES11(SP3)", "hdpr01en02.mayo.edu", "root", bdTest2RootNodeLoginPassWd};	
	private String[] bdTest2EN03RootConParameters = {"SLES11(SP3)", "hdpr01en03.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2EN04RootConParameters = {"SLES11(SP3)", "hdpr01en04.mayo.edu", "root", bdTest2RootNodeLoginPassWd};	
	private String[] bdTest2EN05RootConParameters = {"SLES11(SP3)", "hdpr01en05.mayo.edu", "root", bdTest2RootNodeLoginPassWd};	
	private String[] bdTest2MN01RootConParameters = {"SLES11(SP3)", "hdpr01mn01.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2MN02RootConParameters = {"SLES11(SP3)", "hdpr01mn02.mayo.edu", "root", bdTest2RootNodeLoginPassWd};	
	private String[] bdTest2DN01RootConParameters = {"SLES11(SP3)", "hdpr01dn01.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2DN02RootConParameters = {"SLES11(SP3)", "hdpr01dn02.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2DN03RootConParameters = {"SLES11(SP3)", "hdpr01dn03.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2DN04RootConParameters = {"SLES11(SP3)", "hdpr01dn04.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2DN05RootConParameters = {"SLES11(SP3)", "hdpr01dn05.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2DN06RootConParameters = {"SLES11(SP3)", "hdpr01dn06.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2DN07RootConParameters = {"SLES11(SP3)", "hdpr01dn07.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2DN08RootConParameters = {"SLES11(SP3)", "hdpr01dn08.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2DN09RootConParameters = {"SLES11(SP3)", "hdpr01dn09.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	private String[] bdTest2DN10RootConParameters = {"SLES11(SP3)", "hdpr01dn10.mayo.edu", "root", bdTest2RootNodeLoginPassWd};
	//
	private String[] bdTest3EN01RootConParameters = {"SLES11(SP3)", "hdpr06en01.mayo.edu", "root", bdTest3RootNodeLoginPassWd};	
	private String[] bdTest3EN02RootConParameters = {"SLES11(SP3)", "hdpr06en02.mayo.edu", "root", bdTest3RootNodeLoginPassWd};
	private String[] bdTest3EN03RootConParameters = {"SLES11(SP3)", "hdpr06en03.mayo.edu", "root", bdTest3RootNodeLoginPassWd};		
	private String[] bdTest3MN01RootConParameters = {"SLES11(SP3)", "hdpr06mn01.mayo.edu", "root", bdTest3RootNodeLoginPassWd};	
	private String[] bdTest3MN02RootConParameters = {"SLES11(SP3)", "hdpr06mn02.mayo.edu", "root", bdTest3RootNodeLoginPassWd};
	private String[] bdTest3MN03RootConParameters = {"SLES11(SP3)", "hdpr06mn03.mayo.edu", "root", bdTest3RootNodeLoginPassWd};	
	private String[] bdTest3DN01RootConParameters = {"SLES11(SP3)", "hdpr06dn01.mayo.edu", "root", bdTest3RootNodeLoginPassWd};	
	private String[] bdTest3DN02RootConParameters = {"SLES11(SP3)", "hdpr06dn02.mayo.edu", "root", bdTest3RootNodeLoginPassWd};	
	private String[] bdTest3DN03RootConParameters = {"SLES11(SP3)", "hdpr06dn03.mayo.edu", "root", bdTest3RootNodeLoginPassWd};
	private String[] bdTest3DN04RootConParameters = {"SLES11(SP3)", "hdpr06dn04.mayo.edu", "root", bdTest3RootNodeLoginPassWd};
	private String[] bdTest3DN05RootConParameters = {"SLES11(SP3)", "hdpr06dn05.mayo.edu", "root", bdTest3RootNodeLoginPassWd};
	private String[] bdTest3DN06RootConParameters = {"SLES11(SP3)", "hdpr06dn06.mayo.edu", "root", bdTest3RootNodeLoginPassWd};	
	private String[] bdTest3DN07RootConParameters = {"SLES11(SP3)", "hdpr06dn07.mayo.edu", "root", bdTest3RootNodeLoginPassWd};
	private String[] bdTest3DN08RootConParameters = {"SLES11(SP3)", "hdpr06dn08.mayo.edu", "root", bdTest3RootNodeLoginPassWd};
	private String[] bdTest3DN09RootConParameters = {"SLES11(SP3)", "hdpr06dn09.mayo.edu", "root", bdTest3RootNodeLoginPassWd};
	
	private String[] bdTest2EN01ConParameters = {"SLES11(SP3)", "hdpr01en01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2EN02ConParameters = {"SLES11(SP3)", "hdpr01en02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest2EN03ConParameters = {"SLES11(SP3)", "hdpr01en03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2EN04ConParameters = {"SLES11(SP3)", "hdpr01en04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest2EN05ConParameters = {"SLES11(SP3)", "hdpr01en05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest2MN01ConParameters = {"SLES11(SP3)", "hdpr01mn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2MN02ConParameters = {"SLES11(SP3)", "hdpr01mn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest2DN01ConParameters = {"SLES11(SP3)", "hdpr01dn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2DN02ConParameters = {"SLES11(SP3)", "hdpr01dn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2DN03ConParameters = {"SLES11(SP3)", "hdpr01dn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2DN04ConParameters = {"SLES11(SP3)", "hdpr01dn04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2DN05ConParameters = {"SLES11(SP3)", "hdpr01dn05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2DN06ConParameters = {"SLES11(SP3)", "hdpr01dn06.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2DN07ConParameters = {"SLES11(SP3)", "hdpr01dn07.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2DN08ConParameters = {"SLES11(SP3)", "hdpr01dn08.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2DN09ConParameters = {"SLES11(SP3)", "hdpr01dn09.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest2DN10ConParameters = {"SLES11(SP3)", "hdpr01dn10.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	//
	private String[] bdTest3EN01ConParameters = {"SLES11(SP3)", "hdpr06en01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest3EN02ConParameters = {"SLES11(SP3)", "hdpr06en02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest3EN03ConParameters = {"SLES11(SP3)", "hdpr06en03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};		
	private String[] bdTest3MN01ConParameters = {"SLES11(SP3)", "hdpr06mn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};		
	private String[] bdTest3MN02ConParameters = {"SLES11(SP3)", "hdpr06mn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest3MN03ConParameters = {"SLES11(SP3)", "hdpr06mn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest3DN01ConParameters = {"SLES11(SP3)", "hdpr06dn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest3DN02ConParameters = {"SLES11(SP3)", "hdpr06dn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest3DN03ConParameters = {"SLES11(SP3)", "hdpr06dn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest3DN04ConParameters = {"SLES11(SP3)", "hdpr06dn04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest3DN05ConParameters = {"SLES11(SP3)", "hdpr06dn05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest3DN06ConParameters = {"SLES11(SP3)", "hdpr06dn06.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdTest3DN07ConParameters = {"SLES11(SP3)", "hdpr06dn07.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest3DN08ConParameters = {"SLES11(SP3)", "hdpr06dn08.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdTest3DN09ConParameters = {"SLES11(SP3)", "hdpr06dn09.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	

	private String[] bdSdbxEN01RootConParameters = {"SLES11(SP3)", "hdpr04en01.mayo.edu", "root", bdSdbxRootNodeLoginPassWd}; 
	private String[] bdSdbxEN02RootConParameters = {"SLES11(SP3)", "hdpr04en02.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};	
	private String[] bdSdbxEN03RootConParameters = {"SLES11(SP3)", "hdpr04en03.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxEN04RootConParameters = {"SLES11(SP3)", "hdpr04en04.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};	
	private String[] bdSdbxMN01RootConParameters = {"SLES11(SP3)", "hdpr04mn01.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxMN02RootConParameters = {"SLES11(SP3)", "hdpr04mn02.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};	
	private String[] bdSdbxDN01RootConParameters = {"SLES11(SP3)", "hdpr04dn01.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN02RootConParameters = {"SLES11(SP3)", "hdpr04dn02.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN03RootConParameters = {"SLES11(SP3)", "hdpr04dn03.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN04RootConParameters = {"SLES11(SP3)", "hdpr04dn04.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN05RootConParameters = {"SLES11(SP3)", "hdpr04dn05.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN06RootConParameters = {"SLES11(SP3)", "hdpr04dn06.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN07RootConParameters = {"SLES11(SP3)", "hdpr04dn07.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN08RootConParameters = {"SLES11(SP3)", "hdpr04dn08.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN09RootConParameters = {"SLES11(SP3)", "hdpr04dn09.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN10RootConParameters = {"SLES11(SP3)", "hdpr04dn10.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN11RootConParameters = {"SLES11(SP3)", "hdpr04dn11.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	private String[] bdSdbxDN12RootConParameters = {"SLES11(SP3)", "hdpr04dn12.mayo.edu", "root", bdSdbxRootNodeLoginPassWd};
	
	private String[] bdSdbxEN01ConParameters = {"SLES11(SP3)", "hdpr04en01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName}; 
	private String[] bdSdbxEN02ConParameters = {"SLES11(SP3)", "hdpr04en02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdSdbxEN03ConParameters = {"SLES11(SP3)", "hdpr04en03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxEN04ConParameters = {"SLES11(SP3)", "hdpr04en04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdSdbxMN01ConParameters = {"SLES11(SP3)", "hdpr04mn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxMN02ConParameters = {"SLES11(SP3)", "hdpr04mn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};	
	private String[] bdSdbxDN01ConParameters = {"SLES11(SP3)", "hdpr04dn01.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN02ConParameters = {"SLES11(SP3)", "hdpr04dn02.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN03ConParameters = {"SLES11(SP3)", "hdpr04dn03.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN04ConParameters = {"SLES11(SP3)", "hdpr04dn04.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN05ConParameters = {"SLES11(SP3)", "hdpr04dn05.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN06ConParameters = {"SLES11(SP3)", "hdpr04dn06.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN07ConParameters = {"SLES11(SP3)", "hdpr04dn07.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN08ConParameters = {"SLES11(SP3)", "hdpr04dn08.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN09ConParameters = {"SLES11(SP3)", "hdpr04dn09.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN10ConParameters = {"SLES11(SP3)", "hdpr04dn10.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN11ConParameters = {"SLES11(SP3)", "hdpr04dn11.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	private String[] bdSdbxDN12ConParameters = {"SLES11(SP3)", "hdpr04dn12.mayo.edu", testUserAccountName, testUserPasswordLessPuttyKeyFilePathAndName};
	

	//private String[] hwSdbxNodeConParameters = {"CentOS", "sandbox.hortonworks.com", "root", "tdc2016"};
 
//Default Constructor
  public ConfigureULWResources() {
     //Do Nothing
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

public String[] getBdDev1EN01ConParameters() {
	return bdDev1EN01ConParameters;
}

public void setBdDev1EN01ConParameters(String[] bdDev1EN01ConParameters) {
	this.bdDev1EN01ConParameters = bdDev1EN01ConParameters;
}

public String[] getBdDev1MN01ConParameters() {
	return bdDev1MN01ConParameters;
}

public void setBdDev1MN01ConParameters(String[] bdDev1MN01ConParameters) {
	this.bdDev1MN01ConParameters = bdDev1MN01ConParameters;
}

public String[] getBdDev1DN01ConParameters() {
	return bdDev1DN01ConParameters;
}


public String[] getBdDev1DN04ConParameters() {
	return bdDev1DN04ConParameters;
}



public void setBdDev1DN04ConParameters(String[] bdDev1DN04ConParameters) {
	this.bdDev1DN04ConParameters = bdDev1DN04ConParameters;
}



public String[] getBdDev1DN05ConParameters() {
	return bdDev1DN05ConParameters;
}



public void setBdDev1DN05ConParameters(String[] bdDev1DN05ConParameters) {
	this.bdDev1DN05ConParameters = bdDev1DN05ConParameters;
}



public void setBdDev1DN01ConParameters(String[] bdDev1DN01ConParameters) {
	this.bdDev1DN01ConParameters = bdDev1DN01ConParameters;
}

public String[] getBdDev1DN02ConParameters() {
	return bdDev1DN02ConParameters;
}

public void setBdDev1DN02ConParameters(String[] bdDev1DN02ConParameters) {
	this.bdDev1DN02ConParameters = bdDev1DN02ConParameters;
}

public String[] getBdDev1DN03ConParameters() {
	return bdDev1DN03ConParameters;
}

public void setBdDev1DN03ConParameters(String[] bdDev1DN03ConParameters) {
	this.bdDev1DN03ConParameters = bdDev1DN03ConParameters;
}

public String[] getBdProd2EN01ConParameters() {
	return bdProd2EN01ConParameters;
}

public void setBdProd2EN01ConParameters(String[] bdProd2EN01ConParameters) {
	this.bdProd2EN01ConParameters = bdProd2EN01ConParameters;
}

public String[] getBdProd2EN02ConParameters() {
	return bdProd2EN02ConParameters;
}

public void setBdProd2EN02ConParameters(String[] bdProd2EN02ConParameters) {
	this.bdProd2EN02ConParameters = bdProd2EN02ConParameters;
}

public String[] getBdProd2EN03ConParameters() {
	return bdProd2EN03ConParameters;
}

public void setBdProd2EN03ConParameters(String[] bdProd2EN03ConParameters) {
	this.bdProd2EN03ConParameters = bdProd2EN03ConParameters;
}

public String[] getBdProd2EN04ConParameters() {
	return bdProd2EN04ConParameters;
}

public void setBdProd2EN04ConParameters(String[] bdProd2EN04ConParameters) {
	this.bdProd2EN04ConParameters = bdProd2EN04ConParameters;
}

public String[] getBdProd2EN05ConParameters() {
	return bdProd2EN05ConParameters;
}

public void setBdProd2EN05ConParameters(String[] bdProd2EN05ConParameters) {
	this.bdProd2EN05ConParameters = bdProd2EN05ConParameters;
}

public String[] getBdProd2EN06ConParameters() {
	return bdProd2EN06ConParameters;
}

public void setBdProd2EN06ConParameters(String[] bdProd2EN06ConParameters) {
	this.bdProd2EN06ConParameters = bdProd2EN06ConParameters;
}

public String[] getBdProd2MN01ConParameters() {
	return bdProd2MN01ConParameters;
}

public void setBdProd2MN01ConParameters(String[] bdProd2MN01ConParameters) {
	this.bdProd2MN01ConParameters = bdProd2MN01ConParameters;
}

public String[] getBdProd2MN02ConParameters() {
	return bdProd2MN02ConParameters;
}

public void setBdProd2MN02ConParameters(String[] bdProd2MN02ConParameters) {
	this.bdProd2MN02ConParameters = bdProd2MN02ConParameters;
}

public String[] getBdProd2DN01ConParameters() {
	return bdProd2DN01ConParameters;
}

public void setBdProd2DN01ConParameters(String[] bdProd2DN01ConParameters) {
	this.bdProd2DN01ConParameters = bdProd2DN01ConParameters;
}

public String[] getBdProd2DN02ConParameters() {
	return bdProd2DN02ConParameters;
}

public void setBdProd2DN02ConParameters(String[] bdProd2DN02ConParameters) {
	this.bdProd2DN02ConParameters = bdProd2DN02ConParameters;
}

public String[] getBdProd2DN03ConParameters() {
	return bdProd2DN03ConParameters;
}

public void setBdProd2DN03ConParameters(String[] bdProd2DN03ConParameters) {
	this.bdProd2DN03ConParameters = bdProd2DN03ConParameters;
}

public String[] getBdProd2DN04ConParameters() {
	return bdProd2DN04ConParameters;
}

public void setBdProd2DN04ConParameters(String[] bdProd2DN04ConParameters) {
	this.bdProd2DN04ConParameters = bdProd2DN04ConParameters;
}

public String[] getBdProd2DN05ConParameters() {
	return bdProd2DN05ConParameters;
}

public void setBdProd2DN05ConParameters(String[] bdProd2DN05ConParameters) {
	this.bdProd2DN05ConParameters = bdProd2DN05ConParameters;
}

public String[] getBdProd2DN06ConParameters() {
	return bdProd2DN06ConParameters;
}

public void setBdProd2DN06ConParameters(String[] bdProd2DN06ConParameters) {
	this.bdProd2DN06ConParameters = bdProd2DN06ConParameters;
}

public String[] getBdProd2DN07ConParameters() {
	return bdProd2DN07ConParameters;
}

public void setBdProd2DN07ConParameters(String[] bdProd2DN07ConParameters) {
	this.bdProd2DN07ConParameters = bdProd2DN07ConParameters;
}

public String[] getBdProd2DN08ConParameters() {
	return bdProd2DN08ConParameters;
}

public void setBdProd2DN08ConParameters(String[] bdProd2DN08ConParameters) {
	this.bdProd2DN08ConParameters = bdProd2DN08ConParameters;
}

public String[] getBdProd2DN09ConParameters() {
	return bdProd2DN09ConParameters;
}

public void setBdProd2DN09ConParameters(String[] bdProd2DN09ConParameters) {
	this.bdProd2DN09ConParameters = bdProd2DN09ConParameters;
}

public String[] getBdProd2DN10ConParameters() {
	return bdProd2DN10ConParameters;
}

public void setBdProd2DN10ConParameters(String[] bdProd2DN10ConParameters) {
	this.bdProd2DN10ConParameters = bdProd2DN10ConParameters;
}

public String[] getBdTest2EN01ConParameters() {
	return bdTest2EN01ConParameters;
}

public void setBdTest2EN01ConParameters(String[] bdTest2EN01ConParameters) {
	this.bdTest2EN01ConParameters = bdTest2EN01ConParameters;
}

public String[] getBdTest2EN02ConParameters() {
	return bdTest2EN02ConParameters;
}

public void setBdTest2EN02ConParameters(String[] bdTest2EN02ConParameters) {
	this.bdTest2EN02ConParameters = bdTest2EN02ConParameters;
}

public String[] getBdTest2EN03ConParameters() {
	return bdTest2EN03ConParameters;
}

public void setBdTest2EN03ConParameters(String[] bdTest2EN03ConParameters) {
	this.bdTest2EN03ConParameters = bdTest2EN03ConParameters;
}

public String[] getBdTest2EN04ConParameters() {
	return bdTest2EN04ConParameters;
}

public void setBdTest2EN04ConParameters(String[] bdTest2EN04ConParameters) {
	this.bdTest2EN04ConParameters = bdTest2EN04ConParameters;
}

public String[] getBdTest2EN05ConParameters() {
	return bdTest2EN05ConParameters;
}

public void setBdTest2EN05ConParameters(String[] bdTest2EN05ConParameters) {
	this.bdTest2EN05ConParameters = bdTest2EN05ConParameters;
}

public String[] getBdTest2MN01ConParameters() {
	return bdTest2MN01ConParameters;
}

public void setBdTest2MN01ConParameters(String[] bdTest2MN01ConParameters) {
	this.bdTest2MN01ConParameters = bdTest2MN01ConParameters;
}

public String[] getBdTest2MN02ConParameters() {
	return bdTest2MN02ConParameters;
}

public void setBdTest2MN02ConParameters(String[] bdTest2MN02ConParameters) {
	this.bdTest2MN02ConParameters = bdTest2MN02ConParameters;
}

public String[] getBdTest2DN01ConParameters() {
	return bdTest2DN01ConParameters;
}

public void setBdTest2DN01ConParameters(String[] bdTest2DN01ConParameters) {
	this.bdTest2DN01ConParameters = bdTest2DN01ConParameters;
}

public String[] getBdTest2DN02ConParameters() {
	return bdTest2DN02ConParameters;
}

public void setBdTest2DN02ConParameters(String[] bdTest2DN02ConParameters) {
	this.bdTest2DN02ConParameters = bdTest2DN02ConParameters;
}

public String[] getBdTest2DN03ConParameters() {
	return bdTest2DN03ConParameters;
}

public void setBdTest2DN03ConParameters(String[] bdTest2DN03ConParameters) {
	this.bdTest2DN03ConParameters = bdTest2DN03ConParameters;
}

public String[] getBdTest2DN04ConParameters() {
	return bdTest2DN04ConParameters;
}

public void setBdTest2DN04ConParameters(String[] bdTest2DN04ConParameters) {
	this.bdTest2DN04ConParameters = bdTest2DN04ConParameters;
}

public String[] getBdTest2DN05ConParameters() {
	return bdTest2DN05ConParameters;
}

public void setBdTest2DN05ConParameters(String[] bdTest2DN05ConParameters) {
	this.bdTest2DN05ConParameters = bdTest2DN05ConParameters;
}

public String[] getBdTest2DN06ConParameters() {
	return bdTest2DN06ConParameters;
}

public void setBdTest2DN06ConParameters(String[] bdTest2DN06ConParameters) {
	this.bdTest2DN06ConParameters = bdTest2DN06ConParameters;
}

public String[] getBdTest2DN07ConParameters() {
	return bdTest2DN07ConParameters;
}

public void setBdTest2DN07ConParameters(String[] bdTest2DN07ConParameters) {
	this.bdTest2DN07ConParameters = bdTest2DN07ConParameters;
}

public String[] getBdTest2DN08ConParameters() {
	return bdTest2DN08ConParameters;
}

public void setBdTest2DN08ConParameters(String[] bdTest2DN08ConParameters) {
	this.bdTest2DN08ConParameters = bdTest2DN08ConParameters;
}

public String[] getBdTest2DN09ConParameters() {
	return bdTest2DN09ConParameters;
}

public void setBdTest2DN09ConParameters(String[] bdTest2DN09ConParameters) {
	this.bdTest2DN09ConParameters = bdTest2DN09ConParameters;
}

public String[] getBdTest2DN10ConParameters() {
	return bdTest2DN10ConParameters;
}

public void setBdTest2DN10ConParameters(String[] bdTest2DN10ConParameters) {
	this.bdTest2DN10ConParameters = bdTest2DN10ConParameters;
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

public String[] getBdDev1EN02ConParameters() {
	return bdDev1EN02ConParameters;
}

public void setBdDev1EN02ConParameters(String[] bdDev1EN02ConParameters) {
	this.bdDev1EN02ConParameters = bdDev1EN02ConParameters;
}

public String[] getBdDev1MN02ConParameters() {
	return bdDev1MN02ConParameters;
}

public void setBdDev1MN02ConParameters(String[] bdDev1MN02ConParameters) {
	this.bdDev1MN02ConParameters = bdDev1MN02ConParameters;
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

public String[] getBdDev1EN01RootConParameters() {
	return bdDev1EN01RootConParameters;
}

public void setBdDev1EN01RootConParameters(String[] bdDev1EN01RootConParameters) {
	this.bdDev1EN01RootConParameters = bdDev1EN01RootConParameters;
}

public String[] getBdDev1MN01RootConParameters() {
	return bdDev1MN01RootConParameters;
}

public void setBdDev1MN01RootConParameters(String[] bdDev1MN01RootConParameters) {
	this.bdDev1MN01RootConParameters = bdDev1MN01RootConParameters;
}

public String[] getBdDev1DN01RootConParameters() {
	return bdDev1DN01RootConParameters;
}

public void setBdDev1DN01RootConParameters(String[] bdDev1DN01RootConParameters) {
	this.bdDev1DN01RootConParameters = bdDev1DN01RootConParameters;
}

public String[] getBdDev1DN02RootConParameters() {
	return bdDev1DN02RootConParameters;
}

public void setBdDev1DN02RootConParameters(String[] bdDev1DN02RootConParameters) {
	this.bdDev1DN02RootConParameters = bdDev1DN02RootConParameters;
}

public String[] getBdDev1DN03RootConParameters() {
	return bdDev1DN03RootConParameters;
}

public void setBdDev1DN03RootConParameters(String[] bdDev1DN03RootConParameters) {
	this.bdDev1DN03RootConParameters = bdDev1DN03RootConParameters;
}

public String[] getBdDev1EN02RootConParameters() {
	return bdDev1EN02RootConParameters;
}

public void setBdDev1EN02RootConParameters(String[] bdDev1EN02RootConParameters) {
	this.bdDev1EN02RootConParameters = bdDev1EN02RootConParameters;
}

public String[] getBdDev1MN02RootConParameters() {
	return bdDev1MN02RootConParameters;
}

public void setBdDev1MN02RootConParameters(String[] bdDev1MN02RootConParameters) {
	this.bdDev1MN02RootConParameters = bdDev1MN02RootConParameters;
}

public String[] getBdDev1DN04RootConParameters() {
	return bdDev1DN04RootConParameters;
}

public void setBdDev1DN04RootConParameters(String[] bdDev1DN04RootConParameters) {
	this.bdDev1DN04RootConParameters = bdDev1DN04RootConParameters;
}

public String[] getBdDev1DN05RootConParameters() {
	return bdDev1DN05RootConParameters;
}

public void setBdDev1DN05RootConParameters(String[] bdDev1DN05RootConParameters) {
	this.bdDev1DN05RootConParameters = bdDev1DN05RootConParameters;
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

public String[] getBdProd2EN01RootConParameters() {
	return bdProd2EN01RootConParameters;
}

public void setBdProd2EN01RootConParameters(String[] bdProd2EN01RootConParameters) {
	this.bdProd2EN01RootConParameters = bdProd2EN01RootConParameters;
}

public String[] getBdProd2EN02RootConParameters() {
	return bdProd2EN02RootConParameters;
}

public void setBdProd2EN02RootConParameters(String[] bdProd2EN02RootConParameters) {
	this.bdProd2EN02RootConParameters = bdProd2EN02RootConParameters;
}

public String[] getBdProd2EN03RootConParameters() {
	return bdProd2EN03RootConParameters;
}

public void setBdProd2EN03RootConParameters(String[] bdProd2EN03RootConParameters) {
	this.bdProd2EN03RootConParameters = bdProd2EN03RootConParameters;
}

public String[] getBdProd2EN04RootConParameters() {
	return bdProd2EN04RootConParameters;
}

public void setBdProd2EN04RootConParameters(String[] bdProd2EN04RootConParameters) {
	this.bdProd2EN04RootConParameters = bdProd2EN04RootConParameters;
}

public String[] getBdProd2EN05RootConParameters() {
	return bdProd2EN05RootConParameters;
}

public void setBdProd2EN05RootConParameters(String[] bdProd2EN05RootConParameters) {
	this.bdProd2EN05RootConParameters = bdProd2EN05RootConParameters;
}

public String[] getBdProd2EN06RootConParameters() {
	return bdProd2EN06RootConParameters;
}

public void setBdProd2EN06RootConParameters(String[] bdProd2EN06RootConParameters) {
	this.bdProd2EN06RootConParameters = bdProd2EN06RootConParameters;
}

public String[] getBdProd2MN01RootConParameters() {
	return bdProd2MN01RootConParameters;
}

public void setBdProd2MN01RootConParameters(String[] bdProd2MN01RootConParameters) {
	this.bdProd2MN01RootConParameters = bdProd2MN01RootConParameters;
}

public String[] getBdProd2MN02RootConParameters() {
	return bdProd2MN02RootConParameters;
}

public void setBdProd2MN02RootConParameters(String[] bdProd2MN02RootConParameters) {
	this.bdProd2MN02RootConParameters = bdProd2MN02RootConParameters;
}

public String[] getBdProd2DN01RootConParameters() {
	return bdProd2DN01RootConParameters;
}

public void setBdProd2DN01RootConParameters(String[] bdProd2DN01RootConParameters) {
	this.bdProd2DN01RootConParameters = bdProd2DN01RootConParameters;
}

public String[] getBdProd2DN02RootConParameters() {
	return bdProd2DN02RootConParameters;
}

public void setBdProd2DN02RootConParameters(String[] bdProd2DN02RootConParameters) {
	this.bdProd2DN02RootConParameters = bdProd2DN02RootConParameters;
}

public String[] getBdProd2DN03RootConParameters() {
	return bdProd2DN03RootConParameters;
}

public void setBdProd2DN03RootConParameters(String[] bdProd2DN03RootConParameters) {
	this.bdProd2DN03RootConParameters = bdProd2DN03RootConParameters;
}

public String[] getBdProd2DN04RootConParameters() {
	return bdProd2DN04RootConParameters;
}

public void setBdProd2DN04RootConParameters(String[] bdProd2DN04RootConParameters) {
	this.bdProd2DN04RootConParameters = bdProd2DN04RootConParameters;
}

public String[] getBdProd2DN05RootConParameters() {
	return bdProd2DN05RootConParameters;
}

public void setBdProd2DN05RootConParameters(String[] bdProd2DN05RootConParameters) {
	this.bdProd2DN05RootConParameters = bdProd2DN05RootConParameters;
}

public String[] getBdProd2DN06RootConParameters() {
	return bdProd2DN06RootConParameters;
}

public void setBdProd2DN06RootConParameters(String[] bdProd2DN06RootConParameters) {
	this.bdProd2DN06RootConParameters = bdProd2DN06RootConParameters;
}

public String[] getBdProd2DN07RootConParameters() {
	return bdProd2DN07RootConParameters;
}

public void setBdProd2DN07RootConParameters(String[] bdProd2DN07RootConParameters) {
	this.bdProd2DN07RootConParameters = bdProd2DN07RootConParameters;
}

public String[] getBdProd2DN08RootConParameters() {
	return bdProd2DN08RootConParameters;
}

public void setBdProd2DN08RootConParameters(String[] bdProd2DN08RootConParameters) {
	this.bdProd2DN08RootConParameters = bdProd2DN08RootConParameters;
}

public String[] getBdProd2DN09RootConParameters() {
	return bdProd2DN09RootConParameters;
}

public void setBdProd2DN09RootConParameters(String[] bdProd2DN09RootConParameters) {
	this.bdProd2DN09RootConParameters = bdProd2DN09RootConParameters;
}

public String[] getBdProd2DN10RootConParameters() {
	return bdProd2DN10RootConParameters;
}

public void setBdProd2DN10RootConParameters(String[] bdProd2DN10RootConParameters) {
	this.bdProd2DN10RootConParameters = bdProd2DN10RootConParameters;
}

public String[] getBdTest2EN01RootConParameters() {
	return bdTest2EN01RootConParameters;
}

public void setBdTest2EN01RootConParameters(String[] bdTest2EN01RootConParameters) {
	this.bdTest2EN01RootConParameters = bdTest2EN01RootConParameters;
}

public String[] getBdTest2EN02RootConParameters() {
	return bdTest2EN02RootConParameters;
}

public void setBdTest2EN02RootConParameters(String[] bdTest2EN02RootConParameters) {
	this.bdTest2EN02RootConParameters = bdTest2EN02RootConParameters;
}

public String[] getBdTest2EN03RootConParameters() {
	return bdTest2EN03RootConParameters;
}

public void setBdTest2EN03RootConParameters(String[] bdTest2EN03RootConParameters) {
	this.bdTest2EN03RootConParameters = bdTest2EN03RootConParameters;
}

public String[] getBdTest2EN04RootConParameters() {
	return bdTest2EN04RootConParameters;
}

public void setBdTest2EN04RootConParameters(String[] bdTest2EN04RootConParameters) {
	this.bdTest2EN04RootConParameters = bdTest2EN04RootConParameters;
}

public String[] getBdTest2EN05RootConParameters() {
	return bdTest2EN05RootConParameters;
}

public void setBdTest2EN05RootConParameters(String[] bdTest2EN05RootConParameters) {
	this.bdTest2EN05RootConParameters = bdTest2EN05RootConParameters;
}

public String[] getBdTest2MN01RootConParameters() {
	return bdTest2MN01RootConParameters;
}

public void setBdTest2MN01RootConParameters(String[] bdTest2MN01RootConParameters) {
	this.bdTest2MN01RootConParameters = bdTest2MN01RootConParameters;
}

public String[] getBdTest2MN02RootConParameters() {
	return bdTest2MN02RootConParameters;
}

public void setBdTest2MN02RootConParameters(String[] bdTest2MN02RootConParameters) {
	this.bdTest2MN02RootConParameters = bdTest2MN02RootConParameters;
}

public String[] getBdTest2DN01RootConParameters() {
	return bdTest2DN01RootConParameters;
}

public void setBdTest2DN01RootConParameters(String[] bdTest2DN01RootConParameters) {
	this.bdTest2DN01RootConParameters = bdTest2DN01RootConParameters;
}

public String[] getBdTest2DN02RootConParameters() {
	return bdTest2DN02RootConParameters;
}

public void setBdTest2DN02RootConParameters(String[] bdTest2DN02RootConParameters) {
	this.bdTest2DN02RootConParameters = bdTest2DN02RootConParameters;
}

public String[] getBdTest2DN03RootConParameters() {
	return bdTest2DN03RootConParameters;
}

public void setBdTest2DN03RootConParameters(String[] bdTest2DN03RootConParameters) {
	this.bdTest2DN03RootConParameters = bdTest2DN03RootConParameters;
}

public String[] getBdTest2DN04RootConParameters() {
	return bdTest2DN04RootConParameters;
}

public void setBdTest2DN04RootConParameters(String[] bdTest2DN04RootConParameters) {
	this.bdTest2DN04RootConParameters = bdTest2DN04RootConParameters;
}

public String[] getBdTest2DN05RootConParameters() {
	return bdTest2DN05RootConParameters;
}

public void setBdTest2DN05RootConParameters(String[] bdTest2DN05RootConParameters) {
	this.bdTest2DN05RootConParameters = bdTest2DN05RootConParameters;
}

public String[] getBdTest2DN06RootConParameters() {
	return bdTest2DN06RootConParameters;
}

public void setBdTest2DN06RootConParameters(String[] bdTest2DN06RootConParameters) {
	this.bdTest2DN06RootConParameters = bdTest2DN06RootConParameters;
}

public String[] getBdTest2DN07RootConParameters() {
	return bdTest2DN07RootConParameters;
}

public void setBdTest2DN07RootConParameters(String[] bdTest2DN07RootConParameters) {
	this.bdTest2DN07RootConParameters = bdTest2DN07RootConParameters;
}

public String[] getBdTest2DN08RootConParameters() {
	return bdTest2DN08RootConParameters;
}

public void setBdTest2DN08RootConParameters(String[] bdTest2DN08RootConParameters) {
	this.bdTest2DN08RootConParameters = bdTest2DN08RootConParameters;
}

public String[] getBdTest2DN09RootConParameters() {
	return bdTest2DN09RootConParameters;
}

public void setBdTest2DN09RootConParameters(String[] bdTest2DN09RootConParameters) {
	this.bdTest2DN09RootConParameters = bdTest2DN09RootConParameters;
}

public String[] getBdTest2DN10RootConParameters() {
	return bdTest2DN10RootConParameters;
}

public void setBdTest2DN10RootConParameters(String[] bdTest2DN10RootConParameters) {
	this.bdTest2DN10RootConParameters = bdTest2DN10RootConParameters;
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

public String[] getBdDev1AllNodesRootConParameters() {
	return bdDev1AllNodesRootConParameters;
}

public void setBdDev1AllNodesRootConParameters(
		String[] bdDev1AllNodesRootConParameters) {
	this.bdDev1AllNodesRootConParameters = bdDev1AllNodesRootConParameters;
}

public String[] getBdDev1AllNodesNonRootConParameters() {
	return bdDev1AllNodesNonRootConParameters;
}

public void setBdDev1AllNodesNonRootConParameters(
		String[] bdDev1AllNodesNonRootConParameters) {
	this.bdDev1AllNodesNonRootConParameters = bdDev1AllNodesNonRootConParameters;
}

public String[] getBdProd2AllNodesRootConParameters() {
	return bdProd2AllNodesRootConParameters;
}

public void setBdProd2AllNodesRootConParameters(
		String[] bdProd2AllNodesRootConParameters) {
	this.bdProd2AllNodesRootConParameters = bdProd2AllNodesRootConParameters;
}

public String[] getBdProd2AllNodesNonRootConParameters() {
	return bdProd2AllNodesNonRootConParameters;
}

public void setBdProd2AllNodesNonRootConParameters(
		String[] bdProd2AllNodesNonRootConParameters) {
	this.bdProd2AllNodesNonRootConParameters = bdProd2AllNodesNonRootConParameters;
}

public String[] getBdTest2AllNodesRootConParameters() {
	return bdTest2AllNodesRootConParameters;
}

public void setBdTest2AllNodesRootConParameters(
		String[] bdTest2AllNodesRootConParameters) {
	this.bdTest2AllNodesRootConParameters = bdTest2AllNodesRootConParameters;
}

public String[] getBdTest2AllNodesNonRootConParameters() {
	return bdTest2AllNodesNonRootConParameters;
}

public void setBdTest2AllNodesNonRootConParameters(
		String[] bdTest2AllNodesNonRootConParameters) {
	this.bdTest2AllNodesNonRootConParameters = bdTest2AllNodesNonRootConParameters;
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

public String[] getBdProd2EN07RootConParameters() {
	return bdProd2EN07RootConParameters;
}

public void setBdProd2EN07RootConParameters(String[] bdProd2EN07RootConParameters) {
	this.bdProd2EN07RootConParameters = bdProd2EN07RootConParameters;
}

public String[] getBdProd2EN08RootConParameters() {
	return bdProd2EN08RootConParameters;
}

public void setBdProd2EN08RootConParameters(String[] bdProd2EN08RootConParameters) {
	this.bdProd2EN08RootConParameters = bdProd2EN08RootConParameters;
}

public String[] getBdProd2EN09RootConParameters() {
	return bdProd2EN09RootConParameters;
}

public void setBdProd2EN09RootConParameters(String[] bdProd2EN09RootConParameters) {
	this.bdProd2EN09RootConParameters = bdProd2EN09RootConParameters;
}

public String[] getBdProd2EN10RootConParameters() {
	return bdProd2EN10RootConParameters;
}

public void setBdProd2EN10RootConParameters(String[] bdProd2EN10RootConParameters) {
	this.bdProd2EN10RootConParameters = bdProd2EN10RootConParameters;
}

public String[] getBdProd2EN11RootConParameters() {
	return bdProd2EN11RootConParameters;
}

public void setBdProd2EN11RootConParameters(String[] bdProd2EN11RootConParameters) {
	this.bdProd2EN11RootConParameters = bdProd2EN11RootConParameters;
}

public String[] getBdProd2EN07ConParameters() {
	return bdProd2EN07ConParameters;
}

public void setBdProd2EN07ConParameters(String[] bdProd2EN07ConParameters) {
	this.bdProd2EN07ConParameters = bdProd2EN07ConParameters;
}

public String[] getBdProd2EN08ConParameters() {
	return bdProd2EN08ConParameters;
}

public void setBdProd2EN08ConParameters(String[] bdProd2EN08ConParameters) {
	this.bdProd2EN08ConParameters = bdProd2EN08ConParameters;
}

public String[] getBdProd2EN09ConParameters() {
	return bdProd2EN09ConParameters;
}

public void setBdProd2EN09ConParameters(String[] bdProd2EN09ConParameters) {
	this.bdProd2EN09ConParameters = bdProd2EN09ConParameters;
}

public String[] getBdProd2EN10ConParameters() {
	return bdProd2EN10ConParameters;
}

public void setBdProd2EN10ConParameters(String[] bdProd2EN10ConParameters) {
	this.bdProd2EN10ConParameters = bdProd2EN10ConParameters;
}

public String[] getBdProd2EN11ConParameters() {
	return bdProd2EN11ConParameters;
}

public void setBdProd2EN11ConParameters(String[] bdProd2EN11ConParameters) {
	this.bdProd2EN11ConParameters = bdProd2EN11ConParameters;
}



public String[] getBdDev3AllNodesRootConParameters() {
	return bdDev3AllNodesRootConParameters;
}



public void setBdDev3AllNodesRootConParameters(String[] bdDev3AllNodesRootConParameters) {
	this.bdDev3AllNodesRootConParameters = bdDev3AllNodesRootConParameters;
}



public String[] getBdDev3AllNodesNonRootConParameters() {
	return bdDev3AllNodesNonRootConParameters;
}



public void setBdDev3AllNodesNonRootConParameters(String[] bdDev3AllNodesNonRootConParameters) {
	this.bdDev3AllNodesNonRootConParameters = bdDev3AllNodesNonRootConParameters;
}



public String[] getBdProd3AllNodesRootConParameters() {
	return bdProd3AllNodesRootConParameters;
}



public void setBdProd3AllNodesRootConParameters(String[] bdProd3AllNodesRootConParameters) {
	this.bdProd3AllNodesRootConParameters = bdProd3AllNodesRootConParameters;
}



public String[] getBdProd3AllNodesNonRootConParameters() {
	return bdProd3AllNodesNonRootConParameters;
}



public void setBdProd3AllNodesNonRootConParameters(String[] bdProd3AllNodesNonRootConParameters) {
	this.bdProd3AllNodesNonRootConParameters = bdProd3AllNodesNonRootConParameters;
}



public String[] getBdTest3AllNodesRootConParameters() {
	return bdTest3AllNodesRootConParameters;
}



public void setBdTest3AllNodesRootConParameters(String[] bdTest3AllNodesRootConParameters) {
	this.bdTest3AllNodesRootConParameters = bdTest3AllNodesRootConParameters;
}



public String[] getBdTest3AllNodesNonRootConParameters() {
	return bdTest3AllNodesNonRootConParameters;
}



public void setBdTest3AllNodesNonRootConParameters(String[] bdTest3AllNodesNonRootConParameters) {
	this.bdTest3AllNodesNonRootConParameters = bdTest3AllNodesNonRootConParameters;
}



public String[] getBdDev3EN01RootConParameters() {
	return bdDev3EN01RootConParameters;
}



public void setBdDev3EN01RootConParameters(String[] bdDev3EN01RootConParameters) {
	this.bdDev3EN01RootConParameters = bdDev3EN01RootConParameters;
}



public String[] getBdDev3EN02RootConParameters() {
	return bdDev3EN02RootConParameters;
}



public void setBdDev3EN02RootConParameters(String[] bdDev3EN02RootConParameters) {
	this.bdDev3EN02RootConParameters = bdDev3EN02RootConParameters;
}



public String[] getBdDev3EN03RootConParameters() {
	return bdDev3EN03RootConParameters;
}



public void setBdDev3EN03RootConParameters(String[] bdDev3EN03RootConParameters) {
	this.bdDev3EN03RootConParameters = bdDev3EN03RootConParameters;
}



public String[] getBdDev3EN04RootConParameters() {
	return bdDev3EN04RootConParameters;
}



public void setBdDev3EN04RootConParameters(String[] bdDev3EN04RootConParameters) {
	this.bdDev3EN04RootConParameters = bdDev3EN04RootConParameters;
}



public String[] getBdDev3EN05RootConParameters() {
	return bdDev3EN05RootConParameters;
}



public void setBdDev3EN05RootConParameters(String[] bdDev3EN05RootConParameters) {
	this.bdDev3EN05RootConParameters = bdDev3EN05RootConParameters;
}



public String[] getBdDev3MN01RootConParameters() {
	return bdDev3MN01RootConParameters;
}



public void setBdDev3MN01RootConParameters(String[] bdDev3MN01RootConParameters) {
	this.bdDev3MN01RootConParameters = bdDev3MN01RootConParameters;
}



public String[] getBdDev3MN02RootConParameters() {
	return bdDev3MN02RootConParameters;
}



public void setBdDev3MN02RootConParameters(String[] bdDev3MN02RootConParameters) {
	this.bdDev3MN02RootConParameters = bdDev3MN02RootConParameters;
}



public String[] getBdDev3MN03RootConParameters() {
	return bdDev3MN03RootConParameters;
}



public void setBdDev3MN03RootConParameters(String[] bdDev3MN03RootConParameters) {
	this.bdDev3MN03RootConParameters = bdDev3MN03RootConParameters;
}



public String[] getBdDev3DN01RootConParameters() {
	return bdDev3DN01RootConParameters;
}



public void setBdDev3DN01RootConParameters(String[] bdDev3DN01RootConParameters) {
	this.bdDev3DN01RootConParameters = bdDev3DN01RootConParameters;
}



public String[] getBdDev3DN02RootConParameters() {
	return bdDev3DN02RootConParameters;
}



public void setBdDev3DN02RootConParameters(String[] bdDev3DN02RootConParameters) {
	this.bdDev3DN02RootConParameters = bdDev3DN02RootConParameters;
}



public String[] getBdDev3DN03RootConParameters() {
	return bdDev3DN03RootConParameters;
}



public void setBdDev3DN03RootConParameters(String[] bdDev3DN03RootConParameters) {
	this.bdDev3DN03RootConParameters = bdDev3DN03RootConParameters;
}



public String[] getBdDev3DN04RootConParameters() {
	return bdDev3DN04RootConParameters;
}



public void setBdDev3DN04RootConParameters(String[] bdDev3DN04RootConParameters) {
	this.bdDev3DN04RootConParameters = bdDev3DN04RootConParameters;
}



public String[] getBdDev3DN05RootConParameters() {
	return bdDev3DN05RootConParameters;
}



public void setBdDev3DN05RootConParameters(String[] bdDev3DN05RootConParameters) {
	this.bdDev3DN05RootConParameters = bdDev3DN05RootConParameters;
}



public String[] getBdDev3EN01ConParameters() {
	return bdDev3EN01ConParameters;
}



public void setBdDev3EN01ConParameters(String[] bdDev3EN01ConParameters) {
	this.bdDev3EN01ConParameters = bdDev3EN01ConParameters;
}



public String[] getBdDev3EN02ConParameters() {
	return bdDev3EN02ConParameters;
}



public void setBdDev3EN02ConParameters(String[] bdDev3EN02ConParameters) {
	this.bdDev3EN02ConParameters = bdDev3EN02ConParameters;
}



public String[] getBdDev3EN03ConParameters() {
	return bdDev3EN03ConParameters;
}



public void setBdDev3EN03ConParameters(String[] bdDev3EN03ConParameters) {
	this.bdDev3EN03ConParameters = bdDev3EN03ConParameters;
}



public String[] getBdDev3EN04ConParameters() {
	return bdDev3EN04ConParameters;
}



public void setBdDev3EN04ConParameters(String[] bdDev3EN04ConParameters) {
	this.bdDev3EN04ConParameters = bdDev3EN04ConParameters;
}



public String[] getBdDev3EN05ConParameters() {
	return bdDev3EN05ConParameters;
}



public void setBdDev3EN05ConParameters(String[] bdDev3EN05ConParameters) {
	this.bdDev3EN05ConParameters = bdDev3EN05ConParameters;
}



public String[] getBdDev3MN01ConParameters() {
	return bdDev3MN01ConParameters;
}



public void setBdDev3MN01ConParameters(String[] bdDev3MN01ConParameters) {
	this.bdDev3MN01ConParameters = bdDev3MN01ConParameters;
}



public String[] getBdDev3MN02ConParameters() {
	return bdDev3MN02ConParameters;
}



public void setBdDev3MN02ConParameters(String[] bdDev3MN02ConParameters) {
	this.bdDev3MN02ConParameters = bdDev3MN02ConParameters;
}



public String[] getBdDev3MN03ConParameters() {
	return bdDev3MN03ConParameters;
}



public void setBdDev3MN03ConParameters(String[] bdDev3MN03ConParameters) {
	this.bdDev3MN03ConParameters = bdDev3MN03ConParameters;
}



public String[] getBdDev3DN01ConParameters() {
	return bdDev3DN01ConParameters;
}



public void setBdDev3DN01ConParameters(String[] bdDev3DN01ConParameters) {
	this.bdDev3DN01ConParameters = bdDev3DN01ConParameters;
}



public String[] getBdDev3DN02ConParameters() {
	return bdDev3DN02ConParameters;
}



public void setBdDev3DN02ConParameters(String[] bdDev3DN02ConParameters) {
	this.bdDev3DN02ConParameters = bdDev3DN02ConParameters;
}



public String[] getBdDev3DN03ConParameters() {
	return bdDev3DN03ConParameters;
}



public void setBdDev3DN03ConParameters(String[] bdDev3DN03ConParameters) {
	this.bdDev3DN03ConParameters = bdDev3DN03ConParameters;
}



public String[] getBdDev3DN04ConParameters() {
	return bdDev3DN04ConParameters;
}



public void setBdDev3DN04ConParameters(String[] bdDev3DN04ConParameters) {
	this.bdDev3DN04ConParameters = bdDev3DN04ConParameters;
}



public String[] getBdDev3DN05ConParameters() {
	return bdDev3DN05ConParameters;
}



public void setBdDev3DN05ConParameters(String[] bdDev3DN05ConParameters) {
	this.bdDev3DN05ConParameters = bdDev3DN05ConParameters;
}



public String[] getBdProd3EN01RootConParameters() {
	return bdProd3EN01RootConParameters;
}



public void setBdProd3EN01RootConParameters(String[] bdProd3EN01RootConParameters) {
	this.bdProd3EN01RootConParameters = bdProd3EN01RootConParameters;
}



public String[] getBdProd3EN02RootConParameters() {
	return bdProd3EN02RootConParameters;
}



public void setBdProd3EN02RootConParameters(String[] bdProd3EN02RootConParameters) {
	this.bdProd3EN02RootConParameters = bdProd3EN02RootConParameters;
}



public String[] getBdProd3EN03RootConParameters() {
	return bdProd3EN03RootConParameters;
}



public void setBdProd3EN03RootConParameters(String[] bdProd3EN03RootConParameters) {
	this.bdProd3EN03RootConParameters = bdProd3EN03RootConParameters;
}



public String[] getBdProd3EN04RootConParameters() {
	return bdProd3EN04RootConParameters;
}



public void setBdProd3EN04RootConParameters(String[] bdProd3EN04RootConParameters) {
	this.bdProd3EN04RootConParameters = bdProd3EN04RootConParameters;
}



public String[] getBdProd3EN05RootConParameters() {
	return bdProd3EN05RootConParameters;
}



public void setBdProd3EN05RootConParameters(String[] bdProd3EN05RootConParameters) {
	this.bdProd3EN05RootConParameters = bdProd3EN05RootConParameters;
}



public String[] getBdProd3EN06RootConParameters() {
	return bdProd3EN06RootConParameters;
}



public void setBdProd3EN06RootConParameters(String[] bdProd3EN06RootConParameters) {
	this.bdProd3EN06RootConParameters = bdProd3EN06RootConParameters;
}



public String[] getBdProd3EN07RootConParameters() {
	return bdProd3EN07RootConParameters;
}



public void setBdProd3EN07RootConParameters(String[] bdProd3EN07RootConParameters) {
	this.bdProd3EN07RootConParameters = bdProd3EN07RootConParameters;
}



public String[] getBdProd3EN08RootConParameters() {
	return bdProd3EN08RootConParameters;
}



public void setBdProd3EN08RootConParameters(String[] bdProd3EN08RootConParameters) {
	this.bdProd3EN08RootConParameters = bdProd3EN08RootConParameters;
}



public String[] getBdProd3MN01RootConParameters() {
	return bdProd3MN01RootConParameters;
}



public void setBdProd3MN01RootConParameters(String[] bdProd3MN01RootConParameters) {
	this.bdProd3MN01RootConParameters = bdProd3MN01RootConParameters;
}



public String[] getBdProd3MN02RootConParameters() {
	return bdProd3MN02RootConParameters;
}



public void setBdProd3MN02RootConParameters(String[] bdProd3MN02RootConParameters) {
	this.bdProd3MN02RootConParameters = bdProd3MN02RootConParameters;
}



public String[] getBdProd3MN03RootConParameters() {
	return bdProd3MN03RootConParameters;
}



public void setBdProd3MN03RootConParameters(String[] bdProd3MN03RootConParameters) {
	this.bdProd3MN03RootConParameters = bdProd3MN03RootConParameters;
}



public String[] getBdProd3DN01RootConParameters() {
	return bdProd3DN01RootConParameters;
}



public void setBdProd3DN01RootConParameters(String[] bdProd3DN01RootConParameters) {
	this.bdProd3DN01RootConParameters = bdProd3DN01RootConParameters;
}



public String[] getBdProd3DN02RootConParameters() {
	return bdProd3DN02RootConParameters;
}



public void setBdProd3DN02RootConParameters(String[] bdProd3DN02RootConParameters) {
	this.bdProd3DN02RootConParameters = bdProd3DN02RootConParameters;
}



public String[] getBdProd3DN03RootConParameters() {
	return bdProd3DN03RootConParameters;
}



public void setBdProd3DN03RootConParameters(String[] bdProd3DN03RootConParameters) {
	this.bdProd3DN03RootConParameters = bdProd3DN03RootConParameters;
}



public String[] getBdProd3DN04RootConParameters() {
	return bdProd3DN04RootConParameters;
}



public void setBdProd3DN04RootConParameters(String[] bdProd3DN04RootConParameters) {
	this.bdProd3DN04RootConParameters = bdProd3DN04RootConParameters;
}



public String[] getBdProd3DN05RootConParameters() {
	return bdProd3DN05RootConParameters;
}



public void setBdProd3DN05RootConParameters(String[] bdProd3DN05RootConParameters) {
	this.bdProd3DN05RootConParameters = bdProd3DN05RootConParameters;
}



public String[] getBdProd3DN32RootConParameters() {
	return bdProd3DN32RootConParameters;
}



public void setBdProd3DN32RootConParameters(String[] bdProd3DN32RootConParameters) {
	this.bdProd3DN32RootConParameters = bdProd3DN32RootConParameters;
}



public String[] getBdProd3EN01ConParameters() {
	return bdProd3EN01ConParameters;
}



public void setBdProd3EN01ConParameters(String[] bdProd3EN01ConParameters) {
	this.bdProd3EN01ConParameters = bdProd3EN01ConParameters;
}



public String[] getBdProd3EN02ConParameters() {
	return bdProd3EN02ConParameters;
}



public void setBdProd3EN02ConParameters(String[] bdProd3EN02ConParameters) {
	this.bdProd3EN02ConParameters = bdProd3EN02ConParameters;
}



public String[] getBdProd3EN03ConParameters() {
	return bdProd3EN03ConParameters;
}



public void setBdProd3EN03ConParameters(String[] bdProd3EN03ConParameters) {
	this.bdProd3EN03ConParameters = bdProd3EN03ConParameters;
}



public String[] getBdProd3EN04ConParameters() {
	return bdProd3EN04ConParameters;
}



public void setBdProd3EN04ConParameters(String[] bdProd3EN04ConParameters) {
	this.bdProd3EN04ConParameters = bdProd3EN04ConParameters;
}



public String[] getBdProd3EN05ConParameters() {
	return bdProd3EN05ConParameters;
}



public void setBdProd3EN05ConParameters(String[] bdProd3EN05ConParameters) {
	this.bdProd3EN05ConParameters = bdProd3EN05ConParameters;
}



public String[] getBdProd3EN06ConParameters() {
	return bdProd3EN06ConParameters;
}



public void setBdProd3EN06ConParameters(String[] bdProd3EN06ConParameters) {
	this.bdProd3EN06ConParameters = bdProd3EN06ConParameters;
}



public String[] getBdProd3EN07ConParameters() {
	return bdProd3EN07ConParameters;
}



public void setBdProd3EN07ConParameters(String[] bdProd3EN07ConParameters) {
	this.bdProd3EN07ConParameters = bdProd3EN07ConParameters;
}



public String[] getBdProd3EN08ConParameters() {
	return bdProd3EN08ConParameters;
}



public void setBdProd3EN08ConParameters(String[] bdProd3EN08ConParameters) {
	this.bdProd3EN08ConParameters = bdProd3EN08ConParameters;
}



public String[] getBdProd3MN01ConParameters() {
	return bdProd3MN01ConParameters;
}



public void setBdProd3MN01ConParameters(String[] bdProd3MN01ConParameters) {
	this.bdProd3MN01ConParameters = bdProd3MN01ConParameters;
}



public String[] getBdProd3MN02ConParameters() {
	return bdProd3MN02ConParameters;
}



public void setBdProd3MN02ConParameters(String[] bdProd3MN02ConParameters) {
	this.bdProd3MN02ConParameters = bdProd3MN02ConParameters;
}



public String[] getBdProd3MN03ConParameters() {
	return bdProd3MN03ConParameters;
}



public void setBdProd3MN03ConParameters(String[] bdProd3MN03ConParameters) {
	this.bdProd3MN03ConParameters = bdProd3MN03ConParameters;
}



public String[] getBdProd3DN01ConParameters() {
	return bdProd3DN01ConParameters;
}



public void setBdProd3DN01ConParameters(String[] bdProd3DN01ConParameters) {
	this.bdProd3DN01ConParameters = bdProd3DN01ConParameters;
}



public String[] getBdProd3DN02ConParameters() {
	return bdProd3DN02ConParameters;
}



public void setBdProd3DN02ConParameters(String[] bdProd3DN02ConParameters) {
	this.bdProd3DN02ConParameters = bdProd3DN02ConParameters;
}



public String[] getBdProd3DN03ConParameters() {
	return bdProd3DN03ConParameters;
}



public void setBdProd3DN03ConParameters(String[] bdProd3DN03ConParameters) {
	this.bdProd3DN03ConParameters = bdProd3DN03ConParameters;
}



public String[] getBdProd3DN04ConParameters() {
	return bdProd3DN04ConParameters;
}



public void setBdProd3DN04ConParameters(String[] bdProd3DN04ConParameters) {
	this.bdProd3DN04ConParameters = bdProd3DN04ConParameters;
}



public String[] getBdProd3DN05ConParameters() {
	return bdProd3DN05ConParameters;
}



public void setBdProd3DN05ConParameters(String[] bdProd3DN05ConParameters) {
	this.bdProd3DN05ConParameters = bdProd3DN05ConParameters;
}



public String[] getBdProd3DN32ConParameters() {
	return bdProd3DN32ConParameters;
}



public void setBdProd3DN32ConParameters(String[] bdProd3DN32ConParameters) {
	this.bdProd3DN32ConParameters = bdProd3DN32ConParameters;
}



public String[] getBdTest3EN01RootConParameters() {
	return bdTest3EN01RootConParameters;
}



public void setBdTest3EN01RootConParameters(String[] bdTest3EN01RootConParameters) {
	this.bdTest3EN01RootConParameters = bdTest3EN01RootConParameters;
}



public String[] getBdTest3EN02RootConParameters() {
	return bdTest3EN02RootConParameters;
}



public void setBdTest3EN02RootConParameters(String[] bdTest3EN02RootConParameters) {
	this.bdTest3EN02RootConParameters = bdTest3EN02RootConParameters;
}



public String[] getBdTest3EN03RootConParameters() {
	return bdTest3EN03RootConParameters;
}



public void setBdTest3EN03RootConParameters(String[] bdTest3EN03RootConParameters) {
	this.bdTest3EN03RootConParameters = bdTest3EN03RootConParameters;
}



public String[] getBdTest3MN01RootConParameters() {
	return bdTest3MN01RootConParameters;
}



public void setBdTest3MN01RootConParameters(String[] bdTest3MN01RootConParameters) {
	this.bdTest3MN01RootConParameters = bdTest3MN01RootConParameters;
}



public String[] getBdTest3MN02RootConParameters() {
	return bdTest3MN02RootConParameters;
}



public void setBdTest3MN02RootConParameters(String[] bdTest3MN02RootConParameters) {
	this.bdTest3MN02RootConParameters = bdTest3MN02RootConParameters;
}



public String[] getBdTest3MN03RootConParameters() {
	return bdTest3MN03RootConParameters;
}



public void setBdTest3MN03RootConParameters(String[] bdTest3MN03RootConParameters) {
	this.bdTest3MN03RootConParameters = bdTest3MN03RootConParameters;
}



public String[] getBdTest3DN01RootConParameters() {
	return bdTest3DN01RootConParameters;
}



public void setBdTest3DN01RootConParameters(String[] bdTest3DN01RootConParameters) {
	this.bdTest3DN01RootConParameters = bdTest3DN01RootConParameters;
}



public String[] getBdTest3DN02RootConParameters() {
	return bdTest3DN02RootConParameters;
}



public void setBdTest3DN02RootConParameters(String[] bdTest3DN02RootConParameters) {
	this.bdTest3DN02RootConParameters = bdTest3DN02RootConParameters;
}



public String[] getBdTest3DN03RootConParameters() {
	return bdTest3DN03RootConParameters;
}



public void setBdTest3DN03RootConParameters(String[] bdTest3DN03RootConParameters) {
	this.bdTest3DN03RootConParameters = bdTest3DN03RootConParameters;
}



public String[] getBdTest3DN04RootConParameters() {
	return bdTest3DN04RootConParameters;
}



public void setBdTest3DN04RootConParameters(String[] bdTest3DN04RootConParameters) {
	this.bdTest3DN04RootConParameters = bdTest3DN04RootConParameters;
}



public String[] getBdTest3DN05RootConParameters() {
	return bdTest3DN05RootConParameters;
}



public void setBdTest3DN05RootConParameters(String[] bdTest3DN05RootConParameters) {
	this.bdTest3DN05RootConParameters = bdTest3DN05RootConParameters;
}



public String[] getBdTest3DN06RootConParameters() {
	return bdTest3DN06RootConParameters;
}



public void setBdTest3DN06RootConParameters(String[] bdTest3DN06RootConParameters) {
	this.bdTest3DN06RootConParameters = bdTest3DN06RootConParameters;
}



public String[] getBdTest3DN07RootConParameters() {
	return bdTest3DN07RootConParameters;
}



public void setBdTest3DN07RootConParameters(String[] bdTest3DN07RootConParameters) {
	this.bdTest3DN07RootConParameters = bdTest3DN07RootConParameters;
}



public String[] getBdTest3DN08RootConParameters() {
	return bdTest3DN08RootConParameters;
}



public void setBdTest3DN08RootConParameters(String[] bdTest3DN08RootConParameters) {
	this.bdTest3DN08RootConParameters = bdTest3DN08RootConParameters;
}



public String[] getBdTest3DN09RootConParameters() {
	return bdTest3DN09RootConParameters;
}



public void setBdTest3DN09RootConParameters(String[] bdTest3DN09RootConParameters) {
	this.bdTest3DN09RootConParameters = bdTest3DN09RootConParameters;
}



public String[] getBdTest3EN01ConParameters() {
	return bdTest3EN01ConParameters;
}



public void setBdTest3EN01ConParameters(String[] bdTest3EN01ConParameters) {
	this.bdTest3EN01ConParameters = bdTest3EN01ConParameters;
}



public String[] getBdTest3EN02ConParameters() {
	return bdTest3EN02ConParameters;
}



public void setBdTest3EN02ConParameters(String[] bdTest3EN02ConParameters) {
	this.bdTest3EN02ConParameters = bdTest3EN02ConParameters;
}



public String[] getBdTest3EN03ConParameters() {
	return bdTest3EN03ConParameters;
}



public void setBdTest3EN03ConParameters(String[] bdTest3EN03ConParameters) {
	this.bdTest3EN03ConParameters = bdTest3EN03ConParameters;
}



public String[] getBdTest3MN01ConParameters() {
	return bdTest3MN01ConParameters;
}



public void setBdTest3MN01ConParameters(String[] bdTest3MN01ConParameters) {
	this.bdTest3MN01ConParameters = bdTest3MN01ConParameters;
}



public String[] getBdTest3MN02ConParameters() {
	return bdTest3MN02ConParameters;
}



public void setBdTest3MN02ConParameters(String[] bdTest3MN02ConParameters) {
	this.bdTest3MN02ConParameters = bdTest3MN02ConParameters;
}



public String[] getBdTest3MN03ConParameters() {
	return bdTest3MN03ConParameters;
}



public void setBdTest3MN03ConParameters(String[] bdTest3MN03ConParameters) {
	this.bdTest3MN03ConParameters = bdTest3MN03ConParameters;
}



public String[] getBdTest3DN01ConParameters() {
	return bdTest3DN01ConParameters;
}



public void setBdTest3DN01ConParameters(String[] bdTest3DN01ConParameters) {
	this.bdTest3DN01ConParameters = bdTest3DN01ConParameters;
}



public String[] getBdTest3DN02ConParameters() {
	return bdTest3DN02ConParameters;
}



public void setBdTest3DN02ConParameters(String[] bdTest3DN02ConParameters) {
	this.bdTest3DN02ConParameters = bdTest3DN02ConParameters;
}



public String[] getBdTest3DN03ConParameters() {
	return bdTest3DN03ConParameters;
}



public void setBdTest3DN03ConParameters(String[] bdTest3DN03ConParameters) {
	this.bdTest3DN03ConParameters = bdTest3DN03ConParameters;
}



public String[] getBdTest3DN04ConParameters() {
	return bdTest3DN04ConParameters;
}



public void setBdTest3DN04ConParameters(String[] bdTest3DN04ConParameters) {
	this.bdTest3DN04ConParameters = bdTest3DN04ConParameters;
}



public String[] getBdTest3DN05ConParameters() {
	return bdTest3DN05ConParameters;
}



public void setBdTest3DN05ConParameters(String[] bdTest3DN05ConParameters) {
	this.bdTest3DN05ConParameters = bdTest3DN05ConParameters;
}



public String[] getBdTest3DN06ConParameters() {
	return bdTest3DN06ConParameters;
}



public void setBdTest3DN06ConParameters(String[] bdTest3DN06ConParameters) {
	this.bdTest3DN06ConParameters = bdTest3DN06ConParameters;
}



public String[] getBdTest3DN07ConParameters() {
	return bdTest3DN07ConParameters;
}



public void setBdTest3DN07ConParameters(String[] bdTest3DN07ConParameters) {
	this.bdTest3DN07ConParameters = bdTest3DN07ConParameters;
}



public String[] getBdTest3DN08ConParameters() {
	return bdTest3DN08ConParameters;
}



public void setBdTest3DN08ConParameters(String[] bdTest3DN08ConParameters) {
	this.bdTest3DN08ConParameters = bdTest3DN08ConParameters;
}



public String[] getBdTest3DN09ConParameters() {
	return bdTest3DN09ConParameters;
}



public void setBdTest3DN09ConParameters(String[] bdTest3DN09ConParameters) {
	this.bdTest3DN09ConParameters = bdTest3DN09ConParameters;
}


public String[] getBdDev1MN03RootConParameters() {
	return bdDev1MN03RootConParameters;
}



public void setBdDev1MN03RootConParameters(String[] bdDev1MN03RootConParameters) {
	this.bdDev1MN03RootConParameters = bdDev1MN03RootConParameters;
}



public String[] getBdDev1MN03ConParameters() {
	return bdDev1MN03ConParameters;
}



public void setBdDev1MN03ConParameters(String[] bdDev1MN03ConParameters) {
	this.bdDev1MN03ConParameters = bdDev1MN03ConParameters;
}



public String[] getBdProd2MN03RootConParameters() {
	return bdProd2MN03RootConParameters;
}



public void setBdProd2MN03RootConParameters(String[] bdProd2MN03RootConParameters) {
	this.bdProd2MN03RootConParameters = bdProd2MN03RootConParameters;
}



public String[] getBdProd2MN03ConParameters() {
	return bdProd2MN03ConParameters;
}



public void setBdProd2MN03ConParameters(String[] bdProd2MN03ConParameters) {
	this.bdProd2MN03ConParameters = bdProd2MN03ConParameters;
}



public String[] getBdProd2EN12RootConParameters() {
	return bdProd2EN12RootConParameters;
}



public void setBdProd2EN12RootConParameters(String[] bdProd2EN12RootConParameters) {
	this.bdProd2EN12RootConParameters = bdProd2EN12RootConParameters;
}



public String[] getBdProd2EN13RootConParameters() {
	return bdProd2EN13RootConParameters;
}



public void setBdProd2EN13RootConParameters(String[] bdProd2EN13RootConParameters) {
	this.bdProd2EN13RootConParameters = bdProd2EN13RootConParameters;
}



public String[] getBdProd2EN14RootConParameters() {
	return bdProd2EN14RootConParameters;
}



public void setBdProd2EN14RootConParameters(String[] bdProd2EN14RootConParameters) {
	this.bdProd2EN14RootConParameters = bdProd2EN14RootConParameters;
}



public String[] getBdProd2EN15RootConParameters() {
	return bdProd2EN15RootConParameters;
}



public void setBdProd2EN15RootConParameters(String[] bdProd2EN15RootConParameters) {
	this.bdProd2EN15RootConParameters = bdProd2EN15RootConParameters;
}



public String[] getBdProd2EN16RootConParameters() {
	return bdProd2EN16RootConParameters;
}



public void setBdProd2EN16RootConParameters(String[] bdProd2EN16RootConParameters) {
	this.bdProd2EN16RootConParameters = bdProd2EN16RootConParameters;
}



public String[] getBdProd2EN17RootConParameters() {
	return bdProd2EN17RootConParameters;
}



public void setBdProd2EN17RootConParameters(String[] bdProd2EN17RootConParameters) {
	this.bdProd2EN17RootConParameters = bdProd2EN17RootConParameters;
}



public String[] getBdProd2EN18RootConParameters() {
	return bdProd2EN18RootConParameters;
}



public void setBdProd2EN18RootConParameters(String[] bdProd2EN18RootConParameters) {
	this.bdProd2EN18RootConParameters = bdProd2EN18RootConParameters;
}



public String[] getBdProd2EN19RootConParameters() {
	return bdProd2EN19RootConParameters;
}



public void setBdProd2EN19RootConParameters(String[] bdProd2EN19RootConParameters) {
	this.bdProd2EN19RootConParameters = bdProd2EN19RootConParameters;
}



public String[] getBdProd2EN20RootConParameters() {
	return bdProd2EN20RootConParameters;
}



public void setBdProd2EN20RootConParameters(String[] bdProd2EN20RootConParameters) {
	this.bdProd2EN20RootConParameters = bdProd2EN20RootConParameters;
}



public String[] getBdProd2DN11RootConParameters() {
	return bdProd2DN11RootConParameters;
}



public void setBdProd2DN11RootConParameters(String[] bdProd2DN11RootConParameters) {
	this.bdProd2DN11RootConParameters = bdProd2DN11RootConParameters;
}



public String[] getBdProd2DN12RootConParameters() {
	return bdProd2DN12RootConParameters;
}



public void setBdProd2DN12RootConParameters(String[] bdProd2DN12RootConParameters) {
	this.bdProd2DN12RootConParameters = bdProd2DN12RootConParameters;
}



public String[] getBdProd2DN13RootConParameters() {
	return bdProd2DN13RootConParameters;
}



public void setBdProd2DN13RootConParameters(String[] bdProd2DN13RootConParameters) {
	this.bdProd2DN13RootConParameters = bdProd2DN13RootConParameters;
}



public String[] getBdProd2DN14RootConParameters() {
	return bdProd2DN14RootConParameters;
}



public void setBdProd2DN14RootConParameters(String[] bdProd2DN14RootConParameters) {
	this.bdProd2DN14RootConParameters = bdProd2DN14RootConParameters;
}



public String[] getBdProd2DN15RootConParameters() {
	return bdProd2DN15RootConParameters;
}



public void setBdProd2DN15RootConParameters(String[] bdProd2DN15RootConParameters) {
	this.bdProd2DN15RootConParameters = bdProd2DN15RootConParameters;
}



public String[] getBdProd2DN16RootConParameters() {
	return bdProd2DN16RootConParameters;
}



public void setBdProd2DN16RootConParameters(String[] bdProd2DN16RootConParameters) {
	this.bdProd2DN16RootConParameters = bdProd2DN16RootConParameters;
}



public String[] getBdProd2DN17RootConParameters() {
	return bdProd2DN17RootConParameters;
}



public void setBdProd2DN17RootConParameters(String[] bdProd2DN17RootConParameters) {
	this.bdProd2DN17RootConParameters = bdProd2DN17RootConParameters;
}



public String[] getBdProd2DN18RootConParameters() {
	return bdProd2DN18RootConParameters;
}



public void setBdProd2DN18RootConParameters(String[] bdProd2DN18RootConParameters) {
	this.bdProd2DN18RootConParameters = bdProd2DN18RootConParameters;
}



public String[] getBdProd2DN19RootConParameters() {
	return bdProd2DN19RootConParameters;
}



public void setBdProd2DN19RootConParameters(String[] bdProd2DN19RootConParameters) {
	this.bdProd2DN19RootConParameters = bdProd2DN19RootConParameters;
}



public String[] getBdProd2DN20RootConParameters() {
	return bdProd2DN20RootConParameters;
}



public void setBdProd2DN20RootConParameters(String[] bdProd2DN20RootConParameters) {
	this.bdProd2DN20RootConParameters = bdProd2DN20RootConParameters;
}



public String[] getBdProd2DN21RootConParameters() {
	return bdProd2DN21RootConParameters;
}



public void setBdProd2DN21RootConParameters(String[] bdProd2DN21RootConParameters) {
	this.bdProd2DN21RootConParameters = bdProd2DN21RootConParameters;
}



public String[] getBdProd2DN22RootConParameters() {
	return bdProd2DN22RootConParameters;
}



public void setBdProd2DN22RootConParameters(String[] bdProd2DN22RootConParameters) {
	this.bdProd2DN22RootConParameters = bdProd2DN22RootConParameters;
}



public String[] getBdProd2DN23RootConParameters() {
	return bdProd2DN23RootConParameters;
}



public void setBdProd2DN23RootConParameters(String[] bdProd2DN23RootConParameters) {
	this.bdProd2DN23RootConParameters = bdProd2DN23RootConParameters;
}



public String[] getBdProd2DN24RootConParameters() {
	return bdProd2DN24RootConParameters;
}



public void setBdProd2DN24RootConParameters(String[] bdProd2DN24RootConParameters) {
	this.bdProd2DN24RootConParameters = bdProd2DN24RootConParameters;
}



public String[] getBdProd2DN25RootConParameters() {
	return bdProd2DN25RootConParameters;
}



public void setBdProd2DN25RootConParameters(String[] bdProd2DN25RootConParameters) {
	this.bdProd2DN25RootConParameters = bdProd2DN25RootConParameters;
}



public String[] getBdProd2DN26RootConParameters() {
	return bdProd2DN26RootConParameters;
}



public void setBdProd2DN26RootConParameters(String[] bdProd2DN26RootConParameters) {
	this.bdProd2DN26RootConParameters = bdProd2DN26RootConParameters;
}



public String[] getBdProd2DN27RootConParameters() {
	return bdProd2DN27RootConParameters;
}



public void setBdProd2DN27RootConParameters(String[] bdProd2DN27RootConParameters) {
	this.bdProd2DN27RootConParameters = bdProd2DN27RootConParameters;
}



public String[] getBdProd2DN28RootConParameters() {
	return bdProd2DN28RootConParameters;
}



public void setBdProd2DN28RootConParameters(String[] bdProd2DN28RootConParameters) {
	this.bdProd2DN28RootConParameters = bdProd2DN28RootConParameters;
}



public String[] getBdProd2DN29RootConParameters() {
	return bdProd2DN29RootConParameters;
}



public void setBdProd2DN29RootConParameters(String[] bdProd2DN29RootConParameters) {
	this.bdProd2DN29RootConParameters = bdProd2DN29RootConParameters;
}



public String[] getBdProd2DN30RootConParameters() {
	return bdProd2DN30RootConParameters;
}



public void setBdProd2DN30RootConParameters(String[] bdProd2DN30RootConParameters) {
	this.bdProd2DN30RootConParameters = bdProd2DN30RootConParameters;
}



public String[] getBdProd2DN31RootConParameters() {
	return bdProd2DN31RootConParameters;
}



public void setBdProd2DN31RootConParameters(String[] bdProd2DN31RootConParameters) {
	this.bdProd2DN31RootConParameters = bdProd2DN31RootConParameters;
}



public String[] getBdProd2DN32RootConParameters() {
	return bdProd2DN32RootConParameters;
}



public void setBdProd2DN32RootConParameters(String[] bdProd2DN32RootConParameters) {
	this.bdProd2DN32RootConParameters = bdProd2DN32RootConParameters;
}



public String[] getBdProd2EN12ConParameters() {
	return bdProd2EN12ConParameters;
}



public void setBdProd2EN12ConParameters(String[] bdProd2EN12ConParameters) {
	this.bdProd2EN12ConParameters = bdProd2EN12ConParameters;
}



public String[] getBdProd2EN13ConParameters() {
	return bdProd2EN13ConParameters;
}



public void setBdProd2EN13ConParameters(String[] bdProd2EN13ConParameters) {
	this.bdProd2EN13ConParameters = bdProd2EN13ConParameters;
}



public String[] getBdProd2EN14ConParameters() {
	return bdProd2EN14ConParameters;
}



public void setBdProd2EN14ConParameters(String[] bdProd2EN14ConParameters) {
	this.bdProd2EN14ConParameters = bdProd2EN14ConParameters;
}



public String[] getBdProd2EN15ConParameters() {
	return bdProd2EN15ConParameters;
}



public void setBdProd2EN15ConParameters(String[] bdProd2EN15ConParameters) {
	this.bdProd2EN15ConParameters = bdProd2EN15ConParameters;
}



public String[] getBdProd2EN16ConParameters() {
	return bdProd2EN16ConParameters;
}



public void setBdProd2EN16ConParameters(String[] bdProd2EN16ConParameters) {
	this.bdProd2EN16ConParameters = bdProd2EN16ConParameters;
}



public String[] getBdProd2EN17ConParameters() {
	return bdProd2EN17ConParameters;
}



public void setBdProd2EN17ConParameters(String[] bdProd2EN17ConParameters) {
	this.bdProd2EN17ConParameters = bdProd2EN17ConParameters;
}



public String[] getBdProd2EN18ConParameters() {
	return bdProd2EN18ConParameters;
}



public void setBdProd2EN18ConParameters(String[] bdProd2EN18ConParameters) {
	this.bdProd2EN18ConParameters = bdProd2EN18ConParameters;
}



public String[] getBdProd2EN19ConParameters() {
	return bdProd2EN19ConParameters;
}



public void setBdProd2EN19ConParameters(String[] bdProd2EN19ConParameters) {
	this.bdProd2EN19ConParameters = bdProd2EN19ConParameters;
}



public String[] getBdProd2EN20ConParameters() {
	return bdProd2EN20ConParameters;
}



public void setBdProd2EN20ConParameters(String[] bdProd2EN20ConParameters) {
	this.bdProd2EN20ConParameters = bdProd2EN20ConParameters;
}



public String[] getBdProd2DN11ConParameters() {
	return bdProd2DN11ConParameters;
}



public void setBdProd2DN11ConParameters(String[] bdProd2DN11ConParameters) {
	this.bdProd2DN11ConParameters = bdProd2DN11ConParameters;
}



public String[] getBdProd2DN12ConParameters() {
	return bdProd2DN12ConParameters;
}



public void setBdProd2DN12ConParameters(String[] bdProd2DN12ConParameters) {
	this.bdProd2DN12ConParameters = bdProd2DN12ConParameters;
}



public String[] getBdProd2DN13ConParameters() {
	return bdProd2DN13ConParameters;
}



public void setBdProd2DN13ConParameters(String[] bdProd2DN13ConParameters) {
	this.bdProd2DN13ConParameters = bdProd2DN13ConParameters;
}



public String[] getBdProd2DN14ConParameters() {
	return bdProd2DN14ConParameters;
}



public void setBdProd2DN14ConParameters(String[] bdProd2DN14ConParameters) {
	this.bdProd2DN14ConParameters = bdProd2DN14ConParameters;
}



public String[] getBdProd2DN15ConParameters() {
	return bdProd2DN15ConParameters;
}



public void setBdProd2DN15ConParameters(String[] bdProd2DN15ConParameters) {
	this.bdProd2DN15ConParameters = bdProd2DN15ConParameters;
}



public String[] getBdProd2DN16ConParameters() {
	return bdProd2DN16ConParameters;
}



public void setBdProd2DN16ConParameters(String[] bdProd2DN16ConParameters) {
	this.bdProd2DN16ConParameters = bdProd2DN16ConParameters;
}



public String[] getBdProd2DN17ConParameters() {
	return bdProd2DN17ConParameters;
}



public void setBdProd2DN17ConParameters(String[] bdProd2DN17ConParameters) {
	this.bdProd2DN17ConParameters = bdProd2DN17ConParameters;
}



public String[] getBdProd2DN18ConParameters() {
	return bdProd2DN18ConParameters;
}



public void setBdProd2DN18ConParameters(String[] bdProd2DN18ConParameters) {
	this.bdProd2DN18ConParameters = bdProd2DN18ConParameters;
}



public String[] getBdProd2DN19ConParameters() {
	return bdProd2DN19ConParameters;
}



public void setBdProd2DN19ConParameters(String[] bdProd2DN19ConParameters) {
	this.bdProd2DN19ConParameters = bdProd2DN19ConParameters;
}



public String[] getBdProd2DN20ConParameters() {
	return bdProd2DN20ConParameters;
}



public void setBdProd2DN20ConParameters(String[] bdProd2DN20ConParameters) {
	this.bdProd2DN20ConParameters = bdProd2DN20ConParameters;
}



public String[] getBdProd2DN21ConParameters() {
	return bdProd2DN21ConParameters;
}



public void setBdProd2DN21ConParameters(String[] bdProd2DN21ConParameters) {
	this.bdProd2DN21ConParameters = bdProd2DN21ConParameters;
}



public String[] getBdProd2DN22ConParameters() {
	return bdProd2DN22ConParameters;
}



public void setBdProd2DN22ConParameters(String[] bdProd2DN22ConParameters) {
	this.bdProd2DN22ConParameters = bdProd2DN22ConParameters;
}



public String[] getBdProd2DN23ConParameters() {
	return bdProd2DN23ConParameters;
}



public void setBdProd2DN23ConParameters(String[] bdProd2DN23ConParameters) {
	this.bdProd2DN23ConParameters = bdProd2DN23ConParameters;
}



public String[] getBdProd2DN24ConParameters() {
	return bdProd2DN24ConParameters;
}



public void setBdProd2DN24ConParameters(String[] bdProd2DN24ConParameters) {
	this.bdProd2DN24ConParameters = bdProd2DN24ConParameters;
}



public String[] getBdProd2DN25ConParameters() {
	return bdProd2DN25ConParameters;
}



public void setBdProd2DN25ConParameters(String[] bdProd2DN25ConParameters) {
	this.bdProd2DN25ConParameters = bdProd2DN25ConParameters;
}



public String[] getBdProd2DN26ConParameters() {
	return bdProd2DN26ConParameters;
}



public void setBdProd2DN26ConParameters(String[] bdProd2DN26ConParameters) {
	this.bdProd2DN26ConParameters = bdProd2DN26ConParameters;
}



public String[] getBdProd2DN27ConParameters() {
	return bdProd2DN27ConParameters;
}



public void setBdProd2DN27ConParameters(String[] bdProd2DN27ConParameters) {
	this.bdProd2DN27ConParameters = bdProd2DN27ConParameters;
}



public String[] getBdProd2DN28ConParameters() {
	return bdProd2DN28ConParameters;
}



public void setBdProd2DN28ConParameters(String[] bdProd2DN28ConParameters) {
	this.bdProd2DN28ConParameters = bdProd2DN28ConParameters;
}



public String[] getBdProd2DN29ConParameters() {
	return bdProd2DN29ConParameters;
}



public void setBdProd2DN29ConParameters(String[] bdProd2DN29ConParameters) {
	this.bdProd2DN29ConParameters = bdProd2DN29ConParameters;
}



public String[] getBdProd2DN30ConParameters() {
	return bdProd2DN30ConParameters;
}



public void setBdProd2DN30ConParameters(String[] bdProd2DN30ConParameters) {
	this.bdProd2DN30ConParameters = bdProd2DN30ConParameters;
}



public String[] getBdProd2DN31ConParameters() {
	return bdProd2DN31ConParameters;
}



public void setBdProd2DN31ConParameters(String[] bdProd2DN31ConParameters) {
	this.bdProd2DN31ConParameters = bdProd2DN31ConParameters;
}



public String[] getBdProd2DN32ConParameters() {
	return bdProd2DN32ConParameters;
}



public void setBdProd2DN32ConParameters(String[] bdProd2DN32ConParameters) {
	this.bdProd2DN32ConParameters = bdProd2DN32ConParameters;
}



//public String[] getHwSdbxNodeConParameters() {
//	return hwSdbxNodeConParameters;
//}
//
//public void setHwSdbxNodeConParameters(String[] hwSdbxNodeConParameters) {
//	this.hwSdbxNodeConParameters = hwSdbxNodeConParameters;
//}


}//end class