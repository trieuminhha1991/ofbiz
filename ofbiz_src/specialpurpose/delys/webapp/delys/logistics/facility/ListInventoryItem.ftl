<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script>
	var facilityId = '${parameters.facilityId}';
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	
	<#assign uomList = delegator.findList("Uom", null, null, null, null, false) />
	var uomData = 
	[
		<#list uomList as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	<#assign labelStatusList = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "INV_LABELED_STATUS"), null, null, null, false) />
	var labelStatusData = 
	[
		<#list labelStatusList as item>
		{
			labelStatusId: "${item.statusId}",
			description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	function getDescriptionByUomId(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
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
<div>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	reponsiveRowDetails(grid);
	if(datarecord.rowDetail){
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
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'statusId', type: 'string' },
				{ name: 'labelStatusId', type: 'string' },
            ]
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
            selectionmode: 'singlerow',
            columns: [
						{ text: '${uiLabelMap.ProductProductName}', datafield: 'internalName', align: 'center', width: 150, pinned: true},
						{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'center', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
						{ text: '${uiLabelMap.FormFieldTitle_quantityOnHandTotal}', datafield: 'quantityOnHandTotal', align: 'center', width: 200, cellsalign: 'right',
							cellsrenderer: function(row, colum, value){
								if(value){
									return '<span style=\"text-align: right;\">' + value.toLocaleString('${locale}') + '</span>';
								}
						    }, 
						},
						{ text: '${uiLabelMap.FormFieldTitle_availableToPromiseTotal}', datafield: 'availableToPromiseTotal', align: 'center', width: 250, cellsalign: 'right',
							cellsrenderer: function(row, colum, value){
								if(value){
									return '<span style=\"text-align: right;\">' + value.toLocaleString('${locale}') + '</span>';
								}
						    },
						},
						{ text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', align: 'center', width: 150,
							cellsrenderer: function(row, colum, value){
								if(value){
									return '<span>' +  getDescriptionByUomId(value) + '</span>';
								}
						    }, 
						},
						{ text: '${uiLabelMap.LabeledStatus}', datafield: 'labelStatusId', align: 'center', width: 150,
							cellsrenderer: function(row, colum, value){
								if(value){
									for (var i=0; i<labelStatusData.length; i++){
										if (labelStatusData[i].labelStatusId == value){
											return '<span>' +  labelStatusData[i].description + '</span>';
										}
									}
								}
						    }, 
						},
						{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', align: 'center', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' }
                     ]
        });
	}else {
		grid.jqxGrid({
            width: '98%',
            height: '92%',
            theme: 'olbius',
            localization: getLocalization(),
            source: [],
            sortable: true,
            pagesize: 5,
	 		pageable: true,
            selectionmode: 'singlerow',
            columns: [
					{ text: '${uiLabelMap.ProductProductName}', datafield: 'internalName', align: 'center'},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'center', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
					{ text: '${uiLabelMap.FormFieldTitle_quantityOnHandTotal}', datafield: 'quantityOnHandTotal', align: 'center', width: 150, cellsalign: 'right'},
					{ text: '${uiLabelMap.FormFieldTitle_availableToPromiseTotal}', datafield: 'availableToPromiseTotal', align: 'center', width: 150, cellsalign: 'right'},
					{ text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', align: 'center', width: 150},
					{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', align: 'center', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' }
                      ]
        });
	}
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
					{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.ProductProductId}', datafield: 'productId', align: 'center', width: 250},
					{ text: '${uiLabelMap.ProductProductName}', datafield: 'internalName', align: 'center'},
					{ text: '${uiLabelMap.FormFieldTitle_quantityOnHandTotal}', datafield: 'quantityOnHandTotal', align: 'center', width: 250, cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${locale}') + '</span>';
							}
					    }, 
					},
					{ text: '${uiLabelMap.FormFieldTitle_availableToPromiseTotal}', datafield: 'availableToPromiseTotal', align: 'center', width: 250, cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${locale}') + '</span>';
							}
					    },
					},
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		initrowdetails = "true" initrowdetailsDetail=initrowdetailsDetail
		url="jqxGeneralServicer?sname=JQXgetListInventoryItem&facilityId=${parameters.facilityId}"
	/>
</div>