<div class="modal fade requestModal" id="approveRequest" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="tabbable tabs-shadow tabs-space">
				<ul class="nav nav-tabs" id="marketingDetail">
					<li class="active">
						<a data-toggle="tab" href="#general">${uiLabelMap.generalInfo}</a>
					</li>
					<li>
						<a data-toggle="tab" href="#productSampling">${uiLabelMap.productSampling}</a>
					</li>
					<li>
						<a data-toggle="tab" href="#costList">${uiLabelMap.costList}</a>
					</li>
				</ul>
				<#assign isDisable = true/>
				<div class="tab-content trade-content">
					<div id="general" class="tab-pane in active">
						<#include "component://delys/webapp/delys/marketing/trade/formSampling.ftl"/>
					</div>
					<div id="productSampling" class="tab-pane">
						<#include "component://delys/webapp/delys/marketing/productUom.ftl"/>
					</div>
					<div id="costList" class="tab-pane">
						<#include "component://delys/webapp/delys/marketing/costList.ftl"/>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" id="reject">
					${uiLabelMap.reject}
				</button>
				<button type="button" class="btn btn-primary" id="approve">
					${uiLabelMap.approve}
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal requestModal" tabindex="-1" role="dialog" aria-hidden="true" id="rejectNote">
	<div class="modal-dialog">
		<div class="modal-header modal-header-form">
			<h3 class="modal-header-title">${uiLabelMap.marketingCampaignNote}</h3>
		</div>
		<div class="modal-content">
			<textarea class="note-area no-resize" id="noteArea" autocomplete="off"></textarea>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-default" id="cancelReject">
				${uiLabelMap.cancel}
			</button>
			<button type="button" class="btn btn-primary" id="confirmReject">
				${uiLabelMap.ok}
			</button>
		</div>
	</div>
</div>
