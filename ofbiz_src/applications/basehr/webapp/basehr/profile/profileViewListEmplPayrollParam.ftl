<#include "script/profileViewListEmplPayrollParamScript.ftl"/>
<script type="text/javascript" src="/hrresources/js/profile/emplPayrollParam.js"></script>

<#assign dataField="[{ name: 'code', type: 'string' },
					 { name: 'name', type: 'string' },
					 { name: 'type', type: 'string' },
					 { name: 'periodTypeId', type: 'string' },
					 { name: 'value', type: 'number' },
					 { name: 'paramCharacteristicId', type: 'string'},
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.parameterCode}', datafield: 'code', width:'12%', hidden: true},
					 {text: '${uiLabelMap.PayrollAllowanceAndBonusName}', datafield: 'name', width: 190}, 	
					 { text: '${uiLabelMap.parameterType}', datafield: 'type', hidden: true, filtertype: 'checkedlist'},
                     {text: '${StringUtil.wrapString(uiLabelMap.CommonCharacteristic)}', datafield: 'paramCharacteristicId', width: 140, 
                     	cellsrenderer: function(row, column, value){
                     		for(var i = 0; i < paramCharacteristicArr.length; i++){
                     			if(paramCharacteristicArr[i].paramCharacteristicId == value){
                     				return '<span>' + paramCharacteristicArr[i].description + '</span>';
                     			}
                     		}
                     		return '<span>' + value + '</span>';
                     	}
                     },	
                     { text: '${uiLabelMap.CommonValue}', datafield: 'value', width:160,
						 cellsrenderer: function(row, column, value){
						 	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 	var periodTypeId = data.periodTypeId;
						 	var type = data.type;
						 	var retVal = value;
						 	if(type == 'CONSTPERCENT'){
						 		retVal = retVal + '%';
						 	}else{
						 		retVal = formatcurrency(value);
						 	} 
							return '<span>' + retVal + '</span>';
						 }
                     },
                     {text: '${uiLabelMap.CommonPeriod}', datafield: 'periodTypeId', width:120, filtertype: 'checkedlist',
                     	cellsrenderer: function(row, column, value){
                     		for(var i = 0; i < timePeriodTypeData.length; i++){
                     			if(timePeriodTypeData[i].periodTypeId == value){
                     				return '<span>' + timePeriodTypeData[i].description + '</span>';
                     			}
                     		}
                     		return '<span>' + value + '</span>';
                     	},
                     	createfilterwidget: function(column, columnElement, widget){
							var sourcePeriodType = {
						        localdata: timePeriodTypeData,
						        datatype: 'array'
						    };		
							var filterBoxAdapter = new $.jqx.dataAdapter(sourcePeriodType, {autoBind: true});
						    var dataPeriodTypeList = filterBoxAdapter.records;
						    
						    //dataPeriodTypeList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataPeriodTypeList,  displayMember: 'description', valueMember : 'periodTypeId', 
						    	height: '25px', autoDropDownHeight: false,
								renderer: function (index, label, value) {
									for(i=0; i < timePeriodTypeData.length; i++){
										if(timePeriodTypeData[i].periodTypeId == value){
											return timePeriodTypeData[i].description;
										}
									}
								    return value;
								}
							});									
						}		
                     },
                     { text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', cellsformat: 'd', filtertype:'range', width:'12%'},
                     { text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', cellsformat: 'd', filtertype:'range'}
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="false" showtoolbar="true" clearfilteringbutton="true"
		 url="" dataField=dataField columnlist=columnlist filterable="true"
		 showlist="false" customControlAdvance="<div id='dateTimeInput' style='margin-right: 5px'></div>"
		 />
