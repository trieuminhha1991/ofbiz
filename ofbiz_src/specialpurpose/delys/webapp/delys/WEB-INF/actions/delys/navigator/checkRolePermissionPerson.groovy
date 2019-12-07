import java.util.*;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.util.SalesPartyUtil;
import com.olbius.util.SecurityUtil;

boolean isSalesSup = false;
boolean isAsm = false;
boolean isSalesAdmin = false;
boolean isRsm = false;
boolean isNbd = false;
boolean isCeo = false;
boolean isDistributor = false;
boolean isChiefAccountant = false;
boolean isLog = false;
boolean isPrivateOrg = false; // user is employee of organization, can view private message

if (SalesPartyUtil.isSupervisorEmployee(userLogin, delegator)) {
	isSalesSup = true;
	isPrivateOrg = true;
}
if (SalesPartyUtil.isAsmEmployee(userLogin, delegator)) {
	isAsm = true;
	isPrivateOrg = true;
}
if (SalesPartyUtil.isSalesAdminEmployee(userLogin, delegator)) {
	isSalesAdmin = true;
	isPrivateOrg = true;
}
if (SalesPartyUtil.isRsmEmployee(userLogin, delegator)) {
	isRsm = true;
	isPrivateOrg = true;
}
if (SalesPartyUtil.isNbdEmployee(userLogin, delegator)) {
	isNbd = true;
	isPrivateOrg = true;
}
if (SalesPartyUtil.isCeoEmployee(userLogin, delegator)) {
	isCeo = true;
	isPrivateOrg = true;
}
if (SalesPartyUtil.isDistributor(userLogin, delegator)) {
	isDistributor = true;
}
if (SalesPartyUtil.isChiefAccoutantEmployee(userLogin, delegator)) {
	isChiefAccountant = true;
	isPrivateOrg = true;
}
if (SalesPartyUtil.isLogStoreKeeper(userLogin, delegator) || SalesPartyUtil.isLogSpecialist(userLogin, delegator)) {
	isLog = true;
	isPrivateOrg = true;
}

context.isPrivateOrg = isPrivateOrg;
context.isSalesSup = isSalesSup;
context.isAsm = isAsm;
context.isSalesAdmin = isSalesAdmin;
context.isRsm = isRsm;
context.isNbd = isNbd;
context.isCeo = isCeo;
context.isDistributor = isDistributor;
context.isChiefAccountant = isChiefAccountant;
context.isLog = isLog;
/*if(context.isSalesAdmin == true){
    context.selectedMenuItem = "order";
    context.selectedSubMenuItem = "orderList";
}
if(context.isDistributor == true){
    context.selectedMenuItem = "purchaseOrderDis";
    context.selectedSubMenuItem = "distributorListPO";
}*/