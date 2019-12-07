<#if emplList?has_content>
<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#assign commonUrl = "EmplTimekeepingReport?" + paramList/>
<#assign viewIndexFirst = 0/>
<#assign viewIndexPrevious = viewIndex - 1/>
<#assign viewIndexNext = viewIndex + 1/>
<#assign viewIndexLast = Static["java.lang.Math"].floor(listSize/viewSize)/>
<#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount", lowIndex, "highCount", highIndex, "total", listSize)/>
<#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
<@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" 
   		paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" 
   		paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" 
   		ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" 
   		ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
<table class="table table-hover table-striped table-bordered dataTable" >
	<thead>
		<tr>
			<td>
				${uiLabelMap.EmployeeName}
			</td>
			<td>
				${uiLabelMap.EmployeeId}
			</td>
			<#list dateOfMonth as date>
				<td>${date?string["dd EEE"]}</td>
			</#list>
			<td>
				${uiLabelMap.TotalTimeKeeping}
			</td>
			<td>
				${uiLabelMap.TotalDayWorking}
			</td>
		</tr>
	</thead>
	<tbody>
		<#list emplList as empl>
			<tr>
				<#assign emplName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(empl)>
				<td>${emplName}</td>
				<td>${empl.partyId}</td>
				<#list dateOfMonth as date>
					<#assign resultMap = dispatcher.runSync("getEmplTimeKeepingInDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", empl.partyId, "dateKeeping", date))>
					<td data-rel="tooltip" data-trigger="hover" data-placement="bottom"  title="Loading..." getData="false" partyId="${empl.partyId}" dateKeeping="${date.getTime()}">${resultMap.statusAttendance?if_exists}</td>
				</#list>
				<td>
					<#assign emplPosTypes = Static["com.olbius.util.PartyUtil"].getCurrPositionTypeOfEmpl(delegator, empl.partyId)>
					<#assign dayWork = 0>
					<#list emplPosTypes as emplPosType>
						<#assign tempDayWork = timeKeepingByPosType.get(emplPosType.emplPositionTypeId)>
						<#if (tempDayWork > dayWork)>
							<#assign dayWork = tempDayWork> 
						</#if>
					</#list>
					${dayWork}
				</td>
				<td>
					<#assign emplDayWorking = dispatcher.runSync("getTimeKeepingEmplInPeriod", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", empl.partyId, "fromDate", startMonth,"thruDate", endMonth))>
					${emplDayWorking.countTimekeeping?if_exists}
				</td>
			</tr>
		</#list>
	</tbody>
</table>
<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />

<script type="text/javascript">
	jQuery(function() {
		jQuery('[data-rel=tooltip]').tooltip({
			container:'body',
		});
		$(document).on('hover', '[data-rel=tooltip]', function () { 
			//$(this).tooltip('show');
			//$(this).attr("title", "Loading...");
			var partyId = $(this).attr("partyId");
			var dateKeeping = $(this).attr("dateKeeping");
			var tooltip = $(this);
			var getData = jQuery(this).attr("getData");
			if(getData === "false"){
				jQuery.ajax({
					url: "<@ofbizUrl>getPartyAttendance</@ofbizUrl>",
					type:"POST",
					async: false,
					data: {partyId: partyId, dateKeeping: dateKeeping},
					success: function(data){
						tooltip.attr("data-original-title", data.partyDateKeepingInfo);	
						tooltip.tooltip('show');
						if(data._EVENT_MESSAGE_ == "success"){
							tooltip.attr("getData", "true");	
						}
					}
				});	
			}
			
			
		});
		$(document).on('blur', '[data-rel=tooltip]', function(){
			$(this).tooltip('hide');
			//$(this).attr("title", "");
		});
	});
</script>
</#if>