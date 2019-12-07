<script>
	//Prepare TaxAuthorityRateTypes data
	<#assign tartLength = listTaxAuthorityRateTypes.size()/>
    <#if listTaxAuthorityRateTypes?size gt 0>
	    <#assign tarType="var tarType = ['" + StringUtil.wrapString(listTaxAuthorityRateTypes.get(0).taxAuthorityRateTypeId?if_exists) + "'"/>
		<#assign tarDescription="var tarDescription = ['" + StringUtil.wrapString(listTaxAuthorityRateTypes.get(0).description?if_exists) +"'"/>
		<#if listTaxAuthorityRateTypes?size gt 1>
			<#list 1..(tartLength - 1) as i>
				<#assign tarType=tarType + ",'" + StringUtil.wrapString(listTaxAuthorityRateTypes.get(i).taxAuthorityRateTypeId?if_exists) + "'"/>
				<#assign tarDescription=tarDescription + ",\"" + StringUtil.wrapString(listTaxAuthorityRateTypes.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign tarType=tarType + "];"/>
		<#assign tarDescription=tarDescription + "];"/>
	<#else>
    	<#assign tarType="var tarType = [];"/>
    	<#assign tarDescription="var tarDescription = [];"/>
    </#if>
	${tarType}
	${tarDescription}
	
	var tartData = new Array();
	for(i = 0; i < ${tartLength}; i++){
		var row = {};
		row["taxAuthorityRateTypeId"] = tarType[i];
		row["description"] = tarDescription[i];
		tartData[i] = row;
	} 
	
	//Prepare listTaxAuthorityCategoryViews data
	<#assign tacvLength = listTaxAuthorityCategoryViews.size()/>
    <#if listTaxAuthorityCategoryViews?size gt 0>
	    <#assign taCategoryId="var taCategoryId = ['" + StringUtil.wrapString(listTaxAuthorityCategoryViews.get(0).productCategoryId?if_exists) + "'"/>
		<#assign taDescription="var taDescription = ['" + StringUtil.wrapString(listTaxAuthorityCategoryViews.get(0).description?if_exists) + "[" + StringUtil.wrapString(listTaxAuthorityCategoryViews.get(0).productCategoryId?if_exists) +"]" +"'"/>
		<#if listTaxAuthorityCategoryViews?size gt 1>
			<#list 1..(tacvLength - 1) as i>
				<#assign taCategoryId=taCategoryId + ",'" + StringUtil.wrapString(listTaxAuthorityCategoryViews.get(i).productCategoryId?if_exists) + "'"/>
				<#assign taDescription=taDescription + ",\"" + StringUtil.wrapString(listTaxAuthorityCategoryViews.get(i).description?if_exists) + "[" + StringUtil.wrapString(listTaxAuthorityCategoryViews.get(0).productCategoryId?if_exists) +"]"  + "\""/>
			</#list>
		</#if>
		<#assign taCategoryId=taCategoryId + "];"/>
		<#assign taDescription=taDescription + "];"/>
	<#else>
    	<#assign taCategoryId="var taCategoryId = [];"/>
    	<#assign taDescription="var taDescription = [];"/>
    </#if>
	${taCategoryId}
	${taDescription}
	
	var tacData = new Array();
	for(i = 0; i < ${tacvLength}; i++){
		var row = {};
		row["productCategoryId"] = taCategoryId[i];
		row["description"] = taDescription[i];
		tacData[i] = row;
	}
	
	//Prepare titletransferEnumId data
	<#assign eLength = listEnums.size()/>
    <#if listEnums?size gt 0>
	    <#assign enumId="var enumId = ['" + StringUtil.wrapString(listEnums.get(0).enumId?if_exists) + "'"/>
		<#assign enumDescription="var enumDescription = ['" + StringUtil.wrapString(listEnums.get(0).description?if_exists) +"'"/>
		<#if listEnums?size gt 1>
			<#list 1..(eLength - 1) as i>
				<#assign enumId=enumId + ",'" + StringUtil.wrapString(listEnums.get(i).enumId?if_exists) + "'"/>
				<#assign enumDescription=enumDescription + ",\"" + StringUtil.wrapString(listEnums.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign enumId=enumId + "];"/>
		<#assign enumDescription=enumDescription + "];"/>
	<#else>
    	<#assign enumId="var enumId = [];"/>
    	<#assign enumDescription="var enumDescription = [];"/>
    </#if>
	${enumDescription}
	${enumId}
	
	var enumData = new Array();
	for(i = 0; i < ${eLength}; i++){
		var row = {};
		row["enumId"] = enumId[i];
		row["description"] = enumDescription[i];
		enumData[i] = row;
	}
	
	// Prepare TaxPro Data
	var taxProData = new Array();
	var proRow0 = {}
	proRow0["taxPromotions"] = "Y";
	proRow0["description"] = "Yes";
	taxProData[0] = proRow0;
	
	var proRow1 = {}
	proRow1["taxPromotions"] = "N";
	proRow1["description"] = "No";
	taxProData[1] = proRow1;
	
	// Prepare TaxShip Data
	var taxShipData = new Array();
	var shipRow0 = {};
	shipRow0["taxShipping"] = "Y";
	shipRow0["description"] = "Yes";
	taxShipData[0] = shipRow0;
	
	var shipRow1 = {};
	shipRow1["taxShipping"] = "N";
	shipRow1["description"] = "No";
	taxShipData[1] = shipRow1;
