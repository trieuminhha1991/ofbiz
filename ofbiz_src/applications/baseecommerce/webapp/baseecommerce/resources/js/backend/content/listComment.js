$(document).ready(function() {
	CommentTree.init();
});
if (typeof (CommentTree) == "undefined") {
	var CommentTree = (function() {
		var grid = $("#gridComment");
		var contextMenuComment, gridSelecting;
		var initJqxElements = function() {
			$("#jqxNotificationNestedComment").jqxNotification({
				width : "100%",
				appendContainer : "#containerComment",
				opacity : 0.9,
				autoClose : true,
				template : "info"
			});

			$("#jqxwindowListComment").jqxWindow({
				theme : theme,
				width : 950,
				maxWidth : 2000,
				height : 500,
				maxHeight : 1000,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#alterCancelListComment"),
				modalOpacity : 0.7
			});
			contextMenuComment = $("#contextMenuComment").jqxMenu({
				theme : theme,
				width : 230,
				autoOpenPopup : false,
				mode : "popup",
				popupZIndex : 999999
			});
		};
		var handleEvents = function() {
			$("#removeFilter").on("click", function() {
				grid.jqxGrid("clearfilters");
			});
			grid.on("contextmenu", function() {
				return false;
			});
			grid.on("rowclick", function(event) {
				if (event.args.rightclick) {
					if (gridSelecting) {
						gridSelecting.jqxGrid("clearselection");
					}
					grid.jqxGrid("selectrow", event.args.rowindex);
					var scrollTop = $(window).scrollTop();
					var scrollLeft = $(window).scrollLeft();
					contextMenuComment.jqxMenu("open",
							parseInt(event.args.originalEvent.clientX) + 5
									+ scrollLeft,
							parseInt(event.args.originalEvent.clientY) + 5
									+ scrollTop);
					gridSelecting = grid;
					return false;
				}
			});
			$("body").on("click", function() {
				contextMenuComment.jqxMenu("close");
			});
			contextMenuComment.on("itemclick", function(event) {
				var args = event.args;
				var itemId = $(args).attr("id");
				switch (itemId) {
				case "activateComment":
					var rowIndexSelected = gridSelecting
							.jqxGrid("getSelectedRowindex");
					var rowData = gridSelecting.jqxGrid("getrowdata",
							rowIndexSelected);
					var statusId = rowData.statusId;
					if (statusId == "CTNT_PUBLISHED") {
						DataAccess.execute({
							url : "updateReply",
							data : {
								contentId : rowData.contentId,
								statusId : "CTNT_DEACTIVATED"
							}
						});
					} else {
						DataAccess.execute({
							url : "updateReply",
							data : {
								contentId : rowData.contentId,
								statusId : "CTNT_PUBLISHED"
							}
						});
					}
					gridSelecting.jqxGrid("updatebounddata");
					break;
				default:
					break;
				}
			});
			contextMenuComment.on("shown", function() {
				var rowIndexSelected = gridSelecting
						.jqxGrid("getSelectedRowindex");
				var rowData = gridSelecting.jqxGrid("getrowdata",
						rowIndexSelected);
				var statusId = rowData.statusId;
				if (statusId == "CTNT_PUBLISHED") {
					$("#activateComment").html(
							"<i class=\"fa-frown-o\"></i>&nbsp;&nbsp;"
									+ multiLang.DmsDeactivate);
				} else {
					$("#activateComment").html(
							"<i class=\"fa-smile-o\"></i>&nbsp;&nbsp;"
									+ multiLang.DmsActive);
				}
			});
		};
		var open = function() {
			var wtmp = window;
			var tmpwidth = $("#jqxwindowListComment").jqxWindow("width");
			$("#jqxwindowListComment").jqxWindow({
				position : {
					x : (wtmp.outerWidth - tmpwidth) / 2,
					y : pageYOffset + 40
				}
			});
			$("#jqxwindowListComment").jqxWindow("open");
		};
		var load = function(contentId, isProduct) {
			if (contentId) {
				if (!isProduct) {
					isProduct = "false";
				}
				var source = {
					datatype : "json",
					datafields : [ {
						name : "contentId",
						type : "string"
					}, {
						name : "contentName",
						type : "string"
					}, {
						name : "partyRole",
						type : "string"
					}, {
						name : "longDescription",
						type : "string"
					}, {
						name : "createdStamp",
						type : "string"
					}, {
						name : "statusId",
						type : "string"
					}, {
						name : "numberOfReplies",
						type : "number"
					} ],
					url : "JQGetCommentsByTopic?contentId=" + contentId
							+ "&isProduct=" + isProduct,
					id : "contentId",
					addrow : function(rowid, rowdata, position, commit) {

						commit(true);
					},
					deleterow : function(rowid, commit) {

						commit(true);
					},
					updaterow : function(rowid, newdata, commit) {

						commit(true);
					}
				};
				var dataAdapter = new $.jqx.dataAdapter(source);
				grid
						.jqxGrid({
							localization : getLocalization(),
							width : "100%",
							height : 380,
							theme : theme,
							source : dataAdapter,
							sortable : true,
							pagesize : 10,
							pageable : true,
							columnsresize : true,
							showfilterrow : true,
							filterable : true,
							rowdetails : true,
							rowdetailstemplate : {
								rowdetails : "<div id=\"grid\" style=\"margin: 10px;\"></div>",
								rowdetailsheight : 220,
								rowdetailshidden : true
							},
							initrowdetails : initrowdetails,
							selectionmode : "singlerow",
							columns : [
									{
										text : multiLang.DmsSequenceId,
										datafield : "",
										sortable : false,
										filterable : false,
										editable : false,
										pinned : true,
										groupable : false,
										draggable : false,
										resizable : false,
										width : 40,
										cellsrenderer : function(row, column,
												value) {
											return "<div style=margin:4px;>"
													+ (row + 1) + "</div>";
										}
									},
									{
										text : multiLang.BSCommentId,
										datafield : "contentId",
										filtertype : "input",
										width : 120
									},
									{
										text : multiLang.BSPartyComment,
										datafield : "contentName",
										filtertype : "input",
										width : 120
									},
									{
										text : multiLang.BSComment,
										datafield : "longDescription",
										filtertype : "input"
									},
									{
										text : multiLang.BSTimeComment,
										datafield : "createdStamp",
										filtertype : "input",
										width : 120
									},
									{
										text : multiLang.BSStatus,
										datafield : "statusId",
										filtertype : "checkedlist",
										width : 120,
										cellsrenderer : function(row, colum,
												value) {
											value ? value = mapStatusItem[value]
													: value;
											return "<span>" + value + "</span>";
										},
										createfilterwidget : function(column,
												htmlElement, editor) {
											editor
													.jqxDropDownList({
														autoDropDownHeight : true,
														source : fixSelectAll(listStatusItem),
														displayMember : "statusId",
														valueMember : "statusId",
														renderer : function(
																index, label,
																value) {
															if (index == 0) {
																return value;
															}
															return mapStatusItem[value];
														}
													});
											editor.jqxDropDownList("checkAll");
										}
									}, {
										text : multiLang.BSNumberOfReplies,
										datafield : "numberOfReplies",
										cellsalign : "right",
										filtertype : "number",
										width : 90
									} ],
							handlekeyboardnavigation : function(event) {
								var key = event.charCode ? event.charCode
										: event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									grid.jqxGrid("clearfilters");
									return true;
								}
							}
						});
				CommentTree.open();
			}
		};
		var initrowdetails = function(index, parentElement, gridElement,
				datarecord) {
			var gridDetails = $($(parentElement).children()[0]);
			$(gridDetails).attr("id", "jqxgridDetail" + index);
			var source = {
				datatype : "json",
				datafields : [ {
					name : "contentId",
					type : "string"
				}, {
					name : "contentName",
					type : "string"
				}, {
					name : "partyRole",
					type : "string"
				}, {
					name : "longDescription",
					type : "string"
				}, {
					name : "statusId",
					type : "string"
				}, {
					name : "createdStamp",
					type : "string"
				} ],
				url : "JQGetRepliesByComment?contentId=" + datarecord.contentId,
				async : false,
				id : "contentId",
				addrow : function(rowid, rowdata, position, commit) {
					rowdata.contentIdTo = datarecord.contentId;
					commit(Comments.addReply(rowdata));
				},
				deleterow : function(rowid, commit) {

					commit(true);
				},
				updaterow : function(rowid, newdata, commit) {
					commit(Comments.updateReply(newdata));
				}
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			gridDetails
					.jqxGrid({
						localization : getLocalization(),
						width : "95%",
						height : 208,
						theme : theme,
						source : dataAdapter,
						sortable : true,
						pagesize : 5,
						pageable : true,
						columnsresize : true,
						showfilterrow : true,
						filterable : true,
						editable : true,
						editmode : "dblclick",
						selectionmode : "singlerow",
						columns : [
								{
									text : multiLang.BSReplyId,
									datafield : "contentId",
									filtertype : "input",
									width : 120,
									editable : false,
									cellclassname : cellclassname
								},
								{
									text : multiLang.BSPartyReply,
									datafield : "contentName",
									filtertype : "input",
									width : 120,
									editable : false,
									cellclassname : cellclassname
								},
								{
									text : multiLang.BSReply,
									datafield : "longDescription",
									filtertype : "input",
									editable : true,
									cellclassname : cellclassname,
									validation : function(cell, value) {
										if (!value) {
											return {
												result : false,
												message : multiLang.validFieldRequire
											};
										}
										return true;
									}
								},
								{
									text : multiLang.BSTimeReply,
									datafield : "createdStamp",
									filtertype : "input",
									width : 120,
									editable : false,
									cellclassname : cellclassname
								},
								{
									text : multiLang.BSStatus,
									datafield : "statusId",
									filtertype : "checkedlist",
									width : 120,
									cellsrenderer : function(row, colum, value) {
										value ? value = mapStatusItem[value]
												: value;
										return "<span>" + value + "</span>";
									},
									createfilterwidget : function(column,
											htmlElement, editor) {
										editor
												.jqxDropDownList({
													autoDropDownHeight : true,
													source : fixSelectAll(listStatusItem),
													displayMember : "statusId",
													valueMember : "statusId",
													renderer : function(index,
															label, value) {
														if (index == 0) {
															return value;
														}
														return mapStatusItem[value];
													}
												});
									}
								} ],
						handlekeyboardnavigation : function(event) {
							var key = event.charCode ? event.charCode
									: event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								gridDetails.jqxGrid("clearfilters");
								return true;
							}
						},
						showtoolbar : true,
						toolbarheight : 25,
						rendertoolbar : function(toolbar) {
							var container = $("<div style=\"margin-top: 5px;float: right;cursor: pointer;\"></div>");
							var input = $("<a onclick=\"Comments.reply(\"jqxgridDetail"
									+ index
									+ "\")"><i class="fa fa-reply"></i>"
									+ multiLang.BSReply + "</a>");
							toolbar.append(container);
							container.append(input);
						}
					});
			gridDetails.on("rowclick", function(event) {
				if (event.args.rightclick) {
					if (gridSelecting) {
						gridSelecting.jqxGrid("clearselection");
					}
					gridDetails.jqxGrid("selectrow", event.args.rowindex);
					var scrollTop = $(window).scrollTop();
					var scrollLeft = $(window).scrollLeft();
					contextMenuComment.jqxMenu("open",
							parseInt(event.args.originalEvent.clientX) + 5
									+ scrollLeft,
							parseInt(event.args.originalEvent.clientY) + 5
									+ scrollTop);
					gridSelecting = gridDetails;
					return false;
				}
			});
		};
		var cellclassname = function(row, column, value, data) {
			if (data.partyRole) {
				return "green";
			}
		};
		return {
			init : function() {
				initJqxElements();
				handleEvents();
			},
			open : open,
			load : load
		};
	})();
}
if (typeof (Comments) == "undefined") {
	var Comments = (function() {
		var gridEditing;
		var reply = function(gridId) {
			gridEditing = $("#" + gridId);

			gridEditing.jqxGrid("addrow", null, {}, "first");
		};
		var addReply = function(data) {
			return DataAccess.execute({
				url : "createReply",
				data : data
			}, Comments.reloadGrid);
		};
		var updateReply = function(data) {
			return DataAccess.execute({
				url : "updateReply",
				data : data
			}, Comments.notify);
		};
		var reloadGrid = function(res) {
			setTimeout(function() {
				gridEditing.jqxGrid("updatebounddata");
				gridEditing.jqxGrid("begincelledit", 0, "longDescription");
			}, 100);
			Comments.notify(res);
		};
		var notify = function(res) {
			$("#jqxNotificationNestedComment").jqxNotification("closeLast");
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				$("#jqxNotificationNestedComment").jqxNotification({
					template : "error"
				});
				$("#notificationContentNestedComment").text(
						multiLang.updateError);
				$("#jqxNotificationNestedComment").jqxNotification("open");
			} else {
				$("#jqxNotificationNestedComment").jqxNotification({
					template : "info"
				});
				$("#notificationContentNestedComment").text(
						multiLang.updateSuccess);
				$("#jqxNotificationNestedComment").jqxNotification("open");
			}
		};
		return {
			reply : reply,
			addReply : addReply,
			reloadGrid : reloadGrid,
			updateReply : updateReply,
			notify : notify
		};
	})();
}
