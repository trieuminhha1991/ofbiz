<#if security.hasEntityPermission("IMPORT", "_ADMIN", session)>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.pager.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.sort.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.selection.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.edit2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.filter2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxprogressbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.aggregates.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.export.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.export.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsresize.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.grouping.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>

<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/delys/images/js/import/notify.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>

<div id="container"></div>
<div class="row-fluid">
	<div class="span12">
		<div class="span3">
			<div class="span4 no-left-margin" style="text-align: right;">
				<label>${uiLabelMap.Category}</label>
			</div>
			<div class="span7">
				<div id="category"></div>
			</div>
		</div>
		<div class="span5">
			<div class="span5" style="text-align: right;">
				<label>${uiLabelMap.Product}<span style="color:red;"> *</span></label>
			</div>
			<div class="span7">
				<div id="product"></div>
			</div>
		</div>
		<div class="span4" id="divCapacity" style="visibility: hidden;">
			<div class="span5"><label>${uiLabelMap.ProductPackedQty}: </label></div>
			<div class="span7 no-left-margin" style="margin-top: 4px;font-size: 14px;padding-left: 5px;"><span style="color:#037c07;" id="productCapacity" ></span><i class="green icon-edit" style="margin-left: 5px;cursor: pointer;" onclick="EditProductCapacity()"></i></div>
		</div>
	</div>
</div>
<div class="row-fluid" style="margin-top: 17px;">
		<div class="span12">
			<div id="jqxGidPlanViewer"></div>
		</div>
</div>
<div class="row-fluid">
	<div class="span12" style="margin-top:12px;visibility: hidden;" id="divButton">
		<div class="span3"></div>
		<div class="span7" style="float: right;">
			<button id='alterCancelPlan' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
			<button id='alterSavePlan' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="EditConfigCapacity" style="display:none;">
	<div>${uiLabelMap.ConfigCapacity}</div>
	<div style="overflow-x: hidden;">
		<div><span style="color:red;" id="statusCapacity"></span></div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;width:99%;">
		<div class="row-fluid">
			<div class="span12" style="margin-top: 8px;">
	 			<div class="span5" style="text-align: right;">${uiLabelMap.accProductId}&nbsp;&nbsp;&nbsp;</div>
	 			<div class="span7"><span style="color:#037c07;" id="productIdCapacity"></span></div>
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="span12" style="margin-top: 8px;">
	 			<div class="span5" style="text-align: right;"><label style="margin-top: 5px;">${uiLabelMap.StorageCapacity}<span style="color:red;"> *</span></label></div>
	 			<div class="span7"><div id='txtStorageCapacity'></div></div>
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="span12" style="margin-top: 8px;">
	 			<div class="span5" style="text-align: right;"><label style="margin-top: 5px;">${uiLabelMap.AvailableFromDate}<span style="color:red;"> *</span></label></div>
	 			<div class="span7"><div id='txtFromDateCapacity'></div></div>
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="span12" style="margin-top: 8px;">
	 			<div class="span5" style="text-align: right;"><label style="margin-top: 5px;">${uiLabelMap.AvailableThruDate}&nbsp;&nbsp;&nbsp;</label></div>
	 			<div class="span7"><div id='txtThruDateCapacity'></div></div>
			</div>
		</div>
				
		<div class="row-fluid">
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;width:99%;">
 			<div class="span12 margin-top10 no-left-margin">
 				<button id='alterCancelEditConfigCapacity'  class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
	            <button id='alterSaveEditConfigCapacity' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
	        </div>
	    </div>
	</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<div id="jqxwindowConfirmClose" style="display:none;">
	<div>${uiLabelMap.ConfirmDeleteLocation}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
			<div class="span12" style="font-size: 14px;">
				${StringUtil.wrapString(uiLabelMap.ConfirmCloseEditConfigCapacity)}
			</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
		<div class="row-fluid">
			<div class="span12 margin-top10 no-left-margin">
				<button id='cancelConfirmClose'  class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button id='saveConfirmClose' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonAgree}</button>
			</div>
		</div>
	</div>
</div>


<div id='Menu' style="display:none;">
	<ul>
	    <li><i class='icon-download-alt'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.DownloadPlan)}</li>
	</ul>
</div>


