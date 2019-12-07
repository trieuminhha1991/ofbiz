<div id="alterpopupWindowEdit" class='hide'>
	<div>
		${uiLabelMap.BLEditDeliveryCluster}
	</div>
	<div class="form-window-container">
		<div class='form-window-content'>
            <div class='row-fluid' style="margin-bottom: -10px !important">
                <div class="span6">
                    <div class="row-fluid margin-bottom10">
                        <div class="span5" style="text-align: right">
                            <div class="asterisk"> ${uiLabelMap.BLDeliveryClusterCode} </div>
                        </div>
                        <div class="span7">
                            <input id="wn_deliveryClusterCode"></input>
                        </div>
                    </div>
                    <div class="row-fluid margin-bottom10">
                        <div class="span5" style="text-align: right">
                            <div class="asterisk"> ${uiLabelMap.BLDeliveryClusterName} </div>
                        </div>
                        <div class="span7">
                            <input id="wn_deliveryClusterName"></input>
                        </div>
                    </div>
                </div>
                <div class="span6">
                    <div class='row-fluid'>
                        <div class='span5'>
                            <span class="asterisk" style="text-align: right;">${uiLabelMap.BLShipperNameShort}</span>
                        </div>
                        <div class="span7">
                            <div id="wn_shipper">
                                <div id="wn_jqxGridShipper"></div>
                            </div>
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="span5" style="text-align: right;"><div>${uiLabelMap.Description}</div></div>
                        <div class="span7"><textarea id="wn_description" class='text-popup' style="width: 600px; height: 60px"></textarea></div>
                    </div>
                </div>
            </div>
		</div>
		<div class="form-action">
			<button id="wn_cancelUpdateCluster" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="wn_updateCluster" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<style>
    #alterpopupWindowEdit {
        cursor: default;
    }
</style>

<script type="text/javascript" >
    $(function () {
        OlbUpdateDeliveryClusterObj.init();
    });

    if (typeof (OlbUpdateDeliveryClusterObj) == "undefined") {
        var OlbUpdateDeliveryClusterObj = (function () {
            var validatorVAL;
            var currentGrid = $('#jqxgridDeliveryCluster');
            var shipperDDB;
            var currentRow = -1;
            var currentData = {};
            var isWaitingOpen = false;
            var init = function () {
                initElement();
                initElementComplex();
                initEvent();
                initValidateForm();
            };

            var initElement = function () {
                $("#alterpopupWindowEdit").jqxWindow({
                    width: 680,
                    height: 240,
                    resizable: true,
                    isModal: true,
                    autoOpen: false,
                    cancelButton: $("#wn_cancelUpdateCluster"),
                    modalOpacity: 0.7,
                    theme: theme
                });
                jOlbUtil.input.create("#wn_deliveryClusterCode", {width:'90%',height:28});
                jOlbUtil.input.create("#wn_deliveryClusterName", {width:'90%',height:28});
                jOlbUtil.input.create("#wn_description", {width:'90%',height:40});
            };

            var initElementComplex = function () {
                var configShipper = {
                    useUrl: true,
                    root: 'results',
                    widthButton: '95%',
                    showdefaultloadelement: false,
                    autoshowloadelement: false,
                    datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
                    columns: [
                        {text: uiLabelMap.BLShipperCode, datafield: 'partyCode', width: '30%'},
                        {text: uiLabelMap.FullName, datafield: 'fullName'},
                    ],
                    url: 'JQGetListShipperCluster',
                    useUtilFunc: true,
                    key: 'partyId',
                    keyCode: 'partyCode',
                    description: ['fullName'],
                    autoCloseDropDown: true,
                    filterable: true,
                    sortable: true,
                    dropDownHorizontalAlignment: 'right',
                };
                shipperDDB = new OlbDropDownButton($("#wn_shipper"), $("#wn_jqxGridShipper"), null, configShipper, []);
            };

            var initEvent = function () {
                $("#alterpopupWindowEdit").on("open", function (event) {
                    if(currentRow != -1){
                        currentData = $('#jqxgridDeliveryCluster').jqxGrid('getrowdata', currentRow);
                        initDataUpdate(currentData);
                    }
                });
                $("#alterpopupWindowEdit").on("close", function (event) {
                    clearData();
                });

                $("#wn_updateCluster").on("click", function (event) {
                    if (!validatorVAL.validate()) return false;
                    var row = getData();
                    if(!currentData) return;
                    currentGrid.jqxGrid('updaterow', currentRow, row);
                    $("#alterpopupWindowEdit").jqxWindow('close');
                });

                currentGrid.on('bindingcomplete', function(){
                    if(isWaitingOpen){
                        currentRow = 0;
                        isWaitingOpen = false;
                    }
                });
            };

            var getData = function () {
                var mapResult = {};
                mapResult["deliveryClusterCode"] = $('#wn_deliveryClusterCode').val();
                mapResult["deliveryClusterName"] = $('#wn_deliveryClusterName').val();
                mapResult["description"] = $('#wn_description').val();
                mapResult["executorId"] = shipperDDB.getValue();
                mapResult["deliveryClusterId"] = currentData.deliveryClusterId;
                return mapResult;
            };

            var initDataUpdate = function (row) {
                shipperDDB.selectItem([row.executorId]);
                $("#wn_deliveryClusterCode").jqxInput({ value: row.deliveryClusterCode });
                $("#wn_deliveryClusterName").jqxInput({ value: row.deliveryClusterName });
                $("#wn_description").jqxInput({ value: row.description });
            };

            var clearData = function () {
                var currDate = new Date();
                shipperDDB.clearAll();
                $("#wn_deliveryClusterCode").jqxInput({ value: null });
                $("#wn_deliveryClusterName").jqxInput({ value: null });
                $("#wn_description").val("");
                currentRow = -1;
                currentData = {};
            };

            var updatePopup = function(row){
                currentRow = row;
                $("#alterpopupWindowEdit").jqxWindow('open');
            };

            var initValidateForm = function () {
                var extendRules = [
                ];
                var mapRules = [
                    {input: '#wn_deliveryClusterCode', type: 'validInputNotNull'},
                    {input: '#wn_deliveryClusterName', type: 'validInputNotNull'},
                    {input: '#wn_shipper', type: 'validObjectNotNull', objType: 'dropDownButton'},
                ];
                validatorVAL = new OlbValidator($('#alterpopupWindowEdit'), mapRules, extendRules, {position: 'topcenter'});
            };
            return {
                init: init,
                initValidateForm: initValidateForm,
                updatePopup: updatePopup,
            };
        }());
    }
</script>