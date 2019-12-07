<div id="main-info">
	<div class="row-fluid">
		<div class="span12">
			<#include "component://delys/webapp/delys/accounting/listAPInvoiceRoleJQ.ftl"/>	
		</div>
	</div>		
	<div class="row-fluid">	
		<div class="span12">
			${screens.render("component://delys/widget/accounting/ap/AccApInvoiceScreens.xml#ListInvoiceStatus")}
		</div>
	</div>
	<div class="row-fluid">	
			<div class="span12">
				${screens.render("component://delys/widget/accounting/ap/AccApInvoiceScreens.xml#ListInvoiceApplications")}
		</div>
	</div>
	<div class="row-fluid">	
		<div class="span12">
				${screens.render("component://delys/widget/accounting/ap/AccApInvoiceScreens.xml#ListInvoiceTerms")}
		</div>
	</div>
</div>	
