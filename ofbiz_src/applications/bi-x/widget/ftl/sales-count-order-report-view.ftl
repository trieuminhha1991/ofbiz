<script type="text/javascript" id="salesOrderCount">

    $(function () {
        var config = {
            title: {
                text: 'SALE',
                x: -20 //center
            },
            xAxis: {
                labels: {
                    enabled: false
                },
                title: {
                    text: null
                }
            },
            yAxis: {
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }],
                title: {
                    text: null
                },
                min: 0
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            }
        };

        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'product_store',
                    label : 'Product Store',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}, 'delys_bac_gt'],
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'party_from',
                    label : 'Party From',
                    data : ['company'],
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'party_to',
                    label : 'Party To',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}, 'NPP01A01', 'NPP01A09'],
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'channel',
                    label : 'Channel',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}],
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_product)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}].concat(OLBIUS.getProduct()),
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateType',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_dateType)}',
                    data : date_type_source,
                    index: 2
                }]
            },
            {
                action: 'addDateTimeInput',
                params: [{
                    id: 'from_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
                }],
                before: 'thru_date'
            },
            {
                action: 'addDateTimeInput',
                params: [{
                    id: 'thru_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after: 'from_date'
            }
        ];

        var salesOrderOLap = OLBIUS.oLapChart('salesOrderCount', config, configPopup, 'salesOrderCountOlap', true, true, OLBIUS.defaultLineFunc);


        salesOrderOLap.funcUpdate(function (oLap) {

            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'dateType':  oLap.val('dateType'),
                'type': ['PRODUCT_STORE', 'PARTY_FROM', 'PARTY_TO', 'CHANNEL', 'PRODUCT', 'PARTY_TYPE'],
                'PRODUCT_STORE': oLap.val('product_store'),
                'PARTY_FROM': oLap.val('party_from'),
                'PARTY_TO': oLap.val('party_to'),
                'CHANNEL': oLap.val('channel'),
                'PRODUCT': oLap.val('productId'),
            }, oLap.val('dateType'));
        });
        salesOrderOLap.init(function () {
            salesOrderOLap.runAjax();
        });

    });

</script>