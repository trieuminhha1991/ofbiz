package com.olbius.common.export;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.webapp.event.ExportExcelEvents;

import javolution.util.FastMap;

public class ExportExcelServices {
	public final static String module = ExportExcelServices.class.getName();
	public static final String resource = "CommonUiLabels";
	public static final String resource_error = "CommonErrorUiLabels";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> exportExcelBigData(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String exportName = (String) context.get("exportName");
        Timestamp exportTime = (Timestamp) context.get("exportTime");
		try {
			String eventPath = (String) context.get("eventPath");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			boolean isCheckTimeout = context.get("isCheckTimeout") != null ? (Boolean) context.get("isCheckTimeout") : true;
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
			// get the classloader to use
			ClassLoader cl = null;

			cl = dctx.getClassLoader();
			
			Class<?> c = cl.loadClass(eventPath);

			if (c == null) {
				throw new GenericServiceException("Class [" + eventPath + "] location class is not assignable from com.olbius.ExportExcelAbstract");
			}
			
			final ExportExcelInteface exportExcel = (ExportExcelInteface) c.newInstance();
			exportExcel.init(dctx, context);
			
			String fileName = exportExcel.getFileName();
			String moduleExport = exportExcel.getModuleExport();
			String descriptionExport = exportExcel.getDescriptionExport();
			successResult.put("fileName", fileName);
			SXSSFWorkbook wb = null;
			
			String result = ExportExcelEvents.RESULT_ERROR; //exportExcel.run()
			if (isCheckTimeout) {
				if (!ExportExcelEvents.EXPORT_TYPE_OLAP.equals(exportExcel.getExportType())) {
					result = exportExcel.run();
				} else {
					ExecutorService executor = Executors.newSingleThreadExecutor();
					Callable<String> task = new Callable<String>() {
					   public String call() {
					      return exportExcel.run();
					   }
					};
					Future<String> future = executor.submit(task);
					try {
					   result = future.get(ExportExcelEvents.MAX_TIME_TIME_OUT, TimeUnit.MILLISECONDS); 
					} catch (TimeoutException ex) {
						// handle the timeout
						exportExcel.setIsForceTimeout(true);
						result = ExportExcelEvents.RESULT_TIMEOUT;
					} catch (InterruptedException e) {
						// handle the interrupts
						Debug.logError("Have a interrupt exception when export excel" + e.getMessage(), module);
					} catch (ExecutionException e) {
						// handle other exceptions
						throw e;
					} finally {
						future.cancel(true); // may or may not desire this
					}
				}
			} else {
				result = exportExcel.run();
			}
			
			String exportCond = "";
			String fromDateStr = ExportExcelUtil.getParameter(parameters, "fromDate");
			String thruDateStr = ExportExcelUtil.getParameter(parameters, "thruDate");
			if (fromDateStr != null) exportCond += "FromDate: " + fromDateStr + "@";
			if (thruDateStr != null) exportCond += "ThruDate: " + thruDateStr + "@";
			exportCond += listAllConditions.toString();
			
			//String result = exportExcel.run();
			if (result.equals(ExportExcelEvents.RESULT_ERROR)) {
				invokeChangeStatusFileExport(dispatcher, exportName, exportTime, userLogin);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonErrorWhenGetData", locale));
			} else if (result.equals(ExportExcelEvents.RESULT_TIMEOUT)) {
				// call service async
				
				exportName = c.getName();
				
				// find the same file is exporting
				GenericValue fileExporting = EntityUtil.getFirst(delegator.findByAnd("FileExportedTemp", UtilMisc.toMap("exportName", exportName, 
						"exportCond", exportCond, "module", moduleExport, "statusId", "EXPORTING", "createdByUserLogin", userLogin.getString("userLoginId")), null, false));
				if (fileExporting != null) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "CommonSystemIsExportingFileWithTheSameCondition", locale));
				}
				
