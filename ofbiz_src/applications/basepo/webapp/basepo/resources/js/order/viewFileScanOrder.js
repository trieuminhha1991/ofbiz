$(function() {
	pageViewFileScan.init();
});

var pageViewFileScan = (function() {
	var init = function() {
		initElement();
		initEvent();
		initAttachFile();
	};
	var initElement = function() {
		$("#jqxFileScanUpload").jqxWindow({
			width : 400,
			modalZIndex : 10000,
			height : 220,
			isModal : true,
			autoOpen : false,
			theme : "olbius",
			cancelButton : $("#uploadCancelButton")
		});
		$("#orderHeaderBtn").jqxDropDownButton({
			theme : "olbius",
			dropDownHorizontalAlignment : "left",
			width : "100%",
			dropDownWidth : 350
		});
	};

	var initEvent = function() {
		$("#searchImages").on("click", function() {
			searchImgForOrder();
		});

		$("#uploadOkButton").click(function() {
			saveFileUpload();
		});

		$("#uploadImages").on("click", function() {
			var orderId = $("#orderHeaderId").val();
			if (orderId == null || orderId == "") {
				bootbox.dialog(messageError, [ {
					"label" : "OK",
					"class" : "btn-mini btn-primary width60px",
				} ]);
				return false;
			} else {
				$("#jqxFileScanUpload").jqxWindow("open");
			}
		});

	};

	// var checkExistsOrderId = function() {
	// var row = $("#listFileScan").jqxGrid("getdatarow", 0);
	// // if()
	// };

	var searchImgForOrder = function() {
		var orderId = $("#orderHeaderId").val();
		$("#orderIdLabel").text(orderId);
		var tmpS = $("#listFileScan").jqxGrid("source");
		tmpS._source.url = "jqxGeneralServicer?sname=getListImagesByOrderId&orderId="
				+ orderId;
		$("#listFileScan").jqxGrid("source", tmpS);
	};

	var initAttachFile = function() {
		$("#attachFile").html("");
		listImage = [];
		$("#attachFile")
				.ace_file_input(
						{
							style : "well",
							btn_choose : "click here",
							btn_change : null,
							no_icon : "icon-cloud-upload",
							droppable : true,
							onchange : null,
							thumbnail : "small",
							before_change : function(files, dropped) {
								listImage = [];
								var count = files.length;
								for (var int = 0; int < files.length; int++) {
									var imageName = files[int].name;
									var hashName = imageName.split(".");
									var extended = hashName.pop();
									if (imageName.length > 50) {
										bootbox
												.dialog(
														NameOfImagesMustBeLessThan50Character,
														[ {
															"label" : uiLabelMap.OK,
															"class" : "btn btn-primary standard-bootbox-bt",
															"icon" : "fa fa-check",
														} ]);
										return false;
									} else {
										if (extended == "JPG"
												|| extended == "jpg"
												|| extended == "jpeg"
												|| extended == "gif"
												|| extended == "png") {
											listImage.push(files[int]);
										}
									}
								}
								return true;
							},
							before_remove : function() {
								listImage = [];
								return true;
							}
						});
	};

	var saveFileUpload = function() {
		Loading.show("loadingMacro");
		setTimeout(
				function() {
					var folder = "/basePurchaseOrder/purchaseorder";
					for ( var d in listImage) {
						var file = listImage[d];
						if (file) {
							var dataResourceName = file.name;
							if (dataResourceName
									&& dataResourceName.length > 50) {
								bootbox
										.dialog(
												NameOfImagesMustBeLessThan50Character,
												[ {
													"label" : uiLabelMap.OK,
													"class" : "btn btn-primary standard-bootbox-bt",
													"icon" : "fa fa-check",
												} ]);
								return false;
							}
							var path = "";
							var orderId = $("#orderHeaderId").val();
							var form_data = new FormData();
							form_data.append("uploadedFile", file);
							form_data.append("folder", folder);
							form_data.append("orderId", orderId);
							jQuery.ajax({
								url : "uploadMultiImagesForOrder",
								type : "POST",
								data : form_data,
								cache : false,
								contentType : false,
								processData : false,
								success : function(res) {
									searchImgForOrder();
									// path = res.path;
									// pathScanFile = path;
									// $("#linkId").html("");
									// $("#linkId").attr("onclick", null);
									// $("#linkId").append("<a href=""+path+""
									// onclick="" target="_blank"><i
									// class="fa-file-image-o"></i>"+"scan"+"</a>
									// <a
									// onclick="javascript:PODlvObj.removeScanFile()"><i
									// class="fa-remove"></i></a>");
								}
							}).done(function() {
							});
						}
					}
					Loading.hide("loadingMacro");
					$("#jqxFileScanUpload").jqxWindow("close");
				}, 500);
	};
	return {
		init : init
	};

}());