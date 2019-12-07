package com.olbius.util;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

public class CommonUtil {
	public static List<String> listOperatorPayroll = FastList.newInstance();
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
	/*public static String joinListStringByEntityField(Delegator delegator, List<String> idList, String primaryKey, String entityName, String fieldJoin) throws GenericEntityException{
		List<String> list = FastList.newInstance();
		for(String id: idList){
			GenericValue gv = delegator.findOne(entityName, UtilMisc.toMap(primaryKey, id), false);
			String fieldValue = gv.getString(fieldJoin);
			list.add(fieldValue);
		}
		return StringUtils.join(list, ",");
	}*/
	
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
			if("REGION".equals(geoTypeId)){
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
}
