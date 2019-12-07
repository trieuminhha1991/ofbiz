import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
	
	String confirm = null;
	List<GenericValue> listReceiptStatus = delegator.findList("ReceiptStatus", EntityCondition.makeCondition(UtilMisc.toMap("receiptId", receiptId)), null, null, null, false);
	List<String> listStatus = new ArrayList<String>();
	for (GenericValue status : listReceiptStatus){
		if(!listStatus.contains((String)status.get("statusId"))){
			listStatus.add((String)status.get("statusId"));
		}
	}
	if (listStatus.contains("RECEIPT_INV_RECEIPT")){
		confirm = "RECEIPT_INV_RECEIPT";
	} else {
		if (listStatus.contains("RECEIPT_STK_ACCEPTED") && listStatus.contains("RECEIPT_DLV_ACCEPTED")){
			confirm = "STK_DLV_ACCEPTED";
		}
	}
	context.listReceiptStatus = listReceiptStatus;
	context.confirm = confirm;