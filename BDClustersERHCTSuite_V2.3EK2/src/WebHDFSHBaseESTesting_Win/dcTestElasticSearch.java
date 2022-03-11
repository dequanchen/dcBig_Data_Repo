package WebHDFSHBaseESTesting_Win;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 3/19/2017
*/ 

public class dcTestElasticSearch {
    private static String bdClusterName = "BDDev3"; //BDDev3...BDTest3...BDProd3...BDDev1...BDProd2
    private static String curlExeFilePath = "C:\\Program Files\\curl-7.53.0-win64-mingw\\bin\\curl.exe";
    private static String userName = "wa00336";
    private static String userPW = "QC3Hz2RM";
    private static String tgtResult = "104,Brian,Williams,120000,M,NewYork";
    
    public static void main(String[] args) {        
        String curlCmdExecuteTempResultFilePathAndName = curlExeFilePath.replaceAll("curl.exe", "dcTempCurlExeResult.txt");
        prepareFile_keepOld (curlCmdExecuteTempResultFilePathAndName, "Preparing file for temp curl cmd execution");
                
        String cleanTempTestResultsFileCmd = "echo \"\" > \"" + curlCmdExecuteTempResultFilePathAndName + "\"";
      
        //I. Test F5 Balancer
        System.out.println("\n***I. Now Testing WebHDFS via F5 Balancer on BD Cluster - " + bdClusterName + " ...");      
        
        String f5BalancerIPAddressAndPort = "https://bigdata.mayo.edu/hdp/";
        String bdClusterID = bdClusterName.replace("BD", "").toUpperCase();
        
        //curl -k -u xxxxxx:yyyyyyy -X GET -L "https://bigdataknox.mayo.edu:8442/gateway/MAYOHADOOPTEST2/webhdfs/v1/user/m041785/test/Knox/employee_webhdfs_curl.txt?op=OPEN"
        //String curlCmd_R_F5 = "-k -u " + userName + ":" + userPW + " -X GET -L \"" + f5BalancerIPAddressAndPort + "/gateway/" + bdClusterIdName + "/webhdfs/v1/user/m041785/test/Knox/employee_webhdfs_curl.txt?op=OPEN\" > \"" + curlCmdExecuteTempResultFilePathAndName + "\"";
        String curlCmd_F5_L = "-sk -u " + userName + ":" + userPW + " --location-trusted -X GET ";
        String curlCmd_F5_M = f5BalancerIPAddressAndPort + bdClusterID + "/knox";
        String curlCmd_F5_R = "/webhdfs/v1/user/wa00336/test/Hive/employee_beeline1/dcHiveTestData_employee.txt?op=OPEN > \"" + curlCmdExecuteTempResultFilePathAndName + "\"";
        String curlCmd_R_F5 = curlCmd_F5_L + curlCmd_F5_M + curlCmd_F5_R;
        //String curlCmd_R_F5 = "-sk -u " + userName + ":" + userPW + " --location-trusted -X GET " + f5BalancerIPAddressAndPort + bdClusterName + "/knox/webhdfs/v1/user/wa00336/test/Hive/employee_beeline1/dcHiveTestData_employee.txt?op=OPEN > \"" + curlCmdExecuteTempResultFilePathAndName + "\"";
        
        
        String curlCmd_Full_F5 = cleanTempTestResultsFileCmd + "  && \"" + curlExeFilePath + "\" " + curlCmd_R_F5;
        System.out.println("\n\t--- curlCmd_Full_F5: " + curlCmd_Full_F5);      
        
        
        String winRunCurlCmd_F5 = "cmd /c start cmd.exe /C \"" + curlCmd_Full_F5 + "\"";
        String winRunCurlCmdExeAndStatus_F5 = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_F5, curlCmdExecuteTempResultFilePathAndName, tgtResult, 1);
        System.out.println("\n   I.1-- WebHDFS Testing Result on the F5 Balancer: \n\t" + winRunCurlCmdExeAndStatus_F5);

        
        //II. Test Knox Gateways
        System.out.println("\n***II. Now Testing WebHbase via Knox Gateway servers on BD Cluster - " + bdClusterName + " ..."); 
        
        String firstKnoxNode_ConnStr = "";
        String secondKnoxNode_ConnStr = "";
        String thirdKnoxNode_ConnStr = "";
        String fourthKnoxNode_ConnStr = "";
        
