<script>
	var locationId = '${parameters.locationId}';
	var facilityId = '${parameters.facilityId}';
	var description = '${parameters.description}';
	
	function myFunction(){
    	var data = $('#jqxgrid').jqxGrid('getrowdata', 0);
    	var data2 = $('#jqxgrid').jqxGrid('getrowdata', 1);;
     	alert(data);
	}
	<#assign productList = delegator.findList("ListProductByInventoryItemId", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId)), null, null, null, false) />
	var productData = new Array();
	<#list productList as product>
		<#assign productId = StringUtil.wrapString(product.productId) />
		var row = {};
		row['productId'] = "${product.productId}";
		productData[${product_index}] = row;
	</#list>


	<#assign list = listUom.size()/>
    <#if listUom?size gt 0>
		<#assign uomId="var uomId = ['" + StringUtil.wrapString(listUom.get(0).uomId?if_exists) + "'"/>
		<#assign description="var description = ['" + StringUtil.wrapString(listUom.get(0).description?if_exists) + "'"/>
		<#if listUom?size gt 1>
			<#list 1..(list - 1) as i>
				<#assign uomId=uomId + ",'" + StringUtil.wrapString(listUom.get(i).uomId?if_exists) + "'"/>
				<#assign description=description + ",'" + StringUtil.wrapString(listUom.get(i).description?if_exists) + "'"/>
			</#list>
		</#if>
		<#assign uomId=uomId + "];"/>
		<#assign description=description + "];"/>
	<#else>
		<#assign uomId="var uomId = [];"/>
    	<#assign description="var description = [];"/>
    </#if>
	${uomId}
	${description}
	var adapter = new Array();
	for(var i = 0; i < ${list}; i++){
		var row = {};
		row['uomId'] = uomId[i];
		row['description'] = description[i];
		adapter[i] = row;
	}
	
	
	function getUom(uomId) {
		for ( var x in adapter) {
			if (uomId == adapter[x].uomId) {
				return adapter[x].description;
			}
		}
	}
	
	
	
	
	<#assign locationFacilityList = delegator.findList("LocationFacility", null, null, null, null, false) />
	var locationFacilityData = new Array();
	<#list locationFacilityList as locationFacility>
		<#assign locationId = StringUtil.wrapString(locationFacility.locationId) />
		<#assign description = StringUtil.wrapString(locationFacility.description) />
		var row = {};
		row['locationId'] = "${locationFacility.locationId}";
		row['description'] = "${locationFacility.description}";
		locationFacilityData[${locationFacility_index}] = row;
	</#list>
	
	
	function getDescriptionInLocationFacility(description) {
		for ( var x in locationFacilityData) {
			if (locationId == locationFacilityData[x].locationId) {
				return locationFacilityData[x].description;
			}
		}
	}
	
	<#assign locationFacilityTypeList = delegator.findList("LocationFacilityType", null, null, null, null, false) />
	var locationFacilityTypeData = new Array();
	<#list locationFacilityTypeList as locationFacilityType>
		<#assign locationFacilityTypeId = StringUtil.wrapString(locationFacilityType.locationFacilityTypeId) />
		<#assign description = StringUtil.wrapString(locationFacilityType.description) />
		var row = {};
		row['locationFacilityTypeId'] = "${locationFacilityType.locationFacilityTypeId}";
		row['description'] = "${locationFacilityType.get('description', locale)?if_exists}";
		locationFacilityTypeData[${locationFacilityType_index}] = row;
	</#list>
	
	
