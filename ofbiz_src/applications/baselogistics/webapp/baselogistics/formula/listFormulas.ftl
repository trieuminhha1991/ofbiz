<#include "script/listFormulasScript.ftl"/>
<div>
<#assign datafileds="[{ name: 'formulaId', type: 'string'},
					{ name: 'formulaCode', type: 'string'},
					{ name: 'formulaName', type: 'string'},
					{ name: 'formulaType', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'formulaValue', type: 'string'},
					{ name: 'description', type: 'string'},
					{ name: 'isDefault', type: 'string'},
			   ]"/>
<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaId)}', datafield: 'formulaCode', pinned: true, width:150, cellClassName: formulaCellClass,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaName)}', datafield: 'formulaName', pinned: true, width:150, cellClassName: formulaCellClass,
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Status)}', datafield: 'statusId', pinned: true, width:150, cellClassName: formulaCellClass,	 
			cellsrenderer: function(row, column, value){
				 if (value){
					 return '<span>'+ getFormulaStatusDescription(value)+'</span>';
				 }
		 	}, 
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLFormulaValue)}', datafield: 'formulaValue', minwidth: 300, cellClassName: formulaCellClass, 
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Description)}', datafield: 'description',width: 200, cellClassName: formulaCellClass, 
		},
	"/>
<div id="notifyContainer" >
	<div id="notifyContainer"></div>
</div>

<div id='contextMenuFormula' style="display:none;">
	<ul>
		<li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<li><i class="fa fa-times"></i>${StringUtil.wrapString(uiLabelMap.BLDeactivated)}</li>
		<li><i class="fa fa-signal"></i>${StringUtil.wrapString(uiLabelMap.BLActivated)}</li>
		<li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.Delete)}</li>
	</ul>
</div>
	<@jqGrid filtersimplemode="true" id="jqxgridFromula" addType="popup" dataField=datafileds columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" editable="false" addrefresh="true"  alternativeAddPopup="alterpopupWindowFormula" customTitleProperties="BLListFormula"
		url="jqxGeneralServicer?sname=jQGetListFormulas&formulaTypeId=${parameters.formulaTypeId?if_exists}" mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenuFormula" showlist="true"/>
</div>
<form id = "formFormulaAddNew">
	<div id="alterpopupWindowFormula" class="hide popup-bound">
		<div>${uiLabelMap.BLAddFormula}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid'>
					<div class='span12'>
						<div class='row-fluid'>
							<div class='span6'>
								<div class='row-fluid margin-top10'>
									<div class='span4' style="text-align: right">
										<span class="asterisk">${uiLabelMap.BLFormulaType}</span>
									</div>
									<div class="span7">
										<div id="newFormulaTypeId" style="width: 100%" class="green-label">
										</div>
							   		</div>
								</div>
								<div class='row-fluid margin-top10'>
									<div class='span4' style="text-align: right">
										<span class="asterisk">${uiLabelMap.BLFormulaId}</span>
									</div>
									<div class="span7">
										<input id="newFormulaCode" style="width: 100%" class="green-label" type="text">
										</input>
							   		</div>
								</div>
								<div class='row-fluid margin-top10'>
									<div class='span4' style="text-align: right">
										<span class="asterisk">${uiLabelMap.BLFormulaName}</span>
									</div>
									<div class="span7">
										<input id="newFormulaName" style="width: 100%" class="green-label" type="text">
										</input>
							   		</div>
								</div>
							</div>
							<div class='span6'>
								<div class='row-fluid margin-top10'>
									<div class='span2' style="text-align: right">
										<span>${uiLabelMap.Description}</span>
									</div>
									<div class="span7">
										<textarea id="newFormulaDescription" data-maxlength="500" rows="2" style="resize: vertical;margin-top:0px" class="span12"></textarea>
							   		</div>
								</div>
							</div>
						</div>
					</div>
					<div class='span10'>
						<div class='row-fluid'>
							<div class="span2" style="text-align: right">
								<span class="asterisk">${uiLabelMap.BLFormula}</span>
					   		</div>
							<div class="span8">
								<button class="btn  btn-primary btn-mini" type="button" id="plusOperand" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '+')">
									<span>&#43;</span><!-- + -->
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="minusOperand" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '-')">
									<span>&#45;</span><!-- "-" -->
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="timesOperand" onclick="javascript:ListFormula.insertCharacter('newFormulaValue','*')">
									<span>&#42;</span> <!-- "* -->
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="divideOperand" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '/')">
									<span>&#47;</span> <!-- "/" -->
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="dotCharacter" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '.')">
									<span>&#46;</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="openParenthesis" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '(')">
									<span>&#40;</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="closeParenthesis" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', ')')">
									<span>&#41;</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number1" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '1')">
									<span>1</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number2" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '2')">
									<span>2</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number3" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '3')">
									<span>3</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number4" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '4')">
									<span>4</span></button>
								<button class="btn  btn-primary btn-mini" type="button" id="number5" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '5')">
									<span>5</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number6" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '6')">
									<span>6</span>
								</button>									
								<button class="btn  btn-primary btn-mini" type="button" id="number7" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '7')">
									<span>7</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number8" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '8')">
									<span>8</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number9" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '9')">
									<span>9</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number0" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '0')">
									<span>0</span>
								</button>
								<button class="btn  btn-primary btn-mini  btn-space" type="button" id="SpaceBtn" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', ' ')">
									<span>Space</span>
								</button>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class="span2">
					   		</div>
							<div class="span8">
								<input id="newFormulaValue" data-maxlength="500" autocomplete="off" style="display: block" rows="2" style="resize: vertical;margin-top:0px" class="span12"></input>
								<div id="suggest" style="display: none;position: absolute;background-color: #FFFFFF;border: 1px solid #CCCCFF;width: 252px;z-index: 999;"></div>
					   		</div>
						</div>
					</div>
					<div class='span10'>
						<div class='row-fluid'>
							<div class="span2">
					   		</div>
							<div class="span8">
								<div id="jqxgridListParameters" style="width: 100%" class="green-label"></div>
					   		</div>
						</div>
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
		        <button id="newForCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="newForSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</form>

