<div class="row-fluid margin-bottom-10">
	<div class="span12">
		<div class="widget-box">
			<div class="widget-header widget-header-blue widget-header-flat">
				<h4 class="smaller"> ${uiLabelMap.ListOrder}</h4>
				<div class="widget-toolbar">
					<a href="#orderContainer" role="button" data-toggle="collapse">
						<i class="fa-chevron-left"></i>
					</a>
				</div>
			</div>
			<div class="widget-body no-padding-top collapse" id="orderContainer">
				<div class="widget-main">
					<div class="zone info-zone" id="orderHistory" data-focus="listOrderCustomer">
						<#assign orderUrl="" />
						<#assign customLoadFunction="true"/>
						<#assign jqGridMinimumLibEnable="false"/>
						<#assign isShowTitleProperty="false"/>
						<#assign fromCallCenter="Y"/>
						<#include "component://basesales/webapp/basesales/order/orderList.ftl"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
