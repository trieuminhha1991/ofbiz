<#-- from ../supervisor/agents.ftl -->
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<#include "popup/viewAgentOnMap.ftl"/>

<script type="text/javascript">
	var filterObjData = new Object();
	var cellClass = function (row, columnfield, value) {
 		var data = $('#jqxgridListRetailOutlet').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("PARTY_DISABLED" == data.statusId) {
 				return "background-cancel";
 			} else if ("PARTY_ENABLED" == data.statusId) {
 				return "";
 			} else {
 				return "background-important-nd";
 			}
 		}
    }
    var sortByDay = function(arr) {
        var standard = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
        return arr.sort(function(acc, curr) {
            return standard.indexOf(acc.value) - standard.indexOf(curr.value);
        });
    }
    var days = sortByDay([<#if Days?exists><#list Days as day>{
		value: 	'${StringUtil.wrapString(day.dayOfWeek)}',
		description: '${StringUtil.wrapString(day.description)}'
	},</#list></#if>]);
    var dayMap = {};
    <#if Days?exists>
        <#list Days as day>
		dayMap['${StringUtil.wrapString(day.dayOfWeek)}'] = '${StringUtil.wrapString(day.description)}';
        </#list>
    </#if>
</script>

<#assign dataField="[
				{name: 'partyId', type: 'string'},
				{name: 'partyCode', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'distributorName', type: 'string'},
				{name: 'distributorId', type: 'string'},
				{name: 'distributorCode', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'salesmanName', type: 'string'},
				{name: 'salesmanId', type: 'string'},
				{name: 'address1', type: 'string'},
				{name: 'contactNumber', type: 'string'},
				{name: 'emailAddress', type: 'string'},
				{name: 'officeSiteName', type: 'string'},
				{name: 'latitude', type: 'number'},
				{name: 'longitude', type: 'number'},
				{name: 'geoPointId', type: 'string'},
				{name: 'preferredCurrencyUomId', type: 'string'}]"/>
<#assign columnlist = "
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false, width: '5%', cellClassName: RetailOutletList.cellClass,
				    cellsrenderer: function (row, column, value) {
				        return '<div>' + (row + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSAgentId)}', datafield: 'partyCode', width: '10%', cellClassName: RetailOutletList.cellClass,
					cellsrenderer: function(row, column, value, a, b, data){
				        var link = 'AgentDetail?partyId=' + data.partyId;
				        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSAgentName)}', datafield: 'fullName', cellClassName: RetailOutletList.cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: '10%', cellsalign: 'right', sortable: false, cellClassName: RetailOutletList.cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', width: '18%', sortable: false, cellClassName: RetailOutletList.cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSLocation)}', datafield: 'geoPointId', width: '10%', sortable: false, cellClassName: RetailOutletList.cellClass, filterable: false,
					cellsrenderer: function(row, column, value, a, b, data){
					       	var local = \"\", 
					       	localNoFixed = \"\";
					       	if(!!value) {
					       		local = [data.latitude.toFixed(3), data.longitude.toFixed(3)].join(\", \");
				        		localNoFixed = [data.latitude, data.longitude].join(\", \");
					       	}
					        return '<div class=\"jqx-grid-cell-left-align\" style=\"margin-top: 4px;\" title=\"'+localNoFixed+'\">'+local+'</div>';
						}
				},
			 	{text: '${StringUtil.wrapString(uiLabelMap.DADistributor)}', datafield: 'distributorName', width: '10%', sortable: false, hidden: hiddenDistributor, cellClassName: RetailOutletList.cellClass,
					cellsrenderer: function(row, column, value, a, b, data){
				        var link = 'DistributorDetail?partyId=' + data.distributorId;
				        if (data.distributorCode) {
				        	value = data.distributorCode + ' - ' + value;
						}
				        return '<div style=\"margin:4px\"><a href=\"' + link + '\" target=\"_blank\">' + value + '</a></div>';
					}
			 	},
			 	{text: '${StringUtil.wrapString(uiLabelMap.BSSalesmanId)}', datafield: 'salesmanId', width: '11%', sortable: false, cellClassName: RetailOutletList.cellClass},
			 	{text: '${StringUtil.wrapString(uiLabelMap.BSSalesmanName)}', datafield: 'salesmanName', width: '11%', sortable: false, cellClassName: RetailOutletList.cellClass},
			 	{text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: '8%', editable: true, cellClassName: RetailOutletList.cellClass,
					cellsrenderer: function(row, colum, value){
						value?value=mapStatusItem[value]:value;
				        return '<span>' + value + '</span>';
					},
					createfilterwidget: function (column, htmlElement, editor) {
    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' ,
                            renderer: function (index, label, value) {
                            	if (index == 0) {
                            		return value;
								}
							    return mapStatusItem[value];
			                }
    		        	});
					}
				}
			"/>

