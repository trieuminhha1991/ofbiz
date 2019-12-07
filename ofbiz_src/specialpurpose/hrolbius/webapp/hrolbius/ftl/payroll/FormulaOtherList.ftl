<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxeditor.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtabs.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxvalidator.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>

<#include "component://hrolbius/webapp/hrolbius/ftl/js/createFormula.ftl"/>

<script type="text/javascript">
var periodTypes = [
   	<#list periodTypes as period>
   		{
   			periodTypeId : "${period.periodTypeId}",
   			description : "${StringUtil.wrapString(period.description?default(''))}"
   		},
   	</#list>	
  	];

var allParameterType = [
   		<#if allParameterType?exists>
   			<#list allParameterType as parameterType>
   				{
   					type : "${parameterType.code}",
   					name : "${StringUtil.wrapString(parameterType.name?default(''))}",
   					description : "${StringUtil.wrapString(parameterType.description?default(''))}"
   				},
   			</#list>
   		</#if>          
   ];   
   
var paramCharacteristicArr = [
 	<#if paramCharacteristicList?has_content>
 		<#list paramCharacteristicList as characteristic>
 			{
 				paramCharacteristicId: '${StringUtil.wrapString(characteristic.paramCharacteristicId)}',
 				description: '${StringUtil.wrapString(characteristic.description?if_exists)}'
 			},
 		</#list>
 	</#if>
 ];   
 
var characteristicArr = new Array();
<#if payrollCharacteristic?has_content>
	<#list payrollCharacteristic as characteristic>
		var row = {};
		row["payrollCharacteristicId"] = "${characteristic.payrollCharacteristicId}";
		row["description"] = "${characteristic.description}";
		characteristicArr[${characteristic_index}] = row;
	</#list>
</#if>
</script>

<#assign dataField="[{ name: 'code', type: 'string' },
					 { name: 'name', type: 'string' },
					 { name: 'function', type: 'string'},
					 {name: 'editAction', type: 'string'}]
					"/>
<script type="text/javascript">
<#assign columnlist="{ text: '${uiLabelMap.formulaCode}', datafield: 'code', width: 140},
					{ text: '${uiLabelMap.formulaName}', datafield: 'name'},
					{ text: '${uiLabelMap.formulaDescription}', datafield: 'function', 
						cellsrenderer: function(row, column, value){
							 return '<div><code style=\"white-space: normal;\">' + value + '</code></div>';
						}
					},
				
					"/>
</script>	
<#--<!-- {text: '', width: '50', columntype: 'template', editable: false, filterable: false, datafield: 'editAction',
						cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties){
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return '<div style=\"text-align: center; margin-bottom: 2px\"><a href=\"EditFormula?code='+ data.code +'\" class=\"btn btn-mini btn-primary icon-edit\" ></a></div>';
						   }
					} -->

<div class="row-fluid">
	<div id="appendNotification">
		<div id="createFormulaNtf">
			<span id="notificationText"></span>
		</div>
	</div>
</div>
		
<@jqGrid url="jqxGeneralServicer?sname=JQGetFormulaOther" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" deleterow="true"
	editable="false" removeUrl="jqxGeneralServicer?sname=deletePayrollFormula&jqaction=D"
	deleteColumn="code" jqGridMinimumLibEnable="false"
	editrefresh ="true"
	editmode="click"
	showtoolbar = "true"
	autorowheight = "true"
	addrow="true" alternativeAddPopup="settingFormula" addType="popup"
/>
<#--<!-- customcontrol1="icon-plus-sign open-sans@${uiLabelMap.NewFormula}@EditFormula" -->


<#assign dataFieldFormula = "[{ name: 'code', type: 'string' },
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
							 }" />		
							 
<#assign dataFieldParam = "[{ name: 'code', type: 'string' },
							{ name: 'name', type: 'string' },
							{name: 'type', type: 'string'},
							{ name: 'periodTypeId', type: 'string' },
							{ name: 'paramCharacteristicId', type: 'string' }]"/> 							 					  
