import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.basehr.util.MultiOrganizationUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilValidate;

GenericValue userLogin = (GenericValue)context.get("userLogin");
String companyStr = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

//get supplier party
List<GenericValue> listFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "WAREHOUSE", 
	"ownerPartyId", companyStr)), null, null, null, false);

//get config uom
List<GenericValue> listUom = delegator.findList("Uom", EntityCondition.makeCondition(UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false);

//get reason
listReturnReasons = delegator.findList("ReturnReason", null, null, null, null, false);

//return supplier
String returnId = parameters.returnId;
GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
String fromPartyId = "";
if(returnHeader != null){
	fromPartyId = returnHeader.getString("fromPartyId");
}

String org = "N";
if(companyStr.equals(fromPartyId)){
	org = "Y";
}

context.org = org;
context.listFacility = listFacility;
context.listUom = listUom;
context.listReturnReasons = listReturnReasons;