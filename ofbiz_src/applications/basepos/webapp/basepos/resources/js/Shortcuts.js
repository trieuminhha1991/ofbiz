function activateHotKeys(){
	preventWindow();
	$('body').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		/*114 is code of F3*/
		if (code == 114) {
			if (flagPopup && flagDupPayCash){
			$("#continueNotPrint").removeAttr("disabled");
			var rows = $("#showCartJqxgrid").jqxGrid('getRows');
			if(rows.length >0){
				for (var i = 0; i < rows.length; i++) {
				    // get a row.
					var rowData = rows[i];
					var quantity = rowData.quantityProduct;
					quantity = parseInt(quantity, 10);
					if(isNaN(quantity)){
						bootbox.alert(BPOSQuantityItemInCartNotValid);
						e.preventDefault();
						return false;
					}
				}
			}else{
				bootbox.hideAll();
				bootbox.alert(BPOSNoAnyItemInCart);
				e.preventDefault();
				return false;
			}
			showPayCash();
		}
		e.preventDefault();
		return false;
		}
	});
	
	$('body').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		/*117 is code of F6*/
		if (code === 117) {
			if (flagPopup){
				var rows = $("#showCartJqxgrid").jqxGrid('getRows');
        		if (rows.length > 0){
        			discountFocus();
        		}
			}
			e.preventDefault();
			return false;
		}
	});
	$('body').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		/*80 is code of P*/
		if (e.ctrlKey && code === 80) {
			if (flagPopup){
				var rows = $("#showCartJqxgrid").jqxGrid('getRows');
				if(rows.length >0){
					if (isPrintBeforePayment == 'Y'){
						getDataForPrint();
						$("#PrintOrder").show();
						$("#PrintOrder").css({
							"z-index" : -1,
							position: "absolute"
						});
						$("#jqxPartyList").jqxComboBox('focus');
						setTimeout(function(){
							var tmpWin = $("#PrintOrder").printArea().win;
							if(tmpWin.matchMedia){
								var printEvent = tmpWin.matchMedia('print');
							    printEvent.addListener(function(printEnd) {
							    	if (!printEnd.matches) {
								    	$("#jqxProductList").jqxComboBox('focus');
								    }
								});
							}
						}, 10);
					}
				} else {
					bootbox.hideAll();
					bootbox.alert(BPOSNoAnyItemInCart);
				}
			}
			e.preventDefault();
			return false;
		}
	});
	
	$('body').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//116 la ma cua F5
		if (code === 116) {
			if (flagPopup){
				var rows = $("#showCartJqxgrid").jqxGrid('getRows');
        		if (rows.length > 0){
        			itemQuantityFocus();
        		}
			}
			e.preventDefault();
			return false;
		}
	});
	
	$('body').keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	   //187 la ma cua + 
        if (code == 187) {
        	if (flagPopup){
        		var rows = $("#showCartJqxgrid").jqxGrid('getRows');
        		if (rows.length > 0){
        			incrementItemQuantityWebPOS();
        		}
        	}
			e.preventDefault();
			return false;
        }
	});

	$('body').keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    // 189 la ma cua -
	    if (code == 189) {
	    	if (flagPopup){
	    		var rows = $("#showCartJqxgrid").jqxGrid('getRows');
        		if (rows.length > 0){
        			decrementItemQuantityWebPOS();
        		}
	    	}
	       e.preventDefault();
	       return false;
	    }
	});
	
	$(document).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//119 la ma cua F8
		if (code === 119) {
			if (flagPopup){
				emptyCartWebPOS();
			}
			e.preventDefault();
			return false;
		}
	});
	
	$('body, #showCartJqxgrid').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//38 is code of up key
		if (code == 38) {
			if (flagPopup){
        		if (document.activeElement.getAttribute("id") != "contentshowCartJqxgrid") {
        			$('#showCartJqxgrid').jqxGrid('focus');
        			// set next
        			// Get current selected index
        			var selectedIndex = $('#showCartJqxgrid').jqxGrid('selectedrowindex');
        			if(selectedIndex > 0){
        				$('#showCartJqxgrid').jqxGrid('selectrow', selectedIndex - 1);
        			}
				}
			}
			e.preventDefault();
			return false;
		}
	});
	
	$('body, #showCartJqxgrid').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//40 is code of down key
		if (code === 40) {
			if (flagPopup){
        		if (document.activeElement.getAttribute("id") != "contentshowCartJqxgrid") {
        			$('#showCartJqxgrid').jqxGrid('focus');
        			// set next
        			// Get current selected index
        			var selectedIndex = $('#showCartJqxgrid').jqxGrid('selectedrowindex');
        			var rows = $("#showCartJqxgrid").jqxGrid('getRows');
        			if(selectedIndex < (rows.length - 1)){
        				$('#showCartJqxgrid').jqxGrid('selectrow', selectedIndex + 1);
        			}
				}
			}
			e.preventDefault();
			return false;
		}
	});
	
	$('body').keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if ((code == 39)||(code == 37)){
			if (flagPopup){
				var getselectedrowindexes = $('#showCartJqxgrid').jqxGrid('getselectedrowindexes');
				if (getselectedrowindexes.length == 0){
					$("#showCartJqxgrid").jqxGrid('begincelledit', 0, "uomId");
				} else {
					$("#showCartJqxgrid").jqxGrid('begincelledit', getselectedrowindexes[0], "uomId");
				}
			}
			e.preventDefault();
			return false;
		}
	});
	
	$('#showHoldCartWindow').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		if (code == 13) {
			loadHoldCart()
			e.preventDefault();
			return false;
		}
