package com.olbius.globalSetting;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.ChineseCalendar;
import com.olbius.util.CommonServices;
import com.olbius.util.DateUtil;

public class GlobalSettingServices {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateEmplPositionTypeWorkWeek(DispatchContext dctx, Map<String,Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		Locale locale = (Locale)context.get("locale");
		String dayOfWeek = (String)context.get("dayOfWeek");
		List<String> workShiftIds = (List<String>)context.get("workShiftId");
		if(UtilValidate.isNotEmpty(workShiftIds)){
			if(workShiftIds.contains("_NA_")){
				for(String workShiftId: workShiftIds){
					if(!workShiftId.equals("_NA_")){
						return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "WorkShiftSetIsNotValid", locale));
					}
				}
			}
			try {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
				conditions.add(EntityCondition.makeCondition("dayOfWeek", dayOfWeek));
				conditions.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> currWorkShiftEmplPosType = delegator.findList("EmplPositionTypeWorkWeek", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
				for(GenericValue tempWorkShift: currWorkShiftEmplPosType){
					String workingShiftId = tempWorkShift.getString("workingShiftId");
					//if new workshiftIds don't contains current workshift, expire workshift
					if(!workShiftIds.contains(workingShiftId)){
						tempWorkShift.set("thruDate", UtilDateTime.nowTimestamp());
						tempWorkShift.store();
					}
				}
				for(String workShift: workShiftIds){
					List<GenericValue> workShifts = delegator.findByAnd("EmplPositionTypeWorkWeek", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "dayOfWeek", dayOfWeek, "workingShiftId", workShift),null, false);
					if(UtilValidate.isEmpty(workShifts)){
						GenericValue workShiftGv = delegator.makeValue("EmplPositionTypeWorkWeek");
						workShiftGv.set("emplPositionTypeId", emplPositionTypeId);
						workShiftGv.set("dayOfWeek", dayOfWeek);
						workShiftGv.set("workingShiftId", workShift);
						workShiftGv.set("fromDate", UtilDateTime.nowTimestamp());
						delegator.create(workShiftGv);
					}
					
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	
	/*HUNGNC START EDIT*/
	public Map<String, Object> createInternalPurchaseLimit(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String amountLimit = (String)context.get("amountLimit");
		String amountLimitRemain = (String)context.get("amountLimitRemain");
		Number internalPurchasePrice = (Number)context.get("internalPurchasePrice");
		
		try {
			GenericValue internalPurchaseLimitExits = delegator.findOne("InternalPurchaseLimit", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			if(internalPurchaseLimitExits != null){
			return ServiceUtil.returnError(UtilProperties.getMessage("InternalPurchaseUiLables", "checkDuplicate", locale));
		}
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		} 
		
		if(emplPositionTypeId == null || amountLimit == null || internalPurchasePrice == null){
			return ServiceUtil.returnError(UtilProperties.getMessage("InternalPurchaseUiLables", "checkNullInternal", locale));
		}
		
		if(Integer.parseInt(amountLimit) < Integer.parseInt(amountLimitRemain)){
			return ServiceUtil.returnError(UtilProperties.getMessage("InternalPurchaseUiLables", "checkAmountLimit", locale));
		}
		
		if(Integer.parseInt(internalPurchasePrice.toString()) < 0 || Integer.parseInt(internalPurchasePrice.toString()) > 100){
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "comparativeValue", locale));
		}	
		GenericValue internalPurchaseLimit = delegator.makeValidValue("InternalPurchaseLimit", context);
		
		internalPurchaseLimit.put("emplPositionTypeId", emplPositionTypeId);
		internalPurchaseLimit.put("amountLimit", amountLimit);
		internalPurchaseLimit.put("amountLimitRemain", amountLimitRemain);
		internalPurchaseLimit.put("internalPurchasePrice", internalPurchasePrice);
		
		try {
			internalPurchaseLimit.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));	
		return retMap;
	}
	
