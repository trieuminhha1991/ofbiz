<#assign dataFields = "[{name: 'code', type: 'string'},
					  {name: 'partyId', type: 'string'},
					  {name: 'name', type: 'string'},
					  {name: 'periodTypeId', type: 'string'},
					  {name: 'value', type: 'number'},
					  {name: 'fromDate', type: 'date'},
					  {name: 'thruDate', type: 'date'},
					  {name: 'type', type: 'string'}
					  ]">
<script type="text/javascript">
var periodTypeArr1 = new Array();
<#list periodTypeList as periodType>
	var row = {};
	row["periodTypeId"] = "${periodType.periodTypeId}";
	row["description"] = "${StringUtil.wrapString(periodType.description?if_exists)}";
	periodTypeArr1[${periodType_index}] = row;
</#list>

var parametersArr1 = new Array();
<#list listParameters as parameter>
	var row = {};
	row["code"] = "${parameter.code}";
	row["nameParam"] = "${StringUtil.wrapString(parameter.name?if_exists)}";
	row["type"] = "${parameter.type?if_exists}";
	row["actualValue"] = "${parameter.actualValue?if_exists}";
	row["periodTypeId"] = "${parameter.periodTypeId?if_exists}";
	parametersArr1[${parameter_index}] = row;
</#list>
$(document).ready(function () {
		<#assign columnlist = "{text: '${uiLabelMap.parameterCode}', datafield: 'code', filterable: true, filtertype: 'input', editable: false, cellsalign: 'left', width: 170},
								{text: '${uiLabelMap.parameterName}', datafield: 'name', filterable: true, filtertype: 'input', editable: false, cellsalign: 'left',								
								},
								{text: '${uiLabelMap.CommonPeriodType}', datafield: 'periodTypeId', filterable: false,editable: false, cellsalign: 'left', width: 130,
									columntype: 'custom',
									cellsrenderer: function (row, column, value){
										for(var i = 0; i < periodTypeArr1.length; i++){
											if(periodTypeArr1[i].periodTypeId == value){
												return '<div style=\"margin-left: 3px\">' + periodTypeArr1[i].description + '</div>';		
											}
										}
										return value;
									}		
								},
								{datafield: 'type', hidden: true},
								{text: '${uiLabelMap.parameterValue}', datafield: 'value', filterable: false, filtertype: 'input', editable: true, 
									cellsalign: 'right', width: 130, 
									cellsrenderer: function (row, column, value){
										var data = $('#jqxgridPayrollEmplParams').jqxGrid('getrowdata', row);
										
								    	if(data && data.type == 'CONSTPERCENT' && value){
								    		return '<div style=\"margin-top: 3px; text-align: right; margin-right: 3px;\">' + value + '%' + '</div>';
								    	}else if(data && (data.type == 'CONST' || data.type == 'QUOTA') && value){
								    		return '<div style=\"margin-top: 3px; text-align: right; margin-right: 3px;\">' + formatcurrency(value) + '</div>';
								    	}
										return '<div style=\"margin-top: 2px; text-align: right\">' + value + '</div>';
									}
								},
								{text: '${uiLabelMap.AvailableFromDate}', datafield: 'fromDate', filterable: false, filtertype: 'input', editable: false, cellsalign: 'left', cellsformat: 'dd/MM/yyyy ', columntype: 'template',  width: 140},
								{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', filterable: false, filtertype: 'input', editable: true, cellsalign: 'left', cellsformat: 'dd/MM/yyyy ', columntype: 'template',  width: 140, height:25,
									createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								        editor.jqxDateTimeInput({width: 138, height: 28});
								        editor.val(cellvalue);
								    }
								},
								{text: '', hidden: true, datafield: 'partyId'}
							  "/>
	var theme = 'olbius';
	
	//console.log(parametersArr1);
	
    $("#parameterValueNew").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, decimalDigits: 2, digits: 9, max: 999999999, theme: 'olbius', min: 0});
    $("#fromDateParamNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
    $("#thruDateParamNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
    $("#thruDateParamNew").val(null);
    jQuery("#popupWindowPayrollEmplParams").jqxWindow({showCollapseButton: false, maxHeight: 400, autoOpen: false,
		maxWidth: 520, minHeight: 300, minWidth: 520, height: 300, width: 520, isModal: true, theme:theme});
    var sourceParameters =
    {
        localdata: parametersArr1,
        datatype: "array"
    };
    
    var dataAdapterParameter = new $.jqx.dataAdapter(sourceParameters);    
    $('#parameterCodeNew').jqxDropDownList({ source: dataAdapterParameter, selectedIndex: 0, filterable: true, 
    	searchMode: 'containsignorecase',
    	displayMember: "nameParam", valueMember: "code", itemHeight: 25, height: 25, width: '98%', theme: theme,
        renderer: function (index, label, value) {
        	//console.log(dataAdapterParameter);
        	for(var i = 0; i < parametersArr1.length; i++){
        		if(value == parametersArr1[i].code){
        			return parametersArr1[i].nameParam;
        		}
        	}
            return value;
        }
    });
    var sourcePeriodType = {
   		 localdata: periodTypeArr1,
   	     datatype: "array"	
    }
    var dataAdapterPeriodType = new $.jqx.dataAdapter(sourcePeriodType);
    $('#periodTypeParamNew').jqxDropDownList({  source: dataAdapterPeriodType,
    	displayMember: "description", valueMember: "periodTypeId", itemHeight: 25, height: 25, width: '98%', theme: theme, dropDownHeight: 120,
        renderer: function (index, label, value) {
            for(var i = 0; i < periodTypeArr1.length; i++){
            	if(periodTypeArr1[i].periodTypeId == value){
            		return periodTypeArr1[i].description; 
            	}
            }
            return value;
        }
    });
    $('#periodTypeParamNew').val("${defaultPeriodType}");
    $("#btnSave").click(function () {
    	$("#btnSave").attr("disabled", "disabled");
    	var thruDate = $("#thruDateParamNew").jqxDateTimeInput('getDate');
    	/* if(thruDate){
    		thruDate = $("#thruDateParamNew").jqxDateTimeInput('getDate');
    	} */
    	var parametersSelected = $("#parameterCodeNew").jqxDropDownList('getSelectedItem')
    	var row = {
    		code: $('#parameterCodeNew').val(),
    		partyId: "${parameters.partyId}",
    		periodTypeId: $("#periodTypeParamNew").val(),
    		value: $("#parameterValueNew").val(),
    		fromDate: $("#fromDateParamNew").jqxDateTimeInput('getDate'),
    		name:parametersSelected.label,
    		thruDate: thruDate
    	};
    	$("#jqxgridPayrollEmplParams").jqxGrid('addRow', null, row, "first");
	    $("#popupWindowPayrollEmplParams").jqxWindow('close');
	   
    });
    $("#btnCancel").click(function(){
    	$("#popupWindowPayrollEmplParams").jqxWindow('close');
    });
    $("#popupWindowPayrollEmplParams").on('open', function(event){
    	enableAlterSave();
    	$("#parameterValueNew").val(0);
    });
    $("#parameterCodeNew").on('select', function(event){
    	var args = event.args;
    	var item = args.item;
    	var index = args.index;
    	var datarecord = parametersArr1[index];
    	if(datarecord.type == 'CONSTPERCENT'){
    		$("#parameterValueNew").jqxNumberInput({digits: 3, symbolPosition: 'right', symbol: '%'});
    	}else{
    		$("#parameterValueNew").jqxNumberInput({decimalDigits: 2, digits: 9, max: 999999999, theme: 'olbius', min: 0, symbol: ''});
    	}
    });
});
function enableAlterSave(){
	 $("#btnSave").removeAttr("disabled");
}

