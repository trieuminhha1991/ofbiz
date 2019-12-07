<#include "component://baselogistics/webapp/baselogistics/return/script/detailReturnScript.ftl"/>
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
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.OrderReturnId}: </div>
				</div>  
				<div class="span7">
					<div class="green-label">${returnId}</div>
		   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.CreateBy}: </div>
				</div>  
				<div class="span7">
					<div id="createdBy" class="green-label">${fullNameCreateBy}</div>
				</div>
			</div>
		</div>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.ReturnFrom}: </div>
				</div>  
				<div class="span7">
					<div id="fromPartyId" class="green-label">${fromPartyId} [${returnHeader.fromPartyId}]</div>
		   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
				<#if returnHeaderTypeId == 'CUSTOMER_RETURN'>
					<div>${uiLabelMap.ReceiveToFacility}: </div>
				<#else>
					<div>${uiLabelMap.ExportFromFacility}: </div>
				</#if>
				</div>  
				<div class="span7">
					<div id="destinationFacilityId" class="green-label">${facility.facilityName?if_exists}</div>
		   		</div>
			</div>
		</div>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.ReturnType}: </div>
				</div>  
				<div class="span7">
					<div id="returnHeaderTypeId" class="green-label">${returnHeaderTypeDesc?if_exists}</div>
		   		</div>
			</div>
			<#if returnHeaderTypeId == 'CUSTOMER_RETURN'>
				<#if returnHeader.statusId == 'RETURN_RECEIVED' || returnHeader.statusId == 'RETURN_COMPLETED'>
				<div class='span6'>	
					<div class='span5 text-algin-right'>
						<div>${uiLabelMap.ReceivedDate}: </div>
					</div>  
					<div class="span7">
						<div id="receivedDate" class="green-label"></div>
			   		</div>
				</div>
				<#else>
				<div class='span6'>	
					<div class='span5 text-algin-right'>
						<div>${uiLabelMap.DateExpectedReceiveWarehousing}: </div>
					</div>  
					<div class="span7">
						<div id="receivedDate" class="green-label"></div>
			   		</div>
				</div>
				</#if>
			<#else>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<div>${uiLabelMap.ReturnTo}: </div>
					</div>  
					<div class="span7">
						<div id="needsInventoryReceive" class="green-label">
							${toPartyName?if_exists}
						</div>
			   		</div>
				</div>
			</#if>
		</div>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
				<div>${uiLabelMap.OrderCurrentStatus}: </div>
			</div>  
			<div class="span7">
				<div id="statusId" class="green-label">${statusId}</div>
	   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
				<div>${uiLabelMap.CreatedDate}: </div>
			</div>  
			<div class="span7">
				<div id="entryDate" class="green-label"></div> 
				</div>
			</div>
		</div>
		
		
		<#if returnRequirementCommitments?has_content>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.FromRequirement}: </div>
				</div>  
				<div class="span7">
					<div class="green-label">
						<#list returnRequirementCommitments as requirement>
							<a href="viewDisRequirementDetail?requirementId=${(requirement.requirementId)?if_exists}" target="_blank">${(requirement.requirementId)?if_exists}</a>
						</#list>
					</div>
		   		</div>
			</div>
			<div class='span6'>
				
			</div>
		</div>
		</#if>
		
	</div>
</div>
</div>

