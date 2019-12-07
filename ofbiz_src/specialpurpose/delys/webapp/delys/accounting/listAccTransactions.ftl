<script>
	<#assign acctgTransTypes = delegator.findList("AcctgTransType", null, null, null, null, true) />
	var acctgTransTypesData =  [<#list acctgTransTypes as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>'acctgTransTypeId' : "${item.acctgTransTypeId?if_exists}",'description' : "${description}"},</#list>];
	<#assign glFiscalTypes = delegator.findList("GlFiscalType", null, null, null, null, true) />
	var glFiscalTypesData =  [<#list glFiscalTypes as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>'glFiscalTypeId' : "${item.glFiscalTypeId?if_exists}",'description' : "${description}"	},</#list>];
	<#assign glJournals = delegator.findList("GlJournal", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", "company"), null, null, null, true) />
	var glJournalsData =  [<#list glJournals as item>{<#assign description = StringUtil.wrapString(item.get("glJournalName", locale)?if_exists + "[" + item.glJournalId?if_exists +"]")>'glJournalId' : "${item.glJournalId?if_exists}",'description' : "${description?if_exists}"	},</#list>];
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ACCTG_ENREC_STATUS"), null, null, null, true) />
	var statusItemsData =  [<#list statusItems as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />'statusId' : "${item.statusId?if_exists}",'description' : "${description?if_exists}"},</#list>		];
	var isPostedData = [{isPosted : 'Y',description : '${uiLabelMap.accPostted}'},{isPosted : 'N',description : '${uiLabelMap.accNoPostted}'}];
	<#assign listCurrency = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "CURRENCY_MEASURE")), null, null, null, false) />
	var currency = [<#list listCurrency as item>{uomId : "${item.uomId}",description : "${StringUtil.wrapString(item.uomId + ' : '+ item.get("description", locale)?default(""))}"},</#list>];
	<#assign purpose = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "CONVERSION_PURPOSE")), null, null, null, true) />
	var purpose = [<#list purpose as item>{enumId : "${item.enumId}",description : "${StringUtil.wrapString(item.enumId + ' : '+ item.get("description", locale)?default(""))}"},</#list>];
	<#assign fixedAssetTypeList = delegator.findList("FixedAssetType",  null, null, null, null, true) />
	var fixedAssetTypeData = [
		<#list fixedAssetTypeList as fixedAssetType>
			<#assign description = StringUtil.wrapString(fixedAssetType.get("description", locale)) />
			{
				description: "${description?if_exists?default("")}",
				fixedAssetTypeId: "${fixedAssetType.fixedAssetTypeId}"
			},
		</#list>
	];
</script>
<#include "initGeneralDropdown.ftl"/>
<#include "rowdetail/initRowDetailAccTransaction.ftl"/>
<#assign columnlisttran="{ text: '${uiLabelMap.acctgTransId}', dataField: 'acctgTransId', width: 200, editable: false,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata){
							if(rowdata.isPosted == 'Y'){
								return '<div class=\"jqx-cell-disable\"><span><a href=' + 'EditAccountingTransaction?acctgTransId='+ value + '&organizationPartyId=company' + '>' + value + '</a></span></div>'
							}
							return '<span><a href=' + 'EditAccountingTransaction?acctgTransId='+ value + '&organizationPartyId=company' + '>' + value + '</a></span>'
						}
					 },
 					 { text: '${uiLabelMap.transactionDate}',filtertype: 'range', dataField: 'transactionDate', width: 200, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',
 					 	cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
 					 		if(rowdata.isPosted == 'Y'){
 					 			var x = value;
					 			if(typeof(value) == 'object'){
					 				x = Utils.formatDateDMY(value, '/');
					 			}
								return '<div class=\"jqx-cell-disable\"><span>' + x + '</span></div>'
							}		
 					 	},
						createeditor: function (row, column, editor) {
                            editor.jqxDateTimeInput({width: '200', height: '25px', formatString: 'dd-MM-yyyy',clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
							editor.jqxDateTimeInput('val', null);
                        },
 					 	cellbeginedit: beginedit
 					 },
					 { text: '${uiLabelMap.FormFieldTitle_acctgTransTypeId}', dataField: 'acctgTransTypeId', width: 200,columntype: 'dropdownlist',filtertype: 'checkedlist',
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							var x = value;
							for(i = 0; i < acctgTransTypesData.length; i++){
								if(value == acctgTransTypesData[i].acctgTransTypeId){
									x = acctgTransTypesData[i].description;
								}
							}							
							if(rowdata.isPosted == 'Y'){
								return '<div class=\"jqx-cell-disable\"><span>' + x + '</span></div>'
							}
							return '<span>' + x + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterBoxAdapter2 = new $.jqx.dataAdapter(acctgTransTypesData, {autoBind: true});
				   			var uniqueRecords2 = filterBoxAdapter2.records;
				   			uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   			widget.jqxDropDownList({ theme: theme, source: uniqueRecords2, displayMember: 'description', valueMember: 'acctgTransTypeId',dropDownHeight: 300});
				   		},
						createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({ theme: theme, source: acctgTransTypesData, displayMember: 'description', valueMember: 'acctgTransTypeId',dropDownHeight: 300, height: '25'});
                        },
						cellbeginedit: beginedit
					 },
					 { text: '${uiLabelMap.glFiscalTypeId}', dataField: 'glFiscalTypeId', width: 200, columntype: 'dropdownlist',filtertype: 'checkedlist',
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							var x = value; 
							for(i = 0; i < glFiscalTypesData.length; i++){
								if(value == glFiscalTypesData[i].glFiscalTypeId){
									x = glFiscalTypesData[i].description;
								}
							}							
							if(rowdata.isPosted == 'Y'){
								return '<div class=\"jqx-cell-disable\"><span>' + x + '</span></div>'
							}
							return '<span>' + x + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterBoxAdapter2 = new $.jqx.dataAdapter(glFiscalTypesData, {autoBind: true});
				   			var uniqueRecords2 = filterBoxAdapter2.records;
				   			uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   			widget.jqxDropDownList({ theme: theme, source: uniqueRecords2, displayMember: 'description', valueMember: 'glFiscalTypeId', dropDownWidth: 300});
				   		},
						createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({ theme: theme, source: glFiscalTypesData, displayMember: 'description', valueMember: 'glFiscalTypeId', dropDownWidth: 300, height: '25'});
                        },
						cellbeginedit: beginedit
					 },
					 { text: '${uiLabelMap.accInvoiceId}', dataField: 'invoiceId', width: 200, columntype: 'template',
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							var x = value;
							var data = $('#listTran').jqxGrid('getrowdata',row);
							if(data.acctgTransTypeId == 'PURCHASE_INVOICE'){
								if(rowdata.isPosted == 'Y'){
									return '<div class=\"jqx-cell-disable\"><span><a title=' + x + ' href=' +'accApinvoiceOverviewGlobal?invoiceId=' + x + '>' + x + '</a></span></div>'
								}
								return '<span> <a title=' + x + ' href=' +'accApinvoiceOverviewGlobal?invoiceId=' + x + '>' + x + '</a> </span>';
							}else if(data.acctgTransTypeId == 'SALES_INVOICE'){
								if(rowdata.isPosted == 'Y'){
									return '<div class=\"jqx-cell-disable\"><span><a title=' + x + ' href=' +'accArinvoiceOverviewGlobal?invoiceId=' + x + '>' + x + '</a></span></div>'
								}
								return '<span> <a title=' + x + ' href=' +'accArinvoiceOverviewGlobal?invoiceId=' + x + '>' + x + '</a> </span>';
								
							}else{
								if(rowdata.isPosted == 'Y'){
									return '<div class=\"jqx-cell-disable\"><span><a title=' + x + ' href=' +'accApinvoiceOverviewGlobal?invoiceId=' + x + '>' + x + '</a></span></div>'
								}
								return '<span> ' + x + ' </span>';
							}
							
						},
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxInvoiceTranEdit\"></div>');
                            initInvoiceSelect(editor, $('#jqxInvoiceTranEdit'));
                            editor.jqxDropDownButton('setContent', cellvalue);
	                      },
	                      geteditorvalue: function (row, cellvalue, editor) {
	                          editor.jqxDropDownButton(\"close\");
	                           var ini = $('#jqxInvoiceTranEdit').jqxGrid('getselectedrowindex');
	                            if(ini != -1){
		                            var item = $('#jqxInvoiceTranEdit').jqxGrid('getrowdata', ini);
		                            var selectedPro = item.invoiceId;
		                            return selectedPro;	
	                            }
	                            return cellvalue;
	                      },
						cellbeginedit: beginedit
					 },
					 { text: '${uiLabelMap.paymentId}', dataField: 'paymentId', width: 200,columntype: 'template',
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							var x = value;
							var data = $('#listTran').jqxGrid('getrowdata',row);
							if(data.acctgTransTypeId == 'PURCHASE_INVOICE'){
								if(rowdata.isPosted == 'Y'){
									return '<div class=\"jqx-cell-disable\"><span><a title=' + x + ' href=' +'accAppaymentOverview?paymentId=' + x + '>' + x + '</a></span></div>'
								}
								return '<span> <a title=' + x + ' href=' +'accAppaymentOverview?paymentId=' + x + '>' + x + '</a> </span>';
							}else if(data.acctgTransTypeId == 'SALES_INVOICE'){
								if(rowdata.isPosted == 'Y'){
									return '<div class=\"jqx-cell-disable\"><span><a title=' + x + ' href=' +'accArpaymentOverview?paymentId=' + x + '>' + x + '</a></span></div>'
								}
								return '<span> <a title=' + x + ' href=' +'accArpaymentOverview?paymentId=' + x + '>' + x + '</a> </span>';
							}else {
								if(rowdata.isPosted == 'Y'){
									return '<div class=\"jqx-cell-disable\"><span><a title=' + x + ' href=' +'accArpaymentOverview?paymentId=' + x + '>' + x + '</a></span></div>'
								}
								return '<span>' + x + '</span>';
								
							}
						},
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxPaymentTranEdit\"></div>');
                            initPaymentSelect(editor, $('#jqxPaymentTranEdit'));
                            editor.jqxDropDownButton('setContent', cellvalue);
	                      },
	                      geteditorvalue: function (row, cellvalue, editor) {
	                          editor.jqxDropDownButton(\"close\");
	                           var ini = $('#jqxPaymentTranEdit').jqxGrid('getselectedrowindex');
	                            if(ini != -1){
		                            var item = $('#jqxPaymentTranEdit').jqxGrid('getrowdata', ini);
		                            var selectedPro = item.paymentId;
		                            return selectedPro;	
	                            }
	                            return cellvalue;
	                      },
						cellbeginedit: beginedit
					 },
					 { text: '${uiLabelMap.FormFieldTitle_workEffortId}', dataField: 'workEffortId', width: 200, columntype: 'template',
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							var x = value;
							if(rowdata.isPosted == 'Y'){
								return '<div class=\"jqx-cell-disable\"><span><a title=' + id + ' href=' +'/workeffort/control/EditWorkEffort?workEffortId=' + id + '>' + x + '</a></span></div>'
							}
							return '<span> <a title=' + id + ' href=' +'/workeffort/control/EditWorkEffort?workEffortId=' + id + '>' + x + '</a> </span>';
						},
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxWorkEffortTranEdit\"></div>');
                            initWorkEffortSelect(editor, $('#jqxWorkEffortTranEdit'));
                            editor.jqxDropDownButton('setContent', cellvalue);
	                      },
	                      geteditorvalue: function (row, cellvalue, editor) {
	                          editor.jqxDropDownButton(\"close\");
	                           var ini = $('#jqxWorkEffortTranEdit').jqxGrid('getselectedrowindex');
	                            if(ini != -1){
		                            var item = $('#jqxWorkEffortTranEdit').jqxGrid('getrowdata', ini);
		                            var selectedPro = item.workEffortId;
		                            return selectedPro;	
	                            }	
	                            return cellvalue;
	                      },
						cellbeginedit: beginedit
					 },
					 { text: '${uiLabelMap.ShipmentId}', dataField: 'shipmentId', width: 200, columntype: 'template',
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							var x = value;
							if(rowdata.isPosted == 'Y'){
								return '<div class=\"jqx-cell-disable\"><span><a title=' + id + ' href=' +'/facility/control/EditShipment?shipmentId=' + id + '>' + x + '</a></span></div>'
							}
							return '<span> <a title=' + id + ' href=' +'/facility/control/EditShipment?shipmentId=' + id + '>' + x + '</a> </span>';
						},
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxShipmentTranEdit\"></div>');
                            initShipmentSelect(editor, $('#jqxShipmentTranEdit'));
                            editor.jqxDropDownButton('setContent', cellvalue);
	                      },
	                      geteditorvalue: function (row, cellvalue, editor) {
	                          editor.jqxDropDownButton(\"close\");
	                           var ini = $('#jqxShipmentTranEdit').jqxGrid('getselectedrowindex');
	                            if(ini != -1){
		                            var item = $('#jqxShipmentTranEdit').jqxGrid('getrowdata', ini);
		                            var selectedPro = item.shipmentId;
		                            return selectedPro;	
	                            }
	                            return cellvalue;
	                      },
						cellbeginedit: beginedit
					 },
					 { text: '${uiLabelMap.postedDate}',filtertype: 'range', dataField: 'postedDate', width: 200, cellsformat: 'dd/MM/yyyy',columntype: 'datetimeinput',
					 	cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
					 		if(rowdata.isPosted == 'Y'){
					 			var x = value;
					 			if(typeof(value) == 'object'){
					 				x = Utils. formatDateDMY(value, '/');
					 			}
								return '<div class=\"jqx-cell-disable\"><span>' + x + '</span></div>'
							}
					 	},
						initeditor: function (row, column, editor) {
                            editor.jqxDateTimeInput({width: '200', height: '25px', formatString: 'dd-MM-yyyy',clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
							editor.jqxDateTimeInput('val', null);
                        },
					 	cellbeginedit: beginedit
					 },
					 { text: '${uiLabelMap.accPostTransaction}', width: 150, dataField:'isPosted',columntype: 'dropdownlist',filtertype: 'checkedlist',
						 cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						 	if(rowdata.isPosted == 'Y'){
						 		return '<div class=\"cell-custom-grid jqx-cell-disable\">' + '${uiLabelMap.accPostted}' + '</div>';
						 	}else{
						 		return '<div class=\"cell-custom-grid\"><a href=postAcctgTrans?acctgTransId=' + rowdata.acctgTransId +'>' + '${uiLabelMap.accNonePost}' + '</a></div>';
						 	}
					 	},				 	
						createeditor: function (row, column, editor) {
							var arr = [{description: '${uiLabelMap.accPostted}', value : 'Y'},{description: '${uiLabelMap.accNonePost}', value : 'N'}];
                            editor.jqxDropDownList({ theme: theme, source: arr, displayMember: 'description', valueMember: 'value', width: '200', height: '25', autoDropDownHeight: true});
							editor.on('change', function(event){
								var args = event.args;
			     	   			if (args) {
									var x = $('#datetimeeditorlistTranpostedDate');
									var data = x.jqxDateTimeInput('val');
		     	    				var item = args.item;
			     		    		if (item && item.value == 'Y' && !data){
										x.jqxTooltip({ content: '${uiLabelMap.postedDateIsRequired}', position: 'bottom', theme:'Orange', autoHide:false});
										x.jqxTooltip('open');
										editor.jqxDropDownList('clearSelection');
						     	    }else{x.jqxTooltip('close');}
								}
							});
                        },
                        createfilterwidget: function (column, columnElement, widget) {
                        	var arr = [{description: '${uiLabelMap.accPostted}', value : 'Y'},{description: '${uiLabelMap.accNonePost}', value : 'N'}];
				   			var filterBoxAdapter2 = new $.jqx.dataAdapter(arr, {autoBind: true});
				   			var uniqueRecords2 = filterBoxAdapter2.records;
							uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
                            widget.jqxDropDownList({ theme: theme, source: uniqueRecords2, displayMember: 'description', valueMember: 'value', dropDownWidth: 300});
				   		},
					 	cellbeginedit: beginedit
					 },
					 { text: '${uiLabelMap.PDF}', width: 100, editable: false,
						 cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						 	var data = $(' #listTran').jqxGrid('getrowdata', row);
						 	if(!data.isPosted || data.isPosted == 'N'){
						 		return '<div class=\"cell-custom-grid\">&nbsp;</div>';
						 	}else{
						 		return '<div class=\"cell-custom-grid jqx-cell-disable\"><span><a href=acctgTransDetailReportPdf.pdf?acctgTransId=' + data.acctgTransId +'>' + '${uiLabelMap.PDF}' + '</a></span></div>';
						 	}
					 	},
					 	cellbeginedit: beginedit
					 }
					 "/>
