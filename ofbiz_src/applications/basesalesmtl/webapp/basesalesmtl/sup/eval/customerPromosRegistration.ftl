<#assign id="CustomerPromosRegistration"/>

<script>
	var marks = [<#if marks?exists><#list marks as mark>
			{
				enumId: "${mark.enumId}",
				description: '${StringUtil.wrapString(mark.get("description", locale)?if_exists)}',
			},
		</#list></#if>];
	var statuses = [<#if statuses?exists><#list statuses as status>
			{
				statusId: "${status.statusId}",
				description: '${StringUtil.wrapString(status.get("description", locale)?if_exists)}',
			},
		</#list></#if>];
	
	<#if security.hasEntityPermission("PROMOREGISTER", "_APPROVE", session)>
		var hiddenAccept = false;
		<#else>
		var hiddenAccept = true;
	</#if>
	
	<#if security.hasEntityPermission("PROMOREGISTER", "_CREATE", session)>
		var showtoolbar = true;
	<#else>
		var showtoolbar = false;
	</#if>
	
	<#assign registrationStatusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "PROMO_REGISTRATION"}, null, false)!/>
	var registrationStatusData = [
		<#if registrationStatusList?exists>
		<#list registrationStatusList as statusItem>
		{	"statusId": "${statusItem.statusId}",
			"description": "${StringUtil.wrapString(statusItem.get("description", locale))}"
		},
		</#list>
		</#if>
	];
	
	<#assign registrationResultList = delegator.findByAnd("Enumeration", {"enumTypeId" : "PROD_PROMOEXT_REG_EVAL"}, null, false)!/>
	var registrationResultData = [
		<#if registrationResultList?exists>
		<#list registrationResultList as resultItem>
		{	"enumId": "${resultItem.enumId}",
			"description": "${StringUtil.wrapString(resultItem.get("description", locale))}"
		},
		</#list>
		</#if>
	];
</script>

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, data) {
	var id = $(gridElement).attr('id');
	var grid = $(parentElement.children);
	var partyId = data.partyId;
	var productPromoId = data.productPromoId;
	var productPromoRuleId = data.productPromoRuleId;
	var pre = 'GridDetail-' + id + '-' + index;
	grid.attr('id', pre);

	var datafields = [
			{name: 'employeeId', type: 'string'},
			{name: 'employeeName', type: 'string'},
			{name: 'entryDate', type: 'date', other:'Timestamp'},
			{name: 'url', type: 'string'},
		    {name: 'resultEnumId', type: 'string'},
		    {name: 'resultDescription', type: 'string'}
		];
	var columns = [
			{text: '${uiLabelMap.BSEntryDate}',datafield: 'entryDate', width: '16%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype:'range'},
			{text: '${uiLabelMap.BSEmployeeMarkedId}',datafield: 'employeeId', width: '16%'},
			{text: '${uiLabelMap.BSEmployeeMarkedName}',datafield: 'employeeName', width: '20%'},
           	{text: '${uiLabelMap.BSResult}', datafield: 'resultEnumId', width: '20%',
				cellsrenderer: function(row, column, value){
					if (registrationResultData.length > 0) {
						for(var i = 0 ; i < registrationResultData.length; i++){
							if (value == registrationResultData[i].enumId){
								return '<span title =\"' + registrationResultData[i].description +'\">' + registrationResultData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
			 	}, 
           	},
           	{text: '${uiLabelMap.BSLinkImage}',datafield: 'url', width: '28%', 
			   cellsrenderer:  function (row, column, value, a, b, data){
				   var vl = '\"' + value + '\"';
				   var str = \"<div class='cell-custom-grid'><a href='javascript:PromosRegistration.viewImage(\"
							+ vl +\")' \";
				   if(!value){
						str += ' class=\"disabled\"' ;
				   }
				   str += '><i class=\"fa fa-picture-o\"></i></a></div>';
				   return str;
			   }
			}
		];

	Grid.initGrid({
			url: 'JQGetCustomerPromosEvaluation&partyId='+partyId+'&productPromoId='+ productPromoId+'&productPromoRuleId=' + productPromoRuleId,
			width: '95%',
			showtoolbar: showtoolbar,
			toolbarheight: 25,
			rendertoolbar: function (toolbar) {
				var container = $(\"<div style='margin: 12px;float: right;cursor: pointer;'></div>\");
				var input = $(\"<a onclick='PromosRegistration.openMarkValue(\" + '\"' + pre + '\",\"' + index + '\"' + \")'><i class='fa fa-plus'></i>${StringUtil.wrapString(uiLabelMap.CommonAddNew)}</a>\");
				toolbar.append(container);
				if (!data.resultEnumId) {
					container.append(input);
				}
			},
			height: 270,
			source: {pagesize: 5}
		}, datafields, columns, null, grid);
}"/>

<#assign dataField="[
				{ name: 'partyId', type: 'string'},
				{ name: 'partyCode', type: 'string'},
				{ name: 'productPromoId', type: 'string'},
				{ name: 'productPromoRuleId', type: 'string'},
				{ name: 'ruleName', type: 'string'},
				{ name: 'resultEnumId', type: 'string'},
				{ name: 'url', type: 'string'},
				{ name: 'statusId', type: 'string'},
				{ name: 'fromDate', type: 'date', other: 'Timestamp'},
				{ name: 'thruDate', type: 'date', other: 'Timestamp'},
				{ name: 'promoName', type: 'string'},
				{ name: 'fullName', type: 'string'},
				{ name: 'agreementId', type: 'string'}
			]"/>
<#assign columnlist="
					{ text: '${StringUtil.wrapString(uiLabelMap.BSACAction)}', datafield: 'edit', cellsalign: 'right', width:100, filterable:false, pinned: true, hidden: hiddenAccept,
						cellsrenderer: function (row, column, value, a, b, data) {
							var status = data.statusId;
							var str = '<div class=\"align-right no-margin\" style=\"width:95px;padding: 0 5px 0 0\">';
							if (status == 'PROMO_REGISTRATION_CREATED') {
								str += '<button class=\"btn btn-danger btn-mini grid-custom-button\" onclick=\"PromosRegistration.cancelCustomerReg('+row+')\"><i class=\"fa fa-remove\"></i></button>';
								str += '<button class=\"btn btn-warning btn-mini grid-custom-button\" onclick=\"PromosRegistration.acceptCustomerReg('+row+')\"><i class=\"fa fa-check\"></i></button>';
							} else if (status == 'PROMO_REGISTRATION_ACCEPTED') {
								str += '<button class=\"btn btn-primary btn-mini grid-custom-button\" onclick=\"PromosRegistration.updateResult('+row+')\"><i class=\"fa fa-edit\"></i></button>';
							}
							str += '</div>';
		                    return str;
		                }
	                },
					{ text: '${StringUtil.wrapString(uiLabelMap.BSRetailOutletId)}', datafield: 'partyCode', width: 110,
						cellsrenderer:  function (row, column, value, a, b, data){
						   var id = data.partyId;
						   var des = value ? value : id;
						   return \"<div class='cell-custom-grid'><a href='AgentDetail?partyId=\"+ id +\"'>\"+des+\"</a></div>\";
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSRetailOutletName)}', datafield: 'fullName', width: 100},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSProgramId)}', datafield: 'productPromoId', width: 130},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSAbbRegisterLevel)}', datafield: 'ruleName', width: 70},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', datafield: 'statusId', width: 90, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							if (registrationStatusData.length > 0) {
								for(var i = 0 ; i < registrationStatusData.length; i++){
	    							if (value == registrationStatusData[i].statusId){
	    								return '<span title =\"' + registrationStatusData[i].description +'\">' + registrationStatusData[i].description + '</span>';
	    							}
	    						}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(registrationStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									if (registrationStatusData.length > 0) {
										for(var i = 0; i < registrationStatusData.length; i++){
											if(registrationStatusData[i].statusId == value){
												return '<span>' + registrationStatusData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSResult)}', datafield: 'resultEnumId', width: 70, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							if (registrationResultData.length > 0) {
								for(var i = 0 ; i < registrationResultData.length; i++){
	    							if (value == registrationResultData[i].enumId){
	    								return '<span title =\"' + registrationResultData[i].description +'\">' + registrationResultData[i].description + '</span>';
	    							}
	    						}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(registrationResultData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'enumId', valueMember: 'enumId',
								renderer: function(index, label, value){
									if (registrationResultData.length > 0) {
										for(var i = 0; i < registrationResultData.length; i++){
											if(registrationResultData[i].enumId == value){
												return '<span>' + registrationResultData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype:'range'},
					{ text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype:'range'},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSAgreement)}', datafield: 'agreementId', width: 70, 
						cellsrenderer:  function (row, column, value, a, b, data){
						   return \"<div><a href='exhibitedAgreement.pdf?agreementId=\"+ value +\"' target='_blank'>\" + value + \"</a></div>\";
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSLinkImage)}', datafield: 'url', filterable: false,
						 cellsrenderer:  function (row, column, value, a, b, data){
						   var vl = '\"' + value + '\"';
						   var str = \"<div class='cell-custom-grid'><a href='javascript:PromosRegistration.viewImage(\"
									+ vl +\")' \";
						   if(!value){
								str += ' class=\"disabled\"' ;
						   }
						   str += '><i class=\"fa fa-picture-o\" aria-hidden=\"true\"></i></a></div>';
						   return str;
					   }
					},
			"/>

<#if security.hasEntityPermission("PROMOREGISTER", "_DELETE", session)>
	<#assign deleterow="true" />
<#else>
	<#assign deleterow="false" />
</#if>

<#if Static["com.olbius.basesales.util.SalesPartyUtil"].isDistributor(delegator, userLogin.partyId?default(null))?default(false)>
	<#--<#assign url="jqxGeneralServicer?sname=JQGetCustomerPromosRegistrationDis" />-->
	<#assign selectionmode=""/>
	<#assign mouseRightMenu="false"/>
<#else>
	<#assign selectionmode="singlerow"/>
	<#assign mouseRightMenu="true"/>
</#if>

<#assign url="jqxGeneralServicer?sname=JQGetCustomerPromosRegistration&typeId=${productPromoTypeId?if_exists}" />
<@jqGrid id=id addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" selectionmode=selectionmode
	showtoolbar="true" url=url contextMenuId="Context${id}" mouseRightMenu=mouseRightMenu
	updateUrl='jqxGeneralServicer?jqaction=U&sname=acceptRegistrationPromotion' editColumns='partyId;productPromoId;productPromoRuleId'
	deleterow=deleterow removeUrl='jqxGeneralServicer?jqaction=D&sname=deleteRegistrationPromotion' deleteColumn='productPromoId;productPromoRuleId;fromDate(java.sql.Timestamp);partyId'
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="270"/>

<#include "customerPromosRegAction.ftl"/>
<#include "customerPromosRegContextMenu.ftl"/>
<#include "customerPromosRegNewAgreementPopup.ftl"/>
