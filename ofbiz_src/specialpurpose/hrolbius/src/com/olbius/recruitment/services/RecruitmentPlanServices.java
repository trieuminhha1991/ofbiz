package com.olbius.recruitment.services;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.recruitment.helper.RecruitmentPlanServiceHelper;
import com.olbius.recruitment.helper.RoleTyle;

public class RecruitmentPlanServices implements RoleTyle {
	
	public static final String module = RecruitmentPlanServices.class.getName();
	public static final String resourceNoti = "NotificationUiLabels";
	
	public static Map<String, Object> updateRecruitmentPlanHeader(DispatchContext ctx, Map<String, Object> context){
		//Get Parameters
		Locale locale = (Locale) context.get("locale");
		try {
			return RecruitmentPlanServiceHelper.updateRecruitmentPlanHeader(ctx, context);
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "updateError", new Object[]{e.getMessage()}, locale));
		}
	}
	
	public static Map<String, Object> updateSaleRecruitmentPlanHeader(DispatchContext ctx, Map<String, Object> context){
		//Get Parameters
		Locale locale = (Locale) context.get("locale");
		try {
			return RecruitmentPlanServiceHelper.updateSaleRecruitmentPlanHeader(ctx, context);
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "updateError", new Object[]{e.getMessage()}, locale));
		}
	}
	
	public static Map<String, Object> updateSaleRecruitmentPlanDTHeader(DispatchContext ctx, Map<String, Object> context){
		//Get Parameters
		Locale locale = (Locale) context.get("locale");
		try {
			return RecruitmentPlanServiceHelper.updateSaleRecruitmentPlanDTHeader(ctx, context);
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "updateError", new Object[]{e.getMessage()}, locale));
		}
	}
	
	public static Map<String, Object> updateRecruitmentPlanDTHeader(DispatchContext ctx, Map<String, Object> context){
		//Get Parameters
		Locale locale = (Locale) context.get("locale");
		try {
			return RecruitmentPlanServiceHelper.updateRecruitmentPlanDTHeader(ctx, context);
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "updateError", new Object[]{e.getMessage()}, locale));
		}
	}
	
	public static Map<String, Object> createRecruitmentPlanHeader(DispatchContext dpctx, Map<String, Object> context) {
		//Get Parameters
		Locale locale = (Locale) context.get("locale");
		try {
			return RecruitmentPlanServiceHelper.createRecruitmentPlan(dpctx, context);
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
		}
	}
	
	public static Map<String, Object> createRecruitmentPlanDTHeader(DispatchContext dpctx, Map<String, Object> context) {
		//Get Parameters
		Locale locale = (Locale) context.get("locale");
		try {
			return RecruitmentPlanServiceHelper.createRecruitmentPlanDT(dpctx, context);
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
		}
	}
	
	public static Map<String, Object> createOrUpdateRecruitmentPlan(DispatchContext ctx, Map<String, Object> context){
		//Get Parameters
		Locale locale = (Locale) context.get("locale");
		try {
			return RecruitmentPlanServiceHelper.createOrUpdateRecruitmentPlan(ctx, context);
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
		}
	}
	
	public static Map<String, Object> createOrUpdateRecruitmentPlanDT(DispatchContext ctx, Map<String, Object> context){
		//Get Parameters
		Locale locale = (Locale) context.get("locale");
		try {
			return RecruitmentPlanServiceHelper.createOrUpdateRecruitmentPlanDT(ctx, context);
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
		}
	}
}
