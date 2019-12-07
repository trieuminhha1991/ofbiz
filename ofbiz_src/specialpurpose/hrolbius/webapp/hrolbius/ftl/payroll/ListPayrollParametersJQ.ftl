<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script type="text/javascript" src="/images/jquery/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="/delys/images/js/marketing/utils.js"></script>
<script>
	var periodTypes = [
	<#list periodTypes as period>
		{
			periodTypeId : "${period.periodTypeId}",
			description : "${StringUtil.wrapString(period.description?default(''))}"
		},
	</#list>	
	];
	
	var parameterTypes = [
		<#if parameterTypes?exists>
			<#list parameterTypes as parameterType>
				{
					code : "${parameterType.code}",
					name : "${StringUtil.wrapString(parameterType.name?default(''))}",
					description : "${StringUtil.wrapString(parameterType.description?default(''))}"
				},
			</#list>
		</#if>
	]; 
	var allParameterType=[
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
</script>
<#assign dataField="[{ name: 'code', type: 'string' },
					 { name: 'name', type: 'string' },
					 { name: 'defaultValue', type: 'string' },
					 { name: 'actualValue', type: 'string' },
					 {name: 'type', type: 'string'},
					 { name: 'periodTypeId', type: 'string' },
					 { name: 'paramCharacteristicId', type: 'string' },
					 {name : 'editable', type: 'string'}]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.HRPayrollCode}', datafield: 'code', width: 200, editable: false},
 					 { text: '${uiLabelMap.parameterName}', datafield: 'name', width: 250},
 					 {text: '${uiLabelMap.PayrollParamterType}', datafield: 'type', width: '240', editable: false, filtertype: 'checkedlist',
 					 	cellsrenderer : function(row, column, value){
							for(var i = 0; i < allParameterType.length; i++){
								if(allParameterType[i].type &&  allParameterType[i].type == value){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+ allParameterType[i].description+'</div>';		
								}
							}
							return '&nbsp;';
						},
						createfilterwidget: function (column, columnElement, widget) {
					        var sourceParameterType = {
						        localdata: allParameterType,
						        datatype: 'array'
						    };		
							var filterBoxAdapter = new $.jqx.dataAdapter(sourceParameterType, {autoBind: true});
						    var dataParameteTypeList = filterBoxAdapter.records;
						   
						    dataParameteTypeList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataParameteTypeList,  displayMember: 'description', valueMember : 'type', 
						    	height: '25px', autoDropDownHeight: false,
								renderer: function (index, label, value) {
									for(i=0; i < allParameterType.length; i++){
										if(allParameterType[i].type == value){
											return allParameterType[i].description;
										}
									}
								    return value;
								}
							});	
					    }
 					 },
 					 {text: '${StringUtil.wrapString(uiLabelMap.CommonCharacteristic)}', datafield: 'paramCharacteristicId', width: '130', 
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
 					 {datafield: 'defaultValue', hidden: true, 
 					 	validation: function (cell, value) {
                        	if (!value || isNaN(value)) {
                        		return { result: false, message: \"${StringUtil.wrapString(uiLabelMap.NumberFieldRequired?default(''))}\" };
                        	}
                        	return true;	
                    	},
                    	
 					 },
 					 { text: '${uiLabelMap.HrolbiusDefaultValue}', datafield: 'actualValue', width: 120, cellsalign: 'right',
 					 	validation: function (cell, value) {
                        	if (!value || isNaN(value)) {
                        		return { result: false, message: \"${StringUtil.wrapString(uiLabelMap.NumberFieldRequired?default(''))}\" };
                       			
                        	}
                        	return true;	
                    	},
                    	cellbeginedit: function (row, datafield, columntype) {
                    		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        if (!data.editable){
					        	return false;
					        }
					    },
					    cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
					    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					    	if(data && data.type == 'CONSTPERCENT' && value){
					    		return '<div style=\"margin-top: 3px; text-align: right; margin-right: 3px;\">' + value + '%' + '</div>';
					    	}else if(data && (data.type == 'CONST' || data.type == 'QUOTA') && value){
					    		return '<div style=\"margin-top: 3px; text-align: right; margin-right: 3px;\">' + formatcurrency(value) + '</div>';
					    	}
					    	if(!value){
					    		value = '${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}';
					    	}
					    	return '<div style=\"margin-top: 3px; text-align: right; margin-right: 3px;\">' + value + '</div>';
					    }
                     },
 					
 					 { text: '${uiLabelMap.CommonPeriodType}', datafield: 'periodTypeId', editable: false, filtertype: 'checkedlist',
 					 	createfilterwidget: function(column, columnElement, widget){
						    var filterBoxAdapter = new $.jqx.dataAdapter(periodTypes, {autoBind: true});
							var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description',
						    		autoDropDownHeight: false,valueMember : 'periodTypeId', filterable:true, searchMode:'containsignorecase'});
						},
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
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetPayrollParameters&hasrequest=Y" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	showtoolbar = "true"
	editmode="click" id="jqxgrid"
	deleterow="true" jqGridMinimumLibEnable="false"
	removeUrl="jqxGeneralServicer?jqaction=D&sname=deletePayrollParameter" deleteColumn="code"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPayrollParameter" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="code;name;defaultValue;periodTypeId;type;actualValue;description;paramCharacteristicId" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePayrollParameter"  editColumns="code;name;defaultValue;actualValue;periodTypeId;description"
/>
<div id="popupAddRow" class='hide'>
    <div>${uiLabelMap.CreateNewParameters}</div>
    <div class="form-window-container">
    	<div class="form-window-content">
    		<form>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label asterisk">${uiLabelMap.parameterCode}</label>
					</div>
					<div class="span7">	
						<input type="text" name="codeadd" id="codeadd" />
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label asterisk" >${uiLabelMap.parameterName}</label>
					</div>	
					<div class="span7">
						<input type="text" name="nameadd" id="nameadd"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label" >${uiLabelMap.CommonCharacteristic}</label>
					</div>	
					<div class="span7">
						<div id="characteristicDropDown"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class='control-label asterisk'>${uiLabelMap.CalcSalaryPeriod}</label>
					</div>	
					<div class="span7">
						<div id="periodTypeIdDd">
		   				</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label asterisk" >${uiLabelMap.parameterType}</label>
					</div>
					<div class="span7">
						<div id="parameterTypeAdd"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label" >${uiLabelMap.HrolbiusDefaultValue}</label>
					</div>
					<div class="span7">
						<input type="text" name="actualvalueadd" id="actualvalueadd" />
					</div>
				</div>
				<#--<!-- <div class='row-fluid margin-bottom10'>
					<label class="control-label">${uiLabelMap.HrolbiusDefaultValue}</label>
					<div class="controls">
						<input type="text" name="defaultvalueadd" id="defaultvalueadd" />
					</div>
				</div> -->
				<#--<!-- <div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="control-label" >${uiLabelMap.HRolbiusRecruitmentTypeDescription}</label>
					</div>
					<div class="span8">
						<input type="text" name="descriptionadd" id="descriptionadd"/>
					</div>
				</div> -->
				
	    	</form>
    	</div>
		<div class="form-action">
			<button id="cancelBtn" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveBtn">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>   	
    </div>
</div>  
<script type="text/javascript">
	$(document).ready(function(){	
	    initJqxWindow()
	    btnEvent();
	    initJqxDropDownList();
	    initJqxInput();
	    initJqxValidator();
	});
	
	function initJqxValidator(){
		var popup = $("#popupAddRow");
		popup.jqxValidator({
		   	rules: [{
			    input: '#codeadd',
				message: '${StringUtil.wrapString(uiLabelMap.IdNotSpace?default(''))}',
				action: 'blur',
				rule: function (input, commit) {
                    var s= $("#codeadd").val();
                    if (/\s/.test(s)) {
					    return false;
					}
					return true;
                }
			},{
			    input: '#nameadd',
				message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
				action: 'blur',
				rule: 'required'
			},{
                input: "#parameterTypeAdd", 
                message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                action: 'blur', 
                rule: function (input, commit) {
                    var index = $("#parameterTypeAdd").jqxDropDownList('getSelectedIndex');
                    return index != -1;
                }
            },
            {
                input: "#periodTypeIdDd", 
                message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                action: 'blur', 
                rule: function (input, commit) {
                    var index = $("#periodTypeIdDd").jqxDropDownList('getSelectedIndex');
                    return index != -1;
                }
            },
            {
                input: "#actualvalueadd", 
                message: "${StringUtil.wrapString(uiLabelMap.NumberRequired?default(''))}", 
                action: 'change', 
                rule: function (input, commit) {
                	var val = $("#actualvalueadd").val();
                    return isNaN(val) ? false : true;
                }
            }]
		 });
	}
	
	function initJqxWindow(){
		var popup = $("#popupAddRow");
		popup.jqxWindow({
			maxWidth: 520, minWidth: 520, width: 520, height: 350, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#Cancel")           
	    });
	    popup.on('close', function (event) { 
	    	clearWindowData();
	    	popup.jqxValidator('hide');
	    });
	}
	
	function initJqxInput(){
		$("#codeadd").jqxInput({width: '97%', height: '24', theme: 'olbius'});
	    $("#actualvalueadd").jqxInput({width: '97%', height: '24', theme: 'olbius'});
	    $("#nameadd").jqxInput({width: '97%', height: '24', theme: 'olbius'});
	}
	
	function initJqxDropDownList(){
		var periodTypeDd = $('#periodTypeIdDd');
		var parameterTypeDd = $('#parameterTypeAdd');
		var characteristicDropDown = $("#characteristicDropDown");
		periodTypeDd.jqxDropDownList({
			theme: 'olbius',
			source: periodTypes,
			width: '98%',
			displayMember: "description",
			valueMember : "periodTypeId"
		});
		<#if (periodTypes?size < 8)>
			periodTypeDd.jqxDropDownList({autoDropDownHeight: true});		
		</#if>
		
		parameterTypeDd.jqxDropDownList({
			theme: 'olbius',
			source: parameterTypes,
			width: '98%',
			displayMember: "description",
			valueMember : "code"
		});
		<#if (parameterTypes?size < 8)>
			parameterTypeDd.jqxDropDownList({autoDropDownHeight: true});
		</#if>
		
		
		characteristicDropDown.jqxDropDownList({
			theme: 'olbius',
			source: paramCharacteristicArr,
			width: '98%',
			displayMember: "description",
			valueMember : "paramCharacteristicId"
		});
		<#if (paramCharacteristicList?size < 8)>
			characteristicDropDown.jqxDropDownList({autoDropDownHeight: true});
		</#if>
		
	}
	
	function btnEvent(){
		var skillJqx = $("#jqxgrid");
		var periodTypeDd = $('#periodTypeIdDd');
		var parameterTypeDd = $('#parameterTypeAdd');
		var popup = $("#popupAddRow");
		$("#cancelBtn").click(function(){
			popup.jqxWindow('close');
		});
		$("#saveBtn").click(function () {
			if(!$('#popupAddRow').jqxValidator('validate')){
				return;
			}
			var i = periodTypeDd.jqxDropDownList('getSelectedItem');
			var j = parameterTypeDd.jqxDropDownList('getSelectedItem');
			var periodTypeId = i ? i.value : "";
			var type = j ? j.value : "";
	    	var row = { 
        		code: $("#codeadd").val(),
        		name: $("#nameadd").val(),
        		periodTypeId: periodTypeId,
        		type: type,
        		actualValue : $("#actualvalueadd").val(),
        		paramCharacteristicId: $("#characteristicDropDown").val()
        		/* description : $("#descriptionadd").val() */
        	  };
        	skillJqx.jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        skillJqx.jqxGrid('clearSelection');                        
	        skillJqx.jqxGrid('selectRow', 0);  
	        popup.jqxWindow('close');
	    });
	}
	
    function openPopupCreatePartySkill(){
    	$("#popupAddRow").jqxWindow('open');
    }
    
    function clearWindowData(){
    	var periodTypeDd = $('#periodTypeIdDd');
		var parameterTypeDd = $('#parameterTypeAdd');
		var characteristicDropDown = $("#characteristicDropDown");
		periodTypeDd.jqxDropDownList('clearSelection');
		parameterTypeDd.jqxDropDownList('clearSelection');
		characteristicDropDown.jqxDropDownList('clearSelection');
    }
</script>