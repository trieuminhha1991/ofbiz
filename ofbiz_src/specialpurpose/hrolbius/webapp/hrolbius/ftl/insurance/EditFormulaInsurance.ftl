<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent no-bottom-border">
			<#if insuranceFormula?exists>
				<#assign url = "updateInsuranceFormula">
			<#else>	
				<#assign url = "createInsuranceFormula">
			</#if>
			<div id="" class="widget-body">
				<form action="<@ofbizUrl>${url}</@ofbizUrl>" class="basic-form form-horizontal" method="post" id="createSuspendInsuranceReason">
					<#assign step = 1>				
					<div class="row-fluid">
						<div class="">
							<div class="control-group no-left-margin ">
								<label class="">
									<label for="EditFormula_code" class="asterisk" id="EditFormula_code_title">${uiLabelMap.formulaCode}</label>  
								</label>
								<div class="controls">
									<#if insuranceFormula?has_content>
										${insuranceFormula.code}
										<#assign functionStr = insuranceFormula.function?if_exists>
										<#assign description =  insuranceFormula.description?if_exists>
										<input type="hidden" name="code" value="${insuranceFormula.code}">
									<#else>
										<input type="text" name="code" size="25" id="EditFormula_code">
									</#if>  
							 	</div>
							</div>
					
							<div class="control-group no-left-margin">
							    <label class="">
							    	<label for="EditFormula_name" class="asterisk" id="EditFormula_name_title">${uiLabelMap.formulaName}</label>  
						    	</label>
							    <div class="controls">
									<input type="text" name="description" size="25" value="${description?if_exists}" id="EditFormula_name">  
							    </div>
						    </div>
						<div class="control-group no-left-margin">
							<#if insuranceFormulaList?has_content>
							<#assign insuranceFormulaSize = insuranceFormulaList?size>
							<#assign nbrRows = (insuranceFormulaSize / step)?ceiling>    
							<h4>${uiLabelMap.formula}</h4>
							<div class="space-4"></div>
							<table class="table table-bordered dataTable">
								<#list 1..nbrRows as i>
									<tr>
										<#list ((i-1)*step + 1)..(i*step) as j>		
											<#if (j <= insuranceFormulaSize)>
												<td style="background-color: #f5f5f5">
													<#assign functionParam = delegator.findByAnd("InsuranceFormulaParameters", Static["org.ofbiz.base.util.UtilMisc"].toMap("code", insuranceFormulaList.get(j-1).code), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"),false)>
													<#if functionParam?has_content>
														<#assign tempFunctionStr = insuranceFormulaList.get(j-1).code + "(">
														<#list functionParam as param>	
															<#assign tempFunctionStr = tempFunctionStr + param.parameterName>
															<#if param_has_next>
																<#assign tempFunctionStr = tempFunctionStr + ","> 
															</#if>
														</#list>
														<#assign tempFunctionStr = tempFunctionStr + ")">
														<a href="javascript:void(0)" onclick="cal_add('${tempFunctionStr}','${insuranceFormulaList.get(j-1).description}', 1, true, true)">${tempFunctionStr}</a>
													<#else>	
														<#assign tempFunctionStr = insuranceFormulaList.get(j-1).code + "()">
														<a href="javascript:void(0)" onclick="cal_add('${tempFunctionStr}','${insuranceFormulaList.get(j-1).description}', 1, true)">${tempFunctionStr}</a>
													</#if>
																		
												</td>
												<td>
													${insuranceFormulaList.get(j-1).description?if_exists}
												</td>
											<#else>
												<td style="background-color: #f5f5f5"></td>
												<td></td>
											</#if>										
										</#list>
									</tr>
								</#list>
							</table>
							</#if>
						</div>
						<div class="control-group no-left-margin">
							<#if insuranceParametersList?has_content>
								<#assign insuranceParameterSize = insuranceParametersList?size>
								<#assign insuranceParameterRows = (insuranceParameterSize/step)?ceiling>
								<h4>${uiLabelMap.parameters}</h4>
								<div class="space-4"></div>
								<table class="table table-bordered dataTable">
									<#list 1..insuranceParameterRows as i>
										<tr>
											<#list ((i-1)*step + 1)..(i*step) as j>		
												<#if (j <= insuranceParameterSize)>
													<td style="background-color: #f5f5f5">
														<a href="javascript:void(0)" onclick="cal_add('${insuranceParametersList.get(j-1).insuranceParameterId}', '${insuranceParametersList.get(j-1).description}', 1, false)">${insuranceParametersList.get(j-1).insuranceParameterId}</a>					
													</td>
													<td>
														${insuranceParametersList.get(j-1).description}
													</td>
												<#else>
													<td></td>
													<td></td>
												</#if>										
											</#list>
										</tr>
									</#list>
								</table>
							</#if>
						</div>
						</div>
						<div class="salary-calculate-pad">	
							<div class="row-fluid">
								<div class="span5">	
									<div class="simple-operator">				
										<!-- <h3>${uiLabelMap.CalcOperation}</h3> -->
										<!-- <button class="btn btn-app btn-success btn-small" id="AndBtn" type="button" onclick="cal_add(' AND ',' AND ', 4)">
											<span class="btn-and">AND</span>
										</button> 
										<button class="btn btn-app btn-success btn-small" id="OrBtn" type="button" onclick="cal_add(' OR ',' OR ', 4)">
											<span class="btn-or">OR</span>
										</button> -->
										<button class="btn btn-app btn-primary btn-small" type="button" id="plusOperand" onclick="cal_add('+','+', 6)">
											<span>&#43;</span><!-- + -->
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="minusOperand" onclick="cal_add('-','-', 6)">
											<span>&#45;</span><!-- "-" -->
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number1" onclick="cal_add('1','1', 3)">
											<span>1</span>
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number2" onclick="cal_add('2','2', 3)">
											<span>2</span>
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number3" onclick="cal_add('3','3', 3)">
											<span>3</span>
										</button>
										<div class="clearfix"></div>
										
										<!-- <button class="btn btn-app btn-success btn-small" id="lessThanOpBtn" type="button" onclick="cal_add('<','<', 5)">
											<span>&lt;</span>
										</button>
										<button class="btn btn-app btn-success btn-small" type="button" id="greaterThanOpBtn" onclick="cal_add('>','>', 5)">
											<span>&gt;</span>
										</button> -->
										<button class="btn btn-app btn-primary btn-small" type="button" id="timesOperand" onclick="cal_add('*','*', 6)">
											<span>&#42;</span> <!-- "* -->
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="divideOperand" onclick="cal_add('/','/', 6)">
											<span>&#47;</span> <!-- "/" -->
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number4" onclick="cal_add('4','4', 3)">
											<span>4</span></button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number5" onclick="cal_add('5','5', 3)">
											<span>5</span>
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number6" onclick="cal_add('6','6', 3)">
											<span>6</span>
										</button>									
										<div class="clearfix"></div>
											
										<!-- <button class="btn btn-app btn-success btn-small" type="button" id="ltEqOpBtn" onclick="cal_add('<=','<=', 5)">
											<span>≤</span>
										</button>
										<button class="btn btn-app btn-success btn-small" type="button" id="gtEqBtn" onclick="cal_add('>=','>=', 5)">
											<span>≥</span>
										</button> -->
										<button class="btn btn-app btn-primary btn-small" type="button" id="openParenthesis" onclick="cal_add('(','(', 8)">
											<span>&#40;</span>
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="closeParenthesis" onclick="cal_add(')',')', 8)">
											<span>&#41;</span>
											<!-- ${uiLabelMap.CloseParenthesis} -->
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number7" onclick="cal_add('7','7', 3)">
											<span>7</span>
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number8" onclick="cal_add('8','8', 3)">
											<span>8</span></button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number9" onclick="cal_add('9','9', 3)">
											<span>9</span></button>
										<div class="clearfix"></div>
										
										<!-- <button class="btn btn-app btn-success btn-small" type="button" id="eqOpBtn" onclick="cal_add('=','=', 5)">
											<span>&#61;</span>
										</button>
										<button class="btn btn-app btn-success btn-small" type="button" id="notEqOpBtn" onclick="cal_add('!=','!=', 5)">
											<span>≠</span>
										</button> -->
										<button class="btn btn-app btn-primary btn-small  btn-space" type="button" id="SpaceBtn" onclick="cal_add(' ',' ', 3)">
											<span>Space</span>
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="number0" onclick="cal_add('0','0', 3)">
											<span>0</span>
										</button>
										<button class="btn btn-app btn-primary btn-small" type="button" id="dotCharacter" onclick="cal_add('.','.', 3)">
											<span>&#46;</span>
										</button>
									</div>
								</div>
								<div class="span7">
									<div class="operator-editor">									
										<div id="normalCode" >
											<span>${uiLabelMap.formulaFunction}</span>  
											<div class="float-right" style="margin-right: 3px; margin-bottom: 10px">
												<button class="btn btn-mini btn-danger" type="button" id="removeCalcFunction" onclick="fn_removeword('calc_des',0);">
													<i class="icon-undo"></i>
													${uiLabelMap.CommonRemove}
												</button> 
												<!-- <button class="btn btn-mini btn-primary" type="button" id="addIfStat" onclick="fn_Conditionyn('y')">
													<i class="icon-code-fork"></i>
													${uiLabelMap.AddIfStatement}
												</button> -->
											</div>	
											<#--<!-- <textarea name="function" style="height:80px;" class="autosize-transition span12"  
												id="calc_des" onfocus="FocusColor2(this);fn_Selectitem(this);">${functionStr?if_exists}</textarea> -->
											<div id="calc_des" onfocus="FocusColor2(this);fn_Selectitem(this)"></div>
											<input type="hidden" name="function" id="function">	
											<input id="hid_popdes" type="hidden" >
											<input id="hid_cd_des" type="hidden" disabled="disabled">
											<input id="hid_cal_condition" type="hidden" >
											<input type="hidden" value="NORMAL" name="functionType">
										</div> 
										<div id="complexCode" style="display: block; ">
											<table id="tbCaseInfo" style="margin: 10px 0 10px 0">
												
											</table>
										</div>
									 	
										<button type="submit" class="btn btn-primary btn-small" name="submitButton" id="submitButton">
											<i class="icon-save bigger-125"></i>
											<#if payrollFormula?exists>
												${uiLabelMap.CommonUpdate}
											<#else>
												${uiLabelMap.CommonSave}
											</#if>
										</button>
								  	</div>
						  		</div>
					  		</div>
					  	</div>
				  	</div>
				</form>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	 <div id="jqxWindow">
		<div id="windowHeader">
			<span>
             	${uiLabelMap.SetParamForFormula}           
             </span>
		</div> 
		<div id="windowContent">
			<div class="row-fluid form-horizontal">
				<div id="paramContent"></div>
				<div class="control-group">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<input style='margin-top: 20px;' value="${uiLabelMap.CommonSubmit}" type="button" id='setParam' />
						<input style='margin-top: 20px;' value="${uiLabelMap.CommonClose}" type="button" id='cancelSetParam' />
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript">
//var nbrIfElseBlock = 0;
var iTotcnt = 0; // total if-else condition
var iAddno = 0; 
var oOrderList = new Array();
var oSelitem;
var ohidSelitem;
var ocalSelitem;
var allFunctionAndParam = new Array();
var colvalSelected = "";
var gubunSelected = -1;
var paramInFormulaSelected = new Array();
function fn_Conditionyn(vi){
    if(vi == "y"){
        /* $(".traddcase").css("display","");
        $(".graydot").css("display","none"); */
        jQuery("#normalCode").css("display","none");
        fn_SetCondition("n","n","","");
        $("#ipColDes1").focus();//replace focus() with focusToEnd()
    }
    else{
        var chkvalue = "";
        $("#tbCaseInfo").find("tr").each(function(){
            chkvalue += $(this).find("td > input[name='hidval']:hidden").val();
        });

        if(chkvalue != "")
            if(!confirm('Điều kiện phụ cũng sẽ bị xóa.\n\nDữ liệu sẽ không thể phục hồi sau khi bị xóa.'))
            return;

        //	$(".traddcase").css("display","none");
        $("#normalCode").css("display","");
        $("#tbCaseInfo").find("tr").remove();
        $("#tbCaseInfo").css("display","none");
        iTotcnt = 0;
        iAddno = 0;
        jQuery("#normalCode").css("display","");
        $("#calc_des").focus();
        oOrderList = [];
    }
}

