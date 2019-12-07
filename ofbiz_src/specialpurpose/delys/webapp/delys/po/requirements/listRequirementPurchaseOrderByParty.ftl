<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script>
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	
	<#assign facilities = delegator.findList("Facility", null, null, null, null, false)>
	var listFacilityData = [
							<#if facilities?exists>
								<#list facilities as item>
									{
										facilityId: "${item.facilityId?if_exists}",
										description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapFacilityData = {
						<#if listFacilityData?exists>
							<#list listFacilityData as item>
								"${item.facilityId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	var facilityData = new Array();
	<#list facilities as item>
		var row = {};
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		facilityData[${item_index}] = row;
	</#list>
	
	function getDescriptionByFacilityId(facilityId) {
		for ( var x in facilityData) {
			if (facilityId == facilityData[x].facilityId) {
				return facilityData[x].description;
			}
		}
	}
	
	<#assign packingUoms = delegator.findList("Uom", null, null, null, null, false) />
	var listUomData = [
							<#if packingUoms?exists>
								<#list packingUoms as item>
									{
										uomId: "${item.uomId?if_exists}",
										description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapUomData = {
						<#if listUomData?exists>
							<#list listUomData as item>
								"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	
	
	<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "REQUIREMENT_STATUS"), null, null, null, false) />
	
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
	
	<#assign listProductItem = delegator.findList("Product", null, null, null, null, false) />
	
	var listProductData = [
							<#if listProductItem?exists>
								<#list listProductItem as item>
									{
										productId: "${item.statusId?if_exists}",
										internalName: "${StringUtil.wrapString(item.get("internalName", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapProductIdData = {
						<#if listProductData?exists>
							<#list listProductData as item>
								"${item.productId?if_exists}": "${StringUtil.wrapString(item.get("internalName", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	
	var packingData = new Array();
	<#list packingUoms as item>
		var row = {};
		row['uomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		packingData[${item_index}] = row;
	</#list>
	
	function getDescriptionByUomId(uomId) {
		for ( var x in packingData) {
			if (uomId == packingData[x].uomId) {
				return packingData[x].description;
			}
		}
	}
	
	var productData = 
	[
		<#list listProductItem as product>
		{
			productId: "${product.productId}",
			internalName: "${StringUtil.wrapString(product.get('internalName', locale)?if_exists)}"
		},
		</#list>
	];
	
	function getDescriptionByProductId(productId) {
		for ( var x in productData) {
			if (productId == productData[x].productId) {
				return productData[x].internalName;
			}
		}
	}
	
	
	<#assign listPartyRelationship = delegator.findList("PartyRelationship", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeIdTo", "SUPPLIER"), null, null, null, false) />
	var partyRelationshipData = 
	[
		<#list listPartyRelationship as partyRelationship>
		{
			partyIdTo: "${partyRelationship.partyIdTo}",
			partyIdFrom: "${partyRelationship.partyIdFrom}",
		},
		</#list>
	];
	
	<#assign aaaaaaa = Static['com.olbius.util.SecurityUtil'].getPartiesByRoles("SUPPLIER", delegator)>
	
	var listCurrencyUoms = [
							<#if listCurrencyUoms?exists>
								<#list listCurrencyUoms as item>
								{
									uomId: '${item.uomId?if_exists}',
									description: "${StringUtil.wrapString(item.description)}"
								},
								</#list>
							</#if>
	     	                    ];
		
	var mapCurrencyUoms = {
				        <#if listCurrencyUoms?exists>
			        		<#list listCurrencyUoms as item>
			        			"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
			        		</#list>
				       </#if>
						};
</script>
<style>
.cell-green-color {
    color: black !important;
    background-color: #CCFFFF !important;
}
.cell-gray-color {
	color: black !important;
	background-color: #87CEEB !important;
}
</style>
	<div id="contentNotificationRejectRequestSuccess">
	</div>
					
	<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','jqxgridDetail' + index);
		reponsiveRowDetails(grid);
		if(datarecord.rowDetail){
			var sourceGridDetail =
	        {
	            localdata: datarecord.rowDetail,
	            datatype: 'local',
	            datafields:
	            [
	             	{ name: 'requirementId', type: 'string' },
	             	{ name: 'reqItemSeqId', type: 'string' },
	             	{ name: 'productId', type: 'string' },
	                { name: 'quantity', type: 'number' },
	                { name: 'quantityUomId', type: 'string'},
	            ]
	        };
	        var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	        grid.jqxGrid({
	            width: '98%',
	            height: '92%',
	            theme: 'olbius',
	            localization: {emptydatastring: '${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}'},
	            source: dataAdapterGridDetail,
	            sortable: true,
	            pagesize: 5,
		 		pageable: true,
	            columns: [
							{ text: '${uiLabelMap.accProductName}', datafield: 'productId', align: 'center', width: 300, pinned: true,
								cellsrenderer: function (row, column, value){
									if(value){
										return '<span>' + getDescriptionByProductId(value) + '<span>';
									}
								}
							},
							{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', align: 'center', cellsalign: 'right',
								cellsrenderer: function(row, colum, value){
									if(value){
										return '<span style=\"text-align: right;\">' + value.toLocaleString('${locale}') + '</span>';
									}
							    }, 
							},
							{ text: '${uiLabelMap.UnitProduct}', datafield: 'quantityUomId', align: 'center',
								cellsrenderer: function (row, column, value){
									if(value){
										return '<span>' + getDescriptionByUomId(value) + '<span>';
									}
								}
							},
						]
	        });
		}else {
			grid.jqxGrid({
	            width: '98%',
	            height: '92%',
	            theme: 'olbius',
	            localization: {emptydatastring: '${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}'},
	            source: [],
	            sortable: true,
	            pagesize: 5,
		 		pageable: true,
	            columns: [
							{ text: '${uiLabelMap.accProductName}', datafield: 'productId', align: 'center'},
							{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', align: 'center', width: 150, cellsalign: 'right'},
							{ text: '${uiLabelMap.UnitProduct}', datafield: 'quantityUomId', align: 'center', cellsalign: 'right'},
						]
	        });
			
		}
	}"/>
	<#assign dataField="[
					{ name: 'facilityId', type: 'string'},
					{ name: 'requirementId', type: 'string'},
					{ name: 'createdDate', type: 'date', other: 'Timestamp' },
					{ name: 'requiredByDate', type: 'date', other: 'Timestamp' },
					{ name: 'requirementStartDate', type: 'date', other: 'Timestamp' },
					{ name: 'description', type: 'string'},
					{ name: 'requirementTypeId', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'partyId', type: 'string'},
					{ name: 'rowDetail', type: 'string'}
				]"/>
	<#assign columnlist="
					{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.LogRequiremtId}', datafield: 'requirementId', align: 'center',
					},
					{ text: '${uiLabelMap.LogRequirePurchaseByPartyGroup}', datafield: 'partyId', align: 'center',
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
			        	}, 
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.LogRequirePurchaseCreatedDate)}', datafield: 'createdDate', align: 'left', columntype: 'datetimeinput', editable: false, cellsformat: 'dd/MM/yyyy', filtertype:'range'},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId',
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span>' + mapStatusItem[value] + '<span>';
							}
						}
					},
				"/>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		id="jqxgirdRequirement" addrefresh="true" filterable="true"
		url="jqxGeneralServicer?sname=JQGetListRequirementPurchaseOrderByParty&statusId=REQ_PURCH_SENT"
		customcontrol1="fa fa-thumbs-o-down open-sans@${uiLabelMap.LogRejectRequestPurchaseOrderTitle}@javascript:rejectRequestPurchaseOrder()"
		customcontrol2="fa-file-text-o open-sans@${uiLabelMap.POCreatOrderByRequirement}@javascript:createOrderByRequirement()"
		mouseRightMenu="true" contextMenuId="menuSendRequest" selectionmode= "checkbox" rowselectfunction="rowselectfunction2(event);"
		rowunselectfunction="rowunselectfunction2(event);"
		initrowdetails = "true" initrowdetailsDetail=initrowdetailsDetail
	/>	
				
