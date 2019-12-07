<style type="text/css">
	#horizontalScrollBarjqxContactMechGrid {
	  	visibility: inherit !important;
	}
	<#--#dropDownButtonPopupcustomerIdAdd, #dropDownButtonPopupcontactMechIdAdd, #dropDownButtonPopupproductIdAdd2 {
		width: inherit !important;
	}-->
	div[id^="dropDownButtonPopup"]{
		width: inherit !important;
	}
	.jqx-window-olbius .jqx-window-content table tr td button.jqx-button, 
	.jqx-window-olbius .jqx-window-content table tr td input.jqx-button[type="button"], 
	.jqx-window-olbius .jqx-window-content table tr td input.jqx-button[type="submit"]{
		padding: 7px 13px;
	}
	#descriptionValueAdd {
		margin: 0;
	}
</style>
<script type="text/javascript">
	<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "RETURNCOM_REQ_STATUS"}, null, false) />
	<#assign reasonList = delegator.findByAnd("ReturnReason", null, null, false) />
	<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
	
	var statusData = [
	<#if statusList?exists>
		<#list statusList as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	var reasonData = [
	<#if reasonList?exists>
		<#list reasonList as reasonItem>
		{	reasonId: '${reasonItem.returnReasonId}',
			description: '${StringUtil.wrapString(reasonItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	var uomData = [
	<#if uomList?exists>
		<#list uomList as uomItem>
		{	uomId: '${uomItem.uomId}',
			description: '${StringUtil.wrapString(uomItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
</script>

<#assign columnlist = "{text: '${uiLabelMap.requirementId}', dataField: 'requirementId', width: 150,
							cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                        	return \"<span><a href='/delys/control/viewReturnProductReq?requirementId=\" + data.requirementId + \"'>\" + data.requirementId + \"</a></span>\";
	                        }
						},
					 	{text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
								for(var i = 0; i < statusData.length; i++){
									if(statusData[i].statusId == value){
										return '<span title=' + value + '>' + statusData[i].description + '</span>'
									}
								}
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
					 	{text: '${uiLabelMap.DACreatedDate}', dataField: 'createdDate', width: 150, cellsformat: 'd', filtertype: 'range'},
					 	{text: '${uiLabelMap.Requestor}', dataField: 'createdByUserLogin', width: 150},
					 	{text: '${uiLabelMap.DAContactMechId}', dataField: 'contactMechId', width: 150},
					 	{text: '${uiLabelMap.DADescription}', dataField: 'description', width: 150},
					 	{text: '${uiLabelMap.DARequirementStartDateOrigin}', dataField: 'requirementStartDate', width: 150, cellsformat: 'd', filtertype: 'range'},
					 	{text: '${uiLabelMap.DARequiredByDateOrigin}', dataField: 'requiredByDate', width: 150, cellsformat: 'd', filtertype: 'range'},
					 	{text: '${uiLabelMap.DAReason}', dataField: 'reason', width: 150, filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
								for(var i = 0; i < reasonData.length; i++){
									if(reasonData[i].reasonId == value){
										return '<span title=' + value + '>' + reasonData[i].description + '</span>'
									}
								}
							}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(reasonData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'reasonId', valueMember: 'reasonId',
									renderer: function(index, label, value){
										for(var i = 0; i < reasonData.length; i++){
											if(reasonData[i].reasonId == value){
												return '<span>' + reasonData[i].description + '</span>';
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
				   			}
			   			},
			 	"/>
<#--
{text: '${uiLabelMap.DAFacilityId}', dataField: 'facilityId', width: 150},
-->
<#assign dataField = "[{ name: 'requirementId', type: 'string'},
						{name: 'facilityId', type: 'string'},
		             	{name: 'productStoreId', type: 'string'},
		             	{name: 'contactMechId', type: 'string'},
		             	{name: 'deliverableId', type: 'string'},
						{name: 'fixedAssetId', type: 'string'},
		             	{name: 'productId', type: 'string'},
		             	{name: 'statusId', type: 'string'},
		             	{name: 'description', type: 'string'},
		             	{name: 'createdDate', type: 'date', other: 'Timestamp'},
		             	{name: 'requirementStartDate', type: 'date', other: 'Timestamp'},
		             	{name: 'requiredByDate', type: 'date', other: 'Timestamp'},
						{name: 'estimatedBudget', type: 'string'},
						{name: 'currencyUomId', type: 'string'},
						{name: 'quantity', type: 'string'},
						{name: 'useCase', type: 'string'},
						{name: 'reason', type: 'string'},
						{name: 'createdByUserLogin', type: 'string'},
	 		 	]"/>
<#--
<@jqGrid contextMenuId="Menu" filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" 
		showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=JQGetListRequirement&requirementTypeId=RETURN_PRODCOM_REQ" 
		 createUrl="jqxGeneralServicer?sname=createTransferRequirement&jqaction=C" updateUrl="jqxGeneralServicer?sname=updateTransferRequirement&jqaction=U"
		 addColumns="productStoreIdFrom;currencyUomId;reason;description;productStoreIdTo;facilityIdFrom;facilityIdTo;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);originContactMechId;destContactMechId;listProducts(java.util.List);requirementTypeId[TRANSFER_REQ];estimatedBudget" 
		 />
-->
<#assign tmpCreateUrl = ""/>
<#if security.hasPermission("REREQCOM_ROLE_UPDATE", session)>
	<#assign tmpCreateUrl = "fa fa-bolt@${uiLabelMap.DAApprove}@updateReturnProductReqCom"/>
</#if>
<#assign tmpProposeUrl = ""/>
<#if isSalesAdmin && security.hasPermission("REREQCOM_ROLE_UPDATE", session)>
	<#assign tmpProposeUrl = "fa fa-truck@${uiLabelMap.DATransferRequirement}@proposeReturnProductReqCom"/>
</#if>
<#assign hasPermissionCreate = false/>
<#--<#assign company = Static['org.ofbiz.entity.util.EntityUtilProperties'].getPropertyValue("general.properties", "ORGANIZATION_PARTY", "company", delegator)/>
<#assign userLoginRel = delegator.findByAnd("PartyRelationship", {"partyIdFrom" : company, "partyIdTo" : userLogin.partyId, "roleTypeIdFrom" : "INTERNAL_ORGANIZATIO", "roleTypeIdTo" : "DELYS_DISTRIBUTOR"})/>
userLoginRel?exists && (userLoginRel?length &gt; 0)
-->
<#if security.hasPermission("RETURNREQ_ROLE_CREATE", session)>
	<#if Static['com.olbius.util.SalesPartyUtil'].isSupervisorEmployee(userLogin, delegator)>
		<#assign hasPermissionCreate = true/>
	</#if>
</#if>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListRequirementSentToCompany&requirementTypeId=RETURN_PRODCOM_REQ" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" deleterow="false"
		 createUrl="jqxGeneralServicer?sname=createReturnRequirementToCompany&jqaction=C" addrow="${hasPermissionCreate?string}" 
		 addColumns="contactMechId;companyId;description;distributorId;listProducts(java.util.List);reason;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);requirementTypeId[RETURN_PRODCOM_REQ];" 
		 mouseRightMenu="true" contextMenuId="contextMenu" customcontrol2=tmpProposeUrl 
		 />
<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	</ul>
</div>

<#if hasPermissionCreate>
	<#assign companyId = Static['com.olbius.util.SalesPartyUtil'].getCompanyInProperties(delegator)!>
	<div id="alterpopupWindow" style="display:none">
		<div>${uiLabelMap.DACreateNew}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<input type="hidden" name="requirementTypeId" value="RETURN_PRODCOM_REQ"></input>
				<div class="row-fluid">
					<div class="span6 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.DACompanyId}</label>
							</div>
							<div class="span7">
								<input type="text" id="companyIdAdd" value="${companyId?if_exists}" readonly="true"/>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.DADistributor}</label>
							</div>
							<div class="span7">
								<div id="distributorIdAdd">
						       		<div id="jqxDistributorGrid"></div>
						       	</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.DAAddress}</label>
							</div>
							<div class="span7">
								<div id="contactMechIdAdd">
						       		<div id="jqxContactMechGrid"></div>
						       	</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.DADescription}</label>
							</div>
							<div class="span7">
								<textarea id="descriptionValueAdd" rows="2" class="span12"></textarea>
					   		</div>
						</div>
					</div>
					<div class="span6 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.DARequirementStartDateOrigin}</label>
							</div>
							<div class="span7">
								<div id="requirementStartDateAdd"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.DARequiredByDateOrigin}</label>
							</div>
							<div class="span7">
								<div id="requiredByDateAdd"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.DAReason}</label>
							</div>
							<div class="span7">
								<div id="reasonValueAdd"></div>
					   		</div>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span12">
						<div style="overflow:hidden;overflow-y:visible; max-height:300px !important">
			    			<div id="jqxgridProduct"></div>
			    		</div>
					</div>
				</div>
			</div>
		   	<div class="form-action">
		   		<div class="pull-right form-window-content-custom">
		   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			   		<button id="jqxButtonAddNewRow" class='btn btn-primary form-action-button'><i class='fa-plus'></i> ${uiLabelMap.DAAddNewRow}</button>
					<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		   		</div>
			</div>
		</div>
	</div>
	
	<div id="alterpopupWindowAddNewRow" style="display:none">
		<div>${uiLabelMap.DAAddNewRow}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<input type="hidden" id="productNameAdd2" value=""/>
				<div class="row-fluid">
					<div class="span12 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.DAProduct}</label>
							</div>  
							<div class="span7">
								<div id="productIdAdd2">
						       	 	<div id="jqxgridProductGrid2"></div>
						       	</div>
					   		</div>		
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.DAUom}</label>
							</div>  
							<div class="span7">
								<div id="quantityUomIdAdd2"></div>
					   		</div>		
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.DAExpireDate}</label>
							</div>  
							<div class="span7">
								<div id="expireDateAdd2"></div>
					   		</div>		
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.DAQuantity}</label>
							</div>  
							<div class="span7">
								<input type="text" id="quantityAdd2" value=""/>
					   		</div>		
						</div>
					</div>
				</div>
			</div>
		   	<div class="form-action">
		   		<div class="pull-right form-window-content-custom">
					<button id="alterSave2" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
					<button id="alterCancel2" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		   		</div>
			</div>
		</div>
	</div>
