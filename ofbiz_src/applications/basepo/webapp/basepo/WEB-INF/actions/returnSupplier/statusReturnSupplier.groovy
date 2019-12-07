import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;

listStatusItemReturnHeader = delegator.findList("StatusItem", EntityCondition.makeCondition(UtilMisc.toMap("statusTypeId", "PORDER_RETURN_STTS")), null, null, null, false);
context.listStatusItemReturnHeader = listStatusItemReturnHeader;
