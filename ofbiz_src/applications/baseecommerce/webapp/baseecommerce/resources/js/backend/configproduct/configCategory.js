$(document).ready(function() {
	SlideGrid.init();
});
if (typeof (SlideGrid) == "undefined") {
	var SlideGrid = (function() {
		var grid;
		var initJqxElements = function() {
			var source =
		    {
				datatype: "json",
		        datafields:
		        [
				{ name: 'contentId', type: 'string'},
				{ name: 'productCategoryId', type: 'string'},
				{ name: 'url', type: 'string'},
				{ name: 'originalImageUrl', type: 'string'},
				{ name: 'statusId', type: 'string'}
		        ],
		        url: url,
		        id: 'contentId',
		        addrow: function (rowid, rowdata, position, commit) {
		            commit(DataAccess.execute({
						url: addUrl,
						data: rowdata},
						SlideGrid.notify));
		        },
		        deleterow: function (rowid, commit) {
				commit(true);
		        },
		        updaterow: function (rowid, newdata, commit) {
		            commit(DataAccess.execute({
						url: "updateContent",
						data: newdata},
						SlideGrid.notify));
		        }
		    };
		    var dataAdapter = new $.jqx.dataAdapter(source);
		    grid.jqxGrid({
		    	localization: getLocalization(),
		        width: '100%',
		        theme: 'olbius',
		        pageable: true,
		        pagesize: 10,
		        editable: true,
		        rowsheight: 60,
		        autoheight: true,
		        source: dataAdapter,
		        editmode: 'dblclick',
		        selectionmode: 'singlerow',
		        columns: [
						{text: multiLang.DmsSequenceId, datafield: '', pinned: true, width: 200, editable: false,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>Item ' + (row + 1) + '</div>';
						    }
						},
						{ text: multiLang.DmsCategoryName, datafield: 'productCategoryId', width: 200, editable: false},
						{ text: multiLang.BSLink, datafield: 'url'},
						{ text: multiLang.BSStatus, datafield: 'statusId', editable: false, width: 200,
							cellsrenderer: function(row, colum, value){
								value?value=mapStatusItem[value]:value;
								return '<span>' + value + '</span>';
							}
						},
						{ text: multiLang.BSThumbnail, datafield: 'originalImageUrl', editable: false, width: 180,
							cellsrenderer: function(row, colum, value){
								var img = "<img class='thumbnail' src=" + value + " />";
								return img;
							}
						}],
				handlekeyboardnavigation: function (event) {
	                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
	                if (key == 70 && event.ctrlKey) {
						grid.jqxGrid('clearfilters');
						return true;
	                }
				}
		    });
		    $("#jqxwindowViewImage").jqxWindow({
				theme: 'olbius', width: 700, maxWidth: 2000, height: 430, maxHeight: 1000, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#btnCancel"), modalOpacity: 0.7
			});
		    $("#jqxNotificationNestedSlide").jqxNotification({ width: "100%", appendContainer: "#containerSlide", opacity: 0.9, autoClose: true, template: "info" });
		    $("#contextMenuSlide").jqxMenu({ theme: 'olbius', width: 230, autoOpenPopup: false, mode: 'popup', popupZIndex: 999999});

		};
		var handleEvents = function() {
			$("#largeImage").click(function() {
				$("#txtLargeImage").click();
			});
			$("#txtLargeImage").change(function(){
				Images.readURL(this, $('#largeImage'));
			});
			$("#btnSaveConfig").click(function() {
				saveImage();
			});
			$("#addImage").click(function() {
				$("#txtSlideImage").click();
			});
			$("#txtSlideImage").change(function(){
				Images.readURL(this, $('#previewImage'));
				SlideGrid.open();
			});
			$("#btnSaveImage").click(function() {
				if ($('#txtSlideImage').prop('files')[0]) {
					var url = DataAccess.uploadFile($('#txtSlideImage').prop('files')[0]);
					grid.jqxGrid('addrow', null, { originalImageUrl: url, url: $('#txtUrl').val(), productCategoryId: productCategoryId });
					$("#jqxwindowViewImage").jqxWindow('close');
				}
			});
			$('#jqxwindowViewImage').on('close', function (event) {
				$('#txtSlideImage').val(null);
				$('#txtUrl').val("");
				$('#previewImage').attr('src', "/poresources/logo/product_demo_large.png");
				$("#btnSaveImage").removeClass('hidden');
				$("#btnCancel").html("<i class='icon-remove'></i>" + multiLang.CommonCancel);
			});

			grid.on('contextmenu', function () {
                return false;
            });
			grid.on('rowclick', function (event) {
                if (event.args.rightclick) {
			grid.jqxGrid('selectrow', event.args.rowindex);
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    $("#contextMenuSlide").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
                }
            });
			$('body').on('click', function() {
				$("#contextMenuSlide").jqxMenu('close');
			});
			$("#contextMenuSlide").on('itemclick', function (event) {
		        var args = event.args;
		        var itemId = $(args).attr('id');
		        switch (itemId) {
				case "viewImage":
					var rowIndexSelected = grid.jqxGrid('getSelectedRowindex');
					var rowData = grid.jqxGrid('getrowdata', rowIndexSelected);
					var originalImageUrl = rowData.originalImageUrl;
					$('#txtUrl').val(rowData.url);
					if (originalImageUrl) {
						$("#btnSaveImage").addClass('hidden');
						$("#btnCancel").html("<i class='icon-remove'></i>" + multiLang.CommonClose);
						$('#previewImage').attr('src', originalImageUrl);
						SlideGrid.open();
					}
					break;
				case "activateSlide":
					var rowIndexSelected = grid.jqxGrid('getSelectedRowindex');
					var rowData = grid.jqxGrid('getrowdata', rowIndexSelected);
					var statusId = rowData.statusId;
					var contentId = rowData.contentId;
					if (statusId == 'CTNT_PUBLISHED') {
						grid.jqxGrid('setcellvaluebyid', contentId, "statusId", "CTNT_DEACTIVATED");
					} else {
						grid.jqxGrid('setcellvaluebyid', contentId, "statusId", "CTNT_PUBLISHED");
					}
					break;
				default:
					break;
				}
			});
			$("#contextMenuSlide").on('shown', function () {
				var rowIndexSelected = grid.jqxGrid('getSelectedRowindex');
				var rowData = grid.jqxGrid('getrowdata', rowIndexSelected);
				var statusId = rowData.statusId;
				if (statusId == 'CTNT_PUBLISHED') {
					$("#activateSlide").html("<i class='fa-frown-o'></i>&nbsp;&nbsp;" + multiLang.DmsDeactivate);
				} else {
					$("#activateSlide").html("<i class='fa-smile-o'></i>&nbsp;&nbsp;" + multiLang.DmsActive);
				}
			});
		};
		var loadCategoryImage = function(productCategoryId) {
			var categoryConfig = DataAccess.getData({
					url: "loadCategoryImage",
					data: {productCategoryId: productCategoryId},
					source: "categoryConfig"});

			if (categoryConfig["categoryImageUrl"]) {
				$('#largeImage').attr('src', encodeURI(categoryConfig["categoryImageUrl"]));
			}
			$("#txtIcon").val(categoryConfig["icon"]);
			$("#txtLink").val(categoryConfig["url"]);
		};
		var saveImage = function() {
			var data = new Object();
			if ($('#txtLargeImage').prop('files')[0]) {
				var categoryImageUrl = DataAccess.uploadFile($('#txtLargeImage').prop('files')[0]);
				data.categoryImageUrl = categoryImageUrl;
			}
			data.productCategoryId = productCategoryId;
			data.productCategoryTypeId = "CATALOG_CATEGORY";
			data.icon = $("#txtIcon").val();
			data.url = $("#txtLink").val();
			DataAccess.execute({
				url: "updateProductCategoryAjax",
				data: data },
				SlideGrid.notify);
		};
		var open = function() {
			var wtmp = window;
			var tmpwidth = $('#jqxwindowViewImage').jqxWindow('width');
	        $("#jqxwindowViewImage").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowViewImage").jqxWindow('open');
		};
		var notify = function(res) {
			$(window).scrollTop(0);
			$('#jqxNotificationNestedSlide').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationNestedSlide").jqxNotification({ template: 'error'});
				$("#notificationContentNestedSlide").text(multiLang.updateError);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			}else {
				$("#jqxNotificationNestedSlide").jqxNotification({ template: 'info'});
				$("#notificationContentNestedSlide").text(multiLang.updateSuccess);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			}
			grid.jqxGrid('updatebounddata');
			if (typeof (renderPreview) == "function") {
				renderPreview();
			}
		};
		var initScrollDetector = function() {
			$(window).scroll(function() {
				$(".fontawesome-icon-list").removeClass("freeze");
				var distance = $(window).scrollTop() - $(".fontawesome-icon-list").offset().top;
				if (-45 < distance && distance < 250) {
					$(".fontawesome-icon-list").addClass("freeze");
			    }
			});
		};
		var handlerIconChoice = function() {
			$(".fontawesome-icon-list .fa-hover a").click(function(e) {
				if (e.currentTarget) {
					if (e.currentTarget.firstElementChild) {
						if (e.currentTarget.firstElementChild.className) {
							$("#txtIcon").val(e.currentTarget.firstElementChild.className);
						}
					}
				}
			});
		};
		return {
			init: function() {
				grid = $("#slideGrid");
				initJqxElements();
				handleEvents();
				initScrollDetector();
				handlerIconChoice();
				if (typeof (renderPreview) == "function") {
					renderPreview();
				}
				if (productCategoryId) {
					loadCategoryImage(productCategoryId);
				}
			},
			open: open,
			notify: notify,
			loadCategoryImage: loadCategoryImage
		};
	})();
}
if (typeof (Images) == "undefined") {
	var Images = (function() {
		var readURL = function(input, img) {
			if (input.files && input.files[0]) {
		        var reader = new FileReader();
		        reader.onload = function (e) {
		            img.attr('src', e.target.result);
		        }
		        reader.readAsDataURL(input.files[0]);
		    }
		};
		return {
			readURL: readURL,
		};
	})();
}
