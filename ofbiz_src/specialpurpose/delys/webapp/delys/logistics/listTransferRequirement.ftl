<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<style type="text/css">
.bootbox{
    z-index: 99000 !important;
}
.modal-backdrop{
    z-index: 89000 !important;
}
</style>
</style>
<script>
	<#assign localeStr = "VI" />
	<#if locale = "en">
	    <#assign localeStr = "EN" />
	</#if>
	
	<#if parameters.transferTypeId == "TRANS_SALES_CHANNEL">
		<#assign requirementTypeId = "TRANS_CHANNEL_REQ">
	<#elseif parameters.transferTypeId == "TRANS_INTERNAL">
		<#assign requirementTypeId = "TRANS_INTERNAL_REQ">
	</#if>
	glRequirementId = null;
	flagChange = false;
	flagCheckBind = true;
	flagCheckReady = false;
	<#assign partyId = userLogin.partyId/>
	<#assign statuses = delegator.findList("StatusItem",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "REQUIREMENT_STATUS"), null, null, null, false) />
	var statusData = new Array();
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description?if_exists}';
		statusData[${item_index}] = row;
	</#list>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = new Array();
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))/>
		row['uomId'] = '${item.uomId?if_exists}';
		row['description'] = '${description?if_exists}';
		uomData[${item_index}] = row;
	</#list>
	
	<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)>
	var currencyUomData = new Array();
	<#list currencyUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.abbreviation?if_exists)/>
		row['currencyUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${abbreviation?if_exists}';
		currencyUomData[${item_index}] = row;
	</#list>
	
	<#assign listRoles = Static["com.olbius.util.SecurityUtil"].getCurrentRoles(userLogin.partyId, delegator)>
	<#assign isSpecialist = false>
	<#assign isStorekeeper = false>
	<#list listRoles as role>
		<#assign roleTypeId = StringUtil.wrapString(role)/>
		<#if roleTypeId == 'LOG_SPECIALIST'>
			<#assign isSpecialist = true>
			<#break>
		</#if>
	</#list>
	<#if !isSpecialist>
		<#list listRoles as role>
			<#assign roleTypeId = StringUtil.wrapString(role)/>
			<#if roleTypeId == 'LOG_STOREKEEPER'>
				<#assign isStorekeeper = true>
				<#break>
			</#if>
		</#list>
	</#if>
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
	</#if>
</script>
<div id="containerNotify" style="width: 100%; height: 20%; margin-top: 15px; overflow: auto;">
</div>
<div id="requirement-list" class="tab-pane">
	<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},"/>	
	<#if isStorekeeper>
	<#assign columnlist = columnlist + " 
				{ text: '${uiLabelMap.requirementId}', dataField: 'requirementId', width: 100, pinned: true,
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						if ('REQ_CREATED' == data.statusId){
							return '<span><a href=\"javascript:void(0);\" onclick=\"showPopupDetail(&#39;'+value+'&#39;)\">'+value+'</a></span>';
						} else {
							return '<span><a href=\"javascript:void(0);\" onclick=\"showPopupUtilDetail(&#39;'+value+'&#39;)\">'+value+'</a></span>';
						}
					}
				},"/>
	<#elseif isSpecialist>
	<#assign columnlist = columnlist + " 
				{ text: '${uiLabelMap.requirementId}', dataField: 'requirementId', width: 100, pinned: true,
					cellsrenderer: function (row, column, value){
						return '<span><a href=\"javascript:void(0);\" onclick=\"showPopupUtilDetail(&#39;'+value+'&#39;)\">'+value+'</a></span>';
					}
				},"/>	
	</#if>
	<#assign columnlist = columnlist + " 
					{ text: '${uiLabelMap.FacilityFrom}', dataField: 'facilityFromName', width: 150,
					 },
					 { text: '${uiLabelMap.OriginContactMech}', dataField: 'originAddress', width: 150,
					 },
					 { text: '${uiLabelMap.FacilityTo}', dataField: 'facilityToName', width: 150,
					 },
					 { text: '${uiLabelMap.DestinationContactMech}', dataField: 'destAddress', width: 150,
					 },
					 { text: '${uiLabelMap.RequireDeliveryDate}', dataField: 'requirementStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'date', cellsalign: 'right'},
			 "/>
   <#if isSpecialist>
		 <#assign columnlist = columnlist + "
				 { text: '${StringUtil.wrapString(uiLabelMap.EstimatedStartTransfer)}', datafield: 'estimatedStartDate', filterable: false, width: 150, align: 'center', cellsformat: 'd', editable: true, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',
					validation: function (cell, value) {
						var id = cell.row;
						var data = $('#jqxgrid').jqxGrid('getrowdata', id);
			        	if (value == null){
			        		return { result: false, message: '${uiLabelMap.FieldRequired}' };
			        	}
				        return true;
				    },
				    initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				        editor.jqxDateTimeInput({ formatString: 'dd/MM/yyyy' });
				        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				        if (data.requirementStartDate){
				        	editor.jqxDateTimeInput('val', data.requirementStartDate);
				        }
				    }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.EstimatedFinishTransfer)}', datafield: 'estimatedArrivalDate', filterable: false, width: 150, align: 'center', cellsformat: 'd', editable: true, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',
					validation: function (cell, value) {
						var id = cell.row;
						var data = $('#jqxgrid').jqxGrid('getrowdata', id);
			        	if (value == null){
			        		return { result: false, message: '${uiLabelMap.FieldRequired}' };
			        	}
				        return true;
				    },
				    initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				        editor.jqxDateTimeInput({ formatString: 'dd/MM/yyyy' });
				        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				        if (data.requirementStartDate){
				        	editor.jqxDateTimeInput('val', data.requirementStartDate);
				        }
				    }
				},
		 "/>
	</#if>
		 <#assign columnlist = columnlist + "
					 { text: '${uiLabelMap.Requestor}', dataField: 'partyId', width: 120, 
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var fullName;
								if (data.firstName){
									fullName = data.firstName;
								}
								if (data.middleName){
									fullName = fullName + ' ' + data.middleName;
								}
								if (data.lastName){
									fullName = fullName + ' ' + data.lastName;
								} 
								if (fullName){
									return '<span title=' + fullName + '>' + fullName +'</span>';
								} else {
									return '<span title=' + value + '>' + value +'</span>';
								}
								
							},
						 },
						 { text: '${uiLabelMap.Status}', dataField: 'statusId', minwidth: 100,
								cellsrenderer: function(row, column, value){
									for(var i = 0; i < statusData.length; i++){
										if(statusData[i].statusId == value){
											return '<span title=' + statusData[i].description + '>' + statusData[i].description + '</span>'
										}
									}
								},
							 },
					 "/>
	<#assign dataField="[{ name: 'requirementId', type: 'string' },
                 	{ name: 'statusId', type: 'string' },
                 	{ name: 'facilityFromName', type: 'string' },
                 	{ name: 'facilityToName', type: 'string' },
                 	{ name: 'originAddress', type: 'string' },
                 	{ name: 'destAddress', type: 'string' },
                 	{ name: 'lastName', type: 'string' },
                 	{ name: 'firstName', type: 'string' },
                 	{ name: 'middleName', type: 'string' },
                 	{ name: 'partyId', type: 'string' },
                 	{ name: 'facilityIdFrom', type: 'string' },
					{ name: 'originContactMechId', type: 'string' },
                 	{ name: 'facilityIdTo', type: 'string' },
                 	{ name: 'destContactMechId', type: 'string' },
                 	{ name: 'requiredByDate', type: 'date', other: 'Timestamp' },
					{ name: 'requirementStartDate', type: 'date', other: 'Timestamp' },
					{ name: 'estimatedStartDate', type: 'date', other: 'Timestamp' },
					{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp' },
		 		 	]"/>
	<#if isStorekeeper>
		<#if parameters.statusId?has_content>
			<#if parameters.statusId == "REQ_CREATED">
				<@jqGrid contextMenuId="Menu" filtersimplemode="true" addrefresh="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
					 url="jqxGeneralServicer?sname=getListRequirements&requirementTypeId=${requirementTypeId?if_exists}&statusId=${parameters.statusId?if_exists}&partyId=${partyId}" createUrl="jqxGeneralServicer?sname=createTransferRequirement&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransferRequirement&jqaction=U"
					 addColumns="productStoreIdFrom;currencyUomId;reason;description;productStoreIdTo;facilityIdFrom;facilityIdTo;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);originContactMechId;destContactMechId;listProducts(java.util.List);requirementTypeId;estimatedBudget" 
					 otherParams="partyId:SL-getRequirementRoles(requirementId,roleTypeId*OWNER)<listRequirementRoles>"/>
			<#else>
				<@jqGrid contextMenuId="Menu" filtersimplemode="true" addrefresh="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
				 url="jqxGeneralServicer?sname=getListRequirements&requirementTypeId=${requirementTypeId?if_exists}&statusId=${parameters.statusId?if_exists}&partyId=${partyId}" createUrl="jqxGeneralServicer?sname=createTransferRequirement&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransferRequirement&jqaction=U"
				 addColumns="productStoreIdFrom;currencyUomId;reason;description;productStoreIdTo;facilityIdFrom;facilityIdTo;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);originContactMechId;destContactMechId;listProducts(java.util.List);requirementTypeId;estimatedBudget" 
				 otherParams="partyId:SL-getRequirementRoles(requirementId,roleTypeId*OWNER)<listRequirementRoles>"/>
			</#if>
		<#else>
			<#if requirementTypeId == "TRANS_CHANNEL_REQ">
				<@jqGrid contextMenuId="Menu" filtersimplemode="true" addrefresh="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterChannelpopupWindow" editable="false" 
					url="jqxGeneralServicer?sname=getListRequirements&requirementTypeId=${requirementTypeId?if_exists}&statusId=REQ_CREATED&partyId=${partyId}" createUrl="jqxGeneralServicer?sname=createTransferRequirement&jqaction=C" updateUrl=""
					addColumns="productStoreIdFrom;currencyUomId;reason;description;productStoreIdTo;facilityIdFrom;facilityIdTo;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);originContactMechId;destContactMechId;listProducts(java.util.List);requirementTypeId;estimatedBudget" 
					otherParams="partyId:SL-getRequirementRoles(requirementId,roleTypeId*OWNER)<listRequirementRoles>"/>
			<#else>
				<@jqGrid contextMenuId="Menu" filtersimplemode="true" addrefresh="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
					url="jqxGeneralServicer?sname=getListRequirements&requirementTypeId=${requirementTypeId?if_exists}&statusId=REQ_CREATED&partyId=${partyId}" createUrl="jqxGeneralServicer?sname=createTransferRequirement&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransferRequirement&jqaction=U"
					addColumns="productStoreIdFrom;currencyUomId;reason;description;productStoreIdTo;facilityIdFrom;facilityIdTo;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);originContactMechId;destContactMechId;listProducts(java.util.List);requirementTypeId;estimatedBudget" 
					otherParams="partyId:SL-getRequirementRoles(requirementId,roleTypeId*OWNER)<listRequirementRoles>"/>
			</#if>
		</#if>
	<#elseif isSpecialist>
		<#if parameters.statusId?has_content>
			<#if parameters.statusId == "REQ_PROPOSED">
				<@jqGrid contextMenuId="Menu" selectionmode="checkbox" filtersimplemode="true"  id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
				 url="jqxGeneralServicer?sname=getListRequirements&requirementTypeId=${requirementTypeId?if_exists}&statusId=${parameters.statusId?if_exists}" createUrl="jqxGeneralServicer?sname=createTransferRequirement&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransferRequirement&jqaction=U"
				 addColumns="productStoreIdFrom;currencyUomId;reason;description;productStoreIdTo;facilityIdFrom;facilityIdTo;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);originContactMechId;destContactMechId;listProducts(java.util.List);requirementTypeId;estimatedBudget" 
				 otherParams="partyId:SL-getRequirementRoles(requirementId,roleTypeId*OWNER)<listRequirementRoles>"
				 customcontrol1="icon-ok@${uiLabelMap.Approve}@javascript:void(0);@approveRequirements()"
				 customcontrol2="icon-remove@${uiLabelMap.RequirementCancel}@javascript:void(0);@cancelRequirements()"/>
			<#elseif parameters.statusId == "REQ_APPROVED">
				<@jqGrid contextMenuId="Menu" selectionmode="checkbox" filtersimplemode="true" editmode="click" updateoffline="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
				 url="jqxGeneralServicer?sname=getListRequirements&requirementTypeId=${requirementTypeId?if_exists}&statusId=${parameters.statusId?if_exists}" createUrl="jqxGeneralServicer?sname=createTransferRequirement&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransferRequirement&jqaction=U"
				 addColumns="productStoreIdFrom;currencyUomId;reason;description;productStoreIdTo;facilityIdFrom;facilityIdTo;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);originContactMechId;destContactMechId;listProducts(java.util.List);requirementTypeId;estimatedBudget" 
				 otherParams="partyId:SL-getRequirementRoles(requirementId,roleTypeId*OWNER)<listRequirementRoles>" rowselectfunction="rowselectfunction(event);"
				 customcontrol1="icon-ok@${uiLabelMap.CreateTransfer}@javascript:void(0);@createTransfers()"/>
			</#if>
		<#else>
			<@jqGrid contextMenuId="Menu" filtersimplemode="true"  id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
			 url="jqxGeneralServicer?sname=getListRequirements&requirementTypeId=${requirementTypeId?if_exists}" createUrl="jqxGeneralServicer?sname=createTransferRequirement&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransferRequirement&jqaction=U"
			 addColumns="productStoreIdFrom;currencyUomId;reason;description;productStoreIdTo;facilityIdFrom;facilityIdTo;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);originContactMechId;destContactMechId;listProducts(java.util.List);requirementTypeId;estimatedBudget" 
			 otherParams="partyId:SL-getRequirementRoles(requirementId,roleTypeId*OWNER)<listRequirementRoles>"/>
		</#if>
	</#if>
