<script type="text/javascript" src="/delys/images/js/import/progressing2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<#assign dataField="[{ name: 'requirementId', type: 'string'},
					   { name: 'agreementId', type: 'string'},
					   { name: 'orderId', type: 'string'},
					   { name: 'agreementDate', type: 'date', other: 'Timestamp'},
					   { name: 'partyIdFrom', type: 'string'},
					   { name: 'partyIdTo', type: 'string'},
					   { name: 'description', type: 'string'},
					   { name: 'productStoreId', type: 'string'},
					   { name: 'facilityId', type: 'string'},
					   { name: 'requirementDate',type: 'date', other: 'Timestamp'}
				   ]"/>
					   
   <#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
 						{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementId', width: 120, editable: false,
						   cellsrenderer: function(row, colum, value){
						        var link = 'detailPurchaseAgreement?agreementId=' + value;
						        return '<span><a href=\"' + link + '\">' + value + '</a></span>';
						   }
 						},
 						{ text: '${uiLabelMap.OrderId}', datafield: 'orderId', width: 120, editable: false},
 						{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
 						{ text: '${uiLabelMap.SlideA}', datafield: 'partyIdFrom', width: 150, filtertype: 'checkedlist', editable: false,
							cellsrenderer: function(row, colum, value){
								value?value=mapPartyBuyer[value]:value;
	 	    			        return '<span>' + value + '</span>';
							},createfilterwidget: function (column, htmlElement, editor) {
		    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listPartyBuyer), displayMember: 'partyId', valueMember: 'partyId' ,
		                            renderer: function (index, label, value) {
		                            	if (index == 0) {
		                            		return value;
										}
									    return mapPartyBuyer[value];
					                }
		    		        	});
		    		        	editor.jqxDropDownList('checkAll');
							}
						},
 						{ text: '${uiLabelMap.SlideB}', datafield: 'partyIdTo', width: 150, filtertype: 'checkedlist',editable: false,
							cellsrenderer: function(row, colum, value){
								value?value=mapPartySupplier[value]:value;
	 	    			        return '<span>' + value + '</span>';
	 	    		        },createfilterwidget: function (column, htmlElement, editor) {
		    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listPartySupplier), displayMember: 'partyId', valueMember: 'partyId' ,
		                            renderer: function (index, label, value) {
		                            	if (index == 0) {
		                            		return value;
										}
									    return mapPartySupplier[value];
					                }
		    		        	});
		    		        	editor.jqxDropDownList('checkAll');
							}
 	    		        },
 	    		        { text: '${uiLabelMap.description}', datafield: 'description', minWidth: 180, editable: false },
 	    		        { text: '${uiLabelMap.productStore}', datafield: 'productStoreId', width: 120, editable: false, filtertype: 'checkedlist',
 	    		        	cellsrenderer: function(row, colum, value){
 	    		        		value?value=mapProductStore[value]:value='';
								return '<span>' + value + '</span>';
 	    		        	},createfilterwidget: function (column, htmlElement, editor) {
		    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listProductStore), displayMember: 'productStoreId', valueMember: 'productStoreId' ,
		                            renderer: function (index, label, value) {
		                            	if (index == 0) {
		                            		return value;
										}
									    return mapProductStore[value];
					                }
		    		        	});
		    		        	editor.jqxDropDownList('checkAll');
							}
    		        	}
						"/>
			                
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		customcontrol1="icon-tasks@${uiLabelMap.ListReceiveRequirement}@getListReceiptRequirements"
		url="jqxGeneralServicer?sname=JQGetListEditReceiptRequirement"
		otherParams="productStoreId:S-getProductStoreID(orderId)<productStoreId>"
		contextMenuId="contextMenu" mouseRightMenu="true"
	/>
<div id="showPopup"></div>
			             
<div id='contextMenu' style="display:none;">
	<ul>
		<li id='create'><i class="icon-plus-sign open-sans"></i>&nbsp;&nbsp;${uiLabelMap.CreateReceiptRequirement}</li>
	</ul>
</div>
	    				
<div id='selectFacility' class='hide'>
	 <div>${uiLabelMap.selectDateReceipt}</div>
	 <div class='row-fluid'> 
			<div class='span12 no-left-margin'> 
				<div class='span5'><label class="text-right asterisk">${uiLabelMap.ReceiveDate}</div> 
				<div class='span7'><div id='requirementDate'></div></div> 
			</div>
   			<div class='span12 no-left-margin margin-top8'> 
   				<div class='pull-right margin-right15'>
	   				<input id='alterCancel5' type='button' class="btn btn-danger form-action-button pull-right" value='${uiLabelMap.Cancel}' />
	   				<input style='margin-right: 5px;' type='button' id='alterSave5' class="btn btn-primary form-action-button pull-right" value='${uiLabelMap.Save}'/>
   				</div>
			</div> 
	  </div>
</div>
		
