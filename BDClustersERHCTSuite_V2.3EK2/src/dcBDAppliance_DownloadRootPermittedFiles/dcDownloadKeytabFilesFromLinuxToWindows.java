package dcBDAppliance_DownloadRootPermittedFiles;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdNode;

public class dcDownloadKeytabFilesFromLinuxToWindows {
	private static String tempENName = "EN01";
	private static String bdClusterName = "BDProd2"; //BDDev...BDInt2..BDTest2...BDSdbx...BDProd2

	public static void main(String[] args) {
		if (bdClusterName.equalsIgnoreCase("BDInt2")){
			bdClusterName = "BDTest2";
		}
		
		BdNode aBDNode = new BdNode(tempENName, bdClusterName);
		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
		ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
		System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
		
		String loginUserName = bdENCmdFactory.getUsername(); 						
		String rootUserName = bdENRootCmdFactory.getUsername();
		String rootUserPw = bdENRootCmdFactory.getRootPassword();
		
		System.out.println("*** loginUserName is: " + loginUserName);
		System.out.println("*** rootUserName is: " + rootUserName);
		System.out.println("*** rootUserPw is: " + rootUserPw);
		
		String tempLinuxServerFolderPathAndName = "/data/home/" + loginUserName  +"/currkeytabs";
		
		String tempWinFolderPathAndName = "C:\\Users\\M041785\\Desktop\\dcBigData_Records\\dcBigDataLearning&Work\\Appliances\\EnterpiseSecuring\\CurrKeytabs\\currkeytabs_" + bdClusterName + "\\";
		
		prepareFolder(tempWinFolderPathAndName, "Win temp folder for keytabs fron MC Hadoop cluster - " + bdClusterName);
		
		//Step#1:  mkdir -p /data/home/wa00336/currkeytabs/; //		
		String tempLinuxCmd1 = "rm -r " + tempLinuxServerFolderPathAndName + "; mkdir -p " + tempLinuxServerFolderPathAndName + ";";		
		LoginUserUtil.performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(tempLinuxCmd1, bdENCmdFactory);
		System.out.println("*-1-* On '" + tempENName + "'server, created tempServerFolder: " + tempLinuxServerFolderPathAndName);
		
		//Step#2: echo "tdc" | sudo su - root -c "cp /etc/security/keytabs/* /data/home/wa00336/currkeytabs/; chown wa00336:users -R /data/home/wa00336/currkeytabs/;";
		String tempLinuxCmd2 = "sudo -S su - root -c 'cp /etc/security/keytabs/* " + tempLinuxServerFolderPathAndName + "/; "
							  + "chown " + loginUserName + ":users -R " + tempLinuxServerFolderPathAndName + "/;'";
		LoginUserUtil.performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(tempLinuxCmd2, bdENCmdFactory);
		System.out.println("*-2-* On '" + tempENName + "'server, moved keytabs to the folder: " + tempLinuxServerFolderPathAndName);
		
		//Step#3: Moving keytab files from Linux folder to local Win folder:
		LoginUserUtil.copyFolder_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempLinuxServerFolderPathAndName,  
				tempWinFolderPathAndName, bdENCmdFactory);
		System.out.println("*-3-* from '" + tempENName + "'server, download keytabs to the windows local folder: " + tempWinFolderPathAndName);
		
		try {
			Desktop.getDesktop().open(new File(tempWinFolderPathAndName));
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}//end main
	
	private static void prepareFolder(String localFolderPathAndName, String folderNoticeInfo){
		File aFolderFile = new File (localFolderPathAndName);		
		if (!aFolderFile.exists()){
			aFolderFile.mkdirs();			
			System.out.println("\n .. Created folder for " + folderNoticeInfo +": \n" + localFolderPathAndName); 
		}		
	}//end prepareFolder
}
