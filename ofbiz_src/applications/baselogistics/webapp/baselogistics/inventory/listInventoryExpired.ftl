<@jqGridMinimumLib/>
<script>
	//Prepare for product data
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	
	
	<#assign listTypes = ["PRODUCT_PACKING", "WEIGHT_MEASURE"]>
	<#assign uomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listTypes), null, null, null, false) />
	var uomData = 
	[
		<#list uomList as uom>
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

	<#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("ownerPartyId", ownerPartyId)), null, null, null, false) />
	var mapFacilityData = {  
			<#if facilitys?exists>
				<#list facilitys as item>
					"${item.facilityId?if_exists}": '${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}',
				</#list>
			</#if>	
	};
	
</script>


<#assign dataField="[
				{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'facilityId', type: 'string'},
				{ name: 'quantityUomId', type: 'string'},
				{ name: 'weightUomId', type: 'string'},
				{ name: 'lotId', type: 'string'},
				{ name: 'facilityName', type: 'string'},
				{ name: 'requireAmount', type: 'string'},
				{ name: 'quantityOnHandTotal', type: 'number'},
				{ name: 'amountOnHandTotal', type: 'number'},
				{ name: 'availableToPromiseTotal', type: 'number'},
				{ name: 'uomId', type: 'String'},
				{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
				{ name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
				{ name: 'expireDate', type: 'date', other: 'Timestamp'}, 
				{ name: 'dateSaleNumber', type: 'number'}
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
				{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 130,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 200,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.Facility}', datafield: 'facilityName', align: 'left', width: 150,
					cellsrenderer: function (row, column, value){
					}
				},
				{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', hidden: true, align: 'left', width: 100, cellsalign: 'right', columntype: 'numberinput',
					cellsrenderer: function(row, column, value){
       					if (value){
       						return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 150, cellsalign: 'right', columntype: 'numberinput',
					cellsrenderer: function(row, column, value){
       					if (value){
       						var data = $('#jqxgirdInventoryExpire').jqxGrid('getrowdata', row);
       						if (data.requireAmount && data.requireAmount == 'Y') {
       							value = data.amountOnHandTotal;
       						} 
       						return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
       					}
       				},
				},
				{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', filterable: false,
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgirdInventoryExpire').jqxGrid('getrowdata', row);
   						if (data.requireAmount && data.requireAmount == 'Y') value = data.weightUomId;
						return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
							renderer: function(index, label, value){
					        	if (uomData.length > 0) {
									for(var i = 0; i < uomData.length; i++){
										if(uomData[i].uomId == value){
											return '<span>' + uomData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			}
				},
				{ text: '${uiLabelMap.DateRemainingNumber}', datafield: 'dateSaleNumber', align: 'center', width: 120, cellsalign: 'right', columntype: 'numberinput',
					cellsrenderer: function(row, column, value){
						var listData = $('#jqxgirdInventoryExpire').jqxGrid('getrowdata', row);
  						var expireDate = listData.expireDate;
  						var today = new Date();
  						var expireDateSecond = expireDate.getTime();
  						var todaySecond = today.getTime();
  						var valueDate = (expireDateSecond - todaySecond)/(24*60*60*1000);
       					if (valueDate != null){
       						var numberDate = valueDate | 0;
       						return '<span style=\"text-align: right\">' + numberDate +'</span>';
       					}
       				},
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', dataField: 'expireDate', align: 'left', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', width: 120},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', width: 120 },
				{ text: '${StringUtil.wrapString(uiLabelMap.ReceivedDate)}', dataField: 'datetimeReceived', align: 'left', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right', width: 120 },
				{ text: '${uiLabelMap.Batch}', datafield: 'lotId', align: 'left', width: 100,
					cellsrenderer: function (row, column, value){
					}
				},
			"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdInventoryExpire" addrefresh="true" filterable="true" mouseRightMenu="true" contextMenuId="menuExpiredDate"
	url="jqxGeneralServicer?sname=JQGetListInventoryExpireDate&productId=${parameters.productId?if_exists}" 
	customTitleProperties="BLListProductInventoryExpired" />	
<div id='menuExpiredDate' style="display:none;">
	<ul>
    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<script>
	$(document).ready(function (){
		$("#menuExpiredDate").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	});
	$("#menuExpiredDate").on('itemclick', function (event) {
		var tmpStr = $.trim($(args).text());
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
			$('#jqxgirdInventoryExpire').jqxGrid('updatebounddata');
		}
	});
</script>