var viewKpiRewardPunishmentObj = (function(){
    var init = function(){
        initElement();
        initEvent();
    };

    var initElement = function() {
        $('#contextMenu').jqxMenu({ theme: 'olbius', width: 280, autoOpenPopup: false, mode: 'popup'});
    };

    var initEvent = function(){
        $("#jqxgrid").on('loadCustomControlAdvance', function(){
            var nowDate = new Date();
            var startMonth = new Date(nowDate.getFullYear(), nowDate.getMonth(), 1);
            var endMonth = new Date(nowDate.getFullYear(), nowDate.getMonth() + 1, 0);
            $("#dateTimeInput").jqxDateTimeInput({width: 220, height: 25, selectionMode: 'range'});
            $("#dateTimeInput").jqxDateTimeInput('setRange', startMonth, endMonth);
            refreshGridData(startMonth, endMonth);
            $("#dateTimeInput").on('valueChanged', function(event){
                var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
                if (selection.from != null) {
                    refreshGridData(selection.from, selection.to);
                }
            });
        });
        $("#contextMenu").on('itemclick', function(event){
            var args = event.args;
            var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'viewPunishmentPolicyDetail'){
                openJqxWindow($('#editPolicyWindowDetail'));
                editPolicyObject.setData(dataRecord);
                editPolicyObject.setId(dataRecord.criteriaId, dataRecord.perfCriteriaPolicyId);
            }else if(action == 'editPunishmentPolicy'){
                editKpiPolicyWindow.setData(dataRecord);
                openJqxWindow($("#editKpiPolicyWindow"));
            }
        });
    };
    var refreshGridData = function(fromDate, thruDate){
        var source = $("#jqxgrid").jqxGrid('source');
        source._source.url = "jqxGeneralServicer?sname=JQGetPerfCriteriaPolicySimple&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime();
        $("#jqxgrid").jqxGrid('source', source);
    };
    return{
        init: init,
    }
}());
var editPolicyObject = (function(){
    var criteriaId = "";
    var perfCriteriaPolicyId = "";
    var init = function(){
        initGrid();
        initValidate();
       // initNotification();
        initWindow();
        initBtnEvent();
    };

    var initNotification = function(){
        $('#jqxNotificationsetupPolicyGrid').jqxNotification({width: '100%',
            autoClose: true, template : 'info', appendContainer : "#containersetupPolicyGrid", opacity : 0.9});

        $('#jqxNotificationsetupGrid_edit').jqxNotification({width: '100%',
            autoClose: true, template : 'info', appendContainer : "#containersetupGrid_edit", opacity : 0.9});
    };

    var initWindow = function(){
        createJqxWindow($('#editPolicyWindowDetail'), 780, 375);
    };


    var initGrid = function(){
        var rendertoolbar = function(toolbar){
            var jqxheader = $("<div id='toolbarcontainerPolicy_edit' class='widget-header'><h5><b>" + uiLabelMap.SetupKPIPolicy + "</b></h5><div id='toolbarButtonContainer_edit' class='pull-right'></div></div>");
            if(toolbar.children().length == 0)
            {
                toolbar.append(jqxheader);
                var container = $('#toolbarButtonContainer_edit');
                var grid = $("#setupGrid_edit");
                Grid.createAddRowButton(grid, container, uiLabelMap.CommonAddNew, {type: "popup", container: $("#setupKpiPolicyWindow")});
            }
            return toolbar;
        };

        var config = {
            width: '100%',
            height: 275,
            autoheight: false,
            virtualmode : true,
            showfilterrow: false,
            showtoolbar: true,
            rendertoolbar: rendertoolbar,
            pageable: true,
            sortable: false,
            filterable: false,
            editable: true,
            url : '',
            source : {
                updateUrl : 'jqxGeneralServicer?jqaction=U&sname=updateKpiPolicyitem',
                editColumns : "perfCriteriaPolicyId;fromRating(java.math.BigDecimal);toRating(java.math.BigDecimal);amount(java.math.BigDecimal);criteriaPolSeqId(java.lang.Long);kpiPolicyEnumId;"
            }
        };


        var column = [
            {datafield : 'kpiPolicyEnumId', hidden : 'true'},
            {datafield : 'criteriaPolSeqId', hidden : 'true'},
            {text : uiLabelMap.HRCalcTypeEnumId, width : '20%', dataField : 'kpiCalcTypeEnumId', editable: false,
                cellsrenderer : function(row, column, value){
                    if(OlbCore.isNotEmpty(value) && value == 'KPI_CALC_FORMULA'){
                        return '<span>' + uiLabelMap.HRCalcFormula + '</span>';
                    }else{
                        return '<span>' + uiLabelMap.HRCalcConst + '</span>';
                    }
                }
            },
            {text : uiLabelMap.HRCalcExpression, width : '25%', dataField : 'bonusBaseOnEnumId', editable: false,
                cellsrenderer : function(row, column, value){
                    var data = $('#setupGrid_edit').jqxGrid('getrowdata', row);
                    if(OlbCore.isNotEmpty(value) && OlbCore.isNotEmpty(data.rate) && value == 'KPI_BASEON_TARGET') {
                        return '<span>' + uiLabelMap.HRBaseOnTarget + ' * ' + data.rate +'</span>';
                    }else if (OlbCore.isNotEmpty(value) && OlbCore.isNotEmpty(data.rate) && value == 'KPI_BASEON_ACTUAL'){
                        return '<span>' + uiLabelMap.HRBaseOnActual + ' * ' + data.rate +'</span>';
                    }else {
                        return '<span></span>';
                    }
                }
            },
            {text : uiLabelMap.KPIFromRating, datafield : 'fromRating', width : '10%',
                cellsrenderer : function(row, column, value){
                    var val = value;
                    if(value){
                        val = formatnumber(value);
                    }
                    return '<span>' + val + '</span>';
                },
                cellendedit : function(row, datafield, columntype, oldvalue, newvalue){
                    if(newvalue < 0){
                        return oldvalue;
                    }
                    return newvalue;
                }
            },
            {text : uiLabelMap.KPIToRating, datafield : 'toRating', width : '10%',
                cellsrenderer : function(row, column, value){
                    var val = value;
                    if(value){
                        val = formatnumber(value);
                    }
                    return '<span>' + val + '</span>';
                },
                cellendedit : function(row, datafield, columntype, oldvalue, newvalue){
                    if(newvalue < 0){
                        return oldvalue;
                    }
                    return newvalue;
                }
            },
            {text : uiLabelMap.HRCommonAmount, datafield : 'amount', width : '20%',
                cellsrenderer : function(row, column, value){
                    var val = value;
                    if(value){
                        val = formatnumber(value);
                    }
                    return '<span>' + val + '</span>';
                },
                cellendedit : function(row, datafield, columntype, oldvalue, newvalue){
                    if(newvalue < 0){
                        return oldvalue;
                    }
                    return newvalue;
                }
            },
            {text : uiLabelMap.RewardPunishment, datafield : 'description', width : '15%', columntype : 'dropdownlist',
                createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
                    editor.jqxDropDownList({source : globalVar.enumKpi, width : cellwidth,
                        height : cellheight, displayMember : 'description', valueMember : 'enumId'});
                },
                cellsrenderer : function(row, column, value){
                    if(value){
                        if(value == 'KPI_PUNISHMENT'){
                            return '<span>' + uiLabelMap.KpiPunishment + '</span>';
                        }else if(value == 'KPI_REWARD'){
                            return '<span>' + uiLabelMap.KpiReward + '</span>';
                        }else{
                            return '<span>' + value + '</span>';
                        }
                    }
                },
                cellendedit : function(row, datafield, columntype, oldvalue, newvalue){
                    var data = $('#setupGrid_edit').jqxGrid('getrowdata', row);
                    data.kpiPolicyEnumId = newvalue;
                }
            }
        ];
        var datafield = [
            {name : 'perfCriteriaPolicyId', type : 'string'},
            {name : 'fromRating', type : 'number'},
            {name : 'toRating', type : 'number'},
            {name : 'amount', type : 'number'},
            {name : 'kpiPolicyEnumId', type : 'string'},
            {name : 'description', type : 'string'},
            {name : 'criteriaPolSeqId', type : 'number'},
            {name : 'kpiCalcTypeEnumId', type : 'string'},
            {name : 'bonusBaseOnEnumId', type : 'string'},
            {name : 'rate', type : 'number'}
        ];

        Grid.initGrid(config, datafield, column, null, $('#setupGrid_edit'));
    };

    var refreshGridData = function(criteriaId, perfCriteriaPolicyId){
        var tmpSrc = $('#setupGrid_edit').jqxGrid('source');
        tmpSrc._source.url = "jqxGeneralServicer?sname=getKpiPolicyItem&criteriaId=" + criteriaId + "&perfCriteriaPolicyId=" + perfCriteriaPolicyId;
        $('#setupGrid_edit').jqxGrid('source', tmpSrc);
    };

    var initValidate = function(){
        $('#editPolicyWindowDetail').jqxValidator({
            rules : [
                {
                    input : '#pagersetupGrid_edit', message : uiLabelMap.FieldRequired,
                    rule : function(input, commit){
                        var data = $('#setupGrid_edit').jqxGrid('getrows');
                        if(!data.length){
                            return false;
                        }
                        return true;
                    }
                }

            ]
        });
    };
    var validate = function(){
        return $('#editPolicyWindowDetail').jqxValidator('validate');
    };


    var setData = function(data){
        refreshGridData(data.criteriaId, data.perfCriteriaPolicyId)
    };

    initBtnEvent = function(){
        $('#alterCancel_edit').click(function(){
            $('#editPolicyWindowDetail').jqxWindow('close');
        })
    };

    var closeWindow = function(){
        $('#editPolicyWindowDetail').jqxWindow('close');
    };

    var updateKpiPolicy = function(data){
        $.ajax({
            type : 'POST',
            data : {
                'setup' : JSON.stringify(data),
                'perfCriteriaPolicyId' : perfCriteriaPolicyId
            },
            url : 'updateKpiPolicy',
            datatype : 'json',
            success : function(response){
                if(response.responseMessage == 'success'){
                    var success = uiLabelMap.updateSuccessfully;
                    $("#jqxNotificationsetupGrid_edit").jqxNotification('closeLast');
                    $("#notificationContentsetupGrid_edit").text(success);
                    $("#jqxNotificationsetupGrid_edit").jqxNotification('open');
                    if(data.flag == 'save'){
                        $('#setupKpiPolicyWindow').jqxWindow('close');
                    }
                    $('#jqxgrid').jqxGrid('updatebounddata');
                    $('#setupGrid_edit').jqxGrid('updatebounddata');
                }else{
                    bootbox.dialog(response.errorMessage, [{
                        "label" : uiLabelMap.CommonClose,
                        "class" : "btn-danger btn-small icon-remove open-sans",
                    }])
                }
            }
        })
    };

    var setId = function(criteriaIdTmp, perfCriteriaPolicyIdTmp){
        criteriaId = criteriaIdTmp;
        perfCriteriaPolicyId = perfCriteriaPolicyIdTmp;
    };


    return {
        init : init,
        validate : validate,
        setData : setData,
        setId : setId,
        updateKpiPolicy : updateKpiPolicy
    }
}());
var editKpiPolicyWindow = (function(){
    var init = function(){
        initWindow();
        initJqxDateTime();
        initInput();
        initValidator();
        initBtnEvent();
        initWindowEvent();
    };

    var initInput = function(){
        $('#policyId').jqxInput({width : '250px', height : '20px', disabled: true});
        $('#criteriaId').jqxInput({width : '250px', height : '20px', disabled: true});
    };

    var initWindow = function(){
        createJqxWindow($('#editKpiPolicyWindow'), 440, 280);
    };

    var initJqxDateTime = function(){
        $('#fromDateNew').jqxDateTimeInput({width : '255px', height : '25px'});
        $('#thruDateNew').jqxDateTimeInput({width : '255px', height : '25px', clearString:'Clear', showFooter:true});
    };

    var initValidator = function(){
        $('#editKpiPolicyWindow').jqxValidator({
            rules : [
                {
                    input : '#fromDateNew', message : uiLabelMap.FieldRequired, action : 'blur',
                    rule : function(input, commit){
                        if(!input.jqxDateTimeInput('getDate')){
                            return false;
                        }
                        return true;
                    }
                },
                {
                    input : '#fromDateNew', message : uiLabelMap.FromDateLessThanEqualThruDate, action : 'blur',
                    rule : function(input, commit){
                        if($('#thruDateNew').jqxDateTimeInput('getDate')){
                            if(input.jqxDateTimeInput('getDate') > $('#thruDateNew').jqxDateTimeInput('getDate')){
                                return false;
                            }
                        }
                        return true;
                    }
                }
            ]
        });

    };
    var setData = function(data){
        $("#policyId").val(data.perfCriteriaPolicyId);
        $("#criteriaId").val(data.criteriaId);
        $("#fromDateNew").jqxDateTimeInput('val', data.fromDate);
        $("#thruDateNew").jqxDateTimeInput('val', data.thruDate);
    };
    var getData = function(){
        var data = {};
        data.policyId = $('#policyId').val();
        data.criteriaId	 = $('#criteriaId').val();
        data.fromDate = $('#fromDateNew').jqxDateTimeInput('getDate').getTime();
        if($('#thruDateNew').jqxDateTimeInput('getDate')){
            data.thruDate = $('#thruDateNew').jqxDateTimeInput('getDate').getTime();
        }else{
            data.thruDate = null;
        }

        return data;
    };

    var initBtnEvent = function(){
        $('#alterSave_new').click(function(){
            var valid = $('#editKpiPolicyWindow').jqxValidator('validate');
            if(!valid){
                return false;
            }
            var data = getData();
            editPolicyKpi(data);
        });
        $('#alterCancel_new').click(function(){
            $('#editKpiPolicyWindow').jqxWindow('close');
        });

    };

    var initWindowEvent = function(){
        $('#editKpiPolicyWindow').bind('close', function(){
            $('#policyId').jqxInput('val', null);
            $('#criteriaId').jqxInput('val', null);
            $('#fromDateNew').jqxDateTimeInput('val', new Date());
            $('#thruDateNew').jqxDateTimeInput('val', null);
        });
    };

    var closeWindow = function(){
        $("#editKpiPolicyWindow").jqxWindow('close');
    };

    var editPolicyKpi = function(data){
        $.ajax({
            type : 'POST',
            data : {
                'policyId' : data.policyId,
                'criteriaId' : data.criteriaId,
                'fromDate' : data.fromDate,
                'thruDate' : data.thruDate
            },
            url : 'editKpiPolicy',
            success : function(response){
                $('#jqxNotification').jqxNotification('closeLast');
                if(response.responseMessage == 'success'){
                    $('#container').empty();
                    $('#jqxNotification').jqxNotification({ template: 'info'});
                    $("#jqxNotification").html(response.successMessage);
                    $("#jqxNotification").jqxNotification("open");
                    $("#jqxgrid").jqxGrid('updateBoundData');
                    closeWindow();
                }else{
                    $('#container').empty();
                    $('#jqxNotification').jqxNotification({ template: 'error'});
                    $("#jqxNotification").html(response.errorMessage);
                    $("#jqxNotification").jqxNotification("open");
                }
            }
        })
    };

    var setId = function(criteriaIdTmp){
        criteriaId = criteriaIdTmp;
    };

    return {
        init : init,
        setId : setId,
        setData: setData
    }
}());
var newKPIPolicyItem = (function(){
    var editmode = false;
    var init = function(){
        initWindow();
        initNumberInput();
        initDropDown();
        initValidate();
        initBtnEvent();
        initWindowEvent();
    };

    var initWindow = function(){
        createJqxWindow($('#setupKpiPolicyWindow'), 375, 260);
    };

    var initNumberInput = function(){
        $('#fromRating').jqxNumberInput({width : '98%', height : '22px', symbolPosition: 'right', symbol: '%', spinButtons: true, allowNull: true });
        $('#toRating').jqxNumberInput({width : '98%', height : '22px', symbolPosition: 'right', symbol: '%', spinButtons: true, allowNull: true });
        $('#amount').jqxNumberInput({width : '98%', height : '22px', spinButtons: true });
    };


    initDropDown = function(){
        createJqxDropDownList(globalVar.enumKpi, $('#status'), 'enumId' , 'description', '25px', '98%');
    };

    var initValidate = function(){
        $('#setupKpiPolicyWindow').jqxValidator({
            rules : [
                {input : '#fromRating', action : 'blur', message : uiLabelMap.FieldRequired, rule :
                        function(input, commit){
                            if(input.val() === null){
                                return false;
                            }
                            return true;
                        }
                },
                {input : '#amount', action : 'blur', message : uiLabelMap.FieldRequired, rule :
                        function(input, commit){
                            if(!input.val()){
                                return false;
                            }
                            return true;
                        }
                },
                {input : '#status', action : 'blur', message : uiLabelMap.FieldRequired,
                    rule : function(input, commit){
                        var val = input.val();
                        if(!val){
                            return false;
                        }
                        return true;
                    }
                },
                {
                    input : '#toRating', action : 'blur', message : uiLabelMap.KpiPolicyItemPointValidate,
                    rule : function(input, commit){
                        if(input.val()){
                            if($('#fromRating').val() >= input.val()){
                                return false;
                            }
                        }
                        return true;
                    }
                },
                {input : '#fromRating', action : 'blur', message : uiLabelMap.PointCannotBeNagative,
                    rule : function(input, commit){
                        if(input.val() < 0){
                            return false;
                        }
                        return true;
                    }
                }
            ]
        })
    };

    var getData = function(){
        var data = {};
        data.fromRating = $('#fromRating').val();
        if($('#toRating').val()){
            data.toRating = $('#toRating').val();
        }else{
            data.toRating = null;
        }
        data.amount = $('#amount').val();
        data.kpiPolicyEnumId = $('#status').val();
        data.description = $('#status')[0].textContent;
        return data;
    };

    var initBtnEvent = function(){
        var checkSetup = function(data){
            var rows = [];
            if(!editmode){
                return true;
            }else{
                rows = $('#setupGrid').jqxGrid('getrows');
            }
            if(rows.length != 0){
                for(var i = 0; i < rows.length ; i++){
                    var fromRating = data.fromRating;
                    var toRating = data.toRating;
                    var x = rows[i].fromRating;
                    var y = rows[i].toRating;
                    if(y != null){
                        if(toRating != null){
                            if((fromRating >= x && fromRating <= y) || (toRating >= x && toRating <= y)){
                                return false;
                            }else{
                                if(fromRating < x && toRating >= x){
                                    return false;
                                }
                            }
                        }
                        return true;
                    }else{
                        if(toRating != null){
                            if(toRating > x){
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        $('#saveSetupKpi').click(function(){
            var validate = $('#setupKpiPolicyWindow').jqxValidator('validate');
            if(validate){
                var data = getData();
                data.flag = 'save';
                if(checkSetup(data)){
                    if(editmode){
                        $('#setupGrid').jqxGrid('addRow', null, data, "first");
                        $('#setupKpiPolicyWindow').jqxWindow('close');
                    }else{
                        editPolicyObject.updateKpiPolicy(data);
                    }
                }else{
                    bootbox.dialog(uiLabelMap.WrongSetup, [{
                        "label" : uiLabelMap.CommonClose,
                        "class" : "btn-danger btn-small icon-ok open-sans"
                    }])
                }
            }else{
                return false;
            }
        });

        $('#saveAndContinueSetup').click(function(){
            var validate = $('#setupKpiPolicyWindow').jqxValidator('validate');
            if(validate){
                var data = getData();
                data.flag = 'saveAndContinue';
                if(checkSetup(data)){
                    if(editmode){
                        $('#setupGrid').jqxGrid('addRow', null, data, "first");
                    }else{
                        editPolicyObject.updateKpiPolicy(data);
                    }
                }else{
                    bootbox.dialog(uiLabelMap.WrongSetup, [{
                        "label" : uiLabelMap.CommonClose,
                        "class" : "btn-primary btn-small icon-ok open-sans"
                    }])
                }
            }else{
                return false;
            }
        });

        $('#cancelSetupKpi').click(function(){
            $('#setupKpiPolicyWindow').jqxWindow('close');
        });
    };

    var initWindowEvent = function(){
        $('#setupKpiPolicyWindow').bind('close', function(){
            $('#fromRating').jqxNumberInput('val', null);
            $('#toRating').jqxNumberInput('val', null);
            $('#amount').jqxNumberInput('val', null);
            $('#status').jqxDropDownList('clearSelection');
        });
        $("#" + globalVar.newKPIWindow).on('open', function(){
            editmode = true;
        });
        $("#" + globalVar.newKPIWindow).on('close', function(){
            editmode = false;
        });
    };


    return {
        init : init
    }
}());

var wizardObj = (function(){
    var init = function(){
        $('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
            if(info.step == 1 && (info.direction == "next")) {
                if(!editKPIObj.validate()){
                    return false;
                }
            }
        }).on('finished', function(e) {
            var valid = policyKPI.validate();
            if(!valid){
                return false;
            }
            var data_step1 = editKPIObj.getData();
            var data_step2 = policyKPI.getData();
            var dataSubmit = $.extend({}, data_step1, data_step2);
            bootbox.dialog(uiLabelMap.CreateKPIConfirm, [{
                "label" : uiLabelMap.CommonSubmit,
                "class" : "btn-primary btn-small icon-ok open-sans",
                "callback": function() {
                    createKpi(dataSubmit);
                }
            },{
                "label" : uiLabelMap.CommonClose,
                "class" : "btn-danger btn-small icon-ok open-sans"
            }])
        }).on('stepclick', function(e){
        });
    };

    var createKpi = function(data){
        $.ajax({
            type : 'POST',
            data : {
                'criteriaName': data.criteriaName,
                'description' : data.description,
                'fromDate' : data.fromDate,
                'perfCriDevelopmetTypeId' : data.perfCriDevelopmetTypeId,
                'perfCriteriaTypeId' : data.perfCriteriaTypeId,
                'periodTypeId' : data.periodTypeId,
                'setup': JSON.stringify(data.setup),
                'target' : data.target,
                'thruDate' : data.thruDate,
                'uomId' : data.uomId
            },
            url : 'createNewKpiAndSetup',
            success : function(response){
                if(response.responseMessage == "success"){
                    var success = uiLabelMap.createSuccessfully;
                    Grid.renderMessage('jqxgrid', success, {autoClose: true,
                        template : 'info', appendContainer : "#containerjqxgrid", opacity : 0.9});
                    editKPIObj.closeWindow();
                    $('#jqxgrid').jqxGrid('updatebounddata');
                }else{
                    bootbox.dialog(response.errorMessage,
                        [{
                            "label" : uiLabelMap.CommonClose,
                            "class" : "btn-danger btn-small icon-remove open-sans",
                        }]
                    );
                }
            }
        })
    };

    var resetStep = function(){
        $('#fuelux-wizard').wizard('previous');
    };

    return {
        init : init,
        resetStep : resetStep,
    }
}());
$(document).ready(function(){
    viewKpiRewardPunishmentObj.init();
    editPolicyObject.init();
    newKPIPolicyItem.init();
    editKpiPolicyWindow.init();
});
