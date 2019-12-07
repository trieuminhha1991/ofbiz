<div id="inventoryItemHasBeenReservedForSalesOrder" style="display: none;">
	<div>
		${uiLabelMap.ProductHasBeenReservedForSomeSalesOrder}. 
	</div>
</div>
<div id="inventoryItemHasBeenExported" style="display: none;">
	<div>
		${uiLabelMap.ProductHasBeenExported}. 
	</div>
</div>
<div id="notifyUpdateSuccess" style="display: none;">
	<div>
		${uiLabelMap.NotifiUpdateSucess}. 
	</div>
</div>
<div id="containerNotify" style="width: 100%; overflow: auto;">
</div>
<div>
	<div class="rowfluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="span12">
			<div class='row-fluid'>
				<div class='span4'>
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right">${uiLabelMap.OrderReturnId} </span>
						</div>  
						<div class="span7">
							<div class="green-label">${returnId}</div>
				   		</div>
					</div>
				</div>
				<div class='span4'>
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right">${uiLabelMap.OrderCurrentStatus} </span>
						</div>  
						<div class="span7">
							<div id="statusId" class="green-label">${statusId}</div>
				   		</div>
					</div>
				</div>
				<div class='span4'>
					<div class='row-fluid'>
						<div class='span4'>
							<span style="float: right">${uiLabelMap.RequiredCompletedDate} </span>
						</div>  
						<div class="span8">
							<div id="entryDate" class="green-label"></div> 
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid'>
				<div class='span4'>
					<div class='row-fluid'>	
						<div class='span5'>
							<span style="float: right">${uiLabelMap.ReturnType} </span>
						</div>  
						<div class="span7">
							<div id="returnHeaderTypeId" class="green-label">${returnHeaderTypeDesc?if_exists}</div>
				   		</div>
			   		</div>
				</div>
				<div class='span4'>
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right">${uiLabelMap.ReturnFrom} </span>
						</div>  
						<div class="span7">
							<div id="fromPartyId" class="green-label">${fromPartyId} [${returnHeader.fromPartyId}]</div>
				   		</div>
			   		</div>
				</div>
				<div class='span4'>
					<div class='row-fluid'>
						<#if returnHeaderTypeId == 'CUSTOMER_RETURN'>
							<div class='span4'>
								<span style="float: right">${uiLabelMap.ReceiveDate} </span>
							</div>  
							<div class="span8">
								<div id="receivedDate" class="green-label"></div>
					   		</div>
						<#else>
							<div class='span4'>
								<span style="float: right">${uiLabelMap.ExportDate} </span>
							</div>  
							<div class="span8">
								<div id="receivedDate" class="green-label"></div>
					   		</div>
				   		</#if>
			   		</div>
				</div>
			</div>
			<div class='row-fluid'>
				<div class='span4'>
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right">${uiLabelMap.CreateBy} </span>
						</div>  
						<div class="span7">
							<div id="createdBy" class="green-label">${fullNameCreateBy} - [${returnHeader.createdBy?if_exists}]
								<#list possitions as tmp>
									[${tmp.description}] 
								</#list>
							</div>
						</div>
					</div>
				</div>
				<div class='span4'>
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right">${uiLabelMap.ReturnTo} </span>
						</div>  
						<div class="span7">
							<div id="needsInventoryReceive" class="green-label">
								${toPartyName?if_exists}
							</div>
				   		</div>
					</div>
				</div>
				<div class='span4'>
					<div class='span4'>
						<#if returnHeaderTypeId == 'CUSTOMER_RETURN'>
							<span style="float: right">${uiLabelMap.ReceiveToFacility} </span>
						<#else>
							<span style="float: right" id="labelFacility">${uiLabelMap.ExportFromFacility} </span>
						</#if>
					</div>  
					<div class="span8">
						<#if facility?has_content && (returnHeader.statusId == "RETURN_RECEIVED" || returnHeader.statusId == "SUP_RETURN_SHIPPED" || returnHeader.statusId == "RETURN_COMPLETED" || returnHeader.statusId == "SUP_RETURN_COMPLETED")>
							<div id="destinationFacilityIdDT" class="green-label">${facility.facilityName?if_exists}</div>
						<#else>
							<div id="destinationFacilityId" name="destinationFacilityId" class="green-label" stype="font-weight: bold;"></div>
						</#if>
			   		</div>
				</div>	
			</div>
		</div>
	</div>
</div>