package WebHDFSHBaseESTesting_Win.V1;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 3/1/2017
*/ 

public class dcDiagnoseWebHbaseRestAPI {

	public static void main(String[] args) {
		

	}

}

////Methods for creating user keytab file
//kinit -k -t C:\m041785.keytab M041785@MFAD.MFROOT.ORG && curl -s --negotiate -u :  http://hdpr04mn01.mayo.edu:8084/employee_webhbase1/101/cfs:firstName
//
//1. ON BDDev EN01: Create User Keytab file
//ktutil
//ktutil:  addent -password -p M041785@MFAD.MFROOT.ORG -k 1 -e rc4-hmac
//Password for M041785@MFAD.MFROOT.ORG: [enter your password]
//ktutil:  addent -password -p M041785@MFAD.MFROOT.ORG -k 1 -e aes256-cts
//Password for M041785@MFAD.MFROOT.ORG: [enter your password]
//ktutil:  wkt m041785.keytab
//ktutil:  quit
//
//2.Download m041785.keytab from Hadoop Cluster EN01 to Windows as C:\m041785.keytab