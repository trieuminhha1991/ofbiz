<div class="row-fluid margin-bottom-10">
	<div class="span12">
		<div class="widget-box">
			<div class="widget-header widget-header-blue widget-header-flat">
				<h4 class="smaller"> ${uiLabelMap.ListAgreement}</h4>
				<div class="widget-toolbar">
					<a href="#agreementContainer" role="button" data-toggle="collapse">
						<i class="fa-chevron-left"></i>
					</a>
				</div>
			</div>
			<div class="widget-body no-padding-top collapse" id="agreementContainer">
				<div class="widget-main">
					<div class="zone info-zone" id="agreementCustomer" data-focus="listAgreementCustomer">
						<#assign agreementUrl="" />
						<#assign partyId = Static["com.olbius.basehr.util.CommonUtil"].getCookie(request, "currentPartyId") >
						<#assign customLoadFunction="true"/>
						<#assign jqGridMinimumLibEnable="false"/>
						<#include "agreementPartnerInfo.ftl"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>