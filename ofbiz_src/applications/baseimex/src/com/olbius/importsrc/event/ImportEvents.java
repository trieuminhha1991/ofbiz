package com.olbius.importsrc.event;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 12/26/18.
 */
public class ImportEvents {

    public static String calculateTempTax(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        BigDecimal totalTax = BigDecimal.ZERO;
        String billId = request.getParameter("billId");
        String costAccBaseId = request.getParameter("costAccBaseId");
        try {
            List<GenericValue> packingLists = delegator.findList("PackingListHeaderDetail", EntityCondition.makeCondition(UtilMisc.toMap("billId", billId)), UtilMisc.toSet("packingListId"), null, null, false);
            List<String> packingListIds = EntityUtil.getFieldListFromEntityList(packingLists, "packingListId", true);
			List<GenericValue> productTmps = delegator.findList("PackingListDetailAndOrderAndProduct", EntityCondition.makeCondition("packingListId", EntityOperator.IN, packingListIds), null, null, null, false);
			GenericValue costAccBase = delegator.findOne("CostAccBase", UtilMisc.toMap("costAccBaseId", costAccBaseId), false);
			String invoiceItemTypeId = costAccBase.getString("invoiceItemTypeId");
			for(GenericValue product : productTmps) {
			    BigDecimal quantity = product.getBigDecimal("quantity");
			    String productId = product.getString("productId");
			    BigDecimal unitPrice = product.getBigDecimal("unitPrice");
			    List<EntityCondition> conds = FastList.newInstance();
			    conds.add(EntityCondition.makeCondition("productId", productId));
			    conds.add(EntityUtil.getFilterByDateExpr());
			    if("PINV_IMPTAX_ITEM".equals(invoiceItemTypeId)) {
                    conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.LIKE, "TAX_IMP_%"));
                }
                else {
                    conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.LIKE, "TAX_EXCISE_%"));
                }
                GenericValue productCategoryMember = EntityUtil.getFirst(delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conds), null, null, null, false));
			    if(UtilValidate.isEmpty(productCategoryMember)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCProductNotHaveImportTax", UtilMisc.toMap("productCode", product.get("productCode")),locale));
                    return "error";
                }
			    String productCategoryId = productCategoryMember.getString("productCategoryId");
			    GenericValue rate = EntityUtil.getFirst(delegator.findList("TaxAuthorityRateProduct", EntityCondition.makeCondition("productCategoryId", productCategoryId), null, null, null, false));
			    BigDecimal percentage = rate.getBigDecimal("taxPercentage");
			    totalTax = totalTax.add(quantity.multiply(percentage).multiply(unitPrice).divide(new BigDecimal(100)));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        }
        request.setAttribute("totalTax", totalTax);
        request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
        request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
        return "success";
    }
}