        if (bdClusterName.equalsIgnoreCase("BDDev3")           ) {
            firstKnoxNode_ConnStr = "https://hdpr05en01.mayo.edu:8442/gateway/MAYOHADOOPDEV3";
            secondKnoxNode_ConnStr = "https://hdpr05en02.mayo.edu:8442/gateway/MAYOHADOOPDEV3";
            thirdKnoxNode_ConnStr = "https://hdpr07en01.mayo.edu:8442/gateway/MAYOHADOOPDEV3";
            fourthKnoxNode_ConnStr = "https://hdpr07en02.mayo.edu:8442/gateway/MAYOHADOOPDEV3";
        }
        
        if ( bdClusterName.equalsIgnoreCase("BDTest3")) {
            firstKnoxNode_ConnStr = "https://hdpr05en01.mayo.edu:8442/gateway/MAYOHADOOPTEST3";
            secondKnoxNode_ConnStr = "https://hdpr05en02.mayo.edu:8442/gateway/MAYOHADOOPTEST3";
            thirdKnoxNode_ConnStr = "https://hdpr07en01.mayo.edu:8442/gateway/MAYOHADOOPTEST3";
            fourthKnoxNode_ConnStr = "https://hdpr07en02.mayo.edu:8442/gateway/MAYOHADOOPTEST3";
        }
        if ( bdClusterName.equalsIgnoreCase("BDProd3")) {
            firstKnoxNode_ConnStr = "https://hdpr05en01.mayo.edu:8442/gateway/MAYOHADOOPPROD3";
            secondKnoxNode_ConnStr = "https://hdpr05en02.mayo.edu:8442/gateway/MAYOHADOOPPROD3";
            thirdKnoxNode_ConnStr = "https://hdpr07en01.mayo.edu:8442/gateway/MAYOHADOOPPROD3";
            fourthKnoxNode_ConnStr = "https://hdpr07en02.mayo.edu:8442/gateway/MAYOHADOOPPROD3";
        }
        
