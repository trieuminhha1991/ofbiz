
<script type="text/javascript" id="facilityPieOLap">

    $(function () {

        var config = {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.facility_pie_title)}'
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
        ]);

        var facilityOLap = OLBIUS.oLapChart('facilityPieOLap', config, configPopup, 'facilityProductOlap', true, true, OLBIUS.defaultPieFunc);

        facilityOLap.funcUpdate(function(oLap) {
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
                'olapType': oLap.val('olapType'),
                'facilityId': facilityId,
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'geoId': geo,
                'geoType': geoType
            });
        });

        facilityOLap.init(function () {
            facilityOLap.runAjax();
        });
    });

</script>
