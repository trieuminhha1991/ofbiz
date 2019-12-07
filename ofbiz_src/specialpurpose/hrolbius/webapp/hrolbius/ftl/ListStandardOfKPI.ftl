<div class="widget-body">
	<table class="table table-bordered dataTable" style="margin-top: 10px;">
		<tr style="background-color: #f5f5f5;">
      		<th>${uiLabelMap.PageTitleStandardRating}</th>
      		<th>${uiLabelMap.jobRequirement}</th>
      		<th>${uiLabelMap.jobIntent}</th>
     	 	<th style="text-align: center;">${uiLabelMap.jobTime}</th>
      		<th style="text-align: center;">${uiLabelMap.jobWeight}</th>
    	</tr>
    	<#list standardRatings as standardRating>
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
    			<td style="border:none; border-left: 1px solid #ddd; border-top: 1px solid #ddd; text-align: center;" class="jobWeight" >
    				${(standardRating.weight)?if_exists}
    			</td>
    		<#else><td style="border:none;border-left: 1px solid #ddd;"></td>
    		</#if>
    	</tr>
    	</#list>
    	</#list>
	</table>
</div>