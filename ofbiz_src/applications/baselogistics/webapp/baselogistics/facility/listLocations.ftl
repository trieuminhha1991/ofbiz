<#include "script/locationFacilityScript.ftl"/>
<#include "script/locationTypeScript.ftl"/>
<#include "locationTypeHtml.ftl"/>
<#include 'physicalInventory.ftl'/>

<div class="row-fluid" style="display: none;" id="searchWraper">
<div class="span12">
	<div class="span3"></div>
	<div class="span9"><input type="text" id="jqxInputSearch" style="border-radius: 5px !important;float:right;" spellcheck='false'/></div>
</div>
</div>

<div id="searchGrid" style="display: none;overflow-y: hidden;"></div>

<div id="container"></div>
<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
<div id='Gridcontent'>
<div id="divLocationStatus"></div>
<div id="divHasProductsNotLocation"></div>
<div id="treeGrid" style="border: 1px solid #CCC !important;" class="jqx-grid-olbius2"></div>
<div id="menu"></div>

<div id="jqxwindowPopupAdderFacilityLocationAreaInArea" class="hide popup-bound">
	<div>${uiLabelMap.CreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
		    <div class="row-fluid margin-bottom10 margin-top20">
	        	<div class="span4 text-algin-right">
	        		<div style="margin-right: 10px">${uiLabelMap.AreaMap}</div>
				</div>
	        	<div class="span7">
	        		<div id="tarMapArea"/></div>
	    		</div>
		    </div>
		    <div class="row-fluid margin-bottom10">
	        	<div class="span4 text-algin-right">
	        		<div class="asterisk">${uiLabelMap.Location}</div>
				</div>
	        	<div class="span7">
	        		<div><input id="txtLocationCodeInArea"/></div>
	    		</div>
		    </div>
		    
		    <div class="row-fluid margin-bottom10">
	        	<div class="span4 text-algin-right">
	        		<div style="margin-right: 10px">${uiLabelMap.Description}</div>
				</div>
	        	<div class="span7">
	        		<div><textarea rows="2" id="tarDescriptionInArea" style="resize: none;margin-top: 0px !important; width: 190px"></textarea></div>
	    		</div>
		    </div>
			<div class="form-action popup-footer">
                <button id="alterCancelAdderFacilityLocationAreaInArea" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                <button id="alterSaveAdderFacilityLocationAreaInArea" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	        </div>
		</div>
	</div>
</div>

<div id="jqxwindowPopupMoverProducts" class="hide popup-bound">
	<div>${uiLabelMap.MoveProductsLocation} - ${uiLabelMap.DragAndDropProductToLocation}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="overflow-x: hidden">
			<div class="row-fluid margin-bottom10">
				<div class="span12 no-left-margin">
					<div class="disable-scroll" id="jqxTabsFrom"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12 no-left-margin">
					<div class="disable-scroll" id="jqxTabsTo">
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
                <button id="alterCancelMoverProducts" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                <button id="alterSaveMoverProducts" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		    </div>
	    </div>
	</div>
</div>

<div id="taskBarHiden" style="display:block; margin-right: -3px;">
	<button id='btnCancelReset' class="btn btn-small btn-danger form-action-button pull-right margin-top10"><i class='icon-remove open-sans'></i>${uiLabelMap.CommonCancel}</button>
	<button id='moveProductTo' class="btn btn-small btn-primary form-action-button pull-right margin-top10"><i class='icon-share-alt open-sans'></i>${uiLabelMap.MoveToAnotherLocation}</button>
	<button id='addProduct' class="btn btn-small btn-primary form-action-button pull-right margin-top10"><i class='icon-plus open-sans'></i>${uiLabelMap.AddProductToLocation}</button>
	<button id='viewProduct' class='btn btn-small btn-primary form-action-button pull-right margin-top10'><i class='icon-eye-open open-sans'></i>${uiLabelMap.ViewProductInLocation}</button>
</div>

<div id="divUpdateProductTo" style="display:none; margin-right: 5px !important;">
	<button id='updateProductTo' class="btn btn-small btn-primary form-action-button pull-right margin-top10"><i class='icon-refresh open-sans'></i>${uiLabelMap.Reposition}</button>
</div>
<div id="taskBarMove" style="display:none;">
	<button id='btnReset' class="btn btn-small btn-danger form-action-button pull-right margin-top10"><i class='icon-remove open-sans'></i>${uiLabelMap.CommonCancel}</button>
	<button id='moveProduct' class="btn btn-small btn-primary form-action-button pull-right margin-top10"><i class='icon-share-alt open-sans'></i>${uiLabelMap.MoveProducts}</button>
</div>

</div>

<div id="divFacilityDelivery" style="display:none;">
<div>${uiLabelMap.ExportQuantity}</div>
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

<div id="jqxwindowPopupAdderFacilityLocationArea" class="popup-bound hide"">
	<div>${uiLabelMap.CreateNewLocation}</div>
	<div class='form-window-container'>
    	<div class='form-window-content'>
			<div class="row-fluid margin-bottom10 margin-top20">
	        	<div class="span4 text-algin-right">
	        		<div style="margin-right: 10px">${uiLabelMap.Facility}</div>
    			</div>
	        	<div class="span7">
	        		<div><input id="txtFacility" name="txtFacility"/></div>
        		</div>
		    </div>
		    <div class="row-fluid margin-bottom10">
		    	<div class="span4" style="text-align: right">
					<div class="asterisk"> ${uiLabelMap.LocationType} </div>
				</div>
				<div class="span7">	
					<div class="span8">
				        <div id="txtLocationType" class="green-label"></div>
			        </div>
			        <div class="span2">
			        	<a href="javascript:LocationObj.addLocationFacilityType()" onclick="" style="margin-left: 35px"><i class="icon-plus"></i></a>
			        </div>
				</div>
		    </div>
		    <div class="row-fluid margin-bottom10">
			    <div class="span4 text-algin-right">
			    	<div class="asterisk">${uiLabelMap.LocationName}</div>
		    	</div>
			    <div class="span7">
			    	<div><input id="txtLocationCode" /></div>
			    </div>
		    </div>
		    <div class="row-fluid margin-bottom10">
			    <div class="span4 text-algin-right">
			    	<div style="margin-right: 10px">${uiLabelMap.Description}</span></div>
		    	</div>
			    <div class="span7">
			    	<div><input id="locationDescription"></input></div>
		    	</div>
		    </div>
		    <div class="form-action popup-footer">
                <button id="alterCancelAdderFacilityLocationArea" class='btn btn-danger form-action-button pull-right' style="margin-right:10px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                <button id="alterSaveAdderFacilityLocationArea" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
            </div>
	    </div>
	</div>
</div>

<div id="jqxwindowEditor" class="hide">
	<div>${uiLabelMap.Edit}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
		    <div class="row-fluid margin-bottom10 margin-top20">
	        	<div class="span4 text-algin-right">
	        		<div class="asterisk">${uiLabelMap.Location}</div>
				</div>
	        	<div class="span7">
	        		<div><input id="txtLocationCodeEditor"/></div>
	    		</div>
		    </div>	
		    <div class="row-fluid margin-bottom10">
	        	<div class="span4 text-algin-right">
	        		<div style="margin-right: 10px">${uiLabelMap.Description}</div>
				</div>
	        	<div class="span7">
	        		<div><textarea rows="3" id="tarDescriptionEditor" style="resize: none;margin-top: 0px !important; width: 195px"></textarea></div>
	    		</div>
		    </div>
			<div class="form-action popup-footer">
	            <button id="cancelEdit" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	            <button id="saveEdit" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	        </div>
		</div>
	</div>
</div>

<script>

var facilityIdGlobal = "${facilityId}";
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
						description: '${StringUtil.wrapString(item.description?if_exists)}'
					},
				</#list>
			 </#if>
	];
