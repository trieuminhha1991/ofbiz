<script type="text/javascript" src="/crmresources/js/bootbox.min.js"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/crmresources/js/progressing.js"></script>
<script type="text/javascript" src="/crmresources/js/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#assign listQuantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
<script>
	var editable = false;
	<#if security.hasEntityPermission("INVENTORY_ITEM", "_UPDATE", session)>
	editable = true;
	</#if>
	var mapQuantityUoms = {<#if listQuantityUoms?exists><#list listQuantityUoms as item>
		'${item.uomId?if_exists}': '${StringUtil.wrapString(item.description?if_exists)}',
	</#list></#if>};
	var facilityId = '${parameters.facilityId?if_exists}';
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
	var facilityData = new Array();
	<#if parameters.facilityCondition?exists && parameters.facilityCondition == "owner">
		<#assign listFacilities = Static["com.olbius.baselogistics.util.LogisticsPartyUtil"].getFacilityByRoles(delegator, userLogin.get("partyId"), Static["org.ofbiz.base.util.UtilMisc"].toList("MANAGER", "OWNER"))!/>
		<#list listFacilities as item>
			var row = {};
			row['facilityId'] = "${item.facilityId}";
			row['facilityName'] = "${StringUtil.wrapString(item.facilityName?if_exists)}";
			facilityData.push(row);
		</#list>
	<#elseif parameters.facilityCondition?exists && parameters.facilityCondition == "deposit">
		<#assign listFacilities = Static["com.olbius.baselogistics.util.LogisticsFacilityUtil"].listDepositFacilities(delegator, userLogin.get("userLoginId"))!/>
		<#list listFacilities as item>
			<#if item.ownerPartyId == company>
				var row = {};
				row['facilityId'] = "${item.facilityId}";
				row['facilityName'] = "${StringUtil.wrapString(item.facilityName)}";
				facilityData.push(row);
			</#if>
		</#list>
	</#if>
	
	var mapInventoryItem = new Object();
	function updateInventoryItem(newdata, originalQuantityOnHandTotal, grid) {
		var quantityUomId = newdata.quantityUomId;
		var productId = newdata.productId;
		var inventoryItemId = newdata.inventoryItemId;
		var quantityOnHandTotal = newdata.quantityOnHandTotal;
		var difference = quantityOnHandTotal - originalQuantityOnHandTotal;
		var resultUpdate = createInventoryItemDetailAjax(inventoryItemId, difference);
		if (!resultUpdate) {
			return false;
		}
		var valueOriginalQOH = $('#jqxgrid').jqxGrid('getCellValueByid', productId, "quantityOnHandTotal");
		valueOriginalQOH = valueOriginalQOH + difference;
		$("#jqxgrid").jqxGrid('SetCellValueByid', productId, "quantityOnHandTotal", valueOriginalQOH);
		var valueOriginalATP = $('#jqxgrid').jqxGrid('getCellValueByid', productId, "availableToPromiseTotal");
		
		valueOriginalATP = valueOriginalATP + difference;
		$("#jqxgrid").jqxGrid('SetCellValueByid', productId, "availableToPromiseTotal", valueOriginalATP);
		if (grid) {
			grid.jqxGrid('SetCellValueByid', inventoryItemId, "availableToPromiseTotal", valueOriginalATP);
		}
		return true;
	}
	function getOriginalQuantityOnHandTotalAjax(inventoryItemId) {
		var quantityOnHandTotal = 0;
		$.ajax({
  		  url: "getOriginalQuantityOnHandTotalAjax",
  		  type: "POST",
  		  data: {inventoryItemId: inventoryItemId},
  		  async: false,
  		  success: function(res) {
  			quantityOnHandTotal = res["quantityOnHandTotal"];
  		  }
	  	});
		return quantityOnHandTotal;
	}
	function createInventoryItemDetailAjax(inventoryItemId, quantityOnHandDiff) {
		var result;
		$.ajax({
	  		  url: "createInventoryItemDetailAjax",
	  		  type: "POST",
	  		  async: false,
	  		  data: {inventoryItemId: inventoryItemId, quantityOnHandDiff: quantityOnHandDiff, availableToPromiseDiff: quantityOnHandDiff},
	  		  success: function() {}
		  	}).done(function(res) {
		  		res["_ERROR_MESSAGE_LIST_"]?result=false:result=true;
	  			res["_ERROR_MESSAGE_"]?result=false:result=true;
		  		$('#jqxNotificationNested').jqxNotification('closeLast');
		  		if (result) {
		  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
		          	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
		          	$("#jqxNotificationNested").jqxNotification("open");
				} else {
					$("#jqxNotificationNested").jqxNotification({ template: 'error'});
	    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
	              	$("#jqxNotificationNested").jqxNotification("open");
				}
			});
		return result;
	}
	var locale = '${locale}';
	$(document).ready(function () {
    	locale == "vi_VN"?locale="vi":locale=locale;
    	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	});
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
<div>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
		var sourceGridDetail =
        {
            localdata: datarecord.rowDetail,
            datatype: 'local',
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'productCode', type: 'string' },
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'facilityId', type: 'number' },
                { name: 'productName', type: 'string' },
				{ name: 'quantityOnHandTotal', type: 'number' },
				{ name: 'availableToPromiseTotal', type: 'number' },
				{ name: 'quantityUomId', type: 'string' }
            ],
            addrow: function (rowid, rowdata, position, commit) {
            	
                commit(false);
            },
            deleterow: function (rowid, commit) {
                
                commit(false);
            },
            updaterow: function (rowid, newdata, commit) {
            	$('#jqxgrid').jqxGrid('showloadelement');
            	var inventoryItemId = newdata.inventoryItemId;
            	var originalQuantityOnHandTotal = getOriginalQuantityOnHandTotalAjax(inventoryItemId);
            	var quantityOnHandTotal = newdata.quantityOnHandTotal;
            	
        		var difference = quantityOnHandTotal - originalQuantityOnHandTotal;
        		if (difference == 0) {
        			commit(true);
        			$('#jqxgrid').jqxGrid('hideloadelement');
        			return;
				}
            	
            	bootbox.confirm('${uiLabelMap.OriginalQuantityIs} ' + originalQuantityOnHandTotal.toLocaleString(locale) + ', ${uiLabelMap.NewQuantityIs} ' + quantityOnHandTotal.toLocaleString(locale) + '. ${uiLabelMap.DAAreYouSureUpdate}', function(result) {
        			if (result) {
        				commit(updateInventoryItem(newdata, originalQuantityOnHandTotal, grid));
        			} else {
        				commit(false);
					}
        		});
            	$('#jqxgrid').jqxGrid('hideloadelement');
            },
            id: 'inventoryItemId'
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
            showfilterrow: true,
            filterable: true,
	 		pageable: true,
	 		editable: editable,
            selectionmode: 'singlerow',
            columns: [
			{ text: '${uiLabelMap.ProductProductName}', datafield: 'productName', editable:false},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable:false },
			{ text: '${uiLabelMap.FormFieldTitle_quantityOnHandTotal}', datafield: 'quantityOnHandTotal', cellsalign: 'right', width: 200, filtertype: 'number', columntype:'numberinput',
				cellsrenderer: function(row, colum, value){
					return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
			    }, createeditor: function(row, column, editor){
					editor.jqxNumberInput({ theme: 'olbius', inputMode: 'simple', decimalDigits: 0 });
				}, validation: function (cell, value) {
					if (value >= 0) {
						return true;
					}
					return { result: false, message: '${uiLabelMap.QuantityNotValid}' };
				}
			},
			{ text: '${uiLabelMap.FormFieldTitle_availableToPromiseTotal}', datafield: 'availableToPromiseTotal', cellsalign: 'right', width: 250, filtertype: 'number', columntype:'numberinput', editable:false,
				cellsrenderer: function(row, colum, value){
					return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
			    }
			},
			{ text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', width: 130, editable:false,
				cellsrenderer: function(row, colum, value){
					return '<span style=\"text-align: center;\">' +  mapQuantityUoms[value] + '</span>';
			    }
			},
			{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable:false }]
        });
    }"/>
	<#assign dataField="[{ name: 'productId', type: 'string'},
					{ name: 'productName', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'availableToPromiseTotal', type: 'number' },
					{ name: 'accountingQuantityTotal', type: 'number' },
					{ name: 'rowDetail', type: 'string'}]"/>
	<#assign columnlist="{ text: '${uiLabelMap.ProductProductId}', datafield: 'productCode', width: 250},
					{ text: '${uiLabelMap.ProductProductName}', datafield: 'productName'},
					{ text: '${uiLabelMap.FormFieldTitle_quantityOnHandTotal}', datafield: 'quantityOnHandTotal', cellsalign: 'right', width: 250, filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					    }
					},
					{ text: '${uiLabelMap.FormFieldTitle_availableToPromiseTotal}', datafield: 'availableToPromiseTotal', cellsalign: 'right', width: 250, filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					    }
					}"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail sourceId="productId" clearfilteringbutton="false"
		url="jqxGeneralServicer?sname=JQXgetListInventoryItem&facilityId=${parameters.facilityId?if_exists}&type=${parameters.facilityCondition?if_exists}"
		customtoolbaraction="viewMethod01"/>
