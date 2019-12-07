import java.util.*;
import java.lang.*;
import org.ofbiz.base.util.UtilMisc;

orderId = parameters.orderId;

def orderContentPOs = delegator.findByAnd("OrderContentPaymentOrder" ,UtilMisc.toMap("orderId", orderId), null, false);
List<String> paymentOrderList = new ArrayList<String>();
for (orderContentPOItem in orderContentPOs) {
	def contents = delegator.findByAnd("Content" ,UtilMisc.toMap("contentId", orderContentPOItem.get("contentId")), null, false);
	for (contentItem in contents) {
		def dataResources = delegator.findByAnd("DataResource" ,UtilMisc.toMap("dataResourceId", contentItem.get("dataResourceId")), null, false);
		for (dataResource in dataResources) {
			paymentOrderList.add(dataResource);
		}
	}
}

context.paymentOrderList = paymentOrderList;