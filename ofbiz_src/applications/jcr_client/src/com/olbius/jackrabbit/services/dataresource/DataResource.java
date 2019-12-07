package com.olbius.jackrabbit.services.dataresource;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

public class DataResource {
	public final static String module = DataResource.class.getName();

	public static Map<String, Object> jackrabbitCreateDataResource(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		LocalDispatcher dispatcher = ctx.getDispatcher();

		String dataResourceId = (String) context.get("dataResourceId");
		if(dataResourceId != null) {
			GenericValue dataResource = null;
			Delegator delegator = ctx.getDelegator();
			try {
				dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
			} catch (GenericEntityException e) {
				throw new GenericServiceException(e.getMessage());
			}

			if(dataResource != null) {
				throw new GenericServiceException(dataResourceId + " is exist");
			}
		}
		Map<String, Object> tmp = UtilMisc.toMap("userLogin", context.get("userLogin"), "_uploadedFile_fileName",
				context.get("_uploadedFile_fileName"), "_uploadedFile_contentType", context.get("_uploadedFile_contentType"), "uploadedFile",
				context.get("uploadedFile"), "folder", context.get("folder"), "public", context.get("isPublic"));
		try {
			result = dispatcher.runSync("jackrabbitUploadFile", tmp);
		} catch (GenericServiceException e) {
			throw new GenericServiceException(e);
		}

		tmp = UtilMisc.toMap(context);
		tmp.put("objectInfo", result.get("path"));
		tmp.put("dataResourceName", result.get("name"));
		tmp.put("mimeTypeId", result.get("mimeType"));
		String localeString = (String) context.get("localeString");
		if(localeString == null) {
			Locale locale = (Locale) context.get("locale");
			localeString = locale.getLanguage();
		}

		tmp.put("localeString", localeString);
		String isPublic = (String) context.get("isPublic");
		if(isPublic == null) {
			tmp.put("isPublic", "Y");
		}
		try {
			result = dispatcher.runSync("createDataResource", tmp);
		} catch (GenericServiceException e) {
			throw new GenericServiceException(e);
		}

		return result;
	}

	public static Map<String, Object> jackrabbitUpdateDataResource(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String dataResourceId = (String) context.get("dataResourceId");

		GenericValue dataResource = null;
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();

		Map<String, Object> tmp = null;

		try {
			dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e.getMessage());
		}

		if (dataResource != null) {
			tmp = UtilMisc.toMap("userLogin", context.get("userLogin"), "_uploadedFile_fileName",
					context.get("_uploadedFile_fileName"), "_uploadedFile_contentType", context.get("_uploadedFile_contentType"), "uploadedFile",
					context.get("uploadedFile"), "folder", context.get("folder"), "public", dataResource.getString("isPublic"));
			try {
				result = dispatcher.runSync("jackrabbitUploadFile", tmp);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}

			tmp = UtilMisc.toMap(context);
			tmp.put("objectInfo", result.get("path"));
			tmp.put("dataResourceName", result.get("name"));
			tmp.put("mimeTypeId", result.get("mimeType"));

			try {
				result = dispatcher.runSync("updateDataResource", tmp);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException(dataResourceId + " not found");
		}

		return result;
	}

	public static Map<String, Object> jackrabbitRemoveDataResource(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String dataResourceId = (String) context.get("dataResourceId");

		GenericValue dataResource = null;
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();

		Map<String, Object> tmp = UtilMisc.toMap(context);

		try {
			dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e.getMessage());
		}

		if (dataResource != null) {
			List<GenericValue> dataResourceRole = null;
			try {
				dataResourceRole = delegator.findList("DataResourceRole",
						EntityCondition.makeCondition("dataResourceId", EntityOperator.EQUALS, dataResourceId), null,
						UtilMisc.toList("dataResourceId"), null, false);
			} catch (GenericEntityException e) {
				throw new GenericServiceException(e.getMessage());
			}

			for(GenericValue role : dataResourceRole) {
				try {
					delegator.removeValue(role);
				} catch (GenericEntityException e) {
					throw new GenericServiceException(e.getMessage());
				}
			}

			tmp = UtilMisc.toMap(context);
			tmp.put("statusId", dataResource.getString("statusId"));

			String url = dataResource.getString("objectInfo");

			try {
				result = dispatcher.runSync("removeDataResource", tmp);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}
			tmp = UtilMisc.toMap("userLogin", context.get("userLogin"), "nodePath", url, "public", dataResource.getString("isPublic"));
			try {
				dispatcher.runSync("jackrabbitDeleteNode", tmp);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException(dataResourceId + " not found");
		}

		return result;
	}
}
