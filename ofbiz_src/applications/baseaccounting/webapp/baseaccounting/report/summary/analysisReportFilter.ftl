<div id="jqxPanel" style="position:relative" class="form-horizontal form-window-content-custom">
    <div class="row-fluid">
        <div class="span12">
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
                    <label class="text-info">${uiLabelMap.BACCToDate}</label>
                </div>
                <div class="span6">
                    <div id="toDate"></div>
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
            
            <div class="row-fluid">
                <div class="span5">
                </div>
                <div class="span6 pull-left form-window-content-custom">
                    <button id="alterSave" class='btn btn-primary form-action-button'
                            style="float: left; margin:0px !important"><i
                            class='fa-search'></i> ${uiLabelMap.BACCOK}</button>
                </div>
            </div>
        </div><!--.span12-->
    </div><!--.row-fluid-->
</div>

<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
    if (typeof(filter) == "undefined") {
        var filter = function () {
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
                    url: "JqxGetListGlAccounts",
                    useUtilFunc: true,

                    key: 'glAccountId',
                    description: ['accountName'],
                };

                accutils.initDropDownButton($("#glAccountId"), $("#glAccountGrid"), null, configGlAccount, []);
                accutils.setValueDropDownButtonOnly($("#glAccountId"), '${glAccount.glAccountId}', '${glAccount.accountName}');

                jOlbUtil.dateTimeInput.create("#fromDate", {
                    width: '100%',
                    value: null,
                    formatString: 'dd/MM/yyyy'
                });

                jOlbUtil.dateTimeInput.create("#toDate", {
                    width: '100%',
                    value: null,
                    formatString: 'dd/MM/yyyy'
                });
                $('#fromDate').jqxDateTimeInput('setDate', new Date());
                $('#toDate').jqxDateTimeInput('setDate', new Date());
            };

            var bindEvent = function () {
                $('#fromDate').on('valueChanged', function (event) {
                    var fromDate = event.args.date;
                    var toDate = $("#toDate").jqxDateTimeInput('getDate');
                    if (toDate < fromDate) {
                        $('#toDate').jqxDateTimeInput('setDate', fromDate);
                    }
                });

                $('#toDate').on('valueChanged', function (event) {
                    var toDate = event.args.date;
                    var fromDate = $("#fromDate").jqxDateTimeInput('getDate');
                    if (toDate < fromDate) {
                        $('#fromDate').jqxDateTimeInput('setDate', toDate);
                    }
                });
                $('#alterSave').on('click', function () {
                    var _gridObj = $('#analysisReportGrid');
                    var tmpSource = _gridObj.jqxGrid('source');
                    var fromDate1 = $("#fromDate").jqxDateTimeInput('getDate');
                    var thruDate1 = $("#toDate").jqxDateTimeInput('getDate');

                    var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) + '-' + fromDate1.getFullYear();
                    var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) + '-' + thruDate1.getFullYear();

                    if (typeof(tmpSource) != 'undefined') {
                        tmpSource._source.url = "jqxGeneralServicer?sname=JqxGetAnalysisReport&glAccountId=" + $("#glAccountId").attr('data-value') + '&fromDate=' + fromDate + '&thruDate=' + thruDate;
                        _gridObj.jqxGrid('clearselection');
                        _gridObj.jqxGrid('source', tmpSource);
                    }
                });

                $('#glAccountId').on('open', function (event) {
                    $('#glAccountGrid').jqxGrid('clearselection');
                });
            }

            return {
                initFilter: initFilter,
                bindEvent: bindEvent
            }
        }();
    }
</script>