<#assign customcontrol1=""/>
<#assign mouseRightMenu="false"/>
<#if security.hasEntityPermission("AGENT", "_CREATE", session)>
	<#assign customcontrol1="icon-plus open-sans@${uiLabelMap.BSAddNew}@AddAgent" />
</#if>

<#assign contextMenuItemIdRol = "ctxmnurol">
<#if partyIdFrom?exists>
	<#assign url = "jqxGeneralServicer?sname=JQGetListAgents&partyIdFrom=${partyIdFrom}"/>
<#else>
	<#assign url = "jqxGeneralServicer?sname=JQGetListAgents"/>
</#if>
<#if routeId?exists>
    <#assign url = "jqxGeneralServicer?sname=JQGetListPartyCustomer&routeId=${routeId}"/>
</#if>
<#assign customcontrol2 = "fa fa-file-excel-o@${uiLabelMap.BSExportExcel}@javascript: void(0);@RetailOutletList.exportExcel()">
<@jqGrid id="jqxgridListRetailOutlet" url=url dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" 
		 defaultSortColumn="createdDate" sortdirection="desc" customTitleProperties="${customTitleProperties?if_exists}" isSaveFormData="true" formData="filterObjData"
		 customcontrol1=customcontrol1 customcontrol2=customcontrol2 
		 addrow="false" contextMenuId="contextMenu_${contextMenuItemIdRol}" mouseRightMenu="true"/>

<div id="contextMenu_${contextMenuItemIdRol}" style="display:none;">
	<ul>
		<#if hasOlbPermission("MODULE", "DIS_SALESORDER_VIEW", "")>
		<li id="viewOrder"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListOfOrders}</li>
		</#if>
		<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_VIEW", "")>
		<li id="viewAgreement"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListOfAgreements}</li>
		</#if>
		<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, true)
		||Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, true)>
		<li id="changeDistributor"><i class="fa-retweet"></i>&nbsp;&nbsp;${uiLabelMap.BSChangeDistributor}</li>
		<li id="changeSalesman"><i class="fa-retweet"></i>&nbsp;&nbsp;${uiLabelMap.BSChangeSalesman}</li>
		<li id="changeAddress"><i class="fa-retweet"></i>&nbsp;&nbsp;${uiLabelMap.BSChangeAddress}</li>
		<li id="rejectAgent"><i class="fa-trash red"></i>&nbsp;&nbsp;${uiLabelMap.BSDeactiveRetailer}</li>
		<li id="activeAgent"><i class="fas fa-check"></i>&nbsp;&nbsp;${uiLabelMap.BSActiveRetailer}</li>
        <#if !routeId?exists>
            <li id="viewLocationAgent"><i class="fa-map-marker"></i>&nbsp;&nbsp;${uiLabelMap.BSViewAgentLocationOnMap}</li>
        </#if>
        <li id="viewRouteOfCustomers"><i class="fa fa-road"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSListRouteOfCustomer)}</li>
		<li id="viewVisitingCalendar"><i class="fa-calendar"></i>&nbsp;&nbsp;${uiLabelMap.BSViewVisitingCalendar}</li>
		</#if>
		
	</ul>
