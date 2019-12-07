$(document).ready(function() {
	$("#listContact").tabs();
});
var issueForm, changeStateForm, emailForm;
var jqxid, ck;
var previous = "CONTACT";
function getDataEditor(key) {
	if (ck[key]) {
		return ck[key].getData();
	}
	return "";
}

function setDataEditor(key, content) {
	if (ck[key]) {
		return ck[key].setData(key, content);
	}
}

function rowselectfunction(event) {
	var args = event.args;
	var row = args.rowindex;
	jqxid = event.target.id;
	var key = "currentEmail-" + jqxid;
	var key2 = "currentCustomer-" + jqxid;
	localStorage.removeItem(key);
	localStorage.removeItem(key2);
	var widget = $('#' + jqxid);
	var currentEmail = [];
	var currentId = [];
	if (row.length) {
		for (var x in row) {
			var data = widget.jqxGrid('getrowdata', row[x]);
			currentEmail = currentEmail.concat(getEmail(data));
			currentId.push(data);
		}
	} else {
		var selected = widget.jqxGrid('getselectedrowindexes');
		for (var x in selected) {
			var data = widget.jqxGrid('getrowdata', selected[x]);
			currentEmail = currentEmail.concat(getEmail(data));
			currentId.push(data);
		}
	}
	if (currentEmail.length) {
		localStorage.setItem(key, JSON.stringify(currentEmail));
		localStorage.setItem(key2, JSON.stringify(currentId));
	}
}

function rowunselectfunction(event) {
	var args = event.args;
	var row = args.rowindex;
	jqxid = event.target.id;
	var key = "currentEmail-" + jqxid;
	var widget = $('#' + jqxid);
	var currentEmail = $.parseJSON(localStorage.getItem(key));
	var email = widget.jqxGrid('getrowdata', row);
	if (currentEmail) {
		for (var x in currentEmail) {
			if (currentEmail[x] == email.infoString) {
				currentEmail.splice(x, 1);
				localStorage.setItem(key, JSON.stringify(currentEmail));
				return;
			}
		}
	}

}

function getEmail(data) {
	var cur = [];
	if (data.infoString) {
		var res = data.infoString;
		if (res.length > 1) {
			for (var y in res) {
				cur.push(res[y]);
			}
		} else {
			cur.push(res[0]);
		}
	}
	return cur;
}

function editPopup(id, row) {
	var data = $(id).jqxGrid('getrowdata', row);
	$("#editContactForm").modal("show");
}

function moreDetail(id, row) {
	var data = $(id).jqxGrid('getrowdata', row);
	if (data && data.partyId) {
		$.ajax({
			url : "getCustomerCommunication",
			type : "POST",
			data : {
				partyId : data.partyId
			},
			success : function(res) {
				if (res.status == "success") {
					renderActivity(res.results);
					$("#detail").modal("show");
				}
			},
			error : function() {

			}
		});
	}
}

function renderActivity(data) {
	var str = "";
	var sender = uiLabelMap && uiLabelMap.sender ? uiLabelMap.sender : "Sender";
	var receiver = uiLabelMap && uiLabelMap.receiver ? uiLabelMap.receiver : "Receiver"; 
	var inTime = uiLabelMap && uiLabelMap.InTime ? uiLabelMap.InTime : "In";
	var type = uiLabelMap && uiLabelMap.communicationType ? uiLabelMap.communicationType : "Communication Type";
	if (data && data.length) {
		for (var x in data) {
			var activity = data[x];
			str += "<div class='row-fluid event-row'>"
				+ "<div class='span12'>"
				+ "<b>"+ sender +": &nbsp;</b>"
				+ getName(activity, "firstNameFrom", "middleNameFrom", "lastNameFrom", "groupNameFrom")
				+ "&nbsp;-&nbsp;<b>&nbsp;" + receiver + ":&nbsp;</b>"
				+ getName(activity, "firstNameTo", "middleNameTo", "lastNameTo", "groupNameTo")
				+ "&nbsp;<b>" + inTime + "</b>:&nbsp;" 
				+ renderDatetime(activity.entryDate)
				+ "</div>"
				+ "<div class='span12'>"
				+ type + ": " + activity.communicationEventTypeId
				+ "</div>"
				+ "<div class='span12'>"
				+ activity.content
				+ "</div>"
				+ "</div>";
		}
	} else {
		str += "No history!";
	}
	$("#detail-activity").html(str);
}

function getName(activity, firstName, middleName, lastName, groupName) {
	var str = "";
	if (activity[firstName] || activity[middleName] || activity[lastName]) {
		var option = locale ? locale : "en";
		switch(option) {
			case "vi":
				if (activity[lastName]) {
					str += "<span>" + activity[lastName] + "&nbsp;</span>";
				}
				if (activity[middleName]) {
					str += "<span>" + activity[middleName] + "&nbsp;</span>";
				}
				if (activity[firstName]) {
					str += "<span>" + activity[firstName] + "</span>";
				}
				break;
			default: 
				if (activity[firstName]) {
					str += "<span>" + activity[firstName] + "&nbsp;</span>";
				}
				if (activity[middleName]) {
					str += "<span>" + activity[middleName] + "&nbsp;</span>";
				}
				if (activity[lastName]) {
					str += "<span>" + activity[lastName] + "</span>";
				}
				break;
		}
		
	} else if (activity[groupName]) {
		str += "<span>" + activity[groupName] + "</span>";
	}
	return str;
}

function renderDatetime(date) {
	var str = "";
	if (date) {
		var h = date.hours < 10 ? ("0"+ date.hours) : date.hours;
		var min = date.minutes < 10 ? ("0"+ date.minutes) : date.minutes;
		var sec = date.seconds < 10 ? ("0"+ date.seconds) : date.seconds;
		var d = date.date < 10 ? ("0"+ date.date) : date.date;
		var m = date.month < 9 ? ("0"+ (date.month + 1)) :  (date.month + 1);
		var y = date.year;
		str += h + ":" + min + ":" + sec + " "
			+ d + "/" + m + "/" + (y + 1900);
	}
	return str;
}

function raiseIssue(id, row) {
	var data = $(id).jqxGrid('getrowdata', row);
	if (data) {
		var customer = "";
		if (data.groupName) {
			customer = data.groupName;
		} else if (data.lastName || data.middleName || data.firstName) {
			customer = data.lastName + " " + data.middleName + " " + data.firstName;
		}
		$("#customer").text(customer);
		$("#createIssue").attr("data-id", data.partyId);
	}
	if (issueForm) {
		issueForm.modal("show");
	}
	var editor = ck["issueContent"];
	if (editor) {
		var time = setTimeout(function() {
			editor.focus();
			clearTimeout(time);
		}, 600);
	}
}

function clearIssueForm() {
	$("#customer").text("");
	$("#createIssue").attr("data-id", "");
	ck["issueContent"].setData("");
}
