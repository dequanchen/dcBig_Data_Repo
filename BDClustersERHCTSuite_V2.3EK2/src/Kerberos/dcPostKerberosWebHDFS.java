package Kerberos;

public class dcPostKerberosWebHDFS {
	

	public static void main(String[] args) {
		String krbConfLocalFilePathAndName = "C:\\Windows\\krb5.ini";	
		String hdfsExternalPrincipal = "hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM";	
		String hdfsExternalKeyTabFilePathAndName = "C:\\Windows\\hdfs_test_BDDev.keytab";
		
		System.setProperty("java.security.krb5.conf", krbConfLocalFilePathAndName);
		
		//kinit -k -t C:\Windows\smokeuser.headless.keytab ambari-qa@MAYOHADOOPDEV1.COM &&
		//String userAuthenStr = " -v --user " + esClusterUserName + ":" + esClusterPassWord + " "; 
		

	}//end main

}//end class
