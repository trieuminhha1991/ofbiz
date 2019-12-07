if (typeof (Products) == "undefined") {
	var Products = (function() {
		var jqxwindow, productGrid, jqxnotification;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme : "olbius",
				width : 950,
				maxWidth : 1845,
				minHeight : 310,
				height : 450,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#cancelViewListProducts"),
				modalOpacity : 0.7
			});
			jqxnotification.jqxNotification({
				opacity : 0.9,
				autoClose : true,
				template : "info"
			});
		};
		var render = function(enumId) {
			var source = {
				datatype : "json",
				datafields : [ {
					name : "enumId",
					type : "string"
				}, {
					name : "productId",
					type : "string"
				}, {
					name : "enumRelTypeId",
					type : "string"
				}, {
					name : "sequenceNum",
					type : "number"
				}, {
					name : "productCode",
					type : "string"
				}, {
					name : "productName",
					type : "string"
				}, ],
				url : "getProductsInSubject?enumId=" + enumId,
				async : false,
				addrow : function(rowid, rowdata, position, commit) {
					rowdata.enumRelTypeId = "RECOMMENDED";
					commit(DataAccess.execute({
						url : "createEnumerationRelProduct",
						data : rowdata
					}, Products.notify));
				},
				deleterow : function(rowdata, commit) {
					commit(DataAccess.execute({
						url : "deleteEnumerationRelProduct",
						data : rowdata
					}, Products.notify));
				},
				updaterow : function(rowid, newdata, commit) {
					commit(DataAccess.execute({
						url : "updateEnumerationRelProduct",
						data : newdata
					}, Products.notify));
				}
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			productGrid.jqxGrid({
				source : dataAdapter,
				localization : getLocalization(),
				showfilterrow : true,
				filterable : true,
				editable : true,
				width : "100%",
				height : "90%",
				pagesize : 10,
				pageable : true,
				theme : "olbius",
				sortable : true,
				selectionmode : "singlerow",
				columns : [ {
					text : multiLang.DmsProductId,
					datafield : "productCode",
					width : 250,
					editable : false
				}, {
					text : multiLang.DmsInternalName,
					datafield : "productName",
					editable : false,
					minWidth : 200
				}, {
					text : multiLang.DmsSequenceId,
					datafield : "sequenceNum",
					width : 150,
					align : "right",
					cellsalign : "right",
					columntype : "numberinput",
					validation : function(cell, value) {
						if (value < 0) {
							return {
								result : false,
								message : multiLang.DmsQuantityNotValid
							};
						}
						return true;
					}
				} ],
				handlekeyboardnavigation : function(event) {
					var key = event.charCode ? event.charCode
							: event.keyCode ? event.keyCode : 0;
					if (key == 70 && event.ctrlKey) {
						productGrid.jqxGrid("clearfilters");
						return true;
					}
				}
			});
		};
		var open = function(enumId) {
			if (enumId) {
				render(enumId);
				var wtmp = window;
				var tmpwidth = jqxwindow.jqxWindow("width");
				jqxwindow.jqxWindow({
					position : {
						x : (wtmp.outerWidth - tmpwidth) / 2,
						y : pageYOffset + 70
					}
				});
				jqxwindow.jqxWindow("open");
			}
		};
		var notify = function(res) {
			jqxnotification.jqxNotification("closeLast");
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				jqxnotification.jqxNotification({
					template : "error"
				});
				$("#notificationContentProducts").text(multiLang.updateError);
				jqxnotification.jqxNotification("open");
			} else {
				jqxnotification.jqxNotification({
					template : "info"
				});
				$("#notificationContentProducts").text(multiLang.updateSuccess);
				jqxnotification.jqxNotification("open");
				setTimeout(function() {
					productGrid.jqxGrid("updatebounddata");
				}, 100);
			}
		};
		var _delete = function() {
			var rowindex = productGrid.jqxGrid("getSelectedRowindex");
			if (rowindex != -1) {
				bootbox.confirm(multiLang.ConfirmDelete,
						multiLang.CommonCancel, multiLang.CommonSubmit,
						function(result) {
							if (result) {
								productGrid.jqxGrid("deleterow", productGrid
										.jqxGrid('getrowdata', rowindex));
							}
						});
			} else {
				bootbox.alert("Choose Product To Delete");
			}
		};
		var _add = function(newdata) {
			return productGrid.jqxGrid('addrow', null, newdata);
		};
		return {
			init : function() {
				jqxwindow = $("#jqxwindowViewListProducts");
				jqxnotification = $("#jqxNotificationProducts");
				productGrid = $("#jqxgridViewListProducts");
				initJqxElements();
				AddProduct.init();
			},
			open : open,
			notify : notify,
			_delete : _delete,
			_add : _add
		};
	})();
}