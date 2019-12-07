<div id="alterpopupWindowAdd" class='hide'>
	<div>
		${uiLabelMap.BSCreateRoute}
	</div>
	<div class="form-window-container">
		<div class='form-window-content'>
            <div class='row-fluid'>
                <div class="span6">
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class="asterisk">${uiLabelMap.BSRouteId}</label>
                        </div>
                        <div class="span7">
                            <input type="text" id="RouteId" class='no-space'/>
                        </div>
                    </div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class="asterisk">${uiLabelMap.BSRouteName}</label>
                        </div>
                        <div class="span7">
                            <input type="text" id="RouteName"/>
                        </div>
                    </div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class="asterisk">${uiLabelMap.BSChooseSalesman}</label>
                        </div>
                        <div class="span7">
                            <div id="dropdownSalesman">
                                <div id="salesmanGrid"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="span6">
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class="asterisk">${uiLabelMap.BSScheduleDescription}</label>
                        </div>
                        <div class="span7">
                            <div id="ScheduleTime"></div>
                        </div>
                    </div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class="">${uiLabelMap.BSDescription}</label>
                        </div>
                        <div class="span7">
                            <textarea id="Description" row="3" class='no-resize no-top-bottom-margin' style='width: calc(90% - 12px)'></textarea>
                        </div>
                    </div>
                </div>
            <div>
            <div class='row-fluid'>
                <div class="span12">
                    <div class='row-fluid'>
                        <i class="fa fa-calendar"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSSettingUpWeeksOfRoute)}
                    </div>
                </div>
            </div>
            <div class='row-fluid' style="margin-bottom: 10px">
                <div class="span12">
                    <div class='row-fluid'>
                        <button id="btn1Week" style="margin: 1px; cursor: default;" class="btn btn-mini btn-default">${StringUtil.wrapString(uiLabelMap.BSEveryWeek)}</button>
                        <button id="btn2Week" style="margin: 1px; cursor: default;" class="btn btn-mini btn-default">${StringUtil.wrapString(uiLabelMap.BSTwoWeek)}</button>
                        <button id="btn4Week" style="margin: 1px; cursor: default;" class="btn btn-mini btn-default">${StringUtil.wrapString(uiLabelMap.BSFourWeek)}</button>
                        <button id="btnOptionalWeek" style="margin: 1px; cursor: default;" class="btn btn-mini btn-default">${StringUtil.wrapString(uiLabelMap.BSOptionalWeek)}</button>
                        <button id="btnClearWeek" style="margin: 1px; cursor: default;" class="btn btn-mini btn-danger">${StringUtil.wrapString(uiLabelMap.BSClearSelectWeek)}</button>
                    </div>
                </div>
            </div>
            <div class='row-fluid'>
                <div class="span12">
                    <div class='row-fluid' id="weeksContainer">
                    </div>
                </div>
            </div>
            <div class='row-fluid'>
                <div class="span12">
                    <div class='row-fluid'>
                        <label class="">${uiLabelMap.BSCurrentWeekIs}: <span id="currentWeek"></span></label>
                    </div>
                </div>
            </div>
		</div>
		<div class="form-action">
			<button id="cancelCreateRoute" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="createRoute" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
			<#--<button id="createRouteAndUpdateAddress" class='btn btn-info form-action-button pull-right'><i class='fa-map-marker'></i>${uiLabelMap.SaveAndUpdateAddress}</button>-->
		</div>
	</div>
</div>

