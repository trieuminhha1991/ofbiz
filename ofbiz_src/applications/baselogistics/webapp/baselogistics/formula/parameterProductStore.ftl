<#include "script/parameterProductStoreScript.ftl"/>
<div>
<#assign datafileds="[{ name: 'parameterId', type: 'string'},
					{ name: 'parameterName', type: 'string'},
					{ name: 'parameterCode', type: 'string'},
					{ name: 'parameterValue', type: 'string'},
					{ name: 'productStoreId', type: 'string'},
					{ name: 'storeName', type: 'string'},
					{ name: 'fromDate', type: 'date', other: 'timestamp'},
					{ name: 'thruDate', type: 'date', other: 'timestamp'},
			   ]"/>
<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaParameterId)}', datafield: 'parameterCode', pinned: true, width:150,cellClassName: paramStoreCellClass,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLParameterName)}', datafield: 'parameterName', width:150, cellClassName: paramStoreCellClass,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaParameterValue)}', datafield: 'parameterValue', width:150, cellClassName: paramStoreCellClass,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLProductStoreId)}', datafield: 'productStoreId', minwidth:150, cellClassName: paramStoreCellClass,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLProductStore)}', datafield: 'storeName', minwidth:150, cellClassName: paramStoreCellClass,
		},
		{ text: '${uiLabelMap.BLApplyFromDate}', width: 150, dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right', cellClassName: paramStoreCellClass,
			cellsrenderer: function(row, column, value){
				 if (value){
					 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
				 }
			}, 
		},
		{ text: '${uiLabelMap.BLApplyThruDate}', width: 150, dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right', cellClassName: paramStoreCellClass,
			cellsrenderer: function(row, column, value){
				 if (value){
					 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
				 }
			}, 
		},
	"/>
<div id='contextMenuParamStore' style="display:none;">
	<ul>
		<li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.Delete)}</li>
	</ul>
</div>
	<@jqGrid filtersimplemode="true" id="jqxgridParameterProductStore" addType="popup" dataField=datafileds columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" editable="false" addrefresh="true"  alternativeAddPopup="alterpopupWindowParamStore" customTitleProperties="BLListParameterAndProductStore"
		url="jqxGeneralServicer?sname=jQGetParameterAndProductStores&productStoreId=${parameters.productStoreId?if_exists}" mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenuParamStore" showlist="true"/>
</div>
<form id = "formAddNewParamStore">
	<div id="alterpopupWindowParamStore" class="hide popup-bound">
		<div>${uiLabelMap.BLAddParameterProductStore}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid'>
					<div class='row-fluid margin-top20'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLParamater}</span>
						</div>
						<div class="span7">
							<div id="parameterStoreId" style="width: 100%" class="green-label">
								<div id="jqxgridListParameterStores">
					            </div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLProductStore}</span>
						</div>
						<div class="span7">
							<div id="productStoreParameterId" style="width: 100%" class="green-label">
								<div id="jqxgridListStoreParameters">
					            </div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLFormulaParameterValue}</span>
						</div>
						<div class="span7">
							<input class="green-label" id="parameterValueStore" name="parameterValueStore"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLApplyFromDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="fromDateParamStore" name="fromDateParamStore"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLApplyThruDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="thruDateParamStore" name="thruDateParamStore"></div>
				   		</div>
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
		        <button id="newParamStoreCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="newParamStoreSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</form>

<form id = "formEditParamStore">
	<div id="editpopupWindowParamStore" class="hide popup-bound">
		<div>${uiLabelMap.BLEditParameterProductStore}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid'>
					<div class='row-fluid margin-top20'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLParamater}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="parameterText" name="parameterText"></div>
							<input id="parameterEditId" type="hidden"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLProductStore}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="productStoreText" name="productStoreText"></div>
							<input id="productStoreEditId" type="hidden"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLFormulaParameterValue}</span>
						</div>
						<div class="span7">
							<input class="green-label" id="parameterValueStoreEdit" name="parameterValueStoreEdit"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLApplyFromDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="fromDateParamStoreEdit" name="fromDateParamStoreEdit"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLApplyThruDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="thruDateParamStoreEdit" name="thruDateParamStoreEdit"></div>
				   		</div>
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
		        <button id="editParamStoreCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="editParamStoreSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</form>