package dcModelClasses;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
 * Author: Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
 * Date: 11/30/2014
 */

public class HBase {
	private String bdClusterName = "";	
	private String hadoopInstalledFolder = "/usr/hdp/2.3.4.0-3485"; // /usr/lib (<=TDH2.1.11) ... /usr/hdp/2.3.2.0-2950...2.3.4.0-3485 (HDP/TDH2.3.2/4)
	private String hbaseInstalledFolderPath = "";
	private String hbaseBinFolderPath = "";
	private String hbaseLibFolderPath = "";
	private String hbaseConfigFolderPath = "";
	private String hiveConfigFolderPath = "";
	private String hbaseHomeFolderPath = "";
	
	private String hbaseConfigXmlFilePathAndName = "";
	private String hiveConfigXmlFilePathAndName = "";
	private String hcatInstalledFolderPath = "";
	private String hiveInstalledFolderPath = "";
	
	private String exportHadoopClassPath_ForHBaseStr = "";
	private String setHiveAuxJarsPath_ForHBaseStr = "";
	private String exportPigClassPath_ForHBaseStr = "";
	private String exportPigOpts_ForHBaseStr = "";
	private String exportHcatHome_ForHBaseStr = "";
	private String pigRegisterHBaseJarsForHcatStr = "";
	
	private static String localWorkingAndScriptFilesFolder = "../DC_BDClusterERHCTSuite_V2EK/src/LinuxScriptFiles/HBase/";

	public HBase(String aBdClusterName) {			
		this.bdClusterName = aBdClusterName;
		
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")
				|| this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")
				|| this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")
				|| this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")
				){
			this.hadoopInstalledFolder = "/usr/hdp/2.3.4.0-3485";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")
				|| this.bdClusterName.equalsIgnoreCase("BDProd3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")
				|| this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")
				){
			this.hadoopInstalledFolder = "/usr/hdp/2.5.3.0-37";
		}
		
		
		this.hbaseInstalledFolderPath = this.hadoopInstalledFolder + "/hbase";
		this.hbaseBinFolderPath = this.hbaseInstalledFolderPath + "/bin";
		this.hbaseLibFolderPath = this.hbaseInstalledFolderPath + "/lib";
		this.hbaseConfigFolderPath = "/etc/hbase/conf"; // /etc/hbase/conf.dist (<=TDH2.1.11) ... /etc/hbase/conf (conf.install) (TDH2.3.4)
		this.hiveConfigFolderPath = "/etc/hive/conf";   // /etc/hive/conf.dist (<=TDH2.1.11) ... /etc/hive/conf (conf.install) (TDH2.3.4)
		this.hbaseHomeFolderPath = "/home/hbase";		 
		
		this.hbaseConfigXmlFilePathAndName = this.hbaseConfigFolderPath + "/hbase-site.xml";
		this.hiveConfigXmlFilePathAndName = this.hiveConfigFolderPath + "/hive-site.xml";
		//this.hcatInstalledFolderPath = this.hadoopInstalledFolder + "/hcatalog"; //for TDH1.3.2
		this.hcatInstalledFolderPath = this.hadoopInstalledFolder + "/hive-hcatalog"; ////for TDH2.1
		this.hiveInstalledFolderPath = this.hadoopInstalledFolder + "/hive";
		
		this.exportHcatHome_ForHBaseStr = "export HCAT_HOME=" + this.hcatInstalledFolderPath; // + "/bin";
		System.out.println("\n *** exportHcatHome_ForHBaseStr: " + exportHcatHome_ForHBaseStr);
		