<form id = "formFormulaEdit">
	<div id="editpopupWindowFormula" class="hide popup-bound">
		<div>${uiLabelMap.BLEditFormula}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid'>
					<div class='span12'>
						<div class='row-fluid'>
							<div class='span6'>
								<div class='row-fluid margin-top10'>
									<div class='span4' style="text-align: right">
										<span class="asterisk">${uiLabelMap.BLFormulaType}</span>
									</div>
									<div class="span7">
										<div id="editFormulaTypeId" style="width: 100%" class="green-label">
										</div>
							   		</div>
								</div>
								<div class='row-fluid margin-top10'>
									<div class='span4' style="text-align: right">
										<span class="asterisk">${uiLabelMap.BLFormulaId}</span>
									</div>
									<div class="span7">
										<input id="editFormulaCode" style="width: 100%" class="green-label" type="text">
										</input>
							   		</div>
								</div>
								<div class='row-fluid margin-top10'>
									<div class='span4' style="text-align: right">
										<span class="asterisk">${uiLabelMap.BLFormulaName}</span>
									</div>
									<div class="span7">
										<input id="editFormulaName" style="width: 100%" class="green-label" type="text">
										</input>
							   		</div>
								</div>
							</div>
							<div class='span6'>
								<div class='row-fluid margin-top10'>
									<div class='span2' style="text-align: right">
										<span>${uiLabelMap.Description}</span>
									</div>
									<div class="span7">
										<textarea id="editFormulaDescription" data-maxlength="500" rows="2" style="resize: vertical;margin-top:0px" class="span12"></textarea>
							   		</div>
								</div>
							</div>
						</div>
					</div>
					<div class='span10'>
						<div class='row-fluid'>
							<div class="span2" style="text-align: right">
								<span class="asterisk">${uiLabelMap.BLFormula}</span>
					   		</div>
							<div class="span8">
								<button class="btn  btn-primary btn-mini" type="button" id="plusOperand" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '+')">
									<span>&#43;</span><!-- + -->
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="minusOperand" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '-')">
									<span>&#45;</span><!-- "-" -->
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="timesOperand" onclick="javascript:ListFormula.insertCharacter('newFormulaValue','*')">
									<span>&#42;</span> <!-- "* -->
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="divideOperand" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '/')">
									<span>&#47;</span> <!-- "/" -->
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="dotCharacter" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '.')">
									<span>&#46;</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="openParenthesis" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '(')">
									<span>&#40;</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="closeParenthesis" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', ')')">
									<span>&#41;</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number1" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '1')">
									<span>1</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number2" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '2')">
									<span>2</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number3" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '3')">
									<span>3</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number4" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '4')">
									<span>4</span></button>
								<button class="btn  btn-primary btn-mini" type="button" id="number5" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '5')">
									<span>5</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number6" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '6')">
									<span>6</span>
								</button>									
								<button class="btn  btn-primary btn-mini" type="button" id="number7" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '7')">
									<span>7</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number8" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '8')">
									<span>8</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number9" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '9')">
									<span>9</span>
								</button>
								<button class="btn  btn-primary btn-mini" type="button" id="number0" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', '0')">
									<span>0</span>
								</button>
								<button class="btn  btn-primary btn-mini  btn-space" type="button" id="SpaceBtn" onclick="javascript:ListFormula.insertCharacter('newFormulaValue', ' ')">
									<span>Space</span>
								</button>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class="span2">
					   		</div>
							<div class="span8">
								<input id="editFormulaValue" data-maxlength="500" autocomplete="off" style="display: block" rows="2" style="resize: vertical;margin-top:0px" class="span12"></input>
								<div id="editSuggest" style="display: none;position: absolute;background-color: #FFFFFF;border: 1px solid #CCCCFF;width: 252px;z-index: 999;"></div>
					   		</div>
						</div>
					</div>
					<div class='span10'>
						<div class='row-fluid'>
							<div class="span2">
					   		</div>
							<div class="span8">
								<div id="jqxgridListParametersEdit" style="width: 100%" class="green-label"></div>
					   		</div>
						</div>
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
		        <button id="editForCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="editForSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</form>