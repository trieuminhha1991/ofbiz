<#if security.hasEntityPermission("PRODUCT", "_ADMIN", session)>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>

<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>

<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) >
<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) >
<#assign listProductCategory = delegator.findList("ProductCategory", null, null, null, null, false) >
<script>
	var listProductCategory = [
	                       	<#if listProductCategory?exists>
								<#list listProductCategory as item>
									"${item.productCategoryId?if_exists}".toLowerCase(),
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
	var mapQuantityUom = {
			<#if quantityUoms?exists>
				<#list quantityUoms as item>
					"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
				</#list>
			</#if>
			};
</script>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var sourceGridDetail =
    {
        localdata: datarecord.rowDetail,
        datatype: 'local',
        datafields:
        [
           { name: 'productId', type: 'string'},
		   { name: 'internalName', type: 'string'},
		   { name: 'productCode', type: 'string'},
		   { name: 'description', type: 'string'},
		   { name: 'weight', type: 'string'},
		   { name: 'weightUomId', type: 'string'},
		   { name: 'quantityUomId', type: 'string'},
		   { name: 'productCategoryId', type: 'string'},
		   { name: 'fromDate', type: 'date', other: 'Timestamp'}
        ]
    };
    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
    grid.jqxGrid({
        width: '98%',
        height: '92%',
        theme: 'olbius',
        localization: getLocalization(),
        source: dataAdapterGridDetail,
        sortable: true,
        pagesize: 5,
 		pageable: true,
        selectionmode: 'singlerow',
        columns: [
					{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
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
						   var data = grid.jqxGrid('getrowdata', row);
						   var weightUomId = data.weightUomId;
						   return '<span>' + value +' (' + mapWeightUom[weightUomId] +  ')</span>';
					   }
					}
                 ]
    });
}"/>
<#assign dataField="[{ name: 'productCategoryId', type: 'string'},
					{ name: 'productCategoryTypeId', type: 'string'},
					{ name: 'categoryName', type: 'string'},
					{ name: 'longDescription', type: 'string'},
					{ name: 'rowDetail', type: 'string'}
					]"/>

<#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DACategoryId)}', datafield: 'productCategoryId', align: 'center', width: 270, editable: false},
					{ text: '${StringUtil.wrapString(uiLabelMap.DACategoryName)}', datafield: 'categoryName', align: 'center', width: 300},
					{ text: '${StringUtil.wrapString(uiLabelMap.DADescription)}', datafield: 'longDescription', align: 'center', editable: false }
					"/>
	
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
		initrowdetails = "true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"
		url="jqxGeneralServicer?sname=JQGetListProductCategory"
		updateUrl="jqxGeneralServicer?sname=updateProductCategory&jqaction=U"
		createUrl="jqxGeneralServicer?sname=createProductCategory&jqaction=C"
		editColumns="productCategoryId;productCategoryTypeId;categoryName;longDescription"
		addColumns="productCategoryId;productCategoryTypeId;categoryName;longDescription"
		contextMenuId="contextMenu" mouseRightMenu="true"
		/>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id='editDescription'><i class="icon-edit"></i>&nbsp;&nbsp;${uiLabelMap.EditorDescripton}</li>
	</ul>
</div>

<div id="jqxwindowEditor" style="display:none;">
	<div>${uiLabelMap.EditorDescripton}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<textarea id="tarDescriptionEditor"></textarea>
			</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id='cancelEdit' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button id='saveEdit' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="alterpopupWindow" style="display:none;">	
	<div>${uiLabelMap.AddProductCategory}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DACategoryId}</label></div>
	        	<div class="span9"><input type="text" id="txtProductCategoryId" /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DACategoryName}</label></div>
	        	<div class="span9"><input type="text" id="txtCategoryName" /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3"><label class="text-right">${uiLabelMap.DADescription}&nbsp;&nbsp;&nbsp;</label></div>
	        	<div class="span9"><textarea id="tarDescription"></textarea></div>
	        </div>
	    </div>
	    <div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 30, autoOpenPopup: false, mode: 'popup'});
	$("#jqxgrid").on('contextmenu', function () {
	    return false;
	});
	$("#editDescription").on("click", function() {
		rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var cell = $('#jqxgrid').jqxGrid('getcell', rowIndexEditing, "longDescription" );
		$("#jqxwindowEditor").jqxWindow('open');
		$('#tarDescriptionEditor').jqxEditor({
	        theme: 'olbiuseditor'
	    });
		$('#tarDescriptionEditor').val(cell.value);
	});
	$("#jqxwindowEditor").jqxWindow({ theme: 'olbius',
	    width: 550, maxWidth: 1845, minHeight: 310, height: 340, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelEdit"), modalOpacity: 0.7
	});
	$("#saveEdit").click(function () {
		var newValue = $('#tarDescriptionEditor').val();
		$("#jqxgrid").jqxGrid('setCellValue', rowIndexEditing, "longDescription", newValue);
		$("#jqxwindowEditor").jqxWindow('close');
	});

	$("#alterpopupWindow").jqxWindow({
	    width: 650, maxWidth: 1000, theme: "olbius", minHeight: 420, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
	});
	$('#alterpopupWindow').on('open', function () {
		$('#tarDescription').jqxEditor({
	        theme: 'olbiuseditor',
	        width: '98%',
	        height: 240
	    });
		$("#tarDescription").jqxEditor('val', "");
		$("#txtProductCategoryId").val("");
		$("#txtCategoryName").val("");
	});
	$('#alterpopupWindow').on('close', function () {
		$('#alterpopupWindow').jqxValidator('hide');
	});
	
	$("#alterSave").click(function () {
		if ($('#alterpopupWindow').jqxValidator('validate')) {
			var row = {};
	    	row.productCategoryId = $("#txtProductCategoryId").val();
	    	row.productCategoryTypeId = "CATALOG_CATEGORY";
	    	row.categoryName = $("#txtCategoryName").val();
	    	row.longDescription = $("#tarDescription").val();
	    	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        setCookie("categoryChange");
	        $("#alterpopupWindow").jqxWindow('close');
		}
	});
	$('#alterpopupWindow').jqxValidator({
	    rules: [
					{ input: '#txtProductCategoryId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#txtProductCategoryId', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtProductCategoryId").val();
							if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtProductCategoryId', message: '${StringUtil.wrapString(uiLabelMap.CategoryIdAlreadyExists)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtProductCategoryId").val().toLowerCase();
							if (_.indexOf(listProductCategory, value) === -1) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtCategoryName', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' }
	           ]
	});
	function hasWhiteSpace(s) {
		  return /\s/g.test(s);
	}
	var getLocalization = function () {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
	    localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
	    localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
	    localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
	    localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
	    localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
	    localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
	    localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
	    localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}";
	    localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
	    localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
	    localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	    localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.Groupsheaderstring)}";
	    return localizationobj;
	}
</script>
	<#else>
		<h2> You do not have permission</h2>
</#if>