		prepareFolder(localWorkingAndScriptFilesFolder, "HBase Scripts or Working");
		String tempLocalWorkignFileAndPathName = localWorkingAndScriptFilesFolder + "temp.txt";
		prepareFile (tempLocalWorkignFileAndPathName, "HBase Info");
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		String en1Name = currBdCluster.getCurrentClusterEntryNodeList().get(0);
		BdNode aBDNode = new BdNode(en1Name, bdClusterName);
		ULServerCommandFactory bdENCommFactory = aBDNode.getBdENCmdFactory();
		System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());		
		ArrayList<String> tempFoundFilePathAndNameList = new ArrayList<String>();
		
		
		//1. find and obtain installed major hbase jar files - path-and-names
		String hbaseInstalledJarFilePathAndName = "";
		//for <= TDH2.1.11:
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hbaseInstalledFolderPath,  "hbase-*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		//---The following is special for HBase-0.94 installation on TDH1.3.2
		for (String tempJarFile:tempFoundFilePathAndNameList){ //tempJarFile.contains("lib/hbase/hbase-") &&
			//System.out.println("\n*1* tempJarFile: " + tempJarFile);
			if (tempJarFile.contains("security") && tempJarFile.contains(".jar")  && !tempJarFile.contains("-tests") ){
				hbaseInstalledJarFilePathAndName = tempJarFile;
			}
		}		
		System.out.println(" *** hbaseInstalledJarFilePathAndName: " + hbaseInstalledJarFilePathAndName);
		
		//---The following is special for HBase-0.98 installation on TDH2.1 and HBase.1.1.2 on TDH2.3.4
		String hbaseCommonJarFilePathAndName = "";
		String hbaseClientJarFilePathAndName = "";
		String hbaseServerJarFilePathAndName = "";
		String hbaseProtocolJarFilePathAndName = "";		
		
		if (hbaseInstalledJarFilePathAndName.isEmpty()){
			tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hbaseInstalledFolderPath + "/lib",  "hbase-*", "",
					bdENCommFactory, tempLocalWorkignFileAndPathName);
			for (String tempJarFile:tempFoundFilePathAndNameList){ //tempJarFile.contains("lib/hbase/hbase-") &&
				//The followings are for TDH2.3.4
				if (tempJarFile.contains("-it-")  && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
					hbaseInstalledJarFilePathAndName = tempJarFile;
				}
				if (tempJarFile.contains("-common-")  && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
					hbaseCommonJarFilePathAndName = tempJarFile;
				}
				if (tempJarFile.contains("-client-")  && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
					hbaseClientJarFilePathAndName = tempJarFile;
				}
				if (tempJarFile.contains("-server-")  && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
					hbaseServerJarFilePathAndName = tempJarFile;
				}
				if (tempJarFile.contains("-protocol-")  && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
					hbaseProtocolJarFilePathAndName = tempJarFile;
				}
								
//				//For Version of <= TDH2.1.11
//				if (tempJarFile.contains("-it-")  && tempJarFile.contains("hadoop2") && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
//					hbaseInstalledJarFilePathAndName = tempJarFile;
//				}
//				if (tempJarFile.contains("-common-")  && tempJarFile.contains("hadoop2") && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
//					hbaseCommonJarFilePathAndName = tempJarFile;
//				}
//				if (tempJarFile.contains("-client-")  && tempJarFile.contains("hadoop2") && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
//					hbaseClientJarFilePathAndName = tempJarFile;
//				}
//				if (tempJarFile.contains("-server-")  && tempJarFile.contains("hadoop2") && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
//					hbaseServerJarFilePathAndName = tempJarFile;
//				}
//				if (tempJarFile.contains("-protocol-")  && tempJarFile.contains("hadoop2") && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
//					hbaseProtocolJarFilePathAndName = tempJarFile;
//				}	
			}		
			System.out.println(" *** hbaseInstalledJarFilePathAndName: " + hbaseInstalledJarFilePathAndName);
			System.out.println(" *** hbaseCommonJarFilePathAndName: " + hbaseCommonJarFilePathAndName);
			System.out.println(" *** hbaseClientJarFilePathAndName: " + hbaseClientJarFilePathAndName);
			System.out.println(" *** hbaseServerJarFilePathAndName: " + hbaseServerJarFilePathAndName);
			System.out.println(" *** hbaseProtocolJarFilePathAndName: " + hbaseProtocolJarFilePathAndName);
			
		}
		
		
		//2. find and obtain installed guava-for-hbase jar file path and name
		String guavaForHBaseInstalledJarFilePathAndName = "";
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hbaseInstalledFolderPath,  "guava-1*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){
			if (tempJarFile.contains("hbase/lib/guava-") && tempJarFile.contains(".jar") ){
				guavaForHBaseInstalledJarFilePathAndName = tempJarFile;
			}
		}
		System.out.println(" *** guavaForHBaseInstalledJarFilePathAndName: " + guavaForHBaseInstalledJarFilePathAndName);
		
		
		//3. find and obtain installed hive-hbase-handler jar file path and name
		String hiveHBaseHandlerInstalledJarFilePathAndName = "";
		//for <= TDH2.1.11:
		//tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hadoopInstalledFolder +"/hive",  "hive-hbase-handler-0*", "",
		//		bdENCommFactory, tempLocalWorkignFileAndPathName); 
		//For TDH2.3.4
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hadoopInstalledFolder +"/hive",  "hive-hbase-handler-*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){
			//System.out.println("\n*** tempJarFile: " + tempJarFile);
			if (tempJarFile.contains("hive/lib/hive-hbase-handler-") && tempJarFile.contains(".jar") ){
				hiveHBaseHandlerInstalledJarFilePathAndName = tempJarFile;
			}			
		}	
	
		System.out.println(" *** hiveHBaseHandlerInstalledJarFilePathAndName: " + hiveHBaseHandlerInstalledJarFilePathAndName);
		

		//4. find and obtain installed zookeeper jar file path and name
		String zookeeperInstalledJarFilePathAndName = "";
		//For <=TDH2.1.11
		//tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hadoopInstalledFolder +"/zookeeper",  "zookeeper-*", "",
		//		bdENCommFactory, tempLocalWorkignFileAndPathName);
		//for (String tempJarFile:tempFoundFilePathAndNameList){
		//	if (tempJarFile.contains("lib/zookeeper/zookeeper-") && tempJarFile.contains(".jar") ){
		//		zookeeperInstalledJarFilePathAndName = tempJarFile;
		//	}
		//}		
		//For TDH2.3.4
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hadoopInstalledFolder +"/zookeeper",  "zookeeper*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){
			if (tempJarFile.contains("/zookeeper/zookeeper") && tempJarFile.contains(".jar") ){
				zookeeperInstalledJarFilePathAndName = tempJarFile;
			}
		}	
		System.out.println(" *** zookeeperInstalledJarFilePathAndName: " + zookeeperInstalledJarFilePathAndName);
			
		//find /usr/lib/hbase -name "hbase-0*" | grep -v tests;// /usr/lib/hbase/hbase-0.94.6.1.3.2.0-110-security.jar
		//find /usr/lib/hbase -name "guava-1*" | grep -v tests;// /usr/lib/hbase/lib/guava-11.0.2.jar 
		//find /usr/lib/ -name "hive-hbase-handler-0*" ; // /usr/lib/hive/lib/hive-hbase-handler-0.11.0.1.3.2.0-110.jar
		//find /usr/lib/ -name "zookeeper-*" | grep "zookeeper/zookeeper"; // /usr/lib/zookeeper/zookeeper-3.4.5.1.3.2.0-110.jar
		//find /usr/lib/ -name "zookeeper-*" | grep "usr/lib/zookeeper"; // /usr/lib/zookeeper/zookeeper-3.4.5.1.3.2.0-110.jar
		
		//The following is good for HBase 0.98 and under
		//this.exportHadoopClassPath_ForHBaseStr = "export HADOOP_CLASSPATH=/etc/hadoop/conf:" + this.hbaseConfigFolderPath + ":" 
		//					+ hbaseInstalledJarFilePathAndName + ":" + zookeeperInstalledJarFilePathAndName;
		
		//The following is good for HBase 1.1.1 and above
		this.exportHadoopClassPath_ForHBaseStr = "export HADOOP_CLASSPATH=/etc/hadoop/conf:" + this.hbaseConfigFolderPath + ":" 
				+ hbaseInstalledJarFilePathAndName + ":" + hbaseServerJarFilePathAndName + ":" + zookeeperInstalledJarFilePathAndName;
		
		//exportHadoopClassPath = "export HADOOP_CLASSPATH=/etc/hbase/conf:
		// /usr/lib/hbase/hbase-0.94.6.1.3.2.0-111-security.jar:
		// /usr/lib/zookeeper/zookeeper.jar";
		
		this.setHiveAuxJarsPath_ForHBaseStr = "SET hive.aux.jars.path=file://"+ this.hbaseConfigXmlFilePathAndName 
				+ ",file://" + hiveHBaseHandlerInstalledJarFilePathAndName 
				
				+ ",file://" + hbaseInstalledJarFilePathAndName 
				+ ",file://" + zookeeperInstalledJarFilePathAndName  
				+ ",file://" + guavaForHBaseInstalledJarFilePathAndName
				
				+ ",file://" + hbaseCommonJarFilePathAndName 
				+ ",file://" + hbaseClientJarFilePathAndName 
				+ ",file://" + hbaseServerJarFilePathAndName  
				+ ",file://" + hbaseProtocolJarFilePathAndName				
				;		

		//setHiveAuxJarsPathStr = "SET hive.aux.jars.path=file:///etc/hbase/conf/hbase-site.xml, //or file:///etc/hbase/conf.dist/hbase-site.xml, 
		//file:///usr/lib/hive/lib/hive-hbase-handler-0.11.0.1.3.2.0-111.jar, //or file:///usr/lib/hive/lib/hive-hbase-handler-0.13.0.2.1.2.2-516.jar,
		//file:///usr/lib/hbase/hbase-0.94.6.1.3.2.0-111-security.jar, //or file:///usr/lib/hbase/lib/hbase-it-0.98.0.2.1.2.2-516-hadoop2.jar,
		//file:///usr/lib/zookeeper/zookeeper-3.4.5.1.3.2.0-111.jar, //or file:///usr/lib/zookeeper/zookeeper-3.4.5.2.1.2.0-402.jar,
		//file:///usr/lib/hive/lib/guava-11.0.2.jar"; //or file:///usr/lib/hbase/lib/guava-12.0.1.jar,	
		
		// .. //or  file:///usr/lib/hbase/lib/hbase-common-0.98.0.2.1.2.2-516-hadoop2.jar,
		// .. //or  file:///usr/lib/hbase/lib/hbase-client-0.98.0.2.1.2.2-516-hadoop2.jar,
		// .. //or  file:///usr/lib/hbase/lib/hbase-server-0.98.0.2.1.2.2-516-hadoop2.jar,
		// .. //or  file:///usr/lib/hbase/lib/hbase-protocol-0.98.0.2.1.2.2-516-hadoop2.jar;

		
		
		
		//5.	
		ArrayList<String> tempFoundLinesList = new ArrayList<String>();
		
		String hiveMetastoreUris = "";		
		tempFoundLinesList = HdfsUtil.findLinesInAFile_OnEntryNodeLocal_OnBDCluster (this.hiveConfigXmlFilePathAndName, 
				"thrift://", "", bdENCommFactory, tempLocalWorkignFileAndPathName);
		
		for (String tempLine:tempFoundLinesList){
			if (tempLine.contains(":9083") ){
				hiveMetastoreUris = tempLine.trim();
			}
		}
		System.out.println(" *** hiveMetastoreUris: " + hiveMetastoreUris);
		hiveMetastoreUris = hiveMetastoreUris.replace("<value>", "").replace("</value>", "");
		System.out.println(" *** hiveMetastoreUris: " + hiveMetastoreUris);
		
		this.exportPigOpts_ForHBaseStr = "export PIG_OPTS=-Dhive.metastore.uris=" + hiveMetastoreUris;
		
		//export PIG_OPTS=-Dhive.metastore.uris=thrift://hdp002-hive:9083; //hdp002-hive:9083..hdpr01mn01.mayo.edu:9083
		
		
		//6.
		String hcatCoreInstalledJarFilePathAndName = "";
		String hcatPigAdapterInstalledJarFilePathAndName = "";
		
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hcatInstalledFolderPath + "/share/hcatalog",  "*hcatalog-*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){
			if (tempJarFile.contains("-core-") && tempJarFile.contains(".jar") ){
				hcatCoreInstalledJarFilePathAndName = tempJarFile;
			}
			if (tempJarFile.contains("-pig-adapter-") && tempJarFile.contains(".jar") ){
				hcatPigAdapterInstalledJarFilePathAndName = tempJarFile;
			}
		}			
				
		System.out.println(" *** hcatCoreInstalledJarFilePathAndName: " + hcatCoreInstalledJarFilePathAndName);
		System.out.println(" *** hcatPigAdapterInstalledJarFilePathAndName: " + hcatPigAdapterInstalledJarFilePathAndName);
		
	    	
		
		//7.
		String hiveMetaStoreInstalledJarFilePathAndName = "";		
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hiveInstalledFolderPath +"/lib",  "hive-metastore-*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){
			if (tempJarFile.contains("-metastore-") && tempJarFile.contains(".jar") ){
				hiveMetaStoreInstalledJarFilePathAndName = tempJarFile;
			}			
		}				
		System.out.println(" *** hiveMetaStoreInstalledJarFilePathAndName: " + hiveMetaStoreInstalledJarFilePathAndName);
		
		
		String hiveExecInstalledJarFilePathAndName = "";
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hiveInstalledFolderPath +"/lib",  "hive-exec-*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){			
			if (tempJarFile.contains("-exec-") && tempJarFile.contains(".jar") ){
				hiveExecInstalledJarFilePathAndName = tempJarFile;
			}
		}			
		System.out.println(" *** hiveExecInstalledJarFilePathAndName: " + hiveExecInstalledJarFilePathAndName);
		
				
		String libthriftInstalledJarFilePathAndName = "";
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hiveInstalledFolderPath +"/lib",  "libthrift-*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){			
			if (tempJarFile.contains("libthrift-") && tempJarFile.contains(".jar") ){
				libthriftInstalledJarFilePathAndName = tempJarFile;
			}
		}			
		System.out.println(" *** libthriftInstalledJarFilePathAndName: " + libthriftInstalledJarFilePathAndName);
		
		String libfbInstalledJarFilePathAndName = "";
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hiveInstalledFolderPath +"/lib",  "libfb*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){			
			if (tempJarFile.contains("libfb") && tempJarFile.contains(".jar") ){
				libfbInstalledJarFilePathAndName = tempJarFile;
			}
		}			
		System.out.println(" *** libfbInstalledJarFilePathAndName: " + libfbInstalledJarFilePathAndName);
		
		
		String jdoapiInstalledJarFilePathAndName = "";
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hiveInstalledFolderPath +"/lib",  "jdo*-api-*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){			
			if (tempJarFile.contains("jdo") && tempJarFile.contains("-api-") && tempJarFile.contains(".jar") ){
				jdoapiInstalledJarFilePathAndName = tempJarFile;
			}
		}			
		System.out.println(" *** jdoapiInstalledJarFilePathAndName: " + jdoapiInstalledJarFilePathAndName);
		
				
				
		
		String slf4japiInstalledJarFilePathAndName = "";
		//For <=TDH2.1.11
		//tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hiveInstalledFolderPath +"/lib",  "slf4j-api-*", "",
		//		bdENCommFactory, tempLocalWorkignFileAndPathName);
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hiveInstalledFolderPath.replace("/hive", "/tez") +"/lib",  "slf4j-api-*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){			
			if (tempJarFile.contains("slf4j-api-") && tempJarFile.contains(".jar") ){
				slf4japiInstalledJarFilePathAndName = tempJarFile;
			}
		}			
		System.out.println(" *** slf4japiInstalledJarFilePathAndName: " + slf4japiInstalledJarFilePathAndName);
		
		// /usr/lib/hbase/lib/*
		String hbaseAllJarsStr = this.hbaseInstalledFolderPath + "/lib/*";
		this.exportPigClassPath_ForHBaseStr = "export PIG_CLASSPATH=" 
						+ hcatCoreInstalledJarFilePathAndName + ":"
						+ hcatPigAdapterInstalledJarFilePathAndName + ":"						
						
						+ hiveMetaStoreInstalledJarFilePathAndName + ":"
						+ libthriftInstalledJarFilePathAndName + ":"
						+ hiveExecInstalledJarFilePathAndName + ":"
						+ libfbInstalledJarFilePathAndName + ":"
						+ jdoapiInstalledJarFilePathAndName + ":"
						+ slf4japiInstalledJarFilePathAndName  + ":"						
						+ hbaseAllJarsStr;
				
		//export HIVE_HOME=/usr/lib/hive;     //this.hiveInstalledFolderPath = this.hadoopInstalledFolder + "/hive";   
		//export HCAT_HOME=/usr/lib/hcatalog; //this.hcatInstalledFolderPath = this.hadoopInstalledFolder + "/hcatalog";
