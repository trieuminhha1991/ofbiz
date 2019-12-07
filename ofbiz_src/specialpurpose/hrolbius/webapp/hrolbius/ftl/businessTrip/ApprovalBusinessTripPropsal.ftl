<form name="ApprovalBussinessTrip" id="ApprovalBussinessTrip" method="post" 
	action="<@ofbizUrl>ApprovalBussinessTrip</@ofbizUrl>" class="basic-form form-horizontal">
	<input type="hidden" name="partyBusinessTripId" value="${partyBusinessTripId}"> 
	<input type="hidden" name="approverId" value="${userLogin.partyId}">
	<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
	<div class="row-fluid">
		<div class="control-group no-left-margin">
			<label>
				<label for="proposer" id="proposer_title">
					${uiLabelMap.HREmplProposer}
				</label>
			</label>
			<div class="controls">
				<span class="ui-widget">
					${employeeName?if_exists} [${emplId?if_exists}]
				</span>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label>
				<label>
					${uiLabelMap.EmployeeCurrentDept}
				</label>
			</label>
			<div class="controls">	
				${currDept?if_exists}
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label>
				<label>
					${uiLabelMap.HREmplApprovalCurrStatus}
				</label>
			</label>
			<div class="controls">	
				<#assign statusItem = delegator.findOne("StatusItem", {"statusId": partyBusinessTrip.statusId}, false)>
				${statusItem.description}
			</div>
		</div>
		<#list businessTripApproval as tempApproval>
			<div class="control-group no-left-margin">
				<label>
					<label>
						${uiLabelMap.HRApprover}
					</label>
				</label>
				<div class="controls">	
					<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, tempApproval.partyId, false)>
					${partyName}
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label>
					<label>
						${uiLabelMap.EmplProposalCheck}
					</label>
				</label>
				<div class="controls">	
					<#assign statusItem = delegator.findOne("StatusItem", {"statusId": tempApproval.approvalStatusId}, false)>
					${statusItem.description}
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label>
					<label>
						${uiLabelMap.ApprovalDate}
					</label>
				</label>
				<div class="controls">	
					${tempApproval.approvalDate?string["dd/MM/yyyy"]}
				</div>
			</div>
			<#if tempApproval.comments?has_content>
				<div class="control-group no-left-margin">
					<label>
						<label>
							${uiLabelMap.HRNotes}
						</label>
					</label>
					<div class="controls">	
						${tempApproval.comments}
					</div>
				</div>
			</#if>
		</#list>
		
		<#if checkMap.isPermisson>
			<div class="control-group no-left-margin">
				<label>
					<label>
						${uiLabelMap.CommonStatus}
					</label>
				</label>
				<div class="controls">
					<select name="statusId">
						<#list statusItemList as item>    
							<option value="${item.statusId}">${item.description}</option>
						</#list>
					</select>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label>
					<label>
						${uiLabelMap.HRNotes}
					</label>
				</label>
				<div class="controls">
					<textarea rows="4" cols="3" name="notes"></textarea>
				</div>
			</div>		
		</#if>
		<div class="space-20"></div>
		<h4>I. ${uiLabelMap.BussinessTripPlan}</h4>
		<table class="table table-striped table-hover table-bordered dataTable">
			<thead>
				<tr>
					<th rowspan="2"><label>${uiLabelMap.BussinessTripContent}</label></th>
					<th rowspan="2"><label>${uiLabelMap.BussinessDest}</label> </th>
					<th rowspan="1" align="center" style="text-align: center;" colspan="2"><label>${uiLabelMap.TimeWorking}</label> </th>
					<th rowspan="2"><label>${uiLabelMap.HRNotes}</label> </th>
				</tr>
				<tr>
					<th align="center" style="text-align: center;">${uiLabelMap.CommonFromDate}</th>
					<th align="center" style="text-align: center;">${uiLabelMap.CommonThruDate}</th>
				</tr>
			</thead>
			<tbody>
				<#list businessTripPlanList as tripPlan>
					<tr>
						<td>
							${tripPlan.businessTripContent}
						</td>							
						<td>
							${tripPlan.businessTripDest}
						</td>
						<td>
							${tripPlan.fromDate?string["dd/MM/yyyy HH:mm"]}
						</td>
						<td>
							${tripPlan.thruDate?string["dd/MM/yyyy HH:mm"]}
						</td>
						<td>
							${tripPlan.notes?if_exists}
						</td>
					</tr>
				</#list>
			</tbody>
		</table>
		
		<div class="space-10"></div>
		
		<h4>II. ${uiLabelMap.VerhicleRegis}</h4>
		<table class="table table-striped table-hover table-bordered dataTable">
			<thead>
				<tr>
					<th rowspan="2"><label>${uiLabelMap.Vehicle}</label> </th>
					<th rowspan="2"><label>${uiLabelMap.JourneyBussiness}</label></th>
					<th rowspan="1" style="text-align: center;" align="center" colspan="2"><label>${uiLabelMap.CommonTime}</label></th>
					<th rowspan="2"><label>${uiLabelMap.HRNotes}</label> </th>
				</tr>
				<tr>
					<th align="center" style="text-align: center;"><label>${uiLabelMap.DateDeparture}</label> </th>
					<th align="center" style="text-align: center;"><label>${uiLabelMap.DateArrival}</label> </th>
				</tr>
			</thead>
			<tbody>
				<#list businessTripVehicleList as tripVehicle>
					<tr>
						<td>
							<#assign vehicleType = delegator.findOne("VehicleType",{"vehicleTypeId": tripVehicle.vehicleTypeId} , false)>
							${vehicleType.description}
						</td>
						<td>
							${tripVehicle.journey}
						</td>
						<td>
							${tripVehicle.fromDate?string["dd/MM/yyyy HH:mm"]}
						</td>
						<td>
							${tripVehicle.thruDate?string["dd/MM/yyyy HH:mm"]}
						</td>
						<td>
							${tripVehicle.notes?if_exists}
						</td>
					</tr>
				</#list>
			</tbody>
		</table>
		
		<#if checkMap.isPermisson>
			<div class="form-actions" style="padding-left: 0; text-align: center; margin-top: 30px">
				    <button type="submit" class="btn btn-small btn-info" name="submitButton">
				    	<i class="icon-ok"></i>
				    	${uiLabelMap.CommonSubmit}
				    </button>
			</div>
		</#if>	
</div>
</form>