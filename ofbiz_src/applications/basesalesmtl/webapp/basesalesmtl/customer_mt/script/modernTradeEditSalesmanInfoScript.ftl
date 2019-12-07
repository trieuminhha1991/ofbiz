<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.StartDateMustBeAfterEndDate = '${StringUtil.wrapString(uiLabelMap.StartDateMustBeAfterEndDate)}';
	uiLabelMap.CommonCancel = "${uiLabelMap.CommonCancel}";
	uiLabelMap.OK = "${uiLabelMap.OK}";
	uiLabelMap.BSCustomerId = "${uiLabelMap.BSCustomerId}";
	uiLabelMap.FullName = "${uiLabelMap.FullName}";
	uiLabelMap.BSSupervisorId = "${uiLabelMap.BSSupervisorId}";
    uiLabelMap.BSSupervisor = "${uiLabelMap.BSSupervisor}";
    uiLabelMap.BSSalesmanCode = "${uiLabelMap.BSSalesmanCode}";
</script>

<script type="text/javascript" >
    $(function(){
        OlbMTEditSalesmanInfoObj.init();
    });
    var OlbMTEditSalesmanInfoObj = (function() {
        var validatorVAL;
        var salesmanDDB;
        var salessupDDB;

        var init = function() {
            initInputs();
            initElementComplex();
            initEvents();
            initValidateForm();
        };
        var initInputs = function() {
        };
        var initElementComplex = function() {
            var configSalesman = {
                useUrl: true,
                root: 'results',
                widthButton: '100%',
                showdefaultloadelement: false,
                autoshowloadelement: false,
                datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
                columns: [
                    {text: uiLabelMap.BSSalesmanCode, datafield: 'partyCode', width: '30%'},
                    {text: uiLabelMap.FullName, datafield: 'fullName'},
                ],
                url: '',
                useUtilFunc: true,
                key: 'partyId',
                keyCode: 'partyCode',
                description: ['fullName'],
                autoCloseDropDown: true,
                filterable: true,
                sortable: true,
                dropDownHorizontalAlignment: 'right',
            };
            salesmanDDB = new OlbDropDownButton($("#salesman"), $("#jqxGridSalesman"), null, configSalesman, []);

            var configSalessup = {
                useUrl: true,
                root: 'results',
                widthButton: '100%',
                showdefaultloadelement: false,
                autoshowloadelement: false,
                datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
                columns: [
                    {text: uiLabelMap.BSSupervisorId, datafield: 'partyCode', width: '30%'},
                    {text: uiLabelMap.FullName, datafield: 'fullName'},
                ],
                url: 'JQGetListMTSupervisor',
                useUtilFunc: true,
                key: 'partyId',
                keyCode: 'partyCode',
                description: ['fullName'],
                autoCloseDropDown: true,
                filterable: true,
                sortable: true,
                dropDownHorizontalAlignment: 'right',
            };
            salessupDDB = new OlbDropDownButton($("#salessup"), $("#jqxGridSalessup"), null, configSalessup, []);
        };
        var initEvents = function() {
            salessupDDB.getGrid().rowSelectListener(function(rowData){
                var supervisorId = rowData['partyId'];
                salesmanDDB.updateSource("jqxGeneralServicer?sname=JQGetListMTSalesmanBySupervisor&supervisorId="+supervisorId, null, function(){
                    salesmanDDB.selectItem(null, 0);
                });
            });
        };

        var initValidateForm = function(){
            var extendRules = [

            ];
            var mapRules = [
                {input: '#salesman', type: 'validObjectNotNull', objType: 'dropDownButton'},
                {input: '#salessup', type: 'validObjectNotNull', objType: 'dropDownButton'},
            ];
            validatorVAL = new OlbValidator($('#initChangeSalesman'), mapRules, extendRules, {position: 'topcenter'});
        };

        var getValidator = function(){
            return validatorVAL;
        };
        var getSalesmanDDB = function(){
            return salesmanDDB;
        };
        var getSalessupDDB = function(){
            return salessupDDB;
        };
        return {
            init: init,
            getValidator: getValidator,
            getSalesmanDDB: getSalesmanDDB,
            getSalessupDDB: getSalessupDDB,
        }
    }());
</script>