jQuery(document).ready(function () {
	jQuery("#calc_des").jqxEditor({
	    height: 130,
	    width: 605,
	    theme: 'olbius',
	    tools: '',
	    disabled: true
	});
	jQuery("#calc_des").val("");
	
	 $("#setParam").jqxButton({ width: '100', theme:'olbius'});
	 $("#cancelSetParam").jqxButton({ width: '100', theme : 'olbius'});
	jQuery("#jqxWindow").jqxWindow({
        showCollapseButton: true, width: 600, autoOpen:false, theme: 'olbius',
        initContent: function () {
            
        }
    });
	$("#setParam").click(function(event){
		jQuery("#jqxWindow").jqxWindow('close');
		var cid = jQuery(oSelitem).get(0).id;
		if(gubunSelected > -1 && colvalSelected.length > 0){
			if(paramInFormulaSelected.length > 0){
				for(var i = 0; i < paramInFormulaSelected.length; i++){
					var numberParam = $("#param_" + i).val();
					colvalSelected = colvalSelected.replace(paramInFormulaSelected[i], numberParam);
				}
			}
			setFormula(colvalSelected, gubunSelected, cid);	
			gubunSelected = -1;
			colvalSelected = "";
		}
		
	});
	$("#cancelSetParam").click(function(event){
		jQuery("#jqxWindow").jqxWindow('close');
	});
	jQuery("#EditFormula_code").focus();
	var validator = jQuery("#createSuspendInsuranceReason").validate({
		errorElement: 'span',
		errorClass: 'help-inline',
		focusInvalid: true,
		rules: {
			code: {
				required: true,
			},
			description: {
				required: true,
			},
		},
		messages: {
			code: {
				required: "${uiLabelMap.CommonRequired}",
			},
			description: {
				required: "${uiLabelMap.CommonRequired}",
			},
		},
		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
			/* var errors = validator.numberOfInvalids();
	        if (errors) {                    
	            validator.errorList[0].element.focus();
	        } */
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		submitHandler: function (form) {
			$("#function").val(jQuery("#calc_des").val());
			form.submit();
		},
		invalidHandler: function (form) {
		}
	});
	/* jQuery("#submitButton").click(function(){
		if(!jQuery('#createFormula').valid()){
			validator.focusInvalid();
			return false;
		} 
		var params = jQuery("#createFormula").serializeArray();
		var codeValue = {};
		
		if(iTotcnt > 0){
			codeValue.name = "functionJson";
			jQuery("#calc_des").val('');
			var tempIfElse = {};
			var rootTrIfElse = "trDefine1";
			tempIfElse.statement = jQuery("#ipColDes1").val();
			tempIfElse.if_true = getTrueCond("trDefine1", 1);
			tempIfElse.if_false = getFalseCond("trDefine1", 1);
			var value = JSON.stringify(tempIfElse);
			codeValue.value = escapeHtml(value);
		}else{
			//codeValue.name = "function";
			//codeValue.value = jQuery("#calc_des").val();
		}
		params.push(codeValue);
		var functionRelated = "";
		if(allFunctionAndParam.length){
			for(var i = 0; i < allFunctionAndParam.length - 1; i++){
				functionRelated += allFunctionAndParam[i] + ",";
			}
			functionRelated += allFunctionAndParam[allFunctionAndParam.length - 1]
			params.push({name: "functionRelated", value: functionRelated});	
		}
		jQuery.ajax({
			url: "<@ofbizUrl>createInsuranceFormula</@ofbizUrl>",
			type: 'POST',
			data: params,
			dataType: "json",
			success: function(data){
				bootbox.dialog({
				  message: "${StringUtil.wrapString(uiLabelMap.PayrollSuccess)}",
				  title: "${StringUtil.wrapString(uiLabelMap.PayrollNotify)}",
				  buttons: {
				    success: {
				      label: "${StringUtil.wrapString(uiLabelMap.PayrollCommonSuccess)}!",
				      className: "btn-success",
				     }
				  }
				});
			}
		}).fail(function(){
			bootbox.dialog({
				  message: "${StringUtil.wrapString(uiLabelMap.PayrollError)}",
				  title: "${StringUtil.wrapString(uiLabelMap.PayrollNotify)}",
				  buttons: {
				    danger: {
				      label: "${StringUtil.wrapString(uiLabelMap.PayrollCommonDanger)}!",
				      className: "btn-danger",
				    }
				  }
				});
		});

	}); */
	
	jQuery(".operator-editor").bind('keydown', function(event){
		var keycode = event.which;
		if(keycode == 188){//<=
			jQuery("#ltEqOpBtn").trigger("click");
		}else if(keycode == 190){//>=
			jQuery("#gtEqBtn").trigger("click");		
		}
	});
	
	jQuery(".operator-editor").bind('keypress', function(event){
		var keycode = event.which;
		//var event.ctrlKey
		if(keycode == 124){//"|"
			jQuery("#OrBtn").trigger("click");			
		}else if(keycode == 38){// "&"
			jQuery("#AndBtn").trigger("click");
		}else if(keycode == 40){// "("
			jQuery("#openParenthesis").trigger("click");
		}else if(keycode == 41){// ")"
			jQuery("#closeParenthesis").trigger("click");
		}else if(keycode == 43){//"+"
			jQuery("#plusOperand").trigger("click");
		}else if(keycode == 45){//"-"
			jQuery("#minusOperand").trigger("click");
		}else if(keycode == 42){// "*"
			jQuery("#timesOperand").trigger("click");
		}else if(keycode == 47){//"/"
			jQuery("#divideOperand").trigger("click");
		}else if(keycode == 32){//"space"
			jQuery("#SpaceBtn").trigger("click");
		}else if(keycode == 46){//"."
			jQuery("#dotCharacter").trigger("click");
		}else if(keycode == 61){//"="
			jQuery("#eqOpBtn").trigger("click");
		}else if(keycode == 33){//"!="
			jQuery("#notEqOpBtn").trigger("click");
		}else if(keycode == 60 && !event.ctrlKey){//"<"
			jQuery("#lessThanOpBtn").trigger("click");
		}else if(keycode == 62 && !event.ctrlKey){//">"
			jQuery("#greaterThanOpBtn").trigger("click");
		}else if(keycode == 48){//0
			jQuery("#number0").trigger("click");
		}else if(keycode == 49){//1
			jQuery("#number1").trigger("click");
		}else if(keycode == 50){//2
			jQuery("#number2").trigger("click");
		}else if(keycode == 51){//3
			jQuery("#number3").trigger("click");
		}else if(keycode == 52){//4
			jQuery("#number4").trigger("click");
		}else if(keycode == 53){//5
			jQuery("#number5").trigger("click");
		}else if(keycode == 54){//6
			jQuery("#number6").trigger("click");
		}else if(keycode == 55){//7
			jQuery("#number7").trigger("click");
		}else if(keycode == 56){//8
			jQuery("#number8").trigger("click");
		}else if(keycode == 57){//9
			jQuery("#number9").trigger("click");
		}
		 //jQuery("#number1").trigger("click");
	});
	 
});


