$(function() {
	pageCommonPopupAddAgreement.init();
});

var pageCommonPopupAddAgreement = (function() {
	var form = $("#formAdd");
	var popup = $("#alterpopupWindow");
	var partyFromChange = "";
	var partyToChange = "";
	var GridUtils = Grid;

	var openTextArea = function() {
		$("#textWindow").jqxWindow("open");
	}

	var initPartySelect = function(dropdown, grid, width) {
		var datafields = [ {
			name : "partyId",
			type : "string"
		}, {
			name : "groupName",
			type : "string"
		} ];
		var columns = [
				{
					text : uiLabelMap.PartyPartyId,
					datafield : "partyId",
					width : 120,
					pinned : true
				},
				{
					text : uiLabelMap.PartyGroupName,
					datafield : "groupName",
					cellsrenderer : function(row, columns, value, defaulthtml,
							columnproperties, rowdata) {
						return "<div class=\"custom - cell - grid\">" + value + "</div>";
					}
				} ];

		grid.on("rowselect", function(event) {
			var args = event.args;
			var row = args.row;
			$("#partyIdTo").val(row.partyId);
		});

		var config = {
			url : "jqxGetSupplierPartyId",
			dropdown : {
				width : "100%"
			},
			source : {
				cache : false,
				pagesize : 5
			},
			autorowheight : true,
			filterable : true,
			width : width ? width : 600
		};
		GridUtils.initDropDownButton(config, datafields, columns, null, grid,
				dropdown, "groupName");
	};

	var init = function() {
		initElement();
		initEvent();
		initRules();
	};

	var initElement = function() {
		$("#alterpopupWindow").jqxWindow({
			width : 900,
			height : 260,
			resizable : false,
			isModal : true,
			autoOpen : false,
			cancelButton : $("#cancel"),
			modalOpacity : 0.7
		});
		$("#textWindow").jqxWindow({
			width : 600,
			height : 300,
			resizable : true,
			isModal : true,
			autoOpen : false,
			cancelButton : $("#cancelText"),
			modalOpacity : 0.8,
			initContent : function() {
				$("#editText").jqxEditor({
					width : "100%",
					height : "90%",
					theme : "olbiuseditor"
				});
			}
		});
		initPartySelect($("#partyIdToAdd"), $("#jqxPartyToGrid"), 400);
		$("#agreementDateAdd").jqxDateTimeInput({
			height : "25px",
			width : "100%",
			formatString : "dd-MM-yyyy : HH:mm:ss",
			allowNullDate : true,
			value : null
		});
		$("#fromDateAdd").jqxDateTimeInput({
			height : "25px",
			width : "100%",
			formatString : "dd-MM-yyyy : HH:mm:ss",
			allowNullDate : true,
			value : null
		});
		$("#thruDateAdd").jqxDateTimeInput({
			height : "25px",
			width : "100%",
			formatString : "dd-MM-yyyy : HH:mm:ss",
			allowNullDate : true,
			value : null
		});
		$("#descriptionAdd").jqxInput({
			height : 20,
			width : "98%"
		});
	};

	var initEvent = function() {
		$("#alterpopupWindow").on("close", function() {
			clearForm();
		});

		$("#save").click(function() {
			if (!saveAgreementAction()) {
				return;
			}
			popup.jqxWindow("close");
		});
		$("#saveAndContinue").click(function() {
			if (!saveAgreementAction()) {
				return;
			}
		});
	};

	var clearForm = function() {
		$("#agreementDateAdd").jqxDateTimeInput("val", null);
		// $("#partyIdFromAdd").jqxDropDownButton("setContent","");
		$("#partyIdToAdd").jqxDropDownButton("setContent", "");
		$("#descriptionAdd").val("");
		$("#thruDateAdd").jqxDateTimeInput("val", null);
		$("#fromDateAdd").jqxDateTimeInput("val", null);
		$("#jqxPartyToGrid").jqxGrid("clearSelection");
		form.jqxValidator("hide");
		if (localStorage.getItem("objDate")) {
			var objDate = $.parseJSON(localStorage.getItem("objDate"));
			if (objDate) {
				setMinMaxDate($("#fromDateAdd"), objDate);
				setMinMaxDate($("#thruDateAdd"), objDate);
				setMinMaxDate($("#agreementDateAdd"), objDate);
			}
		}
	};

	var initRules = function() {
		form.jqxValidator({
			rules : [ {
				input : "#partyIdToAdd",
				message : uiLabelMap.CommonRequired,
				action : "change",
				rule : function(input, commit) {
					var value = $("#partyIdToAdd").val();
					if (!value)
						return false;
					return true;
				}
			}, {
				input : "#agreementDateAdd",
				message : uiLabelMap.CommonRequired,
				action : "change",
				rule : function(input, commit) {
					var value = $("#agreementDateAdd").jqxDateTimeInput("val");
					if (!value)
						return false;
					return true;
				}
			}, {
				input : "#fromDateAdd",
				message : uiLabelMap.CommonRequired,
				action : "change",
				rule : function(input, commit) {
					var value = $("#fromDateAdd").jqxDateTimeInput("val");
					if (!value)
						return false;
					return true;
				}
			} ]
		});
	};

	var saveAgreementAction = function() {
		if (!form.jqxValidator("validate")) {
			return false;
		}
		var agreementDateJS = "";
		if ($("#agreementDateAdd").jqxDateTimeInput("getDate") != undefined
				&& $("#agreementDateAdd").jqxDateTimeInput("getDate") != null) {
			agreementDateJS = new Date($("#agreementDateAdd").jqxDateTimeInput(
					"getDate").getTime());
		}
		var fromDateJS = "";
		if ($("#fromDateAdd").jqxDateTimeInput("getDate") != undefined
				&& $("#fromDateAdd").jqxDateTimeInput("getDate") != null) {
			fromDateJS = new Date($("#fromDateAdd").jqxDateTimeInput("getDate")
					.getTime());
		}
		var thruDateJS = "";
		if ($("#thruDateAdd").jqxDateTimeInput("getDate") != undefined
				&& $("#thruDateAdd").jqxDateTimeInput("getDate") != null) {
			thruDateJS = new Date($("#thruDateAdd").jqxDateTimeInput("getDate")
					.getTime());
		}
		var row = {
			partyIdFrom : $("#partyIdFrom").val(),
			partyIdTo : $("#partyIdTo").val(),
			roleTypeIdFrom : $("#roleTypeIdFrom").val(),
			roleTypeIdTo : $("#roleTypeIdTo").val(),
			agreementTypeId : $("#agreementTypeIdAdd").val(),
			description : $("#descriptionAdd").val(),
			// textData:$("#textDataAdd").jqxInput("val"),
			agreementDate : agreementDateJS,
			fromDate : fromDateJS,
			thruDate : thruDateJS
		// statusId:$("#statusIdAdd").val()
		};
		$("#jqxgrid").jqxGrid("addRow", null, row, "first");
		$("#jqxgrid").jqxGrid("clearSelection");
		$("#jqxgrid").jqxGrid("selectRow", 0);
		return true;
	};

	return {
		init : init
	}
}());

