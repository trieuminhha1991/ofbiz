<script>
	<#assign roleTypeList = delegator.findList("RoleType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["AGENT", "CUSTOMER", "SUPPLIER", "CONSUMER", "DISTRIBUTOR", "BUYER",  "VENDOR", "CONTRUCTOR", "PARTNER", "PERSON_ROLE", "ORGANIZATION_ROLE"]), null, null, null, false) />
	var roleTypeData = [<#if roleTypeList?exists><#list roleTypeList as roleType>{description:"${StringUtil.wrapString(roleType.description)?default("")}", roleTypeId:"${roleType.roleTypeId}"},</#list></#if>];	
	<#assign agreementTypeList = delegator.findList("AgreementType", null, null, null, null, false) />
	var agreementTypeData = [<#if agreementTypeList?exists><#list agreementTypeList as agreementType>{<#assign description = StringUtil.wrapString(agreementType.get("description", locale)) />description:"${description}", roleTypeId:"${agreementType.agreementTypeId}"},</#list></#if>];
	<#assign Parties = delegator.findList("PartyNameView", null, null, null, null, false) />
	var partyData =  [<#if Parties?exists><#list Parties as item>{partyId: '${StringUtil.wrapString(item.partyId?if_exists)}',firstName: '${StringUtil.wrapString(item.firstName?if_exists)}',middleName: '${StringUtil.wrapString(item.middleName?if_exists)}',lastName: '${StringUtil.wrapString(item.lastName?if_exists)}',groupName: '${StringUtil.wrapString(item.groupName?if_exists)}',},</#list></#if>]	
	<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "AGREEMENT_STATUS"}, null, false) />
	var statusData = [<#list statusList as statusItem>{<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />statusId: '${statusItem.statusId}',description: "${description}"},</#list>];


</script>
<#include "agreementUiLabel.ftl"/>
<!-- partyIdFrom is a datafield -->
<!-- HTML for Lookup partyIdFrom -->

<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/procurement/listAgreement.js"></script>
<style type="text/css">
	.background-red {
		background-color: #ddd !important;
	}
	#jqxgridFromParty .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxgridToParty .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxgridBillingAccountIdy .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxPanel td{
		padding:5px;
	}
	#addrowbutton{
		margin:0 !important;
		border-radius:0 !important;
	}
</style>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'productId', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyIdTo', type: 'string'},
					 { name: 'roleTypeIdFrom', type: 'string'},
					 { name: 'roleTypeIdTo', type: 'string'},
					 { name: 'agreementTypeId', type: 'string'},
					 { name: 'statusId', type: 'string'}, 
					 { name: 'agreementDate', type: 'date', other:'Timestamp'},
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp'},
					 { name: 'description', type: 'string'},
					 { name: 'textData', type: 'string'},
					 ]
					 "/>
