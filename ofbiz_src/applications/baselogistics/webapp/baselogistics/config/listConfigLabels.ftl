<#include "script/listConfigLabelScript.ftl"/>
<#assign datafileds="[{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string'},
					{ name: 'productName', type: 'string'},
					{ name: 'inventoryItemLabelId', type: 'string'},
					{ name: 'description', type: 'string'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
			   ]"/>
<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', datafield: 'productCode', pinned: true, width:250,
			cellsrenderer: function(row, colum, value){
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', width:250, 
		},
		{ text: '${uiLabelMap.ProductLabel}', datafield: 'description', minwidth: 250, 
		},
	"/>
<div id="notifyContainer" >
	<div id="notifyContainer"></div>
</div>
<div>	

<div id='contextMenu' style="display:none;">
	<ul>
		<li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.CommonDelete)}</li>
		<li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<#if hasOlbPermission("MODULE", "LOG_INVENTORY", "ADMIN")>
	<@jqGrid filtersimplemode="true" id="jqxgridConfigLabel" addType="popup" dataField=datafileds columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" editable="false" addrefresh="true"  alternativeAddPopup="alterpopupWindow"
		url="jqxGeneralServicer?sname=getConfigLabels" mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenu" showlist="true"/>
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgridConfigLabel" addType="popup" dataField=filedShipment columnlist=columnShipment clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" addrefresh="true"
		url="jqxGeneralServicer?sname=getConfigLabels" addColumns=""
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
						<div id = "product">
							<div class="green-label" id="productId" name="productId">
							</div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid margin-top10'>
					<div class='span4' style="text-align: right">
						<span class="asterisk">${uiLabelMap.ProductLabel}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="labelId" name="labelId"></div>
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
						<span class="asterisk">${uiLabelMap.ProductLabel}</span>
					</div>
					<div class="span7">
						<div class="green-label" id="labelIdEdit" name="labelIdEdit"></div>
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