<@jqGridMinimumLib/>
<script>
	//Prepare for product data
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	
	<#assign listTypes = ["PRODUCT_PACKING", "WEIGHT_MEASURE"]>
	<#assign uomList2 = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listTypes), null, null, null, false) />
	var uomData = 
	[
		<#list uomList2 as uom>
			<#if uom.uomTypeId == "WEIGHT_MEASURE">
				{
					uomId: "${uom.uomId}",
					description: "${StringUtil.wrapString(uom.get('abbreviation', locale)?if_exists)}"
				},
			<#else>
				{
				uomId: "${uom.uomId}",
				description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
				},
			</#if>
		</#list>
	];
	function getUomDescription(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
	}
</script>


<#assign dataField="[
				{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'facilityName', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'facilityId', type: 'string'},
				{ name: 'requireAmount', type: 'string'},
				{ name: 'quantityOnHandTotal', type: 'number'},
				{ name: 'amountOnHandTotal', type: 'number'},
				{ name: 'thresholdsDate', type: 'number'},
				{ name: 'availableToPromiseTotal', type: 'number'},
				{ name: 'quantityUomId', type: 'String'},
				{ name: 'weightUomId', type: 'String'},
				{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
				{ name: 'dateSaleNumber', type: 'number'},
				{ name: 'exceedthresholds', type: 'number'},
				{ name: 'timeUom', type: 'String'},
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
				{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 120,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', width: 250,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.Facility}', datafield: 'facilityName', align: 'left', width: 120,
					cellsrenderer: function (row, column, value){
						if(value){
						}
					}
				},
				{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', hidden: true, align: 'left', width: 100, cellsalign: 'right', columntype: 'numberinput', filtertype: 'number',
					cellsrenderer: function(row, column, value){
						if (value){
       						var desc = '';
       						var data = $('#jqxgirdProductInventoryDateReach').jqxGrid('getrowdata', row);
    						for(var i = 0; i < quantityUomData.length; i ++){
    							if (quantityUomData[i].uomId == data.quantityUomId){
    								desc = quantityUomData[i].description;
    								break;
    							}
    						}
       						return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 150, cellsalign: 'right', columntype: 'numberinput', filtertype: 'number',
					cellsrenderer: function(row, column, value){
       					if (value){
       						var data = $('#jqxgirdProductInventoryDateReach').jqxGrid('getrowdata', row);
       						var requireAmount = data.requireAmount;
       						if (requireAmount && requireAmount == 'Y') {
       							value = data.amountOnHandTotal;
       						}
       						return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', filterable: false,
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgirdProductInventoryDateReach').jqxGrid('getrowdata', row);
   						var requireAmount = data.requireAmount;
   						if (requireAmount && requireAmount == 'Y') {
   							value = data.weightUomId;
   						}
						return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(quantityUomData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
							renderer: function(index, label, value){
					        	if (quantityUomData.length > 0) {
									for(var i = 0; i < quantityUomData.length; i++){
										if(quantityUomData[i].uomId == value){
											return '<span>' + quantityUomData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', width: 120 },
				{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', dataField: 'expireDate', align: 'left', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', width: 120},
				{ text: '${uiLabelMap.RemainingDay}', datafield: 'dateSaleNumber', align: 'left', width: 130, cellsalign: 'right', columntype: 'numberinput', filtertype: 'number', filterable: false,
					cellsrenderer: function(row, column, value){
						var listData = $('#jqxgirdProductInventoryDateReach').jqxGrid('getrowdata', row);
  						var expireDate = listData.expireDate;
  						expireDate.setHours(0,0,0);
  						var today = new Date();
  						today.setHours(0,0,0);
  						var expireDateSecond = expireDate.getTime();
  						var todaySecond = today.getTime();
  						var valueDate = (expireDateSecond - todaySecond)/(24*60*60*1000);
       					if (valueDate != null){
       						var numberDate = valueDate | 0;
       						return '<span style=\"text-align: right\">' + formatnumber(numberDate) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.Thresholds}', datafield: 'thresholdsDate', align: 'left', width: 130, cellsalign: 'right', columntype: 'numberinput', filtertype: 'number',  filterable: false,
					cellsrenderer: function(row, column, value){
						return '<span style=\"text-align: right;\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
					},
				},
				{ text: '${uiLabelMap.QxceedThreshold}', datafield: 'exceedthresholds', align: 'left', width: 130, cellsalign: 'right', columntype: 'numberinput', filtertype: 'number', filterable: false,
					cellsrenderer: function(row, column, value){
						var listData = $('#jqxgirdProductInventoryDateReach').jqxGrid('getrowdata', row);
  						var expireDate = listData.expireDate;
  						expireDate.setHours(0,0,0);
  						var thresholdsDate = listData.thresholdsDate;
  						var exceedDateTmp = expireDate.getTime() - (thresholdsDate*24*60*60*1000);
  						var exceedDate = new Date(exceedDateTmp);
  						var today = new Date();
  						today.setHours(0,0,0);
  						var valueDate = (today.getTime() - exceedDate.getTime())/(24*60*60*1000);
       					if (valueDate != null){
       						var numberDate = valueDate | 0;
       						return '<span style=\"text-align: right\">' + formatnumber(numberDate) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.TimeUom}', datafield: 'timeUom', align: 'left', width: 120, filterable: false,
					cellsrenderer: function(row, column, value){
						return '<span style=\"text-align: right;\">${uiLabelMap.Day}</span>';
					},
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ReceiveDate)}', dataField: 'datetimeReceived', align: 'left', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', width: 120},
			"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdProductInventoryDateReach" addrefresh="true" filterable="true" mouseRightMenu="true" contextMenuId="menuReachDate"
	url="jqxGeneralServicer?sname=JQGetListInventoryReachDate&productId=${parameters.productId?if_exists}" 
	customTitleProperties="LogListProductInventoryDateReach" />	
				
<div id='menuReachDate' style="display:none;">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<script>
	$(document).ready(function (){
		$("#menuReachDate").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	});
	$("#menuReachDate").on('itemclick', function (event) {
		var tmpStr = $.trim($(args).text());
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
			$('#jqxgirdProductInventoryDateReach').jqxGrid('updatebounddata');
		}
	});
</script>