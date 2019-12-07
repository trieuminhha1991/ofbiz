<#include "component://basesalesmtl/webapp/basesalesmtl/distributor/requirement/script/listRequirementScript.ftl"/>
<#assign filedShipment="[{ name: 'requirementId', type: 'string'},
					{ name: 'requirementTypeId', type: 'string'},
					{ name: 'requirementStartDate', type: 'date', other: 'Timestamp'},
					{ name: 'requiredByDate', type: 'date', other: 'Timestamp'},
					{ name: 'statusId', type: 'string'},
					{ name: 'estimatedBudget', type: 'number'},
					{ name: 'currencyUomId', type: 'string'},
					{ name: 'reasonEnumId', type: 'string'},
					{ name: 'createdDate', type: 'date', other: 'Timestamp'},
					{ name: 'fullName', type: 'string'},
					{ name: 'departmentFullName', type: 'string'},
					{ name: 'reasonDescription', type: 'string'},
					{ name: 'destFacilityName', type: 'string'},
					{ name: 'originFacilityName', type: 'string'},
					{ name: 'facilityId', type: 'string'},
					{ name: 'destFacilityId', type: 'string'}]"/>
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
				link = urlDetail + value;
		    	return '<span><a target=\"_blank\" href=\"' + link + '\">' + value + '</a></span>';
			}
		},
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
				widget.jqxDropDownList({source: requirementTypeData, displayMember: 'description', valueMember: 'requirementTypeId' });
			},
		},
		{ text: '${uiLabelMap.RequestFromFacility}', datafield: 'originFacilityName', width: 150},
		{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, filtertype: 'checkedlist',
			cellsrenderer: function(row, colum, value){
				for(i=0; i < statusData2.length; i++){
		            if(statusData2[i].statusId == value){
		            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData2[i].description + '</span>';
		            }
		        }
			},
			createfilterwidget: function (column, columnElement, widget) {
				widget.jqxDropDownList({source: statusData, displayMember: 'description', valueMember: 'statusId' });
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.RequirementPurpose)}', datafield: 'reasonEnumId', width:150, filtertype: 'checkedlist',
			cellsrenderer: function(row, colum, value){
				for(i=0; i < reasonEnumData.length; i++){
		            if(reasonEnumData[i].enumId == value){
		            	return '<span style=\"text-align: left;\" title='+value+'>' + reasonEnumData[i].description + '</span>';
		            }
		        }
			},
			createfilterwidget: function (column, columnElement, widget) {
				widget.jqxDropDownList({source: reasonEnumData, displayMember: 'description', valueMember: 'enumId' });
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.LogRequiredByDate)}', dataField: 'requiredByDate', width: 180, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ReqObj.formatFullDate(value)+'</span>';
				 }
			 }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.LogRequirementStartDate)}', dataField: 'requirementStartDate', width: 180, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ReqObj.formatFullDate(value)+'</span>';
				 }
			 }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.EstimatedBudget)}', datafield: 'estimatedBudget', width:150,
			cellsrenderer: function (row, colum, value){
				var data = $('#jqxgridRequirement').jqxGrid('getrowdata', row);
				var desc = '';
				for (var i = 0; i < currencyUomData.length; i ++){
					if (currencyUomData[i].uomId == data.currencyUomId){
						desc = currencyUomData[i].description;
					}
				}
				if(value){
					return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + ' (' +desc+ ')</span>';
				} else {
					value = 0;
					return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + ' (' +desc+ ')</span>';
				}
		 	}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.CreatedDate)}', dataField: 'createdDate', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ReqObj.formatFullDate(value)+'</span>';
				 }
			 }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.CreatedBy)}', datafield: 'fullName', width:150},
		{ text: '${StringUtil.wrapString(uiLabelMap.Department)}', datafield: 'departmentFullName', width:150}"/>

<div id="notifyContainer" >
	<div id="notifyContainer">
	</div>
</div>
<div>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListDisRequirements&requirementTypeId=${(parameters.requirementTypeId)?if_exists}&reasonEnumId=${(parameters.reasonEnumId)?if_exists}"
	dataField=filedShipment columnlist=columnShipment filterable="true" clearfilteringbutton="true"
	showtoolbar="true" filtersimplemode="true"
	addrow="false" id="jqxgridRequirement"/>

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