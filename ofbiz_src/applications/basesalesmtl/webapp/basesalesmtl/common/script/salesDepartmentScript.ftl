<#assign organization = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator,userLogin.get('userLoginId'))/>

<#--
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
-->
<script type="text/javascript">
	<#if organization?if_exists == "MB">
		var rootPartyId = "DSA";
		<#elseif organization?if_exists == "MN">
		var rootPartyId = "DSA_SOUTH";
	</#if>
	var uiLabelMap = {
			OrgUnitName: '${uiLabelMap.OrgUnitName}',
			OrgUnitId: '${uiLabelMap.OrgUnitId}',
			NumEmployees: '${uiLabelMap.NumEmployees}',
			CommonAddress: '${uiLabelMap.CommonAddress}',
			ClickToEdit: '${uiLabelMap.ClickToEdit}',
			CommonEdit: '${uiLabelMap.CommonEdit}',
			UpdateAddress: '${uiLabelMap.UpdateAddress}',
			DAAddNewAddress: '${uiLabelMap.DAAddNewAddress}',
			OrganizationUnit: '${uiLabelMap.OrganizationUnit}',
			HRCommonNotSetting: '${uiLabelMap.HRCommonNotSetting}',
			CommonRequired: '${uiLabelMap.CommonRequired}',
			CommonChooseFile: '${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}',
			AddNewRowConfirm: '${StringUtil.wrapString(uiLabelMap.AddNewRowConfirm)}',
			CommonSubmit: '${StringUtil.wrapString(uiLabelMap.CommonSubmit)}',
			CommonCancel: '${StringUtil.wrapString(uiLabelMap.CommonCancel)}',
			MustntHaveSpaceChar : '${StringUtil.wrapString(uiLabelMap.MustntHaveSpaceChar)}',
			PartyIdCannotIsChildOfItSelf : '${StringUtil.wrapString(uiLabelMap.PartyIdCannotIsChildOfItSelf)}',
			OnlyContainInvalidChar : "${StringUtil.wrapString(uiLabelMap.OnlyContainInvalidChar)}",
			InvalidChar : "${StringUtil.wrapString(uiLabelMap.InvalidChar)}",
	};
</script>

<@jqTreeGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		SalesDepartmentView.init();
	});
	var SalesDepartmentView = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu"));
		};
		var initElementComplex = function(){
			var configProductList = {
				width: '100%',
				height: "auto",
				datafields: [
		               { name: "partyId", type: "string" },
		               { name: "partyCode", type: "string" },
		               { name: "partyIdFrom", type: "string" },
		               { name: "partyName", type: "string" },
		               { name: "postalAddress", type: "string"},
		               { name: "contactMechId", type: "string"},
		               { name: "totalEmployee", type: "number" },                   
		               { name: "comments", type: "string" },                   
		               { name: "expanded",type: "bool"},
		           	],
				columns: [
		          		{text: uiLabelMap.OrgUnitName, datafield: "partyName", minWidth: 400,},			
		          		{text: uiLabelMap.OrgUnitId, datafield: "partyCode", width: 300},
		          		{text: uiLabelMap.NumEmployees, datafield: "totalEmployee", width: 200, cellsalign: "right"}
	          		],
				editable: false,
				editmode: 'click',
				selectionmode: 'singlecell',
				showtoolbar: false,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				virtualmode: true,
				root: "listReturn",
				useUrl: true,
				url: "getOrganizationUnit?partyId=" + rootPartyId,
				key: 'partyId',
				parentKeyId: 'partyIdFrom',
				altRows: false,
				
				pageable: true,
				pagesize: 15,
				showtoolbar: false,
				contextMenu: "contextMenu",
			};
			new OlbTreeGrid($("#treePartyGroupGrid"), null, configProductList, []);
		};
		var initEvent = function(){
			$("#contextMenu").on("itemclick", function (event) {
                var args = event.args;
                var itemId = $(args).attr("id");
		        switch (itemId) {
				case "listAddress":
						var selection = $("#treePartyGroupGrid").jqxTreeGrid("getSelection");
		                var partyId = selection[0].partyId;
		                $("#txtPartyId").val(partyId);
		                PrimaryAddress.open(partyId);
					break;
				default:
					break;
				}
            });
		};
		return {
			init: init
		};
	}());
</script>

<#--<script src="/salesmtlresources/js/common/salesDepartment.js"></script>-->
