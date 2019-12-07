
<script type="text/javascript" id="evaluateAccOLapVI">

    $(function () {

        var config = {
        	service: 'acctgTransTotal',
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.accTopProductChart)}',
                x: -20 //center
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: -45,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
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
                min: 0,
            },
            legend: {
                enabled: false
            },
            tooltip: {
                pointFormat: '{point.y}'
            }
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
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                event: function(popup) {
                    popup.onEvent('thru_date', 'valueChanged', function(event){
                        var thruDate = event.args.date;
                        var fromDate = popup.getDate('from_date');
                        if(thruDate < fromDate) {
                            popup.val('from_date', thruDate);
                        }
                    });
                }
            }
        };

        var accountingOLap = OLBIUS.oLapChart('evaluateAccOLapVI', config, configPopup, 'evaluateAccOlap', true, true, OLBIUS.defaultColumnFunc, 0.5);

        accountingOLap.funcUpdate(function(oLap) {
            oLap.update({
                'group': oLap.val('group'),
                'product': null,
                'code': oLap.val('account'),
                'debitCreditFlag': oLap.val('debitCreditFlag'),
                'currency':  oLap.val('currency'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'orig': oLap.val('organization'),
                'distrib' : true,
                'groupFlag': false,
                'dateFlag': false,
                'productFlag': true,
                //'categoryFlag': true,
                'limit': oLap.val('limit'),
                'sort': oLap.val('sort')
            });
        });

        accountingOLap.init(function () {
            accountingOLap.runAjax();
        });

    });

</script>