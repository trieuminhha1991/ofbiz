<#include "script/listTransferRequirementScript.ftl"/>
<div id="transfers-tab" class="tab-pane<#if activeTab?exists && activeTab == "transfers-tab"> active</#if>">
<#assign columnlist="
			{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: '${uiLabelMap.TransferId}', pinned: true, dataField: 'transferId', width: 120, editable:false, 
				cellsrenderer: function(row, column, value){
					 return '<span><a href=\"javascript:ListTransferObj.showDetailTransfer(&#39;' + value + '&#39;)\"> ' + value  + '</a></span>'
				}
			},
			{ text: '${uiLabelMap.TransferType}', dataField: 'transferTypeId',  width: 200, editable:false, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					for (var i = 0; i < transferTypeData.length; i ++){
						if (value && value == transferTypeData[i].transferTypeId){
							return '<span>' + transferTypeData[i].description + '<span>';
						}
					}
					return '<span>' + value + '<span>';
				},
				createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(transferTypeData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'transferTypeId', valueMember: 'transferTypeId',
						renderer: function(index, label, value){
				        	if (transferTypeData.length > 0) {
								for(var i = 0; i < transferTypeData.length; i++){
									if(transferTypeData[i].transferTypeId == value){
										return '<span>' + transferTypeData[i].description + '</span>';
									}
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			},
			},
			{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable:false, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					for (var i = 0; i < transferStatusData.length; i ++){
						if (value && value == transferStatusData[i].statusId){
							return '<span>' + transferStatusData[i].description + '<span>';
						}
					}
					return '<span>' + value + '<span>';
				},
				createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(transferStatusData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
						renderer: function(index, label, value){
				        	if (transferStatusData.length > 0) {
								for(var i = 0; i < transferStatusData.length; i++){
									if(transferStatusData[i].statusId == value){
										return '<span>' + transferStatusData[i].description + '</span>';
									}
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			},
			},
			{ text: '${uiLabelMap.OriginFacility}', dataField: 'originFacilityName', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.DestFacility}', dataField: 'destFacilityName', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.TransferDate}', width: 150, dataField: 'transferDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
					 }
				}, 
			},
			{ text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.CreatedDate}', width: 150, dataField: 'createdDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
					 }
				}, 
			},
			{ text: '${uiLabelMap.CreatedBy}', dataField: 'createdByUserLogin', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.LastModifyBy}', dataField: 'lastModifyByUserLogin', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
	"/>
	<#assign dataField="[{ name: 'transferId', type: 'string' },
	{ name: 'transferTypeId', type: 'string'},
	{ name: 'statusId', type: 'string'},
	{ name: 'originFacilityId', type: 'string' },
	{ name: 'destFacilityId', type: 'string' },
	{ name: 'originFacilityName', type: 'string' },
	{ name: 'destFacilityName', type: 'string' },
	{ name: 'originContactMechId', type: 'string'},
	{ name: 'createdDate', type: 'date', other: 'Timestamp'},
	{ name: 'transferDate', type: 'date', other: 'Timestamp'},
	{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
	{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
	{ name: 'needsReservesInventory', type: 'string'},
	{ name: 'maySplit', type: 'string'},
	{ name: 'priority', type: 'number'},
	{ name: 'description', type: 'string'},
	{ name: 'createdByUserLogin', type: 'string'},
	{ name: 'lastModifyByUserLogin', type: 'string'},
 	]"/>
	<#if requirement?has_content && requirement.statusId?has_content && (requirement.statusId == "REQ_APPROVED" || requirement.statusId == "REQ_CONFIRMED") && hasOlbPermission("MODULE", "LOG_TRANSFER", "CREATE")>
		<@jqGrid filtersimplemode="true" id="jqxgridTransfer" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" filterable="true"  editable="false" 
			 url="jqxGeneralServicer?sname=jqGetListTransferByRequirement&requirementId=${parameters.requirementId?if_exists}" customTitleProperties="ListTransfer"
			 jqGridMinimumLibEnable="true" bindresize="false" 
			 customcontrol1="icon-plus-sign@${uiLabelMap.QuickCreate}@javascript:ReqDetailObj.quickCreateTransferFromRequirement('${parameters.requirementId?if_exists}');"
			 customcontrol2="icon-plus-sign@${uiLabelMap.AddNew}@javascript:ReqDetailObj.createTransferFromRequirement('${parameters.requirementId?if_exists}');"
		 />
	<#else>
		<@jqGrid filtersimplemode="true" id="jqxgridTransfer" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" filterable="true"  editable="false" 
			 url="jqxGeneralServicer?sname=jqGetListTransferByRequirement&requirementId=${parameters.requirementId?if_exists}" customTitleProperties="ListTransfer"
			 jqGridMinimumLibEnable="true" bindresize="false" 
		 />
	</#if>
</div>

<div id="createTransferWindow" class="hide popup-bound">
	<div>${uiLabelMap.CreateTransfer}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class="span4">
					<div class='row-fluid margin-top10'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.OriginFacility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferOriginFacilityId" name="transferOriginFacilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.OriginAddress}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferOriginContactMechId" name="transferOriginContactMechId"></div>
				   		</div>
					</div>
				</div>
				<div class="span4">
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.DestFacility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferDestFacilityId" name="transferDestFacilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.DestAddress}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferDestContactMechId" name="transferDestContactMechId"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10 hide'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.TransferType}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferTypeId" name="transferTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid hide'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.NeedsReservesInventory}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="needsReservesInventory" name="needsReservesInventory"></div>
				   		</div>
					</div>
					<div class='row-fluid hide'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.MaySplit}</span>
						</div>
						<div class="span7">
							<div id="maySplit" class="green-label"></div>
				   		</div>
					</div>
					<div class='row-fluid hide'>
						<div class='span5' style="text-align: right">
							<span>${uiLabelMap.Priority}</span>
						</div>
						<div class="span7">
							<div id="priority" class="green-label"></div>
				   		</div>
					</div>
				</div>
				<div class="span4">
					<div class='row-fluid margin-top10 hide'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.TransferDate}</span>
						</div>
						<div class="span7">
							<div id="transferDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.TransferDate}</span>
						</div>
						<div class="span7">
							<div id="shipAfterDate" class="green-label"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
						</div>
						<div class="span7">
							<div id="shipBeforeDate" class="green-label"></div>
				   		</div>
					</div>
					<div class='row-fluid hide'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.CarrierParty}</span>
						</div>
						<div class="span7">
							<div id="carrierPartyId" class="green-label"></div>
				   		</div>
					</div>
					<div class='row-fluid hide'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.ShipmentMethod}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferShipmentMethodTypeId"></div>
				   		</div>
					</div>
				</div>
			</div>
			<div class="row-fluid margin-top20">
				<div style="margin-left: 20px;"><div id="listRequirementItemPopup"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="createTransferCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="createTransferSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>