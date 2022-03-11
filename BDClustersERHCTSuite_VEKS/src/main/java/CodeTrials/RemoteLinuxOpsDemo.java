package CodeTrials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/*
 * Dequan Chen, Ph. D. This is a testing for remote operations on Linux/Unix Server without using putty but just the JSch Java API
 * 9/1/2017
 */
public class RemoteLinuxOpsDemo {
@SuppressWarnings("unused")
public static void main(String[] args) throws IOException, JSchException, SftpException {
	//https://stackoverflow.com/questions/21399561/sftp-upload-download-exist-and-move-using-apache-commons-vfs
	//https://stackoverflow.com/questions/25657603/how-to-use-java-jsch-library-to-read-remote-file-line-by-line
	//https://epaul.github.io/jsch-documentation/simple.javadoc/com/jcraft/jsch/ChannelSftp.html
	//https://epaul.github.io/jsch-documentation/simple.javadoc/com/jcraft/jsch/ChannelExec.html
	
//    String domain="hyd\\all";
//    String userName="chiranjeevir";
//    String password="Acvsl@jun2013";
//    String remoteFilePath="\\\\10.0.15.74\\D$\\Suman\\host.txt";
//
//
//    domain="MFAD";
//    userName="m041785";
//    password="geeHoo17";
//    remoteFilePath = "\\\\mfad.mfroot.org\\rchdept\\EDW\\Section Documents\\BigDataRelated\\dcTestData.txt";
//    remoteFilePath = "\\\\mfad.mfroot.org\\rchhome\\users10\\M041785\\employeeData.txt";
//    
//   
//    File f=new File("C:/dequnaTestData.txt"); //Takes the default path, else, you can specify the required path
//    if(f.exists())
//    {
//        f.delete();
//    }
//    f.createNewFile(); 
//    FileObject destn=VFS.getManager().resolveFile(f.getAbsolutePath());
//
//    //domain, username, password
//    UserAuthenticator auth=new StaticUserAuthenticator(domain, userName, password);
//    FileSystemOptions opts=new FileSystemOptions();
//    DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
//
//
//    FileObject fo=VFS.getManager().resolveFile(remoteFilePath,opts);
//    System.out.println(fo.exists());
//    //fo.createFile();
//    destn.copyFrom(fo,Selectors.SELECT_SELF);
//    destn.close();
//    
//      FileReader aFileReader = new FileReader(f);
//   	BufferedReader br = new BufferedReader(aFileReader);
//   	
//   	System.out.println("***Reading the remote file:");
//   			
//   	String line = "";		
//   	while ((line = br.readLine()) != null ) {		
//   		System.out.println(line);
//   	}
//   	br.close();
//   	aFileReader.close();    
	
//	 	FileObject remoteFile = VFS.getManager().resolveFile(createConnectionString(hostName, userName, password, remoteFilePath), createDefaultOptions());
//	    System.out.println(remoteFile.exists());
//	    destn.copyFrom(remoteFile,Selectors.SELECT_SELF);
//	    destn.close();
////////////////////////////////////////////////////////////////////////////////////
    
	
    String hostName="hdpr05mn01.mayo.edu";
    String userName="wa00336";
    //String password="bnhgui89";
    String passwordLessPuttyKeyFile = "C:\\BD\\wa00336Key\\id_rsa_wa00336";
    //String knownHostsFile  = "C:\\BD\\wa00336Key\\known_hosts"; 
    String remoteFilePath = "/data/home/wa00336/test/dcTempData.txt";
    String localFilePath = "C:\\BD\\wa00336Key\\dcTempData.txt";
    //remoteFilePath = "/etc/hosts";
   
    JSch jsch = new JSch();
    jsch.addIdentity(passwordLessPuttyKeyFile);
    //jsch.setKnownHosts(new FileInputStream(knownHostsFile));
    Session session = jsch.getSession(userName, hostName, 22);
    java.util.Properties config = new java.util.Properties(); 
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);  
    
