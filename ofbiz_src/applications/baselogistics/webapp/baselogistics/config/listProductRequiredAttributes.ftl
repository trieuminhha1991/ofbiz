<#include "script/listProductRequiredAttributesScript.ftl"/>
<#assign datafileds="[{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string'},
					{ name: 'productName', type: 'string'},
					{ name: 'facilityId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'facilityCode', type: 'string'},
					{ name: 'expRequired', type: 'string'},
					{ name: 'mnfRequired', type: 'string'},
					{ name: 'lotRequired', type: 'string'},
			   ]"/>
<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', datafield: 'productCode', pinned: true, width:150,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', minwidth:150, 
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}', datafield: 'facilityCode', pinned: true, width:150,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.FacilityName)}', datafield: 'facilityName', minwidth:150, 
		},
		{ text: '${uiLabelMap.ExpireDateRequired}', dataField: 'expRequired', width: 150, editable:false, filtertype: 'checkedlist',
			cellsrenderer: function(row, column, value){
				for (var i = 0; i < yesNoData.length; i ++){
					if (value && value == yesNoData[i].typeId){
						return '<span>' + yesNoData[i].description + '<span>';
					}
				}
				return '<span>' + value + '<span>';
			},
			createfilterwidget: function (column, columnElement, widget) {
				var filterDataAdapter = new $.jqx.dataAdapter(yesNoData, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'typeId', valueMember: 'typeId',
					renderer: function(index, label, value){
			        	if (yesNoData.length > 0) {
							for(var i = 0; i < yesNoData.length; i++){
								if(yesNoData[i].typeId == value){
									return '<span>' + yesNoData[i].description + '</span>';
								}
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
   			}
		},
		{ text: '${uiLabelMap.ManufacturedDateRequired}', dataField: 'mnfRequired', width: 150, editable:false, filtertype: 'checkedlist',
			cellsrenderer: function(row, column, value){
				for (var i = 0; i < yesNoData.length; i ++){
					if (value && value == yesNoData[i].typeId){
						return '<span>' + yesNoData[i].description + '<span>';
					}
				}
				return '<span>' + value + '<span>';
			},
			createfilterwidget: function (column, columnElement, widget) {
				var filterDataAdapter = new $.jqx.dataAdapter(yesNoData, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'typeId', valueMember: 'typeId',
					renderer: function(index, label, value){
			        	if (yesNoData.length > 0) {
							for(var i = 0; i < yesNoData.length; i++){
								if(yesNoData[i].typeId == value){
									return '<span>' + yesNoData[i].description + '</span>';
								}
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
   			}
		},
		{ text: '${uiLabelMap.LotRequired}', dataField: 'lotRequired', width: 150, editable:false, filtertype: 'checkedlist',
			cellsrenderer: function(row, column, value){
				for (var i = 0; i < yesNoData.length; i ++){
					if (value && value == yesNoData[i].typeId){
						return '<span>' + yesNoData[i].description + '<span>';
					}
				}
				return '<span>' + value + '<span>';
			},
			createfilterwidget: function (column, columnElement, widget) {
				var filterDataAdapter = new $.jqx.dataAdapter(yesNoData, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'typeId', valueMember: 'typeId',
					renderer: function(index, label, value){
			        	if (yesNoData.length > 0) {
							for(var i = 0; i < yesNoData.length; i++){
								if(yesNoData[i].typeId == value){
									return '<span>' + yesNoData[i].description + '</span>';
								}
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
   			}
		},
	"/>
<div id="notifyContainer" >
	<div id="notifyContainer"></div>
</div>
<div>	

<div id='contextMenu' style="display:none;">
	<ul>
		<li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.Delete)}</li>
	</ul>
</div>
<#if hasOlbPermission("MODULE", "LOG_INVENTORY", "ADMIN")>
	<@jqGrid filtersimplemode="true" id="jqxgridConfigRequiredDate" addType="popup" dataField=datafileds columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" editable="false" addrefresh="true"  alternativeAddPopup="alterpopupWindow" customTitleProperties="ListProductRequiredAttributes"
		url="jqxGeneralServicer?sname=getProductRequiredAttributes" mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenu" showlist="true"/>
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgridConfigRequiredDate" addType="popup" dataField=filedShipment columnlist=columnShipment clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" addrefresh="true"
		url="jqxGeneralServicer?sname=getProductRequiredAttributes" addColumns="" customTitleProperties="ListProductRequiredAttributes"
		createUrl="" mouseRightMenu="true" contextMenuId="contextMenu" jqGridMinimumLibEnable="true"
		showlist="true"/>
</#if>

<div id="alterpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.ProductAndLabel}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='row-fluid margin-top20'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.Product}</span>
					</div>
					<div class="span7">
						<div id="productId" style="width: 100%" class="green-label">
							<div id="jqxgridListProduct">
				            </div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.Facility}</span>
					</div>
					<div class="span7">
						<div id="facilityId" style="width: 100%" class="green-label">
							<div id="jqxgridListFacility">
				            </div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.ExpireDateRequired}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="expRequiredId" name="expRequiredId"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.ManufacturedDateRequired}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="mnfRequiredId" name="mnfRequiredId"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.LotRequired}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="lotRequiredId" name="lotRequiredId"></div>
			   		</div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="newCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="newSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="editpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.ProductAndLabel}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='row-fluid margin-top20'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.Product}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="productIdEdit" name="productIdEdit"></div>
						<input id="editProductId" type="hidden"></input>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.Facility}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="facilityIdEdit" name="facilityIdEdit"></div>
						<input id="editFacilityId" type="hidden"></input>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.ExpireDateRequired}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="expRequiredIdEdit" name="expRequiredIdEdit"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.ManufacturedDateRequired}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="mnfRequiredIdEdit" name="mnfRequiredIdEdit"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.LotRequired}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="lotRequiredIdEdit" name="lotRequiredIdEdit"></div>
			   		</div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="editCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="editSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>