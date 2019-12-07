if (typeof (Products) == "undefined") {
	var Products = (function() {
		var initJqxElements = function() {
			$("#jqxNotificationProducts").jqxNotification({
				opacity : 0.9,
				autoClose : true
			});
			$("#jqxwindowProducts").jqxWindow({
				theme : theme,
				width : 950,
				maxWidth : 1845,
				minHeight : 310,
				height : 470,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#cancelProducts"),
				modalOpacity : 0.7
			});
			$("#productMenu").jqxMenu({
				theme : theme,
				width : 200,
				autoOpenPopup : false,
				mode : "popup",
				popupZIndex : 999999
			});
		};
		var handleEvents = function() {
			$("#jqxgridProducts").on(
					"rowselect",
					function(event) {
						var args = event.args;
						var rowData = args.row;
						checkProductErasable(rowData.productId,
								rowData.productCategoryId);
					});

			$("#productMenu").on(
					"itemclick",
					function(event) {
						var args = event.args;
						var itemId = $(args).attr("id");
						switch (itemId) {
						case "configProduct":
							var rowIndexSelected = $("#jqxgridProducts")
									.jqxGrid("getSelectedRowindex");
							var rowData = $("#jqxgridProducts").jqxGrid(
									"getrowdata", rowIndexSelected);
							window.open("ConfigProductAndCategories?productId="
									+ rowData.productId, "_blank");
							break;
						case "viewReviews":
							var rowIndexSelected = $("#jqxgridProducts")
									.jqxGrid("getSelectedRowindex");
							var rowData = $("#jqxgridProducts").jqxGrid(
									"getrowdata", rowIndexSelected);
							if (rowIndexSelected == -1) {
								rowIndexSelected = gridSelecting
										.jqxGrid("getSelectedRowindex");
								rowData = gridSelecting.jqxGrid("getrowdata",
										rowIndexSelected);
							}
							window.open("ProductReviews?productId="
									+ rowData.productId, "_blank");
							break;
						case "viewContent":
							var rowIndexSelected = $("#jqxgridProducts")
									.jqxGrid("getSelectedRowindex");
							var rowData = $("#jqxgridProducts").jqxGrid(
									"getrowdata", rowIndexSelected);
							if (rowIndexSelected == -1) {
								rowIndexSelected = gridSelecting
										.jqxGrid("getSelectedRowindex");
								rowData = gridSelecting.jqxGrid("getrowdata",
										rowIndexSelected);
							}
							window.open("ListProductContent?productId="
									+ rowData.productId, "_blank");
							break;
						case "addContent":
							var rowIndexSelected = $("#jqxgridProducts")
									.jqxGrid("getSelectedRowindex");
							var rowData = $("#jqxgridProducts").jqxGrid(
									"getrowdata", rowIndexSelected);
							if (rowIndexSelected == -1) {
								rowIndexSelected = gridSelecting
										.jqxGrid("getSelectedRowindex");
								rowData = gridSelecting.jqxGrid("getrowdata",
										rowIndexSelected);
							}
							window.open("ContentEditorEngine?productId="
									+ rowData.productId + "&type=PRODUCT",
									"_blank");
							break;
						case "viewComments":
							var rowIndexSelected = $("#jqxgridProducts")
									.jqxGrid("getSelectedRowindex");
							var rowData = $("#jqxgridProducts").jqxGrid(
									"getrowdata", rowIndexSelected);
							CommentTree.load(rowData.productId, "true");
							break;
						case "view":
							var rowIndexSelected = $("#jqxgridProducts")
									.jqxGrid("getSelectedRowindex");
							var rowData = $("#jqxgridProducts").jqxGrid(
									"getrowdata", rowIndexSelected);
							window.open(
									"/baseecommerce/control/productmaindetail?product_id="
											+ rowData.productId, "_blank");
							break;
						default:
							break;
						}
					});
		};
		var render = function(productCategoryId) {
			var source = {
				datatype : "json",
				datafields : [ {
					name : "productId",
					type : "string"
				}, {
					name : "productCode",
					type : "string"
				}, {
					name : "mainCategoryId",
					type : "string"
				}, {
					name : "primaryProductCategoryId",
					type : "string"
				}, {
					name : "productName",
					type : "string"
				}, {
					name : "productCategoryId",
					type : "string"
				}, {
					name : "fromDate",
					type : "date",
					other : "Timestamp"
				}, {
					name : "thruDate",
					type : "date",
					other : "Timestamp"
				}, {
					name : "sequenceNum",
					type : "number"
				}, {
					name : "isBestSell",
					type : "bool"
				}, {
					name : "isPromos",
					type : "bool"
				}, {
					name : "isFeatured",
					type : "bool"
				}, {
					name : "isNew",
					type : "bool"
				}, {
					name : "bestSellFromDate",
					type : "number"
				}, {
					name : "promosFromDate",
					type : "number"
				}, {
					name : "newFromDate",
					type : "number"
				}, {
					name : "featuredFromDate",
					type : "number"
				} ],
				url : "getProductByProductCategoryIdIncludeChild?productCategoryId="
						+ productCategoryId,
				async : false,
				addrow : function(rowid, rowdata, position, commit) {
					commit(true);
				},
				deleterow : function(rowid, commit) {
					var data = $("#jqxgridProducts").jqxGrid("getrowdatabyid",
							rowid);
					if (typeof (data.fromDate) == "object") {
						var originFromDate = data.fromDate.time;
						data.fromDate ? data.fromDate = new Date(
								data.fromDate.time).toSQLTimeStamp()
								: data.fromDate;
						var sec = originFromDate
								- new Date(data.fromDate).getTime();
						if (sec < 100) {
							if (sec < 10) {
								sec = "00" + sec;
							} else {
								sec = "0" + sec;
							}
						}
						data.fromDate = data.fromDate + "." + sec;
					} else if (typeof (data.fromDate) == "number") {
						var originFromDate = data.fromDate.time;
						data.fromDate ? data.fromDate = new Date(data.fromDate)
								.toSQLTimeStamp() : data.fromDate;
						var sec = originFromDate
								- new Date(data.fromDate).getTime();
						if (sec < 100) {
							if (sec < 10) {
								sec = "00" + sec;
							} else {
								sec = "0" + sec;
							}
						}
						data.fromDate = data.fromDate + "." + sec;
					}
					var result = DataAccess.execute({
						url : "removeProductFromCategoryAjax",
						data : data
					}, Products.notify);
					if (result && !$("#btnDeleteThisProduct").hasClass("hide")) {
						$("#btnDeleteThisProduct").addClass("hide");
					}
					commit(result);
				},
				updaterow : function(rowid, data, commit) {
					if (typeof (data.fromDate) == "object") {
						data.fromDate ? data.fromDate = data.fromDate.time
								: data.fromDate;
					}
					if (!$("#btnDeleteThisProduct").hasClass("hide")) {
						$("#btnDeleteThisProduct").addClass("hide");
					}
					commit(DataAccess.execute({
						url : "configProductCategoryAjax",
						data : data
					}, Products.notify));
					checkProductErasable(data.productId, data.productCategoryId);
				},
				id : "productId"
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#jqxgridProducts").jqxGrid(
					{
						source : dataAdapter,
						localization : getLocalization(),
						showfilterrow : true,
						filterable : true,
						editable : true,
						width : "100%",
						height : 350,
						pagesize : 10,
						pageable : true,
						theme : theme,
						sortable : true,
						enabletooltips : true,
						selectionmode : "singlerow",
						columns : [ {
							text : multiLang.DmsCategoryId,
							dataField : "productCategoryId",
							width : 150,
							editable : false
						}, {
							text : multiLang.DmsProductId,
							datafield : "productCode",
							width : 150,
							editable : false
						}, {
							text : multiLang.DmsInternalName,
							datafield : "productName",
							editable : false,
							minWidth : 200
						}, {
							text : multiLang.DmsSequenceId,
							datafield : "sequenceNum",
							width : 50,
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
						}, {
							text : multiLang.BSInTheListSelling,
							dataField : "isBestSell",
							columntype : "checkbox",
							align : "center",
							width : 100,
							filterable : false,
							sortable : false
						}, {
							text : multiLang.BSIsPromosProduct,
							dataField : "isPromos",
							columntype : "checkbox",
							align : "center",
							width : 100,
							filterable : false,
							sortable : false
						}, {
							text : multiLang.BSIsNewProduct,
							dataField : "isNew",
							columntype : "checkbox",
							align : "center",
							width : 100,
							filterable : false,
							sortable : false
						}, {
							text : multiLang.BEFeaturedProducts,
							dataField : "isFeatured",
							columntype : "checkbox",
							align : "center",
							width : 100,
							filterable : false,
							sortable : false
						} ],
						handlekeyboardnavigation : function(event) {
							var key = event.charCode ? event.charCode
									: event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								$("#jqxgridProducts").jqxGrid("clearfilters");
								return true;
							}
						}
					});
			$("#jqxgridProducts").jqxGrid("clearselection");
		};
		var open = function(productCategoryId) {
			render(productCategoryId);
			var wtmp = window;
			var tmpwidth = $("#jqxwindowProducts").jqxWindow("width");
			$("#jqxwindowProducts").jqxWindow({
				position : {
					x : (wtmp.outerWidth - tmpwidth) / 2,
					y : pageYOffset + 70
				}
			});
			$("#jqxwindowProducts").jqxWindow("open");
		};
		var notify = function(res) {
			$("#jqxNotificationProducts").jqxNotification("closeLast");
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				$("#jqxNotificationProducts").jqxNotification({
					template : "error"
				});
				$("#notificationContentProducts").text(multiLang.updateError);
				$("#jqxNotificationProducts").jqxNotification("open");
			} else {
				$("#jqxNotificationProducts").jqxNotification({
					template : "info"
				});
				$("#notificationContentProducts").text(multiLang.updateSuccess);
				$("#jqxNotificationProducts").jqxNotification("open");
				Products.refresh();
			}
		};
		var _delete = function() {
			var rowIndexEditing = $("#jqxgridProducts").jqxGrid(
					"getSelectedRowindex");
			if (rowIndexEditing != -1) {
				bootbox.confirm(multiLang.ConfirmDelete,
						multiLang.CommonCancel, multiLang.CommonSubmit,
						function(result) {
							if (result) {
								var productId = $("#jqxgridProducts").jqxGrid(
										"getcellvalue", rowIndexEditing,
										"productId");
								$("#jqxgridProducts").jqxGrid("deleterow",
										productId);
							}
						});
			} else {
				bootbox.alert("Choose Product To Delete");
			}
		};
		var checkProductErasable = function(productId, productCategoryId) {
			if (!$("#btnDeleteThisProduct").hasClass("hide")) {
				$("#btnDeleteThisProduct").addClass("hide");
			}
			var erasable = DataAccess.getData({
				url : "checkProductErasable",
				data : {
					productId : productId,
					productCategoryId : productCategoryId
				},
				source : "erasable"
			});
			if (erasable == "Y") {
				$("#btnDeleteThisProduct").removeClass("hide");
			}
		};
		var refresh = function() {
			$("#jqxgridProducts").jqxGrid("updatebounddata");
		};
		return {
			init : function() {
				initJqxElements();
				handleEvents();
				AddProduct.init();
			},
			notify : notify,
			open : open,
			refresh : refresh,
			_delete : _delete
		};
	})();
}