<#assign columnlistParam = "{ text: '${uiLabelMap.HRPayrollCode}', datafield: 'code', width: 130, editable: false},
							{ text: '${uiLabelMap.parameterName}', datafield: 'name', width: 150},
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
		<div>
			<div id="jqxTabsOuter" style="overflow: hidden !important;">
				<ul>
					<li>${uiLabelMap.DAInformation}</li>
					<li>${uiLabelMap.formulaFunction}</li>
				</ul>
				<div>
					<div class="form-window-container" style="margin-top: 20px; height: 90%">
						<div class="form-window-content">
							<form id="createNewFormulaForm">
								<div class='row-fluid margin-bottom10'>
									<div class='span5 text-algin-right'>
										<label class="asterisk">${uiLabelMap.formulaCode}</label>
									</div>
									<div class="span7">
										<input type="text" id="newFormulaCode">
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span5 text-algin-right'>
										<label class="asterisk">${uiLabelMap.formulaName}</label>
									</div>
									<div class="span7">
										<input type="text" id="newFormulaName">
									</div>
								</div>	
							</form>
						</div>
						<div style="margin-right: 5px ">
							<button type="button" class='btn btn-primary form-action-button pull-right' id="nextBtn">
							${uiLabelMap.CommonNext}&nbsp;<i class='icon-arrow-right'></i></button>
						</div>
					</div>
				</div>
				<div>
					<#include "component://hrolbius/webapp/hrolbius/ftl/payroll/createFormula/createFormulaCommon.ftl"/>
				</div>
			</div>
		</div>
	</div>
</div>
<@jqGrid url="jqxGeneralServicer?sname=JQGetAllFormula" dataField=dataFieldFormula columnlist=columnlistFormula
	jqGridMinimumLibEnable="false" idExisted="true" width="100%" bindresize="false" viewSize="7"
	editable="false" id="jqxGridFormula" height="257px" autoheight="false" pagesizeoptions="['7', '14', 20]"
	showtoolbar = "false" filterable="false" sortable="false" rowsheight="28"
