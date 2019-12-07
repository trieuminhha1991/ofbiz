var voucherInvoiceViewObj = (function(){
	var init = function(){
		initColorbox();
		initElement();
		initEvent();
	};
	
	var initColorbox = function(){
		var colorbox_params = {
				reposition:true,
				scalePhotos:true,
				scrolling:false,
				previous:'<i class="icon-arrow-left"></i>',
				next:'<i class="icon-arrow-right"></i>',
				close:'&times;',
				current:'{current} of {total}',
				maxWidth:'100%',
				maxHeight:'86%',
				onOpen:function(){
					document.body.style.overflow = 'hidden';
				},
				onClosed:function(){
					document.body.style.overflow = 'auto';
				},
				onComplete:function(){
					$.colorbox.resize();
				}
			};

			$('[data-rel="colorbox"]').colorbox(colorbox_params);
			$("#cboxLoadingGraphic").append("<i class='icon-spinner orange'></i>");//let's add a custom loading icon

	};
	
	var initElement = function(){
		jOlbUtil.contextMenu.create($("#contextMenu"));
	};
	
	var deleteSuccess = function(){
		Loading.show('loadingMacro');
		if(globalVar.businessType == "AR"){
			window.location.href = 'ViewARInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
		} else {
			window.location.href = 'ViewAPInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
		}
	};
	
	var updateSuccess = function(){
		$.ajax({
			url: 'getInvoiceAndVoucherDiffAmount',
			data: {invoiceId: globalVar.invoiceId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				$("#actualTotalAmount").html(formatcurrency(response.amount, globalVar.currencyUomId));
				$("#diffTotalAnount").html(formatcurrency(response.diffInvoiceAmount, globalVar.currencyUomId));
				$("#actualTaxAmount").html(formatcurrency(response.taxAmount, globalVar.currencyUomId));
				$("#diffTaxAnount").html(formatcurrency(response.diffInvoiceTaxAmount, globalVar.currencyUomId));
			},
			complete: function(){
				
			}
		});
	};
	
	var changeLinkImg = function(row){
		var data = $('#jqxVoucherList').jqxGrid('getrowdata', row);
		$("#viewImgVoucher").prop("href", data.objectInfo);
		$("#viewImgVoucher").trigger('click');
	};
	
	var initEvent = function(){
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var tmpId = $(args).attr('id');
			var idGrid = "#jqxVoucherList";
			
	        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
	        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
	        
	        switch(tmpId) {
	    		case "edit": {
	    			if (rowData) {
						editVoucherObj.openPopupEditVoucher(rowData);
					}
					break;
				};
	    		default: break;
	    	}
	    });
	};
	
	return {
		init: init,
		changeLinkImg: changeLinkImg,
		deleteSuccess: deleteSuccess,
		updateSuccess: updateSuccess
	}
}());

$(document).ready(function () {
	voucherInvoiceViewObj.init();
});