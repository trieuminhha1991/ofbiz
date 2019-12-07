<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<script src="/delys/images/js/generalUtils.js"></script>
<script type="text/javascript">
	var listInvoiceType = [
		<#list listInvoiceType as type>
			{
				invoiceTypeId : '${type.invoiceTypeId?if_exists}',
				description : '${type.description?default('')}'
			},		
		</#list>
	]
	var listStt = [
		<#list listStatusItem as stt>
		{
			sttId : '${stt.statusId?if_exists}',
			description : '${stt.description?default('')}'
		},
		</#list>
	] 

</script>
 <#assign dataField = "[
 	{name : 'invoiceId',type : 'string'},
 	{name : 'invoiceTypeId',type : 'string'},
 	{name : 'invoiceDate',type : 'date',other : 'Timestamp'},
 	{name : 'dueDate',type : 'date',other : 'Timestamp'},
 	{name : 'statusId',type : 'string'},
 	{name : 'referenceNumber',type : 'string'},
 	{name : 'description',type : 'string'},
 	{name : 'partyIdFrom',type : 'string'},
 	{name : 'partyId',type : 'string'},
 	{name : 'total',type : 'number'},
 	{name : 'amountNotApplied',type : 'number'},
 	{name : 'amountToApply',type : 'number'},
 	{ name: 'partyNameResultFrom', type: 'string'},
 	{ name: 'partyNameResultTo', type: 'string'}
 ]" />       
 
 <#assign columnlist="
 	{text : '${uiLabelMap.FormFieldTitle_invoiceId}',datafield : 'invoiceId',cellsrenderer : function(row,columnfield,value){
 		var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
 		return '<a href=\"invoiceOverview?invoiceId='+ data.invoiceId +'\">' + data.invoiceId +'</a>';	
 	},width : '15%'},
 	{text : '${uiLabelMap.FormFieldTitle_invoiceTypeId}',datafield : 'invoiceTypeId',width : '15%',filtertype : 'checkedlist',cellsrenderer : function(row){
 		var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
 		for(var index in listInvoiceType){
 			if(listInvoiceType[index].invoiceTypeId == data.invoiceTypeId){
 				return '<span>' + listInvoiceType[index].description + '</span>';
 			}
 		}
 	},createfilterwidget: function (column, columnElement, widget) {
			   				var sourceIT =
						    {
						        localdata: listInvoiceType,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceIT,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords = filterBoxAdapter.records;
			   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecords, displayMember: 'invoiceTypeId', valueMember : 'invoiceTypeId', height: '21px',renderer: function (index, label, value) 
							{
								for(i=0;i < listInvoiceType.length; i++){
									if(listInvoiceType[i].invoiceTypeId == value){
										return listInvoiceType[i].description;
									}
								}
							    return value;
							}});
							widget.jqxDropDownList('checkAll');
			   			}},
 	{text : '${uiLabelMap.AccountingInvoiceDate}',datafield : 'invoiceDate',filtertype: 'range',cellsformat : 'dd/MM/yyyy',width : '15%'},
 	{text : '${uiLabelMap.AccountingDueDate}',datafield : 'dueDate',filtertype: 'range',cellsformat : 'dd/MM/yyyy',width : '15%'},
 	{text : '${uiLabelMap.CommonStatus}',datafield : 'statusId',filtertype : 'checkedlist',width : '15%',cellsrenderer : function(row){
 	var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
 		for(var index in listStt){
 			if(listStt[index].sttId == data.statusId){
 				return '<span>' + listStt[index].description + '</span>';
 			}
 		}
 	},createfilterwidget: function (column, columnElement, widget) {
			   				var sourceIT =
						    {
						        localdata: listStt,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceIT,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords = filterBoxAdapter.records;
			   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecords, displayMember: 'sttId', valueMember : 'sttId', height: '21px',renderer: function (index, label, value) 
							{
								for(i=0;i < listStt.length; i++){
									if(listStt[i].sttId == value){
										return listStt[i].description;
									}
								}
							    return value;
							}});
							widget.jqxDropDownList('checkAll');
			   			}},
 	{text : '${uiLabelMap.AccountingReferenceNumber}',datafield : 'referenceNumber',width : '10%'},
 	{text : '${uiLabelMap.CommonDescription}',datafield : 'description',width : '15%'},
 	{text : '${uiLabelMap.AccountingVendorParty}',datafield : 'partyIdFrom',filtertype: 'olbiusdropgrid',cellsrenderer : function(row){
 		var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
 		return '<a href=\"/partymgr/control/viewprofile?partyId=' + data.partyIdFrom + '\">'+ data.partyNameResultFrom +'['+ data.partyIdFrom +']' +'</a>';
 	},width : '20%'},
 	{text : '${uiLabelMap.AccountingToParty}',datafield : 'partyId',filtertype: 'olbiusdropgrid',cellsrenderer : function(row){
 		var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
 		return '<a href=\"/partymgr/control/viewprofile?partyId=' + data.partyId + '\">' + data.partyNameResultTo +'['+ data.partyId +']'+'</a>';
 	},width : '20%'},
 	{text : '${uiLabelMap.AccountingAmount}',datafield : 'total',filterable : false,width : '15%', cellsrenderer:
	 	function(row, colum, value){
	 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	 		return \"<span>\" + formatcurrency(data.total,data.currencyUomId) + \"</span>\";
	 	}},
 	{text : '${uiLabelMap.FormFieldTitle_paidAmount}',datafield : 'amountNotApplied',filterable : false,width : '15%',cellsrenderer:
	 	function(row, colum, value){
	 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	 		return \"<span>\" + formatcurrency(data.total - data.amountToApply,data.currencyUomId) + \"</span>\";
	 	}
 	},
 	{text : '${uiLabelMap.FormFieldTitle_outstandingAmount}',datafield : 'amountToApply',filterable : false,width : '15%', cellsrenderer:
	 	function(row, colum, value){
	 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	 		return \"<span>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</span>\";
	 	}},
 	{text : '${uiLabelMap.CommonSelectAll}',width : '10%',columntype : 'checkbox',filtertype  :'bool',filterable : false	}
 "/>
  
<@jqGrid dataField=dataField  columnlist=columnlist editable="true" filterable="true" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" 
	otherParams="total:S-getInvoiceTotal(inputValue{invoiceId})<outputValue>;amountToApply:S-getInvoiceNotApplied(inputValue{invoiceId})<outputValue>;partyNameResultFrom:S-getPartyNameForDate(partyId{partyIdFrom},compareDate{invoiceDate},lastNameFirst*Y)<fullName>;partyNameResultTo:S-getPartyNameForDate(partyId,compareDate{invoiceDate},lastNameFirst*Y)<fullName>"
	url="jqxGeneralServicer?sname=jqGetListInvoice" />
<#include "component://delys/webapp/delys/accounting/popup/popupGridPartyFilter.ftl"/>
