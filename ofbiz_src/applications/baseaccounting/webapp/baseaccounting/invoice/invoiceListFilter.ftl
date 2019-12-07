<div id="jqxPanel" style="position:relative" class="form-horizontal form-window-content-custom">
    <div class="row-fluid">
        <div class="row-fluid">
            <div class="span5">
                <label class="text-info">${uiLabelMap.BACCFromDate}</label>
            </div>
            <div class="span6">
                <div id="fromDueDate"></div>
            </div>
        </div>
        <div class="row-fluid">
            <div class="span5">
                <label class="text-info">${uiLabelMap.BACCToDate}</label>
            </div>
            <div class="span6">
                <div id="toDueDate"></div>
            </div>
        </div>
        <div class='row-fluid'>
            <div class='span5'>
                <label class='text-info'>${uiLabelMap.BACCInvoiceHaveNoDueDate}</label>
            </div>
            <div class="span7">
                <label class="pull-left" style="padding : 5px 0 0"><input name="switch-field-1" class="ace-switch ace-switch-6" type="checkbox" checked/><span class="lbl"></span></label>
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
</div>

<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
    if (typeof(filter) == "undefined") {
        var filter = function () {
            var initFilter = function () {
                jOlbUtil.dateTimeInput.create("#fromDueDate", {
                    width: '100%',
                    value: null,
                    formatString: 'dd/MM/yyyy'
                });

                jOlbUtil.dateTimeInput.create("#toDueDate", {
                    width: '100%',
                    value: null,
                    formatString: 'dd/MM/yyyy'
                });
                var date = new Date();
                var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
                var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
                $('#fromDueDate').jqxDateTimeInput('setDate', firstDay);
                $('#toDueDate').jqxDateTimeInput('setDate', lastDay);
            };

            var bindEvent = function () {
                $('#fromDueDate').on('valueChanged', function (event) {
                    var fromDueDate = event.args.date;
                    var toDueDate = $("#toDueDate").jqxDateTimeInput('getDate');
                    if (toDueDate < fromDueDate) {
                        $('#toDueDate').jqxDateTimeInput('setDate', fromDueDate);
                    }
                });

                $('#toDueDate').on('valueChanged', function (event) {
                    var toDueDate = event.args.date;
                    var fromDueDate = $("#fromDueDate").jqxDateTimeInput('getDate');
                    if (toDueDate < fromDueDate) {
                        $('#fromDueDate').jqxDateTimeInput('setDate', toDueDate);
                    }
                });

                $('#alterSave').on('click', function () {

                    var _gridObj = $('#jqxgridInvoice');
                    var tmpSource = _gridObj.jqxGrid('source');
                    var fromDueDate = $("#fromDueDate").jqxDateTimeInput('getDate').getTime();
                    var toDueDate = $("#toDueDate").jqxDateTimeInput('getDate').getTime();
                    var isNullDueDateBoolean = $('input[name=switch-field-1]').is(":checked");
                    var isNullDueDate = 'N';
                    if(isNullDueDateBoolean) isNullDueDate = 'Y';
                    console.log('isNullDueDate', isNullDueDate);
                    var url = "jqxGeneralServicer?sname=jqxGetListInvoiceByDueDate&fromDueDate=" + fromDueDate + "&toDueDate=" + toDueDate + "&invoiceType=${businessType}&isNullDueDate=" + isNullDueDate;
                    if (typeof(tmpSource) != 'undefined') {
                        tmpSource._source.url = url;
                        _gridObj.jqxGrid('clearselection');
                        _gridObj.jqxGrid('source', tmpSource);
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