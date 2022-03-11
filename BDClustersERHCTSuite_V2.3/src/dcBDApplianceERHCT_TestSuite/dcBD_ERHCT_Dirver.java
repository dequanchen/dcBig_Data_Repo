package dcBDApplianceERHCT_TestSuite;

import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 1/5/2016 (Kerberos); 1/14/2016; 2/29/2016; 3/24/2016
*/

public class dcBD_ERHCT_Dirver {
	private static int loginingTimesSeqNo = 5; //BDDev-->11; //BDSdbx--> 5
	
	public static void main(String[] args) {
		String [] erhctParametersArray = new String [4];
		erhctParametersArray[0] = Integer.toString(loginingTimesSeqNo);
		
		String bdClusterName = "BDDev";
		bdClusterName = getUserChosenMcHadoopClusterName();
		System.out.println("*** The bdClusterName is: " + bdClusterName );
		erhctParametersArray[1] = bdClusterName;
		
		String loginUserAccountName = JOptionPane.showInputDialog("Please input the trusted Login User Account Name for MC \'" + bdClusterName + "\' Hadoop Cluster" , "");
		System.out.println("\n*** The loginUserAccountName is: " + loginUserAccountName );
		erhctParametersArray[2] = loginUserAccountName;
		
		String loginUserADPassWd = getUserPromptInputPassWord(loginUserAccountName);
		if (loginUserADPassWd.isEmpty()){
			System.out.println("\n*** Your input AD password for the login user account is empty, so this program is exitiing... !!! ");
			System.exit(0);
		}
		//System.out.println("\n*** The loginUserADPassWd is: " + loginUserADPassWd );
		erhctParametersArray[3] = loginUserADPassWd;
			
		String userChosenRunMode = getUserChosenRunMode();
		System.out.println("*** The userChosenRunMode is: " + userChosenRunMode );
		
		try {
			if (userChosenRunMode.equalsIgnoreCase("Diagnosis or Development")){
				if (bdClusterName.equalsIgnoreCase("BDDev")){
					dcBDDev_ERHCT_Driver_Diagnosis.main(erhctParametersArray);					
				}
				if (bdClusterName.equalsIgnoreCase("BDInt")){
					//dcBDProd2_Int_ERHCT_Driver_Diagnosis.main(erhctParametersArray);					
				}
				if (bdClusterName.equalsIgnoreCase("BDProd")){
					//dcBDInt2_Prod_ERHCT_Driver_Diagnosis.main(erhctParametersArray);
				}
				if (bdClusterName.equalsIgnoreCase("BDSdbx")){
					dcBDSdbx_ERHCT_Driver_Diagnosis.main(erhctParametersArray);
				}
			}
			
			if (userChosenRunMode.equalsIgnoreCase("Regular or Ochestrated All")){
				if (bdClusterName.equalsIgnoreCase("BDDev")){
					dcBDDev_ERHCT_Driver.main(erhctParametersArray);
				}
				if (bdClusterName.equalsIgnoreCase("BDInt")){
					//dcBDProd2_Int_ERHCT_Driver.main(erhctParametersArray);
				}
				if (bdClusterName.equalsIgnoreCase("BDProd")){
					//dcBDInt2_Prod_ERHCT_Driver.main(erhctParametersArray);
				}
				if (bdClusterName.equalsIgnoreCase("BDSdbx")){
					dcBDSdbx_ERHCT_Driver.main(erhctParametersArray);
				}
			}			
		} catch (Exception e) {			
			e.printStackTrace();
		}

	}//end main
	
