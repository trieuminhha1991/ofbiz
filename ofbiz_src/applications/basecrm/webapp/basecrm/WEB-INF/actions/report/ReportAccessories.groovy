/*
 * ReportAccessories - The Accessories for Olbius Report
 */

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.util.SalesPartyUtil;

Map<String, Object> LocalData = FastMap.newInstance();

/*
 * ReportAccessories - marketingCampaigns
 */
def dummy = delegator.findByAnd("MarketingCampaign", null, null, true);
def marketingCampaigns = "[{ text: '" + UtilProperties.getMessage("OlapUiLabels", "olap_all", locale) + "', value: 'all' },";
def flag = false;
for (value in dummy) {
	if (flag) {
		marketingCampaigns += ",";
	}
	marketingCampaigns += "{ value: " + "\'" + value.get("marketingCampaignId") + "\'" + ", text: " + "\'" + value.get("campaignName", locale) + "\'" + " }";
	flag = true;
}
marketingCampaigns += "]";
LocalData.marketingCampaigns = marketingCampaigns;

/*
 * ReportAccessories - employeeCallCenter
 */
dummy = delegator.findByAnd("EmployeeCallCenter", null, null, true);
def employeeCallCenter = "[{ text: '" + UtilProperties.getMessage("OlapUiLabels", "olap_all", locale) + "', value: 'all' },";
flag = false;
for (value in dummy) {
	if (flag) {
		employeeCallCenter += ",";
	}
	employeeCallCenter += "{ value: " + "\'" + value.get("partyId") + "\'" + ", text: " + "\'" + value.get("partyName") + "\'" + " }";
	flag = true;
}
employeeCallCenter += "]";
LocalData.employeeCallCenter = employeeCallCenter;


context.LocalData = LocalData;