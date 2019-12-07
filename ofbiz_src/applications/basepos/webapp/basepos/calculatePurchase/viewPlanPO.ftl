<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.orange.css" type="text/css" />
<script>
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
		var urlStr = \'jqxGeneralServicer?sname=JQViewPlanPOItemDetail&hasrequest=Y&productId=\' + productId  + \'&orderId=\' + \"${orderId?if_exists}\"; 
		var id = datarecord.uid.toString();
        var grid = $($(parentElement).children()[0]);
        $(grid).attr(\"id\",productId+\"jqxgridDetail\");
	 	var sourceRowDetail =
    	{
        datafields:
	        [
	            { name: \'productId\', type: \'string\' },
	            { name: \'facilityId\', type: \'string\' },
	            { name: \'qohDetail\', type: \'number\' },
	            { name: \'qooDetail\', type: \'number\' },
	            { name: \'qpdDetail\', type: \'number\' },
	            { name: \'facilityName\', type: \'string\' },
	            { name: \'poQuantity\', type: \'number\' },
	            { name: \'facilityLid\', type: \'number\' },
	        ],
        cache: false,
        root: \'results\',
        datatype: \"json\",
        updaterow: function (rowid, rowdata) {
        
        },
        beforeprocessing: function (data) {
           
        },
        pager: function (pagenum, pagesize, oldpagenum) {
           
        },
        sortcolumn: \'facilityId\',
		sortdirection: \'asc\',
        type: \'POST\',
        data: {
	        noConditionFind: \'Y\',
	        conditionsFind: \'N\',
	    },
	    pagesize:15,
        contentType: \'application/x-www-form-urlencoded\',
        url: urlStr
    };
    var dataAdapterRowDetail = new $.jqx.dataAdapter(sourceRowDetail);
    if (grid != null) {
    	grid.jqxGrid({
             source: dataAdapterRowDetail,
             width: '85%',
             height: 150,
             showtoolbar:false,
	 		 editable:false,
	 		 editmode:\"click\",
	 		 showheader: true,
	 		 selectionmode:\"singlecell\",
	 		 theme: 'energyblue',
             columns: [
                  { text: \'${uiLabelMap.BPOSProductID}\', datafield: \'productId\',editable: false, hidden: true },	
                  { text: \'${uiLabelMap.BPOSFacilityId}\', datafield: \'facilityId\',editable: false, width: 110, },
                  { text: \'${uiLabelMap.BPOSFacilityName}\', datafield: \'facilityName\',editable: false, width: 200, },
                  { text: \'${uiLabelMap.BPOSQPD}\', datafield: \'qpdDetail\',editable: false, width: 125, rendered: tooltiprendererQPD, cellsalign:\'right\'},
                  { text: \'${uiLabelMap.BPOSQOH_PO}\', datafield: \'qohDetail\',editable: false, width: 125, rendered: tooltiprendererQOH, cellsalign:\'right\',  cellsformat:\'d\'},
                  { text: \'${uiLabelMap.BPOSQOO}\', datafield: \'qooDetail\',editable: false, width: 125, cellsalign:\'right\',  cellsformat:\'d\', rendered: tooltiprendererQOO},
                  { text: \'${uiLabelMap.BPOSCalculatePO}\', datafield: \'poQuantity\', width: 125, rendered: tooltiprendererPO, cellsalign:\'right\',  cellsformat:\'d\'},
                  { text: \'${uiLabelMap.BPOSLID}\', datafield: \'facilityLid\',editable: false, width: 125, rendered: tooltiprendererLID, cellsalign:\'right\'},   
               ]
            });
       }
 }"/>
<#assign dataField = "[	{ name: 'productId', type: 'string' },
						 { name: 'productName', type: 'string',},
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
						 { name: 'comments', type: 'string'}
					]"/>
<#assign columngroups = "
		 { text: '${uiLabelMap.BPOSSystemInfo}', align: 'center', name: 'SystemInformation' },
		 { text: '${uiLabelMap.BPOSPurchaseOrder}', align: 'center', name: 'PurchaseOrder' }
	"/>
<#assign columnlist = "
					{ text: '${uiLabelMap.BPOSProductID}',  datafield: 'productId', editable: false,cellsalign:'left',
						width: 120, columngroup: 'SystemInformation',
					},
					{ text: '${uiLabelMap.BPOSProductName}', datafield: 'productName',
						width: 250,columngroup: 'SystemInformation', editable: false
					},
					{ text: '${uiLabelMap.BPOSQPD}', datafield: 'qpd', filtertype: 'number',editable: false, cellsalign: 'right',
						width: 70,columngroup: 'SystemInformation', rendered: tooltiprendererQPD,
					},
					{ text: '${uiLabelMap.BPOSQOH_PO}', datafield: 'qoh', filtertype: 'number',editable: false, cellsalign: 'right',
					  width: 70,columngroup: 'SystemInformation', rendered: tooltiprendererQOH, cellsformat: 'd'
					},
					{ text: '${uiLabelMap.BPOSQOO}', datafield: 'qoo',editable: false, cellsalign: 'right',
					  width: 70,columngroup: 'SystemInformation', rendered: tooltiprendererQOO, cellsformat: 'd', filtertype: 'number',
					},
					{ text: '${uiLabelMap.BPOSLID}', datafield: 'sysLid', editable: false, cellsalign: 'right', filtertype: 'number',
						width: 70,columngroup: 'SystemInformation', rendered: tooltiprendererLID
					},
					{ text: '${uiLabelMap.BPOSLastsold}', datafield: 'lastSold',filtertype: 'range' , cellsformat: 'dd/MM/yyyy',
						width: 150, cellsalign: 'right',columngroup: 'SystemInformation', editable: false, rendered: tooltiprendererLS
					},
					{ text: '${uiLabelMap.BPOSLastReceived}', datafield: 'lastRecevied',filtertype: 'range', cellsformat: 'dd/MM/yyyy',
						width: 150, cellsalign: 'right',columngroup: 'PurchaseOrder', editable: false, rendered: tooltiprendererLR
					},
					{ text: '${uiLabelMap.BPOSPickStandard}', datafield: 'pickStandard',editable:false, filtertype: 'number',
					  width: 70, columngroup: 'PurchaseOrder', rendered: tooltiprendererPS, cellsalign: 'right', cellsformat: 'd'
					},
					{ text: '${uiLabelMap.BPOSQtyBox}', datafield: 'qtyBox',editable: true,
					  width: 70, columngroup: 'PurchaseOrder', rendered: tooltiprendererQB, cellsalign: 'right', cellsformat: 'd', filtertype: 'number',
					},
					{ text: '${uiLabelMap.BPOSQtyPic}', datafield: 'qtyPic',editable: true,
					  width: 70,columngroup: 'PurchaseOrder', rendered: tooltiprendererQP, cellsalign: 'right', cellsformat: 'd', filtertype: 'number',
					},
					{ text: '${uiLabelMap.BPOSTotalPOQuantity}', datafield: 'totalPO',editable:false, 
				  		width: 90,columngroup: 'PurchaseOrder', rendered: tooltiprendererPO, cellsalign: 'right', cellsformat: 'd', filtertype: 'number',
					},
					{ text: '${uiLabelMap.BPOSLID}', datafield: 'totalLid', editable: false, cellsalign: 'right', filtertype: 'number',
						width: 70,columngroup: 'PurchaseOrder', rendered: tooltiprendererLID
					},
					{ text: '${uiLabelMap.BPOSUnitCostPurchase}', datafield: 'unitCost', filtertype: 'number',
				  		width: 70, columngroup: 'PurchaseOrder', cellsalign: 'right',
				  		cellsrenderer: function (row, column, value) {
	     					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	     					if (data && data.unitCost){
	     						return '<div style=\"text-align: right;\">' + formatcurrency(data.unitCost, data.currencyUomId) + '</div>';
	     					} else {
	     						return '<div style=\"text-align: right;\">' + formatcurrency(0, data.currencyUomId) + '</div>';
	     					}
     					}
					},
					{ text: '${uiLabelMap.BPOSTotalCost}', datafield: 'totalItemCost',editable:false, filtertype: 'number',
				  		width: 100,columngroup: 'PurchaseOrder', cellsalign: 'right',
				  		cellsrenderer: function (row, column, value) {
	     					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	     					if (data && data.totalItemCost){
	     						return '<div style=\"text-align: right;\">' + formatcurrency(data.totalItemCost, data.currencyUomId) + '</div>';
	     					} else {
	     						return '<div style=\"text-align: right;\">' + formatcurrency(0, data.currencyUomId) + '</div>';
	     					}
     					},
					},
					{ text: '${uiLabelMap.BPOSNotes}', datafield: 'comments',editable: false,
				  		width: 200,columngroup: 'PurchaseOrder'
					},
			"/>	
<@jqGrid filtersimplemode="true" dataField=dataField filterable="false" columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
		 editable="true" editrefresh ="true" editmode="click" initrowdetails = "true" columngrouplist = columngroups showlist="false"
		 initrowdetailsDetail= initrowdetailsDetail filtersimplemode = "true" 
		 url="jqxGeneralServicer?sname=JQViewPlanPO&orderId=${parameters.orderId?if_exists}"
/>
</div>