<#assign dataField="[{ name: 'acctgTransId', type: 'string' },
                 	{ name: 'transactionDate', type: 'date', other:'Timestamp' },
                 	{ name: 'acctgTransTypeId', type: 'string' },
                 	{ name: 'acctgTransTypeDescription', type: 'string' },
					{ name: 'glFiscalTypeId', type: 'string' },
					{ name: 'glFiscalTypeDescription', type: 'string' },
					{ name: 'invoiceId', type: 'string' },
					{ name: 'invoiceDescription', type: 'string' },
                 	{ name: 'paymentId', type: 'string' },
                 	{ name: 'paymentDescription', type: 'string' },
                 	{ name: 'workEffortId', type: 'string' },
                 	{ name: 'workEffortName', type: 'string' }, 
                 	{ name: 'shipmentId', type: 'string'},
                 	{ name: 'shipmentDescription', type: 'string'},
                 	{ name: 'isPosted', type: 'string'},
                 	{ name: 'postedDate', type: 'date', other:'Timestamp'}
		 		 	]"/>
<#if parameters.paymentId?exists>
	<#assign showtoolbar="false"/>
	<#assign url="jqxGeneralServicer?sname=JQListTransaction&paymentId=${parameters.paymentId}"/>
<#else>
	<#assign showtoolbar="true"/>
	<#assign url="jqxGeneralServicer?sname=JQListTransaction"/>	 
