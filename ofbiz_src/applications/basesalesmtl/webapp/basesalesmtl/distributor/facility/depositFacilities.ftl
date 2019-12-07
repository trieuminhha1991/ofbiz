<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js?v=1.0.5"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/logresources/js/facility/depositFacility.js"></script>
<#assign localeStr = "VI" />
<#if locale = "en">
	<#assign localeStr = "EN" />
</#if>

<script>
var uiLabelMap = {}

uiLabelMap.Location = "${StringUtil.wrapString(uiLabelMap.Location)}";
uiLabelMap.Inventory = "${StringUtil.wrapString(uiLabelMap.Inventory)}";
uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
uiLabelMap.ViewDetailInNewPage = "${StringUtil.wrapString(uiLabelMap.ViewDetailInNewPage)}	";
uiLabelMap.BSViewDetail = "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}";
uiLabelMap.CommonDelete = "${StringUtil.wrapString(uiLabelMap.CommonDelete)}";
uiLabelMap.BSRefresh = "${uiLabelMap.BSRefresh}";
uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
uiLabelMap.BLEmployeeId = "${StringUtil.wrapString(uiLabelMap.BLEmployeeId)}";
uiLabelMap.BLEmployeeName = "${StringUtil.wrapString(uiLabelMap.BLEmployeeName)}";
uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
uiLabelMap.FacilityIdExisted = "${StringUtil.wrapString(uiLabelMap.FacilityIdExisted)}";
uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
</script>

<div id="jqxNotificationUpdateSuccess" >
	<div id="notificationContentUpdateSuccess">
	</div>
</div>
<div id="contentNotificationUpdateSuccess" style="width:100%"></div>
<#assign dataField="[{ name: 'facilityId', type: 'string'},
					{name: 'facilityCode', type: 'string'},
					{ name: 'facilityTypeId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'ownerPartyId', type: 'string'},
					{ name: 'ownerPartyName', type: 'string'},
					{ name: 'payToPartyId', type: 'string'},
					{ name: 'fullName', type: 'string'},
					{ name: 'description', type: 'string'}]"/>
					
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.DAFacilityId)}', datafield: 'facilityCode', width: 150,
						cellsrenderer: function(row, colum, value){
						var data = $('#jqGridDepositFacility').jqxGrid('getrowdata', row);
			    		return '<span><a href=\"' + 'detailDepositFacility?facilityId=' + data.facilityId + '\">' + data.facilityCode + '</a></span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DAFacilityTypeId)}', datafield: 'facilityTypeId', filtertype: 'checkedlist', width: 180,
						cellsrenderer: function(row, colum, value){
							return '<span title=' + value + '>' + mapFacilityType[value] + '</span>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listFacilityType, displayMember: 'description', valueMember: 'facilityTypeId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapFacilityType[value];
				                }
	    		        	});
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.Owner)}', datafield: 'fullName', width: 350},
					{ text: '${StringUtil.wrapString(uiLabelMap.DAName)}', datafield: 'facilityName', width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.DADescription)}', datafield: 'description'}"/>
<@jqGrid id="jqGridDepositFacility" filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" viewSize="15"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" mouseRightMenu="true" contextMenuId="menuForDepositFacility" alternativeAddPopup="alterpopupWindow"
		url="jqxGeneralServicer?sname=JQGetListFacilityByOwnerParty&type=deposit"
	/>

<div id='menuForDepositFacility' style="display:none;">
	<ul>
		<li><i class="fa fa-folder-open-o"></i>${uiLabelMap.ViewDetailInNewPage}</li>
	    <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-database"></i>${uiLabelMap.Inventory}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>






<script>

<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "AREA_MEASURE"), null, null, null, false)>
	
var wardData = [];
	var listStoreKeeperParty = [];
	<#assign facs = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company, "primaryFacilityGroupId", "FACILITY_CONSIGN")), null, null, null, false) />
	var facilityData = new Array();
	<#list facs as item>
		var row = {};
		row['facilityId'] = "${item.facilityId}";
		row['parentFacilityId'] = "${item.parentFacilityId?if_exists}";
		row['facilityName'] = "${StringUtil.wrapString(item.facilityName?if_exists)}";
		facilityData[${item_index}] = row;
	</#list>
	
	<#assign countries = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY")), null, null, null, false) />
	var countryData = new Array();
	<#list countries as item>
		var row = {};
		row['geoId'] = "${item.geoId}";
		row['description'] = "${StringUtil.wrapString(item.geoName?if_exists)}";
		countryData[${item_index}] = row;
	</#list>

<#assign stores = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", company)), null, null, null, false) />
	var prodStoreData = new Array();
	<#list stores as item>
		var row = {};
		row['productStoreId'] = "${item.productStoreId}";
		row['description'] = "${StringUtil.wrapString(item.storeName?if_exists)}";
		prodStoreData.push(row);
	</#list>

	<#assign hasPerm = true>
	<#assign addPerm = "false">
	var hiddenParent = false;
	var parentFacilityId = null;
	<#if parameters.facilityId?has_content>
		<#assign params="jqxGeneralServicer?sname=jqGetFacilities&parentFacilityId=${parameters.facilityId}&facilityTypeId=WAREHOUSE&facilityGroupId=FACILITY_INTERNAL">
		<#assign title="ListChildFacility">
		hiddenParent = true;
		<#assign parentFacilityId= parameters.facilityId?if_exists>
		
		parentFacilityId = "${parameters.facilityId}";
	<#else>
		<#assign params="jqxGeneralServicer?sname=jqGetFacilities&facilityTypeId=WAREHOUSE&facilityGroupId=FACILITY_INTERNAL">
		<#assign title="ListFacility">
		hiddenParent = false;
	</#if>
	<#if hasOlbPermission("MODULE", "LOGISTICS", "ADMIN") || hasOlbPermission("MODULE", "LOG_FACILITY", "ADMIN")>
		<#assign addPerm = "true">
	<#elseif !hasOlbPermission("MODULE", "LOGISTICS", "VIEW") && !hasOlbPermission("MODULE", "LOG_FACILITY", "VIEW")>
		<#assign hasPerm = false>
	</#if>
	<#if hasOlbPermission("MODULE", "LOGISTICS", "CREATE") || hasOlbPermission("MODULE", "LOG_FACILITY", "CREATE")>
		<#assign addPerm = "true">
	</#if>

	<#if hasPerm = false>
		<div class="alert alert-danger">
			<strong>
				<i class="ace-icon fa fa-times"></i>
				${uiLabelMap.noViewPerm}
			</strong>
		</div>
	</#if>
	
	var glFacilityId = null;
	
	var sourceUom = [<#list listUoms as item>{key: '${item.uomId}', value: '${item.description}'}<#if item_index!=(listUoms?size)>,</#if></#list>];
	
	var curFacilityId = '${parameters.facilityId?if_exists}';


<#assign listFacilityType = delegator.findList("FacilityType", null, null, null, null, false)>


var mapFacilityType = {<#if listFacilityType?exists><#list listFacilityType as item>
				"${item.facilityTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
			</#list></#if>};
var listFacilityType = [<#if listFacilityType?exists><#list listFacilityType as item>{
	facilityTypeId: "${item.facilityTypeId?if_exists}", description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
},</#list></#if>];
</script>