</div>
<div>
<#assign columnlistProduct="
				 {
                      text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
                      groupable: false, draggable: false, resizable: false,
                      datafield: '', columntype: 'number', width: 50,
                      cellsrenderer: function (row, column, value) {
                          return '<div style=margin:4px;>' + (value + 1) + '</div>';
                      }
                  },
				 { text: '${uiLabelMap.ProductId}', dataField: 'productId', width: 120, align: 'left', editable: false, pinned: true},
				 { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'left', minwidth: 150, editable: false,
				 },
				 { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: 120, align: 'left', cellsalign: 'right', cellsformat: 'dd/MM/yyyy', editable: false, filtertype: 'date',
				 },
				 { text: '${uiLabelMap.QuantityRequest}', dataField: 'quantity', width: 120, align: 'left', cellsalign: 'right', columntype: 'numberinput', editable: true,
                     validation: function (cell, value) {
                         if (value < 0) {
                             return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
                         }
                         return true;
                     },
                     initeditor: function (row, cellvalue, editor) {
                         editor.jqxNumberInput({ decimalDigits: 0, digits: 10});
                         if (cellvalue){
                        	 editor.jqxNumberInput('val', cellvalue);
                         } else {
                        	 var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
                    		 var test = false;
                			 var rowExp = data.expireDate;
                			 var quantityDis;
                			 for (var i = 0; i < listCurProducts.length; i ++){
                				 var curExp = listCurProducts[i].expireDate;
                				 if (data.productId == listCurProducts[i].productId &&  rowExp.getDate() == curExp.getDate() &&  rowExp.getMonth() == curExp.getMonth() &&  rowExp.getFullYear() == curExp.getFullYear()){
                					 test = true;
                					 quantityDis = listCurProducts[i].quantity;
                					 break;
                				 }
                			 }
                			 if (test){
                				 editor.jqxNumberInput('val', quantityDis);
                			 }
                         }
                     },
                     cellsrenderer: function (row, column, value){
                    	 if (glRequirementId){
                    		 if (value){
                    			 return '<span style=\"text-align: right\">'+value.toLocaleString('${localeStr}')+'</span>';
                    		 } else {
                    			 var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
                        		 var test = false;
                    			 var rowExp = data.expireDate;
                    			 var quantityDis;
                    			 for (var i = 0; i < listCurProducts.length; i ++){
                    				 var curExp = listCurProducts[i].expireDate;
                    				 if (data.productId == listCurProducts[i].productId &&  rowExp.getDate() == curExp.getDate() &&  rowExp.getMonth() == curExp.getMonth() &&  rowExp.getFullYear() == curExp.getFullYear()){
                    					 test = true;
                    					 quantityDis = listCurProducts[i].quantity;
                    					 break;
                    				 }
                    			 }
                    			 if (test){
	                    			 return '<span style=\"text-align: right\">'+quantityDis.toLocaleString('${localeStr}')+'</span>';
                    			 } else {
                    				 return '<span style=\"text-align: right\">'+value.toLocaleString('${localeStr}')+'</span>';
                    			 }
                    		 }
                    	 } else {
                    		 return '<span style=\"text-align: right\">'+value.toLocaleString('${localeStr}')+'</span>';
                    	 }
                     },
                     updaterow: function (rowid, rowdata, commit) {
                         commit(true);
                     }
				 },
				 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', width: 100, filterable: false, align: 'left', cellsalign: 'right', columntype: 'dropdownlist', 
					 initeditor: function (row, cellvalue, editor) {
		                   var packingUomData = new Array();
		                   var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
		                   var packingUomIdArray = data['qtyUomIds'];
		                   for (var i = 0; i < packingUomIdArray.length; i++) {
			                    var packingUomIdItem = packingUomIdArray[i];
			                    var row = {};
			                    row['quantityUomId'] = '' + packingUomIdItem.uomId;
			                    row['description'] = '' + packingUomIdItem.description;
			                    packingUomData[i] = row;
		                   }
		                   var sourceDataPacking =
		                   {
			                   localdata: packingUomData,
			                   datatype: 'array'
		                   };
		                   var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
		                   editor.jqxDropDownList({ selectedIndex: 0, source: dataAdapterPacking, displayMember: 'description', valueMember: 'quantityUomId'
		                   });
					 },
					 cellsrenderer: function(row, column, value){
						for(var i = 0; i < uomData.length; i++){
							if(uomData[i].uomId == value){
								return '<span title=' + uomData[i].description + '>' + uomData[i].description + '</span>'
							}
						}
					},
				 },
				 { text: '${uiLabelMap.SummaryATP}', dataField: 'ATP', width: 150, align: 'center', editable: false,
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);	
						var desc;
						for(var i = 0; i < uomData.length; i++){
							if(uomData[i].uomId == data.quantityUomId){
								desc = uomData[i].description;
							}
						}
						return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + ' ('+desc+')</span>'
					},
				 },
				 { text: '${uiLabelMap.SummaryQOH}', dataField: 'QOH', width: 150, align: 'center', editable: false,
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);	
						var desc;
						for(var i = 0; i < uomData.length; i++){
							if(uomData[i].uomId == data.quantityUomId){
								desc = uomData[i].description;
							}
						}
						return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + ' ('+desc+')</span>'
					},
				 },
				 "/>
