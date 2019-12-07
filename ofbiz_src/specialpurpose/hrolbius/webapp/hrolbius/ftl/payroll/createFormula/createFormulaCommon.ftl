<style type="text/css">
	#jqxTabsOuter .jqx-tabs-content-olbius {
		height: 100% !important;
	}
	.borderLeftNone{
		border-left: none !important;
	}
	.borderTopNone{
		border-top: none !important;
	}
	.borderRightNone{
		border-right: none !important;
	}
	
	#jqxTabs .jqx-grid-olbius .jqx-grid-content{
		width: 100% !important
	}
	#jqxTabs .jqx-grid-olbius .jqx-grid-content{
		border-left: none !important 
	}
	
	#jqxTabsOuter .jqx-editor iframe{
		height: 80px;
	}
	
	.text-algin-center{
		text-align: center;
	}
	
	.width100{
		width: 100%;
	}
	
	.marginTop4{
		margin-top: 4px
	}
	
	.margin-bottom2{
		margin-bottom: 2px
	}
</style>

<div id="mainSplitter" class="borderLeftNone borderTopNone">
	<div style="overflow: auto !important;" class="borderLeftNone">
		<div id="nestedSplitter" class="inputFormula jqx-hideborder jqx-hidescrollbars borderLeftNone" style=" overflow: hidden !important;">
			<div class="enterFormula jqx-hideborder jqx-hidescrollbars" style="overflow: auto !important;">
				<div class='form-window-container'>
	           		<div class='form-window-content'>
	            		<div class='row-fluid'>
	            			<div class="operator-editor">
	           					<div class='span10'>
	           						<!-- <label class="control-label" style="margin-top: 5px; margin-left: 10px">${uiLabelMap.formulaFunction}</label> -->
	           						<div style="margin-left: 10px; margin-top: 10px" class="normalCode">
	            						<textarea name="function" class="autosize-transition" id="calc_des"></textarea>
	            						<input id="hid_popdes" type="hidden" >
										<input id="hid_cd_des" type="hidden" disabled="disabled">
										<input id="hid_cal_condition" type="hidden" >
			            			</div>	            					
	           						<div id="tbCaseInfo" style="margin-top: 10px"></div>
	           					</div>
	           					<div class="span2" style="text-align:center; margin: 0">
	           						<div class="normalCode">
	            						<div style="margin-top: 10px;">
		            						<button class="btn btn-mini btn-danger" type="button" id="removeCalcFunction" onclick="fn_removeword('calc_des',0);">
												<i class="icon-undo"></i>
												${uiLabelMap.CommonRemove}
											</button> 
											<button class="btn btn-mini btn-primary" type="button" id="addIfStat" onclick="fn_Conditionyn('y')" style="width: 70px">
												<i class="icon-code-fork"></i>
												${uiLabelMap.AddIfStatement}
											</button>
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
          			<button class="btn  btn-success btn-mini" id="AndBtn" type="button" onclick="cal_add(' AND ',' AND ', 4)">
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
				</button>
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
    	<div id="jqxTabs" class="borderLeftNone borderRightNone">
			<ul>
				<li>${uiLabelMap.formulaFunction}</li>
          		<li>${uiLabelMap.parameters}</li>
      		</ul>
        	<div>
      			<div id="jqxGridFormulaContainer">
       			<div id="jqxGridFormula" class="borderLeftNone borderRightNone"></div>
      			</div>
      		</div>
			<div>
	      		<div id="jqxGridParamContainer" >
	       			<div id="jqxGridParam" class="borderLeftNone borderRightNone"></div>
	      		</div>
      		</div>
		</div>
    </div>
</div>
<div class="" style="margin-top: 8px; margin-right: 5px">
	<button id="cancelBtn" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
	<button type="button" class='btn btn-primary form-action-button pull-right' id="saveBtn">
		<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
	<button type="button" class='btn btn-success form-action-button pull-right' id="backBtn">
		<i class='icon-arrow-left'></i>&nbsp;${uiLabelMap.CommonBack}</button>
</div>