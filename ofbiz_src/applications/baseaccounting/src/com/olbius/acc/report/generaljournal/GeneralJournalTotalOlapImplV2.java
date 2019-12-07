package com.olbius.acc.report.generaljournal;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.services.OlbiusOlapService;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GeneralJournalTotalOlapImplV2 extends OlbiusOlapService {

	private OlbiusQuery query;
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationId;
		try {
			organizationId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			putParameter("organizationId", organizationId);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		String organizationPartyId = (String) getParameter("organizationId");
		
		query.select("cr_amount")
			.select("dr_amount")
			.select("transaction_date")
			.select("account_code").select("account_name")
			.select("account_recip_code")
			.select("account_recip_name")
			.select("currency_id")
			.select("organization_party_id")
			.select("organization_party_name")
			.select("acctg_trans_type_id")
			.select("party_id")
			.select("party_name")
			.select("delivery_id")
			.select("voucher_code")
			.select("facility_id")
			.select("facility_name")
			.select("description")
			.from("acctg_trans_sum_day_fact")
			.orderBy("acctg_sum_temp_id", "DESC")
			.where(Condition.makeBetween("transaction_date", getFromDate(), getThruDate())
							.andEQ("organization_party_id", organizationPartyId));
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("transactionDate", "transaction_date", new ReturnResultCallback<String>() {
			@Override
			public String get(Object object) {
				if(UtilValidate.isNotEmpty(object)){
					SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
					Date time = (Date)object;
					return sp.format(time);
				}
				return null;
			}
		});				
		addDataField("voucherCode", "voucher_code");
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