<#assign listPartyBuyer = delegator.findList("PartyGroupAndPartyRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "BUYER"), null, null, null, false) />
<#assign listPartySupplier = delegator.findList("PartyGroupAndPartyRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "SUPPLIER"), null, null, null, false) />
<#assign listProductStore = delegator.findList("ProductStore", null, null, null, null, false) />    
<#assign listFacility = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", Static["com.olbius.util.MultiOrganizationUtil"].getCurrentOrganization(delegator)), null, null, null, false) />
	
<script>
	var listFacility = [
					<#if listFacility?exists>
						<#list listFacility as item>
						{
							facilityId: "${item.facilityId?if_exists}",
							facilityName: "${item.facilityName?if_exists}"
						},
						</#list>
					</#if>
	                    ];
	
	var listProductStore = [
					<#if listProductStore?exists>
						<#list listProductStore as item>
							{
								productStoreId: "${item.productStoreId?if_exists}",
								storeName: "${item.storeName?if_exists}"
							},
						</#list>
					</#if>
	                    ];
	
	var mapProductStore = {
		<#if listProductStore?exists>
			<#list listProductStore as item>
					"${item.productStoreId?if_exists}": "${item.storeName?if_exists}",
			</#list>
		</#if>
	};
	var listPartyBuyer = [
					<#if listPartyBuyer?exists>
						<#list listPartyBuyer as item>
							{
								partyId: "${item.partyId?if_exists}",
								groupName: "${StringUtil.wrapString(item.get('groupName', locale)?if_exists)}"
							},
						</#list>
					</#if>
	                  ];
	var mapPartyBuyer = {
		<#if listPartyBuyer?exists>
			<#list listPartyBuyer as item>
					"${item.partyId?if_exists}": "${StringUtil.wrapString(item.get('groupName', locale)?if_exists)}",
			</#list>
		</#if>
	};
	
	var listPartySupplier = [
							<#if listPartySupplier?exists>
								<#list listPartySupplier as item>
									{
										partyId: "${item.partyId?if_exists}",
										groupName: "${StringUtil.wrapString(item.get('groupName', locale)?if_exists)}"
									},
								</#list>
							</#if>
			                  ];
	var mapPartySupplier = {
		<#if listPartySupplier?exists>
			<#list listPartySupplier as item>
					"${item.partyId?if_exists}": "${StringUtil.wrapString(item.get('groupName', locale)?if_exists)}",
			</#list>
		</#if>
	};
	var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 200, height: 32, autoOpenPopup: false, mode: 'popup'});
	$("#jqxgrid").on('contextmenu', function () {
	    return false;
	});
	$("#create").on("click", function() {
		var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		showCost(rowIndexSelected);
	});
	function showCost(i){
	   	var data = $('#jqxgrid').jqxGrid('getrowdata', i);
		var orderId = data.orderId;
		var reqDate = new Date(data.requirementDate).getTime();
		var reqByDate = new Date(data.requiredByDate).getTime();
		$.ajax({
			url: 'showOrderCost?orderId='+orderId+'&departmentId=IMPORT_ADMIN&requirementId='+'&agreementId='+data.agreementId+'&productStoreId='+data.productStoreId+'&facilityId='+data.facilityId+'&contactMechId='+'&requiredByDate='+reqByDate+'&requirementDate='+reqDate+'&statusId='+data.statusId,
	    	type: "POST",
	    	data: {},
	    	async: false,
	    	success: function(data2) {
	    		$("#showPopup").html(data2);
	    		$('#window').jqxWindow('open');
	    	},
	    	error: function(data2){
	    	}
			});
	}
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
	function createRequirement(dataqqq){
		$("#selectFacility").jqxWindow("open");
	}
	
   	$("#selectFacility").jqxWindow({ theme:'olbius',
        width: 400, maxWidth: 1000, height: 130, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel5"), modalOpacity: 0.7
    });
   	$("#requirementDate").jqxDateTimeInput({theme: "olbius"});
   	$('#requirementDate').val(null);
	$("#alterSave5").click(function () {
		if($('#selectFacility').jqxValidator("validate")){
	    	dataFacility.requirementDate = $('#requirementDate').val().toTimeStamp();
	    	executeTask(dataFacility, "updateReceiptRequirement");
	    	$('#selectFacility').jqxWindow('close');
		}
	});
	$("#selectFacility").jqxValidator({
	   	rules: [
	   	        { input: "#requirementDate", message: "${StringUtil.wrapString(uiLabelMap.DateNotValid)}", action: 'change', 
	   	        	rule: function (input, commit) {
	   	        		var requirementDate = $("#requirementDate").jqxDateTimeInput('getDate');
						var now = new Date();
						now.setHours(0,0,0,0);
						if(requirementDate >= now){
							return true;
						}
						return false;
	   	        	}
	   	        }
           ]
	 });
	 function executeTask(data, url) {
    	var requirementId;
    	jQuery.ajax({
			url: url,
			type: "POST",
			data: data,
			success: function(res) {
				requirementId = res["requirementId"];
	        }
		}).done(function() {
			$("#clearfilteringbuttonjqxgrid").click();
		});
	}
</script>