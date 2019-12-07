<script type="text/javascript" id="salesOrderTotal">

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
                    id : 'filter',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_filter)}',
                    data : ['PRODUCT_STORE', 'PARTY_FROM', 'PARTY_TO', 'CHANNEL', 'PRODUCT'],
                    index: 0
                }],
                event : function(popup) {
                    popup.onEvent('filter', 'select', function(event) {
                        var args = event.args;
                        var item = popup.item('filter', args.index);
                        var filter = item.value;
                        popup.hide('product_store');
                        popup.hide('party_from');
                        popup.hide('party_to');
                        popup.hide('channel');
                        popup.hide('productId');
                        popup.clear('product_store');
                        popup.clear('party_from');
                        popup.clear('party_to');
                        popup.clear('channel');
                        popup.clear('productId');
                        if(filter == 'PRODUCT_STORE') {
                            popup.show('product_store');
                        } else if(filter == 'PARTY_FROM') {
                            popup.show('party_from');
                        } else if(filter == 'PARTY_TO') {
                            popup.show('party_to');
                        } else if(filter == 'CHANNEL') {
                            popup.show('channel');
                        } else if(filter == 'PRODUCT') {
                            popup.show('productId');
                        }
                        popup.resize();
                    });
                }
            },
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
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}, 'company'],
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'party_to',
                    label : 'Party To',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}],
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'channel',
                    label : 'Channel',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}],
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_product)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}].concat(OLBIUS.getProduct()),
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'tax',
                    label : 'Tax',
                    data : [{'text': 'TRUE', 'value': true}, {'text': 'FALSE', 'value': false}],
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'currency',
                    label : 'Currency',
                    data : ['VND'],
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
                /*event: function (popup) {
                    popup.onEvent('from_date', 'valueChanged', function (event) {
                        var fromDate = event.args.date;
                        var thruDate = popup.getDate('thru_date');
                        if (thruDate < fromDate) {
                            popup.val('thru_date', fromDate);
                        }
                    });
                }*/
            },
            {
                action: 'addDateTimeInput',
                params: [{
                    id: 'thru_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after: 'from_date'
                /*event: function (popup) {
                    popup.onEvent('thru_date', 'valueChanged', function (event) {
                        var thruDate = event.args.date;
                        var fromDate = popup.getDate('from_date');
                        if (thruDate < fromDate) {
                            popup.val('from_date', thruDate);
                        }
                    });
                }*/
            }
        ];

        var salesOrderOLap = OLBIUS.oLapChart('salesOrderTotal', config, configPopup, 'salesOrderTotalOlap', true, true, OLBIUS.defaultLineFunc);

        salesOrderOLap.funcUpdate(function (oLap) {

            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'dateType':  oLap.val('dateType'),
                'currency':  oLap.val('currency'),
                'type': [oLap.val('filter')],
                'show': oLap.val('filter'),
                'PRODUCT_STORE': oLap.val('product_store'),
                'PARTY_FROM': oLap.val('party_from'),
                'PARTY_TO': oLap.val('party_to'),
                'CHANNEL': oLap.val('channel'),
                'PRODUCT': oLap.val('productId'),
                'taxFlag' : oLap.val('tax')
            }, oLap.val('dateType'));
        });
        salesOrderOLap.init(function () {
            salesOrderOLap.runAjax();
        });

    });

</script>