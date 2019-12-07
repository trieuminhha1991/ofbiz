<#if parameters.agreementId?exists>
<script>
	<#assign invItemTypeList = delegator.findList("InvoiceItemType", null, null, null, null, false) />
	var iitData = [<#list invItemTypeList as item>{
		invoiceItemTypeId : "${item.invoiceItemTypeId?if_exists}",
		description : "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list>];
	<#assign termTypeList = delegator.findList("TermType", null, null, null, null, false) />
	var ttData = [<#list termTypeList as item >{
			termTypeId : "${item.termTypeId?if_exists}",
			description : "${StringUtil.wrapString(item.get("description", locale))}"
		},</#list>];
</script>
<#assign dataField="[{ name: 'agreementTermId', type: 'string' },
					{ name: 'termTypeId', type: 'string'},
					{ name: 'agreementId', type: 'string'},
					{ name: 'agreementItemSeqId', type: 'string'},
					{ name: 'invoiceItemTypeId', type: 'string'},
					{ name: 'fromDate', type: 'date',other : 'Timestamp'},
					{ name: 'thruDate', type: 'date',other : 'Timestamp'},
					{ name: 'termValue', type: 'number'},
					{ name: 'termDays', type: 'number',type : 'Long'},
					{ name: 'textValue', type: 'string'},
					{ name: 'minQuantity', type: 'number',type : 'Double'},
					{ name: 'maxQuantity', type: 'number', type : 'Double'},
					{ name: 'description', type: 'string'}]"/>

