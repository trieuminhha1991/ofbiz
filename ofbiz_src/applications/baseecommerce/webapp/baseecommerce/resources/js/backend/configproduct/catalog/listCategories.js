$(document).ready(function() {
	Categories.init();
});

if (typeof (Categories) == "undefined") {
	var Categories = (function() {
		var initJqxElements = function() {
			$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true });
			$("#contextMenu").jqxMenu({ width: 220, theme: "olbius", autoOpenPopup: false, mode: "popup" });
			$("#contextMenu").css('visibility', 'visible');
            $("#moveTarget").parent().first().css('overflow-y', 'auto');
            $("#moveTarget").parent().first().css('height', '130px');
            $("body").css('overflow-y', 'hidden');
		};
		var handleEvents = function() {
			$("#treeGrid").on("contextmenu", function () {
			        return false;
		    });
		    $("#treeGrid").on("rowClick", function (event) {
		        var args = event.args;
		        if (args.originalEvent.button == 2) {
		            var scrollTop = $(window).scrollTop();
		            var scrollLeft = $(window).scrollLeft();
		            $("#contextMenu").jqxMenu("open", parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
		            return false;
		        }
		    });
		    $("body").on("click", function() {
		    	$("#contextMenu").jqxMenu("close");
			});
		    $("#contextMenu").on("shown", function () {
				var rowIndexEditing = $("#treeGrid").jqxTreeGrid("getSelection");
				var parentProductCategoryId = rowIndexEditing[0].parentProductCategoryId;
//				$("#addProductContainer").removeClass("hide");
				$("#contextMenu").jqxMenu("disable", "configCategory", true);
				if (parentProductCategoryId) {
					if (_.indexOf(rootCategories, parentProductCategoryId) === -1) {
//						$("#contextMenu").jqxMenu("disable", "moveToCategory", false);
					} else {
//						$("#contextMenu").jqxMenu("disable", "moveToCategory", true);
						$("#contextMenu").jqxMenu("disable", "configCategory", false);
//						$("#addProductContainer").addClass("hide");
					}
				} else {
					$("#contextMenu").jqxMenu("disable", "moveToCategory", true);
//					$("#addProductContainer").addClass("hide");
				}
				for ( var x in mainCategories) {
					$("#contextMenu").jqxMenu("disable", mainCategories[x].productCategoryId, false);
				}
				$("#contextMenu").jqxMenu("disable", parentProductCategoryId, true);
		    });
		    $("#contextMenu").on("itemclick", function (event) {
		        var args = event.args;
		        var itemId = $(args).attr("id");
		        switch (itemId) {
		        case "configCategory":
					var rowIndexEditing = $("#treeGrid").jqxTreeGrid("getSelection");
					window.location.href = "ConfigCategory?productCategoryId=" + rowIndexEditing[0].productCategoryId;
					break;
		        case "viewListProduct":
					var rowIndexEditing = $("#treeGrid").jqxTreeGrid("getSelection");
					$("#lblProductCategoryId").text(rowIndexEditing[0].productCategoryId);
					Products.open(rowIndexEditing[0].productCategoryId);
					break;
				default:
					if (itemId) {
						var rowIndexEditing = $("#treeGrid").jqxTreeGrid("getSelection");
						Categories.moveCategory(rowIndexEditing[0].productCategoryId, itemId);
					}
					break;
				}
		    });
		};
		var render = function() {
			var source =
	        {
	            dataType: "json",
	            dataFields: [{ name: "productCategoryId", type: "string" },
						{ name: "productCategoryTypeId", type: "string" },
						{ name: "parentProductCategoryId", type: "string" },
						{ name: "categoryName", type: "string" },
						{ name: "longDescription", type: "string" },
						{ name: "sequenceNum", type: "number" },
						{ name: "fromDate", type: "date", other: "Timestamp"}],
	            hierarchy:
	            {
	                keyDataField: { name: "productCategoryId" },
	                parentDataField: { name: "parentProductCategoryId" }
	            },
	            id: "productCategoryId",
	            url: urlCategories,
	            async: false,
	            addRow: function (rowID, rowData, position, parentID, commit) {
	            	commit(true);
	            },
	            updateRow: function (rowID, rowData, commit) {
					if (typeof (rowData.fromDate) == 'object') {
						rowData.fromDate = rowData.fromDate.time;
					}
					commit(DataAccess.execute({
						url: "updateCategoryAndRollupAjax",
						data: rowData},
						Categories.notify));
	            },
	            deleteRow: function (rowID, commit) {
            		commit(true);
	            }
	        };
	        var dataAdapter = new $.jqx.dataAdapter(source);
	        $("#treeGrid").jqxTreeGrid({
	            source: dataAdapter,
	            localization: getLocalization(),
	            width: "100%",
	            sortable: true,
	            theme: "olbius",
	            columnsResize: true,
	            columnsReorder: true,
	            pageable: true,
	            pageSize: 10,
	            pageSizeOptions: ['5', '10', '15'],
	            editable: editable,
	            pagerMode: "advanced",
	            columns: [{ text: multiLang.DmsCategoryId, dataField: "productCategoryId", width: 200, editable: false},
				{ text: multiLang.DmsCategoryName, dataField: "categoryName", width: 250},
				{ text: multiLang.DmsDescription, dataField: "longDescription"},
				{ text: multiLang.BSSequenceNumber, datafield: 'sequenceNum', width: 100}]
	        });
	        lockRoot();
		};
		var lockRoot = function () {
			var data = $("#treeGrid").jqxTreeGrid('getRows');
			for ( var x in data) {
				if (!data[x].fromDate) {
					$("#treeGrid").jqxTreeGrid('lockRow', data[x].uid);
				}
			}
		};
		var moveCategory = function(productCategoryId, primaryParentCategoryId) {
			DataAccess.execute({
		url: "moveProductCategoryAjax",
		data: {	productCategoryId: productCategoryId,
						primaryParentCategoryId: primaryParentCategoryId,
						productCategoryTypeId: "CATALOG_CATEGORY"}},
		Categories.notify);
			render();
		}
		var notify = function(res) {
			$('#jqxNotificationNested').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
				$("#notificationContentNested").text(multiLang.updateError);
				$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: 'info'});
				$("#notificationContentNested").text(multiLang.updateSuccess);
				$("#jqxNotificationNested").jqxNotification("open");
			}
		};
		var fixme = function() {
			return DataAccess.execute({
				url: "fixCategory",
				data: {}});
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				render();
				AddCatagory.init();
				Products.init();
			},
			notify: notify,
			render: render,
			moveCategory: moveCategory,
			fixme: fixme
		};
	})();
}