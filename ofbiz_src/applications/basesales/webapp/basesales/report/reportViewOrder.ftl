<style>
	.jqx-widget-header-olbius > div > div {
	    white-space: normal;
		text-align: center!important;
	}
</style>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
 	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
</script>
<script  type="text/javascript">
<#assign orderStatusData = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "ORDER_STATUS"), null, false)>
	var orderStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
];

   	var statusItemData = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if orderStatusData?exists>
		<#list orderStatusData as orderStatusL >
			statusItemData.push({ 'value': '${orderStatusL.statusId?if_exists}', 'text': '${StringUtil.wrapString(orderStatusL.get("description", locale))?if_exists}'});
		</#list>
	</#if>

<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!>
	
   	var channelData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if salesChannel?exists>
		<#list salesChannel as salesChannelL >
			channelData2.push({ 'value': '${salesChannelL.enumId?if_exists}', 'text': '${StringUtil.wrapString(salesChannelL.get("description", locale))?if_exists}'});
		</#list>
	</#if>
	
	
	var filterType = [{'text': '${StringUtil.wrapString(uiLabelMap.BSNullObject)}', 'value': null}, {'text': '${StringUtil.wrapString(uiLabelMap.BSEstimateDate)}', 'value': 'ESTIMATE'}, {'text': '${StringUtil.wrapString(uiLabelMap.BSRangeDate)}', 'value': 'RANGE'}];
</script>
<script id="test">
$(function(){
	var column = [];
	
	column.push({ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
						datafield: 'stt', columntype: 'number', width: '3%',
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (value + 1) + '</div>';
						}
					});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: 'orderId', type: 'string', width: '15%', cellsalign: 'left', pinned: true});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSOrderDate)}', datafield: 'orderDate', type: 'olapDate', width: '16%', cellsalign: 'left',});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', datafield: 'channel', type: 'string', width: '15%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSCallcenterId)}', datafield: 'caceId', type: 'string', width: '15%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSCallcenterName)}', datafield: 'caceName', type: 'string', width: '15%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSSalesExecutiveId)}', datafield: 'saexId', type: 'string', width: '15%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSSalesExecutiveName)}', datafield: 'saexName', type: 'string', width: '15%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'cusId', type: 'string', width: '15%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'cusName', type: 'string', width: '20%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerAddress)}', datafield: 'cusAddress', type: 'string', width: '25%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerPhone)}', datafield: 'cusPhone', type: 'string', width: '10%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSOrderAddress)}', datafield: 'orderAddress', type: 'string', width: '25%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSAddress1)}', datafield: 'orderRoad', type: 'string', width: '10%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSWard)}', datafield: 'orderWard', type: 'string', width: '10%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSCounty)}', datafield: 'orderDistrict', type: 'string', width: '10%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSStateProvince)}', datafield: 'orderState', type: 'string', width: '10%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSCountry)}', datafield: 'orderCountry', type: 'string', width: '10%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', datafield: 'orderStatus', type: 'string', width: '10%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', datafield: 'orderStatusId', type: 'string', width: '0%', cellsalign: 'left', hidden: true});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', datafield: 'deliveryStatusId', type: 'string', width: '0%', cellsalign: 'left', hidden: true});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSManufacturingDeliveryDate)}', datafield: 'expectedDeliveryDate', type: 'olapDate', width: '15%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSDeliveryRangeDate)}', datafield: 'expectedDeliveryRangeDate', type: 'olapDate', width: '29%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSDeliveryDate)}', datafield: 'actualDeliveryDate', type: 'olapDate', width: '15%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSDeliveryStatus)}', datafield: 'deliveryStatus', type: 'string', width: '10%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSOrderNote)}', datafield: 'orderNote', type: 'olapDate', width: '8%', cellsalign: 'left'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'orderValue', type: 'number', width: '15%', cellsalign: 'right', cellsformat: 'n2'});
	column.push({ text: '${StringUtil.wrapString(uiLabelMap.BSTax)}', datafield: 'orderTax', type: 'number', width: '10%', cellsalign: 'right', cellsformat: 'n2'});
	
	$.ajax({url: 'getStoreListColumn',
	    type: 'post',
	    async: false,
	    success: function(data) {
			var listDatafield = data.listResultStore;
	    	for (var i = 0; i < listDatafield.length; i++){
	    		var name = listDatafield[i].internal_name ? listDatafield[i].internal_name : "";
	    		var code = listDatafield[i].product_code;
	    		var full_title = name + "</b><br><b>" + code;
	    		var field = {text: full_title, datafield:listDatafield[i].product_id, type: 'string', width: '12.5%', cellsalign: 'right', cellsformat: 'n2', align: 'center', };
	    		column.push(field);
    		}
	    },
	    error: function(data) {
	    	alert('Error !!');
	    }
	});
	
	var config = {
	    title: '${StringUtil.wrapString(uiLabelMap.BSOrderReport)}',
	    service: 'salesOrder',
	    columns: column,
	    columnsheight: 75,
	};

	var configPopup = [
	    {
	        action : 'addDateTimeInput',
	        params : [{
	            id : 'from_date',
	            label : '${StringUtil.wrapString(uiLabelMap.BSFromDateOrder)}',
	            value: OLBIUS.dateToString(currentFirstDay)
	        }],
	        before: 'thru_date'
	    },
	    {
            action : 'addDateTimeInput',
            params : [{
                id : 'from_date_2',
                label : '${StringUtil.wrapString(uiLabelMap.BSFromDateDelivery)}',
                value: OLBIUS.dateToString(currentFirstDay),
            }],
            before: 'thru_date_2'
        },
	    {
	        action : 'addDateTimeInput',
	        params : [{
	            id : 'thru_date',
	            label : '${StringUtil.wrapString(uiLabelMap.BSThruDateOrder)}',
	            value: OLBIUS.dateToString(cur_date),
	        }],
	        after: 'from_date'
	    },
	    {
            action : 'addDateTimeInput',
            params : [{
                id : 'thru_date_2',
                label : '${StringUtil.wrapString(uiLabelMap.BSThruDateDelivery)}',
                value: OLBIUS.dateToString(cur_date),
            }],
            after: 'from_date_2'
	    },
	    {
	        action : 'addDropDownListMultil',
	        params : [{
	            id : 'orderStatus',
	            label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
	            data : statusItemData,
	            index: 0
	        }]
	    },
	    {
	        action : 'addDropDownListMultil',
	        params : [{
	            id : 'channel',
	            label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
	            data : channelData2,
	            index: 0
	        }]
	    },
	];

    var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateOrderGrid', true);

    testGrid.funcUpdate(function (oLap) {
        oLap.update({
        	'fromDate': oLap.val('from_date'),
        	'thruDate': oLap.val('thru_date'),
            'fromDate2': oLap.val('from_date_2'),
            'thruDate2': oLap.val('thru_date_2'),
            'orderStatus': oLap.val('orderStatus'),
            'channel': oLap.val('channel'),
        });
    });

    testGrid.init(function () {
        testGrid.runAjax();
    }, false, function(oLap){
    	var dataAll = oLap.getAllData();
    	if(dataAll.length != 0){
        	var fromDateInput = oLap.val('from_date');
        	var thruDateInput = oLap.val('thru_date');
        	var dateFromDate = new Date(fromDateInput);
        	var dateThruDate = new Date(thruDateInput);
        	var dateFrom = dateFromDate.getTime();
        	var thruFrom = dateThruDate.getTime();
        	var fromDateInput2 = oLap.val('from_date_2');
        	var thruDateInput2 = oLap.val('thru_date_2');
        	var dateFromDate2 = new Date(fromDateInput2);
        	var dateThruDate2 = new Date(thruDateInput2);
        	var dateFrom2 = dateFromDate2.getTime();
        	var thruFrom2 = dateThruDate2.getTime();
        	var orderStatus = oLap.val('orderStatus');
        	var channel = oLap.val('channel');
        	
        	window.location.href = "exportOrderReportToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&orderStatus=" + orderStatus  + "&channel=" + channel + "&fromDate2=" + dateFrom2 + "&thruDate2=" + thruFrom2;
    	}else{
    		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
    		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
    		    "class" : "btn-small btn-primary width60px",
    		    }]
    		   );
    	}
	});
});
</script>
<#--

