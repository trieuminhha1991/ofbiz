<#assign listContactMechType = delegator.findList("ContactMechType",null,null,null,null,false) />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript">
	<#if listContactMechType?exists>
		var data = [
            <#list listContactMechType as contactMechType>
	            {
	            	contactMechTypeId : "${contactMechType.contactMechTypeId?if_exists}",
	            	description : "${contactMechType.get('description',locale)?if_exists}",
	            },
	        </#list>
        ];
		<#else> data = [];
	</#if>
	var source = {
			localdata : data,
			datatype : 'array',
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
</script>
<form id="PartySelectContactType" class="form-horizontal">
	<div class="row-fluid form-window-content">
		<div class="span12">
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartySelectContactType}
		        </div>
				<div class="span6">
					<div id="choosePartyContactType">
				</div>
			</div>
		</div>
	</div>
</form>
<script type="text/javascript">
	$(document).ready(function() {
		$("#choosePartyContactType").jqxComboBox({width: 220, source : dataAdapter, valueMember : "contactMechTypeId", displayMember: "description"});	
	});
</script>