function getTrueCond(trRootId, condNo){
	var childCond = jQuery("tr[parent_id="+ trRootId +"_True]")[0];
	if(jQuery("#trTrue" + condNo).css('display') != 'none'){
		return jQuery("#ipColTrue" + condNo).val();
	}else if(childCond){
		var childCondId = childCond.id;
		var childCondNo = childCondId.substring("trDefine".length);
		var tempRet = {};
		tempRet.statement = jQuery("#ipColDes" + childCondNo).val();
		tempRet.if_true = getTrueCond(childCondId, childCondNo);
		tempRet.if_false = getFalseCond(childCondId, childCondNo) 
		return tempRet; 
	}
}

function getFalseCond(trRootId, condNo){
	var childCond = jQuery("tr[parent_id="+ trRootId +"_False]")[0];
	if(jQuery("#trFalse" + condNo).css('display') != 'none'){
		return jQuery("#ipColFalse" + condNo).val();
	}else if(childCond){
		var childCondId = childCond.id;
		var childCondNo = childCondId.substring("trDefine".length);
		var tempRet = {};
		tempRet.statement = jQuery("#ipColDes" + childCondNo).val();
		tempRet.if_true = getTrueCond(childCondId, childCondNo);
		tempRet.if_false = getFalseCond(childCondId, childCondNo) 
		return tempRet; 
	}
}