        if (bdClusterName.equalsIgnoreCase("BDDev1")) {
            firstKnoxNode_ConnStr =  "https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1";
            secondKnoxNode_ConnStr = "https://hdpr01kx02.mayo.edu:8442/gateway/MAYOHADOOPDEV1";
            thirdKnoxNode_ConnStr =  "https://hdpr01kx03.mayo.edu:8442/gateway/MAYOHADOOPDEV1";
            fourthKnoxNode_ConnStr = "https://hdpr01kx04.mayo.edu:8442/gateway/MAYOHADOOPDEV1";
        }
        if (bdClusterName.equalsIgnoreCase("BDProd2")) {
            firstKnoxNode_ConnStr =  "https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPPROD2";
            secondKnoxNode_ConnStr = "https://hdpr01kx02.mayo.edu:8442/gateway/MAYOHADOOPPROD2";
            thirdKnoxNode_ConnStr =  "https://hdpr01kx03.mayo.edu:8442/gateway/MAYOHADOOPPROD2";
            fourthKnoxNode_ConnStr = "https://hdpr01kx04.mayo.edu:8442/gateway/MAYOHADOOPPROD2";
        }
        //String winRunCurlCmd_1stKnox = winRunCurlCmd_F5.replaceAll(f5BalancerIPAddressAndPort, firstKnoxNodeIPAddressAndPort);
        String winRunCurlCmd_1stKnox = winRunCurlCmd_F5.replaceAll(curlCmd_F5_M, firstKnoxNode_ConnStr);        
        System.out.println("\n\t--- winRunCurlCmd_1stKnox: " + winRunCurlCmd_1stKnox);  
        String winRunCurlCmdExeAndStatus_1stKnox = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_1stKnox, curlCmdExecuteTempResultFilePathAndName, tgtResult, 1);
        System.out.println("\n   II.1 WebHDFS Testing on the First Knox Node Testing Status: \n\t" + winRunCurlCmdExeAndStatus_1stKnox);

        //String winRunCurlCmd_2ndKnox = winRunCurlCmd_F5.replaceAll(f5BalancerIPAddressAndPort, secondKnoxNodeIPAddressAndPort);
        String winRunCurlCmd_2ndKnox = winRunCurlCmd_F5.replaceAll(curlCmd_F5_M, secondKnoxNode_ConnStr);
        //System.out.println("\n\t--- winRunCurlCmd_2ndKnox: " + winRunCurlCmd_2ndKnox);  
        String winRunCurlCmdExeAndStatus_2ndKnox = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_2ndKnox, curlCmdExecuteTempResultFilePathAndName, tgtResult, 1);
        System.out.println("\n   II.2 WebHDFS Testing on the Second Knox Node Testing Status: \n\t" + winRunCurlCmdExeAndStatus_2ndKnox);
        
        //String winRunCurlCmd_3rdKnox = winRunCurlCmd_F5.replaceAll(f5BalancerIPAddressAndPort, thirdKnoxNodeIPAddressAndPort);
        String winRunCurlCmd_3rdKnox = winRunCurlCmd_F5.replaceAll(curlCmd_F5_M, thirdKnoxNode_ConnStr);
        //System.out.println("\n\t--- winRunCurlCmd_3rdKnox: " + winRunCurlCmd_3rdKnox);  
        String winRunCurlCmdExeAndStatus_3rdKnox = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_3rdKnox, curlCmdExecuteTempResultFilePathAndName, tgtResult, 1);
        System.out.println("\n   II.3 WebHDFS Testing on the Third Knox Node Testing Status: \n\t" + winRunCurlCmdExeAndStatus_3rdKnox);
                       
        //String winRunCurlCmd_4thKnox = winRunCurlCmd_F5.replaceAll(f5BalancerIPAddressAndPort, secondKnoxNodeIPAddressAndPort);
        String winRunCurlCmd_4thKnox = winRunCurlCmd_F5.replaceAll(curlCmd_F5_M, fourthKnoxNode_ConnStr);
        //System.out.println("\n\t--- winRunCurlCmd_4thKnox: " + winRunCurlCmd_4thKnox);  
        String winRunCurlCmdExeAndStatus_4thKnox = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_4thKnox, curlCmdExecuteTempResultFilePathAndName, tgtResult, 1);
        System.out.println("\n   II.4 WebHDFS Testing on the Fourth Knox Node Testing Status: \n\t" + winRunCurlCmdExeAndStatus_4thKnox);
        
        
        //III. Test HDFS Name Nodes
        System.out.println("\n***III. Now Testing WebHDFS via HDFS Name Nodes on BD Cluster - " + bdClusterName + " ...");
        
        String firstHDFSNN_ConnStr = "";
        String secondHDFSNN_ConnStr = "";
        ////BDDev3...BDTest3...BDProd3...BDDev1...BDProd2
        if (bdClusterName.equalsIgnoreCase("BDDev3")) {
          firstHDFSNN_ConnStr = "http://hdpr05mn01.mayo.edu:50070";
          secondHDFSNN_ConnStr = "http://hdpr05mn02.mayo.edu:50070";
        }
       
        
        if (bdClusterName.equalsIgnoreCase("BDTest3")) {
          firstHDFSNN_ConnStr = "http://hdpr06mn01.mayo.edu:50070";
          secondHDFSNN_ConnStr = "http://hdpr06mn02.mayo.edu:50070";
        }
        
        if (bdClusterName.equalsIgnoreCase("BDProd3")) {
          firstHDFSNN_ConnStr = "http://hdpr07mn01.mayo.edu:50070";
          secondHDFSNN_ConnStr = "http://hdpr07mn02.mayo.edu:50070";
        }
        
        if (bdClusterName.equalsIgnoreCase("BDDev1")) {
          firstHDFSNN_ConnStr = "http://hdpr03mn01.mayo.edu:50070";
          secondHDFSNN_ConnStr = "http://hdpr03mn02.mayo.edu:50070";
        }
        
        if (bdClusterName.equalsIgnoreCase("BDProd2")) {
          firstHDFSNN_ConnStr = "http://hdpr02mn01.mayo.edu:50070";
          secondHDFSNN_ConnStr = "http://hdpr02mn02.mayo.edu:50070";
        }   
        
        
        
        //userADName="wa00336";
        //userADPasswd="xxxxxxx";
        //echo "$userADPasswd" | kinit $userADName;
        //
        //echo 'tnkgu6h2' | kinit wa00336 #Not working on Windows
        //echo | set /p="tnkgu6h2" | kinit wa00336 #working on Windows
        //echo "tnkgu6h2" > "H:/userPW.txt"
        //type "H:/userPW.txt" | kinit wa00336 #working on Windows
        
        String userAuthenCmd = "echo | set /p=\"" + userPW + "\" | kinit " + userName;
        System.out.println("\n\t--- userAuthenCmd: " + userAuthenCmd);        
        
        String curlCmd_R_1stNameNode = " -sk --negotiate -u :  --location-trusted " + firstHDFSNN_ConnStr + "/webhdfs/v1/user/m041785/test/Knox/employee_webhdfs_curl.txt?op=OPEN > \"" + curlCmdExecuteTempResultFilePathAndName + "\"";
        
        String curlCmd_Full_1stNameNode = cleanTempTestResultsFileCmd + "  && " + userAuthenCmd + " && \"" + curlExeFilePath + "\" " + curlCmd_R_1stNameNode ;
        String winRunCurlCmd_1stNameNode = "cmd /c start cmd.exe /C  \"" + curlCmd_Full_1stNameNode + "\"";       
        System.out.println("\n\t--- winRunCurlCmd_1stNameNode: " + winRunCurlCmd_1stNameNode);  
        String winRunCurlCmdExeAndStatus_1stNameNode = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_1stNameNode, curlCmdExecuteTempResultFilePathAndName, tgtResult, 3);
        System.out.println("\n  III.1 WebHDFS Testing Result on the First Name Node Rest API: \n\t" + winRunCurlCmdExeAndStatus_1stNameNode);
                
        String winRunCurlCmd_2ndNameNode = winRunCurlCmd_1stNameNode.replaceAll(firstHDFSNN_ConnStr, secondHDFSNN_ConnStr);
        String winRunCurlCmdExeAndStatus_2ndNameNode = runCurlCmdAndGetCurlTestStatus (winRunCurlCmd_2ndNameNode, curlCmdExecuteTempResultFilePathAndName, tgtResult, 3);
        System.out.println("\n  III.2 WebHDFS Testing Result on the Second Name Node Rest API: \n\t" + winRunCurlCmdExeAndStatus_2ndNameNode);
        

    }//end main
    
    private static String runCurlCmdAndGetCurlTestStatus (String winRunCurlCmd, String curlCmdExecuteTempResultFilePathAndName, String tgtResult, int sleepSeconds){        
        Runtime rt = Runtime.getRuntime();
        @SuppressWarnings("unused")
        int exitVal = 10000;
        
        String curlTestStatus = "Failed - Result: Not Contains the Target --- " + tgtResult;
        try {
            Process proc = rt.exec(winRunCurlCmd);
            
            exitVal = proc.waitFor();
//          System.out.println("\n--- winRunCurlCmd: " + winRunCurlCmd);
//          System.out.println("--- Executing winRunCurlCmd exitVal is: " + exitVal);
            
            //Desktop.getDesktop().open(new File(curlCmdExecuteTempResultFilePathAndName));
            try {
                Thread.sleep(sleepSeconds*1000);
            } catch (InterruptedException e) {          
                e.printStackTrace();
            }
            String curlCmdResult = findTargetLineInALocalFile (curlCmdExecuteTempResultFilePathAndName, tgtResult);
            //System.out.println("\n--- curlCmdResult: " + curlCmdResult);
            
            if (curlCmdResult.contains(tgtResult)){
                curlTestStatus = "Succeeded - Result: Contains the Target --- " + tgtResult;
            }

                                
        } catch (IOException e1) {          
            e1.printStackTrace();
        } catch (InterruptedException e2) {         
            e2.printStackTrace();
        }
        
        return curlTestStatus;      
    }//end runCurlCmdAndGetCurlTestStatus
    
    private static String findTargetLineInALocalFile (String localFilePathAndName, String target){
        String foundLine = "";
        try {
            FileReader aFileReader = new FileReader(localFilePathAndName);
            BufferedReader br = new BufferedReader(aFileReader);
            String line = "";
                        
            while ((line = br.readLine()) != null ) {
                if (line.contains(target)){
                    foundLine = line;
                    break;
                } 
            }//end while            
            br.close();
            aFileReader.close();            
        } catch (FileNotFoundException e) {         
            e.printStackTrace();
        } catch (IOException e) {           
            e.printStackTrace();
        }       
        return foundLine;
    }//end findTargetLineInALocalFile
    
    private static void prepareFile_keepOld (String localFilePathAndName, String fileNoticeInfo){
        File aFile = new File (localFilePathAndName);
        try {
//          if (aFile.exists()){
//              aFile.delete();
//              System.out.println("\n .. Deleted file for dump_Backloading: \n" + localFilePathAndName);
//              aFile.createNewFile();
//              System.out.println("\n .. Created file for dump_Backloading: \n" + localFilePathAndName);
//          }
            
            if (!aFile.exists()){
                aFile.createNewFile();
                System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
            }               
        } catch (IOException e) {               
            e.printStackTrace();
        }           
    }//end prepareFile

}
