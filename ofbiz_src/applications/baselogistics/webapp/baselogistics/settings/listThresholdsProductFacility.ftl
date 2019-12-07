<@jqGridMinimumLib/>
<script>
	//Prepare for product data
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	
	<#assign cond1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isVariant", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "N")>
	<#assign cond2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isVirtual", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "N")>
	<#assign andCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(cond1, cond2), Static["org.ofbiz.entity.condition.EntityOperator"].AND)>
	
	<#assign cond3 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isVariant", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "Y")>
	
	<#assign orCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(cond3, andCond), Static["org.ofbiz.entity.condition.EntityOperator"].OR)>
	<#assign cond4 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "CATALOG_CATEGORY")>
	
	<#assign allCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(cond4, orCond), Static["org.ofbiz.entity.condition.EntityOperator"].AND)>
	
	<#assign products = delegator.findList("ProductCategoryAndProduct", allCond, null, null, null, false) />
	
	var mapProductData = {
		<#if products?exists>
			<#list products as item>
				<#assign s1 = StringUtil.wrapString(item.get('productName', locale)?if_exists)/>
				"${item.productId?if_exists}": "${s1}",
			</#list>
		</#if>	
	};
	
	<#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("ownerPartyId", ownerPartyId, "facilityTypeId", "WAREHOUSE")), null, null, null, false) />
	var mapFacilityData = {  
			<#if facilitys?exists>
				<#list facilitys as item>
					"${item.facilityId?if_exists}": '${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}',
				</#list>
			</#if>	
	};
	

	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list quantityUoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		quantityUomData.push(row);
	</#list>
</script>
<div id="contentNotificationAddSuccess">
</div>
<#assign dataField="[
				{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'facilityId', type: 'string'},
				{ name: 'facilityName', type: 'string'},
				{ name: 'quantityUomId', type: 'string'},
				{ name: 'minimumStock', type: 'number'},
				{ name: 'reorderQuantity', type: 'number'},
				{ name: 'thresholdsSale', type: 'number'},
				{ name: 'thresholdsDate', type: 'number'},
				{ name: 'thresholdsQuantity', type: 'number'},
				{ name: 'thresholdsQuantityMax', type: 'number'},
			]"/>
<#assign columnlist="
				{
				    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 150,
				},
				{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 250,
				},
				{ text: '${uiLabelMap.Facility}', datafield: 'facilityName', align: 'left', width: 180,
				},
				{ text: '${uiLabelMap.LOGThresholdsSale}', datafield: 'thresholdsSale', align: 'left', cellsalign: 'right', width: 200, filtertype: 'number',
					cellsrenderer: function(row, colum, value){
						if(value){
							return '<span style=\"text-align: right;\">' + formatnumber(value) + ' (${uiLabelMap.Day})</span>';
						}
				    }, 
				},
				{ text: '${uiLabelMap.LOGThresholdsNearDate}', datafield: 'thresholdsDate', align: 'left', cellsalign: 'right', width: 200, filtertype: 'number',
					cellsrenderer: function(row, colum, value){
						if(value){
							return '<span style=\"text-align: right;\">' + formatnumber(value) + ' (${uiLabelMap.Day})</span>';
						}
				    }, 
				},
				{ text: '${uiLabelMap.BLMin}', columngroup: 'safetyQuantity', datafield: 'thresholdsQuantity', align: 'left', cellsalign: 'right', width: 120, filtertype: 'number',
					cellsrenderer: function(row, colum, value){
						return '<span style=\"text-align: right;\">' + formatnumber(value) +'</span>';
				    }, 
				},
				{ text: '${uiLabelMap.BLMax}', columngroup: 'safetyQuantity', datafield: 'thresholdsQuantityMax', align: 'left', cellsalign: 'right', width: 120, filtertype: 'number',
					cellsrenderer: function(row, colum, value){
						if(value){
							return '<span style=\"text-align: right;\">' + formatnumber(value) +'</span>';
						}
				    }, 
				},
				
			"/>
 <#assign columngrouplist="
				{ text: '${uiLabelMap.BLSafetyInventory}', name: 'safetyQuantity', align: 'center'},
			">

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdProductFacility" addrefresh="true" filterable="true"
	url="jqxGeneralServicer?sname=JQGetListProductFacility" columngrouplist=columngrouplist
	customTitleProperties="ConfigInventoryWarning"
	customcontrol1="fa icon-plus@${uiLabelMap.AddNew}@javascript:addProductFacility()"
	mouseRightMenu="true" contextMenuId="menuProductFacility" />	
				