</#if>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlisttran clearfilteringbutton="true"  addrefresh="true"
	     showtoolbar=showtoolbar
	     addrow="true" filterable="true" alternativeAddPopup="accTransaction"
		 url=url id="listTran" initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="200"
		 createUrl="jqxGeneralServicer?sname=createAcctgTrans&jqaction=C" 
	     addColumns="acctgTransTypeId;glFiscalTypeId;finAccountTransId;description;shipmentId;fixedAssetId;invoiceId;paymentId;productId;workEffortId;" +
	     "voucherRef;voucherDate(java.sql.Timestamp);receiptId;theirAcctgTransId;glJournalId;inventoryItemId;physicalInventoryId;groupStatusId;partyId;roleTypeId;" +
	     "transactionDate(java.sql.Timestamp);scheduledPostingDate(java.sql.Timestamp);isPosted;postedDate(java.sql.Timestamp);organizationPartyId"
	     updateUrl="jqxGeneralServicer?sname=updateAcctgTrans&jqaction=U" editable="true"
	     editColumns="acctgTransId;acctgTransTypeId;glFiscalTypeId;finAccountTransId;description;shipmentId;fixedAssetId;invoiceId;paymentId;productId;workEffortId;" +
	     "voucherRef;voucherDate(java.sql.Timestamp);receiptId;theirAcctgTransId;glJournalId;inventoryItemId;physicalInventoryId;groupStatusId;partyId;roleTypeId;" +
	     "transactionDate(java.sql.Timestamp);scheduledPostingDate(java.sql.Timestamp);isPosted;postedDate(java.sql.Timestamp)"
	     removeUrl="jqxGeneralServicer?sname=deleteAcctgTrans&jqaction=D"
	     deleterow="true" deleteColumn="acctgTransId" deleteConditionFunction="deleteConditionFunction"
	     customcontrol1="fa fa-flash@${uiLabelMap.QuickAddTransaction}@javascript: void(0);@quickAddTransaction()"
	     />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>		     
<script src="/delys/images/js/generalUtils.js"></script>
<script>
	var deleteConditionFunction = function(){
		var index = $("#listTran").jqxGrid("getselectedrowindex");
		if(index == -1){
			return false;
		}
		var data = $("#listTran").jqxGrid("getrowdata", index);
		if(data.isPosted == 'Y'){
			return false;
		}
		return true;
	};
	var beginedit = function(row, datafield, columntype){
		var data = $("#listTran").jqxGrid("getrowdata", row);
		if(data.isPosted == "Y"){
			return false;
		}
	};
</script>
<#include "popup/popupAccTransaction.ftl"/>
<#include "popup/popupQuickAddTransaction.ftl"/>
<#include "popup/popupAccTransactionEntry.ftl"/>