<div id='menuSendRequest' style="display:none;">
	<ul>
	    <li><i class="fa-envelope"></i>&nbsp;&nbsp;${uiLabelMap.LogTitleSendRequest}</li>
	    <li><i class="fa-trash"></i>&nbsp;&nbsp;${uiLabelMap.DSDeleteRowGird}</li>
	</ul>
</div>

<div id="alterpopupWindow" class="hide">
	<div class="row-fluid">
		${uiLabelMap.LogAddRequestPurchaseLabelProduct}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="contentNotificationCreatePurchaseOrder" class="popup-notification">
			</div>
			<div class="row-fluid">
				<div class="span5">
					<#--<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<label class="asterisk">${uiLabelMap.OrderId} </label>
						</div>
						<div class="span7">
							<input id="orderId" style="width: 100%"></input>
						</div>
					</div>-->
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<label class="asterisk">${uiLabelMap.OrderName} </label>
						</div>
						<div class="span7">
							<input id="orderName" style="width: 100%"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<label class="asterisk">${uiLabelMap.POSupplier} </label>
						</div>
						<div class="span7">
							<div id="supplier" style="width: 100%"></div>
						</div>
					</div>
				</div>
				<div class="span7">
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<label>Entry Date </label>
						</div>
						<div class="span7">
							<div id="entryDate" style="width: 100%"></div>
						</div>	
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<label class="asterisk">${uiLabelMap.currencyUomId} </label>
						</div>
						<div class="span7">
							<div id="currencyUomId" style="width: 100%"></div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="span5">
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<label>Order Date </label>
						</div>
						<div class="span7">
							<div id="orderDate" style="width: 100%"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<label class='asterisk'> ${uiLabelMap.LogRequirePurchaseForFacility} </label>
						</div>
						<div class="span7">
							<div id="originFacilityId" style="width: 100%" class="green-label">
								<div id="jqxgridListFacilityId">
					            </div>
							</div>
						</div>
					</div>
				</div>
				<div class="span7">
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<label class="asterisk"> ${uiLabelMap.PODestination} </label>
						</div>
						<div class="span7" id="contentContactMech">
							<div id="contactMechId">
				            </div>
						</div>
					</div>
				</div>
			</div>
			
	    	<div class="row-fluid">
	    		<div>
	    		    <div style="margin: 20px 0px 10px 20px !important;"><div id="jqxgridRequirementItem"></div></div>
	    	    </div>
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

<div id="jqxNotificationRejectRequestSuccess" >
	<div id="notificationRejectRequestSuccess">
	</div>
</div>

<div id="jqxNotificationCreatePurchaseOrder" >
	<div id="notificationCreatePurchaseOrder">
	</div>
</div>

<div id="jqxNotificationAddSupplierProduct" >
	<div id="notificationAddSupplierProduct">
	</div>
</div>

