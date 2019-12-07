package com.olbius.salesmtl.report.route;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.cache.dimension.PartyDimension;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;

public class RouteHistory extends OlbiusBuilder {

	public static final String DEPARTMENT = "DEPARTMENT";
	public static final String USERDEPARTMENT = "USERDEPARTMENT";
	public static final String DEPARTMENTID = "DEPARTMENTID";
	public static final String SALESMAN = "SALESMAN";
	public static final String DIS = "DIS";
	public static final String SUP = "SUP";
	public static final String ASM = "ASM";
	public static final String RSM = "RSM";
	public static final String CSM = "CSM";
	public static final String DSA = "DSA";
	public static final String PARTIES = "PARTIES";
	public static final String AGENTS = "AGENTS";

	public RouteHistory(Delegator delegator) {
		super(delegator);
	}

	@Override
	public void prepareResultGrid() {
		addDataField("party_name", "party_name");
		addDataField("party_id", "party_id");
		addDataField("party_code", "party_code");
		addDataField("party_id_to", "party_id_to");
		addDataField("route_id", "route_id");
		addDataField("_count", "_count");
		addDataField("_order", "_order");
		addDataField("_total", "_total");
	}

	private List<Object> getParties(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = PartyDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}

	private Object getParty(String partyId) {
		if (partyId != null) {
			return PartyDimension.D.getId(delegator, partyId);
		}
		return null;
	}

	private OlbiusQuery query;

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = initQuery();
		}
		return query;
	}

	private OlbiusQuery initQuery() {
		OlbiusQuery query1 = makeQuery();
		OlbiusQuery query2 = makeQuery();
		OlbiusQuery query = makeQuery();

		List<Object> parties = getParties((List<?>) getParameter(PARTIES));
		List<Object> agents = getParties((List<?>) getParameter(AGENTS));

		Object departmentId = getParty((String) getParameter(DEPARTMENTID));
		String department = (String) getParameter(DEPARTMENT);
		String userDepartment = (String) getParameter(USERDEPARTMENT);

		query2.select("sof.*").from("sales_order_fact", "sof")
				.where(Condition.makeBetween("sof.order_date", getFromDate(), getThruDate()).andEQ("sof.order_status",
						"ORDER_COMPLETED"));

		query1.distinct().select("pd1.name", "party_id_to").select("pd2.name", "route_id")
				.select("rhf.party_dim_id_from", "rhf.from_date", "rhf.thru_date", "sof.total", "sof.order_id")
				.from("route_history_fact", "rhf")
				.join(Join.INNER_JOIN, "party_dimension", "pd1", "pd1.dimension_id = rhf.party_dim_id_to")
				.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = rhf.route_dim_id")
				.join(Join.LEFT_OUTER_JOIN, query2, "sof",
						"sof.created_by = rhf.party_dim_id_from and sof.party_to_dim_id = rhf.party_dim_id_to")
				.where(Condition.makeBetween("rhf.from_date", getFromDate(), getThruDate())
						.andIn("rhf.party_dim_id_from", parties).andIn("rhf.party_dim_id_to", agents));

		query2 = makeQuery();

		query2.select(new Sum("temp.total"), "_total").select("COUNT(temp.order_id)", "_order")
				.select("COUNT(*)", "_count").select("temp.party_dim_id_from", "temp.party_id_to", "temp.route_id")
				.from(query1, "temp").groupBy("temp.party_dim_id_from", "temp.party_id_to", "temp.route_id");

		query.select("case when SUM(hado._total) <> 0 then SUM(hado._total) else 0 end", "_total")
				.select(new Sum("hado._order"), "_order").select(new Sum("hado._count"), "_count").select("pd.party_id")
				.select("pd.party_code")
				.select("COALESCE(pd.name, '') || ' ' || COALESCE(pd.last_name, '') || ' ' || COALESCE(pd.middle_name, '') || ' ' || COALESCE(pd.first_name, '')",
						"party_name")
				.select("null as party_id_to", !SALESMAN.equals(department))
				.select("null as route_id", !SALESMAN.equals(department))
				.select("hado.party_id_to", "party_id_to", SALESMAN.equals(department))
				.select("hado.route_id", "route_id", SALESMAN.equals(department)).from(query2, "hado")
				.join(Join.INNER_JOIN, "level_relationship", "lr", "lr.salesman_id = hado.party_dim_id_from")
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.salesman_id",
						SALESMAN.equals(department))
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.distributor",
						DIS.equals(department))
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.sup_dep", SUP.equals(department))
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.asm_dep", ASM.equals(department))
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.rsm_dep", RSM.equals(department))
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.csm_dep", CSM.equals(department))
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.dsa_dep", DSA.equals(department))
				.groupBy("pd.party_id", "pd.party_code", "pd.name", "pd.last_name", "pd.middle_name", "pd.first_name")
				.groupBy("party_id_to", SALESMAN.equals(department)).groupBy("route_id", SALESMAN.equals(department));
		if (departmentId != null) {
			query.where(getConditionByDepartment(userDepartment, departmentId));
		}
		return query;
	}

	private Condition getConditionByDepartment(String userDepartment, Object departmentId) {
		String field = null;
		switch (userDepartment) {
		case SALESMAN:
			field = "lr.salesman_id";
			break;
		case DIS:
			field = "lr.distributor";
			break;
		case SUP:
			field = "lr.sup_dep";
			break;
		case ASM:
			field = "lr.asm_dep";
			break;
		case RSM:
			field = "lr.rsm_dep";
			break;
		case CSM:
			field = "lr.csm_dep";
			break;
		case DSA:
			field = "lr.dsa_dep";
			break;
		default:
			break;
		}
		return Condition.makeEQ(field, departmentId);
	}
}
