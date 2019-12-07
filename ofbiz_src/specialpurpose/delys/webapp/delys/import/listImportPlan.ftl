<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>

<#assign customTimePeriodId = parameters.customTimePeriodId !>

<#assign dataField="[{ name: 'productPlanId', type: 'string'},
					{ name: 'createByUserLoginId', type: 'string'},
					{ name: 'productPlanName', type: 'string'},
					{ name: 'organizationPartyId', type: 'string'},
					{ name: 'internalPartyId', type: 'string'},
					{ name: 'statusId', type: 'string'}
					]"/>
<#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.PlanId}' , datafield: 'productPlanId', width: 150, editable: false,
						cellsrenderer: function(row, colum, value){
							return '<span><a href=\"javascript:productPlanIdClick(&#39;' + value + '&#39;)\">' + value + '</a></span>';
						}
					},
					{ text: '${uiLabelMap.accGeoName}' , datafield: 'internalPartyId', width: 200, editable: false},
					{ text: '${uiLabelMap.PlanName}' , datafield: 'productPlanName', width: 200},
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


<#if security.hasEntityPermission("QA", "_ADMIN", session)>

	<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=JQGetListImportPlan&customTimePeriodId=${customTimePeriodId?if_exists}"
		contextMenuId="contextMenu" mouseRightMenu="true"
		/>
	
<div id='contextMenu' style="display:none;">
	<ul>
		<li id="ViewListImportPlan"><i class="icon-task"></i>&nbsp;&nbsp;${uiLabelMap.ViewListImportPlan}</li>
		<li id="ViewListProductPrepareImport"><i class="icon-task"></i>&nbsp;&nbsp;${uiLabelMap.ViewListProductPrepareImport}</li>
	</ul>
</div>
<script>
	var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 280, height: 56, autoOpenPopup: false, mode: 'popup'});
	$("#jqxgrid").on('contextmenu', function () {
	    return false;
	});
	$("#ViewListImportPlan").on("click", function() {
		rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var productPlanId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "productPlanId");
		productPlanIdClick(productPlanId);
	});
	$("#ViewListProductPrepareImport").on("click", function() {
		rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var productPlanId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "productPlanId");
		window.location.href = "viewListProductPrepareImport?productPlanId=" + productPlanId;
	});
</script>
	<#else>
	
	<#if security.hasEntityPermission("IMPORT", "_ADMIN", session)>
		<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
			showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
			customcontrol1="icon-plus-sign open-sans@${uiLabelMap.createImportPlan}@createNewImportPlanForYear"
			customcontrol2="icon-external-link@${uiLabelMap.planOfThisYear}@importPlan"
			url="jqxGeneralServicer?sname=JQGetListImportPlan&customTimePeriodId=${customTimePeriodId?if_exists}"
			updateUrl="jqxGeneralServicer?sname=updateProductPlanHeader&jqaction=U"
			editColumns="productPlanId;productPlanName"
			contextMenuId="contextMenu" mouseRightMenu="true"
			/>
		
		<div id='contextMenu' style="display:none;">
			<ul>
				<li id="ViewListImportPlan"><i class="icon-task"></i>&nbsp;&nbsp;${uiLabelMap.ViewListImportPlan}</li>
				<li id="ViewListAgreement"><i class="icon-task"></i>&nbsp;&nbsp;${uiLabelMap.ViewListAgreement}</li>
			</ul>
		</div>
		
		<script>
			var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 280, height: 56, autoOpenPopup: false, mode: 'popup'});
			$("#jqxgrid").on('contextmenu', function () {
			    return false;
			});
			$("#ViewListImportPlan").on("click", function() {
				rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
				var productPlanId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "productPlanId");
				productPlanIdClick(productPlanId);
			});
			$("#ViewListAgreement").on("click", function() {
				rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
				var productPlanId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "productPlanId");
				window.location.href = "viewListAgreement?productPlanId=" + productPlanId;
			});
		</script>
		<#else>
		<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
			showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
			url="jqxGeneralServicer?sname=JQGetListImportPlan"
			/>
	</#if>
</#if>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PLAN_STATUS"), null, null, null, false) />
<#assign listPartyGroup = delegator.findList("PartyGroupAndPartyRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "BUYER"), null, null, null, false) />
<#assign listPerson = delegator.findList("Person", null, null, null, null, false) />
<script>
		function productPlanIdClick(productPlanId) {
			var form = document.createElement("form");
			form.setAttribute('method', "post");
//			form.setAttribute('method', "get");
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