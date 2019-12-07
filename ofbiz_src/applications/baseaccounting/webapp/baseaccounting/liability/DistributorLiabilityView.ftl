<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%;"><label style="margin-left:-5%;font-weight:bold;font-size :22px;" class="center">${StringUtil.wrapString(uiLabelMap.BACCLiabilityDistributor)} [${parameters.disId?if_exists}]</label></div>
				<div style="width:100%; border-bottom: 1px solid #c5d0dc">
					<ul class="nav nav-tabs" role="tablist">
							<li>
								<a data-toggle="tab" href="#Receivable" aria-controls="Receivable" role="tab">
									${uiLabelMap.BACCReceivables}
								</a>
							</li>
							<li  class="active">
								<a data-toggle="tab" href="#Payable" aria-controls="Payable" role="tab">
									${uiLabelMap.BACCPayable}
								</a>
							</li>
					</ul>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content">
						<#include "script/DistributorData.ftl"/>
						<#include "LiabilitiesDistributorDetail.ftl"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>	