<#assign columnlist="
			{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: '${uiLabelMap.ProductId}', pinned: true, dataField: 'productCode', width: 150 },
			{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200 },
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', width: 160, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
				cellsrenderer: function (row, column, value){
					if (value === null || value === undefined || '' === value){
						return '<span style=\"text-align: right\">_NA_</span>';
					}
				}
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'left', width: 160, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
				cellsrenderer: function (row, column, value){
					if (value === null || value === undefined || '' === value){
						return '<span style=\"text-align: right\">_NA_</span>';
					}
				}
			},
			{ text: '${uiLabelMap.ReturnReceivedQuantity}', dataField: 'receivedQuantity', width: 150, filtertype: 'number',
				cellsrenderer: function (row, column, value){
					var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
					var descriptionUom = data.quantityUomId;
					for(var i = 0; i < quantityUomData.length; i++){
						if(data.quantityUomId == quantityUomData[i].uomId){
							descriptionUom = quantityUomData[i].description;
					 	}
					}
					if (value){
						return '<span style=\"text-align: right\">' + value.toLocaleString(locale) +' (' + descriptionUom +  ')</span>';
					} else {
						return '<span style=\"text-align: right\">0</span>';
					}
				}
			},
			{ text: '${uiLabelMap.RequiredNumber}', dataField: 'returnQuantity', width: 150, filtertype: 'number',
				cellsrenderer: function (row, column, value){
					var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
					var descriptionUom = data.quantityUomId;
					for(var i = 0; i < quantityUomData.length; i++){
						if(data.quantityUomId == quantityUomData[i].uomId){
							descriptionUom = quantityUomData[i].description;
					 	}
					}
					return '<span style=\"text-align: right\">' + value.toLocaleString(locale) +' (' + descriptionUom +  ')</span>';
				}
			},
			{ text: '${uiLabelMap.Batch}', dataField: 'lotId', width: 100, 
				cellsrenderer: function (row, column, value){
					if(value){
						return '<span style=\"text-align: right\">' + value + '<span>';
					} else {
						return '<span style=\"text-align: right\">_NA_<span>';
					}
				}
			},
			{ text: '${uiLabelMap.UnitPrice}', dataField: 'returnPrice', width: 150, cellsalign: 'right',
				cellsrenderer: function (row, column, value){
					if(value){
						return '<span style=\"text-align: right\">' + formatcurrency(value, '${returnHeader.currencyUomId?if_exists}') + '<span>';
					} else {
						return '<span style=\"text-align: right\">_NA_<span>';
					}
				}
			},
			{ text: '${uiLabelMap.Reason}', dataField: 'returnReasonId', width: 200,
				cellsrenderer: function (row, column, value){
					if(value){
						return '<span>' + mapReturnReason[value] + '<span>';
					}
				}
			},
			{ text: '${uiLabelMap.ReturnType}', dataField: 'returnTypeId', width: 200, 
				cellsrenderer: function (row, column, value){
					if(value){
						return '<span>' + mapReturnType[value] + '<span>';
					}
				}
			}"/>

<#assign dataField="[{ name: 'orderId', type: 'string' },
			{ name: 'productId', type: 'string' },
			{ name: 'productCode', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'description', type: 'string' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'returnQuantity', type: 'number' },
			{ name: 'receivedQuantity', type: 'number' },
			{ name: 'returnPrice', type: 'number' },
			{ name: 'actualExportedQuantity', type: 'number' },
			{ name: 'actualDeliveredQuantity', type: 'number' },
			{ name: 'returnReasonId', type: 'string' },
			{ name: 'statusId', type: 'string' },
			{ name: 'lotId', type: 'string' },
			{ name: 'returnTypeId', type: 'string' },
			{ name: 'expiredDate', type: 'date', other: 'Timestamp' },
			{ name: 'expireDate', type: 'date', other: 'Timestamp' },
			{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
			{ name: 'datetimeReceived', type: 'date', other: 'Timestamp' },
			{ name: 'manufacturedDate', type: 'date', other: 'Timestamp' }]"/>
<@jqGrid filtersimplemode="true" id="jqxgridProductReturn" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" filterable="true"  editable="false" 
	url="jqxGeneralServicer?sname=JQGetListReturnDetail&returnId=${parameters.returnId?if_exists}" 
	customTitleProperties="ListProductReturnItem"
	jqGridMinimumLibEnable="true" bindresize="false" 
/>
		
		
<script>
	var DetailReturnSales = (function() {
		var changeReturnStatus = function changeReturnStatus(returnId, statusId){
			if ("CUSTOMER_RETURN" == returnHeaderTypeId){
				if ("RETURN_RECEIVED" == statusId){
					window.location.replace("prepareReceiveReturnForSup?returnId="+returnId+"&facilityId="+destinationFacilityId);
				} else if ("RETURN_ACCEPTED" == statusId){
					var mapAccept = {};
					mapAccept["returnId"] = returnId;
					mapAccept["statusId"] = statusId;
					mapAccept["needsInventoryReceive"] = "N";
					
					bootbox.dialog(uiLabelMap.AreYouSureAccept, 
					[{"label": uiLabelMap.CommonCancel, 
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					    "callback": function() {bootbox.hideAll();}
					}, 
					{"label": uiLabelMap.OK,
					    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					    "callback": function() {
					    	Loading.show('loadingMacro');
					    	setTimeout(function(){		
					    		$.ajax({
					      			  url: "updateReturnHeaderForSales",
					      			  type: "POST",
					      			  data: mapAccept,
					      			  dataType: "json",
					      		}).done(function(data) {
					      			location.reload();
					      		});
					    	Loading.hide('loadingMacro');
					    	}, 500);
					    }
					}]);
				}
			}
		}
		return {
			changeReturnStatus: changeReturnStatus
		}
	})();
</script>