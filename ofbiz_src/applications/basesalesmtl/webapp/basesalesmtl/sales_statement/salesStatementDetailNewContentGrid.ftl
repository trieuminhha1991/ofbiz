<#--<#assign listProduct = Static["com.olbius.salesmtl.SalesStatementServices"].getListProduct(delegator)!/>-->
<#assign listProduct = []/>
<#assign dataField = "[
				{ name: 'partyId', type: 'string' },
				{ name: 'partyCode', type: 'string' },
				{ name: 'partyIdFrom', type: 'string' },
				{ name: 'partyName', type: 'string' },
               	{ name: 'totalEmployee', type: 'string'},                   
           	"/>
<#if listProduct?exists>
<#list listProduct as product>
	<#assign dataField = dataField + "
				{ name: 'prodCode_${product.productId}', type: 'number', formatter: 'integer'},
   			"/>
</#list>

<#--
{ name: 'prodId_${product.productId}', type: 'number', formatter: 'integer'},
-->
</#if>
<#assign dataField = dataField + "]"/>

<#assign uiLabelMapPartyId = '${uiLabelMap.BSEmployeeId}'>
<#if "SALES_IN" == salesStatement.salesStatementTypeId>
	<#assign uiLabelMapPartyId = '${uiLabelMap.BSDistributorId}'>
</#if>

<#assign columnlist = "
				{text: '${uiLabelMap.OrgUnitName}', datafield: 'partyIdFrom', width: '24%', pinned: true, editable: false},
				{text: '${uiLabelMapPartyId}', datafield: 'partyCode', width: '24%', pinned: true, editable: false},
				{text: '${uiLabelMap.BSEmployeeName}', datafield: 'partyName', width: '24%', pinned: true, editable: false},
		 	"/>
<#if listProduct?exists>
<#list listProduct as product>
	<#assign columnlist = columnlist + "
				{text: '${product.productCode}', dataField: 'prodCode_${product.productId}', width: '120px', cellsalign: 'right', cellClassName: cellClassCommon, editable: true},
			"/>
</#list>
</#if>

<div id="jqxSalesStatement" class="jqx-tree-grid-olbius"></div>

<#assign contextMenuSsdncItemId = "ctxmnussdnc">
<div id='contextMenu_${contextMenuSsdncItemId}' style="display:none">
	<ul>
	    <li id="${contextMenuSsdncItemId}_expand"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpand)}</li>
	    <li id="${contextMenuSsdncItemId}_expandAll"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpandAll)}</li>
	    <li id="${contextMenuSsdncItemId}_collapse"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapse)}</li>
		<li id="${contextMenuSsdncItemId}_collapseAll"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapseAll)}</li>
		<li id="${contextMenuSsdncItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<script type="text/javascript">
	$(function(){
		OlbSalesStatementContentNew.init();
	});
	var OlbSalesStatementContentNew = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuSsdncItemId}"));
		};
		var initElementComplex = function(){
			var datafields = ${dataField};
           	var columns = [${columnlist}];
			var configProductList = {
				datafields: datafields,
				columns: columns,
				width: '100%',
				height: 'auto',
				editable: true,
				editmode: 'click',
				sortable: true,
				pageable: true,
				useUtilFunc: false,
				useUrl: true,
				url: "",
				virtualmode: false,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				showtoolbar: false,
				clearfilteringbutton: false,
				selectionmode: 'multiplecellsadvanced',
				groupable: true,
				showgroupsheader: true,
				showaggregates: true,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				columnsresize: true,
			};
			new OlbGrid($("#jqxSalesStatement"), null, configProductList, []);
			<#-- "jqxGeneralServicer?sname=JQListOrganizationUnitManager&pagesize=0&salesStatementId=${parameters.salesStatementId?if_exists}",-->
		};
		var initEvent = function(){
			$("#jqxSalesStatement").on('contextmenu', function () {
	            return false;
	        });
		   	$("#jqxSalesStatement").on('rowClick', function (event) {
	            var args = event.args;
	            if (args.originalEvent.button == 2) {
	                var scrollTop = $(window).scrollTop();
	                var scrollLeft = $(window).scrollLeft();
	                $("#contextMenu_${contextMenuSsdncItemId}").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	                return false;
	            }
	        });
	        $("#contextMenu_${contextMenuSsdncItemId}").on('itemclick', function (event) {
	            var args = event.args;
		        // var tmpKey = $.trim($(args).text());
		        var tmpId = $(args).attr('id');
		        var idGrid = "#jqxSalesStatement";
		        var rowData;
		        var id;
	        	var selection = $(idGrid).jqxTreeGrid('getSelection');
	        	if (selection.length > 0) rowData = selection[0];
	        	if (rowData) id = rowData.partyId;
	        	switch(tmpId) {
	        		case "${contextMenuSsdncItemId}_refesh": { 
	        			$(idGrid).jqxTreeGrid('updateBoundData');
	        			break;
	        		};
	        		case "${contextMenuSsdncItemId}_expandAll": { 
	        			$(idGrid).jqxTreeGrid('expandAll', true);
	        			break;
	        		};
	        		case "${contextMenuSsdncItemId}_collapseAll": { 
	        			$(idGrid).jqxTreeGrid('collapseAll', true);
	        			break;
        			};
	        		case "${contextMenuSsdncItemId}_expand": { 
	        			if(id) $(idGrid).jqxTreeGrid('expandRow', id);
	        			break;
	        		};
	        		case "${contextMenuSsdncItemId}_collapse": { 
	        			if(id) $(idGrid).jqxTreeGrid('collapseRow', id);
	        			break;
        			};
	        		default: break;
	        	}
	        });
	        $("#jqxSalesStatement").on("cellendedit", function (event) {
		    	var args = event.args;
		    	var columnName = args.datafield;
		    	if (columnName && columnName.substring(0, 9) == "prodCode_") {
		    		var rowBoundIndex = args.rowindex;
			    	var data = $("#jqxSalesStatement").jqxGrid("getrowdata", rowBoundIndex);
			    	if (data && data.partyId) {
			    		var partyId = data.partyId;
			    		var productId = columnName.substring(9, columnName.length);
			    		var oldValue = args.oldvalue;
		   				var newValue = args.value;
				   		var itemId = "" + partyId + "@" + productId;
				   		if (typeof(productListMap[itemId]) != 'undefined') {
				   			var itemValue = productListMap[itemId];
				   			itemValue.quantity = newValue;
				   			productListMap[itemId] = itemValue;
				   		} else {
				   			var itemValue = {};
				   			itemValue.partyId = partyId;
				   			itemValue.productId = productId;
				   			itemValue.quantity = newValue;
				   			productListMap[itemId] = itemValue;
				   		}
			    	}
		    	}
	    	});
		};
		return {
			init: init
		}
	}());
</script>