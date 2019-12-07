<div id="jqxPanel" style="position:relative" class="form-horizontal form-window-content-custom">
    <div class="row-fluid">
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
            <div class='span5'>
                <label class="text-info">${uiLabelMap.BACCOrganization}</label>
            </div>
            <div class="span6">
                <div id="enumPartyTypeId"></div>
            </div>
        </div>

        <div class='row-fluid'>
            <div class='span5'>
                <label class=''></label>
            </div>
            <div class="span6">
                <div id="partyId" style="display: inline-block; float: left;">
                    <div id="partyGrid"></div>
                </div>
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
    <#assign enumPartyTypeList = delegator.findByAnd("Enumeration", {"enumTypeId" : "PARTY_TYPE"}, null, false) />
    var enumPartyTypeArr = [
    <#if enumPartyTypeList?exists>
        <#list enumPartyTypeList as enumPartyType>
            {
                enumId: "${enumPartyType.enumId}",
                description: '${StringUtil.wrapString(enumPartyType.description)}'
            },
        </#list>
    </#if>];
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
                    url: "JqxGetListGlAccountsLiability",
                    useUtilFunc: true,

                    key: 'glAccountId',
                    description: ['accountName'],
                };

                accutils.initDropDownButton($("#glAccountId"), $("#glAccountGrid"), null, configGlAccount, []);
                accutils.setValueDropDownButtonOnly($("#glAccountId"), '${liabilityGlAccount.glAccountId}', '${liabilityGlAccount.accountName}');

                var configParty = {
                    useUrl: true,
                    root: 'results',
                    widthButton: '80%',
                    showdefaultloadelement: false,
                    autoshowloadelement: false,
                    datafields: [{name: 'partyId', type: 'string'}, {
                        name: 'fullName',
                        type: 'string'
                    }],
                    columns: [
                        {text: '${uiLabelMap.BACCPartyId}', datafield: 'partyId', width: '30%'},
                        {text: '${uiLabelMap.BACCFullName}', datafield: 'fullName'}
                    ],
                    url: "JqxGetParties&enumId=SUPPLIER_PTY_TYPE",
                    useUtilFunc: true,

                    key: 'partyId',
                    description: ['fullName'],
                };

                accutils.initDropDownButton($("#partyId"), $("#partyGrid"), null, configParty, []);

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

                accutils.createJqxDropDownList($("#enumPartyTypeId"), enumPartyTypeArr, {
                    valueMember: 'enumId',
                    displayMember: 'description',
                    width: '80%',
                    height: 25
                });
                $("#enumPartyTypeId").val("SUPPLIER_PTY_TYPE");
            }

            var bindEvent = function () {
                $("#enumPartyTypeId").on('select', function (event) {
                    var args = event.args;
                    if (args) {
                        var item = args.item;
                        var value = item.value;
                        var grid = $("#partyGrid");
                        $("#partyId").val("");
                        var source = grid.jqxGrid('source');
                        source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
                        grid.jqxGrid('source', source);
                    }
                });

                $('#glAccountId').on('open', function (event) {
                    $('#glAccountGrid').jqxGrid('clearselection');
                });

                $('#partyId').on('open', function (event) {
                    $('#partyGrid').jqxGrid('clearselection');
                });

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
                    var _gridObj = $('#liabilityBalGrid');
                    var tmpSource = _gridObj.jqxGrid('source');

                    var fromDate1 = $("#fromDate").jqxDateTimeInput('getDate');
                    var thruDate1 = $("#toDate").jqxDateTimeInput('getDate');

                    var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) + '-' + fromDate1.getFullYear();
                    var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) + '-' + thruDate1.getFullYear();

                    var url = "jqxGeneralServicer?sname=JqxGetLiabilityBalance&glAccountId=" + $("#glAccountId").attr('data-value') + '&fromDate=' + fromDate + '&thruDate=' + thruDate;
                    var partyId = $('#partyId').attr('data-value');
                    if(typeof(partyId) != 'undefined') {
                        url = url + '&partyId=' + partyId;
                    }

                    if (typeof(tmpSource) != 'undefined') {
                        tmpSource._source.url = url;
                        _gridObj.jqxGrid('clearselection');
                        _gridObj.jqxGrid('source', tmpSource);
                    }
                });
            }

            return {
                initFilter: initFilter,
                bindEvent: bindEvent
            }
        }();
    }
</script>