</#if>
<#--<div id="dialog-message" title="${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}!" style="display:none">-->
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var requirementId = data.requirementId;
				var url = 'viewReturnProductReq?requirementId=' + requirementId;
				var win = window.open(url, '_blank');
				win.focus();
			}
        }
	});
</script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.culture.vi-VN.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/delys/images/js/sales/salesCommon.js"></script>
<script type="text/javascript">
<#if hasPermissionCreate>
	initWindowFirst();
	initWindowSecond();
	initEvent();
	initValidateForm();
	
	<#--
	$("#alterSave").jqxButton({width: 100, theme: theme});
	$("#alterCancel").jqxButton({width: 100, theme: theme});
	$("#jqxButtonAddNewRow").jqxButton({ width: '150', theme: theme});
	$("#alterCancel2").jqxButton({width: 100, theme: theme});
	$("#alterSave2").jqxButton({width: 100, theme: theme});
	-->
	
	function initEvent() {
		$("#jqxButtonAddNewRow").on('click', function () {
			$('#alterpopupWindowAddNewRow').jqxWindow('open');
	    });
	    
	    // update the edited row when the user clicks the 'Save' button.
		$("#alterSave").click(function () {
			if(!$('#alterpopupWindow').jqxValidator('validate')) return false;
			var row;
			var selectedIndexs = $('#jqxgridProduct').jqxGrid('getselectedrowindexes');
			var listProducts = new Array();
			for(var i = 0; i < selectedIndexs.length; i++){
				var data = $('#jqxgridProduct').jqxGrid('getrowdata', selectedIndexs[i]);
					var map = {};
					map['productId'] = data.productId;
					map['quantity'] = data.quantity;
					map['quantityUomId'] = data.quantityUomId;
					map['expireDate'] = (new Date(data.expireDate)).getTime();
					listProducts[i] = map;
			}
			if (listProducts == null || listProducts.length <= 0) {
				<#--
				$("#dialog-message").text("${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}!" );
				$("#dialog-message").dialog({
			      	resizable: false,
			      	height:180,
			      	modal: true,
			      	buttons: {
			        	"${StringUtil.wrapString(uiLabelMap.wgcancel)}": function() {
			          		$(this).dialog("close");
			        	}
			      	}
				});
				-->
				bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
					"label" : "OK",
					"class" : "btn-small btn-primary",
					}]
				);
				return false;
			}
			listProducts = JSON.stringify(listProducts);
			var today = new Date();
			row = {	companyId: $('#companyIdAdd').val(),
					createdByUserLogin: '${userLogin.userLoginId}',
					statusId: 'REREQCOM_CREATED',
					createdDate: today,
					distributorId: $('#distributorIdAdd').val(),
					contactMechId:$('#contactMechIdAdd').val(),
					description:$('#descriptionValueAdd').val(),
					requirementStartDate:$('#requirementStartDateAdd').val(),
					requiredByDate:$('#requiredByDateAdd').val(),
					reason:$('#reasonValueAdd').val(),
					listProducts:listProducts
		  	};
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
			$("#jqxgrid").jqxGrid('updatebounddata');
	    	$("#alterpopupWindow").jqxWindow('close');
		});
		
		$("#jqxgridProductGrid2").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#jqxgridProductGrid2").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['productId'] +'</div>';
	        $('#productIdAdd2').jqxDropDownButton('setContent', dropDownContent);
	        $('#productNameAdd2').val(row['internalName']);
	        var productId = $("#productIdAdd2").val();
	        var tmpQuantityUom = $("#quantityUomIdAdd2").jqxDropDownList('source');
		 	tmpQuantityUom._source.url = "getListQuantityUomByProduct";
		 	tmpQuantityUom._source.data = {'productId' : productId};
		 	$("#quantityUomIdAdd2").jqxDropDownList('source', tmpQuantityUom);
	    });
	    
	    // update the edited row when the user clicks the 'Save' button.
	    $("#alterSave2").click(function () {
	    	if($('#alterpopupWindowAddNewRow').jqxValidator('validate')){
	    		var quantityUomIdObj = $('#quantityUomIdAdd2').jqxDropDownList('getSelectedItem');
	    		var quantityUomIdStr = "";
	    		if (quantityUomIdObj != null) quantityUomIdStr = quantityUomIdObj.value;
		    	var row = {
		        	productId: $('#productIdAdd2').val(),
		        	internalName: $('#productNameAdd2').val(),
		        	quantityUomId: quantityUomIdStr,
		        	quantity: $('#quantityAdd2').val(),
		        	expireDate: new Date($('#expireDateAdd2').jqxDateTimeInput('getDate'))
		        };
			   	$("#jqxgridProduct").jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        $("#jqxgridProduct").jqxGrid('clearSelection');                        
		        $("#jqxgridProduct").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindowAddNewRow").jqxWindow('close');
		        
		        // reset value on window
				$('#productIdAdd2').val("");
				$('#productNameAdd2').val("");
				$('#quantityUomId').val("");
				$('#quantity').val("");
				$('#expireDate').val("");
				$("#jqxgridProductGrid2").jqxGrid('clearSelection');
	        }else{
	        	return;
	        }
	    });
	    
	    $("#jqxDistributorGrid").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#jqxDistributorGrid").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
	        $("#distributorIdAdd").jqxDropDownButton('setContent', dropDownContent);
	        
	        var distributorId = $("#distributorIdAdd").val();
	        var tmpAddress = $("#jqxContactMechGrid").jqxGrid('source');
		 	tmpAddress._source.url = "jqxGeneralServicer?sname=JQGetPartyPostalAddresses&partyId="+distributorId;
		 	$("#jqxContactMechGrid").jqxGrid('source', tmpAddress);
	    });
	    
	    $("#jqxContactMechGrid").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#jqxContactMechGrid").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['contactMechId'] + '</div>';
	        $("#contactMechIdAdd").jqxDropDownButton('setContent', dropDownContent);
	    });
	}
	
	function initWindowFirst() {
		// Create Window
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 1366, maxHeight: 768, minWidth: 960, minHeight: 540, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		$("#companyIdAdd").jqxInput({width: 'calc(100% - 5px)', disabled: true});
		$("#requirementStartDateAdd").jqxDateTimeInput({width: '99%', height: 25, formatString: 'yyyy-MM-yy HH:mm:ss', culture: 'vi-VN'});
		$("#requiredByDateAdd").jqxDateTimeInput({width: '99%', height: 25, formatString: 'yyyy-MM-yy HH:mm:ss', allowNullDate: true, value: null, culture: 'vi-VN'});
		
		<#--
		var sourceReason = [
				"${StringUtil.wrapString(uiLabelMap.DACollectionProductIsAboutToExpire)}", 
				"${StringUtil.wrapString(uiLabelMap.DAConsumptionDifficultProduct)}", 
				"${StringUtil.wrapString(uiLabelMap.DAOther)}"
		];
		-->
		<#assign returnReasons = delegator.findByAnd("ReturnReason", null, ["sequenceId"], false)!/>
		var dataReason = [
		<#if returnReasons?exists>
			<#list returnReasons as returnReason>
			{	returnReasonId : '${returnReason.returnReasonId}',
				description : '${StringUtil.wrapString(returnReason.get("description", locale))}'
			},
			</#list>
		</#if>
		];
		var configReason = {
			key: 'returnReasonId',
			value: 'description',
			selectedIndex: 0,
			width: '99%',
			placeHolder: '${uiLabelMap.DASelectAReason}...'
		};
		jSalesCommon.initDropDownList($('#reasonValueAdd'), dataReason, configReason, ["RTN_NORMAL_RETURN"]);
		
		// List distributor by customer
	    var configDistributor = {
	    	width: 600,
	    	widthButton: '100%',
	    	heightButton: 25,
	    	useUrl: true,
	    	url: 'jqxGeneralServicer?sname=JQGetListDistributorBySupervisor',
	    	root: 'results',
	    	showdefaultloadelement: false,
	    	autoshowloadelement: false,
	    	datafields: [
		      	{name: 'partyId', type: 'string'},
		      	{name: 'fullName', type: 'string'},
		      	{name: 'lastName', type: 'string'},
		      	{name: 'firstName', type: 'string'},
		      	{name: 'middleName', type: 'string'},
			],
	    	columns: [
	    		{text: '${StringUtil.wrapString(uiLabelMap.DAPartyId)}', datafield: 'partyId', width:'18%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DAFullName)}', datafield: 'fullName'},
				{text: '${StringUtil.wrapString(uiLabelMap.DAFirstName)}', datafield: 'lastName', width:'15%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DALastName)}', datafield: 'firstName', width:'15%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DAMiddleName)}', datafield: 'middleName', width:'15%'}
			]
	    };
	    jSalesCommon.initDropDownButton($("#distributorIdAdd"), $("#jqxDistributorGrid"), null, configDistributor, []);
	    
	    // List address by customer
		var configAddressCustomer = {
			width: 600,
			widthButton: '100%',
			heightButton: 25,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetPartyPostalAddresses',
			root: 'results',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			datafields: [
				{name: 'contactMechId', type: 'string'},
				{name: 'address1', type: 'string'},
				{name: 'address2', type: 'string'},
				{name: 'directions', type: 'string'},
				{name: 'city', type: 'string'},
				{name: 'postalCode', type: 'string'},
			 	{name: 'stateProvinceGeoId', type: 'string'},
				{name: 'countyGeoId', type: 'string'},
				{name: 'countryGeoId', type: 'string'},
				{name: 'contactMechPurposeTypeId', type: 'string'},
		    ],
			columns: [
				{text: '${uiLabelMap.DAContactMechId}', datafield: 'contactMechId', width:'150px'},
				{text: '${uiLabelMap.DAAddress1}', datafield: 'address1', width:'150px'},
				{text: '${uiLabelMap.DADirections}', datafield: 'directions', width:'100px'},
				{text: '${uiLabelMap.DACity}', datafield: 'city', width:'100px'},
				{text: '${uiLabelMap.DAPostalCode}', datafield: 'postalCode', width:'100px'},
				{text: '${uiLabelMap.DAStateProvinceGeoId}', datafield: 'stateProvinceGeoId', width:'100px'},
				{text: '${uiLabelMap.DACountyGeoId}', datafield: 'countyGeoId', width:'100px'},
				{text: '${uiLabelMap.DACountryGeoId}', datafield: 'countryGeoId', width:'100px'},
				{text: '${uiLabelMap.DAContactMechPurposeTypeId}', datafield: 'contactMechPurposeTypeId', width:'200px'}
			]
		}
		jSalesCommon.initDropDownButton($("#contactMechIdAdd"), $("#jqxContactMechGrid"), null, configAddressCustomer, []);
	}
	
	function initWindowSecond() {
		$("#alterpopupWindowAddNewRow").jqxWindow({
			maxWidth: 960, maxHeight: 600, minWidth: 480, minHeight: 300, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7, theme:theme           
		});
		$("#expireDateAdd2").jqxDateTimeInput({width: '98%', height: 25, formatString: 'dd/MM/yyyy', allowNullDate: true, value: null});
		$("#quantityAdd2").jqxInput({width: 'calc(98% - 5px)'});
		
		// Product 2 JQX Dropdown
		var configProductWin2 = {
			width: 610,
			widthButton: '98%',
			heightButton: 25,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProductSalesOnlyProduct',
			root: 'results',
			datafields: [
				{name: 'productId', type: 'string'},
	        	{name: 'internalName', type: 'string'},
	       		{name: 'productName', type: 'string'},
	        	{name: 'productTypeId', type: 'string'}
	    	],
	    	columns: [
	    		{text: '${uiLabelMap.DAProductId}', datafield: 'productId', width:'180px'},
	        	{text: '${uiLabelMap.DAInternalName}', datafield: 'internalName', width:'250px'},
	        	{text: '${uiLabelMap.DAProductTypeId}', datafield: 'productTypeId', width:'180px'}
	        ]
		}
	    jSalesCommon.initDropDownButton($("#productIdAdd2"), $("#jqxgridProductGrid2"), null, configProductWin2, []);
	    
	    var configQuantityUomWin2 = {
	    	width: '98%',
	    	height: 25,
	    	key: 'uomId',
	    	value: 'description',
	    	dropDownWidth: 200,
	    	displayDetail: true,
	    	useUrl: true,
	    	url: 'getListQuantityUomByProduct',
	    	root: "listQuantityUom",
	    	placeHolder: '${StringUtil.wrapString(uiLabelMap.DAChooseAQuantityUom)}',
	    	datafields: [
	        	{ name: 'uomId' },
	            { name: 'description' }
	        ],
	    };
	    jSalesCommon.initDropDownList($("#quantityUomIdAdd2"), null, configQuantityUomWin2, []);
	}
	
	function initValidateForm() {
		$('#alterpopupWindow').jqxValidator({
			rules: [
				{input: '#companyIdAdd', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#companyIdAdd').val() == null || $('#companyIdAdd').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#distributorIdAdd', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#distributorIdAdd').val() == null || $('#distributorIdAdd').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#contactMechIdAdd', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#contactMechIdAdd').val() == null || $('#contactMechIdAdd').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#reasonValueAdd', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule:
					function (input, commit) {
						if($('#reasonValueAdd').val() == null || $('#reasonValueAdd').val() == ''){
							return false;
						}
						return true;
					}
				}
			]
		});
		$('#alterpopupWindowAddNewRow').jqxValidator({
			rules: [
				{input: '#productIdAdd2', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#productIdAdd2').val() == null || $('#productIdAdd2').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#quantityUomIdAdd2', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#quantityUomIdAdd2').val() == null || $('#quantityUomIdAdd2').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#expireDateAdd2', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).jqxDateTimeInput('getDate');
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#quantityAdd2', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule:
					function (input, commit) {
						if($('#quantityAdd2').val() == null || $('#quantityAdd2').val() == ''){
							return false;
						}
						return true;
					}
				}
			]
		});
	}