<#assign columnlist="
	{ text: '${uiLabelMap.termTypeId}', datafield: 'termTypeId',filtertype : 'checkedlist',editable: false, width: 300,
		cellsrenderer: function(row, colum, value){
			for(i = 0; i < ttData.length; i++){
				if(value == ttData[i].termTypeId){
					return \"<span>\" + ttData[i].description + \"</span>\";
				}
			}
			return \"<span>\" + value + \"</span>\";
		}, createfilterwidget : function(column,columnElement,widget){
			widget.jqxDropDownList({ source: ttData, displayMember: 'description', valueMember: 'termTypeId', autoDropDownHeight: false });
		}
	},
	{ text: '${uiLabelMap.agreementItemSeqId}', datafield: 'agreementItemSeqId', editable: false, width: 180 },
	{ text: '${uiLabelMap.invoiceItemTypeIdPO}', datafield: 'invoiceItemTypeId', columntype: 'dropdownlist', filtertype : 'checkedlist', width: 300, 
		createeditor: function (row, column, editor) {
			editor.jqxDropDownList({source: iitData, displayMember:\"description\", valueMember: \"invoiceItemTypeId\", filterable: true, placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}' });
		}, cellsrenderer: function(row, colum, value) {
			for(i = 0; i < iitData.length; i++){
				if(value == iitData[i].invoiceItemTypeId){
					return \"<span>\" + iitData[i].description + \"</span>\";
				}
			}
			return \"<span>\" + value + \"</span>\";
		}, cellvaluechanging: function (row, datafield, columntype, oldvalue, newvalue) {
			if(newvalue == '' || !newvalue) return oldvalue;
		},
		createfilterwidget : function(column, columnElement, widget) {
			widget.jqxDropDownList({ source: iitData, displayMember: 'description', valueMember: 'invoiceItemTypeId', autoDropDownHeight: false });
		}
	},
	{ text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype : 'range', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: 150, editable : true,
		createeditor: function (row, column, editor) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			editor.jqxDateTimeInput({ width: 150, height: 25, formatString: 'dd-MM-yyyy', value : (data.fromDate != null) ? data.fromDate : null, allowNullDate : true});
		}
	},
	{ text: '${uiLabelMap.thruDate}', datafield: 'thruDate', filtertype : 'range', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: 150,
		createeditor: function (row, column, editor) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			editor.jqxDateTimeInput({ width: 150, height: 25, formatString: 'dd-MM-yyyy', value : (data.thruDate != null) ? data.thruDate : null, allowNullDate : true });
		}
	},
	{ text: '${uiLabelMap.termValue}', datafield: 'termValue', filtertype : 'number', width: 150, cellsformat : 'd', columntype : 'numberinput',
		createeditor : function(row, column, editor) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			editor.jqxNumberInput({ min : 0, max : 999999999, spinButtons : false, value : data.termValue ? data.termValue : '', decimalDigits : 2, digits : 15 });
		}
	},
	{ text: '${uiLabelMap.termDays}', datafield: 'termDays', filtertype : 'number', width: 80, columntype : 'numberinput', cellsformat : 'd', textalign: 'right',
		createeditor : function(row, column, editor) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			editor.jqxNumberInput({ min : 0, max : 999999999, spinButtons : false, value : data.termDays ? data.termDays : '', decimalDigits : 2, digits : 15 });
		}
	},
	{ text: '${uiLabelMap.textValue}', datafield: 'textValue', width: 350, textalign: 'right' },
	{ text: '${uiLabelMap.FormFieldTitle_minQuantity}', datafield: 'minQuantity', filtertype : 'number', columntype : 'numberinput', width: 150, cellsformat : 'd', textalign: 'right',
		createeditor : function(row, column, editor) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			editor.jqxNumberInput({ min : 0, max : 999999999, spinButtons : false, value : data.minQuantity ? data.minQuantity : '', decimalDigits : 2, digits : 15 });
		}, validation : function(cell, value) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
			var	min = value;
			var max = data.maxQuantity;
			min = parseInt(min);
			max = parseInt(max);
			if ((!min && !max) || (!max && min) || (!min && max)) {
				return true; 
			} else if (min >= max) {
				return {result : false, message : '${StringUtil.wrapString(uiLabelMap.MinQuantityMaxQuantity?default(''))}'};
			}
			return true;	
		}
	},
	{ text: '${uiLabelMap.FormFieldTitle_maxQuantity}', datafield: 'maxQuantity', filtertype : 'number', columntype : 'numberinput', width: 150, cellsformat : 'd', textalign: 'right',
		createeditor : function(row, column, editor) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			editor.jqxNumberInput({ min : 0, max : 999999999, spinButtons : false, value : data.maxQuantity ? data.maxQuantity : '', decimalDigits : 2, digits : 15 });
		}, validation : function(cell, value){
			var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
			var	min = data.minQuantity;
			var max = value;
			min = parseInt(min);
			max = parseInt(max);
			if ((!max && !min) || (!max && min) || (max && !min)) {
				return true;
			} else if (min >= max) {
				return {result : false,message : '${StringUtil.wrapString(uiLabelMap.MinQuantityMaxQuantity?default(''))}'};
			} 
			return true;		
		}
	},
	{ text: '${uiLabelMap.description}', datafield: 'description', width: 250 }
					 "/>
<@jqGrid filtersimplemode="true" filterable="true" autoheight="true" addType="popup" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
	customTitleProperties="AccountingAgreementItemTerms"
	url="jqxGeneralServicer?sname=JQListAgreementTerms&agreementId=${parameters.agreementId}"
	updateUrl="jqxGeneralServicer?sname=updateAgreementTerm&jqaction=U&agreementId=${parameters.agreementId}"
	createUrl="jqxGeneralServicer?sname=createAgreementTerm&jqaction=C&agreementId=${parameters.agreementId}"
	removeUrl="jqxGeneralServicer?sname=deleteAgreementTerm&jqaction=D"
	editColumns="agreementId[${parameters.agreementId}];agreementTermId;termTypeId;agreementItemSeqId;invoiceItemTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);termValue;termDays;textValue;minQuantity;maxQuantity;description"
	addColumns="agreementId[${parameters.agreementId}];termTypeId;invoiceItemTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);termValue;termDays;textValue;minQuantity;maxQuantity;description"
	deleteColumn="agreementId[${parameters.agreementId}];agreementTermId"/>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#include 'viewEditAgreementTerms.ftl'/>
</#if>