<script type="text/javascript" >
    $("body").css("cursor", "default");
    $(function () {
        // ensure that AddRouteObj init one time.
        if (flagPopupLoad) {
            AddRouteObj.init();
            flagPopupLoad = false;
            setTimeout(function(){ flagPopupLoad = true }, 300);
        }
    });

    if (typeof (AddRouteObj) == "undefined") {
        var AddRouteObj = (function () {
            var validatorVAL;
            var currentGrid = $('#ListRoute');
            var scheduleTimeCBB;
            var salesmanDDB;
            var flagUpdate = false;
            var currentRow = -1;
            var currentData = {};
            var isWaitingOpen = false;
            var selectedWeeks = [];
            var originalSelectedWeeks = [];
            var flag1Weeks = false;
            var flag2Weeks = false;
            var flag4Weeks = false;
            var flagOptionalWeeks = true;
            var init = function () {
                initElement();
                initElementComplex();
                initEvent();
                initValidateForm();
            };

            var initElement = function () {
                $("#alterpopupWindowAdd").jqxWindow({
                    width: 680,
                    height: 500,
                    resizable: true,
                    isModal: true,
                    autoOpen: false,
                    cancelButton: $("#cancelCreateRoute"),
                    modalOpacity: 0.7,
                    theme: theme
                });

                var width = '90%';
                var height = '25px';
                $('#RouteId').jqxInput({
                    width: 'calc(' + width + ' - 5px)',
                    height: 20,
                    theme: 'olbius'
                });
                $('#RouteName').jqxInput({
                    width: 'calc(' + width + ' - 5px)',
                    height: 20,
                    theme: 'olbius'
                });
            };

            var initElementComplex = function () {
                var width = '90%';
                var height = '25px';
                var configScheduleTime = {
                    placeHolder: "${StringUtil.wrapString(uiLabelMap.DAClickToChoose)}",
                    key: "value",
                    value: "description",
                    width: width,
                    dropDownHeight: 260,
                    dropDownWidth: 'auto',
                    autoDropDownHeight: false,
                    displayDetail: true,
                    dropDownHorizontalAlignment: "left",
                    autoComplete: true,
                    multiSelect: true,
                    searchMode: "containsignorecase",
                    renderer: null,
                    renderSelectedItem: null
                };
                scheduleTimeCBB = new OlbComboBox($("#ScheduleTime"), days, configScheduleTime, []);


                var configPartySalesman = {
                    useUrl: true,
                    root: 'results',
                    widthButton: '90%',
                    datafields: [
                        {name: "partyId", type: "string"},
                        {name: "partyCode", type: "string"},
                        {name: "fullName", type: "string"},
                    ],
                    columns: [
                        {text: "${uiLabelMap.BSSalesmanCode}", datafield: "partyCode", width: "30%"},
                        {text: "${uiLabelMap.BSSalesman}", datafield: "fullName"},
                    ],
                    url: 'JQGetListSalesmanManagement',
                    useUtilFunc: true,
                    key: 'partyId',
                    keyCode: 'partyCode',
                    description: ['fullName'],
                    autoCloseDropDown: true,
                    filterable: false,
                    sortable: false,
                    autorowheight: false,
                    showfilterrow: true,
                    selectionmode: 'singlerow',
                };
                salesmanDDB = new OlbDropDownButton($("#dropdownSalesman"), $("#salesmanGrid"), null, configPartySalesman, []);
                initWeeks();
            };

            var initWeeks = function() {
                var container = $("#weeksContainer");
                var colorClass = 'btn-default';
                var text = '';
                var title = '';
                for (var i = 1; i < 55; i++) {
                    if (i == getWeekNumber()) {
                        colorClass = 'btn-info';
                        title = 'this week'
                    } else {
                        colorClass = 'btn-default';
                        title = 'week ' + i;
                    }
                    if (i < 10) {
                        text = '0' + i;
                    } else {
                        text = i;
                    }
                    container.append("<button id='week"+ text +"' title='"+title+"' style='margin: 1px' class='btn btn-mini " + colorClass + "'>"+ text +"</button>");
                }
                $("#currentWeek").text(getWeekNumber());
            };

            function getWeekNumber() {
                var d = new Date();
                d = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
                d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay()||7));
                var yearStart = new Date(Date.UTC(d.getUTCFullYear(),0,1));
                var weekNo = Math.ceil(( ( (d - yearStart) / 86400000) + 1)/7);
                return weekNo;
            }

            var initEvent = function () {
                $("#alterpopupWindowAdd").on("open", function (event) {
                    if(currentRow != -1){
                        currentData = $('#ListRoute').jqxGrid('getrowdata', currentRow);
                        initDataUpdate(currentData);
                    }
                });
                $("#alterpopupWindowAdd").on("close", function (event) {
                    clearData();
                });

                $("#createRoute").on("click", function (event) {
                    if (!validatorVAL.validate()) return false;
                    var row = getData();

                    if(!flagUpdate){
                        currentGrid.jqxGrid('addRow', null, row, "first");
                    }else if(currentRow != -1){
                        if(!currentData) return;
                        currentGrid.jqxGrid('updaterow', currentRow, row);
                    }
                    $("#alterpopupWindowAdd").jqxWindow('close');
                });

                $("#createRouteAndUpdateAddress").click(function(){
                    if (!validatorVAL.validate()) return false;
                    $("#createRoute").click();
                    if(currentRow != -1){
                        RouteAddress.open(currentRow);
                    }else{
                        isWaitingOpen = true;
                    }

                });

                currentGrid.on('bindingcomplete', function(){
                    if(isWaitingOpen){
                        currentRow = 0;
                        isWaitingOpen = false;
                        RouteAddress.open(self.currentRow);
                    }
                });
                initWeeksEvent();
            };


            var initWeeksEvent = function() {
                var btnWeek;
                var text;
                for (var i = 1; i < 55; i++) {
                    text = getWeekStr(i);
                    btnWeek = $("#week" + text);
                    btnWeek.on('click', function (e) {
                        pushWeekData(e.currentTarget.innerText);
                    });
                }

                $("#btn1Week").on('click', function () {
                    $("#btn1Week").removeClass( "btn-default" ).addClass( "btn-primary" );
                    $("#btn2Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btn4Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btnOptionalWeek").removeClass( "btn-primary" ).addClass( "btn-default" );
                    flag1Weeks = true;
                    flag2Weeks = false;
                    flag4Weeks = false;
                    flagOptionalWeeks = false;
                    selectedWeeks = [];
                    for (var i = 1; i < 55; i++) {
                        pushWeekData(getWeekStr(i));
                    }
                });

                $("#btn2Week").on('click', function () {
                    $("#btn1Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btn2Week").removeClass( "btn-default" ).addClass( "btn-primary" );
                    $("#btn4Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btnOptionalWeek").removeClass( "btn-primary" ).addClass( "btn-default" );
                    flag1Weeks = false;
                    flag2Weeks = true;
                    flag4Weeks = false;
                    flagOptionalWeeks = false;
                    selectedWeeks = [];
                    var isEvenWeek = false;
                    var currWeek = getWeekNumber();
                    if (currWeek % 2 == 0) {
                        isEvenWeek = true;
                    }
                    clearAllWeekView();
                    for (var i = 1; i < 55; i++) {
                        if (isEvenWeek) {
                            if (i%2 == 0) {
                                updateWeekView(getWeekStr(i), true);
                                selectedWeeks.push(getWeekStr(i));
                            } else {
                                updateWeekView(getWeekStr(i), false);
                            }
                        } else {
                            if (i%2 == 0) {
                                updateWeekView(getWeekStr(i), false);
                            } else {
                                updateWeekView(getWeekStr(i), true);
                                selectedWeeks.push(getWeekStr(i));
                            }
                        }
                    }

                });

                $("#btn4Week").on('click', function () {
                    $("#btn1Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btn2Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btn4Week").removeClass( "btn-default" ).addClass( "btn-primary" );
                    $("#btnOptionalWeek").removeClass( "btn-primary" ).addClass( "btn-default" );
                    flag1Weeks = false;
                    flag2Weeks = false;
                    flag4Weeks = true;
                    flagOptionalWeeks = false;
                    selectedWeeks = [];
                    var currWeek = getWeekNumber();
                    clearAllWeekView();
                    for (var i = 1; i <= currWeek; i++) {
                        if ((currWeek - i) % 4 == 0) {
                            updateWeekView(getWeekStr(i), true);
                            selectedWeeks.push(getWeekStr(i));
                        } else {
                            updateWeekView(getWeekStr(i), false);
                        }
                    }

                    for (var i = currWeek; i < 55; i++) {
                        if ((i - currWeek) % 4 == 0) {
                            updateWeekView(getWeekStr(i), true);
                            selectedWeeks.push(getWeekStr(i));
                        } else {
                            updateWeekView(getWeekStr(i), false);
                        }
                    }

                });

                $("#btnOptionalWeek").on('click', function () {
                    $("#btn1Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btn2Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btn4Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btnOptionalWeek").removeClass( "btn-default" ).addClass( "btn-primary" );
                    flag1Weeks = false;
                    flag2Weeks = false;
                    flag4Weeks = false;
                    flagOptionalWeeks = true;
                });

                $("#btnClearWeek").on('click', function () {
                    $("#btn1Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btn2Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btn4Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                    $("#btnOptionalWeek").removeClass( "btn-primary" ).addClass( "btn-default" );
                    flag1Weeks = false;
                    flag2Weeks = false;
                    flag4Weeks = false;
                    flagOptionalWeeks = true;
                    if (OlbCore.isNotEmpty(originalSelectedWeeks)) {
                        selectedWeeks = cloneArray(originalSelectedWeeks);
                        updateAllWeekView();
                    }else {
                        clearAllWeekView();
                    }
                });
            };

            var getWeekStr = function(weekNum){
                if (weekNum < 10) {
                    return '0' + weekNum;
                } else {
                    return '' + weekNum;
                }
            };

            var pushWeekData = function(week) {
                if (flag1Weeks || flagOptionalWeeks) {
                    if ($.inArray( week, selectedWeeks ) > -1) {
                        selectedWeeks.splice($.inArray(week, selectedWeeks),1);
                        updateWeekView(week, false);
                    } else {
                        selectedWeeks.push(week);
                        updateWeekView(week, true);
                    }
                }

                if (flag2Weeks) {
                    selectedWeeks = [];
                    for (var i = 1; i <= week; i++) {
                        if ((week - i) % 2 == 0) {
                            updateWeekView(getWeekStr(i), true);
                            selectedWeeks.push(getWeekStr(i));
                        } else {
                            updateWeekView(getWeekStr(i), false);
                        }
                    }

                    for (var i = week; i < 55; i++) {
                        if ((i - week) % 2 == 0) {
                            updateWeekView(getWeekStr(i), true);
                            selectedWeeks.push(getWeekStr(i));
                        } else {
                            updateWeekView(getWeekStr(i), false);
                        }
                    }
                }

                if (flag4Weeks) {
                    selectedWeeks = [];
                    for (var i = 1; i <= week; i++) {
                        if ((week - i) % 4 == 0) {
                            updateWeekView(getWeekStr(i), true);
                            selectedWeeks.push(getWeekStr(i));
                        } else {
                            updateWeekView(getWeekStr(i), false);
                        }
                    }

                    for (var i = week; i < 55; i++) {
                        if ((i - week) % 4 == 0) {
                            updateWeekView(getWeekStr(i), true);
                            selectedWeeks.push(getWeekStr(i));
                        } else {
                            updateWeekView(getWeekStr(i), false);
                        }
                    }
                }
            };

            var updateWeekView = function(week, isSelected) {
                var weekEl = $("#week" + week);
                if (isSelected) {
                    weekEl.removeClass( "btn-default btn-info" ).addClass( "btn-success" );
                } else {
                    weekEl.removeClass( "btn-info btn-success" ).addClass( "btn-default" );
                }
            };

            var updateAllWeekView = function () {
                for (var i = 1; i < 55; i++) {
                    updateWeekView(getWeekStr(i), false);
                }
                $.each(selectedWeeks, function (i, v) {
                    updateWeekView(v, true);
                });
            };

            var clearAllWeekView = function () {
                selectedWeeks = [];
                for (var i = 1; i < 55; i++) {
                    updateWeekView(getWeekStr(i), false);
                }
            };

            var cloneArray = function(orig) {
                var dest = [];
                if (OlbCore.isNotEmpty(orig)) {
                    $.each(orig, function (i, v){
                        dest.push(v);
                    });
                }
                return dest;
            };

            var getData = function () {
                var mapResult = {};
                mapResult["routeCode"] = $('#RouteId').val();
                mapResult["routeName"] = $('#RouteName').val();
                mapResult["scheduleRoute"] = JSON.stringify(getTimes());
                mapResult["description"] = $('#Description').val();
                mapResult["salesmanId"] = $('#dropdownSalesman').data('value');
                if (flagUpdate) {
                    mapResult["routeId"] = currentData.routeId;
                }
                mapResult["weeks"] = JSON.stringify(selectedWeeks);
                return mapResult;
            };

            var getTimes = function(){
                var items = $("#ScheduleTime").jqxComboBox('getSelectedItems');
                var arr = [];
                for(var x in items){
                    arr.push(items[x].value);
                }
                return arr;
            };

            var initDataUpdate = function (row) {
                salesmanDDB.selectItem([row.salesmanId]);
                var selectDate = [];
                //parse scheduleRoute
                var sR = [];
                if (OlbCore.isNotEmpty(row.scheduleRoute)) {
                    sR = row.scheduleRoute.match(/\w+/gm);
                    $.each(sR, function (i, v) {
                        selectDate.push(v);
                    });
                }
                scheduleTimeCBB.selectItem(selectDate,null);
                $("#RouteId").jqxInput({ value: row.routeCode });
                $("#RouteName").jqxInput({ value: row.routeName });
                $("#Description").val(row.description);
                if (OlbCore.isNotEmpty(row.weeks)) {
                    selectedWeeks = [];
                    selectedWeeks = row.weeks.match(/\w+/gm);
                    if (OlbCore.isNotEmpty(selectedWeeks)){
                        updateAllWeekView();
                        originalSelectedWeeks = cloneArray(selectedWeeks);
                    } else {
                        selectedWeeks = [];
                        originalSelectedWeeks = [];
                    }
                } else {
                    selectedWeeks = [];
                    originalSelectedWeeks = [];
                    flagOptionalWeeks = true;
                }

                salesmanDDB.disable(true);
            };

            var clearData = function () {
                var currDate = new Date();
                salesmanDDB.clearAll();
                salesmanDDB.disable(false);
                scheduleTimeCBB.clearAll();
                $("#RouteId").jqxInput({ value: null });
                $("#RouteName").jqxInput({ value: null });
                $("#Description").val("");
                currentRow = -1;
                currentData = {};
                flagUpdate = false;
                clearAllWeekView();
                flag1Weeks = false;
                flag2Weeks = false;
                flag4Weeks = false;
                flagOptionalWeeks = true;
                originalSelectedWeeks = [];
                $("#btn1Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                $("#btn2Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                $("#btn4Week").removeClass( "btn-primary" ).addClass( "btn-default" );
                $("#btnOptionalWeek").removeClass( "btn-primary" ).addClass( "btn-default" );
            };

            var updatePopup = function(row){
                $("#alterpopupWindowAdd").jqxWindow('setTitle', "${StringUtil.wrapString(uiLabelMap.Edit)} ${StringUtil.wrapString(uiLabelMap.PartyBasicInformation)}");
                flagUpdate = true;
                currentRow = row;
                $("#alterpopupWindowAdd").jqxWindow('open');
            };

            var initValidateForm = function () {
                var extendRules = [
                    {input: '#ScheduleTime', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
                        rule: function (input, commit) {
                            var index = input.jqxComboBox('getSelectedItems');
                            return index.length != 0;
                        }
                    },
                ];
                var mapRules = [
                    {input: "#RouteName", type: "validInputNotNull"},
                    {input: '#RouteId', type: 'validInputNotNull'},
                    {input: '#dropdownSalesman', type: 'validObjectNotNull', objType: 'dropDownButton'},
                ];
                validatorVAL = new OlbValidator($("#alterpopupWindowAdd"), mapRules, extendRules, {position: "right"});
            };

            return {
                init: init,
                initValidateForm: initValidateForm,
                updatePopup: updatePopup,
                getWeekNumber: getWeekNumber,
            };
        }());
    }
</script>