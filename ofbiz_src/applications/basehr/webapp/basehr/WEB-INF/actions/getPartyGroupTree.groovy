import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.basehr.model.JqxTreeJson;
import com.olbius.basehr.organization.utils.OrganizationUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;

public static List<JqxTreeJson> getPartyGroupTree(Delegator delegator, Organization org){
	List<JqxTreeJson> retList = FastList.newInstance();
	List<Organization> childList = org.getChilListOrg();
	String parentId = org.getOrg().getString("partyId");
	for(Organization tempOrg: childList){
		String tempPartyId = tempOrg.getOrg().getString("partyId");
		if(PropertiesUtil.GROUP_TYPE.equals(tempOrg.getOrgType())){
			GenericValue tempPartyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", tempPartyId), false);
			if(tempPartyGroup != null){
				String tempGroupName = tempPartyGroup.getString("groupName");
				if(tempGroupName == null){
					tempGroupName = tempPartyId;
				}
				String iconUrl = OrganizationUtils.getPartyLogoImg(delegator, tempPartyId);
				retList.add(new JqxTreeJson(tempPartyId, tempGroupName, parentId, tempPartyId, iconUrl));
			}
		}
		retList.addAll(getPartyGroupTree(delegator, tempOrg));
		 
	}
	return retList;
}

List<String> rootCompanyList = FastList.newInstance();
if(PartyUtil.isFullPermissionView(delegator, userLogin.userLoginId)){
	String rootCompanyId = PartyUtil.getRootOrganization(delegator, userLogin.userLoginId);
	rootCompanyList.add(rootCompanyId);
}else{
	rootCompanyList = PartyUtil.getListOrgManagedByParty(delegator, userLogin.userLoginId);
}

List<JqxTreeJson> treePartyGroup = FastList.newInstance();

if(rootCompanyList != null){
	for(String rootCompanyId: rootCompanyList){
		Organization org = PartyUtil.buildOrg(delegator, rootCompanyId, true, false);
		GenericValue parentOrg = org.getOrg();
		String partyId = parentOrg.getString("partyId");
		GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
		if(partyGroup != null){
			String groupName = partyGroup.getString("groupName");
			if(groupName == null){
				groupName = partyId;
			}
			treePartyGroup.add(new JqxTreeJson(partyId, groupName, "-1", partyId));
			treePartyGroup.addAll(getPartyGroupTree(delegator, org));
		}
	}
	if(rootCompanyList.size() > 0){
		context.rootPartyId = rootCompanyList.get(0);
		List<String> expandedList = FastList.newInstance();
		expandedList.add(rootCompanyList.get(0));
		context.treePartyGroup = treePartyGroup;
		context.expandedList = expandedList;
		context.defaultPartyId = rootCompanyList.get(0);
	}
}
 