</#if>
</script>

<#if hasPermissionCreate>
	<#assign dataFieldProduct="[{name: 'productId', type: 'string'},
				             	{name: 'internalName', type: 'string'},
				             	{name: 'quantity', type: 'string'},
				             	{name: 'quantityUomId', type: 'string'},
				             	{name: 'productPackingUomId', type: 'string'},
				             	{name: 'packingUomId', type: 'string'}
		 		 		]"/>
	<#assign columnlistProduct="{text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '150px', align: 'center', editable: false},
					 			{text: '${uiLabelMap.ProductName}', dataField: 'internalName', align: 'center', editable: false},
							 	{ text: '${uiLabelMap.DAUom}', dataField: 'quantityUomId', width: '120px', columntype: 'dropdownlist', 
								 	cellsrenderer: function(row, column, value){
			    						for (var i = 0 ; i < uomData.length; i++){
			    							if (value == uomData[i].uomId){
			    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
			    							}
			    						}
			    						return '<span title=' + value +'>' + value + '</span>';
									},
								 	initeditor: function (row, cellvalue, editor) {
								 		var packingUomData = new Array();
										var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
										
										var itemSelected = data['quantityUomId'];
										var packingUomIdArray = data['packingUomId'];
										for (var i = 0; i < packingUomIdArray.length; i++) {
											var packingUomIdItem = packingUomIdArray[i];
											var row = {};
											if (packingUomIdItem.description == undefined || packingUomIdItem.description == '') {
												row['description'] = '' + packingUomIdItem.uomId;
											} else {
												row['description'] = '' + packingUomIdItem.description;
											}
											row['uomId'] = '' + packingUomIdItem.uomId;
											packingUomData[i] = row;
										}
								 		var sourceDataPacking =
							            {
							                localdata: packingUomData,
							                datatype: \"array\"
							            };
							            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
							            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'
							            	//renderer: function (index, label, value) {
									        //	return '[' + value + '] ' + label;
									        //}
							            });
							            
			                          	//editor.jqxDropDownList({source: dataAdapterPacking, displayMember:'description', valueMember: 'uomId'});
							            editor.jqxDropDownList('selectItem', itemSelected);
			                      	}
			                 	},
							 	{text: '${uiLabelMap.DAExpireDate}', dataField: 'expireDate', width: '150px', editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filterable: false, sortable:false},
							 	{text: '${uiLabelMap.QuantityRequest}', dataField: 'quantity', width: '200px', align: 'center', cellsalign: 'right', filterable: false, sortable:false, columntype: 'numberinput', editable: true,
			                     	validation: function (cell, value) {
			                         	if (value < 0) {
			                             	return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
			                         	}
			                         	return true;
			                     	},
			                     	createeditor: function (row, cellvalue, editor) {
			                         	editor.jqxNumberInput({ decimalDigits: 0, digits: 10 });
			                     	}
							 	}
					 	"/>

