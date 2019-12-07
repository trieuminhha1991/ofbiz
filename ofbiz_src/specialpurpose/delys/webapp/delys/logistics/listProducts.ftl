<#if security.hasEntityPermission("PRODUCT", "_VIEW", session)>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript">
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [
						<#if weightUoms?exists>
							<#list weightUoms as item> 
							{
								weightUomId: "${item.uomId?if_exists}",
								description: "${StringUtil.wrapString(item.description?if_exists)}"
							},
							</#list>
						</#if>
                        ];
	
	var mapWeightUom = {
							<#if weightUoms?exists>
								<#list weightUoms as item>
									"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
								</#list>
							</#if>
							};
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [
						<#if quantityUoms?exists>
							<#list quantityUoms as item> 
							{
								quantityUomId: "${item.uomId?if_exists}",
								description: "${StringUtil.wrapString(item.description?if_exists)}"
							},
							</#list>
						</#if>
                        ];
	
	var mapQuantityUom = {
						<#if quantityUoms?exists>
							<#list quantityUoms as item>
								"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
							</#list>
						</#if>
						};
	
	function updateListProduct(){
		$("#jqxgrid").jqxGrid('updatebounddata');
	}
</script>
<#assign dataField="[{ name: 'productId', type: 'string'},
				   { name: 'internalName', type: 'string'},
				   { name: 'productCode', type: 'string'},
				   { name: 'description', type: 'string'},
				   { name: 'weight', type: 'string'},
				   { name: 'weightUomId', type: 'string'},
				   { name: 'quantityUomId', type: 'string'},
				   { name: 'productCategoryId', type: 'string'},
				   { name: 'fromDate', type: 'date', other: 'Timestamp'}
				   ]"/>

<#assign columnlist="
		   {	text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
		   },
		   { text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', width: 200, align: 'center', editable:false },
		   { text: '${StringUtil.wrapString(uiLabelMap.DAInternalName)}', datafield: 'internalName', editable:false, width: 250, align: 'center'},
		   { text: '${StringUtil.wrapString(uiLabelMap.description)}', datafield: 'description', editable:false, minwidth: 200, align: 'center'},
		   { text: '${StringUtil.wrapString(uiLabelMap.QuantityUomId)}', editable:false, datafield: 'quantityUomId', width: 150, align: 'center',
	        	cellsrenderer: function(row, colum, value){
    				return '<span title= ' + value + '>' + mapQuantityUom[value] +  '</span>';
				}
		   },
		   { text: '${StringUtil.wrapString(uiLabelMap.weight)}', datafield: 'weight', editable:false, width: 150, align: 'center',
			   cellsrenderer: function(row, colum, value){
				   var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				   var weightUomId = data.weightUomId;
				   return '<span>' + value +' (' + mapWeightUom[weightUomId] +  ')</span>';
			   }
		   },
		   "/>
			   
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" editrefresh="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" editColumns="productId;productCode"
		url="jqxGeneralServicer?sname=JQGetListProduct" functionAfterUpdate="updateListProduct()"
		contextMenuId="contextMenu" mouseRightMenu="true"
	/>
<#assign menuHeight = 60 />
<div id='contextMenu' style="display:none;">
	<ul>
		<li id='viewProductDetails'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewProductDetails}</li>
		<#if security.hasEntityPermission("PRODUCT_PRICE", "_ADMIN", session)>
			<#assign menuHeight = 85 />
			<li id='viewProductPrices'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewProductPrices}</li>
		</#if>
		<li id='viewProductPacking'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewProductPacking}</li>
	</ul>
</div>
<script>
			   
	var contextMenu = $("#contextMenu").jqxMenu({ width: 220, height: '${menuHeight}', autoOpenPopup: false, mode: 'popup', theme: 'olbius' });
    $("#jqxgrid").on('contextmenu', function () {
        return false;
    });
    
    $("#viewProductDetails").on("click", function() {
    	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
		var productId = rowData.productId;
		var fromDate = rowData.fromDate.toSQLTimeStamp();
		var productCategoryId = rowData.productCategoryId;
		window.location.href = "addNewProduct?productId=" + productId + "&fromDate=" + fromDate + "&productCategoryId=" + productCategoryId;
    });
    $("#viewProductPrices").on("click", function() {
    	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
    	var productId = rowData.productId;
    	window.location.href = "ListProductPrice?productId=" + productId;
    });
    $("#viewProductPacking").on("click", function() {
    	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
    	var productId = rowData.productId;
    	var form = "<form method='POST' action='ListProductConfigPacking' id='ListProductConfigPacking'><input type='hidden' name='productId' value=" + productId + " /></form>";
    	$('body').append(form);
    	$("#ListProductConfigPacking").submit();
    });
</script>
	<#else>   
		<h2> You do not have permission</h2>
</#if>