<div id="alterpopupWindow" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.AddNew}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<input type="hidden" id="productIdInput"></input>
			<input type="hidden" id="facilityIdInput"></input>
			<div class="span12">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.Product} </label>
					</div>
					<div class="span7">
						<div id="productIdEdit" style="width: 100%" class="green-label"></div>
						<div id="productId" style="width: 100%" class="green-label">
							<div id="jqxgridListProduct">
				            </div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.Facility} </label>
					</div>
					<div class="span7">
						<div id="facilityIdEdit" style="width: 100%" class="green-label"></div>
						<div id="facilityId" style="width: 100%" class="green-label">
							<div id="jqxgridListFacility">
				            </div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.LOGThresholdsSale} </label>
					</div>
					<div class="span7">
						<div id="thresholdsSale" style="width: 100%"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.LOGThresholdsNearDate} </label>
					</div>
					<div class="span7">
						<div id="thresholdsDate" style="width: 100%"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.BLSafetyInventory} ${uiLabelMap.BLMin?lower_case}</label>
					</div>
					<div class="span7">
						<div id="thresholdsQuantity" style="width: 100%"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.BLSafetyInventory} ${uiLabelMap.BLMax?lower_case} </label>
					</div>
					<div class="span7">
						<div id="thresholdsQuantityMax" style="width: 100%"></div>
					</div>
				</div>
			</div>
	    </div>
	    <div class="form-action popup-footer">
            <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
            <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	    </div>
	</div>
</div>	

<div id='menuProductFacility' style="display:none;">
	<ul>
		<li><i class="fa fa-pencil-square-o"></i>${uiLabelMap.Edit}</li>
	    <li><i class="fa fa-trash red"></i>${uiLabelMap.Delete}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="jqxNotificationAddSuccess" >
	<div id="notificationAddSuccess"> 
	</div>
