package dcModelClasses.ApplianceEntryNodes;

import dcModelClasses.ConfigureULWResources;
import dcModelClasses.ULServerCommandFactory;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/06-07/2014 
*/ 

public class BdNode {	
	private String bdNodeName = "";
	private String bdClusterName = "";
	private ULServerCommandFactory bdENCommFactory = null;

	public BdNode(String aBdNodeName, String aBdClusterName) {
		if (aBdClusterName.equalsIgnoreCase("BDProd2")){
			aBdClusterName = "BDInt";
		}
		if (aBdClusterName.equalsIgnoreCase("BDInt2")){
			aBdClusterName = "BDProd";
		}
		this.bdNodeName = aBdNodeName;
		this.bdClusterName = aBdClusterName;
		ConfigureULWResources currConfigureULWResource  = new ConfigureULWResources();
		
		if (this.bdNodeName.equalsIgnoreCase("EN01")){
			if (bdClusterName.equalsIgnoreCase("BDInt")
					|| bdClusterName.equalsIgnoreCase("Int")
					|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdIntEN01ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProdEN01ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDDev")
					|| bdClusterName.equalsIgnoreCase("Dev")
					|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDevEN01ConParameters());				
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSDBX")
					|| bdClusterName.equalsIgnoreCase("BDSBX")
					|| bdClusterName.equalsIgnoreCase("SDBX")
					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN01ConParameters());								
			}	
			
			if (this.bdClusterName.equalsIgnoreCase("HWSdbx") || this.bdClusterName.equalsIgnoreCase("HW_Sdbx") ){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getHwSdbxNodeConParameters());
			}	
		}//end 1st-if for an Entry node
		
		
		if (this.bdNodeName.equalsIgnoreCase("EN02")){
			if (bdClusterName.equalsIgnoreCase("BDInt")
					|| bdClusterName.equalsIgnoreCase("Int")
					|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdIntEN02ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProdEN02ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDDev")
					|| bdClusterName.equalsIgnoreCase("Dev")
					|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDevEN02ConParameters());				
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSDBX")
					|| bdClusterName.equalsIgnoreCase("BDSBX")
					|| bdClusterName.equalsIgnoreCase("SDBX")
					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN02ConParameters());								
			}			
		}//end 2nd-if for an Entry node
		
		//...for more entry node, e.g., EN03, EN04..
		if (this.bdNodeName.equalsIgnoreCase("EN03")){
			if (bdClusterName.equalsIgnoreCase("BDInt")
					|| bdClusterName.equalsIgnoreCase("Int")
					|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdIntEN03ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProdEN03ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDDev")
					|| bdClusterName.equalsIgnoreCase("Dev")
					|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
				//this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDevEN03ConParameters());				
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSDBX")
					|| bdClusterName.equalsIgnoreCase("BDSBX")
					|| bdClusterName.equalsIgnoreCase("SDBX")
					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN03ConParameters());								
			}			
		}//end 2nd-if for an Entry node
		
		if (this.bdNodeName.equalsIgnoreCase("EN04")){
			if (bdClusterName.equalsIgnoreCase("BDInt")
					|| bdClusterName.equalsIgnoreCase("Int")
					|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdIntEN04ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProdEN04ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDDev")
					|| bdClusterName.equalsIgnoreCase("Dev")
					|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
				//this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDevEN04ConParameters());				
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSDBX")
					|| bdClusterName.equalsIgnoreCase("BDSBX")
					|| bdClusterName.equalsIgnoreCase("SDBX")
					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN04ConParameters());								
			}			
		}//end 2nd-if for an Entry node
	
		//...for more entry node, e.g., EN05, EN06..
		if (this.bdNodeName.equalsIgnoreCase("EN05")){
			if (bdClusterName.equalsIgnoreCase("BDInt")
					|| bdClusterName.equalsIgnoreCase("Int")
					|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdIntEN05ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProdEN05ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDDev")
					|| bdClusterName.equalsIgnoreCase("Dev")
					|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
				//this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDevEN05ConParameters());				
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSDBX")
					|| bdClusterName.equalsIgnoreCase("BDSBX")
					|| bdClusterName.equalsIgnoreCase("SDBX")
					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
				//this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN05ConParameters());								
			}			
		}//end 2nd-if for an Entry node
		
		if (this.bdNodeName.equalsIgnoreCase("EN06")){
			if (bdClusterName.equalsIgnoreCase("BDInt")
					|| bdClusterName.equalsIgnoreCase("Int")
					|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdIntEN06ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
				//this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProdEN06ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDDev")
					|| bdClusterName.equalsIgnoreCase("Dev")
					|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
				//this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDevEN06ConParameters());				
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSDBX")
					|| bdClusterName.equalsIgnoreCase("BDSBX")
					|| bdClusterName.equalsIgnoreCase("SDBX")
					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
				//this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN06ConParameters());								
			}			
		}//end 2nd-if for an Entry node
		
		//end...for more entry node, e.g., MN01, MN02..

		if (this.bdNodeName.equalsIgnoreCase("MN02")){
			if (bdClusterName.equalsIgnoreCase("BDInt")
					|| bdClusterName.equalsIgnoreCase("Int")
					|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdIntMN02ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProdMN02ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDDev")
					|| bdClusterName.equalsIgnoreCase("Dev")
					|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDevMN02ConParameters());				
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSDBX")
					|| bdClusterName.equalsIgnoreCase("BDSBX")
					|| bdClusterName.equalsIgnoreCase("SDBX")
					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN02ConParameters());								
			}			
		}//end last-if for an Entry node (2ndary master node)
		
		if (this.bdNodeName.equalsIgnoreCase("MN01")){
			if (bdClusterName.equalsIgnoreCase("BDInt")
					|| bdClusterName.equalsIgnoreCase("Int")
					|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdIntMN01ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProdMN01ConParameters());				
			}
			if (bdClusterName.equalsIgnoreCase("BDDev")
					|| bdClusterName.equalsIgnoreCase("Dev")
					|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDevMN01ConParameters());				
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSDBX")
					|| bdClusterName.equalsIgnoreCase("BDSBX")
					|| bdClusterName.equalsIgnoreCase("SDBX")
					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
				this.bdENCommFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN01ConParameters());								
			}			
		}//end last-if for an Entry node (2ndary master node)
		
	}//end constructor
/////////////////////////////////////////////////////////////
	
	public String getBdNodeName() {
		return bdNodeName;
	}

	public void setBdNodeName(String bdNodeName) {
		this.bdNodeName = bdNodeName;
	}

	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public ULServerCommandFactory getBdENCommFactory() {
		return bdENCommFactory;
	}

	public void setBdENCommFactory(ULServerCommandFactory bdENCommFactory) {
		this.bdENCommFactory = bdENCommFactory;
	}

}//end class
