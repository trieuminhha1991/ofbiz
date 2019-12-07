<script type="text/javascript">
	<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)>
	var salesChannel = [
	    <#list salesChannel as salesChannelL>
	    {
	    	enumId : "${salesChannelL.enumId}",
	    	description: "${StringUtil.wrapString(salesChannelL.get("description", locale))}"
	    },
	    </#list>	
	];
	
	var listChannelDataSource = [];
  	for(var x in salesChannel){
    	var channelDataSource = {
     		text: salesChannel[x].description,
     		value: salesChannel[x].enumId,
    	}
    listChannelDataSource.push(channelDataSource);
   	} 
</script>
<script id="test">
$(function(){
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.KPromoSalesReport)}',
            groupable: true,
            closeablegroups: false,
            groups: ['promoName'],
            columns: [
                { text: '${StringUtil.wrapString(uiLabelMap.KPromotion)}', datafield: 'promoName', type: 'string', width: '25%'},
                { text: '${StringUtil.wrapString(uiLabelMap.KCustomer)}', datafield: 'party', type: 'string', width: '30%'},
                { text: '${StringUtil.wrapString(uiLabelMap.KProductName)}', datafield: 'productName', type: 'string', width: '30%'},
                { text: '${StringUtil.wrapString(uiLabelMap.KQuantity)}', datafield: 'quantity1', type: 'number', width: '15%', cellsalign:'right'}
            ]
        };

        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'organization',
                    label : 'organization',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_true)}', value: 'true'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_false)}', value: 'false'}],
                    index: 1
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateType',
                    label : 'dateType',
                    data : date_type_source,
                    index: 2
                }]
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
                }],
                before: 'thru_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after: 'from_date'
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'storeChannel',
                    label : '${StringUtil.wrapString(uiLabelMap.KChannel)}',
                    data : listChannelDataSource,
                }]
            }
        ];


        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluatePromoSalesOlapGridByChannel', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'orig': oLap.val('organization'),
                'dateType': oLap.val('dateType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'storeChannel': oLap.val('storeChannel')
            }, oLap.val('dateType'));
        });

        testGrid.init(function () {
            testGrid.runAjax();
        },function(oLap){
        	if(oLap){
        		var dateTypeInput = oLap.val('dateType');
            	var fromDateInput = oLap.val('from_date');
            	var thruDateInput = oLap.val('thru_date');
            	var storeChannelInput = oLap.val('storeChannel');
            	var dateFromDate = new Date(fromDateInput);
            	var dateThruDate = new Date(thruDateInput);
            	var dateFrom = dateFromDate.getTime();
            	var thruFrom = dateThruDate.getTime();
            	
            	window.location.href = "exportSalesReportPromoTo.pdf?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&storeChannel=" + storeChannelInput;
        	}else{
        		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
        		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
        		    "class" : "btn-small btn-primary width60px",
        		    }]
        		   );
        	}
        }
        );
    });
</script>