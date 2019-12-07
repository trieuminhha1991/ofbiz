package com.olbius.salesman;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.webapp.website.WebSiteWorker;

import com.olbius.order.OrderReadHelper;
import com.olbius.order.ShoppingCartEvents;
import com.olbius.order.ShoppingCartHelper;

import org.ofbiz.marketing.tracking.TrackingCodeEvents;
import org.ofbiz.order.order.OrderServices;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.party.contact.*;

public class SalesmanService {
	
	public static final String module = SalesmanService.class.getName();
	
	public static Map<String, Object> fetchRouteByParty(DispatchContext ctx,
			   Map<String, Object> context) {
			  Map<String, Object> result = new FastMap<String, Object>();
			  String partyIdFrom = (String) context.get("partyIdFrom");
			  Delegator delegator = ctx.getDelegator();
			  List<GenericValue> getListRouteByParty = new ArrayList<GenericValue>();
			  Set<String> fieldToSelects = FastSet.newInstance();
			  fieldToSelects.add("partyIdFrom");
			  fieldToSelects.add("partyIdTo");
			  fieldToSelects.add("roleTypeIdFrom");
			  fieldToSelects.add("roleTypeIdTo");
			  try {
				  getListRouteByParty = delegator.findList("PartyRelationship",
			     EntityCondition.makeCondition(UtilMisc.toMap(
			       "partyIdFrom", partyIdFrom)), fieldToSelects,
			     null, null, false);

			  } catch (GenericEntityException e) {
			   Debug.logError(e, module);
			   return ServiceUtil.returnError(e.getMessage());
			  }
			  
			  result.put("getListRouteAndCustomer", getListRouteByParty);

			  return result;
			 }
	
}
