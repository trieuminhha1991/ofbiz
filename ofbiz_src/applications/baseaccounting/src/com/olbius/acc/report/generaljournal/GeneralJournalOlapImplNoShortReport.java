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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GeneralJournalOlapImplNoShortReport extends OlbiusOlapService {

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
		
		String dateType = getDateType((String) getParameter("dateType"));
		String organizationPartyId = (String) getParameter("organizationId");
		if (dateType.equals("year_month_day")) dateType = "transaction_date";
		
		query.select(dateType)
			.select("acctg_trans_id")
			.select("document_date")
			.select("document_id")
			.select("voucher_code")
			.select("document_number")
			.select("party_id")
			.select("party_code")
			.select("party_name")
			.select("description")
			.select("acctg_trans_type_id")
			.select("account_code")
			.select("dr_amount")
			.select("cr_amount")
			.select("account_name")
			.select("currency_id")
			.select("account_recip_code")
			.select("account_recip_name")
			.select("order_id")
			.select("return_id")
			.select("product_code")
			.select("product_name")
			.select("sales_method_channel_name")
			.select("facility_id")
			.select("facility_name")
			.from("acctg_document_list_fact")
            .orderBy("document_date", "DESC")
			.orderBy("acctg_trans_id", "DESC")
			.orderBy("acctg_trans_entry_seq_id")
			.where(Condition.makeBetween("transaction_date", getFromDate(), getThruDate())
							.andEQ("organization_party_id", organizationPartyId));
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		if (dateType.equals("year_month_day")) {
			addDataField("dateTime", "transaction_date", new ReturnResultCallback<String>() {
				@Override
				public String get(Object object) {
					if(UtilValidate.isNotEmpty(object)){
						SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
						java.sql.Date time = (java.sql.Date)object;
						return sp.format(time);
					}
					return null;
				}
			});
		} else {
			addDataField("dateTime", dateType);	
		}
			
		addDataField("documentDate", "document_date", new ReturnResultCallback<String>() {
			@Override
			public String get(Object object) {
				if(UtilValidate.isNotEmpty(object)){
					SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
					Timestamp time = (Timestamp)object;
					return sp.format(time);
				}
				return null;
			}
		});
		addDataField("acctgTransId", "acctg_trans_id");
		addDataField("documentId", "document_id");
		addDataField("voucherCode", "voucher_code");
		addDataField("documentNumber", "document_number");
		addDataField("partyId", "party_id");
		addDataField("partyCode", "party_code");
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
		addDataField("orderId", "order_id");
		addDataField("returnId", "return_id");
		addDataField("productCode", "product_code");
		addDataField("productName", "product_name");
		addDataField("salesMethodChannelName", "sales_method_channel_name");
		addDataField("productStoreId", "facility_id");
		addDataField("productStoreName", "facility_name");
	}
}