if (typeof (ProductImage) == "undefined") {
	var ProductImage = (function() {
		var grid;
		var additionalImages = new Object();
		var initJqxElements = function() {
		    $("#jqxwindowViewImage").jqxWindow({
				theme: 'olbius', width: 700, maxWidth: 2000, height: 420, maxHeight: 1000, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#btnCancel"), modalOpacity: 0.7
			});
		    $("#jqxNotificationNestedSlide").jqxNotification({ width: "100%", appendContainer: "#containerSlide", opacity: 0.9, autoClose: true, template: "info" });
		    $("#contextMenuSlide").jqxMenu({ theme: 'olbius', width: 230, autoOpenPopup: false, mode: 'popup', popupZIndex: 999999});

		};
		var handleEvents = function() {
			$("#addImage").click(function() {
				$("#txtSlideImage").click();
			});
			$("#txtSlideImage").change(function(){
				Images.readURL(this, $('#previewImage'));
				ProductImage.open();
			});
			$("#btnSaveImage").click(function() {
				if ($('#txtSlideImage').prop('files')[0]) {
					var url = DataAccess.uploadFile($('#txtSlideImage').prop('files')[0]);
					grid.jqxGrid('addrow', null, { productId: productId, originalImageUrl: url, url: $('#txtUrl').val() });
					$("#jqxwindowViewImage").jqxWindow('close');
				}
			});
			$('#jqxwindowViewImage').on('close', function (event) {
				$('#txtSlideImage').val(null);
				$('#txtUrl').val("");
				$('#previewImage').attr('src', "/poresources/logo/product_demo_large.png");
				$("#btnSaveImage").removeClass('hidden');
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
						$('#previewImage').attr('src', originalImageUrl);
						$("#btnSaveImage").addClass('hidden');
						ProductImage.open();
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
		var open = function() {
			var wtmp = window;
			var tmpwidth = $('#jqxwindowViewImage').jqxWindow('width');
	        $("#jqxwindowViewImage").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowViewImage").jqxWindow('open');
		};
		var notify = function(res) {
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
		};
		var renderPreview = function() {
			SlidePreview.render(DataAccess.getData({
						url: "JQGetSlideOfProduct?productId=" + productId + "&type=preview",
						data: {},
						source: "listSlide"}));
		};
		var renderSlide = function(productId) {
			var source =
		    {
				datatype: "json",
		        datafields:
		        [
				{ name: 'productId', type: 'string'},
				{ name: 'contentId', type: 'string'},
				{ name: 'url', type: 'string'},
				{ name: 'originalImageUrl', type: 'string'},
				{ name: 'statusId', type: 'string'}
		        ],
		        url: "JQGetSlideOfProduct?productId=" + productId,
		        id: 'contentId',
		        addrow: function (rowid, rowdata, position, commit) {

		            commit(DataAccess.execute({
						url: "addImageToSlideOfProduct",
						data: rowdata},
						ProductImage.notify));
		        },
		        deleterow: function (rowid, commit) {

				commit(true);
		        },
		        updaterow: function (rowid, newdata, commit) {

		            commit(DataAccess.execute({
						url: "updateContent",
						data: newdata},
						ProductImage.notify));
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
						{text: multiLang.BSSlideName, datafield: '', pinned: true, width: 200, editable: false,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>Slide ' + (row + 1) + '</div>';
						    }
						},
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
		};
		var setValue = function(data) {
			if (data.largeImageUrl) {
				$('#largeImage').attr('src', encodeURI(data.largeImageUrl));
			}
			if (data.smallImageUrl) {
				$('#smallImage').attr('src', encodeURI(data.smallImageUrl));
			}
			if (data.ADDITIONAL_IMAGE_1) {
				$('#additional1').attr('src', encodeURI(data.ADDITIONAL_IMAGE_1));
			}
			if (data.ADDITIONAL_IMAGE_2) {
				$('#additional2').attr('src', encodeURI(data.ADDITIONAL_IMAGE_2));
			}
			if (data.ADDITIONAL_IMAGE_3) {
				$('#additional3').attr('src', encodeURI(data.ADDITIONAL_IMAGE_3));
			}
			if (data.ADDITIONAL_IMAGE_4) {
				$('#additional4').attr('src', encodeURI(data.ADDITIONAL_IMAGE_4));
			}
			additionalImages.ADDITIONAL_IMAGE_1Id = data.ADDITIONAL_IMAGE_1Id;
			additionalImages.ADDITIONAL_IMAGE_2Id = data.ADDITIONAL_IMAGE_2Id;
			additionalImages.ADDITIONAL_IMAGE_3Id = data.ADDITIONAL_IMAGE_3Id;
			additionalImages.ADDITIONAL_IMAGE_4Id = data.ADDITIONAL_IMAGE_4Id;
			if (data.productId) {
				renderSlide(data.productId);
			}
		};
		var getValue = function() {
			var value = new Object();
			if ($('#txtLargeImage').prop('files')[0]) {
				value.largeImageUrl = $('#txtLargeImage').prop('files')[0];
			}
			if ($('#txtSmallImage').prop('files')[0]) {
				value.smallImageUrl = $('#txtSmallImage').prop('files')[0];
			}
			if ($('#txtAdditional1').prop('files')[0]) {
				value.ADDITIONAL_IMAGE_1 = $('#txtAdditional1').prop('files')[0];
			}
			if ($('#txtAdditional2').prop('files')[0]) {
				value.ADDITIONAL_IMAGE_2 = $('#txtAdditional2').prop('files')[0];
			}
			if ($('#txtAdditional3').prop('files')[0]) {
				value.ADDITIONAL_IMAGE_3 = $('#txtAdditional3').prop('files')[0];
			}
			if ($('#txtAdditional4').prop('files')[0]) {
				value.ADDITIONAL_IMAGE_4 = $('#txtAdditional4').prop('files')[0];
			}
			var value = _.extend(value, additionalImages);
			return value;
		};
		return {
			init: function() {
				grid = $("#slideGrid");
				initJqxElements();
				handleEvents();
				Images.init();
			},
			open: open,
			notify: notify,
			setValue: setValue,
			getValue: getValue
		};
	})();
}
if (typeof (Images) == "undefined") {
	var Images = (function() {
		var handleEvents = function() {
			$("#largeImage").click(function() {
				$("#txtLargeImage").click();
			});
			$("#txtLargeImage").change(function(){
				Images.readURL(this, $('#largeImage'));
			});
			$("#smallImage").click(function() {
				$("#txtSmallImage").click();
			});
			$("#txtSmallImage").change(function(){
				Images.readURL(this, $('#smallImage'));
			});


			$("#additional1").click(function() {
				$("#txtAdditional1").click();
			});
			$("#txtAdditional1").change(function(){
				Images.readURL(this, $('#additional1'));
			});
			$("#additional2").click(function() {
				$("#txtAdditional2").click();
			});
			$("#txtAdditional2").change(function(){
				Images.readURL(this, $('#additional2'));
			});
			$("#additional3").click(function() {
				$("#txtAdditional3").click();
			});
			$("#txtAdditional3").change(function(){
				Images.readURL(this, $('#additional3'));
			});
			$("#additional4").click(function() {
				$("#txtAdditional4").click();
			});
			$("#txtAdditional4").change(function(){
				Images.readURL(this, $('#additional4'));
			});
		};
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
			init: function() {
				handleEvents();
			},
			readURL: readURL
		};
	})();
}