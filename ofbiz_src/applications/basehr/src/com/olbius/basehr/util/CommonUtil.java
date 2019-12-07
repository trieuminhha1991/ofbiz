package com.olbius.basehr.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class CommonUtil {
	public static List<String> listOperatorPayroll = FastList.newInstance();
	public static final String EMPL_AGREEMENT_TYPE = "EMPLOYMENT_AGREEMENT";
	static{
		listOperatorPayroll.add("AND");
		listOperatorPayroll.add("OR");
		listOperatorPayroll.add("lt=");
		listOperatorPayroll.add("lt");
		listOperatorPayroll.add("gt=");
		listOperatorPayroll.add("gt");
		listOperatorPayroll.add("=");
		listOperatorPayroll.add("!=");
	}
	public static List<GenericValue> getPositionTypeOfDept(String departmentId, Delegator delegator) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, departmentId));
		//FIXME need filter by date
		conditions.add(EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate"));
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		List<GenericValue> positionTypeList = delegator.findList("EmplPosition", EntityCondition.makeCondition(conditions, EntityOperator.AND), 
																UtilMisc.toSet("emplPositionTypeId"), null, options, false);
		
		return positionTypeList;
	}
	
	public static boolean checkPositionTypeInDept(String positionTypeId, String departmentId, Delegator delegator) throws GenericEntityException{
		List<String> positionTypeList = EntityUtil.getFieldListFromEntityList(getPositionTypeOfDept(departmentId, delegator), "emplPositionTypeId", true) ;
		if(UtilValidate.isEmpty(positionTypeList) || !positionTypeList.contains(positionTypeId)){
			return false;
		}
		return true;
	}	
	public static boolean checkWhiteSpace(String str){
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(str);
		boolean found = matcher.find();
		return found;
	}

	public static boolean containsValidCharacter(String s) {
	    return (s == null) ? false : s.matches("[A-Za-z0-9_]+");
	}
	
	public static boolean checkValidStringId(String s){
		String p = "[a-zA-Z0-9_]+";
		return Pattern.matches(p, s);
	}
	
	public static String getPartyTypeOfParty(Delegator delegator, String partyTypeId) throws GenericEntityException {
		if(partyTypeId != null){
			GenericValue partyType = delegator.findOne("PartyType", UtilMisc.toMap("partyTypeId", partyTypeId), false);
			if(partyType == null){
				return null;
			}
			String parentTypeId = partyType.getString("parentTypeId");
			if(parentTypeId == null){
				if(PropertiesUtil.PERSON_TYPE.equals(partyTypeId)){
					return PropertiesUtil.PERSON_TYPE;
				}else{
					return PropertiesUtil.GROUP_TYPE;
				}
				
			}
			return getPartyTypeOfParty(delegator, parentTypeId);
		}else{
			return null;
		}
	}
	
	public static String getCurrentYear(TimeZone timeZone, Locale locale){
		Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
		int year = UtilDateTime.getYear(nowtimestamp, timeZone, locale);
		return String.valueOf(year);
	}
	
	public static String convertTimestampToDate(Timestamp input) throws GenericEntityException{
		String returnDate = null;
		String tmp = input.toString();
		String[] list = tmp.split(" ");
		String tmp1 = list[0];
		String[] list1 = tmp1.split("-");
		returnDate = list1[2] + "/" + list1[1] + "/" + list1[0];
	return returnDate;
	}

	public static String removeWhiteSpace(String emplTimesheetName) {
		String retStr = emplTimesheetName.replaceAll("\\s", "");
		return retStr;
	}
	
	public static String getPostalAddressDetails(Delegator delegator, String contactMechId) throws GenericEntityException{
		StringBuffer postalAddr = new StringBuffer();
		GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
		if(postalAddress != null){
			String address1 = postalAddress.getString("address1");
			if(address1 != null){
				postalAddr.append(address1);
			}
			
			String wardGeoId = postalAddress.getString("wardGeoId");
			if(wardGeoId != null){
				postalAddr.append(", ");
				GenericValue wardGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", wardGeoId), false);
				postalAddr.append(wardGeo.getString("geoName"));
			}
			String districtGeoId = postalAddress.getString("districtGeoId");
			if(districtGeoId != null){
				postalAddr.append(", ");
				GenericValue districtGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId), false);
				postalAddr.append(districtGeo.getString("geoName"));
			}
			String provinceGeoId = postalAddress.getString("stateProvinceGeoId");
			if(provinceGeoId != null){
				postalAddr.append(", ");
				GenericValue provinceGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", provinceGeoId), false);
				postalAddr.append(provinceGeo.getString("geoName"));
			}
			String countryGeoId = postalAddress.getString("countryGeoId");
			if(countryGeoId != null){
				postalAddr.append(", ");
				GenericValue countryGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", countryGeoId), false);
				postalAddr.append(countryGeo.getString("geoName"));
			}
		}
		return postalAddr.toString();
	}

	public static void addGeoAssoc(Delegator delegator, String geoToId,
			List<String> geoList, String geoRootType) throws GenericEntityException {
		List<GenericValue> parentGeoList = delegator.findByAnd("GeoAssoc", UtilMisc.toMap("geoIdTo", geoToId, "geoAssocTypeId", "REGIONS"), null, false);
		for(GenericValue parentGeoAssoc: parentGeoList){
			GenericValue parentGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", parentGeoAssoc.getString("geoId")), false);
			if(!geoRootType.equals(parentGeo.getString("geoTypeId"))){
				addGeoAssoc(delegator, parentGeoAssoc.getString("geoId"), geoList, geoRootType);
			}
			if(!geoList.contains(parentGeo.getString("geoId"))){
				geoList.add(parentGeo.getString("geoId"));	
			}
		}
	}

	public static boolean checkPostalAddressInGeoList(Delegator delegator,
			GenericValue postalAddr, List<String> geoList) throws GenericEntityException {
		String regionIdPostalAddr = postalAddr.getString("regionGeoId");
		String stateProvinceIdPostalAddr = postalAddr.getString("stateProvinceGeoId");
		String districtGeoIdPostalAddr = postalAddr.getString("districtGeoId");
		String wardGeoIdPostalAddr = postalAddr.getString("wardGeoId");
		String countryGeoIdPostalAddr = postalAddr.getString("countryGeoId");
		List<String> regionAssocStateProvince = null;
		List<String> stateProviceAssocDistrict = null;
		List<String> districtAssocWard = null;
		if(stateProvinceIdPostalAddr != null && regionIdPostalAddr == null){
			List<GenericValue> regionAssocStateProvinceGv = delegator.findByAnd("GeoAssoc", UtilMisc.toMap("geoIdTo", stateProvinceIdPostalAddr), null, false);
			regionAssocStateProvince = EntityUtil.getFieldListFromEntityList(regionAssocStateProvinceGv, "geoId", true);
		}
		if(districtGeoIdPostalAddr != null && stateProvinceIdPostalAddr == null){
			List<GenericValue> stateProviceAssocDistrictGv = delegator.findByAnd("GeoAssoc", UtilMisc.toMap("geoIdTo", districtGeoIdPostalAddr), null, false);
			stateProviceAssocDistrict = EntityUtil.getFieldListFromEntityList(stateProviceAssocDistrictGv, "geoId", true);
		}
		if(wardGeoIdPostalAddr != null && districtGeoIdPostalAddr == null){
			List<GenericValue> wardGeoIdGeoPostalAddrGv = delegator.findByAnd("GeoAssoc", UtilMisc.toMap("geoIdTo", wardGeoIdPostalAddr), null, false);
			districtAssocWard = EntityUtil.getFieldListFromEntityList(wardGeoIdGeoPostalAddrGv, "geoId", true);
		}
		for(String geoId: geoList){
			GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
			String geoTypeId = geo.getString("geoTypeId");
			if("COUNTRY".equals(geoTypeId)){
				if(geoId.equals(countryGeoIdPostalAddr)){
					return true;
				}else if(regionAssocStateProvince != null){
					for(String tempRegionId: regionAssocStateProvince){
						if(tempRegionId.equals(geoId)){
							return true;
						}
					}
				}
			}else if("REGION".equals(geoTypeId)){
				if(geoId.equals(regionIdPostalAddr)){
					return true;
				}else if(regionAssocStateProvince != null){
					for(String tempRegionId: regionAssocStateProvince){
						if(tempRegionId.equals(geoId)){
							return true;
						}
					}
				}
			}else if("STATE".equals(geoTypeId) || "PROVINCE".equals(geoTypeId) || "CITY".equals(geoTypeId)){
				if(geoId.equals(stateProvinceIdPostalAddr)){
					return true;
				}else if(stateProviceAssocDistrict != null){
					for(String tempStateId: stateProviceAssocDistrict){
						if(tempStateId.equals(geoId)){
							return true;
						}
					}
				}
			}else if("DISTRICT".equals(geoTypeId)){
				if(geoId.equals(districtGeoIdPostalAddr)){
					return true;
				}else if(districtAssocWard != null){
					for(String tempDistrictId: districtAssocWard){
						if(tempDistrictId.equals(geoId)){
							return true;
						}
					}
				}
			}else if("WARD".equals(geoTypeId)){
				if(geoId.equals(wardGeoIdPostalAddr)){
					return true;
				}
			}
		}
		return false;
	}

	public static List<String> getAllChildOfGeoInGeoBoundary(Delegator delegator,
			String geoId, List<String> geoListBoundary) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("geoId", geoId));
		conditions.add(EntityCondition.makeCondition("geoAssocTypeId", "REGIONS"));
		if(geoListBoundary != null){
			conditions.add(EntityCondition.makeCondition("geoIdTo", EntityJoinOperator.IN, geoListBoundary));	
		}
		List<GenericValue> geoChildList = delegator.findList("GeoAssoc", EntityCondition.makeCondition(conditions), null, null, null, false);
		List<String> retList = FastList.newInstance();
		for(GenericValue geoChild: geoChildList){
			String geoIdChild = geoChild.getString("geoIdTo");
			if(!retList.contains(geoIdChild)){
				retList.add(geoIdChild);
			}
			retList.addAll(getAllChildOfGeoInGeoBoundary(delegator, geoIdChild, geoListBoundary));
		}
		return retList;
	}
	
	public static List<Map<String, Timestamp>> combineListContinuousTime(List<Map<String, Timestamp>> listConvert, long deviation){
		Collections.sort(listConvert, new Comparator<Map<String, Timestamp>>() {
			@Override
			public int compare(Map<String, Timestamp> o1,
					Map<String, Timestamp> o2) {
				Timestamp obj1Time = o1.get("fromDate");
				Timestamp obj2Time = o2.get("fromDate");
				if(obj1Time.before(obj2Time)){
					return -1;
				}else if(obj1Time.after(obj2Time)){
					return 1;
				}
				return 0;
			}
		});
		Iterator<Map<String, Timestamp>> it = listConvert.iterator();
		Timestamp fromDate = null, thruDate = null;
		List<Map<String, Timestamp>> listRet = FastList.newInstance();
		while(it.hasNext()){
			Map<String, Timestamp> tmpMap = it.next();
			if(fromDate == null){
				fromDate = tmpMap.get("fromDate");
				thruDate = tmpMap.get("thruDate");
			}else{
				Timestamp tmpFromDate = tmpMap.get("fromDate");
				Timestamp tmpThruDate = tmpMap.get("thruDate");
				Long tmpFromDateLong = tmpFromDate.getTime();
				if(thruDate != null){
					Long thruDateLong = thruDate.getTime();
					if(Math.abs((tmpFromDateLong - thruDateLong)) < deviation){
						thruDate = tmpThruDate;
					}else{
						Map<String, Timestamp> newMap = FastMap.newInstance();
						newMap.put("fromDate", fromDate);
						newMap.put("thruDate", thruDate);
						listRet.add(newMap);
						fromDate = tmpFromDate;
						thruDate = tmpThruDate;
					}
				}
			}
		}
		Map<String, Timestamp> newMap = FastMap.newInstance();
		newMap.put("fromDate", fromDate);
		newMap.put("thruDate", thruDate);
		listRet.add(newMap);
		return listRet;
	}
	public static String getCookie(HttpServletRequest request, String key){
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(key)) {
				return cookie.getValue();
			}
		}
        }
		return "";
	}
	public static void setCookie(HttpServletResponse response, String key, String value, int age){
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(age);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static String getRegionOfStateProvince(Delegator delegator, String stateProvinceGeoId, String geoTypeId, String geoAssocTypeId) throws GenericEntityException {
		List<GenericValue> geoAssocList = delegator.findByAnd("GeoAssocAndGeoFrom", 
				UtilMisc.toMap("geoTypeId", geoTypeId, "geoIdTo", stateProvinceGeoId, "geoAssocTypeId", geoAssocTypeId), null, false);
		if(UtilValidate.isNotEmpty(geoAssocList)){
			return geoAssocList.get(0).getString("geoId");
		}
		return null;
	}

	public static List<EntityCondition> filterByEmplNameConds(String partyNameParam) {
		//partyNameParam = partyNameParam.replaceAll("\\s", "");
		List<EntityCondition> tempConds = FastList.newInstance();
		tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullNameFirstNameFirst"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam.toUpperCase() + "%")));
		tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullNameLastNameFirst"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
		tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastNameFirstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
		tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstNameLastName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
		return tempConds;
	}

	public static List<String> convertListValueMemberToListDesMember(
			Delegator delegator, String entityName, List<String> valueMemberList, String valueMember,
			String displayMember) throws GenericEntityException {
		List<String> retList = FastList.newInstance();
		for(String keyValue: valueMemberList){
			GenericValue gv = delegator.findOne(entityName, UtilMisc.toMap(valueMember, keyValue), false);
			if(gv != null && gv.getString(displayMember) != null){
				retList.add(gv.getString(displayMember));
			}
		}
		return retList;
	}
	
	public static String convertUrl(String scheme, String serverName, int port, String path){
		String url = scheme + "://" + serverName + ":" + port + path;
		return url;
	}
	
	@SuppressWarnings("unchecked")
	public static String getErrorMessageFromService(Map<String, Object> resultService) {
		String errMes = "";
		if(resultService.get(ModelService.ERROR_MESSAGE_LIST) != null){
			List<String> errList = (List<String>)resultService.get(ModelService.ERROR_MESSAGE_LIST);
			errMes = errList.get(0);
		}else{
			errMes = (String)resultService.get(ModelService.ERROR_MESSAGE);
		}
		return errMes;
	}
	
	public static Map<String, Object> sendNotify(LocalDispatcher dispatcher, Locale locale, String partyId, 
			GenericValue userLogin, String header, String action, String targetLink) throws GenericServiceException{
		List<String> partyIds = FastList.newInstance();
		partyIds.add(partyId);
		return sendNotify(dispatcher, locale, partyIds, null, userLogin, header, action, targetLink, null, null, null, null, null);
	}
	
	public static Map<String, Object> sendNotifyByRoles(LocalDispatcher dispatcher, Locale locale, String roleTypeId, 
			GenericValue userLogin, String header, String action, String targetLink) throws GenericServiceException{
		return sendNotify(dispatcher, locale, null, UtilMisc.toList(roleTypeId), userLogin, header, action, targetLink, null, null, null, null, null);
	}
	public static Map<String, Object> sendNotifyByRoles(LocalDispatcher dispatcher, Locale locale, List<String> roleTypes, 
			GenericValue userLogin, String header, String action, String targetLink) throws GenericServiceException{
		return sendNotify(dispatcher, locale, null, roleTypes, userLogin, header, action, targetLink, null, null, null, null, null);
	}
	
	public static Map<String, Object> sendNotify(LocalDispatcher dispatcher, Locale locale, List<String> partyIds, 
			GenericValue userLogin, String header, String action, String targetLink) throws GenericServiceException{
		return sendNotify(dispatcher, locale, partyIds, null, userLogin, header, action, targetLink, null, null, null, null, null);
	}
	
	public static Map<String, Object> sendNotify(LocalDispatcher dispatcher, Locale locale, List<String> partyIds, List<String> roleTypeIds, GenericValue userLogin, 
			String header, String action, String targetLink, String ntfType, String state, String sendToGroup, String sendrecursive, 
			Timestamp dateTime) throws GenericServiceException{
		if (UtilValidate.isEmpty(partyIds) && UtilValidate.isEmpty(roleTypeIds)) {
			return ServiceUtil.returnSuccess();	
		}
 		if(ntfType == null){
 			ntfType = "ONE";
 		}
 		if(state == null){
 			state = "open";
 		}
		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", 
 				UtilMisc.<String, Object>toMap("partiesList", partyIds, 
 						"header", header, 
 						"state", state, 
 						"action", action, 
 						"targetLink", targetLink, 
 						"dateTime", dateTime, 
 						"ntfType", ntfType, 
 						"roleList", roleTypeIds, 
 						"sendToGroup", sendToGroup, 
 						"sendrecursive", sendrecursive, 
 						"userLogin", userLogin)
 				);
 		if (ServiceUtil.isError(tmpResult)) {
 			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
 		}
 		return ServiceUtil.returnSuccess();
	}

	public static String getMonthYearPeriodNameByCustomTimePeriod(
			Delegator delegator, String customTimePeriodId) throws GenericEntityException {
		GenericValue customPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		String parentPeriodId = customPeriod.getString("parentPeriodId");
		GenericValue parentPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", parentPeriodId), false);
		String retString = customPeriod.getString("periodName");
		if(parentPeriod != null){
			retString += "-" + parentPeriod.getString("periodName");
		}
		return retString;
	}
	
	public static String getRomanNumerals(int numberDecimal) {
	    LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
	    roman_numerals.put("M", 1000);
	    roman_numerals.put("CM", 900);
	    roman_numerals.put("D", 500);
	    roman_numerals.put("CD", 400);
	    roman_numerals.put("C", 100);
	    roman_numerals.put("XC", 90);
	    roman_numerals.put("L", 50);
	    roman_numerals.put("XL", 40);
	    roman_numerals.put("X", 10);
	    roman_numerals.put("IX", 9);
	    roman_numerals.put("V", 5);
	    roman_numerals.put("IV", 4);
	    roman_numerals.put("I", 1);
	    String res = "";
	    for(Map.Entry<String, Integer> entry : roman_numerals.entrySet()){
	      int matches = numberDecimal/entry.getValue();
	      res += repeat(entry.getKey(), matches);
	      numberDecimal = numberDecimal % entry.getValue();
	    }
	    return res;
	  }
	  public static String repeat(String s, int n) {
	    if(s == null) {
	        return null;
	    }
	    final StringBuilder sb = new StringBuilder();
	    for(int i = 0; i < n; i++) {
	        sb.append(s);
	    }
	    return sb.toString();
	  }

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getParameterMapWithFileUploaded(HttpServletRequest request) throws FileUploadException, IOException {
		Map<String, Object> retMap = FastMap.newInstance();
		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		List<Map<String, Object>> listFileUploaded = FastList.newInstance();
		for (FileItem item : items) {
            if (item.isFormField()) {
                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                String fieldName = item.getFieldName();
                String fieldValue = IOUtils.toString(item.getInputStream(), "UTF-8");
                retMap.put(fieldName, fieldValue);
            } else {
                // Process form file field (input type="file").
            	Map<String, Object> tempMap = FastMap.newInstance();
                String fileName = FilenameUtils.getName(item.getName());
                InputStream fileContent = item.getInputStream();
                ByteBuffer bf = ByteBuffer.wrap(IOUtils.toByteArray(fileContent));
                tempMap.put("uploadedFile", bf);
                tempMap.put("_uploadedFile_fileName", fileName);
                tempMap.put("_uploadedFile_contentType", item.getContentType());
                listFileUploaded.add(tempMap);
            }
        }
		retMap.put("listFileUploaded", listFileUploaded);
		return retMap;
	}
	
	public static List<Map<String, Object>> getListPartyRelByParent(Delegator delegator, String partyIdFrom) throws GenericEntityException {
		List<Map<String, Object>> listReturn = FastList.newInstance();
		if( partyIdFrom != null){
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
			EntityCondition commonConds = EntityCondition.makeCondition(conditions);
				List<GenericValue> childList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND,
																						EntityCondition.makeCondition("partyIdFrom", partyIdFrom)), 
																						null, UtilMisc.toList("partyIdTo"), null, false);
				for(GenericValue child: childList){
					Map<String, Object> tempMap = FastMap.newInstance();
					String partyIdTo = child.getString("partyIdTo");
					tempMap.put("id", partyIdTo);
					tempMap.put("value", partyIdTo);
					tempMap.put("label", PartyHelper.getPartyName(delegator, partyIdTo, false));
					//check if partyIdTo have children
					List<GenericValue> tempChildList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND,
							EntityCondition.makeCondition("partyIdFrom", partyIdTo)), 
							null, UtilMisc.toList("partyIdTo"), null, false);
					if(UtilValidate.isNotEmpty(tempChildList)){
						Map<String, Object> childs = FastMap.newInstance();
						childs.put("label", "Loading...");
						childs.put("value", "getListPartyRelByParent");
						List<Map<String, Object>> listChilds = FastList.newInstance();
						listChilds.add(childs);
						tempMap.put("items", listChilds);
					}
					listReturn.add(tempMap);
				}
			
		}
		return listReturn;
	}
	
	public static List<String> getAllPartyByParent(Delegator delegator, String partyIdFrom){
		List<String> listResult = FastList.newInstance();
		listResult.add(partyIdFrom);
		try {
			List<Map<String, Object>> partyList = CommonUtil.getListPartyRelByParent(delegator, partyIdFrom);
			if(UtilValidate.isNotEmpty(partyList)){
				for(Map<String, Object> party : partyList){
					if(UtilValidate.isNotEmpty(party)){
						if(party.containsKey("value")){
							listResult.add(party.get("value").toString());
						}
						if(party.containsKey("items")){
							List<String> item = CommonUtil.getAllPartyByParent(delegator, party.get("value").toString());
							for(String dep : item){
								listResult.add(dep);
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		//remove duplicate element
		Set<String> hs = new HashSet<>();
		hs.addAll(listResult);
		listResult.clear();
		listResult.addAll(hs);
		
		return listResult;
	}
	
	public static String cleanJoinStringValue(String value, String separator) {
		if(value == null){
			return null;
		}
		String[] valueArr = value.split(separator);
		Set<String> setValue = FastSet.newInstance();
		for(String temp: valueArr){
			String tempStr = temp.trim();
			setValue.add(tempStr);
		}
 		return StringUtils.join(setValue, ", ");
	}
	public static BigDecimal roundingNumber(BigDecimal amount,
			Integer roundingNumber, boolean isAfterDecimal){
		if(roundingNumber == null){
			roundingNumber = 0;
		}
		if(isAfterDecimal){
			return amount.setScale(roundingNumber, RoundingMode.HALF_UP);
		}
		BigDecimal ten = new BigDecimal(10);
		ten = ten.pow(roundingNumber);
		BigDecimal tempBig = amount.divide(ten, 0, RoundingMode.HALF_UP);
		amount = tempBig.multiply(ten);
		return amount;
	}

	public static boolean isImageFile(String _uploadedFile_contentType) {
		if(_uploadedFile_contentType == null){
			return false;
		}
		String type = _uploadedFile_contentType.split("/")[0];
		if(type!= null && type.equals("image")){
			return true;
		}
		return false;
	}
}
