package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Case;
import com.olbius.bi.olap.query.function.Max;
import com.olbius.bi.olap.query.function.Min;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.util.FacilityUtil;

import javolution.util.FastList;

public class InventoryTurnoverRatio extends OlbiusOlapService{


	private OlbiusQuery query;
	private ReturnResultGridFacility result = new ReturnResultGridFacility();
	private GenericValue userLogin = null;
	private Security security = null;
	private String module = InventoryReport.class.getName();

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));

		putParameter("dateType", context.get("dateType"));

		putParameter("group", context.get("group[]"));

		putParameter("facility", context.get("facility[]"));

		userLogin = (GenericValue) context.get("userLogin");
		security = dctx.getSecurity();
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private List<Object> getFacilities(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = FacilityDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}

	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		OlbiusQuery tmp = makeQuery();
		OlbiusQuery tmp2 = makeQuery();

		List<Object> facilities = getFacilities((List<?>) getParameter("facility"));
		if (facilities.isEmpty()){
			List<String> listFacilityIds = FastList.newInstance();
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

			Boolean hasRole = false;
			if (com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "LOG_REPORT")){
				hasRole = true;
			}
			if (!hasRole){
				hasRole = com.olbius.basehr.util.SecurityUtil.hasRole("ACC_MANAGER_EMP", userLogin.getString("partyId"), delegator);
			}
			if (hasRole){ 
				List<GenericValue> listFas = FastList.newInstance();
				try {
					listFas = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", company), null, null, null, false);
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return null;
				}
				if (!listFas.isEmpty()){
					listFacilityIds = EntityUtil.getFieldListFromEntityList(listFas, "facilityId", true);
				}
			} else  { 
				listFacilityIds = FacilityUtil.getFacilityManages(delegator, userLogin);
			}
			
			facilities = getFacilities(listFacilityIds);
		}
		if (facilities.isEmpty()) return query;
		
		String dateType = getDateType((String) getParameter("dateType"));

		String slt = "facility_dim_id, product_dim_id";
		boolean flag = true;

		tmp.select("inventory_date_dim_id").from("inventory_item_fact", "iif")

				.select(new Sum("iif.inventory_type ='RECEIVE' AND iif.inventory_change_type= 'PURCHASE_ORDER'",
						"(CASE WHEN prd.require_amount = 'Y' THEN iif.amount_on_hand_total * iif.unit_cost ELSE iif.quantity_on_hand_total * iif.unit_cost END)",
						"0"), "cogs")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = iif.product_dim_id")
				.where(Condition
						.makeBetween("iif.inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andIn("iif.facility_dim_id", facilities)
						.andEQ("prd.product_type", "FINISHED_GOOD"))
				.groupBy("iif.inventory_date_dim_id");
		
		tmp.select(slt, flag).groupBy(slt, flag);
		query.select(dateType).select(new Sum("iif.cogs"), "cogs")
				.from(tmp, "iif")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "iif.inventory_date_dim_id = dd.dimension_id")
				.groupBy(dateType);
		query.select(slt, flag).groupBy(slt, flag);
		
		tmp = makeQuery();
		tmp.select("fp.product_dim_id").select("fp.facility_dim_id").from("product_facility_fact", "fp").where(Condition.makeIn("fp.facility_dim_id", facilities))
		.groupBy(slt, flag);
		
		tmp2 = makeQuery();
		tmp2.select("x.facility_dim_id").select("x.product_dim_id").select("y.".concat(dateType)).select("y.cogs")
		.from(tmp, "x")
		.join(Join.LEFT_OUTER_JOIN, query, "y", "x.facility_dim_id = y.facility_dim_id and x.product_dim_id = y.product_dim_id");
		
		tmp = makeQuery();
		tmp.from(tmp2, "ref")
				.extend(makeQuery().distinct().select(dateType).from("date_dimension").where(
						Condition.makeBetween("dimension_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))),
						"ddex", true);

		tmp.select("ddex.".concat(dateType))
				.select(new Case().when("ref.".concat(dateType).concat(" = ddex.").concat(dateType), "ref.cogs")
						.els("0"), "cogs");
		tmp.select(slt, flag);
		
		query = makeQuery();
		query.from(tmp, "query");
		query.select(dateType).select("sum(cogs)", "cogs");
		query.select(slt, flag).groupBy(slt, flag);
		query.groupBy(dateType);

		tmp = makeQuery();

		tmp.select("tmp.*").from(query, "tmp");

		tmp.select("fd.facility_code", "fd.facility_id", "fd.facility_name", "party_dimension.party_id")
				.select("party_dimension.name", "partyName");
		tmp.join(Join.INNER_JOIN, "facility_dimension", "fd", "fd.dimension_id = tmp.facility_dim_id");
		tmp.join(Join.LEFT_OUTER_JOIN, "party_dimension",
				"party_dimension.dimension_id = fd.owner_party_dim_id");
		tmp.select("prd.product_id").select("prd.product_name").select("prd.product_code")
		.select("CASE WHEN prd.require_amount = 'Y' THEN prd.weight_uom_dim_id ELSE prd.uom_dim_id END as uom_id");
		tmp.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = tmp.product_dim_id");
		tmp.where(Condition.make("tmp.".concat(dateType).concat(" is not null")).andEQ("prd.product_type", "FINISHED_GOOD"));
		
		query = makeQuery();
		query.select("tmp.facility_id")
		.select("tmp.party_id")
		.select("tmp.facility_code")
		.select("tmp.facility_dim_id")
		.select("tmp.product_dim_id")
		.select("tmp.cogs")
		.select("tmp.".concat(dateType))
		.select("tmp.partyName")
		.select("tmp.product_code")
		.select("tmp.product_id")
		.select("tmp.product_name")
		.select("tmp.facility_name")
		.from(tmp, "tmp");
		query.select("ud.currency_id", "uom_id").select("ud.description", "uom_name");
		query.join(Join.INNER_JOIN, "currency_dimension", "ud", "ud.dimension_id = tmp.uom_id");
		query.orderBy("tmp.facility_dim_id");
		query.orderBy("tmp.product_dim_id");
		query.orderBy(dateType);
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("cogs", "cogs");
		addDataField("inventoryEnd");
		addDataField("inventoryBegin");
		addDataField("turnoverRatio");
		addDataField("facility", "facility_name");
		addDataField("product", "product_name");
		addDataField("facility_id", "facility_id");
		addDataField("facility_code", "facility_code");
		addDataField("product_code", "product_code");
		addDataField("uom", "uom_name");
		result.fromDate = getSqlTime(getFromDate());
		result.thruDate = getSqlTime(getThruDate());
	}

	@Override
	protected OlapResultQueryInterface returnResultGrid() {
		return result;
	}

	private class ReturnResultGridFacility extends ReturnResultGridEx {

		private long fromDate;
		private long thruDate;

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = super.getObject(result);

			try {
				String dateType = getDateType((String) getParameter("dateType"));
				Object tmp = map.get("dateTime");
				BigDecimal cogs = (BigDecimal)map.get("cogs");
				OlbiusQuery query = makeQuery();
				BigDecimal turnoverRatio = BigDecimal.ZERO; 

				query.select(new Min("date_value"), "min").select(new Max("date_value"), "max").from("date_dimension")
						.where(Condition.makeEQ(dateType, tmp).andBetween("dimension_id", fromDate, thruDate));

				java.sql.Date min = null;
				java.sql.Date max = null;

				ResultSet resultSet = null;
				try {
					resultSet = query.getResultSet();
					if (resultSet.next()) {
						min = resultSet.getDate("min");
						max = resultSet.getDate("max");
					}
				} finally {
					if (resultSet != null) {
						resultSet.close();
					}
				}

				OlbiusQuery queryMax = makeQuery();

				queryMax.select("ff.*")
						.from("product_facility_fact", "ff")
						.join(Join.INNER_JOIN, "product_dimension", "prd", "ff.product_dim_id = prd.dimension_id")
						.where(Condition.make("ff.date_dim_id", Condition.LESS_EQ, getSqlTime(getSqlThruDate(max))))
						.orderBy("ff.date_dim_id", OlbiusQuery.DESC).limit(1);

				OlbiusQuery queryMin = makeQuery();

				queryMin.select("ff.*")
						.from("product_facility_fact", "ff")
						.join(Join.INNER_JOIN, "product_dimension", "prd", "ff.product_dim_id = prd.dimension_id")
						.where(Condition.make("date_dim_id", Condition.LESS_EQ, getSqlTime(getSqlFromDate(min))))
						.orderBy("ff.date_dim_id", OlbiusQuery.DESC).limit(1);

						queryMax.where().andEQ("facility_dim_id", result.getLong("facility_dim_id"));
						queryMin.where().andEQ("facility_dim_id", result.getLong("facility_dim_id"));
						queryMax.where().andEQ("product_dim_id", result.getLong("product_dim_id"));
						queryMin.where().andEQ("product_dim_id", result.getLong("product_dim_id"));

				BigDecimal inventoryEnd = BigDecimal.ZERO;
				try {
					resultSet = makeQuery().select(new Sum("quantity"), "total").select("average_cost").from(queryMax, "tmp")
							.getResultSet();
					if (resultSet.next()) {
						
						BigDecimal quantityEnd = BigDecimal.ZERO;
						BigDecimal averageCostEnd = BigDecimal.ZERO;
						if (UtilValidate.isNotEmpty(resultSet.getBigDecimal("total"))) {
							quantityEnd = resultSet.getBigDecimal("total");
						}
						if (UtilValidate.isNotEmpty(resultSet.getBigDecimal("average_cost"))) {
							averageCostEnd = resultSet.getBigDecimal("average_cost");
						}
						inventoryEnd = averageCostEnd.multiply(quantityEnd);
						map.put("inventoryEnd", inventoryEnd);
					} else {
						map.put("inventoryEnd", 0);
					}
				} finally {
					if (resultSet != null) {
						resultSet.close();
					}
				}
				
				BigDecimal inventoryBegin = BigDecimal.ZERO;
				try {
					resultSet = makeQuery().select(new Sum("quantity"), "total").select("average_cost").from(queryMin, "tmp")
							.getResultSet();
					if (resultSet.next()) {
						
						BigDecimal quantityBegin = BigDecimal.ZERO;
						BigDecimal averageCostBegin = BigDecimal.ZERO;
						if (UtilValidate.isNotEmpty(resultSet.getBigDecimal("total"))) {
							quantityBegin = resultSet.getBigDecimal("total");
						}
						if (UtilValidate.isNotEmpty(resultSet.getBigDecimal("average_cost"))) {
							averageCostBegin = resultSet.getBigDecimal("average_cost");
						}
						inventoryBegin = averageCostBegin.multiply(quantityBegin);
						map.put("inventoryBegin", inventoryBegin);
					} else {
						map.put("inventoryBegin", 0);
					}
				} finally {
					if (resultSet != null) {
						resultSet.close();
					}
				}
				if (inventoryEnd.compareTo(BigDecimal.ZERO) != 0) {
					turnoverRatio = cogs.divide((inventoryBegin.add(inventoryEnd)), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(2));
				}
				map.put("turnoverRatio", turnoverRatio);
			} catch (Exception e) {
				Debug.logError(e, getModule());
			}

			return map;
		}

	}
}
