<#--<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>-->

<script type="text/javascript" id="evaluateAccOLapII">

    $(function () {

        var config = {
        	service: 'acctgTransTotal',
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.accNetRevenueChart)}',
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
            },
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true,
                        formatter: function() {
                            if(this.point.index == 0) {
                                return '0%';
                            } else {
                                if(this.series.data[this.point.index-1].y == 0) return '0%';
                                return parseFloat((this.y/this.series.data[this.point.index-1].y)*100 - 100).toFixed(2) + '%';
                            }
                        }
                    }
                }
            }
        };

        <#assign groups = delegator.findByAnd("PartyRole", Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "SUBSIDIARY"), null, false)>
        var _group = [];
        <#list groups as g>
            var x = "${g.partyId?if_exists}";
            _group.push(x);
        </#list>
        var configPopup = {
            'group' : {
                action : 'addDropDownList',
                params : [{
                    id : 'group',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_group)}',
                    data : _group,
                    index: 0
                }]
            },
            'dateType' : {
                action : 'addDropDownList',
                params : [{
                    id : 'dateType',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_dateType)}',
                    data : date_type_source,
                    index: 2
                }]
            },
            'currency' : {
                action : 'addDropDownList',
                params : [{
                    id : 'currency',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_currency)}',
                    data : ['VND'],
                    index: 0
                }]
            },
            'organization' : {
                action : 'addDropDownList',
                params : [{
                    id : 'organization',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_organization)}',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_true)}', value: 'true'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_false)}', value: 'false'}],
                    index: 0
                }]
            },
            /*'account' : {
                action : 'addDropDownList',
                params : [{
                    id : 'account',
                    label : 'Account',
                    data : [{text: 'Doanh thu bán hàng', value: '511'}],
                    index: 0
                }]
            },
            'debitCreditFlag' : {
                action : 'addDropDownList',
                params : [{
                    id : 'debitCreditFlag',
                    label : 'Flag',
                    data : [{text: 'Credit', value: 'C'}, {text: 'Debit', value: 'D'}],
                    index: 0
                }]
            },
            'account2' : {
                action : 'addDropDownList',
                params : [{
                    id : 'account2',
                    label : 'Account 2',
                    data : [{text: 'Các khoản giảm trừ', value: '521'}],
                    index: 0
                }]
            },
            'debitCreditFlag2' : {
                action : 'addDropDownList',
                params : [{
                    id : 'debitCreditFlag2',
                    label : 'Flag 2',
                    data : [{text: 'Credit', value: 'C'}, {text: 'Debit', value: 'D'}],
                    index: 1
                }]
            },*/
            'level' : {
                action : 'addDropDownList',
                params : [{
                    id : 'level',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_level)}',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_true)}', value: 'true'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_false)}', value: 'false'}],
                    index: 0
                }]
            },
            'limit': {
                action : 'addDropDownList',
                params : [{
                    id : 'limit',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_limit)}',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_unlimit)}', value: '0'}, '2', '5', '10', '20'],
                    index: 2
                }]
            },
            'sort': {
                action : 'addDropDownList',
                params : [{
                    id : 'sort',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_sort)}',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_asc)}', value: true}, {text: '${StringUtil.wrapString(uiLabelMap.olap_desc)}', value: false}],
                    index: 0
                }]
            },
            'distrib' : {
                action : 'addDropDownList',
                params : [{
                    id : 'distribution',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_distribution)}',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_true)}', value: 'true'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_false)}', value: 'false'}],
                    index: 0
                }]
            },
            'fromDateId' : {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
                }],
                event: function(popup) {
                    popup.onEvent('from_date', 'valueChanged', function(event){
                        var fromDate = event.args.date;
                        var thruDate = popup.getDate('thru_date');
                        if(thruDate < fromDate) {
                            popup.val('thru_date', fromDate);
                        }
                    });
                }
            },
            'thruDateId' : {
                action: 'addDateTimeInput',
                params: [{
                    id: 'thru_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                event: function (popup) {
                    popup.onEvent('thru_date', 'valueChanged', function (event) {
                        var thruDate = event.args.date;
                        var fromDate = popup.getDate('from_date');
                        if (thruDate < fromDate) {
                            popup.val('from_date', thruDate);
                        }
                    });
                }
            }
        };
