package com.olbius.salesmtl.report.route;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.salesmtl.SupervisorServices;

import javolution.util.FastMap;

public class RouteServices {

	public static Map<String, Object> routeHistory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			RouteHistory grid = new RouteHistory(delegator);
			grid.setOlapResultType(OlapGrid.class);
			String department = null;
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String levelId = (String) context.get("levelId");
			if (SalesPartyUtil.isDistributor(delegator, userLogin.getString("partyId"))) {
				if (UtilValidate.isNotEmpty(levelId)) {
					department = RouteHistory.SALESMAN;
				} else {
					department = RouteHistory.DIS;
				}
				grid.putParameter(RouteHistory.AGENTS, SupervisorServices.agentsOfDistributor(delegator, userLogin.getString("partyId")));
			} else {
				List<String> currentDepartments = PartyUtil.getDepartmentOfEmployee(delegator,
						userLogin.getString("partyId"), UtilDateTime.nowTimestamp());
				String currentDepartment = null;
				if (UtilValidate.isNotEmpty(currentDepartments)) {
					currentDepartment = currentDepartments.get(0);
				}
				grid.putParameter(RouteHistory.DEPARTMENTID, currentDepartment);
				if (UtilValidate.isNotEmpty(levelId)) {
					if (levelId.contains(RouteHistory.SUP)) {
						department = RouteHistory.SALESMAN;
					} else if (levelId.contains(RouteHistory.ASM)) {
						department = RouteHistory.SUP;
					} else if (levelId.contains(RouteHistory.RSM)) {
						department = RouteHistory.ASM;
					} else if (levelId.contains(RouteHistory.CSM)) {
						department = RouteHistory.RSM;
					} else if (levelId.contains(RouteHistory.DSA)) {
						department = RouteHistory.CSM;
					}
				} else {
					levelId = currentDepartment;
					if (levelId.contains(RouteHistory.SUP)) {
						department = RouteHistory.SUP;
					} else if (levelId.contains(RouteHistory.RSM)) {
						department = RouteHistory.RSM;
					} else if (levelId.contains(RouteHistory.RSM)) {
						department = RouteHistory.RSM;
					} else if (levelId.contains(RouteHistory.CSM)) {
						department = RouteHistory.CSM;
					} else if (levelId.contains(RouteHistory.DSA)) {
						department = RouteHistory.DSA;
					} else {
						department = RouteHistory.SALESMAN;
					}
				}
				if (UtilValidate.isNotEmpty(currentDepartment)) {
					String userDepartment = null;
					if (currentDepartment.contains(RouteHistory.SUP)) {
						userDepartment = RouteHistory.SUP;
					} else if (currentDepartment.contains(RouteHistory.RSM)) {
						userDepartment = RouteHistory.RSM;
					} else if (currentDepartment.contains(RouteHistory.RSM)) {
						userDepartment = RouteHistory.RSM;
					} else if (currentDepartment.contains(RouteHistory.CSM)) {
						userDepartment = RouteHistory.CSM;
					} else if (currentDepartment.contains(RouteHistory.DSA)) {
						userDepartment = RouteHistory.DSA;
					} else {
						userDepartment = RouteHistory.SALESMAN;
					}
					grid.putParameter(RouteHistory.USERDEPARTMENT, userDepartment);
				}
			}
			
			grid.setFromDate((Date) context.get("fromDate"));
			grid.setThruDate((Date) context.get("thruDate"));
			grid.putParameter(RouteHistory.DEPARTMENT, department);
			grid.putParameter(RouteHistory.PARTIES, context.get("parties[]"));
			result = grid.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
