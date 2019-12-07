<#assign localeStr = "VI" />
<#if locale = "en">
    <#assign localeStr = "EN" />
</#if>
<h4 class="row header smaller lighter blue" style="margin: !important;font-weight:500;line-height:20px;font-size:18px;">
	${uiLabelMap.ListShipmentItems}
</h4>
<#assign dataField2="[{ name: 'shipmentId', type: 'string' },
                 	{ name: 'shipmentItemSeqId', type: 'string' },
                 	{ name: 'productId', type: 'string' },
                 	{ name: 'internalName', type: 'string' },
                 	{ name: 'quantityUomId', type: 'string' },
                 	{ name: 'quantity', type: 'number' },
                 	{ name: 'weight', type: 'number' },
                 	{ name: 'totalWeight', type: 'number' },
                 	{ name: 'weightUomId', type: 'string' },
                 	
		 		 	]"/>
<#assign columnlist2="
					{
						    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (value + 1) + '</div>';
						    }
						},						
					{ text: '${uiLabelMap.ProductProductId}', dataField: 'productId', width: 150, editable: false, pinned: true},
						{ text: '${uiLabelMap.ProductName}', dataField: 'internalName', width: 150, editable: false},
						{ text: '${uiLabelMap.quantity}', dataField: 'quantity', cellsalign: 'right', width: 150, editable: false,
							cellsrenderer: function(row, colum, value){
				 			    var data = $('#jqxgridShipmentItem').jqxGrid('getrowdata', row);
				 			    var quantityUomId = data.quantityUomId;
				 			    var quantityUomAbb = '';
				 			    for(var i = 0; i < quantityUomData.length; i++){
									 if(quantityUomId == quantityUomData[i].uomId){
										 quantityUomAbb = quantityUomData[i].description;
									 }
								 }
				 			    var tmp = data.quantity;
				 			    return '<span style=\"text-align: right\">' + tmp.toLocaleString('${localeStr}') +' (' + quantityUomAbb +  ')</span>';
				 			}
						},
					 	{ text: '${uiLabelMap.weight}', dataField: 'weight', width: 150, cellsalign: 'right', editable: false,
					 		cellsrenderer: function(row, colum, value){
				 			    var data = $('#jqxgridShipmentItem').jqxGrid('getrowdata', row);
				 			    var weightUomId = data.weightUomId;
				 			    var weightUomAbb = '';
				 			    for(var i = 0; i < weightUomData.length; i++){
									 if(weightUomId == weightUomData[i].uomId){
										 weightUomAbb = weightUomData[i].description;
									 }
								 }
				 			    return '<span style=\"text-align: right\">' + data.weight.toLocaleString('${localeStr}') +' (' + weightUomAbb +  ')</span>';
				 			}
					 	},
					 	{ text: '${uiLabelMap.TotalWeight}', dataField: 'totalWeight', cellsalign: 'right', minwidth: 150, editable: false,
					 		cellsrenderer: function(row, colum, value){
				 			    var data = $('#jqxgridShipmentItem').jqxGrid('getrowdata', row);
				 			    var total = (parseFloat(data.weight) * parseFloat(data.quantity)).toFixed(2);
				 			    var weightUomId = data.weightUomId;
				 			    var weightUomAbb = '';
				 			    for(var i = 0; i < weightUomData.length; i++){
									 if(weightUomId == weightUomData[i].uomId){
										 weightUomAbb = weightUomData[i].description;
									 }
								 }
				 			    var tmp = parseInt(total);
				 			    return '<span style=\"text-align: right\">' + tmp.toLocaleString('${localeStr}') +' (' + weightUomAbb +  ')</span>';
				 			}
					 	},
				 	"/>
<@jqGrid filtersimplemode="true" viewSize="5" width="900" id="jqxgridShipmentItem" usecurrencyfunction="true" addType="popup" dataField=dataField2 columnlist=columnlist2 clearfilteringbutton="true" showtoolbar="false" addrow="true" filterable="true" editmode="dblclick" editable="true" 
		url="jqxGeneralServicer?sname=getListShipmentItem&shipmentId=${parameters.shipmentId?if_exists}" bindresize="false" editrefresh="true" functionAfterUpdate="" customLoadFunction="true"
		updateUrl="" editColumns="shipmentId;shipmentItemSeqId;quantity(java.math.BigDecimal)" otherParams="" height="241" autoheight="false"
	/>