<div id="alterpopupWindow" style="display:none;">
<div>${uiLabelMap.AddNewProductSupplier}</div>
<div style="overflow-y: hidden;">
		<div id="notifyStatus"></div>
		<div class="row-fluid">
 			<div class="span12 margin-bottom10">
	 			<div class="span3" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.ProductSupplier}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="ProductSupplier1"></div></div>
	 			<div class="span4" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.FormFieldTitle_productId}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="ProductId1"></div></div>
 			</div>
		</div>
		<div class="row-fluid">
 			<div class="span12">
	 			<div class="span3" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_supplierProductId}<span style="color:red;"> *</span></label></div>
		 		<div class="span2"><input type='text' id="supplierProductId1"></input></div>
		 		<div class="span4" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_supplierProductName}<span style="color:red;"> *</span></label></div>
    	 		<div class="span2"><input type='text' id="supplierProductName1"></input></div>
 			</div>
		</div>
		<div class="row-fluid">
 			<div class="span12 margin-bottom10">
    	 		<div class="span3" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_shippingPrice}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="shippingPrice1"></div></div>
	 			<div class="span4" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_lastPrice}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="lastPrice1"></div></div>
 			</div>
		</div>
		<div class="row-fluid">
 			<div class="span12 margin-bottom10">
	 			<div class="span3" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.ProductCurrencyUomId}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="ProductCurrencyUomId1"></div></div>
    	 		<div class="span4" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.ProductPackingUom}<span style="color:red;"> *</span></label></div>
		 		<div class="span2"><div id="quantityUomId1"></div></div>
 			</div>
		</div>
		<div class="row-fluid">
 			<div class="span12">
	 			<div class="span3" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_minimumOrderQuantity}&nbsp;&nbsp;&nbsp;</label></div>
		 		<div class="span2"><div id="minimumOrderQuantity1"></div></div>
		 		<div class="span4" style="text-align: right;"><label>${uiLabelMap.accComments}&nbsp;&nbsp;&nbsp;</label></div>
		 		<div class="span2"><input type='text' id="comments1" /></div>
	 		</div>
 		</div>
 		<div class="row-fluid">
 			<div class="span12">
	 			<div class="span3" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.FormFieldTitle_availableFromDate}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="availableFromDate1"></div></div>
	 			<div class="span4" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.FormFieldTitle_availableThruDate}&nbsp;&nbsp;&nbsp;</label></div>
		 		<div class="span2"><div id="AvailableThruDate1"></div></div>
	 		</div>
 		</div>
 		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
 		<div class="row-fluid">
            <div class="span12 margin-top10">
            	<div class="span12">
            		<button id='alterCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.btnCancel}</button>
            		<button id='alterSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
        		</div>
            </div>
    	</div>
   </div>
</div>


<div id="jqxNotificationStatus">
	<div id="notificationContentStatus">
	</div>
</div>


