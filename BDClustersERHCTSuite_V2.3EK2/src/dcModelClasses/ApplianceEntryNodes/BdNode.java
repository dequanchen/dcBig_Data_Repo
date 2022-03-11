package dcModelClasses.ApplianceEntryNodes;

import dcModelClasses.ConfigureULWResources;
import dcModelClasses.ULServerCommandFactory;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/06-07/2014; 5/5/2017
*/ 

public class BdNode {	
	private String bdNodeName = "";
	private String bdClusterName = "";
	private ULServerCommandFactory bdENCmdFactory = null;
	private ULServerCommandFactory bdENRootCmdFactory = null;

	public BdNode(String aBdNodeName, String aBdClusterName) {		
		this.bdNodeName = aBdNodeName;
		this.bdClusterName = aBdClusterName;
		
		ConfigureULWResources currConfigureULWResource  = new ConfigureULWResources();
		
		if (bdClusterName.equalsIgnoreCase("BDDev1")
				//|| bdClusterName.equalsIgnoreCase("Dev")
				|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			if (this.bdNodeName.equalsIgnoreCase("AllNodes")
					|| this.bdNodeName.equalsIgnoreCase("All") ){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1AllNodesNonRootConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1AllNodesRootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("EN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN02RootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("MN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN01ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN02ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN03ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN03RootConParameters());
			}

			if (this.bdNodeName.equalsIgnoreCase("KX01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode01RootConParameters());
			}			
			if (this.bdNodeName.equalsIgnoreCase("KX02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode02ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode02RootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("DN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN05ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1DN05RootConParameters());
			}			
			
		}//end "BDDev1"
		
		if (bdClusterName.equalsIgnoreCase("BDDev3")
				|| bdClusterName.equalsIgnoreCase("MC_BDDev3")){
			if (this.bdNodeName.equalsIgnoreCase("AllNodes")
					|| this.bdNodeName.equalsIgnoreCase("All") ){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3AllNodesNonRootConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3AllNodesRootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("EN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN05ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3EN05RootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("MN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3MN01ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3MN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3MN02ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3MN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3MN03ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3MN03RootConParameters());
			}	
			
			if (this.bdNodeName.equalsIgnoreCase("DN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN05ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev3DN05RootConParameters());
			}			
			
		}//end "BDDev3"
		
		
		
		
		
		if (bdClusterName.equalsIgnoreCase("BDProd2")
				|| bdClusterName.equalsIgnoreCase("Int")
				|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			if (this.bdNodeName.equalsIgnoreCase("AllNodes")
					|| this.bdNodeName.equalsIgnoreCase("All") ){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2AllNodesNonRootConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2AllNodesRootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("EN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN01ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN02ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN03ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN04ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN05ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN05RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN06")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN06ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN06RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN07")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN07ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN07RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN08")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN08ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN08RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN09")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN09ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN09RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN10")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN10ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN10RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN11")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN11ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN11RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN12")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN12ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN12RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN13")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN13ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN13RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN14")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN14ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN14RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN15")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN15ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN15RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN16")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN16ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN16RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN17")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN17ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN17RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN18")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN18ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN18RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN19")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN19ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN19RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN20")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN20ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN20RootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("MN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN01ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN02ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN03ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN03RootConParameters());
			}

			if (this.bdNodeName.equalsIgnoreCase("KX03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03RootConParameters());
			}			
			if (this.bdNodeName.equalsIgnoreCase("KX04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode04RootConParameters());				
			}	
			

			if (this.bdNodeName.equalsIgnoreCase("DN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN01ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN02ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN03ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN04ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN05ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN05RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN06")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN06ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN06RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN07")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN07ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN07RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN08")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN08ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN08RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN09")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN09ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN09RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN10")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN10ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN10RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN11")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN11ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN11RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN12")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN12ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN12RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN13")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN13ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN13RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN14")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN14ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN14RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN15")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN15ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN15RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN16")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN16ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN16RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN17")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN17ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN17RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN18")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN18ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN18RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN19")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN19ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN19RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN20")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN20ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN20RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN21")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN21ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN21RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN22")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN22ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN22RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN23")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN23ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN23RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN24")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN24ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN24RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN25")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN25ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN25RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN26")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN26ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN26RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN27")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN27ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN27RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN28")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN28ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN28RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN29")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN29ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN29RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN30")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN30ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN30RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN31")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN31ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN31RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN32")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN32ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2DN32RootConParameters());
			}
			
		}//end "BDProd2"
		
		if (bdClusterName.equalsIgnoreCase("BDProd3")
				|| bdClusterName.equalsIgnoreCase("MC_BDProd3")){
			if (this.bdNodeName.equalsIgnoreCase("AllNodes")
					|| this.bdNodeName.equalsIgnoreCase("All") ){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3AllNodesNonRootConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3AllNodesRootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("EN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN05ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN05RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN06")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN06ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN06RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN07")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN07ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN07RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN08")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN08ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3EN08RootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("MN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3MN01ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3MN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3MN02ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3MN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3MN03ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3MN03RootConParameters());
			}	
			
			if (this.bdNodeName.equalsIgnoreCase("DN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN05ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN05RootConParameters());
			}
			//...
			if (this.bdNodeName.equalsIgnoreCase("DN32")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN32ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd3DN32RootConParameters());
			}	
			
		}//end "BDProd3"
		
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			if (this.bdNodeName.equalsIgnoreCase("AllNodes")
					|| this.bdNodeName.equalsIgnoreCase("All") ){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2AllNodesNonRootConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2AllNodesRootConParameters());
			}			
			
			
			if (this.bdNodeName.equalsIgnoreCase("EN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN05ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN05RootConParameters());
			}

			if (this.bdNodeName.equalsIgnoreCase("MN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2MN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2MN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2MN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2MN02RootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("KX03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03RootConParameters());
			}			
			if (this.bdNodeName.equalsIgnoreCase("KX04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode04RootConParameters());				
			}	
			

			if (this.bdNodeName.equalsIgnoreCase("DN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN05ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN05RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN06")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN06ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN06RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN07")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN07ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN07RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN08")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN08ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN08RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN09")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN09ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN09RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN10")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN10ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2DN10RootConParameters());
			}
			
		}//end "BDTest2"
		
		if (bdClusterName.equalsIgnoreCase("BDTest3")
				|| bdClusterName.equalsIgnoreCase("MC_BDTest3")){
			if (this.bdNodeName.equalsIgnoreCase("AllNodes")
					|| this.bdNodeName.equalsIgnoreCase("All") ){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3AllNodesNonRootConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3AllNodesRootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("EN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3EN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3EN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3EN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3EN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3EN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3EN03RootConParameters());
			}
						
			if (this.bdNodeName.equalsIgnoreCase("MN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3MN01ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3MN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3MN02ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3MN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3MN03ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3MN03RootConParameters());
			}	
			
			if (this.bdNodeName.equalsIgnoreCase("DN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN05ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN05RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN06")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN06ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN06RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN07")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN07ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN07RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN08")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN08ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN08RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN09")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN09ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest3DN09RootConParameters());
			}
			
		}//end "BDTest3"
		
		
		
		if (bdClusterName.equalsIgnoreCase("BDSdbx")
				|| bdClusterName.equalsIgnoreCase("BDSBX")
				|| bdClusterName.equalsIgnoreCase("SDBX")
				|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
			if (this.bdNodeName.equalsIgnoreCase("AllNodes")
					|| this.bdNodeName.equalsIgnoreCase("All") ){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxAllNodesNonRootConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxAllNodesRootConParameters());
			}


			if (this.bdNodeName.equalsIgnoreCase("EN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("EN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN04RootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("MN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("MN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN02RootConParameters());
			}
			
			if (this.bdNodeName.equalsIgnoreCase("KX01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode01RootConParameters());
			}			
			if (this.bdNodeName.equalsIgnoreCase("KX02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode02ConParameters());	
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode02RootConParameters());
			}	
			
			if (this.bdNodeName.equalsIgnoreCase("DN01")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN01ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN01RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN02")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN02ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN02RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN03")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN03ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN03RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN04")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN04ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN04RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN05")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN05ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN05RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN06")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN06ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN06RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN07")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN07ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN07RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN08")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN08ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN08RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN09")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN09ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN09RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN10")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN10ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN10RootConParameters());
			}
			if (this.bdNodeName.equalsIgnoreCase("DN11")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN11ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN11RootConParameters());			
			}
			if (this.bdNodeName.equalsIgnoreCase("DN12")){
				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN12ConParameters());
				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxDN12RootConParameters());
			}
			
		}//end "BDSdbx"	
		
