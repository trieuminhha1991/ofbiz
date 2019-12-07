package com.olbius.administration;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.administration.util.SettingUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.security.util.SecurityUtil;
import com.olbius.common.util.BaseUtil;

import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AdministrationServices {
	public static final String module = AdministrationServices.class.getName();

	public static Map<String, Object> createApplicationSetting(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Security security = ctx.getSecurity();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			if (security.hasEntityPermission("APPLICATIONSETTING", "_CREATE", userLogin)) {
				GenericValue applicationSetting = delegator.makeValidValue("ApplicationSetting", context);
				applicationSetting.create();
				result.putAll(applicationSetting.getPrimaryKey());
			} else {
				Locale locale = (Locale) context.get("locale");
				throw new Exception(
						UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYouHavenotViewPermission", locale));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateApplicationSetting(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Security security = ctx.getSecurity();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			if (security.hasEntityPermission("APPLICATIONSETTING", "_UPDATE", userLogin)) {
				GenericValue applicationSetting = delegator.makeValidValue("ApplicationSetting", context);
				applicationSetting.store();
				result.putAll(applicationSetting.getPrimaryKey());
			} else {
				Locale locale = (Locale) context.get("locale");
				throw new Exception(
						UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYouHavenotViewPermission", locale));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createApplicationSettings(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			if (security.hasEntityPermission("APPLICATIONSETTING", "_CREATE", userLogin)) {
				Object settings = context.get("settings");
				JSONArray settingsArr = JSONArray.fromObject(settings);
				for (Object o : settingsArr) {
					JSONObject setting = JSONObject.fromObject(o);
					result.clear();
					result.putAll(setting);
					result.put("userLogin", userLogin);
					dispatcher.runSync("createApplicationSetting", result);
				}
				result.clear();
			} else {
				Locale locale = (Locale) context.get("locale");
				throw new Exception(
						UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYouHavenotViewPermission", locale));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateApplicationSettings(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			if (security.hasEntityPermission("APPLICATIONSETTING", "_UPDATE", userLogin)) {
				Object settings = context.get("settings");
				JSONArray settingsArr = JSONArray.fromObject(settings);
				for (Object o : settingsArr) {
					JSONObject setting = JSONObject.fromObject(o);
					result.clear();
					result.putAll(setting);
					result.put("userLogin", userLogin);
					dispatcher.runSync("updateApplicationSetting", result);
				}
				result.clear();
			} else {
				Locale locale = (Locale) context.get("locale");
				throw new Exception(
						UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYouHavenotViewPermission", locale));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> getApplicationSetting(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue applicationSetting = SettingUtil.getValueApplicationSetting(delegator,
					context.get("applicationSettingId"), context.get("partyId"),
					context.get("applicationSettingTypeId"), context.get("applicationSettingEnumId"));
			if (UtilValidate.isNotEmpty(applicationSetting)) {
				result.put("applicationSetting", applicationSetting);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> getValueApplicationSetting(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			result = getApplicationSetting(ctx, context);
			if (UtilValidate.isNotEmpty(result)) {
				GenericValue applicationSetting = (GenericValue) result.get("applicationSetting");
				if (UtilValidate.isNotEmpty(applicationSetting)) {
					result.clear();
					result.put("value", applicationSetting.get("value"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateVisualThemeResources(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");

			if (security.hasEntityPermission("THEMESSETTING", "_UPDATE", userLogin)) {
				Object resources = context.get("resources");
				JSONArray resourcesArr = JSONArray.fromObject(resources);
				for (Object o : resourcesArr) {
					JSONObject setting = JSONObject.fromObject(o);
					result.clear();
					result.putAll(setting);
					result.put("userLogin", userLogin);
					dispatcher.runSync("updateVisualThemeResource", result);
				}
				result.clear();
			} else {
				Locale locale = (Locale) context.get("locale");
				throw new Exception(
						UtilProperties.getMessage("BaseAccountingUiLabels", "BACCYouHavenotViewPermission", locale));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> getBrandLogo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			result.put("originalUrl", new BrandLogo(delegator, context.get("partyId")).getOriginalUrl());
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> getBrandLogoBase64(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			result.put("base64", new BrandLogo(delegator, context.get("partyId")).getBase64());
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> storeBrandLogo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			Object partyId = context.get("partyId");
			if (UtilValidate.isEmpty(partyId)) {
				partyId = PartyUtil.getRootOrganization(delegator);
			}
			BrandLogo brandLogo = new BrandLogo(delegator, partyId);
			if (UtilValidate.isNotEmpty(brandLogo)) {
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				ByteBuffer byteBuffer = (ByteBuffer) context.get("logoImg");
				String base64 = new String(Base64.encodeBase64(byteBuffer.array()));
				result.put("uploadedFile", byteBuffer);
				result.put("_uploadedFile_fileName", "img");
				result.put("userLogin", userLogin);
				result = dispatcher.runSync("jackrabbitUploadFile", result);
				if (ServiceUtil.isSuccess(result)) {
					String originalUrl = (String) result.get("path");
					brandLogo.setOriginalUrl(originalUrl);
					brandLogo.setBase64(base64);
					brandLogo.store();
				}
			}
			result.clear();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSystemConfig(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		// Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			Security security = ctx.getSecurity();
			if (security.hasEntityPermission("SYSTEMCONFIG", "_UPDATE", userLogin)) {
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("systemConfigId");
				}
				listAllConditions.add(EntityCondition.makeCondition("systemConfigTypeId", "SYSTEM"));
				listIterator = delegator.find("SystemConfig", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSystemConfig service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> updateSystemConfig(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			Security security = ctx.getSecurity();
			if (!security.hasEntityPermission("SYSTEMCONFIG", "_UPDATE", userLogin)) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
			}
			String systemConfigId = (String) context.get("systemConfigId");
			String systemValue = (String) context.get("systemValue");
			if (UtilValidate.isNotEmpty(systemConfigId)) {
				GenericValue systemConfig = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", systemConfigId), false);
				if (systemConfig != null && "Y".equals(systemConfig.getString("manualSetting"))) {
					systemConfig.set("systemValue", systemValue);
					delegator.store(systemConfig);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateSystemConfig service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	public static Map<String, Object> updateSystemConfigWB(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			Security security = ctx.getSecurity();
			if (!security.hasEntityPermission("SYSTEMCONFIG", "_UPDATE", userLogin)) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
			}
			
			String weightBarcodeType = (String) context.get("weightBarcodeType");
			String prefixWeightBarcode = (String) context.get("prefixWeightBarcode");
			String patternWeightBarcode = (String) context.get("patternWeightBarcode");
			Integer decimalsInWeight = (Integer) context.get("decimalsInWeight");
			if (UtilValidate.isNotEmpty(weightBarcodeType)) {
				Map<String, Object> updateWBResult = dispatcher.runSync("updateSystemConfig", UtilMisc.toMap(
						"systemConfigId", "WeightBarcodeType", "systemValue", weightBarcodeType, "userLogin", userLogin, "locale", locale));
				if (ServiceUtil.isError(updateWBResult)) {
					return updateWBResult;
				}
			}
			if (UtilValidate.isNotEmpty(prefixWeightBarcode)) {
				Map<String, Object> updateWBResult = dispatcher.runSync("updateSystemConfig", UtilMisc.toMap(
						"systemConfigId", "PrefixWeightBarcode", "systemValue", prefixWeightBarcode, "userLogin", userLogin, "locale", locale));
				if (ServiceUtil.isError(updateWBResult)) {
					return updateWBResult;
				}
			}
			if (UtilValidate.isNotEmpty(patternWeightBarcode)) {
				Map<String, Object> updateWBResult = dispatcher.runSync("updateSystemConfig", UtilMisc.toMap(
						"systemConfigId", "PatternWeightBarcode", "systemValue", patternWeightBarcode, "userLogin", userLogin, "locale", locale));
				if (ServiceUtil.isError(updateWBResult)) {
					return updateWBResult;
				}
			}
			if (UtilValidate.isNotEmpty(decimalsInWeight)) {
				Map<String, Object> updateWBResult = dispatcher.runSync("updateSystemConfig", UtilMisc.toMap(
						"systemConfigId", "DecimalsInWeight", "systemValue", decimalsInWeight.toString(), "userLogin", userLogin, "locale", locale));
				if (ServiceUtil.isError(updateWBResult)) {
					return updateWBResult;
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateSystemConfigWB service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	public static Map<String, Object> changeDocumentLogo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			Security security = ctx.getSecurity();
			if (!security.hasEntityPermission("LOGOCONFIG", "_UPDATE", userLogin)) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
			}
			ByteBuffer uploadedFileDocLogo = (ByteBuffer) context.get("uploadedFile");
			String contentType = (String) context.get("_uploadedFile_contentType");
			if (UtilValidate.isNotEmpty(uploadedFileDocLogo)) {
				// encode image to base64
				String resourceValue = "data:" + contentType + ";base64," + Base64.encodeBase64String(uploadedFileDocLogo.array());
				
				// store value
				String visualThemeId = BaseUtil.getCurrentVisualThemeBackOffice(delegator, userLogin);
				GenericValue logoDocument = EntityUtil.getFirst(delegator.findByAnd("ImageDataTextResource", UtilMisc.toMap("visualThemeId", visualThemeId, "resourceTypeEnumId", "VT_LOGO_IMAGE_BASE64"), null, false));
				if (logoDocument != null) {
					logoDocument.set("resourceValue", resourceValue);
					delegator.store(logoDocument);
				} else {
					logoDocument = delegator.makeValue("ImageDataTextResource");
					logoDocument.set("visualThemeId", visualThemeId);
					logoDocument.set("resourceTypeEnumId", "VT_LOGO_IMAGE_BASE64");
					logoDocument.set("sequenceId", "01");
					logoDocument.set("resourceValue", resourceValue);
					delegator.create(logoDocument);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling changeDocumentLogo service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
}
