<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>

<#assign listQuantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
<script>
	
	var mapQuantityUoms = {
		<#if listQuantityUoms?exists>
			<#list listQuantityUoms as item>
					'${item.uomId?if_exists}': '${StringUtil.wrapString(item.description?if_exists)}',
			</#list>
		 </#if>
	};

	var facilityId = '${parameters.facilityId}';
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	var mapInventoryItem = new Object();
	function updateInventoryItem(newdata, originalQuantityOnHandTotal) {
		var quantityUomId = newdata.quantityUomId;
		var productId = newdata.productId;
		var inventoryItemId = newdata.inventoryItemId;
		var quantityOnHandTotal = newdata.quantityOnHandTotal;
		var difference = quantityOnHandTotal - originalQuantityOnHandTotal;
		var resultUpdate = createInventoryItemDetailAjax(inventoryItemId, difference);
		if (!resultUpdate) {
			return false;
		}
		var valueOriginalQuantityOnHandTotalProduct = $('#jqxgrid').jqxGrid('getCellValueByid', productId, "quantityOnHandTotal");
		var updateOriginalQuantityOnHandTotalProduct = valueOriginalQuantityOnHandTotalProduct + difference;
		$("#jqxgrid").jqxGrid('SetCellValueByid', productId, "quantityOnHandTotal", updateOriginalQuantityOnHandTotalProduct);
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
	  		  data: {inventoryItemId: inventoryItemId, quantityOnHandDiff: quantityOnHandDiff},
	  		  success: function(res) {
	  			  res["_ERROR_MESSAGE_LIST_"]?result=false:result=true;
	  			  res["_ERROR_MESSAGE_"]?result=false:result=true;
	  		  }
		  	});
		return result;
	}
	var locale = '${locale}';
	$(document).ready(function () {
    	locale == "vi_VN"?locale="vi":locale=locale;
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
	
//	reponsiveRowDetails(grid);
		var sourceGridDetail =
        {
            localdata: datarecord.rowDetail,
            datatype: 'local',
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'facilityId', type: 'number' },
                { name: 'internalName', type: 'string' },
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
            	bootbox.confirm('${uiLabelMap.OriginalQuantityIs} ' + originalQuantityOnHandTotal.toLocaleString(locale) + ', ${uiLabelMap.NewQuantityIs} ' + quantityOnHandTotal.toLocaleString(locale) + '. ${uiLabelMap.DAAreYouSureUpdate}', function(result) {
        			if (result) {
        				commit(updateInventoryItem(newdata, originalQuantityOnHandTotal));
        			}else {
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
	 		pageable: true,
	 		editable: true,
            selectionmode: 'singlerow',
            columns: [
						{ text: '${uiLabelMap.ProductProductName}', datafield: 'internalName', align: 'center', editable:false},
						{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'center', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center', editable:false },
						{ text: '${uiLabelMap.FormFieldTitle_quantityOnHandTotal}', datafield: 'quantityOnHandTotal', align: 'center', cellsalign: 'right', width: 200, cellsalign: 'right', columntype:'numberinput',
							cellsrenderer: function(row, colum, value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
						    },
						    createeditor: function(row, column, editor){
								editor.jqxNumberInput({ theme: 'olbius', inputMode: 'simple', decimalDigits: 0 });
							},validation: function (cell, value) {
								if (value >= 0) {
									return true;
								}
								return { result: false, message: '${uiLabelMap.QuantityNotValid}' };
							}
						},
						{ text: '${uiLabelMap.FormFieldTitle_availableToPromiseTotal}', datafield: 'availableToPromiseTotal', align: 'center', cellsalign: 'right', width: 250, cellsalign: 'right', columntype:'numberinput', editable:false,
							cellsrenderer: function(row, colum, value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
						    }
						},
						{ text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', align: 'center', width: 130, editable:false,
							cellsrenderer: function(row, colum, value){
								return '<span style=\"text-align: center;\">' +  mapQuantityUoms[value] + '</span>';
						    },
						},
						{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', align: 'center', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center', editable:false }
                     ]
        });
 }"/>
	<#assign dataField="[
					{ name: 'productId', type: 'string'},
					{ name: 'internalName', type: 'string' },
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'availableToPromiseTotal', type: 'number' },
					{ name: 'accountingQuantityTotal', type: 'number' },
					{ name: 'rowDetail', type: 'string'}
				]"/>
	<#assign columnlist="
					{ text: '${uiLabelMap.ProductProductId}', datafield: 'productId', align: 'center', width: 250},
					{ text: '${uiLabelMap.ProductProductName}', datafield: 'internalName', align: 'center'},
					{ text: '${uiLabelMap.FormFieldTitle_quantityOnHandTotal}', datafield: 'quantityOnHandTotal', align: 'center', cellsalign: 'right', width: 250, cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					    }, 
					},
					{ text: '${uiLabelMap.FormFieldTitle_availableToPromiseTotal}', datafield: 'availableToPromiseTotal', cellsalign: 'right', align: 'center', width: 250, cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString(locale) + '</span>';
					    },
					},
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		initrowdetails = "true" initrowdetailsDetail=initrowdetailsDetail sourceId="productId"
		url="jqxGeneralServicer?sname=JQXgetListInventoryItem&facilityId=${parameters.facilityId}"
	/>
</div>
<style>
.bootbox{
	  z-index: 20001 !important;
	 }
	 .modal-backdrop{
	  z-index: 20000 !important;
	 }
</style>