	private static String getUserChosenMcHadoopClusterName (){		
		JPanel tgtClustersPanel = new JPanel();
	    tgtClustersPanel.add(new JLabel("Please Select Target MC Hadoop Cluster To Do ERHC Testing: "));
        DefaultComboBoxModel<String> tgtBDClusters = new DefaultComboBoxModel<String>();
        tgtBDClusters.addElement("BDDev");
        tgtBDClusters.addElement("BDInt");
        tgtBDClusters.addElement("BDProd");
        tgtBDClusters.addElement("BDSdbx");
        
        JComboBox<String> comboBox_Clusters = new JComboBox<String>(tgtBDClusters);
        tgtClustersPanel.add(comboBox_Clusters);
        
        String tgtClusterName ="";
        int result = JOptionPane.showConfirmDialog(null, tgtClustersPanel, "MC Hadoop Cluster", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        switch (result) {
            case JOptionPane.OK_OPTION:
            	tgtClusterName = comboBox_Clusters.getSelectedItem().toString();
                System.out.println("You Selected the MC Hadoop Cluster: " + tgtClusterName);
                break;
            case JOptionPane.CANCEL_OPTION: 
            	//Ask user one more time to select a target table or just cancel the program execution
            	System.out.println("No Target MC Hadoop Cluster Selected - Please Select A MC Hadoop Cluster for ERHC Testing");
            	result = JOptionPane.showConfirmDialog(null, tgtClustersPanel, "Staging Target Table", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            	switch (result) {
            	    case JOptionPane.OK_OPTION:
            	    	tgtClusterName = comboBox_Clusters.getSelectedItem().toString();
	                    System.out.println("You Selected the MC Hadoop Cluster: " + tgtClusterName);
	                    break;
	                case JOptionPane.CANCEL_OPTION:
	                	System.out.println("User Confirmed - Exiting Current program");
		            	System.exit(0);	            	
            	}
        }//end switch
        	               	        
        return tgtClusterName;
	}
	
	private static String getUserChosenRunMode (){		
		JPanel tgtRunModePanel = new JPanel();
	    tgtRunModePanel.add(new JLabel("Please Select ERHC Testing Run Mode: "));
        DefaultComboBoxModel<String> tgtBDRunMode = new DefaultComboBoxModel<String>();
        tgtBDRunMode.addElement("Diagnosis or Development");
        tgtBDRunMode.addElement("Regular or Ochestrated All");        
        
        JComboBox<String> comboBox_RunMode = new JComboBox<String>(tgtBDRunMode);
        tgtRunModePanel.add(comboBox_RunMode);
        
        String tgtRunMode ="";
        int result = JOptionPane.showConfirmDialog(null, tgtRunModePanel, "ERHCT Run Mode", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        switch (result) {
            case JOptionPane.OK_OPTION:
            	tgtRunMode = comboBox_RunMode.getSelectedItem().toString();
                System.out.println("You Selected ERHCT Run Mode: " + tgtRunMode);
                break;
            case JOptionPane.CANCEL_OPTION: 
            	//Ask user one more time to select a target table or just cancel the program execution
            	System.out.println("No Target MC Hadoop Cluster Selected - Please Select A MC Hadoop Cluster for ERHC Testing");
            	result = JOptionPane.showConfirmDialog(null, tgtRunModePanel, "Staging Target Table", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            	switch (result) {
            	    case JOptionPane.OK_OPTION:
            	    	tgtRunMode = comboBox_RunMode.getSelectedItem().toString();
	                    System.out.println("You Selected ERHCT Run Mode: " + tgtRunMode);
	                    break;
	                case JOptionPane.CANCEL_OPTION:
	                	System.out.println("User Confirmed - Exiting Current program");
		            	System.exit(0);	            	
            	}
        }//end switch
        	               	        
        return tgtRunMode;
	}
	
	private static String getUserPromptInputPassWord (String accountName){
		//1. Method #1:
		JPasswordField jpf = new JPasswordField(25);
		JLabel messageLabel = new JLabel("Type in Enterprise AD Password: ");
	
	    JPanel pwPanel = new JPanel(new GridLayout(0,1));
	    pwPanel.add(messageLabel);
	    pwPanel.add(jpf);
	    String title = "Enter the AD Password for the Login User - " + accountName;
	    pwPanel.getFocusTraversalKeys(0);	    	   
		JOptionPane.showMessageDialog(null, pwPanel, title, JOptionPane.INFORMATION_MESSAGE );			
		String pw = new String(jpf.getPassword());
		return pw;
				
//		//2. Method #2:			
//		JPasswordField jpf = new JPasswordField(30);	    
//		jpf.getFocusTraversalKeys(0);
//		int x = JOptionPane.showConfirmDialog(null, jpf, "Enter the AD Password for the Login User - " + accountName, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
//	    
//		if (x == JOptionPane.OK_OPTION) {
//	      return new String( jpf.getPassword( ));
//	    } else {
//	    	return "";
//	    }	
		
//		//3. Method #3:
//		JPanel pwPanel = new JPanel();
//		JLabel label = new JLabel("Type in AD Password: ");
//		JPasswordField jpf = new JPasswordField(24);
//		pwPanel.add(label);
//		pwPanel.add(jpf);
//		pwPanel.getFocusTraversalKeys(0);
//		String[] options = new String[]{ "OK"};
//		int x = JOptionPane.showOptionDialog(null, pwPanel, "Enter the AD Password for the Login User Account - " + accountName,
//		                         JOptionPane.NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
//		                         null, options, options[0]);
//		if(x == JOptionPane.OK_OPTION) {
//			return new String(jpf.getPassword());
//		} else {
//			return "";
//		}

	}//end getUserPromptInputPassWord

}