<@jqGrid id="jqxgridProduct" idExisted="true" filtersimplemode="true" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" 
		dataField=dataFieldProduct columnlist=columnlistProduct clearfilteringbutton="true" showtoolbar="false" addrow="false" editable="true" 
		url="jqxGeneralServicer?sname=JQGetListProductSales" filterable="true" width="930" bindresize="false" alternativeAddPopup="alterpopupWindow" addType="popup" 
		deleterow="false" offmode="true" editmode="click" selectionmode="checkbox"/>
</#if>		

<#--
validation: function (cell, value) {
  	if (value == '') return true;
  	var year = value.getFullYear();
  	if (year >= 2015) {
      	return {result: false, message: 'Ship Date should be before 1/1/2015'};
  	}
  	return true;
}
-->
<#--
<@jqGrid selectionmode="checkbox" idExisted="true" filtersimplemode="true" width="930" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" 
		id="jqxgridProduct" dataField=dataFieldProduct columnlist=columnlistProduct clearfilteringbutton="true" showtoolbar="false" addrow="false" filterable="true" editable="true" 
	 	url="jqxGeneralServicer?sname=JQGetListProductSales" editmode="click" bindresize="false" jqGridMinimumLibEnable="false" offmode="true"/>
JQGetListRequirement&requirementTypeId=RETURN_PRODCOM_REQ
-->