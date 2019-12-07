<#assign listGeo  = delegator.findByAnd("Geo",null,null,false) />
<#assign listGlAccountOrganizationAndClass  = delegator.findByAnd("GlAccountOrganizationAndClass",{"organizationPartyId" : "${parameters.organizationPartyId}"},["accountCode"],false) />
<#assign taxAuthorityGlAccounts  = delegator.findByAnd("TaxAuthorityGlAccount",{"organizationPartyId" : "${parameters.organizationPartyId}"},["taxAuthGeoId","taxAuthPartyId"],false) />

<script>
if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {};
uiLabelMap.BACCaccountName = '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}';
uiLabelMap.BACCaccountCode = '${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}';
var tahArray = new Array();
	tahArray = [	
		<#list taxAuthorityHavingNoGlAccountList as acc>
			<#assign taxAuthPartyId = acc.taxAuthPartyId?if_exists />
			<#assign taxAuthGeoId = acc.taxAuthGeoId?if_exists />
			<#assign partyView = delegator.findOne("PartyNameView", {"partyId" : taxAuthPartyId}, true) !/>
			<#assign geo = delegator.findOne("Geo", {"geoId" : taxAuthGeoId}, true) />	
			{
				'taxAuthPartyId' : '${taxAuthPartyId}' + ';' + '${taxAuthGeoId}',
				<#if partyView?exists>
					'description' : '<span class="custom-style-word">[ ' + '${taxAuthPartyId}'+ ' ]'+'${StringUtil.wrapString(partyView.firstName?if_exists)}' +'${StringUtil.wrapString(partyView.middleName?if_exists)}' +'${StringUtil.wrapString(partyView.lastName?if_exists)}' +'${StringUtil.wrapString(partyView.groupName?if_exists)}' + '-' + '[ ' + '${geo.geoId?if_exists}' + ' ]' + '${StringUtil.wrapString(geo.geoName?if_exists)}</span>'
				<#else>	
					'description' :'<span class="custom-style-word">[ ' +'${taxAuthPartyId?if_exists}'+' ]'+ '-' + '[ ' + '${geo.geoId?if_exists}' + ' ]' + '${StringUtil.wrapString(geo.geoName?if_exists)}</span>'
				</#if>
			},
		</#list>	
		]
	


var listTAGRenderer = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	if(data.geoName) return '<span class="custom-style-word">'+data.taxAuthGeoId  +' ['+  data.geoName +']</span>';
    return data.taxAuthGeoId;
}

var listGlAccountOrganizationAndClassRenderer = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	if(data.accountName) return '<span class="custom-style-word">'+data.accountCode +'</span>';
    return '';
}
var updateListAfterDel = function(){
	var row = {
			taxAuthPartyId : (data ? (data.split('#;')[0].split('=')[2] + ';' + data.split('#;')[1]) : '')
		}
		updateListTax('delete',row,data.responseMessage);
}
var rowdataAdd = {};
function updateListTax(action,row,status){
	if(action == 'delete' && status != 'error'){
		var data = {
				taxAuthPartyId : row.taxAuthPartyId ?  row.taxAuthPartyId : '',
				description : getDescription(row.taxAuthPartyId)
			}
		tahArray.splice(0,0,data);
		$('#taxAuthPartyGeoIdAdd').jqxDropDownList('source',tahArray);
	}
};	

function getDescription(id){
	var des;
	if(id) {
			$.ajax({
				url : 'getDescriptionTax',
				data : {
					id : id
				},
				async : false,
				datatype : 'json',
				type : 'POST',
				success : function(response){
					des = response.description;
				}
			})
		};
		return des;
	}
</script>