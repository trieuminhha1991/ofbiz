<#assign salesMethodChannelEnum = Static["com.olbius.basesales.util.SalesUtil"].getListSalesMethodChannelEnum(delegator)!/>
<#assign listRoleType = delegator.findByAnd("RoleType", null, null, true)!/>
<script type="text/javascript">
	var salesMethodChannelEnumData = [
	<#if salesMethodChannelEnum?exists>
		<#list salesMethodChannelEnum as enumItem>
		{	enumId: '${enumItem.enumId}',
			description: '${StringUtil.wrapString(enumItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	var roleTypeData = [
	<#if listRoleType?exists>
		<#list listRoleType as roleTypeItem>
		{	roleTypeId : "${roleTypeItem.roleTypeId}",
			description : "${StringUtil.wrapString(roleTypeItem.get("description", locale))}",
			descriptionSearch : "${StringUtil.wrapString(roleTypeItem.get("description", locale))} [${roleTypeItem.roleTypeId}]",
		},
		</#list>
	</#if>
	];
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
    uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
    uiLabelMap.BSPartyId = "${StringUtil.wrapString(uiLabelMap.BSPartyId)}";
    uiLabelMap.BSFullName = "${StringUtil.wrapString(uiLabelMap.BSFullName)}";
</script>

<div class="row-fluid">
	<div class="span12">
		<form class="form-horizontal form-window-content-custom" method="post" action="<@ofbizUrl>findProductPriceAction</@ofbizUrl>">
			<div class="row-fluid">
				<div class="span6">
					<div class='row-fluid' style="display:none">
						<div class='span5'>
							<label>${uiLabelMap.BSPSSalesChannelType}</label>
						</div>
						<div class="span7">
							<div id="salesMethodChannelEnumId"></div>
				   		</div>
					</div>
					<#--<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSRoleTypeF}</label>
						</div>
						<div class="span7">
							<div id="roleTypeId"></div>
				   		</div>
					</div>-->
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPSSalesChannel}</label>
						</div>
						<div class="span7">
							<div id="productStoreId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSPartyId}</label>
						</div>
						<div class="span7">
							<div id="partyId">
								<div id="partyGrid"></div>
							</div>
				   		</div>
					</div>
				</div><!-- .span6 -->
				<div class="span6">
					<div class="row-fluid">
				   		<div class="pull-left">
							<button type="button" id="btnFindProductPrice" class="btn btn-small btn-primary"><i class="fa fa-search"></i>&nbsp;${uiLabelMap.BSActionFind}</button>
				   		</div>
					</div>
				</div><!-- .span6 -->
			</div><!-- .row-fluid -->
		</form>
	</div>
</div>

<div class="row-fluid">
	<div class="span12">
		<#include "productPriceFindProductItems.ftl"/>
	</div>
</div><!-- .row-fluid -->


<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true/>

<script type="text/javascript" src="/salesresources/js/product/productPriceFind.js"></script>


<#--
backup
/*var configRoleType = {
			placeHolder: uiLabelMap.BSClickToChoose,
			key: 'roleTypeId',
			value: 'descriptionSearch',
			width: '100%',
			dropDownHeight: 200,
			autoDropDownHeight: false,
			displayDetail: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
			multiSelect: false,
		};
		new OlbComboBox($("#roleTypeId"), roleTypeData, configRoleType, []);*/

/*var roleTypeIds = [];
			var listRoleTypeData = $("#roleTypeId").jqxComboBox('getSelectedItems');
			if (OlbCore.isNotEmpty(listRoleTypeData)) {
				for (var i = 0; i < listRoleTypeData.length; i++) {
					var roleTypeItem = listRoleTypeData[i];
					roleTypeIds.push(roleTypeItem.value);
				}
			}
			var roleTypeIdsStr = JSON.stringify(roleTypeIds);*/
			/*var roleTypeId;
			var roleTypeData = $("#roleTypeId").jqxComboBox('getSelectedItem');
			if (OlbCore.isNotEmpty(roleTypeData)) {
				roleTypeId = roleTypeData.value;
			}
			*/
			var otherParam = "";
			//if (OlbCore.isNotEmpty(roleTypeId)) otherParam += "&roleTypeId=" + roleTypeId;
-->
