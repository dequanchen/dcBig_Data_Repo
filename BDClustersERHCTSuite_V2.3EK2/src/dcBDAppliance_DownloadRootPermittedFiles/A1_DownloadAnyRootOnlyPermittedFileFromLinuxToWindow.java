package dcBDAppliance_DownloadRootPermittedFiles;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 3/28/2017
*/ 

public class A1_DownloadAnyRootOnlyPermittedFileFromLinuxToWindow extends Thread {
	private String bdClusterName = ""; //BDProd2..BDTest2..BDSbx..BDDev
	private String entryNodeName = ""; //EN01
	private String srcLinuxServerFolderPathAndName = ""; ///etc/security/keytabs/
	private String tempLinuxServerFolderName = ""; //downloads
	private String downloadFileName = ""; //*..hue.ini
	private String destWindowsFolderPathAndName = ""; //"C:\\Users\\M041785\\Desktop\\dcBigData_Records\\dcBigDataLearning&Work\\Appliances\\EnterpiseSecuring\\Downloads\\currkeytabs_" + bdClusterName + "\\";
	
	
	public A1_DownloadAnyRootOnlyPermittedFileFromLinuxToWindow (String aBdClusterName, String aEntryNodeName, 
			String aSrcLinuxServerFolderPathAndName, String aTempLinuxServerFolderName, 
			String aDownloadFileName, String aDestWindowsFolderPathAndName) {
		this.bdClusterName = aBdClusterName;
		this.entryNodeName = aEntryNodeName;
		this.srcLinuxServerFolderPathAndName = aSrcLinuxServerFolderPathAndName;
		this.tempLinuxServerFolderName = aTempLinuxServerFolderName;
		this.downloadFileName = aDownloadFileName;
		this.destWindowsFolderPathAndName = aDestWindowsFolderPathAndName;			
	}
	
	public static void main(String[] args)  {
		if (args.length < 5) {
		System.out.println("*** Error:  Big Data ClusterName etc... Have Not Been Specified Yet!!!");
		return;
		}			
		new A1_DownloadAnyRootOnlyPermittedFileFromLinuxToWindow (args[0], args[1], args[2], args[3], args[4], args[5]).start();
	
	}

	public void run() {
		synchronized(this){
			if (bdClusterName.equalsIgnoreCase("BDInt2")){
				bdClusterName = "BDTest2";
			}
			
			prepareFolder(destWindowsFolderPathAndName, "Win folder for file(s) downloads fron MC Hadoop cluster - " + bdClusterName);
			
			BdNode aBDNode = new BdNode(this.entryNodeName, this.bdClusterName);
			ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
			ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
			System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
			
			String loginUserName = bdENCmdFactory.getUsername(); 						
			String rootUserName = bdENRootCmdFactory.getUsername();
			String rootUserPw = bdENRootCmdFactory.getRootPassword();
			
			System.out.println("*** loginUserName is: " + loginUserName);
			System.out.println("*** rootUserName is: " + rootUserName);
			System.out.println("*** rootUserPw is: " + rootUserPw);
			
			String tempLinuxServerFolderPathAndName = "/data/home/" + loginUserName  +"/" + this.tempLinuxServerFolderName;
			
			//Step#1:  Prepare the temp or staging Linux local folder for downloads 
			   //e.g., mkdir -p /data/home/m041785/downloads/; 	
			String tempLinuxCmd1 = "rm -r " + tempLinuxServerFolderPathAndName + "; mkdir -p " + tempLinuxServerFolderPathAndName + ";";		
			LoginUserUtil.performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(tempLinuxCmd1, bdENCmdFactory);
			System.out.println("*-1-* On '" + this.entryNodeName + "'server, created tempServerFolder: " + tempLinuxServerFolderPathAndName);
			
			
			//Step#2: Copy src root-owned file from srcLinuxServerFolderPathAndName to tempLinuxServerFolderPathAndName
			   //e.g., echo "tdc" | sudo su - root -c "cp /etc/security/keytabs/* /data/home/wa00336/currkeytabs/; chown wa00336:users -R /data/home/wa00336/currkeytabs/;";
			if (!this.srcLinuxServerFolderPathAndName.endsWith("/")){
				this.srcLinuxServerFolderPathAndName = this.srcLinuxServerFolderPathAndName + "/";
			}
			String srcLinuxFileFullPathAndName = this.srcLinuxServerFolderPathAndName + this.downloadFileName;
			String tempLinuxCmd2 = "sudo -S su - root -c 'cp " + srcLinuxFileFullPathAndName + " " + tempLinuxServerFolderPathAndName + "/; "
								  + "chown " + loginUserName + ":users -R " + tempLinuxServerFolderPathAndName + "/;'";
			LoginUserUtil.performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(tempLinuxCmd2, bdENCmdFactory);
			System.out.println("*-2-* On '" + this.entryNodeName + "'server, moved keytabs to the folder: " + tempLinuxServerFolderPathAndName);
			
			//Step#3: Download file(s) from Linux folder to local Win folder
			LoginUserUtil.copyFolder_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempLinuxServerFolderPathAndName,  
					this.destWindowsFolderPathAndName, bdENCmdFactory);
			System.out.println("*-3-* from '" + this.entryNodeName + "'server, download keytabs to the windows local folder: " + this.destWindowsFolderPathAndName);
			
			try {
				Desktop.getDesktop().open(new File(this.destWindowsFolderPathAndName));
			} catch (IOException e) {			
				e.printStackTrace();
			}		
			
		}// end synchronized		
	}//end run()
	
	private static void prepareFolder(String localFolderPathAndName, String folderNoticeInfo){
		File aFolderFile = new File (localFolderPathAndName);		
		if (!aFolderFile.exists()){
			aFolderFile.mkdirs();			
			System.out.println("\n .. Created folder for " + folderNoticeInfo +": \n" + localFolderPathAndName); 
		}		
	}//end prepareFolder

}
