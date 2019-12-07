<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<@jqOlbCoreLib hasCore=false hasValidator=true/>

<script>
	var partySelected = null;
	<#assign status = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "IMPORT_PLAN_STATUS"), null, null, null, false)>
	var statusData = [];
	<#list status as item>
		var row = {};
		<#assign desc = StringUtil.wrapString(item.get('description', locale))/>
		row['statusId'] = "${item.statusId?if_exists}";
		row['description'] = "${desc?if_exists}";
		statusData.push(row);
	</#list>
	
	var getStatusDesc = function (statusId){
		for (var i in statusData) {
			if (statusData[i].statusId == statusId) {
				return statusData[i].description;
			}
		}
		return statusId;
	}

</script>
<#assign dataField="[{ name: 'productPlanId', type: 'string'},
				     { name: 'productPlanCode', type: 'string'},
					 { name: 'parentProductPlanId', type: 'string'},
					 { name: 'productPlanName', type: 'string'},
					 { name: 'productPlanTypeId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'supplierPartyId', type: 'string'},
					 { name: 'supplierPartyCode', type: 'string'},
					 { name: 'supplierPartyName', type: 'string'},
					 { name: 'customTimePeriodId', type: 'string'},
					 { name: 'organizationPartyId', type: 'string'},
					 { name: 'organizationPartyCode', type: 'string'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'},
					 { name: 'createByUserLoginId', type: 'string'},
				]"/>
				
<#assign columnlist="{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.POProductPlanID}', datafield: 'productPlanCode', align: 'left',width: 120,
						cellsrenderer: function(row, colum, value) {
							var data = grid.jqxGrid('getrowdata', row);
							if (!value) value = data.productPlanId;
							return \"<span><a href='listImExPlanItem?productPlanId=\" + data.productPlanId + \"'>\" + value + \"</a></span>\";
						}
					},
					{text: '${uiLabelMap.DmsNamePlan}', datafield: 'productPlanName', align: 'left'},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable:false, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							if (value) return '<span>' + getStatusDesc(value) + '<span>';
							return '<span>' + value + '<span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
						        	if (value) return '<span>' + getStatusDesc(value) + '<span>';
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					},
					{text: '${uiLabelMap.SupplierId}', datafield: 'supplierPartyCode', width: 150, align: 'left'},
					{text: '${uiLabelMap.SupplierName}', datafield: 'supplierPartyName', width: 200, align: 'left'},
					{text: '${uiLabelMap.CurrencyUom}', datafield: 'currencyUomId', width: 100, align: 'left'},
					{text: '${uiLabelMap.FromDate}', datafield: 'fromDate', width: 130, align: 'left', cellsformat: 'dd/MM/yyyy', filtertype: 'range',},
					{text: '${uiLabelMap.ThruDate}', datafield: 'thruDate', width: 130, align: 'left', cellsformat: 'dd/MM/yyyy', filtertype: 'range',},
					{text: '${uiLabelMap.CreatedBy}', datafield: 'createByUserLoginId', width: 150, align: 'left'},
			"/>
			
<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" 
	id="jqxgirdProductPlan"  addrefresh="true" filterable="true" rowsheight="30" selectionmode= "none"
	url="jqxGeneralServicer?sname=JQGetListProductPlan" showtoolbar="true" contextMenuId="contextMenu"	mouseRightMenu="true"
	customcontrol1="fa fa-plus open-sans@${uiLabelMap.DmsCreateNew}@javascript:createProductPlan()"
/>
					
<div id='contextMenu' class="hide">
	<ul>
    	<li><i class="fa fa-plus"></i>${StringUtil.wrapString(uiLabelMap.AddNew)}</li>
    	<li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.Detail)}</li>
    	<li><i class="fa red fa-trash"></i>${StringUtil.wrapString(uiLabelMap.Cancel)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<script>
	var grid = $("#jqxgirdProductPlan");
	$("#contextMenu").jqxMenu({ width: 170, autoOpenPopup: false, mode: 'popup', theme: theme});
		
	$("#contextMenu").on('itemclick', function (event) {
		var rowindex = grid.jqxGrid('getselectedrowindex');
		var data = grid.jqxGrid("getrowdata", rowindex);
		var productPlanId = data.productPlanId;
		var tmpStr = $.trim($(args).text());
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.AddNew)}"){
			createProductPlan();
		}
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.Detail)}"){
			viewDetailPlan(data.productPlanId);
		}
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.Cancel)}"){
			cancelPlan(data.productPlanId, data.statusId);
		}
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
			grid.jqxGrid('updatebounddata');
		}
	});
	
	function createProductPlan(){
		window.location.href = 'createImExPlan';
	}
	
	function viewDetailPlan(productPlanId){
		window.location.href = 'listImExPlanItem?productPlanId=' + productPlanId;
	}
	
	var cancelPlan = function (productPlanId, statusId){
		if (statusId == "IMPORT_PLAN_APPROVED" || statusId == "IMPORT_PLAN_CREATED"){
		
			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}", 
			[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll();}
			}, 
			{"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		$.ajax({
				    		url: "changeProductPlanStatus",
				    		type: "POST",
				    		async: false,
				    		data: {
				    			productPlanId: productPlanId,
				    			statusId: "IMPORT_PLAN_CANCELLED",
				    		},
				    		success: function (res){
				    			if (res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_ != null) {
									jOlbUtil.alert.error(res._ERROR_MESSAGE_);
									Loading.hide("loadingMacro");
									return false;
								}
				    			location.reload();
				    		}
				    	});
					Loading.hide('loadingMacro');
	            	}, 500);
			    }
			}]);
			
		} else {
			jOlbUtil.alert.error("${StringUtil.wrapString(uiLabelMap.BLProductPlanHasBeenCancelled)}");
			return false;
		}
	};
</script>