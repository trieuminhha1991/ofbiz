<script>
var uiLabelMap = {
	MKTG_CAMP_PLANNED : "${uiLabelMap.MKTG_CAMP_PLANNED}",
	MKTG_CAMP_APPROVED : "${uiLabelMap.MKTG_CAMP_APPROVED}",
	MKTG_CAMP_INPROGRESS : "${uiLabelMap.MKTG_CAMP_INPROGRESS}",
	MKTG_CAMP_COMPLETED : "${uiLabelMap.MKTG_CAMP_COMPLETED}",
	MKTG_CAMP_CANCELLED : "${uiLabelMap.MKTG_CAMP_CANCELLED}",
	MKTG_CAMP_WAC : "${uiLabelMap.MKTG_CAMP_WAC}",
	MKTG_CAMP_WAC : "${uiLabelMap.MKTG_CAMP_WAC}",
	MKTG_CAMP_CKPRI : "${uiLabelMap.MKTG_CAMP_CKPRI}",
	MKTG_CAMP_RJPRI : "${uiLabelMap.MKTG_CAMP_RJPRI}",
	MKTG_CAMP_APPAY : "${uiLabelMap.MKTG_CAMP_APPAY}",
	MKTG_CAMP_RFPAY : "${uiLabelMap.MKTG_CAMP_RFPAY}",
	MKTG_CAMP_CEOAP : "${uiLabelMap.MKTG_CAMP_CEOAP}",
	MKTG_CAMP_CEORJ : "${uiLabelMap.MKTG_CAMP_CEORJ}",
	MKTG_CAMP_MDRAP : "${uiLabelMap.MKTG_CAMP_MDRAP}",
	MKTG_CAMP_MDRRJ : "${uiLabelMap.MKTG_CAMP_MDRRJ}",
}
var listMKStatus = [<#if listMKStatus?exists><#list listMKStatus as status>{statusId: "${status.statusId}", description: "${uiLabelMap[status.statusId]}"},</#list></#if>];
var listMKType = [<#if listMKType?exists><#list listMKType as type>{statusId: "${type.marketingTypeId}", description: "${type.name}"},</#list></#if>];
function showCampaign(id){
	window.location.href=  "<@ofbizUrl>EditTradeCampaign</@ofbizUrl>?id=" + id;
}
	
</script>
<div class="">
	<#assign dataField="[{ name: 'marketingCampaignId', type: 'string'},
						 { name: 'campaignName', type: 'string'},
						 { name: 'campaignSummary', type: 'string'},
						 { name: 'fromDate', type: 'date'},
						 { name: 'thruDate', type: 'date'},
						 { name: 'budgetedCost', type: 'string'},
						 { name: 'estimatedCost', type: 'string'},
						 { name: 'people', type: 'string'},
						 { name: 'statusId', type: 'string'},
						 { name: 'isActive', type: 'string'}]"/>
	<#assign columnlist="{ text: '${uiLabelMap.marketingCampaignId}', datafield: 'marketingCampaignId', width: '160px', editable: false, 
							cellsrenderer: function(row, colum, value){
						 		var data = $('#listRequest').jqxGrid('getrowdata', row);
	        					return '<div class=\"click\" style=\"margin-left: 10px;\" onclick=' 
	        							+ 'showCampaign(\"' + data.marketingCampaignId + '\"' + ')' + '>' 
	        							+  data.marketingCampaignId + '</div>'	
					 		}
						 },
						 { text: '${uiLabelMap.campaignName}', datafield: 'campaignName',  width: '200px'},
						 { text: '${uiLabelMap.campaignSummary}', datafield: 'campaignSummary',  width: '220px'},
						 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: '120px',columntype : 'datetimeinput', filtertype: 'date',  cellsformat:'dd-MM-yyyy',
						 	createeditor: function(row, cellvalue, editor){
	                            editor.jqxDateTimeInput({height: '25px', width: 'auto',  formatString: 'dd-MM-yyyy', allowNullDate: true, value: null}); 
							},
						 },
						 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: '120px', filtertype: 'date', cellsformat:'dd-MM-yyyy',columntype : 'datetimeinput',
						 	createeditor: function(row, cellvalue, editor){
	                            editor.jqxDateTimeInput({height: '25px', width: 'auto',  formatString: 'dd-MM-yyyy', allowNullDate: true, value: null}); 
							},
						 },
						 { text: '${uiLabelMap.budgetedCost}', datafield: 'budgetedCost', hidden: true},
						 { text: '${uiLabelMap.estimatedCost}', datafield: 'estimatedCost', hidden: true},
						 { text: '${uiLabelMap.people}', datafield: 'people', hidden: true},
						 { text: '${uiLabelMap.statusId}', datafield: 'statusId', filtertype: 'checkedlist', columntype:'dropdownlist',editable: false,
						 	cellsrenderer: function(row, column, value){
				 				return '<div style=\"margin-top: 4px; margin-left: 5px;\">' + uiLabelMap[value] + '</div>';
					 		},
					 		createfilterwidget: function(column, columnElement, widget){
							    var filterBoxAdapter = new $.jqx.dataAdapter(listMKStatus, {autoBind: true});
								var dataSoureList = filterBoxAdapter.records;
							    dataSoureList.splice(0, 0, {statusId: 999999, description: '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})'});
							    widget.jqxDropDownList({checkboxes: true, source: dataSoureList, displayMember: 'description', dropDownHeight: 300, autoDropDownHeight: false,valueMember : 'statusId', filterable:true, searchMode:'containsignorecase'});
								/*widget.on('checkChange', function(event){
									var selected = event.args;
									if(args.value == 999999){
										if(args.checked){
											widget.jqxDropDownList('checkAll');
										}else{
											widget.jqxDropDownList('uncheckAll');
										}
									}
								});*/
							}
					 	 },
					 	 { text: '${uiLabelMap.isActive}', datafield: 'isActive', filtertype: 'checkedlist', width: '100px',columntype: 'dropdownlist',
						 	cellsrenderer: function(row, column, value){
						 		return '<div style=\"margin-top: 4px; margin-left: 5px;\">' + value + '</div>';
					 		},
					 		createeditor: function(row, cellvalue, editor){
								var sourceGlat =
					            {
					                localdata: [\"N\", \"Y\"],
					                datatype: \"array\"
					            };
					            var current = $('#listRequest').jqxGrid('getrowdata', row);
					            var selectedIndex = 0;
					            if(current.isCompulsory == 'Y'){
					            	selectedIndex = 1;
					            } 
					            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
	                            editor.jqxDropDownList({source: dataAdapterGlat, selectedIndex: selectedIndex,  autoDropDownHeight: true}); 
							},
							createfilterwidget: function(column, columnElement, widget){
							    var sourceGlat =
					            {
					                localdata: [\"N\", \"Y\"],
					                datatype: \"array\"
					            };
					            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat, {autoBind: true});
								var dataSoureList = dataAdapterGlat.records;
							    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({checkboxes: true, source: dataAdapterGlat, dropDownWidth: 100 , autoDropDownHeight: true});
							},
					 	 }"/>
	
	<@jqGrid url="jqxGeneralServicer?sname=JQGetListTradeMarketing&type=spl" dataField=dataField columnlist=columnlist
		clearfilteringbutton="true"
		autorowheight="true"
		filterable="true"
		editable="true"
		editrefresh="true"
		editmode="click"
		showtoolbar = "true" deleterow="true"
		id="listRequest" contextMenuId="jqxmenu" mouseRightMenu="true"
		removeUrl="jqxGeneralServicer?sname=deletePartyInsuranceReport&jqaction=D" deleteColumn="marketingCampaignId"
		createUrl="jqxGeneralServicer?jqaction=C&sname=createMarketingCampaignHeader" alternativeAddPopup="popupAddrow" addrow="true" addType="popup" 
		addColumns="marketingCampaignId;marketingName;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);statusId;" addrefresh="true"
		updateUrl="jqxGeneralServicer?jqaction=U&sname=updateMarketingCampaignHeader"  
		editColumns="marketingCampaignId;campaignName;campaignSummary;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);statusId;isActive"
/>
</div>
<div id='jqxmenu'  style="display:none;">
    <ul>
        <li id="edit"><a href="javascript:void(0)"><i class='fa fa-edit'></i>&nbsp;Edit</a></li>
        <li id="del"><a href="javascript:void(0)"><i class='fa fa-trash'></i>&nbsp;Delete</a></li>
    </ul>
</div>