</script>	
<div>
		<div id="myTable">
				<#assign dataField="[{ name: 'inventoryItemId', type: 'string'},
				   { name: 'locationId', type: 'string'},
				   { name: 'productId', type: 'string'},
				   { name: 'locationFacilityTypeId', type: 'string'},
				   { name: 'description', type: 'string'},
				   { name: 'quantity', type: 'number'},
				   { name: 'uomId', type: 'string'},
				   ]"/>

				<#assign columnlist="{ text: '${uiLabelMap.ProductProductId}', datafield: 'productId', filtertype: 'checkedlist', columntype: 'dropdownlist', 
					    createeditor: function (row, value, editor) {
			                editor.jqxDropDownList({ selectedIndex: 0,  source: productData, displayMember: 'productId', valueMember: 'productId'
			                	
			                });
			        	}, 
				   },
				   { text: '${uiLabelMap.FacilityLocationPosition}', datafield: 'locationId', columntype: 'dropdownlist', filtertype: 'checkedlist',
					   cellsrenderer: function(row, colum, value){
					       var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					       var description = data.locationId;
					       var description = getDescriptionInLocationFacility(description);
					       return '<span>' + description + '</span>';
					   }, 
					   createeditor: function (row, value, editor) {
			               editor.jqxDropDownList({ selectedIndex: 0,  source: locationFacilityData, displayMember: 'locationId', valueMember: 'locationId',
			               	 renderer: function (index, label, value) {
			               		 var datarecord = locationFacilityData[index];
			               		 return datarecord.description;
			               	 }
			               });
			           },
					   
				   },
				   
				   { text: '${uiLabelMap.Quantity}', datafield: 'quantity'},
				   { text: '${uiLabelMap.QuantityUomId}', datafield: 'uomId', columntype: 'dropdownlist', filtertype: 'checkedlist',
					   cellsrenderer: function(row, colum, value){
					       var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					       var uomId = data.uomId;
					       var uomId = getUom(uomId);
					       return '<span>' + uomId + '</span>';
					   }, 
					   createeditor: function (row, value, editor) {
			                editor.jqxDropDownList({ selectedIndex: 0,  source: adapter, displayMember: 'uomId', valueMember: 'uomId',
			                	renderer: function (index, label, value) {
				                    var datarecord = adapter[index];
				                    return datarecord.description;
				                }
			                });
			        	},   
			        	createfilterwidget: function (column, columnElement, widget) {
			        		widget.jqxDropDownList({ selectedIndex: 0,  source: adapter, displayMember: 'uomId', valueMember: 'uomId',
			                	renderer: function (index, label, value) {
				                    var datarecord = adapter[index];
				                    return datarecord.description;
				                }
			                });
			        	},
			       },
				   "/>
			
				<@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist  addrow="true"  editable="true"  alternativeAddPopup="alterpopupWindow" showtoolbar="true" editmode="click" selectionmode="singlecell"
			 		url="jqxGeneralServicer?sname=JQXgetLocationByProductId&locationId=${parameters.locationId}"
			 		createUrl="jqxGeneralServicer?locationId=${parameters.locationId}&jqaction=C&sname=JQXcreateInventoryItemByProductId" 	
			 		addColumns="locationId;quantity(java.math.BigDecimal);productId;uomId"
			 	/>
		</div>
		
</div>

<div id="alterpopupWindow">
	<div>${uiLabelMap.AddNewInventoryItemNotLocation} ${uiLabelMap.DSInFacility} ${parameters.facilityId} ${uiLabelMap.DSInlocation} ${parameters.description}</div>
	<div style="overflow: hidden;">
	    <table>
		 	<tr>
	 			<td align="left"><input id="locationId" type="hidden" value=${parameters.locationId}></input></td>
		 	</tr>
		 	<tr>
		 		<td align="right">${uiLabelMap.ProductProductId}:</td>
		 		<td align="left"><div id="productId2"></div></td>
		 	</tr>
		 	<tr>
		 		<td align="right">${uiLabelMap.Quantity}:</td>
	 			<td align="left"><input id="quantity"></input></td>
		 	</tr>
		 	<tr>
		 		<td align="right">${uiLabelMap.QuantityUomId}:</td>
	 			<td align="left"><div id="uomId2"></div></td>
		 	</tr>
	        <tr>
	            <td style="padding-top: 10px;float:right;" colspan="3">
	            	<div style="margin:0px auto;width:100%;">
	                	<input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" />
	                	<input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" />
	            	</div>
	        	</td>
	        </tr>
	    </table>
	</div>
</div>

<script>
	$("#productId2").jqxDropDownList({ selectedIndex: 0,  source: productData, displayMember: "productId", valueMember: "productId"});
	
	//Create productId
	$("#quantity").jqxInput();
	
	//Create uomId
	$("#uomId2").jqxDropDownList({ selectedIndex: 0,  source: adapter, displayMember: "description", valueMember: "uomId"});
	
	
	
	$("#alterpopupWindow").jqxWindow({
		width: 600 ,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7           
	});
	$("#alterCancel").jqxButton();
	$("#alterSave").jqxButton();
	
	// update the edited row when the user clicks the 'Save' button.
	$("#alterSave").click(function () {
		var row;
		row = { 
			locationId:$('#locationId').val(),	
		    quantity:$('#quantity').val(),
		    productId:$('#productId2').val(), 
		    uomId:$('#uomId2').val() 
		};
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		// select the first row and clear the selection.
		$("#jqxgrid").jqxGrid('clearSelection');                        
		$("#jqxgrid").jqxGrid('selectRow', 0); 
		$("#alterpopupWindow").jqxWindow('close');
	 });
	
</script>
		
		
