<#include "script/listProductRelationshipScript.ftl"/>
<#assign datafileds="[{ name: 'productIdFrom', type: 'string'},
					{ name: 'productIdTo', type: 'string'},
					{ name: 'productDescriptionFrom', type: 'string'},
					{ name: 'productDescriptionTo', type: 'string'},
					{ name: 'invLabelDescriptionFrom', type: 'string'},
					{ name: 'invLabelDescriptionTo', type: 'string'},
					{ name: 'inventoryItemLabelIdFrom', type: 'string'},
					{ name: 'inventoryItemLabelIdTo', type: 'string'},
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
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductOriginal)}', datafield: 'productDescriptionFrom', width:250,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductAttached)}', datafield: 'productDescriptionTo', width:250,
		},
		{ text: '${uiLabelMap.LabelOriginal}', datafield: 'invLabelDescriptionFrom', minwidth: 250, 
		},
		{ text: '${uiLabelMap.LabelAttached}', datafield: 'invLabelDescriptionTo', minwidth: 250, 
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
	<@jqGrid filtersimplemode="true" id="jqxgridProductRelationship" addType="popup" dataField=datafileds columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" editable="false" addrefresh="true"  alternativeAddPopup="alterpopupWindow"
		url="jqxGeneralServicer?sname=getProductRelationships" mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenu" showlist="true"/>
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgridProductRelationship" addType="popup" dataField=filedShipment columnlist=columnShipment clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" addrefresh="true"
		url="jqxGeneralServicer?sname=getProductRelationships" addColumns=""
		createUrl="" mouseRightMenu="true" contextMenuId="contextMenu" jqGridMinimumLibEnable="true"
		showlist="true"/>
</#if>

<div id="alterpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.ProductAndLabel}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='span6'>
					<div class='row-fluid'>
						<div class='row-fluid margin-top20'>
							<div class='span4' style="text-align: right">
								<span class="asterisk">${uiLabelMap.ProductOriginal}</span>
							</div>
							<div class="span7">
								<div id="productFrom" class="green-label">
									<div id="productIdFrom">
						            </div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid margin-top10'>
							<div class='span4' style="text-align: right">
								<span class="asterisk">${uiLabelMap.LabelOriginal}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="inventoryItemLabelIdFrom" name="inventoryItemLabelIdFrom"></div>
					   		</div>
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='row-fluid'>	
						<div class='row-fluid margin-top20'>
							<div class='span4' style="text-align: right">
								<span class="asterisk">${uiLabelMap.ProductAttached}</span>
							</div>
							<div class="span7">
								<div id="productTo" class="green-label">
									<div id="productIdTo">
						            </div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid margin-top10'>
							<div class='span4' style="text-align: right">
								<span class="asterisk">${uiLabelMap.LabelAttached}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="inventoryItemLabelIdTo" name="inventoryItemLabelIdTo"></div>
					   		</div>
						</div>
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
				<div class='span6'>
					<div class='row-fluid'>
						<div class='row-fluid margin-top20'>
							<div class='span4' style="text-align: right">
								<span class="asterisk">${uiLabelMap.ProductOriginal}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="productIdFromEdit" name="productIdFromEdit"></div>
								<input type="hidden" id="inputProductIdFromEdit" name="inputProductIdFromEdit"></input>
					   		</div>
						</div>
						<div class='row-fluid margin-top10'>
							<div class='span4' style="text-align: right">
								<span class="asterisk">${uiLabelMap.ProductAttached}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="inventoryItemLabelIdFromEdit" name="inventoryItemLabelIdFromEdit"></div>
								<input type="hidden" id="inputLabelFromEdit" name="inputLabelFromEdit"></input>
					   		</div>
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='row-fluid'>	
						<div class='row-fluid margin-top20'>
							<div class='span4' style="text-align: right">
								<span class="asterisk">${uiLabelMap.ProductAttached}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="productIdToEdit" name="productIdToEdit"></div>
								<input type="hidden" id="inputProductIdToEdit" name="inputProductIdToEdit"></input>
					   		</div>
						</div>
						<div class='row-fluid margin-top10'>
							<div class='span4' style="text-align: right">
								<span class="asterisk">${uiLabelMap.LabelAttached}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="inventoryItemLabelIdToEdit" name="inventoryItemLabelIdToEdit"></div>
								<input type="hidden" id="inputLabelToEdit" name="inputLabelToEdit"></input>
					   		</div>
						</div>
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