//		export PIG_CLASSPATH=/usr/lib/hcatalog/share/hcatalog/hcatalog-core-0.11.0.1.3.2.0-112.jar:
//				/usr/lib/hcatalog/share/hcatalog/hcatalog-pig-adapter-0.11.0.1.3.2.0-112.jar:
//				
//				/usr/lib/hive/lib/hive-metastore-0.11.0.1.3.2.0-110.jar:
//				/usr/lib/hive/lib/libthrift-0.9.0.jar:
//				/usr/lib/hive/lib/hive-exec-0.11.0.1.3.2.0-110.jar:
//				/usr/lib/hive/lib/libfb303-0.9.0.jar:
//				/usr/lib/hive/lib/jdo2-api-2.3-ec.jar:
//				/usr/lib/hive/lib/slf4j-api-1.6.1.jar
		
		//8. Get pig register jars that are needed for pig-through-Hcat loading a HBase table
		//String guavaForHBaseInstalledJarFilePathAndName = "";		
		
		String hbaseHtraceCoreJarFilePathAndName = "";			
		tempFoundFilePathAndNameList = HdfsUtil.findFileList_OnEntryNodeLocal_OnBDCluster (this.hbaseInstalledFolderPath + "/lib",  "htrace-*", "",
				bdENCommFactory, tempLocalWorkignFileAndPathName);
		for (String tempJarFile:tempFoundFilePathAndNameList){ //tempJarFile.contains("lib/hbase/hbase-") &&
			if (tempJarFile.contains("htrace-")  && tempJarFile.contains("-core-") && tempJarFile.contains(".jar") && !tempJarFile.contains("-tests") ){
				hbaseHtraceCoreJarFilePathAndName = tempJarFile;
			}
			
		}		
		System.out.println(" *** hbaseHtraceCoreJarFilePathAndName: " + hbaseHtraceCoreJarFilePathAndName);
		
		this.pigRegisterHBaseJarsForHcatStr = "REGISTER " + hbaseCommonJarFilePathAndName + ";"
				+ "REGISTER " + hbaseClientJarFilePathAndName + ";"
				+ "REGISTER " + hbaseServerJarFilePathAndName + ";"
				+ "REGISTER " + hbaseProtocolJarFilePathAndName + ";"				
				+ "REGISTER " + hbaseHtraceCoreJarFilePathAndName + ";"
				//+ "REGISTER " + zookeeperInstalledJarFilePathAndName + ";"
				//+ "REGISTER " + guavaForHBaseInstalledJarFilePathAndName + ";"
				;
		
		System.out.println(" *** this.pigRegisterHBaseJarsForHcatStr: " + this.pigRegisterHBaseJarsForHcatStr + "\n\n");
		
	}//end constructor
	

	
	public String getExportHcatHome_ForHBaseStr() {
		return exportHcatHome_ForHBaseStr;
	}

	public void setExportHcatHome_ForHBaseStr(String exportHcatHome_ForHBaseStr) {
		this.exportHcatHome_ForHBaseStr = exportHcatHome_ForHBaseStr;
	}

	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public String getHadoopInstalledFolder() {
		return hadoopInstalledFolder;
	}

	public void setHadoopInstalledFolder(String hadoopInstalledFolder) {
		this.hadoopInstalledFolder = hadoopInstalledFolder;
	}

	public String getHbaseInstalledFolderPath() {
		return hbaseInstalledFolderPath;
	}

	public void setHbaseInstalledFolderPath(String hbaseInstalledFolderPath) {
		this.hbaseInstalledFolderPath = hbaseInstalledFolderPath;
	}

	public String getHbaseBinFolderPath() {
		return hbaseBinFolderPath;
	}

	public void setHbaseBinFolderPath(String hbaseBinFolderPath) {
		this.hbaseBinFolderPath = hbaseBinFolderPath;
	}

	public String getHbaseLibFolderPath() {
		return hbaseLibFolderPath;
	}

	public void setHbaseLibFolderPath(String hbaseLibFolderPath) {
		this.hbaseLibFolderPath = hbaseLibFolderPath;
	}

	public String getHbaseConfigFolderPath() {
		return hbaseConfigFolderPath;
	}

	public void setHbaseConfigFolderPath(String hbaseConfigFolderPath) {
		this.hbaseConfigFolderPath = hbaseConfigFolderPath;
	}

	public String getHiveConfigFolderPath() {
		return hiveConfigFolderPath;
	}

	public void setHiveConfigFolderPath(String hiveConfigFolderPath) {
		this.hiveConfigFolderPath = hiveConfigFolderPath;
	}

	public String getHbaseHomeFolderPath() {
		return hbaseHomeFolderPath;
	}

	public void setHbaseHomeFolderPath(String hbaseHomeFolderPath) {
		this.hbaseHomeFolderPath = hbaseHomeFolderPath;
	}

	public String getHbaseConfigXmlFilePathAndName() {
		return hbaseConfigXmlFilePathAndName;
	}

	public void setHbaseConfigXmlFilePathAndName(
			String hbaseConfigXmlFilePathAndName) {
		this.hbaseConfigXmlFilePathAndName = hbaseConfigXmlFilePathAndName;
	}

	public String getHiveConfigXmlFilePathAndName() {
		return hiveConfigXmlFilePathAndName;
	}

	public void setHiveConfigXmlFilePathAndName(String hiveConfigXmlFilePathAndName) {
		this.hiveConfigXmlFilePathAndName = hiveConfigXmlFilePathAndName;
	}

	public String getExportHadoopClassPath_ForHBaseStr() {
		return exportHadoopClassPath_ForHBaseStr;
	}

	public void setExportHadoopClassPath_ForHBaseStr(
			String exportHadoopClassPath_ForHBaseStr) {
		this.exportHadoopClassPath_ForHBaseStr = exportHadoopClassPath_ForHBaseStr;
	}

	public String getSetHiveAuxJarsPath_ForHBaseStr() {
		return setHiveAuxJarsPath_ForHBaseStr;
	}

	public void setSetHiveAuxJarsPath_ForHBaseStr(
			String setHiveAuxJarsPath_ForHBaseStr) {
		this.setHiveAuxJarsPath_ForHBaseStr = setHiveAuxJarsPath_ForHBaseStr;
	}

	public String getExportPigClassPath_ForHBaseStr() {
		return exportPigClassPath_ForHBaseStr;
	}

	public void setExportPigClassPath_ForHBaseStr(
			String exportPigClassPath_ForHBaseStr) {
		this.exportPigClassPath_ForHBaseStr = exportPigClassPath_ForHBaseStr;
	}

	public String getExportPigOpts_ForHBaseStr() {
		return exportPigOpts_ForHBaseStr;
	}

	public void setExportPigOpts_ForHBaseStr(String exportPigOpts_ForHBaseStr) {
		this.exportPigOpts_ForHBaseStr = exportPigOpts_ForHBaseStr;
	}

	public static String getLocalWorkingAndScriptFilesFolder() {
		return localWorkingAndScriptFilesFolder;
	}

	public static void setLocalWorkingAndScriptFilesFolder(
			String localWorkingAndScriptFilesFolder) {
		HBase.localWorkingAndScriptFilesFolder = localWorkingAndScriptFilesFolder;
	}
	
	
	public String getPigRegisterHBaseJarsForHcatStr() {
		return pigRegisterHBaseJarsForHcatStr;
	}

	public void setPigRegisterHBaseJarsForHcatStr(
			String pigRegisterHBaseJarsForHcatStr) {
		this.pigRegisterHBaseJarsForHcatStr = pigRegisterHBaseJarsForHcatStr;
	}



	private static void prepareFile (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
			if (aFile.exists()){
				aFile.delete();
				System.out.println("\n .. Deleted file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}
			
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile
	
	private static void prepareFolder(String localFolderPathAndName, String folderNoticeInfo){
		File aFolderFile = new File (localFolderPathAndName);		
		if (!aFolderFile.exists()){
			aFolderFile.mkdirs();			
			System.out.println("\n .. Created folder for " + folderNoticeInfo +": \n" + localFolderPathAndName); 
		}		
	}//end prepareFolder
	
}//end class
