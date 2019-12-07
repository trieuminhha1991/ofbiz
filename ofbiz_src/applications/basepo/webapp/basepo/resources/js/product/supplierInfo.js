if (typeof (AddSupplier) == "undefined") {
	var AddSupplier = (function() {
		var initJqxElements = function() {
			$("#jqxwindowAddSupplier").jqxWindow({
				theme: "olbius", width: 950, maxWidth: 1200, height: 320, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#btnCancelSupplier"), modalOpacity: 0.7
			});
			$("#supplierAdd").jqxDropDownList({ source: partyRelationshipData, width: 218, height: 30, displayMember: 'groupName', valueMember: 'partyId', disabled: false, placeHolder: multiLang.filterchoosestring, autoDropDownHeight: false });
			$("#lastPrice").jqxNumberInput({theme: "olbius", width: 218, height: 30, decimalDigits: 0, min: 0 });
			$("#shippingPrice").jqxNumberInput({theme: "olbius", width: 218, height: 30, decimalDigits: 0, min: 0 });
			$("#productCurrencyUomId").jqxDropDownList({ source: [], displayMember: 'preferredCurrencyUomId', valueMember: 'preferredCurrencyUomId', width: 218, height: 30, theme: "olbius", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true });
			$("#minimumOrderQuantity").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: 218, height: 30, decimalDigits: 0, min: 1, decimal: 1 });
			$("#availableFromDate").jqxDateTimeInput({width: 218, height: 30, theme: "olbius"});
			$("#availableThruDate").jqxDateTimeInput({width: 218, height: 30, theme: "olbius"});
			$("#availableThruDate").jqxDateTimeInput('setDate', null);
			$("#canDropShip").jqxDropDownList({ source: canDropShipData, width: 218, height: 30, displayMember: 'description', valueMember: 'id' , disabled: false, placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true, selectedIndex: 0, disabled: true });
			
			setTimeout(function() {
				if (locale=="vi") {
					$("#lastPrice").jqxNumberInput({ decimalSeparator: ',', groupSeparator: "." });
					$("#shippingPrice").jqxNumberInput({ decimalSeparator: ',', groupSeparator: "." });
				}
			}, 50);
		};
		var handleEvents = function() {
			$('#supplierAdd').on('select', function (event) {     
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var label = item.label;
				    var value = item.value;
				    if (value) {
				    	loadCurrencyUomIdBySupplier(value);
					}
			    }
			});
			$('#jqxwindowAddSupplier').on('close', function (event) {
				$('#jqxwindowAddSupplier').jqxValidator('hide');
				$("#supplierAdd").jqxDropDownList('clearSelection');
				$("#lastPrice").jqxNumberInput('setDecimal', 0);
				$("#shippingPrice").jqxNumberInput('setDecimal', 0);
				$("#productCurrencyUomId").jqxDropDownList('clearSelection');
				$("#minimumOrderQuantity").jqxNumberInput('setDecimal', 1);
				$("#availableThruDate").jqxDateTimeInput('setDate', null);
				$("#canDropShip").jqxDropDownList('val', "N");
				$("#supplierProductId").val("");
				$("#comments").val("");
				$("#jqxwindowAddSupplier").data("uid", null);
			});
			$("#btnSaveSupplier").click(function() {
				if (AddSupplier.validate()) {
					var uid = $("#jqxwindowAddSupplier").data("uid");
					if (uid) {
						$('#jqxgridProductSuppliers').jqxGrid('updaterow', uid, AddSupplier.getValue());
					} else {
						$('#jqxgridProductSuppliers').jqxGrid('addrow', null, AddSupplier.getValue());
					}
					$("#jqxwindowAddSupplier").jqxWindow("close");
				}
			});
		};
		var initValidator = function() {
			$('#jqxwindowAddSupplier').jqxValidator({
			    rules: [{ input: '#supplierAdd', message: multiLang.fieldRequired, action: 'change', 
		                	rule: function (input, commit) {
		                		var value = input.jqxDropDownList('val');
		                		if (value) {
		                			return true;
		                		}
		                		return false;
		                	}
		                },
		                { input: '#minimumOrderQuantity', message: multiLang.DmsQuantityNotValid, action: 'valueChanged', 
		                	rule: function (input, commit) {
		                		var value = input.jqxNumberInput('getDecimal');
		                		if (value >= 0) {
		                			return true;
								}
		                		return false;
		                	}
		                },
		                { input: '#lastPrice', message: multiLang.DmsPriceNotValid, action: 'valueChanged', 
		                	rule: function (input, commit) {
		                		var value = input.jqxNumberInput('getDecimal');
		                		if (value >= 0) {
		                			return true;
		                		}
		                		return false;
		                	}
		                },
		                { input: '#productCurrencyUomId', message: multiLang.fieldRequired, action: 'change', 
		                	rule: function (input, commit) {
		                		var value = input.jqxDropDownList('val');
		                		if (value) {
		                			return true;
		                		}
		                		return false;
		                	}
		                },
		                { input: '#availableFromDate', message: multiLang.fieldRequired, action: 'valueChanged', 
		                	rule: function (input, commit) {
		                		var value = input.jqxDateTimeInput('getDate');
		                		if (value) {
		                			return true;
		                		}
		                		return false;
		                	}
		                },
		                { input: '#supplierProductId', message: multiLang.fieldRequired, action: 'keyup, blur', rule: 'required' },
		                { input: '#supplierProductId', message: multiLang.containSpecialSymbol, action: 'keyup, blur',
							rule: function (input, commit) {
								var value = input.val();
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									return true;
								}
								return false;
							}
						}],
		           position: 'bottom'
			});
		};
		var loadCurrencyUomIdBySupplier = function(partyId) {
			var data = DataAccess.getData({
						url: "loadCurrencyUomIdBySupplier",
						data: {partyId: partyId},
						source: "listProductCurrencyUomId"});
			$("#productCurrencyUomId").jqxDropDownList({ source: data });
		};
		var validate = function() {
			return $('#jqxwindowAddSupplier').jqxValidator('validate');
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#supplierAdd").jqxDropDownList('val', data.partyId);
				$("#productCurrencyUomId").jqxDropDownList('val', data.currencyUomId);
				$("#comments").val(data.comments);
				$("#supplierProductId").val(data.supplierProductId);
				$("#canDropShip").jqxDropDownList('val', data.canDropShip);
				if (data.availableFromDate) {
					if (data.availableFromDate.time) {
						$('#availableFromDate').jqxDateTimeInput('setDate', new Date(data.availableFromDate.time));
					} else if (data.availableFromDate) {
						$('#availableFromDate').jqxDateTimeInput('setDate', new Date(data.availableFromDate));
					}
				}
				if (data.availableThruDate) {
					if (data.availableThruDate.time) {
						$('#availableThruDate').jqxDateTimeInput('setDate', new Date(data.availableThruDate.time));
					} else if (data.availableThruDate) {
						$('#availableThruDate').jqxDateTimeInput('setDate', new Date(data.availableThruDate));
					}
				}
				$("#lastPrice").jqxNumberInput('setDecimal', data.lastPrice);
				$("#shippingPrice").jqxNumberInput('setDecimal', data.shippingPrice);
				$("#minimumOrderQuantity").jqxNumberInput('setDecimal', data.minimumOrderQuantity);
				
				$("#productCurrencyUomId").jqxDropDownList({ disabled: true });
				$('#availableFromDate').jqxDateTimeInput({disabled: true});
				$("#minimumOrderQuantity").jqxNumberInput({disabled: true});
			}
		};
		var getValue = function() {
			var value = new Object();
			value.partyId = $("#supplierAdd").jqxDropDownList('val');
			value.groupName = $("#supplierAdd").jqxDropDownList('getSelectedItem').label;
			value.currencyUomId = $("#productCurrencyUomId").jqxDropDownList('val');
			value.comments = $("#comments").val();
			value.supplierProductId = $("#supplierProductId").val();
			value.canDropShip = $("#canDropShip").jqxDropDownList('val');
			var availableFromDate;
			$('#availableFromDate').jqxDateTimeInput('getDate')?availableFromDate=$('#availableFromDate').jqxDateTimeInput('getDate').getTime():availableFromDate;
			value.availableFromDate = availableFromDate;
			var availableThruDate;
			$('#availableThruDate').jqxDateTimeInput('getDate')?availableThruDate=$('#availableThruDate').jqxDateTimeInput('getDate').getTime():availableThruDate;
			value.availableThruDate = availableThruDate;
			value.lastPrice = $("#lastPrice").jqxNumberInput('getDecimal');
			value.shippingPrice = $("#shippingPrice").jqxNumberInput('getDecimal');
			value.minimumOrderQuantity = $("#minimumOrderQuantity").jqxNumberInput('getDecimal');
			return value;
		};
		var setSupplier = function(data) {
			if (!_.isEmpty(data)) {
				$("#supplierAdd").jqxDropDownList({ source: data, selectedIndex: 0 });
			}
		};
		var open = function() {
			var wtmp = window;
			var tmpwidth = $("#jqxwindowAddSupplier").jqxWindow("width");
	        $("#jqxwindowAddSupplier").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowAddSupplier").jqxWindow("open");
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			validate: validate,
			setValue: setValue,
			getValue: getValue,
			setSupplier: setSupplier,
			open: open
		};
	})();
}