</script>
<#assign dataField="[{ name: 'taxAuthorityRateTypeId', type: 'string' },
					 { name: 'productStoreId', type: 'string'},
					 { name: 'productCategoryId', type: 'string'},
					 { name: 'titleTransferEnumId', type: 'string'},
					 { name: 'minItemPrice', type: 'number'},
					 { name: 'minPurchase', type: 'number'},
					 { name: 'taxShipping', type: 'string'},
					 { name: 'taxPercentage', type: 'number'},
					 { name: 'taxPromotions', type: 'string'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'},
					 { name: 'description', type: 'string'},
					 { name: 'taxAuthorityRateSeqId', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.accTaxAuthorityRateTypeId}', width: 200, datafield: 'taxAuthorityRateTypeId',columntype: 'dropdownlist',
					 		createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: tartData, displayMember:\"taxAuthorityRateTypeId\", valueMember: \"taxAuthorityRateTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = tartData[index];
			                    return datarecord.description;
			                  }
                        });
					 },
					 cellsrenderer : function (row, column, value) {
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        				for(i=0;i < tarType.length; i++){
        				if(tarType[i] == data.taxAuthorityRateTypeId){
        					return \"<span>\" + tarDescription[i] + \"</span>\";
        					}
        				}
        				return \"\";
    				}
					 },
					 { text: '${uiLabelMap.productStoreId}', width: 150, datafield: 'productStoreId', columntype: 'template',
                     	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxPSGrid\"></div>');
                            editor.jqxDropDownButton();
                            // prepare the data
						    var sourcePS = { datafields: [
						      { name: 'productStoreId', type: 'string' },
						      { name: 'storeName', type: 'string' },
						    ],
							cache: false,
							root: 'results',
							datatype: 'json',
							updaterow: function (rowid, rowdata) {
							// synchronize with the server - send update command   
							},
							beforeprocessing: function (data) {
				    			sourcePS.totalrecords = data.TotalRows;
							},
							filter: function () {
				   				// update the grid and send a request to the server.
				   				$(\"#jqxPSGrid\").jqxGrid('updatebounddata');
							},
							pager: function (pagenum, pagesize, oldpagenum) {
				  				// callback called when a page or page size is changed.
							},
							sort: function () {
				  				$(\"#jqxPSGrid\").jqxGrid('updatebounddata');
							},
							sortcolumn: 'productStoreId',
               				sortdirection: 'asc',
							type: 'POST',
							data: {
								noConditionFind: 'Y',
								conditionsFind: 'N',
							},
							pagesize:5,
							contentType: 'application/x-www-form-urlencoded',
							url: 'jqxGeneralServicer?sname=JQGetListProductStores',
							};
						    var dataAdapterPS = new $.jqx.dataAdapter(sourcePS,
						    {
						    	formatData: function (data) {
							    	if (data.filterscount) {
			                            var filterListFields = \"\";
			                            for (var i = 0; i < data.filterscount; i++) {
			                                var filterValue = data[\"filtervalue\" + i];
			                                var filterCondition = data[\"filtercondition\" + i];
			                                var filterDataField = data[\"filterdatafield\" + i];
			                                var filterOperator = data[\"filteroperator\" + i];
			                                filterListFields += \"|OLBIUS|\" + filterDataField;
			                                filterListFields += \"|SUIBLO|\" + filterValue;
			                                filterListFields += \"|SUIBLO|\" + filterCondition;
			                                filterListFields += \"|SUIBLO|\" + filterOperator;
			                            }
			                            data.filterListFields = filterListFields;
			                        }
			                         data.$skip = data.pagenum * data.pagesize;
			                         data.$top = data.pagesize;
			                         data.$inlinecount = \"allpages\";
			                        return data;
			                    },
			                    loadError: function (xhr, status, error) {
				                    alert(error);
				                },
				                downloadComplete: function (data, status, xhr) {
				                        if (!sourcePS.totalRecords) {
				                            sourcePS.totalRecords = parseInt(data[\"odata.count\"]);
				                        }
				                }, 
				                beforeLoadComplete: function (records) {
				                	for (var i = 0; i < records.length; i++) {
				                		if(typeof(records[i])==\"object\"){
				                			for(var key in records[i]) {
				                				var value = records[i][key];
				                				if(value != null && typeof(value) == \"object\" && typeof(value) != null){
				                					var date = new Date(records[i][key][\"time\"]);
				                					records[i][key] = date;
				                				}
				                			}
				                		}
				                	}
				                }
						    });
				            $(\"#jqxPSGrid\").jqxGrid({
				            	width:400,
				                source: dataAdapterPS,
				                filterable: true,
				                virtualmode: true, 
				                sortable:true,
				                editable: false,
				                autoheight:true,
				                pageable: true,
				                rendergridrows: function(obj)
								{
									return obj.data;
								},
				                columns: [
				                  { text: 'productStoreId', datafield: 'productStoreId'},
				                  { text: 'storeName', datafield: 'storeName'},
				                ]
				            });
				            $(\"#jqxPSGrid\").on('rowselect', function (event) {
				            	//$(\"#jqxPSGrid\").jqxGrid({ disabled: true});
                                var args = event.args;
                                var row = $(\"#jqxPSGrid\").jqxGrid('getrowdata', args.rowindex);
                                var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['productStoreId'] +'</div>';
                                //$('#dropDownButtonContentjqxDD').html(dropDownContent);
                                editor.jqxDropDownButton('setContent', dropDownContent);
                            });
                      },
                        geteditorvalue: function (row, cellvalue, editor) {
                            // return the editor's value.
                            editor.jqxDropDownButton(\"close\");
                            return cellvalue;
                     }},
					 { text: '${uiLabelMap.ProductCategoryId}', width: 150, datafield: 'productCategoryId',columntype: 'dropdownlist',
					 		createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: tacData, displayMember:\"productCategoryId\", valueMember: \"productCategoryId\",
                            renderer: function (index, label, value) {
			                    var datarecord = tacData[index];
			                    return datarecord.description;
			                  }
                        });
					 },
					 cellsrenderer : function (row, column, value) {
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        				for(i=0;i < tacData.length; i++){
        				var tacItemData = tacData[i];
        				if(tacItemData[\"productCategoryId\"] == data.productCategoryId){
        					var description = tacItemData[\"description\"];
        					return \"<span>\" + description + \"</span>\";
        					}
        				}
        				return \"\";
    					}
					 },
					 { text: '${uiLabelMap.AccountingTitleTransfer}', width: 200, datafield: 'titleTransferEnumId',columntype: 'dropdownlist',
					 		createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: enumData, displayMember:\"enumId\", valueMember: \"enumId\",
                            renderer: function (index, label, value) {
			                    var datarecord = enumData[index];
			                    return datarecord.description;
			                  }
                        });
					 },
					 cellsrenderer : function (row, column, value) {
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        				for(i=0;i < enumData.length; i++){
        				var enumItemData = enumData[i];
        				if(enumItemData[\"enumId\"] == data.titleTransferEnumId){
        					var description = enumItemData[\"description\"];
        					return \"<span>\" + description + \"</span>\";
        					}
        				}
        				return \"\";
    					}
					 },
					 { text: '${uiLabelMap.FormFieldTitle_minItemPrice}', width: 150, datafield: 'minItemPrice'},
					 { text: '${uiLabelMap.FormFieldTitle_minPurchase}', width: 100, datafield: 'minPurchase'},
					 { text: '${uiLabelMap.FormFieldTitle_taxShipping}', width: 150, datafield: 'taxShipping',columntype: 'dropdownlist',
					 		createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: taxShipData, displayMember:\"taxShipping\", valueMember: \"taxShipping\",
                            renderer: function (index, label, value) {
			                    var datarecord = taxShipData[index];
			                    return datarecord.description;
			                  }
                        });
					 },
					 cellsrenderer : function (row, column, value) {
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        				for(i=0;i < taxShipData.length; i++){
        				var taxShipItemData = taxShipData[i];
        				if(taxShipItemData[\"taxShipping\"] == data.taxShipping){
        					var description = taxShipItemData[\"description\"];
        					return \"<span>\" + description + \"</span>\";
        					}
        				}
        				return \"\";
    					}
					 },
					 { text: '${uiLabelMap.FormFieldTitle_taxPercentage}', width: 150, datafield: 'taxPercentage'},
					 { text: '${uiLabelMap.FormFieldTitle_taxPromotions}', width: 150, datafield: 'taxPromotions',columntype: 'dropdownlist',
					 		createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: taxProData, displayMember:\"taxPromotions\", valueMember: \"taxPromotions\",
                            renderer: function (index, label, value) {
			                    var datarecord = taxProData[index];
			                    return datarecord.description;
			                  }
                        });
					 },
					 cellsrenderer : function (row, column, value) {
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        				for(i=0;i < taxProData.length; i++){
        				var taxProItemData = taxProData[i];
        				if(taxProItemData[\"taxPromotions\"] == data.taxShipping){
        					var description = taxProItemData[\"description\"];
        					return \"<span>\" + description + \"</span>\";
        					}
        				}
        				return \"\";
    					}
					 },
					 { text: '${uiLabelMap.fromDate}', width: 150, datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', columntype: 'template',
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '300px', height: '25px',  formatString: 'dd-MM-yyyy hh:mm:ss' });
                     	}
                     },
					 { text: '${uiLabelMap.accThruDate}', width: 150, datafield: 'thruDate' ,cellsformat: 'dd/MM/yyyy', columntype: 'template',
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '300px', height: '25px',  formatString: 'dd-MM-yyyy hh:mm:ss' });
                     	}
                     },
					 { text: '${uiLabelMap.description}', width: 200, datafield: 'description'}
					"/>
	
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" deleterow="true" editable="true"
		 url="jqxGeneralServicer?sname=JQListTaxAuthorityRateProducts&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}" 
		 updateUrl="jqxGeneralServicer?sname=updateTaxAuthorityRateProduct&jqaction=U&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 createUrl="jqxGeneralServicer?sname=createTaxAuthorityRateProduct&jqaction=C&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 removeUrl="jqxGeneralServicer?sname=deleteTaxAuthorityRateProduct&jqaction=C&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 editColumns="taxAuthPartyId[${parameters.taxAuthPartyId}];taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthorityRateTypeId;productStoreId;productCategoryId;titleTransferEnumId;minItemPrice(java.math.BigDecimal);minPurchase(java.math.BigDecimal);taxShipping;taxPercentage(java.math.BigDecimal);taxPromotions;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);description;taxAuthorityRateSeqId"
		 addColumns="taxAuthPartyId[${parameters.taxAuthPartyId}];taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthorityRateTypeId;productStoreId;productCategoryId;titleTransferEnumId;minItemPrice(java.math.BigDecimal);minPurchase(java.math.BigDecimal);taxShipping;taxPercentage(java.math.BigDecimal);taxPromotions;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);description"	 
		 deleteColumn="taxAuthorityRateSeqId"
		 height="100" autoheight="true"/>

