<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.pager.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.sort.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.selection.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.edit2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.filter.js"></script>
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
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>

<div class="row-fluid" style="display: none;" id="searchWraper">
<div class="span12">
	<div class="span3"></div>
	<div class="span9"><input type="text" id="jqxInputSearch" style="border-radius: 5px !important;float:right;" spellcheck='false'/></div>
</div>
</div>

<div id="searchGrid" style="display: none;overflow-y: hidden;"></div>


<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<div id='Gridcontent'>
<div id="divLocationStatus"></div>
<div id="divHasProductsNotLocation"></div>
<div id="treeGrid"></div>
<div id="menu"></div>

<div id="jqxwindowPopupAdderFacilityLocationArea" style="display:none;">
	<div>${uiLabelMap.CreateNewFacilityLocation}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.DAFacility}:</div>
	        	<div class="span7"><input type="text" name="txtFacility" disabled /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
		    <div class="span12 no-left-margin">
			    <div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.LocationType}<span style="color:red;"> *</span></div>
			    <div class="span7"><div id="txtLocationType" ></div></div>
		    </div>
	    </div>
	    <div class="row-fluid" style="margin-top: 7px;">
		    <div class="span12 no-left-margin">
			    <div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.FacilityLocationPosition}<span style="color:red;"> *</span></div>
			    <div class="span7"><input type="text" name="txtLocationCode" id="txtLocationCode" /></div>
		    </div>
	    </div>
	    <div class="row-fluid">
		    <div class="span12 no-left-margin">
			    <div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.Description}<span style="color:red;"> *</span></div>
		    </div>
	    </div>
	    <div class="row-fluid">
		    <div class="span12 no-left-margin">
		    	<div class="span3"></div>
			    <div class="span9"><textarea id="tarDescription"></textarea></div>
		    </div>
	    </div>
	    <hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
	    <div class="row-fluid" style="margin-top: 8px;">
		    <div class="span12 no-left-margin">
			    <div class="span9"></div>
			    <div class="span3" style="padding-left: 15px;"><button id='alterSaveAdderFacilityLocationArea'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button><button id='alterCancelAdderFacilityLocationArea'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button></div>
		    </div>
	    </div>
	</div>
</div>
<div id="jqxwindowPopupAdderFacilityLocationAreaInArea" style="display:none;">
	<div id="titleAdder">${uiLabelMap.AddNewArea}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid"  id="divOrderNotes" style="display: none;">
			<div class="span12 no-left-margin">
				<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.OrderNotes}:</div>
				<div class="span9" style="padding-right: 8px;margin-top: 8px;"><div id="txtOrderNote"></div></div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 no-left-margin">
				<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.AreaMap}:</div>
				<div class="span7"><div id="tarMapArea" style=""></div></div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 no-left-margin">
				<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.FacilityLocationPosition}<span style="color:red;"> *</span></div>
				<div class="span7"><input type="text" name="txtLocationCodeInArea" id="txtLocationCodeInArea" /></div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 no-left-margin">
				<div class="span3" style="text-align: right;margin-top: 10px;">${uiLabelMap.Description}<span style="color:red;"> *</span></div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 no-left-margin">
				<div class="span3"></div>
				<div class="span9"><textarea id="tarDescriptionInArea"></textarea></div>
			</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
		<div class="row-fluid">
			<div class="span12 no-left-margin">
				<div class="span9"></div>
				<div class="span3" style="padding-left: 15px;"><button id='alterSaveAdderFacilityLocationAreaInArea'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button><button id='alterCancelAdderFacilityLocationAreaInArea'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button></div>
			</div>
		</div>
	</div>
</div>

<div id="jqxwindowEditor" style="display:none;">
<div>${uiLabelMap.EditorDescripton}</div>
<div style="overflow-x: hidden;">
	<div class="row-fluid">
		<div class="span12 no-left-margin">
			<div class="span2" style="text-align: right;margin-top: 8px;">${uiLabelMap.FacilityLocationPosition}<span style="color:red;"> *</span></div>
			<div class="span10"><input type="text" name="txtLocationCodeEditor" id="txtLocationCodeEditor" /></div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12">
			<div class="span2" style="text-align: right;margin-top: 10px;">${uiLabelMap.Description}<span style="color:red;"> *</span></div>
			<div class="span10"><textarea id="tarDescriptionEditor"></textarea></div>
		</div>
	</div>
	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
	<div class="row-fluid">
		<div class="span12" style="margin-top: 6px;">
			<div class="span9"></div>
			<div class="span3" style="padding-left: 25px;"><button id='saveEdit'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button><button id='cancelEdit'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button></div>
		</div>
	</div>
</div>
</div>

<div id="jqxwindowPopupAdderProductToLocation" style="display:none;">
<div>${uiLabelMap.AddProductInLocationFacility}</div>
<div style="overflow-x: hidden;">
	<div class="row-fluid">
		<div class="span12 no-left-margin">
			<div id="gridFrom"></div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12 no-left-margin">
			<div class="disable-scroll" id="jqxTabsContain">
			</div>
		</div>
	</div>
	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
	<div class="row-fluid">
		<div class="span12 no-left-margin" style="margin-top: 8px;">
			<div class="span9"></div>
			<div class="span3" style="float:right;">
				<button style="float:right;" id='alterCancelAdderProductToLocation'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button style="float:right;" id='alterSaveAdderProductToLocation'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
</div>

<div id="jqxwindowPopupViewerProductsInLocations" style="display:none;">
<div>${uiLabelMap.PartyListItems}</div>
<div class='disable-scroll-x'>
	<div class="row-fluid disable-scroll">
		<div class="span12 no-left-margin">
			<div class="disable-scroll" id="jqxTabsViewerProductsInLocations">
			</div>
		</div>
	</div>
	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
	<div class="row-fluid">
		<div class="span12 no-left-margin" style="margin-top: 8px;">
			<div class="span10"></div>
			<div class="span2" style="float:right;"><button style="float:right;" id='alterCancelViewerProductsInLocations'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button></div>
		</div>
	</div>
</div>
</div>


<div id="jqxwindowPopupMoverProducts" style="display:none;">
<div>${uiLabelMap.moveProductsLocation}</div>
<div style="overflow-x: hidden;">
	<div class="row-fluid">
		<div class="span12 no-left-margin">
			<div class="disable-scroll" id="jqxTabsFrom"></div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12 no-left-margin">
			<div class="disable-scroll" id="jqxTabsTo">
			</div>
		</div>
	</div><hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
	<div class="row-fluid">
		<div class="span12 no-left-margin" style="margin-top: 8px;">
			<div class="span9"></div>
			<div class="span3" style="float:right;">
				<button style="float:right;" id='alterCancelMoverProducts'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button style="float:right;" id='alterSaveMoverProducts'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
</div>

<div id="taskBarHiden" style="display:block;margin-top: 12px;">
	<button id='viewProduct' class="customCssButton"><i class='icon-eye-open open-sans'></i>${uiLabelMap.ViewProductInLocation}</button>
	<button id='addProduct' class="customCssButton"><i class='icon-plus open-sans'></i>${uiLabelMap.AddProductInLocationFacility}</button>
	<button id='moveProductTo' class="customCssButton"><i class='icon-share-alt open-sans'></i>${uiLabelMap.MoveProductsTo}</button>
	<button id='btnCancelReset' class="customCssButton"><i class='icon-remove open-sans'></i>${uiLabelMap.CommonCancel}</button>
</div>
<div id="divUpdateProductTo" style="display:none;margin-top: 12px;">
	<button id='updateProductTo' class="customCssButton"><i class='icon-refresh open-sans'></i>${uiLabelMap.Reposition}</button>
</div>
<div id="taskBarMove" style="display:none;margin-top: 12px;">
	<button id='moveProduct' class="customCssButton"><i class='icon-share-alt open-sans'></i>${uiLabelMap.MoveProducts}</button>
	<button id='btnReset' class="customCssButton"><i class='icon-remove open-sans'></i>${uiLabelMap.CommonCancel}</button>
</div>

</div>


<div id="divPhysicalInventory" style="display: none;">
	<div id="gridPhysicalInventory"></div>
	<div class="row-fluid">
		<div class="span12 no-left-margin">
			<div class="span4"></div>
			<div class="span8" style="float:right;">
				<button style="float:right;" id='cancelExport'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button style="float:right;" id='updatePhysicalInventory'><i class='icon-edit'></i>${uiLabelMap.UpdatePhysicalInventory}</button>
				<button style="float:right;" id='exportExcel'><i class=' icon-download-alt'></i>${uiLabelMap.exportExcel}</button>
			</div>
		</div>
	</div>
</div>

<div id="divCreatePhysicalInventoryAndVariance" style="display: none;">
	<div id="gridCreatePhysicalInventoryAndVariance"></div>
	<div class="row-fluid">
		<div class="span12 no-left-margin">
			<div class="span6"></div>
			<div class="span6" style="float:right;">
				<button style="float:right;" id='cancelExportCreatePhysicalInventoryAndVariance'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button style="display: none;float:right;" id='exportCreatePhysicalInventoryAndVariance'><i class=' icon-download-alt'></i>${uiLabelMap.exportExcel}</button>
				<button style="float:right;" id='updatePhysicalInventoryAndVariance'><i class=' icon-ok'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="divInputVariance" style="display:none;">
	<div>${uiLabelMap.UpdateQuantityProduct}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="span3" style="text-align: right;">${StringUtil.wrapString(uiLabelMap.Quantity)}<span style="color:red;"> *</span></div>
				<div class="span9"><div id="txtQuantityVariance"></div><label id="lblPacking" style="color:#037c07;position: absolute;margin-left: 230px;margin-top: -25px;"></label></div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12">
				<div class="span3" style="text-align: right;">${StringUtil.wrapString(uiLabelMap.accComments)}<span style="color:red;"> *</span></div>
				<div class="span9"><textarea  class="note-area no-resize" id="tarCommentsVariance" autocomplete="off" style="resize: none;"></textarea></div>
			</div>
		</div>
		<hr style="margin: 10px 0px 5px 0px; border-top: 1px solid grey;">
		<div class="row-fluid">
			<div class="span12 no-left-margin">
				<div class="span4"></div>
				<div class="span7" style="float:right;">
					<button style="float:right;" id='alterCancelInputVariance'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
					<button style="float:right;" id='alterSaveInputVariance'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>


<div id="divFacilityDelivery" style="display:none;">
<div>${uiLabelMap.FacilityDeliveryQuantity}</div>
<div style="overflow-x: hidden;">
	<div class="row-fluid">
		<div class="span12">
			<div class="span3" style="text-align: right;">${StringUtil.wrapString(uiLabelMap.Quantity)}<span style="color:red;"> *</span></div>
			<div class="span9"><div id="txtQuantityDelivery"></div><label id="lblUomId" style="color:#037c07;margin-top: -24px;margin-left: 230px;position: absolute;" ></label></div>
		</div>
	</div>
	<hr style="margin: 10px 0px 5px 0px; border-top: 1px solid grey;">
	<div class="row-fluid">
		<div class="span12 no-left-margin">
			<div class="span4"></div>
			<div class="span7" style="float:right;">
				<button style="float:right;" id='alterCancelFacilityDelivery'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button style="float:right;" id='alterSaveFacilityDelivery'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
</div>

<#assign listLocationFacilityType = delegator.findList("LocationFacilityType", null, null, null, null, false) />
<#assign listProduct = delegator.findList("Product", null, null, null, null, false) />
<#assign listVarianceReason = delegator.findList("VarianceReason", null, null, null, null, false) />
<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
<#assign inventoryItem = delegator.findList("InventoryItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityId", facilityId), null, null, null, false)>
<script>

	function disableScrolling(){
	    var x= window.scrollX;
	    var y= window.scrollY;
//	    window.onscroll = function(){window.scrollTo(x, y);};
	}

	function enableScrolling(){
	    window.onscroll = function(){};
	}
	
	
var quantityUomData = [
              <#if quantityUoms?exists>
				<#list quantityUoms as item>
					{
						quantityUomId: '${item.uomId?if_exists}',
						description: '${StringUtil.wrapString(item.abbreviation?if_exists)}'
					},
				</#list>
			 </#if>
	];
