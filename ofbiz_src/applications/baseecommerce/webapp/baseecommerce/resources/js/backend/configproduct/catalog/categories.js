if (typeof (Categories) == "undefined") {
	var Categories = (function() {
		var initJqxElements = function() {
			$("#jqxwindowViewListCategories").jqxWindow({ theme: 'olbius',
				width: 950, maxWidth: 1845, minHeight: 310, height: 450, resizable: false,  isModal: true, autoOpen: false,
				cancelButton: $("#cancelViewListCategories"), modalOpacity: 0.7
			});
			$("#jqxNotificationCategories").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {

		};
		var initValidator = function() {

		};
		var render = function(prodCatalogId) {
			var source =
	        {
	            datatype: "json",
	            datafields:
	            [
	               { name: 'prodCatalogId', type: 'string'},
				   { name: 'productCategoryId', type: 'string'},
				   { name: 'prodCatalogCategoryTypeId', type: 'string'},
				   { name: 'categoryName', type: 'string'},
				   { name: 'sequenceNum', type: 'number'},
				   { name: "fromDate", type: "date", other: "Timestamp"}
	            ],
	            url: "getCategoriesByProdCatalogId?prodCatalogId=" + prodCatalogId,
	            async: false,
	            addrow: function (rowid, rowdata, position, commit) {
		            	rowdata.prodCatalogCategoryTypeId = "PCCT_BROWSE_ROOT";
		                commit(DataAccess.execute({
								url: "addProductCategoryToProdCatalogAjax",
								data: rowdata},
								Categories.notify));
	            },
	            deleterow: function (rowid, commit) {
					var data = $('#jqxgridViewListCategories').jqxGrid('getrowdatabyid', rowid);
						commit(DataAccess.execute({
							url: "removeProductCategoriesFromProdCatalogAjax",
							data: data},
							Categories.notify));
	            },
	            updaterow: function (rowid, newdata, commit) {
					if (typeof (newdata.fromDate) == 'object') {
						var originFromDate = newdata.fromDate.time;
						newdata.fromDate?newdata.fromDate=new Date(newdata.fromDate.time).toSQLTimeStamp():newdata.fromDate;
						var sec = originFromDate - new Date(newdata.fromDate).getTime();
						if (sec < 100) {
							if (sec < 10) {
								sec = "00" + sec;
							} else {
								sec = "0" + sec;
							}
						}
						newdata.fromDate = newdata.fromDate + '.' + sec;
					}
	                commit(DataAccess.execute({
						url: "updateProductCategoryToProdCatalogAjax",
						data: newdata},
						Categories.notify));
	            },
	            id: 'productCategoryId'
	        };
	        var dataAdapter = new $.jqx.dataAdapter(source);
	        $("#jqxgridViewListCategories").jqxGrid({
	            source: dataAdapter,
	            localization: getLocalization(),
	            showfilterrow: true,
	            filterable: true,
	            editable: true,
				width: '100%',
	            height: '90%',
	            pagesize: 10,
	            pageable: true,
	            theme: 'olbius',
	            sortable: true,
	            selectionmode: 'singlerow',
	            columns: [
						{ text: multiLang.DmsProdCatalogId, datafield: 'prodCatalogId', width: 200, editable: false},
						{ text: multiLang.DmsCategoryId, datafield: 'productCategoryId', width: 200, editable: false},
						{ text: multiLang.DmsCategoryName, datafield: 'categoryName', editable: false},
						{ text: multiLang.BSSequenceNumber, datafield: 'sequenceNum', width: 100}
		            ],
	            handlekeyboardnavigation: function (event) {
		                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
		                if (key == 70 && event.ctrlKey) {
							$('#jqxgridViewListCategories').jqxGrid('clearfilters');
							return true;
		                }
					}
	        });
		};
		var open = function(prodCatalogId) {
			render(prodCatalogId);
			var wtmp = window;
			var tmpwidth = $('#jqxwindowViewListCategories').jqxWindow('width');
	        $("#jqxwindowViewListCategories").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowViewListCategories").jqxWindow('open');
		};
		var notify = function(res) {
			$('#jqxNotificationCategories').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationCategories").jqxNotification({ template: 'error'});
				$("#notificationContentCategories").text(multiLang.updateError);
				$("#jqxNotificationCategories").jqxNotification("open");
			}else {
				$("#jqxNotificationCategories").jqxNotification({ template: 'info'});
				$("#notificationContentCategories").text(multiLang.updateSuccess);
				$("#jqxNotificationCategories").jqxNotification("open");
				setTimeout(function() {
					$('#jqxgridViewListCategories').jqxGrid('updatebounddata');
				}, 100);
			}
		};
		var _delete = function() {
			var rowIndexEditing = $('#jqxgridViewListCategories').jqxGrid('getSelectedRowindex');
			if (rowIndexEditing != -1) {
				bootbox.confirm(multiLang.ConfirmDelete, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
					if (result) {
					var productCategoryId = $('#jqxgridViewListCategories').jqxGrid('getcellvalue', rowIndexEditing, "productCategoryId" );
						$('#jqxgridViewListCategories').jqxGrid('deleterow', productCategoryId);
					}
				});
			} else {
				bootbox.alert("Choose Category To Delete");
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
				AddCategory.init();
			},
			open: open,
			notify: notify,
			_delete: _delete
		};
	})();
}