//@param:
// 	tr_parent: if-else block inner have parent_id is: "id of if-else block outer" + "suffix" ( _True or _False)
function fn_SetCondition(pos, val_yn, del_id, tr_parent){ 
    var strBtnCon = 'IF';  
    var strBtnDel = 'Xóa'; 
    var strBtnCC = 'Hủy'; 
    var strCaseName = 'IF Điều kiện'; 


    var strTag;

    if(pos == "n" && val_yn == "n"){
    	iTotcnt++;
        iAddno++;
        
        strTag = '<tr class="trgroup1" id="trDefine1"><th>['+ strCaseName + '1]</th>';
        strTag += '<td class="center"><input type="text" id="ipColDes1" class="default" style="ime-mode:disabled;background-color:#f7f7f7;"' 
        			+ 'onblur="BlurColor3(this);text_check(\'ipColDes1\',300);" readonly onfocus="FocusColor2(this);fn_Selectitem(this);" onkeypress="fn_nopress(this)" />';
        strTag += '<input type="hidden" name="hidpushpop" id="hidPopDes1"/><input type="hidden" name="hidval" disabled="disabled" id="hidColDes1"/><input type="hidden" name="cal_val" id="hidCalc_FC1"/></td>';
        strTag += '<td class="info"><button class="btn btn-mini btn-danger icon-reply marginTop-10" onclick="fn_removeword(\'ipColDes1\');" >'+strBtnDel+' </button>';
        strTag += '<button class="btn btn-mini btn-primary icon-remove marginTop-10" onclick="fn_Conditionyn(\'n\')" >'+strBtnCC+'</button></td></tr>';     

        strTag += '<tr id="trTrue1" class="trgroup1"><th class="p_l30px">[True1]</th>';
        strTag += '<td class="center"><input type="text" id="ipColTrue1" class="default" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColTrue1\',300);" readonly onfocus="FocusColor2(this);fn_Selectitem(this);" onkeypress="fn_nopress(this)" />';
        strTag += '<input type="hidden" name="hidpushpop" id="hidPopTDes1"/><input type="hidden"  name="hidval" disabled="disabled" id="hidTrueDes1"/><input type="hidden" name="cal_val" id="hidCalc_FT1"/></td>';
        strTag += '<td class="info"><button type="button" class="btn btn-mini btn-danger icon-reply marginTop-10" onclick="fn_removeword(\'ipColTrue1\');">'+strBtnDel+'</button>';
        strTag += '<button class="btn btn-mini btn-primary icon-code-fork marginTop-10" onclick="fn_SetCondition(\'trTrue1\',\'y\',\'trTrue1\', \'trDefine1_True\');">'+ strBtnCon +'</button></td></tr>';

        strTag += '<tr id="trFalse1" class="trgroup1"><th class="p_l30px">[False1]</th>';
        strTag += '<td class="center"><input type="text" id="ipColFalse1" class="default" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColFalse1\',300);" readonly onfocus="FocusColor2(this);fn_Selectitem(this);" onkeypress="fn_nopress(this)" />';
        strTag += '<input type="hidden" name="hidpushpop" id="hidPopFDes1"/><input type="hidden"  name="hidval" disabled="disabled" id="hidFalseDes1"/><input type="hidden" name="cal_val" id="hidCalc_FF1"/></td>';
        strTag += '<td class="info"><button class="btn btn-mini btn-danger icon-reply marginTop-10" onclick="fn_removeword(\'ipColFalse1\');">'+strBtnDel+'</button>';
        strTag += '<button class="btn btn-mini btn-primary icon-code-fork marginTop-10" onclick="fn_SetCondition(\'trFalse1\',\'y\',\'trFalse1\', \'trDefine1_False\')">'+ strBtnCon +'</button></td></tr>';

        jQuery("#tbCaseInfo").append(strTag);
        jQuery("#tbCaseInfo").css("display","");
        
        jQuery("#ipColDes1").focus(); 
    }
    else{
        if(val_yn == "y"){
        	iTotcnt++;
            iAddno++;
            /* if(iTotcnt > 10){
                alert('Bạn không thể tạo nhiều hơn 10 điều kiện.'); //10개 이상 조건을 생성 할 수 없습니다.
                return false;
            } */
            strTrtag1 = "trTrue" + iAddno;
            strTrtag2 = "trFalse" + iAddno;
            var strTxt= pos.substr(2,pos.length);
                              
            strTag = '<tr class="trgroup'+ iAddno +' '+ del_id +'" id="trDefine'+iAddno+'" parent_id="' + tr_parent + '"><th id="'+strTxt+'">'+strTxt+' ['+ strCaseName + iAddno + ']</th>';
            strTag += '<td class="center"><input type="text" id="ipColDes'+ iAddno+'" class="default" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColDes'+iAddno+'\',300);" readonly onfocus="fn_Selectitem(this)" onkeypress="fn_nopress(this)" />';
            strTag += '<input type="hidden" name="hidpushpop" id="hidPopDes'+iAddno+'"/><input type="hidden" name="hidval" disabled="disabled" id="hidColDes'+iAddno+'"/><input type="hidden" name="cal_val" id="hidCalc_FC'+iAddno+'"/></td>';
            strTag += '<td class="info"><button class="btn btn-mini btn-danger icon-reply marginTop-10" onclick="fn_removeword(\'ipColDes'+iAddno+'\');">'+strBtnDel+' </button>';
            strTag += '<button class="btn btn-mini btn-primary icon-remove marginTop-10" onclick="fn_SetCondition(\''+ pos +'\',\'n\',\'\')">'+strBtnCC+'</button></td></tr>';     
            
            strTag += '<tr id="trTrue'+iAddno+'" class="trgroup'+ iAddno +' '+ del_id+'"><th class="p_l30px" id="'+strTxt+'">[True'+iAddno+']</th>';
            strTag += '<td class="center"><input type="text" id="ipColTrue'+iAddno+'" class="default" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColTrue'+iAddno+'\',300);" readonly onfocus="fn_Selectitem(this)" onkeypress="fn_nopress(this)" />';
            strTag += '<input type="hidden" name="hidpushpop" id="hidPopTDes'+iAddno+'"/><input type="hidden" name="hidval" disabled="disabled" id="hidTrueDes'+iAddno+'"/><input type="hidden" name="cal_val" id="hidCalc_FT'+iAddno+'"/></td>';
            strTag += '<td class="info"><button class="btn btn-mini btn-danger icon-reply marginTop-10" onclick="fn_removeword(\'ipColTrue'+iAddno+'\');">'+strBtnDel+'</button>';
            strTag += '<button class="btn btn-mini btn-primary icon-code-fork marginTop-10" onclick="fn_SetCondition(\'trTrue'+iAddno+'\',\'y\',\'trTrue'+ iAddno+ ' ' + del_id +'\', \'trDefine'+ iAddno +'_True\');">'+ strBtnCon +'</button></td></tr>';
        
            strTag += '<tr id="trFalse'+iAddno+'" class="trgroup'+ iAddno +' '+ del_id+'"><th class="p_l30px" id="'+strTxt+'">[False'+iAddno+']</th>';
            strTag += '<td class="center"><input type="text" id="ipColFalse'+iAddno+'" class="default" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColFalse'+iAddno+'\',300);" readonly onfocus="FocusColor2(this);fn_Selectitem(this)" onkeypress="fn_nopress(this)" />';
            strTag += '<input type="hidden" name="hidpushpop" id="hidPopFDes'+iAddno+'"/><input type="hidden" name="hidval" disabled="disabled" id="hidFalseDes'+iAddno+'"/><input type="hidden" name="cal_val" id="hidCalc_FF'+iAddno+'"/></td>';
            strTag += '<td class="info"><button class="btn btn-mini btn-danger icon-reply marginTop-10" onclick="fn_removeword(\'ipColFalse'+iAddno+'\');">'+strBtnDel+'</button>';
            strTag += '<button class="btn btn-mini btn-primary icon-code-fork marginTop-10" onclick="fn_SetCondition(\'trFalse'+iAddno+'\',\'y\',\'trFalse'+ iAddno+ ' ' + del_id + '\', \'trDefine'+ iAddno +'_False\')" >'+ strBtnCon +'</button></td></tr>';

            jQuery("#"+pos).after(strTag);
            jQuery("#"+pos).css("display","none");
            jQuery("#"+pos).find("input:text").val("");
            jQuery("#"+pos).find("input[name=hidpushpop]").val("");
            jQuery("#"+pos).find("input[name=hidval]").val("");
            jQuery("#"+pos).find("input[name=cal_val]").val("");
            jQuery("#ipColDes"+iAddno).focus();
            oOrderList.push(pos + "-" + val_yn + "-" + del_id);
        }
        else{
            var chkvalue = "";
            $("tr[class*='"+pos+"']").each(function(){
                chkvalue += $(this).find("td > input[name='hidval']:hidden").val();
            });

            if(chkvalue != "")
                 if(!confirm('Điều kiện phụ cũng sẽ bị xóa.\n\nDữ liệu sẽ không thể phục hồi sau khi bị xóa.'))
                    return;
            
            $("#"+pos).css("display","");
            var ichk = 0;
            $("tr[class*='"+pos+"']").each(function() {
                $(this).remove();
                ichk++;
            });
            
            ichk = parseInt(ichk/3);
            iTotcnt = iTotcnt - ichk;
            oOrderList.push(pos + "-" + val_yn + "-" + del_id);
        }
    }
}

