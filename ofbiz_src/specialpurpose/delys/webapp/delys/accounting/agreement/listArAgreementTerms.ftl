<#if parameters.agreementId?exists>
<script>
	<#assign invItemTypeList = delegator.findList("InvoiceItemType", null, null, null, null, false) />
	var iitData = new Array();
	<#list invItemTypeList as item >
		var row = {};
		row['invoiceItemTypeId'] = '${item.invoiceItemTypeId?if_exists}';
		row['description'] = '${item.get('description',locale)?if_exists}';
		iitData[${item_index}] = row;
	</#list>
	
	<#assign termTypeList = delegator.findList("TermType", null, null, null, null, false) />
	var ttData = new Array();
	<#list termTypeList as item >
		var row = {};
		row['termTypeId'] = '${item.termTypeId?if_exists}';
		row['description'] = '${item.get('description',locale)?if_exists}';
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
					 { name: 'textValue', type: 'number', width: 150},
					 { name: 'minQuantity', type: 'number', width: 150},
					 { name: 'maxQuantity', type: 'number', width: 150},
					 { name: 'description', type: 'string', width: 150}
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
					 { text: '${uiLabelMap.invoiceItemTypeId}', datafield: 'invoiceItemTypeId', columntype: 'dropdownlist', width: 180, 
					 	createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({width: 180, dropDownWidth: 300, source: iitData, displayMember:\"description\", valueMember: \"invoiceItemTypeId\",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',
                            renderer: function (index, label, value) {
			                    var datarecord = iitData[index];
			                    return datarecord.description;
			                  },selectionRenderer : function(){
			                  		var data = editor.jqxDropDownList('getSelectedItem');
			                  		if(data != null) return data.label;
			                  		return '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc)}';
			                  }
                        });
                        },cellvaluechanging: function (row, datafield, columntype, oldvalue, newvalue) {
					 			if(newvalue == '' || !newvalue) return oldvalue;
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
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: 150,editable : false,
                     	createeditor: function (row, column, editor) {
                     		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd/MM/yyyy' ,value : (data.fromDate != null) ? data.fromDate : null,allowNullDate : true});
                     	}
                     },
					 { text: '${uiLabelMap.thruDate}',datafield: 'thruDate',cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: 150,
                     	createeditor: function (row, column, editor) {
                     			var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd/MM/yyyy' ,value : (data.thruDate != null) ? data.thruDate : null,allowNullDate : true});
                     	},validation : function(cell,value){
                     		var data = $('#jqxgrid').jqxGrid('getrowdata',cell.row);
                     		if(data.fromDate >= value || value < (new Date())){
                     			return {result : false,message : '${uiLabelMap.fromDateValidate}'}	
                     		}else if(!value){
                     			return true;
                     		}
                     		return true;
                     	}
                     },
					 { text: '${uiLabelMap.termValue}',datafield: 'termValue', width: 150,columntype : 'numberinput',cellsformat : 'd',createeditor : function(row,cellvalue,editor){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2,value : data.termValue ? data.termValue : ''});
					 }},
					 { text: '${uiLabelMap.termDays}',datafield: 'termDays', width: 150,columntype : 'numberinput',cellsformat : 'd',createeditor : function(row,cellvalue,editor){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2,value : data.termDays ? data.termDays : ''});
					 }},
					 { text: '${uiLabelMap.textValue}',datafield: 'textValue', width: 150,columntype : 'numberinput',cellsformat : 'd',createeditor : function(row,cellvalue,editor){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2,value : data.textValue ? data.textValue : ''});
					 }},
					 { text: '${uiLabelMap.FormFieldTitle_minQuantity}',datafield: 'minQuantity', width: 150,cellsformat : 'd',columntype : 'numberinput',createeditor : function(row,cellvalue,editor){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2,value : data.minQuantity ? data.minQuantity : ''});
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
					 { text: '${uiLabelMap.FormFieldTitle_maxQuantity}',datafield: 'maxQuantity', width: 150,cellsformat : 'd',columntype : 'numberinput',createeditor : function(row,cellvalue,editor){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 		editor.jqxNumberInput({digits : 15,spinButtons : true,decimalDigits : 2,value : data.maxQuantity ? data.maxQuantity : ''});
					 },validation : function(cell,value){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',cell.row);
					 	var	min = data.minQuantity;
						var max = value;
						min = parseInt(min);
						max = parseInt(max);
						if((!max && !min) || (!max && min) || (max && !min)){
							return true;
						}else if(min >= max){
							return {result : false,message : '${StringUtil.wrapString(uiLabelMap.MinQuantityMaxQuantity?default(''))}'};
						} 
						return true;		
					 }},
					 { text: '${uiLabelMap.description}',datafield: 'description', width: 250}
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField functionAfterAddRow=notificationAddSuccess columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListAgreementTerms&agreementId=${parameters.agreementId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementTerm&jqaction=U&agreementId=${parameters.agreementId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementTerm&jqaction=C&agreementId=${parameters.agreementId}"
		 removeUrl="jqxGeneralServicer?sname=deleteAgreementTerm&jqaction=D&agreementId=${parameters.agreementId}"
		 editColumns="agreementId[${parameters.agreementId}];agreementTermId;termTypeId;agreementItemSeqId;invoiceItemTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);termValue;termDays;textValue;minQuantity;maxQuantity;description"
		 addColumns="agreementId[${parameters.agreementId}];termTypeId;invoiceItemTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);termValue;termDays;textValue;minQuantity;maxQuantity;description"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementTermId" addrefresh="true"
		 />
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#include 'popupAgreementTerms.ftl'/>
</#if>