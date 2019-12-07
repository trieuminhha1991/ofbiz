<#include "script/CreateNewFormulaScript.ftl"/> 
<style>
	#calc_des > div > .jqx-widget-content-olbiuseditor{
		height: 92px!important;
	}
</style>

<link rel="stylesheet" type="text/css" href="/hrresources/css/payroll/payroll.css">
<#--<!-- <#assign dataFieldFormula = "[{ name: 'code', type: 'string' },
							  { name: 'name', type: 'string' },
							  {name: 'payrollCharacteristicId', type: 'string'}]"/>
							  
<#assign columnlistFormula = "{text: '${uiLabelMap.formulaCode}', datafield: 'code', width: 220},
 					 		  {text: '${uiLabelMap.formulaName}', datafield: 'name', width: 220},
 					 		  {text: '${uiLabelMap.CommonCharacteristic}', datafield: 'payrollCharacteristicId',
								 cellsrenderer: function(row, column, value){
								 	for(var i = 0; i < characteristicArr.length; i++){
								 		if(characteristicArr[i].payrollCharacteristicId == value){
								 			return '<div style=\"margin-left: 3px\">' + characteristicArr[i].description + '</div>';
								 		}
								 	}		
								 	return '<div style=\"margin-left: 3px\">' + value + '</div>';
								 }	 
							 }" />		 -->
							 
<#assign dataFieldParam = "[{name: 'code', type: 'string' },
							{name: 'name', type: 'string' },
							{name: 'type', type: 'string'},
							{name: 'periodTypeId', type: 'string' },
							{name: 'paramCharacteristicId', type: 'string' }]"/> 							 					  
<#assign columnlistParam = "{text: '${uiLabelMap.HRPayrollCode}', datafield: 'code', width: 130, editable: false},
							{text: '${uiLabelMap.parameterName}', datafield: 'name', width: 150},
							{text: '${uiLabelMap.PayrollParamterType}', datafield: 'type', width: '240', editable: false, filtertype: 'checkedlist',
		 					 	cellsrenderer : function(row, column, value){
									for(var i = 0; i < allParameterType.length; i++){
										if(allParameterType[i].type &&  allParameterType[i].type == value){
											return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+ allParameterType[i].description+'</div>';		
										}
									}
									return '&nbsp;';
								},
		 					 },
		 					 {text: '${StringUtil.wrapString(uiLabelMap.CommonCharacteristic)}', datafield: 'paramCharacteristicId', width: '110', 
		 					 	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		 					 		for(var i = 0; i < paramCharacteristicArr.length; i++){
		 					 			if(value == paramCharacteristicArr[i].paramCharacteristicId){
			 					 			return '<span title=\"' + value + '\">' + paramCharacteristicArr[i].description + '</span>';
			 					 		}
		 					 		}
		 					 		if(!value){
										return '<span>${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}</span>';
									}
		 					 		return '<span>' + value + '</span>';
		 					 	}
		 					 },
		 					 { text: '${uiLabelMap.CommonPeriodType}', datafield: 'periodTypeId', editable: false, filtertype: 'checkedlist',
								cellsrenderer : function(row, column, value){
									var val = $('#jqxgrid').jqxGrid('getrowdata', row);
									for(var x in periodTypes){
										if(periodTypes[x].periodTypeId &&  periodTypes[x].periodTypeId == value){
											return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+periodTypes[x].description+'</div>';		
										}
									}
									if(!value){
							    		value = '${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}';
							    	}
							    	return '<span>' + value + '</span>';
								}
		 					 }"/> 
		 					 
