<script type="text/javascript">
	var showOrderStatusGlobalObject = (function(){
		var orderStatusList = new Array();
		<#assign orderStatusList = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_STATUS"), null, null, null, false) /> 
		<#if orderStatusList?exists><#list orderStatusList as status >
			var row = {};
			row["statusId"] = "${status.statusId?if_exists}";
			row["description"] = "${status.get("description", locale)?if_exists}";
			orderStatusList[${status_index}] = row;
		</#list></#if>
		var showDescriptionStatus = function(statusId){
		 	var description = statusId;
			for(i = 0; i < orderStatusList.length; i++){
				if(orderStatusList[i].statusId == statusId){
					return "<span>" + orderStatusList[i].description + "</span>"
				}
			}
		};
		return {
			orderStatusList : orderStatusList,
			showDescriptionStatus: showDescriptionStatus
		}
	}());
</script>