/>
<@jqGrid url="jqxGeneralServicer?&hasrequest=Y&sname=JQGetPayrollParameters" dataField=dataFieldParam columnlist=columnlistParam
	jqGridMinimumLibEnable="false" filterable="false" width="100%" bindresize="false" viewSize="7" pagesizeoptions="['7', '14', 20]"
	editable="false" id="jqxGridParam" idExisted="true" height="257px" autoheight="false" sortable="false" rowsheight="28"
	showtoolbar = "false"/>	
		<script type="text/javascript">
	$(document).ready(function(){
		initJqxGridEvent();
		initJqxInput();
		initJqxWindow();
		initBtnEvent();		
		initJqxValidator();
		initJqxNotification();
	});
	
	function initJqxInput(){
		$("#newFormulaCode").jqxInput({height: 19, width: 247, theme: 'olbius'});
		$("#newFormulaName").jqxInput({height: 19, width: 247, theme: 'olbius'});
	}
	
	
	function initJqxWindow(){
		$("#settingFormula").jqxWindow({width: 900, height: 600, resizable: true,  isModal: true, autoOpen: false, theme: 'olbius',
			initContent: function(){
				$('#jqxTabsOuter').jqxTabs({ width: '100%', height: '99%',  theme: 'olbius',
					initTabContent: function(){
						initJqxSplitter();
						$('#jqxTabs').jqxTabs({ width: '100%', height: '100%',  theme: 'olbius' });
						
					}	
				});
				initJqxEditor();	
				initJqxTabsEvent($("#jqxTabsOuter"));
				$('#jqxTabsOuter').jqxTabs('disableAt', 1);
			}	
		});
	}
	
	function initBtnEvent(){
		$("#cancelBtn").click(function(event){
			$("#settingFormula").jqxWindow('close');
		});
		
		$("#saveBtn").click(function(event){
			$(this).attr("disabled", "disabled");
			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ConfirmCreateFormula)}",
				[
					{
						"label": "${uiLabelMap.CommonSubmit}",
						"class" : "icon-ok btn btn-mini btn-primary",
						"callback": function(){
							$("#saveBtn").removeAttr("disabled");
							createNewDeductionFormula();
						}
					},
					{
						"label" : "${uiLabelMap.CommonCancel}",
		    			"class" : "btn-danger icon-remove btn-mini",
		    		 	"callback": function() {
		    		 		$("#saveBtn").removeAttr("disabled");
		    		    }
					}
				]					
			);
		});
		
		$("#backBtn").click(function(){
			$('#jqxTabsOuter').jqxTabs('disableAt', 1);
			$('#jqxTabsOuter').jqxTabs('previous');
		});
		
		$("#nextBtn").click(function(event){
			var valid = $('#createNewFormulaForm').jqxValidator('validate');
			if(!valid){
				return;
			}
			$('#jqxTabsOuter').jqxTabs('enableAt', 1);
			$('#jqxTabsOuter').jqxTabs('next');
		});
	}
	
	function initJqxValidator(){
		$('#createNewFormulaForm').jqxValidator({
			rules:[
			       {input: '#newFormulaCode', message: '${StringUtil.wrapString(uiLabelMap.CommonRequired)}', action: 'blur',  rule: 'required'},
			       {input: '#newFormulaName', message: '${StringUtil.wrapString(uiLabelMap.CommonRequired)}', action: 'blur',  rule: 'required'},
			]
		});
	}
	
	function createNewDeductionFormula(){
		$("#jqxgrid").jqxGrid('showloadelement');
		$("#jqxgrid").jqxGrid({disabled: true});
		var data = getFormulaData();
		data.push({name: "code", value: $("#newFormulaCode").val()});
		data.push({name: "name", value: $("#newFormulaName").val()});
		
		$.ajax({
			url: 'createFormula',
			data: data,
			type: 'POST',
			success: function(data){
				$("#createFormulaNtf").jqxNotification('closeLast');
				if(data._EVENT_MESSAGE_){
					$("#notificationText").text(data._EVENT_MESSAGE_);
					$("#createFormulaNtf").jqxNotification({ template: 'info' });
					$("#createFormulaNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					$("#notificationText").text(data._ERROR_MESSAGE_);
					$("#createFormulaNtf").jqxNotification({ template: 'error' });
					$("#createFormulaNtf").jqxNotification('open');
				}
			},
			complete: function (jqXHR, textStatus){
				$("#settingFormula").jqxWindow('close');
				$("#jqxgrid").jqxGrid('hideloadelement');
				$("#jqxgrid").jqxGrid({disabled: false});
			}
		});
	}
	
	function initJqxTabsEvent(tabsDiv){
		tabsDiv.on('tabclick', function (event){ 			
		    var clickedItem = event.args.item;
		    if(clickedItem == 0){
		    	$('#jqxTabsOuter').jqxTabs('disableAt', 1);
		    }
		}); 
	}
	
	function initJqxSplitter(){
		$('#mainSplitter').jqxSplitter({ width: '100%', height: '87%', orientation: 'horizontal', panels: [{ size: 160 }, { size: 320 }], splitBarSize: 3, });
		$("#nestedSplitter").jqxSplitter( {width: '100%', height: '100%',  orientation: 'horizontal', panels: [{ size: 120}], splitBarSize: 0})
	}
	
	function initJqxEditor(){
		$('#calc_des').jqxEditor({ 
    		width: '100%',
            theme: 'olbiuseditor',
            tools: '',
            height: 100,
            disabled:true
        });	
    	$('#calc_des').val("");
    	$('#calc_des').focus(function(){
    		FocusColor2(this);
    		fn_Selectitem(this);
    	});
	}
	
	function initJqxNotification(){
		 $("#createFormulaNtf").jqxNotification({
	        width: "100%", opacity: 1, appendContainer: "#appendNotification",
	        autoOpen: false, autoClose: false
	    });
	}
	
	function initJqxGridEvent(){
		$("#jqxGridFormula").on('rowDoubleClick', function (event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#jqxGridFormula").jqxGrid('getrowdata', boundIndex);
		    cal_add(data.code, data.name, 1, true);
		});
		$("#jqxGridParam").on('rowDoubleClick', function (event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#jqxGridParam").jqxGrid('getrowdata', boundIndex);
		    cal_add(data.code, data.name, 1, false);
		});
	}
</script>