<#assign listProductCategory = delegator.findList("ProductCategory", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"), null, Static["org.ofbiz.base.util.UtilMisc"].toList("categoryName"), null, false) />
<#assign listProduct = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "FINISHED_GOOD"), null, Static["org.ofbiz.base.util.UtilMisc"].toList("internalName"), null, false) />
<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
<#assign listCurrencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)>
<#assign listPartyRole = delegator.findList("PartyRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "SUPPLIER"), null, null, null, false)>
<#assign listPartyGroup = delegator.findList("PartyGroup", null, null, null, null, false) />
<script type="text/javascript">
	var mapPartyGroup = {
	        <#if listPartyGroup?exists>
	    		<#list listPartyGroup as item>
	    			"${item.partyId?if_exists}": "${StringUtil.wrapString(item.groupName?if_exists)}",
	    		</#list>
	       </#if>
			};
	var listParty = [
		<#if listPartyRole?exists>
			<#list listPartyRole as item>
			{
				partyId: '${item.partyId?if_exists}',
				groupName: mapPartyGroup['${item.partyId?if_exists}']
			},
			</#list>
		</#if>
	       ];
	var listCurrencyUoms = [
							<#if listCurrencyUoms?exists>
								<#list listCurrencyUoms as item>
								{
									uomId: '${item.uomId?if_exists}',
									description: "${StringUtil.wrapString(item.description)}"
								},
								</#list>
							</#if>
	     	                    ];
	var locale = '${locale}';
	$(document).ready(function(){
		locale=="vi_VN"?locale="vi":locale="en";
		if (!productPlanIdHeaderGlobal) {
			window.location.href = "getImportPlans";
		}
		getMapProductsWithCategory();
		getMapHasSupplierProduct();
		$('#txtThruDateCapacity').val(null);
	});
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	$("#jqxwindowConfirmClose").jqxWindow({theme: 'olbius',
	    width: 300, maxWidth: 1845, minHeight: 25, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancelConfirmClose"), modalOpacity: 0.7
	});
	$("#saveConfirmClose").click(function () {
		$("#EditConfigCapacity").jqxWindow('close');
		$("#jqxwindowConfirmClose").jqxWindow('close');
		$("#jqxGidPlanViewer").css("visibility", "hidden");
		$("#divCapacity").css("visibility", "hidden");
		$("#divButton").css("visibility", "hidden");
		$("#product").jqxDropDownList('clearSelection');
	});
	
	$("#alterSavePlan").click(function () {
		var rows = $('#jqxGidPlanViewer').jqxGrid('getboundrows');
		if (rows.length == 0) {
			return;
		}
		saveProductPlanItemEventAjax(rows);
	});
	$("#alterCancelPlan").click(function () {
		if ($('#product').val()) {
			loadPlanOfProduct($('#product').val());
		}
	});
	function saveProductPlanItemEventAjax(data) {
		$('#jqxGidPlanViewer').jqxGrid('showloadelement');
		var result;
		$.ajax({
			url: "saveProductPlanItemEventAjax",
			type: "POST",
			data: {dataProductPlan: JSON.stringify(data), quantityUomId: uomToId, productId: productIdGlobal },
			success: function(res) {
				result = res["RESULT_MESSAGE"];
			}
		}).done(function() {
			$('#jqxNotificationNested').jqxNotification('closeLast');
			if (result == "SUSSESS") {
				if ($('#product').val()) {
					loadPlanOfProduct($('#product').val());
				}
	  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
	          	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	          	$("#jqxNotificationNested").jqxNotification("open");
			} else {
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
				$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
	          	$("#jqxNotificationNested").jqxNotification("open");
			}
			$('#jqxGidPlanViewer').jqxGrid('hideloadelement');
		});
	}
	$("#EditConfigCapacity").jqxWindow({
	    width: 450,
	    theme: "olbius",
	    height: 290,
	    resizable: false,
	    isModal: true,
	    autoOpen: false,
	    modalOpacity: 0.7
	});
	$('#EditConfigCapacity').on('open', function (event) {
		
	});
	$('#EditConfigCapacity').on('close', function (event) {
		$('#EditConfigCapacity').jqxValidator('hide');
		$('#txtThruDateCapacity').val(null);
	});
	$("#txtStorageCapacity").jqxNumberInput({ inputMode: 'simple', spinButtons: true, theme: "olbius", width: '200px', decimalDigits: 0, min: 0  });
	$("#txtFromDateCapacity").jqxDateTimeInput({theme: "olbius", width: '200px' });
	$("#txtThruDateCapacity").jqxDateTimeInput({theme: "olbius", width: '200px' });
	$("#alterSaveEditConfigCapacity").click(function () {
	    if ($('#EditConfigCapacity').jqxValidator('validate')) {
	    	var data = {};
	    	data.productId = $("#product").val();
			data.uomFromId = "PALLET";
			data.quantityConvert = $("#txtStorageCapacity").val();
			data.fromDate = $("#txtFromDateCapacity").val().toTimeStamp();
			data.thruDate = $("#txtThruDateCapacity").val().toTimeStamp();
	    	insertConfigPackingAjax(data);
	    	$("#EditConfigCapacity").jqxWindow('close');
	    }
	});
	$("#alterCancelEditConfigCapacity").click(function () {
		if (quantityConvertInvalid) {
			$("#jqxwindowConfirmClose").jqxWindow('open');
		} else {
			$('#EditConfigCapacity').jqxWindow('close');
		}
	});
	$('#EditConfigCapacity').jqxValidator({
	    rules: [
	            { input: '#txtStorageCapacity', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
	            	rule: function (input, commit) {
	            		var value = $("#txtStorageCapacity").val();
	            		if (value > 0) {
	            			return true;
						}
	            		return false;
	            	}
	            },
	            { input: '#txtFromDateCapacity', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
	            	rule: function (input, commit) {
	            		var value = $("#txtFromDateCapacity").val();
	            		if (value) {
	            			return true;
						}
	            		return false;
	            	}
	            },
	            { input: '#txtThruDateCapacity', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
	            	rule: function (input, commit) {
	            		var thruDate = $("#txtThruDateCapacity").val().toMilliseconds();
	            		if (!thruDate) {
	            			return true;
						}
	            		var fromDate = $("#txtFromDateCapacity").val().toMilliseconds();
	            		if (fromDate <= thruDate) {
	            			return true;
						}
	            		return false;
	            	}
	            }
	           ]
	});
	function insertConfigPackingAjax(data) {
		var saveSuccess = true;
		jQuery.ajax({
	        url: "updateProductCapacityAjax",
	        type: "POST",
	        data: data,
	        success : function(res) {
	        	res["_EVENT_MESSAGE_"] == undefined?saveSuccess=true:saveSuccess=false;
			}
	    }).done(function() {
	    	$('#jqxNotificationNested').jqxNotification('closeLast');
	  		if (saveSuccess) {
	  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
	          	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	          	$("#jqxNotificationNested").jqxNotification("open");
			} else {
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
				$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
	          	$("#jqxNotificationNested").jqxNotification("open");
			}
	  		loadPlanOfProduct(data.productId);
		});
	}
	function getConfigPackingAjax(jsonObject) {
		var configPackingInfo;
		jQuery.ajax({
	        url: "getConfigPackingAjax",
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success : function(res) {
	        	configPackingInfo = res["configPackingInfo"];
			}
	    });
		return configPackingInfo;
	}
	var listProductCategory = [
								<#if listProductCategory?exists>
									<#list listProductCategory as item>
									{
										productCategoryId: "${item.productCategoryId?if_exists}",
										categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}"
									},
									</#list>
								</#if>
	                          ];
	var mapProductCategory = {
							<#if listProductCategory?exists>
								<#list listProductCategory as item>
									"${item.productCategoryId?if_exists}": "${StringUtil.wrapString(item.categoryName?if_exists)}",
								</#list>
							</#if>
							};
	
	var listProduct = [
					<#if listProduct?exists>
						<#list listProduct as item>
						{
							productId: "${item.productId?if_exists}",
							internalName: "${StringUtil.wrapString(item.internalName?if_exists)}"
						},
						</#list>
					</#if>
                 ];
	var mapProduct = {
					<#if listProduct?exists>
						<#list listProduct as item>
							"${item.productId?if_exists}": "${StringUtil.wrapString(item.internalName?if_exists)}",
						</#list>
					</#if>
					};
	
	var mapStatus = {
			SalesForecastNotAvalible: "${StringUtil.wrapString(uiLabelMap.SalesForcastNotAvailable)}",
			HasInventoryReality: "${StringUtil.wrapString(uiLabelMap.StoredAndHaveInventory)}",
			Stored: "${StringUtil.wrapString(uiLabelMap.Stored)}",
			Recommend: "${StringUtil.wrapString(uiLabelMap.Recommend)}",
			HaveChanged: "${StringUtil.wrapString(uiLabelMap.HaveChanged)}",
			Imported: "${StringUtil.wrapString(uiLabelMap.Imported)}",
			NotImport: "${StringUtil.wrapString(uiLabelMap.NotImport)}",
			"": ""
	};
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
	$("#category").jqxDropDownList({ source: listProductCategory, displayMember: 'categoryName', valueMember: 'productCategoryId', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#product").jqxDropDownList({ source: listProduct, filterable: true, displayMember: 'internalName', valueMember: 'productId', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	
	$('#category').on('change', function (event){
	    var args = event.args;
	    if (args) {
		    var index = args.index;
		    var item = args.item;
		    var label = item.label;
		    var value = item.value;
		    if (changed) {
		    	if (hasChangeModeConfirm()) {
			    	$("#product").jqxDropDownList('clearSelection');
				    $('#jqxGidPlanViewer').jqxGrid('clear');
				    $("#product").jqxDropDownList({ source: mapProductsWithCategory[value] });
				    if (!mapProductsWithCategory[value]) {
				    	$("#product").notify(label + " ${StringUtil.wrapString(uiLabelMap.NotHaveProductInCategory)}.", "error");
					}
			    }
			} else {
				$("#product").jqxDropDownList('clearSelection');
			    $('#jqxGidPlanViewer').jqxGrid('clear');
			    $("#product").jqxDropDownList({ source: mapProductsWithCategory[value] });
			    if (!mapProductsWithCategory[value]) {
			    	$("#product").notify(label + " ${StringUtil.wrapString(uiLabelMap.NotHaveProductInCategory)}.", "error");
				}
			}
		    changed = false;
		    $("#divCapacity").css("visibility", "hidden");
			$("#divButton").css("visibility", "hidden");
	    }
	});
	$('#product').on('change', function (event){
		var args = event.args;
		if (args) {
			var index = args.index;
			var item = args.item;
			var label = item.label;
			var value = item.value;
			if (changed) {
				if (hasChangeModeConfirm()) {
					setTimeout(function () {
						loadPlanOfProduct(value);
					}, 300 );
				}
			}else {
				if (mapHasSupplierProduct[value]) {
					loadPlanOfProduct(value);
				} else {
					$("#ProductId1").jqxDropDownList('val', value);
					$("#ProductId1").jqxDropDownList({ disabled: true });
					$("#alterpopupWindow").jqxWindow('open');
				}
			}
		}
	});
	
	var productPlanIdHeaderGlobal = "${productPlanId?if_exists}";
	
	var mapProductsWithCategory;
	function getMapProductsWithCategory() {
		$.ajax({
			  url: "getMapProductsWithCategoryAjax",
			  type: "POST",
			  data: {},
			  success: function(res) {
				  mapProductsWithCategory = res["mapProductsWithCategory"];
			  }
		  	}).done(function() {
		  		
		  	});
	}
	var quantityConvert;
	var uomToId;
	var quantityConvertInvalid = false;
	var productIdGlobal = "";
	function loadPlanOfProduct(productId) {
		$('#jqxGidPlanViewer').jqxGrid('showloadelement');
		normalMode();
		quantityConvertInvalid = false;
		var planOfProduct;
		$.ajax({
			url: "loadPlanOfProductAjax",
			type: "POST",
			data: { productId: productId, productPlanId: productPlanIdHeaderGlobal },
			success: function(res) {
				planOfProduct = res["planOfProduct"];
				quantityConvert = parseInt(res["quantityConvert"]);
				uomToId = res["uomToId"];
			}
		}).done(function() {
			$('#jqxGidPlanViewer').jqxGrid('hideloadelement');
			if (quantityConvert == 0) {
				quantityConvertInvalid = true;
				$("#statusCapacity").text("${StringUtil.wrapString(uiLabelMap.quantityConvertInvalid)}");
				$("#productIdCapacity").text(productId);
				$('#txtStorageCapacity').jqxNumberInput('val', 0);
				$("#EditConfigCapacity").jqxWindow('open');
				return;
			}
			$("#productCapacity").text(quantityConvert + " [" + mapQuantityUom[uomToId] + "]");
			bindDataPlan(planOfProduct);
			productIdGlobal = productId;
			changed = false;
		});
	}
	function EditProductCapacity() {
		var oldCapacity = $("#productCapacity").text();
		var data = {};
		data.uomToId = uomToId;
		data.productId = productIdGlobal;
		var oldCapacityInfo = getConfigPackingAjax(data);
		if (oldCapacityInfo.fromDate) {
			oldCapacityInfo.fromDate = oldCapacityInfo.fromDate['time'];
		}
		if (oldCapacityInfo.thruDate) {
			oldCapacityInfo.thruDate = oldCapacityInfo.thruDate['time'];
		}
		$("#statusCapacity").text("");
		$("#productIdCapacity").text(oldCapacityInfo.productId);
		$('#txtStorageCapacity').jqxNumberInput('val', oldCapacityInfo.quantityConvert);
		$('#txtFromDateCapacity').jqxDateTimeInput('val', new Date(oldCapacityInfo.fromDate));
		var thruDate = oldCapacityInfo.thruDate;
		thruDate?thruDate = new Date(thruDate): thruDate = thruDate;
		$('#txtThruDateCapacity').jqxDateTimeInput('val', thruDate);
		$("#EditConfigCapacity").jqxWindow('open');
	}
	$("#productCapacity").on("click", function() {
		
	});
	function bindDataPlan(planOfProduct) {
		var source =
	    {
	        localdata: planOfProduct,
	        datatype: "local",
	        datafields:
	        [
	            { name: 'productPlanName', type: 'string' },
	            { name: 'statusId', type: 'string' },
	            { name: 'salesForecastQuantity', type: 'number' },
	            { name: 'importQuantityRecommend', type: 'number' },
	            { name: 'palletQuantity', type: 'number' },
	            { name: 'inventoryForecast', type: 'number' },
	            { name: 'inventoryReality', type: 'number' },
	            { name: 'salesInventoryFocastDays', type: 'number' },
	            { name: 'lastInventoryForecast', type: 'number' },
	            { name: 'status', type: 'string' },
	            { name: 'productPlanId', type: 'string' },
	            { name: 'productPlanItemSeqId', type: 'string' }
	        ],
	        addrow: function (rowid, rowdata, position, commit) {
	        	
	            commit(true);
	        },
	        deleterow: function (rowid, commit) {
	            
	            commit(true);
	        },
	        updaterow: function (rowid, newdata, commit) {
	        	
	            commit(true);
	        }
	    };
	    var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxGidPlanViewer").jqxGrid({
			source: dataAdapter,
			width: '100%',
			autoheight: true,
			theme: 'olbius',
			sortable: false,
			editable: true,
			editmode: 'selectedrow',
			altrows: true,
			rowsheight: 30,
			localization: {emptydatastring: '${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}'},
			selectionmode: 'singlerow',
			columns: [
			  { text: '${StringUtil.wrapString(uiLabelMap.Time)}', dataField: 'productPlanName', align: 'center', width: 90, editable: false,
				  cellsrenderer: function(row, colum, value){
					  return '<span>${StringUtil.wrapString(uiLabelMap.Month)} ' + value + '</span>';
				  }
			  },
			  { text: '${StringUtil.wrapString(uiLabelMap.InventoriesInMonth)}', dataField: 'inventoryReality', align: 'center', cellsalign: 'right', width: 130, editable: false,
				  cellsrenderer: function(row, colum, value){
					  if (isFirefox) {
						  if (value > 0) {
							  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '&nbsp;&nbsp;&nbsp;&nbsp;<div style=\"float: right; margin-top: -26px;color: green;padding-left: 8px;font-size: 15px;margin-right: -10px;  font-family:Arial Rounded MT Bold,Ubuntu,sans-serif,Arial Unicode MS,Zapf Dingbats,Segoe UI Emoji,Segoe UI Symbol,NotoColorEmoji,EmojiSymbols,Symbola,Noto,Android Emoji,AndroidEmoji,lucida grande,tahoma,verdana,arial,AppleColorEmoji,Apple Color Emoji!important\">☑</div>' + '</span>';
						  }
						  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '&nbsp;&nbsp;&nbsp;&nbsp;<div style=\"float: right; margin-top: -26px;color: green;padding-left: 8px;font-size: 15px;margin-right: -10px;\">☐</div>' + '</span>';
						} else {
							  if (value > 0) {
								  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '<div style=\"float: right; margin-top: -8px;color: green;padding-left: 8px;font-size: 15px;margin-right: -9px;  font-family:Arial Rounded MT Bold,Ubuntu,sans-serif,Arial Unicode MS,Zapf Dingbats,Segoe UI Emoji,Segoe UI Symbol,NotoColorEmoji,EmojiSymbols,Symbola,Noto,Android Emoji,AndroidEmoji,lucida grande,tahoma,verdana,arial,AppleColorEmoji,Apple Color Emoji!important\">☑</div>' + '</span>';
							  }
							  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '<div style=\"float: right; margin-top: -11px;color: green;padding-left: 8px;font-size: 15px;margin-right: -9px;\">☐</div>' + '</span>';
						}
	        	  }
			  },
			  { text: '${StringUtil.wrapString(uiLabelMap.SalesForecast)}', dataField: 'salesForecastQuantity', align: 'center', cellsalign: 'right', width: 140, editable: false,
				  cellsrenderer: function(row, colum, value){
	        		  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
	        	  }
			  },
			  { text: '${StringUtil.wrapString(uiLabelMap.ImportVolume)}', dataField: 'importQuantityRecommend', align: 'center', cellsalign: 'right', width: 150, editable: false,
				  cellsrenderer: function(row, colum, value){
	        		  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
	        	  }
			  },
			  { text: '${StringUtil.wrapString(uiLabelMap.Pallet)}', dataField: 'palletQuantity', align: 'center', width: 100, columntype: 'numberinput', cellsalign: 'right',
				  validation: function (cell, value) {
		        		if (value == null){
		        			return true;
		        		}
		        		if (value < 0) {
		        			return { result: false, message: '${StringUtil.wrapString(uiLabelMap.QuantityNotValid)}' };
		        		}
		        		return true;
	               }, cellsrenderer: function(row, colum, value){
	         		  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
	               },cellbeginedit: function (row, datafield, columntype, value) {
	            	   	var data = $("#jqxGidPlanViewer").jqxGrid('getrowdata', row);
      		    	    var statusId = data.statusId;
      		    	    if (statusId=="PLAN_COMPLETED" || statusId=="PLAN_ORDERED" ||statusId=="PLAN_PROCESSING") {
      		    	    	return false;
						} else {
							return true;
						}
		            }
			  },
			  { text: '${StringUtil.wrapString(uiLabelMap.ForecastSurvive)}', dataField: 'inventoryForecast', align: 'center', cellsalign: 'right', width: 120, editable: false,
				  cellsrenderer: function(row, colum, value){
					  var importQuantityRecommend = $('#jqxGidPlanViewer').jqxGrid('getcellvalue', row, "importQuantityRecommend");
					  if (importQuantityRecommend > 0 && value == 0) {
						  return '<span style=\"text-align: right;\">_N/A_</span>';
					  }
					  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
	        	  }
			  },
			  { text: '${StringUtil.wrapString(uiLabelMap.InventoriesOnDays)}', dataField: 'salesInventoryFocastDays', align: 'center', cellsalign: 'right', width: 180, editable: false,
				  cellsrenderer: function(row, colum, value){
					  if (value != 0) {
						  var strValue = value.toString();
						  var halfLeft = strValue.split(".")[1];
						  var halfRight = strValue.split(".")[0];
						  if (halfLeft) {
								if (halfRight > 0) {
									value = "${StringUtil.wrapString(uiLabelMap.Cross)} " + halfRight + " ${StringUtil.wrapString(uiLabelMap.Days)} ";
								} else {
									value = "${StringUtil.wrapString(uiLabelMap.Less)} " + halfRight + " ${StringUtil.wrapString(uiLabelMap.Days)} ";
								}
						  }else {
							  value = value + " ${StringUtil.wrapString(uiLabelMap.Days)} ";
						  }
					  }else {
						  var importQuantityRecommend = $('#jqxGidPlanViewer').jqxGrid('getcellvalue', row, "importQuantityRecommend");
						  if (importQuantityRecommend > 0 && value == 0) {
							  return '<span style=\"text-align: right;\">_N/A_</span>';
						  }
					  }
	        		  return '<span style=\"text-align: right;\">' + value + '</span>';
	        	  }  
			  },
			  { text: '${StringUtil.wrapString(uiLabelMap.Note)}', dataField: 'status', align: 'center', editable: false,
				  cellsrenderer: function(row, colum, value){
					  return '<span>' + mapStatus[value] + '</span>';
				  }  
			  }
		  ]
		});
		$("#jqxGidPlanViewer").css("visibility", "visible");
		$("#divCapacity").css("visibility", "visible");
		$("#divButton").css("visibility", "visible");
	}
	
	var contextMenu = $("#Menu").jqxMenu({ width: 170, height: 30, autoOpenPopup: false, mode: 'popup', theme: 'olbius' });
	$("#jqxGidPlanViewer").on('contextmenu', function () {
	    return false;
	});
	$("#jqxGidPlanViewer").on('rowclick', function (event) {
	    if (event.args.rightclick) {
	        $("#jqxGidPlanViewer").jqxGrid('selectrow', event.args.rowindex);
	        var scrollTop = $(window).scrollTop();
	        var scrollLeft = $(window).scrollLeft();
	        contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	        return false;
	    }
	});
	$("#Menu").on('itemclick', function (event) {
	    var args = event.args;
	    if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.DownloadPlan)}") {
	    	$("#jqxGidPlanViewer").jqxGrid('exportdata', 'xls', 'KeHoachNhapKhauSanPham_' + productIdGlobal);
	    }
	    if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.ViewDetails)}") {
	    	alert();
	    }
	});
	var changed = false;
	$("#jqxGidPlanViewer").on('cellendedit', function (event){
		    var args = event.args;
		    var dataField = event.args.datafield;
		    var rowBoundIndex = event.args.rowindex;
		    var value = args.value;
		    var oldvalue = args.oldvalue;
		    var rowData = args.row;
		    if (dataField == "palletQuantity") {
		    	var importQuantityRecommend = value * quantityConvert;
		    	var salesForecastQuantity = rowData.salesForecastQuantity;
		    	var salesInventoryFocastDays = 0;
		    	var inventoryForecast = 0;
		    	if (salesForecastQuantity == 0) {
		    		inventoryForecast = "_N/A_";
		    		salesInventoryFocastDays = "_N/A_";
				} else {
					var inventoryReality = rowData.inventoryReality;
			    	var lastInventoryForecast = 0;
			    	if (rowBoundIndex > 0) {
			    		lastInventoryForecast = $('#jqxGidPlanViewer').jqxGrid('getcellvalue', rowBoundIndex - 1, "inventoryForecast");
					}
			    	inventoryForecast = importQuantityRecommend - salesForecastQuantity + inventoryReality + lastInventoryForecast;
			    	salesInventoryFocastDays = inventoryForecast/(salesForecastQuantity/30);
				}
		    	$("#jqxGidPlanViewer").jqxGrid('setcellvalue', rowBoundIndex, "importQuantityRecommend", importQuantityRecommend);
		    	$("#jqxGidPlanViewer").jqxGrid('setcellvalue', rowBoundIndex, "inventoryForecast", inventoryForecast);
		    	$("#jqxGidPlanViewer").jqxGrid('setcellvalue', rowBoundIndex, "salesInventoryFocastDays", salesInventoryFocastDays);
		    	if (oldvalue !== value) {
		    		$("#jqxGidPlanViewer").jqxGrid('setcellvalue', rowBoundIndex, "status", "HaveChanged");
		    		changed = true;
		    		$(window).bind('beforeunload', function(e) {
		    		    if (confirm) {
		    		        return "${StringUtil.wrapString(uiLabelMap.PlanHasChangedConfirmWhenLeave)}";
		    		    }
		    		});
				}
		    	updateRow(rowBoundIndex);
			}
	});
	function hasChangeModeConfirm() {
		var cf = confirm("${StringUtil.wrapString(uiLabelMap.PlanHasChangedConfirm)}");
	    if (cf == true) {
	    	$("#alterSavePlan").click();
	    	normalMode();
	    } else {
	    }
	    return true;
	}
	function normalMode() {
		$(window).off('beforeunload');
	}
	function updateRow(rowBoundIndex) {
		for (var i = rowBoundIndex + 1; i < 12; i++) {
			var data = $('#jqxGidPlanViewer').jqxGrid('getrowdata', i);
			var palletQuantity = data.palletQuantity;
			if (palletQuantity == 0) {
				continue;
			}
			var importQuantityRecommend = palletQuantity * quantityConvert;
	    	var salesForecastQuantity = data.salesForecastQuantity;
	    	var inventoryForecast = 0;
	    	var salesInventoryFocastDays = 0;
	    	
	    	if (salesForecastQuantity == 0) {
	    		inventoryForecast = "_N/A_";
	    		salesInventoryFocastDays = "_N/A_";
			} else {
				var inventoryReality = data.inventoryReality;
		    	var inventoryForecastOriginal = data.inventoryForecast;
		    	var lastInventoryForecast = 0;
		    	if (i > 0) {
		    		lastInventoryForecast = $('#jqxGidPlanViewer').jqxGrid('getcellvalue', i - 1, "inventoryForecast");
				}
		    	inventoryForecast = importQuantityRecommend - salesForecastQuantity + inventoryReality + lastInventoryForecast;
		    	salesInventoryFocastDays = inventoryForecast/(salesForecastQuantity/30);
		    	if (inventoryForecastOriginal !== inventoryForecast) {
		    		$("#jqxGidPlanViewer").jqxGrid('setcellvalue', i, "status", "HaveChanged");
				}
			}
	    	$("#jqxGidPlanViewer").jqxGrid('setcellvalue', i, "inventoryForecast", inventoryForecast);
	    	$("#jqxGidPlanViewer").jqxGrid('setcellvalue', i, "salesInventoryFocastDays", salesInventoryFocastDays);
		}
	}
	var isOpera = !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;
	var isFirefox = typeof InstallTrigger !== 'undefined';
	var isSafari = Object.prototype.toString.call(window.HTMLElement).indexOf('Constructor') > 0;
	var isChrome = !!window.chrome && !isOpera;
	var isIE = false || !!document.documentMode;
	
	var mapHasSupplierProduct;
	function getMapHasSupplierProduct() {
		$.ajax({
			  url: "getMapHasSupplierProductAjax",
			  type: "POST",
			  data: {},
			  success: function(res) {
				  mapHasSupplierProduct = res["mapHasSupplierProduct"];
			  }
		  	}).done(function() {
		  		
		  	});
	}
	
	$("#jqxNotificationStatus").jqxNotification({ width: "100%", appendContainer: "#notifyStatus", opacity: 0.9, autoClose: true, template: "info" });
	$("#ProductSupplier1").jqxDropDownList({ source: listParty, displayMember: 'groupName', valueMember: 'partyId', width: 218, height: 30, theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#ProductCurrencyUomId1").jqxDropDownList({ source: listCurrencyUoms, displayMember: 'description', valueMember: 'uomId', width: 218, height: 30, theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#quantityUomId1").jqxDropDownList({ source: quantityUomData, displayMember: 'description', valueMember: 'quantityUomId', width: 218, height: 30, theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#ProductId1").jqxDropDownList({ source: listProduct, displayMember: 'internalName', valueMember: 'productId', width: 218, height: 30, theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#availableFromDate1").jqxDateTimeInput({ width: 218, height: 30, theme: "olbius"});
	$("#AvailableThruDate1").jqxDateTimeInput({ width: 218, height: 30, theme: "olbius"});
	
	$("#lastPrice1").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: 218, height: 30, decimalDigits: 3, min: 0 });
	$("#shippingPrice1").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: 218, height: 30, decimalDigits: 3, min: 0 });
	$("#minimumOrderQuantity1").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: 218, height: 30, decimalDigits: 0, min: 0 });
	$("#alterpopupWindow").jqxWindow({ theme:'olbius',
        width: 1050, maxWidth: 2000, height: 350, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
    });
	$('#AvailableThruDate1 ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
	$('#availableFromDate1').on('valueChanged', function (event){
		var jsDate = event.args.date;
		$('#AvailableThruDate1 ').jqxDateTimeInput('setMinDate', jsDate);
	}); 
	$('#alterpopupWindow').on('close', function () {
		$('#alterpopupWindow').jqxValidator('hide');
		$('#AvailableThruDate1 ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
	});
	$('#alterpopupWindow').on('open', function () {
		$("#AvailableThruDate1").jqxDateTimeInput('val', null);
		$("#ProductSupplier1").jqxDropDownList('clearSelection');
		$("#ProductCurrencyUomId1").jqxDropDownList('clearSelection');
		$("#quantityUomId1").jqxDropDownList('clearSelection');
		$("#lastPrice1").jqxNumberInput('val', 0);
		$("#shippingPrice1").jqxNumberInput('val', 0);
		$("#minimumOrderQuantity1").jqxNumberInput('val', 0);
		$("#supplierProductId1").val("");
		$("#supplierProductName1").val("");
		$("#comments1").val("");
	});
	$('#alterpopupWindow').jqxValidator({
        rules: [
                { input: '#ProductSupplier1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#ProductSupplier1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#ProductId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#ProductId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#supplierProductId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
                { input: '#supplierProductId1', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
					rule: function (input, commit) {
						var value = $("#supplierProductId1").val();
						if (!value.containSpecialChars()) {
							return true;
						}
						return false;
					}
				},
                { input: '#supplierProductName1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
                { input: '#supplierProductName1', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
					rule: function (input, commit) {
						var value = $("#supplierProductName1").val();
						if (!value.containSpecialChars()) {
							return true;
						}
						return false;
					}
				},
                { input: '#shippingPrice1', message: '${StringUtil.wrapString(uiLabelMap.PriceNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#shippingPrice1").val();
                		if (value >= 0) {
                			return true;
						}
                		return false;
                	}
                },
                { input: '#lastPrice1', message: '${StringUtil.wrapString(uiLabelMap.PriceNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#lastPrice1").val();
                		if (value >= 0) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#ProductCurrencyUomId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#ProductCurrencyUomId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#quantityUomId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#quantityUomId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#availableFromDate1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#availableFromDate1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#comments1', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
					rule: function (input, commit) {
						var value = $("#comments1").val();
						if (!value.containSpecialChars()) {
							return true;
						}
						return false;
					}
				}
               ]
    });
    $("#alterSave").click(function () {
        if ($('#alterpopupWindow').jqxValidator('validate')) {
        	var row;
        	var tempFrDate = $('#availableFromDate1').jqxDateTimeInput('getDate');
            var tempThrDate = $('#AvailableThruDate1').jqxDateTimeInput('getDate');
            tempFrDate?tempFrDate=tempFrDate.toSQLTimeStamp():tempFrDate;
            tempThrDate?tempThrDate=tempThrDate.toSQLTimeStamp():tempThrDate;
            var partyId = $('#ProductSupplier1').val();
            var currencyUomId = $('#ProductCurrencyUomId1').val();
            var lastPrice = $('#lastPrice1').jqxNumberInput('getDecimal');
            var supplierProductName = $('#supplierProductName1').val();
            var shippingPrice = $('#shippingPrice1').jqxNumberInput('getDecimal');
            var supplierProductId = $('#supplierProductId1').val();
            var productId = $("#ProductId1").val();
            var quantityUomId = $("#quantityUomId1").val();
        	row = {
            		productId: productId,
            		comments: $('#comments1').val(),
            		currencyUomId: currencyUomId,
            		quantityUomId: quantityUomId,
            		lastPrice: lastPrice,
            		minimumOrderQuantity: $('#minimumOrderQuantity1').jqxNumberInput('getDecimal'),
            		partyId: partyId,
            		shippingPrice: shippingPrice,
            		supplierProductId: supplierProductId,
            		supplierProductName: supplierProductName,
            		availableFromDate: tempFrDate,
            		availableThruDate: tempThrDate
            	  };
        	createSupplierProductAjax(row);
    	   	
            $("#alterpopupWindow").jqxWindow('close');
		}
    });
    
    function createSupplierProductAjax(data) {
    	var saveSuccess = true;
		$.ajax({
			  url: "createSupplierProductAjax",
			  type: "POST",
			  data: data,
			  success: function(res) {
				  res["_EVENT_MESSAGE_"] == undefined?saveSuccess=true:saveSuccess=false;
			  }
		  	}).done(function() {
		  		$('#jqxNotificationNested').jqxNotification('closeLast');
		  		if (saveSuccess) {
		  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
		          	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
		          	$("#jqxNotificationNested").jqxNotification("open");
				} else {
					$("#jqxNotificationNested").jqxNotification({ template: 'error'});
					$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
		          	$("#jqxNotificationNested").jqxNotification("open");
				}
		  		getMapHasSupplierProduct();
		  		loadPlanOfProduct(data.productId);
		  	});
	}
    $('#ProductSupplier1').on('change', function (event) {
    	var args = event.args;
        if (args) {
	        var index = args.index;
	        var item = args.item;
	        var label = item.label;
	        var value = item.value;
	        var productId = $("#ProductId1").val();
	        checkHasDataSupplierProuductAjax(value, productId);
        }
    });
    $('#ProductId1').on('change', function (event) {
    	var args = event.args;
        if (args) {
	        var index = args.index;
	        var item = args.item;
	        var label = item.label;
	        var value = item.value;
	        var partyId = $("#ProductSupplier1").val();
	        checkHasDataSupplierProuductAjax(partyId, value);
        }
    });
	function checkHasDataSupplierProuductAjax(partyId, productId) {
		if (!partyId || !productId) {
			return;
		}
		var hasData = false;
		$.ajax({
	  		  url: "checkHasDataSupplierProuductAjax",
	  		  type: "POST",
	  		  data: {partyId: partyId, productId: productId},
	  		  dataType: "json",
	  		  success: function(res) {
	  			hasData = res["hasData"];
	  		  }
		  	}).done(function() {
		  		$('#jqxNotificationStatus').jqxNotification('closeLast');
		  		if (hasData) {
					$("#jqxNotificationStatus").jqxNotification({ template: 'error'});
					$("#notificationContentStatus").text("${StringUtil.wrapString(uiLabelMap.HasDataSupplierProuduct)}");
		          	$("#jqxNotificationStatus").jqxNotification("open");
		          	$("#alterSave").addClass('hidden');
				}else {
					$("#alterSave").removeClass('hidden');
				}
		  	});
	}
</script>
<style>
#pagerjqxGidPlanViewer {
	  display: none;
}
label {
	  margin-top: 4px!important;
}
</style>
	<#else>
		<h2> You do not have permission</h2>
</#if>