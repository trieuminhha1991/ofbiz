<div id="Context${id}" class="hide">
	<ul>
		<#if security.hasEntityPermission("PROMOREGISTER", "_APPROVE", session)>
		<li action="edit" id="edit">
			<i class="fa fa-check"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.HRCommonAccept)}
		</li>
		<li action="cancel" id="cancel">
			<i class="fa fa-remove"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.HRCommonReject)}
		</li>
		<li action="marked" id="marked">
			<i class="fa fa-edit"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSMarked)}
		</li>
		</#if>
		<li action="viewimage" id="viewimage">
			<i class="fa fa-eye"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSViewImage)}
		</li>
		<li action="createAgreement" id="createAgreement">
			<i class="fa fa-file-o"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSCreateAgreement)}
		</li>
	</ul>
</div>
<script>
	var ContextMenuPromos = (function(){
		var self = {};
		self.grid = $('#${id}');;
		self.initContext = function(){
			var ct = $('#Context${id}');
			ct.jqxMenu({ theme: 'olbius', width: 200, autoOpenPopup: false, mode: 'popup' });
			ct.on('itemclick', function (event) {
		        var args = event.args;
		        var itemId = $(args).attr('action');
		        var row = self.grid.jqxGrid('getSelectedRowindex');
		        switch (itemId) {
					case 'edit':
						PromosRegistration.acceptCustomerReg(row);
						break;
					case 'cancel':
						PromosRegistration.cancelCustomerReg(row);
						break;
					case 'marked':
						PromosRegistration.updateResult(row);
						break;
					case 'viewimage':
						var data = PromosRegistration.getGridData(row);
						if(data.url){
							PromosRegistration.viewImage(row);
						}
						break;
					case 'createAgreement':
						var data = PromosRegistration.getGridData(row);
						if(data){
							$("#wn_customerId").val(data.partyId);
							$("#wn_reg_productPromoId").val(data.productPromoId);
							$("#wn_reg_productPromoRuleId").val(data.productPromoRuleId);
							$("#wn_reg_fromDate").val((new Date(data.fromDate)).getTime());
							
							var customerDesc = data.fullName + " (" + data.partyCode + ")";
							$("#wn_customerDesc").text(customerDesc);
							
							OlbExhibitionAgreementNew.openWindowNewAgreement();
						}
						break;
					default:
						break;
				}
			});
			ct.on('shown', function(event){
				var args = event.args;
		        var itemId = $(args).attr('action');
		        var row = self.grid.jqxGrid('getSelectedRowindex');
				var data = PromosRegistration.getGridData(row);
				if(data.url){
					ct.jqxMenu('disable', 'viewimage', false);
				}else{
					ct.jqxMenu('disable', 'viewimage', true);
				}
				if (data.statusId == "PROMO_REGISTRATION_CREATED") {
					ct.jqxMenu('disable', 'edit', false);
					ct.jqxMenu('disable', 'cancel', false);
					ct.jqxMenu('disable', 'marked', true);
					ct.jqxMenu('disable', 'createAgreement', true);
				} else {
					ct.jqxMenu('disable', 'edit', true);
					ct.jqxMenu('disable', 'cancel', true);
					if (data.statusId == "PROMO_REGISTRATION_ACCEPTED") {
						ct.jqxMenu('disable', 'marked', false);
						if (data.agreementId) ct.jqxMenu('disable', 'createAgreement', true);
					} else {
						ct.jqxMenu('disable', 'marked', true);
						ct.jqxMenu('disable', 'createAgreement', true);
					}
				}
			})
		};
		$(document).ready(function(){
			self.initContext();
		});
	})();
</script>