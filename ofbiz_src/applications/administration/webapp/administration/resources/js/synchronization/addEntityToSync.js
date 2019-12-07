if (typeof (AddEntityToSync) == "undefined") {
	var AddEntityToSync = (function() {
		var jqxwindow, mainGrid;
		var initJqxElements = function() {

			var source = {
				datatype : "json",
				datafields : [ {
					name : "entityGroupId"
				}, {
					name : "entityGroupName"
				} ],
				url : "loadEntityGroupId"
			};
			var dataAdapter = new $.jqx.dataAdapter(source);

			$("#txtEntityGroupId").jqxComboBox({
				theme : theme,
				source : dataAdapter,
				width : 218,
				height : 25,
				displayMember : "entityGroupName",
				valueMember : "entityGroupId",
				autoDropDownHeight : true
			});

			$("#txtEntityOrPackage").jqxComboBox({
				theme : theme,
				source : [],
				width : 218,
				height : 25,
				displayMember : "text",
				valueMember : "value",
				multiSelect : true
			});

			jqxwindow.jqxWindow({
				theme : theme,
				width : 500,
				height : 200,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#btnCancelEntityToSync"),
				modalOpacity : 0.7
			});
		};
		var handleEvents = function() {
			$("#btnSaveEntityToSync").click(
					function() {
						if (jqxwindow.jqxValidator("validate")) {
							var row = {};
							row = {
								entityGroupId : $("#txtEntityGroupId").jqxComboBox("val"),
								entityOrPackage : _.pluck($("#txtEntityOrPackage").jqxComboBox("getSelectedItems"), "value"),
								applEnumId : "ESIA_INCLUDE"
							};
							mainGrid.jqxGrid("addRow", null, row, "first");
							jqxwindow.jqxWindow("close");
						}
					});
			$("#txtEntityGroupId")
					.on(
							"change",
							function(event) {
								var args = event.args;
								if (args) {
									var item = args.item;
									var label = item.label;
									var value = item.value;
									if (value) {
										var source = {
											datatype : "json",
											datafields : [ {
												name : "value"
											}, {
												name : "text"
											} ],
											url : "loadEntitiesNotInGroup?entityGroupId="
													+ value
										};
										var dataAdapter = new $.jqx.dataAdapter(
												source);
										$("#txtEntityOrPackage").jqxComboBox({
											source : dataAdapter
										});
									}
								}
							});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				$("#txtEntityGroupId").jqxComboBox("clearSelection");
				$("#txtEntityOrPackage").jqxComboBox("clearSelection");
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
				rules : [ {
					input : "#txtEntityGroupId",
					message : multiLang.fieldRequired,
					action : "change",
					rule : function(input, commit) {
						var value = input.jqxComboBox("getSelectedItem");
						if (value) {
							return true;
						}
						return false;
					}
				}, {
					input : "#txtEntityOrPackage",
					message : multiLang.fieldRequired,
					action : "change",
					rule : function(input, commit) {
						var value = input.jqxComboBox("getSelectedItem");
						if (value) {
							return true;
						}
						return false;
					}
				} ]
			});
		};
		return {
			init : function() {
				jqxwindow = $("#addEntityToSync");
				mainGrid = $("#jqxgridWhatToSync");
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}