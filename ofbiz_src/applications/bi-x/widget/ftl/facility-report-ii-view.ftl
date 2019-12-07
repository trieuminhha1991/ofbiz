
<script type="text/javascript" id="facilityOLapII">

    $(function () {

        var config = {
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.facility_ii_title)}',
                x: -20 //center
            },
            xAxis: {
                labels: {
                    enabled: false
                },
                title : {
                    text: null
                }
            },
            yAxis: {
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }],
                title : {
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

        var _geoType = OLBIUS.getGeoType();


        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'filter',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_filter)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': 'null'}, '${StringUtil.wrapString(uiLabelMap.facility_facilityId)}'].concat(_geoType),
                    index: 0
                }],
                event: function(popup) {
                    popup.onEvent('filter', 'select', function(event){
                        var args = event.args;
                        var item = popup.item('filter', args.index);
                        popup.hide('facility');
                        popup.clear('facility');
                        for(var i in _geoType) {
                            popup.hide('geoType-'+_geoType[i]);
                            popup.clear('geoType-'+_geoType[i]);
                        }
                        if(item.value == '${StringUtil.wrapString(uiLabelMap.facility_facilityId)}') {
                            popup.show('facility');
                        } else if(item.value != 'null') {
                            popup.show('geoType-'+item.value);
                        }
                        popup.resize();
                    });
                }
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'facility',
                    label : '${StringUtil.wrapString(uiLabelMap.facility_facilityId)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}].concat(OLBIUS.getFacility()),
                    index: 0,
                    hide : true
                }]
            }
        ];
        for(var i in _geoType) {
            configPopup.push({
                action : 'addDropDownList',
                params : [{
                    id : 'geoType-'+_geoType[i],
                    label : 'Geo',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}].concat(OLBIUS.getGeo(_geoType[i])),
                    index: 0,
                    hide: true
                }]
            })
        }

        configPopup = configPopup.concat([
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
                    id : 'dateType',
                    label : '${StringUtil.wrapString(uiLabelMap.facility_dateType)}',
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
        ]);

        var tmp = ['RECEIVE', 'EXPORT', 'INVENTORY', 'BOOK', 'AVAILABLE'];

        var $update = function(type) {
            return function(oLap) {
                var filter = oLap.val('filter');
                var facilityId = null;
                var geoType = null;
                var geo = null;
                if(filter == '${StringUtil.wrapString(uiLabelMap.facility_facilityId)}') {
                    facilityId = oLap.val('facility');
                }
                if(filter != 'null') {
                    geoType = filter;
                    geo = oLap.val('geoType-'+filter);
                }

                oLap.update({
                    'olapType': type,
                    'facilityId': facilityId,
                    'productId': oLap.val('productId'),
                    'fromDate': oLap.val('from_date'),
                    'thruDate': oLap.val('thru_date'),
                    'dateType': oLap.val('dateType'),
                    'geoId': geo,
                    'geoType': geoType
                }, oLap.val('dateType'));
            }
        }

        var index = 0;

        var color = 0;
        var marker = 0;

        var flag = true;
        
        var tickFlag = true;

        var $fn = function(data, chart, datetype, removeSeries, flagFunc, olap) {

            if(index == 0) {
                if (removeSeries) {
                    while (chart.series.length > 0) {
                        chart.series[0].remove(false);
                    }
                }
            }

            if(data.xAxis.length != 0 && tickFlag) {
                var _tmp = {
                    labels: {
                        enabled: true
                    },
                    categories: data.xAxis
                };

                if(datetype) {
                    _tmp['labels']['formatter'] = OLBIUS.getFormaterAxisLabel(datetype);
                }
                _tmp['tickInterval'] = OLBIUS.getTickIntervalSize(data.xAxis.length, OLBIUS.getTickInterval());

                chart.xAxis[0].update(_tmp, false);

                color = 0;
                marker = 0;
                tickFlag = false;
            }


            for (var i in data.yAxis) {
                chart.addSeries({
                    name: tmp[index],
                    data: data.yAxis[i],
                    color: Highcharts.getOptions().colors[color++],
                    marker: {
                        symbol: Highcharts.getOptions().symbols[marker++]
                    }
                }, false);
            }

            if(data.xAxis.length != 0) {
                flag = false;
            }

            index++;

            olap.funcUpdate($update(tmp[index]));

            if(index == tmp.length - 1) {
                chart.redraw();

                if(flag) {
                    flagFunc();
                }
                index = 0;
            } else {
                olap.runAjax();
            }
        };

        var facilityOLap = OLBIUS.oLapChart('facilityOLapII', config, configPopup, 'facilityProductOlap', true, true, $fn);

        facilityOLap.funcUpdate($update(tmp[0]));

        facilityOLap.init(function () {
        	tickFlag = true;
            facilityOLap.runAjax();
        });

    });

</script>