{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress1)}', datafield: 'address', type: 'string', width: '15%', cellsalign: 'left'},
                { text: '${StringUtil.wrapString(uiLabelMap.Ward)}', datafield: 'ward', type: 'string', width: '15%', cellsalign: 'left'},
                { text: '${StringUtil.wrapString(uiLabelMap.District)}', datafield: 'district', type: 'string', width: '15%', cellsalign: 'left'},
                { text: '${StringUtil.wrapString(uiLabelMap.CityProvince)}', datafield: 'state', type: 'string', width: '15%', cellsalign: 'left'},
                
                
{
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date_2',
                    label : '${StringUtil.wrapString(uiLabelMap.BSFromDateDelivery)}',
                    value: OLBIUS.dateToString(past_date),
                    hide: true
                }],
                before: 'thru_date_2'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date_2',
                    label : '${StringUtil.wrapString(uiLabelMap.BSThruDateDelivery)}',
                    value: OLBIUS.dateToString(cur_date),
                    hide: true
                }],
                after: 'from_date_2'
},
{
            action : 'addDropDownList',
            params : [{
                id : 'orderStatus',
                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
                data : statusItemData,
                index: 0
            }],
            event : function(popup) {
                    popup.onEvent('orderStatus', 'select', function(event) {
                        var args = event.args;
                        var item = popup.item('orderStatus', args.index);
                        var filter = item.value;
                        popup.hide('from_date_2');
                        popup.hide('thru_date_2');
                        popup.clear('from_date_2');
                        popup.clear('thru_date_2');
                        if(filter == 'ORDER_COMPLETED') {
                            popup.show('from_date_2');
                            popup.show('thru_date_2');
                        }
                        popup.resize();
                    });
                }
       		},
 -->