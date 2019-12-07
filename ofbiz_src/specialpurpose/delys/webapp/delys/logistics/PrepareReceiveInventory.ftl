<div class="widget-box olbius-extra">
	
  	<div class="widget-header widget-header-small header-color-blue2">
  	<h6>${uiLabelMap.selectFacility}</h6>
  	<div class="widget-toolbar">
    		<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div>
  	</div>
	 <div class="widget-body">
	 <div class="widget-body-inner">
	 <div class="widget-main">
	 	<form name="selectFacility" method="post" id="selectFacility" action="<@ofbizUrl>DelysReceiveInventory</@ofbizUrl>">
	 		<div id="facility">
				 <select name="facilityId">
			         <#list listFacilities as facility>
			         	<option value="${facility.facilityId}">${facility.facilityName?if_exists}</option>
			         </#list>
				 </select>
			</div>
			<div>
				<button class="btn btn-primary btn-small" type="submit"><i class="icon-ok"></i> ${uiLabelMap.CommonSubmit}</button>
			</div>
	 	</form>
	 </div>
</div>
</div>
</div>	