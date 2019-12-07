<script>
	<#assign deliveryBoxTypes = delegator.findList("DeliveryBoxType", null, null, null, null, false) />
	var boxTypeData = new Array();
	<#list deliveryBoxTypes as item>
		var row = {};
		row['deliveryBoxTypeId'] = '${item.deliveryBoxTypeId}';
		row['description'] = '${item.description}';
		boxTypeData[${item_index}] = row;
	</#list>
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.abbreviation) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		weightUomData[${item_index}] = row;
	</#list>
	<#assign deliveryBoxTypes = delegator.findList("DeliveryBoxType", null, null, null, null, false)>
	var deliveryBoxTypeData = new Array();
	<#list deliveryBoxTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description) />
		row['deliveryBoxTypeId'] = '${item.deliveryBoxTypeId}';
		row['description'] = '${description?if_exists}';
		deliveryBoxTypeData[${item_index}] = row;
	</#list>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = new Array();
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.abbreviation) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		quantityUomData[${item_index}] = row;
	</#list>
</script>
<#assign packageRowDetail = "function (index, parentElement, gridElement, datarecord) {
 	var packageDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
    packages = packageDataAdapter.records;
	
	 var nestedGrids = new Array();
     var id = datarecord.uid.toString();
    
     var grid = $($(parentElement).children()[0]);
     $(grid).attr('id', 'jqxgridDetail_'+index);
     nestedGrids[index] = grid;
   
     var packagebyid = [];
     for (var m = 0; m < packages.length; m++) {
    	 packagebyid.push(packages[m]);
     }
     var packagessource = { datafields: [
             { name: 'deliveryEntryId', type:'string' },
			 { name: 'deliveryPackageSeqId', type:'string' },
			 { name: 'shipmentId', type:'string' },
         	 { name: 'shipmentItemSeqId', type:'string' },
         	 { name: 'productId', type:'string' },
         	 { name: 'productName', type:'string' },
         	 { name: 'weight', type:'string' },
         	 { name: 'weightUomId', type:'string' },
             { name: 'quantity', type: 'number' },
             { name: 'quantityUomId', type:'string' },
             { name: 'totalWeight', type:'string' },
         ],
         localdata: packagebyid,
         updaterow: function (rowid, newdata, commit) {
        	 commit(true);
        	 var deliveryEntryId = newdata.deliveryEntryId;
        	 var shipmentPackageSeqId = newdata.shipmentPackageSeqId;
        	 var shipmentId = newdata.shipmentId;
        	 var shipmentItemSeqId = newdata.shipmentItemSeqId;
        	 var quantity = newdata.quantity;
        	 
        	 $.ajax({
                 type: 'POST',                        
                 url: 'updateDeliveryPackageContent',
                 data: { deliveryEntryId: deliveryEntryId, delivertPackageSeqId: deliveryPackageSeqId, shipmentId: shipmentId, shipmentItemSeqId: shipmentItemSeqId, quantity: quantity},
                 success: function (data, status, xhr) {
                     if (data.responseMessage == 'error'){
                     	commit(false);
                     } else{
                     	commit(true);
                     	grid.jqxGrid('updatebounddata');
                     }
                 },
                 error: function () {
                     commit(false);
                 }
             });
         }
     }
     var nestedGridAdapter = new $.jqx.dataAdapter(packagessource);
    
     if (grid != null) {
    	 var a ;
         grid.jqxGrid({
             source: nestedGridAdapter, width: '96%', autoheight:'true',
             showtoolbar:false,
             showstatusbar: false,
	 		 editable: true,
	 		 editmode:'selectedrow',
	 		 showheader: true,
	 		 selectionmode:'singlerow',
	 		 columnsresize: true,
	 		 rowsheight: 28,
	 		 pagesize: 5,
	 		 pageable: true,
	 		 pagesizeoptions: ['5', '10', '15'],
	 		 theme: 'olbius',
             columns: [
                   { text: '${uiLabelMap.Product}', datafield: 'productId', width: 150 , editable: false, cellsalign: 'center'},
                   { text: '${uiLabelMap.Quantity}', dataField: 'quantity', width: 150, editable: false, cellsalign: 'center', 
						cellsrenderer: function(row, column, value){
							var data = grid.jqxGrid('getrowdata', row);
							for(var i = 0; i < quantityUomData.length; i++){
								if(quantityUomData[i].uomId == data.quantityUomId){
									return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +' ('+ quantityUomData[i].description + ')</span></div>'
								}
							}
						}	
                   },
                   { text: '${uiLabelMap.weight}', dataField: 'weight', width: 150, editable: false, cellsalign: 'center',
                	   cellsrenderer: function(row, column, value){
							var data = grid.jqxGrid('getrowdata', row);
							for(var i = 0; i < weightUomData.length; i++){
								if(weightUomData[i].uomId == data.weightUomId){
									return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +' ('+ weightUomData[i].description + ')</span>'
								}
							}
						}	
                   },
                   { text: '${uiLabelMap.TotalWeight}', dataField: 'totalWeight', editable: false, cellsalign: 'center',
                	   cellsrenderer: function(row, column, value){
							var data = grid.jqxGrid('getrowdata', row);
							var totalWeight = (parseFloat(data.weight) * parseFloat(data.quantity)).toFixed(2);
							for(var i = 0; i < weightUomData.length; i++){
								if(weightUomData[i].uomId == data.weightUomId){
									return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + totalWeight +' ('+ weightUomData[i].description + ')</span>'
								}
							}
						}
                   },
               ]
         });
     }
}"/>
<#assign dataField="[{ name: 'deliveryEntryId', type: 'string'},
				   { name: 'deliveryPackageSeqId', type: 'string'},
				   { name: 'deliveryBoxTypeId', type: 'string'},
				   { name: 'weight', type: 'number'},
				   { name: 'weightUomId', type: 'string'},
				   { name: 'rowDetail', type: 'string'},
				   ]"/>
						   
