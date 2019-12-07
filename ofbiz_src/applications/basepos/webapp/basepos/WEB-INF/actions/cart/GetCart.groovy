import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.basepos.session.WebPosSession;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.ofbiz.product.store.ProductStoreWorker;

import javolution.util.FastList;
HttpSession session = request.getSession(true);
WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");

if(UtilValidate.isNotEmpty(webposSession)){
	productStoreId = ProductStoreWorker.getProductStoreId(request);
	productStore = delegator.findOne("ProductStore", [productStoreId : productStoreId], false);
	showPricesWithVatTax = "Y";
	if (productStore){
		showPricesWithVatTax = productStore.showPricesWithVatTax;
	}
	context.showPricesWithVatTax = showPricesWithVatTax;
}