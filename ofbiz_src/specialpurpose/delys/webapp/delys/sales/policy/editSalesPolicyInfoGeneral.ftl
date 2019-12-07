<style type="text/css">
	#horizontalScrollBarjqxgridSalesStatement {
		visibility: inherit !important;
	}
	#salesStatementId {
		width:100%!important;
	}
	#roleTypeIds {
		margin-bottom:0;
	}
	#productStoreId_chzn, #roleTypeIds_chzn {
		margin-bottom:0;
		width:100% !important;
	}
	#productStoreId_chzn .chzn-drop, #roleTypeIds_chzn .chzn-drop{
		width:100% !important;
	}
	.div-size-small .field-lookup input[type="text"], 
	.div-size-small .view-calendar input[type="text"]{
		padding: 0 6px;
		min-height: 25px;
		margin-bottom:0;
	}
	.div-size-small .view-calendar button {
		height: 27px;
		margin-left: -5px;
		margin-top:2px;
	}
	.div-size-small .field-lookup a:before {
		height: 22px;
		margin-top:0;
	}
	.exhibited-group {
		<#if salesPolicy?exists && salesPolicy.salesPolicyTypeId?exists && (salesPolicy.salesPolicyTypeId == "EXHIBITED")>
			display: block;
		<#else>
			display: none;
		</#if>
	}
</style>
<div class="row-fluid">
	<#if salesPolicyId?has_content && salesPolicy?exists>
		<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>updateProductPromoAdvance</@ofbizUrl>" name="editSalesPolicy" id="editSalesPolicy">
			<input type="hidden" name="salesPolicyId" value="${salesPolicyId}">
	<#else>
		<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>createProductPromoAdvance</@ofbizUrl>" name="editSalesPolicy" id="editSalesPolicy">
			<input type="hidden" name="statusId" value="SALES_PL_CREATED">
	</#if>
		<div class="row-fluid">
			<div class="span6">
				<div class="control-group">
					<label class="control-label" for="salesPolicyId">${uiLabelMap.DASalesPolicyId}</label>
					<div class="controls">
						<input type="text" class="span12" name="salesPolicyId" id="salesPolicyId" value="<#if salesPolicy?exists>${salesPolicy.salesPolicyId?if_exists}<#else>${parameters.salesPolicyId?if_exists}</#if>"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label required" for="policyName">${uiLabelMap.DASalesPolicyName}</label>
					<div class="controls">
						<input type="text" class="span12" name="policyName" id="policyName" value="<#if salesPolicy?exists>${salesPolicy.policyName?if_exists}<#else>${parameters.policyName?if_exists}</#if>" size="100"/>
					</div>
				</div>
				<#--
				<div class="control-group">
					<label class="control-label" for="salesStatementId">${uiLabelMap.DASalesInSalesOutId}</label>
					<div class="controls">
						<div id="salesStatementId">
				       	 	<div id="jqxgridSalesStatement"></div>
				       	</div>
					</div>
				</div>
				-->
				<div class="control-group">
					<label class="control-label required" for="roleTypeIds">${uiLabelMap.DelysRoleTypeApply}</label>
					<div class="controls">
						<div id="roleTypeIds"></div>
					</div>
				</div>
				<#--<div class="control-group">
					<label class="control-label required" for="productStoreIds">${uiLabelMap.DelysPromotionStore}</label>
					<div class="controls">
						<div id="productStoreIds"></div>
					</div>
				</div>-->
				<div class="control-group">
					<label class="control-label" for="promoText">${uiLabelMap.DAContent}</label>
					<div class="controls">
						<textarea id="promoText" name="promoText" data-maxlength="50" rows="2" style="resize: vertical;margin-bottom:0" class="span12"><#if salesPolicy?exists>${salesPolicy.promoText?if_exists}</#if></textarea>
					</div>
				</div>
			</div><!--.span6-->
			<div class="span6">
				<div class="control-group">
					<label class="control-label required" for="fromDate">${uiLabelMap.DAFromDate}</label>
					<div class="controls">
						<div id="fromDate"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="thruDate">${uiLabelMap.DAThruDateOld}</label>
					<div class="controls">
						<div id="thruDate"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label required" for="geoIdsInclude">${uiLabelMap.DAGeoIdInclude}</label>
					<div class="controls">
						<div id="geoIdsInclude"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="geoIdsExclude">${uiLabelMap.DAGeoIdExclude}</label>
					<div class="controls">
						<div id="geoIdsExclude"></div>
					</div>
				</div>
			</div><!--.span6-->
		</div><!--.row-->
	</form>
</div><!--.row-fluid-->

