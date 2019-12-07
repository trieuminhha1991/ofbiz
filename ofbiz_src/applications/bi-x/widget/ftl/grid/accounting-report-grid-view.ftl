<script type="text/javascript" id="test">
    $(function(){
        var config = {
            title: 'TEST',
            columns: [
                { text: 'date', datafield: 'date', type: 'olapdate', width: 150},
                { text: 'party', datafield: 'party', type: 'string', width: 150},
                { text: 'accountCode', datafield: 'accountCode', type: 'string', width: 150},
                { text: 'currency', datafield: 'currency', type: 'string', width: 150},
                { text: 'debitCredit', datafield: 'debitCredit', type: 'string', width: 150},
                { text: 'product', datafield: 'product', type: 'string', width: 150},
                { text: 'category', datafield: 'category', type: 'string', width: 150},
                { text: 'amount', datafield: 'amount', type: 'number', width: 150}
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
            }
        ];

        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateAccOlapGrid', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'orig': oLap.val('organization'),
                'dateType': oLap.val('dateType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date')
            }, oLap.val('dateType'));
        });

        testGrid.init(function () {
            testGrid.runAjax();
        });
    });
</script>