package com.olbius.basehr.importExport;

public class ImportExportFileFactory {
	public static final String EXCEL_FILE = "excel";
	static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        }
	    });
	}
	
	public static ImportExportFile getImportExportFile(String fileType){
		if(EXCEL_FILE.equals(fileType)){
			return new ImportExportExcel();
		}
		return null;
	}
}
