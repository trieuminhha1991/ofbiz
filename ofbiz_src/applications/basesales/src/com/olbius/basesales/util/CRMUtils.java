package com.olbius.basesales.util;

import java.math.BigDecimal;
import java.util.*;
import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.olbius.common.util.EntityMiscUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class CRMUtils {
	private static String default_string = "47b56994cbc2b6d10aa1be30f70165adb305a41a";

	public static List<GenericValue> doFilterGenericValue(List<GenericValue> listData,
			List<EntityCondition> listAllConditions) {
		List<GenericValue> listReturn = FastList.newInstance();
		List<Map<String, Object>> listConditions = makeListConditions(listAllConditions);
		if (UtilValidate.isNotEmpty(listConditions)) {
			for (GenericValue x : listData) {
				boolean pass = true;
				for (Map<String, Object> m : listConditions) {
					String fieldName = (String) m.get("fieldName");
					String operator = (String) m.get("operator");
					String value = (String) m.get("value");
					Object fieldValue = x.get(fieldName);
					if (UtilValidate.isEmpty(fieldValue) || UtilValidate.isEmpty(operator)
							|| UtilValidate.isEmpty(value.trim())) {
						pass = false;
						break;
					}
					switch (operator) {
					case "LIKE":
						if (!fieldValue.toString().contains(value)) {
							pass = false;
						}
						break;
					case "NOT_LIKE":
						if (fieldValue.toString().contains(value)) {
							pass = false;
						}
						break;
					case "EQUAL":
						if (!fieldValue.toString().equals(value)) {
							pass = false;
						}
						break;
					case "NOT_EQUAL":
						if (fieldValue.toString().equals(value)) {
							pass = false;
						}
						break;
					case "=":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) != 0) {
								pass = false;
							}
						}
						if (fieldValue instanceof Integer) {
							int fieldValueInt = (int) fieldValue;
							int valueInt = Integer.valueOf(value);
							if (fieldValueInt != valueInt) {
								pass = false;
							}
						}
						break;
					case ">=":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) < 0) {
								pass = false;
								break;
							}
						}
						if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value);
							if (fieldValueTs.before(valueTs)) {
								pass = false;
							}
						}
						break;
					case "<=":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) > 0) {
								pass = false;
								break;
							}
						}
						if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value);
							if (fieldValueTs.after(valueTs)) {
								pass = false;
							}
						}
						break;
					case ">":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) <= 0) {
								pass = false;
								break;
							}
						}
						break;
					case "<":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) >= 0) {
								pass = false;
								break;
							}
						}
						break;
					case "<>":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) == 0) {
								pass = false;
								break;
							}
						}
						break;
					default:
						break;
					}
				}
				if (pass) {
					listReturn.add(x);
				}
			}
		} else {
			return listData;
		}
		return listReturn;
	}

	public static List<Map<String, Object>> doFilter(List<Map<String, Object>> listData,
			List<EntityCondition> listAllConditions) {
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<Map<String, Object>> listConditions = makeListConditions(listAllConditions);
		if (UtilValidate.isNotEmpty(listConditions)) {
			for (Map<String, Object> x : listData) {
				boolean pass = true;
				for (Map<String, Object> m : listConditions) {
					String fieldName = (String) m.get("fieldName");
					String operator = (String) m.get("operator");
					String value = (String) m.get("value");

					Object fieldValue = x.get(fieldName);

					if (UtilValidate.isEmpty(fieldValue) || UtilValidate.isEmpty(operator)
							|| UtilValidate.isEmpty(value.trim())) {
						pass = false;
						break;
					}

					switch (operator) {
					case "LIKE":
						if (!fieldValue.toString().contains(value)) {
							pass = false;
						}
						break;
					case "NOT_LIKE":
						if (fieldValue.toString().contains(value)) {
							pass = false;
						}
						break;
					case "EQUAL":
						if (!fieldValue.toString().equals(value)) {
							pass = false;
						}
						break;
					case "NOT_EQUAL":
						if (fieldValue.toString().equals(value)) {
							pass = false;
						}
						break;
					case "RANGE":
						String valueFrom = (String) m.get("valueFrom");
						String valueTo = (String) m.get("valueTo");
						if (UtilValidate.isEmpty(valueFrom.trim()) || UtilValidate.isEmpty(valueTo.trim())) {
							pass = false;
							break;
						}
						if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueFromTs = Timestamp.valueOf(valueFrom + " 00:00:00.0");
							Timestamp valueToTs = Timestamp.valueOf(valueTo + " 23:59:59.0");
							if (fieldValueTs.before(valueFromTs)) {
								pass = false;
							}
							if (fieldValueTs.after(valueToTs)) {
								pass = false;
							}
						}
						if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueFromDate = Date.valueOf(valueFrom);
							Date valueToDate = Date.valueOf(valueTo);
							if (fieldValueDate.compareTo(valueFromDate) < 0) {
								pass = false;
							}
							if (fieldValueDate.compareTo(valueToDate) > 0) {
								pass = false;
							}
						}
						break;
					case "=":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) != 0) {
								pass = false;
							}
						} else if (fieldValue instanceof Integer) {
							int fieldValueInt = (int) fieldValue;
							int valueInt = Integer.valueOf(value);
							if (fieldValueInt != valueInt) {
								pass = false;
							}
						} else if (fieldValue instanceof String) {
							if (!fieldValue.toString().equals(value)) {
								pass = false;
							}
						}
						break;
					case ">=":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) < 0) {
								pass = false;
							}
						} else if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
							if (fieldValueTs.before(valueTs)) {
								pass = false;
							}
						} else if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueDate = Date.valueOf(value);
							if (fieldValueDate.compareTo(valueDate) < 0) {
								pass = false;
							}
						}
						break;
					case "<=":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) > 0) {
								pass = false;
								break;
							}
						} else if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
							if (fieldValueTs.after(valueTs)) {
								pass = false;
							}
						} else if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueDate = Date.valueOf(value);
							if (fieldValueDate.compareTo(valueDate) > 0) {
								pass = false;
							}
						}
						break;
					case ">":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) <= 0) {
								pass = false;
								break;
							}
						}
						break;
					case "<":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) >= 0) {
								pass = false;
								break;
							}
						}
						break;
					case "<>":
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if (fieldValueBd.compareTo(valueBd) == 0) {
								pass = false;
								break;
							}
						}
						break;
					default:
						break;
					}
				}
				if (pass) {
					listReturn.add(x);
				}
			}
		} else {
			return listData;
		}
		return listReturn;
	}

	public static List<Map<String, Object>> makeListConditions(List<EntityCondition> listConditions) {
		List<Map<String, Object>> listMapConditions = FastList.newInstance();
		// [partyIdFrom LIKE '%Minh Hòa%']
		// [(birthDate >= '2015-08-18 00:00:00.0' AND birthDate <= '2015-08-31
		// 23:59:59.0')]
		for (EntityCondition condition : listConditions) {
			String cond = condition.toString();
			if (UtilValidate.isNotEmpty(cond)) {
				String[] conditionSplit = cond.split(" ");
				Map<String, Object> condMap = FastMap.newInstance();
				String fieldName = null;
				String operator = null;
				String value = null;

				if (cond.contains("'%")) {
					fieldName = conditionSplit[0];
					operator = conditionSplit[1];
					String[] valueSplit = cond.split("'%");
					value = valueSplit[1];
				} else {
					fieldName = conditionSplit[0];
					operator = conditionSplit[1];
					value = conditionSplit[2].trim();
					if (conditionSplit.length > 4) {
						if (UtilValidate.isNotEmpty(conditionSplit[4].trim())) {
							if ("AND".equals(conditionSplit[4].trim())) {
								operator = "RANGE";
								String valueFrom = conditionSplit[2].trim();
								String valueTo = conditionSplit[7].trim();
								valueFrom = cleanValue(valueFrom);
								valueTo = cleanValue(valueTo);
								condMap.put("valueFrom", valueFrom);
								condMap.put("valueTo", valueTo);
							}
						}
					}
				}
				fieldName = cleanFieldName(fieldName);
				value = cleanValue(value);
				condMap.put("fieldName", fieldName);
				condMap.put("operator", operator);
				condMap.put("value", value);
				listMapConditions.add(condMap);
			}
		}
		return listMapConditions;
	}

	public static String cleanValue(String value) {
		if (value.contains("(")) {
			value = value.replace("(", "");
		}
		if (value.contains(")")) {
			value = value.replace(")", "");
		}
		if (value.contains("'")) {
			value = value.replace("'", "");
		}
		if (value.contains("%")) {
			value = value.replace("%", "");
		}
		return value;
	}

	public static String cleanFieldName(String fieldName) {
		if (fieldName.contains("(")) {
			fieldName = fieldName.replace("(", "");
		}
		if (fieldName.contains(")")) {
			fieldName = fieldName.replace(")", "");
		}
		return fieldName;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCompact(List<Map<String, Object>> listOriginal,
			Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
		int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = pagesize * pageNum + 1;
		int end = start + pagesize;
		int totalRows = 0;

		listReturn = doFilter(listOriginal, listAllConditions);
		if (UtilValidate.isNotEmpty(listSortFields)) {
			listReturn = EntityMiscUtil.sortList(listReturn, listSortFields);
		}
		if (end > listReturn.size()) {
			end = listReturn.size();
		}
		totalRows = listReturn.size();
		listReturn = listReturn.subList(start, end);
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listReturn);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCompact(List<Map<String, Object>> listOriginal,
			Map<String, ? extends Object> context, int totalRows) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
		int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = pagesize * pageNum;
		int end = start + pagesize;

		listReturn = doFilter(listOriginal, listAllConditions);
		if (UtilValidate.isNotEmpty(listSortFields)) {
			listReturn = EntityMiscUtil.sortList(listReturn, listSortFields);
		}
		if (end > listReturn.size()) {
			end = listReturn.size();
		}
		listReturn = listReturn.subList(start, end);
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listReturn);
		return successResult;
	}

	public static Map<String, Object> listCompact(List<Map<String, Object>> listOriginal,
			List<EntityCondition> listAllConditions, List<String> listSortFields, Map<String, String[]> parameters) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
		int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = pagesize * pageNum;
		int end = start + pagesize;
		int totalRows = 0;

		listReturn = doFilter(listOriginal, listAllConditions);
		if (UtilValidate.isNotEmpty(listSortFields)) {
			listReturn = EntityMiscUtil.sortList(listReturn, listSortFields);
		}
		if (end > listReturn.size()) {
			end = listReturn.size();
		}
		totalRows = listReturn.size();
		listReturn = listReturn.subList(start, end);

		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listReturn);
		return successResult;
	}

	public static List<String> getPartiesByRolesAndPartyTo(String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo,
			Delegator delegator, boolean filterByDateExpr) throws GenericEntityException {
		List<String> listParties = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		if (filterByDateExpr) {
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		}
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdFrom",
				roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo)));
		List<GenericValue> listPartyRelationship = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(conditions, EntityJoinOperator.AND), null, null, null, false);
		for (GenericValue x : listPartyRelationship) {
			listParties.add(x.getString("partyIdFrom"));
		}
		return listParties;
	}

	public static Map<String, Object> getPartiesByCampaign(Delegator delegator, String partyIdFrom,
			String marketingCampaignId) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		if ("any".equals(marketingCampaignId)) {
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom)));
		} else {
			conditions.add(EntityCondition.makeCondition(
					UtilMisc.toMap("partyIdFrom", partyIdFrom, "marketingCampaignId", marketingCampaignId)));
		}
		conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "CONTACT_REJECTED"));
		List<GenericValue> partyCampaignRelationships = delegator.findList("PartyCampaignRelationship",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		List<String> listContactId = EntityUtil.getFieldListFromEntityList(partyCampaignRelationships, "partyIdTo",
				true);
		for (GenericValue x : partyCampaignRelationships) {
			Map<String, Object> extendId = FastMap.newInstance();
			extendId.put("marketingCampaignId", x.getString("marketingCampaignId"));
			extendId.put("partyIdFrom", x.getString("partyIdFrom"));
			extendId.put("partyIdTo", x.getString("partyIdTo"));
			extendId.put("roleTypeIdFrom", x.getString("roleTypeIdFrom"));
			extendId.put("roleTypeIdTo", x.getString("roleTypeIdTo"));
			extendId.put("statusId", x.getString("statusId"));
			extendId.put("fromDate", x.getTimestamp("fromDate"));
			result.put(x.getString("partyIdTo"), extendId);
		}
		result.put("listContactId", listContactId);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<EntityCondition> makeCondition(Map<String, ? extends Object> context, Delegator delegator)
			throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		for (EntityCondition e : listAllConditions) {
			String condition = e.toString();
			String[] conditionSplitted = condition.split(" ");
			if (conditionSplitted.length < 2) {
				continue;
			}
			String fieldName = conditionSplitted[0];
			fieldName = cleanFieldName(fieldName);

			String operator = conditionSplitted[1];
			if ("LIKE".equals(operator)) {
				conditionSplitted = condition.split(" '%");
				if (conditionSplitted.length < 2) {
					continue;
				}
				String value = conditionSplitted[1].trim();
				value = cleanValue(value).toUpperCase();
				if (UtilValidate.isEmpty(value)) {
					continue;
				}
				List<EntityCondition> dummyConditions = FastList.newInstance();
				List<String> partyIds = FastList.newInstance();
				switch (fieldName) {
				case "partyId":
					conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"),
							EntityJoinOperator.LIKE, "%" + value.replaceAll("\\s+", "%") + "%"));
					break;
				case "groupName":
					conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"),
							EntityJoinOperator.LIKE, "%" + value.replaceAll("\\s+", "%") + "%"));
					break;
				case "partyFullName":
					conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyFullName"),
							EntityJoinOperator.LIKE, "%" + value.replaceAll("\\s+", "%") + "%"));
					break;
				case "contactNumber":
					if (value.length() < 4) {
						continue;
					}
					conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"),
							EntityJoinOperator.IN, listOwnerTelecomNumber(delegator, value)));
					break;
				case "emailAddress":
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN,
							listOwnerEmail(delegator, value)));
					break;
				case "representativeMember":
					dummyConditions = FastList.newInstance();
					Map<String, Object> mappingPartyName = mappingPartyName(value);
					if (mappingPartyName.containsKey("firstName")) {
						dummyConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"),
								EntityJoinOperator.LIKE,
								"%" + mappingPartyName.get("firstName").toString().replaceAll("\\s+", "%") + "%"));
					}
					if (mappingPartyName.containsKey("middleName")) {
						dummyConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleName"),
								EntityJoinOperator.LIKE,
								"%" + mappingPartyName.get("middleName").toString().replaceAll("\\s+", "%") + "%"));
					}
					if (mappingPartyName.containsKey("lastName")) {
						dummyConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"),
								EntityJoinOperator.LIKE,
								"%" + mappingPartyName.get("lastName").toString().replaceAll("\\s+", "%") + "%"));
					}
					List<String> partyGourpIds = searchGroupOfParty(delegator, EntityCondition
							.makeCondition(dummyConditions, (EntityJoinOperator) mappingPartyName.get("JoinOperator")));
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyGourpIds));
					break;
				case "saler":
					dummyConditions = FastList.newInstance();
					mappingPartyName = mappingPartyName(value);
					if (mappingPartyName.containsKey("firstName")) {
						dummyConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"),
								EntityJoinOperator.LIKE,
								"%" + mappingPartyName.get("firstName").toString().replaceAll("\\s+", "%") + "%"));
					}
					if (mappingPartyName.containsKey("middleName")) {
						dummyConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleName"),
								EntityJoinOperator.LIKE,
								"%" + mappingPartyName.get("middleName").toString().replaceAll("\\s+", "%") + "%"));
					}
					if (mappingPartyName.containsKey("lastName")) {
						dummyConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"),
								EntityJoinOperator.LIKE,
								"%" + mappingPartyName.get("lastName").toString().replaceAll("\\s+", "%") + "%"));
					}
					partyIds = searchSalerOfParty(delegator, EntityCondition.makeCondition(dummyConditions,
							(EntityJoinOperator) mappingPartyName.get("JoinOperator")));
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
					break;
				case "address1":
					if (value.contains("!") || value.contains("@") || value.contains("#") || value.contains("$")) {
						// Advanced search
						String prefix = value.substring(0, 1);
						value = value.substring(1).trim();
						partyIds = FastList.newInstance();
						switch (prefix) {
						case "!":
							// for ward
							partyIds = searchAddressOfParty(delegator,
									makeConditionByGeoType(delegator, "WARD", value, "wardGeoId"));
							conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
							break;
						case "@":
							// for district
							partyIds = searchAddressOfParty(delegator,
									makeConditionByGeoType(delegator, "DISTRICT", value, "districtGeoId"));
							conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
							break;
						case "#":
							// for province
							partyIds = searchAddressOfParty(delegator,
									makeConditionByGeoType(delegator, "PROVINCE", value, "stateProvinceGeoId"));
							conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
							break;
						case "$":
							// for country
							partyIds = searchAddressOfParty(delegator,
									makeConditionByGeoType(delegator, "COUNTRY", value, "countryGeoId"));
							conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
							break;
						default:
							break;
						}
					} else {
						// simple search for address1
						partyIds = searchAddressOfParty(delegator, EntityCondition.makeCondition(
								EntityFunction.UPPER_FIELD("address1"), EntityJoinOperator.LIKE, "%" + value + "%"));
						conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
					}
					break;
				case "districtGeoName":
					partyIds = searchAddressOfParty(delegator, makeConditionByDistrict(delegator, value));
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
					break;
				case "supervisor":
					partyIds = searchSupervisor(delegator, value);
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
					break;
				case "distributor":
					partyIds = searchDistributor(delegator, value);
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
					break;
				case "salesman":
					partyIds = searchSalesman(delegator, value);
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
					break;
				case "supervisorMT":
					partyIds = searchSupervisorMT(delegator, value);
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
					break;
				default:
					conditions.add(e);
					break;
				}
			} else if ("=".equals(operator)) {
				switch (fieldName) {
				case "resultEnumTypeId":
					String conditionStr = e.toString();
					List<EntityCondition> reCond = FastList.newInstance();
					String[] listCond = conditionStr.split(" OR ");
					if (UtilValidate.isNotEmpty(listCond)) {
						for (String curCond : listCond) {
							String[] tmp = curCond.split(" = ");
							if (UtilValidate.isNotEmpty(tmp) && !curCond.contains(default_string)) {
								String re = cleanValue(tmp[1]);
								reCond.add(EntityCondition.makeCondition("resultEnumTypeId", EntityOperator.LIKE,
										re + ",%"));
							} else if (curCond.contains(default_string)) {
								reCond.add(EntityCondition.makeCondition("isCall", EntityOperator.NOT_EQUAL, "Y"));
							}
						}
					}
					conditions.add(EntityCondition.makeCondition(reCond, EntityOperator.OR));
					break;
				default:
					conditions.add(e);
					break;
				}
			} else if (">=".equals(operator)) {
				// (entryDate >= '2015-11-08 00:00:00.0' AND entryDate <=
				// '2015-11-18 23:59:59.0')
				if (condition.contains("AND") && condition.contains("entryDate")) {
					// is range
					List<EntityCondition> conditionsRange = FastList.newInstance();
					conditionsRange.add(EntityCondition.makeCondition(UtilMisc.toMap("communicationEventTypeId",
							"PHONE_COMMUNICATION", "statusId", "COM_SCHEDULED")));
					conditionsRange.add(e);
					List<GenericValue> communicationEvents = delegator.findList("CommunicationEvent",
							EntityCondition.makeCondition(conditionsRange), UtilMisc.toSet("partyIdTo"), null, null,
							false);
					List<String> partyIds = EntityUtil.getFieldListFromEntityList(communicationEvents, "partyIdTo",
							true);
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
				}
			} else {
				conditions.add(e);
			}
		}
		return conditions;
	}

	private static List<String> searchSalesman(Delegator delegator, String value) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullName"), EntityJoinOperator.LIKE,
				"%" + value.replaceAll("\\s+", "%") + "%"));
		List<GenericValue> persons = delegator.findList("PartySalesman",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		List<String> partyIds = EntityUtil.getFieldListFromEntityList(persons, "partyId", true);
		return partyIds;
	}

	private static List<String> searchSupervisor(Delegator delegator, String value) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityJoinOperator.EQUALS, "SALESSUP_DEPT_GT"));
		conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityJoinOperator.LIKE,
				"%" + value.replaceAll("\\s+", "%") + "%"));
		List<GenericValue> supervisors = delegator.findList("PartyToAndPartyNameDetail",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		List<String> partyIds = EntityUtil.getFieldListFromEntityList(supervisors, "partyId", true);
		conditions.clear();
		conditions.add(EntityCondition.makeCondition("supervisorId", EntityJoinOperator.IN, partyIds));
		List<GenericValue> distributors = delegator.findList("PartyDistributor",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		partyIds = EntityUtil.getFieldListFromEntityList(distributors, "partyId", true);
		return partyIds;
	}

	private static List<String> searchSupervisorMT(Delegator delegator, String value) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityJoinOperator.EQUALS, "SALESSUP_DEPT_MT"));
		conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityJoinOperator.LIKE,
				"%" + value.replaceAll("\\s+", "%") + "%"));
		List<GenericValue> supervisors = delegator.findList("PartyToAndPartyNameDetail",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		List<String> partyIds = EntityUtil.getFieldListFromEntityList(supervisors, "partyId", true);
		conditions.clear();
		conditions.add(EntityCondition.makeCondition("supervisorId", EntityJoinOperator.IN, partyIds));
		List<GenericValue> partyCustomers = delegator.findList("PartyCustomer",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		partyIds = EntityUtil.getFieldListFromEntityList(partyCustomers, "partyId", true);
		return partyIds;
	}

	private static List<String> searchDistributor(Delegator delegator, String value) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdTo", "INTERNAL_ORGANIZATIO",
				"roleTypeIdFrom", "DISTRIBUTOR", "partyRelationshipTypeId", "DISTRIBUTOR_REL")));
		conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityJoinOperator.LIKE,
				"%" + value.replaceAll("\\s+", "%") + "%"));
		List<GenericValue> supervisors = delegator.findList("PartyFromAndPartyNameDetail",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		List<String> partyIds = EntityUtil.getFieldListFromEntityList(supervisors, "partyId", true);
		conditions.clear();
		conditions.add(EntityCondition.makeCondition("distributorId", EntityJoinOperator.IN, partyIds));
		List<GenericValue> partyCustomers = delegator.findList("PartyCustomer",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		partyIds = EntityUtil.getFieldListFromEntityList(partyCustomers, "partyId", true);
		return partyIds;
	}

	public static boolean isPartyHasLatestResult(Delegator delegator, GenericValue e, List<String> keys) {
		String communicationEventId = e.getString("communicationEventId");
		String resultEnumTypeId = e.getString("resultEnumTypeId");
		if (UtilValidate.isNotEmpty(communicationEventId)) {
			String[] tmpEntryList = communicationEventId.split(",");
			String[] tmpResultEnum = resultEnumTypeId.split(",");
			Timestamp best = null;
			Timestamp cur = null;
			int i = 0;
			int curIndex = 0;
			GenericValue event = null;
			for (String entry : tmpEntryList) {
				try {
					event = delegator.findOne("CommunicationEvent", UtilMisc.toMap("communicationEventId", entry),
							false);
					cur = event.getTimestamp("entryDate");
					if (best == null || (best.getTime() < cur.getTime())) {
						best = (Timestamp) cur.clone();
						curIndex = i;
					}
				} catch (GenericEntityException e1) {
					e1.printStackTrace();
				}
				i++;
			}
			if (curIndex < tmpResultEnum.length && keys.contains(tmpResultEnum[curIndex])) {
				return true;
			}
		} else if (keys.contains(default_string)) {
			return true;
		}

		return false;
	}

	public static Timestamp convertStringToTimestamp(String str_date) {
		try {
			DateFormat formatter;
			formatter = new SimpleDateFormat("dd/MM/yyyy");
			// you can change format of date
			Date date = (Date) formatter.parse(str_date);
			java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
			return timeStampDate;
		} catch (ParseException e) {
			System.out.println("Exception :" + e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<EntityCondition> makeCondition(Map<String, ? extends Object> context, Delegator delegator,
			boolean forAgreement) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		for (EntityCondition e : listAllConditions) {
			String condition = e.toString();
			String[] conditionSplitted = condition.split(" ");
			if (conditionSplitted.length < 2) {
				continue;
			}
			String fieldName = conditionSplitted[0];
			String operator = conditionSplitted[1];
			if ("LIKE".equals(operator)) {
				conditionSplitted = condition.split(" '%");
				if (conditionSplitted.length < 2) {
					continue;
				}
				String value = conditionSplitted[1].trim();
				value = cleanValue(value).toUpperCase();
				fieldName = cleanFieldName(fieldName);
				switch (fieldName) {
				case "groupName":
					conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyFullName"),
							EntityJoinOperator.LIKE, "%" + value.replaceAll("\\s+", "%") + "%"));
					break;
				case "partyFrom":
					conditions.add(EntityCondition.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"),
											EntityJoinOperator.LIKE, "%" + value.replaceAll("\\s+", "%") + "%"),
									EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyFullName"),
											EntityJoinOperator.LIKE, "%" + value.replaceAll("\\s+", "%") + "%")),
							EntityJoinOperator.OR));
					break;
				default:
					conditions.add(e);
					break;
				}
			} else {
				conditions.add(e);
			}
		}
		return conditions;
	}

	private static List<String> listOwnerEmail(Delegator delegator, String infoString) throws GenericEntityException {
		List<GenericValue> listContactMech = delegator.findList(
				"ContactMech", EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("infoString"),
						EntityJoinOperator.LIKE, "%" + infoString + "%"),
				UtilMisc.toSet("contactMechId"), null, null, false);
		List<String> listContactMechId = EntityUtil.getFieldListFromEntityList(listContactMech, "contactMechId", true);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("contactMechId", EntityJoinOperator.IN, listContactMechId));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_EMAIL")));
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		List<String> ownerId = EntityUtil.getFieldListFromEntityList(listPartyContactMechPurpose, "partyId", true);
		return ownerId;
	}

	private static List<String> listOwnerTelecomNumber(Delegator delegator, String contactNumber)
			throws GenericEntityException {
		List<GenericValue> listTelecomNumber = delegator.findList("TelecomNumber",
				EntityCondition.makeCondition("contactNumber", EntityJoinOperator.LIKE, "%" + contactNumber + "%"),
				UtilMisc.toSet("contactMechId"), null, null, false);
		List<String> listContactMechId = EntityUtil.getFieldListFromEntityList(listTelecomNumber, "contactMechId",
				true);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("contactMechId", EntityJoinOperator.IN, listContactMechId));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_PHONE")));
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		List<String> ownerId = EntityUtil.getFieldListFromEntityList(listPartyContactMechPurpose, "partyId", true);
		return ownerId;
	}

	private static Map<String, Object> mappingPartyName(String partyFullName) {
		Map<String, Object> mapping = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(partyFullName)) {
			String[] arrayName = partyFullName.split(" ");
			switch (arrayName.length) {
			case 1:
				mapping.put("lastName", arrayName[0]);
				mapping.put("middleName", arrayName[0]);
				mapping.put("firstName", arrayName[0]);
				mapping.put("JoinOperator", EntityJoinOperator.OR);
				break;
			case 2:
				mapping.put("lastName", arrayName[0]);
				mapping.put("firstName", arrayName[1]);
				mapping.put("JoinOperator", EntityJoinOperator.AND);
				break;
			case 3:
				mapping.put("lastName", arrayName[0]);
				mapping.put("middleName", arrayName[1]);
				mapping.put("firstName", arrayName[2]);
				mapping.put("JoinOperator", EntityJoinOperator.AND);
				break;
			default:
				if (arrayName.length > 3) {
					mapping.put("lastName", arrayName[0]);
					String firstName = "";
					for (int i = 1; i < (arrayName.length - 1); i++) {
						firstName += arrayName[i] + " ";
					}
					mapping.put("middleName", firstName.trim());
					mapping.put("firstName", arrayName[arrayName.length - 1]);
					mapping.put("JoinOperator", EntityJoinOperator.AND);
				}
				break;
			}
		}
		return mapping;
	}

	private static List<String> searchGroupOfParty(Delegator delegator, EntityCondition condition)
			throws GenericEntityException {
		List<GenericValue> persons = delegator.findList("Person", condition, UtilMisc.toSet("partyId"), null, null,
				false);
		List<String> partyIds = EntityUtil.getFieldListFromEntityList(persons, "partyId", true);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.IN, partyIds));
		conditions.add(EntityCondition.makeCondition(
				UtilMisc.toMap("roleTypeIdFrom", "REPRESENTATIVE", "roleTypeIdTo", "ORGANIZATION_UNIT")));
		List<GenericValue> partyIdTos = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdTo"), null, null, false);
		List<String> partyGourpIds = EntityUtil.getFieldListFromEntityList(partyIdTos, "partyIdTo", true);
		return partyGourpIds;
	}

	private static List<String> searchSalerOfParty(Delegator delegator, EntityCondition condition)
			throws GenericEntityException {
		List<GenericValue> persons = delegator.findList("Person", condition, UtilMisc.toSet("partyId"), null, null,
				false);
		List<String> partyIds = EntityUtil.getFieldListFromEntityList(persons, "partyId", true);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.IN, partyIds));
		conditions.add(EntityCondition.makeCondition(
				UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE", "partyRelationshipTypeId", "SALES_REP_REL")));
		List<GenericValue> partyIdTos = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdTo"), null, null, false);
		partyIds = EntityUtil.getFieldListFromEntityList(partyIdTos, "partyIdTo", true);
		return partyIds;
	}

	private static List<String> searchAddressOfParty(Delegator delegator, EntityCondition condition)
			throws GenericEntityException {
		List<GenericValue> postalAddresses = delegator.findList("PostalAddress", condition,
				UtilMisc.toSet("contactMechId"), null, null, false);
		List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(postalAddresses, "contactMechId", true);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("contactMechId", EntityJoinOperator.IN, contactMechIds));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_LOCATION")));
		List<GenericValue> partyContactMechPurposes = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		List<String> partyIds = EntityUtil.getFieldListFromEntityList(partyContactMechPurposes, "partyId", true);
		return partyIds;
	}

	private static EntityCondition makeConditionByGeoType(Delegator delegator, String geoTypeId, String value,
			String geoId) throws GenericEntityException {
		List<EntityCondition> dummyConditions = FastList.newInstance();
		dummyConditions.clear();
		dummyConditions.add(EntityCondition.makeCondition("geoTypeId", EntityJoinOperator.EQUALS, geoTypeId));
		dummyConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("geoName"),
				EntityJoinOperator.LIKE, "%" + value + "%"));
		List<GenericValue> geos = delegator.findList("Geo", EntityCondition.makeCondition(dummyConditions),
				UtilMisc.toSet("geoId"), null, null, false);
		List<String> geoIds = EntityUtil.getFieldListFromEntityList(geos, "geoId", true);
		return EntityCondition.makeCondition(geoId, EntityJoinOperator.IN, geoIds);
	}

	private static EntityCondition makeConditionByDistrict(Delegator delegator, String value)
			throws GenericEntityException {
		List<EntityCondition> dummyConditions = FastList.newInstance();
		dummyConditions.clear();
		dummyConditions.add(EntityCondition.makeCondition("geoTypeId", EntityJoinOperator.EQUALS, "DISTRICT"));
		dummyConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("geoName"),
				EntityJoinOperator.LIKE, "%" + value + "%"));
		List<GenericValue> geos = delegator.findList("Geo", EntityCondition.makeCondition(dummyConditions),
				UtilMisc.toSet("geoId"), null, null, false);
		List<String> geoIds = EntityUtil.getFieldListFromEntityList(geos, "geoId", true);
		return EntityCondition.makeCondition("districtGeoId", EntityJoinOperator.IN, geoIds);
	}
}