</div>
<script>

	$("#alterpopupWindow").jqxValidator({
	rules:[
		{
			input: '#productId', 
            message: "${StringUtil.wrapString(uiLabelMap.FieldRequired)}", 
            action: 'blur', 
            position: 'right',
            rule: function (input) {	
            	var tmps = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes');
            	if (tmps.length <=0 && checkUpdate == false){
            		return false;
            	}
            	return true;
            }
		},
		{
			input: '#facilityId', 
            message: "${StringUtil.wrapString(uiLabelMap.FieldRequired)}", 
            action: 'blur', 
            position: 'right',
            rule: function (input) {	
            	var tmps = $("#jqxgridListFacility").jqxGrid('getselectedrowindexes');
            	if (tmps.length <=0 && checkUpdate == false){
            		return false;
            	}
            	return true;
            }
		},
		{
			input: '#thresholdsSale', 
            message: "${StringUtil.wrapString(uiLabelMap.ThresholdsMustBeGreaterThanZero)}", 
            action: 'blur', 
            position: 'right',
            rule: function (input) {	
            	var thresholdsSale = $('#thresholdsSale').val();
            	if ((!thresholdsSale || thresholdsSale <=0)){
            		return false;
            	}
            	return true;
            }
		},
		{
			input: '#thresholdsDate', 
            message: "${StringUtil.wrapString(uiLabelMap.ThresholdsMustBeGreaterThanZero)}", 
            action: 'blur', 
            position: 'right',
            rule: function (input) {	
            	var thresholdsDate = $('#thresholdsDate').val();
            	if ((!thresholdsDate || thresholdsDate <=0)){
            		return false;
            	}
            	return true;
            }
		},
		{
			input: '#thresholdsQuantity', 
            message: "${StringUtil.wrapString(uiLabelMap.ThresholdsMustBeGreaterThanZero)}", 
            action: 'blur', 
            position: 'right',
            rule: function (input) {	
            	var thresholdsQty = $('#thresholdsQuantity').val();
            	if ((!thresholdsQty || thresholdsQty <=0)){
            		return false;
            	}
            	return true;
            }
		},
		{
			input: '#thresholdsQuantityMax', 
            message: "${StringUtil.wrapString(uiLabelMap.ThresholdsMustBeGreaterThanZero)}", 
            action: 'blur', 
            position: 'right',
            rule: function (input) {	
            	var thresholdsQty = $('#thresholdsQuantityMax').val();
            	if ((!thresholdsQty || thresholdsQty <=0)){
            		return false;
            	}
            	return true;
            }
		},
		],
	});

	$("#jqxNotificationAddSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddSuccess", opacity: 0.9, autoClose: true, template: "success" });
	var facilityData = [
		<#if facilitys?exists>
  			<#list facilitys as item>
  				{
  					facilityId: "${item.facilityId?if_exists}",
  					facilityName: "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}"
  				},
  			</#list>
  		</#if>	
	];
	
	
	var productData = [
  		<#if products?exists>
  			<#list products as item>
  				{
  					productId: "${item.productId?if_exists}",
  					quantityUomId: "${item.quantityUomId?if_exists}",
  					productCategoryId: "${item.productCategoryId?if_exists}",
  					categoryName: "${item.categoryName?if_exists}",
  					productCode: "${item.productCode?if_exists}",
  					productName: "${StringUtil.wrapString(item.get('productName', locale)?if_exists)}"
  				},
  			</#list>
  		</#if>
  	];
	
	$("#productId").jqxDropDownButton(); 
	$('#productId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">${uiLabelMap.PleaseSelectTitle}</div>');
	$("#productIdInput").jqxInput(); 
	$("#facilityId").jqxDropDownButton();
	$('#facilityId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">${uiLabelMap.PleaseSelectTitle}</div>');
	$("#facilityIdInput").jqxInput();
	$("#thresholdsSale").jqxNumberInput({ width: 200,  spinButtons: true, spinMode: 'simple',  min:0, decimalDigits: 0 });
	$("#thresholdsDate").jqxNumberInput({ width: 200,  spinButtons: true, spinMode: 'simple',  min:0, decimalDigits: 0 });
	$("#thresholdsQuantity").jqxNumberInput({ width: 200,  spinButtons: true, spinMode: 'simple',  min:0, decimalDigits: 0 });
	$("#thresholdsQuantityMax").jqxNumberInput({ width: 200,  spinButtons: true, spinMode: 'simple',  min:0, decimalDigits: 0 });
	
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 700, minWidth: 550, height:330 ,minHeight: 100, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:'olbius'           
	});
	 
	var checkUpdate = false;
	$("#menuProductFacility").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
	$("#menuProductFacility").on('itemclick', function (event) {
		requirementIdData = "";
        var args = event.args;
        var rowindex = $("#jqxgirdProductFacility").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgirdProductFacility").jqxGrid('getrowdata', rowindex);
        var productId = dataRecord.productId;
        var productCode = dataRecord.productCode;
        var facilityId = dataRecord.facilityId;
        var thresholdsSale = dataRecord.thresholdsSale;
        var thresholdsDate = dataRecord.thresholdsDate;
        var thresholdsQuantity = dataRecord.thresholdsQuantity;
        var thresholdsQuantityMax = dataRecord.thresholdsQuantityMax;
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.Delete)}") {
        	bootbox.dialog("${uiLabelMap.ConfirmDelete}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	deleteProductFacility(productId, facilityId);
		        }
	        }]);
        } else if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.Edit)}"){ 
        	checkUpdate = true;
        	$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.Edit)}');
        	$("#productIdEdit").show();
			$("#facilityIdEdit").show();
			$("#productIdEdit").html("");
			$("#facilityIdEdit").html("");
			$("#productIdEdit").text(productCode);
			$("#facilityIdEdit").text(facilityId);
        	$("#alterpopupWindow").jqxWindow('open'); 
        	$("#productIdInput").val(productId);
        	$("#facilityIdInput").val(facilityId);
        	editProductFacility(productId, facilityId, thresholdsSale, thresholdsDate, thresholdsQuantity, thresholdsQuantityMax)
        } else if($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
			$('#jqxgirdProductFacility').jqxGrid('updatebounddata');
 		}
    });
	
	var sourceProduct =
	{
	     datafields:[{name: 'productId', type: 'string'},
	                 {name: 'productCode', type: 'string'},	
	                 {name: 'productCategoryId', type: 'string'},
	                 {name: 'categoryName', type: 'string'},
	         		 {name: 'productName', type: 'string'},
	         		 {name: 'quantityUomId', type: 'string'},
	 				],
	     localdata: productData,
	     datatype: "array",
	};
	var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
	$("#jqxgridListProduct").jqxGrid({
	     source: dataAdapterProduct,
	     filterable: true,
	     showfilterrow: true,
	     theme: theme,
	     autoheight:true,
	     pageable: true, 
	     selectionmode: 'checkbox',
	     localization: getLocalization(),
	     columns: [{text: '${uiLabelMap.ProductId}', datafield: 'productCode', width: '20%', },
	       		   {text: '${uiLabelMap.ProductName}', datafield: 'productName', width: '50%',},
	       		   {text: '${uiLabelMap.Category}', datafield: 'categoryName', width: '30%',},
	       		   {text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', width: '15%', filtertype: 'checkedlist',
		       			cellsrenderer: function(row, column, value){
							for (var i = 0; i < quantityUomData.length; i ++){
								if (value && value == quantityUomData[i].uomId){
									return '<span>' + quantityUomData[i].description + '<span>';
								}
							}
							return '<span>' + value + '<span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(quantityUomData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId',
								renderer: function(index, label, value){
						        	if (quantityUomData.length > 0) {
										for(var i = 0; i < quantityUomData.length; i++){
											if(quantityUomData[i].uomId == value){
												return '<span>' + quantityUomData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
	       		   },
	     		]
	});
	 
	$("#jqxgridListProduct").on('rowselect', function (event) {
        var args = event.args;
        var rows = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes');
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rows.length +' ${uiLabelMap.Product}</div>';
        $('#productId').jqxDropDownButton('setContent', dropDownContent);
    });
	
	$("#jqxgridListProduct").on('rowunselect', function (event) {
        var args = event.args;
        var rows = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes');
        if (rows.length == 0){
        	$('#productId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">${uiLabelMap.PleaseSelectTitle}</div>');
        } else {
        	var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rows.length +' ${uiLabelMap.Facility}</div>';
            $('#productId').jqxDropDownButton('setContent', dropDownContent);
        }
    });
	
	var sourceFacility =
	{
	     datafields:[{name: 'facilityId', type: 'string'},
	         		{name: 'facilityName', type: 'string'},
	 				],
	     localdata: facilityData,
	     datatype: "array",
	}; 
	var dataAdapterFacility = new $.jqx.dataAdapter(sourceFacility);
	$("#jqxgridListFacility").jqxGrid({
	     source: dataAdapterFacility,
	     filterable: true,
	     showfilterrow: true,
	     theme: theme,
	     autoheight:true,
	     pageable: true, 
	     selectionmode: 'checkbox',
	     localization: getLocalization(),
	     columns: [{text: '${uiLabelMap.FacilityId}', datafield: 'facilityId', width: '35%', },
	       			{text: '${uiLabelMap.FacilityName}', datafield: 'facilityName', width: '65%', },
	     		] 
	});
	
	$("#jqxgridListFacility").on('rowselect', function (event) {
        var args = event.args;
        var rows = $("#jqxgridListFacility").jqxGrid('getselectedrowindexes');
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rows.length +' ${uiLabelMap.Facility}</div>';
        $('#facilityId').jqxDropDownButton('setContent', dropDownContent);
    });
	
	$("#jqxgridListFacility").on('rowunselect', function (event) {
        var args = event.args;
        var rows = $("#jqxgridListFacility").jqxGrid('getselectedrowindexes');
        if (rows.length == 0){
        	$('#facilityId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">${uiLabelMap.PleaseSelectTitle}</div>');
        } else {
        	var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rows.length +' ${uiLabelMap.Facility}</div>';
            $('#facilityId').jqxDropDownButton('setContent', dropDownContent);
        }
    });
	
	function addProductFacility(){
		checkUpdate = false;
		$('#alterpopupWindow').jqxWindow('open');
	}
	
	$("#addButtonSave").click(function () {
		if (checkUpdate){
			var productId = $("#productIdInput").val();
			var facilityId = $("#facilityIdInput").val();
			
			var thresholdsSale = $('#thresholdsSale').val();
			var thresholdsDate = $('#thresholdsDate').val();
			var thresholdsQuantity = $('#thresholdsQuantity').val();
			var thresholdsQuantityMax = $('#thresholdsQuantityMax').val();
			var validate = $('#alterpopupWindow').jqxValidator('validate');
			if(validate != false){
				editThresholdsProductFacility(productId, facilityId, thresholdsSale, thresholdsDate, thresholdsQuantity, thresholdsQuantityMax);
			}
		} else {
			var listProductIds = [];
			var listFacilityIds = [];
			var products = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes');
			for (var i = 0; i < products.length; i ++){
				var data = $("#jqxgridListProduct").jqxGrid('getrowdata', products[i]);
				var map = {};
				map["productId"] = data.productId;
				listProductIds.push(map);
			}
			var facilities = $("#jqxgridListFacility").jqxGrid('getselectedrowindexes');
			for (var i = 0; i < facilities.length; i ++){
				var data = $("#jqxgridListFacility").jqxGrid('getrowdata', facilities[i]);
				var map = {};
				map["facilityId"] = data.facilityId;
				listFacilityIds.push(map);
			}
			var thresholdsSale = $('#thresholdsSale').val();
			var thresholdsDate = $('#thresholdsDate').val();
			var thresholdsQuantity = $('#thresholdsQuantity').val();
			var thresholdsQuantityMax = $('#thresholdsQuantityMax').val();
			
			var validate = $('#alterpopupWindow').jqxValidator('validate');
			if(validate != false){
				if(checkUpdate == false){
					bootbox.dialog("${uiLabelMap.POAreYouSureAddItem}", 
							[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
								"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					            "callback": function() {bootbox.hideAll();}
					        }, 
					        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
					            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					            "callback": function() {
					            	addThresholdsProductFacility(listProductIds, listFacilityIds, thresholdsSale, thresholdsDate, thresholdsQuantity, thresholdsQuantityMax);
					        }
			        }]);
				}else{
					bootbox.dialog("${uiLabelMap.AreYouSureUpdate}", 
							[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
								"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					            "callback": function() {bootbox.hideAll();}
					        }, 
					        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
					            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					            "callback": function() {
					            	addThresholdsProductFacility(listProductIds, listFacilityIds, thresholdsSale, thresholdsDate,thresholdsQuantity, thresholdsQuantityMax);
					        }
			        }]);
				}
			}
		}
	});
	
	function addThresholdsProductFacility(listProductIds, listFacilityIds, thresholdsSale, thresholdsDate, thresholdsQuantity, thresholdsQuantityMax){
		var listProductIdsJson = JSON.stringify(listProductIds);
		var listFacilityIdsJson = JSON.stringify(listFacilityIds);
		$.ajax({
			url: "addThresholdsProductFacilitys",
			type: "POST",
			data: {listProductIds: listProductIdsJson, listFacilityIds: listFacilityIdsJson, thresholdsSale: thresholdsSale, thresholdsDate: thresholdsDate, thresholdsQuantity: thresholdsQuantity, thresholdsQuantityMax: thresholdsQuantityMax},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			var value = data["value"];
			$('#jqxgirdProductFacility').jqxGrid('updatebounddata');
	    	$('#alterpopupWindow').jqxWindow('close');
	    	if(value == "update"){
	    		$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
	    	}
	    	if(value == "create"){
	    		$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
	    	}
	    	$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.AddNew)}');
	    	checkUpdate = false;
		});
	}
	
	$('#alterpopupWindow').on('open', function (event) {
		if (checkUpdate == false){
			$("#productIdEdit").hide();
			$("#facilityIdEdit").hide();
			$("#productId").show();
			$("#facilityId").show();
		} else {
			$("#productIdEdit").show();
			$("#facilityIdEdit").show();
			$("#productId").hide();
			$("#facilityId").hide();
		}
	});
	
	$('#alterpopupWindow').on('close', function (event) { 
		$('#alterpopupWindow').jqxValidator('hide');
		$('#jqxgridListFacility').jqxGrid('clearselection');
		$('#productId').val("");
		$('#productIdInput').val("");
		$('#facilityId').val("");
		$('#facilityIdInput').val("");
		$('#thresholdsSale').val(0); 
		$('#thresholdsDate').val(0);
		$('#thresholdsQuantity').val(0);
		$('#thresholdsQuantityMax').val(0);
		$('#jqxgridListFacility').jqxGrid('clearselection');
		$('#jqxgridListProduct').jqxGrid('clearselection');
	}); 
	
	function deleteProductFacility(productId, facilityId){
		$.ajax({
			url: "deleteProductFacilityByLOG",
			type: "POST",
			data: {productId: productId, facilityId: facilityId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			$('#jqxgirdProductFacility').jqxGrid('updatebounddata');
			$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiDeleteSucess)}');
			$("#jqxNotificationAddSuccess").jqxNotification('open');
		});
	}
	
	function editProductFacility(productId, facilityId, thresholdsSale, thresholdsDate, thresholdsQuantity, thresholdsQuantityMax){
		$('#productId').val(mapProductData[productId]);  
		$('#facilityId').val(mapFacilityData[facilityId]);
		$('#thresholdsSale').val(thresholdsSale);
		$('#thresholdsDate').val(thresholdsDate);
		$('#thresholdsQuantity').val(thresholdsQuantity);
		$('#thresholdsQuantityMax').val(thresholdsQuantityMax);
	}
	
	function editThresholdsProductFacility(productId, facilityId, thresholdsSale, thresholdsDate, thresholdsQuantity, thresholdsQuantityMax){
		bootbox.dialog("${uiLabelMap.AreYouSureUpdate}", 
			[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {bootbox.hideAll();}
	        }, 
	        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	$.ajax({
	        			url: "editThresholdsProductFacility",
	        			type: "POST",
	        			data: {productId: productId, facilityId: facilityId, thresholdsSale: thresholdsSale, thresholdsDate: thresholdsDate, thresholdsQuantity: thresholdsQuantity, thresholdsQuantityMax: thresholdsQuantityMax},
	        			dataType: "json",
	        			success: function(data) {
	        			}
	        		}).done(function(data) { 
	        			$('#jqxgirdProductFacility').jqxGrid('updatebounddata');
	        			$('#alterpopupWindow').jqxWindow('close');
	        			$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiUpdateSucess)}');
	        			$("#jqxNotificationAddSuccess").jqxNotification('open');
	        		});
	        }
        }]);
	}
</script>