function fn_nopress(obj){ 
    /* if(event.keyCode==8)
        event.returnValue = false;
    if(event.keyCode == 13){
        if($(obj).closest("tr").next().css("display") == "none")
            $(obj).closest("tr").next().next().find("input:text").focus();
        else
            $(obj).closest("tr").next().find("input:text").focus();
    }
    else
        event.returnValue=false; */
}

function fn_Selectitem(obj){
    oSelitem = obj;
    if($(obj).get(0).id == "calc_des"){
        oSelpopitem = $("#hid_popdes");
        ohidSelitem = $("#hid_cd_des");
        ocalSelitem = $("#hid_cal_condition");
    }
    else{
         oSelitem = obj;
         oSelpopitem = $(obj).closest("tr").find("td > input[name='hidpushpop']:hidden");
        ohidSelitem = $(obj).closest("tr").find("td > input[name='hidval']:hidden");
        ocalSelitem = $(obj).closest("tr").find("td > input[name='cal_val']:hidden"); 
    }
}

function fn_removeword(obj){
    oSelitem = jQuery("#"+obj);
    var textVar = oSelitem.val();
    
    if(obj == "calc_des"){
        oSelpopitem = $("#hid_popdes");
        ohidSelitem = $("#hid_cd_des");
        ocalSelitem = $("#hid_cal_condition");
    }
    else{
        oSelpopitem = $(oSelitem).closest("tr").find("td > input[name='hidpushpop']:hidden");
        ohidSelitem = $(oSelitem).closest("tr").find("td > input[name='hidval']:hidden");
        ocalSelitem = $(oSelitem).closest("tr").find("td > input[name='cal_val']:hidden");
    }
	//var indexArr = jQuery().val() == "" ? 0 : jQuery().val().split('_');
	//var index = indexArr.pop();
	
	//jQuery(oSelpopitem).val(indexArr.join("_"));
    
	var arDespop = $(oSelpopitem).val() == "" ? [] : $(oSelpopitem).val().split('_');
	var index = arDespop.pop();
    $(oSelpopitem).val(arDespop.join("_"));
	if(!index){
		index = textVar.length;
	}
    //remove a word
    oSelitem.val(textVar.substring(0, textVar.length - index));
    
    var arCode = $(ohidSelitem).val() == "" ? [] : $(ohidSelitem).val().split('_');
    arCode.pop();
    $(ohidSelitem).val(arCode.join("_")); 

    var arCalc = $(ocalSelitem).val() == "" ? [] : $(ocalSelitem).val().split('_');
    arCalc.pop();
    $(ocalSelitem).val(arCalc.join("_"));

    //jQuery(oSelitem).val($(ohidSelitem).val().replace(/_/gi,""));
    
    jQuery(oSelitem).focus().val(jQuery(oSelitem).val());
}

