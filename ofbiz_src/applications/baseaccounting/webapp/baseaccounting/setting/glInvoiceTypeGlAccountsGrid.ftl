<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<#assign dataField="[{ name: 'invoiceItemTypeId', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'defaultGlAccountId', type: 'string' },
					 { name: 'accountCode', type: 'string' },
					 { name: 'accountName', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceItemTypeId}', width: 250, datafield: 'invoiceItemTypeId', editable:false, hidden: true},
					 { text: '${uiLabelMap.description}', width: '30%', datafield: 'description', editable:false},
					 { text: '${uiLabelMap.AccountingGlAccount}', datafield: 'defaultGlAccountId',width  :'400',columntype : 'template',
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						 	   editor.append('<div id=\"glAccountGrid\"></div>');
						 	   var configGlAccount = {
									useUrl: true,
									showDetail: false,
									root: 'results',
									widthButton: '400',
									showdefaultloadelement: false,
									autoshowloadelement: false,
									datafields: [{name: 'glAccountId', type: 'string'}, {name: 'accountName', type: 'string'}],
									columns: [
										{text: '${uiLabelMap.BACCGlAccountId}', datafield: 'glAccountId', width: '30%'},
										{text: '${uiLabelMap.BACCAccountName}', datafield: 'accountName'}
									],
									url: 'JqxGetListGlAccounts',
									useUtilFunc: true,
									
									key: 'glAccountId',
								};
								accutils.initDropDownButton(editor, $('#glAccountGrid'), null, configGlAccount, []);
								EDITOR_GRID = editor;
						},
						cellsrenderer  : function(row){
							var data = $('#jqxgrid').jqxGrid('getrowdata',row);
							if(typeof(data) != 'undefined'){
							var code = data.accountCode ? data.accountCode : '';
								return '<span>' + code + '<span>';
							}
							return '';
						}
					 },
					 { text : '${uiLabelMap.BACCAccountName}',datafield : 'accountName',editable : false}
					"/>	
<@jqGrid url="jqxGeneralServicer?sname=JQGetListInvoiceItemTypeGLA" dataField=dataField columnlist=columnlist showtoolbar="true" editable="true" 
		 height="640" filterable="true" sortable="true" editmode="selectedcell" clearfilteringbutton="true"
		 id="jqxgrid" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateInvoiceItemType" editColumns="invoiceItemTypeId;description;defaultGlAccountId"
		 />