<div id="alterpopupWindowAddSupplierProduct" style="display:none;">
<div style="font-size:18px!important;">${uiLabelMap.AddNewProductSupplier}</div>
<div style="overflow-y: hidden;">
		<div id="contentNotificationAddSupplierProduct" class="popup-notification">
		</div>
		<div class="row-fluid">
 			<div class="span12" style="margin-top: 18px;">
	 			<div class="span3" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.ProductSupplier}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="supplierAdd"></div></div>
	 			<div class="span4" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.ProductCurrencyUomId}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="productCurrencyUomId"></div></div>
 			</div>
		</div>
		<div class="row-fluid">
 			<div class="span12" style="margin-top: 8px;">
    	 		<div class="span3" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_shippingPrice}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="shippingPrice"></div></div>
	 			<div class="span4" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_lastPrice}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="lastPrice"></div></div>
 			</div>
		</div>
		<div class="row-fluid">
 			<div class="span12" style="margin-top: 8px;">
	 			<div class="span3" style="text-align: right;"><label>${uiLabelMap.FormFieldTitle_minimumOrderQuantity}</label></div>
		 		<div class="span2"><div id="minimumOrderQuantity"></div></div>
		 		<div class="span4" style="text-align: right;"><label>${uiLabelMap.accComments}</label></div>
		 		<div class="span2"><input type='text' id="comments" /></div>
	 		</div>
 		</div>
 		<div class="row-fluid">
 			<div class="span12" style="margin-top: 8px;">
	 			<div class="span3" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.FormFieldTitle_availableFromDate}<span style="color:red;"> *</span></label></div>
	 			<div class="span2"><div id="availableFromDate"></div></div>
	 			<div class="span4" style="text-align: right;"><label style="margin-top: 4px;">${uiLabelMap.FormFieldTitle_availableThruDate}</label></div>
		 		<div class="span2"><div id="availableThruDate"></div></div>
	 		</div>
 		</div>
 		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
 		<div class="row-fluid">
            <div class="span12 margin-top10">
            	<div class="span12">
            		<button id='alterCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.btnCancel}</button>
            		<button id='alterSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
        		</div>
            </div>
    	</div>
   </div>
</div>

