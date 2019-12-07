<@jqOlbCoreLib />
<script type="text/javascript">
	if (typeof(OlbContactMechEdit) == 'undefined') {
		var OlbContactMechEdit = (function(){
			var init = function(){
				initElement();
				initEvent();
			};
			var initElement = function(){
				jOlbUtil.windowPopup.create($("#windowEditContactMech"), {maxWidth: 960, width: 960, height: 420, cancelButton: $("#we_alterCancel")});
			};
			var loadContactMechContent = function(contactMechId){
				$.ajax({
					type: 'POST',
					url: 'editContactMechAjax',
					data: {
						contactMechId: contactMechId,
						partyId: jOlbUtil.getAttrDataValue('shipToCustomerPartyId')
					},
					beforeSend: function(){
						$("#loader_page_common_ectm").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, "default", "default", function(){
				    		$("#windowEditContactMechContainer").html(data);
						});
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common_ectm").hide();
					},
				});
			};
			var initEvent = function(){
				$('body').on("createContactmechComplete", function(){
					$("#windowEditContactMech").jqxWindow("close");
					$("#shippingContactMechGrid").jqxGrid("updatebounddata");
				});
			};
			var openWindowEditContactMech = function(contactMechId){
				$("#windowEditContactMech").jqxWindow("open");
				loadContactMechContent(contactMechId);
			};
			return {
				init: init,
				openWindowEditContactMech: openWindowEditContactMech
			};
		}());
		
		OlbContactMechEdit.init();
	}
</script>