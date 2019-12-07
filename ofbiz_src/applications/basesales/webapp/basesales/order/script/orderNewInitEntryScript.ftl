<@jqGridMinimumLib />
<@jqOlbCoreLib hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	var OlbSalesOrderInitEntry = (function(){
		var productStoreDDL;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initComplexElement();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowInitEntry"), {width: 600, height: 200});
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		};
		var initComplexElement = function(){
			var configProductStore = {
				width: '98%',
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListProductStoreBySeller',
				key: 'productStoreId',
				value: 'storeName',
				autoDropDownHeight: true
			}
			productStoreDDL = new OlbDropDownList($("#wn_productStoreId"), null, configProductStore, [<#if defaultProductStoreId?exists>'${defaultProductStoreId?if_exists}'</#if>]);
		};
		var initEvent = function(){
			$("#alterSave").on("click", function(){
				if (!validatorVAL.validate()) return false;
				jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToCreate}", 
					function(){
						$.ajax({
							type: 'POST',
							url: 'setProductStoreRoleCustomer',
							data: {
								partyId: "${parameters.partyId?if_exists}",
								productStoreId: productStoreDDL.getValue()
							},
							beforeSend: function(){
								$("#loader_page_common").show();
							},
							success: function(data){
								jOlbUtil.processResultDataAjax(data, 
									function(data, errorMessage){
							        	$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'error'});
							        	$("#jqxNotification").html(errorMessage);
							        	$("#jqxNotification").jqxNotification("open");
							        	
							        	return false;
									}, function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
							        	$("#jqxNotification").jqxNotification("open");
							        	
							        	var url = "newSalesOrder?partyId=${parameters.partyId?if_exists}";
							        	<#if parameters.agreementId?exists>url += "&agreementId=${parameters.agreementId}"</#if>
							        	var win = window.open(url, '_self');
										win.focus();
									}
								);
							},
							error: function(data){
								alert("Send request is error");
							},
							complete: function(data){
								$("#loader_page_common").hide();
							},
						});
					}
				);
			});
			$("#alterCancel").on("click", function(){
				var win = window.open("newSalesOrder", '_self');
				win.focus();
			});
		};
		var initValidateForm = function(){
			var mapRules = [{input: '#wn_productStoreId', type: 'validInputNotNull', objType: 'dropDownList'}];
			validatorVAL = new OlbValidator($('#alterpopupWindowInitEntry'), mapRules, [], {position: 'bottom'});
		};
		return {
			init: init,
		};
	}());
	$(function(){
		OlbSalesOrderInitEntry.init();
		$("#alterpopupWindowInitEntry").jqxWindow("open");
	});
</script>