<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>

<script>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false)>
	var mapUomData = {
			<#if uoms?exists>
				<#list uoms as item>
					<#assign s1 = StringUtil.wrapString(item.get("description", locale)?if_exists)/>
					"${item.uomId?if_exists}": "${s1}",
				</#list>
			</#if>	
	};
	
	<#assign facilitys = delegator.findList("Facility", null, null, null, null, false)>
	var mapFacilityData = {
			<#if facilitys?exists>
				<#list facilitys as item>
					<#assign s1 = StringUtil.wrapString(item.get("facilityName", locale)?if_exists)/>
					"${item.facilityId?if_exists}": "${s1}",
				</#list>
			</#if>	
	};
	
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	};
	
	var dateCurrent = new Date();
	var todayString = "";
	todayString += addZero(dateCurrent.getDate()) + '/';
	todayString += addZero(dateCurrent.getMonth()+1) + '/';
	todayString += addZero(dateCurrent.getFullYear());
	var listOrderExport = '${StringUtil.wrapString(uiLabelMap.ListOrderExport)}' + " ("+todayString+")";
</script>

<#assign dataField="[
				{ name: 'orderId', type: 'string'},
				{ name: 'originFacilityId', type: 'string'},
				{ name: 'facilityName', type: 'string'},
				{ name: 'estimatedDeliveryDate', type: 'date', other: 'Timestamp'},
				{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
				{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
				{ name: 'quantity', type: 'number'},
				{ name: 'receiveFacility', type: 'string'},
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
				{ text: '${uiLabelMap.OrderId}', datafield: 'orderId', align: 'left', width: 100,
				},
				{ text: '${uiLabelMap.DateExpectedReceiveWarehousing}', dataField: 'shipAfterDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype:'range',
				},
				{ text: '${uiLabelMap.LogWarehouse}', datafield: 'facilityName', align: 'left', width: 120,
				},
				{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', align: 'center', cellsalign: 'right', width: 90, align: 'left',
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.StockIn)}', datafield: 'receiveFacility', filterable: false, sortable: false, cellsrenderer:
				       function(row, colum, value){
					        var data = $('#jqxgirdReportReceive').jqxGrid('getrowdata', row);
			        		return '<span><a href=\"' + 'viewDetailPO?orderId=' + data.orderId + '\"><i class=\"fa fa-download \"></i>' + '${uiLabelMap.StockIn}' + '</a></span>';
			           }
				},
			"/>
<#assign listOrderExport = '${StringUtil.wrapString(uiLabelMap.ListOrderExport)} ( ${nowTimestamp?date?string(\"dd/MM/yyyy\")} )' />
<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdReportReceive" addrefresh="true" filterable="true" viewSize="5"
	url="jqxGeneralServicer?sname=JQGetReportReceiveExpectedUnderOrderPortlet"
	customTitleProperties="ListOrderReceive" />	