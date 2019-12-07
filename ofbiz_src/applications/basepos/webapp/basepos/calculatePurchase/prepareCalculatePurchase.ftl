<#include "component://widget/templates/jqwLocalization.ftl" />
<style>     
	.yellow {
	    color: black\9;
	    background-color: yellow\9;
	}
	.yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
	    color: #333!important;
	    background-color: #FFF555;
	}
	.bootbox{
  		z-index: 20001 !important;
 	}
 	.modal-backdrop{
  		z-index: 20000 !important;
 	}
 	#fuelux-wizard{
 		display: none !important;
 	}
</style>
<script>
	<#assign facilitys = delegator.findList("Facility", null, null, null, null, false) !>
	var puData = [
				<#if facilitys?exists>
					<#list facilitys as facility >
						{
							facilityId: '${facility.facilityId?if_exists}',
							description: "${facility.facilityName?if_exists}"
						},
					</#list>
				</#if>
	              ];
	<#assign supplierList = delegator.findList("SupplierAndInfo", null, null, null, null, false) !>
	var supplierData = [
					<#if supplierList?exists>
						<#list supplierList as itemSup >
						{
							supplierPartyId: '${itemSup.partyId?if_exists}',
							description: '${(itemSup.groupName)?if_exists}'
						},
						</#list>
					</#if>
		               ];
	
	var tooltiprendererQPD = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipQPD)}", theme: 'orange' });
	}
	var tooltiprendererQOH = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipQOH)}", theme: 'orange' });
	}
	var tooltiprendererQOO = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipQOO)}", theme: 'orange' });
	}
	var tooltiprendererLID = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipLID)}", theme: 'orange' });
	}
	var tooltiprendererLS = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipLS)}", theme: 'orange' });
	}
	var tooltiprendererLR = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipLR)}", theme: 'orange' });
	}
	var tooltiprendererPS = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipPS)}", theme: 'orange' });
	}
	var tooltiprendererQB = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipQB)}", theme: 'orange' });
	}
	var tooltiprendererQP = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipQP)}", theme: 'orange' });
	}
	var tooltiprendererPO = function (element) {
	    $(element).jqxTooltip({content: "${StringUtil.wrapString(uiLabelMap.BPOSTooltipPO)}", theme: 'orange' });
	}
</script>
<div class="row-fluid">
	<div class="span12">
		<div id="fuelux-wizard" class="row-fluid" data-target="#step-container">
			<ul class="wizard-steps">
				<li data-target="#step1" class="active">
					<span class="step">1</span>
					<span class="title">${uiLabelMap.BPOSFillingInformation}</span>
				</li>
				<li data-target="#step2">
					<span class="step">2</span>
					<span class="title">${uiLabelMap.BPOSReview}</span>
				</li>
			</ul>
		</div>
		<div class="step-content row-fluid position-relative" id="step-container">
			<div class="step-pane active" id="step1">
				<#include "calculatePurchase.ftl"/>
			</div>
			<div class="step-pane" id="step2">
				<div class="row-fluid">
					<div class="span12">
						<div id="jqxgridReview" style="width: 100%"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid wizard-actions" id="wizard-actions">
			<button class="btn btn-prev btn-small" id="btnPrev">
				<i class="icon-arrow-left"></i>
				${uiLabelMap.CommonBack}
			</button>
			<button class="btn btn-success btn-next btn-small" data-last="${uiLabelMap.CommonCreate}">
				${uiLabelMap.CommonNext}
				<i class="icon-arrow-right icon-on-right"></i>
			</button>
		</div>
	</div>
</div>

