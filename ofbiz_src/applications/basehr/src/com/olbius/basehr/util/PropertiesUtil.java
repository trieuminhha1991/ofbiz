package com.olbius.basehr.util;

import java.util.List;
import java.util.Locale;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;

public class PropertiesUtil {
	public static final String RESOURCE_NOTI = "NotificationUiLabels";
	public static final int retiredAge = 60;
	public static final String PERMANENT_RESIDENCE = "PERMANENT_RESIDENCE";
	public static final String CURRENT_RESIDENCE = "CURRENT_RESIDENCE";
	public static final String PERSON_TYPE = "PERSON";
	public static final String GROUP_TYPE = "PARTY_GROUP";
	public static final String INVOICE_ITEM_TYPE_PAYROL_EARN_HOURS = "PAYROL_EARN_HOURS";
	public static final List<String> ROLE_TYPE_FULL_PERMS = FastList.newInstance();
	public static final List<String> ROLE_TYPE_FULL_PERMS_ACT = FastList.newInstance();
	public static final List<String> MANAGER_SENIOR_ROLE = UtilMisc.toList("GENERAL_DIRECTOR", "OPERATION_MANAGER", "CHIEF_EXECUTIVE");
	public static final String KPI_TURN_OVER = "KPI_TURNOVER_SALE";
	public static final String KPI_SKU = "KPI_SKU_SALE";
	public static final String APPR_ACCEPT = "ACCEPT";
	public static final String APPR_REJECT= "REJECT";
	public static final String ACC_MANAGER_ROLE = "ACC_MANAGER_EMP";
	
	static{
		ROLE_TYPE_FULL_PERMS.add("CHIEF_EXECUTIVE");
		ROLE_TYPE_FULL_PERMS.add("HRMADMIN");
		ROLE_TYPE_FULL_PERMS.add("GENERAL_DIRECTOR");
		ROLE_TYPE_FULL_PERMS.add("OPERATION_MANAGER");
		ROLE_TYPE_FULL_PERMS.add("SALARY_SPEC");
		ROLE_TYPE_FULL_PERMS.add("HR_MANAGER");
		ROLE_TYPE_FULL_PERMS.add("RECRUITMENT_SPEC");
	}
	static{
		ROLE_TYPE_FULL_PERMS_ACT.add("CHIEF_EXECUTIVE");
		ROLE_TYPE_FULL_PERMS_ACT.add("HRMADMIN");
		ROLE_TYPE_FULL_PERMS_ACT.add("GENERAL_DIRECTOR");
		ROLE_TYPE_FULL_PERMS_ACT.add("HR_MANAGER");
	}
	public static String getProperty(String key, Locale locale){
		return UtilProperties.getMessage(RESOURCE_NOTI, key, locale);
	}
	/**
	 * Role of Director
	 */
	public static final String DIRECTOR_ROLE = "CHIEF_EXECUTIVE";
	/**
	 * Role of Director
	 */
	public static final String GENERAL_DIRECTOR_ROLE = "GENERAL_DIRECTOR";
	/**
	 * Role of Employee
	 */
	public static final String EMPL_ROLE = "EMPLOYEE";
	/**
	 * Role of Head of department
	 */
	public static final String MANAGER_ROLE = "MANAGER";
	/**
	 * Role of Human Resource Manager
	 */
	public static final String HRM_ROLE = "HR_MANAGER";
	/**
	 * Role of Human recruitment special
	 */
	public static final String HRM_REC_SPEC = "RECRUITMENT_SPEC";
	/**
	 * Role of Internal Organization
	 */
	public static final String ORG_ROLE = "INTERNAL_ORGANIZATIO";
	/**
	 * Role of ASM employee
	 */
	public static final String ASM_ROLE = "ASM_EMPL";
	public static final String ASM_GT_DEPT_ROLE = "ASM_DEPT_GT";
	public static final String ASM_MT_DEPT_ROLE = "ASM_DEPT_MT";
	
	/**
	 * Role of RSM employee
	 */
	public static final String RSM_ROLE = "RSM_EMPL";
	public static final String RSM_GT_DEPT_ROLE = "RSM_DEPT_GT";
	public static final String RSM_MT_DEPT_ROLE = "RSM_DEPT_MT";
	
	/**
	 * Role of CSM employee
	 */
	public static final String CSM_ROLE = "CSM_EMPL";
	
	public static final String CSM_GT_DEPT_ROLE = "CSM_DEPT_GT";
	public static final String CSM_MT_DEPT_ROLE = "CSM_DEPT_MT";
	
	/**
	 * Role of NBD
	 */
	public static final String NBD_ROLE = "DELYS_NBD";
	
	public static final String SALES_DEPT_ROLE = "SALES_DEPARTMENT";
	
	/**
	 * Role of SALESUP employee
	 */
	public static final String SUP_ROLE = "SALESSUP_EMPL";
	
}
