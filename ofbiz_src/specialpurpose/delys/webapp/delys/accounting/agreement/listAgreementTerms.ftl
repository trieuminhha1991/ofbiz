<#if parameters.agreementId?exists>
<#include "initAgreementTerm.ftl"/>
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
					 { name: 'description', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.termTypeId}', datafield: 'termTypeId',filtertype : 'checkedlist',editable: false, width: 300,
					 	cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < ttData.length; i++){
					 			if(value == ttData[i].termTypeId){
					 				return \"<span>\" + ttData[i].description + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	},createfilterwidget : function(column,columnElement,widget){
					    	var source = {
					    		localdata : ttData,
					    		datatype : 'array'
					    	};
					    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
					    	var uniRecords = filterBoxAdapter.records;
					    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					    	widget.jqxDropDownList({source: uniRecords, dropDownHeight : 200,dropDownWidth : 300,displayMember: 'description', valueMember: 'termTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
												{
													for(i=0;i < ttData.length; i++){
														if(ttData[i].termTypeId == value){
															return ttData[i].description;
														}
													}
												    return value;
												}});
					    }
					 },
					 { text: '${uiLabelMap.agreementItemSeqId}', datafield: 'agreementItemSeqId', editable: false, width: 180},
					 { text: '${uiLabelMap.invoiceItemTypeId}', datafield: 'invoiceItemTypeId', columntype: 'template',filtertype : 'checkedlist', width: 300, 
					 	createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({dropDownWidth: 300, source: iitData, displayMember:\"description\", valueMember: \"invoiceItemTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = iitData[index];
			                    return datarecord.description;
			                  },selectionRenderer : function(){
			                  		var data = editor.jqxDropDownList('getSelectedItem');
			                  		if(data != null) return data.label;
			                  		return '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc)}';
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
					 	},cellvaluechanging: function (row, datafield, columntype, oldvalue, newvalue) {
					 			if(newvalue == '' || !newvalue) return oldvalue;
					 		},createfilterwidget : function(column,columnElement,widget){
					    	var source = {
					    		localdata : iitData,
					    		datatype : 'array'
					    	};
					    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
					    	var uniRecords = filterBoxAdapter.records;
					    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					    	widget.jqxDropDownList({source: uniRecords, dropDownHeight : 200,dropDownWidth : 300,displayMember: 'description', valueMember: 'invoiceItemTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
										{
											for(i=0;i < iitData.length; i++){
												if(iitData[i].invoiceItemTypeId == value){
													return iitData[i].description;
												}
											}
										    return value;
										}});
					    }
					 },
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate',filtertype : 'range',cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: 150,editable : false,
                     	createeditor: function (row, column, editor) {
                     		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd-MM-yyyy' ,value : (data.fromDate != null) ? data.fromDate : null,allowNullDate : true});
                     	}
                     },
					 { text: '${uiLabelMap.thruDate}',datafield: 'thruDate',filtertype : 'range',cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: 150,
                     	createeditor: function (row, column, editor) {
                     		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd-MM-yyyy',value : (data.thruDate != null) ? data.thruDate : null,allowNullDate : true });
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
					 { text: '${uiLabelMap.termValue}',datafield: 'termValue',filtertype : 'number', width: 150,cellsformat : 'd',columntype : 'numberinput',createeditor : function(row,column,editor){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 	editor.jqxNumberInput({min : 0,max : 999999999,spinButtons  : false,value : data.termValue ? data.termValue : '',decimalDigits : 2,digits : 15});
					 }},
					 { text: '${uiLabelMap.termDays}',datafield: 'termDays',filtertype : 'number', width: 80,columntype : 'numberinput',cellsformat : 'd', textalign: 'right',createeditor : function(row,column,editor){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 	editor.jqxNumberInput({min : 0,max : 999999999,spinButtons  : false,value : data.termDays ? data.termDays : '',decimalDigits : 2,digits : 15});
					 }},
					 { text: '${uiLabelMap.textValue}',datafield: 'textValue', width: 350,textalign: 'right'},
					 { text: '${uiLabelMap.FormFieldTitle_minQuantity}',datafield: 'minQuantity',filtertype : 'number',columntype : 'numberinput', width: 150,cellsformat : 'd', textalign: 'right',createeditor : function(row,column,editor){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 	editor.jqxNumberInput({min : 0,max : 999999999,spinButtons  : false,value : data.minQuantity ? data.minQuantity : '',decimalDigits : 2,digits : 15});
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
					 { text: '${uiLabelMap.FormFieldTitle_maxQuantity}',datafield: 'maxQuantity',filtertype : 'number', columntype : 'numberinput',width: 150,cellsformat : 'd', textalign: 'right',createeditor : function(row,column,editor){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 	editor.jqxNumberInput({min : 0,max : 999999999,spinButtons  : false,value : data.maxQuantity ? data.maxQuantity : '',decimalDigits : 2,digits : 15});
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
<@jqGrid filtersimplemode="true" filterable="true" autoheight="true" addType="popup" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true" 
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