/* var entityMap = {
	    "&": "&amp;",
	    "<": "&lt;",
	    ">": "&gt;",
	    '"': '&quot;',
	    "'": '&#39;',
	    "/": '&#x2F;'
}; */

function escapeHtml(string) {
   return string.replace(/</g, "lt").replace(/>/g, "gt");
}

function cal_add(colval,des,gubun, is_function, has_param){	
	if(!oSelitem){
		jQuery("#calc_des").focus();
	}
    var cid = jQuery(oSelitem).get(0).id;

    if(cid.indexOf("ipColDes") < 0 && (gubun == 2 || gubun == 4 || gubun == 5)){
        alert("Nó chỉ được dùng trong điều kiện IF");
        jQuery(oSelitem).focus();
        return;
    }
    var col = jQuery(ohidSelitem).val() == "" ? [] : jQuery(ohidSelitem).val().split('_');
    var SelCalc = $(ocalSelitem).val() == "" ? [] : $(ocalSelitem).val().split('_');
    var last_gubun = SelCalc[SelCalc.length-1];
  	
    switch(gubun)
	    {
    		case 0:
	        case 1:
	            if(col.length > 0){
	                if(last_gubun == gubun || last_gubun == "2" || last_gubun == "3" || (last_gubun == "8" && col[col.length-1] == ")")){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); 
	                    $(oSelitem).focus();
	                    return;
	                }
	            }
	        break;
	        case 2:
	            if(col.length > 0){
	                if(last_gubun != "4"){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }
	            }
	        break;
	        case 3: 
	            if(col.length > 0){
	                if(last_gubun == "1" || last_gubun == "2"){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }
	                if(last_gubun == "3"){
	                    var arNum = [];
	                    for(var i = SelCalc.length - 1; i > -1 ; i--){
	                        if(SelCalc[i] == "3")
	                            arNum.push(col[i]);
	                        else
	                            break;
	                    }

	                    if(arNum.length > 0){
	                        if(arNum[arNum.length-1] =="."){
	                            alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                            $(oSelitem).focus();
	                            return;
	                        }
	                        else
	                        {
	                            var chkpoint = 0;
	                            for(var i = 0; i < arNum.length; i++){
	                                if(arNum[i] == ".")
	                                    chkpoint++;
	                            }
	                            if(chkpoint > 0 && colval == "."){
	                                alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                                $(oSelitem).focus();
	                                return;
	                            }
	                        }
	                    }
	                }
	                else if(gubun == "3" && colval == "."){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}.");
	                    $(oSelitem).focus();
	                    return;
	                }else if(last_gubun == "8" && col[col.length-1] == ")"){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }
	            }else if(colval == "."){
	                alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                $(oSelitem).focus();
	                return;                        
	            }
	        break;
	        case 4:
	            if(col.length > 0){
	                var chkarray = [];
	                var chkok = 0;
	                var chknextVal = 0;

	                for(var i = 0; i < SelCalc.length; i++){
	                    if(SelCalc[i] == "4")
	                        chkarray = [];
	                     else
	                        chkarray.push(SelCalc[i]);
	                }

	                if(chkarray.length > 0){
	                    for(var i = 0; i < chkarray.length; i++){
	                        if(chkarray[i] == "5" || chkarray[i] == "2")
	                        {
	                            chkok++;
	                            chknextVal = i;
	                        }
	                    }
	                }

	                if(chkok < 1){
	                    alert ("Vui lòng chọn một phép toán."); //연산자를 선택 바랍니다
	                    $(oSelitem).focus();
	                    return;
	                }
	                else if(chkarray.length - 1 == chknextVal && chkarray[chkarray.length-1] != "2"){
	                    alert ("Vui lòng chọn một phép toán."); //연산자를 선택 바랍니다
	                    $(oSelitem).focus();
	                    return;
	                }
	                else{
	                    if(!(col[col.length - 1] == ")" || last_gubun == 1 || last_gubun == 2 || last_gubun == 3 ) )
	                    {
	                        alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                        $(oSelitem).focus();
	                        return;
	                    }
	                }
	            }
	            else{
	                alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                $(oSelitem).focus();
	                return;
	            }
	        break;
	        case 5:
	            if(col.length > 0){
	                if(last_gubun == "2" || last_gubun == "4" || last_gubun == "5" || last_gubun == "6" || last_gubun == "7" || (last_gubun == "8" && col[col.length-1] == "(") ){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }
	                else{
	                    var chkarray = [];
	                    var chkSetVal = [];
	                    var chkok = 0;

	                    for(var i = 0; i < SelCalc.length; i++){
	                        if(SelCalc[i] == "4"){
	                            chkarray = [];
	                            chkSetVal = [];
	                        }
	                        else{
	                            chkarray.push(SelCalc[i]);
	                            chkSetVal.push(col[i]);
	                        }
	                    }

	                    if(chkarray.length > 0){
	                        for(var i = 0; i < chkarray.length; i++){
	                            if(chkarray[i] == "5")
	                            chkok++;    
	                        }
	                    }

	                    if(chkok > 0 && gubun == 5){
	                        alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                        $(oSelitem).focus();
	                        return;
	                    };

	                    var arleft = 0;
	                    var strCalc = chkSetVal.join("");

	                    for(var i = 0 ;i < strCalc.length ; i++){
	                        if(strCalc.charAt(i) == "(")
	                            arleft++;
	                        if(strCalc.charAt(i) == ")")
	                            arleft--;
	                    }

	                    if(arleft != 0 ){
	                        alert("Công thức tính này không hợp lệ.\n\nVui lòng kiểm tra các dấu ngoặc đơn."); //계산식이 유효하지 않습니다.\n\n괄호를 확인 바랍니다.
	                        $(oSelitem).focus();
	                        return;
	                    }
	                }
	            }
	            else if(col.length == 0 && gubun == 5){
	                alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                $(oSelitem).focus();
	                return;
	            }
	        break;
	        case 6:
	            if(col.length == 0){
	                if(colval != "-" ){
	                    alert ("Hoạt động không thể được lựa chọn đầu tiên"); //연산자를 먼저 선택할 수 없습니다. 계산식을 정확히 입력 바랍니다.
	                    $(oSelitem).focus();
	                    return;
	                }
	            }else{
	                if(!(last_gubun == "1" || (last_gubun == "3" && SelCalc[SelCalc.length-1] != ".") || (last_gubun == "8" && colval == ")")  || col[col.length-1] == ")" || (last_gubun == "7" && colval == "-") )  ){
	                    alert ("Hoạt động không thể được lựa chọn đầu tiên"); //연산자를 먼저 선택할 수 없습니다. 계산식을 정확히 입력 바랍니다.
	                    $(oSelitem).focus();
	                    return;
	                }
	            }
	        break;
	        case 7: 
	            if(col.length > 0){
	                if(!(last_gubun == 4 || last_gubun == 5 ||  last_gubun == 6 || last_gubun == 7 || last_gubun == 8) ){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }else if(last_gubun == "8" && col[col.length-1] == ")"){
	                    $(oSelitem).focus();
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    return;
	                }
	            }
	        break;
	        case 8: //괄호
	            if(col.length == 0 && colval == "("){}
	            else{
	                var arleft = 0;

	                var strCalc = col.join("");

	                for(var i = 0 ;i < strCalc.length ; i++){
	                    if(strCalc.charAt(i) == "(")
	                        arleft++;
	                    if(strCalc.charAt(i) == ")")
	                        arleft--;
	                }

	                if(arleft < 0 || (arleft == 0 && colval == ")") ){
	                    alert("Vui lòng chọn một Mặt Hàng trước."); 
	                    $(oSelitem).focus();
	                    return;
	                }

	                if(col[col.length -1] == "(" && colval == ")"){
	                    alert ("Bạn không thể nhập chỉ với ’( )’. Vui lòng nhập lại.");
	                    $(oSelitem).focus();
	                    return;
	                }

	                if( !( (colval == "(" && (last_gubun == 4 || last_gubun == 5 || last_gubun == 6 || last_gubun == 7 || col[col.length-1] == "(")) || (colval == ")" && (last_gubun == 1 || last_gubun == 3 || col[col.length-1] == ")") ) ) ){
	                    alert("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\nVui lòng kiểm tra các dấu ngoặc đơn."); 
	                    $(oSelitem).focus();
	                    return false;
	                }
	             }
	        break;
	     }
    
	 /* if(is_function){
		 colval = colval +"()";
	 } */	
	 if(has_param){
		 //replace parameters in formula by number user input
		 var parenthesisIndexOf = colval.indexOf("(");
		 var closeParenthesisIndex = colval.indexOf(")");
		 var paramList = colval.substring(parenthesisIndexOf + 1, closeParenthesisIndex);
		 var paramArr = paramList.split(",");
		 var valueSetArr = settingParam(paramArr);
		 colvalSelected = colval;
		 
		 gubunSelected = gubun;
	 }else{
		 setFormula(colval, gubun, cid);
	 }
}