<#assign dataFieldProduct="[{ name: 'productId', type: 'string' },
             	{ name: 'productName', type: 'string' },
				{ name: 'ATP', type: 'number' },
             	{ name: 'QOH', type: 'number' },
             	{ name: 'expireDate',  type: 'date', other: 'Timestamp'},
             	{ name: 'quantity', type: 'number' },
             	{ name: 'quantityUomId', type: 'string' },
             	{ name: 'qtyUomIds', type: 'string' },
	 		 	]"/>
	<@jqGrid selectionmode="checkbox" customTitleProperties="ProductListProduct" idExisted="true" filtersimplemode="true" width="900" autoheight="false" height="290" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" 
	id="jqxgridProduct" 
	dataField=dataFieldProduct columnlist=columnlistProduct clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" editable="true" 
	 url="" editmode="click" bindresize="false" jqGridMinimumLibEnable="false" otherParams="qtyUomIds:S-getProductPackingUoms(productId)<listPackingUoms>;" updateoffline="true"
	 customcontrol1="icon-plus@${uiLabelMap.AddRow}@javascript:void(0);@addProductToReq()" readyFunction="gridPopupReadyFunction()" customLoadFunction="true"
	 />
</div>
<div>
<#assign columnlistProductDetail="
				 {
                      text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
                      groupable: false, draggable: false, resizable: false,
                      datafield: '', columntype: 'number', width: 50,
                      cellsrenderer: function (row, column, value) {
                          return '<div style=margin:4px;>' + (value + 1) + '</div>';
                      }
                  },
				 { text: '${uiLabelMap.ProductId}', dataField: 'productId', width: 150, align: 'left', editable: false, pinned: true},
				 { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'left', minwidth: 150, editable: false,
				 },
				 { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: 120, align: 'left', cellsalign: 'right', cellsformat: 'dd/MM/yyyy', editable: false, filtertype: 'date',
				 },
				 { text: '${uiLabelMap.QuantityRequest}', dataField: 'quantity', width: 120, align: 'left', cellsalign: 'right', columntype: 'numberinput', editable: true,
                     cellsrenderer: function (row, column, value){
                		 return '<span style=\"text-align: right\">'+value.toLocaleString('${localeStr}')+'</span>';
                     },
				 },
				 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', width: 100, filterable: false, align: 'left', cellsalign: 'right', columntype: 'dropdownlist', 
					 cellsrenderer: function(row, column, value){
						for(var i = 0; i < uomData.length; i++){
							if(uomData[i].uomId == value){
								return '<span title=' + uomData[i].description + '>' + uomData[i].description + '</span>'
							}
						}
					},
				 },
				 "/>
<#assign dataFieldProductDetail="[{ name: 'productId', type: 'string' },
             	{ name: 'productName', type: 'string' },
             	{ name: 'expireDate',  type: 'date', other: 'Timestamp'},
             	{ name: 'quantity', type: 'number' },
             	{ name: 'quantityUomId', type: 'string' },
	 		 	]"/>
	<@jqGrid customTitleProperties="ProductListProduct" idExisted="true" filtersimplemode="true" width="900" autoheight="false" height="275" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" 
		id="jqxgridProductDetail" dataField=dataFieldProductDetail columnlist=columnlistProductDetail clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" editable="false" 
		url="" editmode="click" bindresize="false" jqGridMinimumLibEnable="false" updateoffline="false"
		readyFunction="gridPopupReadyFunction()" customLoadFunction="true"
	 />
</div>

<div id="popupDetailWindow" style="display: none">
	<div>${uiLabelMap.TransferRequirement}</div>
	<div style="overflow: hidden;">
	    <div>
			<h4 class="row header smaller lighter blue" style="margin-right:25px !important;margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				${uiLabelMap.GeneralInfo}
			</h4>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.CreatedBy}:</div>
						</div>
						<div class="span7">
							<div><div id="createdByUserLogin" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.FacilityFrom}:</div>
						</div>
						<div class="span7">
							<div><div id="originFacilityIdDT" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.OriginContactMech}:</div>
						</div>
						<div class="span7">
							<div><div id="originContactMechIdDT" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.TransferDate}:</div>
						</div>
						<div class="span7">
							<div><div id="requirementStartDateDT" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.Reason}:</div>
						</div>
						<div class="span7">
							<div><div id="reasonDT" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.Status}:</div>
						</div>
						<div class="span7">
							<div><div id="statusIdDT" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.FacilityTo}:</div>
						</div>
						<div class="span7">
							<div><div id="destFacilityIdDT" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.DestinationContactMech}:</div>
						</div>
						<div class="span7">
							<div><div id="destContactMechIdDT" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.RequiredByDate}:</div>
						</div>
						<div class="span7">
							<div><div id="requiredByDateDT" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.Description}:</div>
						</div>
						<div class="span7">
							<div><div id="descriptionDT" class="green-label" style="width: 200px;"></div></div>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div style="margin: 20px 0px 10px 20px !important;"><div id="jqxgridProductDetail"></div></div>
				</div>
			</div>
			<div class="form-action">
	            <div class='row-fluid'>
	                <div class="span12 margin-top20" style="">
	                    <button id="detailCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:6px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
	                </div>
	            </div>
	        </div>
		</div>
	</div>
</div>

<div style="display: none" id="productStoreIdFrom"></div>
<div style="display: none" id="productStoreIdTo"></div>

<div id="alterpopupWindow" style="display: none">
	<div>${uiLabelMap.CreateNewTransferRequirement}</div>
	<input type="hidden" name="requirementTypeId" value="${requirementTypeId?if_exists}"></input>
	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<h4 class="row header smaller lighter blue" style="margin: 5px 25px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
	            ${uiLabelMap.GeneralInfo}
	        </h4>
	        <div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.FacilityFrom}:</div>
						</div>
						<div class="span7">
							<div><div id="facilityIdFrom" class="green-label"></div></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.OriginContactMech}:</div>
						</div>
						<div class="span7">
							<div><div id="originContactMechId" class="green-label"></div></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.RequiredByDate}:</div>
						</div>
						<div class="span7">
							<div><div id="requiredByDate" class="green-label"></div></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.TransferDate}:</div>
						</div>
						<div class="span7">
							<div><div id="requirementStartDate" class="green-label"></div></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.Reason}:</div>
						</div>
						<div class="span7">
							<div style="width: 195px; display: inline-block;"><input id="reason"></input></div><a onclick="showReasonEditor()" style="display: inline-block, padding-left: 10px"><i style="padding-left: 20px;" class="icon-edit"></i></a>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.FacilityTo}:</div>
						</div>
						<div class="span7">
							<div><div id="facilityIdTo" class="green-label"></div></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.DestinationContactMech}:</div>
						</div>
						<div class="span7">
							<div><div id="destContactMechId" class="green-label"></div></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.estimatedBudget}:</div>
						</div>
						<div class="span7">
							<div><input id="estimatedBudget" type="number" class="green-label"/></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.currencyUomId}:</div>
						</div>
						<div class="span7">
							<div><div id="currencyUomId" class="green-label"></div></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.description}:</div>
						</div>
						<div class="span7">
							<div style="width: 195px; display: inline-block;"><input id="description"></input></div><a onclick="showDescriptionEditor()" style="display: inline-block, padding-left: 10px"><i style="padding-left: 20px;" class="icon-edit"></i></a>
						</div>
					</div>
				</div>
				<div style="margin: 20px 0px 10px 20px !important;"><div id="jqxgridProduct"></div></div>
			</div>
		</div>
		<div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
	        </div>
	    </div>
	</div>
