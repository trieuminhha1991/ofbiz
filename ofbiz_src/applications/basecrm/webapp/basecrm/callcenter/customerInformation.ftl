<script src="/crmresources/js/callcenter/Family.js"></script>
<script src="/crmresources/js/callcenter/Bussineses.js"></script>
<script src="/crmresources/js/callcenter/School.js"></script>
<script src="/crmresources/js/callcenter/Contact.js"></script>
<script src="/crmresources/js/callcenter/CreateContact.js"></script>
<script src="/crmresources/js/callcenter/Liability.js"></script>
<style type="text/css">
	.cio-block-container .widget-toolbar a{
	    cursor: pointer;
	}
</style>

<div class="row-fluid margin-bottom-10">
	<div class="span12">
		<div class="widget-box cio-block-container">
			<div class="widget-header widget-header-blue widget-header-flat">
				<h4 class="smaller"> ${uiLabelMap.userProfile}</h4>
				
				<div class="widget-toolbar">
					<a id="createContact" role="button" data-toggle="collapse" class="collapsed hide">
						<i class="fa-plus"></i>${uiLabelMap.BSAddNew}
					</a>
					<a id="saveCustomerInfo" onclick="Processor.saveCustomer()" role="button" data-toggle="collapse" class="collapsed">
						<i class="fa-check"></i>${uiLabelMap.CommonSave}
					</a>
				</div>
			</div>
			<div class="widget-body no-padding-top">
				<div class="widget-main">
					<div class="zone info-zone" id="infoContainer">
						<#include "info/customerInfoEditable.ftl"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="menuContactType" style="display:none;">
	<ul>
		<li id="btnCreateFamily"><i class="fa-plus"></i>${uiLabelMap.DmsFamily}</li>
		<li id="btnCreateBusinesses"><i class="fa-plus"></i>${uiLabelMap.DmsBusiness}</li>
		<li id="btnCreateSchool"><i class="fa-plus"></i>${uiLabelMap.DmsSchool}</li>
	</ul>
</div>