<div class="row-fluid margin-bottom-10">
	<div class="span12">
		<div class="widget-box">
			<div class="widget-header widget-header-blue widget-header-flat">
				<h4 class="smaller"> ${uiLabelMap.ListInvoice}</h4>
				<div class="widget-toolbar">
					<a href="#invoiceContainer" role="button" data-toggle="collapse">
						<i class="fa-chevron-left"></i>
					</a>
				</div>
			</div>
			<div class="widget-body no-padding-top collapse" id="invoiceContainer">
				<div class="widget-main">
					<div class="zone info-zone" id="orderHistory" data-focus="listCustomerInvoice">
						<#assign commHistory="" />
						<#assign customLoadFunction="true"/>
						<#assign jqGridMinimumLibEnable="false"/>
						<#include "listCustomerInvoice.ftl"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>