<#include "script/productFormulaScript.ftl"/>
<div>
<#assign datafileds="[{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string'},
					{ name: 'productName', type: 'string'},
					{ name: 'formulaCode', type: 'string'},
					{ name: 'formulaName', type: 'string'},
					{ name: 'formulaValue', type: 'string'},
					{ name: 'formulaId', type: 'string'},
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
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', datafield: 'productCode', pinned: true, width:150, cellClassName: prFormulaCellClass,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', width:150, cellClassName: prFormulaCellClass,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaId)}', datafield: 'formulaCode', minwidth:150, cellClassName: prFormulaCellClass,  
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaName)}', datafield: 'formulaName', width:150, cellClassName: prFormulaCellClass,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaValue)}', datafield: 'formulaValue', minwidth:150, cellClassName: prFormulaCellClass,  
		},
		{ text: '${uiLabelMap.BLApplyFromDate}', width: 150, dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right', cellClassName: prFormulaCellClass,
			cellsrenderer: function(row, column, value){
				 if (value){
					 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
				 }
			}, 
		},
		{ text: '${uiLabelMap.BLApplyThruDate}', width: 150, dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right', cellClassName: prFormulaCellClass,
			cellsrenderer: function(row, column, value){
				 if (value){
					 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
				 }
			}, 
		},
	"/>
<div id='contextMenuProductFormula' style="display:none;">
	<ul>
		<li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.Delete)}</li>
	</ul>
</div>
	<@jqGrid filtersimplemode="true" id="jqxgridFromularProduct" addType="popup" dataField=datafileds columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" editable="false" addrefresh="true"  alternativeAddPopup="alterpopupWindow" customTitleProperties="BLListProductAndFormula"
		url="jqxGeneralServicer?sname=jQGetListFormulaProducts&formulaTypeId=${parameters.formulaTypeId?if_exists}" mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenuProductFormula" showlist="true"/>
</div>
<form id = "formAddNew">
	<div id="alterpopupWindow" class="hide popup-bound">
		<div>${uiLabelMap.BLAddFormulaProduct}</div>
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
							<span class="asterisk">${uiLabelMap.BLFormula}</span>
						</div>
						<div class="span7">
							<div id="formulaId" style="width: 100%" class="green-label">
								<div id="jqxgridListFormula">
					            </div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.BLApplyFromDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="fromDate" name="fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLApplyThruDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="thruDate" name="thruDate"></div>
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
</form>

<form id = "formEdit">
	<div id="editpopupWindow" class="hide popup-bound">
		<div>${uiLabelMap.BLEditFormulaProduct}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid'>
					<div class='row-fluid margin-top20'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.Product}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="productText" name="productText"></div>
							<input id="editProductId" type="hidden"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLFormula}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="formulaText" name="formulaText"></div>
							<input id="editFormulaId" type="hidden"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLApplyFromDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="fromDateEdit" name="fromDateEdit"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span4' style="text-align: right">
							<span>${uiLabelMap.BLApplyThruDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="thruDateEdit" name="thruDateEdit"></div>
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
</form>