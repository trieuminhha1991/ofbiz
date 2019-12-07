import java.sql.Date;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;

import org.ofbiz.base.util.UtilValidate;

if(parameters.id){
	campaign = delegator.findOne("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", parameters.id), false)
	if(campaign != null){
		thruDate = campaign.getTimestamp('thruDate');
		if(thruDate){
			long currentMillis = System.currentTimeMillis();
			Date currentDate = Date.valueOf(new Date(currentMillis).toString());
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
			if(currentTimestamp > thruDate){
				context.isThruDate = true;
			}
		}
		context.campaign = campaign;
	}
}
