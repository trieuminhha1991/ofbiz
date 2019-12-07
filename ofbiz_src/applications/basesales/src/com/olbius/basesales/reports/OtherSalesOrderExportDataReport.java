package com.olbius.basesales.reports;

import java.util.Date;
import java.util.Map;

import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class OtherSalesOrderExportDataReport extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("orderStatusId", (String) context.get("orderStatusId"));
		putParameter("fileName", "OtherSalesOrderExportDataReport"); // cache the specific file
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
		
		query.select("SOF.order_id")
			.select("SOF.order_item_seq_id")
			.select("SOF.order_name")
			.select("SOF.order_date")
			.select("SOF.currency_uom")
			.select("SOF.product_store_id")
			.select("SOF.product_store_name")
			.select("SOF.tax_amount")
			.select("SOF.discount_amount")
			.select("SOF.sub_total_amount")
			.select("SOF.total_amount")
			.select("SOF.product_id")
			.select("SOF.product_name")
			.select("SOF.primary_product_category_id")
			.select("SOF.quantity")
			.select("SOF.cancel_quantity")
			.select("SOF.return_quantity")
			.select("SOF.total_quantity")
			.select("SOF.total_selected_amount")
			.select("SOF.unit_price")
			.select("SOF.quantity_uom_id")
			.select("SOF.return_id")
			.select("SOF.return_price")
			.select("SOF.product_avg_cost")
			.select("SOF.sales_method_channel_enum_id")
			.select("SOF.sales_channel_enum_id")
			.select("SOF.order_status_id")
			.select("SOF.order_item_status_id")
			.select("SOF.creator_id")
			.select("SOF.sgc_customer_id")
			.from("sales_order_export_data", "SOF")
			.where(Condition.makeBetween("SOF.order_date", getFromDate(), getThruDate()));
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("order_id", "order_id");
		addDataField("order_item_seq_id", "order_item_seq_id");
		addDataField("order_name", "order_name");
		addDataField("order_date", "order_date");
		addDataField("currency_uom", "currency_uom");
		addDataField("product_store_id", "product_store_id");
		addDataField("product_store_name", "product_store_name");
		addDataField("tax_amount", "tax_amount");
		addDataField("discount_amount", "discount_amount");
		addDataField("sub_total_amount", "sub_total_amount");
		addDataField("total_amount", "total_amount");
		addDataField("product_id", "product_id");
		addDataField("product_name", "product_name");
		addDataField("primary_product_category_id", "primary_product_category_id");
		addDataField("quantity", "quantity");
		addDataField("cancel_quantity", "cancel_quantity");
		addDataField("return_quantity", "return_quantity");
		addDataField("total_quantity", "total_quantity");
		addDataField("total_selected_amount", "total_selected_amount");
		addDataField("unit_price", "unit_price");
		addDataField("quantity_uom_id", "quantity_uom_id");
		addDataField("return_id", "return_id");
		addDataField("return_price", "return_price");
		addDataField("product_avg_cost", "product_avg_cost");
		addDataField("sales_method_channel_enum_id", "sales_method_channel_enum_id");
		addDataField("sales_channel_enum_id", "sales_channel_enum_id");
		addDataField("order_status_id", "order_status_id");
		addDataField("order_item_status_id", "order_item_status_id");
		addDataField("creator_id", "creator_id");
		addDataField("sgc_customer_id", "sgc_customer_id");
	}
	
}