var mapQuantityUom = {
	<#if quantityUoms?exists>
		<#list quantityUoms as item>
				'${item.uomId?if_exists}': '${StringUtil.wrapString(item.description?if_exists)}',
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
	
	$("#updatePhysicalInventoryAndVariance").click(function () {
		bootbox.dialog('${uiLabelMap.AreYouSureSave}', 
		[{"label": '${uiLabelMap.CommonCancel}', 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": '${uiLabelMap.OK}',
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
		    	setTimeout(function(){		
		    		progessDataToCreatePhysicalInventoryAndVariance();
		    	Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);	
	});
	
	function updateInventoryItemInLocation(data){
		$.ajax({
  		  url: "updateInventoryItemInLocation",
  		  type: "POST",
  		  data: data,
  		  async: false,
  		  success: function(res) {
  		  }
	  	});
	}
	function createNewInventoryItemInLocation(data){
		$.ajax({
  		  url: "createNewInventoryItemInLocation",
  		  type: "POST",
  		  data: data,
  		  async: false,
  		  success: function(res) {
  		  }
	  	});
	}
	function createPhysicalInventoryAndVarianceAjax(data) {
		$.ajax({
  		  url: "createPhysicalInventoryAndVarianceLog",
  		  type: "POST",
  		  data: data,
		  async: false,
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
	
	
	var varianceReasonIdActive = "";
	var rowBoundIndexReasonIdActive = "";
	$("#exportExcel").click(function () {
		$("#gridPhysicalInventory").jqxGrid('exportdata', 'xls', '${StringUtil.wrapString(uiLabelMap.InventoryDetails)}');
		window.location.href = "exportInventoryCountTheVotes?facilityId=" + facilityIdGlobal;
	});
	$("#cancelExport").click(function () {
		$("#divPhysicalInventory").css({ "display": "none"});
		$("#divCreatePhysicalInventoryAndVariance").css({ "display": "none"});
        $("#Gridcontent").css({ "display": "block"});
        $("#gridPhysicalInventoryClearFilters").css("display", "none");
        $("#exportCreatePhysicalInventoryAndVariance").html("<i class=' icon-edit'></i>${StringUtil.wrapString(uiLabelMap.ExportExcel)}");
	});
	$("#cancelExportCreatePhysicalInventoryAndVariance").click(function () {
		$("#divPhysicalInventory").css({ "display": "none"});
		$("#divCreatePhysicalInventoryAndVariance").css({ "display": "none"});
		$("#Gridcontent").css({ "display": "block"});
		$("#gridPhysicalInventoryClearFilters").css("display", "none");
		$("#exportCreatePhysicalInventoryAndVariance").html("<i class=' icon-edit'></i>${StringUtil.wrapString(uiLabelMap.ExportExcel)}");
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
                { name: 'productName', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' },
                { name: 'statusId', type: 'string' },
                { name: 'quantityUomId', type: 'string' },
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
            theme: 'olbius',
            pageable: true,
            autoheight: true,
            sortable: true,
            selectionmode: 'singlerow',
            columns: [
              { text: '${uiLabelMap.InventoryItemId}', dataField: 'inventoryItemId', align: 'left', width: 150, editable:false },
              { text: '${uiLabelMap.ProductId}', dataField: 'productId', align: 'left', width: 180},
              { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'left', width: 180},
              { text: '${StringUtil.wrapString(uiLabelMap.Status)}', dataField: 'statusId', align: 'left', width: 150, editable:false,
 					cellsrenderer: function(row, colum, value){
 						for(i=0; i < statusData.length; i++){
 				            if(statusData[i].statusId == value){
 				            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData[i].description + '</span>';
 				            }
 				        }
 						if (!value){
 			            	return '<span style=\"text-align: left;\" title='+value+'>${uiLabelMap.InventoryGood}</span>';
 						}
 					}
				},
              { text: '${uiLabelMap.ReceivedDate}', dataField: 'datetimeReceived', align: 'left', width: 150, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left' },
              { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', align: 'left', width: 150, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left' },
              { text: '${uiLabelMap.Unit}', dataField: 'uomId', align: 'left',
            	  cellsrenderer: function(row, colum, value){
    			        var data = $("#searchGrid").jqxGrid('getrowdata', row);
    			        var productId = data.productId;
    			        if (value){
    			        	return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
    			        } else {
    			        	if (data.quantityUomId){
    			        		return '<span>' + mapQuantityUom[data.quantityUomId] + '</span>';
    			        	} else {
    			        		return '<span>_NA_</span>';
    			        	}
    			        }
    		        }  
              },
              { text: '${uiLabelMap.Quantity}', dataField: 'quantity', align: 'left', width: 160, cellsalign: 'right',
            	  cellsrenderer: function(row, colum, value){
						return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
					}  
              },
              { text: '${uiLabelMap.AreaMap}', dataField: '', align: 'left', minwidth: 200,
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
              { text: '${uiLabelMap.StockOut}', dataField: 'facilityDelivery', align: 'left', width: 100,
            	  cellsrenderer: function(row, colum, value){
            		  return "<span><a onclick='facilityDelivery(" + row + ")' ><i class='fa-truck'></i> ${uiLabelMap.StockOut}</a></span>";
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
	$("#alterSaveFacilityDelivery").click(function () {
		if ($('#divFacilityDelivery').jqxValidator('validate')) {
			bootbox.dialog(uiLabelMap.AreYouSureSave, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	Loading.show('loadingMacro');
			    	setTimeout(function(){		
			    		var row = $("#divFacilityDelivery").attr("row");
						var data = $("#searchGrid").jqxGrid('getrowdata', row);
						var deliveryQuantity = $("#txtQuantityDelivery").val();
						var oldQuantity = data.quantity;
						var newQuantity = oldQuantity - deliveryQuantity;
						$("#searchGrid").jqxGrid('setcellvalue', row, "quantity", newQuantity);
						$("#divFacilityDelivery").jqxWindow("close");
			    	Loading.hide('loadingMacro');
			    	}, 500);
			    }
			}]);	
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
    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.UpdateError)}");
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
		setTimeout(function(){
    		$("#viewProduct").attr('disabled', true);
    		$("#addProduct").attr('disabled', true);
    		$("#moveProductTo").attr('disabled', true);
    		$("#btnCancelReset").attr('disabled', true);
    		$("#moveProduct").attr('disabled', true);
    		$("#btnReset").attr('disabled', true); 
    	}, 500);
	});
	$("#jqxwindowPopupMoverProducts").jqxWindow({theme: 'olbius',
	    width: 1200, maxWidth: 1800, height: 630, maxHeight: 700, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelMoverProducts"), modalOpacity: 0.7
	});
	$("#alterSaveMoverProducts").click(function () {
		bootbox.dialog(uiLabelMap.AreYouSureSave, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
		    	setTimeout(function(){		
		    		var totalRecord = new Array();
					for ( var x in arrayLocationIdFrom) {
						totalRecord.push.apply(totalRecord, mapInventoryFrom[arrayLocationIdFrom[x]]);
					}
					for ( var e in arrayLocationIdTo) {
						totalRecord.push.apply(totalRecord, mapInventoryTo[arrayLocationIdTo[e]]);
					}
					for ( var z in totalRecord) {
						if (typeof totalRecord[z].expireDate == 'object') {
							if (totalRecord[z].expireDate){
								totalRecord[z].expireDate = totalRecord[z].expireDate.getTime();
							}
						}
					}
					saveDataToLocation(totalRecord);
					$("#jqxwindowPopupMoverProducts").jqxWindow('close');
		    	Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);	
	});
	
	$("#jqxwindowPopupMoverProducts").on('open', function (event) {
		disableScrolling();
	});
	$("#jqxwindowPopupMoverProducts").on('close', function (event) {
		enableScrolling();
	});
	
	var mapInventoryFrom;
	var mapInventoryTo;
	var idGridFromActive;
	var idGridToActive;
	$("#moveProduct").click(function () {
		if (arrayLocationId.length == 0) {
			bootbox.dialog('${StringUtil.wrapString(uiLabelMap.ChooseLocation)}', [{
	            "label" : '${uiLabelMap.OK}',
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            }]
	        );
			return;
		}
		moveMode = true;
		updateMode = false;
		arrayLocationIdTo = arrayLocationId;
		renderHtmlMover();
		$('#jqxTabsMoverFrom').jqxTabs({ height: 260, theme: 'olbius'});
		$('#jqxTabsMoverTo').jqxTabs({ height: 260, theme: 'olbius'});
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
		$("#jqxwindowPopupMoverProducts").jqxWindow('open');
		$("#moveProduct").attr('disabled', true);
		$("#btnReset").attr('disabled', true);
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
                	$("#" + idGridToActiveHtml).jqxGrid('setcellvalue', 0, 'productName', dataRowFrom.productName);
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
                { name: 'productName', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' },
                { name: 'statusId', type: 'string' },
                { name: 'quantityUomId', type: 'string' }
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
                pageable: true,
                showfilterrow: true,
                filterable: true,
                columns: [
                   { text: '${uiLabelMap.InventoryItemId}', dataField: 'inventoryItemId', align: 'left', width: 100 },
                   { text: '${uiLabelMap.ProductId}', dataField: 'productId', align: 'left', width: 130, cellclassname: cellclassname },
                   { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'left', minwidth: 200,},
                   { text: '${StringUtil.wrapString(uiLabelMap.Status)}', dataField: 'statusId', align: 'left', width: 150, editable:false,
	   					cellsrenderer: function(row, colum, value){
	   						for(i=0; i < statusData.length; i++){
	   				            if(statusData[i].statusId == value){
	   				            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData[i].description + '</span>';
	   				            }
	   				        }
	   						if (!value){
	   			            	return '<span style=\"text-align: left;\" title='+value+'>${uiLabelMap.InventoryGood}</span>';
	   						}
	   					}
  					},
	               { text: '${uiLabelMap.Quantity}', dataField: 'quantity', align: 'left', width: 130, cellsalign: 'right', columntype:"numberinput", filtertype: 'number',
                	   cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
						}
	               },
	               { text: '${uiLabelMap.ReceivedDate}', dataField: 'datetimeReceived', align: 'left', width: 120, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left' },
	               { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', align: 'left', width: 120, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left' },
	               { text: '${uiLabelMap.Unit}', dataField: 'uomId', align: 'left', filtertype: 'checkedlist', width: 120,
		            	  cellsrenderer: function(row, colum, value){
		    			        var data = $("#" + gridIdFromActive).jqxGrid('getrowdata', row);
		    			        var productId = data.productId;
		    			        if (value){
		    			        	return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
		    			        } else {
		    			        	if (data.quantityUomId){
		    			        		return '<span>' + mapQuantityUom[data.quantityUomId] + '</span>';
		    			        	} else {
		    			        		return '<span>_NA_</span>';
		    			        	}
		    			        }
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
                { name: 'productName', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' },
                { name: 'quantityUomId', type: 'string' },
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
                pageable: true,
                columns: [
                   { text: '${uiLabelMap.InventoryItemId}', dataField: 'inventoryItemId', align: 'left', width: 100, editable:false },
                   { text: '${uiLabelMap.ProductId}', dataField: 'productId', align: 'left', width: 130, editable:false },
                   { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'left', minwidth: 200, editable:false },
	               { text: '${uiLabelMap.EnterAmount}', dataField: 'quantity', align: 'left', width: 130, cellsalign: 'right', columntype:'numberinput', filtertype: 'number', 
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
							return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
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
	               { text: '${uiLabelMap.ReceivedDate}', dataField: 'datetimeReceived', align: 'left', width: 120, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left' },
	               { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', align: 'left', width: 120, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left' },
	               { text: '${uiLabelMap.Unit}', dataField: 'uomId', align: 'left', editable:false, width: 120,
	            	  cellsrenderer: function(row, colum, value){
	    			        var data = $("#" + gridIdToActive).jqxGrid('getrowdata', row);
	    			        var productId = data.productId;
	    			        if (productId == undefined) {
								return null;
							}
	    			        if (value){
	    			        	return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
	    			        } else {
	    			        	if (data.quantityUomId){
	    			        		return '<span>' + mapQuantityUom[data.quantityUomId] + '</span>';
	    			        	} else {
	    			        		return '<span>_NA_</span>';
	    			        	}
	    			        }
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
	
	function renderHtmlMover() {
		var moveTabsFrom = "<div id='jqxTabsMoverFrom' style='height: 260px !important; margin-left: 20px !important; margin-right: 20px !important; border: 1px solid #CCC !important;'><ul style='margin-left: 8px'>"; 
		var moveGridsFrom = "";
		for ( var f in arrayLocationIdFrom) {
			var gridId = "jqxGridsFrom" + arrayLocationIdFrom[f];
			moveTabsFrom += "<li style='margin-top: 6px; height: 15px !important; border-bottom-width: 0px; border-color: white;'>" + getLocationCode(arrayLocationIdFrom[f]) + "</li>";
			moveGridsFrom += "<div style='overflow: hidden; height: 218px !important;'><div style='border:none;' id=" + gridId + " ></div></div>";
		}
		moveTabsFrom += "</ul>" + moveGridsFrom + "</div>";
		$("#jqxTabsFrom").html(moveTabsFrom);
		
		
		var moveTabsTo ="<div id='jqxTabsMoverTo' style='height: 260px !important; margin-left: 20px !important; margin-right: 20px !important; border: 1px solid #CCC !important;'><ul style='margin-left: 8px'>";
		var moveGridsTo = "";
		for ( var t in arrayLocationIdTo) {
			var gridId = "jqxGridsTo" + arrayLocationIdTo[t];
			moveTabsTo += "<li style=\'margin-top: 6px; height: 15px !important; border-bottom-width: 0px; border-color: white;'>" + getLocationCode(arrayLocationIdTo[t]) + "</li>";
			moveGridsTo += "<div style='overflow: hidden; height: 218px !important;'><div style='border:none;' id=" + gridId + " ></div></div>";
		}
		moveTabsTo += "</ul>" + moveGridsTo + "</div>";
		$("#jqxTabsTo").html(moveTabsTo);
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
	
	function renderHtmlContainGrids() {
		var htmlRenderTabs = "<div id='jqxTabsLocation' style='margin-left: 20px !important; margin-right: 20px !important; border: 1px solid #CCC !important;'><ul style='margin-left: 8px' id='tabDynamic'>";
		var htmlRenderGrids = "";
		for ( var x in arrayLocationId) {
			var gridId = "jqxGrid" + arrayLocationId[x];
	        htmlRenderTabs += "<li value=" + arrayLocationId[x] + " style=\"margin-top: 6px; height: 15px !important; border-bottom-width: 0px; border-color: white;\">" + getLocationCode(arrayLocationId[x]) + "</li>";
	        htmlRenderGrids += "<div style='overflow: hidden;'><div style='border:none;' id=" + gridId + " ></div></div>";
		}
		htmlRenderTabs += "</ul>" + htmlRenderGrids + "</div>";
		$("#jqxTabsContain").html(htmlRenderTabs);
	}
	function changeTargetWhenGridActive() {
		var activeGrid = "jqxGrid" + idGridActive;
		setTimeout(function() {
    		var gridCells = $('#gridFrom').find('.dragable');
    		if (gridCells.length == 0) {
				return;
			}
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
                	var statusId = dataRowFrom.statusId;
                	var inventoryItemId = dataRowFrom.inventoryItemId;
                	var productName = dataRowFrom.productName;
                	var productId = dataRowFrom.productId;
                	/*var quantityOnHandTotal = dataRowFrom.quantityOnHandTotal;*/
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'productId', productId);
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'datetimeReceived', datetimeReceived);
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'expireDate', expireDate);
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'statusId', statusId);
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'inventoryItemId', inventoryItemId);
                	$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'productName', productName);
                	/*$("#" + activeGrid).jqxGrid('setcellvalue', 0, 'quantity', quantityOnHandTotal);*/
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
                { name: 'productCode', type: 'string' },
                { name: 'productName', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' },
                { name: 'statusId', type: 'string' },
                { name: 'quantityUomId', type: 'string' },
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
                    height: 188,
                    theme: 'olbius',
                    selectionmode: 'singlerow',
                    editable:true,
                    pageable: true,
                    sortable: true,
                    columns: [
                       { text: '${uiLabelMap.InventoryItemId}', dataField: 'inventoryItemId', align: 'left', width: 100, editable:false },
                       { text: '${uiLabelMap.ProductId}', dataField: 'productId', align: 'left', width: 120, editable:false },
                       { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'left', minwidth: 200, editable:false },
                       { text: '${StringUtil.wrapString(uiLabelMap.Status)}', dataField: 'statusId', align: 'left', width: 150, editable:false,
	       					cellsrenderer: function(row, colum, value){
	       						for(i=0; i < statusData.length; i++){
	       				            if(statusData[i].statusId == value){
	       				            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData[i].description + '</span>';
	       				            }
	       				        }
	       						if (!value){
	       			            	return '<span style=\"text-align: left;\" title='+value+'>${uiLabelMap.InventoryGood}</span>';
	       						}
	       					}
       					},
    	               { text: '${uiLabelMap.EnterAmount}', dataField: 'quantity', align: 'left', width: 120, cellsalign: 'right', columntype:"numberinput", 
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
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
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
    	               { text: '${uiLabelMap.ReceivedDate}', dataField: 'datetimeReceived', align: 'left', width: 130, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left' },
    	               { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', align: 'left', width: 130, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left' },
    	               { text: '${uiLabelMap.Unit}', dataField: 'uomId', align: 'left', editable:false, width: 120,
 		            	  cellsrenderer: function(row, colum, value){
 		    			        var data = $("#" + activeGrid).jqxGrid('getrowdata', row);
 		    			        var productId = data.productId;
 		    			        if (productId == undefined) {
									return null;
								}
 		    			        if (value){
 		    			        	return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
 		    			        } else {
 		    			        	if (data.quantityUomId){
 		    			        		return '<span>' + mapQuantityUom[data.quantityUomId] + '</span>';
 		    			        	} else {
 		    			        		return '<span>_NA_</span>';
 		    			        	}
 		    			        }
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
		setTimeout(function(){
    		$("#viewProduct").attr('disabled', true);
    		$("#addProduct").attr('disabled', true);
    		$("#moveProductTo").attr('disabled', true);
    		$("#btnCancelReset").attr('disabled', true);
    	}, 500);
		$('#jqxTabsLocation').jqxTabs('destroy');
		reset();
		enableScrolling();
	});
	$("#jqxwindowPopupAdderProductToLocation").on('open', function (event) {
		disableScrolling();
	});
	
	$("#btnCancelReset").click(function () {
		moveMode = false;
		updateMode = false;
		reset();
		$("#viewProduct").attr('disabled', true);
		$("#addProduct").attr('disabled', true);
		$("#moveProductTo").attr('disabled', true);
		$("#btnCancelReset").attr('disabled', true);
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
	    if (LocationFacilityObj.checkHasChild(locationId)) {
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
		    if (LocationFacilityObj.checkHasChild(locationId)) {
		    	arrayRowChecked.splice(idex, 1);
		    	refreshLocationId();
			}
		var idexAll = allRowsChecked.indexOf(locationId);
		allRowsChecked.splice(idexAll, 1);
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
	
    function getChildOfLocation(parentLocationFacilityTypeId) {
    	var listChild = [];
		for ( var x in listLocationFacilityType) {
			if (parentLocationFacilityTypeId == listLocationFacilityType[x].parentLocationFacilityTypeId) {
				listChild.push(listLocationFacilityType[x].locationFacilityTypeId);
			}
		}
		return listChild;
	}
    
	var listLocationFacilityType = [
							<#if listLocationFacilityType?exists>
							<#list listLocationFacilityType as item>
								<#if item.description?has_content>
									<#assign desc = StringUtil.wrapString(item.description)>
								<#else>
									<#assign desc = "">
								</#if>
								<#if item.locationFacilityTypeId?has_content>
									<#assign typeId = StringUtil.wrapString(item.locationFacilityTypeId)>
								<#else>
									<#assign typeId = "">
								</#if>
								<#if item.parentLocationFacilityTypeId?has_content>
									<#assign parentTypeId = StringUtil.wrapString(item.parentLocationFacilityTypeId)>
								<#else>
									<#assign parentTypeId = "">
								</#if>
								{
									description: '${desc}',
									locationFacilityTypeId: '${typeId}',
									parentLocationFacilityTypeId: '${parentTypeId}',
								},
							</#list>
							</#if>
							];
	function getLocationFacilityType(locationFacilityTypeId) {
		if (locationFacilityTypeId != null) {
			for ( var x in listLocationFacilityType) {
				if (locationFacilityTypeId == listLocationFacilityType[x].locationFacilityTypeId) {
					return listLocationFacilityType[x].locationFacilityTypeId; 
				}
			}
		} else {
			return "";
		}
	}
	var listProduct = [];
	<#if listProduct?exists>
		<#list listProduct as item>
			var row = {};
			row["quantityUomId"] = "${item.quantityUomId?if_exists}";
			row["productId"] = "${item.productId?if_exists}";
			row["productName"] = "${StringUtil.wrapString(item.productName?if_exists)}";
			listProduct.push(row);
		</#list>
	</#if>
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
				"${item.productId?if_exists}": "${StringUtil.wrapString(item.productName?if_exists)}",
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
					item.productName = listProduct[g].productName;
					break;
				}
			}
			listProductAvalibleInFacility.push(item);
		}
		return listProductAvalibleInFacility;
	}
	
	var arrayVarianceReason = [];
	var locale = '${locale}';
	var listProductAvalibleInFacilitySearch;
    $(document).ready(function () {
    	setTimeout(function(){
    		$("#viewProduct").attr('disabled', true);
    		$("#addProduct").attr('disabled', true);
    		$("#moveProductTo").attr('disabled', true);
    		$("#btnCancelReset").attr('disabled', true);
    		$("#moveProduct").attr('disabled', true);
    		$("#btnReset").attr('disabled', true);
    	}, 500);
    	
    	$.jqx.theme = 'olbius';
    	theme = $.jqx.theme;
    	
    	getGeneralQuantity();
    	LocationFacilityObj.getListlocationFacility(facilityIdGlobal);
    	checkHasInventoryInLocation();
    	if (checkProductNotLocationAjax()) {
			$("#divHasProductsNotLocation").text("${StringUtil.wrapString(uiLabelMap.HasProductsNotLocation)}");
		}else {
			$("#divHasProductsNotLocation").text("");
		}
        $("#jqxInputSearch").jqxInput({ placeHolder: " Search", displayMember: "productName", valueMember: "productId", width: 500, height: 25,
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
    
    function clearPopup() {
    	$("#txtLocationCode").val("");
    	$("#txtLocationCodeInArea").val("");
    	$("#txtLocationCodeAisle").val("");
    	$("#tarDescriptionInArea").val("");
    	$("#locationDescription").val("");
	}
    
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
        localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.NoDataToDisplay)}";
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
		height: 25px;
		color: #037C07 !important;
		font-weight: 400;
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
<#include 'listAddProductLocationFacility.ftl'/>
<#include 'viewProductInLocationFacility.ftl'/>