</div>
<div id='Menu' style="display: none">
	<ul>
		<#if isStorekeeper>
			<li id='menuSentReq'>${uiLabelMap.CommonSend}</li>
			<li id='menuDeleteReq'>${uiLabelMap.CommonDelete}</li>
		</#if>
		<#if isSpecialist>
	    	<li id='menuAppoveReq'>${uiLabelMap.Approve}</li>
	    	<li id='menuCreateTransfer'>${uiLabelMap.CreateTransfer}</li>
	    	<li id='menuCancelReq'>${uiLabelMap.RequirementCancel}</li>
	    </#if>
	</ul>
</div>
<div id="jqxReasonWindow" style="display: none">
	<div id="headerReason">
		<span>
		    ${uiLabelMap.Reason}
		</span>
	</div>
	<div style="overflow: hidden;" id="contentReason">
		<textarea id="editorReason">
		</textarea>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelReason" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="okReason" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button> 
				</div>
			</div>
		</div>
	</div>
</div>
<div id="jqxDescriptionWindow" style="display: none">
	<div id="headerDes">
		<span>
		    ${uiLabelMap.Description}
		</span>
	</div>
	<div style="overflow: hidden;" id="contentDes">
		<textarea id="editorDescription">
		</textarea>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelDescription" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="okDescription" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="sendNotifyId" style="display: none;">
	<div>
		${uiLabelMap.sentSuccessfully}
	</div>
</div>
<div id="updateNotifyId" style="display: none;">
	<div>
		${uiLabelMap.updateSuccessfully}
	</div>
</div>
<div id="approveNotifyId" style="display: none;">
	<div>
		${uiLabelMap.ApproveSuccessfully}
	</div>
</div>
<div id="deleteNotifyId" style="display: none;">
	<div>
		${uiLabelMap.deleteSuccessfully}
	</div>
</div>
<div id="cancelNotifyId" style="display: none;">
	<div>
		${uiLabelMap.CancelReqSuccessfully}
	</div>