<div id="popupWindow" style="display: none;">
	<div>${uiLabelMap.CommonSelect}</div>
	<div style="overflow-y: hidden;">
		<div class="row-fluid">
 			<div class="span12 margin-top10">
	 			<div class="span4"><label class="asterisk align-right" style="margin-top: 5px;">${uiLabelMap.BPOSSelectSupplier}</label></div>
				<div class="span8"><div id="supplierPartyId"></div></div>
 			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 margin-top10">
	 			<div class="span4"><label class="asterisk align-right" style="margin-top: 3px;">${uiLabelMap.BPOSPurchaseOrder}</label></div>
				<div class="span8">
					<div id="poNum">
	 					 <div style='float: left;  margin-top: 6px;' id='jqxRadioButton1'>${uiLabelMap.BPOSOnePO}</div>
        				 <div style='float: left;  margin-top: 6px;' id='jqxRadioButton2'>${uiLabelMap.BPOSMultiPO}</div>
	 				</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 margin-top10">
	 			<div class="span4"><label class="asterisk align-right" style="margin-top: 5px;">${uiLabelMap.BPOSFacilityName}</label></div>
				<div class="span8">
					<div id="facilityId">
	 				</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 margin-top10">
	 			<div class="span4"><label class="asterisk align-right" style="margin-top: 5px;">${uiLabelMap.BPOShipByDate}</label></div>
				<div class="span8">
					<div id="shipByDate">
	 				</div>
				</div>
			</div>
		</div>
 		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
 		<div class="row-fluid">
            <div class="span12 margin-top10">
            	<div class="span12">
            		<button type="button" id='alterCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
            		<button type="button" id='alterSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonCreate}</button>
        		</div>
            </div>
    	</div>
	</div>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var dataSelected = new Array();
	$(document).ready(function (){
		$("#popupWindow").jqxWindow({ width: 480, height: 270, theme:'olbius', resizable: false,  isModal: true, cancelButton: $("#alterCancel"), autoOpen: false, modalOpacity: 0.8 });
		$("#supplierPartyId").jqxDropDownList({ selectedIndex: -1,  source: supplierData, displayMember: "description", valueMember: "supplierPartyId", width: '100%', height: '28px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 200  });
		$("#facilityId").jqxDropDownList({ selectedIndex: -1,  source: puData, displayMember: "description", valueMember: "facilityId", width: '100%', height: '28px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 200  });
		$("#jqxRadioButton1").jqxRadioButton({ width: 80, height: 25, checked: true});
		$("#jqxRadioButton2").jqxRadioButton({ width: 80, height: 25});
		$('#jqxRadioButton2').on('checked', function (event) {
			$("#facilityId").jqxDropDownList({disabled: true});
		});
		$('#jqxRadioButton1').on('checked', function (event) {
			$("#facilityId").jqxDropDownList({disabled: false});
		});	
		
		$("#shipByDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy HH:mm:ss', width: '100%', showFooter:true, allowNullDate: false});
	});
	
	$(function() {
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if((info.step == 1) && (info.direction == "next")) {
				var selectedRowIndexes = $('#jqxgrid').jqxGrid('selectedrowindexes');
				var numRow = selectedRowIndexes.length;
				
				if(numRow == 0) {
					bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BPOSPleaseSelectProduct)}",
						[{
							"label" : "${uiLabelMap.CommonSubmit}",
						    "class" : "btn-primary btn-small icon-ok",
						    "callback": function() {
						    	
						    }	
						}]		
					);
					return false;
				} else {
					var flagSum = true;
					var flagInfo = true;
					var productError = '';
					var productDetails = new Array();
					for (var i = 0; i < numRow ; i++){
						var data = $('#jqxgrid').jqxGrid("getrowdata", selectedRowIndexes[i]);
						var productId = data.productId;
						var element = document.getElementById(productId + 'jqxgridDetail');
						if (element){
							var dataDetail = $('#' + productId + 'jqxgridDetail').jqxGrid('getrows');
							productDetails.push(dataDetail);
						} else {
							flagInfo = false;
							break;
						}
					}
					if (flagInfo == false){
						bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BPOSNotifyFillAllInFo)}",
							[{
								"label" : "${uiLabelMap.CommonSubmit}",
							    "class" : "btn-primary btn-small icon-ok",
							    "callback": function() {
							    	
							    }	
							}]		
						);
						return false;
					} else {
						for (var j = 0; j < productDetails.length; j++){
							var data = $('#jqxgrid').jqxGrid("getrowdata", selectedRowIndexes[j]);
							var totalPO = data.totalPO;
							var dataPro = productDetails[j];
							var sumPO = 0;
							for (var k = 0; k < dataPro.length; k++){
								var poQuantity = 0;
								if (dataPro[k].poQuantity){
									poQuantity = dataPro[k].poQuantity;
								} 
								sumPO = sumPO + poQuantity;
							}
							if (totalPO != sumPO){
								flagSum = false;
								productError = dataPro[j].productId;
								break;
							}
						}
						if (flagSum == false){
							bootbox.dialog("${uiLabelMap.BPOSErrorTotalPO}: " + productError + "<br/>" + "${uiLabelMap.BPOSTotalPOAlert}: " + totalPO + "<br/>" + "${uiLabelMap.BPOSTotalPOFacilityAlert}: " + sumPO + "<br/>" + "${uiLabelMap.BPOSTotalPOError}",
								[{
									"label" : "${uiLabelMap.CommonSubmit}",
								    "class" : "btn-primary btn-small icon-ok",
								    "callback": function() {
								    	
								    }	
								}]		
							);
							return false;
						} else{
							for(var index in selectedRowIndexes) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', selectedRowIndexes[index]);
								var row = {};
								if (data.productId != undefined){
									row["productId"] = data.productId;
								} else{
									row["productId"] = "";
								}
								row["productName"] = data.productName;
								if (data.qpd != undefined){
									row["qpd"] = data.qpd;
								} else{
									row["qpd"] = "";
								}
								if (data.qoh != undefined){
									row["qoh"] = data.qoh;
								} else{
									row["qoh"] = 0;
								}
								if (data.qoo != undefined){
									row["qoo"] = data.qoo;
								} else{
									row["qoo"] = "";
								}
								if (data.sysLid != undefined){
									row["sysLid"] = data.sysLid;
								} else{
									row["sysLid"] = "";
								}
								
								if (data.lastRecevied != undefined){
									row["lastRecevied"] = data.lastRecevied;
								} else{
									row["lastRecevied"] = "";
								}
								
								if (data.lastSold != undefined){
									row["lastSold"] = data.lastSold;
								} else{
									row["lastSold"] = "";
								}
								if (data.pickStandard != undefined){
									row["pickStandard"] = data.pickStandard;
								} else{
									row["pickStandard"] = "";
								}
								if (data.qtyBox != undefined){
									row["qtyBox"] = data.qtyBox;
								} else{
									row["qtyBox"] = "";
								}
								if (data.qtyPic != undefined){
									row["qtyPic"] = data.qtyPic;
								} else{
									row["qtyPic"] = "";
								}
								if (data.totalPO != undefined){
									row["totalPO"] = data.totalPO;
								} else{
									row["totalPO"] = "";
								}
								if (data.totalLid != undefined){
									row["totalLid"] = data.totalLid;
								} else{
									row["totalLid"] = "";
								}
								if (data.unitCost != undefined){
									row["unitCost"] = Math.round(data.unitCost);
								} else{
									row["unitCost"] = "";
								}
								if (data.totalItemCost != undefined){
									row["totalItemCost"] = Math.round(data.totalItemCost);
								} else{
									row["totalItemCost"] = "";
								}
								if (data.comments != undefined){
									row["comments"] = data.comments;
								} else{
									row["comments"] = "";
								}
								if (data.currencyUomId != undefined){
									row["currencyUomId"] = data.currencyUomId;
								} else{
									row["currencyUomId"] = "";
								}
					 			var rowDetailTmp = $('#' + data.productId + 'jqxgridDetail').jqxGrid('getrows');
					 			var rowDetail = new Array();
					 			for (var i = 0; i < rowDetailTmp.length; i++){
					 				var facility = new Object();
					 				if (rowDetailTmp[i].facilityId){
					 					facility.facilityId = rowDetailTmp[i].facilityId;
					 				} else {
					 					facility.facilityId = "";
					 				}
					 				
					 				if (rowDetailTmp[i].facilityName){
					 					facility.facilityName = rowDetailTmp[i].facilityName;
					 				} else {
					 					facility.facilityName = "";
					 				}
					 				
					 				if (rowDetailTmp[i].productId){
					 					facility.productId = rowDetailTmp[i].productId;
					 				} else {
					 					facility.productId = "";
					 				}
					 				
					 				if (rowDetailTmp[i].qohDetail){
					 					facility.qohDetail = rowDetailTmp[i].qohDetail;
					 				} else {
					 					facility.qohDetail = 0;
					 				}
					 				
					 				if (rowDetailTmp[i].qooDetail){
					 					facility.qooDetail = rowDetailTmp[i].qooDetail;
					 				} else {
					 					facility.qohDetail = 0;
					 				}
					 				
					 				if (rowDetailTmp[i].qpdDetail){
					 					facility.qpdDetail = rowDetailTmp[i].qpdDetail;
					 				} else {
					 					facility.qpdDetail = 0;
					 				}
					 				
					 				if (rowDetailTmp[i].poQuantity){
					 					facility.poQuantity = rowDetailTmp[i].poQuantity;
					 				} else {
					 					facility.poQuantity = 0;
					 				}
					 				
					 				if (rowDetailTmp[i].facilityLid){
					 					facility.facilityLid = rowDetailTmp[i].facilityLid;
					 				} else {
					 					facility.facilityLid = 0;
					 				}
					 				rowDetail.push(facility);
					 			}
					 			var flag = 1;
					 			for (var i = 0; i < rowDetail.length; i++){
					 				for (var j = 0; j < rowDetail.length; j++){
					 					var lidi = rowDetail[i].facilityLid;
					 					var lidj = rowDetail[j].facilityLid;
					 					if (!lidi) lidi = 0;
					 					if (!lidj) lidj = 0;
					 					if (Math.abs(lidi-lidj) > 5) {
					 						flag = 0;
					 					}
										break;
					 				}
					 			}
					 			
					 			row["flag"] = flag;
								
								if (rowDetail != undefined){
									row["rowDetail"] = rowDetail;
								} else{
									row["rowDetail"] = "";
								}
								
								dataSelected[index] = row;
							}
							
							var initrowdetails  = function (index, parentElement, gridElement, datarecord){
								var productId = datarecord.productId;
							 	var ordersDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
						        orders = ordersDataAdapter.records;
							 	var nestedGrids = new Array();
						        var id = datarecord.uid.toString();
						        var grid = $($(parentElement).children()[0]);
						        $(grid).attr("id",productId+"jqxgridDetail");
						        nestedGrids[index] = grid;
						        var ordersbyid = [];
						        for (var m = 0; m < orders.length; m++) {
						        	ordersbyid.push(orders[m]);
						        }
						        var orderssource = { datafields: [
							         	 { name: 'productId', type:'string'},
							             { name: 'facilityId', type: 'string'},
							             { name: 'qohDetail', type: 'number'},
							             { name: 'qooDetail', type: 'number'},
							             { name: 'qpdDetail', type: 'number'},
							             { name: 'facilityName', type: 'string'},
							             { name: 'poQuantity', type:'number'},
							        	 { name: 'facilityLid', type: 'number'},
						         	 ],
						             localdata: ordersbyid
						         }
						        var nestedGridAdapter = new $.jqx.dataAdapter(orderssource);
						        
						         if (grid != null) {
						             grid.jqxGrid({
						                 source: nestedGridAdapter,
						                 width: '82%',
						                 height: 150,
						                 showtoolbar:false,
								 		 editable:false,
								 		 editmode:"click",
								 		 showheader: true,
								 		 selectionmode:"singlecell",
								 		 localization: getLocalization(),
								 		 theme: 'energyblue',
						                 columns: [
						                   { text: '${uiLabelMap.BPOSProductID}', datafield: 'productId',editable: false, hidden: true },	
						                   { text: '${uiLabelMap.BPOSFacilityId}', datafield: 'facilityId',editable: false, width: 100 },
						                   { text: '${uiLabelMap.BPOSFacilityName}', datafield: 'facilityName',editable: false, width: 185},
						                   { text: '${uiLabelMap.BPOSQPD}', datafield: 'qpdDetail',editable: false, width: 120, cellsalign: 'right', rendered: tooltiprendererQPD},
						                   { text: '${uiLabelMap.BPOSQOH_PO}', datafield: 'qohDetail',editable: false, width: 120, cellsformat: 'd', cellsalign: 'right', rendered: tooltiprendererQOH},
						                   { text: '${uiLabelMap.BPOSQOO}', datafield: 'qooDetail',editable: false, width: 120, cellsformat: 'd', cellsalign: 'right', rendered: tooltiprendererQOO},
						                   { text: '${uiLabelMap.BPOSCalculatePO}', datafield: 'poQuantity', width: 120, cellsformat: 'd', cellsalign: 'right', rendered: tooltiprendererPO},
						                   { text: '${uiLabelMap.BPOSLID}', datafield: 'facilityLid',editable: false, width: 125, cellsalign: 'right', rendered: tooltiprendererLID}
						                ]
						             });
						         }
							 }
							var sourceReview = {
									localdata: dataSelected,
									dataType: "array",
									datafields:[
											{ name: 'productId', type: 'string' },
											{ name: 'productName', type: 'string',},
											{ name: 'currencyUomId', type: 'string',},
											{ name: 'qpd', type: 'number' },
											{ name: 'qoh', type: 'number'},
											{ name: 'qoo', type: 'number'},
											{ name: 'sysLid', type: 'number'},		
											{ name: 'lastSold', type: 'date', other: 'Timestamp'},
											{ name: 'lastRecevied', type: 'date', other: 'Timestamp'},
											{ name: 'pickStandard', type: 'number'},
											{ name: 'qtyBox', type: 'number'},
											{ name: 'qtyPic', type: 'number'},
											{ name: 'totalPO', type: 'number'},
											{ name: 'totalLid', type: 'number'},
											{ name: 'unitCost', type: 'number'},
											{ name: 'totalItemCost', type: 'number'},
											{ name: 'comments', type: 'string'},
											{ name: 'rowDetail', type: 'array'},
											{ name: 'flag', type: 'number'},
									   ]
								};
							
							var cellclass = function (row, columnfield, value) {
								var data = $('#jqxgridReview').jqxGrid('getrowdata', row);
				                if (data.flag == 0) {
				                    return 'yellow';
				                }
				            }
							var columnsReview = [
											{ text: '${uiLabelMap.BPOSProductID}',  datafield: 'productId', editable: false, cellsalign: 'center',
												width: 120, columngroup: 'SystemInformation', cellclassname: cellclass
											},
											{ text: '${uiLabelMap.BPOSProductName}', datafield: 'productName',
												width: 250,columngroup: 'SystemInformation', editable: false, cellclassname: cellclass
											},
											{ text: '${uiLabelMap.BPOSQPD}', datafield: 'qpd', filtertype: 'number',editable: false, sortable: false,
												width: 70,columngroup: 'SystemInformation', cellclassname: cellclass, cellsalign: 'right', rendered: tooltiprendererQPD
											},
											{ text: '${uiLabelMap.BPOSQOH_PO}', datafield: 'qoh', filtertype: 'number',editable: false, sortable: false,
											  width: 70,columngroup: 'SystemInformation', cellclassname: cellclass, cellsformat: 'd', cellsalign: 'right', rendered: tooltiprendererQOH
											},
											{ text: '${uiLabelMap.BPOSQOO}', datafield: 'qoo',editable: false, sortable: false,
											  width: 70,columngroup: 'SystemInformation', cellclassname: cellclass, cellsformat: 'd', cellsalign: 'right', rendered: tooltiprendererQOO
											},
											{ text: '${uiLabelMap.BPOSLID}', datafield: 'sysLid', editable: false, cellsalign: 'right', sortable: false,
												width: 70,columngroup: 'SystemInformation', cellclassname: cellclass, rendered: tooltiprendererLID
											},
											{ text: '${uiLabelMap.BPOSLastsold}', datafield: 'lastSold',filtertype: 'range' , cellsformat: 'dd/MM/yyyy',
												width: 150, cellsalign: 'right',columngroup: 'SystemInformation', editable: false, cellclassname: cellclass, rendered: tooltiprendererLS
											},
											{ text: '${uiLabelMap.BPOSLastReceived}', datafield: 'lastRecevied',filtertype: 'range', cellsformat: 'dd/MM/yyyy',
												width: 150, cellsalign: 'right',columngroup: 'PurchaseOrder', editable: false, cellclassname: cellclass, rendered: tooltiprendererLR
											},
											{ text: '${uiLabelMap.BPOSPickStandard}', datafield: 'pickStandard',editable:false, filtertype: 'number',
											  width: 70, columngroup: 'PurchaseOrder', cellclassname: cellclass, cellsformat: 'd', cellsalign: 'right', rendered: tooltiprendererPS
											},
											{ text: '${uiLabelMap.BPOSQtyBox}', datafield: 'qtyBox',editable:false,
											  width: 70, columngroup: 'PurchaseOrder', cellclassname: cellclass, cellsformat: 'd', cellsalign: 'right', rendered: tooltiprendererQB
											},
											{ text: '${uiLabelMap.BPOSQtyPic}', datafield: 'qtyPic',editable:false,
											  width: 70,columngroup: 'PurchaseOrder', cellclassname: cellclass, cellsformat: 'd', cellsalign: 'right', rendered: tooltiprendererQP
											},
											{ text: '${uiLabelMap.BPOSTotalPOQuantity}', datafield: 'totalPO',editable:false, 
													width: 90,columngroup: 'PurchaseOrder', cellclassname: cellclass, cellsformat: 'd', cellsalign: 'right', rendered: tooltiprendererPO
											},
											{ text: '${uiLabelMap.BPOSLID}', datafield: 'totalLid', editable: false, cellsalign: 'right',
												width: 70,columngroup: 'PurchaseOrder', cellclassname: cellclass, rendered: tooltiprendererLID
											},
											{ text: '${uiLabelMap.BPOSUnitCostPurchase}', datafield: 'unitCost', editable:false,
												cellsrenderer: function (row, column, value) {
							     					var data = $('#jqxgridReview').jqxGrid('getrowdata', row);
							     					if (data && data.unitCost){
							     						return '<div style=\"text-align: right;\">' + formatcurrency(data.unitCost, data.currencyUomId) + '</div>';
							     					} else {
							     						return '<div style=\"text-align: right;\">' + formatcurrency(0, data.currencyUomId) + '</div>';
							     					}
						     					},
												width: 100, columngroup: 'PurchaseOrder', cellclassname: cellclass
											},
											{ text: '${uiLabelMap.BPOSTotalCost}', datafield: 'totalItemCost',editable:false,
												cellsrenderer: function (row, column, value) {
							     					var data = $('#jqxgridReview').jqxGrid('getrowdata', row);
							     					if (data && data.totalItemCost){
							     						return '<div style=\"text-align: right;\">' + formatcurrency(data.totalItemCost, data.currencyUomId) + '</div>';
							     					} else {
							     						return '<div style=\"text-align: right;\">' + formatcurrency(0, data.currencyUomId) + '</div>';
							     					}
						     					},
												width: 100,columngroup: 'PurchaseOrder', cellclassname: cellclass
											},
											{ text: '${uiLabelMap.BPOSNotes}', datafield: 'comments', editable: false,
										  		width: 200,columngroup: 'PurchaseOrder', cellclassname: cellclass
											}
						     			];
							var dataAdapter = new $.jqx.dataAdapter(sourceReview);
							$("#jqxgridReview").jqxGrid({
			                	width: '100%',
			                	source: dataAdapter, 
			                	columns: columnsReview, 
			                	editable: false,
			                	pageable: true,
						        autoheight: false,
						        sortable: false,
						        altrows: true,
						        showaggregates: false,
						        showstatusbar: false,
						        enabletooltips: true,
						        columnsresize: true,
						        columnsreorder: true,
						        rowdetails: true,
						        localization: getLocalization(),
						        initrowdetails: initrowdetails,
				                rowdetailstemplate: { rowdetails: "<div id='grid' style='margin: 10px;'></div>", rowdetailsheight: 220, rowdetailshidden: false },
						        columngroups: 
						            [
						                { text: '${uiLabelMap.BPOSSystemInfo}', align: 'center', name: 'SystemInformation' },
				 						{ text: '${uiLabelMap.BPOSPurchaseOrder}', align: 'center', name: 'PurchaseOrder' }
						            ],
					            ready: function () {
					            	var rowscount = $("#jqxgridReview").jqxGrid('getdatainformation').rowscount;
					            	for (var i = 0; i < rowscount; i++){
					            		$("#jqxgridReview").jqxGrid('hiderowdetails', i);
					            	}
					            }
			                });
						}
					}
					
				}
			}
		}).on('finished', function(e) {
			var rowIndexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
	 		var numRow = rowIndexes.length;
			var offset = $("#jqxgrid").offset();
	        $("#popupWindow").jqxWindow({ position: { x: parseInt(offset.left) + 400, y: parseInt(offset.top) + 100} });
	        $("#popupWindow").jqxWindow('show');
	        $("#alterSave").click(function () {
				var supplierPartyId = $("#supplierPartyId").val();
				var mainFacility = $("#facilityId").val();
				var multiPo = $("#jqxRadioButton2").val();
				var onePo = $("#jqxRadioButton1").val();
				var shipByDate = $("#shipByDate").jqxDateTimeInput('getDate').getTime();
				if (onePo == true){
					var hasMtilPO = false;
				} else {
					var hasMtilPO = true;
				}
				
				if ((supplierPartyId == '')||((hasMtilPO == false)&&(mainFacility == ''))||((hasMtilPO == true)&&(mainFacility == '')&&(supplierPartyId == ''))){
					$("#popupWindow").jqxWindow('hide');
					bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BPOSNotifyFillAllInFo)}",
							[{
								"label" : "${uiLabelMap.CommonSubmit}",
							    "class" : "btn-primary btn-small icon-ok open-sans",
							    "callback": function() {
							    	$("#popupWindow").jqxWindow('show');
							    }	
							},
							]		
					);
					return false;
				} else {
					var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
					var productList = new Object();
					var grandTotal = 0;
					for (var i = 0; i < rowindexes.length;i++){
						var rowdata = $('#jqxgrid').jqxGrid('getrowdata',rowindexes[i]);
						
						grandTotal += rowdata.totalItemCost;
					}
					var productIdList = [];
					
					var rowReviews = $('#jqxgridReview').jqxGrid('getrows');
					for(var i = 0; i < rowReviews.length; i++){
						  var row = rowReviews[i];
						  var productInfo = new Object();
						  productInfo.productId = row.productId;
						  productInfo.productName = row.productName;
						  productInfo.currencyUomId = row.currencyUomId;
						  productInfo.qpd = row.qpd;
						  productInfo.qoh = row.qoh;
						  productInfo.qoo = row.qoo;
						  productInfo.sysLid = row.sysLid;
						  if(row.lastSold){
							  var lastSoldTmp = row.lastSold.getTime();
							  productInfo.lastSold = lastSoldTmp;
						  }
						  if(row.lastRecevied){
							  var lastReceviedTmp = row.lastRecevied.getTime();
							  productInfo.lastRecevied = lastReceviedTmp;
						  }
						 
						  productInfo.pickStandard = row.pickStandard;
						  if (row.qtyBox){
							  productInfo.qtyBox = row.qtyBox;
						  } else {
							  productInfo.qtyBox = 0;
						  }
						  if (row.qtyPic){
							  productInfo.qtyPic = row.qtyPic;
						  } else {
							  productInfo.qtyPic = 0;
						  }
						  productInfo.totalPO = row.totalPO;
						  productInfo.totalLid = row.totalLid;
						  productInfo.unitCost = Math.round(row.unitCost);
						  productInfo.totalItemCost = Math.round(row.totalItemCost);
						  productInfo.comments = row.comments;
						  productInfo.flag = row.flag;
						  var productDetail = [];
						  var rowDetail = row.rowDetail;
						  var dataDetail = rowDetail.localdata;
						  if(dataDetail){
							  for(var index = 0; index < dataDetail.length; index++){
								  var productDetailSelected = dataDetail[index];
								  var productDetailTmp = new Object();
								  productDetailTmp.productId = productDetailSelected.productId;
								  productDetailTmp.facilityId = productDetailSelected.facilityId;
								  productDetailTmp.qohDetail = productDetailSelected.qohDetail;
								  productDetailTmp.qpdDetail = productDetailSelected.qpdDetail;
								  productDetailTmp.facilityName = productDetailSelected.facilityName;
								  if (productDetailSelected.poQuantity){
									  productDetailTmp.poQuantity = productDetailSelected.poQuantity;
								  } else {
									  productDetailTmp.poQuantity = 0;
								  }
								  if (productDetailSelected.facilityLid){
									  productDetailTmp.facilityLid = productDetailSelected.facilityLid;
								  } else {
									  productDetailTmp.facilityLid = 0;
								  }
								  productDetail.push(productDetailTmp);
							  }
						  }
						  productInfo.rowDetail = productDetail;
						  productIdList.push(productInfo);
					 }
					
					var param = "supplierPartyId=" + supplierPartyId + "&hasMtilPO=" + hasMtilPO +"&mainFacility="+mainFacility + "&productIds="+JSON.stringify(productIdList) + "&grandTotal=" + grandTotal + "&shipByDate=" +shipByDate;
					jQuery.ajax({url: 'createPurchaseOrder',
						 data: param,
			 		     type: 'post',
			 		     async: false,   
			 		     success: function(data) {
			 		    	var serverError = getServerError(data);
					        	if (serverError != "") {
				        		bootbox.dialog(serverError,
										[{
											"label" : "${uiLabelMap.CommonSubmit}",
										    "class" : "btn-primary btn-small icon-ok open-sans",
										    "callback": function() {
										    	$("#popupWindow").jqxWindow('show');
										    }	
										},
										]		
								);
					        	}else{
					        		var BPOSCreatePurchaseOrderSuccess = "${StringUtil.wrapString(uiLabelMap.BPOSCreatePurchaseOrderSuccess)}";
					        		bootbox.dialog(BPOSCreatePurchaseOrderSuccess,
										[{
											"label" : "${uiLabelMap.CommonSubmit}",
										    "class" : "btn-primary btn-small icon-ok",
										    "callback": function() {
										    	window.location = "<@ofbizUrl>POHistory</@ofbizUrl>";
										    }	
										}]		
									);
					        	}
			 		     },
			 		     error: function(data) {
	
			 		     }
					});
					$("#popupWindow").jqxWindow('close');
				}
			}); 
		}).on('stepclick', function(e, info){
			//return false;//prevent clicking on steps
		});
	});
	
	function getServerError(data) {
	    var serverErrorHash = [];
	    var serverError = "";
	    if (data._ERROR_MESSAGE_LIST_ != undefined) {
	        serverErrorHash = data._ERROR_MESSAGE_LIST_;
	        $.each(serverErrorHash, function(i, error) {
	          if (error != undefined) {
	              if (error.message != undefined) {
	                  serverError += error.message;
	              } else {
	                  serverError += error;
	              }
	            }
	        });
	    }
	    if (data._ERROR_MESSAGE_ != undefined) {
	        serverError = data._ERROR_MESSAGE_;
	    }
	    return serverError;
	}
</script>