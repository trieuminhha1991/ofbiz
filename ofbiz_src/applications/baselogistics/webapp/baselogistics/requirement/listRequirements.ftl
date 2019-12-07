<#include "script/listRequirementScript.ftl"/>
<#assign filedShipment="[{ name: 'requirementId', type: 'string'},
					{ name: 'requirementTypeId', type: 'string'},
					{ name: 'requirementStartDate', type: 'date', other: 'Timestamp'},
					{ name: 'requiredByDate', type: 'date', other: 'Timestamp'},
					{ name: 'statusId', type: 'string'},
					{ name: 'estimatedBudget', type: 'number'},
					{ name: 'grandTotal', type: 'number'},
					{ name: 'currencyUomId', type: 'string'},
					{ name: 'reasonEnumId', type: 'string'},
					{ name: 'createdDate', type: 'date', other: 'Timestamp'},
					{ name: 'fullName', type: 'string'},
					{ name: 'reasonDescription', type: 'string'},
					{ name: 'destFacilityName', type: 'string'},
					{ name: 'originFacilityName', type: 'string'},
					{ name: 'facilityId', type: 'string'},
					{ name: 'destFacilityId', type: 'string'},
			   ]"/>
<#assign columnShipment="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.RequirementId)}', datafield: 'requirementId', pinned: true, width:150,
		cellsrenderer: function(row, colum, value){
			var link = 'viewRequirementDetail?requirementId=' + value;
	    	return '<span><a href=\"' + link + '\">' + value + '</a></span>';
		}
	},"/>