<#assign columnlist = "{ text: '${uiLabelMap.DAAgreementId}', width:100, datafield: 'agreementId', cellclassname: cellclass,
						   	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        					return '<a style = \"margin-left: 10px\" href=' + 'ListAgreementTermsPO?agreementId=' + data.agreementId + '>' +  data.agreementId + '</a>'
    						}
					 	},
					 	{ text: '${uiLabelMap.DAPartyFrom}', width:230, datafield: 'partyIdFrom', filtertype: 'olbiusdropgrid', cellclassname: cellclass,
						 	cellsrenderer: function (row, column, value) {
								for(i = 0; i < partyData.length; i++){
									if(partyData[i].partyId == value){
										var result = '<a title=\"' + value + '\"' + ' style = \"margin-left: 10px\" ' +  ' href=\"' + '/partymgr/control/viewprofile?partyId=' + value + '\">' + partyData[i].firstName + '&nbsp' + partyData[i].middleName + '&nbsp' + partyData[i].lastName + '&nbsp' + partyData[i].groupName + '&nbsp' + '</a>';
										return result;
									}
								}
	        					return '<a title= \"' + value  + '\"' +' style = \"margin-left: 10px\" href=' + '/partymgr/control/viewprofile?partyId=' + value + '>' + value + '</a>'
	    					},
						 	createfilterwidget: function (column, columnElement, widget) {
				   				widget.width(490);
				   			}
					 	},
					 	{ text: '${uiLabelMap.DAPartyTo}', width:230, datafield: 'partyIdTo', filtertype: 'olbiusdropgrid', cellclassname: cellclass,
						 	cellsrenderer: function (row, column, value) {
								for(i = 0; i < partyData.length; i++){
									if(partyData[i].partyId == value){
										return '<a title=\"' + value  + '\"' + ' style = \"margin-left: 10px\" href=' + '/partymgr/control/viewprofile?partyId=' + value + '>' + partyData[i].firstName + '&nbsp' + partyData[i].middleName + '&nbsp' + partyData[i].lastName + '&nbsp' + partyData[i].groupName + '&nbsp' + '</a>';
									}
								}
	        					return '<a title=\"' + value  + '\"' + ' style = \"margin-left: 10px\" href=' + '/partymgr/control/viewprofile?partyId=' + value + '>' + value + '</a>'
	    					},
						 	createfilterwidget: function (column, columnElement, widget) {
				   				widget.width(490);
				   			}
					 	},
					 	{ text: '${uiLabelMap.DARoleTypeIdFrom}', width:150, datafield: 'roleTypeIdFrom', filtertype: 'checkedlist', cellclassname: cellclass,
							cellsrenderer: function (row, column, value) {
	        						for(i = 0 ; i < roleTypeData.length; i++){
	        							if(value == roleTypeData[i].roleTypeId){
	        								return '<span title=' + value +'>' + roleTypeData[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
	    					createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(roleTypeData,
				                {
				                    autoBind: true
				                });
				                var empty = {roleTypeId: '', description: 'Empty'};
				   				var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				uniqueRecords2.splice(1, 0, empty);
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'roleTypeId', valueMember : 'roleTypeId', height: '21px',renderer: function (index, label, value) 
								{
									for(i=0;i < uniqueRecords2.length; i++){
										if(uniqueRecords2[i].roleTypeId == value){
											return uniqueRecords2[i].description;
										}
									}
								    return value;
								}});
				   			}
					 	},
					 	{ text: '${uiLabelMap.DARoleTypeIdTo}', width:150, datafield: 'roleTypeIdTo', filtertype: 'checkedlist', cellclassname: cellclass,
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < roleTypeData.length; i++){
	        							if(data.roleTypeIdTo == roleTypeData[i].roleTypeId){
	        								return '<span title=' + value +'>' + roleTypeData[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
	    					createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(roleTypeData,
				                {
				                    autoBind: true
				                });
				   				var empty = {roleTypeId: '', description: 'Empty'};
				   				var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				uniqueRecords2.splice(1, 0, empty);
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'roleTypeId', valueMember : 'roleTypeId', height: '21px',renderer: function (index, label, value) 
								{
									for(i=0;i < uniqueRecords2.length; i++){
										if(uniqueRecords2[i].roleTypeId == value){
											return uniqueRecords2[i].description;
										}
									}
								    return value;
								}});
								widget.jqxDropDownList('checkAll');
				   			}
					 	},
					 	{ text: '${uiLabelMap.DAAgreementTypeId}', width:150, datafield: 'agreementTypeId', filtertype: 'checkedlist', cellclassname: cellclass,
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < agreementTypeData.length; i++){
	        							if(value == agreementTypeData[i].agreementTypeId){
	        								return '<span title = ' + agreementTypeData[i].description +'>' + agreementTypeData[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
	    					createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(agreementTypeData,
				                {
				                    autoBind: true
				                });
				                var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'agreementTypeId', valueMember : 'agreementTypeId', height: '21px',renderer: function (index, label, value) 
								{
									for(i=0;i < agreementTypeData.length; i++){
										if(agreementTypeData[i].agreementTypeId == value){
											return agreementTypeData[i].description;
										}
									}
								    return value;
								}});
								widget.jqxDropDownList('checkAll');
				   			}
					 	},
					 	{text: '${uiLabelMap.DAStatus}', dataField: 'statusId', width: '160px', filtertype: 'checkedlist', cellclassname: cellclass, 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < statusData.length; i++){
	    							if (value == statusData[i].statusId){
	    								return '<span title = ' + statusData[i].description +'>' + statusData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
									renderer: function(index, label, value){
										for(var i = 0; i < statusData.length; i++){
											if(statusData[i].statusId == value){
												return '<span>' + statusData[i].description + '</span>';
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
				   			}
			   			}, 
			   			{ text: '${uiLabelMap.DAProductId}', width:150, datafield: 'productId', cellclassname: cellclass, 
						 	cellsrenderer: function (row, column, value) {
								for(i = 0; i < sourceP.records.length; i++){
									if(sourceP.records[i].productId == value){
										return '<span title = ' + value + '>' + sourceP.records[i].productName + '</span>';
									}
								}
								return '<span title = ' + value + '>' + value + '</span>';
	    					}
    				 	},
					 	{ text: '${uiLabelMap.DAAgreementDate}', width:150, datafield: 'agreementDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', cellclassname: cellclass},
					 	{ text: '${uiLabelMap.DAFromDate}', width:150, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', cellclassname: cellclass},
					 	{ text: '${uiLabelMap.DAThruDate}', width:150, datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', cellclassname: cellclass},
					 	{ text: '${uiLabelMap.DADescription}', width:150, datafield: 'description', cellclassname: cellclass},
					 	{ text: '${uiLabelMap.textValue}', width:150, datafield: 'textData', cellclassname: cellclass}
					 	"/>		

<@jqGrid url="jqxGeneralServicer?sname=JQGetListApAgreement" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addrow="true" addType="popup" addrow="true" addType="popup" deleterow="true"
		 createUrl="jqxGeneralServicer?sname=createAgreementPO&jqaction=C" addColumns="productId;partyIdFrom;partyIdTo;roleTypeIdFrom;roleTypeIdTo;agreementTypeId;agreementDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);description;textData;statusId[AGREEMENT_CREATED]"
		 removeUrl="jqxGeneralServicer?sname=cancelAgreement&jqaction=C" deleteColumn="agreementId" jqGridMinimumLibEnable="false"
		 mouseRightMenu="true" contextMenuId="contextMenu" autorowheight="true"
		 />
<#include "agreementContextMenu.ftl"/>
<#include "copyAgreement.ftl"/>
<#include "newAgreementWindow.ftl"/>