function setFormula(colval, gubun, cid){
	$(oSelitem).val($(oSelitem).val()+colval);
	 if(cid.indexOf("ipColDes") > -1 && gubun == 1){
		allFunctionAndParam.push(colval);		
	 }
	 var arDespop = jQuery(oSelpopitem).val() == "" ? [] : $(oSelpopitem).val().split('_');
	 arDespop.push(colval.length);
	 $(oSelpopitem).val(arDespop.join("_"));
	
	 var arCode = jQuery(ohidSelitem).val() == "" ? [] : $(ohidSelitem).val().split('_');
	 arCode.push(colval);
	 $(ohidSelitem).val(arCode.join("_"));
	
	 var arCalc = jQuery(ocalSelitem).val() == "" ? [] : $(ocalSelitem).val().split('_');
	 arCalc.push(gubun);
	 $(ocalSelitem).val(arCalc.join("_"));
	 $(oSelitem).focus();
}

function settingParam(paramArr){
	$("#paramContent").empty();
	for(var i = 0; i < paramArr.length; i++){
		var divControlGroup = $("<div class='control-group'></div>");
		var label = $("<label class='control-label'>"+ paramArr[i] +"</label>");
		var divControls = $("<div class='controls'></div>");
		divControlGroup.append(label);
		divControlGroup.append(divControls);
		divControls.append("<div id='param_" + i + "'></div>");
		$("#paramContent").append(divControlGroup);
		$("#param_" + i).jqxNumberInput({ width: '200px', height: '25px',  spinButtons: false, decimalDigits: 0, digits: 4, min: 0 });
	}
	paramInFormulaSelected = paramArr; 
	var height = (paramArr.length + 1)  * 25 + 130;
	jQuery("#jqxWindow").jqxWindow({height: height});
	jQuery("#jqxWindow").jqxWindow('open');
}

function FocusColor2(This) { 
  This.style.backgroundColor = "#f7f7f7";
}

function BlurColor3(This){ 
	This.style.backgroundColor = "#f7f7f7";
}

function text_check(text, total, result){
	
}
</script>