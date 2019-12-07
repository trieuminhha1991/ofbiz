package com.olbius.docmanager.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.jcr.security.Privilege;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.docmanager.helper.DocumentHelper;
import com.olbius.jackrabbit.core.Constant;
import com.olbius.jackrabbit.security.OlbiusUserManager;

public class DocumentServices {
	
	public static final String module = DocumentServices.class.getName();
	public static final String resource = "hrolbiusUiLabels";
	public static final String resourceNoti = "NotificationUiLabels";
	
	public static Map<String, Object> hrmUploadFile(DispatchContext dpctx, Map<String, Object> context){
		
		LocalDispatcher localDis = dpctx.getDispatcher();
		Delegator delegator = dpctx.getDelegator();
		
		Locale locale = (Locale)context.get("locale");
		
		Map<String, Object> jrbUploadFileCtx = context;
		String dataCategoryId = (String)jrbUploadFileCtx.remove("dataCategoryId");
		jrbUploadFileCtx.put("public", "N");
		Map<String, Object> jrbUploadFileResult = null;
		try {
			//Upload file to JCR Repos
			jrbUploadFileResult =  localDis.runSync("jackrabbitUploadFile", jrbUploadFileCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "uploadError", new Object[]{e.getMessage()}, locale));
		}
		
		if(ServiceUtil.isSuccess(jrbUploadFileResult)){
			//Create a Data Resource
			Map<String, Object> dataResouceCtx = FastMap.newInstance();
			dataResouceCtx.put("dataCategoryId", dataCategoryId);
			dataResouceCtx.put("dataResourceName",jrbUploadFileResult.get("name"));
			dataResouceCtx.put("dataResourceTypeId","OFBIZ_FILE");
			try {
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				dataResouceCtx.put("userLogin", userLogin);
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			dataResouceCtx.put("objectInfo",jrbUploadFileResult.get("path"));
			try {
				localDis.runSync("createDataResource", dataResouceCtx);
			} catch (GenericServiceException e) {
				Debug.log(e.getMessage(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "uploadError", new Object[]{e.getMessage()}, locale));
			}
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "uploadSuccessfully", locale));
	}
	
	public static Map<String, Object> changeMode(DispatchContext dpctx, Map<String, Object> context){
		
		LocalDispatcher dispatcher = dpctx.getDispatcher();
		Delegator delegator = dpctx.getDelegator();
		
		
		//Get parameters
		String path = (String) context.get("path");
		String authId = (String) context.get("authId");
		String tenant = delegator.getDelegatorTenantId();
		Locale locale = (Locale)context.get("locale");
		String privilege = (String)context.get("privilege");
		
		if (tenant == null) {
			tenant = Constant.getTenantDefault();
		}
		
		//Add / path
		if (path.startsWith("/")) {
			path = "/" + tenant + path;
		} else {
			path = "/" + tenant + "/" + path;
		}
		authId = tenant + OlbiusUserManager.PARTY + authId;

		boolean allow = Boolean.valueOf((String) context.get("allow"));
		
		//Init privileges
		PrivilegeEnum priEnum = PrivilegeEnum.valueOf(privilege);
		List<String> privileges = new ArrayList<String>();
		switch (priEnum) {
		case JCR_READ:
			privileges.add(Privilege.JCR_READ);
			break;
		case JCR_WRITE:
			privileges.add(Privilege.JCR_WRITE);
			break;
		case JCR_ALL:
			privileges.add(Privilege.JCR_ALL);
			break;
		default:
			throw new IllegalArgumentException();
		}
		
		Map<String, Object> input = UtilMisc.toMap("userLogin", context.get("userLogin"), "path", path, "privileges", privileges, "allow", allow,
				"authId", authId, "public", "N");

		try {
			dispatcher.runSync("jackrabbitAddEntry", input);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}

		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "changeModeSuccessfully", locale));
	}
	
public static Map<String, Object> getDocumentInfo(DispatchContext dpctx, Map<String, Object> context){
		
		LocalDispatcher dispatcher = dpctx.getDispatcher();
		Delegator delegator = dpctx.getDelegator();
		//Get parameters
		String dataResourceId = (String) context.get("dataResourceId");
		
		String path="";
		
		//get tenant
		String tenant = delegator.getDelegatorTenantId();
		if (tenant == null) {
			tenant = Constant.getTenantDefault();
		}
		
		try {
			path = DocumentHelper.getPath(dataResourceId, false, dpctx);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getMessage(), module);
		}
		Map<String, Object> getNodePropCtx = UtilMisc.toMap("userLogin", context.get("userLogin"), "public", "N", "path", path, "namespace", "olbiusDms");
		try {
			getNodePropCtx = dispatcher.runSync("jackrabbitGetNodeProperties", getNodePropCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}
		
		Map<String, Object> getEntryCtx = UtilMisc.toMap("userLogin", context.get("userLogin"), "path", path, "public", "N");
		Map<String, Object> entriesMap = FastMap.newInstance();
		try {
			entriesMap = dispatcher.runSync("jackrabbitGetEntry", getEntryCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}
		
		
		List<Map<String, Object>> entriesList = FastList.newInstance();
		for (Object x : (List<?>)entriesMap.get("entries")) {
			Map<String, Object> templEntry = FastMap.newInstance();
			
			JackrabbitAccessControlEntry entry = (JackrabbitAccessControlEntry) x;
			String allow = "";

			if (entry.isAllow()) {
				allow = "Allow";
			} else {
				allow = "Deny";
			}
			
			templEntry.put("allow", allow);
			templEntry.put("user", entry.getPrincipal().getName().replaceFirst(tenant + OlbiusUserManager.USER, "")
					.replaceFirst(tenant + OlbiusUserManager.PARTY, ""));
			templEntry.put("privileges", Arrays.toString(entry.getPrivileges()).replaceAll("jcr:", "").replaceAll("rep:", "+"));
			entriesList.add(templEntry);
		}
		Map<?, ?> tmp = (Map<?, ?>) getNodePropCtx.get("properties");

		Map<String, String> properties = new TreeMap<String, String>();

		Map<?, ?> fileProperties = (Map<?, ?>) tmp.get("fileProperties");
		Map<?, ?> contentProperties = (Map<?, ?>) tmp.get("contentProperties");

		for (Object x : contentProperties.keySet()) {
			properties
					.put(((String) x).replaceAll("olbiusDms:", "").replaceAll("jcr:", "").replaceAll("rep:", ""), (String) contentProperties.get(x));
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("fileProperties", fileProperties);
		result.put("contentProperties", properties);
		result.put("entries", entriesList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

		return result;
	}
	enum PrivilegeEnum{
		JCR_READ, JCR_WRITE, JCR_ALL;
	}
	
	public static Map<String, Object> removeDocument(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		
		Map<String, Object> result = FastMap.newInstance();

		String dataResourceId = (String) context.get("dataResourceId");

		String path="";
		try {
			path = DocumentHelper.getPath(dataResourceId, false , ctx);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}

		Map<String, Object> input = UtilMisc.toMap("userLogin", context.get("userLogin"), "nodePath", path, "public", "N");
		
		try {
			dispatcher.runSync("jackrabbitDeleteNode", input);
		} catch (GenericServiceException e) {
			throw new GenericServiceException(e);
		}
		
		Map<String, Object> delDataResourceCtx = UtilMisc.toMap("userLogin", context.get("userLogin"), "dataResourceId", dataResourceId);
		
		try {
			delegator.removeByAnd("DataResourceRole", UtilMisc.toMap("dataResourceId", dataResourceId));
			dispatcher.runSync("removeDataResource", delDataResourceCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
