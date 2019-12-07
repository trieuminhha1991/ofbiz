<#assign getAlGlAccount="getAll"/>
<script src="../images/js/generalUtils.js"></script>		 
<script>
	var initDropDown = function(dropdown,grid){
		GridUtils.initDropDownButton({url : 'getListGLAccountOACsData&getAlGlAccount=${getAlGlAccount?if_exists}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 400, dropDownHorizontalAlignment: true}},
		[
			{name : 'glAccountId',type : 'string'},
			{name : 'accountCode',type : 'string'},
			{name : 'accountName',type : 'string'}
		], 
		[
			{text : '${uiLabelMap.accountCode}',datafield : 'accountCode',width : '30%'},
			{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
		]
		, null, grid,dropdown,'glAccountId');
	}
</script>	
<#assign dataField="[{ name: 'invoiceItemTypeId', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'defaultGlAccountId', type: 'string' },
					 { name: 'accountCode', type: 'string' },
					 { name: 'accountName', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceItemTypeId}', width: 250, datafield: 'invoiceItemTypeId', editable:false, hidden: true},
					 { text: '${uiLabelMap.description}', width: '30%', datafield: 'description', editable:false},
					 { text: '${uiLabelMap.AccountingGlAccount}', datafield: 'defaultGlAccountId',width  :'400',columntype : 'template',createeditor : function(row,cellvalue,editor){
					 	var container = $('<div id=\"glAccountId\"><div id=\"jqxgridGlAccount\"></div></div>');
					 	editor.append(container);
					 	editor.jqxDropDownButton('setContent',cellvalue);
					 	 initDropDown($('#glAccountId'),$('#jqxgridGlAccount'));
					 },geteditorvalue: function (row, cellvalue, editor) {
					 		 editor.jqxDropDownButton(\"close\");
	                           var ini = $('#jqxgridGlAccount').jqxGrid('getselectedrowindex');
	                            if(ini != -1){
		                            var item = $('#jqxgridGlAccount').jqxGrid('getrowdata', ini);
		                            var selectedPro = item.glAccountId;
		                            return selectedPro;	
	                            }
	                            return cellvalue;
						},cellsrenderer  : function(row){
							var data = $('#jqxgrid').jqxGrid('getrowdata',row);
							if(typeof(data) != 'undefined'){
							var code = data.accountCode ? data.accountCode : '';
								return '<span>' + code + '<span>';
							}
							return '';
						}},
					 { text : '${uiLabelMap.accountName}',datafield : 'accountName',editable : false}
					"/>	
<@jqGrid url="jqxGeneralServicer?sname=JQGetListInvoiceItemTypeGLA" dataField=dataField columnlist=columnlist showtoolbar="true" editable="true" 
		 height="640" filterable="true" sortable="true" editmode="selectedcell" clearfilteringbutton="true"
		 id="jqxgrid" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateInvoiceItemType" editColumns="invoiceItemTypeId;description;defaultGlAccountId"
		 />
	 
		 
		 