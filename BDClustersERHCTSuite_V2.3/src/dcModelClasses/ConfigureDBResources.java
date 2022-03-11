package dcModelClasses;


/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 04/09/2014 
*/ 

public class ConfigureDBResources { 
   
  private String[] edwProjDevDBConParameters = {"Microsoft SQL Server", "ROPEIM802Q\\PROJDEV", "TU05303", "78uijknm"}; //SqlServer V2012..vbfgrt45... bigdataTU..78uijknm
 
  //Default Constructor
  public ConfigureDBResources() {
     //Do Nothing
  }//end Default Constructor 



//-----------------------------------------------------------------------------------------
  //The following are getters and setters 

  public String[] getEdwProjDevDBConParameters() {
		return edwProjDevDBConParameters;
  }

  public void setEdwProjDevDBConParameters(String[] edwProjDevDBConParameters) {
		this.edwProjDevDBConParameters = edwProjDevDBConParameters;
  }

}//end class