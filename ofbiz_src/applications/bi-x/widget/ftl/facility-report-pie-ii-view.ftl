
<script type="text/javascript" id="facilityPieIIOLap">

    $(function () {

        var config = {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.facility_pie_ii_title)}'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            series: [{
                type: 'pie'
            }],
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            }
        };

        var _olapTypeSource = [{text: '${StringUtil.wrapString(uiLabelMap.facility_receive)}', value: 'RECEIVE'}, {text: '${StringUtil.wrapString(uiLabelMap.facility_export)}', value: 'EXPORT'},
            {text:'${StringUtil.wrapString(uiLabelMap.facility_inventory)}', value: 'INVENTORY'}, {text: '${StringUtil.wrapString(uiLabelMap.facility_book)}', value: 'BOOK'},
            {text: '${StringUtil.wrapString(uiLabelMap.facility_available)}', value: 'AVAILABLE'}];

        var _geoType = OLBIUS.getGeoType();

        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'filter',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_filter)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.facility_facilityId)}', 'value': 'null'}].concat(_geoType),
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.facility_productId)}',
                    data : OLBIUS.getProduct(),
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'olapType',
                    label : '${StringUtil.wrapString(uiLabelMap.facility_olapType)}',
                    data : _olapTypeSource,
                    index: 0
                }]
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
                }],
                before : 'thru_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after : 'from_date'
            }
        ];

        var facilityOLap = OLBIUS.oLapChart('facilityPieIIOLap', config, configPopup, 'facilityProductOlap', true, true, OLBIUS.defaultPieFunc);

        facilityOLap.funcUpdate(function(oLap) {
            var filter = oLap.val('filter');
            var geoType = null;
            if(filter != 'null') {
                geoType = filter;
            }
            oLap.update({
                'productId': oLap.val('productId'),
                'olapType': oLap.val('olapType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'geoType': geoType
            });
        });

        facilityOLap.init(function () {
            facilityOLap.runAjax();
        });
    });

</script>
