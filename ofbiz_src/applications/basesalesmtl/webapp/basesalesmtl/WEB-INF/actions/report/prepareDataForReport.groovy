import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator; 
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;

def partyId = userLogin.getString("partyId");

def dummy = delegator.findByAnd("Product", UtilMisc.toMap("productTypeId", "FINISHED_GOOD"), null, false);
def products = "[";
def flag = false;
for(value in dummy) {
    if(flag) {
        products += ",";
    }
    products += "{ productId: " + "\'" + value.get("productId") + "\'" + ", productCode: " + "\'" + value.get("productCode") + "\'" + ", productName: " + "\'" + value.get("productName") + "\'" + " }";
    flag = true;
}
products += "]";
context.products = products;


def conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "CATALOG_CATEGORY")));
conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.NOT_EQUAL, "BROWSE_ROOT"));
dummy = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions), null, null, null, false);
def categories = "[";
flag = false;
for(value in dummy) {
    if(flag) {
    	categories += ",";
    }
    categories += "{ productCategoryId: " + "\'" + value.get("productCategoryId") + "\'" + ", categoryName: " + "\'" + value.get("categoryName") + "\'" + " }";
    flag = true;
}
categories += "]";
context.categories = categories;



def conditions2 = FastList.newInstance();
String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
conditions2.add(EntityCondition.makeCondition(UtilMisc.toMap("payToPartyId", organization)));
dummy = delegator.findList("ProductStore", EntityCondition.makeCondition(conditions2), null, null, null, false);
def salesChannel = "[";
flag = false;
if(dummy != null){
	for(value in dummy) {
	    if(flag) {
	    	salesChannel += ",";
	    }
	    salesChannel += "{ productStoreId: " + "\'" + value.get("productStoreId") + "\'" + ", storeName: " + "\'" + value.get("storeName") + "\'" + " }";
	    flag = true;
	}
}
salesChannel += "]";
context.salesChannel = salesChannel;


def conditions3 = FastList.newInstance();
conditions3.add(EntityCondition.makeCondition(UtilMisc.toMap("enumTypeId", "SALES_METHOD_CHANNEL")));
dummy = delegator.findList("Enumeration", EntityCondition.makeCondition(conditions3), null, null, null, false);
def channelType = "[";
flag = false;
if(dummy != null){
	for(value in dummy) {
	    if(flag) {
	    	channelType += ",";
	    }
	    channelType += "{ enumId: " + "\'" + value.get("enumId") + "\'" + ", description: " + "\'" + value.get("description") + "\'" + " }";
	    flag = true;
	}
}
channelType += "]";
context.channelType = channelType;


def conditions4 = FastList.newInstance();
conditions4.add(EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", "PARTY_GROUP_CUSTOMER")));
dummy = delegator.findList("PartyType", EntityCondition.makeCondition(conditions4), null, null, null, false);
def customerType = "[";
flag = false;
if(dummy != null){
	for(value in dummy) {
	    if(flag) {
	    	customerType += ",";
	    }
	    customerType += "{ partyTypeId: " + "\'" + value.get("partyTypeId") + "\'" + ", description: " + "\'" + value.get("description") + "\'" + " }";
	    flag = true;
	}
}
customerType += "]";
context.customerType = customerType;


def conditions5 = FastList.newInstance();
conditions5.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "PARENT_ORGANIZATION")));
conditions5.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdTo", "SUBSIDIARY")));
dummy = delegator.findList("PartyToAndPartyNameDetail", EntityCondition.makeCondition(conditions5), null, null, null, false);
def region = "[";
flag = false;
if(dummy != null){
	for(value in dummy) {
	    if(flag) {
	    	region += ",";
	    }
	    region += "{ partyId: " + "\'" + value.get("partyId") + "\'" + ", description: " + "\'" + value.get("description") + "\'" + " }";
	    flag = true;
	}
}
region += "]";
context.region = region;

def conditions6 = FastList.newInstance();
conditions6.add(EntityCondition.makeCondition("statusTypeId", "ORDER_STATUS"));
conditions6.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_HOLD"));
conditions6.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
conditions6.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
conditions6.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_SADAPPROVED"));
dummy = delegator.findList("StatusItem", EntityCondition.makeCondition(conditions6), null, null, null, false);
def orderStatus1 = "[";
flag = false;
if(dummy != null){
	for(value in dummy) {
	    if(flag) {
	    	orderStatus1 += ",";
	    }
	    orderStatus1 += "{ statusId: " + "\'" + value.get("statusId") + "\'" + ", description: " + "\'" + value.get("description") + "\'" + " }";
	    flag = true;
	}
}
orderStatus1 += "]";
context.orderStatus1 = orderStatus1;
