<#if salesReps?has_content>
<div id="salesreps-tab" class="tab-pane">
	<h4 class="smaller lighter green" style="display:inline-block">
		<#-- <i class="fa-file"></i> -->
		${uiLabelMap.OrderSalesReps}
	</h4>
		<div class="widget-box olbius-extra">
		    <div class="widget-body">
		    <div class="widget-body-inner">
		        <div class="widget-body-inner">
		      <table class="table table-hover table-bordered table-striped dataTable" cellspacing='0'>
		      <tr class="header-row">
		        <td width="50%">${uiLabelMap.PartyLastName}</td>
		        <td width="50%">${uiLabelMap.PartyFirstName}</td>
		      </tr>
		    <#list salesReps as salesRep>
		      <#assign party = salesRep.getRelatedOne("Party", false)?if_exists/>
		      <#assign person = party.getRelatedOne("Person", false)?if_exists/>
		      <#if person?exists && person?has_content>
		      <tr>
		        <td width="50%">${person.lastName}</td>
		        <td width="50%">${person.firstName}</td>
		      </tr>
		      </#if>
		    </#list>
		      </table>
		    </div>
		</div>
		</div>
		</div>
</div><!--#salesreps-tab-->
</#if>
