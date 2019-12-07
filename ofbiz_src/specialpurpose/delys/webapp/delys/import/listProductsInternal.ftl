<#if security.hasEntityPermission("PRODUCT", "_ADMIN", session)>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
 <script type="text/javascript" src="/delys/images/js/ckeditor/ckeditor.js"></script>
 <script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
 <script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
 <div id="container"></div>
 
<#assign dataField="[{ name: 'productId', type: 'string'},
				   { name: 'primaryProductCategoryId', type: 'string'},
				   { name: 'internalName', type: 'string'},
				   { name: 'productName', type: 'string'},
				   { name: 'brandName', type: 'string'},
				   { name: 'weight', type: 'number'},
				   { name: 'productWeight', type: 'number'},
				   { name: 'weightUomId', type: 'string'},
				   { name: 'quantityUomId', type: 'string'},
				   { name: 'description', type: 'string'},
				   { name: 'productCategoryId', type: 'string'},
				   { name: 'fromDate', type: 'date', other: 'Timestamp'},
				   { name: 'thruDateQA', type: 'date', other: 'Timestamp'},
				   { name: 'expireDateQA', type: 'string'}
				   ]"/>

<#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', width: 200, align: 'center', editable:false},
				{ text: '${StringUtil.wrapString(uiLabelMap.DAInternalName)}', datafield: 'internalName', editable:false, width: 200, align: 'center' },
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductName)}', datafield: 'productName', editable:false, width: 200, align: 'center' },
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductBrandName)}', datafield: 'brandName', editable:false, width: 200, align: 'center' },
				{ text: '${StringUtil.wrapString(uiLabelMap.description)}', datafield: 'description', width: 250, editable:false, align: 'center' },
				{ text: '${StringUtil.wrapString(uiLabelMap.UnitLess)}', editable:false, datafield: 'quantityUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', width: 150, align: 'center',
				   cellsrenderer: function(row, colum, value){
					   value?value=mapQuantityUom[value]:value;
	   			       return '<span>' + value + '</span>';
   		        	}, createfilterwidget: function (column, htmlElement, editor) {
	   		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listQuantityUom), displayMember: 'uomId', valueMember: 'uomId' ,
	                           renderer: function (index, label, value) {
		                           	if (index == 0) {
		                           		return value;
									}
								    return mapQuantityUom[value];
				                }
	   		        	});
	   		        	editor.jqxDropDownList('checkAll');
                   }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.weight)}', datafield: 'weight', editable:false, width: 150, align: 'center', filtertype: 'number', cellsalign: 'right'},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductWeightUomId)}', editable:false, datafield: 'weightUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', width: 150, align: 'center',
				   cellsrenderer: function(row, colum, value){
					   value?value=mapWeightUom[value]:value;
	   			       return '<span>' + value + '</span>';
   		        	}, createfilterwidget: function (column, htmlElement, editor) {
	   		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listWeightUom), displayMember: 'uomId', valueMember: 'uomId' ,
	                           renderer: function (index, label, value) {
		                           	if (index == 0) {
		                           		return value;
									}
								    return mapWeightUom[value];
				                }
	   		        	});
	   		        	editor.jqxDropDownList('checkAll');
                   }
				},
				{ text: '${uiLabelMap.thruDateOfPubich}', datafield: 'thruDateQA', width: 180, filtertype: 'range', cellsformat: 'dd/MM/yyyy', align: 'center'},
				{ text: '${StringUtil.wrapString(uiLabelMap.ImportRemainDays)}', editable:false, datafield: 'expireDateQA', width: 150, align: 'center',
				   cellsrenderer: function(row, colum, value){
					   var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					   var thruDateQA = data.thruDateQA;
					   return '<span class=\"text-right\">' + excuteDate(thruDateQA) + '</span>';
					}
				}
			   "/>
			   <@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" editrefresh="true"
					showtoolbar="true" addrow="false" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false" editColumns="productId;productCode"
					url="jqxGeneralServicer?sname=JQGetListProductQA" viewSize="15"
					customcontrol1="icon-plus-sign open-sans@${uiLabelMap.AddNewProduct}@addNewProduct"
					contextMenuId="contextMenu" mouseRightMenu="true"
					removeUrl="jqxGeneralServicer?sname=removeProductFromCategory&jqaction=D" deleteColumn="productId;fromDate(java.sql.Timestamp);productCategoryId"
				/>
			   