//		if (code == 27){
//			var allData = $("#showHoldCartList").jqxGrid('getrows');
//			if (allData.length > 0){
//				var data = $("#showHoldCartList").jqxGrid('getrowdata', allData.length - 1);
//				loadCart(data.id);
//			}
//			e.preventDefault();
//			return false;
//		}
		if ((code == 38)||(code == 40)) {
			$("#showHoldCartList").jqxGrid('focus');
			e.preventDefault();
			return false;
		}
	});
	
	$('#salesHistoryWindow').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//38 is code of up key
		if ((code == 38)||(code == 40)) {
			$("#salesHistory").jqxGrid('focus');
			e.preventDefault();
			return false;
		}
	});
	
	$('body').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//27 is code of ESC
		if (code == 27) {
			if (_.indexOf(LocalConfig.notHideOverlayDiv, document.activeElement.getAttribute("id")) < 0) {
				hideOverlayDiv();
			}
			e.preventDefault();
			return false;
		}
	});

	$('body, #showCartJqxgrid').keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    //46 la ma cua delete
	    if(e.shiftKey && code == 46){
	    	if (flagPopup){
	    		var rowIndex = $("#showCartJqxgrid").jqxGrid('getselectedrowindex');
	    		deleteCartItem(rowIndex);
	    	}
	    	e.preventDefault();
	    	return false;
	    }
	});
	
	$('body').keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    //8 la ma cua backspace
	    if(e.ctrlKey && code == 8 && POSPermission.has("POS_RETURN_CTRL_BACKSPACE", "CREATE")){
	    	if (flagPopup){
	    		processItemDiscount();
	    	}
	    	e.preventDefault();
	    	return false;
	    }
	});
	
	$(document).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
    	//120 la ma cua F9
        if (code == 120) {
        	if (flagPopup){
        		holdCart();
        	}
            e.preventDefault();
			return false;
        }
	});
	
	$('body').keydown(function(e) {
    var code = (e.keyCode ? e.keyCode : e.which);
    	//121 la ma cua f10
        if (code == 121) {
        	if (flagPopup){
        		bootbox.hideAll();
        		Loading.show('loadingMacro');
        		viewHoldCart();
        	}
        }
	});
				
	$('body').keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    	//69 is code of E
	        if (e.ctrlKey && code == 69) {
	        	if (flagPopup){
	        		$('#alterpopupWindowClose').jqxWindow('open');
	        	}
	        	e.preventDefault();
	        	return false;
	        }
	});
	
	$('body').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//67 is code of C
		if (e.ctrlKey && code == 67 && POSPermission.has("POS_PROMOTION_CTRL_C", "CREATE")) {
			if (flagPopup){
				$("#promotionCode").val('');
				$('#alterpopupWindowPromotionCode').jqxWindow('open');
			}
			e.preventDefault();
			return false;
		}
	});
}
	
function preventWindow(){
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 187){e.preventDefault();}
	});
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 189){e.preventDefault();}
	});
	//114 F3
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 114){e.preventDefault();}
	});
	//115 F4
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 115){e.preventDefault();}
	});
	//116 F5
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 116){e.preventDefault();}
	});
	//117 F6
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 117){e.preventDefault();}
	});
	//118 F7
	$(window).keydown(function(e){
		 var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 118){e.preventDefault();}
	});
	//119 F8
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 119){e.preventDefault();}
	});
	//120 F9
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 120){e.preventDefault();}
	});
	//121 F10
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 121){e.preventDefault();}
	});
	$(window).keydown(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(e.ctrlKey && code == 78){e.preventDefault();}
		
	});
	$(window).keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    //49 la ma cua 1
	    if(e.ctrlKey && code == 49){
	    	e.preventDefault();
	    }
	});
	$(window).keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    //50 la ma cua 2
	    if(e.ctrlKey && code == 50){
	    	e.preventDefault();
	    }
	});
	$(window).keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    //46 la ma cua delete
	    if(e.shiftKey && code == 46){
	    	e.preventDefault();
	    }
	});
	$(window).keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    //8 la ma cua backspace
	    if(e.ctrlKey && code == 8){
	    	e.preventDefault();
	    }
	});
	$(window).keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    //79 la ma cua O
	    if(e.ctrlKey && code == 79){
	    	e.preventDefault();
	    }
	});
	$(window).keydown(function(e) {
	    var code = (e.keyCode ? e.keyCode : e.which);
	    //69 la ma cua E
	    if(e.ctrlKey && code == 69){
	    	e.preventDefault();
	    }
	});
	$(window).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//67 la ma cua C
		if(e.ctrlKey && code == 67){
			e.preventDefault();
		}
	});
	$(window).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//73 la ma cua I
		if(e.ctrlKey && code == 73){
			e.preventDefault();
		}
	});
	$(window).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//72 la ma cua H
		if(e.ctrlKey && code == 72){
			e.preventDefault();
		}
	});
	$(window).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//80 la ma cua P
		if(e.ctrlKey && code == 80){
			e.preventDefault();
		}
	});
	$(window).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//76 la ma cua L
		if(e.ctrlKey && code == 76){
			e.preventDefault();
		}
	});
	$(window).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//84 la ma cua T
		if(e.ctrlKey && code == 84){
			e.preventDefault();
		}
	});
	$(window).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//83 la ma cua S
		if(e.ctrlKey && code == 83){
			e.preventDefault();
		}
	});
	$(window).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//78 la ma cua N
		if(e.ctrlKey && code == 78){
			e.preventDefault();
		}
	});
	$(window).keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//71 la ma cua G
		if(e.ctrlKey && code == 71){
			e.preventDefault();
		}
	});
	$(window).keydown(function(e) {
		if (e.which === 8 && !$(e.target).is("input, textarea")) {
			return false;
		}
	});
}	