<#assign columnlist="{ text: '${uiLabelMap.SequenceId}', datafield: 'deliveryPackageSeqId', width: 110, editable: false,
					},
					{text: '${uiLabelMap.BoxType}', datafield: 'deliveryBoxTypeId', minwidth: 150, filtertype:'input', editable: false, cellsrenderer:
						function(row, colum, value){
							var data = $('#jqxgridDeliveryEntryPackage').jqxGrid('getrowdata', row);
							if (data && data.deliveryBoxTypeId){
								for(var i = 0; i < boxTypeData.length; i++){
								   	if(boxTypeData[i].deliveryBoxTypeId == data.deliveryBoxTypeId){
								   		return '<span title=' + value + '>' + boxTypeData[i].description + '</span>'
								 	}
							   	}
							}
						}
					},
					{ text: '${uiLabelMap.weight}',  datafield: 'weight', minwidth: 120, editable: false,
						cellsrenderer: function(row, colum, value){
							   	var data = $('#jqxgridDeliveryEntryPackage').jqxGrid('getrowdata', row);
							   	if (data && data.deliveryBoxTypeId){
							   		var weightUom;
								   	for(var i = 0; i < weightUomData.length; i++){
									   	if(weightUomData[i].uomId == data.weightUomId){
									   		weightUom = weightUomData[i].description;
									 	}
								   	}
								    return '<span>' + value +' (' + weightUom +  ')</span>';
							   	}
							   	
							}
					   },
					   "/>
<@jqGrid filtersimplemode="true" addType="popup" id="jqxgridDeliveryEntryPackage" dataField=dataField columnlist=columnlist clearfilteringbutton="true" initrowdetailsDetail=packageRowDetail initrowdetails = "true"
					showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
					url="jqxGeneralServicer?sname=getDeliveryEntryPackages&deliveryEntryId=${parameters.deliveryEntryId?if_exists}"
					createUrl="jqxGeneralServicer?sname=createDeliveryEntryPackage&jqaction=C"  customCss="sdemgt"
					updateUrl="jqxGeneralServicer?sname=updateDeliveryEntryPackage&jqaction=U"
					addColumns="deliveryEntryId;deliveryPackageSeqId;deliveryBoxTypeId;listShipmentItems(java.util.List);"
