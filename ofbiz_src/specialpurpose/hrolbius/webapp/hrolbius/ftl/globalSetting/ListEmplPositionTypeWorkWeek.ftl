	<#assign filterByDateCond = Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr()>
	<table class="table table-hover table-striped table-bordered dataTable" >
		<thead>
			<tr>
				<td>
					${uiLabelMap.EmplPositionTypeId}
				</td>
				<#list dayOfWeek as day>
					<td>${day.description}</td>
				</#list>
				<td></td>
			</tr>
		</thead>
		<tbody>
			<#list emplPositionTypes as positionType>
				<tr>
					<td>
						<#if positionType.description?has_content>
							${positionType.description?if_exists}
						<#else>
							${positionType.emplPositionTypeId}
						</#if>
					</td>
					<#list dayOfWeek as day>
						<#assign condition1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("emplPositionTypeId", positionType.emplPositionTypeId)>
						<#assign condition2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("dayOfWeek", day.dayOfWeek)>
						<#assign conditions = [filterByDateCond, condition1, condition2]>
						<#assign workShiftList = delegator.findList("EmplPositionTypeWorkWeekAndShift", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditions, Static["org.ofbiz.entity.condition.EntityOperator"].AND), null, null, null, false)>
						<#--<!-- <#assign workShiftListStr = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(workShiftList, "workingShiftId", true)> -->
						
						<td>
							<#if workShiftList?has_content>
								<#list workShiftList as tempShift>
									<#if (tempShift_index < workShiftList?size - 1)>	
										${tempShift.description},
									</#if> 	
								</#list>
								${workShiftList.get(workShiftList?size - 1).description?if_exists}
							<#else>
								<p style="text-align: center;">
									&#45;
								</p>								
							</#if>
						</td>
					</#list>
					<td>
						<a class="btn btn-mini btn-primary" href="<@ofbizUrl>editEmplPositionTypeWorkWeek?emplPositionTypeId=${positionType.emplPositionTypeId}</@ofbizUrl>">
							<i class="icon-edit"></i>
						</a>
					</td>
				</tr>
			</#list>
		</tbody>
	</table>