</script>
<div class="row-fluid">
	<div id="popupWindowPayrollEmplParams" class='hide'>
		<div id="PayrollEmplParamsWindowHeader">
			${uiLabelMap.AddPayrollParameterFor} ${employee.lastName?if_exists} ${employee.middleName?if_exists} ${employee.firstName?if_exists} 
		</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<form class="">
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label">${uiLabelMap.parameterCode}</label>
						</div>
						<div class="span7">
							<div id="parameterCodeNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label">${uiLabelMap.parameterValue}</label>
						</div>
						<div class="span7">
							<div id="parameterValueNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label">${uiLabelMap.CommonPeriodType}</label>
						</div>
						<div class="span7">
							<div id="periodTypeParamNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label">${uiLabelMap.AvailableFromDate}</label>
						</div>
						<div class="span7">
							<div id="fromDateParamNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label">${uiLabelMap.CommonThruDate}</label>
						</div>
						<div class="span7">
							<div id="thruDateParamNew"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="form-action">
				<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>		
</div>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" 
		 filterable="false" alternativeAddPopup="popupWindowPayrollEmplParams" deleterow="true" editable="true" addrow="true"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQGetListParameterEmpl&partyId=${parameters.partyId}" id="jqxgridPayrollEmplParams"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createEmplPayrollParameters" selectionmode="singlerow" editmode="selectedcell"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmplPayrollParameters" bindresize="false" width="99%"
		 addColumns="partyId;code;periodTypeId;value;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" functionAfterAddRow="enableAlterSave()"
		 editColumns="partyId;code;periodTypeId;value;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" jqGridMinimumLibEnable="false"
		 removeUrl="jqxGeneralServicer?sname=deletePayrollEmplParameters&jqaction=D" deleteColumn="partyId;;code;fromDate(java.sql.Timestamp)"
		 deletesuccessfunction="refreshAfterDelete"
	/>			
	
			  