<#assign menuHeight = 60 />
<div id='contextMenu' style="display:none;">
	<ul>
		<li id='viewProductDetails'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewProductDetails}</li>
		<li id='viewProductPacking'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewProductPacking}</li>
		<#if security.hasEntityPermission("PRODUCT_SUPPLIER", "_ADMIN", session)>
			<#assign menuHeight = 85 />
			<li id='viewProducttSuppliers'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.viewProducttSuppliers}</li>
		</#if>
	</ul>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#assign listQuantityUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
<#assign listWeightUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />

<script>
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	$(document).ready(function() {
		if(getCookie().checkContainValue("newProduct")){
			deleteCookie("newProduct");
			$('#jqxNotificationNested').jqxNotification('closeLast');
			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
	        $("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}");
	        $("#jqxNotificationNested").jqxNotification("open");
		 }
		if(getCookie().checkContainValue("updateProduct")){
			deleteCookie("updateProduct");
			$('#jqxNotificationNested').jqxNotification('closeLast');
			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
          	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
          	$("#jqxNotificationNested").jqxNotification("open");
		 }
	});
    $("#contextMenu").jqxMenu({ width: 230, height: '${menuHeight}', autoOpenPopup: false, mode: 'popup', theme: 'olbius' });
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
    $("#viewProductPacking").on("click", function() {
    	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
    	var productId = rowData.productId;
    	var form = "<form method='POST' action='ListProductsConfigPackingImport' id='ListProductConfigPacking'><input type='hidden' name='productId' value=" + productId + " /></form>";
    	$('body').append(form);
    	$("#ListProductConfigPacking").submit();
    });
    $("#viewProducttSuppliers").on("click", function() {
    	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
    	var productId = rowData.productId;
    	var form = "<form method='POST' action='ListProductSupplierImport' id='ListProductSupplier'><input type='hidden' name='productId' value=" + productId + " /></form>";
    	$('body').append(form);
    	$("#ListProductSupplier").submit();
    });
	    
    var listQuantityUom = [
	                        <#if listQuantityUom?exists>
		                        <#list listQuantityUom as item>
		                        {
		                        	uomId: '${item.uomId?if_exists}',
		                        	description: "${StringUtil.wrapString(item.description)}"
		                        },
		                        </#list>
	                        </#if>
	                        ];
	
	var mapQuantityUom = {
						<#if listQuantityUom?exists>
							<#list listQuantityUom as item>
								"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
							</#list>
						</#if>
						};
	var listWeightUom = [
	                       <#if listWeightUom?exists>
	                       <#list listWeightUom as item>
	                       {
	                    	   uomId: '${item.uomId?if_exists}',
	                    	   description: "${StringUtil.wrapString(item.description)}"
	                       },
	                       </#list>
	                       </#if>
	                       ];
	
	var mapWeightUom = {
						<#if listWeightUom?exists>
							<#list listWeightUom as item>
								"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
							</#list>
						</#if>
						};
	function fixSelectAll(dataList) {
    	var sourceST = {
		        localdata: dataList,
		        datatype: "array"
		    };
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
        var uniqueRecords2 = filterBoxAdapter2.records;
		uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		return uniqueRecords2;
	}
	function excuteDate(value) {
			if (value) {
				value = value.toString().toMilliseconds();
				var now = new Date().getTime();
				var leftTime;
				leftTime = value - now;
				leftTime = Math.ceil(leftTime/86400000);
				return leftTime;
			} else {
				return "";
			}
	}
</script>
		<#else>   
				<h2> You do not have permission</h2>
</#if>