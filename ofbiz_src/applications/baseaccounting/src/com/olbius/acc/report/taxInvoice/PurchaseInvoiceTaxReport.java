package com.olbius.acc.report.taxInvoice;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class PurchaseInvoiceTaxReport extends OlbiusOlapService{
	private static final String invoiceType = "PURCHASE_INVOICE";
	private OlbiusQuery query;
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("month", (String) context.get("month"));
		putParameter("quarter", (String) context.get("quarter"));
		putParameter("year", (String) context.get("year"));
		putParameter("calendarType", (String) context.get("calendarType"));
		putParameter("locale", context.get("locale"));
		putParameter("timeZone", context.get("timeZone"));
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
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		fromCal.set(Calendar.DATE, 1);
		toCal.set(Calendar.DATE, 1);
		String calendarType = (String) getParameter("calendarType");
		Locale locale = (Locale)getParameter("locale");
		TimeZone timeZone = (TimeZone)getParameter("timeZone");
		if(calendarType != null){
			String monthStr = (String) getParameter("month");
			String quarterStr = (String) getParameter("quarter");
			String yearStr = (String) getParameter("year");
			switch (calendarType) {
			case "MONTH":
				if(monthStr != null && monthStr.trim().length() > 0){
					fromCal.set(Calendar.MONTH, Integer.parseInt(monthStr));
					toCal.set(Calendar.MONTH, Integer.parseInt(monthStr));
				}
				break;
			case "QUARTER":
				if(quarterStr != null && quarterStr.trim().length() > 0){
					int quarter = Integer.parseInt(quarterStr);
					fromCal.set(Calendar.MONTH, quarter * 3 - 2);
					toCal.set(Calendar.MONTH, quarter * 3);
				}
				break;
			case "YEAR":
				fromCal.set(Calendar.MONTH, 0);
				toCal.set(Calendar.MONTH, 11);
				break;
			default:
				break;
			}
			if(yearStr != null && yearStr.trim().length() > 0){
				fromCal.set(Calendar.YEAR, Integer.parseInt(yearStr));
				toCal.set(Calendar.YEAR, Integer.parseInt(yearStr));
			}
		}
		Timestamp fromDate = UtilDateTime.getMonthStart(new Timestamp(fromCal.getTimeInMillis()));
		Timestamp thruDate = UtilDateTime.getMonthEnd(new Timestamp(toCal.getTimeInMillis()), timeZone, locale);
		OlbiusQuery invoiceTypeQuery = new OlbiusQuery();
		invoiceTypeQuery.select("rel.dimension_id", "dimension_id")
		.from("invoice_type_relationship", "rel")
		.join(Join.INNER_JOIN, "invoice_type_dimension", "type", "rel.parent_dim_id = type.dimension_id and type.invoice_type_id = '" + invoiceType + "'");
		Condition condition = Condition.makeBetween("invoice_date_dim_id", getSqlTime(fromDate), getSqlTime(thruDate));
		query.select("tax.*")
		.select("case when tax_dim.tax_percentage is not null then tax_dim.tax_percentage else 0 end", "tax_percentage")
		.select("currency.currency_id")
		.from("invoice_tax_fact", "tax")
		.join(Join.INNER_JOIN, "tax_auth_rate_prod_dimension", "tax_dim", "tax.tax_auth_rate_prod_dim_id = tax_dim.dimension_id")
		.join(Join.INNER_JOIN, "currency_dimension", "currency", "currency.dimension_id = tax.currency_uom_dim_id")
		.join(Join.INNER_JOIN, invoiceTypeQuery, "type", "type.dimension_id = tax.invoice_type_dim_id")
		.where(condition);
		return query;
	}
	
	@Override
	public void prepareResultGrid() {
		/*String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);*/
		addDataField("invoice_id", "invoice_id");
		addDataField("invoice_date", "invoice_date");
		addDataField("party_name", "party_name");
		addDataField("tax_code", "tax_code");
		addDataField("amount", "amount");
		addDataField("tax_amount", "tax_amount");
		addDataField("tax_percentage", "tax_percentage");
		addDataField("currency_id", "currency_id");
	}

}
