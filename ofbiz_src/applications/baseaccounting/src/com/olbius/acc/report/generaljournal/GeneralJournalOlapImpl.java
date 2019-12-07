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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GeneralJournalOlapImpl extends OlbiusOlapService {
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
		query.select("*")
		.from("acctg_document_list_fact")
		.where(cond)
		.orderBy("transaction_date", "DESC")
		.orderBy("acctg_trans_entry_seq_id")
		.orderBy("reciprocal_seq_id");
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
		addDataField("documentId", "document_id");
		addDataField("voucherCode", "voucher_code");
		addDataField("documentNumber", "document_number");
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
		addDataField("orderId", "order_id");
		addDataField("returnId", "return_id");
		addDataField("productCode", "product_code");
		addDataField("productName", "product_name");
		addDataField("salesMethodChannelName", "sales_method_channel_name");
		addDataField("productStoreId", "facility_id");
	}
}