<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accTaxAuthorityRateTypeId}:</td>
	 			<td align="left">
	 				<div id="taxAuthorityRateTypeIdAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.productStoreId}:</td>
	 			<td align="left">
	 				<div id="productStoreIdAdd">
	 					<div id="jqxPSGrid"/>
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
	 			<td align="right">${uiLabelMap.ProductCategoryId}:</td>
	 			<td align="left">
 					<div id="productCategoryIdAdd">
 					</div>
 				</td>
 			</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.AccountingTitleTransfer}:</td>
	 			<td align="left">
	 				<div id="titleTransferEnumIdAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_minItemPrice}:</td>
	 			<td align="left">
	 				<input id="minItemPriceAdd">
	 				</input>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_minPurchase}:</td>
	 			<td align="left">
	 				<input id="minPurchaseAdd">
	 				</input>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_taxShipping}:</td>
	 			<td align="left">
	 				<div id="taxShippingAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_taxPercentage}:</td>
	 			<td align="left">
	 				<input id="taxPercentageAdd">
	 				</input>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_taxPromotions}:</td>
	 			<td align="left">
	 				<div id="taxPromotionsAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.fromDate}:</td>
	 			<td align="left">
	 				<div id="fromDateAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accThruDate}:</td>
	 			<td align="left">
	 				<div id="thruDateAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.description}:</td>
	 			<td align="left">
	 				<input id="descriptionAdd">
	 				</input>
	 			</td>
    	 	</tr>
            <tr>
                <td align="right"></td>
                <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
            </tr>
        </table>
    </div>