/>
<style type="text/css">
    .sdemgt{
        margin-top: 10px !important;
    }
</style>
<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.accCreateNew}</div>
	<div style='height: 400px;'>
	<input type="hidden" value="${parameters.deliveryEntryId?if_exists}" id="deliveryEntryId"/>
	<div style="overflow: scroll; height: 420px;">
		<div class='row-fluid' style="margin-left: 50px">
			<div class='span12'>
				<h4 class="row header smaller lighter blue">
				${uiLabelMap.PackageInfo}
				</h4>
			</div>
		</div>
		<div class='row-fluid' style="margin-left: 50px">
			<div class='span3' style="text-align: right; line-height: 25px;">
				${uiLabelMap.BoxType}:
			</div>
			<div class='span3'>
				<div id="deliveryBoxTypeId">
				</div>
			</div>
		</div>
		<div class='row-fluid'>
			<div class='span12'>
				<div style="margin-left: 50px"><#include "listShipmentToPacking.ftl" /></div>
			</div>
		</div>   
	</div>
	</div>
	<div class='row-fluid' style="position: absolute; bottom: 10px; z-index: 1000">
		<div class='span6'>
			<div class='pull-right'>
				<input type="button" id="alterSave" value="${uiLabelMap.CommonSave}" />
			</div>
		</div>
		<div class='span6'>
			<input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" />
		</div>
	</div>  
</div>
<script type="text/javascript">

	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#deliveryBoxTypeId").jqxDropDownList({source: deliveryBoxTypeData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "deliveryBoxTypeId"});
	
	$("#alterSave").jqxButton({width: 100, theme: theme});
	$("#alterCancel").jqxButton({width: 100, theme: theme});
	
	// update the edited row when the user clicks the 'Save' button.
	$("#alterSave").click(function () {
		var row;
		//Get List Order Item
		var selectedIndexs = $('#jqxgridShipmentByDE').jqxGrid('getselectedrowindexes');
		var listShipmentItems = new Array();
		
		for(var i = 0; i < selectedIndexs.length; i++){
			var dataShipment = $('#jqxgridShipmentByDE').jqxGrid('getrowdata', selectedIndexs[i]);
			
			var shipmentIdTmp = dataShipment.shipmentId;
			var itemSelectedIndexs = $('#jqxgridDetail_'+shipmentIdTmp).jqxGrid('getselectedrowindexes');
			for(var j = 0; j < itemSelectedIndexs.length; j++){
				var dataShipmentItem = $('#jqxgridDetail_'+shipmentIdTmp).jqxGrid('getrowdata', itemSelectedIndexs[j]);
				var map = {};
				map['shipmentItemSeqId'] = dataShipmentItem.shipmentItemSeqId;
				map['shipmentId'] = dataShipmentItem.shipmentId;
				map['productId'] = dataShipmentItem.productId;
				map['weight'] = dataShipmentItem.weight;
				map['weightUomId'] = dataShipmentItem.weightUomId;
				map['quantity'] = dataShipmentItem.quantityToPacking;
				map['quantityUomId'] = dataShipmentItem.quantityUomId;
				listShipmentItems.push(map);
			}
		}
		var listShipmentItems = JSON.stringify(listShipmentItems);
		row = { 
			deliveryEntryId:$('#deliveryEntryId').val(),
			deliveryBoxTypeId:$('#deliveryBoxTypeId').val(),
			listShipmentItems:listShipmentItems
		};
		$("#jqxgridDeliveryEntryPackage").jqxGrid('addRow', null, row, "first");
		// select the first row and clear the selection.
		$("#jqxgridDeliveryEntryPackage").jqxGrid('updatebounddata');
		$("#jqxgridDeliveryEntryPackage").jqxGrid('clearSelection');                        
		$("#jqxgridDeliveryEntryPackage").jqxGrid('selectRow', 0);
		
		$("#alterpopupWindow").jqxWindow('close');
	});
</script>