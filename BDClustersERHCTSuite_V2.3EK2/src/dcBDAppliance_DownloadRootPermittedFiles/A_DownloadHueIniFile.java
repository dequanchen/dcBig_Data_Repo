package dcBDAppliance_DownloadRootPermittedFiles;

public class A_DownloadHueIniFile {
	private static String bdClusterName = "BDProd2"; //BDDev...BDInt2..BDTest2...BDSdbx...BDProd2
	private static String entryNodeName = "EN01";
	private static String tempLinuxServerFolderName = "downloads"; //full path and name like: /data/home/m041785/downloads
	
	private static String srcLinuxServerFolderPathAndName = "/etc/hue/conf.empty/"; ///etc/hue/conf.empty/
	private static String downloadFileName = "hue.ini";
	private static String destWindowsFolderPathAndName = "C:\\Users\\M041785\\Desktop\\dcBigData_Records\\dcBigDataLearning&Work\\Appliances\\"
			+ "ProblemIdAndFixing\\Hue_Issues\\CurrentHueIniFiles\\" + bdClusterName + "\\";

	public static void main(String[] args) {
		srcLinuxServerFolderPathAndName = "/etc/security/keytabs/";
		downloadFileName = "hdfs.headless.keytab";
		destWindowsFolderPathAndName = "C:\\Users\\M041785\\Desktop\\dcBigData_Records\\dcBigDataLearning&Work\\Appliances\\"
				+ "EnterpiseSecuring\\CurrKeytabs\\" + bdClusterName + "\\";
	
				
		new A1_DownloadAnyRootOnlyPermittedFileFromLinuxToWindow (bdClusterName, entryNodeName, srcLinuxServerFolderPathAndName, 
				tempLinuxServerFolderName, downloadFileName, destWindowsFolderPathAndName).start();

		}
}