</div>
<div id="createTransferNotifyId" style="display: none;">
	<div>
		${uiLabelMap.CreateTransferSuccessfully}
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript">
	document.oncontextmenu = function() {
	    return false;
	}
	checkShowPopup = true;
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#popupDetailWindow").jqxWindow({
	    maxWidth: 1500, minWidth: 945, modalZIndex: 10000, zIndex:10000, minHeight: 585, maxHeight: 1200, resizable: true, cancelButton: $("#detailCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
	});
	initGridjqxgridProductDetail();
	// show detail
	function showPopupUtilDetail(requirementId){
		var requirementDT;
		$.ajax({
		    type: "POST",
		    url: "getDetailRequirementById",
		    data: {'requirementId': requirementId},
		    dataType: "json",
		    async: false,
		    success: function(response){
		    	requirementDT = response;
		    },
		    error: function(response){
		        alert("Error:" + response);
		    }
		});
		$('#originFacilityIdDT').text(requirementDT.facilityFromName);
		$('#destFacilityIdDT').text(requirementDT.facilityToName);
		$('#originContactMechIdDT').text(requirementDT.facilityFromAddress);
		$('#destContactMechIdDT').text(requirementDT.facilityToAddress);
		
		var requiredByDate = new Date(requirementDT.requiredByDate);
		if (requiredByDate.getMonth()+1 < 10){
			if (requiredByDate.getDate() < 10){
				$("#requiredByDateDT").text('0' + requiredByDate.getDate() + '/0' + (requiredByDate.getMonth()+1) + '/' + requiredByDate.getFullYear());
			} else {
				$("#requiredByDateDT").text(requiredByDate.getDate() + '/0' + (requiredByDate.getMonth()+1) + '/' + requiredByDate.getFullYear());
			}
		} else {
			if (requiredByDate.getDate() < 10){
				$("#requiredByDateDT").text('0' + requiredByDate.getDate() + '/' + (requiredByDate.getMonth()+1) + '/' + requiredByDate.getFullYear());
			} else {
				$("#requiredByDateDT").text(requiredByDate.getDate() + '/' + (requiredByDate.getMonth()+1) + '/' + requiredByDate.getFullYear());
			}
		}
		
		var requirementStartDate = new Date(requirementDT.requirementStartDate);
		if (requirementStartDate.getMonth()+1 < 10){
			if (requirementStartDate.getDate() < 10){
				$("#requirementStartDateDT").text('0' + requirementStartDate.getDate() + '/0' + (requirementStartDate.getMonth()+1) + '/' + requirementStartDate.getFullYear());
			} else {
				$("#requirementStartDateDT").text(requirementStartDate.getDate() + '/0' + (requirementStartDate.getMonth()+1) + '/' + requirementStartDate.getFullYear());
			}
		} else {
			if (requirementStartDate.getDate() < 10){
				$("#requirementStartDateDT").text('0' + requirementStartDate.getDate() + '/' + (requirementStartDate.getMonth()+1) + '/' + requirementStartDate.getFullYear());
			} else {
				$("#requirementStartDateDT").text(requirementStartDate.getDate() + '/' + (requirementStartDate.getMonth()+1) + '/' + requirementStartDate.getFullYear());
			}
		}
		for (var i = 0; i < statusData.length; i ++){
			if (requirementDT.statusId == statusData[i].statusId){
				$('#statusIdDT').text(statusData[i].description);
			}
		}
		$('#reasonDT').text(requirementDT.reason);
		$('#descriptionDT').text(requirementDT.description);
		if (requirementDT.createdByPartyName){
			$('#createdByUserLogin').text(requirementDT.createdByPartyName);
		} else {
			$('#createdByUserLogin').text(requirementDT.createdByUserLogin);
		}
		var tmpS = $("#jqxgridProductDetail").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getRequirementItems&requirementId="+requirementDT.requirementId;
	 	$("#jqxgridProductDetail").jqxGrid('source', tmpS);
	 	
		$("#popupDetailWindow").jqxWindow('open');
	}
	//Create Window
	var curFacId = null;
	$('#document').ready(function(){
		
		loadDataConfigPacking();
		$("#description").jqxInput({placeHolder: ". . .", height: 20, width: '195', minLength: 1});
		$("#reason").jqxInput({placeHolder: ". . .", height: 20, width: '195', minLength: 1});
		
		$("#jqxReasonWindow").jqxWindow({
			maxWidth: 640, minWidth: 640, minHeight: 360, maxHeight: 360, resizable: true,  isModal: true, autoOpen: false, initContent : function(){
				$('#editorReason').jqxEditor({
		            height: '85%',
		            width: '100%',
		            theme: theme,
		        });
			},
		});
		
		$("#jqxDescriptionWindow").jqxWindow({
			maxWidth: 640, minWidth: 640, minHeight: 360, maxHeight: 360, resizable: true,  isModal: true, autoOpen: false, initContent : function(){
				$('#editorDescription').jqxEditor({
		            height: '85%',
		            width: '100%',
		            theme: theme,
		        });
			},
		});
		
		$("#updateNotifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
            autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success",
        });
		$("#sendNotifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
            autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success",
        });
		$("#approveNotifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
            autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
        });
		$("#deleteNotifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
            autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
        });
		$("#cancelNotifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
            autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
        });
		$("#createTransferNotifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
            autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 3000, template: "success"
        });
		$("#estimatedBudget").jqxInput({height: 22, width: 200, minLength: 1});
		
		// init popup
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 1500, minWidth: 960, modalZIndex: 10000, minHeight: 645, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:theme           
		});
		initGridjqxgridProduct();
		
		$('#alterpopupWindow').on('open', function (event) {
			flagChange = true;
			listCurProducts = new Array();
		 	if (glRequirementId){
		 		return false;
		 	} else {
		 		updateMultiElement({
					productStoreId: $("#productStoreIdFrom").val(),
					productStoreIdTo: $("#productStoreIdTo").val(),
					contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
					contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
				}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'facilityIdFrom', 'facilityIdTo', 'originContactMechId', 'destContactMechId');
		 		curFacId = $("#facilityIdFrom").val();
		 	}
		});
		curFacId = $("#facilityIdFrom").val();
	});
	
	$('#alterpopupWindow').on('close', function (event) {
		glRequirementId = null;
		flagChange = false;
		flagCheckBind = true;
		testAdd = true;
		listCurProducts = new Array();
		$('#jqxgridProduct').jqxGrid('clear');
		$('#jqxgridProduct').jqxGrid('clearselection');
		$("#facilityIdFrom").jqxDropDownList('clear');
		$("#facilityIdTo").jqxDropDownList('clear');
		$("#productStoreIdFrom").jqxDropDownList({selectedIndex: 0});
		$("#productStoreIdTo").jqxDropDownList({selectedIndex: 0});
		$("#originContactMechId").jqxDropDownList('clear');
		$("#destContactMechId").jqxDropDownList('clear');
		$("#requirementStartDate").jqxDateTimeInput('setDate', new Date());
		$("#requiredByDate").jqxDateTimeInput('setDate', new Date());
		$("#estimatedBudget").val(0);
		$("#currencyUomId").jqxDropDownList('val', 'VND');
		$("#reason").val('');
		$("#description").val('');
		
		updateMultiElement({
			productStoreId: $("#productStoreIdFrom").val(),
			productStoreIdTo: $("#productStoreIdTo").val(),
			contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
			contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
		}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'facilityIdFrom', 'facilityIdTo', 'originContactMechId', 'destContactMechId');
	
	});
	var listCurProducts = new Array();
	var testAdd = true;
	function gridPopupReadyFunction(){
		flagCheckReady = true;
		$("#jqxgridProduct").on("bindingComplete", function (event) {
			if (glRequirementId){
				var listRequirementItems = new Array();
			 	$.ajax({
				    type: "POST",
				    url: "getItemByRequirementId",
				    data: {'requirementId': glRequirementId},
				    dataType: "json",
				    async: false,
				    success: function(response){
				    	listRequirementItems = response.listRequirementItems;
				    },
				    error: function(response){
				        alert("Error:" + response);
				    }
				});
			 	if (listRequirementItems){
			 		for (var i=0; i<listRequirementItems.length; i++){
			 			var temp = {};
			 			temp['productId']=listRequirementItems[i].productId;
			 			var dateTemp = listRequirementItems[i].expireDate;
			 			var expireDateTmp = new Date(dateTemp.time);
			 			temp['expireDate']=expireDateTmp;
			 			temp['quantity']=listRequirementItems[i].quantity;
			 			listCurProducts.push(temp);
			 		}
			 	}
				$('#jqxgridProduct').jqxGrid('clearselection');
				var rows = $('#jqxgridProduct').jqxGrid('getrows');
			 	for (var i = 0; i < rows.length; i ++){
			 		for (var j=0; j<listCurProducts.length; j++){
			 			var rowExp = rows[i].expireDate;
		 				var curExp = listCurProducts[j].expireDate;
		 				if (rows[i].productId == listCurProducts[j].productId &&  rowExp.getDate() == curExp.getDate() &&  rowExp.getMonth() == curExp.getMonth() &&  rowExp.getFullYear() == curExp.getFullYear()){
		 					$('#jqxgridProduct').jqxGrid('selectrow', i);
		 					break;
		 				}
			 		}
			 	}
			}
	 	});
	}
	
	function addProductToReq(){
		if (testAdd){
			if (glRequirementId){
				checkShowPopup = false;
				var tmpS = $("#jqxgridProduct").jqxGrid('source');
				var curFacilityId = $('#facilityIdFrom').val();
				tmpS._source.url = "jqxGeneralServicer?sname=getListProducts&facilityId="+curFacilityId+"&requirementId="+glRequirementId;
			 	$("#jqxgridProduct").jqxGrid('source', tmpS);
			}
			testAdd = false;
		}
	}
	
	function showPopupDetail(requirementId){
		checkShowPopup = true;
		flagChange = false;
		var requirementDT;
		glRequirementId = requirementId;
		$.ajax({
		    type: "POST",
		    url: "getTransferRequirementDetail",
		    data: {'requirementId': requirementId},
		    dataType: "json",
		    async: false,
		    success: function(response){
		    	requirementDT = response;
		    },
		    error: function(response){
		        alert("Error:" + response);
		    }
		});
		var source = $("#facilityIdFrom").jqxDropDownList('source');
		var tmp = parseInt(source.length);
		if (tmp <= 0){
			updateMultiElement({
				productStoreId: $("#productStoreIdFrom").val(),
				productStoreIdTo: $("#productStoreIdTo").val(),
				contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
				contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
			}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'facilityIdFrom', 'facilityIdTo', 'originContactMechId', 'destContactMechId');
		}
		$("#productStoreIdFrom").val(requirementDT.productStoreIdFrom);
		var tmpS = $("#jqxgridProduct").jqxGrid('source');
	 	var facilityIdDT = requirementDT.facilityIdFrom;
	 	$("#facilityIdFrom").jqxDropDownList('val', facilityIdDT);
	 	update({
			facilityId: $("#facilityIdFrom").val(),
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
	 	$("#originContactMechId").jqxDropDownList('val', requirementDT.originContactMechId);
	 	var facilityIdToDT = requirementDT.facilityIdTo;
	 	$("#facilityIdTo").jqxDropDownList('val', facilityIdToDT);
	 	update({
			facilityId: $("#facilityIdTo").val(),
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
	 	$("#destContactMechId").jqxDropDownList('val', requirementDT.destContactMechId);
	 	$("#productStoreIdTo").val(requirementDT.productStoreIdTo);
	 	if (requirementDT.productStoreIdTo == requirementDT.productStoreIdFrom){
	 		$("#facilityIdTo").jqxDropDownList('removeItem', requirementDT.facilityIdFrom);
	 	}
	 	
	 	$("#requirementStartDate").val(requirementDT.requirementStartDate);
		$("#requiredByDate").val(requirementDT.requiredByDate);
		if (requirementDT.estimatedBudget instanceof Number){
		}
		$("#estimatedBudget").val(requirementDT.estimatedBudget);
		$("#currencyUomId").jqxDropDownList('val', requirementDT.currencyUomId);
		$("#reason").val(requirementDT.reason);
		$("#description").val(requirementDT.description);
		
		tmpS._source.url = "jqxGeneralServicer?sname=getListProducts&requirementId="+requirementId;
	 	$("#jqxgridProduct").jqxGrid('source', tmpS);
	 	
	 	tmpS._source.url = "jqxGeneralServicer?sname=getListProducts&requirementId="+requirementId;
	 	$("#jqxgridProduct").jqxGrid('source', tmpS);
		$("#alterpopupWindow").jqxWindow('open');
		curFacId = $("#facilityIdFrom").val();
	}
	
	function createNewTransfer(){
		var selectedIndexs = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		var listRequirements = new Array();
		for(var i = 0; i < selectedIndexs.length; i++){
			var data = $('#jqxgrid').jqxGrid('getrowdata', selectedIndexs[i]);
			var map = {};
			map['requirementId'] = data.requirementId;
			listRequirements.push(map);
		}
	}
	function showDescriptionEditor(){
		$("#jqxDescriptionWindow").jqxWindow('open');
	}
	function showReasonEditor(){
		$("#jqxReasonWindow").jqxWindow('open');
	}
	$("#okReason").click(function () {
		var des = $('#editorReason').val();
		var tmp = des.substring(5, des.length - 6);
		$("#reason").val(tmp);
		$("#jqxReasonWindow").jqxWindow('close');
	});
	$("#cancelReason").click(function () {
		$("#jqxReasonWindow").jqxWindow('close');
	});
	
	$("#cancelDescription").click(function () {
		$("#jqxDescriptionWindow").jqxWindow('close');
	});
	$("#okDescription").click(function () {
		var des = $('#editorDescription').val();
		var tmp = des.substring(5, des.length - 6);
		$("#description").val(tmp);
		$("#jqxDescriptionWindow").jqxWindow('close');
	});
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getFullYear()) + '-';
			dateStr += addZero(value.getMonth()+1) + '-';
			dateStr += addZero(value.getDate()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	}
	
	var listConfigPacking = [];
	function loadDataConfigPacking(){
		listConfigPacking = [];
		$.ajax({
			url: "loadDataConfigPacking",
			type: "POST",
			data: {},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			listConfigPacking = data["listConfigPacking"];
		});
	}
	
	$('#jqxgridProduct').on('rowSelect', function (event) 
	{	
		if (!glRequirementId){
			var args = event.args;
		    var rowBoundIndex = args.rowindex;
		    var rowData = args.row;
		    if(typeof event.args.rowindex != 'number'){
	            var tmpArray = event.args.rowindex;
	            for(i = 0; i < tmpArray.length; i++){
	                if(checkRequiredTranferProductByFacilityToFacility(tmpArray[i])){
	                    $('#jqxgridProduct').jqxGrid('clearselection');
	                    break; // Stop for first item
	                }
	            }
	        }else{
	            checkRequiredTranferProductByFacilityToFacility(event.args.rowindex);
	        }
		}
	});
	
	function checkRequiredTranferProductByFacilityToFacility(rowindex){
		var data = $('#jqxgridProduct').jqxGrid('getrowdata', rowindex);
		if(data == undefined){
            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                        $("#jqxgridProduct").jqxGrid('begincelledit', rowindex, "quantity");
                    }
                }]
            );
            return true;
		}else{
			var quantity = data.quantity;
			var productId = data.productId;
			var atp = data.ATP;
	    	var quantityUomId = data.quantityUomId;
	        if(quantity == 0 || quantity == undefined){
	            $('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
	                "label" : "OK",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridProduct").jqxGrid('begincelledit', rowindex, "quantity");
	                    }
	                }]
	            );
	            return true;
	        }else{
        		if(quantity > atp){
        			$('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
        			bootbox.dialog("${uiLabelMap.LogCheckQuantityTranfer}!", 
        				[{
                            "label" : "OK",
                            "class" : "btn btn-primary standard-bootbox-bt",
                            "icon" : "fa fa-check",
                            "callback": function() {
                            	$("#jqxgridProduct").jqxGrid('begincelledit', rowindex, "quantity");
                        	}
                        }]
                    );
        			return true;
        		}
	        }
		}
	}
	
	
	// update the edited row when the user clicks the 'Save' button.
	$("#addButtonSave").click(function () {
		var row;
		var selectedIndexs = $('#jqxgridProduct').jqxGrid('getselectedrowindexes');
		if (selectedIndexs.length <= 0){
			bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
			return false;
		}
		var notifyString;
		if (glRequirementId){
			notifyString = "${uiLabelMap.AreYouSureUpdateRequirement}";
		} else {
			notifyString = "${uiLabelMap.AreYouSureCreateNewRequirement}";
		}
		bootbox.confirm(notifyString,function(result){ 
			if(result){	
				var listProducts = new Array();
				for(var i = 0; i < selectedIndexs.length; i++){
					var data = $('#jqxgridProduct').jqxGrid('getrowdata', selectedIndexs[i]);
					var map = {};
					if (data.quantity && parseInt(data.quantity) > 0){
						map['productId'] = data.productId;
						map['quantity'] = data.quantity;
						map['quantityUomId'] = data.quantityUomId;
						map['expireDate'] = data.expireDate.getTime();
						listProducts.push(map);
					}
				}
				listProducts = JSON.stringify(listProducts);
				var requirementStartDate = $('#requirementStartDate').jqxDateTimeInput('value');
				var requiredStartDate = $('#requiredByDate').jqxDateTimeInput('value');
				row = { 
						productStoreIdFrom:$('#productStoreIdFrom').val(),
						productStoreIdTo:$('#productStoreIdTo').val(),
						facilityIdFrom:$('#facilityIdFrom').val(),
						facilityIdTo:$('#facilityIdTo').val(),
						originContactMechId:$('#originContactMechId').val(),
						destContactMechId:$('#destContactMechId').val(),
						requirementStartDate:requirementStartDate.getTime(),
						requiredByDate:requiredStartDate.getTime(),
						estimatedBudget:$('#estimatedBudget').val(),
						requirementTypeId:'${requirementTypeId?if_exists}',
						currencyUomId:$('#currencyUomId').val(),
						reason:$('#reason').val(),
						description:$('#description').val(),
						statusId:"REQ_CREATED",
						partyId: '${partyId}',
						requirementId: glRequirementId,
						listProducts:listProducts
		    	  };
				if (glRequirementId){
					jQuery.ajax({
				        url: "updateTransferRequirementItems",
				        type: "POST",
				        data: row,
				        success: function(res) {
			        		$("#updateNotifyId").jqxNotification("open");
				        }
				    });
				} else {
					$("#jqxgrid").jqxGrid('addRow', null, row, "first");
				}
		    	$("#alterpopupWindow").jqxWindow('close');
		    	$("#jqxgrid").jqxGrid('updatebounddata');
			}
		});
	}); 
	
	var contextMenu = $("#Menu").jqxMenu({ width: 150, height: 58, autoOpenPopup: false, mode: 'popup'});
    $("#jqxgrid").on('contextmenu', function () {
        return false;
    });
    // handle context menu clicks.
    $("#Menu").on('itemclick', function (event) {
        var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.CommonSend)}") {
            bootbox.confirm("${uiLabelMap.AreYouSureSent}",function(result){ 
    			if(result){	
    				var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
    	            var reqId = dataRecord.requirementId;
    				sendRequirement({
    	            	requirementId: reqId,
    	            	roleTypeId: 'LOG_SPECIALIST',
    					sendMessage: 'NewTransferRequirementMustBeApprove',
    					action: "getListTransferRequirements",
    				}, 'sendTransferRequirement', 'jqxgrid');
    			}
    		});
        }
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.Approve)}") {
        	 bootbox.confirm("${uiLabelMap.DAAreYouSureApprove}",function(result){ 
     			if(result){	
     				var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
     	            var reqId = dataRecord.requirementId;
     	            approveRequirement({
     	            	requirementId: reqId,
     				}, 'approveTransferRequirement', 'jqxgrid');
     			}
     		});
        }
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.CreateTransfer)}") {
        	bootbox.confirm("${uiLabelMap.AreYouSureCreateNewTransfer}",function(result){ 
    			if(result){
    				var listRequirements = new Array();
    				var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
    				requirementId = dataRecord.requirementId;
    				var map = {};
    				var estimatedStartDate = dataRecord.estimatedStartDate;
			    	var estimatedArrivalDate = dataRecord.estimatedArrivalDate;
			    	map["requirementId"] = requirementId;
			    	map["estimatedStartDate"] = estimatedStartDate.getTime();
			    	map["estimatedArrivalDate"] = estimatedArrivalDate.getTime();
			    	listRequirements.push(map);
			    	listRequirements = JSON.stringify(listRequirements);
					jQuery.ajax({
				        url: "createTransferFromRequirements",
				        type: "POST",
				        data: {
				        	listRequirements: listRequirements,
				        	action: "viewTransfer",
				        	header: "TransferHaveBeenCreated",
				        },
				        success: function(res) {
			        		$("#jqxgrid").jqxGrid('updatebounddata');
			        		$("#createTransferNotifyId").jqxNotification("open");
				        }
				    });
    			}
    		});
        }
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.CommonDelete)}") {
            bootbox.confirm("${uiLabelMap.DAAreYouSureDelete}",function(result){ 
    			if(result){
    				var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
    				removeRequirement({
    	            	requirementId: dataRecord.requirementId,
    	    		}, 'removeTransferRequirement', 'jqxgrid');
    			}
    		});
        }
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.RequirementCancel)}") {
            bootbox.confirm("${uiLabelMap.AreYouSureCancelReq}",function(result){ 
    			if(result){
    				var listRequirements = new Array();
    				var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
    				requirementId = dataRecord.requirementId;
    				var map = {};
			    	map["requirementId"] = requirementId;
			    	listRequirements.push(map);
			    	listRequirements = JSON.stringify(listRequirements);
					jQuery.ajax({
				        url: "cancelMultiRequirements",
				        type: "POST",
				        data: {
				        	statusId: "REQ_REJECTED",
				        	listRequirements: listRequirements,
				        	action: "getListTransferRequirements",
				        	header: "TransferReqRejected",
				        },
				        success: function(res) {
			        		$("#jqxgrid").jqxGrid('updatebounddata');
			        		$("#cancelNotifyId").jqxNotification("open");
				        }
				    });
    			}
    		});
        }
    });
    function cancelRequirements(){
    	var selectedIndexs = $('#jqxgrid').jqxGrid('getselectedrowindexes');
    	if(selectedIndexs.length == 0 || selectedIndexs == undefined){
    		bootbox.dialog("${uiLabelMap.LogCheckCancelRequireTranfer}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
    		return false;
    	}else{
    		bootbox.confirm("${uiLabelMap.AreYouSureCancelReq}",function(result){ 
     			if(result){	
    				var listRequirements = new Array();
    				for(var i = 0; i < selectedIndexs.length; i++){
    			    	var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', selectedIndexs[i]);
    			    	var reqId = dataRecord.requirementId;
    			    	var map = {};
    			    	map["requirementId"] = reqId;
    			    	listRequirements.push(map);
    				}
    				listRequirements = JSON.stringify(listRequirements);
    				jQuery.ajax({
    			        url: "cancelMultiRequirements",
    			        type: "POST",
    			        data: {
    			        	statusId: "REQ_REJECTED",
    			        	listRequirements: listRequirements,
    			        	action: "getListTransferRequirements",
    			        	header: "TransferReqRejected",
    			        },
    			        success: function(res) {
    		        		$("#jqxgrid").jqxGrid('updatebounddata');
    		        		$("#cancelNotifyId").jqxNotification("open");
    			        }
    			    });
     			}
        	});
    	}
    }
    function createTransfers(){
    	var selectedIndexs = $('#jqxgrid').jqxGrid('getselectedrowindexes');
    	if(selectedIndexs.length == 0 || selectedIndexs == undefined){
    		bootbox.dialog("${uiLabelMap.LogCheckCreateTranfer}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
    		return false;
    	}else{
    		bootbox.confirm("${uiLabelMap.AreYouSureCreateNewTransfer}",function(result){ 
     			if(result){	
    				var listRequirements = new Array();
    				for(var i = 0; i < selectedIndexs.length; i++){
    			    	var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', selectedIndexs[i]);
    			    	var map = {};
    			    	var estimatedStartDate = dataRecord.estimatedStartDate;
    			    	var estimatedArrivalDate = dataRecord.estimatedArrivalDate;
    			    	map["requirementId"] = dataRecord.requirementId;
    			    	map["estimatedStartDate"] = estimatedStartDate.getTime();
    			    	map["estimatedArrivalDate"] = estimatedArrivalDate.getTime();
    			    	listRequirements.push(map);
    				}
    				listRequirements = JSON.stringify(listRequirements);
    				jQuery.ajax({
    			        url: "createTransferFromRequirements",
    			        type: "POST",
    			        data: {
    			        	listRequirements: listRequirements,
    			        	action: "viewTransfer",
    			        	header: "TransferHaveBeenCreated",
    			        },
    			        success: function(res) {
    		        		$("#jqxgrid").jqxGrid('updatebounddata');
    		        		$("#createTransferNotifyId").jqxNotification("open");
    			        }
    			    });
     			}
        	});
    	}
    }
    function approveRequirements(){
    	var selectedIndexs = $('#jqxgrid').jqxGrid('getselectedrowindexes');
    	if(selectedIndexs.length == 0 || selectedIndexs == undefined){
    		bootbox.dialog("${uiLabelMap.LogCheckApproveRequireTranfer}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
    		return false;
    	}else{
    		bootbox.confirm("${uiLabelMap.DAAreYouSureApprove}",function(result){ 
     			if(result){	
    				var listRequirements = new Array();
    				for(var i = 0; i < selectedIndexs.length; i++){
    			    	var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', selectedIndexs[i]);
    			    	var reqId = dataRecord.requirementId;
    			    	var map = {};
    			    	map["requirementId"] = reqId;
    			    	listRequirements.push(map);
    				}
    				listRequirements = JSON.stringify(listRequirements);
    				jQuery.ajax({
    			        url: "approveMultiRequirements",
    			        type: "POST",
    			        data: {
    			        	statusId: "REQ_APPROVED",
    			        	listRequirements: listRequirements,
    			        	action: "getListTransferRequirements",
    			        	header: "TransferReqApproved",
    			        },
    			        success: function(res) {
    		        		$("#jqxgrid").jqxGrid('updatebounddata');
    		        		$("#jqxgrid").jqxGrid('clearselection');
    		        		$("#approveNotifyId").jqxNotification("open");
    			        }
    			    });
     			}
        	});
    	}
    }
    $("#jqxgrid").on('rowClick', function (event) {
        if (event.args.rightclick) {
        	var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', event.args.rowindex);
        	<#if isStorekeeper >
        		if ("REQ_CREATED" == dataRecord.statusId){
            		$("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
            	} 
        		if ("REQ_REJECTED" == dataRecord.statusId){
        			$("#menuSentReq").hide();
            		$("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
            	}
        	<#elseif isSpecialist>
	        	if ("REQ_PROPOSED" == dataRecord.statusId){
	        		$("#menuCreateTransfer").hide();
	        		$("#menuAppoveReq").show();
	    	    	$("#menuCancelReq").show();
	        		$("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
	                var scrollTop = $(window).scrollTop();
	                var scrollLeft = $(window).scrollLeft();
	                contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	                return false;
	        	} 
	        	if ("REQ_APPROVED" == dataRecord.statusId){
	        		$("#menuCreateTransfer").show();
	    	    	$("#menuAppoveReq").hide();
	    	    	$("#menuCancelReq").hide();
	        		$("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
	                var scrollTop = $(window).scrollTop();
	                var scrollLeft = $(window).scrollLeft();
	                contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	                return false;
	        	} 
        	</#if>
        }
    });
    
	<#assign listProductStores = delegator.findList("ProductStoreRoleDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "company", "roleTypeId", "OWNER")), null, null, null, false)>
	var productStoreData = new Array();
	<#list listProductStores as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.storeName?if_exists)/>
		row['productStoreId'] = '${item.productStoreId?if_exists}';
		row['description'] = '${description?if_exists}';
		productStoreData[${item_index}] = row;
	</#list>
	
	var tmpFlag = true;
	var tmpFlag2 = true;
	var facilityFromData = new Array();
	var facilityToData = new Array();
	var originContactMechData = new Array();
	var destContactMechData = new Array();
	var isNull = false;
	var stop = true;
	// create new 
	$("#facilityIdFrom").jqxDropDownList({source: facilityFromData, autoDropDownHeight:true, displayMember:"description", valueMember: "facilityId"});
	$("#facilityIdTo").jqxDropDownList({source: facilityToData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "facilityId"});
	$("#productStoreIdFrom").jqxDropDownList({source: productStoreData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "productStoreId"});
	$("#productStoreIdTo").jqxDropDownList({source: productStoreData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "productStoreId"});
	$("#originContactMechId").jqxDropDownList({source: originContactMechData, autoDropDownHeight:true, displayMember:"description", valueMember: "contactMechId"});
	$("#destContactMechId").jqxDropDownList({source: destContactMechData, autoDropDownHeight:true, displayMember:"description", valueMember: "contactMechId"});
	$("#requirementStartDate").jqxDateTimeInput({height: '25px', formatString: 'dd/MM/yyyy'});
	$("#requiredByDate").jqxDateTimeInput({height: '25px', formatString: 'dd/MM/yyyy'});
	$("#estimatedBudget").jqxNumberInput({ width: '195px', height: '22px', spinButtons: true });
	$("#currencyUomId").jqxDropDownList({source: currencyUomData, autoDropDownHeight:true, displayMember:"description", valueMember: "currencyUomId"});
	$("#currencyUomId").jqxDropDownList('val', 'VND');
	
	$("#productStoreIdTo").change(function(){
		isNull = false;
		if (!glRequirementId){
			updateMultiElement({
	        	productStoreIdTo: $(this).val(),
	        	productStoreId: $("#productStoreIdFrom").val(),
	        	facilityId : $("#facilityIdFrom").val(),
	        	facilityIdTo : $("#facilityIdTo").val(),
	        	contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
				contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
			}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'facilityIdFrom', 'facilityIdTo', 'originContactMechId', 'destContactMechId');
		} else {
			updateFacilityByProductStore($(this).val());
		}
	});
	$("#productStoreIdFrom").change(function(){
		if (glRequirementId && flagChange){
			bootbox.confirm("${uiLabelMap.ChangeFromFacilityInEditTranfer}",function(result){ 
				if(result){
					deleteRequirementItems(glRequirementId);
					updateChangePrductStoreFrom();
				}
			});
		} else {
			updateChangePrductStoreFrom();
		}
	});
	function updateFacilityByProductStore(productStoreId){
		jQuery.ajax({
	        url: "updateFacilityByProductStore",
	        type: "POST",
	        data: {
	        	productStoreId: productStoreId,
	        },
	        async: false,
	        success: function(res) {
	        	var json = res['listFacilities'];
	            renderHtml2(json, "facilityId", "facilityName", "facilityIdTo");
	            $("#facilityIdTo").jqxDropDownList({selectedIndex: 0});
	            update({
					facilityId: $("#facilityIdTo").val(),
					contactMechPurposeTypeId: "SHIPPING_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
	            $("#destContactMechId").jqxDropDownList({selectedIndex: 0});
	        }
	    });
	}
	function updateChangePrductStoreFrom(){
		isNull = false;
		if ($("#productStoreIdTo").val()){
			updateMultiElement({
				productStoreId: $("#productStoreIdFrom").val(),
				productStoreIdTo: $("#productStoreIdTo").val(),
				facilityId : $("#facilityIdFrom").val(),
				facilityIdTo : $("#facilityIdTo").val(),
				contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
				contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
			}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'facilityIdFrom', 'facilityIdTo', 'originContactMechId', 'destContactMechId');
		} else {
			updateMultiElement({
	        	productStoreId: $(this).val(),
	        	productStoreIdTo: $("#productStoreIdTo").val(),
	        	facilityId : $("#facilityIdFrom").val(),
	        	facilityIdTo : $("#facilityIdTo").val(),
	        	contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
				contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
			}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'facilityIdFrom', 'facilityIdTo', 'originContactMechId', 'destContactMechId');
			update({
				facilityId: $("#facilityIdFrom").val(),
				contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
		}
	}
	function updateChangeFacilityFrom(){
//		if (flagChange){
			if(tmpFlag){
				tmpFlag = false;
				return;
			}
			isNull = false;
			if ($("#productStoreIdFrom").val() == $("#productStoreIdTo").val()){
				if ($("#facilityIdFrom").val() == $("#facilityIdTo").val()){
					updateFacilityContactMech({
						productStoreId: $("#productStoreIdTo").val(),
			        	facilityIdTo : $("#facilityIdTo").val(),
			        	contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
						contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
					}, 'getDiffFacilities' , 'listFacilities', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'facilityIdTo', 'originContactMechId', 'destContactMechId');
					tmpFlag = false;
				} else {
					update({
						facilityId: $("#facilityIdFrom").val(),
						contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
						}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
				}
			} else {
				update({
					facilityId: $("#facilityIdFrom").val(),
					contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
			}
			var tmpS = $("#jqxgridProduct").jqxGrid('source');
		 	var curFacilityId = $("#facilityIdFrom").val();
		 	tmpS._source.url = "jqxGeneralServicer?sname=getListProducts&facilityId="+curFacilityId;
		 	$("#jqxgridProduct").jqxGrid('source', tmpS);
//		}
	}
	$("#facilityIdFrom").on('change', function(event){
		if (curFacId == $("#facilityIdFrom").val()){
			return;
		}
		if (glRequirementId && flagChange){
			bootbox.confirm("${uiLabelMap.ChangeFromFacilityInEditTranfer}",function(result){ 
				if(result){
					deleteRequirementItems(glRequirementId);
					updateChangeFacilityFrom();
					curFacId = $("#facilityIdFrom").val();
				} else {
					$("#facilityIdFrom").val(curFacId);
				}
			});
		} else {
			updateChangeFacilityFrom();
		}
	});
	$("#facilityIdTo").on('select', function(event){
		isNull = false;
		if(tmpFlag2){
			tmpFlag2 = false;
			return;
		}
		if ($("#productStoreIdFrom").val() == $("#productStoreIdTo").val()){
			if ($("#facilityIdFrom").val() == $("#facilityIdTo").val()){
				updateFacilityContactMech({
					productStoreId: $("#productStoreIdFrom").val(),
		        	facilityIdTo : $("#facilityIdTo").val(),
		        	contactMechPurposeTypeIdFrom: "SHIPPING_LOCATION",
					contactMechPurposeTypeIdTo: "SHIP_ORIG_LOCATION"
				}, 'getDiffFacilities' , 'listFacilities', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'contactMechId',  'address1', 'contactMechId',  'address1', 'facilityIdFrom', 'destContactMechId', 'originContactMechId');
				tmpFlag2 = false;
			} else {
				update({
					facilityId: $("#facilityIdTo").val(),
					contactMechPurposeTypeId: "SHIPPING_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
			}
		} else {
			update({
				facilityId: $("#facilityIdTo").val(),
				contactMechPurposeTypeId: "SHIPPING_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
		}
	});
	function deleteRequirementItems(requirementId){
		jQuery.ajax({
	        url: "deleteItemOfRequirement",
	        type: "POST",
	        data: {
	        	requirementId: requirementId,
	        },
	        async: false,
	        success: function(res) {
	        }
	    });
	}
	function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml2(json, key, value, id);
	        }
	    });
	}
	function sendRequirement(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	$("#"+jqxgrid).jqxGrid('updatebounddata');
	        	$("#sendNotifyId").jqxNotification("open");
	        }
	    });
	}
	function approveRequirement(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
        		$("#"+jqxgrid).jqxGrid('updatebounddata');
        		$("#approveNotifyId").jqxNotification("open");
	        }
	    });
	}
	function createTransfer(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	window.location.href = "<@ofbizUrl>viewTransfer?transferId="+res.transferId+"</@ofbizUrl>";
	        }
	    });
	}
	function removeRequirement(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	$("#"+jqxgrid).jqxGrid('updatebounddata');
	        	$("#deleteNotifyId").jqxNotification("open");
	        }
	    });
	}
	function updateFacilityContactMech(jsonObject, url, data1, data2, data3, key1, value1, key2, value2, key3, value3, id1, id2, id3) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json1 = res[data1];
	            renderHtml2(json1, key1, value1, id1);
	            var json2 = res[data2];
	            renderHtml2(json2, key2, value2, id2);
	            var json3 = res[data3];
	            renderHtml2(json3, key3, value3, id3);
	        }
	    });
	    tmpFlag = true;
	    tmpFlag2 = true;
	}
	function updateMultiElement(jsonObject, url, data1, data2, data3, data4, key1, value1, key2, value2, key3, value3, key4, value4, id1, id2, id3, id4) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json1 = res[data1];
	            renderHtml2(json1, key1, value1, id1);
	            var json2 = res[data2];
	            renderHtml2(json2, key2, value2, id2);
	            var json3 = res[data3];
	            renderHtml2(json3, key3, value3, id3);
	            var json4 = res[data4];
	            renderHtml2(json4, key4, value4, id4);
