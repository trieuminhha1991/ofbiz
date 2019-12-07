
<script type="text/javascript" id="evaluateAccOLapIII">

    $(function () {

        var config = {
        	service: 'acctgTransTotal',
            chart: {
                type: 'area'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.accParticipatingChart)}',
                x: -20 //center
            },
            credits: {
                enabled: false
            },            
            xAxis: {
                tickmarkPlacement: 'on',
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
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b><br/>',
                shared: true
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
        };

        var _dateAccountSource = [{text: '${StringUtil.wrapString(uiLabelMap.BACCSaleIncome)}', value: '511'}, {text: '${StringUtil.wrapString(uiLabelMap.BACCDeductions)}', value: '521'},
            {text:'${StringUtil.wrapString(uiLabelMap.BACCCOGS)}', value: '632'}, {text: '${StringUtil.wrapString(uiLabelMap.BACCSellExp)}', value: '641'},
            {text: '${StringUtil.wrapString(uiLabelMap.BACCGenAdmExp)}', value: '642'}];
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
            'filter': {
                action : 'addDropDownList',
                params : [{
                    id : 'filter',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_filter)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': 'null'}, {'text': '${StringUtil.wrapString(uiLabelMap.olap_product)}', 'value': 'product'},
                        {'text': '${StringUtil.wrapString(uiLabelMap.olap_category)}', 'value': 'category'}],
                    index: 0
                }],
                event: function(popup) {
                    popup.onEvent('filter', 'select', function(event){
                        var args = event.args;
                        var item = popup.item('filter', args.index);
                        var filter = item.value;
                        if(filter == 'null') {
                            popup.hide('productId');
                            popup.hide('category');
                        }
                        if(filter == 'product') {
                            popup.show('productId');
                            popup.clear('productId');
                            popup.hide('category');
                        }
                        if(filter == 'category') {
                            popup.hide('productId');
                            popup.show('category');
                            popup.clear('category');
                        }
                        popup.resize();
                    });
                }
            },
            'productId' : {
                action : 'addDropDownList',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_product)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}].concat(OLBIUS.getProduct()),
                    index: 0,
                    hide: true
                }]
            },
            'category' : {
                action : 'addDropDownList',
                params : [{
                    id : 'category',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_category)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}].concat(OLBIUS.getCategory()),
                    index: 0,
                    hide: true
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
            'level' : {
                action : 'addDropDownList',
                params : [{
                    id : 'level',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_level)}',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_true)}', value: 'true'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_false)}', value: 'false'}],
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
            'account' : {
                action : 'addDropDownList',
                params : [{
                    id : 'account',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_account)}',
                    data : _dateAccountSource,
                    index: 0
                }]
            },
            'debitCreditFlag' : {
                action : 'addDropDownList',
                params : [{
                    id : 'debitCreditFlag',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_flag)}',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_credit)}', value: 'C'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_debit)}', value: 'D'}],
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

//        var _group = OLBIUS.getChildGroups('CSM_GT');

        var accountingOLap = OLBIUS.oLapChart('evaluateAccOLapIII', config, configPopup, 'evaluateAccOlap', true, true, OLBIUS.defaultLineFunc, 0.5);

        accountingOLap.funcUpdate(function(oLap) {
            var product = oLap.val('productId');
//            var group;
//            if(oLap.val('level') == 'false') {
//                group = _group;
//            } else {
//                group = null;
//            }
            oLap.update({
                'group': oLap.val('group'),
                'product': product,
                'code': oLap.val('account'),
                'debitCreditFlag': oLap.val('debitCreditFlag'),
                'currency':  oLap.val('currency'),
                'dateType':  oLap.val('dateType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'orig': oLap.val('organization'),
                'level': oLap.val('level'),
                'distrib': oLap.val('distribution'),
                'groupFlag': true,
                'productFlag': (product!=null && product!='')
            }, oLap.val('dateType'));
        });

        accountingOLap.init(function () {
            accountingOLap.runAjax();
        });
    });

</script>