</div>
<script>
 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
// Create productStoreId
var sourcePS = { datafields: [
						      { name: 'productStoreId', type: 'string' },
						      { name: 'storeName', type: 'string' },
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourcePS.totalrecords = data.TotalRows;
				},
				filter: function () {
				   // update the grid and send a request to the server.
				   $("#jqxPSGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxPSGrid").jqxGrid('updatebounddata');
				},
				sortcolumn: 'productStoreId',
               	sortdirection: 'asc',
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize:5,
				contentType: 'application/x-www-form-urlencoded',
				url: 'jqxGeneralServicer?sname=JQGetListProductStores',
			};
var dataAdapterPS = new $.jqx.dataAdapter(sourcePS);
$('#productStoreIdAdd').jqxDropDownButton({ width: 150, height: 25});
$("#jqxPSGrid").jqxGrid({
		width:400,
		source: dataAdapterPS,
		filterable: true,
		virtualmode: true, 
		sortable:true,
		editable: false,
		autoheight:true,
		pageable: true,
		rendergridrows: function(obj)
		{	
			return obj.data;
		},
		columns: 
		[
			{ text: 'productStoreId', datafield: 'productStoreId'},
			{ text: 'storeName', datafield: 'storeName'},
		]
	});
$("#jqxPSGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxPSGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['productStoreId'] + '</div>';
                $("#productStoreIdAdd").jqxDropDownButton('setContent', dropDownContent);
            });