</div>
<div class="container_loader">
    <div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
        <div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
            <div>
                <div class="jqx-grid-load"></div>
                <span>${uiLabelMap.BSLoading}...</span>
            </div>
        </div>
    </div>
</div>
<#include "retailOutletSatellite.ftl"/>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />
<@jqOlbCoreLib />
<script type="text/javascript">
	$(document).ready(function() {
		AgentSatellite.init($("#jqxgridListRetailOutlet"));
		$(document).ready(function() {
			$('#jqxgridListRetailOutlet').jqxGrid({ enabletooltips: true });
			RetailOutletList.init();
		});
	});
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	   },</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list></#if>};
	
	<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "DISTRIBUTOR_ADMIN", userLogin, false) || (hiddenDistributor?exists && hiddenDistributor == "true")>
		var hiddenDistributor = true;
	<#else>
		var hiddenDistributor = false;
	</#if>
	
	var RetailOutletList = (function(){
		var mainGrid = $("#jqxgridListRetailOutlet");
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuItemIdRol}"));
		};
		var initEvent = function(){
			$("#contextMenu_${contextMenuItemIdRol}").on('shown', function (event) {
					var rowindex = mainGrid.jqxGrid('getSelectedRowindex');
	        		var dataRecord = mainGrid.jqxGrid('getRowData', rowindex);
	        		if ( dataRecord.statusId == 'PARTY_DISABLED') {
	        			$("#contextMenu_${contextMenuItemIdRol}").jqxMenu('disable', 'rejectAgent', true);
	        			$("#contextMenu_${contextMenuItemIdRol}").jqxMenu('disable', 'activeAgent', false);
	        		}
	       	 		else {
	        			$("#contextMenu_${contextMenuItemIdRol}").jqxMenu('disable', 'rejectAgent', false);
	        			$("#contextMenu_${contextMenuItemIdRol}").jqxMenu('disable', 'activeAgent', true);
	        		}
			});
			$("#contextMenu_${contextMenuItemIdRol}").on("itemclick", function (event) {
		        var args = event.args;
		        var itemId = $(args).attr("id");
		        switch (itemId) {
				case "changeDistributor":
					$("#wcjqxgridDistributor").jqxGrid("clearSelection");
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
			    	var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
			    	if (rowData) {
			    		partyId = rowData.partyId;
			    		if (rowData.distributorId) {
			    			Grid.setDropDownValue($("#divDistributor"), rowData.distributorId, rowData.distributorName);
				    		var source = { datatype: "json",
									datafields: [{ name: "productStoreId" },
									             { name: "storeName" }],
									             url: "loadProductStores?getAll=N&payToPartyId=" + rowData.distributorId};
							var dataAdapter = new $.jqx.dataAdapter(source);
							$("#txtProductStore").jqxComboBox({ source: dataAdapter });
						} else {
							Grid.cleanDropDownValue($("#divDistributor"));
						}
			    		AgentSatellite.openChangeDistributor();
					}
					break;
				case "changeSalesman":
					$("#wcjqxgridSalesman").jqxGrid("clearSelection");
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					if (rowData) {
						partyId = rowData.partyId;
						if (rowData.salesmanId) {
							Grid.setDropDownValue($("#divSalesman"), rowData.salesmanId, rowData.salesmanName);
						} else {
							Grid.cleanDropDownValue($("#divSalesman"));
						}
						AgentSatellite.openChangeSalesman(rowData.distributorId);
					}
					break;
				case "changeAddress":

					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					if (rowData) {
						partyId = rowData.partyId;
						AgentSatellite.openChangeAddress(partyId);
					}
					break;
				case "rejectAgent":
					jOlbUtil.confirm.dialog("${uiLabelMap.ConfirmDeactiveRetailer}", function(result){
						if (result) {
							var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
							var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
							if (rowData) {
								DataAccess.execute({
									url: "setAgentStatus",
									data: {
										partyId: rowData.partyId,
										statusId: 'PARTY_DISABLED'
										}
									}, AgentSatellite.notify);
							}
						}
					}, multiLang.CommonCancel, multiLang.CommonSubmit);
					break;
				case "activeAgent":
					jOlbUtil.confirm.dialog("${uiLabelMap.ConfirmActiveRetailer}", function(result){
						if (result) {
							var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
							var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
							if (rowData) {
								DataAccess.execute({
									url: "setAgentStatus",
									data: {
										partyId: rowData.partyId,
										statusId: 'PARTY_ENABLED'
										}
									}, AgentSatellite.notify);
							}
						}
					}, multiLang.CommonCancel, multiLang.CommonSubmit);
					break;
				case "viewOrder":
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					if (rowData) {
						window.open("listSalesOrderDis?me=agent&sub=Agents&partyId=" + rowData.partyId, '_blank');
					}
					break;
				case "viewAgreement":
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					if (rowData) {
						window.open("AgreementWithAgent?partyIdFrom=" + rowData.partyId, '_blank');
					}
					break;
				case "viewLocationAgent":
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					if (rowData) {
						$(".agentInfo").text(rowData.fullName + " [" + rowData.partyId + "]");
						AgentOnMap.open(rowData);
					
					}
					break; 
					
				case "viewVisitingCalendar":
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					if (rowData) {
						$(".agentInfo").text(rowData.fullName + " [" + rowData.partyId + "]");
                        AgentSatellite.openVisitingCalendar(rowData.partyId);
					}
					break;

				case "viewRouteOfCustomers":
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					if (rowData) {
						$(".agentInfo").text(rowData.fullName + " [" + rowData.partyId + "]");
                        AgentSatellite.openRoutesOfCustomer(rowData.partyId);
					}
					break;
					
				default:
					break;
				}
			});
		};
		var exportExcel = function(){
			<#--window.location.href = "exportRetailOutletListExcel<#if partyIdFrom?exists>&partyIdFrom=${partyIdFrom}</#if>";-->
			var form = document.createElement("form");
			form.setAttribute("method", "POST");
			form.setAttribute("action", "exportRetailOutletListExcel");
			form.setAttribute("target", "_blank");
			
 			var hiddenField0 = document.createElement("input");
	        hiddenField0.setAttribute("type", "hidden");
	        hiddenField0.setAttribute("name", "partyIdFrom");
	        hiddenField0.setAttribute("value", "${partyIdFrom?if_exists}");
	        form.appendChild(hiddenField0);

            var hiddenField1 = document.createElement("input");
            hiddenField1.setAttribute("type", "hidden");
            hiddenField1.setAttribute("name", "routeId");
            hiddenField1.setAttribute("value", "${routeId?if_exists}");
            form.appendChild(hiddenField1);
			
			if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
		    	$.each(filterObjData.data, function(key, value) {
		    		var hiddenField0 = document.createElement("input");
			        hiddenField0.setAttribute("type", "hidden");
			        hiddenField0.setAttribute("name", key);
			        hiddenField0.setAttribute("value", value);
			        form.appendChild(hiddenField0);
		    	});
	    	}
			document.body.appendChild(form);
			form.submit();
		};

        var cellClass = function (row, columnfield, value) {
            var data = $('#jqxgridListRetailOutlet').jqxGrid('getrowdata', row);
            if (typeof(data) != 'undefined') {
                if ("PARTY_DISABLED" == data.statusId) {
                    return "background-cancel";
                }
            }
        };

		return {
			init: init,
			exportExcel: exportExcel,
			cellClass: cellClass,
		};
	}());
</script>
