$(function(){
    var interval = setInterval(function(){
        if(accCommon !== undefined && typeof accCommon === "function"){
            OlbOpenTimePeriod.init();
            clearInterval(interval);
        }
    },200)
})

var OlbOpenTimePeriod = (function(){
    var validatorVAL;
    var accCm = new accCommon();
    var init = function(){
        initjqxWindow();
        initDropDownList();
        initNumberInput();
        initDateTimeInput();
        initInput();
        initValidator();
        initContextMenus();
        bindEvent();
    };

    var initjqxWindow = function(){
        $("#alterpopupWindow").jqxWindow({
            width: 580, height : 380,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme
        });
    }

    var initValidator = function(){
        var mapRules = [
            {input: '#fromDate', type: 'validInputNotNull',action : 'change,close,blur'},
            {input: '#thruDate', type: 'validInputNotNull',action : 'change,close,blur'},
            {input: '#periodTypeIdAdd', type: 'validInputNotNull',action : 'change,close,blur'},
            {input: '#periodNameAdd', type: 'validInputNotNull',action : 'change,close,blur'}
        ]
        validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
        accCm.dateUtil.init('fromDate','thruDate');
    }

    var initDateTimeInput = function(){
        jOlbUtil.dateTimeInput.create('#fromDate',{width: '250px', height: '25px',allowNullDate : true,value : null});
        jOlbUtil.dateTimeInput.create('#thruDate',{width: '250px', height: '25px',allowNullDate : true,value : null});
    }

    var initDropDownList = function(){
        accCm.createDropDownList('#periodTypeIdAdd',{ source: dataPT, displayMember: "description", valueMember: "periodTypeId",obj : accCm});
        var configperiodType = {
            width:'250px',
            height: 25,
            key: "customTimePeriodId",
            value: "periodName",
            displayDetail: false,
            dropDownWidth: 250,
            autoDropDownHeight: 'auto',
            multiSelect: false,
            filterable: true,
            filterPlaceHolder: uiLabelMap.BACCSearchDropDownList,
            placeHolder: uiLabelMap.BACCPleaseChooseAcc,
            useUrl: false, url: '',
        };

        new OlbDropDownList($("#parentPeriodIdAdd"), dataOOtp, configperiodType, []);
        // accCm.createDropDownList('#parentPeriodIdAdd',{autoDropDownHeight : false, source: dataOOtp, displayMember: "periodName", valueMember: "customTimePeriodId",obj : accCm})
    }

    var initInput = function(){
        jOlbUtil.input.create('#periodNameAdd',{width: 245,height : 25});
    }

    var initNumberInput = function(){
        jOlbUtil.numberInput.create('#periodNum',{width: 250,height : 25,min : 0,max : 999999999999,digits : 17,decimalDigits : 0,spinButtons : true});
    }

    var initContextMenus = function(){
        jOlbUtil.contextMenu.create("#contextMenu", 2, { width: 200, autoOpenPopup: false, mode: 'popup', theme: accCm.theme})
    }


    var save  = function(){
        if(!validatorVAL.validate()) { return;}
        var row = getData();
        $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');
        $("#jqxgrid").jqxGrid('selectRow', 0);

        return true;
    }

    var getData = function(){
        var row;
        row = {
            fromDate:$('#fromDate').jqxDateTimeInput('getDate'),
            isClosed:"N",
            parentPeriodId:$('#parentPeriodIdAdd').jqxDropDownList("val"),
            periodName:$('#periodNameAdd').val(),
            periodNum:$('#periodNum').val(),
            periodTypeId:$('#periodTypeIdAdd').val(),
            thruDate: $('#thruDate').jqxDateTimeInput('getDate'),
        };
        return row;
    }

    var bindEvent = function(){
        // update the edited row when the user clicks the 'Save' button.
        $("#save").click(function () {
            if(save())  $("#alterpopupWindow").jqxWindow('close');
        });

        $('#saveAndContinue').click(function(){
            save();
        })

        $('#alterpopupWindow').on('close',function(){
            $('#periodTypeIdAdd').jqxDropDownList('clearSelection');
            $('#parentPeriodIdAdd').jqxDropDownList('clearSelection');
            $("#fromDate").jqxDateTimeInput('value',null);
            $("#thruDate").jqxDateTimeInput('value',null);
            $("#periodNameAdd").jqxInput('val','');
            $("#periodNum").jqxNumberInput('clear');
            $('#formAdd').jqxValidator('hide');
            accCm.dateUtil.resetDate();
        });

        //event for context menus
        $("#contextMenu").on('itemclick', function (event) {
            var args = event.args;
            var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var tmpKey = $.trim($(args).text());
            if (tmpKey == uiLabelMap.BACCRefresh )
            {
                $("#jqxgrid").jqxGrid('updatebounddata');
            }
            else if (tmpKey == uiLabelMap.BACCaccIsClosed )
            {
                bootbox.dialog(uiLabelMap.BACCConfirmClosingPeriod ,
                    [{
                        "label" : uiLabelMap.CommonSubmit,
                        "class" : "btn-primary btn-mini icon-ok",
                        "callback": function() {
                            closingPeriod(rowindex);
                        }
                    },
                        {
                            "label" : uiLabelMap.CommonClose,
                            "class" : "btn-danger btn-mini icon-remove",
                            "callback": function() {

                            }
                        }]
                );
            }

        });

    };

    var closingPeriod = function (rowindex) {
        var tmpData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
        var data = 'columnList0' + '=' + 'customTimePeriodId';
        data = data + '&' + 'columnValues0' + '=' +  tmpData.customTimePeriodId;
        data += "&rl=1";
        $.ajax({
            type: "POST",
            url: 'jqxGeneralServicer?&jqaction=U&sname=closeFinancialTimePeriod',
            data: data,
            beforeSend : function(){
                $('#jqxgrid').jqxGrid('showloadelement');
            },
            success: function(odata, status, xhr)
            {
                $('#jqxgrid').jqxGrid('updatebounddata');
                if($('#jqxGridClosed').length > 0) $('#jqxGridClosed').jqxGrid('updatebounddata');

                try{
                    if(odata.hasOwnProperty('results')){
                        if(odata['results'].hasOwnProperty('errorMessage')) Grid.updateGridMessage('jqxgrid', 'error', uiLabelMap.BACCcloseTimesPeriodsError);
                    }
                    if(odata.responseMessage) $('#jqxgrid').jqxGrid('hideloadelement');
                    if(odata.responseMessage == "error")
                    {
                        var error = odata.hasOwnProperty('errorMessageList') ? odata.errorMessageList : null;
                        if(error != null) {
                            if( error instanceof Array){
                                error = (function(error){
                                    var msg = '';
                                    var i = 0;
                                    $.each(error,function(){
                                        msg += i++ +'. ' + this + ' \r\n ';
                                    })
                                    return msg;

                                }(error))
                            }else if(error instanceof String){

                            }
                        }else error =  uiLabelMap.BACCcloseTimesPeriodsError
                        Grid.updateGridMessage('jqxgrid', 'error',error);
                        $('#jqxgrid').jqxGrid('focus');
                    }
                    else
                    {
                        Grid.updateGridMessage('jqxgrid', 'success', uiLabelMap.BACCcloseTimesPeriodsSuccess);
                        $('#jqxgrid').jqxGrid('focus');
                    }
                    var interval = setInterval(function(){
                        if($('#notificationContentjqxgrid').length > 0){
                            if($('#notificationContentjqxgrid').css('white-space') != 'pre-line'){
                                $('#notificationContentjqxgrid').css('white-space','pre-line');
                            }else clearInterval(interval);
                        }
                    },100);

                }catch(err){
                    console.log('error when close final Time Periods');
                }finally{

                }
            },
            error: function(arg1)
            {
            }
        });
    };

    return {
        init : init,
        accCm : accCm
    }
}());
