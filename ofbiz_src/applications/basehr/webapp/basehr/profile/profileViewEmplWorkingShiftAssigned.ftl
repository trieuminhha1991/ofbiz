<#include "script/profileViewEmplWSAssignedScript.ftl"/>
<script type="text/javascript" src="/hrresources/js/profile/emplWorkingShiftAssigned.js"></script>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.EmplWorkingShiftAssigned}</h4>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div id="employeeWorkingShift"></div>
			<div style="position:relative">
				<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
					<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
					   	<div>
						    <div class="jqx-grid-load"></div>
						    <span>${uiLabelMap.DALoading}...</span>
					   	</div>
				  	</div>
				</div>
			</div>
		</div>	
	</div>
</div>
