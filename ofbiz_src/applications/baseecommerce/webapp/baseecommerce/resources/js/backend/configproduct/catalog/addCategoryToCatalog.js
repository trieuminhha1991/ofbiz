if (typeof (AddCategory) == "undefined") {
	var AddCategory = (function() {
		var initJqxElements = function() {
			$("#jqxwindowAddCategoryToCatalog").jqxWindow({
				theme: 'olbius', width: 550, maxWidth: 1845, minHeight: 200, height: 200, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelAddCategoryToCatalog"), modalOpacity: 0.7
			});

			$("#txtCategoryId").jqxDropDownList({ theme: 'olbius', width: 218, height: 30, source: [],
				displayMember: 'categoryName', valueMember: 'productCategoryId', placeHolder: multiLang.filterchoosestring});

			$("#jqxNotificationCategory").jqxNotification({ width: "100%", appendContainer: "#containerCategory",
				opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$('#jqxwindowAddCategoryToCatalog').on('open', function () {
				$("#txtCategoryId").jqxDropDownList('clearSelection');
			});
			$('#jqxwindowAddCategoryToCatalog').on('close', function () {
				$('#jqxwindowAddCategoryToCatalog').jqxValidator('hide');
			});

			$("#saveAddCategoryToCatalog").click(function () {
				if ($('#jqxwindowAddCategoryToCatalog').jqxValidator('validate')) {
					var row = {};
					row.prodCatalogId = $(".lblProdCatalogId").first().text();
					row.productCategoryId = $("#txtCategoryId").jqxDropDownList('getSelectedItem').value;
					row.categoryName = $("#txtCategoryId").jqxDropDownList('getSelectedItem').label;
					$("#jqxgridViewListCategories").jqxGrid('addRow', null, row, "first");
					$("#jqxwindowAddCategoryToCatalog").jqxWindow('close');
				}
			});
		};
		var initValidator = function() {
			$('#jqxwindowAddCategoryToCatalog').jqxValidator({
			    rules: [{ input: '#txtCategoryId', message: multiLang.fieldRequired, action: 'change',
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}
						}]
			});
		};
		var open = function() {
			loadCategories();
			var wtmp = window;
			var tmpwidth = $('#jqxwindowAddCategoryToCatalog').jqxWindow('width');
	        $("#jqxwindowAddCategoryToCatalog").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowAddCategoryToCatalog").jqxWindow('open');
		};
		var loadCategories = function() {
			var rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var prodCatalogId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "prodCatalogId");
			$("#txtCategoryId").jqxDropDownList({source: DataAccess.getData({
																url: "getListCategoryAvalibleInCatalog",
																data: {prodCatalogId: prodCatalogId},
																source: "listCategoryAvalible"})});
		};
		var notify = function(res) {
			loadCategories();
			$('#jqxNotificationCategory').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationCategory").jqxNotification({ template: 'error'});
				$("#notificationContentCategory").text(multiLang.updateError);
				$("#jqxNotificationCategory").jqxNotification("open");
			}else {
				$("#jqxNotificationCategory").jqxNotification({ template: 'info'});
				$("#notificationContentCategory").text(multiLang.updateSuccess);
				$("#jqxNotificationCategory").jqxNotification("open");
			}
			if (res["productCategoryId"]) {
				$("#txtCategoryId").jqxDropDownList('val', res["productCategoryId"]);
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
				RootCatagory.init();
			},
			open: open,
			notify: notify
		};
	})();
}