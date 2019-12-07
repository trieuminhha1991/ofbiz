package com.olbius.basehr.importExport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public interface ImportExportFile {
	public void importDataFromFile(Delegator delegator, GenericValue userLogin, Object config) throws GenericEntityException;
	public void exportDataToFile(HttpServletRequest request, HttpServletResponse response, Object config, String url);
	
}
