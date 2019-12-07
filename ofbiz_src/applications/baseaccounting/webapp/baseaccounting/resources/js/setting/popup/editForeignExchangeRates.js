    $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
    $(function () {
        EditForeign.init();
    });
	var EditForeign = (function(){
        var bankEditGrid;
        var init = function(){
            initElement();
            bindEvent();
            initRules();
        };
        var initData = function(data) {
            $('#uomIdEdit').jqxDropDownList('val', data.uomId);
            $('#uomIdToEdit').jqxDropDownList('val', data.uomIdTo);
            $('#purposeEnumIdEdit').jqxDropDownList('val', data.purposeEnumId);
            // $('#bankIdEdit').jqxDropDownList('val', data.bankId);
            $("#purchaseRateEdit").jqxNumberInput('val', data.purchaseExchangeRate);
            $("#conversionFactorEdit").jqxNumberInput('val', data.conversionFactor);
            $("#sellingRateEdit").jqxNumberInput('val', data.sellingExchangeRate);
            $("#fromDateEdit").jqxDateTimeInput('val', data.fromDate);
            $("#thruDateEdit").jqxDateTimeInput('val', data.thruDate);
            // initBankDropDownButton(data.bankId);
            bankEditGrid.clearAll();
            bankEditGrid.selectItem([data.bankId]);
            // $("#bankIdEdit").jqxDropDownButton('val', data.bankId);
        };

        var initBankDropDownButton = function() {
            var configBank = {
                useUrl: true,
                root: 'results',
                widthButton: '88%',
                showdefaultloadelement: false,
                autoshowloadelement: false,
                datafields: [{name: 'bankId', type: 'string'}, {name: 'bankName', type: 'string'}, {name: 'shortName', type: 'string'}],
                columns: [
                    {text: uiLabelMap.BACCBankId, datafield: 'bankId', width: '30%'},
                    {text: uiLabelMap.BACCBankName, datafield: 'bankName', width: '50%'},
                    {text: uiLabelMap.BACCShortName, datafield: 'shortName'}
                ],
                url: 'JQGetListBankConversion',
                useUtilFunc: true,
                key: 'bankId',
                pageable: true,
                pagesizeoptions: ['10', '20', '30', '40', '50'],
                description: function (rowData) {
                    if (rowData) {
                        var descriptionValue = rowData['bankName'];
                        return descriptionValue;
                    }
                },
                autoCloseDropDown: true,
                filterable: true,
                sortable: true
            };
            bankEditGrid = new OlbDropDownButton($("#bankIdEdit"), $("#bankGridEdit"), null, configBank, []);
        };
		var initElement = function(){
			$('#uomIdEdit').jqxDropDownList({disabled: true, autoDropDownHeight : (luData.length > 10 ? false : true),width: '250', height: '25px',filterable:  (luData.length > 10 ? true : false),source: luData, displayMember: "description", valueMember: "uomId"});
		    $('#uomIdToEdit').jqxDropDownList({disabled: true, autoDropDownHeight : (luData.length > 10 ? false : true), width: '250', height: '25px',filterable : (luData.length > 10 ? true : false),  source: luData, displayMember: "description", valueMember: "uomId"});
		    $('#purposeEnumIdEdit').jqxDropDownList({width: '250', height: '25px', autoDropDownHeight :  (leData.length > 10 ? false : true), source: leData, displayMember: "description", valueMember: "enumId"});
		    // $('#bankIdEdit').jqxDropDownList({filterable: true,width: '250', height: '25px', autoDropDownHeight :  (bankData.length > 10 ? false : true), source: bankData, displayMember: "bankName", valueMember: "bankId"});
			$("#fromDateEdit").jqxDateTimeInput({disabled:true, width: '250', height: '25px',allowNullDate : true,value : null, formatString: 'dd/MM/yyyy HH:mm:ss'});
			$("#thruDateEdit").jqxDateTimeInput({width: '250', height: '25px',allowNullDate : true,value : null, formatString: 'dd/MM/yyyy HH:mm:ss'});
            // $("#purchaseRateEdit").jqxNumberInput({width: '250',height : 25,digits : 15,min  : 0,max : 9999999,decimalDigits : 2});
            $("#purchaseRateEdit").jqxNumberInput({width: '250', height: 25, digits: 15, min: 0, max: 9999999,decimalDigits : 2});
            $("#conversionFactorEdit").jqxNumberInput({width: '250', height: 25, digits: 15,min: 0, max: 9999999, decimalDigits : 2});
			$("#sellingRateEdit").jqxNumberInput({width: '250', height: 25, digits: 15,min: 0, max: 9999999, decimalDigits: 2});
			initjqxWindow();
            initBankDropDownButton();
			filterDate.init('fromDate','thruDate');
		};
		var initjqxWindow = function(){
			$("#editPopupWindow").jqxWindow({
		        width: 530,height :430,  resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelEdit"), modalOpacity: 0.7, theme: theme
		    });
		};
		var initRules = function(){
			$('#formEdit').jqxValidator({
				rules : [
					{input : '#bankIdEdit',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
						var val = input.jqxDropDownButton('val');
						if(!val) return false;
						return true;
					}},
					{input : '#purchaseRateEdit',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
						var val = input.jqxNumberInput('val');
						if(!val) return false;
						return true;
					}},
                    {input : '#conversionFactorEdit',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
                        var val = input.jqxNumberInput('val');
                        if(!val) return false;
                        return true;
                    }},
                    {input : '#sellingRateEdit',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
						var val = input.jqxNumberInput('val');
						if(!val) return false;
						return true;
					}},
                    {input : '#fromDateEdit',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
                        var val = input.jqxDateTimeInput('val');
                        if(!val) return false;
                        return true;
                    }}
				]
			})
		};
		var save = function(){
			if(!$('#formEdit').jqxValidator('validate')){return false;}
			var data = getData();
            $.ajax({
                url: 'updateFXConversionNew',
                type: "POST",
                data: data,
                cache: false,
                success: function(response) {
                    if(response._ERROR_MESSAGE_){
                        bootbox.dialog(response._ERROR_MESSAGE_,
                            [
                                {
                                    "label" : uiLabelMap.CommonClose,
                                    "class" : "btn-danger btn-small icon-remove open-sans",
                                }]
                        );
                        return false;
                    }
                    $("#editPopupWindow").jqxWindow('close');
                    $("#jqxgrid").jqxGrid('updatebounddata');
                    return true;
                }
            });
            };

		var getData = function() {
		    var purchaseExchangeRate = $('#purchaseRateEdit').val() + '';
            purchaseExchangeRate = purchaseExchangeRate.replace('.', ',');
            var conversionFactor = $('#conversionFactorEdit').val() + '';
            conversionFactor = conversionFactor.replace('.', ',');
            var sellingExchangeRate = $('#sellingRateEdit').val() + '';
            sellingExchangeRate = sellingExchangeRate.replace('.', ',');
            var data = {};
            data.fromDate =  $('#fromDateEdit').jqxDateTimeInput('getDate').getTime();
            data.uomId = $('#uomIdEdit').val();
            data.uomIdTo = $('#uomIdToEdit').val();
            data.purposeEnumId = $('#purposeEnumIdEdit').jqxDropDownList('val');
            data.purchaseExchangeRate = purchaseExchangeRate;
            data.conversionFactor = conversionFactor;
            data.sellingExchangeRate = sellingExchangeRate;
            data.bankId = jOlbUtil.getAttrDataValue('bankIdEdit');
            data.thruDate = $('#thruDateEdit').jqxDateTimeInput('getDate') != null ? $('#thruDateEdit').jqxDateTimeInput('getDate').getTime() : null;
            return data;
        };
		
		var bindEvent = function(){
			$("#saveEdit").click(function () {
			    save();
		    });
            $("#editPopupWindow").on('close',function(){
                clear();
            });
        };

        var clear = function(){
            $('#fromDateEdit').jqxDateTimeInput('val',null);
            $('#thruDateEdit').jqxDateTimeInput('val',null);
            $('#purchaseRateEdit').jqxNumberInput('clear');
            $('#conversionFactorEdit').jqxNumberInput('clear');
            $('#sellingRateEdit').jqxNumberInput('clear');
            $('#uomIdEdit').jqxDropDownList('clearSelection');
            $('#uomIdToEdit').jqxDropDownList('clearSelection');
            $('#purposeEnumIdEdit').jqxDropDownList('clearSelection');
            filterDate.resetDate();
            setTimeout(function(){
                $('#formEdit').jqxValidator('hide');
            },200)
        };
		
		return {
		    init: init,
            initData: initData
		}
	}());
    