    //System.out.println(session.getHostKey());
    session.connect();
    //System.out.println(session.getHostKey());
    
//    //1. ChannelSftp  - Remotely file operations and some critical or frequently-used CLI commands 
//    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");  
//    sftp.connect();
//    //System.out.println(sftp.ls("/data/home/wa00336/"));
//    
//    OutputStream out = sftp.put(remoteFilePath, 0); //0 - OVERWRite; 2 - APPEND; 1 - RESUME
//    OutputStreamWriter writer = new OutputStreamWriter(out);
//    writer.write("Joe Smith");
//    writer.write("\nDavid Wang");
//    writer.append("\nChris Chen");
//    writer.close();
//    out.close();
//    		
//    InputStream stream = sftp.get(remoteFilePath);
//    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
//    String line = "";		
//	while ((line = br.readLine()) != null ) {		
//		System.out.println(line);
//	}
//	br.close();
//	stream.close();
//	
//	sftp.get(remoteFilePath, localFilePath);
//	//Desktop.getDesktop().open(new File(localFilePath));
//	
//	System.out.println("\n*** - On Remote Server at the version #: " + sftp.getServerVersion());
//	System.out.println(sftp.realpath(remoteFilePath));
//	System.out.println(sftp.ls(remoteFilePath));
//	System.out.println(sftp.lstat(remoteFilePath));
//	System.out.println(sftp.pwd());
//	sftp.getHome();
//	System.out.println(sftp.pwd());
//	//sftp.mkdir("newTrial");
//	sftp.cd("/data/home/wa00336/newTrial");
//	System.out.println(sftp.pwd());
//	
//	System.out.println("\n*** - On Local windows: ");
//	System.out.println(sftp.lpwd());
//	sftp.lcd("C:\\BD\\wa00336Key");
//	System.out.println(sftp.lpwd());
//	
//	sftp.exit();
	
//	//2.ChannelExec  - Remotely executing any commands  
//    ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
//    
//    channelExec.setCommand("mkdir -p /data/home/wa00336/newTrial");
//    channelExec.setCommand("ls -la " + remoteFilePath);
//    InputStream in = channelExec.getInputStream(); 	
//    channelExec.connect();
//    
//    BufferedReader br = new BufferedReader(new InputStreamReader(in));
//    String line = "";
//	while ((line = br.readLine()) != null ) {		
//		System.out.println(line);
//	}
//	br.close();
//	in.close(); 
//       
//    
//	System.out.println(channelExec.getExitStatus()); 
//	channelExec.disconnect();
//	System.out.println(channelExec.getExitStatus());

    
	//3. ChannelSftp + ChannelExec for remotely running a script file
	ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");  
    sftp.connect();
           
    ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
    
    channelExec.setCommand("mkdir -p /data/home/wa00336/newTrial");
    String localScriptFilePath = "C:\\BD\\wa00336Key\\dcTestingHDFS_ScriptFile1.sh";
    String remoteScriptFilePath = "/data/home/wa00336/newTrial/dcTestingHDFS_ScriptFile1.sh";
    sftp.rm(remoteScriptFilePath);
    sftp.put(localScriptFilePath, remoteScriptFilePath);
    
    channelExec.setCommand("ls -la " + remoteScriptFilePath);
    channelExec.setCommand("chmod +x " + remoteScriptFilePath);
    channelExec.setCommand("sh " + remoteScriptFilePath);
    
    InputStream in = channelExec.getInputStream(); 	
    channelExec.connect();
    
    System.out.println(channelExec.getExitStatus());
    
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String line = "";
	while ((line = br.readLine()) != null ) {		
		System.out.println(line);
	}
	br.close();
	in.close();    
	 
	channelExec.disconnect();
	System.out.println(channelExec.getExitStatus());    
	sftp.exit();
	
	session.disconnect();

}//end main
	

	public static String createConnectionString(String hostName, String username, String password, String remoteFilePath) {
	    return "sftp://" + username + ":" + password + "@" + hostName + "" + remoteFilePath;
	}
	
	public static FileSystemOptions createDefaultOptions() throws FileSystemException {
	        // Create SFTP options
	        FileSystemOptions opts = new FileSystemOptions();

	        // SSH Key checking
	        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

	        /*
	         * Using the following line will cause VFS to choose File System's Root
	         * as VFS's root. If I wanted to use User's home as VFS's root then set
	         * 2nd method parameter to "true"
	         */
	        // Root directory set to user home
	        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

	        // Timeout is count by Milliseconds
	        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

	        return opts;
	 }
}