//        var _group = ['MB'];

        var func = function(data, chart, dateType, removeSeries, flagFunc, oLap) {
            var yAxis = data.yAxis;
            var xAxis = data.xAxis;
            var group;
//            if(oLap.val('level') == 'false') {
//                group = _group;
//            } else {
//                group = null;
//            }
            jQuery.ajax({
                url: 'evaluateAccOlap',
                async: false,
                type: 'POST',
                data: {
                	'service': 'acctgTrans',
                    'group': oLap.val('group'),
                    'product': null,
//                    'code': oLap.val('account2'),
                    'code': '521',
//                    'debitCreditFlag': oLap.val('debitCreditFlag2'),
                    'debitCreditFlag': 'D',
                    'currency':  oLap.val('currency'),
                    'dateType':  oLap.val('dateType'),
                    'fromDate': oLap.val('from_date'),
                    'thruDate': oLap.val('thru_date'),
                    'orig': oLap.val('organization'),
                    'level': oLap.val('level'),
                    'distrib': oLap.val('distribution'),
                    'groupFlag': true,
                    'productFlag': false
                },
                success: function (data) {
                    var tmp = {
                        labels: {
                            enabled: true
                        },
                        categories: data.xAxis
                    };
                    if(dateType) {
                        tmp['labels']['formatter'] = OLBIUS.getFormaterAxisLabel(dateType);
                    }
                    if(data.xAxis)
                        tmp['tickInterval'] = OLBIUS.getTickIntervalSize(data.xAxis.length, OLBIUS.getTickInterval());

                    chart.xAxis[0].update(tmp, false);

                    if (removeSeries) {
                        while (chart.series.length > 0) {
                            chart.series[0].remove(false);
                        }
                    }

                    var color = 0;
                    var marker = 0;
                    for (var i in yAxis) {
                        var tmp = [];
                        for(var j in yAxis[i]) { 
                        	if (typeof(data.yAxis) != 'undefined' || typeof(data.yAxis[i]) != 'undefined' || typeof(data.yAxis[i][j]) != 'undefined' || data.yAxis[i][j] != null)                            
                        		tmp.push(Number(yAxis[i][j]));
                        	else
                            tmp.push(Number(yAxis[i][j]) - Number(data.yAxis[i][j]));
                        }
                        chart.addSeries({
                            name: i, data: tmp,
                            color: Highcharts.getOptions().colors[color++],
                            marker: {
                                symbol: Highcharts.getOptions().symbols[marker++]
                            }
                        }, false);
                    }
                    chart.redraw();
                    if(!xAxis || xAxis.length == 0) {
                        flagFunc();
                    }
                }
            });
        };

        var accountingOLap = OLBIUS.oLapChart('evaluateAccOLapII', config, configPopup, 'evaluateAccOlap', true, true, func,  0.45);

        accountingOLap.funcUpdate(function(oLap) {
            var group;
            if(oLap.val('level') == 'false') {
                group = _group;
            } else {
                group = null;
            }
            oLap.update({
                'group': group,
                'product': null,
//                'code': oLap.val('account'),
//                'debitCreditFlag': oLap.val('debitCreditFlag'),
                'code': '511',
                'debitCreditFlag': 'C',
                'currency':  oLap.val('currency'),
                'dateType':  oLap.val('dateType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'orig': oLap.val('organization'),
                'level': oLap.val('level'),
                'distrib': oLap.val('distribution'),
                'groupFlag': true,
                'productFlag': false
            }, oLap.val('dateType'));
        });

        accountingOLap.init(function () {
            accountingOLap.runAjax();
        });

    });

</script>