/* this function for init all grid use */
// var popupAction = (function(){
//			
// var bindEvent = function(){
// $("#saveAndContinue").click(function () {
// popupAction.save();
// popupAction.clear();
// });
//		    
// $("#textWindow").on("close",function(){
// $("#editText").jqxEditor("val","");
// });
//		     
// $("#addText").click(function(){
// var text = $("#editText").jqxEditor("val");
// $("#descriptionAdd").jqxInput("val",text);
// $("#textWindow").jqxWindow("close");
// $("#descriptionAdd").jqxTooltip({content :
// $("#descriptionAdd").jqxInput("val"),disabled : false});
// });
// $("#descriptionAdd").on("change",function(){
// $("#descriptionAdd").jqxTooltip({content :
// $("#descriptionAdd").jqxInput("val")});
// })
// $("#fromDateAdd").on("change",function(){
// var dateTmp = $("#fromDateAdd").jqxDateTimeInput("getDate");
// if(dateTmp && dateTmp != null && typeof(dateTmp) != "undefined"){
// $("#thruDateAdd").jqxDateTimeInput("setMinDate",new Date(dateTmp.getYear() +
// 1900,dateTmp.getMonth(),dateTmp.getDate() + 1));
// $("#agreementDateAdd").jqxDateTimeInput("setMaxDate",new
// Date(dateTmp.getYear() + 1900,dateTmp.getMonth(),dateTmp.getDate()));
// }
// });
// $("#thruDateAdd").on("change",function(){
// var dateTmp = $("#thruDateAdd").jqxDateTimeInput("getDate");
// if(dateTmp && dateTmp != null && typeof(dateTmp) != "undefined"){
// $("#fromDateAdd").jqxDateTimeInput("setMaxDate",new Date(dateTmp.getYear() +
// 1900,dateTmp.getMonth(),dateTmp.getDate() - 1));
// }
// });
// $("#agreementDateAdd").on("change",function(){
// var dateTmp = $("#agreementDateAdd").jqxDateTimeInput("getDate");
// if(dateTmp && dateTmp != null && typeof(dateTmp) != "undefined"){
// $("#fromDateAdd").jqxDateTimeInput("setMinDate",new Date(dateTmp.getYear() +
// 1900,dateTmp.getMonth(),dateTmp.getDate()));
// }
// });
// popup.on("close",function(){
// clearForm();
// $("#descriptionAdd").jqxTooltip({disabled : true});
// })
// };
// var setMinMaxDate = function(object,date){
// object.jqxDateTimeInput("setMinDate",date.min);
// object.jqxDateTimeInput("setMaxDate",date.max);
// }
// var getRolesByParty = function (role, partyId, listRole){
// var jsc = Array();
// $.ajax({
// url: "getListRoleBelongCondition",
// type: "POST",
// dataType: "json",
// data: {partyId:partyId,listRoleCondition:listRole},
// success: function (data) {
// jsc = data.listRoleBelong;
// renderRole(role, jsc);
// }
// });
// };
// var renderRole = function(role, data){
// role.jqxDropDownList("source", data);
// };
// return {
// init : function(){
// initProductGrid();
// initElementGrid();
// initWindow();
// bindEvent();
// initRules();
// },
// save : saveAction,
// clear: clearForm,
// getRolesByParty : getRolesByParty
// };
// }());
