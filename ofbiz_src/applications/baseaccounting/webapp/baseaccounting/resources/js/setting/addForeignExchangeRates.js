    $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var action = (function(){
		var initElement = function(){
			$('#uomId').jqxDropDownList({autoDropDownHeight : (luData.length > 10 ? false : true),width: '250', height: '25px',filterable:  (luData.length > 10 ? true : false),source: luData, displayMember: "description", valueMember: "uomId",placeHolder : uiLabelMap.PleaseChooseAcc ? uiLabelMap.PleaseChooseAcc : ''});
		    $('#uomIdTo').jqxDropDownList({autoDropDownHeight : (luData.length > 10 ? false : true), width: '250', height: '25px',filterable : (luData.length > 10 ? true : false),  source: luData, displayMember: "description", valueMember: "uomId",placeHolder : uiLabelMap.PleaseChooseAcc ? uiLabelMap.PleaseChooseAcc : ''});
		    $('#purposeEnumId').jqxDropDownList({width: '250', height: '25px', autoDropDownHeight :  (leData.length > 10 ? false : true), source: leData, displayMember: "description", valueMember: "enumId",placeHolder : uiLabelMap.PleaseChooseAcc ? uiLabelMap.PleaseChooseAcc : ''});
		    // $('#bankId').jqxDropDownList({filterable: true,width: '250', height: '25px', autoDropDownHeight :  (bankData.length > 10 ? false : true), source: bankData, displayMember: "bankName", valueMember: "bankId",placeHolder : uiLabelMap.PleaseChooseAcc ? uiLabelMap.PleaseChooseAcc : ''});
			$("#fromDate").jqxDateTimeInput({width: '250', height: '25px',allowNullDate : true,value : null, formatString: 'dd/MM/yyyy HH:mm:ss'});
			$("#thruDate").jqxDateTimeInput({width: '250', height: '25px',allowNullDate : true,value : null, formatString: 'dd/MM/yyyy HH:mm:ss'});
            $("#purchaseRate").jqxNumberInput({width: '250',height : 25,digits : 15,min  : 0,max : 9999999,decimalDigits : 2});
            $("#conversionFactor").jqxNumberInput({width: '250',height : 25,digits : 15,min  : 0,max : 9999999,decimalDigits : 2});
			$("#sellingRate").jqxNumberInput({width: '250',height : 25,digits : 15,min  : 0,max : 9999999,decimalDigits : 2});
			initjqxWindow();
			initBankDropDownButton();
			filterDate.init('fromDate','thruDate');
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
                new OlbDropDownButton($("#bankId"), $("#bankGrid"), null, configBank, []);
        };
		var initjqxWindow = function(){
			$("#alterpopupWindow").jqxWindow({
		        width: 530,height :430,  resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme: theme          
		    });
		}
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#bankId',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
						var val = input.jqxDropDownButton('val');
						if(!val) return false;
						return true;
					}},
					{input : '#purchaseRate',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
						var val = input.jqxNumberInput('val');
						if(!val) return false;
						return true;
					}},
                    {input : '#conversionFactor',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
                        var val = input.jqxNumberInput('val');
                        if(!val) return false;
                        return true;
                    }},
                    {input : '#sellingRate',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
						var val = input.jqxNumberInput('val');
						if(!val) return false;
						return true;
					}},{input : '#uomId',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}},
					{input : '#uomIdTo',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}},
                    {input : '#fromDate',message : (uiLabelMap.FieldRequiredAccounting?uiLabelMap.FieldRequiredAccounting : ''),action : 'change,close,blur',rule : function(input){
                        var val = input.jqxDateTimeInput('val');
                        if(!val) return false;
                        return true;
                    }}
				]
			})
		};
		var save = function(){
			if(!$('#formAdd').jqxValidator('validate')){return;};
            var purchaseExchangeRate = $('#purchaseRate').val() + '';
            purchaseExchangeRate = purchaseExchangeRate.replace('.', ',');
            var conversionFactor = $('#conversionFactor').val() + '';
            conversionFactor = conversionFactor.replace('.', ',');
            var sellingExchangeRate = $('#sellingRate').val() + '';
            sellingExchangeRate = sellingExchangeRate.replace('.', ',');
				var row;
			        row = {
			        		fromDate: $('#fromDate').jqxDateTimeInput('getDate'),
			        		uomId:$('#uomId').val(),
			        		uomIdTo:$('#uomIdTo').val(),
			        		purposeEnumId:$('#purposeEnumId').jqxDropDownList('val'),
                            purchaseExchangeRate: purchaseExchangeRate,
                            conversionFactor: conversionFactor,
			        		sellingExchangeRate: sellingExchangeRate,
                            bankId : jOlbUtil.getAttrDataValue('bankId'),
			        		thruDate:  $('#thruDate').jqxDateTimeInput('getDate')
			        	  };
				   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			        // select the first row and clear the selection.
			        $("#jqxgrid").jqxGrid('clearSelection');                        
			        $("#jqxgrid").jqxGrid('selectRow', 0);  
			        return true;
			};
		
		var clear = function(){
			$('#fromDate').jqxDateTimeInput('val',null);
			$('#thruDate').jqxDateTimeInput('val',null);
            $('#purchaseRate').jqxNumberInput('clear');
            $('#conversionFactor').jqxNumberInput('clear');
			$('#sellingRate').jqxNumberInput('clear');
			$('#bankId').jqxDropDownList('clearSelection');
			$('#uomId').jqxDropDownList('clearSelection');
			$('#uomIdTo').jqxDropDownList('clearSelection');
			$('#purposeEnumId').jqxDropDownList('clearSelection');
			filterDate.resetDate();
			setTimeout(function(){
				$('#formAdd').jqxValidator('hide');
			},200)
		};
		
		var bindEvent = function(){
			$("#save").click(function () {
		    	if(save())  $("#alterpopupWindow").jqxWindow('close');
		    });
		    
		    $("#saveAndContinue").click(function () {
		    	save();
		    });
		    $("#alterpopupWindow").on('close',function(){
		    	clear();
		    });
		};
		
		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			}
		}
	}());

	$(document).ready(function(){
		action.init();
	});
    
