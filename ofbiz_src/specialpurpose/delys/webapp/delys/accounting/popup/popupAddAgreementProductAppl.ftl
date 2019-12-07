 <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
	    			<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.accProductName}
	    				</div>
	    				<div class='span7'>
							<div id="productIdAdd">
			 					<div id="jqxProductGrid"></div>
			 				</div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.unitPrice}
	    				</div>
	    				<div class='span7'>
							<div id="priceAdd">
	 						</div>
	    				</div>
					</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>					

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
//Create theme
 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var popupAction = (function(){
		var initElement = function(){
			$("#priceAdd").jqxNumberInput({spinMode: 'simple', width: 250, height: 25, min: 0,max : 999999999999,decimalDigits: 0,digits : 15, spinButtons: false});
			$("#alterpopupWindow").jqxWindow({
			        width: 600,height : 200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
		    });
			initGridProduct();
		}
		var initGridProduct = function(){
				var datafields = [
						      { name: 'productId', type: 'string' },
						      { name: 'productName', type: 'string' }
				];
				var columns = [
					{ text: '${uiLabelMap.accProductId}', datafield: 'productId', width: '50%'},
					{ text: '${uiLabelMap.accProductName}', datafield: 'productName'}
				];
				GridUtils.initDropDownButton({url: 'JQGetListProducts',width : 500,filterable : true,dropdown : {width : 250,height :25}},
					datafields,columns,null,$('#jqxProductGrid'),$('#productIdAdd'),'productId');
		}
		
		var save = function(){
			if(!$('#formAdd').jqxValidator('validate')){return;};
			var row;
	        row = { 
	        		productId:$('#productIdAdd').jqxDropDownButton('val'), 
	        		price:$('#priceAdd').jqxNumberInput('val')
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);          		 
			return true;
		}
		
		var saveAndContinue  = function(){
			save();
			clear();
		}
		var clear = function(){
			$("#priceAdd").jqxNumberInput('clear');	
			$("#productIdAdd").jqxDropDownButton('val', '');	
			$('#jqxProductGrid').jqxGrid('clearSelection'); 
		};
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#productIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(){
						var val = $('#productIdAdd').val();
						if(!val) return false;
						return true;
					}},
					{input : '#productIdAdd',message : '${StringUtil.wrapString(uiLabelMap.NotiProductsonAgreement?default(''))}',action : 'change,close',rule : function(){
						if(!filterProductsAdd()) return false;
						return true;
					}}
				]
			})
			
		}
		
		var bindEvent = function(){
		    $("#save").click(function () {
		    	if(!save()){
		    		return false;
		    	}
		    	clear();
		    	$("#alterpopupWindow").jqxWindow('close');
		    });
			
			$('#saveAndContinue').click(function(){
				saveAndContinue();
			});
			
			$('#cancel').click(function(){
				clear();
			})
		}
		
		var getDataAdd = function(){
			var row = {
				productId:$('#productIdAdd').jqxDropDownButton('val')
			}
			return row;
		}
		
		var filterProductsAdd = function(){
			var listAgreementProducts = $('#jqxgrid').jqxGrid('getboundrows');
			var dataAdd = getDataAdd();
			<#if parameters.agreementId?exists>
				dataAdd.agreementId = '${parameters.agreementId?if_exists}';
			<#else>
				dataAdd.agreementId = "";
			</#if>
			<#if parameters.agreementItemSeqId?exists>
				dataAdd.agreementItemSeqId = '${parameters.agreementItemSeqId?if_exists}';
			<#else>
				dataAdd.agreementItemSeqId = "";	
			</#if>
			if(listAgreementProducts && listAgreementProducts.length > 0 && typeof(dataAdd) != 'undefined' && dataAdd != null ){
				var result = false;
				$.each(listAgreementProducts,function(){
					if($(this)[0].agreementId == dataAdd.agreementId && $(this)[0].agreementItemSeqId == dataAdd.agreementItemSeqId && $(this)[0].productId == dataAdd.productId){
						result = true;
						return false;
					}
				})
			}
			if(result) return false;
			return true;
		}
		
		
		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			},
			filterProductsAdd : filterProductsAdd
		}
	}());
	
   $(document).ready(function(){
   		popupAction.init();
   })
</script>