//	            if (!glRequirementId && glRequirementId != null && glRequirementId != undefined){
	            	var tmpS = $("#jqxgridProduct").jqxGrid('source');
				 	var curFacilityId = $("#facilityIdFrom").val();
				 	tmpS._source.url = "jqxGeneralServicer?sname=getListProducts&facilityId="+curFacilityId;
				 	$("#jqxgridProduct").jqxGrid('source', tmpS);
//	            } else {
//	            	var tmpS = $("#jqxgridProduct").jqxGrid('source');
//				 	tmpS._source.url = "jqxGeneralServicer?sname=getListProducts";
//				 	$("#jqxgridProduct").jqxGrid('source', tmpS);
//	            }
	        }
	    });
	}
	function renderHtml2(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
//		if (id == "productStoreIdTo"){
			if($("#"+id).length){
				$("#"+id).jqxDropDownList('clear');
				$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
			}
//		} else {
//			if($("#"+id).length){
//				$("#"+id).jqxDropDownList('clear');
//				$("#"+id).jqxDropDownList({source: source});
//			}
//		}
	}
	
	function rowselectfunction(event){
        if(checkRequiredData(event.args.rowindex)){
            $('#jqxgrid').jqxGrid('unselectrow', event.args.rowindex);
        }
	}
	function checkRequiredData(rowindex){
	    var data = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
        if(!data.estimatedStartDate){
            $('#jqxgrid').jqxGrid('unselectrow', rowindex);
            bootbox.dialog("${uiLabelMap.MissingEstimatedStartDate}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                        $("#jqxgrid").jqxGrid('begincelledit', rowindex, "estimatedStartDate");
                    }
                }]
            );
            return true;
        }
        if(!data.estimatedArrivalDate){
            $('#jqxgrid').jqxGrid('unselectrow', rowindex);
            bootbox.dialog("${uiLabelMap.MissingEstimatedCompletedDate}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                        $("#jqxgrid").jqxGrid('begincelledit', rowindex, "estimatedArrivalDate");
                    }
                }]
            );
            return true;
        }
	}
</script>