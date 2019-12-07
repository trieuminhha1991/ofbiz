	var listGender = [];
	var mapGender = {};
	var gridSelecting;
	$(document).ready(function() {
		mapGender = {"M": multiLang.male, "F": multiLang.female};
		listGender = [{value: "M", label: multiLang.male}, {value: "F", label: multiLang.female}];
		
		$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	});
	function getPersonAge(birthDate) {
		var birthYear = new Date(birthDate).getFullYear();
		var currentYear = new Date().getFullYear();
		var partyAge = currentYear - birthYear;
		if (partyAge < 0) {
			return "";
		} else if (partyAge < 2) {
			var birthMonth = new Date(birthDate).getMonth();
			var currentMonth = new Date().getMonth();
			partyAge = currentMonth - birthMonth + 1 + partyAge*12;
			return "<span class='green'> (" + partyAge + ") " + multiLang.DmsMonths;
		}
		partyAge += 1;
		return "<span class='green'> (" + partyAge + ") " + multiLang.age;
	}
	function deleteMember(rowData) {
		var result;
		var success = true;
		$.ajax({
			url: "removeMemberInFamily",
			type: "POST",
			data: {familyId: rowData.familyId, partyId: rowData.partyId, roleTypeIdFrom: rowData.roleTypeIdFrom},
			async: false,
			success: function(res) {
				result = res;
			}
		}).done(function() {
			$("#jqxNotificationNested").jqxNotification("closeLast");
			if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
				success = false;
				$("#jqxNotificationNested").jqxNotification({ template: "error"});
				$("#notificationContentNested").text(multiLang.updateError);
				$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: "info"});
				$("#notificationContentNested").text(multiLang.updateSuccess);
				$("#jqxNotificationNested").jqxNotification("open");
			}
		});
		return success;
	}
	var isBusinesesMode = false;
	function addMember(partyData, roleTypeId, roleType) {
		var rowIndexSelected = $("#jqxgrid").jqxGrid("getSelectedRowindex");
		var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowIndexSelected);
		var newdata = new Object();
		newdata.familyId = rowData.familyId;
		newdata.partyId = partyData.partyId;
		newdata.partyFullName = partyData.partyFullName;
		newdata.gender = partyData.gender;
		newdata.roleTypeFrom = roleType;
		newdata.roleTypeIdFrom = roleTypeId;
		newdata.birthDate = partyData.birthDate;
		newdata.idNumber = partyData.idNumber;
		newdata.contactNumber = partyData.primaryPhone;
		newdata.emailAddress = partyData.emailAddress;
		if ($("#jqxgridDetail" + rowIndexSelected).length > 0) {
			$("#jqxgridDetail" + rowIndexSelected).jqxGrid("addrow", rowData.uid, newdata);
		} else {
			addMemberAjax({partyId: partyData.partyId, familyId: rowData.familyId, roleTypeIdFrom: roleTypeId}, true);
		}
	}
	function addMemberAjax(partyData, updatebounddata) {
		var result;
		var success = true;
		$.ajax({
			url: "addMemberToFamily",
			type: "POST",
			data: partyData,
			async: false,
			success: function(res) {
				result = res;
			}
		}).done(function() {
			$("#jqxNotificationNested").jqxNotification("closeLast");
			if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
				success = false;
				$("#jqxNotificationNested").jqxNotification({ template: "error"});
				$("#notificationContentNested").text(multiLang.updateError);
				$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: "info"});
				$("#notificationContentNested").text(multiLang.updateSuccess);
				$("#jqxNotificationNested").jqxNotification("open");
				if (updatebounddata) {
					$("#jqxgrid").jqxGrid("updatebounddata");
				}
			}
		});
		return success;
	}
	function testReport() {
		var result;
		$.ajax({
			url: "testReport",
			type: "POST",
			data: {},
			async: false,
			success: function(res) {
				result = res["list"];
			}
		}).done(function() {
		});
	}
	function addMember2(familyId, gridId) {
		var xxx = $("#" + gridId).jqxGrid("addrow", 0, [{familyId: familyId}], "first");
		$("#" + gridId).jqxGrid("beginrowedit", 0);
		$("#saveMember2" + gridId).removeClass("hide");
	}
	function deleteMember2(partyId, gridId) {
		var rowIndexSelected = $("#" + gridId).jqxGrid("getSelectedRowindex");
		if (rowIndexSelected == -1) {
			bootbox.alert(ChooseMemberToDelete);
		} else {
			bootbox.confirm(multiLang.ConfirmDelete, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
				if (result) {
					var rowData = $("#" + gridId).jqxGrid("getrowdata", rowIndexSelected);
					$("#" + gridId).jqxGrid("deleterow", rowData.uid);
				}
			});
		}
	}
	function saveMember2(familyId, gridId) {
		saveData(gridId);
	}
	function saveData(gridId) {
		$("#" + gridId).attr("mode", "none");
		$("#addMemberTag" + gridId).html("<i class='fa-plus'></i>&nbsp;" + multiLang.DmsAddMember);
		var rows = $("#" + gridId).jqxGrid("getdisplayrows");
		for ( var x in rows) {
			rows[x].birthDate?rows[x].birthDate=new Date(rows[x].birthDate).getTime():rows[x].birthDate;
		}
		var result;
		$.ajax({
			url: "createOrStoreMember",
			type: "POST",
			data: {rows: JSON.stringify(rows)},
			dataType: "json",
			async: true,
			success: function(res) {
				result = res;
			}
		}).done(function() {
			$("#jqxNotificationNested").jqxNotification("closeLast");
			if (result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]) {
				$("#jqxNotificationNested").jqxNotification({ template: "error"});
				$("#notificationContentNested").text(multiLang.updateError);
				$("#jqxNotificationNested").jqxNotification("open");
				setTimeout(function () {
					location.reload();
				}, 1000);
			} else {
				$("#jqxNotificationNested").jqxNotification({ template: "info"});
				$("#notificationContentNested").text(multiLang.updateSuccess);
				$("#jqxNotificationNested").jqxNotification("open");
				$("#saveMember2" + gridId).addClass("hide");
			}
		});
	}