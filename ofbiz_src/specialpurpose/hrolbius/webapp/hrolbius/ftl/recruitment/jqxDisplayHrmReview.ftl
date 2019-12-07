<div class="row-fluid" >
	<div class="span12">
		<ol type="I">
			<li><h6><b>${uiLabelMap.proposal}</h6></b></li>
			<div id="disHrmResultId"></div>
			<li><h6><b>${uiLabelMap.extTime}</h6></b></li>
			<div id="disHrmExtTime"></div>
			<li><h6><b>${uiLabelMap.comment}</h6></b></li>
			<div id="disHrmComment"></div>
		</ol>  
	</div>
</div>
<script>
	<#assign mapCondition = Static["org.ofbiz.base.util.UtilMisc"].toMap("offerProbationId", parameters.offerProbationId, "roleTypeId", "HRMADMIN")>
	<#assign probReview = delegator.findList("ProbationaryReviewView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(mapCondition), null, null, null, false)>
	var resultId = $('<textarea />').html('${probReview[0].resultId?if_exists}').text();
	for(var i = 0; i < reviewResultData.length; i++){
		if('${probReview[0].resultId?if_exists}' == reviewResultData[i].resultId){
			resultId = $('<textarea />').html(reviewResultData[i].description).text();
		}
	}
	$("#disHrmResultId").append(resultId);
	$("#disHrmExtTime").html('${probReview[0].extTime?if_exists}');
	$("#disHrmComment").html('${probReview[0].comment?if_exists}');
</script>