	public static Map<String, Object> updateInternalPurchaseLimit(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		Number internalPurchasePrice = (Number)context.get("internalPurchasePrice");
		String amountLimit = (String)context.get("amountLimit");
		String amountLimitRemain = (String)context.get("amountLimitRemain");
		Locale locale = (Locale)context.get("locale");
		if( Integer.parseInt(internalPurchasePrice.toString())  < 0 || Integer.parseInt(internalPurchasePrice.toString()) > 100){
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "comparativeValue", locale));
		}
		if(Integer.parseInt(amountLimit) < Integer.parseInt(amountLimitRemain)){
			return ServiceUtil.returnError(UtilProperties.getMessage("InternalPurchaseUiLables", "checkAmountLimit", locale));
		}
		try {
			GenericValue internalPurchaseLimit = delegator.findOne("InternalPurchaseLimit", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			if(UtilValidate.isEmpty(internalPurchaseLimit)){
				return ServiceUtil.returnError(UtilProperties.getMessage("InternalPurchaseUiLables", "checkNullInternal", locale));
			}
			
			internalPurchaseLimit.setNonPKFields(context);
			internalPurchaseLimit.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	/*HUNGNC END EDIT*/
	
	public static Map<String, Object> updateHealthCareProvider(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		String contactMechId = (String)context.get("contactMechId");
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		String countryGeoId = (String)context.get("countryGeoId");
		try {
			GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			GenericValue party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
			party.setNonPKFields(context);
			party.store();
			if(stateProvinceGeoId != null && countryGeoId != null){
				GenericValue stateProvince = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
				GenericValue country = delegator.findOne("Geo", UtilMisc.toMap("geoId", countryGeoId), false);
				String address1 = stateProvince.getString("geoName") + ", " + country.getString("geoName");
				if(contactMechId != null){
					dispatcher.runSync("updatePartyPostalAddress", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "countryGeoId", countryGeoId, "stateProvinceGeoId", stateProvinceGeoId, "userLogin", userLogin, "address1", address1, "city", stateProvince.getString("geoName"), "postalCode", "10000"));	
				}else{
					dispatcher.runSync("createPartyPostalAddress", UtilMisc.toMap("partyId", partyId, "countryGeoId", countryGeoId, "stateProvinceGeoId", stateProvinceGeoId, "userLogin", userLogin, "address1", address1, "city", stateProvince.getString("geoName"), "postalCode", "10000"));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	public static Map<String, Object> deleteEmplPositionType(DispatchContext dctx, Map<String, Object> context){
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue emplPosType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			emplPosType.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("GlobalHRSettingUiLabels", "ErrorConstrainKey", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> deleteRecruitmentType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String recruitmentTypeId = (String)context.get("recruitmentTypeId");
		try {
			GenericValue recruitmentType = delegator.findOne("RecruitmentType", UtilMisc.toMap("recruitmentTypeId", recruitmentTypeId), false);
			recruitmentType.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(e.getMessage().contains("violates foreign key constraint")){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotDeleteBecauseContrainsKey", locale));	
			}
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "deleteSuccessfully", locale));
	}
	//JQX Services for Global Settings HR
	
	/*
	 * Description : get List Pay Grade JQ
	 * @param DispatchContext 
	 * @param Context 
	 * @return
	 * @Exception
	 * 
	 * */
		public static Map<String,Object> JQgetListPayGrade(DispatchContext dpct,Map<String,Object> context){
				Delegator delegator = dpct.getDelegator();
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				Map<String,Object> result = FastMap.newInstance();
				EntityListIterator listIterator = null;
				try {
					listIterator = delegator.find("PayGrade",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, null);
					if(listIterator.getCompleteList().size() > 0){
						result.put("listIterator", listIterator);		
					}
				} catch (Exception e) {
					e.printStackTrace();
					return ServiceUtil.returnError("Fatal Error when get list employee in Org" + e.getMessage());
					// TODO: handle exception
				}
				return result;		
				}
	/*
	 * Description : get List Pay Grade JQ
	 * @param DispatchContext 
	 * @param Context 
	 * @return
	 * @Exception
	 * 
	 * */
		public static Map<String,Object> JQgetListSalarySteps(DispatchContext dpct,Map<String,Object> context){
				Delegator delegator = dpct.getDelegator();
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
				String payGradeId = (String) parameters.get("payGradeId")[0];
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				Map<String,Object> result = FastMap.newInstance();
				EntityListIterator listIterator = null;
				try {
					if(!payGradeId.isEmpty()){
						listAllConditions.add(EntityCondition.makeCondition("payGradeId",payGradeId));
					}
					listIterator = delegator.find("SalaryStep",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, null);
					if(listIterator.getCompleteList().size() > 0){
						result.put("listIterator", listIterator);		
					}
				} catch (Exception e) {
					e.printStackTrace();
					return ServiceUtil.returnError("Fatal Error when get list employee in Org" + e.getMessage());
					// TODO: handle exception
				}
				return result;		
				}
	/*
	 * Service Create or Update SalaryStep 
	 * @DispatchContext
	 * @Context
	 * @return
	 * 
	 * */
	public static Map<String,Object> creatOrUpdateSalaryStep(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		String salaryStepSeqId = (String) context.get("salaryStepSeqId");
		String payGradeId = (String) context.get("payGradeId");
		Timestamp dateModified = (Timestamp) context.get("dateModified");
		BigDecimal amount = (BigDecimal) context.get("amount");
		try{
			GenericValue referenceEntity = delegator.findOne("SalaryStep", false, UtilMisc.toMap("salaryStepSeqId", salaryStepSeqId,"payGradeId",payGradeId));
			if(UtilValidate.isNotEmpty(referenceEntity)){
					referenceEntity.set("dateModified", dateModified);
					referenceEntity.set("amount", amount);
					referenceEntity.store();
			}else{
				if(!salaryStepSeqId.isEmpty() && !payGradeId.isEmpty()){
					GenericValue SalaryStep = delegator.makeValue("SalaryStep");
					SalaryStep.set("salaryStepSeqId", salaryStepSeqId);
					SalaryStep.set("payGradeId", payGradeId);
					SalaryStep.set("dateModified", dateModified);
					SalaryStep.set("amount", amount);
					SalaryStep.create();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal error when create or update salary step" + e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	/*
	 * Description : get List Pay Grade JQ
	 * @param DispatchContext 
	 * @param Context 
	 * @return
	 * @Exception
	 * 
	 * */
		public static Map<String,Object> JQgetListRescruitmentCriteria(DispatchContext dpct,Map<String,Object> context){
				Delegator delegator = dpct.getDelegator();
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				Map<String,Object> result = FastMap.newInstance();
				EntityListIterator listIterator = null;
				try {
					listIterator = delegator.find("EmplPositionTypeAndCriteria",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, null);
					if(listIterator.getCompleteList().size() > 0){
						result.put("listIterator", listIterator);		
					}
				} catch (Exception e) {
					e.printStackTrace();
					return ServiceUtil.returnError("Fatal Error when get list RecruitmentCriteria" + e.getMessage());
					// TODO: handle exception
				}
				return result;		
				}
		/*
		 * Description :create Recruitment Criteria And Empl Position Type
		 * @param DispatchContext 
		 * @param Context 
		 * @return
		 * @Exception
		 * 
		 * */
			public static Map<String,Object> JQCreateCriteriaAndEmplPositionType(DispatchContext dpct,Map<String,Object> context){
					Delegator delegator = dpct.getDelegator();
					String recruitmentCriteriaId = (String) context.get("recruitmentCriteriaId");
					String description = (String) context.get("description");
					@SuppressWarnings("unchecked")
					List<Map<Integer,String>> listEmplPos = (List<Map<Integer,String>>) context.get("ListEmplPos");
					try {
						try {
							GenericValue RecruitmentVal = delegator.makeValue("RecruitmentCriteria");
							RecruitmentVal.set("recruitmentCriteriaId", recruitmentCriteriaId);
							RecruitmentVal.set("description", description);
							RecruitmentVal.create();
						} catch (Exception e) {
							e.printStackTrace();
							return ServiceUtil.returnError("Fatal error when create RecruitmentCriteria cause : " + e.getMessage());
							// TODO: handle exception
						}
						
						try {
							if(UtilValidate.isNotEmpty(listEmplPos)){
								for(Map<Integer,String> pos : listEmplPos){
										if(!((String)pos.get("value")).isEmpty()) {
											GenericValue EmplPosTypeCriteriaVal = delegator.makeValue("EmplPositionTypeCriteria");
											EmplPosTypeCriteriaVal.set("recruitmentCriteriaId", recruitmentCriteriaId);
											EmplPosTypeCriteriaVal.set("emplPositionTypeId", (String) pos.get("value"));
											EmplPosTypeCriteriaVal.create();
										}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							return ServiceUtil.returnError("Fatal error when create Empl Position Type Criteria cause : " + e.getMessage());
							// TODO: handle exception
						}
					} catch (Exception e) {
						e.printStackTrace();
						return ServiceUtil.returnError("Fatal Error when get list RecruitmentCriteria" + e.getMessage());
						// TODO: handle exception
					}
					return ServiceUtil.returnSuccess();		
					}
				
				
				/*
				 * Description :update or delete Recruitment Criteria And Empl Position Type
				 * @param DispatchContext 
				 * @param Context 
				 * @return
				 * @Exception
				 * 
				 * */
					public static Map<String,Object> JQDeleteCriteriaAndEmplPositionType(DispatchContext dpct,Map<String,Object> context){
							Delegator delegator = dpct.getDelegator();
							String recruitmentCriteriaId = (String) context.get("recruitmentCriteriaId");
							String emplPositionTypeId = (String) context.get("emplPositionTypeId");
							List<GenericValue> listEmplTypeCriteria = FastList.newInstance();
							try {
								listEmplTypeCriteria = delegator.findList("EmplPositionTypeCriteria", EntityCondition.makeCondition("recruitmentCriteriaId",recruitmentCriteriaId), null, null, null, false);
								try {
									if( listEmplTypeCriteria.size() > 1){
										if(UtilValidate.isNotEmpty(emplPositionTypeId) && UtilValidate.isNotEmpty(recruitmentCriteriaId)){
											GenericValue tempDel = delegator.findOne("EmplPositionTypeCriteria", false, UtilMisc.toMap("recruitmentCriteriaId", recruitmentCriteriaId,"emplPositionTypeId",emplPositionTypeId));
											tempDel.remove();
										}
									}else {
											GenericValue tempDel = delegator.findOne("EmplPositionTypeCriteria", false, UtilMisc.toMap("recruitmentCriteriaId", recruitmentCriteriaId,"emplPositionTypeId",emplPositionTypeId));
											tempDel.remove();
											GenericValue recruiDel = delegator.findOne("RecruitmentCriteria", false, UtilMisc.toMap("recruitmentCriteriaId", recruitmentCriteriaId));
											recruiDel.remove();
									}	
								} catch (Exception e) {
									e.printStackTrace();
									return ServiceUtil.returnError("Fatal error when delete Empl Position Type Criteria cause : " + e.getMessage());
									// TODO: handle exception
								}
								try {
									
								} catch (Exception e) {
									e.printStackTrace();
									return ServiceUtil.returnError("Fatal error when delete  Recruitment Criteria cause : " + e.getMessage());
									// TODO: handle exception
								}
							} catch (Exception e) {
								e.printStackTrace();
								return ServiceUtil.returnError("Fatal Error when get list RecruitmentCriteria" + e.getMessage());
								// TODO: handle exception
							}
							return ServiceUtil.returnSuccess();		
					}
					
	public static Map<String,Object> JQgetListRecruitmentForm (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("RecruitmentForm", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List Recruitment Form cause : " + e.getMessage());
		}
		return result;
	}			
	
	public static Map<String,Object> JQgetListRecruitmentType (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("RecruitmentType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List Recruitment Type cause : " + e.getMessage());
		}
		return result;
	}		
	
	public static Map<String,Object> JQgetListTrainingType (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("TrainingType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List TrainingType  cause : " + e.getMessage());
		}
		return result;
	}		
	
	public static Map<String,Object> JQgetListTrainingLevel (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("TrainingLevel", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List TrainingLevel cause : " + e.getMessage());
		}
		return result;
	}	
	
	public static Map<String,Object> JQgetListTrainingForm (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("TrainingFormType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List TrainingForm cause : " + e.getMessage());
		}
		return result;
	}	
	
	public static Map<String,Object> JQgetListJobInterviewType (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("JobInterviewType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List JobInterviewType cause : " + e.getMessage());
		}
		return result;
	}	
	
	
	public static Map<String,Object> JQgetListJobQuestionType (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("JobQuestionType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List JobQuestionType cause : " + e.getMessage());
		}
		return result;
	}	
	
	public static Map<String,Object> JQgetListEmplPositionTypes (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("EmplPositionType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List EmplPositionType cause : " + e.getMessage());
		}
		return result;
	}	
	
	public static Map<String,Object> JQgetListQualificationTypes (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listAllConditions.add(EntityCondition.makeCondition("parentTypeId","DEGREE"));	
			listIterator = delegator.find("PartyQualType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List QualificationTypes  cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map<String,Object> JQgetListExamTypes (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listAllConditions.add(EntityCondition.makeCondition("enumTypeId","EXAM_TYPE"));	
			listIterator = delegator.find("Enumeration", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List ExamTypes cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map<String,Object> JQgetListPersonalBackground (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("PersonalBackground", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List ExamTypes cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map<String,Object> JQgetListEmplLeaveType (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("EmplLeaveType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List ExamTypes cause : " + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String,Object> JQgetListPunishmentType (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("PunishmentType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List ExamTypes cause : " + e.getMessage());
		}
		return result;
	}
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String,Object> JQgetFindPerfReviewItemType (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("PerfReviewItemType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List ExamTypes cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map uppPerfReviewItemType(DispatchContext dctx, Map context) {
		Map upp = ServiceUtil
				.returnSuccess("You have called on service 'update successfully!");
		return upp;
	}
	
	public static Map updatePerfReviewItemType(DispatchContext dctx, Map context) {

		String perfReviewItemTypeId = (String) context.get("perfReviewItemTypeId");
		String description = (String) context.get("description");
		String emplPositionTypeId = (String) context.get("emplPositionTypeId");
		Double weight = (Double) context.get("weight");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");

		Delegator del = dctx.getDelegator();
		GenericValue g = del.makeValue("PerfReviewItemType");
		g.put("perfReviewItemTypeId", perfReviewItemTypeId);
		g.put("description", description);
		g.put("emplPositionTypeId", emplPositionTypeId);
		g.put("weight", weight);
		g.put("fromDate", fromDate);
		g.put("thruDate", thruDate);
		try {
			del.store(g);
		} catch (Exception e) {

		}

		 Map upp = ServiceUtil.returnSuccess("update successful");
		 return upp;
	}	
	
	public static Map<String, Object> deletePerfReviewItemType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String recruitmentTypeId = (String)context.get("perfReviewItemTypeId");
		try {
			GenericValue recruitmentType = delegator.findOne("PerfReviewItemType", UtilMisc.toMap("recruitmentTypeId", recruitmentTypeId), false);
			recruitmentType.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(e.getMessage().contains("violates foreign key constraint")){
				return ServiceUtil.returnError(UtilProperties.getMessage("GlobalHRSettingUiLabels", "CannotDeleteBecauseContrainsKey", locale));	
			}
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String,Object> JQgetListMarialStatus (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("MaritalStatus", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List ExamTypes cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map<String,Object> JQgetListEthnicOrigin (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("EthnicOrigin", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List Ethnic Origin cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map<String,Object> JQgetListReligion (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("Religion", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List Religion cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map<String,Object> JQgetListNationality (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("Nationality", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List Nationality  cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map<String,Object> JQgetListUniversity (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("EducationSchool", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List University  cause : " + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListMajor (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("Major", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List Major  cause : " + e.getMessage());
		}
		return result;
	}
	
	/*public static Map<String,Object> createWorkingShiftHR(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		String description = (String) context.get("description");
		Time startTime = (Time) context.get("startTime");
		Time endTime = (Time) context.get("endTime");
		Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
		EntityFindOptions options = new EntityFindOptions();
		try {
			
			List<EntityCondition> listcond = FastList.newInstance();
			listcond.add(EntityCondition.makeCondition("workingShiftId",EntityJoinOperator.NOT_EQUAL,"_NA_"));
			listcond.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(nowtimestamp)));
			List<GenericValue> listWorkingShift = delegator.findList("WorkingShift", EntityCondition.makeCondition(listcond,EntityJoinOperator.AND), null,null, options, false);
			boolean validateTime = false ;
			if(UtilValidate.isNotEmpty(listWorkingShift)){
				for(GenericValue working : listWorkingShift){
					if(endTime.before((Time) working.getTime("startTime")) || startTime.after((Time) working.getTime("endTime"))){
						validateTime = true;
					}
				}
				if(validateTime){
					try {
						String workingShiftId = delegator.getNextSeqId("WorkingShift");
						GenericValue workingShift = delegator.makeValue("WorkingShift");
						workingShift.set("workingShiftId", workingShiftId);
						workingShift.set("description", description);
						workingShift.set("startTime", startTime);
						workingShift.set("endTime", endTime);
						workingShift.set("fromDate", UtilDateTime.getDayStart(nowtimestamp));
						workingShift.create();	
					} catch (Exception e) {
						e.printStackTrace();
						return ServiceUtil.returnError("Error when create working Shift cause : " + e.getMessage());
						// TODO: handle exception
					}
					
				}else return ServiceUtil.returnError("Time is not valid");
			}else {
					try{	
						String workingShiftId = delegator.getNextSeqId("WorkingShift");
						GenericValue workingShift = delegator.makeValue("WorkingShift");
						workingShift.set("workingShiftId", workingShiftId);
						workingShift.set("description", description);
						workingShift.set("startTime", startTime);
						workingShift.set("endTime", endTime);
						workingShift.set("fromDate", UtilDateTime.getDayStart(nowtimestamp));
						workingShift.create();
					} catch (Exception e) {
						e.printStackTrace();
						return ServiceUtil.returnError("Error when create working Shift cause : " + e.getMessage());
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal error when create working Shift cause : " + e.getMessage());
			// TODO: handle exception
		}
		return ServiceUtil.returnSuccess();
	}*/
	
}