				exportTime = UtilDateTime.nowTimestamp();
				GenericValue fileExportedTmp = delegator.makeValue("FileExportedTemp");
                fileExportedTmp.put("exportName", exportName);
                fileExportedTmp.put("exportTime", exportTime);
                fileExportedTmp.put("exportCond", exportCond);
                fileExportedTmp.put("fileName", fileName + ExportExcelEvents.FILE_EXE_TYPE);
                fileExportedTmp.put("module", moduleExport);
                fileExportedTmp.put("statusId", "EXPORTING");
                fileExportedTmp.put("description", descriptionExport);
                fileExportedTmp.put("createdByUserLogin", userLogin.get("userLoginId"));
                delegator.create(fileExportedTmp);
				
				Map<String, Object> parametersCtx = UtilMisc.toMap("eventPath", eventPath, "parameters", parameters, "isCheckTimeout", false, 
						"exportName", exportName, "exportTime", exportTime, "listAllConditions", listAllConditions, "listSortFields", listSortFields, "userLogin", userLogin, "locale", locale);
				dispatcher.runAsync("exportExcelBigData", parametersCtx);
				
				successResult.put("resultCode", ExportExcelEvents.RESULT_TIMEOUT);
			} else {
				// return wb
				if (isCheckTimeout) {
					wb = exportExcel.getWb();
					successResult.put("workBook", wb);
				} else {
					wb = exportExcel.getWb();
					// store file into JCR
					String path = null;
		        	String fileType = ExportExcelEvents.FILE_CONTENT_TYPE;
		        	wb.write(baos);
		        	ByteBuffer uploadedFile = ByteBuffer.wrap(baos.toByteArray());
		            Map<String, Object> contentCtx = FastMap.newInstance();
		            contentCtx.put("userLogin", userLogin);
		            contentCtx.put("uploadedFile", uploadedFile);
		            contentCtx.put("_uploadedFile_fileName", fileName);
		            contentCtx.put("_uploadedFile_contentType", fileType);
		            contentCtx.put("public", "Y");
		            contentCtx.put("folder", "/dirtmp");
		            try {
		                Map<String, Object> fileResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
		                if (ServiceUtil.isError(fileResult)) {
		                	//return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
		                	Debug.logError(ServiceUtil.getErrorMessage(fileResult), module);
		                }
		                path = (String) fileResult.get("path");
		                String name = (String) fileResult.get("name");
		                String mimeType = (String) fileResult.get("mimeType");
		                
		                GenericValue fileExportedTmp = delegator.findOne("FileExportedTemp", UtilMisc.toMap("exportName", exportName, "exportTime", exportTime), false);
		                if (fileExportedTmp == null) {
		                	fileExportedTmp = delegator.makeValue("FileExportedTemp");
		                	fileExportedTmp.put("exportName", exportName);
		                    fileExportedTmp.put("exportTime", UtilDateTime.nowTimestamp());
		                    fileExportedTmp.put("exportCond", exportCond);
		                    fileExportedTmp.put("fileName", fileName + ExportExcelEvents.FILE_EXE_TYPE);
		                    fileExportedTmp.put("fileSize", Long.valueOf(baos.size()));
		                    fileExportedTmp.put("fileType", fileType);
		                    fileExportedTmp.put("path", path);
		                    fileExportedTmp.put("name", name);
		                    fileExportedTmp.put("mimeType", mimeType);
		                    fileExportedTmp.put("module", moduleExport);
		                    fileExportedTmp.put("statusId", "COMPLETED");
		                    fileExportedTmp.put("description", descriptionExport);
		                    fileExportedTmp.put("finishTime", UtilDateTime.nowTimestamp());
		                    fileExportedTmp.put("createdByUserLogin", userLogin.get("userLoginId"));
		                    delegator.create(fileExportedTmp);
		                } else {
		                	//fileExportedTmp.put("exportCond", parameters + "@" + listAllConditions.toString());
		                    //fileExportedTmp.put("fileName", fileName + ExportExcelEvents.FILE_EXE_TYPE);
		                    fileExportedTmp.put("fileSize", Long.valueOf(baos.size()));
		                    fileExportedTmp.put("fileType", fileType);
		                    fileExportedTmp.put("path", path);
		                    fileExportedTmp.put("name", name);
		                    fileExportedTmp.put("mimeType", mimeType);
		                    //fileExportedTmp.put("module", moduleExport);
		                    fileExportedTmp.put("statusId", "COMPLETED");
		                    fileExportedTmp.put("finishTime", UtilDateTime.nowTimestamp());
		                    //fileExportedTmp.put("createdByUserLogin", userLogin.get("userLoginId"));
		                    delegator.store(fileExportedTmp);
		                }
		            } catch (GenericServiceException e) {
		            	invokeChangeStatusFileExport(dispatcher, exportName, exportTime, userLogin);
		                Debug.logError(e, module);
		                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonErrorWhenStoreFile", locale));
		            }
				}
				
				successResult.put("resultCode", ExportExcelEvents.RESULT_SUCCESS);
			}
		} catch (Exception e) {
			invokeChangeStatusFileExport(dispatcher, exportName, exportTime, userLogin);
			String errMsg = "Fatal error calling exportExcelBigData service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	private static void invokeChangeStatusFileExport(LocalDispatcher dispatcher, String exportName, Timestamp exportTime, GenericValue userLogin) {
        Map<String, Object> changeStatusCtx = UtilMisc.toMap("exportName", exportName, "exportTime", exportTime, "statusId", "ERROR", "userLogin", userLogin);
        try {
			dispatcher.runAsync("changeStatusFileExported", changeStatusCtx, false);
		} catch (GenericServiceException e) {
			Debug.logError("Error when invoike service change status record log: " + e, module);
		}
	}
	
	public static Map<String, Object> changeStatusFileExported(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String exportName = (String) context.get("exportName");
        Timestamp exportTime = (Timestamp) context.get("exportTime");
        String statusId = (String) context.get("statusId");
        
    	try {
			if (userLogin != null) {
                GenericValue fileExportedTmp = delegator.findOne("FileExportedTemp", UtilMisc.toMap("exportName", exportName, "exportTime", exportTime), false);
                if (fileExportedTmp != null) {
                    fileExportedTmp.put("statusId", statusId);
                    fileExportedTmp.put("finishTime", UtilDateTime.nowTimestamp());
                    delegator.store(fileExportedTmp);
                }
			}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling changeStatusFileExported service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListFileExported(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
			if (userLogin != null) {
				String module = ExportExcelUtil.getParameter(parameters, "module");
				if (UtilValidate.isNotEmpty(module)) listAllConditions.add(EntityCondition.makeCondition("module", module));
				listAllConditions.add(EntityCondition.makeCondition("createdByUserLogin", userLogin.get("userLoginId")));
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-exportTime");
				}
				listIterator = delegator.find("FileExportedTemp", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListFileExported service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> deleteFileExported(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		
		try {
			String exportName = (String) context.get("exportName");
			Timestamp exportTime = (Timestamp) context.get("exportTime");
			
			GenericValue fileInfo = delegator.findOne("FileExportedTemp", UtilMisc.toMap("exportName", exportName, "exportTime", exportTime), false);
			if (UtilValidate.isNotEmpty(fileInfo)) {
				// delete in JCR
				String path = fileInfo.getString("path");
				String isPublic = fileInfo.getString("isPublic") != null ? fileInfo.getString("isPublic") : "Y";
				if (UtilValidate.isNotEmpty(path)) {
					if ("Y".equals(isPublic)) path = path.substring(27, path.length());
					else path = path.replaceFirst("/storage/repository/security/default", "");
					Map<String, Object> contentCtx = UtilMisc.toMap("public", isPublic, "curPath", path, "userLogin", userLogin, "locale", locale);
					Map<String, Object> fileResult = dispatcher.runSync("jackrabbitDeleteNode", contentCtx);
	                if (ServiceUtil.isError(fileResult)) {
	                	Debug.logError(ServiceUtil.getErrorMessage(fileResult), module);
	                	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
	                }
				}
				
				// delete in database
				delegator.removeValue(fileInfo);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling deleteFileExported service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
}