</div>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<script>
	var viewMethod01 = function(container){
//		<#if parameters.facilityCondition?exists><#if parameters.facilityCondition == "deposit">
//			var str = "<div class='pull-right' style='margin-top: 10px;'><a style='cursor: pointer;' onclick='ReportWarehouse.send()'><i class='fa-paper-plane-o'></i>${StringUtil.wrapString(uiLabelMap.BSReportWarehouseInventoried)}</a></div>";
//			container.append(str);
//		</#if></#if>
		
		var str = "<div id='facilityId' class='pull-right margin-top5' style='margin-top: 4px;margin-right: 5px;'></div>";
		container.append(str);
	     $("#facilityId").jqxComboBox({placeHolder: '${StringUtil.wrapString(uiLabelMap.Facility)}', checkboxes: true, source: facilityData, displayMember: "facilityName", valueMember: "facilityId", width: 200, height: 25});
	     $("#facilityId").jqxComboBox('checkAll'); 
	     $("#facilityId").on('checkChange', function (event) {
	         if (event.args) {
	        	 var listFaTmp = $("#facilityId").jqxComboBox('getCheckedItems');
	        	 var listFas = new Array();
	        	 for (var i = 0; i < listFaTmp.length; i ++){
	        		 var row = {};
	        		 row["facilityId"] = listFaTmp[i].value;
	        		 listFas.push(row);
	        	 }
	        	 if (listFas.length > 0){
	        		 listFas = JSON.stringify(listFas);
		        	 var tmpS = $("#jqxgrid").jqxGrid('source');
		        	 tmpS._source.url = "jqxGeneralServicer?sname=JQXgetListInventoryItem&type=${parameters.facilityCondition?if_exists}&listFacilityIds="+listFas;
		        	 $("#jqxgrid").jqxGrid('source', tmpS);
	        	 } else {
	        		 var tmpS = $("#jqxgrid").jqxGrid('source');
		        	 tmpS._source.url = "jqxGeneralServicer?sname=JQXgetListInventoryItem&type=${parameters.facilityCondition?if_exists}";
		        	 $("#jqxgrid").jqxGrid('source', tmpS);
	        	 }
	         }
	     });
	}
	var ReportWarehouse = (function() {
		var payToPartyName = "${(facility.payToPartyName)?if_exists}";
		var send = function() {
			bootbox.confirm("${StringUtil.wrapString(uiLabelMap.BSReportWarehouseInventoriedTo)} " + payToPartyName, "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}", function(result) {
				if (result) {
					DataAccess.execute({
						url: "createNotification",
						data: {partyId: "OLBMBLOGM", header: "${StringUtil.wrapString(uiLabelMap.BSThereIsReportWarehouseInventoried)}" + ": " + "${(facility.facilityName)?if_exists}", action: "getInventory",
								targetLink: "sub=DepositFacilities;facilityId=" + facilityId, ntfType: "ONE", sendToSender: "Y", sendToGroup: "Y"}
						});
				}
			});
		};
		return {
			send: send
		};
	})();
</script>