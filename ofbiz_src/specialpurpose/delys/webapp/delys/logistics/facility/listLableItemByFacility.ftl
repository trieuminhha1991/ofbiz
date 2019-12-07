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
	
	<#assign listLabel = delegator.findList("Label", null, null, null, null, false) />
	var labelData = 
	[
		<#list listLabel as label>
		{
			labelId: "${label.labelId}",
			labelName: "${StringUtil.wrapString(label.get('labelName', locale)?if_exists)}"
		},
		</#list>
	];
	
	function getDescriptionByLabelId(labelId) {
		for ( var x in labelData) {
			if (labelId == labelData[x].labelId) {
				return labelData[x].labelName;
			}
		}
	}
	
</script>
<div>
<#if security.hasEntityPermission("FACILITY", "_VIEW", session)> 

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
                { name: 'labelId', type: 'string' },
                { name: 'labelItemId', type: 'string' },
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'facilityId', type: 'number' },
                { name: 'ownerPartyId', type: 'string' },
				{ name: 'quantityOnHandTotal', type: 'number' },
				{ name: 'availableToPromiseTotal', type: 'number' },
				{ name: 'statusId', type: 'string' },
            ]
        };
        var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
        grid.jqxGrid({
            width: '98%',
            height: '92%',
            theme: 'olbius',
            localization: {emptydatastring: '${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}'},
            source: dataAdapterGridDetail,
            sortable: true,
            pagesize: 5,
	 		pageable: true,
            selectionmode: 'singlerow',
            columns: [
						{ text: '${uiLabelMap.LogLableItemId}', datafield: 'labelItemId', align: 'center', width: 150, pinned: true},
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
						{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', align: 'center', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
						{ text: '${uiLabelMap.statusId}', datafield: 'statusId', align: 'center'},
                     
					]
        });
	}else {
		grid.jqxGrid({
            width: '98%',
            height: '92%',
            theme: 'olbius',
            localization: {emptydatastring: '${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}'},
            source: [],
            sortable: true,
            pagesize: 5,
	 		pageable: true,
            selectionmode: 'singlerow',
            columns: [
						{ text: '${uiLabelMap.LogLableItemId}', datafield: 'labelItemId', align: 'center'},
						{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'center', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
						{ text: '${uiLabelMap.FormFieldTitle_quantityOnHandTotal}', datafield: 'quantityOnHandTotal', align: 'center', width: 150, cellsalign: 'right'},
						{ text: '${uiLabelMap.FormFieldTitle_availableToPromiseTotal}', datafield: 'availableToPromiseTotal', align: 'center', cellsalign: 'right'},
						{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', align: 'center', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
						{ text: '${uiLabelMap.statusId}', datafield: 'statusId', align: 'center'},
					]
        });
	}
 }"/>
	<#assign dataField="[
					{ name: 'labelId', type: 'string'},
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'availableToPromiseTotal', type: 'number' },
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
					{ text: '${uiLabelMap.LogLableId}', datafield: 'labelId', align: 'center', 
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span>' +  getDescriptionByLabelId(value) + '</span>';
							}
					    }, 
					},
					{ text: '${uiLabelMap.FormFieldTitle_quantityOnHandTotal}', datafield: 'quantityOnHandTotal', align: 'center', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${locale}') + '</span>';
							}
					    }, 
					},
					{ text: '${uiLabelMap.FormFieldTitle_availableToPromiseTotal}', datafield: 'availableToPromiseTotal', align: 'center', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${locale}') + '</span>';
							}
					    },
					},
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		initrowdetails = "true" initrowdetailsDetail=initrowdetailsDetail id="jqxgirdLableItem"
		url="jqxGeneralServicer?sname=JQGetListLableItem&facilityId=${facilityId?if_exists}"
	/>		
<#else>   
	<h2> You do not have permission</h2>
</#if>
</div>