<#assign columnShipment = columnShipment + "
		{ text: '${StringUtil.wrapString(uiLabelMap.RequirementType)}', datafield: 'requirementTypeId', width:150, filtertype: 'checkedlist',
			cellsrenderer: function (row, colum, value){
		 		var data = $('#jqxgridRequirement').jqxGrid('getrowdata', row);
		 		if (value){
		 			for (var i = 0; i < requirementTypeData.length; i++){
		 				if (value == requirementTypeData[i].requirementTypeId){
		 					return '<span>'+requirementTypeData[i].description+'</span>';
		 				}
		 			}
		 		} else {
	 				return '<span>_NA_</span>';
		 		}
		 	},
		 	createfilterwidget: function (column, columnElement, widget) {
				var tmp = requirementTypeData;
				var filterDataAdapter = new $.jqx.dataAdapter(tmp, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'requirementTypeId', valueMember: 'requirementTypeId',
					renderer: function(index, label, value){
			        	if (tmp.length > 0) {
							for(var i = 0; i < tmp.length; i++){
								if(tmp[i].requirementTypeId == value){
									return '<span>' + tmp[i].description + '</span>';
								}
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
			},
		},">
<#if parameters.requirementTypeId?has_content && parameters.requirementTypeId == "TRANSFER_REQUIREMENT">
		<#assign columnShipment = columnShipment + "
			{ text: '${uiLabelMap.FacilityFrom}', datafield: 'originFacilityName', align: 'left', width: 150,
			},
			{ text: '${uiLabelMap.FacilityTo}', datafield: 'destFacilityName', align: 'left', width: 150,
			},
			">
<#else>
		<#assign columnShipment = columnShipment + "
			{ text: '${uiLabelMap.FacilityRequired}', datafield: 'originFacilityName', align: 'left', width: 150,
			},">
</#if>
<#assign columnShipment = columnShipment + "
		{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'left', width: 150, filtertype: 'checkedlist',
			cellsrenderer: function(row, colum, value){
				for(i=0; i < statusData2.length; i++){
		            if(statusData2[i].statusId == value){
		            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData2[i].description + '</span>';
		            }
		        }
			},
			createfilterwidget: function (column, columnElement, widget) {
				var tmp = statusData;
				var filterDataAdapter = new $.jqx.dataAdapter(tmp, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
					renderer: function(index, label, value){
			        	if (tmp.length > 0) {
							for(var i = 0; i < tmp.length; i++){
								if(tmp[i].statusId == value){
									return '<span>' + tmp[i].description + '</span>';
								}
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
			},
	},
	{ text: '${uiLabelMap.RemainingSubTotal}', dataField: 'grandTotal', filtertype: 'number', width: 150, editable:false, cellsformat: 'd', cellsalign: 'right',
		cellsrenderer: function(row, column, value){
		}
	},
	">
	<#if hasReason == "Y">
		<#assign columnShipment = columnShipment + "{ text: '${StringUtil.wrapString(uiLabelMap.RequirementPurpose)}', datafield: 'reasonEnumId', minwidth:150, filtertype: 'checkedlist',
			cellsrenderer: function(row, colum, value){
				for(i=0; i < reasonEnumData.length; i++){
		            if(reasonEnumData[i].enumId == value){
		            	return '<span style=\"text-align: left;\" title='+value+'>' + reasonEnumData[i].description + '</span>';
		            }
		        }
			},
			createfilterwidget: function (column, columnElement, widget) {
				var tmp = reasonEnumData;
				var filterDataAdapter = new $.jqx.dataAdapter(tmp, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'enumId', valueMember: 'enumId',
					renderer: function(index, label, value){
			        	if (tmp.length > 0) {
							for(var i = 0; i < tmp.length; i++){
								if(tmp[i].enumId == value){
									return '<span>' + tmp[i].description + '</span>';
								}
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
			},
		},">
	</#if>
	<#assign columnShipment = columnShipment + "{ text: '${StringUtil.wrapString(uiLabelMap.LogRequiredByDate)}', dataField: 'requiredByDate', align: 'left', width: 180, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ReqObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.LogRequirementStartDate)}', dataField: 'requirementStartDate', align: 'left', width: 180, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
			 } else {
				 return '<span style=\"text-align: right\">'+DatetimeUtilObj.formatToMinutes(value)+'</span>';
			 }
		 }, 
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.EstimatedBudget)}', datafield: 'estimatedBudget', width:150, hidden: true,
		cellsrenderer: function (row, colum, value){
			if(value){
				return '<span style=\"text-align: right;\">' + formatcurrency(value) + '</span>';
			} else {
				value = 0;
				return '<span style=\"text-align: right;\">' + formatcurrency(value) + '</span>';
			}
	 	}
	},
	{ hidden: true, text: '${StringUtil.wrapString(uiLabelMap.CreatedDate)}', dataField: 'createdDate', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ReqObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.CreatedBy)}', datafield: 'fullName', width:150,
	},
	"/>

<div id="notifyContainer" >
	<div id="notifyContainer">
	</div>
</div>
<div>	
<#if hasOlbPermission("MODULE", "LOG_REQUIREMENT", "CREATE")>
	<#if parameters.requirementTypeId?has_content>
		<#if parameters.requirementTypeId == "TRANSFER_REQUIREMENT">
			<@jqGrid filtersimplemode="true" id="jqxgridRequirement" addType="popup" dataField=filedShipment columnlist=columnShipment clearfilteringbutton="true"
				showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" addrefresh="true"
				url="jqxGeneralServicer?sname=getRequirements&requirementTypeId=${parameters.requirementTypeId?if_exists}&statusId=${parameters.statusId?if_exists}" addColumns=""
				createUrl="" mouseRightMenu="true" contextMenuId="RequirementMenu" jqGridMinimumLibEnable="true"
				showlist="true" customTitleProperties="ListRequirements" selectionmode="checkbox" useCache="true" keyCache="requirementId" cacheMode="selection"	
				customcontrol2="icon-plus open-sans@${uiLabelMap.AddNew}@javascript: void(0);@ReqObj.createRequirement()"
				customcontrol1="icon-plus open-sans@${uiLabelMap.CreateTransfer}@javascript: void(0);@ReqObj.createTransfer()"/>
		<#else>
			<@jqGrid filtersimplemode="true" id="jqxgridRequirement" addType="popup" dataField=filedShipment columnlist=columnShipment clearfilteringbutton="true"
				showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" addrefresh="true"
				url="jqxGeneralServicer?sname=getRequirements&requirementTypeId=${parameters.requirementTypeId?if_exists}&statusId=${parameters.statusId?if_exists}" addColumns=""
				createUrl="" mouseRightMenu="true" contextMenuId="RequirementMenu" jqGridMinimumLibEnable="true"
				showlist="true" customTitleProperties="ListRequirements" 
				customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript: void(0);@ReqObj.createRequirement()"/>
		</#if>
	<#else>
		<@jqGrid filtersimplemode="true" id="jqxgridRequirement" addType="popup" dataField=filedShipment columnlist=columnShipment clearfilteringbutton="true"
			showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" addrefresh="true"
			url="jqxGeneralServicer?sname=getRequirements&requirementTypeId=${parameters.requirementTypeId?if_exists}&statusId=${parameters.statusId?if_exists}" addColumns=""
			createUrl="" mouseRightMenu="true" contextMenuId="RequirementMenu" jqGridMinimumLibEnable="true"
			showlist="true" customTitleProperties="ListRequirements" 
			customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript: void(0);@ReqObj.createRequirement()"/>
	</#if>
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgridRequirement" addType="popup" dataField=filedShipment columnlist=columnShipment clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" addrefresh="true"
		url="jqxGeneralServicer?sname=getRequirements&requirementTypeId=${parameters.requirementTypeId?if_exists}&statusId=${parameters.statusId?if_exists}" addColumns=""
		createUrl="" mouseRightMenu="true" contextMenuId="RequirementMenu" jqGridMinimumLibEnable="true"
		showlist="true" customTitleProperties="ListRequirements"/>
</#if>

<div id='RequirementMenu' style="display:none;">
	<ul>
	    <li><i class="fa fa-folder-open-o"></i>${uiLabelMap.ViewDetailInNewPage}</li>
	    <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<div id="alterPopupTransferWindow" class="hide popup-bound">
	<div>${uiLabelMap.CreateTransfer}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<h4 class="row header smaller lighter blue" style="margin: 5px 0px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
		        ${uiLabelMap.GeneralInfo}
		    </h4>
			<div class='row-fluid'>
				<div class="span4">
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.OriginFacility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="originFacilityId" name="originFacilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.OriginAddress}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="originContactMechId" name="originContactMechId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.DestFacility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="destFacilityId" name="destFacilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.DestAddress}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="destContactMechId" name="destContactMechId"></div>
				   		</div>
					</div>
				</div>
				<div class="span4">
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.TransferType}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="transferTypeId" name="transferTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.NeedsReservesInventory}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="needsReservesInventory" name="needsReservesInventory"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.MaySplit}</span>
						</div>
						<div class="span7">
							<div id="maySplit" class="green-label"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span>${uiLabelMap.Priority}</span>
						</div>
						<div class="span7">
							<div id="priority" class="green-label"></div>
				   		</div>
					</div>
				</div>
				<div class="span4">
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.TransferDate}</span>
						</div>
						<div class="span7">
							<div id="transferDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span>${uiLabelMap.ShippingAbout}</span>
						</div>
						<div class="span7">
							<div class="row-fluid" style="margin-bottom: -10px !important">
								<div class="span5">
									<div id="shipAfterDate" class="green-label"></div>
								</div>
								<div class="span5">
									<div id="shipBeforeDate" class="green-label"></div>
								</div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.CarrierParty}</span>
						</div>
						<div class="span7">
							<div id="carrierPartyId" class="green-label"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="asterisk">${uiLabelMap.ShipmentMethod}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="shipmentMethodTypeId"></div>
				   		</div>
					</div>
				</div>
			</div>
			<div class="row-fluid margin-top10">
	    		<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.ListProduct}
				</h4>
				<div style="margin-left: 20px;"><div id="listRequirementItem"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="createTransferCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="createTransferSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>