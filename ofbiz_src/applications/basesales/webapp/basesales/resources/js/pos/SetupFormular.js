var setupFormularObject = (function() {
	var sourceInput = [ "qoh", "qoo", "qtyL", "qtyS", "pqdL", "qpdS", "lidL",
			"lidS", "lastSold", "lastReceived", "shortPeriod", "longPeriod",
			"if", "(", ")", "==", "!=", ">", ">=", "<", "<=", "?", ":" ];
	var operationList = [ "if", "(", ")", "==", "!=", ">", ">=", "<", "<=",
			"?", ":" ];
	var operandList = [ "qoh", "qoo", "qtyL", "qtyS", "pqdL", "qpdS", "lidL",
			"lidS", "lastSold", "lastReceived", "shortPeriod", "longPeriod" ];
	var operandListGrid = [];
	var shortPeriod = null;
	var longPeriod = null;
	var formularOrigin = null;
	var initSourceOperand = function() {
		var qoh = {};
		qoh.operandId = "qoh";
		qoh.description = uiLabelMap.SettingTooltipQOH;
		operandListGrid[0] = qoh;

		var qoo = {};
		qoo.operandId = "qoo";
		qoo.description = uiLabelMap.SettingTooltipQOO;
		operandListGrid[1] = qoo;

		var qtyL = {};
		qtyL.operandId = "qtyL";
		qtyL.description = uiLabelMap.SettingQtyLHint;
		operandListGrid[2] = qtyL;

		var qtyS = {};
		qtyS.operandId = "qtyS";
		qtyS.description = uiLabelMap.SettingQtySHint;
		operandListGrid[3] = qtyS;

		var pqdL = {};
		pqdL.operandId = "pqdL";
		pqdL.description = uiLabelMap.SettingQPDLHint;
		operandListGrid[4] = pqdL;

		var qpdS = {};
		qpdS.operandId = "qpdS";
		qpdS.description = uiLabelMap.SettingQPDSHint;
		operandListGrid[5] = qpdS;

		var lidL = {};
		lidL.operandId = "lidL";
		lidL.description = uiLabelMap.SettingLIDLHint;
		operandListGrid[6] = lidL;

		var lidS = {};
		lidS.operandId = "lidS";
		lidS.description = uiLabelMap.SettingLIDSHint;
		operandListGrid[7] = lidS;

		var lastSold = {};
		lastSold.operandId = "lastSold";
		lastSold.description = uiLabelMap.SettingLastsold;
		operandListGrid[8] = lastSold;

		var lastReceived = {};
		lastReceived.operandId = "lastReceived";
		lastReceived.description = uiLabelMap.SettingLastReceived;
		operandListGrid[9] = lastReceived;
	};

	var getPeirodLength = function() {
		$.ajax({
			url : 'getPeriodLength',
			type : 'post',
			success : function(data) {
				getResultOfGetPeriodLength(data);
			},
			error : function(data) {
				getResultOfGetPeriodLength(data);
			}
		});
	};
	var getResultOfGetPeriodLength = function(data) {
		var serverError = commonObject.getServerError(data);
		if (serverError != "") {
			commonObject.showNotification(serverError, 'error');
		} else {
			/*
			 * commonObject.showNotification(uiLabelMap.SettingUpdateSuccess,
			 * 'success');
			 */
			/* updateData(); */
			if (data.shortPeriod) {
				shortPeriod = data.shortPeriod;
			}
			if (data.longPeriod) {
				longPeriod = data.longPeriod;
			}
		}
	};
	var formular = [];
	var init = function() {
		checkStatus();
		getPeirodLength();
		initObject();
		validateSetupPeriodTimeWindow();
		initEvent();
	};
	var initObject = function() {
		jqxNotificationObject.initJqxNotification();
		jqxWindowObject.createJqxWindow("setupPeriodTimeWindow", 450, 170,
				initInputTimePeriod, null);
		jqxWindowObject.createJqxWindow("setupFormularWindow", 850, 500,
				initObjectFormularWindow, null);
	};
	var initObjectFormularWindow = function() {
		/* initSplitter(); */
		/* initEditorFormular(); */
		initInputFormular();
		initJqxGridOperand();
		initValueFormular();
	};
	/*
	 * var initEditorFormular = function(){ var config = { tools: '', pasteMode:
	 * 'text' }; jqxEditorObject.initJqxEditor("jqxEditorFormular", null,
	 * 100,config); };
	 */
	var initJqxGridOperand = function() {
		initSourceOperand();
		var dataField = [ {
			name : 'operandId',
			type : 'string'
		}, {
			name : 'description',
			type : 'string'
		} ];
		var column = [ {
			text : uiLabelMap.SettingOperand,
			datafield : 'operandId',
			width : 150
		}, {
			text : uiLabelMap.SettingDescription,
			datafield : 'description'
		} ];
		var grid = $("#jqxGridOperand");
		var config = {
			datatype : "array",
			theme : 'olbius',
			showtoolbar : false,
			filterable : false,
			width : '100%',
			virtualmode : false,
			editable : false,
			localization : getLocalization(),
			source : {
				pagesize : 15,
				id : 'jqxGridOperand',
				localdata : operandListGrid
			}
		};
		Grid.initGrid(config, dataField, column, null, grid);
	};

	var initInputFormular = function() {
		var config = {
			source : function(query, response) {
				var item = query.split(" ").pop();
				$("#jqxInputFormular").jqxInput({
					query : item
				});
				response(sourceInput);
			},
			renderer : function(itemValue, inputValue) {
				var terms = inputValue.split(" ");
				terms.pop();
				terms.push(itemValue);
				terms.push(" ");
				var value = terms.join(" ");
				value = value.toUpperCase();
				return value;
			},
			minLength : 1
		}
		jqxInputObject.initJqxInput("jqxInputFormular", "99%", 70, config);
		$("#jqxInputFormular").jqxInput({
			theme : ''
		});
	}
	var dataFieldDetail = [ {
		name : 'productId',
		type : 'string'
	}, {
		name : 'facilityId',
		type : 'string'
	}, {
		name : 'facilityName',
		type : 'string'
	}, {
		name : 'qoh',
		type : 'number'
	}, {
		name : 'qoo',
		type : 'number'
	}, {
		name : 'qtyL',
		type : 'number'
	}, {
		name : 'qtyS',
		type : 'number'
	}, {
		name : 'qpdL',
		type : 'number'
	}, {
		name : 'qpdS',
		type : 'number'
	}, {
		name : 'lidL',
		type : 'number'
	}, {
		name : 'lidS',
		type : 'number'
	}, {
		name : 'lastSold',
		type : 'date',
		other : 'Timestamp'
	}, {
		name : 'lastReceived',
		type : 'date',
		other : 'Timestamp'
	}, {
		name : 'status',
		type : 'string'
	} ];
	var columnDetail = [
			{
				text : uiLabelMap.SettingFacilityId,
				datafield : 'facilityId',
				width : 100
			},
			{
				text : uiLabelMap.SettingFacilityName,
				datafield : 'facilityName',
				width : 150
			},
			{
				text : uiLabelMap.SettingQOH_PO,
				datafield : 'qoh',
				width : 100,
				columntype : 'numberinput',
				filtertype : 'number',
				cellsrenderer : function(row, column, value, a, b, data) {
					return '<div style=\"margin:4px;text-align: right;\">'
							+ value.toLocaleString(locale) + '</div>';
				}
			},
			{
				text : uiLabelMap.SettingQOO,
				datafield : 'qoo',
				width : 100,
				columntype : 'numberinput',
				filtertype : 'number',
				cellsrenderer : function(row, column, value, a, b, data) {
					return '<div style=\"margin:4px;text-align: right;\">'
							+ value.toLocaleString(locale) + '</div>';
				}
			},
			{
				text : uiLabelMap.SettingQTYL,
				datafield : 'qtyL',
				width : 100,
				columntype : 'numberinput',
				filtertype : 'number',
				cellsrenderer : function(row, column, value, a, b, data) {
					return '<div style=\"margin:4px;text-align: right;\">'
							+ value.toLocaleString(locale) + '</div>';
				}
			},
			{
				text : uiLabelMap.SettingQPDL,
				datafield : 'qpdL',
				width : 100,
				columntype : 'numberinput',
				filtertype : 'number',
				cellsrenderer : function(row, column, value, a, b, data) {
					return '<div style=\"margin:4px;text-align: right;\">'
							+ value.toLocaleString(locale) + '</div>';
				}
			},
			{
				text : uiLabelMap.SettingLIDL,
				datafield : 'lidL',
				width : 100,
				columntype : 'numberinput',
				filtertype : 'number',
				cellsrenderer : function(row, column, value, a, b, data) {
					return '<div style=\"margin:4px;text-align: right;\">'
							+ value.toLocaleString(locale) + '</div>';
				}
			},
			{
				text : uiLabelMap.SettingQTYS,
				datafield : 'qtyS',
				width : 100,
				columntype : 'numberinput',
				filtertype : 'number',
				cellsrenderer : function(row, column, value, a, b, data) {
					return '<div style=\"margin:4px;text-align: right;\">'
							+ value.toLocaleString(locale) + '</div>';
				}
			},
			{
				text : uiLabelMap.SettingQPDS,
				datafield : 'qpdS',
				width : 100,
				columntype : 'numberinput',
				filtertype : 'number',
				cellsrenderer : function(row, column, value, a, b, data) {
					return '<div style=\"margin:4px;text-align: right;\">'
							+ value.toLocaleString(locale) + '</div>';
				}
			},
			{
				text : uiLabelMap.SettingLIDS,
				datafield : 'lidS',
				width : 100,
				columntype : 'numberinput',
				filtertype : 'number',
				cellsrenderer : function(row, column, value, a, b, data) {
					return '<div style=\"margin:4px;text-align: right;\">'
							+ value.toLocaleString(locale) + '</div>';
				}
			}, {
				text : uiLabelMap.SettingLastsold,
				datafield : 'lastSold',
				width : '150',
				filtertype : 'range',
				cellsformat : 'dd/MM/yyyy'
			}, {
				text : uiLabelMap.SettingLastReceived,
				datafield : 'lastReceived',
				width : '150',
				filtertype : 'range',
				cellsformat : 'dd/MM/yyyy'
			}, {
				text : uiLabelMap.SettingStatus,
				datafield : 'status'
			} ];

	var initRowDetail = function(index, parentElement, gridElement, datarecord) {
		var productId = datarecord.productId;
		var urlStr = 'JQGetListProductFacilitySummary&productId=' + productId;
		var id = datarecord.uid.toString();
		var grid = $($(parentElement).children()[0]);
		$(grid).attr("id", productId + "jqxgridRowDetail");
		var config = {
			url : urlStr,
			width : '95%',
			autoheight : true,
			showtoolbar : false,
			editable : false,
			editmode : "click",
			showheader : true,
			selectionmode : "singlecell",
			theme : theme,
			pageable : false,
			localization : getLocalization()
		};
		Grid.initGrid(config, dataFieldDetail, columnDetail, null, grid);
	};

	var initInputTimePeriod = function() {
		var config = {
		/*
		 * spinButtons : true, min: 0
		 */
		};
		jqxNumberInputObject
				.initJqxNumberInput("longPeriod", '99%', 25, config);
		jqxNumberInputObject.initJqxNumberInput("shortPeriod", '99%', 25,
				config);
		initValueForTimePeriod();
	};
	var initValueForTimePeriod = function() {
		$('#longPeriod').jqxNumberInput('val', longPeriod);
		$('#shortPeriod').jqxNumberInput('val', shortPeriod);
	};
	var openFormularWindow = function() {
		$('#setupFormularWindow').jqxWindow('open');
	};
	var closeFormularWindow = function() {
		$('#setupFormularWindow').jqxWindow('close');
	};
	var openTimePeriodWindow = function() {
		$('#setupPeriodTimeWindow').jqxWindow('open');
	};
	var closeTimePeriodWindow = function() {
		$('#setupPeriodTimeWindow').jqxWindow('close');
	};
	var resetInputTimePeriod = function() {
		initValueForTimePeriod();
	};
	var initEvent = function() {
		$("#cancelTimePeriodButton").on('click', function() {
			resetInputTimePeriod();
		});
		$("#createTimePeriodButton").on('click', function() {
			createTimePeriod();
		});
		$("#createFormularButton").on('click', function() {
			createFormular();
		});
		$("#cancelFormularButton").on('click', function() {
			initValueFormular();
		});
		$('#setupFormularWindow').on('open', function(event) {
			/* $("#jqxInputFormular").val(""); */
			initValueFormular();
		});
		/* changeUpperCase(); */
	};
	var validateSetupPeriodTimeWindow = function() {
		$('#setupPeriodTimeWindow').jqxValidator({
			rules : [ {
				input : '#longPeriod',
				message : uiLabelMap.SettingDataNotValid,
				action : 'keyup, blur',
				rule : function(input, commit) {
					var longPeriod = input.jqxNumberInput('val');
					if (longPeriod > 0) {
						return true;
					} else {
						return false;
					}
				}
			}, {
				input : '#shortPeriod',
				message : uiLabelMap.SettingDataNotValid,
				action : 'keyup, blur',
				rule : function(input, commit) {
					var shortPeriod = input.jqxNumberInput('val');
					if (shortPeriod > 0) {
						return true;
					} else {
						return false;
					}
				}
			} ]
		});
	};
	var changeUpperCase = function() {
		$('#jqxInputFormular').keyup(function() {
			this.value = this.value.toUpperCase();
			this.focus();
		});
	};
	var createTimePeriod = function() {
		var param = {};
		var shortPeriod = $("#shortPeriod").val();
		var longPeriod = $("#longPeriod").val();
		param.shortPeriod = shortPeriod;
		param.longPeriod = longPeriod;
		$
				.ajax({
					url : 'updateTimePeriod',
					data : param,
					type : 'post',
					beforeSend : function() {
						$("#loader_page_common").show();
						$("#createTimePeriodButton").attr("disabled",
								"disabled");
						$("#cancelTimePeriodButton").attr("disabled",
								"disabled");
					},
					success : function(data) {
						getResultOfCreateTimePeriod(data);
					},
					error : function(data) {
						getResultOfCreateTimePeriod(data);
					},
					complete : function(data) {
						$("#loader_page_common").hide();
						$("#createTimePeriodButton").removeAttr("disabled",
								"disabled");
						$("#cancelTimePeriodButton").removeAttr("disabled",
								"disabled");
					}
				});
	};

	var getResultOfCreateTimePeriod = function(data) {
		closeTimePeriodWindow();
		var serverError = commonObject.getServerError(data);
		if (serverError != "") {
			commonObject.showNotification(serverError, 'error');
		} else {
			commonObject.showNotification(uiLabelMap.SettingUpdateSuccess,
					'success');
			updateData();
			var title = uiLabelMap.SettingLastUpdate;
			title += " : " + data.lastUpdate;
			commonObject.setTitleGrid("jqxGridFormular", title);
		}
	};
	var updateData = function() {
		$("#jqxGridFormular").jqxGrid('updatebounddata');
	};
	var checkOneWord = function(str) {
		str = str.toUpperCase();
		var flag = false; // default is not valid
		var lastIndex = formular.length - 1;
		var topStack = formular[lastIndex];
		if (typeof topStack == "undefined") {
			flag = true;
		} else {
			if (str != ")") {
				if (topStack == "IF") {
					if (str == "(") {
						flag = true;
					}
				} else if (topStack == "(") {
					if (str == "IF") {
						flag = true;
					} else {
						for (var index = 0; index < operandList.length; index++) {
							var tmp = operandList[index];
							if (str == tmp.toUpperCase()) {
								flag = true;
								// break;
							}
						}
					}
				} else if (topStack == ">" || topStack == "<"
						|| topStack == ">=" || topStack == "<="
						|| topStack == "==" || topStack == "!=") {
					if (isNaN(str)) {
						for (var index = 0; index < operandList.length; index++) {
							var tmp = operandList[index];
							if (str == tmp.toUpperCase()) {
								flag = true;
								// break;
							}
						}
					} else {
						flag = true;
					}
				} else if (topStack == "QOH" || topStack == "QOO"
						|| topStack == "QTYS" || topStack == "QTYL"
						|| topStack == "QPDL" || topStack == "LIDL"
						|| topStack == "QPDS" || topStack == "LIDS"
						|| topStack == "LASTSOLD" || topStack == "LASTRECEIVED"
						|| topStack == "OLBIUS_POS") {
					for (var index = 0; index < operationList.length; index++) {
						var tmp = operationList[index];
						if (str == tmp.toUpperCase()) {
							flag = true;
							// break;
						}
					}
				} else if (topStack == "?" || topStack == ":") {
					// there is topStack == "?" || topStack == ":"
					if (str == "(" || str == "IF") {
						flag = true;
					} else {
						var first = str.charAt(0);
						var tmp = str.length - 1;
						var last = str.charAt(tmp);
						if (first == '"' && last == '"') {
							flag = true;
						} else {
							flag = false;
						}
					}
				} else {
					for (var index = 0; index < operationList.length; index++) {
						var tmp = operationList[index];
						if (str == tmp.toUpperCase()) {
							flag = true;
							// break;
						}
					}
				}
			}
		}
		return flag;
	};
	var validateInput = function(str) {
		var flag = false; // default is not validate
		var strTmp = str.split(" ");
		if (strTmp.length == 1) {
			flag = checkOneWord(str);
		} else {
			// default operator
			for (var index = 0; index < operatorList.length; index++) {
				var tmp = operatorList[index];
				flag = checkOneWord(tmp);
				if (!flag) {
					break;
				}
			}
		}
		return flag;
	};
	var checkOrderOperaHand = function(str) {
		var formularLength = formular.length;
		var flag = false; // default is not valid
		if (str.toUpperCase() == "?") {
			var top = formularLength - 1;
			var topStack = formular[top];
			if (topStack == "OLBIUS_POS") {
				flag = true;
			} else {
				flag = false;
			}
		} else if (str.toUpperCase() == ":") {
			var tmpLength = formularLength - 2;
			var tmp = formular[tmpLength];
			if (tmp == "?" || tmp == "IF") {
				flag = true;
			} else {
				flag = false;
			}
		} else {
			flag = true;
		}
		return flag;
	};

	var operatorList = [ "qoh", "qoo", "qtyL", "qtyS", "pqdL", "qpdS", "lidL",
			"lidS", "lastSold", "lastReceived", "?", ":", ">", ">=", "<", "<=",
			"==", "!=" ];
	var checkPushIntoStack = function(str) {
		var flag = false; // default is not valid
		if (validateInput(str) || str.toUpperCase() == "IF"
				|| str.toUpperCase() == "(") {
			if (checkOrderOperaHand(str)) {
				flag = true;
			}
		}
		return flag;
	};

	var popOutStack = function() {
		var flag = false;
		while (!flag) {
			var popStack = formular.pop();
			if (typeof popStack != "undefined") {
				if (popStack == "(") {
					flag = true;
					break;
				}
			} else {
				break;
			}
		}
		return flag;
	};
	/*
	 * var separateFormular = function(str){ var result = []; var pattern = new
	 * RegExp("if"); var tmpObj = str.match(pattern); if(tmpObj != null){ var
	 * index = } };
	 */

	var lastCheckFormular = function() {
		var flag = true; // default is validate
		// cuoi cung thi trong stack cung chi con 6 phan tu
		if (formular.length != 6) {
			flag = false;
		} else {
			for (var index = 0; index < formular.length; index++) {
				var tmp = formular[index];
				if (tmp == "(") {
					flag = false;
					break;
				}
			}
		}
		return flag;
	};
	var validateFormular = function(str) {
		var flag = true;
		if (str.length > 0) {
			var strTmp = str.split(" ");
			var lastIndex = strTmp.length - 1;
			for (var i = 0; i < strTmp.length; i++) {
				var tmp = strTmp[i];
				if (tmp != " " && tmp != "") {
					tmp = tmp.toUpperCase();
					if (checkPushIntoStack(tmp)) {
						formular.push(tmp);
					} else {
						if (tmp == ")") {
							var flagTmp = popOutStack();
							if (flagTmp) {
								// add pos (default into stack)
								formular.push("OLBIUS_POS");
							} else {
								flag = false;
								break;
							}
						} else {
							flag = false;// not validate
							break;
						}
					}
				}
			}
			// kiem tra lai lan nua de xem stack con chua if hoac ( hay khong.
			// Neu co chua thi la cong thuc khong hop le
			flag = lastCheckFormular();
		} else {
			flag = false;
		}
		return flag;
	};

	/*
	 * var validateElement = function(str, seperate){ var flag = true; //defautl
	 * is validate var strRight = str.subString(lastIndex, str.length);
	 * if(!validateOperator(strRight)){ flag = false; } if(flag){ var strLeft =
	 * str.subString(0, lastIndex); return validateFormular(strLeft); } return
	 * flag; };
	 * 
	 * var validateFormular = function(str){
	 * 
	 * var lastIndex = str.lastIndexOf(":"); //process for : if(lastIndex >0){
	 * validateElement(str, ":"); }else{ //process for ? var lastIndex1 =
	 * str.lastIndexOf("?"); var strLeft = str.subString(0, lastIndex1);
	 * if(lastIndex1 > 0){ validateElement(strLeft, "?"); }else{ //process for
	 * if } } };
	 */
	var initValueFormular = function() {
		$("#jqxInputFormular").val(formularOrigin);
	};
	var prepareProcess = function(str) {
		str = str.toUpperCase();
		str = str.replace(/IF/g, " IF ");
		str = str.replace(/\(/g, " ( ");
		str = str.replace(/>/g, " > ");
		str = str.replace(/>=/g, " >= ");
		str = str.replace(/</g, " > ");
		str = str.replace(/<=/g, " >= ");
		str = str.replace(/==/g, " == ");
		str = str.replace(/!=/g, " != ");
		str = str.replace(/\)/g, " ) ");
		str = str.replace(/\?/g, " ? ");
		str = str.replace(/:/g, " : ");
		/* str = str.replace(/=/g, " = "); */
		return str;
	};
	var createFormular = function() {
		resetFormular();
		var formularStr = $("#jqxInputFormular").val();
		var str = prepareProcess(formularStr);
		var flag = validateFormular(str);
		if (flag) {
			var param = {};
			param.formular = str;
			$.ajax({
				url : 'updateProductSummary',
				data : param,
				type : 'post',
				beforeSend : function() {
					$("#loader_page_common").show();
					$("#createFormularButton").attr("disabled", "disabled");
					$("#cancelFormularButton").attr("disabled", "disabled");
				},
				success : function(data) {
					getResultOfCreateFormular(data);
				},
				error : function(data) {
					getResultOfCreateFormular(data);
				},
				complete : function(data) {
					$("#loader_page_common").hide();
					$("#createFormularButton").removeAttr("disabled",
							"disabled");
					$("#cancelFormularButton").removeAttr("disabled",
							"disabled");
				}
			});
		} else {
			bootbox.alert(uiLabelMap.SettingFormularIsNotValid);
		}
	};
	var getResultOfCreateFormular = function(data) {
		closeFormularWindow();
		var serverError = commonObject.getServerError(data);
		if (serverError != "") {
			commonObject.showNotification(serverError, 'error');
		} else {
			commonObject.showNotification(uiLabelMap.SettingUpdateSuccess,
					'success');
			updateData();
			var title = uiLabelMap.SettingLastUpdate;
			title += " : " + data.lastUpdate;
			commonObject.setTitleGrid("jqxGridFormular", title);
			var tmpFormular = data.formular;
			if (typeof tmpFormular == "undefined") {
				formularOrigin = tmpFormular;
			}
		}
	};
	var checkStatus = function() {
		$.ajax({
			url : 'getProductSummaryServiceInfo',
			type : 'post',
			success : function(data) {
				getResultOfCheckStatus(data);
			},
			error : function(data) {
				getResultOfCheckStatus(data);
			}
		});
	};
	var getResultOfCheckStatus = function(data) {
		var serverError = commonObject.getServerError(data);
		if (serverError != "") {
			commonObject.showNotification(serverError, 'error');
		} else {
			var status = data.status;
			if (status == "PROCESSING") {
				$("#loader_page_common").show();
				setTimeout(checkStatus, 10000);
			} else {
				$("#loader_page_common").hide();
				updateData();
				var title = uiLabelMap.SettingLastUpdate;
				var lastUpdate = data.lastUpdate;
				if (typeof lastUpdate != "undefined") {
					title += " : " + data.lastUpdate;
				}
				var tmpFormular = data.formular;
				if (typeof tmpFormular == "undefined") {
					formularOrigin = "";
				} else {
					formularOrigin = tmpFormular;
				}
				commonObject.setTitleGrid("jqxGridFormular", title);
			}
		}
	};
	var resetFormular = function() {
		formular = [];
	};
	return {
		init : init,
		openTimePeriodWindow : openTimePeriodWindow,
		openFormularWindow : openFormularWindow,
		initRowDetail : initRowDetail
	}
}());
$(document).ready(function() {
	setupFormularObject.init();
});
