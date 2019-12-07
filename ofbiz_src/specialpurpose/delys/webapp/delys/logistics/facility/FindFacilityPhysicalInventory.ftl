<script>
	var facilityId = '${parameters.facilityId}';
	<#assign list = listVarianceReason.size()/>
    <#if listVarianceReason?size gt 0>
		<#assign varianceReasonId="var varianceReasonId = ['" + StringUtil.wrapString(listVarianceReason.get(0).varianceReasonId?if_exists) + "'"/>
		<#assign description="var description = ['" + StringUtil.wrapString(listVarianceReason.get(0).description?if_exists) + "'"/>
		<#if listVarianceReason?size gt 1>
			<#list 1..(list - 1) as i>
				<#assign varianceReasonId=varianceReasonId + ",'" + StringUtil.wrapString(listVarianceReason.get(i).varianceReasonId?if_exists) + "'"/>
				<#assign description=description + ",'" + StringUtil.wrapString(listVarianceReason.get(i).description?if_exists) + "'"/>
			</#list>
		</#if>
		<#assign varianceReasonId=varianceReasonId + "];"/>
		<#assign description=description + "];"/>
	<#else>
		<#assign varianceReasonId="var varianceReasonId = [];"/>
    	<#assign description="var description = [];"/>
    </#if>
	${varianceReasonId}
	${description}
	var adapter = new Array();
	
	for(var i = 0; i < ${list}; i++){
		var row = {};
		row['varianceReasonId'] = varianceReasonId[i];
		row['description'] = description[i];
		adapter[i] = row;
	}
	
</script>
	<#assign dataField="[{ name: 'inventoryItemId', type: 'string'},	
		{ name: 'locationSeqId', type: 'string'},
		{ name: 'productId', type: 'string'},
		{ name: 'internalName', type: 'string'},
		{ name: 'quantityOnHand', type: 'number'},
		{ name: 'availableToPromise', type: 'number'},
		
		{ name: 'QOH', type: 'number'},
		
		{ name: 'ATP', type: 'number'},
		
		{ name: 'expireDate', type: 'date'},
		{ name: 'varianceReasonId', type: 'string'},
		{ name: 'availableToPromiseVar', type: 'string'},
		{ name: 'quantityOnHandVar', type: 'string'},
		{ name: 'facilityId', type: 'string'},
    ]"/>

	<#assign columnlist=" { text: '${uiLabelMap.InventoryItemId}', datafield: 'inventoryItemId', editable:false, width: 150},
		{ text: '${uiLabelMap.ProductLocationSeqId}', datafield: 'locationSeqId', editable:false, width: 150},
		{ text: '${uiLabelMap.ProductProductId}', datafield: 'productId', filtertype: 'checkedlist', editable:false, width: 150},
		{ text: '${uiLabelMap.ProductProductName}', datafield: 'internalName', filtertype: 'checkedlist', editable:false, minwidth: 150},
		
		{ text: '${uiLabelMap.ProductProductQOH}', datafield: 'QOH', editable:false, minwidth: 150},
		{ text: '${uiLabelMap.ProductExpireDate}', datafield: 'expireDate',editable:false,  cellsformat: 'dd/MM/yyyy', minwidth: 150},
		{ text: '${uiLabelMap.VarianceReadsonId}', datafield: 'varianceReasonId', editable:true, columntype:'dropdownlist', minwidth: 150,
			createeditor: function (row, value, editor) { 
        		var sourcePt =
	            {
	                localdata: adapter,
	                datatype: \"array\"
	            };
	            var dataAdapterPt = new $.jqx.dataAdapter(sourcePt);
                editor.jqxDropDownList({source: dataAdapterPt, valueMember: \"varianceReasonId\",
                    renderer: function (index, label, value) {
	                    var datarecord = adapter[index];
	                    return datarecord.description;
	                } 
                });
        	}
		},
		{ text: '${uiLabelMap.ProductProductQOHVar}', datafield: 'quantityOnHandVar', editable:true}
	"/>
			
	<@jqGrid filtersimplemode="true" id="jqxgrid" filterable="true" dataField=dataField columnlist=columnlist  editable="true"  showtoolbar="true" 
	url="jqxGeneralServicer?sname=JQXgetProductByFacilityIdPhysical&facilityId=${parameters.facilityId}" 
	updateUrl="jqxGeneralServicer?sname=createPhysicalVariancesLog&jqaction=U&facilityId=${parameters.facilityId}"	
	customcontrol1="icon-print@${uiLabelMap.PrintToPDF}@printFacilityPhysicalInventory.pdf?facilityId=${facilityId}"
	editColumns="facilityId[${parameters.facilityId}];varianceReasonId;availableToPromiseVar;quantityOnHandVar;inventoryItemId;productId;expireDate(java.sql.Timestamp)"	
	editmode="selectedrow" />
<script>
	$('#jqxgrid').on('filter', function () {
		 var filterGroups = $('#jqxgrid').jqxGrid('getfilterinformation');
	     var filterValue = "";
	     for (var i = 0; i < filterGroups.length; i++) {
	         var filterGroup = filterGroups[i];
	         filterValue += "Filter Column: " + filterGroup.filtercolumn + "\n";
	         var filters = filterGroup.filter.getfilters();
	         for (var j = 0; j < filters.length; j++) {
	        	 filterValue += "\nValue: " + filters[j].value + "\n";
	         }
	     }
	 });
	
</script>