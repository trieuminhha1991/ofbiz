package com.olbius.acc.report.generaljournal;

import com.olbius.acc.report.incomestatement.query.IncomeOlapConstant;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.services.OlbiusOlapService;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import java.util.Date;
import java.util.Map;

public class GeneralJournalTotalOlapImpl extends OlbiusOlapService {
	private OlbiusQuery query;
	protected String organizationPartyId;
	protected String dateType;
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		try {
			String organizationId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			putParameter("organizationId", organizationId);
			putParameter("dateType", (String) context.get(IncomeOlapConstant.DATATYPE));
			setFromDate((Date) context.get("fromDate"));
			setThruDate((Date) context.get("thruDate"));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}

	private void initQuery() {
		query = new OlbiusQuery(getSQLProcessor());
		getSqlTime(getFromDate());
		organizationPartyId = (String) getParameter("organizationId");
		Condition cond = Condition.make("organization_party_id", Condition.EQ, organizationPartyId);
		cond.and(Condition.makeBetween("transaction_date", getFromDate(), getThruDate()));
		query.select("sum(cr_amount)", "cr_amount").select("sum(dr_amount)", "dr_amount")
		.select("transaction_date").select("account_code").select("account_name")
		.select("account_recip_code").select("account_recip_name").select("currency_id")
		.select("organization_party_id").select("organization_party_name").select("acctg_trans_type_id")
		.select("party_id").select("party_name").select("delivery_id")
		.select("voucher_code").select("facility_id").select("facility_name")
		.select("description")
		.from("acctg_document_list_fact")
		.where(cond)
		.groupBy("transaction_date").groupBy("account_code").groupBy("account_name")
		.groupBy("account_recip_code").groupBy("account_recip_name").groupBy("currency_id")
		.groupBy("organization_party_id").groupBy("organization_party_name").groupBy("acctg_trans_type_id")
		.groupBy("party_id").groupBy("party_name").groupBy("delivery_id")
		.groupBy("voucher_code").groupBy("facility_id").groupBy("facility_name")
		.groupBy("description")
		.orderBy("transaction_date", "DESC");
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("transactionDate", "transaction_date", new ReturnResultCallback<Long>() {
			@Override
			public Long get(Object object) {
				if(UtilValidate.isNotEmpty(object)){
					Date time = (Date)object;
					return time.getTime();
				}
				return null;
			}
		});		
		addDataField("partyId", "party_id");
		addDataField("partyName", "party_name");
		addDataField("description", "description");
		addDataField("acctgTransTypeId", "acctg_trans_type_id");
		addDataField("accountCode", "account_code");
		addDataField("drAmount", "dr_amount");
		addDataField("crAmount", "cr_amount");
		addDataField("accountName", "account_name");
		addDataField("currencyId", "currency_id");
		addDataField("accountRecipCode", "account_recip_code");
		addDataField("accountRecipName", "account_recip_name");
		addDataField("deliveryId", "delivery_id");
		addDataField("facilityId", "facility_id");
		addDataField("facilityName", "facility_name");
	}
}
