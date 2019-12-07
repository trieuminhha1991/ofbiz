<div id="alterpopupWindowPaymentMethodNew" style="display: none;">
	<div>${StringUtil.wrapString(uiLabelMap.BSAddNewPaymentMethodForStore)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPaymentMethodTypeId}</label>
						</div>
						<div class='span7'>
							<div id="wn_paymentMethodTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPaymentServiceTypeEnumId}</label>
						</div>
						<div class='span7'>
							<div id="wn_paymentServiceTypeEnumId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSApplyToAllProducts}</label>
						</div>
						<div class='span7'>
							<div id="wn_applyToAllProducts"></div>
				   		</div>
					</div>
				</div><!-- .span12 -->
			</div><!-- .row-fluid -->
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="wn_alterCancel1" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="wn_alterSave1" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/salesresources/js/setting/productStoreNewPayment.js"></script>