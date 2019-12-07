import java.util.ArrayList;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.administration.*;
import java.util.Map;
import com.olbius.basehr.util.MultiOrganizationUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
	
GenericValue userLogin = (GenericValue)context.get("userLogin");
String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
GenericValue partyCompany = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", company), false);
String companyName = partyCompany.getString("groupName");
BrandLogo brLogo = new BrandLogo(delegator, company);
String logo = brLogo.getBase64();

String companyAddress = "";
List<GenericValue> listCtms = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", company, "contactMechPurposeTypeId", "PRIMARY_LOCATION")), null, null, null, false);
listCtms = EntityUtil.filterByDate(listCtms);
if (!listCtms.isEmpty()) {
	GenericValue ctm = delegator.findOne("PostalAddressDetail", UtilMisc.toMap("contactMechId", listCtms.get(0).get("contactMechId")), false);
	companyAddress = ctm.getString("fullName");
}

String taxIdCompany = "";
List<GenericValue> listTaxs = delegator.findList("PartyTaxAuthInfo", EntityCondition.makeCondition(UtilMisc.toMap("partyId", company)), null, null, null, false);
listTaxs = EntityUtil.filterByDate(listTaxs);
if (!listTaxs.isEmpty()) {
	taxIdCompany = listTaxs.get(0).getString("partyTaxId");
}


context.logo = logo;    
context.taxIdCompany = taxIdCompany;    
context.companyName = companyName;
context.companyAddress = companyAddress;