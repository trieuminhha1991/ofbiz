if (typeof (CustomerConsideration) == "undefined") {
	var CustomerConsideration = (function() {
		var initJqxElements = function() {
			$("#customers-panel").jqxSplitter({
				width : "100%",
				height : "100%",
				orientation : "vertical",
				panels : [ {
					size : "30%"
				} ]
			});
			$("#customer-info-panel").jqxSplitter({
				width : "100%",
				height : "100%",
				orientation : "horizontal",
				panels : [ {
					size : "35%"
				} ]
			});
			$("#customer-insight-panel").jqxSplitter({
				width : "100%",
				height : "100%",
				orientation : "vertical",
				panels : [ {
					size : "50%"
				} ]
			});
			$("#listCustomers-cons").jqxListBox({
				displayMember : "memberName",
				valueMember : "memberId",
				width : "100%",
				autoHeight : true
			});
		};
		var handleEvents = function() {
			$("#listCustomers-cons")
					.on(
							"select",
							function(event) {
								var index = event.args.index;
								var records = $("#listCustomers-cons")
										.jqxListBox("source").records;
								if (!_.isEmpty(records)) {
									CustomerInfoCons
											.setValue(records[index].memberConsideration);
								}
							});
		};
		var load = function(partyId) {
			var source = {
				datatype : "json",
				datafields : [ {
					name : "memberId"
				}, {
					name : "memberName"
				}, {
					name : "memberConsideration"
				} ],
				id : "memberId",
				url : "loadPartiesAndConsideration?partyId=" + partyId
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#listCustomers-cons")
					.jqxListBox(
							{
								source : dataAdapter,
								renderer : function(index, label, value) {
									var records = $("#listCustomers-cons")
											.jqxListBox("source").records;
									if (!_.isEmpty(records)) {
										value = records[index].memberName
												+ "<br/>";
										if (records[index].memberConsideration) {
											if (records[index].memberConsideration.gender) {
												value += "["
														+ mapGender[records[index].memberConsideration.gender]
														+ "] ";
											}
											if (records[index].memberConsideration.memberRole) {
												value += "<i>"
														+ records[index].memberConsideration.memberRole
														+ "</i>";
											}
										}
									}
									return value;
								}
							});
		};
		return {
			init : function() {
				initJqxElements();
				handleEvents();
			},
			load : load
		};
	})();
}