<#include "script/ViewListEmplAgreementScript.ftl"/>
<#assign datafield = "[{name: 'agreementDate', type: 'date'},
						{name: 'agreementId', type: 'string'},
						{name: 'description', type: 'string'},
						{name: 'partyIdFrom', type: 'string'},
						{name: 'groupName', type: 'string'},
						{name: 'partyIdRep', type: 'string'},
						{name: 'partyRepName', type: 'string'},
						{name: 'payRate', type: 'number'},
						{name: 'agreementCode', type: 'string'},						
						{name: 'partyIdTo', type: 'string'},
						{name: 'partyCode', type: 'string'},
						{name: 'currencyUomId', type: 'string'},
						{name: 'fullName', type: 'string'},
						{name: 'emplPositionType', type: 'string'},
						{name: 'emplPositionTypeId', type: 'string'},
						{name: 'workPlace', type: 'string'},
						{name: 'agreementTypeId', type: 'string'},
						{name: 'agreementPeriod', type: 'string'},
						{name: 'agreementPeriodDesc', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'thruDate', type: 'date'},
						{name: 'basicSalary', type: 'number'},
						{name: 'insuranceSalary', type: 'number'},
						]"
 					/>
 					
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.AgreementDate)}', datafield: 'agreementDate', cellsformat : 'dd/MM/yyyy', columntype: 'template', width: '13%', cellsalign: 'left', filterType : 'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.EmplAgreementNumber)}', datafield: 'agreementCode', width: '10%',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								return '<span><a href=\"javascript:viewListEmplAgreementObject.showDetailAgreement(&#39;'+row+'&#39);\"> ' + value  + '</a></span>';
							},
						},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: '17%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionType', width: '20%'},
						{text: '${StringUtil.wrapString(uiLabelMap.agreementTypeId)}', datafield: 'agreementTypeId', width: '22%', filterType : 'checkedlist',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								for(var i = 0; i < globalVar.agreementTypeArr.length; i++){
									if(value == globalVar.agreementTypeArr[i].agreementTypeId){
										return '<span>' + globalVar.agreementTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget : function(column, columnElement, widget){
								var source = {
										localdata : globalVar.agreementTypeArr,
										datatype : 'array'
								};
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								var dataFilter = filterBoxAdapter.records;
								widget.jqxDropDownList({source : dataFilter, valueMember : 'agreementTypeId', displayMember : 'description'});
								if(dataFilter.length <= 8){
									widget.jqxDropDownList({autoDropDownHeight : true});
								}else{
									widget.jqxDropDownList({autoDropDownHeight : false});
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.ContactDuration)}', datafield: 'agreementPeriodDesc', width: '12%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HREffectiveDate)}', datafield: 'fromDate', cellsformat : 'dd/MM/yyyy', columntype: 'template', width: '12%', cellsalign: 'left',filtertype : 'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.HRExpireDate)}', datafield: 'thruDate', cellsformat : 'dd/MM/yyyy', columntype: 'template', width: '12%', cellsalign: 'left',filtertype : 'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.SalaryBaseFlat)}', datafield: 'basicSalary', width: '14%',filterType : 'number',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			    	    		  var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 			  return '<span>' + formatcurrency(data.basicSalary,data.currencyUomId) + '</span>';
			    	    	  }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceSalaryShort)}', datafield: 'insuranceSalary', width: '14%',filterType : 'number',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			    	    		  var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 			  return '<span>' + formatcurrency(data.insuranceSalary,data.currencyUomId) + '</span>';
			    	    	  }
						},
						"/>
</script>
<#if security.hasEntityPermission("HR_AGREEMENT", "_ADMIN", session)>
	<#assign addrow = "true">
<#else>
	<#assign addrow = "false">
</#if> 					
<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" 
	 filterable="true" alternativeAddPopup="popupWindowAddAgreement" deleterow="true" editable="false" addrow=addrow
	 url="" id="jqxgrid" showlist="false" filterable="true" sortable="true" deleterow="false" 
	 customControlAdvance="<div id='statusDropDwonList'></div>"
	 removeUrl="" deleteColumn="" updateUrl="" editColumns="" selectionmode="singlerow" jqGridMinimumLibEnable="false" />

${setContextField("windowPopupId", "popupWindowAddAgreement")}	 
<#include "createEmplAgreement.ftl"/>	
<script type="text/javascript" src="/hrresources/js/agreement/ViewListEmplAgreement.js"></script> 