//		if (this.bdNodeName.equalsIgnoreCase("AllNodes")
//				|| this.bdNodeName.equalsIgnoreCase("All") ){			
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2AllNodesNonRootConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2AllNodesRootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2AllNodesNonRootConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2AllNodesRootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1AllNodesNonRootConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1AllNodesRootConParameters());
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxAllNodesNonRootConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxAllNodesRootConParameters());
//			}	
//			
////			if (this.bdClusterName.equalsIgnoreCase("HWSdbx") || this.bdClusterName.equalsIgnoreCase("HW_Sdbx") ){
////				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getHwSdbxAllNodesNonRootConParameters());
////				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getHwSdbxAllNodesRootConParameters());
////			}			
//		}
				
		
//		if (this.bdNodeName.equalsIgnoreCase("EN01")){
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN01ConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN01RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN01ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN01RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN01ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN01RootConParameters());
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN01ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN01RootConParameters());
//			}	
//			
////			if (this.bdClusterName.equalsIgnoreCase("HWSdbx") || this.bdClusterName.equalsIgnoreCase("HW_Sdbx") ){
////				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getHwSdbxNodeConParameters());
////				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getHwSdbxNodeRootConParameters());
////			}	
//		}//end 1st-if for an Entry node
//		
//		
//		if (this.bdNodeName.equalsIgnoreCase("EN02")){
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN02ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN02RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN02ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN02RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN02ConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN02RootConParameters());
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN02ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN02RootConParameters());
//			}			
//		}//end 2nd-if for an Entry node
//		
//		//...for more entry node, e.g., EN03, EN04..
//		if (this.bdNodeName.equalsIgnoreCase("EN03")){
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN03ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN03RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN03ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN03RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				//this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN03ConParameters());
//				//this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN03RootConParameters());
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN03ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN03RootConParameters());
//			}			
//		}//end 2nd-if for an Entry node
//		
//		if (this.bdNodeName.equalsIgnoreCase("EN04")){
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN04ConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN04RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN04ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN04RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				//this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN04ConParameters());
//				//this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN04RootConParameters());
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN04ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN04RootConParameters());
//			}			
//		}//end 2nd-if for an Entry node
//	
//		//...for more entry node, e.g., EN05, EN06..
//		if (this.bdNodeName.equalsIgnoreCase("EN05")){
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN05ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN05RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN05ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN05RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				//this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN05ConParameters());
//				//this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN05RootConParameters());
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				//this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN05ConParameters());
//				//this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN05RootConParameters());
//			}			
//		}//end 2nd-if for an Entry node
//		
//		if (this.bdNodeName.equalsIgnoreCase("EN06")){
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN06ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2EN06RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				//this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN06ConParameters());
//				//this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2EN06RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				//this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN06ConParameters());
//				//this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1EN06RootConParameters());
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				//this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN06ConParameters());
//				//this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxEN06RootConParameters());
//			}			
//		}//end 2nd-if for an Entry node
//		
//		//end...for more entry node, e.g., MN01, MN02..
//
//		if (this.bdNodeName.equalsIgnoreCase("MN02")){
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN02ConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN02RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2MN02ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2MN02RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN02ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN02RootConParameters());
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN02ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN02RootConParameters());
//			}			
//		}//end last-if for an Entry node (2ndary master node)
//		
//		if (this.bdNodeName.equalsIgnoreCase("MN01")){
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN01ConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdProd2MN01RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2MN01ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdTest2MN01RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN01ConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdDev1MN01RootConParameters());
//			}
//			
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN01ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdSdbxMN01RootConParameters());
//			}			
//		}//end last-if for an Entry node (2ndary master node)
///////////////////////////////////////////////////////////////////////////////				
//		if (this.bdNodeName.equalsIgnoreCase("KX02")
//				|| this.bdNodeName.equalsIgnoreCase("BDProd2=KX02")){
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03RootConParameters());
//			}				
//		}//end BDProd2 Knox node
//		
//		if (this.bdNodeName.equalsIgnoreCase("KX03")
//				|| this.bdNodeName.equalsIgnoreCase("BDProd2=KX03")){
//			//if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
//			//		|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//			//		|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//			//	this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03ConParameters()); 
//			//	this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03RootConParameters());
//			//}
//			
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode03RootConParameters());
//			}				
//		}//end BDTest2 Knox node
//		
//		if (this.bdNodeName.equalsIgnoreCase("KX01")
//				||this.bdNodeName.equalsIgnoreCase("BDDev1=KX01")){
//			if (bdClusterName.equalsIgnoreCase("BDDev1")
//					|| bdClusterName.equalsIgnoreCase("Dev")
//					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode01ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode01RootConParameters());
//			}
//			if (bdClusterName.equalsIgnoreCase("BDSDBX")
//					|| bdClusterName.equalsIgnoreCase("BDSBX")
//					|| bdClusterName.equalsIgnoreCase("SDBX")
//					|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode02ConParameters());	
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode02RootConParameters());
//			}	
//		}//end BDDev1 Knox node
//		
//		if (this.bdNodeName.equalsIgnoreCase("KX04")
//				|| this.bdNodeName.equalsIgnoreCase("BDTest2=KX04")){
//			//if (bdClusterName.equalsIgnoreCase("BDSDBX")
//			//		|| bdClusterName.equalsIgnoreCase("BDSBX")
//			//		|| bdClusterName.equalsIgnoreCase("SDBX")
//			//		|| bdClusterName.equalsIgnoreCase("MC_BDSDBX")){
//			//	this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode04ConParameters());	
//			//	this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode04RootConParameters());
//			//}	
//			
//			if (bdClusterName.equalsIgnoreCase("BDProd2")
//					|| bdClusterName.equalsIgnoreCase("Int")
//					|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//				this.bdENCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode04ConParameters());
//				this.bdENRootCmdFactory = new ULServerCommandFactory(currConfigureULWResource.getBdKnoxNode04RootConParameters());
//			}				
//		}//end BDSdbx Knox node
		
		
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

	public ULServerCommandFactory getBdENCmdFactory() {
		return bdENCmdFactory;
	}

	public void setBdENCmdFactory(ULServerCommandFactory bdENCmdFactory) {
		this.bdENCmdFactory = bdENCmdFactory;
	}

	public ULServerCommandFactory getBdENRootCmdFactory() {
		return bdENRootCmdFactory;
	}

	public void setBdENRootCmdFactory(ULServerCommandFactory bdENRootCmdFactory) {
		this.bdENRootCmdFactory = bdENRootCmdFactory;
	}
	
}//end class
