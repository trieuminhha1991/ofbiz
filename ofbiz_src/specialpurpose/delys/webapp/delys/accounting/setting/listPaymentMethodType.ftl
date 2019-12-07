<#assign getAlGlAccount="getAll" />
<script src="../images/js/generalUtils.js"></script>		 
<script type="text/javascript" language="Javascript">
var initDropDown = function(dropdown,grid){
		GridUtils.initDropDownButton({url : 'getListGLAccountOACsData&getAlGlAccount=${getAlGlAccount?if_exists}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 500, dropDownHorizontalAlignment: false}},
		[
			{name : 'glAccountId',type : 'string'},
			{name : 'accountName',type : 'string'},
			{name : 'accountCode',type : 'string'}
		], 
		[
			{text : '${uiLabelMap.accountCode}',datafield : 'accountCode',width : '30%'},
			{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
		]
		, null, grid,dropdown,'glAccountId');
	}
</script>
<#assign dataField="[{ name: 'paymentMethodTypeId', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'defaultGlAccountId', type: 'string' },
					 { name: 'accountCode', type: 'string' },
					 { name: 'accountName', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.CommonPaymentMethodType}', datafield: 'paymentMethodTypeId',editable:false, selectable:false, hidden: true},
					 { text: '${uiLabelMap.description}', datafield: 'description',editable:false},
					 { text: '${uiLabelMap.accDefaultGlAccountId}', columntype: 'template',width  :'400', datafield: 'defaultGlAccountId',createeditor : function(row,cellvalue,editor){
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
							},cellsrenderer : function(row){
								var data = $('#jqxgrid').jqxGrid('getrowdata',row);
								if(typeof(data) != 'undefined'){
									if(data.accountCode != null){
										return '<span>' + data.accountCode + '</span>';
									}else return '';
								}
								return '<span>' + data.defaultGlAccountId + '</span>';
							}},
					 	{ text: '${uiLabelMap.accountName}', datafield: 'accountName',editable:false},
					"/>	
<@jqGrid url="jqxGeneralServicer?sname=JQGetListPaymentMethodTypes" dataField=dataField columnlist=columnlist showtoolbar="true" editable="true" clearfilteringbutton="true"
		 editrefresh="true"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePaymentMethodType" editColumns="paymentMethodTypeId;description;defaultGlAccountId" height="640" 
		 />