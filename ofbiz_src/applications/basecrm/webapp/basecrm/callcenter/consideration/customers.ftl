<script src="/crmresources/js/callcenter/customers.js"></script>

<div id="customers-panel">
	<div>
		<div style="border: none;" id="listCustomers-cons"></div>
	</div>
	<div id="ContentPanel">
		
	
	<div id="customer-info-panel">
		<div>
			<#include "component://basecrm/webapp/basecrm/callcenter/consideration/customerInfoCons.ftl"/>
		</div>
		<div>
			
			<div id="customer-insight-panel">
				<div>
					<#include "component://basecrm/webapp/basecrm/callcenter/consideration/insightProducts.ftl"/>
				</div>
				<div>
					<#include "component://basecrm/webapp/basecrm/callcenter/consideration/insightSubjects.ftl"/>
				</div>
			</div>
		
		</div>
	</div>
	
	</div>
</div>