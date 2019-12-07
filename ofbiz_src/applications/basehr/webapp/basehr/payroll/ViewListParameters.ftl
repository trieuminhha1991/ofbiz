<#include "script/ViewListParametersScript.ftl"/>

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
						   
						    //dataParameteTypeList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
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
 					 {text: '${StringUtil.wrapString(uiLabelMap.CommonCharacteristic)}', datafield: 'paramCharacteristicId', width: '130', editable: false,
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
					    	}else if(data && (data.type == 'CONST' || data.type == 'QUOTA'||data.type == 'BASE_ON_WORK_DAY') && value){
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
						    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
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

<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
	<#assign addrow = "true">
	<#assign deleterow = "true">
<#else>
	<#assign addrow = "false">
	<#assign deleterow = "false">	
</#if>
<#assign customControl = "icon-filter open-sans@${uiLabelMap.HRCommonRemoveFilter}@javascript: void(0);@removerFilter()">				 
<@jqGrid url="jqxGeneralServicer?sname=JQGetPayrollParameters&hasrequest=Y" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	showtoolbar = "true"
	editmode="click" id="jqxgrid" showlist="false"
	deleterow=deleterow jqGridMinimumLibEnable="false"
	removeUrl="jqxGeneralServicer?jqaction=D&sname=deletePayrollParameter" deleteColumn="code"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPayrollParameter" alternativeAddPopup="popupAddRow" addrow=addrow addType="popup" 
	addColumns="code;name;defaultValue;periodTypeId;type;actualValue;description;paramCharacteristicId" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePayrollParameter"  editColumns="code;name;type;defaultValue;actualValue;periodTypeId;description"
	customcontrol1 = customControl
/>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
	<#include "CreateParameters.ftl"/>
</#if>
<script type="text/javascript">
	function removerFilter(){
		$('#jqxgrid').jqxGrid('clearfilters');
	}
</script>