<script type="text/javascript">
	$(function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		
		$(".chzn-select").chosen({
			search_contains: true
		});
		
		$("#salesPolicyId").jqxInput({height: 25, maxLength:20});
		$("#policyName").jqxInput({height:25, maxLength:100});
		
		<#if salesPolicy?exists>
			$("#salesPolicyId").jqxInput({disabled: true});
		</#if>
		
		$("#fromDate").jqxDateTimeInput({width: '218px', height: '25px', allowNullDate: true, value: null, formatString: 'yyyy-MM-dd HH:mm:ss'});
		<#if salesPolicy?exists && salesPolicy.fromDate?exists>
			$('#fromDate').jqxDateTimeInput('setDate', "${salesPolicy.fromDate}");
		</#if>
		$("#thruDate").jqxDateTimeInput({width: '218px', height: '25px', allowNullDate: true, value: null, formatString: 'yyyy-MM-dd HH:mm:ss'});
		<#if salesPolicy?exists && salesPolicy.thruDate?exists>
			$('#thruDate').jqxDateTimeInput('setDate', "${salesPolicy.thruDate}");
		</#if>
		
		// list roleTypes =======================================================================
		<#assign roleTypes = Static["com.olbius.util.SalesPartyUtil"].getListGVRoleMemberDescendantInGroup("SALES_COMMISSIO_ROLE", delegator)/>
		var roleTypeData = [
			<#list roleTypes as roleTypeItem>
			{
				'roleTypeId' : '${roleTypeItem.roleTypeId}',
				'description' : '${StringUtil.wrapString(roleTypeItem.get("description", locale))}'
			},
			</#list>
		];
		var sourceRoleType = {
			localdata: roleTypeData,
	        datatype: "array",
	        datafields: [
	            { name: 'roleTypeId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterRoleType = new $.jqx.dataAdapter(sourceRoleType, {
	        	formatData: function (data) {
	                if ($("#roleTypeIds").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#roleTypeIds").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $("#roleTypeIds").jqxComboBox({source: dataAdapterRoleType, multiSelect: true, width: '100%', height: 25,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	valueMember: "roleTypeId", 
	    	renderer: function (index, label, value) {
                    var valueStr = label + " [" + value + "]";
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterRoleType.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterRoleType.dataBind();
	        }
	    });
	    
	    <#if salesPolicy?exists && policyRoleTypeAppl?exists>
		    <#if policyRoleTypeAppl?is_collection>
		    	<#assign partyRoleTypesApplies = policyRoleTypeAppl/>
			    <#list roleTypes as roleType>
					<#list partyRoleTypesApplies as partyRoleTypesApply>
						<#if partyRoleTypesApply.roleTypeId == roleType.roleTypeId>
							$("#roleTypeIds").jqxComboBox('selectItem',"${partyRoleTypesApply.roleTypeId}");
						</#if>
					</#list>
				</#list>
		    <#else>
				<#assign partyRoleTypesApply = parameters.partyRoleTypesApply/>
			    <#list roleTypes as roleType>
					<#if partyRoleTypesApply.roleTypeId == roleType.roleTypeId>
						$("#roleTypeIds").jqxComboBox('selectItem',"${partyRoleTypesApply.roleTypeId}");
					</#if>
				</#list>
		    </#if>
	    </#if>
		
		<#--
		// list ProductStore =======================================================================
		var productStoreData = new Array();
		<#list productStores as productStoreItem>
			var row = {};
			row['productStoreId'] = '${productStoreItem.productStoreId}';
			row['description'] = '${productStoreItem.get("storeName", locale)}';
			productStoreData[${productStoreItem_index}] = row;
		</#list>
		var sourceProductStore = {
			localdata: productStoreData,
	        datatype: "array",
	        datafields: [
	            { name: 'productStoreId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterProductStore = new $.jqx.dataAdapter(sourceProductStore, {
	        	formatData: function (data) {
	                if ($("#productStoreIds").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#productStoreIds").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $("#productStoreIds").jqxComboBox({source: dataAdapterProductStore, multiSelect: true, width: '100%', height: 25, 
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	valueMember: "productStoreId", 
	    	renderer: function (index, label, value) {
                    var valueStr = label + " [" + value + "]";
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterProductStore.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterProductStore.dataBind();
	        }
	    });
	    
	    <#if salesPolicy?exists && productStorePromoAppl?exists>
		    <#if productStorePromoAppl?is_collection>
		    	<#assign productStoreIdApplies = productStorePromoAppl/>
			    <#list productStores as productStore>
					<#list productStoreIdApplies as productStoreIdApply>
						<#if productStoreIdApply.productStoreId == productStore.productStoreId>
							$("#productStoreIds").jqxComboBox('selectItem',"${productStoreIdApply.productStoreId}");
						</#if>
					</#list>
				</#list>
		    <#else>
				<#assign productStoreIdApply = parameters.productStoreIds/>
			    <#list productStores as productStore>
					<#if productStoreIdApply.productStoreId == productStore.productStoreId>
						$("#productStoreIds").jqxComboBox('selectItem',"${productStoreIdApply.productStoreId}");
					</#if>
				</#list>
		    </#if>
	    </#if>
		-->
	    
	    <#--
	    // list Sales policy JQX Dropdown =====================================================================
	    var sourceP2 =
	    {
	        datafields:[{name: 'salesId', type: 'string'},
	            		{name: 'salesName', type: 'string'},
	            		{name: 'organizationPartyId', type: 'string'},
	            		{name: 'internalPartyId', type: 'string'},
	            		{name: 'fromDate', type: 'date', other: 'Timestamp'},
						{name: 'thruDate', type: 'date', other: 'Timestamp'},
						{name: 'salesTypeId', type: 'string'},
        			],
	        cache: false,
	        root: 'results',
	        datatype: "json",
	        updaterow: function (rowid, rowdata) {
	            // synchronize with the server - send update command   
	        },
	        beforeprocessing: function (data) {
	            sourceP2.totalrecords = data.TotalRows;
	        },
	        filter: function () {
	            // update the grid and send a request to the server.
	            $("#jqxgridSalesStatement").jqxGrid('updatebounddata');
	        },
	        pager: function (pagenum, pagesize, oldpagenum) {
	            // callback called when a page or page size is changed.
	        },
	        sort: function () {
	            $("#jqxgridSalesStatement").jqxGrid('updatebounddata');
	        },
	        sortcolumn: 'salesId',
			sortdirection: 'asc',
	        type: 'POST',
	        data: {
		        noConditionFind: 'Y',
		        conditionsFind: 'N',
		    },
		    pagesize:5,
	        contentType: 'application/x-www-form-urlencoded',
	        url: 'jqxGeneralServicer?sname=JQGetListSalesStatementApproved',
	    };
	    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2,
	    {
	    	autoBind: true,
	    	formatData: function (data) {
		    	if (data.filterscount) {
	                var filterListFields = "";
	                for (var i = 0; i < data.filterscount; i++) {
	                    var filterValue = data["filtervalue" + i];
	                    var filterCondition = data["filtercondition" + i];
	                    var filterDataField = data["filterdatafield" + i];
	                    var filterOperator = data["filteroperator" + i];
	                    filterListFields += "|OLBIUS|" + filterDataField;
	                    filterListFields += "|SUIBLO|" + filterValue;
	                    filterListFields += "|SUIBLO|" + filterCondition;
	                    filterListFields += "|SUIBLO|" + filterOperator;
	                }
	                data.filterListFields = filterListFields;
	            }
	            return data;
	        },
	        loadError: function (xhr, status, error) {
	            alert(error);
	        },
	        downloadComplete: function (data, status, xhr) {
	                if (!sourceP2.totalRecords) {
	                    sourceP2.totalRecords = parseInt(data["odata.count"]);
	                }
	        }, 
	        beforeLoadComplete: function (records) {
	        	for (var i = 0; i < records.length; i++) {
	        		if(typeof(records[i])=="object"){
	        			for(var key in records[i]) {
	        				var value = records[i][key];
	        				if(value != null && typeof(value) == "object" && typeof(value) != null){
	        					//var date = new Date(records[i][key]["time"]);
	        					//records[i][key] = date;
	        				}
	        			}
	        		}
	        	}
	        }
	    });
	    $("#salesStatementId").jqxDropDownButton({ theme: theme, height: 25});
	    $("#jqxgridSalesStatement").jqxGrid({
	    	width:750,
	        source: dataAdapterP2,
	        filterable: true,
	        columnsresize: true, 
	        showfilterrow: true,
	        virtualmode: true, 
	        sortable:true,
	        theme: theme,
	        editable: false,
	        autoheight:true,
	        pageable: true,
	        rendergridrows: function(obj){
				return obj.data;
			},
	        columns: [{text: '${uiLabelMap.DAStatementId}', datafield: 'salesId', width:'14%'},
	          			{text: '${uiLabelMap.DAStatementName}', datafield: 'salesName'},
	          			{text: '${uiLabelMap.DAOrganizationId}', datafield: 'organizationPartyId', width:'14%'},
	          			{text: '${uiLabelMap.DAPartyApplyId}', datafield: 'internalPartyId', width:'14%'},
	          			{text: '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', dataField: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss'}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss'}, 
	          			{text: '${uiLabelMap.DAStatementType}', datafield: 'salesTypeId', width:'14%'},
	          			
	        		]
	    });
	    $("#jqxgridSalesStatement").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#jqxgridSalesStatement").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['salesId'] +'</div>';
	        $('#salesStatementId').jqxDropDownButton('setContent', dropDownContent);
	    });
	    -->
	    
	    // list geoIdsInclude =======================================================================
		<#assign geoList = Static["com.olbius.util.SalesPartyUtil"].getListDescendantGeoIncludeDefaultCountry(delegator)/>
		<#if geoList?exists>
			var geoData = [
				<#list geoList as geoItem>
				{
					geoId: '${geoItem.geoId}',
					geoName: "${StringUtil.wrapString(geoItem.get("geoName", locale))}",
					abbreviation: "${geoItem.abbreviation?default("")}",
					geoTypeId: "${geoItem.geoTypeId?default("")}"
				},
				</#list>
			];
		<#else>
			var geoData = [];
		</#if>
		var sourceGeo = {
			localdata: geoData,
	        datatype: "array",
	        datafields: [
	            { name: 'geoId' },
	            { name: 'geoName'},
	            { name: 'abbreviation'},
	            { name: 'geoTypeId'}
	        ]
	    };
	    var dataAdapterGeo = new $.jqx.dataAdapter(sourceGeo, {
	        	formatData: function (data) {
	                if ($("#geoIdsInclude").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#geoIdsInclude").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $("#geoIdsInclude").jqxComboBox({source: dataAdapterGeo, multiSelect: true, width: '100%', height: 25,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "geoName", 
	    	valueMember: "geoId", 
	    	renderer: function (index, label, value) {
	    		var item = dataAdapterGeo.records[index];
                    var valueStr = label + " [" + value + "] - " + item.abbreviation + " - " + item.geoTypeId;
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterGeo.records[index];
	            if (item != null) {
	                var label = item.geoName;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterGeo.dataBind();
	        }
	    });
	    
	    <#if salesPolicy?exists && salesPolicyGeoApplInclude?exists>
		    <#if salesPolicyGeoApplInclude?is_collection>
		    	<#assign salesPolicyGeoApplies = salesPolicyGeoApplInclude/>
			    <#list geoList as geoItem>
					<#list salesPolicyGeoApplies as salesPolicyGeoApply>
						<#if salesPolicyGeoApply.geoId == geoItem.geoId>
							$("#geoIdsInclude").jqxComboBox('selectItem',"${salesPolicyGeoApply.geoId}");
						</#if>
					</#list>
				</#list>
		    <#else>
				<#assign salesPolicyGeoApply = parameters.salesPolicyGeoApplInclude/>
			    <#list geoList as geoItem>
					<#if salesPolicyGeoApply.geoId == geoItem.geoId>
						$("#geoIdsInclude").jqxComboBox('selectItem',"${salesPolicyGeoApply.geoId}");
					</#if>
				</#list>
		    </#if>
	    </#if>
	    
	    // ============== geo id exclude
	    var dataAdapterGeo = new $.jqx.dataAdapter(sourceGeo, {
	        	formatData: function (data) {
	                if ($("#geoIdsExclude").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#geoIdsExclude").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $("#geoIdsExclude").jqxComboBox({source: dataAdapterGeo, multiSelect: true, width: '100%', height: 25,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "geoName", 
	    	valueMember: "geoId", 
	    	renderer: function (index, label, value) {
	    		var item = dataAdapterGeo.records[index];
                    var valueStr = label + " [" + value + "] - " + item.abbreviation + " - " + item.geoTypeId;
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterGeo.records[index];
	            if (item != null) {
	                var label = item.geoName;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterGeo.dataBind();
	        }
	    });
	    
	    <#if salesPolicy?exists && salesPolicyGeoApplExclude?exists>
		    <#if salesPolicyGeoApplExclude?is_collection>
		    	<#assign salesPolicyGeoApplies = salesPolicyGeoApplExclude/>
			    <#list geoList as geoItem>
					<#list salesPolicyGeoApplies as salesPolicyGeoApply>
						<#if salesPolicyGeoApply.geoId == geoItem.geoId>
							$("#geoIdsExclude").jqxComboBox('selectItem',"${salesPolicyGeoApply.geoId}");
						</#if>
					</#list>
				</#list>
		    <#else>
				<#assign salesPolicyGeoApply = parameters.salesPolicyGeoApplExclude/>
			    <#list geoList as geoItem>
					<#if salesPolicyGeoApply.geoId == geoItem.geoId>
						$("#geoIdsExclude").jqxComboBox('selectItem',"${salesPolicyGeoApply.geoId}");
					</#if>
				</#list>
		    </#if>
	    </#if>
	});
</script>