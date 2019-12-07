<div class="widget-body">
	<form method="post" name="ratingEmployee" action="<@ofbizUrl>ratingEmployee</@ofbizUrl>">	 
	<input type="hidden" name="employeePartyId" value="${employeePartyId}" />
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
    	<#list standardRatings as standardRating>
    	<#assign jobRatingList = delegator.findList("ListJobRating", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("standardRatingId", standardRating.get("standardRatingId",locale)), null, null, null, false)?if_exists />
    	<#list jobRatingList as jobRating>
    	<#assign firstJobRatingId = jobRatingList.get(0).jobRatingId>
    	<tr>
    		<#if jobRating.jobRatingId == firstJobRatingId>
    			<td style="border:none; border-left: 1px solid #ddd; border-top: 1px solid #ddd;">
    				<input type="hidden" name="standardRatingId" value="${(standardRating.standardRatingId)?if_exists}" />
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
    			<td style="border:none; border-left: 1px solid #ddd; border-top: 1px solid #ddd;">
    				<input data-id="${standardRating_index}" type="number" name="jobMark" min="0" max="10" style="width: 50px; text-align: center;" required/>
    			</td>
    		<#else><td style="border:none;border-left: 1px solid #ddd;"></td>
    		</#if>
    		<#if jobRating.jobRatingId == firstJobRatingId>
    			<td style="width:50px;" id="result" style="border:none; border-left: 1px solid #ddd; border-top: 1px solid #ddd; text-align: center;">
    				<span class="result${standardRating_index}"></span>
    				<input type="hidden" name="jobResult" class="resultHidden${standardRating_index}" />
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
      		<th><input type="button" name="button" value="Total" style="text-align: center;"/></th>
      		<th class="total" style="text-align: center;"></th>
    	</tr>
	</table>
	<button name="submit" class="btn btn-small btn-primary" style="margin-top: 10px;">
		<i class="icon-ok open-sans">
		<#if employeePartyId==partyId >
			${uiLabelMap.EmplPerfReviewRating}
		<#else> ${uiLabelMap.PerfReviewRating} </#if>
		</i>
	</button>
	</form>
</div> 
<script type="text/javascript">
    $(document).ready(function(){
    	$("input[name='jobMark']").change(function(){
    		var mark = $(this).val();
    		var id = $(this).attr('data-id');
        	var jobWeight = $(".jobWeight"+id).text();
    		var result = mark*jobWeight/100;
    		$(".result"+id).text(result);
    		$(".resultHidden"+id).val(result);
		});
	
		$("input[type='button']").click(function(){
			var list = $("td[id='result']");
			var total = 0;
			for (var i=0; i < list.length; i++){
				total = total + parseFloat($(list[i]).text());
			}
			if(total){
				$(".total").text(parseFloat(total).toFixed(2));
			} else alert("Error!! Please enter all fields");
		});
    })
</script>