<script>
	$('#document').ready(function(){
		loadFacility();
	});
	
	$("#supplierAdd").jqxDropDownList({ source: partyRelationshipData, displayMember: 'partyIdTo', valueMember: 'partyIdTo', disabled: false, placeHolder: '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}', autoDropDownHeight: true,
		renderer: function (index, label, value) 
		{
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
	});
	$("#lastPrice").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: '200px', decimalDigits: 3, min: 0 });
	$("#shippingPrice").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: '200px', decimalDigits: 3, min: 0 });
	$("#productCurrencyUomId").jqxDropDownList({ source: listCurrencyUoms, displayMember: 'description', valueMember: 'uomId', width: '200px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#minimumOrderQuantity").jqxNumberInput({inputMode: 'simple', spinButtons: true, theme: "olbius", width: '200px', decimalDigits: 0, min: 0 });
	$("#comments").jqxInput({width: '195'});
	$("#availableFromDate").jqxDateTimeInput({width: '200px', theme: "olbius"});
	$("#availableThruDate").jqxDateTimeInput({width: '200px', theme: "olbius"});
	$("#alterpopupWindowAddSupplierProduct").jqxWindow({ theme:'olbius',
        width: 1150, maxWidth: 1000, height: 270, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
    });
	
	$('#availableThruDate ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
	$('#availableFromDate').on('valueChanged', function (event){  
		var jsDate = event.args.date; 
		$('#availableThruDate ').jqxDateTimeInput('setMinDate', jsDate);
	}); 
	
	$('#alterpopupWindowAddSupplierProduct').on('close', function () {
		$('#alterpopupWindowAddSupplierProduct').jqxValidator('hide');
		$('#availableThruDate ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
	});
	$('#alterpopupWindowAddSupplierProduct').on('open', function () {
		$('#alterpopupWindow').jqxValidator('hide');
		$("#availableThruDate").jqxDateTimeInput('val', null);
		$("#supplierAdd").jqxDropDownList('clearSelection');
		$("#productCurrencyUomId").jqxDropDownList('clearSelection');
		$("#lastPrice").jqxNumberInput('val', 0);
		$("#shippingPrice").jqxNumberInput('val', 0);
		$("#minimumOrderQuantity").jqxNumberInput('val', 0);
		$("#comments").val("");
	});
	
	$('#alterpopupWindowAddSupplierProduct').jqxValidator({
        rules: [
                { input: '#supplierAdd', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#supplierAdd").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#shippingPrice', message: '${StringUtil.wrapString(uiLabelMap.PriceNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#shippingPrice").val();
                		if (value >= 0) {
                			return true;
						}
                		return false;
                	}
                },
                { input: '#lastPrice', message: '${StringUtil.wrapString(uiLabelMap.PriceNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#lastPrice").val();
                		if (value > 0) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#productCurrencyUomId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#productCurrencyUomId").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#availableFromDate', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#availableFromDate").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#productCurrencyUomId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#productCurrencyUomId").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
               ]
    });
	
	var productDataAddToSupplier = "";
	var supplierYesOrNo = "";
	$('#alterSave').on('click', function () {
		var availableFromDate = $('#availableFromDate').val().toMilliseconds();
        var availableThruDate = $('#availableThruDate').val().toMilliseconds();
        var partyId = $('#supplierAdd').val();
        var currencyUomId = $('#productCurrencyUomId').val();
        var lastPrice = $('#lastPrice').val();
        var minimumOrderQuantity = $('#minimumOrderQuantity').val();
        var shippingPrice = $('#shippingPrice').val();
        var comments = $('#comments').val();
        var validate = $('#alterpopupWindowAddSupplierProduct').jqxValidator('validate');
        if(validate != false){
        	bootbox.confirm("${uiLabelMap.LogAddNewReally}",function(result){ 
    			if(result){	
    				addNewSupplierForProductId(productDataAddToSupplier, availableFromDate, availableThruDate, partyId, currencyUomId, lastPrice, minimumOrderQuantity, shippingPrice, comments);
    			}
        	});
	    }
	});	
	
	var createOrderByRequirementCheck = 0;
	function addNewSupplierForProductId(productDataAddToSupplier, availableFromDate, availableThruDate, partyId, currencyUomId, lastPrice, minimumOrderQuantity, shippingPrice, comments){
		createOrderByRequirementCheck = 1;
		$.ajax({
			url: "addNewSupplierForProductId",
			type: "POST",
			data: {productId: productDataAddToSupplier, availableFromDate: availableFromDate, availableThruDate: availableThruDate, partyId: partyId, currencyUomId: currencyUomId, lastPrice: lastPrice, minimumOrderQuantity: minimumOrderQuantity, shippingPrice: shippingPrice, comments: comments},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			var value = data["value"];
			if(value == "success"){
				createOrderByRequirement();
				$('#alterpopupWindowAddSupplierProduct').jqxWindow('close');
			}
			if(value == "exits"){
				$("#notificationAddSupplierProduct").text('${StringUtil.wrapString(uiLabelMap.POProductExitsInSupplier)}');
				$("#jqxNotificationAddSupplierProduct").jqxNotification('open');
			}
		});
	}
	
	$("#jqxNotificationCreatePurchaseOrder").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreatePurchaseOrder", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationAddSupplierProduct").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddSupplierProduct", opacity: 0.9, autoClose: true, template: "error" });
	$("#originFacilityId").jqxDropDownButton({width: 200});
	$('#originFacilityId').jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'); 
	function loadFacility(){
    	var listFacility;
    	$.ajax({
			url: "loadListFacilityByPO",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listFacility = data["listFacility"];
			bindingDataToJqxGirdFacilityList(listFacility);
		});
    }
    
    function bindingDataToJqxGirdFacilityList(listFacility){
 	    var sourceP2 =
 	    {
 	        datafields:[{name: 'facilityId', type: 'string'},
 	            		{name: 'facilityName', type: 'string'},
         				],
 	        localdata: listFacility,
 	        datatype: "array",
 	    };
 	    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2);
 	    $("#jqxgridListFacilityId").jqxGrid({
 	        source: dataAdapterP2,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        autoheight:true,
	        pageable: true,
 	        columns: [{text: '${uiLabelMap.FacilityId}', datafield: 'facilityId'},
 	          			{text: '${uiLabelMap.DAFacilityName}', datafield: 'facilityName'},
 	        		]
 	    });
    }
    
    var facilityIdByData = "";
    $("#jqxgridListFacilityId").on('rowselect', function (event) {
    	facilityIdByData = "";
        var args = event.args;
        var row = $("#jqxgridListFacilityId").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ getDescriptionByFacilityId(row['facilityId']) +'</div>';
        $('#originFacilityId').jqxDropDownButton('setContent', row['facilityId']);
        facilityIdByData = row['facilityId'];
    });
    
    var contactMechIdByData = "";
    $('#originFacilityId').on('close', function (event) { 
    	contactMechIdByData = "";
    	$.ajax({
			url: "loadContactMechByFacilityId",
			type: "POST",
			data: {facilityId: facilityIdByData},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			var address1 = data["address1"];
			var countryGeoId = data["countryGeoId"];
			var stateProvinceGeoId = data["stateProvinceGeoId"];
			var contactMechId = data["contactMechId"];
			if(address1 != undefined){
				contactMechIdByData = contactMechId;
				$("#contentContactMech").empty();
				$("#contentContactMech").append("<div id='contactMechId'></div>")
				$("#contactMechId").text(address1);
				$("#contactMechId").jqxRadioButton({width: 350});
				$("#contactMechId").append("<p style='margin-left: 23px;'>" + countryGeoId +"</p>");
				$("#contactMechId").append("<p style='margin-left: 23px;'>" + stateProvinceGeoId +"</p>");
			}
			else{
				$("#contactMechId").text('${StringUtil.wrapString(uiLabelMap.PONoValue)}');
			}
		});
    }); 
    
	function loadCurrencyUomId(supplier){
		$.ajax({
			url: "loadCurrencyUomId",
			type: "POST",
			data: {supplier: supplier},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			var listCurrencyUomIdBySupplier = data["listCurrencyUomIdBySupplier"];
			if(listCurrencyUomIdBySupplier.length == 0){
				$("#currencyUomId").jqxDropDownList({ source: [], disabled: false, placeHolder: '${StringUtil.wrapString(uiLabelMap.PONoValue)}', autoDropDownHeight: true});
			}
			else{
				$("#currencyUomId").jqxDropDownList({ source: listCurrencyUomIdBySupplier, displayMember: 'currencyUomId', valueMember: 'currencyUomId', disabled: false, placeHolder: '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}', autoDropDownHeight: true});
			}
		});
	}
	
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 1200, height:600 ,minHeight: 300, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:theme           
	});
	
	$('#alterpopupWindow').on('close', function (event) {
		$('#originFacilityId').jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'); 
		$("#supplier").jqxDropDownList('clearSelection'); 
		$("#currencyUomId").jqxDropDownList('clearSelection');
		contactMechIdByData = "";
		$('#orderName').val("");
		$('#alterpopupWindow').jqxValidator('hide');
		requirementIdTotal = []; 
		dataBindingSoure = [];
		dataSoureBindingByConvert = [];
		createOrderByRequirementCheck = 0;
	}); 
	
	$('#alterpopupWindow').on('open', function (event) {
		createOrderByRequirementCheck = 0;
		$("#contactMechId").text('${StringUtil.wrapString(uiLabelMap.PONoValue)}');
	}); 
	
	$("#currencyUomId").jqxDropDownList({ source: [], disabled: true, placeHolder: '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}', autoDropDownHeight: true});
	/*$("#orderId").jqxInput({width: '195'});*/
	$("#orderName").jqxInput({width: '195'});
	$("#orderDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', showFooter:true,
	     clearString:'Clear'});
	/*$("#estimatedDeliveryDate").jqxDateTimeInput({ formatString: 'dd/MM/yyyy', showFooter:true,
	     clearString:'Clear'});*/
	$("#entryDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', showFooter:true,
	     clearString:'Clear' });
	/*$("#estimatedShipDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', showFooter:true,
	     clearString:'Clear'});*/
	$("#supplier").jqxDropDownList({ source: partyRelationshipData, displayMember: 'partyIdTo', valueMember: 'partyIdTo', disabled: false, placeHolder: '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}', autoDropDownHeight: true,
		renderer: function (index, label, value) 
		{
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
	});
	
	function rejectRequestPurchaseOrder(){
		if(requirementIdTotal.length == 0){
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.LogCheckCancelRequireTranfer)}");
		}else{
			bootbox.confirm("${uiLabelMap.AreYouSureSent}",function(result){ 
    			if(result){	
    				var requirementData = [];
    				for(var i in requirementIdTotal){
    					requirementData.push(requirementIdTotal[i].requirementId);
    				}
    				rejectRequirementPurchaseOrderByPartyGroup(requirementData);
    			}
    		});
		}
	}
	
	$("#jqxNotificationRejectRequestSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationRejectRequestSuccess", opacity: 0.9, autoClose: true, template: "success" });
	function rejectRequirementPurchaseOrderByPartyGroup(requirementData){
		$.ajax({
			url: "rejectRequirementPurchaseOrderByPartyGroup",
			type: "POST",
			data: {requirementData: requirementData},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			$("#jqxgirdRequirement").jqxGrid('updatebounddata');
        	$("#notificationRejectRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.sentSuccessfully)}');
			$("#jqxNotificationRejectRequestSuccess").jqxNotification('open');
			requirementIdTotal = [];
		});
	}
	
	var requirementIdTotal = [];
	function rowselectfunction2(event){
		var args = event.args;
		if(typeof event.args.rowindex != 'number'){
            var rowBoundIndex = args.rowindex;
	    	if(rowBoundIndex.length == 0){
	    		requirementIdTotal = [];
	    	}else{
	    		for ( var x in rowBoundIndex) {
		    		var rowID = $('#jqxgirdRequirement').jqxGrid('getRowId', rowBoundIndex[x]);
    		        var data = $('#jqxgirdRequirement').jqxGrid('getrowdatabyid', rowID);
    		        requirementIdTotal.push(data);
				}
	    	}
        }else{
        	var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgirdRequirement').jqxGrid('getRowId', tmpArray);
	        var data = $('#jqxgirdRequirement').jqxGrid('getrowdatabyid', rowID);
	        requirementIdTotal.push(data);
        }
    }
	
	function rowunselectfunction2(event){
		var args = event.args;
	    if(typeof event.args.rowindex != 'number'){
	    	var rowBoundIndex = args.rowindex;
	    	for ( var x in rowBoundIndex) {
	    		var rowID = $('#jqxgirdRequirement').jqxGrid('getRowId', rowBoundIndex[x]);
	    		var data = $('#jqxgirdRequirement').jqxGrid('getrowdatabyid', rowID);
	    		var ii = requirementIdTotal.indexOf(data);
	    		requirementIdTotal.splice(ii, 1);
			}
	    }else{
	    	var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgirdRequirement').jqxGrid('getRowId', tmpArray);
	        var data = $('#jqxgirdRequirement').jqxGrid('getrowdatabyid', rowID);
	        var ii = requirementIdTotal.indexOf(data);
    		requirementIdTotal.splice(ii, 1);
	    }
    }
	
	function createOrderByRequirement(){
		if(requirementIdTotal.length == 0){
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.POSelectRequirementToCreateOrder)}");
		}else{
			/*bootbox.confirm("${uiLabelMap.POAreYouSureCreate}",function(result){ 
    			if(result){	*/
    				var requirementData = [];
    				for(var i in requirementIdTotal){
    					requirementData.push(requirementIdTotal[i].requirementId);
    				}
    				loadRequirementItemByRequirementByCreateOrder(requirementData);
    			/*}
    		});*/
		}
	}
	
	
	function loadRequirementItemByRequirementByCreateOrder(requirementData){
		$.ajax({
			url: "loadRequirementItemByRequirementByCreateOrder",
			type: "POST",
			data: {requirementData: requirementData},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			var listRequirementItemData = data["listRequirementItemData"];
			$('#jqxgirdRequirement').jqxGrid('clearSelection');
			bindingDataToJqxRequirementItem(listRequirementItemData);
		});
	}
	
	var dataBindingSoure = [];
	var dataSoureBindingByConvert = [];
	function bindingDataToJqxRequirementItem(listRequirementItemData){
		dataBindingSoure = [];
		var productIdSource = [];
		var quantitySource = [];
		var quantityUomIdSource = [];
		for(var i in listRequirementItemData){
			var productIdCheck = listRequirementItemData[i].productId;
			var quantityCheck = listRequirementItemData[i].quantity;
			var quantityUomIdCheck = listRequirementItemData[i].quantityUomId;
			var quantityDataSum = 0;
			
			for(var x in listRequirementItemData){
				var productIdDataCheck = listRequirementItemData[x].productId;
				var quantityDataCheck = listRequirementItemData[x].quantity;
				var quantityUomIdDataCheck = listRequirementItemData[x].quantityUomId;
				
				if(productIdCheck == productIdDataCheck && quantityUomIdDataCheck == quantityUomIdCheck){
					quantityDataSum = quantityDataSum + quantityDataCheck;
				}
			}
			
			if(productIdSource.length == 0){
				quantityUomIdSource.push(quantityUomIdCheck);
				productIdSource.push(productIdCheck);	
				quantitySource.push(quantityDataSum);
			}else{
				var checkLan2 = 0;
				for(var j in productIdSource){
					var productTest = productIdSource[j];
					var quantityUomIdTest = quantityUomIdSource[j];
					if(productIdCheck == productTest && quantityUomIdCheck == quantityUomIdTest){
						checkLan2 = 1;
					}
				}
				if(checkLan2 == 0){
					quantityUomIdSource.push(quantityUomIdCheck);
					productIdSource.push(productIdCheck);	
					quantitySource.push(quantityDataSum);
				}
			}
			
		}
		
		for(var x in productIdSource){
			fetchUomIdBasicByProduct(productIdSource[x], quantitySource[x], quantityUomIdSource[x]);
		}
		
		var productIdSourceByConvert = [];
		var quantitySourceByConvert = [];
		var quantityUomIdSourceByConvert = [];
		for(var n in dataBindingSoure){
			var productIdBySoure = dataBindingSoure[n].productId;
			var quantityBySoure = dataBindingSoure[n].quantity;
			var quantityUomIdBySoure = dataBindingSoure[n].quantityUomId;
			var sumQuantityConvert = 0;
			for(var m in dataBindingSoure){
				var productIdBySoureCheck = dataBindingSoure[m].productId;
				var quantityBySoureCheck = dataBindingSoure[m].quantity;
				var quantityUomIdBySoureCheck = dataBindingSoure[m].quantityUomId;
				if(productIdBySoure == productIdBySoureCheck){
					sumQuantityConvert = sumQuantityConvert + quantityBySoureCheck;
				}
			}
			
			if(productIdSourceByConvert.length == 0){
				quantityUomIdSourceByConvert.push(quantityUomIdBySoure);
				productIdSourceByConvert.push(productIdBySoure);	
				quantitySourceByConvert.push(sumQuantityConvert);
			}else{
				var checkConvertLan2 = 0;
				for(var j in productIdSourceByConvert){
					var productConvertTest = productIdSourceByConvert[j];
					var quantityUomIdConvertTest = quantityUomIdSourceByConvert[j];
					if(productIdBySoure == productConvertTest && quantityUomIdBySoure == quantityUomIdConvertTest){
						checkConvertLan2 = 1;
					}
				}
				
				if(checkConvertLan2 == 0){
					quantityUomIdSourceByConvert.push(quantityUomIdBySoure);
					productIdSourceByConvert.push(productIdBySoure);	
					quantitySourceByConvert.push(sumQuantityConvert);
				}
			}
			
		}
		
		/*dataSoureBindingByConvert = [];
		for(var a in productIdSourceByConvert){
			var requirementItemDataBySupplierToConvert = {
				productId: productIdSourceByConvert[a],
				quantity: quantitySourceByConvert[a],
				quantityUomId: quantityUomIdSourceByConvert[a],
			}
			dataSoureBindingByConvert.push(requirementItemDataBySupplierToConvert);
		}*/
		loadProductBySupplier(productIdSourceByConvert, quantitySourceByConvert, quantityUomIdSourceByConvert);
	}
	
	function loadProductBySupplier(productIdSourceByConvert, quantitySourceByConvert, quantityUomIdSourceByConvert){
		$.ajax({
			url: "loadProductBySupplier",
			type: "POST",
			data: {productId: productIdSourceByConvert},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			var mapSupplierProduct = data["mapSupplierProduct"];
			var supplierProductData = {};
			for(var key in mapSupplierProduct)
			{
				var supplierData = [];
				var supplierYerOrNo = "no";
				var productSupplier = mapSupplierProduct[key];
				if(productSupplier.length != 0){
					for(var x in productSupplier){
						supplierData.push(productSupplier[x].partyId);
					}
					supplierYerOrNo = "yes";
				}
				supplierProductData[key] = supplierYerOrNo;
			}
			
			dataSoureBindingByConvert = [];
			for(var x in quantitySourceByConvert){
				var requirementItemDataBySupplierToConvert = {
					productId: productIdSourceByConvert[x],
					quantity: quantitySourceByConvert[x],
					quantityUomId: quantityUomIdSourceByConvert[x],
					suppiler: supplierProductData[productIdSourceByConvert[x]],
				}
				dataSoureBindingByConvert.push(requirementItemDataBySupplierToConvert);
			}
			
			loadProductDataSumToJqx(dataSoureBindingByConvert);
			if(createOrderByRequirementCheck == 0){
				$('#alterpopupWindow').jqxWindow('open');
			}
		});
	}
	
	function fetchUomIdBasicByProduct(productIdSource, quantitySource, quantityUomIdSource){
		$.ajax({
			url: "fetchUomIdBasicByProduct",
			type: "POST",
			data: {productId: productIdSource},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			var quantityUomIdBasic = data["quantityUomId"];
			tranferProductQuantityToQuantityUomIdBasic(productIdSource, quantitySource, quantityUomIdSource, quantityUomIdBasic);
		});
	}
	
	function tranferProductQuantityToQuantityUomIdBasic(productIdSource, quantitySource, quantityUomIdSource, quantityUomIdBasic){
		$.ajax({
			url: "tranferProductQuantityToQuantityUomIdBasic",
			type: "POST",
			data: {productId: productIdSource, uomFromId: quantityUomIdSource, uomToId: quantityUomIdBasic},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			var quantityTranfer = data["quantityTranfer"];
			var quantityConvert = quantityTranfer * quantitySource;
			var requirementItemData = 
			{
				productId: productIdSource,
				quantity: quantityConvert,
				quantityUomId: quantityUomIdBasic, 
			}
			dataBindingSoure.push(requirementItemData);
		});
	}
	
	function loadProductDataSumToJqx(valueDataSoure){
		var sourceProduct =
 	    {
 	        datafields:[{name: 'productId', type: 'string'},
 	            		{name: 'quantity', type: 'number' },
 	            		{name: 'quantityUomId', type: 'string' },
 	            		{name: 'suppiler', type: 'string' },
         				],
 	        localdata: valueDataSoure,
 	        datatype: "array",
 	    };
 	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
 	    $("#jqxgridRequirementItem").jqxGrid({
 	        source: dataAdapterProduct,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        width: 1140,
	        height: 300,
	        autoheight: false,
	        pageable: true,
	        editable: true,
	        selectionmode: 'checkbox',
 	        columns: [	
 	                  	{text: '${uiLabelMap.LogProductId}', datafield: 'productId', width: '180', editable: false,},
 	          			{text: '${uiLabelMap.POQuantityBasicUntransformed}', datafield: 'quantity', width: '250', align: 'center', editable: false, filterable: true, cellsalign: 'right', columntype: 'numberinput',
 	          				cellsrenderer: function(row, column, value){
 	          					if (value){
 	          						return '<span style=\"text-align: right\">' + value.toLocaleString(value) +'</span>';
 	          					}
 	          				},
 	          			},
 	          			{ text: '${uiLabelMap.POInitialUnits}', dataField: 'quantityUomId', align: 'center', cellsalign: 'right', filterable: true, editable: false , columntype: 'dropdownlist',
	 	  					 cellsrenderer: function (row, column, value) {
	 	  						 if (value){
	 	                      		 return '<span style=\"text-align: right\">' + getDescriptionByUomId(value) +'</span>';
	 	                      	 }
	 	  					 },
	 	  				},
	 	  				{ text: '${uiLabelMap.POSuppiler}', dataField: 'suppiler', align: 'center', cellsalign: 'right', filterable: true, editable: false, width: '250',
	 	  					cellsrenderer: function (row, column, value) {
	 	                       
	 	                       if(value == "no"){
	 	                    	  return '<span><a href=\"javascript:void(0);\" onclick=\"addSupplierProduct('+row+')\">'+'${StringUtil.wrapString(uiLabelMap.PONoSupplierProduct)}'+'</a></span>';
	 	                       }if(value == "yes"){
	 	                    	  return '<span><a href=\"javascript:void(0);\" onclick=\"addSupplierProduct('+row+')\">'+'${StringUtil.wrapString(uiLabelMap.POSupplierProductAlreadyExists)}'+'</a></span>';
	 	                       }
	 	                    }, 
	 	                    cellClassName: function (row, columnfield, value) {
	 	                      var rowData = $("#jqxgridRequirementItem").jqxGrid('getrowdata', row);
	 	                      var rows = $('#jqxgridRequirementItem').jqxGrid('getboundrows');
	 	                      if(rowData['suppiler'] ==  "no"){
	 	                    	  return 'cell-green-color';
	 	                      }
	 	                    },
	 	  				},
 	          		 ]
 	    });
	}
	
	function addSupplierProduct(row){
		var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
		var productId = data.productId;
		productDataAddToSupplier = productId;
		supplierYesOrNo = data.suppiler;
		if(supplierYesOrNo == "yes"){
			bootbox.confirm("${uiLabelMap.POSupplierProductAlreadyExistsNotifi}", function(result){ 
				if(result){	
					$('#alterpopupWindowAddSupplierProduct').jqxWindow('setTitle', '${uiLabelMap.POAddSupplierForProduct}: ' + getDescriptionByProductId(productId));
					$('#alterpopupWindowAddSupplierProduct').jqxWindow('open');
				}
			});	
		}else{
			$('#alterpopupWindowAddSupplierProduct').jqxWindow('setTitle', '${uiLabelMap.POAddSupplierForProduct}: ' + getDescriptionByProductId(productId));
			$('#alterpopupWindowAddSupplierProduct').jqxWindow('open');
		}
	}
	
	function loadProductDataSumToJqx2(valueDataSoure){
		var sourceProduct =
 	    {
 	        datafields:[{name: 'productId', type: 'string'},
 	            		{name: 'quantity', type: 'number' },
 	            		{name: 'quantityUomId', type: 'string' },
 	            		{name: 'lastPrice', type: 'string' },
         				],
 	        localdata: valueDataSoure,
 	        datatype: "array",
 	    };
 	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
 	    $("#jqxgridRequirementItem").jqxGrid({
 	        source: dataAdapterProduct,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        width: 1140,
	        height: 300,
	        autoheight: false,
	        pageable: true,
	        editable: true,
	        selectionmode: 'checkbox',
 	        columns: [	
 	                  	{text: '${uiLabelMap.LogProductId}', datafield: 'productId', width: '180', editable: false,},
 	          			{text: '${uiLabelMap.POQuantityBasicUntransformed}', datafield: 'quantity', width: '250', align: 'center', editable: false, filterable: true, cellsalign: 'right', columntype: 'numberinput',
 	          				cellsrenderer: function(row, column, value){
 	          					if (value){
 	          						return '<span style=\"text-align: right\">' + value.toLocaleString(value) +'</span>';
 	          					}
 	          				},
 	          			},
 	          			{ text: '${uiLabelMap.POInitialUnits}', dataField: 'quantityUomId', align: 'center', cellsalign: 'right', filterable: true, editable: false , columntype: 'dropdownlist', width: '200',
	 	  					 cellsrenderer: function (row, column, value) {
	 	  						 if (value){
	 	                      		 return '<span style=\"text-align: right\">' + getDescriptionByUomId(value) +'</span>';
	 	                      	 }
	 	  					 },
	 	  				},
	 	  				{text: '${uiLabelMap.POLastPrice}', datafield: 'lastPrice', editable: false, cellsalign: 'right',  align: 'center'},
 	          		 ]
 	    });
	}
	
	$('#supplier').on('select', function (event){
	    var args = event.args;
	    if (args) {
		    var index = args.index;
		    var item = args.item;
		    var value = item.value;
		    $.ajax({
				url: "fetchProductBySupplier",
				type: "POST",
				data: {supplier: value},
				dataType: "json",
				success: function(data) {
				}
			}).done(function(data) {
				var listProductBySupplier = data["listProductBySupplier"];
				var listLastPriceBySupplier = data["listLastPrice"];
				var productIdSourceBySupllier = [];
				var quantitySourceBySupllier = [];
				var quantityUomIdSourceBySupllier = [];
				var lastPriceBySupllier = [];
				for(var b in dataSoureBindingByConvert){
					var productIdDataBySoure = dataSoureBindingByConvert[b].productId;
					var quantityDataBySoure = dataSoureBindingByConvert[b].quantity;
					var quantityUomIdDataBySoure = dataSoureBindingByConvert[b].quantityUomId;
					var checkProductBySupplier = 0;
					for(var c in listProductBySupplier){
						var productIdBySupplier = listProductBySupplier[c];
						if(productIdDataBySoure == productIdBySupplier){
							checkProductBySupplier = 1;
							var lastPrice = listLastPriceBySupplier[c];
							lastPriceBySupllier.push(lastPrice);
						}
					}
					
					if(checkProductBySupplier == 1){
						productIdSourceBySupllier.push(productIdDataBySoure);
						quantitySourceBySupllier.push(quantityDataBySoure);
						quantityUomIdSourceBySupllier.push(quantityUomIdDataBySoure);
					}
				}
				var dataSoureBinding= [];
				for(var x in productIdSourceBySupllier){
					var requirementItemDataBySupplier = {
						productId: productIdSourceBySupllier[x],
						quantity: quantitySourceBySupllier[x],
						quantityUomId: quantityUomIdSourceBySupllier[x],
						lastPrice: lastPriceBySupllier[x],
					}
					dataSoureBinding.push(requirementItemDataBySupplier);
				}
				loadProductDataSumToJqx2(dataSoureBinding);
			});
		    loadCurrencyUomId(value);
	    }                        
	});
	
	
	$('#alterpopupWindow').jqxValidator({
		rules: 
			[
			 	{ input: '#orderName', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
		        { input: '#supplier', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	        	   rule: function () {
	        		    var supplier = $('#supplier').val();
	            	    if(supplier == ""){
	            	    	return false; 
	            	    }else{
	            	    	return true; 
	            	    }
	            	    return true; 
	        	    }
                },
			 	{ input: '#currencyUomId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	        	   rule: function () {
	        		    var currencyUomId = $('#currencyUomId').val();
	            	    if(currencyUomId == ""){
	            	    	return false; 
	            	    }else{
	            	    	return true; 
	            	    }
	            	    return true; 
	        	    }
                }, 
                { input: '#originFacilityId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
 	        	   rule: function () {
 	        		    var originFacilityId = $('#originFacilityId').val();
 	            	    if(originFacilityId == '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'){
 	            	    	return false; 
 	            	    }else{
 	            	    	return true; 
 	            	    }
 	            	    return true; 
 	        	    }
                }, 
                { input: '#contactMechId', message: '${uiLabelMap.PODestinationSelectNotifi}', action: 'valueChanged, blur', 
	        	   rule: function () {
	        		    var contactMechId = $('#contactMechId').val();
	        		    if(contactMechId == false){
 	            	    	return false; 
 	            	    }else{
 	            	    	return true; 
 	            	    }
 	            	    return true;
	        	    }
	            }, 
		    ]
	});
	
	$('#addButtonSave').on('click', function () {
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		var orderItems = $('#jqxgridRequirementItem').jqxGrid('getRows');
		var supplier = $('#supplier').val();
		var orderName = $('#orderName').val();
		/*var orderId = $('#orderId').val();*/
		var currencyUomId = $('#currencyUomId').val();
		if(validate != false){
			if(orderItems.length == 0){
				$("#notificationCreatePurchaseOrder").text('${StringUtil.wrapString(uiLabelMap.PONoProductToCreatePurchaseOrder)}');
				$("#jqxNotificationCreatePurchaseOrder").jqxNotification('open');
			}else{
				bootbox.confirm("${uiLabelMap.POAreYouSureCreate}",function(result){ 
	    			if(result){	
	    				createPurchaseOrderByShoppingCartOfPO(orderItems, supplier, orderName, currencyUomId);
	    			}
				});
			}
		}
	}); 
	
	function createPurchaseOrderByShoppingCartOfPO(orderItems, supplier, orderName, currencyUomId){
		$.ajax({
			url: "createPurchaseOrderByRequirement",
			type: "POST",
			data: {partyIdFrom: supplier, orderName: orderName, currencyUomId: currencyUomId, contactMechId: contactMechIdByData ,orderItems: JSON.stringify(orderItems)},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			$('#alterpopupWindow').jqxWindow('close');
		});
	}
	
</script>