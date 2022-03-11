package OS_Hardening;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class dcUpdateXMLFileTrial {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		String xmlFilePathAndName = "C:\\BD\\BD_UAT\\oozie\\knox\\workflow-configuration_BDDev.xml";
		ArrayList<Node> allDocNodeList = new ArrayList<Node> ();
		
		DOMParser parser = new DOMParser();
	    parser.parse("../BDClustersERHCTSuite_V2.3EK2/src/OS_Hardening/workflow-configuration_BDDev.xml");
	    Document doc = parser.getDocument();
	    System.out.println("\n*** doc.toString(): " + doc.toString());    
		
		
//		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//		Document doc = docBuilder.parse(xmlFilePathAndName);
//		System.out.println("\n*** doc.toString(): " + doc.toString());
	    
//	    NodeList root = doc.getChildNodes();
//	    System.out.println("\n*1* root.getLength(): " + root.getLength());
//	    System.out.println("\n*** root.item(0).getNodeName(): " + root.item(0).getNodeName());
//        System.out.println("*** root.item(0).getNodeType(): " + root.item(0).getNodeType());
//        System.out.println("*** root.item(0).getNodeValue(): " + root.item(0).getNodeValue());
//	    
//	    
//	    Node configuration = getNode("configuration", root);
//	    NodeList configNodes = configuration.getChildNodes();
//	    System.out.println("\n*2* configNodes.getLength(): " + configNodes.getLength());
	    
		allDocNodeList = getAllNodesOfAnXMLDoc(doc, allDocNodeList);
	    
	    for ( int i = 0; i < allDocNodeList.size();i++ ) {
	        Node node = allDocNodeList.get(i);
	        System.out.println("\n(" + (i+1) + ") *** node.getNodeName(): " + node.getNodeName());
	        System.out.println("*** node.getNodeType(): " + node.getNodeType());
	        System.out.println("*** node.getNodeValue(): " + node.getNodeValue());
	        
	       
	    }
	    
//	    FileReader aFileReader = new FileReader("../BDClustersERHCTSuite_V2.3EK2/src/OS_Hardening/workflow-configuration_BDDev.xml");
//		BufferedReader br = new BufferedReader(aFileReader);
//		String line = "";
//		while ((line = br.readLine()) != null) {
//			 System.out.println("*** line: " + line);				
//		}
//		br.close();
	    


		XPath xPath = XPathFactory.newInstance().newXPath();
		Node startDateNode = (Node) xPath.compile("/configuration/property/value").evaluate(doc, XPathConstants.NODE);
		System.out.println("\n*** startDateNode: " + startDateNode.getNodeValue());
		//startDateNode.setTextContent("29/07/2015");
		
		
		
	}
	
	private static ArrayList<Node> getAllNodesOfAnXMLDoc (Document doc, ArrayList<Node> allDocNodeList){
		NodeList rootNodeList = doc.getChildNodes();
		 
		getAllNodesOfARootNode (rootNodeList, allDocNodeList);
		
		return allDocNodeList;
	}//end getAllNodesOfAnXMLDoc
	
	private static void getAllNodesOfARootNode (NodeList aNodeList, ArrayList<Node> allDocNodeList){
		for (int i = 0; i < aNodeList.getLength(); i++) {
			Node tempNode = aNodeList.item(i);
			allDocNodeList.add(tempNode);
			boolean tempNodeChildreStatus = IsNodeHasChildren (tempNode);
			if (tempNodeChildreStatus == true){
				NodeList tempNodeList = tempNode.getChildNodes();
				
				getAllNodesOfARootNode (tempNodeList, allDocNodeList);
			}
		}		
		
	}//end getAllNodesOfARootNode
	
	private static boolean IsNodeHasChildren (Node aNode){
		boolean hasChildrenStatus = false;
		if (aNode.getChildNodes().getLength() >= 1){
			hasChildrenStatus = true;
		}
		
		return hasChildrenStatus; 
	}
	
//	protected static Node getNode(String tagName, NodeList nodes) {
//	    for ( int x = 0; x < nodes.getLength(); x++ ) {
//	        Node node = nodes.item(x);
//	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
//	            return node;
//	        }
//	    }
//	 
//	    return null;
//	}

}