<div class="row-fluid">
	<div id="settingFormula" class="hide">
		<div>${uiLabelMap.SettingFormula}</div>
		<div class="form-window-container">
			<div class='form-window-content' >
				<div class="row-fluid" >
		    		<div class="span12 boder-all-profile" 
		    				style="margin: 0 0 5px; padding: 5px 5px 5px">
			    		<div id="mainSplitter" class="borderLeftNone borderTopNone borderRightNone borderBottomNone">
							<div style="overflow: auto !important;" class="borderLeftNone">
								<div id="nestedSplitter" class="inputFormula jqx-hideborder jqx-hidescrollbars borderLeftNone" style=" overflow: hidden !important;">
									<div class="enterFormula jqx-hideborder jqx-hidescrollbars" style="overflow: auto !important;">
										<div class='form-window-container'>
							           		<div class='form-window-content'>
							            		<div class='row-fluid'>
							            			<div class="operator-editor">
							           					<div class='span11'>
							           						<!-- <label class="control-label" style="margin-top: 5px; margin-left: 10px">${uiLabelMap.formulaFunction}</label> -->
							           						<div style="margin-left: 10px; margin-top: 10px" class="normalCode">
							            						<textarea name="function" class="autosize-transition" id="calc_des"></textarea>
							            						
							            						<input id="hid_popdes" type="hidden" >
																<input id="hid_cd_des" type="hidden" disabled="disabled">
																<input id="hid_cal_condition" type="hidden" >
									            			</div>	            					
							           						<div id="tbCaseInfo" style="margin-top: 10px"></div>
							           					</div>
							           					<div class="span1" style="text-align:center; margin: 0">
							           						<div class="normalCode">
							            						<div style="margin-top: 10px;">
								            						<button class="btn btn-mini btn-danger" type="button" 
								            							id="removeCalcFunction" onclick="fn_removeword('calc_des',0);">
																		<i class="fa-backward icon-only"></i>
																	</button> 
																	<!-- <button class="btn btn-mini btn-primary" type="button" id="addIfStat" onclick="fn_Conditionyn('y')" style="width: 70px">
																		<i class="icon-code-fork"></i>
																		${uiLabelMap.AddIfStatement}
																	</button> -->
							            						</div>
							           						</div>
							           					</div>
							            			</div>
							            		</div>
							            		<div class='row-fluid'>
							            			
							            		</div>
							           		</div>
							           	</div>
									</div>
						          	<div class="operator borderTopNone" style="overflow: hidden !important; border: none !important;">
						          		<div class="" style="margin-left: 5px; margin-top: 5px; text-align: center;">
						          		<!-- <button class="btn  btn-success btn-mini" id="AndBtn" type="button" onclick="cal_add(' AND ',' AND ', 4)">
											<span class="btn-and">AND</span>
										</button> 
										<button class="btn  btn-success btn-mini" id="OrBtn" type="button" onclick="cal_add(' OR ',' OR ', 4)">
											<span class="btn-or">OR</span>
										</button>
										<button class="btn  btn-success btn-mini" id="lessThanOpBtn" type="button" onclick="cal_add('<','<', 5)">
											<span>&lt;</span>
										</button>
										<button class="btn  btn-success btn-mini" type="button" id="ltEqOpBtn" onclick="cal_add('<=','<=', 5)">
											<span>≤</span>
										</button>
										<button class="btn  btn-success btn-mini" type="button" id="gtEqBtn" onclick="cal_add('>=','>=', 5)">
											<span>≥</span>
										</button>
										<button class="btn  btn-success btn-mini" type="button" id="greaterThanOpBtn" onclick="cal_add('>','>', 5)">
											<span>&gt;</span>
										</button>
										<button class="btn  btn-success btn-mini" type="button" id="eqOpBtn" onclick="cal_add('=','=', 5)">
											<span>&#61;</span>
										</button>
										<button class="btn  btn-success btn-mini" type="button" id="notEqOpBtn" onclick="cal_add('!=','!=', 5)">
											<span>≠</span>
										</button> -->
										<button class="btn  btn-primary btn-mini" type="button" id="plusOperand" onclick="cal_add('+','+', 6)">
											<span>&#43;</span><!-- + -->
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="minusOperand" onclick="cal_add('-','-', 6)">
											<span>&#45;</span><!-- "-" -->
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="timesOperand" onclick="cal_add('*','*', 6)">
											<span>&#42;</span> <!-- "* -->
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="divideOperand" onclick="cal_add('/','/', 6)">
											<span>&#47;</span> <!-- "/" -->
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="dotCharacter" onclick="cal_add('.','.', 3)">
											<span>&#46;</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="openParenthesis" onclick="cal_add('(','(', 8)">
											<span>&#40;</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="closeParenthesis" onclick="cal_add(')',')', 8)">
											<span>&#41;</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="number1" onclick="cal_add('1','1', 3)">
											<span>1</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="number2" onclick="cal_add('2','2', 3)">
											<span>2</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="number3" onclick="cal_add('3','3', 3)">
											<span>3</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="number4" onclick="cal_add('4','4', 3)">
											<span>4</span></button>
										<button class="btn  btn-primary btn-mini" type="button" id="number5" onclick="cal_add('5','5', 3)">
											<span>5</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="number6" onclick="cal_add('6','6', 3)">
											<span>6</span>
										</button>									
										<button class="btn  btn-primary btn-mini" type="button" id="number7" onclick="cal_add('7','7', 3)">
											<span>7</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="number8" onclick="cal_add('8','8', 3)">
											<span>8</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="number9" onclick="cal_add('9','9', 3)">
											<span>9</span>
										</button>
										<button class="btn  btn-primary btn-mini" type="button" id="number0" onclick="cal_add('0','0', 3)">
											<span>0</span>
										</button>
										<button class="btn  btn-primary btn-mini  btn-space" type="button" id="SpaceBtn" onclick="cal_add(' ',' ', 3)">
											<span>Space</span>
										</button>
						          		</div>
						          	</div>
								</div>
							</div>
						    <div class="listFormulaAndParameters jqx-hideborder jqx-hidescrollbars" style="overflow: hidden !important;">
					        	<!-- <div>
					      			<div id="jqxGridFormulaContainer">
					       				<div id="jqxGridFormula" class="borderLeftNone"></div>
					      			</div>
					      		</div> -->
								<div>
						      		<div id="jqxGridParamContainer" >
						       			<div id="jqxGridParam" class="borderLeftNone"></div>
						      		</div>
					      		</div>
						    </div>
						</div>
		    		</div>
				</div>
			</div>
			<div class="form-action">
				<button id="cancelBuildFormula" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveBuildFormula">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSubmit}</button>
			</div>
		</div>
	</div>
</div>
<#--<!-- <@jqGrid url="jqxGeneralServicer?sname=JQGetAllFormula" dataField=dataFieldFormula columnlist=columnlistFormula
	jqGridMinimumLibEnable="false" idExisted="true" width="100%" bindresize="false" viewSize="7"
	editable="false" id="jqxGridFormula" height="240px" autoheight="false" pagesizeoptions="['7', '14', 20]"
	showtoolbar = "false" filterable="false" sortable="false" rowsheight="28"
/> -->
<@jqGrid url="jqxGeneralServicer?&hasrequest=Y&sname=JQGetPayrollParameters" dataField=dataFieldParam columnlist=columnlistParam
	jqGridMinimumLibEnable="false" filterable="false" width="100%" bindresize="false" viewSize="10" pagesizeoptions="['10', '15', 20]"
	editable="false" id="jqxGridParam" idExisted="true" height="315px" autoheight="false" sortable="false" rowsheight="28"
	showtoolbar = "false"/>	
	
<script type="text/javascript" src="/hrresources/js/payroll/CreateNewFormula.js"></script>
<script type="text/javascript" src="/hrresources/js/payroll/CreateNewFormulaCalculator.js"></script>		
 	
