<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.orange.css" type="text/css" />
<script type="text/javascript" src="/posresources/js/calculatePurchase.js"></script>
<style>
	#columntablejqxgrid .jqx-checkbox-default{
		display: none;
	}
</style>
<script>
var cellclassEdit = function (row, columnfield, value) {
	return 'yellow';
}
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
<div id="calculatePurchase">
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var productId = datarecord.productId;
	var urlStr = \'jqxGeneralServicer?sname=JQCalculatePODetail&hasrequest=Y&productId=\' + productId; 
	var id = datarecord.uid.toString();
    var grid = $($(parentElement).children()[0]);
    $(grid).attr(\"id\",productId+\"jqxgridDetail\");
 	var sourceRowDetail = {
	        datafields:
	        [
	            { name: \'productId\', type: \'string\' },
	            { name: \'facilityId\', type: \'string\' },
	            { name: \'qohDetail\', type: \'number\' },
	            { name: \'qooDetail\', type: \'number\' },
	            { name: \'qpdDetail\', type: \'number\' },
	            { name: \'facilityName\', type: \'string\' },
	            { name: \'poQuantity\', type: \'number\' },
	            { name: \'facilityLid\', type: \'number\' }
	        ],
	        cache: false,
	        root: \'results\',
	        datatype: \"json\",
	        beforeprocessing : function(data) {
			},
	        sortcolumn: \'facilityId\',
			sortdirection: \'asc\',
	        type: \'POST\',
	        data: {
		        noConditionFind: \'Y\',
		        conditionsFind: \'N\',
		    },
		    pagesize: 15,
		    pager: function (pagenum, pagesize, oldpagenum) {
	        },
	        contentType: \'application/x-www-form-urlencoded\',
	        url: urlStr
	};
    var dataAdapterRowDetail = new $.jqx.dataAdapter(sourceRowDetail, 
    {
    	formatData : function (data) {
    	},
		loadError : function(xhr, status, error) {
		},
		downloadComplete : function(data, status, xhr) {
		},
		beforeLoadComplete : function(records) {
		}
    });
    if (grid != null) {
             grid.jqxGrid({
             source: dataAdapterRowDetail,
             width: '85%',
             height: 150,
             showtoolbar:false,
	 		 editable:true,
	 		 editmode:\"click\",
	 		 showheader: true,
	 		 selectionmode:\"singlecell\",
	 		 theme: 'energyblue',
	         pageable: false,
	 		 localization: getLocalization('${locale}'),
             columns: [
                  { text: \'${uiLabelMap.BPOSProductID}\', datafield: \'productId\',editable: false, hidden: true },	
                  { text: \'${uiLabelMap.BPOSFacilityId}\', datafield: \'facilityId\',editable: false, width: 100, cellsalign:\'left\', },
                  { text: \'${uiLabelMap.BPOSFacilityName}\', datafield: \'facilityName\',editable: false, cellsalign:\'left\', width: 196},
                  { text: \'${uiLabelMap.BPOSQPD}\', datafield: \'qpdDetail\',editable: false, width: 125, cellsalign:\'right\', rendered: tooltiprendererQPD },
                  { text: \'${uiLabelMap.BPOSQOH_PO}\', datafield: \'qohDetail\',editable: false, width: 125, cellsalign:\'right\',  cellsformat:\'d\', rendered: tooltiprendererQOH},
                  { text: \'${uiLabelMap.BPOSQOO}\', datafield: \'qooDetail\',editable: false, width: 125, cellsalign:\'right\',  cellsformat:\'d\', rendered: tooltiprendererQOO},
                  { text: \'${uiLabelMap.BPOSCalculatePO}\', datafield: \'poQuantity\', width: 125, columntype: 'numberinput', cellsalign:\'right\', cellsformat:\'d\', rendered: tooltiprendererPO, cellclassname: cellclassEdit,
					  initeditor: function (row, cellvalue, editor) {
		                	editor.jqxNumberInput({ digits: 5, min: 0, decimalDigits: 0})
		              },
		              validation : function (cell,value) {
				      	if ( value < 0) {
				        	return {result:false , message: \'${uiLabelMap.BPOSValidateGreaterThanZero}\'};
				        }    
				        return true;              
				      }
	           	  },
                  { text: \'${uiLabelMap.BPOSLID}\', datafield: \'facilityLid\',editable: false, width: 125, cellsalign:\'right\', rendered: tooltiprendererLID}
               ]
            });
            grid.on(\'cellendedit\', function (event){
				var dataRow = $('#jqxgrid').jqxGrid('getrowdata', index);
				var totalPO = dataRow.totalPO;
				var rowBoundIndex = event.args.rowindex;
				var data = grid.jqxGrid('getrowdata', rowBoundIndex);
				var column = event.args.datafield;
				var value = args.value;
				var oldvalue = args.oldvalue;
				var dataAllRowDetail = grid.jqxGrid('getrows');
				var qpdDetail = grid.jqxGrid('getcellvalue', rowBoundIndex, \"qpdDetail\");
				var qohDetail = grid.jqxGrid('getcellvalue', rowBoundIndex, \"qohDetail\");
				var totalPoQuantity = parseInt(qohDetail) + parseInt(value);
				var facilityLid = 0;
				if (qpdDetail > 0){
					facilityLid = totalPoQuantity/qpdDetail;
				}
		    	facilityLid = parseFloat(facilityLid); 
		    	facilityLid = Math.round(facilityLid * 10)/10;
				grid.jqxGrid('setcellvalue', rowBoundIndex, \"facilityLid\", facilityLid);
				grid.jqxGrid('setcellvalue', rowBoundIndex, \"poQuantity\", value);
			});
       }
 }"/>
 
<#assign dataField = "[	 { name: 'productId', type: 'string' },
						 { name: 'productName', type: 'string',},
						 { name: 'qpd', type: 'number' },
						 { name: 'qoh', type: 'number'},
						 { name: 'qoo', type: 'number'},
						 { name: 'sysLid', type: 'number'},		
						 { name: 'lastSold', type: 'date', other: 'Timestamp'},
						 { name: 'lastReceived', type: 'date', other: 'Timestamp'},
						 { name: 'pickStandard', type: 'number'},
						 { name: 'qtyBox', type: 'number'},
						 { name: 'qtyPic', type: 'number'},
						 { name: 'totalPO', type: 'number'},
						 { name: 'totalLid', type: 'number'},
						 { name: 'unitCost', type: 'number'},
						 { name: 'totalItemCost', type: 'number'},
						 { name: 'comments', type: 'string'},
						 { name: 'rowDetail', type: 'string'},
						 { name: 'currencyUomId', type: 'string'},
					 	]"/>
<#assign columngroups = "
		 { text: '${uiLabelMap.BPOSSystemInfo}', align: 'center', name: 'SystemInformation' },
		 { text: '${uiLabelMap.BPOSPurchaseOrder}', align: 'center', name: 'PurchaseOrder' }
		"/>
<#assign columnlist = "
					{ text: '${uiLabelMap.BPOSProductID}',  datafield: 'productId', editable: false, cellsalign:'center',
						width: 120, columngroup: 'SystemInformation',
					},
					{ text: '${uiLabelMap.BPOSProductName}', datafield: 'productName', cellsalign:'left',
						width: 250,columngroup: 'SystemInformation', editable: false
					},
					{ text: '${uiLabelMap.BPOSQPD}', datafield: 'qpd', filtertype: 'number',editable: false, cellsalign:'right', sortable: false,
						width: 70,columngroup: 'SystemInformation', rendered: tooltiprendererQPD
					},
					{ text: '${uiLabelMap.BPOSQOH_PO}', datafield: 'qoh', filtertype: 'number',editable: false, cellsalign:'right', sortable: false,
					  width: 70,columngroup: 'SystemInformation', rendered: tooltiprendererQOH
					},
					{ text: '${uiLabelMap.BPOSQOO}', datafield: 'qoo',editable: false, cellsalign:'right', filtertype: 'number', sortable: false,
					  width: 70,columngroup: 'SystemInformation', rendered: tooltiprendererQOO
					},
					{ text: '${uiLabelMap.BPOSLID}', datafield: 'sysLid', editable: false, cellsalign: 'right', cellsalign:'right', filtertype: 'number', sortable: false,
						width: 70,columngroup: 'SystemInformation', rendered: tooltiprendererLID
					},
					{ text: '${uiLabelMap.BPOSLastsold}', datafield: 'lastSold',filtertype: 'range' , cellsformat: 'dd/MM/yyyy',
						width: 150, cellsalign: 'right',columngroup: 'SystemInformation', editable: false, cellsalign:'left', rendered: tooltiprendererLS
					},
					{ text: '${uiLabelMap.BPOSLastReceived}', datafield: 'lastReceived',filtertype: 'range', cellsformat: 'dd/MM/yyyy',
						width: 150, cellsalign: 'right',columngroup: 'PurchaseOrder', editable: false, cellsalign:'left', rendered: tooltiprendererLR
					},
					{ text: '${uiLabelMap.BPOSPickStandard}', datafield: 'pickStandard',editable:false, filtertype: 'number', cellsalign:'right',
					  width: 70, columngroup: 'PurchaseOrder', rendered: tooltiprendererPS,
					},
					{ text: '${uiLabelMap.BPOSQtyBox}', datafield: 'qtyBox',editable: true, cellsalign:'right', filtertype: 'number', filterable: false, sortable: false,
					  width: 70, columngroup: 'PurchaseOrder', columntype: 'numberinput', cellclassname: cellclassEdit, cellsformat: 'd', rendered: tooltiprendererQB,
					  initeditor: function (row, cellvalue, editor) {
						  editor.jqxNumberInput({ digits: 5, min: 0, decimalDigits: 0})
		              },
		              validation : function (cell,value) {
				        if ( value < 0) {
				        	return {result:false , message: '${uiLabelMap.BPOSValidateGreaterThanZero}'};
				        }    
				        return true;              
				      }
					},
					{ text: '${uiLabelMap.BPOSQtyPic}', datafield: 'qtyPic',editable: true, cellsalign:'right', filtertype: 'number', filterable: false, sortable: false,
					  width: 70,columngroup: 'PurchaseOrder', columntype: 'numberinput', cellclassname: cellclassEdit, cellsformat: 'd', rendered: tooltiprendererQP,
					  initeditor: function (row, cellvalue, editor) {
		                	editor.jqxNumberInput({ digits: 5, min: 0, decimalDigits: 0})
		            	},
		            	validation : function (cell,value) {
				        if ( value < 0) {
				        	return {result:false , message: '${uiLabelMap.BPOSValidateGreaterThanZero}'};
				        }    
				        return true;              
				      }
					},
					{ text: '${uiLabelMap.BPOSTotalPOQuantity}', datafield: 'totalPO',editable:false, cellsalign:'right', filtertype: 'number', filterable: false, sortable: false,
				  		width: 90,columngroup: 'PurchaseOrder', cellsformat: 'd', rendered: tooltiprendererPO
					},
					{ text: '${uiLabelMap.BPOSLID}', datafield: 'totalLid', editable: false, cellsalign: 'right', cellsalign:'right', filtertype: 'number', filterable: false, sortable: false,
						width: 70,columngroup: 'PurchaseOrder', rendered: tooltiprendererLID
					},
					{ text: '${uiLabelMap.BPOSUnitCostPurchase}', datafield: 'unitCost', editable:true, cellsalign:'right', filtertype: 'number', filterable: false, sortable: false,
				  		width: 100, columngroup: 'PurchaseOrder', columntype: 'numberinput', cellclassname: cellclassEdit,
				  		cellsrenderer: function (row, column, value) {
	     					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	     					if (data && data.unitCost){
	     						return '<div style=\"text-align: right;\">' + formatcurrency(data.unitCost, data.currencyUomId) + '</div>';
	     					} else {
	     						return '<div style=\"text-align: right;\">' + formatcurrency(0, data.currencyUomId) + '</div>';
	     					}
     					},
					  	initeditor: function (row, cellvalue, editor) { 
		                	editor.jqxNumberInput({min: 0})
		            	},
		            	validation : function (cell,value) {
				        if ( value < 0) {
				        	return {result:false , message: '${uiLabelMap.BPOSValidateGreaterThanZero}'};
				        }    
				        return true;              
				      }
					},
					{ text: '${uiLabelMap.BPOSTotalCost}', datafield: 'totalItemCost',editable:false, cellsalign:'right', filtertype: 'number', filterable: false, sortable: false,
						cellsrenderer: function (row, column, value) {
	     					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	     					if (data && data.totalItemCost){
	     						return '<div style=\"text-align: right;\">' + formatcurrency(data.totalItemCost, data.currencyUomId) + '</div>';
	     					} else {
	     						return '<div style=\"text-align: right;\">' + formatcurrency(0, data.currencyUomId) + '</div>';
	     					}
     					},
				  		width: 100,columngroup: 'PurchaseOrder',
					},
					{ text: '${uiLabelMap.BPOSNotes}', datafield: 'comments',editable:true, cellsalign:'left', filterable: false, sortable: false,
				  		width: 200, columngroup: 'PurchaseOrder', cellsalign:'left', cellclassname: cellclassEdit
					}
					"/>	
<@jqGrid filtersimplemode="true" showlist="false" dataField=dataField filterable="true" columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
		 editable="true" editrefresh ="true" editmode="click" selectionmode= "checkbox" viewSize="10" columngrouplist = columngroups initrowdetails = "true"
		 filtersimplemode = "true" initrowdetailsDetail= initrowdetailsDetail customtoolbaraction="searchProductSupplier" bindresize="true"
		 url="jqxGeneralServicer?sname=jqGetProductAndProductFacility&hasrequest=Y" rowselectfunction="rowselectfunction(event);"
		/>
</div>
<script type="text/javascript">
	var BPOSSupplierId = "${StringUtil.wrapString(uiLabelMap.BPOSSupplierId)}";
	var BPOSSupplierName = "${StringUtil.wrapString(uiLabelMap.BPOSSupplierName)}";
	var BPOSMobile = "${StringUtil.wrapString(uiLabelMap.BPOSMobile)}";
	var BPOSEmail = "${StringUtil.wrapString(uiLabelMap.BPOSEmail)}";
	var BPOSAddress = "${StringUtil.wrapString(uiLabelMap.BPOSAddress)}";
	var BPOSChooseSupplier = "${StringUtil.wrapString(uiLabelMap.BPOSChooseSupplier)}";
	
	$("#jqxgrid").on('cellendedit', function (event){
	    var args = event.args;
	    var dataField = event.args.datafield;
	    var rowBoundIndex = event.args.rowindex;
	    var value = args.value;
	    var oldvalue = args.oldvalue;
	    var rowData = args.row;
	    var qtyBox = 0;
	    var qtyPic = 0;
		if (rowData.qtyBox){
			qtyBox = rowData.qtyBox;
		}
		if (rowData.qtyPic){
			qtyPic = rowData.qtyPic;
		}
	    var sys_total = rowData.qoo + rowData.qoh;
	    if (dataField == 'qtyBox'){
	    	$("#jqxgrid").jqxGrid('setcellvalue', rowBoundIndex, "qtyBox", value);
	    	var totalPO1 = value*rowData.pickStandard + qtyPic;
	    	$("#jqxgrid").jqxGrid('setcellvalue', rowBoundIndex, "totalPO", totalPO1);
	    	var total1 = parseInt(sys_total) + parseInt(totalPO1);
	    	var totalLid1 = 0;
	    	if(rowData.qpd > 0){
	    		totalLid1 = total1/rowData.qpd;
	    	}
	    	var temp1 = parseFloat(totalLid1); 
	    	totalLid1 = Math.round(temp1 * 10)/10;
	    	$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "totalLid", totalLid1);
	    	var totalItemCost = parseInt(totalPO1) * parseFloat(rowData.unitCost);
			$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "totalItemCost", totalItemCost);
	    }
	    if (dataField == 'qtyPic'){
	    	$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "qtyPic", value);
	    	var totalPO2 = qtyBox*rowData.pickStandard + value;
	    	$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "totalPO", totalPO2);
	    	var total2 = parseInt(sys_total) + parseInt(totalPO2);
	    	var totalLid2 = 0;
	    	if(rowData.qpd > 0){
	    		totalLid2 = total2/rowData.qpd;
	    	}
	    	
	    	var temp2 = parseFloat(totalLid2); 
	    	totalLid2 = Math.round(temp2 * 10)/10;
	    	$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "totalLid", totalLid2);
	    	var totalItemCost = parseInt(totalPO2) * parseFloat(rowData.unitCost);
			$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "totalItemCost", totalItemCost);
	    }
	    if (dataField == 'unitCost'){
	    	$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "unitCost", value);
	    	$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "totalItemCost", rowData.totalPO*value);
	    }
	    if (dataField == 'comments'){
	    	$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "comments", value);
	    }
	}); 
	
	function rowselectfunction(event){
		var gridId = $(event.currentTarget).attr("id");
		if(typeof event.args.rowindex != 'number'){
            var tmpArray = event.args.rowindex;
            for(i = 0; i < tmpArray.length; i++){
                if(checkRequiredProduct(tmpArray[i], gridId)){
                    $('#'+gridId).jqxGrid('clearselection');
                    break; // Stop for first item
                }
            }
        } else {
            if(checkRequiredProduct(event.args.rowindex, gridId)){
                $('#'+gridId).jqxGrid('unselectrow', event.args.rowindex);
            }
        }
    }
	
	function checkRequiredProduct(rowindex, gridId){
		var data = $('#'+gridId).jqxGrid('getrowdata', rowindex);
		if(data == undefined){
			bootbox.dialog("${uiLabelMap.BPOSItemMissingFieldsQuantity}",
				[{
					"label" : "${uiLabelMap.CommonSubmit}",
				    "class" : "btn-primary btn-small icon-ok",
				    "callback": function() {
				    	$("#"+gridId).jqxGrid('begincelledit', rowindex, "qtyBox");
				    }	
				}]		
			);
            return true;
		} else {
			var qtyBox = data.qtyBox;
			var qtyPic = data.qtyPic;
			var unitCost = data.unitCost;
			if(((!qtyBox) && (!qtyPic))||((qtyBox == 0) && (qtyPic == 0))){
	            $('#'+gridId).jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.BPOSItemMissingFieldsQuantity}",
    				[{
    					"label" : "${uiLabelMap.CommonSubmit}",
    				    "class" : "btn-primary btn-small icon-ok",
    				    "callback": function() {
    				    	$("#"+gridId).jqxGrid('begincelledit', rowindex, "qtyBox");
    				    }	
    				}]		
	    		);
	            return true;
	        } else {
		        	if(unitCost == 0 || unitCost == undefined){
			            $('#'+gridId).jqxGrid('unselectrow', rowindex);
			            bootbox.dialog("${uiLabelMap.BPOSItemMissingFieldsCost}",
		    				[{
		    					"label" : "${uiLabelMap.CommonSubmit}",
		    				    "class" : "btn-primary btn-small icon-ok",
		    				    "callback": function() {
		    				    	$("#"+gridId).jqxGrid('begincelledit', rowindex, "unitCost");
		    				    }	
		    				}]		
					    );
			            return true;
			        } else {
			        	$("#"+gridId).jqxGrid('showrowdetails', rowindex);
			        }
		        }
	        }
		}
</script>