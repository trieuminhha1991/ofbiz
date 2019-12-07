<#if parameters.agreementId?exists && parameters.agreementItemSeqId?exists>
<script>
	<#assign invItemTypeList = delegator.findList("InvoiceItemType", null, null, null, null, false) />
	var iitData = new Array();
	var row = {};
	<#list invItemTypeList as item >
		var row = {};
		row['invoiceItemTypeId'] = '${item.invoiceItemTypeId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		iitData[${item_index}] = row;
	</#list>
	
	<#assign termTypeList = delegator.findList("TermType", null, null, null, null, false) />
	var ttData = new Array();
	<#list termTypeList as item >
		var row = {};
		row['termTypeId'] = '${item.termTypeId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		ttData[${item_index}] = row;
	</#list>
	
</script>
<#assign dataField="[{ name: 'agreementTermId', type: 'string' },
					 { name: 'termTypeId', type: 'string'},
					 { name: 'agreementId', type: 'string'},
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'invoiceItemTypeId', type: 'string'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date', width: 150},
					 { name: 'termValue', type: 'string', width: 150},
					 { name: 'termDays', type: 'string', width: 150},
					 { name: 'textValue', type: 'string', width: 150},
					 { name: 'minQuantity', type: 'number', width: 150},
					 { name: 'maxQuantity', type: 'number', width: 150},
					 { name: 'description', type: 'string', width: 150},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.termTypeId}', datafield: 'termTypeId', editable: false, width: 150,
					 	cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < ttData.length; i++){
					 			if(value == ttData[i].termTypeId){
					 				return \"<span>\" + ttData[i].description + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}
					 },
					 { text: '${uiLabelMap.agreementItemSeqId}', datafield: 'agreementItemSeqId', editable: false, width: 180},
					 { text: '${uiLabelMap.invoiceItemTypeId}', datafield: 'invoiceItemTypeId', columntype: 'template', width: 180, 
					 	createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({width: 150, source: iitData, displayMember:\"description\", valueMember: \"invoiceItemTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = iitData[index];
			                    return datarecord.description;
			                  }
                        });
                        },
                      cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < iitData.length; i++){
					 			if(value == iitData[i].invoiceItemTypeId){
					 				return \"<span>\" + iitData[i].description + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}
					 },
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', columntype: 'template', width: 150,editable : false,
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd-MM-yyyy hh:mm:ss' });
                     	}
                     },
					 { text: '${uiLabelMap.thruDate}',datafield: 'thruDate',cellsformat: 'dd/MM/yyyy', columntype: 'template', width: 150,
                     	createeditor: function (row, column, editor) {
                     	var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'yyyy-MM-dd hh:mm:ss' ,allowNullDate : true,value : (data.thruDate!=null)?data.thruDate : null});
                     	},validation : function(cell,value){
							var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",cell.row);
							if(data.fromDate >= value || value < (new Date())){
								return {result : false,message : '${uiLabelMap.thruDateValidate}'}
							}
							return true;
                     	}
                     },
					 { text: '${uiLabelMap.termValue}',datafield: 'termValue', width: 150,columntype : 'numberinput',createeditor : function(row,cellvalue,editor){
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2});
					 }},
					 { text: '${uiLabelMap.termDays}',datafield: 'termDays', width: 150,columntype : 'numberinput',createeditor : function(row,cellvalue,editor){
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2});
					 }},
					 { text: '${uiLabelMap.textValue}',datafield: 'textValue', width: 150,columntype : 'numberinput',createeditor : function(row,cellvalue,editor){
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2});
					 }},
					 { text: '${uiLabelMap.FormFieldTitle_minQuantity}',datafield: 'minQuantity', width: 150,columntype : 'numberinput',createeditor : function(row,cellvalue,editor){
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2});
					 },validation : function(cell,value){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',cell.row);
					 	var	min = value;
						var max = data.maxQuantity;
						min = parseInt(min);
						max = parseInt(max);
						if((!min && !max) || (!max && min) || (!min && max)){
							 return true; 
						}else if(min >= max){
							return {result : false,message : '${StringUtil.wrapString(uiLabelMap.MinQuantityMaxQuantity?default(''))}'};
						}
						return true;	
					 }},
					 { text: '${uiLabelMap.FormFieldTitle_maxQuantity}',datafield: 'maxQuantity', width: 150,columntype : 'numberinput',createeditor : function(row,cellvalue,editor){
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2});
					 },validation : function(cell,value){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',cell.row);
					 	var	min = data.minQuantity;
						var max = value;
						min = parseInt(min);
						max = parseInt(max);
						if((!max && !min) || (!max && min) || (max && !min)){
							return true;
						}else if(min >= max){
							return {result : false,css : 'margin-right : 50px !important;',message : '${StringUtil.wrapString(uiLabelMap.MinQuantityMaxQuantity?default(''))}'};
						} 
						return true;	
					 }},
					 { text: '${uiLabelMap.description}',datafield: 'description', width: 250}
					 "/>
<@jqGrid filtersimplemode="false" addrefresh="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListAgreementItemTerms&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementTerm&jqaction=U&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementTerm&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=deleteAgreementTerm&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 editColumns="agreementId[${parameters.agreementId}];agreementTermId;termTypeId;agreementItemSeqId[${parameters.agreementItemSeqId}];invoiceItemTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);termValue;termDays;textValue;minQuantity;maxQuantity;description"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];termTypeId;invoiceItemTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);termValue;termDays;textValue;minQuantity;maxQuantity;description"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];agreementTermId"
		 />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#include "component://delys/webapp/delys/accounting/popup/popupAddAgreementItemTerms.ftl"/>
</#if>