var mapQuantityUom = {
	<#if quantityUoms?exists>
		<#list quantityUoms as item>
				'${item.uomId?if_exists}': '${StringUtil.wrapString(item.abbreviation?if_exists)}',
		</#list>
	 </#if>
};
var listVarianceReason = [
				<#list listVarianceReason as item>{
					varianceReasonId: '${item.varianceReasonId?if_exists}',
					description: '${StringUtil.wrapString(item.get("description", locale)?if_exists)}'},
				</#list>
	];
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	var cellclassname = function (row, column, value, data) {
        return "dragable";
    };
	$(document).keypress(function(ev) {
		var keycode = (ev.keyCode ? ev.keyCode : ev.which);
		if(ev.shiftKey & keycode == 70){
			searchProducts();
		}
	});
	
	$("#divInputVariance").jqxWindow({theme: 'olbius',
	    width: 450, maxWidth: 1845, minHeight: 200, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancelInputVariance"), modalOpacity: 0.7
	});
	$("#alterCancelInputVariance").jqxButton({template: "danger" });
	$("#alterSaveInputVariance").jqxButton({template: "primary"});
	$("#txtQuantityVariance").jqxNumberInput({ width: '218px', inputMode: 'simple', decimalDigits: 0});
	$("#alterSaveInputVariance").click(function () {
		if ($('#divInputVariance').jqxValidator('validate')) {
			var thisQuantityVariance = $('#txtQuantityVariance').jqxNumberInput('val');
			var thisCommentsVariance = $("#tarCommentsVariance").val();
			thisCommentsVariance = thisCommentsVariance.trim();
			var commentForReason = varianceReasonIdActive + "desr";
			$("#gridCreatePhysicalInventoryAndVariance").jqxGrid('setcellvalue', rowBoundIndexReasonIdActive, varianceReasonIdActive, thisQuantityVariance);
			$("#gridCreatePhysicalInventoryAndVariance").jqxGrid('setcellvalue', rowBoundIndexReasonIdActive, commentForReason, thisCommentsVariance);
			$("#divInputVariance").jqxWindow('close');
		}
	});
	$('#divInputVariance').jqxValidator({
        rules: [
					{ input: '#txtQuantityVariance', message: '${StringUtil.wrapString(uiLabelMap.QuantityNotValid)}', action: 'keyup, change', 
						rule: function (input, commit) {
							var value = $("#txtQuantityVariance").val();
							if (_.indexOf(['VAR_STOLEN', 'VAR_DAMAGED', 'VAR_SAMPLE', 'VAR_MISSHIP_ORDERED', 'VAR_MISSHIP_SHIPPED'], varianceReasonIdActive) == -1) {
								if (value > 0) {
									return true;
								}
							} else {
								if (value < 0) {
									return true;
								}
							}
							return false;
						}
					},
					{ input: '#tarCommentsVariance', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, change', rule: 'required' }
               ]
    });
	$('#divInputVariance').on('close', function (event) {
		varianceReasonIdActive = "";
    	rowBoundIndexReasonIdActive = "";
    	enableScrolling();
    	$('#divInputVariance').jqxValidator('hide');
	});
	var openGuide = true;
	$("#divInputVariance").on('open', function (event) {
		$('#txtQuantityVariance').jqxNumberInput('val', 0);
		$("#tarCommentsVariance").val("");
		disableScrolling();
		if (openGuide) {
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.guideCreatePhysicalInventoryAndVariance)}");
		}
		openGuide = false;
	});
	function physicalInventory() {
		getAllProductForPhysicalInventory(null, facilityIdGlobal);
		$("#gridPhysicalInventoryClearFilters").css("display", "block");
		$("#gridPhysicalInventoryClearFilters").css("float", "right");
		$("#gridPhysicalInventoryClearFilters").css("margin-top", "5px");
		$("#searchGrid").css({ "display": "none"});
		$("#divCreatePhysicalInventoryAndVariance").css({ "display": "none"});
		$("#searchWraper").css({ "display": "none"});
		$("#Gridcontent").animate({ "margin-top": "0px"});
		$('#jqxInputSearch').val('');
		$("#searchGrid").css({ "display": "none"});
        listProductAvalible = [];
	}
	function getAllProductForPhysicalInventory(productId, facilityId) {
		var allProduct = [];
		$.ajax({
	  		  url: "getListProductAvalibleAjax",
	  		  type: "POST",
	  		  data: {productId: productId, facilityId: facilityId},
	  		  dataType: "json",
	  		  async: false,
	  		  success: function(res) {
	  			allProduct = listProductAvalible.concat(res["listProductAvalible"]);
	  		  }
		  	}).done(function() {
		  		
		  	});
		bindGridForPhysicalInventory(allProduct);
		renderGridCreatePhysicalInventoryAndVariance(allProduct);
	}
	function bindGridForPhysicalInventory(allProduct) {
		for ( var x in allProduct) {
			if (typeof allProduct[x].expireDate == 'object') {
				allProduct[x].expireDate = allProduct[x].expireDate['time'];
				allProduct[x].datetimeReceived = allProduct[x].datetimeReceived['time'];
			}
 		}
		var sourceForPhysicalInventory =
        {
            localdata: allProduct,
            datatype: "local",
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' },
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
        var dataAdapterForPhysicalInventory = new $.jqx.dataAdapter(sourceForPhysicalInventory);
        $("#gridPhysicalInventory").jqxGrid({
            source: dataAdapterForPhysicalInventory,
            localization: {groupsheaderstring: "${StringUtil.wrapString(uiLabelMap.Groupsheaderstring)}", filterselectstring: "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}"},
            showfilterrow: true,
            filterable: true,
            editable:false,
            handlekeyboardnavigation: function (event) {
                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                if (key == 70 && event.ctrlKey) {
                	ClearFiltersGridPhysicalInventory();
                	return true;
                }
		 	},
		 	groupable: true,
		 	groups: ['locationId'],
		 	width: '100%',
            autoheight: true,
            theme: 'olbius',
            sortable: true,
            showaggregates: true,
            showstatusbar: true,
            statusbarheight: 40,
            selectionmode: 'singlerow',
            columns: [
				{ text: '${StringUtil.wrapString(uiLabelMap.FacilityLocationPosition)}', dataField: 'locationId', editable:false, filtertype: 'checkedlist', align: 'center', cellsalign: 'center', minwidth: 200,
				    cellsrenderer: function(row, colum, value){
				  	  var locationCode = getLocationCode(value);
				  	  return '<span>' + locationCode + '</span>';
				    },
				    createfilterwidget: function (column, htmlElement, editor) {
                		  var listProductAvalibleInFacility = getAllProductInFacility();
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listlocationFacility), displayMember: 'locationId', valueMember: 'locationId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return getLocationCode(value);
				                }
	    		        	});
	    		        	editor.jqxDropDownList('checkAll');
                    }
				},
                  { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_inventoryItemId)}', dataField: 'inventoryItemId', filtertype: 'input', align: 'center', cellsalign: 'center', width: 150, editable:false },
	              { text: '${StringUtil.wrapString(uiLabelMap.accProductId)}', dataField: 'productId', align: 'center', cellsalign: 'center', filtertype: 'checkedlist', editable:false, width: 180, cellclassname: cellclassname,
                	  cellsrenderer: function(row, colum, value){
							return '<span>' + mapProductWithName[value] + '</span>';
                	  },
                	  createfilterwidget: function (column, htmlElement, editor) {
                		  var listProductAvalibleInFacility = getAllProductInFacility();
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listProductAvalibleInFacility), displayMember: 'productId', valueMember: 'productId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapProductWithName[value];
				                }
	    		        	});
	    		        	editor.jqxDropDownList('checkAll');
	                    }
	              },
	              { text: '${StringUtil.wrapString(uiLabelMap.DatetimeReceived)}', dataField: 'datetimeReceived', align: 'center', editable:false, width: 200, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
	              { text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'center', editable:false, width: 200, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
	              { text: '${StringUtil.wrapString(uiLabelMap.PackingUnit)}', dataField: 'uomId', editable:false, align: 'center', filtertype: 'checkedlist', cellsalign: 'center',
	            	  cellsrenderer: function(row, colum, value){
	    			        return '<span>' + mapQuantityUom[value] + '</span>';
	    		      },
		              createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(quantityUomData), displayMember: 'quantityUomId', valueMember: 'quantityUomId' ,
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
	              { text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', dataField: 'quantity', align: 'center', width: 160, columntype:'numberinput', cellsalign: 'right', filtertype: 'number',
	            	  cellsrenderer: function(row, colum, value){
							return '<span style=\"float: right;padding-right: 5px;text-align: right;\">' + value.toLocaleString(locale) + '</span>';
	            	  },
						aggregates: ['sum'],
						aggregatesrenderer: function (aggregates) {
							var renderstring = "";
							$.each(aggregates, function (key, value) {
								renderstring += '<div style="position: relative; margin: 4px; overflow: hidden;">' + '${uiLabelMap.Total}' + ': ' + value.toLocaleString(locale) +'</div>';
							});
							return renderstring;
						},
						createeditor: function(row, column, editor){
							editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
						}
	              }
	            ]
        });
        $("#divPhysicalInventory").css({ "display": "block"});
        $("#Gridcontent").css({ "display": "none"});
	}
	$("#updatePhysicalInventory").jqxButton({template: "primary", height: "30px", width: "170px" });
	$("#updatePhysicalInventory").click(function () {
		$("#divPhysicalInventory").css({ "display": "none"});
        $("#divCreatePhysicalInventoryAndVariance").css({ "display": "block"});
	});
	
	$("#updatePhysicalInventoryAndVariance").jqxButton({template: "primary", height: "30px", width: "70px" });
	$("#updatePhysicalInventoryAndVariance").click(function () {
		progessDataToCreatePhysicalInventoryAndVariance();
	});
	
	
	$("#exportCreatePhysicalInventoryAndVariance").jqxToggleButton();
	$("#exportCreatePhysicalInventoryAndVariance").on('click', function () {
        var toggled = $("#exportCreatePhysicalInventoryAndVariance").jqxToggleButton('toggled');
        if (toggled) {
   		 	$("#exportCreatePhysicalInventoryAndVariance").html("<i class='icon-ok'></i>${StringUtil.wrapString(uiLabelMap.CommonSave)}");
   		 	$("#gridCreatePhysicalInventoryAndVariance").jqxGrid('exportdata', 'xls', '${StringUtil.wrapString(uiLabelMap.InventoryCountTheVotes)}');
        }else {
        	progessDataToCreatePhysicalInventoryAndVariance();
        }
    });
	function renderGridCreatePhysicalInventoryAndVariance(allProduct) {
		for ( var x in allProduct) {
			if (typeof allProduct[x].expireDate == 'object') {
				allProduct[x].expireDate = allProduct[x].expireDate['time'];
			}
			allProduct[x].locationCode = getLocationCode(allProduct[x].locationId);
 		}
		var sourceGridCreatePhysicalInventoryAndVariance =
        {
            localdata: allProduct,
            datatype: "local",
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'locationId', type: 'string' },
                { name: 'uomId', type: 'string' },
                { name: 'VAR_STOLEN', type: 'number' },
                { name: 'VAR_FOUND', type: 'number' },
                { name: 'VAR_DAMAGED', type: 'number' },
                { name: 'VAR_SAMPLE', type: 'number' },
                { name: 'VAR_MISSHIP_ORDERED', type: 'number' },
                { name: 'VAR_MISSHIP_SHIPPED', type: 'number' },
            ],
            addrow: function (rowid, rowdata, position, commit) {
            	
                commit(true);
            },
            deleterow: function (rowid, commit) {
                
                commit(true);
            },
            updaterow: function (rowid, newdata, commit) {
            	
                commit(true);
            },
        };
        var dataAdapterGridCreatePhysicalInventoryAndVariance = new $.jqx.dataAdapter(sourceGridCreatePhysicalInventoryAndVariance);
		$("#gridCreatePhysicalInventoryAndVariance").jqxGrid({
				source : dataAdapterGridCreatePhysicalInventoryAndVariance,
				localization: getLocalization(),
				editable: false,
				autoheight: true,
				editmode: 'selectedrow',
				width: '100%',
	            theme: 'olbius',
	            sortable: true,
	            selectionmode: 'singlerow',
	            showpinnedcolumnbackground: true,
	            columns: [
				{ text: '${StringUtil.wrapString(uiLabelMap.FacilityLocationPosition)}', dataField: 'locationId', pinned: true, editable: false, filtertype: 'input', align: 'center', cellsalign: 'center', minwidth: 200,
					    cellsrenderer: function(row, colum, value){
					  	  var locationCode = getLocationCode(value);
					  	  return '<span>' + locationCode + '</span>';
					    }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_inventoryItemId)}', dataField: 'inventoryItemId', align: 'center', width: 150, editable:false },
				{ text: '${StringUtil.wrapString(uiLabelMap.accProductId)}', dataField: 'productId', align: 'center', width: 180, editable:false},
				{ text: '${StringUtil.wrapString(uiLabelMap.PackingUnit)}', dataField: 'uomId', align: 'center', width: 180, editable:false,
					cellsrenderer: function(row, colum, value){
    			        return '<span>' + mapQuantityUom[value] + '</span>';
					},	
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'center', width: 200, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center', editable:false },
				  
				{ text: getDescriptionVarianceReason('VAR_STOLEN'), dataField: 'VAR_STOLEN', columngroup: 'VarianceReasonDetails', align: 'center', cellsalign: 'right', columntype:'numberinput', width: 180,
					  cellsrenderer: function(row, colum, value){
						return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					  },
					  createeditor: function(row, column, editor){
						editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
					  }
				},
				{ text: '', dataField: 'VAR_STOLENdesr', columngroup: 'VarianceReasonDetails',hidden: true},
				{ text: getDescriptionVarianceReason('VAR_FOUND'), dataField: 'VAR_FOUND', columngroup: 'VarianceReasonDetails', align: 'center', cellsalign: 'right', columntype:'numberinput', width: 180,
					  cellsrenderer: function(row, colum, value){
						  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					  },
					  createeditor: function(row, column, editor){
						  editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
					  }
				},
				{ text: '', dataField: 'VAR_FOUNDdesr', columngroup: 'VarianceReasonDetails',hidden: true},
				{ text: getDescriptionVarianceReason('VAR_DAMAGED'), dataField: 'VAR_DAMAGED', columngroup: 'VarianceReasonDetails', align: 'center', cellsalign: 'right', columntype:'numberinput', width: 180,
					  cellsrenderer: function(row, colum, value){
						  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					  },
					  createeditor: function(row, column, editor){
						  editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
					  }
				},
				{ text: '', dataField: 'VAR_DAMAGEDdesr', columngroup: 'VarianceReasonDetails',hidden: true},
				{ text: getDescriptionVarianceReason('VAR_SAMPLE'), dataField: 'VAR_SAMPLE', columngroup: 'VarianceReasonDetails', align: 'center', cellsalign: 'right', columntype:'numberinput', width: 180,
					  cellsrenderer: function(row, colum, value){
						  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					  },
					  createeditor: function(row, column, editor){
						  editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
						  }
				},
				{ text: '', dataField: 'VAR_SAMPLEdesr', columngroup: 'VarianceReasonDetails',hidden: true},
				{ text: getDescriptionVarianceReason('VAR_MISSHIP_ORDERED'), dataField: 'VAR_MISSHIP_ORDERED', columngroup: 'VarianceReasonDetails', align: 'center', cellsalign: 'right', columntype:'numberinput', width: 250,
					  cellsrenderer: function(row, colum, value){
						  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					  },
					  createeditor: function(row, column, editor){
						  editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
					  }
				},
				{ text: '', dataField: 'VAR_MISSHIP_ORDEREDdesr', columngroup: 'VarianceReasonDetails',hidden: true},
				{ text: getDescriptionVarianceReason('VAR_MISSHIP_SHIPPED'), dataField: 'VAR_MISSHIP_SHIPPED', columngroup: 'VarianceReasonDetails', align: 'center', cellsalign: 'right', columntype:'numberinput', width: 250,
					  cellsrenderer: function(row, colum, value){
						  return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					  },
					  createeditor: function(row, column, editor){
						  editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
					  }
				},
				{ text: '', dataField: 'VAR_MISSHIP_SHIPPEDdesr', columngroup: 'VarianceReasonDetails',hidden: true}
	            ],
	            columngroups: [
	                           { text: '${StringUtil.wrapString(uiLabelMap.VarianceReasonDetails)}', align: 'center', name: 'VarianceReasonDetails' }
	                       ]
	    });
	}
	function progessDataToCreatePhysicalInventoryAndVariance() {
		var gridCreatePhysicalInventoryAndVarianceData = $("#gridCreatePhysicalInventoryAndVariance").jqxGrid('getdisplayrows');
		for ( var z in gridCreatePhysicalInventoryAndVarianceData) {
			if (typeof gridCreatePhysicalInventoryAndVarianceData[z].expireDate == 'object') {
				gridCreatePhysicalInventoryAndVarianceData[z].expireDate = gridCreatePhysicalInventoryAndVarianceData[z].expireDate.getTime();
			}
		}
		var dataRecord = new Array();
		var partyId = "${userLogin.userLoginId}";
		
		for ( var s in gridCreatePhysicalInventoryAndVarianceData) {
			var thisObject = gridCreatePhysicalInventoryAndVarianceData[s];
			var inventoryItemId = thisObject.inventoryItemId;
			for ( var x in arrayVarianceReason) {
				if (_.has(thisObject, arrayVarianceReason[x])) {
					var varianceReasonId = arrayVarianceReason[x];
					var comments = varianceReasonId + "desr";
					var quantityOnHandVar = thisObject[varianceReasonId];
					var rowRecord = {};
					rowRecord.partyId = partyId;
					rowRecord.varianceReasonId = varianceReasonId;
					rowRecord.quantityOnHandVar = quantityOnHandVar;
					rowRecord.inventoryItemId = inventoryItemId;
					rowRecord.comments = thisObject[comments];
					rowRecord.generalComments = thisObject[comments];
					dataRecord.push(rowRecord);
					createPhysicalInventoryAndVarianceAjax(rowRecord);
				}
			}
		}
		if (dataRecord.length > 0) {
			$('#jqxNotificationNested').jqxNotification('closeLast');
			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
          	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
          	$("#jqxNotificationNested").jqxNotification("open");
		}
		$("#gridCreatePhysicalInventoryAndVariance").jqxGrid('updatebounddata');
	}
	function createPhysicalInventoryAndVarianceAjax(data) {
		$.ajax({
	  		  url: "createPhysicalInventoryAndVariance",
	  		  type: "POST",
	  		  data: data,
	  		  success: function(res) {
	  			  
	  		  }
		  	});
	}
	function getDescriptionVarianceReason(varianceReasonId) {
		for ( var x in listVarianceReason) {
			var thisVarianceReasonId = listVarianceReason[x].varianceReasonId;
			if (varianceReasonId == thisVarianceReasonId) {
				return listVarianceReason[x].description;
			}
		}
		return "";
	}
	
	$("#gridCreatePhysicalInventoryAndVariance").on("celldoubleclick", function (event){
			    var args = event.args;
			    var rowBoundIndex = args.rowindex;
			    var rowVisibleIndex = args.visibleindex;
			    var rightClick = args.rightclick; 
			    var ev = args.originalEvent;
			    var columnIndex = args.columnindex;
			    var dataField = args.datafield;
			    var value = args.value;
			    if (_.indexOf(arrayVarianceReason, dataField) != -1) {
			    	varianceReasonIdActive = dataField;
			    	rowBoundIndexReasonIdActive = rowBoundIndex;
			    	var data = $("#gridCreatePhysicalInventoryAndVariance").jqxGrid('getrowdata', rowBoundIndex);
			    	var uomId = mapQuantityUom[data.uomId];
			    	$("#lblPacking").text("(" + uomId + ")");
			    	var wtmp = window;
			    	var tmpwidth = $('#divInputVariance').jqxWindow('width');
                    $("#divInputVariance").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
			    	$("#divInputVariance").jqxWindow('open');
				}
			});
	var varianceReasonIdActive = "";
	var rowBoundIndexReasonIdActive = "";
	$("#exportExcel").jqxButton({template: "primary", height: "30px", width: "150px" });
	$("#exportExcel").click(function () {
//		$("#gridPhysicalInventory").jqxGrid('exportdata', 'xls', '${StringUtil.wrapString(uiLabelMap.InventoryDetails)}');
		window.location.href = "exportInventoryCountTheVotes?facilityId=" + facilityIdGlobal;
	});
	$("#cancelExport").jqxButton({template: "danger", height: "30px", width: "60px" });
	$("#cancelExport").click(function () {
		$("#divPhysicalInventory").css({ "display": "none"});
		$("#divCreatePhysicalInventoryAndVariance").css({ "display": "none"});
        $("#Gridcontent").css({ "display": "block"});
        $("#gridPhysicalInventoryClearFilters").css("display", "none");
        $("#exportCreatePhysicalInventoryAndVariance").html("<i class=' icon-edit'></i>${StringUtil.wrapString(uiLabelMap.exportExcel)}");
	});
	$("#cancelExportCreatePhysicalInventoryAndVariance").jqxButton({template: "danger", height: "30px", width: "70px"});
	$("#cancelExportCreatePhysicalInventoryAndVariance").click(function () {
		$("#divPhysicalInventory").css({ "display": "none"});
		$("#divCreatePhysicalInventoryAndVariance").css({ "display": "none"});
		$("#Gridcontent").css({ "display": "block"});
		$("#gridPhysicalInventoryClearFilters").css("display", "none");
		$("#exportCreatePhysicalInventoryAndVariance").html("<i class=' icon-edit'></i>${StringUtil.wrapString(uiLabelMap.exportExcel)}");
	});
	function ClearFiltersGridPhysicalInventory() {
		$('#gridPhysicalInventory').jqxGrid('clearfilters');
	}
	var isSearch = true;
	function searchProducts() {
		if (isSearch) {
			listProductAvalibleInFacilitySearch = getAllProductInFacility();
			$("#searchWraper").css({ "display": "block"});
			$("#Gridcontent").animate({ "margin-top": "20px"});
			$('#jqxInputSearch').jqxInput('focus');
		}else {
			closeSearch();
		}
		isSearch = !isSearch;
		setTimeout(function(){
			$('#jqxInputSearch').val('');
		}, 10);
	}
	$("#jqxInputSearch").on('select', function (event) {
        if (event.args) {
            var item = event.args.item;
            var value = item.value;
            getAllProductInLocation(value, facilityIdGlobal);
            for ( var x in listProductAvalibleInFacilitySearch) {
				var productId = listProductAvalibleInFacilitySearch[x].productId;
				if (value == productId) {
					listProductAvalibleInFacilitySearch.splice(x, 1);
				}
			}
        }
    });
	var listProductAvalible = [];
	function getAllProductInLocation(productId, facilityId) {
		$.ajax({
	  		  url: "getListProductAvalibleAjax",
	  		  type: "POST",
	  		  data: {productId: productId, facilityId: facilityId},
	  		  dataType: "json",
	  		  async: false,
	  		  success: function(res) {
	  			listProductAvalible = listProductAvalible.concat(res["listProductAvalible"]);
	  		  }
		  	}).done(function() {
		  		showResultProducts();
		  	});
	}
	function showResultProducts() {
		listProductAvalible = _.uniq(listProductAvalible);
		for ( var x in listProductAvalible) {
			if (typeof listProductAvalible[x].expireDate == 'object') {
				listProductAvalible[x].expireDate = listProductAvalible[x].expireDate['time'];
				listProductAvalible[x].datetimeReceived = listProductAvalible[x].datetimeReceived['time'];
			}
 		}
		var sourceSearch =
        {
            localdata: listProductAvalible,
            datatype: "local",
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' },
                { name: 'facilityDelivery', type: 'string' }
            ],
            addrow: function (rowid, rowdata, position, commit) {
            	
                commit(true);
            },
            deleterow: function (rowid, commit) {
                
                commit(true);
            },
            updaterow: function (rowid, newdata, commit) {
            	var result = deliveryInLocationAjax(newdata);
                commit(result);
            }
        };
        var dataAdapterSearch = new $.jqx.dataAdapter(sourceSearch);
        $("#searchGrid").jqxGrid({
            source: dataAdapterSearch,
            localization: getLocalization(),
            width: '100%',
            theme: 'olbius',
            pageable: true,
            autoheight: true,
            pagesize: 15,
            sortable: true,
            selectionmode: 'singlerow',
            columns: [
              { text: '${uiLabelMap.FormFieldTitle_inventoryItemId}', dataField: 'inventoryItemId', align: 'center', width: 150, editable:false },
              { text: '${uiLabelMap.accProductId}', dataField: 'productId', align: 'center', width: 180},
              { text: '${uiLabelMap.DatetimeReceived}', dataField: 'datetimeReceived', align: 'center', width: 150, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
              { text: '${uiLabelMap.ProductExpireDate}', dataField: 'expireDate', align: 'center', width: 150, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
              { text: '${uiLabelMap.PackingUnit}', dataField: 'uomId', align: 'center',
            	  cellsrenderer: function(row, colum, value){
    			        var data = $("#searchGrid").jqxGrid('getrowdata', row);
    			        var productId = data.productId;
    			        return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
    		        }  
              },
              { text: '${uiLabelMap.Quantity}', dataField: 'quantity', align: 'center', width: 160, cellsalign: 'right',
            	  cellsrenderer: function(row, colum, value){
						return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					}  
              },
              { text: '${uiLabelMap.AreaMap}', dataField: '', align: 'center', minwidth: 200,
            	  cellsrenderer: function(row, colum, value){
            		    var data = $("#searchGrid").jqxGrid('getrowdata', row);
						var locationId = data.locationId;
						var parentLocationId = getParentLocation(locationId);
						var map = getPathArea(locationId, parentLocationId, '');
						var dataShort = executeMyData(map);
				        var id = 'description' + locationId;
				        return "<span id='" + id + "' onmouseenter='showMore(\"" + map + "\",\"" + id + "\"," + true + ")' >" + dataShort + "</span>";
					}
              },
              { text: '${uiLabelMap.FacilityDelivery}', dataField: 'facilityDelivery', align: 'center', width: 100,
            	  cellsrenderer: function(row, colum, value){
            		  return "<span><a onclick='facilityDelivery(" + row + ")' ><i class='fa-truck'></i> ${uiLabelMap.FacilityDelivery}</a></span>";
            	  }
              }
            ]
        });
        $("#searchGrid").css({ "display": "block"});
        $("#divPhysicalInventory").css({ "display": "none"});
        $("#divCreatePhysicalInventoryAndVariance").css({ "display": "none"});
        $("#Gridcontent").css({ "display": "none"});
	}
	function facilityDelivery(row) {
		$("#divFacilityDelivery").attr("row", row);
		$("#divFacilityDelivery").jqxWindow("open");
		var data = $("#searchGrid").jqxGrid('getrowdata', row);
		var uomId = data.uomId;
		$("#lblUomId").text(mapQuantityUom[uomId]);
		$("#txtQuantityDelivery").jqxNumberInput("val", 0);
	}
	$("#divFacilityDelivery").jqxWindow({theme: 'olbius',
	    width: 450, maxWidth: 1845, minHeight: 120, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelFacilityDelivery"), modalOpacity: 0.7
	});
	$("#txtQuantityDelivery").jqxNumberInput({ width: '218px', inputMode: 'simple', decimalDigits: 0});
	$("#alterCancelFacilityDelivery").jqxButton({template: "danger" });
	$("#alterSaveFacilityDelivery").jqxButton({template: "primary"});
	$("#alterSaveFacilityDelivery").click(function () {
		if ($('#divFacilityDelivery').jqxValidator('validate')) {
			var row = $("#divFacilityDelivery").attr("row");
			var data = $("#searchGrid").jqxGrid('getrowdata', row);
			var deliveryQuantity = $("#txtQuantityDelivery").val();
			var oldQuantity = data.quantity;
			var newQuantity = oldQuantity - deliveryQuantity;
			$("#searchGrid").jqxGrid('setcellvalue', row, "quantity", newQuantity);
			$("#divFacilityDelivery").jqxWindow("close");
		}
	});
	function deliveryInLocationAjax(data) {
		var result;
		var info;
		$.ajax({
    		  url: "deliveryInLocationAjax",
    		  type: "POST",
    		  data: data,
    		  async: false,
    		  success: function(res) {
    			  info = res["info"];
    		  }
	  	}).done(function() {
	  		$('#jqxNotificationNested').jqxNotification('closeLast');
	  		if (info == "success") {
	  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
              	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
              	$("#jqxNotificationNested").jqxNotification("open");
              	result = true;
			} else {
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
              	$("#jqxNotificationNested").jqxNotification("open");
              	result = false;
			}
	  	});
		return result;
	}
	$('#divFacilityDelivery').jqxValidator({
        rules: [
					{ input: '#txtQuantityDelivery', message: '${StringUtil.wrapString(uiLabelMap.QuantityNotValid)}', action: 'keyup, change', 
						rule: function (input, commit) {
							var value = $("#txtQuantityDelivery").val();
							if (value > 0) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtQuantityDelivery', message: '${StringUtil.wrapString(uiLabelMap.QuantityBigerThanCurrentQuantity)}', action: 'keyup, change', 
						rule: function (input, commit) {
							var value = $("#txtQuantityDelivery").val();
							var row = $("#divFacilityDelivery").attr("row");
							var data = $("#searchGrid").jqxGrid('getrowdata', row);
							var oldQuantity = data.quantity;
							if (value <= oldQuantity) {
								return true;
							}
							return false;
						}
					}
               ]
    });
	
	var sizeSub = 40;
	function showMore(data, id, autoHide) {
			data = data.trim();
		    $("#" + id).jqxTooltip({ content: data, position: 'right', autoHideDelay: 3000, closeOnClick: false, autoHide: autoHide});
	}
   function executeMyData(dataShow) {
	   if (dataShow != null) {
		   var datalength = dataShow.length;
	        var dataShowShort = "";
	        if (datalength > sizeSub) {
	        	dataShowShort = dataShow.substr(0, sizeSub) + "...";
			}else {
				dataShowShort = dataShow;
			}
		   return dataShowShort;
		} else {
			 return '';
		}
   }
   function closeSearch() {
		$("#searchWraper").css({ "display": "none"});
		$("#divCreatePhysicalInventoryAndVariance").css({ "display": "none"});
		$("#Gridcontent").animate({ "margin-top": "0px"});
		$('#jqxInputSearch').val('');
		$("#searchGrid").css({ "display": "none"});
        $("#Gridcontent").css({ "display": "block"});
        listProductAvalible = [];
	}
	$('#jqxwindowPopupMoverProducts').on('close', function (event) {
		$('#jqxTabsMoverFrom').jqxTabs('destroy');
		$('#jqxTabsMoverTo').jqxTabs('destroy');
		$( "#btnReset" ).trigger( "click" );
	});
	$("#jqxwindowPopupMoverProducts").jqxWindow({theme: 'olbius',
	    width: 950, maxWidth: 1845, height: 620, maxHeight: 675, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelMoverProducts"), modalOpacity: 0.7
	});
	$("#alterCancelMoverProducts").jqxButton({template: "danger" });
	$("#alterSaveMoverProducts").jqxButton({template: "primary"});
	$("#alterSaveMoverProducts").click(function () {
		var totalRecord = new Array();
		$('#alterSaveMoverProducts').jqxButton({disabled: true });
		for ( var x in arrayLocationIdFrom) {
			totalRecord.push.apply(totalRecord, mapInventoryFrom[arrayLocationIdFrom[x]]);
		}
		for ( var e in arrayLocationIdTo) {
			totalRecord.push.apply(totalRecord, mapInventoryTo[arrayLocationIdTo[e]]);
		}
		for ( var z in totalRecord) {
			if (typeof totalRecord[z].expireDate == 'object') {
				totalRecord[z].expireDate = totalRecord[z].expireDate.getTime();
			}
		}
		saveDataToLocation(totalRecord);
		$("#jqxwindowPopupMoverProducts").jqxWindow('close');
	});
	
	$("#jqxwindowPopupMoverProducts").on('open', function (event) {
		disableScrolling();
	});
	$("#jqxwindowPopupMoverProducts").on('close', function (event) {
		enableScrolling();
	});
	
	
	var listlocationFacilityTemp = [];
	$("#moveProductTo").jqxButton({template: "primary", height: "30px", width: "200px" });
	var arrayLocationIdFrom = [];
	var arrayLocationIdTo = [];
	var moveMode = false;
	$("#moveProductTo").click(function () {
		if (arrayLocationId.length == 0) {
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.ChooseLocation)}");
			return;
		}
		arrayLocationIdFrom = arrayLocationId;
		arrayLocationIdFrom = _.uniq(arrayLocationIdFrom);
		for ( var x in arrayLocationIdFrom) {
			if (!mapHasInventoryInLocation[arrayLocationIdFrom[x]]) {
				arrayLocationIdFrom.splice(x, 1);
			}
		}
		if (arrayLocationIdFrom.length == 0) {
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.LocationNotHasProduct)}");
			return;
		}
		listlocationFacilityTemp = JSON.stringify(listlocationFacility);
		var listlocationFacilityFrom = [];
		for ( var x in listlocationFacility) {
			var locationId = listlocationFacility[x].locationId;
			for ( var y in arrayLocationIdFrom) {
				var locationIdFr = arrayLocationIdFrom[y];
				if (locationId == locationIdFr) {
					listlocationFacilityFrom.push(listlocationFacility[x]);
				}
			}
		}
		moveMode = true;
		if (!updateMode) {
			listlocationFacility = _.difference(listlocationFacility, listlocationFacilityFrom);
			clearLocationWasLootAllChild();
		}
		reset();
		renderTreeGridLocationFacility();
		$("#divUpdateProductTo").css("display", "none");
		$("#taskBarHiden").css("display", "none");
		$("#taskBarMove").css("display", "block");
		bootbox.alert("${StringUtil.wrapString(uiLabelMap.ChooseLocationToMove)}");
	});
	
	$("#moveProduct").jqxButton({template: "primary"});
	var mapInventoryFrom;
	var mapInventoryTo;
	var idGridFromActive;
	var idGridToActive;
	$("#moveProduct").click(function () {
		if (arrayLocationId.length == 0) {
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.ChooseLocation)}");
			return;
		}
		moveMode = true;
		updateMode = false;
		arrayLocationIdTo = arrayLocationId;
		renderHtmlMover();
		$('#jqxTabsMoverFrom').jqxTabs({ width: '100%', height: 250, theme: 'olbius'});
		$('#jqxTabsMoverTo').jqxTabs({ width: '100%', height: 250, theme: 'olbius'});
		idGridFromActive = arrayLocationIdFrom[0];
		idGridToActive = arrayLocationIdTo[0];
		$('#jqxTabsMoverFrom').on('selected', function (event) { 
		    var selectedTab = event.args.item;
		    idGridFromActive = arrayLocationIdFrom[selectedTab];
		    activeStartGrid();
		});
		$('#jqxTabsMoverTo').on('selected', function (event) { 
			var selectedTab = event.args.item;
			idGridToActive = arrayLocationIdTo[selectedTab];
			activeStartGrid();
		});
		mapInventoryFrom = getListInventoryItemInLocationWithParam(arrayLocationIdFrom);
		mapInventoryTo = getListInventoryItemInLocationWithParam(arrayLocationIdTo);
		for ( var v in arrayLocationIdFrom) {
			for ( var b in mapInventoryFrom[arrayLocationIdFrom[v]]) {
				mapInventoryFrom[arrayLocationIdFrom[v]][b].expireDate == undefined ? mapInventoryFrom[arrayLocationIdFrom[v]][b].expireDate = null : mapInventoryFrom[arrayLocationIdFrom[v]][b].expireDate = mapInventoryFrom[arrayLocationIdFrom[v]][b].expireDate['time'];
				mapInventoryFrom[arrayLocationIdFrom[v]][b].datetimeReceived == undefined ? mapInventoryFrom[arrayLocationIdFrom[v]][b].datetimeReceived = null : mapInventoryFrom[arrayLocationIdFrom[v]][b].datetimeReceived = mapInventoryFrom[arrayLocationIdFrom[v]][b].datetimeReceived['time'];
			}
			bindDataGridsFrom(arrayLocationIdFrom[v]);
		}
		for ( var v in arrayLocationIdTo) {
			for ( var b in mapInventoryTo[arrayLocationIdTo[v]]) {
				mapInventoryTo[arrayLocationIdTo[v]][b].expireDate == undefined ? mapInventoryTo[arrayLocationIdTo[v]][b].expireDate = null : mapInventoryTo[arrayLocationIdTo[v]][b].expireDate = mapInventoryTo[arrayLocationIdTo[v]][b].expireDate['time'];
				mapInventoryTo[arrayLocationIdTo[v]][b].datetimeReceived == undefined ? mapInventoryTo[arrayLocationIdTo[v]][b].datetimeReceived = null : mapInventoryTo[arrayLocationIdTo[v]][b].datetimeReceived = mapInventoryTo[arrayLocationIdTo[v]][b].datetimeReceived['time'];
			}
			bindDataGridsTo(arrayLocationIdTo[v]);
			handleOldMovevalue(arrayLocationIdTo[v]);
		}
		activeStartGrid();
		var wtmp = window;
    	var tmpwidth = $('#jqxwindowPopupMoverProducts').jqxWindow('width');
        $("#jqxwindowPopupMoverProducts").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
		$("#jqxwindowPopupMoverProducts").jqxWindow('open');
	});
	var idGridFromActiveHtml= "";
	var idGridToActiveHtml = "";
	function activeStartGrid() {
		idGridFromActiveHtml = "jqxGridsFrom" + idGridFromActive;
		idGridToActiveHtml = "jqxGridsTo" + idGridToActive;
		setTimeout(function() {
    		var gridCells = $('#' + idGridFromActiveHtml).find('.dragable');
            gridCells.jqxDragDrop({
                appendTo: 'body',  dragZIndex: 99999,
                dropAction: 'none',
                initFeedback: function (feedback) {
                    feedback.height(25);
                },
                dropTarget: $("#" + idGridToActiveHtml), revert: true
            });
            gridCells.off('dragStart');
            gridCells.off('dragEnd');
            gridCells.off('dropTargetEnter');
            gridCells.off('dropTargetLeave');
            gridCells.on('dropTargetEnter', function () {
                gridCells.jqxDragDrop({ revert: false });
            });
            gridCells.on('dropTargetLeave', function () {
                gridCells.jqxDragDrop({ revert: true });
            });
            gridCells.on('dragStart', function (event) {
                var value = $(this).text();
                var args = event.args;
                
                var position = $.jqx.position(event.args);
                var cell = $("#" + idGridFromActiveHtml).jqxGrid('getcellatposition', position.left, position.top);
                var column = cell.column;
                $(this).jqxDragDrop('data', {
                    value: value
                });
            });
            gridCells.on('dragEnd', function (event) {
                var value = $(this).text();
                var position = $.jqx.position(event.args);
                var cell = $("#" + idGridToActiveHtml).jqxGrid('getcellatposition', position.left, position.top);
                var column = cell.column;
                var oldValue = cell.value;
                if (oldValue) {
					return;
				}
                if (checkHasProductInLocationTo(idGridToActiveHtml)) {
                	var dataRowFrom = getMovedDataRowFrom();
                	
                	var expireDate = dataRowFrom.expireDate;
                	var datetimeReceived = dataRowFrom.datetimeReceived;
                	var inventoryItemId = dataRowFrom.inventoryItemId;
                	$("#" + idGridToActiveHtml).jqxGrid('setcellvalue', 0, 'productId', value);
                	$("#" + idGridToActiveHtml).jqxGrid('setcellvalue', 0, 'datetimeReceived', datetimeReceived);
                	$("#" + idGridToActiveHtml).jqxGrid('setcellvalue', 0, 'expireDate', expireDate);
                	$("#" + idGridToActiveHtml).jqxGrid('setcellvalue', 0, 'inventoryItemId', inventoryItemId);
                	$("#" + idGridToActiveHtml).jqxGrid('begincelledit', 0, "quantity");
				}
            });
		}, 500);
	}
	function checkHasProductInLocationTo(gridId) {
		var dataFrom = getMovedDataRowFrom();
		var inventoryItemId = dataFrom.inventoryItemId;
		var arrayProductAvalible = $('#' + gridId).jqxGrid('getdisplayrows');
		for ( var x in arrayProductAvalible) {
			
			var thisInventoryItemId = arrayProductAvalible[x].inventoryItemId;
			
			if (thisInventoryItemId == undefined) {
				continue;
			}
			if (thisInventoryItemId == inventoryItemId) {
				var boundindex = arrayProductAvalible[x].boundindex;
				$("#" + gridId).jqxGrid('begincelledit', boundindex, "quantity");
				return false;
			}
		}
		return true;
	}
	function getMovedDataRowFrom() {
		if ($('#' + idGridFromActiveHtml).jqxGrid('getselectedcell') == null) {
			return false;
		}
		var rowindex = $('#' + idGridFromActiveHtml).jqxGrid('getselectedcell').rowindex;
		var data = $('#' + idGridFromActiveHtml).jqxGrid('getrowdata', rowindex);
		return data;
	}
	function handleOldMovevalue(gridId) {
		var activeGrid = "jqxGridsTo" + gridId;
		$("#" + activeGrid).on('cellvaluechanged', function (event) {
        		    var args = event.args;
        		    var datafield = event.args.datafield;
        		    var rowBoundIndex = args.rowindex;
        		    var value = args.newvalue;
        		    var	oldvalue = args.oldvalue;
        		    
        		    if (datafield == 'quantity') {
        		    	var data = $('#' + activeGrid).jqxGrid('getrowdata', rowBoundIndex);
        		    	var inventoryItemId = data.inventoryItemId;
	    				if (oldvalue != undefined) {
	    					newQuantity = setQuantityResource(inventoryItemId, oldvalue, true);
	        			}
	    				newQuantity = setQuantityResource(inventoryItemId, value, false);
	    				if (newQuantity < 0) {
							data.quantity = data.quantity + newQuantity;
						}
            		    data.uomId = mapProductWithUom[data.productId];
            		    data.locationId = idGridToActive;
    					refreshDataResoure();
					}
        		    
		});
	}
	function refreshDataResoure() {
		mapInventoryTo[idGridToActive] = removeNullElement($('#' + idGridToActiveHtml).jqxGrid('getboundrows'));
    	setTimeout(function() {
    		bindDataGridsTo(idGridToActive);
		    $('#' + idGridFromActiveHtml).jqxGrid('updatebounddata');
		    activeStartGrid();
		}, 10);
	}
	function bindDataGridsFrom(gridIdFrom) {
		var gridIdFromActive = "jqxGridsFrom" + gridIdFrom;
		var dataInLocationFrom = mapInventoryFrom[gridIdFrom];
		var sourceMoveFrom =
        {
            localdata: dataInLocationFrom,
            datatype: "local",
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' }
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
        var dataAdapterMoveFrom = new $.jqx.dataAdapter(sourceMoveFrom);
        $("#" + gridIdFromActive).jqxGrid({
                source: dataAdapterMoveFrom,
                localization: getLocalization(),
                width: '100%',
                theme: 'olbius',
                selectionmode: 'singlecell',
                height: '100%',
                editable:false,
                sortable: true,
                showfilterrow: true,
                filterable: true,
                columns: [
                   { text: '${uiLabelMap.FormFieldTitle_inventoryItemId}', dataField: 'inventoryItemId', align: 'center', width: 120 },
                   { text: '${uiLabelMap.accProductId}', dataField: 'productId', align: 'center', width: 180, cellclassname: cellclassname },
	               { text: '${uiLabelMap.Quantity}', dataField: 'quantity', align: 'center', width: 150, cellsalign: 'right', columntype:"numberinput", filtertype: 'number',
                	   cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
						}
	               },
	               { text: '${uiLabelMap.DatetimeReceived}', dataField: 'datetimeReceived', align: 'center', width: 170, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
	               { text: '${uiLabelMap.ProductExpireDate}', dataField: 'expireDate', align: 'center', width: 170, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
	               { text: '${uiLabelMap.PackingUnit}', dataField: 'uomId', align: 'center', filtertype: 'checkedlist',
		            	  cellsrenderer: function(row, colum, value){
		    			        var data = $("#" + gridIdFromActive).jqxGrid('getrowdata', row);
		    			        var productId = data.productId;
		    			        return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
		    		      },
		    		      createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(quantityUomData), displayMember: 'quantityUomId', valueMember: 'quantityUomId' ,
								    renderer: function (index, label, value) {
								    	if (index == 0) {
								    		return value;
										}
									    return mapQuantityUom[value];
								    }
								});
								editor.jqxDropDownList('checkAll');
		            	  }
		              }
                ],
                handlekeyboardnavigation: function (event) {
                    var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                    if (key == 70 && event.ctrlKey) {
                    	$("#" + gridIdFromActive).jqxGrid('clearfilters');
                    	return true;
                    }
                }
          });
	}
	
	function bindDataGridsTo(gridIdTo) {
		var gridIdToActive = "jqxGridsTo" + gridIdTo;
		var sourceBlankFirst = [{}];
		var dataInLocationTo = mapInventoryTo[gridIdTo];
		sourceBlankFirst.push.apply(sourceBlankFirst, dataInLocationTo);
		var sourceMoveTo =
        {
            localdata: sourceBlankFirst,
            datatype: "local",
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' }
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
        var dataAdapterMoveTo = new $.jqx.dataAdapter(sourceMoveTo);
        $("#" + gridIdToActive).jqxGrid({
                source: dataAdapterMoveTo,
                localization: getLocalization(),
                width: '100%',
                height: '100%',
                theme: 'olbius',
                selectionmode: 'singlerow',
                editable:true,
                sortable: true,
                columns: [
                   { text: '${uiLabelMap.FormFieldTitle_inventoryItemId}', dataField: 'inventoryItemId', align: 'center', width: 120, editable:false },
                   { text: '${uiLabelMap.accProductId}', dataField: 'productId', align: 'center', width: 180, editable:false },
	               { text: '${uiLabelMap.OrderChooseAmount}', dataField: 'quantity', align: 'center', width: 150, cellsalign: 'right', columntype:'numberinput', filtertype: 'number', 
                	   validation: function (cell, value) {
                		   if (value < 0) {
                			   return { result: false, message: "${StringUtil.wrapString(uiLabelMap.QuantityNotValid)}" };
                		   }
                		   var data = $('#' + gridIdToActive).jqxGrid('getrowdata', cell.row);
           		    	   var inventoryItemId = data.inventoryItemId;
           		    	   var orinalQuantity = getQuantityEquity(inventoryItemId);
                		   var result = validateMovedQuantity(value, orinalQuantity);
                		   return result;
                	    },
                	    cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
						},
						createeditor: function(row, column, editor){
            	    		editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
						},
						cellbeginedit: function (row, datafield, columntype, value) {
							var data = $('#' + gridIdToActive).jqxGrid('getrowdata', row);
           		    	    var inventoryItemId = data.inventoryItemId;
           		    	    var checkHasIventory = getQuantityResource(inventoryItemId);
           		    	    if (checkHasIventory == 0) {
           		    	    	return false;
							} else {
								return true;
							}
			            }
	               },
	               { text: '${uiLabelMap.DatetimeReceived}', dataField: 'datetimeReceived', align: 'center', width: 170, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
	               { text: '${uiLabelMap.ProductExpireDate}', dataField: 'expireDate', align: 'center', width: 170, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
	               { text: '${uiLabelMap.PackingUnit}', dataField: 'uomId', align: 'center', editable:false,
	            	  cellsrenderer: function(row, colum, value){
	    			        var data = $("#" + gridIdToActive).jqxGrid('getrowdata', row);
	    			        var productId = data.productId;
	    			        if (productId == undefined) {
								return null;
							}
	    			        return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
	    		      }
	              }
                ]
          });
	}
	function validateMovedQuantity(value, orinalQuantity) {
		var result = {};
		if (value < orinalQuantity) {
			result.result = false;
			result.message = "${StringUtil.wrapString(uiLabelMap.QuantityNotLessThanCurrentValue)}";
			return result;
		}
		var dataFrom = getMovedDataRowFrom();
		if (!dataFrom) {
			return true;
		}
		var indexRowFrom = dataFrom.uid;
		var inventoryItemId = dataFrom.inventoryItemId;
		var quantityResource = getQuantityResource(inventoryItemId);
		if (value == null || value == "") {
			result.result = false;
			result.message = "${StringUtil.wrapString(uiLabelMap.QuantityNotValid)}";
			return result;
		}
		value = value - orinalQuantity;
		if (value > quantityResource) {
			result.result = true;
		} else {
			result.result = true;
		}
		return result;
	}
	function getQuantityEquity(inventoryItemId) {
		var dataInLocationTo = mapInventoryTo[idGridToActive];
		var orinalQuantity = 0;
		for ( var x in dataInLocationTo) {
			var thisInventoryItemId = dataInLocationTo[x].inventoryItemId;
			if (inventoryItemId == thisInventoryItemId) {
				orinalQuantity = dataInLocationTo[x].quantity;
				return orinalQuantity;
			}
		}
		return orinalQuantity;
	}
	function getOriginalQuantity(gridId, inventoryItemId) {
		var dataInLocationActive = dataInLocation[gridId];
		var originalQuantity = 0;
		for ( var x in dataInLocationActive) {
			var thisInventoryItemId = dataInLocationActive[x].inventoryItemId;
			if (inventoryItemId == thisInventoryItemId) {
				originalQuantity = dataInLocationActive[x].quantity;
				return originalQuantity;
			}
		}
		return originalQuantity;
	}
	function getQuantityResource(inventoryItemId) {
		var dataInLocationFrom = mapInventoryFrom[idGridFromActive];
		for ( var x in dataInLocationFrom) {
			var thisInventoryItemId = dataInLocationFrom[x].inventoryItemId;
			if (inventoryItemId == thisInventoryItemId) {
				var quantity = dataInLocationFrom[x].quantity;
				return quantity;
			}
		}
		return 0;
	}
	function setQuantityResource(inventoryItemId, remain, restore) {
		var newQuantity = 0;
		var dataInLocationFrom = mapInventoryFrom[idGridFromActive];
		for ( var x in dataInLocationFrom) {
			var thisInventoryItemId = dataInLocationFrom[x].inventoryItemId;
			if (inventoryItemId == thisInventoryItemId) {
				if (restore) {
					newQuantity = parseInt(remain) + parseInt(dataInLocationFrom[x].quantity);
					dataInLocationFrom[x].quantity = newQuantity;
				} else {
					newQuantity = parseInt(dataInLocationFrom[x].quantity) - parseInt(remain);
					if (newQuantity < 0) {
						dataInLocationFrom[x].quantity = 0;
					} else {
						dataInLocationFrom[x].quantity = newQuantity;
					}
				}
			}
		}
		return newQuantity;
	}
	$("#btnReset").jqxButton({template: "danger" });
	$("#btnReset").click(function () {
		listlocationFacility = JSON.parse(listlocationFacilityTemp);
		renderTreeGridLocationFacility();
		reset();
		arrayLocationIdFrom = [];
		arrayLocationIdTo = [];
		moveMode = false;
		updateMode = false;
		$("#taskBarMove").css("display", "none");
		if (isNeedReposition) {
			$("#divUpdateProductTo").css("display", "block");
		}
	});
	
	function clearLocationWasLootAllChild() {
		var listLocationWasLootAllChild = [];
		for ( var x in listlocationFacility) {
			var locationId = listlocationFacility[x].locationId;
			if (_.indexOf(allRowsChecked, locationId) != -1) {
				listLocationWasLootAllChild.push(listlocationFacility[x]);
			}
		}
		listlocationFacility = _.difference(listlocationFacility, listLocationWasLootAllChild);
	}
	
	function renderHtmlMover() {
		var moveTabsFrom = "<div id='jqxTabsMoverFrom'><ul>";
		var moveGridsFrom = "";
		for ( var f in arrayLocationIdFrom) {
			var gridId = "jqxGridsFrom" + arrayLocationIdFrom[f];
			moveTabsFrom += "<li>" + getLocationCode(arrayLocationIdFrom[f]) + "</li>";
			moveGridsFrom += "<div style='overflow: hidden;'><div style='border:none;' id=" + gridId + " ></div></div>";
		}
		moveTabsFrom += "</ul>" + moveGridsFrom + "</div>";
		$("#jqxTabsFrom").html(moveTabsFrom);
		
		
		var moveTabsTo ="<div id='jqxTabsMoverTo'><ul>";
		var moveGridsTo = "";
		for ( var t in arrayLocationIdTo) {
			var gridId = "jqxGridsTo" + arrayLocationIdTo[t];
			moveTabsTo += "<li>" + getLocationCode(arrayLocationIdTo[t]) + "</li>";
			moveGridsTo += "<div style='overflow: hidden;'><div style='border:none;' id=" + gridId + " ></div></div>";
		}
		moveTabsTo += "</ul>" + moveGridsTo + "</div>";
		$("#jqxTabsTo").html(moveTabsTo);
	}
	
	$('#jqxwindowPopupViewerProductsInLocations').on('close', function (event) {
		enableScrolling();
		$('#jqxTabsViewer').jqxTabs('destroy');
		reset();
	});
	$("#jqxwindowPopupViewerProductsInLocations").on('open', function (event) {
		disableScrolling();
	});
	$("#jqxwindowPopupViewerProductsInLocations").jqxWindow({theme: 'olbius',
	    width: 950, maxWidth: 1845, minHeight: 465, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelViewerProductsInLocations"), modalOpacity: 0.7
	});
	$("#alterCancelViewerProductsInLocations").jqxButton({template: "danger" });
	$("#viewProduct").jqxButton({template: "primary", height: "30px", width: "200px"});
	$("#viewProduct").click(function () {
		if (arrayLocationId.length == 0) {
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.ChooseLocation)}");
			return;
		}
		renderHtmlGridViewer();
		updateMode = false;
		moveMode = false;
		$('#jqxTabsViewer').jqxTabs({ width: '100%', height: 350, theme: 'olbius'});
		dataInLocation = getListInventoryItemInLocation();
		for ( var s in arrayLocationId) {
			for ( var d in dataInLocation[arrayLocationId[s]]) {
				dataInLocation[arrayLocationId[s]][d].expireDate == undefined?dataInLocation[arrayLocationId[s]][d].expireDate = null : dataInLocation[arrayLocationId[s]][d].expireDate = dataInLocation[arrayLocationId[s]][d].expireDate['time'];
				dataInLocation[arrayLocationId[s]][d].datetimeReceived == undefined?dataInLocation[arrayLocationId[s]][d].datetimeReceived = null : dataInLocation[arrayLocationId[s]][d].datetimeReceived = dataInLocation[arrayLocationId[s]][d].datetimeReceived['time'];
			}
			bindDataGridViewer(arrayLocationId[s]);
		}
		var wtmp = window;
    	var tmpwidth = $('#jqxwindowPopupViewerProductsInLocations').jqxWindow('width');
        $("#jqxwindowPopupViewerProductsInLocations").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
		$("#jqxwindowPopupViewerProductsInLocations").jqxWindow('open');
	});
	function renderHtmlGridViewer() {
		var htmlRenderTabs = "<div id='jqxTabsViewer'><ul>";
		var htmlRenderGrids = "";
		for ( var x in arrayLocationId) {
			var gridId = "jqxGridViewer" + arrayLocationId[x];
	        htmlRenderTabs += "<li>" + getLocationCode(arrayLocationId[x]) + "</li>";
	        htmlRenderGrids += "<div style='overflow: hidden;'><div style='border:none;' id=" + gridId + " ></div></div>";
		}
		htmlRenderTabs += "</ul>" + htmlRenderGrids + "</div>";
		$("#jqxTabsViewerProductsInLocations").html(htmlRenderTabs);
	}
	function bindDataGridViewer(gridId) {
		var activeGrid = "jqxGridViewer" + gridId;
		var dataViewer = dataInLocation[gridId];
		var sourceViewer =
        {
            localdata: dataViewer,
            datatype: "local",
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' }
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
        var dataAdapterViewer = new $.jqx.dataAdapter(sourceViewer);
        $("#" + activeGrid).jqxGrid({
                source: dataAdapterViewer,
                localization: getLocalization(),
                width: '100%',
                theme: 'olbius',
                selectionmode: 'singlerow',
                height: '100%',
                editable:false,
                sortable: true,
                pageable: true,
                columns: [
                   { text: '${uiLabelMap.FormFieldTitle_inventoryItemId}', dataField: 'inventoryItemId', align: 'center', width: 120, editable:false },
                   { text: '${uiLabelMap.accProductId}', dataField: 'productId', align: 'center', width: 180, editable:false },
	               { text: '${uiLabelMap.Quantity}', dataField: 'quantity', align: 'center', width: 150, cellsalign: 'right', columntype:"numberinput", filtertype: 'number', 
                	   validation: function (cell, value) {
                		   var result = validateQuantity(value);
                		   return result;
                	    },
                	    cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
						}
	               },
	               { text: '${uiLabelMap.DatetimeReceived}', dataField: 'datetimeReceived', align: 'center', width: 170, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
	               { text: '${uiLabelMap.ProductExpireDate}', dataField: 'expireDate', align: 'center', width: 170, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
	               { text: '${uiLabelMap.PackingUnit}', dataField: 'uomId', align: 'center', filtertype: 'checkedlist', editable:false,
						cellsrenderer: function(row, colum, value){
					        var data = $("#" + activeGrid).jqxGrid('getrowdata', row);
							var productId = data.productId;
							return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(quantityUomData), displayMember: 'quantityUomId', valueMember: 'quantityUomId' ,
							    renderer: function (index, label, value) {
							    	if (index == 0) {
							    		return value;
									}
								    return mapQuantityUom[value];
							    }
							});
							editor.jqxDropDownList('checkAll');
						}
	              }
                ]
          });
	}
	
	function getAllProductNotLocationAjax() {
		var listProductNotLocation = [];
		$.ajax({
			url: "getAllProductNotLocationAjax",
			type: "POST",
			data: {facilityId: facilityIdGlobal},
			dataType: "json",
			async: false,
			success: function(res) {
				listProductNotLocation = res["listProductNotLocation"];
			}
		}).done(function() {
			renderAddProduct(listProductNotLocation);
		});
	}
	function checkProductNotLocationAjax() {
		var result = false;
		$.ajax({
			url: "checkProductNotLocationAjax",
			type: "POST",
			data: {facilityId: facilityIdGlobal},
			dataType: "json",
			async: false,
			success: function(res) {
				result = res["result"];
			}
		});
		return result;
	}
	var idGridActive;
	var listProductNotLocationTemp = [];
	var dataInLocation;
	function renderAddProduct(listProductNotLocation) {
		for ( var d in listProductNotLocation) {
			listProductNotLocation[d].expireDate == undefined?listProductNotLocation[d].expireDate = null : listProductNotLocation[d].expireDate = listProductNotLocation[d].expireDate['time'];
			listProductNotLocation[d].datetimeReceived == undefined?listProductNotLocation[d].datetimeReceived = null : listProductNotLocation[d].datetimeReceived = listProductNotLocation[d].datetimeReceived['time'];
		}
		updateMode = false;
		moveMode = false;
		dataInLocation = getListInventoryItemInLocation();
		renderHtmlContainGrids();
		$('#jqxTabsLocation').jqxTabs({ width: '100%', height: 250, theme: 'olbius'});
		idGridActive = arrayLocationId[0];
		$('#jqxTabsLocation').on('selected', function (event) { 
		    var selectedTab = event.args.item;
		    idGridActive = arrayLocationId[selectedTab];
		    changeTargetWhenGridActive();
		});
		
		for ( var s in arrayLocationId) {
			for ( var d in dataInLocation[arrayLocationId[s]]) {
				dataInLocation[arrayLocationId[s]][d].expireDate == undefined?dataInLocation[arrayLocationId[s]][d].expireDate = null : dataInLocation[arrayLocationId[s]][d].expireDate = dataInLocation[arrayLocationId[s]][d].expireDate['time'];
				dataInLocation[arrayLocationId[s]][d].datetimeReceived == undefined?dataInLocation[arrayLocationId[s]][d].datetimeReceived = null : dataInLocation[arrayLocationId[s]][d].datetimeReceived = dataInLocation[arrayLocationId[s]][d].datetimeReceived['time'];
			}
			bindDataGridTo(arrayLocationId[s]);
			handleOldvalue(arrayLocationId[s]);
		}
		changeTargetWhenGridActive();
		var source =
        {
            localdata: listProductNotLocation,
            datafields:
            [
                { name: 'productId', type: 'string' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'quantityOnHandTotal', type: 'number' },
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'inventoryItemId', type: 'string' },
                { name: 'uomId', type: 'string' }
            ],
        };
		listProductNotLocationTemp = listProductNotLocation;
		var dataAdapter = new $.jqx.dataAdapter(source);
        $("#gridFrom").jqxGrid({
            source: dataAdapter,
            localization: getLocalization(),
            showfilterrow: true,
            filterable: true,
            width: '100%',
            height: 200,
            theme: 'olbius',
            selectionmode: 'singlecell',
            sortable: true,
            columns: [
              { text: '${uiLabelMap.FormFieldTitle_inventoryItemId}', dataField: 'inventoryItemId', align: 'center', width: 120, editable:false },
              { text: '${uiLabelMap.accProductId}', dataField: 'productId', align: 'center', width: 180 , cellclassname: cellclassname },
              { text: '${uiLabelMap.ATPTotal}', dataField: 'quantityOnHandTotal', align: 'center', filtertype: 'number', width: 150, cellsalign: 'right',
            	  cellsrenderer: function(row, colum, value){
						return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
            	  }
              },
              { text: '${uiLabelMap.DatetimeReceived}', dataField: 'datetimeReceived', align: 'center', width: 170, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
              { text: '${uiLabelMap.ProductExpireDate}', dataField: 'expireDate', align: 'center', width: 170, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
              { text: '${uiLabelMap.PackingUnit}', dataField: 'uomId', align: 'center', filtertype: 'checkedlist',
            	  cellsrenderer: function(row, colum, value){
    			        var data = $('#gridFrom').jqxGrid('getrowdata', row);
    			        var productId = data.productId;
    			        return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
            	  },
            	  createfilterwidget: function (column, htmlElement, editor) {
						editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(quantityUomData), displayMember: 'quantityUomId', valueMember: 'quantityUomId' ,
						    renderer: function (index, label, value) {
						    	if (index == 0) {
						    		return value;
								}
							    return mapQuantityUom[value];
						    }
						});
						editor.jqxDropDownList('checkAll');
            	  }
              }
            ],
            handlekeyboardnavigation: function (event) {
                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                if (key == 70 && event.ctrlKey) {
                	$('#gridFrom').jqxGrid('clearfilters');
                	return true;
                }
            },
        });
        var wtmp = window;
    	var tmpwidth = $('#jqxwindowPopupAdderProductToLocation').jqxWindow('width');
        $("#jqxwindowPopupAdderProductToLocation").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
        $("#jqxwindowPopupAdderProductToLocation").jqxWindow('open');
        $('#alterSaveAdderProductToLocation').jqxButton({disabled: false });
	}
	
	function renderHtmlContainGrids() {
		var htmlRenderTabs = "<div id='jqxTabsLocation'><ul id='tabDynamic'>";
		var htmlRenderGrids = "";
		for ( var x in arrayLocationId) {
			var gridId = "jqxGrid" + arrayLocationId[x];
	        htmlRenderTabs += "<li value=" + arrayLocationId[x] + ">" + getLocationCode(arrayLocationId[x]) + "</li>";
	        htmlRenderGrids += "<div style='overflow: hidden;'><div style='border:none;' id=" + gridId + " ></div></div>";
		}
		htmlRenderTabs += "</ul>" + htmlRenderGrids + "</div>";
		$("#jqxTabsContain").html(htmlRenderTabs);
	}
	function changeTargetWhenGridActive() {
		var activeGrid = "jqxGrid" + idGridActive;
		setTimeout(function() {
    		var gridCells = $('#gridFrom').find('.dragable');
            gridCells.jqxDragDrop({
                appendTo: 'body',  dragZIndex: 99999,
                dropAction: 'none',
                initFeedback: function (feedback) {
                    feedback.height(25);
                },
                dropTarget: $("#" + activeGrid), revert: true
            });
            gridCells.off('dragStart');
            gridCells.off('dragEnd');
            gridCells.off('dropTargetEnter');
            gridCells.off('dropTargetLeave');
            gridCells.on('dropTargetEnter', function () {
                gridCells.jqxDragDrop({ revert: false });
            });
            gridCells.on('dropTargetLeave', function () {
                gridCells.jqxDragDrop({ revert: true });
            });
            gridCells.on('dragStart', function (event) {
                var value = $(this).text();
                var args = event.args;
                
                var position = $.jqx.position(event.args);
                var cell = $("#gridFrom").jqxGrid('getcellatposition', position.left, position.top);
                var column = cell.column;
                $(this).jqxDragDrop('data', {
                    value: value
                });
            });
            gridCells.on('dragEnd', function (event) {
                var value = $(this).text();
                var position = $.jqx.position(event.args);
                var cell = $("#" + activeGrid).jqxGrid('getcellatposition', position.left, position.top);
                var column = cell.column;
                var oldValue = cell.value;
                if (oldValue) {
					return;
				}
                if (checkHasProductInLocation(activeGrid)) {
                	var dataRowFrom = getDataRowFrom();
                	var expireDate = dataRowFrom.expireDate;
                	var datetimeReceived = dataRowFrom.datetimeReceived;
                	var inventoryItemId = dataRowFrom.inventoryItemId;
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'productId', value);
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'datetimeReceived', datetimeReceived);
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'expireDate', expireDate);
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'inventoryItemId', inventoryItemId);
                	$("#" + activeGrid).jqxGrid('begincelledit', 0, "quantity");
				}
            });
		}, 500);
	}
	
	function checkHasProductInLocation(gridId) {
		var dataFrom = getDataRowFrom();
		var inventoryItemId = dataFrom.inventoryItemId;
		var arrayProductAvalible = $('#' + gridId).jqxGrid('getdisplayrows');
		for ( var x in arrayProductAvalible) {
			var thisInventoryItemId = arrayProductAvalible[x].inventoryItemId;
			if (thisInventoryItemId == undefined) {
				continue;
			}
			if (thisInventoryItemId == inventoryItemId) {
				var boundindex = arrayProductAvalible[x].boundindex;
				$("#" + gridId).jqxGrid('begincelledit', boundindex, "quantity");
				return false;
			}
		}
		return true;
	}
	function getListInventoryItemInLocation() {
		var mapInventoryItemInLocation;
		$.ajax({
  		  url: "getListInventoryItemInLocation",
  		  type: "POST",
  		  data: {arrayLocationId: arrayLocationId},
  		  dataType: "json",
  		  async: false,
  		  success: function(res) {
  			mapInventoryItemInLocation = res["mapInventoryItemInLocation"];
  		  }
	  	 });
		return mapInventoryItemInLocation;
	}
	function getListInventoryItemInLocationWithParam(dataLocation) {
		var mapInventoryItemInLocation;
		$.ajax({
			url: "getListInventoryItemInLocation",
			type: "POST",
			data: {arrayLocationId: dataLocation},
			dataType: "json",
			async: false,
			success: function(res) {
				mapInventoryItemInLocation = res["mapInventoryItemInLocation"];
			}
		});
		return mapInventoryItemInLocation;
	}
	function getDataRowFrom() {
		if ($('#gridFrom').jqxGrid('getselectedcell') == null) {
			return false;
		}
		var rowindex = $('#gridFrom').jqxGrid('getselectedcell').rowindex;
		var data = $('#gridFrom').jqxGrid('getrowdata', rowindex);
		return data;
	}
	function checkQuantityResource(inventoryItemId) {
		var rows = $('#gridFrom').jqxGrid('getboundrows');
		var quantityOnHandTotal = 0;
		for ( var x in rows) {
			var thisInventoryItemId = rows[x].inventoryItemId;
			if (thisInventoryItemId == inventoryItemId) {
				return rows[x].quantityOnHandTotal;
			}
		}
		return quantityOnHandTotal;
	}
	function validateQuantity(value) {
		var result = {};
		var dataFrom = getDataRowFrom();
		if (!dataFrom) {
			return true;
		}
		var indexRowFrom = dataFrom.uid;
		var inventoryItemId = dataFrom.inventoryItemId;
		
		var quantityOnHandTotal = getQuantityOnHandTotal(inventoryItemId);
		if (value == null || value == "") {
			result.result = false;
			result.message = "${StringUtil.wrapString(uiLabelMap.QuantityNotValid)}";
			return result;
		}
		quantityOnHandTotal = oldValueAdd + quantityOnHandTotal;
		if (value > quantityOnHandTotal) {
			result.result = true;
		} else {
			result.result = true;
		}
		return result;
	}
	
	function rotateElementsArray(arrayOrinal) {
		var out = [];
		for (var i = arrayOrinal.length - 1; i >= 0; i--) {
			out.push(arrayOrinal[i]);
		}
		return out;
	}
	function getQuantityOnHandTotal(inventoryItemId) {
		for ( var x in listProductNotLocationTemp) {
			var thisInventoryItemId = listProductNotLocationTemp[x].inventoryItemId;
			if (inventoryItemId == thisInventoryItemId) {
				var quantityOnHandTotal = listProductNotLocationTemp[x].quantityOnHandTotal;
				return quantityOnHandTotal;
			}
		}
		return 0;
	}
	function setQuantityOnHandTotal(inventoryItemId, remain, restore) {
		var newQuantity = 0;
		for ( var x in listProductNotLocationTemp) {
			var thisInventoryItemId = listProductNotLocationTemp[x].inventoryItemId;
			if (inventoryItemId == thisInventoryItemId) {
				if (restore) {
					newQuantity = parseInt(remain) + parseInt(listProductNotLocationTemp[x].quantityOnHandTotal);
					listProductNotLocationTemp[x].quantityOnHandTotal = newQuantity;
					
				} else {
					newQuantity = parseInt(listProductNotLocationTemp[x].quantityOnHandTotal) - parseInt(remain);
					if (newQuantity < 0) {
						listProductNotLocationTemp[x].quantityOnHandTotal = 0;
					}else {
						listProductNotLocationTemp[x].quantityOnHandTotal = newQuantity;
					}
				}
			}
		}
		return newQuantity;
	}
	var isChange = true;
	function bindDataGridTo(gridId) {
		var activeGrid = "jqxGrid" + gridId;
		isChange = false;
		var sourceBlankFirst = [{}];
		var dataInLocationActive = dataInLocation[gridId];
//		dataInLocationActive = rotateElementsArray(dataInLocationActive);
		sourceBlankFirst.push.apply(sourceBlankFirst, dataInLocationActive);
		var sourceTo =
        {
            localdata: sourceBlankFirst,
            datatype: "local",
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' },
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
        var dataAdapterTo = new $.jqx.dataAdapter(sourceTo);
        $("#" + activeGrid).jqxGrid(
                {
                    source: dataAdapterTo,
                    width: '100%',
                    height: '100%',
                    theme: 'olbius',
                    selectionmode: 'singlerow',
                    editable:true,
                    sortable: true,
                    columns: [
                       { text: '${uiLabelMap.FormFieldTitle_inventoryItemId}', dataField: 'inventoryItemId', align: 'center', width: 120, editable:false },
                       { text: '${uiLabelMap.accProductId}', dataField: 'productId', align: 'center', width: 180, editable:false },
    	               { text: '${uiLabelMap.OrderChooseAmount}', dataField: 'quantity', align: 'center', width: 150, cellsalign: 'right', columntype:"numberinput", 
                    	   validation: function (cell, value) {
                    		   if (value < 0) {
                    			   return { result: false, message: "${StringUtil.wrapString(uiLabelMap.QuantityNotValid)}" };
                    		   }
              		    	   var inventoryItemId = $('#' + activeGrid).jqxGrid('getcellvalue', cell.row, "inventoryItemId");
              		    	   var originalQuantity = getOriginalQuantity(gridId, inventoryItemId);
              		    	   if (value < originalQuantity) {
              		    		   return { result: false, message: "${StringUtil.wrapString(uiLabelMap.QuantityNotLessThanCurrentValue)}" };
              		    	   }
                    		   var result = validateQuantity(value);
                    		   return result;
                    	    }, createeditor: function(row, column, editor){
                    	    		editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
							}, cellsrenderer: function(row, colum, value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
							},
							cellbeginedit: function (row, datafield, columntype, value) {
								var data = $('#' + activeGrid).jqxGrid('getrowdata', row);
               		    	    var inventoryItemId = data.inventoryItemId;
               		    	    var checkHasIventory = checkQuantityResource(inventoryItemId);
               		    	    if (checkHasIventory == 0) {
               		    	    	return false;
								} else {
									return true;
								}
				            }
    	               },
    	               { text: '${uiLabelMap.DatetimeReceived}', dataField: 'datetimeReceived', align: 'center', width: 170, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
    	               { text: '${uiLabelMap.ProductExpireDate}', dataField: 'expireDate', align: 'center', width: 170, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
    	               { text: '${uiLabelMap.PackingUnit}', dataField: 'uomId', align: 'center', editable:false,
 		            	  cellsrenderer: function(row, colum, value){
 		    			        var data = $("#" + activeGrid).jqxGrid('getrowdata', row);
 		    			        var productId = data.productId;
 		    			        if (productId == undefined) {
									return null;
								}
 		    			        return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
 		    		        }  
 		              }
                    ]
          });
	}
	var oldValueAdd = 0;
	function handleOldvalue(gridId) {
		var activeGrid = "jqxGrid" + gridId;
		$("#" + activeGrid).on('cellvaluechanged', function (event) {
        		    var args = event.args;
        		    var datafield = event.args.datafield;
        		    var rowBoundIndex = args.rowindex;
        		    var value = args.newvalue;
        		    var	oldvalue = args.oldvalue;
        		    oldValueAdd = oldvalue;
        		    if (datafield == 'quantity') {
        		    	var data = $('#' + activeGrid).jqxGrid('getrowdata', rowBoundIndex);
        		    	var inventoryItemId = data.inventoryItemId;
	    				if (oldvalue != undefined) {
	    					newQuantity= setQuantityOnHandTotal(inventoryItemId, oldvalue, true);
	        			}
	    				newQuantity = setQuantityOnHandTotal(inventoryItemId, value, false);
	    				if (newQuantity < 0) {
	    					data.quantity = data.quantity + newQuantity;
						}
            		    data.uomId = mapProductWithUom[data.productId];
            		    data.locationId = idGridActive;
    					executeData($('#' + activeGrid).jqxGrid('getboundrows'));
					}
        		    
		});
		$("#" + activeGrid).on('cellbeginedit', function (event) 
				{
				    var args = event.args;
				    var dataField = event.args.datafield;
				    var rowBoundIndex = event.args.rowindex;
				    oldValueAdd = args.value;
				});
	}
	function executeData(data) {
		dataInLocation[idGridActive] = removeNullElement(data);

    	setTimeout(function() {
	    	bindDataGridTo(idGridActive);
		    $('#gridFrom').jqxGrid('updatebounddata');
		    changeTargetWhenGridActive();
		}, 10);
	}
	function removeNullElement(data) {
		var out = [];
		for ( var x in data) {
			if (data[x].productId != undefined) {
				out.push(data[x]);
			}
		}
		return out;
	}
	$('#jqxwindowPopupAdderProductToLocation').on('close', function (event) {
		$('#jqxTabsLocation').jqxTabs('destroy');
		reset();
		enableScrolling();
	});
	$("#jqxwindowPopupAdderProductToLocation").on('open', function (event) {
		disableScrolling();
	});
	$("#jqxwindowPopupAdderProductToLocation").jqxWindow({theme: 'olbius',
	    width: 950, maxWidth: 1845, minHeight: 565, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelAdderProductToLocation"), modalOpacity: 0.7
	});
	$("#alterCancelAdderProductToLocation").jqxButton({template: "danger", height: "30px", width: "70px" });
	$("#alterSaveAdderProductToLocation").jqxButton({template: "primary", height: "30px", width: "70px" });
	$("#alterSaveAdderProductToLocation").click(function () {
		var totalRecord = new Array();
		$('#alterSaveAdderProductToLocation').jqxButton({disabled: true });
		for ( var x in arrayLocationId) {
			totalRecord.push.apply(totalRecord, dataInLocation[arrayLocationId[x]]);
		}
		for ( var z in totalRecord) {
			if (typeof totalRecord[z].expireDate == 'object') {
				totalRecord[z].expireDate = totalRecord[z].expireDate.getTime();
			}
		}
		saveDataToLocation(totalRecord);
		$("#jqxwindowPopupAdderProductToLocation").jqxWindow('close');
	});
	function saveDataToLocation(totalRecord) {
		var result;
		$.ajax({
  		  url: "addToLocationAjax",
  		  type: "POST",
  		  data: {totalRecord: JSON.stringify(totalRecord)},
  		  dataType: "json",
  		  async: true,
  		  success: function(res) {
  			  result = res["RESULT_MESSAGE"];
  		  }
	  	}).done(function() {
	  		if (updateMode) {
				location.reload();
			}
	  		getListlocationFacility(facilityIdGlobal);
	  		getGeneralQuantity();
	  		checkHasInventoryInLocation();
	  		if (checkProductNotLocationAjax()) {
				$("#divHasProductsNotLocation").text("${StringUtil.wrapString(uiLabelMap.HasProductsNotLocation)}");
			}else {
				$("#divHasProductsNotLocation").text("");
			}
	  		$('#jqxNotificationNested').jqxNotification('closeLast');
	  		if (result == "SUSSESS") {
	  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
              	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
              	$("#jqxNotificationNested").jqxNotification("open");
              	isNeedReposition = false;
			} else {
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			}
	  	});
	}
	
	$("#addProduct").jqxButton({template: "primary", height: "30px", width: "200px" });
	$("#addProduct").click(function () {
		if (arrayLocationId.length == 0) {
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.ChooseLocation)}");
			return;
		}
		getAllProductNotLocationAjax();
	});
	$("#updateProductTo").jqxButton({template: "primary", height: "30px", width: "200px" });
	var updateMode = false;
	$("#updateProductTo").click(function () {
		updateMode = true;
		arrayLocationHasProductNeedReposition = _.uniq(arrayLocationHasProductNeedReposition);
		arrayLocationId = arrayLocationHasProductNeedReposition;
		$("#moveProductTo").click();
	});
	
	$("#btnCancelReset").jqxButton({template: "danger", height: "30px", width: "70px" });
	$("#btnCancelReset").click(function () {
		moveMode = false;
		updateMode = false;
		reset();
	});
	
	function reset() {
		arrayLocationId = [];
		arrayRowChecked = [];
		allRowsChecked = [];
		$("#treeGrid").jqxTreeGrid('updateBoundData');
		$("#taskBarHiden").css("display", "block");
	}
	var arrayLocationId = [];
	function refreshLocationId() {
		arrayLocationId = [];
		for ( var x in arrayRowChecked) {
			var thisLocationId = arrayRowChecked[x].locationId;
			arrayLocationId.push(thisLocationId);
		}
		arrayLocationId = _.uniq(arrayLocationId);
		if (moveMode) {
			if (arrayLocationId.length == 0) {
				$("#taskBarMove").css("display", "block");
			}else {
				$("#taskBarMove").css("display", "block");
			}
		}
	}
	var arrayRowChecked = [];
	var allRowsChecked = [];
	$('#treeGrid').on('rowCheck', function (event) {
		var args = event.args;
	    var row = args.row;
	    var key = args.key;
	    var locationId = row.locationId;
	    if (checkHasChild(locationId)) {
	    	arrayRowChecked.push(row);
	    	refreshLocationId();
		}
	    allRowsChecked.push(locationId);
	    allRowsChecked = _.uniq(allRowsChecked);
	    if (!moveMode) {
	    	$("#taskBarHiden").css("display", "block");
	    	$("#divUpdateProductTo").css("display", "none");
		}
	});
	$('#treeGrid').on('rowUncheck', function (event) {
		var row = args.row;
		var idex = arrayRowChecked.indexOf(row);
		
		 var locationId = row.locationId;
		    if (checkHasChild(locationId)) {
		    	arrayRowChecked.splice(idex, 1);
		    	refreshLocationId();
			}
		var idexAll = allRowsChecked.indexOf(locationId);
		allRowsChecked.splice(idexAll, 1);
	});
	
	$("#jqxwindowPopupAdderFacilityLocationArea").jqxWindow({theme: 'olbius',
	    width: 780, maxWidth: 1845, minHeight: 500, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelAdderFacilityLocationArea"), modalOpacity: 0.7
	});
	$("#jqxwindowPopupAdderFacilityLocationArea").on('open', function (event) {
		disableScrolling();
	});
	$("#jqxwindowPopupAdderFacilityLocationArea").on('close', function (event) {
		enableScrolling();
		$('#jqxwindowPopupAdderFacilityLocationArea').jqxValidator('hide');
	});
	$("#alterSaveAdderFacilityLocationArea").jqxButton({template: "primary"});
	$("#alterCancelAdderFacilityLocationArea").jqxButton({template: "danger" });
	$("#alterSaveAdderFacilityLocationArea").click(function () {
		if ($('#jqxwindowPopupAdderFacilityLocationArea').jqxValidator('validate')) {
			var data = {};
			var description = $("#tarDescription").val().replaceAll("<div><br></div>","");
			description = description.trim();
			$('#alterSaveAdderFacilityLocationArea').jqxButton({disabled: true });
			data.facilityId = facilityIdGlobal;
			data.parentLocationId = null;
			data.locationCode = $("input[name='txtLocationCode']").val();
			data.description = description;
			data.locationFacilityTypeId = $("#txtLocationType").val();
			$("#jqxwindowPopupAdderFacilityLocationArea").jqxWindow('close');
			$("#treeGrid").jqxTreeGrid('addRow', null, data, 'last');
		}
	});
	
	$('#jqxwindowPopupAdderFacilityLocationArea').jqxValidator({
        rules: [
					{ input: '#txtLocationType', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#txtLocationType").val();
							if (value) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtLocationCode', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#tarDescription', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#tarDescription").val();
							if (value == "<div>​</div>" || value == "<br>" || value == "") {
								return false;
							}
							return true;
						}
					},
					{ input: '#txtLocationCode', message: '${StringUtil.wrapString(uiLabelMap.DuplicateLocationCode)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtLocationCode").val();
							if (_.indexOf(arrayLocationCodeAvalible, value) != -1) {
								return false;
							}
							return true;
						}
					},
					{ input: '#txtLocationCode', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtLocationCode").val();
							if (!value.containSpecialChars()) {
								return true;
							}
							return false;
						}
					}
               ]
    });
	
	function addFacilityLocationArea() {
		clearPopup();
		$("input[name='txtFacility']").val(facilityIdGlobal);
		$('#alterSaveAdderFacilityLocationArea').jqxButton({disabled: false });
		$("#txtLocationType").jqxDropDownList({ source: arrayLocationTypeParent, selectedIndex: 0, displayMember: 'description', valueMember: 'locationFacilityTypeId', autoDropDownHeight: true , width: '218', height: '25', theme: 'olbius'});
		var wtmp = window;
    	var tmpwidth = $('#jqxwindowPopupAdderFacilityLocationArea').jqxWindow('width');
        $("#jqxwindowPopupAdderFacilityLocationArea").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
		$("#jqxwindowPopupAdderFacilityLocationArea").jqxWindow('open');
		$('#tarDescription').jqxEditor({
	        theme: 'olbiuseditor',
	        width: '96%'
	    });
	}
	$("#jqxwindowPopupAdderFacilityLocationAreaInArea").on('open', function (event) {
		disableScrolling();
	});
	$("#jqxwindowPopupAdderFacilityLocationAreaInArea").on('close', function (event) {
		enableScrolling();
		$('#jqxwindowPopupAdderFacilityLocationAreaInArea').jqxValidator('hide');
	});
	$("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow({theme: 'olbius',
		width: 780, maxWidth: 1845, minHeight: '500px', resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelAdderFacilityLocationAreaInArea"), modalOpacity: 0.7
	});
	$("#alterSaveAdderFacilityLocationAreaInArea").jqxButton({template: "primary"});
	$("#alterCancelAdderFacilityLocationAreaInArea").jqxButton({template: "danger" });
	
	$('#jqxwindowPopupAdderFacilityLocationAreaInArea').jqxValidator({
        rules: [
					{ input: '#txtLocationCodeInArea', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#tarDescriptionInArea', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#tarDescriptionInArea").val();
							if (value == "<div>​</div>" || value == "<br>" || value == "") {
								return false;
							}
							return true;
						}
					},
					{ input: '#txtLocationCodeInArea', message: '${StringUtil.wrapString(uiLabelMap.DuplicateLocationCode)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtLocationCodeInArea").val();
							if (_.indexOf(arrayLocationCodeAvalible, value) != -1) {
								return false;
							}
							return true;
						}
					},
					{ input: '#txtLocationCodeInArea', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtLocationCodeInArea").val();
							if (!value.containSpecialChars()) {
								return true;
							}
							return false;
						}
					}
               ]
    });
	
	
	var mapHasInventoryInLocation;
	function checkHasInventoryInLocation() {
		mapHasInventoryInLocation = null;
		$.ajax({
  		  url: "checkHasInventoryInLocationAjax",
  		  type: "POST",
  		  data: {facilityId: facilityIdGlobal},
  		  dataType: "json",
  		  async: false,
  		  success: function(res) {
  			mapHasInventoryInLocation = res["result"];
  		  }
	  	 });
	}
	
	var locationIdGlobal = null;
	var warningMode = false;
	function addFacilityLocationAreaInArea(locationId, parentLocationId, typeInsert) {
		$("#jqxwindowPopupAdderFacilityLocationAreaInArea").attr("typeInsert", typeInsert);
		var path = getPathArea(locationId, parentLocationId, "");
		$("#tarMapArea").text(path);
		clearPopup();
		locationIdGlobal = locationId;
		$("#txtOrderNote").text("");
		var height = "500px";
		if (mapHasInventoryInLocation[locationId]) {
			$("#txtOrderNote").text("${StringUtil.wrapString(uiLabelMap.StatusLocationHasItems)}.");
			$("#divOrderNotes").css("display", "block");
			height = "540px";
			warningMode = true;
		}else {
			$("#txtOrderNote").text("");
			$("#divOrderNotes").css("display", "none");
			warningMode = false;
		}
		$("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow({height: height });
		var wtmp = window;
    	var tmpwidth = $('#jqxwindowPopupAdderFacilityLocationAreaInArea').jqxWindow('width');
        $("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
		$("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow('open');
		$('#alterSaveAdderFacilityLocationAreaInArea').jqxButton({disabled: false });
		$('#tarDescriptionInArea').jqxEditor({
	        theme: 'olbiuseditor',
	        width: '96%'
	    });
	}
	$("#alterSaveAdderFacilityLocationAreaInArea").click(function () {
		if ($('#jqxwindowPopupAdderFacilityLocationAreaInArea').jqxValidator('validate')) {
			var data = {};
			var description = $("#tarDescriptionInArea").val().replaceAll("<div><br></div>","");
			description = description.trim();
			var typeInsert = $("#jqxwindowPopupAdderFacilityLocationAreaInArea").attr("typeInsert");
			$('#alterSaveAdderFacilityLocationAreaInArea').jqxButton({disabled: true });
			data.facilityId = facilityIdGlobal;
			data.parentLocationId = locationIdGlobal;
			data.locationCode = $("input[name='txtLocationCodeInArea']").val();
			data.description = description;
			data.locationFacilityTypeId = typeInsert;
			$("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow('close');
			$("#treeGrid").jqxTreeGrid('addRow', null, data, 'last', rowKey);
		}
	});
	function getPathArea(locationId, parentLocationId, path) {
		if (parentLocationId == 'null' || parentLocationId == null) {
			path = getLocationCode(locationId);
		}else {
			for ( var x in listlocationFacility) {
				var thisLocationId = listlocationFacility[x].locationId;
				if (thisLocationId == parentLocationId) {
					var thisParentLocationId = listlocationFacility[x].parentLocationId;
					path = getLocationCode(locationId);
					path = getPathArea(thisLocationId, thisParentLocationId, path) + " - " + path;
					break;
				}
			}
		}
		
		return path;
	}
	function getLocationCode(locationId) {
		var locationCode = "";
		var originalList = [];
		if (listlocationFacilityTemp.length == 0) {
			originalList = listlocationFacility;
		} else {
			 originalList = JSON.parse(listlocationFacilityTemp);
		}
		
		for ( var x in originalList) {
			var thisLocationId = originalList[x].locationId;
			if (thisLocationId == locationId) {
				locationCode = originalList[x].locationCode;
			}
		}
		return locationCode;
	}
	function getParentLocation(locationId) {
		var parentLocationId = "";
		var originalList = [];
		if (listlocationFacilityTemp.length == 0) {
			originalList = listlocationFacility;
		} else {
			 originalList = JSON.parse(listlocationFacilityTemp);
		}
		for ( var x in originalList) {
			var thisLocationId = originalList[x].locationId;
			if (locationId == thisLocationId) {
				parentLocationId = originalList[x].parentLocationId;
				return parentLocationId;
			}
		}
	}
	var rowKey = null;
    $("#treeGrid").on('rowSelect', function (event) {
        var args = event.args;
        rowKey = args.key;
    });
	$("#treeGrid").on('contextmenu', function (e) {
	    return false;
	});
    $("#treeGrid").on('rowClick', function (event) {
        var args = event.args;
        if (args.originalEvent.button == 2) {
            setTimeout(function() {
            	renderMenu(event);
			}, 0);
            return false;
        }
    });
    var contextMenu;
    function renderMenu(event) {
		if (contextMenu)contextMenu.jqxMenu('close');
    	
    	var scrollTop = $(window).scrollTop();
        var scrollLeft = $(window).scrollLeft();
        var selection = $("#treeGrid").jqxTreeGrid('getSelection');
    	if (selection.length != 0) {
    		menuHeight = '58px';
        	var locationFacilityTypeId = selection[selection.length - 1].locationFacilityTypeId;
        	var child = getChildOfLocation(locationFacilityTypeId);
        	var menu = "";
        	menu += "<div id='contextMenu'><ul>";
        	var locationIdParam = selection[selection.length - 1].locationId;
        	var parentLocationIdParam = selection[selection.length - 1].parentLocationId;
        	if (child.length != 0) {
        		menuHeight = '82px';
        		menu += "<li><i class='icon-plus'></i>&nbsp;&nbsp;${uiLabelMap.accCreateNew}<ul id='menuAddNew' style='width:250px;'>";
        		for ( var x in child) {
    				var locationName = getLocationFacilityType(child[x]).split("[")[0];
    				menu += "<li locationId=" + locationIdParam + " title =" + locationName + " parentLocationId=" + parentLocationIdParam + " typeInsert=" + child[x] + " ><i class='icon-plus'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.CreateNewFacilityType)}: " + child[x] + "</li>";
    				continue;
    			}
        		menu += "</ul></li>";
			}
        	menu += "<li locationId=" + locationIdParam + " title =" + locationName + " parentLocationId=" + parentLocationIdParam + " typeInsert='functionDELETE'><i class='icon-trash'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.CommonRemove)}</li>";
        	menu += "<li locationId=" + locationIdParam + " title =" + locationName + " parentLocationId=" + parentLocationIdParam + " typeInsert='functionEDIT'><i class='icon-edit'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.Edit)}</li>";
        	menu += "</ul></div>";
        	$("#menu").html(menu);
		}
        
    	contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: '150px', height: menuHeight, autoOpenPopup: false, mode: 'popup'});
        contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
        
        contextMenu.on('itemclick', function (event){
    	    if (event.args.attributes.typeInsert == undefined) {
				return;
			}
    	    var typeInsert = event.args.attributes.typeInsert.value;
    	    var locationId = event.args.attributes.locationId.value;
    	    var parentLocationId = event.args.attributes.parentLocationId.value;
    	    if (typeInsert == "functionDELETE") {
    	    	confirmDeleteLocation(locationId);
				return;
			}
    	    if (typeInsert == "functionEDIT") {
    	    	editDescription(locationId);
				return;
			}
    	    $("#titleAdder").text("${StringUtil.wrapString(uiLabelMap.CreateNewFacilityType)}: " + typeInsert);
    	    addFacilityLocationAreaInArea(locationId, parentLocationId, typeInsert);
    	});
	}
    function editDescription(locationId) {
    	locationIdGlobal = locationId;
    	$('#saveEdit').jqxButton({disabled: false });
    	var wtmp = window;
    	var tmpwidth = $('#jqxwindowEditor').jqxWindow('width');
        $("#jqxwindowEditor").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
    	$("#jqxwindowEditor").jqxWindow('open');
    	$('#tarDescriptionEditor').jqxEditor({
	        theme: 'olbiuseditor',
	        width: '96%'
	    });
    	var descriptionEdit = $("#treeGrid").jqxTreeGrid('getCellValue', rowKey, 'description');
    	var locationCodeEdit = $("#treeGrid").jqxTreeGrid('getCellValue', rowKey, 'locationCode');
    	var index = _.indexOf(arrayLocationCodeAvalible, locationCodeEdit);
    	arrayLocationCodeAvalible.splice(index, 1);
    	$('#tarDescriptionEditor').val(descriptionEdit);
    	$('#txtLocationCodeEditor').val(locationCodeEdit);
    	
	}
    $("#jqxwindowEditor").jqxWindow({theme: 'olbius',
	    width: 650, maxWidth: 1845, minHeight: 370, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelEdit"), modalOpacity: 0.7
	});
    $("#jqxwindowEditor").on('open', function (event) {
		disableScrolling();
	});
	$("#jqxwindowEditor").on('close', function (event) {
		bindArrayLocationCode();
		enableScrolling();
		$('#jqxwindowEditor').jqxValidator('hide');
	});
    $("#cancelEdit").jqxButton({template: "danger" });
	$("#saveEdit").jqxButton({template: "primary"});
	$("#saveEdit").click(function () {
		if ($('#jqxwindowEditor').jqxValidator('validate')) {
			$('#saveEdit').jqxButton({disabled: true });
			var txtLocationCodeEditor = $('#txtLocationCodeEditor').val().trim();
			var tarDescriptionEditor = $('#tarDescriptionEditor').val().replaceAll("<div><br></div>","");
			updateDescription(txtLocationCodeEditor, tarDescriptionEditor);
			$("#jqxwindowEditor").jqxWindow('close');
		}
	});
	$('#jqxwindowEditor').jqxValidator({
        rules: [
					{ input: '#txtLocationCodeEditor', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#tarDescriptionEditor', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#tarDescriptionEditor").val();
							if (value == "<div>​</div>" || value == "<br>" || value == "") {
								return false;
							}
							return true;
						}
					},
					{ input: '#txtLocationCodeEditor', message: '${StringUtil.wrapString(uiLabelMap.DuplicateLocationCode)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtLocationCodeEditor").val();
							if (_.indexOf(arrayLocationCodeAvalible, value) != -1) {
								return false;
							}
							return true;
						}
					},
					{ input: '#txtLocationCodeEditor', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtLocationCodeEditor").val();
							if (!value.containSpecialChars()) {
								return true;
							}
							return false;
						}
					}
               ]
    });
	function updateDescription(txtLocationCodeEditor, tarDescriptionEditor) {
		var result;
		$.ajax({
  		  url: "updateLocationFacilityAjax",
  		  type: "POST",
  		  data: {locationId: locationIdGlobal, locationCode: txtLocationCodeEditor, description: tarDescriptionEditor},
  		  dataType: "json",
  		  success: function(res) {
  			result = res["success"];
  		  }
	  	}).done(function() {
	  		$('#jqxNotificationNested').jqxNotification('closeLast');
	  		if (result) {
	  			getListlocationFacility(facilityIdGlobal);
	  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
              	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			}
	  	});
	}
    var arrayLocationIdNeedDelete = [];
    function confirmDeleteLocation(locationId) {
    	arrayLocationIdNeedDelete= [];
		if (checkHasChild(locationId)) {
			arrayLocationIdNeedDelete.push(locationId);
			bootbox.confirm("${StringUtil.wrapString(uiLabelMap.ConfirmDeleteLocationAloneDetails)}", function(result) {
				if (result) {
					deleteLocationAjax(arrayLocationIdNeedDelete);
				}else {
					
				}
			});
		} else {
			var arrayLocationIdFirst = [];
			arrayLocationIdFirst.push(locationId);
			arrayLocationIdNeedDelete = _.uniq(getListChild(locationId, arrayLocationIdFirst));
			bootbox.confirm("${StringUtil.wrapString(uiLabelMap.ConfirmDeleteLocationDetails)}", function(result) {
				if (result) {
					deleteLocationAjax(arrayLocationIdNeedDelete);
				}else {
					
				}
			});
		}
	}
   
    
    function deleteLocationAjax(arrayLocationId) {
    	var result;
    	$.ajax({
    		  url: "deleteLocationAjax",
    		  type: "POST",
    		  data: {arrayLocationId: arrayLocationId, facilityId: facilityIdGlobal},
    		  dataType: "json",
    		  async: true,
    		  success: function(res) {
    			  result = res["result"];
    		  }
  	  	}).done(function() {
  	  		getListlocationFacility(facilityIdGlobal);
  	  		getGeneralQuantity();
  	  		checkHasInventoryInLocation();
  	  		reset();
  	  		$('#jqxNotificationNested').jqxNotification('closeLast');
  	  		if (result == "success") {
  	  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
            	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
            	$("#jqxNotificationNested").jqxNotification("open");
            	isNeedReposition = false;
  			} else {
  				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
      			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
                $("#jqxNotificationNested").jqxNotification("open");
  			}
  	  	});
	}
    
    $('#treeGrid').on('rowSelect', function (event){
    	if(contextMenu)contextMenu.jqxMenu('close');
	});
    $("#contextMenu").on('closed', function () {
		contextMenu.jqxMenu('destroy');
	});
    function getChildOfLocation(parentLocationFacilityTypeId) {
    	var listChild = [];
		for ( var x in listLocationFacilityType) {
			var childLocationFacilityTypeId = listLocationFacilityType[x].parentLocationFacilityTypeId;
			if (parentLocationFacilityTypeId == childLocationFacilityTypeId) {
				var locationFacilityTypeId = listLocationFacilityType[x].locationFacilityTypeId;
				listChild.push(locationFacilityTypeId);
			}
		}
		return listChild;
	}
    
	var listLocationFacilityType = [
							<#if listLocationFacilityType?exists>
								<#list listLocationFacilityType as item> {
									description: '${item.description?if_exists}'  + " [#<i style=\"color:green;\">" + '${item.locationFacilityTypeId?if_exists}' + "</i>]",
									locationFacilityTypeId: '${item.locationFacilityTypeId?if_exists}',
									parentLocationFacilityTypeId: '${item.parentLocationFacilityTypeId?if_exists}'},
								</#list>
							</#if>
							];
	function getLocationFacilityType(locationFacilityTypeId) {
		if (locationFacilityTypeId != null) {
			for ( var x in listLocationFacilityType) {
				if (locationFacilityTypeId == listLocationFacilityType[x].locationFacilityTypeId) {
					return listLocationFacilityType[x].description;
				}
			}
		} else {
			return "";
		}
	}
	var arrayLocationTypeParent = [];
	function getLocationTypeParent() {
		for ( var x in listLocationFacilityType) {
			var parentLocationFacilityTypeId = listLocationFacilityType[x].parentLocationFacilityTypeId;
			if (parentLocationFacilityTypeId == "") {
				arrayLocationTypeParent.push(listLocationFacilityType[x]);
			}
		}
		return arrayLocationTypeParent;
	}
	var listProduct = [
	<#if listProduct?exists>
		<#list listProduct as item>{
			quantityUomId: '${item.quantityUomId?if_exists}',
			productId: '${item.productId?if_exists}',
			internalName: '${StringUtil.wrapString(item.internalName?if_exists)}'},
		</#list>
	</#if>
	];
	var mapProductWithUom = {
		<#if listProduct?exists>
			<#list listProduct as item>
				'${item.productId?if_exists}': '${item.quantityUomId?if_exists}',
			</#list>
		</#if>
	};
	var mapProductWithName = {
		<#if listProduct?exists>
			<#list listProduct as item>
				'${item.productId?if_exists}': '${StringUtil.wrapString(item.internalName?if_exists)}',
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
	var inventoryItem = new Array();
	<#if inventoryItem?exists>
		<#list inventoryItem as item>
			inventoryItem.push('${item.productId?if_exists}');
		</#list>
	</#if>
	
	function getAllProductInFacility() {
		var listProductAvalibleInFacility = [];
		inventoryItem = _.uniq(inventoryItem);
		for ( var x in inventoryItem) {
			var item = {};
			var productId = inventoryItem[x];
			item.productId = productId;
			for ( var g in listProduct) {
				var thisProductId = listProduct[g].productId;
				if (productId == thisProductId) {
					item.internalName = listProduct[g].internalName;
					break;
				}
			}
			listProductAvalibleInFacility.push(item);
		}
		return listProductAvalibleInFacility;
	}
	
	var facilityIdGlobal = "${facilityId}";
	var arrayVarianceReason = [];
	var locale = '${locale}';
	var listProductAvalibleInFacilitySearch;
    $(document).ready(function () {
    	locale == "vi_VN"?locale="vi":locale=locale;
    	getGeneralQuantity();
    	getListlocationFacility(facilityIdGlobal);
    	checkHasInventoryInLocation();
    	getLocationTypeParent();
    	if (checkProductNotLocationAjax()) {
			$("#divHasProductsNotLocation").text("${StringUtil.wrapString(uiLabelMap.HasProductsNotLocation)}");
		}else {
			$("#divHasProductsNotLocation").text("");
		}
        $("#jqxInputSearch").jqxInput({ placeHolder: " Search", displayMember: "internalName", valueMember: "productId", width: 500, height: 25,
            source: function (query, response) {
                var item = query.split(/,\s*/).pop();
                $("#jqxInputSearch").jqxInput({ query: item });
                response(listProductAvalibleInFacilitySearch);
            },
            renderer: function (itemValue, inputValue) {
                var terms = inputValue.split(/,\s*/);
                terms.pop();
                terms.push(itemValue);
                terms.push("");
                var value = terms.join(", ");
                return value;
            }
        });
        var afterFT = $("#gridPhysicalInventoryClearFilters").html();
		$("#gridPhysicalInventoryClearFilters").html("<span style='color:red;font-size:80%;left:5px;position:relative;'>x</span>");
		$("#gridPhysicalInventoryClearFilters").append(afterFT);
		$("#gridPhysicalInventoryClearFilters").css("display", "none");
		for ( var r in listVarianceReason) {
			arrayVarianceReason.push(listVarianceReason[r].varianceReasonId);
		}
    });
    function createLocationFacilityAjax(data) {
    	var result = {};
    	var locationId;
    	var success = true;
    	$.ajax({
    		  url: "createLocationFacilityAjax",
    		  type: "POST",
    		  data: data,
    		  dataType: "json",
    		  async: false,
    		  success: function(res) {
    			  locationId = res["locationId"];
    		  }
    	}).done(function() {
    		getGeneralQuantity();
    		$('#jqxNotificationNested').jqxNotification('closeLast');
    		if (locationId == undefined) {
    			success = false;
    			$("#jqxNotificationNested").jqxNotification({ template: 'error'});
    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: 'info'});
              	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			}
    	});
    	result.locationId = locationId;
    	result.success = success;
    	return result;
    }
    function checkHasChild(locationId) {
		for ( var x in listlocationFacility) {
			var parentLocationId = listlocationFacility[x].parentLocationId;
			if (parentLocationId == null) {
				continue;
			}
			if (parentLocationId == locationId) {
				return false;
			}
		}
		return true;
	}
    function getListChild(locationId, listChild) {
    	for ( var x in listlocationFacility) {
    		var parentLocationId = listlocationFacility[x].parentLocationId;
    		var thislocationId = listlocationFacility[x].locationId;
    		if (parentLocationId == locationId) {
    			listChild.push(thislocationId);
    			getListChild(thislocationId, listChild);
    		}
    	}
    	return listChild;
    }
    var listLocationHasChild = [];
    function getLocationHasChild() {
    	listLocationHasChild = [];
		for ( var g in listlocationFacility) {
			var locationId = listlocationFacility[g].locationId;
			if (!checkHasChild(locationId)) {
				listLocationHasChild.push(listlocationFacility[g]);
			}
		}
	}
    var listlocationFacility;
    function getListlocationFacility(facilityId) {
    	$.ajax({
    		url: "getLocationFacilityAjax",
    		type: "POST",
    		data: {facilityId: facilityId},
    		dataType: "json",
    		success: function(res) {
    			listlocationFacility = res["listlocationFacility"];
    		}
    	}).done(function() {
    		if (listlocationFacility.length == 0) {
    		}
    		renderTreeGridLocationFacility();
    		getLocationHasChild();
    		bindArrayLocationCode();
    	});
    }
    var arrayLocationCodeAvalible = [];
    function bindArrayLocationCode() {
    	arrayLocationCodeAvalible = [];
		for ( var x in listlocationFacility) {
			arrayLocationCodeAvalible.push(listlocationFacility[x].locationCode);
		}
	}
    function clearPopup() {
    	$("input[name='txtLocationCode']").val("");
    	$("input[name='txtLocationCodeInArea']").val("");
    	$("input[name='txtLocationCodeAisle']").val("");
    	$("#tarDescriptionInArea").val("");
    	$("#tarDescription").val("");
	}
    function renderTreeGridLocationFacility() {
    	var source =
        {
            dataType: "json",
            dataFields: [
					{ name: 'locationId', type: 'string' },         
					{ name: 'facilityId', type: 'string' }, 
					{ name: 'locationCode', type: 'string' },
					{ name: 'parentLocationId', type: 'string' },
					{ name: 'locationFacilityTypeId', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'locationStatus', type: 'string' },
            ],
            hierarchy:
            {
                keyDataField: { name: 'locationId' },
                parentDataField: { name: 'parentLocationId' }
            },
            id: 'locationId',
            localData: listlocationFacility,
             addRow: function (rowID, rowData, position, parentID, commit) {
            	 var result = createLocationFacilityAjax(rowData);
            	 if (result.success) {
            		 rowData.locationId = result.locationId;
            		 listlocationFacility.push(rowData);
            		 bindArrayLocationCode();
            		 if (warningMode) {
            			 setTimeout(function() {
            				 location.reload();
						}, 100);
					}
				}
            	 commit(result.success);
                 newRowID = rowID;
             },
             updateRow: function (rowID, rowData, commit) {
            	 commit(true);
             },
             deleteRow: function (rowID, commit) {
            	 
            	 commit(true);
             }
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#treeGrid").jqxTreeGrid({
            source: dataAdapter,
            localization: getLocalization(),
            width: '100%',
            sortable: true,
            theme: 'olbius',
            columnsresize: true,
            columnsReorder: true,
            checkboxes: true,
            autoRowHeight: false,
            altRows: true,
            selectionMode: 'multipleRows',
            hierarchicalCheckboxes: true,
            rendered: function () {
            	if (isNeedReposition) {
        			$('#divLocationStatus').text('${StringUtil.wrapString(uiLabelMap.NeedReposition)}');
        			if (!updateMode && !moveMode) {
        				$('#divUpdateProductTo').css('display', 'block');
        			}
        		}else {
        			$('#divLocationStatus').text('');
        			$('#divUpdateProductTo').css('display', 'none');
        		}
            },
            columns: [
                      { text: '${uiLabelMap.FacilityLocationPosition}', dataField: 'locationCode', align: 'center', width: 250, cellclassname: cellclassnameNeedReposition},
                      { text: '${uiLabelMap.SelectTypeLocationFacility}', dataField: 'locationFacilityTypeId', align: 'center', width: 250, cellclassname: cellclassnameNeedReposition,
                    	  	cellsrenderer: function (row, column, value, rowData) {
        	  					var locationFacilityTypeId = rowData.locationFacilityTypeId;
        	  					var locationFacilityType = getLocationFacilityType(locationFacilityTypeId);
        	  					return '<span>' + locationFacilityType + '</span>';
          					}
                      },
                      { text: '${uiLabelMap.Description}', dataField: 'description', align: 'center', minWidth: 250, cellclassname: cellclassnameNeedReposition },
                      { text: '${uiLabelMap.LocationStatus}', dataField: '', align: 'center', width: 250, cellclassname: cellclassnameNeedReposition,
                    	  cellsrenderer: function (row, column, value, rowData) {
                    		  	var locationId = rowData.locationId;
                    		  	var status = totalQuantity[locationId];
        	  					return "<span style='float:right;'>" + status.toLocaleString(locale) + "</span>";
                    	  }
                      }
                    ]
        });
	}
    var isNeedReposition = false;
    var arrayLocationHasProductNeedReposition = [];
    var cellclassnameNeedReposition = function (row, column, value, data) {
    	var locationId = data.locationId;
    	if (!checkHasChild(locationId)) {
			if (mapHasInventoryInLocation[locationId]) {
				arrayLocationHasProductNeedReposition.push(locationId);
				arrayLocationHasProductNeedReposition = _.uniq(arrayLocationHasProductNeedReposition);
				isNeedReposition = true;
				return "needReposition";
			}
			if (checkChildNeedUpdate(locationId)) {
	    		isNeedReposition = true;
				return "needReposition";
			}
		}
        return "";
    };
    
    var tmpWidth = $(window).width() - 40;
    $(window).bind('resize', function() {
        var sibar = $('#sidebar');
        if($('#sidebar').css("display") != "none"){
        	$('#treeGrid').jqxTreeGrid({ width: $(window).width() - $('#sidebar').width() - 40 , theme: 'olbius' });
        	$("#searchGrid").jqxGrid({ width: $(window).width() - $('#sidebar').width() - 40 , theme: 'olbius' });
        	$("#gridCreatePhysicalInventoryAndVariance").jqxGrid({ width: $(window).width() - $('#sidebar').width() - 40 , theme: 'olbius' });
        	$("#gridPhysicalInventory").jqxGrid({ width: $(window).width() - $('#sidebar').width() - 40 , theme: 'olbius' });
        }else{
            $('#treeGrid').jqxTreeGrid({ width: tmpWidth, theme: 'olbius' });
            $("#searchGrid").jqxGrid({ width: tmpWidth, theme: 'olbius' });
            $("#gridCreatePhysicalInventoryAndVariance").jqxGrid({ width: tmpWidth, theme: 'olbius' });
            $("#gridPhysicalInventory").jqxGrid({ width: tmpWidth, theme: 'olbius' });
        }
        tmpWidth = $('#treeGrid').jqxTreeGrid('width');
    });
    $('#sidebar').bind('resize', function() {
        $('#treeGrid').jqxTreeGrid({ width: tmpWidth, theme: 'olbius' });
        $("#searchGrid").jqxGrid({ width: tmpWidth, theme: 'olbius' });
        $("#gridCreatePhysicalInventoryAndVariance").jqxGrid({ width: tmpWidth, theme: 'olbius' });
        $("#gridPhysicalInventory").jqxGrid({ width: tmpWidth, theme: 'olbius' });
        tmpWidth = $('#treeGrid').jqxGrid('width');
    });
    
    function checkChildNeedUpdate(parentLocationId) {
    	var result = false;
		for ( var x in listlocationFacility) {
			var thisParentLocationId = listlocationFacility[x].parentLocationId;
			if (thisParentLocationId == parentLocationId) {
				var locationId = listlocationFacility[x].locationId;
		    	if (!checkHasChild(locationId)) {
					if (mapHasInventoryInLocation[locationId]) {
						arrayLocationHasProductNeedReposition.push(locationId);
						arrayLocationHasProductNeedReposition = _.uniq(arrayLocationHasProductNeedReposition);
						result = true;
					}else {
						if (!result) {
							result = checkChildNeedUpdate(locationId);
						}
					}
				}
			}
		}
		return result;
	}
    var totalQuantity;
    function getGeneralQuantity() {
    	totalQuantity = null;
    	$.ajax({
  		  url: "getGeneralQuantityAjax",
  		  type: "POST",
  		  data: {facilityId: facilityIdGlobal},
  		  dataType: "json",
  		  async: false,
  		  success: function(res) {
  			  totalQuantity = res["totalQuantity"];
  		  }
	  	});
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

<style>
	#tarMapArea {
		width: 100%;
		height: 75px;
		margin-bottom: 10px;
		margin-top: 10px;
		color: #037C07 !important;
		font-weight: 400;
		font-size: 14px;
	}
	#txtOrderNote {
		color: red !important;
	}
	#divLocationStatus {
		color: red !important;
		font-size: 18px;
	}
	#divHasProductsNotLocation {
		color: #c09853 !important;
		font-size: 18px;
		margin-top: 8px;
		margin-bottom: 8px;
	}
	.dragable{
		color: #037C07 !important;
	}
	.needReposition{
		background-color: #FF9494 !important;
	}
	#pagergridPhysicalInventory {
		display: none;
	}
	body {
		  -webkit-user-select: none;
		     -moz-user-select: -moz-none;
		      -ms-user-select: none;
		          user-select: none;
		}
	.jqx-icon-close-olbius{
		  background-image: url(/aceadmin/jqw/jqwidgets/styles/images/close.png);
	}
	.jqx-tabs-headerWrapper {
		background-color: red!important;
	}
	hr {
		opacity: 0.4;
	}
	.customCssButton {
		height: 28px;
	}
	.jqx-grid-pager-olbius {
		z-index: 0!important;
	}
	.jqx-tabs-title-container-olbius {
		width: 100%;
	}
</style>