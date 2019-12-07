<div id="jqxPanel" style="position:relative" class="form-horizontal form-window-content-custom">
    <div class="row-fluid">
        <div class="span6">
            <div class="row-fluid">
                <div class="span5">
                    <label class="text-info">${uiLabelMap.BACCFromDate}</label>
                </div>
                <div class="span6">
                    <div id="fromDate"></div>
                </div>
            </div>
            <div class="row-fluid">
                <div class="span5">
                    <label class="text-info">${uiLabelMap.BACCGlAccountId}</label>
                </div>
                <div class="span6">
                    <div id="glAccountId">
                        <div id="glAccountGrid"></div>
                    </div>
                </div>
            </div>
        </div><!--.span6-->
        <div class="span6">
            <div class="row-fluid">
                <div class="span5">
                    <label class="text-info">${uiLabelMap.BACCToDate}</label>
                </div>
                <div class="span6">
                    <div id="toDate"></div>
                </div>
            </div>
        </div>
    </div><!--.row-fluid-->
    <div class="row-fluid">
        <div class="span12">
            <div class="row-fluid">
                <div class="span5">
                </div>
                <div class="span6 pull-left form-window-content-custom">
                    <button id="alterSave" class='btn btn-primary form-action-button'
                            style="float: left; margin:0px !important"><i
                            class='fa-search'></i> ${uiLabelMap.BACCOK}</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
    if (typeof(cashFilter) == "undefined") {
        var cashFilter = function () {
            var initFilter = function () {
                var configGlAccount = {
                    useUrl: true,
                    root: 'results',
                    widthButton: '80%',
                    dropDownHorizontalAlignment: 'right',
                    showdefaultloadelement: false,
                    autoshowloadelement: false,
                    datafields: [{name: 'glAccountId', type: 'string'}, {
                        name: 'accountName',
                        type: 'string'
                    }],
                    columns: [
                        {
                            text: '${uiLabelMap.BACCGlAccountId}',
                            datafield: 'glAccountId',
                            width: '30%'
                        },
                        {text: '${uiLabelMap.BACCAccountName}', datafield: 'accountName'}
                    ],
                    url: "JqxGetListCashGlAccounts",
                    useUtilFunc: true,

                    key: 'glAccountId',
                    description: ['accountName'],
                };

                accutils.initDropDownButton($("#glAccountId"), $("#glAccountGrid"), null, configGlAccount, []);
                accutils.setValueDropDownButtonOnly($("#glAccountId"), '${cashGlAccount.glAccountId}', '${cashGlAccount.glAccountId}');

                jOlbUtil.dateTimeInput.create("#fromDate", {
                    width: '100%',
                    value: null,
                    formatString : 'dd/MM/yyyy'
                });

                jOlbUtil.dateTimeInput.create("#toDate", {
                    width: '100%',
                    value: null,
                    formatString : 'dd/MM/yyyy'
                });
                $('#fromDate').jqxDateTimeInput('setDate', new Date());
                $('#toDate').jqxDateTimeInput('setDate', new Date());
            };



            var bindEvent = function () {
                $('#alterSave').on('click', function () {
                    var _gridObj = $('#cashDetailGrid');
                    var tmpSource = _gridObj.jqxGrid('source');

                    var fromDate1 = $("#fromDate").jqxDateTimeInput('getDate');
                    var thruDate1 = $("#toDate").jqxDateTimeInput('getDate');

                    var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) + '-' + fromDate1.getFullYear();
                    var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) + '-' + thruDate1.getFullYear();

                    if (typeof(tmpSource) != 'undefined') {
                        tmpSource._source.url = "jqxGeneralServicer?sname=JqxGetCashDetailBook&glAccountId=" + $("#glAccountId").attr('data-value') + '&fromDate=' + fromDate + '&thruDate=' + thruDate;
                        _gridObj.jqxGrid('clearselection');
                        _gridObj.jqxGrid('source', tmpSource);
                    }
                });

                $('#fromDate').on('valueChanged', function (event)
                {
                    var fromDate = event.args.date;
                    var toDate = $("#toDate").jqxDateTimeInput('getDate');
                    if(toDate < fromDate) {
                        $('#toDate').jqxDateTimeInput('setDate', fromDate);
                    }
                });

                $('#toDate').on('valueChanged', function (event)
                {
                    var toDate = event.args.date;
                    var fromDate = $("#fromDate").jqxDateTimeInput('getDate');
                    if(toDate < fromDate) {
                        $('#fromDate').jqxDateTimeInput('setDate', toDate);
                    }
                });

            };

            return {
                initFilter: initFilter,
                bindEvent: bindEvent
            }
        }();
    }
</script>