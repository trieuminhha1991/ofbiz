<div class="widget-body">
	<input type="hidden" name="ntfId" value="${ntfId}"/>
	<table class="table table-bordered dataTable" style="margin-top: 10px;">
		<tr style="background-color: #f5f5f5;">
      		<th>${uiLabelMap.PageTitleStandardRating}</th>
      		<th>${uiLabelMap.jobRequirement}</th>
      		<th>${uiLabelMap.jobIntent}</th>
     	 	<th style="text-align: center;">${uiLabelMap.jobTime}</th>
      		<th style="text-align: center;">${uiLabelMap.jobWeight}</th>
      		<th style="text-align: center;">${uiLabelMap.MarkRating}</th>
      		<th style="text-align: center;">${uiLabelMap.ResultRating}</th>
    	</tr>
    	<#assign total = 0 />
    	<#list standardRatings as standardRating>
    	<#assign entity1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("standardRatingId", standardRating.get("standardRatingId",locale)) />
		<#assign entity2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ratingType", "Leader-Rating") />
		<#assign entity3 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("employeePartyId", employeePartyId ) />
		<#assign entity = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND , entity1, entity2, entity3) />
		<#assign listMarkRating = delegator.findList("ListMarkRatingEmployee", entity, null, null, null, false)?if_exists />
    	<#assign jobRatingList = delegator.findList("ListJobRating", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("standardRatingId", standardRating.get("standardRatingId",locale)), null, null, null, false)?if_exists />
    	<#list jobRatingList as jobRating>
    	<#assign firstJobRatingId = jobRatingList.get(0).jobRatingId>
    	<tr>
    		<#if jobRating.jobRatingId == firstJobRatingId>
    			<td style="border:none; border-left: 1px solid #ddd; border-top: 1px solid #ddd;">
    				${(standardRating.standardName)?if_exists}
    			</td>
    			<#else><td style="border:none;border-left: 1px solid #ddd;"></td>
    		</#if>
    	    <td>${(jobRating.jobRequirement)?if_exists}</td>
    		<td>${(jobRating.jobIntent)?if_exists}</td>
    		<#assign timeJobRating = delegator.findList("TimeJobRating", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("timeJobRatingId", jobRating.get("jobTime",locale)), null, null, null, false) />
    		<td style="width:80px; text-align: center;">${(timeJobRating.get(0).description)?if_exists}</td>
    		<#if jobRating.jobRatingId == firstJobRatingId>
    			<td style="border:none; border-left: 1px solid #ddd; border-top: 1px solid #ddd; text-align: center;" class="jobWeight${standardRating_index}" >
    				${(standardRating.weight)?if_exists}
    			</td>
    		<#else><td style="border:none;border-left: 1px solid #ddd;"></td>
    		</#if>
    		<#if jobRating.jobRatingId == firstJobRatingId>
    			<td style="border:none; border-left: 1px solid #ddd; border-top: 1px solid #ddd; text-align: center;">
    				${listMarkRating.get(0).jobMark}
    			</td>
    		<#else><td style="border:none;border-left: 1px solid #ddd;"></td>
    		</#if>
    		<#if jobRating.jobRatingId == firstJobRatingId>
    			<td style="width:50px;" id="result" style="border:none; border-left: 1px solid #ddd; border-top: 1px solid #ddd; text-align: center;">
    				${listMarkRating.get(0).jobResult}
					<#assign rs = listMarkRating.get(0).jobResult />
    				<#assign total = total + Static["java.lang.Float"].parseFloat(rs)/>
    			</td>
    		<#else><td style="border:none;border-left: 1px solid #ddd;"></td>
    		</#if>
    	</tr>
    	</#list>
    	</#list>
    	<tr style="background-color: #f5f5f5;">
      		<th>${uiLabelMap.PageTitleTotal}</th>
      		<th></th>
      		<th></th>
     	 	<th></th>
      		<th style="text-align: center;">100%</th>
      		<th></th>
      		<th class="total" style="text-align: center;">${total}</th>
    	</tr>
	</table>
</div> 