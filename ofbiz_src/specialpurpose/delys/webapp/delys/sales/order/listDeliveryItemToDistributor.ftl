<#assign localeStr = "VI" />
<#if locale = "en">
	<#assign localeStr = "EN" />
</#if>
<script type="text/javascript">
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		quantityUomData[${item_index}] = row;
	</#list>
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", userLogin.partyId), null, null, null, false)>
	var facilityData = [];
	<#list facilities as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("facilityName", locale)) />
		row['facilityId'] = '${item.facilityId}';
		row['description'] = '${description}';
		facilityData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${description?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	function getFormattedDate(date) {
		  var year = date.getFullYear();
		  var month = (1 + date.getMonth()).toString();
		  month = month.length > 1 ? month : '0' + month;
		  var day = date.getDate().toString();
		  day = day.length > 1 ? day : '0' + day;
		  return day + '/' + month + '/' + year;
	}
</script>
<#assign dataField2="[{ name: 'deliveryId', type: 'string' },
                 	{ name: 'deliveryItemSeqId', type: 'string' },
                 	{ name: 'fromOrderId', type: 'string' },
                 	{ name: 'fromOrderItemSeqId', type: 'string' },
                 	{ name: 'fromTransferItemSeqId', type: 'string' },
                 	{ name: 'productId', type: 'string' },
                 	{ name: 'quantityUomId', type: 'string' },
                 	{ name: 'comment', type: 'string' },
                 	{ name: 'actualExportedQuantity', type: 'number' },
                 	{ name: 'actualDeliveredQuantity', type: 'number' },
                 	{ name: 'statusId', type: 'string' },
                 	{ name: 'quantity', type: 'number' },
                 	{ name: 'inventoryItemId', type: 'string' },
					{ name: 'actualExpireDate', type: 'date', other: 'Timestamp'},
					{ name: 'actualDeliveredExpireDate', type: 'date', other: 'Timestamp'},
					{ name: 'expireDate', type: 'date', other: 'Timestamp'},
                 	{ name: 'deliveryStatusId', type: 'string'},
					{ name: 'weight', type: 'number'},
					{ name: 'weightUomId', type: 'String'},
					{ name: 'facilityId', type: 'String'},
		 		 	]"/>
<#assign columnlist2="
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
					        groupable: false, draggable: false, resizable: false,
					        datafield: '', columntype: 'number', width: 50,
					        cellsrenderer: function (row, column, value) {
					            return '<span style=margin:4px;>' + (value + 1) + '</span>';
					        }
					    },
						{ text: '${uiLabelMap.ProductProductId}', dataField: 'productId', width: 150, editable: false, pinned: true},
						{ text: '${uiLabelMap.ExpireDate}', cellsalign: 'right', dataField: 'actualExpireDate', cellsformat:'dd/MM/yyyy', width: 150, editable: false},
						{ text: '${uiLabelMap.quantity}', dataField: 'actualExportedQuantity', width: 150, editable: false,
							cellsrenderer: function(row, column, value){
								return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
							}
						},
						{ text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', cellsalign: 'left', width: 80, editable: false,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridDeliveryItem').jqxGrid('getrowdata', row);
								var descriptionUom = data.quantityUomId;
								for(var i = 0; i < quantityUomData.length; i++){
									if(data.quantityUomId == quantityUomData[i].uomId){
										descriptionUom = quantityUomData[i].description;
								 	}
								}
								return '<span>'+ descriptionUom +'</span>';
							 }
						},
//						{ text: '${uiLabelMap.ActualDeliveredExpireDate}', cellsalign: 'right', dataField: 'actualDeliveredExpireDate', cellsformat:'dd/MM/yyyy', width: 150, editable: false, columntype: 'datetimeinput',
//							cellsrenderer: function(row, column, value){
//								var data = $('#jqxgridDeliveryItem').jqxGrid('getrowdata', row);
//								var exp = data.actualExpireDate;
//								if (exp.getMonth()+1 < 10){
//									return '<span style=\"text-align: right\">' + exp.getDate() + '/0' + (exp.getMonth()+1) + '/' + exp.getFullYear() + '</span>';
//								} else {
//									return '<span style=\"text-align: right\">' + exp.getDate() + '/' + (exp.getMonth()+1) + '/' + exp.getFullYear() + '</span>';
//								}
//								
//							},
//							initeditor: function (row, cellvalue, editor) {
//								if (!cellvalue){
//									var data = $('#jqxgridDeliveryItem').jqxGrid('getrowdata', row);
//									editor.jqxDateTimeInput('val', Date(data.actualExpireDate));
//								}
//							},
//						},
//						{ text: '${uiLabelMap.actualDeliveredQuantity}', dataField: 'actualDeliveredQuantity', width: 150, editable: false, cellsformat: 'number', columntype: 'numberinput',
//							cellsrenderer: function(row, column, value){
//								var data = $('#jqxgridDeliveryItem').jqxGrid('getrowdata', row);
//								var actualExportedQuantity = data.actualExportedQuantity;
//								if (value){
//									return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
//								} else {
//									return '<span style=\"text-align: right\" title=' + actualExportedQuantity.toLocaleString('${localeStr}') + '>' + actualExportedQuantity.toLocaleString('${localeStr}') + '</span>'
//								}
//							},
//							initeditor: function (row, cellvalue, editor) {
//								if (!cellvalue){
//									var data = $('#jqxgridDeliveryItem').jqxGrid('getrowdata', row);
//									editor.jqxNumberInput('val', data.actualExportedQuantity);
//								}
//		                     },
//						},
						{ text: '${StringUtil.wrapString(uiLabelMap.FacilityToReceive)}', datafield: 'facilityId', minwidth: 150, align: 'left', columntype: 'dropdownlist',
							cellsrenderer: function(row, column, value){
								if (value){
									for (var i=0; i<facilityData.length; i++){
										if (facilityData[i].facilityId == value){
											return '<span title=' + value + '>' + facilityData[i].description + '</span>';
										}
									}
								} else {
									return '<span title=' + value + '>' + facilityData[0].description + '</span>';
								}
							},
							initeditor: function (row, cellvalue, editor) {
								var sourceDataFacility =
								{
				                   localdata: facilityData,
				                   datatype: 'array'
								};
								var dataAdapterFacility = new $.jqx.dataAdapter(sourceDataFacility);
								editor.jqxDropDownList({ selectedIndex: 0, source: dataAdapterFacility, displayMember: 'description', valueMember: 'facilityId'
								});
							 },
						},
	 	"/>
<@jqGrid id="jqxgridDeliveryItem" autoheight="true" dataField=dataField2 columnlist=columnlist2 clearfilteringbutton="false" showtoolbar="true" filterable="false" editable="true" 
		url="jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=${parameters.deliveryId?if_exists}" jqGridMinimumLibEnable="false" offlinerefreshbutton="false"
		otherParams="productId,quantityUomId,expireDate:S-getDeliveryItemDetail(deliveryId,deliveryItemSeqId)<productId,quantityUomId,expireDate>;"
		customTitleProperties="ProductsWillBeReceive" viewSize="5" sortable="true" selectionmode="checkbox" editmode="dblclick" updateoffline="true"
		defaultSortColumn="deliveryItemSeqId ASC"/>
