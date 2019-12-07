package com.olbius.activemq.receive;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import com.olbius.activemq.receive.handle.AbstractOlbiusReceiveEvent;
import com.olbius.jms.data.MessageData;
import com.olbius.jms.data.Product;
import com.olbius.jms.data.SupplierProduct;

/**
 * @author Nguyen Ha
 *
 */
public class GetSupplier extends AbstractOlbiusReceiveEvent {

	public void insert(SupplierProduct supplierProduct) throws GenericServiceException, GenericEntityException {

		createSupplier(supplierProduct.getGroup().getOwnId());
		
		for (Product product : supplierProduct.getProducts()) {

			GenericValue genericValue = delegator.findOne("SupplierProduct",
					UtilMisc.toMap(UtilMisc.toMap("partyId", supplierProduct.getGroup().getOwnId(), "productId",
							product.getOwnId(), "currencyUomId", product.getCurrencyUom().getOwnId(),
							"minimumOrderQuantity", product.getMessageData().get("minimumOrderQuantity"),
							"availableFromDate",
							new Timestamp((long) product.getMessageData().get("availableFromDate")))),
					false);

			Map<String, Object> input = new HashMap<String, Object>();

			input.put("userLogin", userLogin);

			input.put("supplierPrefOrderId", "10_MAIN_SUPPL");

			input.put("quantityUomId", product.getQuantityUom().getOwnId());
			input.put("availableThruDate", product.getMessageData().get("availableThruDate") != null
					? new Timestamp((long) product.getMessageData().get("availableThruDate")) : null);
			input.put("lastPrice", product.getMessageData().get("lastPrice"));

			if (genericValue == null) {
				input.put("partyId", supplierProduct.getGroup().getOwnId());
				input.put("productId", product.getOwnId());
				input.put("currencyUomId", product.getCurrencyUom().getOwnId());
				input.put("minimumOrderQuantity", product.getMessageData().get("minimumOrderQuantity"));
				input.put("availableFromDate", new Timestamp((long) product.getMessageData().get("availableFromDate")));
				dispatcher.runSync("createSupplierProduct", input);
			} else {
				input.put("partyId", genericValue.get("partyId"));
				input.put("productId", genericValue.get("productId"));
				input.put("currencyUomId", genericValue.get("currencyUomId"));
				input.put("minimumOrderQuantity", genericValue.get("minimumOrderQuantity"));
				input.put("availableFromDate", genericValue.get("availableFromDate"));
				dispatcher.runSync("updateSupplierProduct", input);
			}

			GenericValue value = EntityUtil
					.getFirst(EntityUtil.filterByDate(delegator.findByAnd("ProductCategoryMember",
							UtilMisc.toMap("productCategoryId", "BROWSE_ROOT", "productId", product.getOwnId()), null,
							false)));
			if (value == null) {
				value = delegator.makeValue("ProductCategoryMember");
				value.set("productCategoryId", "BROWSE_ROOT");
				value.set("productId", product.getOwnId());
				value.set("fromDate", new Timestamp(System.currentTimeMillis()));
				delegator.createOrStore(value);
			}

		}
	}

	@Override
	public void receiveHandle(MessageData messageData) throws GenericServiceException, GenericEntityException {
		SupplierProduct supplierProduct = (SupplierProduct) messageData;
		insert(supplierProduct);
	}

	private void createSupplier(String partyId) throws GenericServiceException {
		
		String[] tmp = new String[]{"SUPPLIER", "SUPPLIER_AGENT", "SHIP_FROM_VENDOR", "BILL_FROM_VENDOR"};
		
		Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId);
		
		for(String s : tmp) {
			context.put("roleTypeId", s);
			dispatcher.runSync("createPartyRole", context);
		}

		String partyIdFrom = "company";
		String partyIdTo = partyId;
		String roleTypeIdFrom = "INTERNAL_ORGANIZATIO";
		String roleTypeIdTo = "SUPPLIER";
		String partyRelationShipTypeId = "SUPPLIER_REL";
		Timestamp nowDate = UtilDateTime.nowTimestamp();

		Map<String, Object> contextInputPartyRelationShip = UtilMisc.toMap("userLogin", userLogin, "partyIdFrom",
				partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo,
				"fromDate", nowDate, "partyRelationShipTypeId", partyRelationShipTypeId);
		dispatcher.runSync("createPartyRelationship", contextInputPartyRelationShip);
	}

}