//Create taxAuthorityRateTypeId
$("#taxAuthorityRateTypeIdAdd").jqxDropDownList({source: tartData, displayMember:"taxAuthorityRateTypeId", valueMember: "taxAuthorityRateTypeId",
    renderer: function (index, label, value) {
        var datarecord = tartData[index];
        return datarecord.description;
      }
});
//Create titleTransferEnumId
$("#titleTransferEnumIdAdd").jqxDropDownList({source: enumData, displayMember:"enumId", valueMember: "enumId",
    renderer: function (index, label, value) {
        var datarecord = enumData[index];
        return datarecord.description;
      }
});
//Create productCategoryId
$("#productCategoryIdAdd").jqxDropDownList({source: tacData, displayMember:"productCategoryId", valueMember: "productCategoryId",
    renderer: function (index, label, value) {
        var datarecord = tacData[index];
        return datarecord.description;
      }
});
//Create minItemPrice
$("#minItemPriceAdd").jqxInput();
//Create minPurchase
$("#minPurchaseAdd").jqxInput();
//Create taxShipping
$("#taxShippingAdd").jqxDropDownList({source: taxShipData, displayMember:"taxShipping", valueMember: "taxShipping",
    renderer: function (index, label, value) {
        var datarecord = taxShipData[index];
        return datarecord.description;
      }
});
//Create taxPercentage
$("#taxPercentageAdd").jqxInput();
//Create taxPromotions
$("#taxPromotionsAdd").jqxDropDownList({source: taxProData, displayMember:"taxPromotions", valueMember: "taxPromotions",
    renderer: function (index, label, value) {
        var datarecord = taxProData[index];
        return datarecord.description;
      }
});
//Create fromDate
$("#fromDateAdd").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});
//Create thruDate
$("#thruDateAdd").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});
//Create Description
$("#descriptionAdd").jqxInput();

//Create Popup
$("#alterpopupWindow").jqxWindow({
        width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
    });
    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { 
        		taxAuthorityRateTypeId:$('#taxAuthorityRateTypeIdAdd').val(),
        		productStoreId:$('#productStoreIdAdd').val(),
        		titleTransferEnumId:$('#titleTransferEnumIdAdd').val(),
        		productCategoryId:$('#productCategoryIdAdd').val(),
        		minItemPrice:$('#minItemPriceAdd').val(),
        		minPurchase:$('#minPurchaseAdd').val(),
        		taxShipping:$('#taxShippingAdd').val(),
        		taxPercentage:$('#taxPercentageAdd').val(),
        		taxPromotions:$('#taxPromotionsAdd').val(),
        		fromDate:"Date(" + $('#fromDateAdd').jqxDateTimeInput('getDate').getTime() + ")",
        		thruDate:"Date(" + $('#thruDateAdd').jqxDateTimeInput('getDate').getTime() + ")",
        		description:$('#descriptionAdd').val()
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>