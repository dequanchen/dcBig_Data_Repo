package Kerberos;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;


public class dcPostKerberosBDClusterTrial {

	public static void main(String[] args) {
		String bdClusterName = "BDDev"; //BDProd..BDInt..BDSbx..BDDev
		
			
//			String hdfsNN1StateFindingFullCmdStr = "su hdfs -c 'hdfs haadmin -getServiceState nn1'";
//			String currNameNodeStateFinding_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(hdfsNN1StateFindingFullCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
//			System.out.println("\n -*- currNameNodeStateFinding_PlinkSingleFullCmd: \n" + currNameNodeStateFinding_PlinkSingleFullCmd);

		
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		System.out.println("currBdCluster.getBdHdfsNnIPAddressAndPort(): " + currBdCluster.getBdHdfs1stNnIPAddressAndPort());
		
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		//Configuration configuration = currBdCluster.getConfiguration();
		
		String currSrcHdfsFilePathAndName = "/concV.properties";
		System.out.println("\n*** In the HDFS file: " + currSrcHdfsFilePathAndName);
		
		try {
			//String krbConfFilePathAndName = dcPostKerberosHDFSTrial.class.getResource("krb5.conf").getPath();
			//System.setProperty("java.security.krb5.conf", krbConfFilePathAndName);
			FileStatus[] status = currHadoopFS.listStatus(new Path(currSrcHdfsFilePathAndName));				
			BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
			String line = "";
			
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()){
					System.out.println("*** line: " + line );
				}
															
			}//end while
			br.close(); 
			
		} catch (Exception e) {
            e.printStackTrace();
        }//end try		
	}//end main
}//end class
