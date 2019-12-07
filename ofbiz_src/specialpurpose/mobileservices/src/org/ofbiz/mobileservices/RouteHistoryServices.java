package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.Mobile;
import org.ofbiz.ProcessMobileApps;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.DateTimeConverters;
import org.ofbiz.base.conversion.DateTimeConverters.TimestampToString;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.customer.Customer;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.service.SalesmanServices;

public class RouteHistoryServices implements Mobile {

	public static final String module = RouteHistoryServices.class.getName();

	public static Map<String, Object> createRouteHistory(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		Long frD = (Long) context.get("fromDateLong");
		Long thD = (Long) context.get("thruDateLong");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String partyIdFrom = (String) context.get("partyIdFrom");
		Double latitude = (Double) context.get("latitude");
		Double longitude = (Double) context.get("longitude");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		if(UtilValidate.isEmpty(partyIdFrom)){
			partyIdFrom = userLogin.getString("partyId");
			context.put("partyIdFrom", partyIdFrom);
		}
		try{
			if(UtilValidate.isEmpty(fromDate)){
				if(UtilValidate.isNotEmpty(frD)){
					fromDate = new Timestamp(frD);
				}else{
					fromDate = UtilDateTime.nowTimestamp();
				}
				context.put("fromDate", fromDate);
			}
			if(UtilValidate.isEmpty(thruDate) && UtilValidate.isNotEmpty(thD)){
				thruDate = new Timestamp(thD);
				context.put("thruDate", thruDate);
			}
			if(UtilValidate.isNotEmpty(latitude) && UtilValidate.isNotEmpty(longitude)){
				String geoPointId = MobileUtils.createGeoPoint(delegator, latitude, longitude);
				context.put("geoPointId", geoPointId);
			}
			GenericValue prmo = delegator.makeValidValue("RouteHistory", context);
			prmo.create();
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnSuccess();
		}
		return res;
	}
	
}
