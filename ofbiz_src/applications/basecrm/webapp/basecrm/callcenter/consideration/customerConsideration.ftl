<script src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script src="/crmresources/js/callcenter/customerConsideration.js"></script>

<div class="ace-settings-container" id="crm-consideration-container">
	<div class="btn btn-app btn-mini btn-warning ace-settings-btn" id="crm-consideration-btn">
		<i class="fa-comments-o bigger-150"></i>
	</div>
	
	<div class="ace-settings-box" id="ace-settings-box">
	
	
	<div class="jqx-hideborder jqx-hidescrollbars" id="customerTabs">
		<ul>
<!--			<li style="margin-left: 30px;">${uiLabelMap.CRMConsideration}</li> -->
			<li>${uiLabelMap.CRMPurchaseHistory}</li>
		</ul>
<!--		<div>
			<div id="cons-panel">
				<div class="crm-cons-product">
					<#include "component://basecrm/webapp/basecrm/callcenter/consideration/products.ftl"/>
				</div>
				<div class="crm-cons-main">
					
					<div id="crm-cons-main">
						<div class="crm-cons-tag">
							<#include "component://basecrm/webapp/basecrm/callcenter/consideration/tagSubject.ftl"/>
						</div>
						<div class="crm-cons-customer">
							<#include "component://basecrm/webapp/basecrm/callcenter/consideration/customers.ftl"/>
						</div>
					</div>
					
				</div>
			</div>
		</div> -->
		<div>
			<#include "component://basecrm/webapp/basecrm/callcenter/consideration/purchaseHistory.ftl"/>
		</div>
	</div>
	
	</div>
</div>