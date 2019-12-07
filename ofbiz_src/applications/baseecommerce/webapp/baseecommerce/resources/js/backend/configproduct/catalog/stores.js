if (typeof (Stores) == "undefined") {
	var Stores = (function() {
		var initJqxElements = function() {
			$("#jqxwindowStores").jqxWindow({
				theme: 'olbius', width: 950, maxWidth: 1845, minHeight: 310, height: 450, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelStores"), modalOpacity: 0.7
			});

			$("#jqxNotificationStores").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
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
	               { name: 'productStoreId', type: 'string'},
	               { name: 'prodCatalogId', type: 'string'},
				   { name: 'storeName', type: 'string'},
				   { name: 'payToPartyId', type: 'string'},
				   { name: 'defaultCurrencyUomId', type: 'string'},
				   { name: 'fromDate', type: 'date', other: 'Timestamp'},
				   { name: 'thruDate', type: 'date', other: 'Timestamp'},
				   { name: 'sequenceNum', type: 'number'}
	            ],
	            url: "getStoreListByProdCatalogId?prodCatalogId=" + prodCatalogId,
	            async: false,
	            addrow: function (rowid, rowdata, position, commit) {
			commit(DataAccess.execute({
					url: "createProductStoreCatalogAjax",
					data: rowdata},
					Stores.notify));
	            },
	            deleterow: function (rowid, commit) {
			var data = $('#jqxgridStores').jqxGrid('getrowdatabyid', rowid);
			if (typeof (data.fromDate) == 'object') {
				var originFromDate = data.fromDate.time;
				data.fromDate?data.fromDate=new Date(data.fromDate.time).toSQLTimeStamp():data.fromDate;
				var sec = originFromDate - new Date(data.fromDate).getTime();
				if (sec < 100) {
					if (sec < 10) {
						sec = "00" + sec;
					} else {
						sec = "0" + sec;
					}
				}
				data.fromDate = data.fromDate + '.' + sec;
			}
			commit(DataAccess.execute({
				url: "deleteProductStoreCatalogAjax",
				data: data},
				Stores.notify));
	            },
	            updaterow: function (rowid, newdata, commit) {
			commit(DataAccess.execute({
				url: "updateProductStoreCatalogAjax",
				data: newdata},
				Stores.notify));
	            },
	            id: 'productStoreId'
	        };
	        var dataAdapter = new $.jqx.dataAdapter(source);
	        $("#jqxgridStores").jqxGrid({
	            source: dataAdapter,
	            localization: getLocalization(),
	            showfilterrow: true,
	            filterable: true,
	            editable:false,
				width: '100%',
	            height: '90%',
	            pagesize: 10,
	            pageable: true,
	            theme: 'olbius',
	            sortable: true,
	            selectionmode: 'singlerow',
	            columns: [
						{ text: multiLang.BSProductStoreId, datafield: 'productStoreId', width: 200},
						{ text: multiLang.DmsStoreName, datafield: 'storeName'},
						{ text: multiLang.DmsdefaultCurrencyUomId, datafield: 'defaultCurrencyUomId', width: 200}
		            ],
	            handlekeyboardnavigation: function (event) {
		                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
		                if (key == 70 && event.ctrlKey) {
					$('#jqxgridStores').jqxGrid('clearfilters');
					return true;
		                }
					}
	        });
		}
		var open = function(prodCatalogId) {
			render(prodCatalogId);
			var wtmp = window;
			var tmpwidth = $('#jqxwindowStores').jqxWindow('width');
	        $("#jqxwindowStores").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowStores").jqxWindow('open');
		};
		var _delete = function() {
			var rowIndexEditing = $('#jqxgridStores').jqxGrid('getSelectedRowindex');
			if (rowIndexEditing != -1) {
				bootbox.confirm(multiLang.ConfirmDelete, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
					if (result) {
					var productStoreId = $('#jqxgridStores').jqxGrid('getcellvalue', rowIndexEditing, "productStoreId" );
						$('#jqxgridStores').jqxGrid('deleterow', productStoreId);
					}
				});
			} else {
				bootbox.alert("Choose Store To Delete");
			}
		};
		var notify = function(res) {
			$('#jqxNotificationStores').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationStores").jqxNotification({ template: 'error'});
			$("#notificationContentStores").text(multiLang.updateError);
			$("#jqxNotificationStores").jqxNotification("open");
			}else {
				$("#jqxNotificationStores").jqxNotification({ template: 'info'});
			$("#notificationContentStores").text(multiLang.updateSuccess);
			$("#jqxNotificationStores").jqxNotification("open");
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
				AddStore.init();
			},
			open: open,
			_delete: _delete,
			notify: notify
		};
	})();
}