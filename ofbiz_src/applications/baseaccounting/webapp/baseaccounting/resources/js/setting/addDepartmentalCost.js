$(document).ready(function() {
	AddDepartmentalCost.init();
});
if (typeof (AddDepartmentalCost)) {
	var AddDepartmentalCost = (function() {
		var initJqxElements = function() {
			$("#alterpopupWindow").jqxWindow({
		        width: 950, height : 250, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme: theme           
		    });
			
			var source = { datatype: "json",
					datafields: [{ name: "partyId" },
					             { name: "partyName" }],
					             url: "getListOrganizations"};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#txtOrganization").jqxDropDownList({ theme: theme, source: dataAdapter, width: "98%", height: 30, displayMember: "partyName",
				valueMember: "partyId", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true,
				renderer: function (index, label, value) {
					var data = $("#txtOrganization").jqxDropDownList('source');
					var source = data.records;
					if(source.length){
						var obj = source[index];
						if(obj){
				            return (obj.partyName?obj.partyName.trim():obj.partyName) + " [" + (obj.partyCode?obj.partyCode:obj.partyId) + "]";
						}
					}
				}	
			});
			
			var source = { datatype: "json",
					datafields: [{ name: "invoiceItemTypeId" },
					             { name: "description" }],
					             url: "getListInvoiceItemTypes"};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#txtInvoiceItemType").jqxComboBox({ source: dataAdapter, theme: theme, displayMember: "description", valueMember: "invoiceItemTypeId",
				width: "98%", height: 30 });
			
			$("#txtDepartment").jqxDropDownList({ theme: theme, source: [], width: "98%", height: 30, displayMember: "groupName",
				valueMember: "partyId", placeHolder: multiLang.filterchoosestring,
				renderer: function (index, label, value) {
					var data = $("#txtDepartment").jqxDropDownList('source');
					var source = data.records;
					if(source.length){
						var obj = source[index];
						if(obj){
				            return obj.groupName + " [" + (obj.partyCode?obj.partyCode:obj.partyId) + "]";
						}
					}
				}
			});
			
			$("#txtFromDate").jqxDateTimeInput({ theme: theme, width: "98%", height: 30 });
			$("#txtThruDate").jqxDateTimeInput({ theme: theme, width: "98%", height: 30 });
			$("#txtThruDate").jqxDateTimeInput("setDate", null);
		};
		var handleEvents = function() {
			$("#txtOrganization").on("change", function (event){     
				var args = event.args;
				if (args) {
					var index = args.index;
					var item = args.item;
					var label = item.label;
					var value = item.value;
					if (value) {
						var source = { datatype: "json",
								datafields: [{ name: "partyId" },
								             { name: "partyCode" },
								             { name: "groupName" }],
								             url: "getListDepartments?organizationPartyId=" + value};
						var dataAdapter = new $.jqx.dataAdapter(source);
						$("#txtDepartment").jqxDropDownList({ source: dataAdapter });
					}
				}
			});
			$("#save").click(function() {
				if ($("#alterpopupWindow").jqxValidator("validate")) {
					$("#jqxgrid").jqxGrid("addRow", null, AddDepartmentalCost.getValue(), "first");
					$("#alterpopupWindow").jqxWindow("close");
				}
			});
			$("#alterpopupWindow").on("close", function() {
				$("#txtInvoiceItemType").jqxComboBox("clearSelection");
				$("#txtOrganization").jqxDropDownList("clearSelection");
				$("#txtDepartment").jqxDropDownList("clearSelection");
				$("#txtThruDate").jqxDateTimeInput("setDate", null);
			});
		};
		var initValidator = function() {
			$("#alterpopupWindow").jqxValidator({
			    rules: [{ input: "#txtInvoiceItemType", message: multiLang.fieldRequired, action: 'change',
							rule: function (input, commit) {
								if (input.jqxComboBox("val")) {
									return true;
								}
								return false;
							}
		          		},
		          		{ input: "#txtOrganization", message: multiLang.fieldRequired, action: 'change',
							rule: function (input, commit) {
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
		          		},
		          		{ input: "#txtDepartment", message: multiLang.fieldRequired, action: 'change',
		          			rule: function (input, commit) {
		          				if (input.jqxDropDownList("val")) {
		          					return true;
		          				}
		          				return false;
		          			}
		          		},
		          		{ input: "#txtFromDate", message: multiLang.fieldRequired, action: 'change',
		          			rule: function (input, commit) {
		          				if (input.jqxDateTimeInput("getDate")) {
		          					return true;
		          				}
		          				return false;
		          			}
		          		}]
			});
		};
		var getValue = function() {
			var value = {
				invoiceItemTypeId: $("#txtInvoiceItemType").jqxComboBox("val"),
				organizationPartyId: $("#txtOrganization").jqxDropDownList("val"),
				departmentId: $("#txtDepartment").jqxDropDownList("val"),
				fromDate: $("#txtFromDate").jqxDateTimeInput("getDate"),
				thruDate: $("#txtThruDate").jqxDateTimeInput("getDate")
			};
			value.fromDate = value.fromDate?value.fromDate.toSQLTimeStamp():value.fromDate;
			value.thruDate = value.thruDate?value.thruDate.toSQLTimeStamp():value.thruDate;
			return value;
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue
		}
	})();
}