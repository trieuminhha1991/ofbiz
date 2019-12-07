<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>

<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>

<script>
	var statusId = "PLAN_PROPOSED";  
	function fixSelectAll(dataList) {
		var sourceST = {
		        localdata: dataList,
		        datatype: "array"
		    };
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
		var uniqueRecords2 = filterBoxAdapter2.records;
		uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		return uniqueRecords2;
	}
</script>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PLAN_STATUS"), null, null, null, false) />
<#assign listPartyGroup = delegator.findList("PartyGroupAndPartyRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "BUYER"), null, null, null, false) />
<#assign listPerson = delegator.findList("Person", null, null, null, null, false) />
<#assign dataField="[{ name: 'productPlanId', type: 'string'},
			{ name: 'createByUserLoginId', type: 'string'},
			{ name: 'productPlanName', type: 'string'},
			{ name: 'organizationPartyId', type: 'string'},
			{ name: 'internalPartyId', type: 'string'},
			{ name: 'customTimePeriodId', type: 'string'},
			{ name: 'statusId', type: 'string'}
			]
		"/>
<#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.PlanId}' , datafield: 'productPlanId', width: 150, editable: false,
					},
					{ text: '${uiLabelMap.PlanName}' , datafield: 'productPlanName', width: 200},
					{ text: '${uiLabelMap.POPartyGroup}' , datafield: 'internalPartyId', width: 200, editable: false,
						cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
						} 
					},
					{ text: '${uiLabelMap.organizationPartyId}' , datafield: 'organizationPartyId', filtertype: 'checkedlist', minwidth: 200, editable: false,
						cellsrenderer: function(row, colum, value){
							value?value=mapPartyGroup[value]:value;
							return '<span>' + value + '</span>';
						},createfilterwidget: function (column, htmlElement, editor) {
					    	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listPartyGroup), displayMember: 'partyId', valueMember: 'partyId' ,
					            renderer: function (index, label, value) {
					            	if (index == 0) {
					            		return value;
									}
								    return mapPartyGroup[value];
					            }
					    	});
					    	editor.jqxDropDownList('checkAll');
						}
					},
					{ text: '${uiLabelMap.Status}' , datafield: 'statusId', width: 200, columntype: 'dropdownlist', filtertype: 'checkedlist', editable: false,
						cellsrenderer: function(row, colum, value){
							value?value=mapStatusItem[value]:value;
							return '<span>' + value + '</span>';
						},createfilterwidget: function(row, column, editor){
							editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listStatusItem), displayMember: 'statusId', valueMember: 'statusId',
							    renderer: function (index, label, value) {
							    	if (index == 0) {
					            		return value;
									}
							    	return mapStatusItem[value];
							    } 
							});
							editor.jqxDropDownList('checkAll');
						}
					}
					"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdListPlan" filterable="true"
	url="jqxGeneralServicer?sname=JQXGetListPlanProposalByPO&statusId=PLAN_PROPOSED"
	contextMenuId="contextMenu" mouseRightMenu="true"
/>	

<div id='contextMenu' style="display:none;">
	<ul>
		<li id="viewDetailPlanByProductPlanId"><i class="ace-icon fa fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.ViewPlanDetailTitle}</li>
	</ul>
</div>

<script>
	var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 280, height: 30, autoOpenPopup: false, mode: 'popup'});
	$("#jqxgirdListPlan").on('contextmenu', function () {
	    return false;
	});
	
	$("#viewDetailPlanByProductPlanId").on("click", function() {
		var a = $('#jqxgirdListPlan').jqxGrid('getSelectedRowindex');
		var data = $('#jqxgirdListPlan').jqxGrid('getrowdatabyid', a);
		var productPlanId = data.productPlanId;
		var productPlanName = data.productPlanName;
		var customTimePeriodId = data.customTimePeriodId;
		var internalPartyId = data.internalPartyId;
		var statusId = data.statusId;
		window.location.href = "getListPlanProposalDetailByPO?productPlanId="+productPlanId+"&productPlanName="+productPlanName+"&customTimePeriodId="+customTimePeriodId+"&internalPartyId="+internalPartyId+"&statusId="+statusId;
	});

	function productPlanIdClick(productPlanId) {
		var form = document.createElement("form");
		form.setAttribute('method', "post");
		form.setAttribute('action', "resultPlanOfYear");
		var input = document.createElement("input");
		input.setAttribute('name', "productPlanHeaderId");
		input.setAttribute('value', productPlanId);
		input.setAttribute('type', "hidden");
		form.appendChild(input);
		document.getElementsByTagName('body')[0].appendChild(form);
		form.submit();
	}
	
	var listPartyGroup = [
						<#if listPartyGroup?exists>
							<#list listPartyGroup as item>
								{
									partyId: "${item.partyId?if_exists}",
									groupName: "${StringUtil.wrapString(item.groupName?if_exists)}"
								},
							</#list>
						</#if>
					];
	var mapPartyGroup = {
					<#if listPartyGroup?exists>
						<#list listPartyGroup as item>
							"${item.partyId?if_exists}": "${StringUtil.wrapString(item.groupName?if_exists)}",
						</#list>
					</#if>
					"": ""
				};
	
	/*var listPartyData = 
						[
							<#if listParty?exists>
								<#list listParty as item>
									{
										partyId: "${item.partyId?if_exists}",
										description: "${StringUtil.wrapString(item.description?if_exists)}"
									},
								</#list>
							</#if>
						];
	var mapPartyData = {
					<#if listPartyData?exists>
						<#list listPartyData as item>
							"${item.partyId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
						</#list>
					</#if>
					"": ""
				};*/
	
	var listStatusItem = [
						<#if listStatusItem?exists>
							<#list listStatusItem as item>
								{
									statusId: "${item.statusId?if_exists}",
									description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
								},
							</#list>
						</#if>
	                      ];
	var mapStatusItem = {
						<#if listStatusItem?exists>
							<#list listStatusItem as item>
								"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	var mapPerson = {
		<#if listPerson?exists>
			<#list listPerson as item>
				"${item.partyId?if_exists}": "${StringUtil.wrapString(item.lastName?if_exists)}" + " ${StringUtil.wrapString(item.middleName?if_exists)}" + " ${StringUtil.wrapString(item.firstName?if_exists)}",
			</#list>
		</#if>	
	};
	function fixSelectAll(dataList) {
		var sourceST = {
		        localdata: dataList,
		        datatype: "array"
		    };
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
		var uniqueRecords2 = filterBoxAdapter2.records;
		uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		return uniqueRecords2;
	}
</script>