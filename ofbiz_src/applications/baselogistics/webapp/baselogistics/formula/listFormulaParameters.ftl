<#include "script/listFormulaParametersScript.ftl"/>
<div>
<#assign datafileds="[{ name: 'parameterId', type: 'string'},
					{ name: 'parameterCode', type: 'string'},
					{ name: 'parameterName', type: 'string'},
					{ name: 'parameterValue', type: 'string'},
					{ name: 'parameterTypeId', type: 'string'},
					{ name: 'defaultValue', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'description', type: 'string'},
			   ]"/>
<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, cellClassName: cellClassName,
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaParameterId)}', datafield: 'parameterCode', pinned: true, width:120, cellClassName: cellClassName,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLParameterName)}', datafield: 'parameterName', pinned: true, width:150,cellClassName: cellClassName,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaParameterType)}', datafield: 'parameterTypeId', width:150,cellClassName: cellClassName,
			cellsrenderer: function(row, column, value){
				 if (value){
					 return '<span>'+ getParameterDescription(value)+'</span>';
				 }
			 }, 
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Status)}', datafield: 'statusId', width:150, cellClassName: cellClassName,	 
			cellsrenderer: function(row, column, value){
				 if (value){
					 return '<span>'+ getFormulaParameterStatusDescription(value)+'</span>';
				 }
		 	}, 
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaParameterValue)}', datafield: 'parameterValue', minwidth: 200, cellClassName: cellClassName,
			cellsrenderer: function(row, column, value){
				var data = $('#jqxgridFromulaParameter').jqxGrid('getrowdata', row);
			 	if (data.parameterTypeId == 'PARAM_SYSTEM'){
				 	return '<span>${uiLabelMap.BLUpdateWhenCalculating}</span>';
			 	} else {
			 		if (typeof parseInt(value) == 'number') {
			 			return '<span style=\"text-align: right\">'+formatnumber(value)+'</span>';
			 		}
			 	}
		 	},  
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLDefaultValue)}', datafield: 'defaultValue', minwidth: 200, cellClassName: cellClassName,
			cellsrenderer: function(row, column, value){
				var data = $('#jqxgridFromulaParameter').jqxGrid('getrowdata', row);
			 	if (data.parameterTypeId == 'PARAM_SYSTEM'){
				 	return '<span>${uiLabelMap.BLUpdateWhenCalculating}</span>';
			 	} else {
			 		if (typeof parseInt(value) == 'number') {
			 			return '<span style=\"text-align: right\">'+formatnumber(value)+'</span>';
			 		}
			 	}
		 	}, 
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Description)}', datafield: 'description', width: 200, cellClassName: cellClassName,
		},
	"/>
<div id="notifyContainer" >
	<div id="notifyContainer"></div>
</div>

<div id='contextMenuParameter' style="display:none;">
	<ul>
		<li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<li><i class="fa fa-times"></i>${StringUtil.wrapString(uiLabelMap.BLDeactivated)}</li>
		<li><i class="fa fa-signal"></i>${StringUtil.wrapString(uiLabelMap.BLActivated)}</li>
		<li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.Delete)}</li>
	</ul>
</div>
	<@jqGrid filtersimplemode="true" id="jqxgridFromulaParameter" addType="popup" dataField=datafileds columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" editable="false" addrefresh="true"  alternativeAddPopup="alterpopupWindowFormulaParameter" customTitleProperties="BLListFormulaParameter"
		url="jqxGeneralServicer?sname=jQGetListFormulaParameters&formulaParameterTypeId=${parameters.formulaParameterTypeId?if_exists}" mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenuParameter" showlist="true"/>
</div>
<form id = "formFormulaParameterAddNew">
	<div id="alterpopupWindowFormulaParameter" class="hide popup-bound">
		<div>${uiLabelMap.BLAddFormulaParameter}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid'>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLFormulaParameterType}</span>
						</div>
						<div class="span7">
							<div id="newParameterTypeId" style="width: 100%" class="green-label">
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLFormulaParameterId}</span>
						</div>
						<div class="span7">
							<input id="newParameterCode" style="width: 100%" class="green-label" type="text">
							</input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLParameterName}</span>
						</div>
						<div class="span7">
							<input id="newParameterName" style="width: 100%" class="green-label" type="text">
							</input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLFormulaParameterValue}</span>
						</div>
						<div class="span7">
							<input id="newParameterValue" style="width: 100%" class="green-label" type="text">
							</input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLDefaultValue} <i title="${uiLabelMap.BLApplyForAllProductStore}" class="fa-question-circle" style="cursor: pointer;"></i></span>
						</div>
						<div class="span7">
							<input id="newParameterDefaultValue" style="width: 100%" class="green-label" type="text">
							</input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLApplyForProductStore}</span>
						</div>
						<div class="span7">
							<div id="newProductStoreId" style="width: 100%" class="green-label">
								<div id="jqxgridListProductStore">
					            </div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.Description}</span>
						</div>
						<div class="span7">
							<textarea id="newParameterDescription" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px" class="span12"></textarea>
				   		</div>
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
		        <button id="newParamCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="newParamSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</form>

<form id = "formFormulaParameterEdit">
	<div id="editpopupWindowFormulaParameter" class="hide popup-bound">
		<div>${uiLabelMap.BLEditFormulaParameter}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid'>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLFormulaParameterType}</span>
						</div>
						<div class="span7">
							<div id="editParameterTypeId" style="width: 100%" class="green-label">
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLFormulaParameterId}</span>
						</div>
						<div class="span7">
							<input id="editParameterCode" type="text"></input>
							<input id="editParameterId" type="hidden"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLParameterName}</span>
						</div>
						<div class="span7">
							<input id="editParameterName" style="width: 100%" class="green-label" type="text">
							</input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLFormulaParameterValue}</span>
						</div>
						<div class="span7">
							<input id="editParameterValue" type="text"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLDefaultValue}</span>
						</div>
						<div class="span7">
							<input id="editParameterDefaultValue" style="width: 100%" class="green-label" type="text">
							</input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.Description}</span>
						</div>
						<div class="span7">
							<textarea id="editParameterDescription" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px" class="span12"></textarea>
				   		</div>
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
		        <button id="editParamCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="editParamSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</form>