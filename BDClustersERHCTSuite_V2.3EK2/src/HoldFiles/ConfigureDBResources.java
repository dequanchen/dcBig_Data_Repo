package HoldFiles;


/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 04/09/2014; 2/14/2017
*/ 

public class ConfigureDBResources { 
   
  private String[] edwProjDevDBConParameters = {"RDBMSServerType", "rdbmsSverInstanceName", "TU/WAxxxxx", "PWxxxxxx"}; 
 
  //Default Constructor
  public ConfigureDBResources() {
	  // ...
	  //code to get all the properties components from a secured silo or database
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