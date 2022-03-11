package dcModelClasses;

import dcModelClasses.HBase;

public class dcModelClassTesting {
	private static String bdClusterName = "BDInt";//BDProd..BDInt..BDDev

	public static void main(String[] args) {
		HBase aHBase = new HBase(bdClusterName);	        
//		String exportHadoopClassPath = aHBase.getExportHadoopClassPath_ForHBaseStr();
		String setHiveAuxJarsPathStr = aHBase.getSetHiveAuxJarsPath_ForHBaseStr();
//		String exportPigClassPath = aHBase.getExportPigClassPath_ForHBaseStr();
//		String exportPigOpts = aHBase.getExportPigOpts_ForHBaseStr();
//		String exportHcatHome = aHBase.getExportHcatHome_ForHBaseStr();
//		String pigRegisterHBaseJarsForHcatStr = aHBase.getPigRegisterHBaseJarsForHcatStr();
		
//		System.out.println(" *** ExportHadoopClassPath_ForHBaseStr: " + exportHadoopClassPath);
        System.out.println(" *** SetHiveAuxJarsPath_ForHBaseStr: " + setHiveAuxJarsPathStr); 
//        System.out.println(" *** ExportPigClassPath_ForHBaseStr: " + exportPigClassPath);
//        System.out.println(" *** ExportPigOpts_ForHBaseStr: " + exportPigOpts);
//        System.out.println(" *** exportHcatHome: " + exportHcatHome);
//        System.out.println(" *** pigRegisterHBaseJarsForHcatStr: " + pigRegisterHBaseJarsForHcatStr);
	}

}
