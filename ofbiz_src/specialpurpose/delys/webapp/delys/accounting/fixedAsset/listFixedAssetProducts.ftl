<script type="text/javascript">	
	<#assign fixedAssetProductTypeList = delegator.findList("FixedAssetProductType", null, null, null, null, false) />
	var dataFixedAssetProductTypeListView = new Array();
	var row = {};
	row['fixedAssetProductTypeId'] = '';
	row['description'] = '';
	dataFixedAssetProductTypeListView[0] = row;
	<#list fixedAssetProductTypeList as fixedAssetProductType>
		var row = {};
		row['fixedAssetProductTypeId'] = '${fixedAssetProductType.fixedAssetProductTypeId?if_exists}';
		row['description'] = '${fixedAssetProductType.get("description",locale)?if_exists}';
		dataFixedAssetProductTypeListView[${fixedAssetProductType_index} + 1] = row;
	</#list>	

	<#assign uomAndTypeList = delegator.findList("UomAndType", null, null, null, null, false) />
	var dataUomAndTypeListView = new Array();
	var row = {};
	row['uomId'] = '';
	row['description'] = '';
	dataUomAndTypeListView[0] = row;
	<#list uomAndTypeList as uomAndType >
		var row = {};
		row['uomId'] = '${uomAndType.uomId?if_exists}';
		row['description'] = "[" + '${uomAndType.typeDescription?if_exists}'  + "] " + '${uomAndType.description?if_exists}';
		dataUomAndTypeListView[${uomAndType_index} + 1] = row;
	</#list>
	
 	var cellclass = function (row, columnfield, value) {
 		var now = new Date();
		now.setHours(0,0,0,0);
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        if (data.thruDate != undefined && data.thruDate != null && Date.parseExact(data.thruDate,"dd/MM/yyyy HH:mm:ss") <= now) {
            return 'background-red';
        }
    }
</script>

<#assign params="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=listFixedAssetProductJqx">
<#assign dataField="[{ name: 'productId', type: 'string'},
					 { name: 'fixedAssetProductTypeId', type: 'string'},
					 { name: 'fixedAssetId', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'thruDate', type: 'date', other: 'Timestamp'},
					 { name: 'sequenceNum', type: 'number'},
					 { name: 'quantity', type: 'number'},
					 { name: 'comments', type: 'string'},
					 { name: 'quantityUomId', type: 'string'},
					 { name: 'productName', type: 'string'}					 
				   ]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.accProductName)}', datafield: 'productId', width: '20%', editable: false, cellsrenderer:
				       function(row, colum, value){
					        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        if (!data.productName)  var valuess = '';
					        else valuess = data.productName;
					        return \"<span> <a href='/catalog/control/EditProduct?productId=\" + data.productId + \"'>\" + valuess + \" [\" + data.productId + \"]</a></span>\";
			         }},
					 { text: '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetProductTypeId)}', datafield: 'fixedAssetProductTypeId', width: '10%', editable: false, filterable: false, cellclassname: cellclass,
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataFixedAssetProductTypeListView.length; i++){
	        							if(data.fixedAssetProductTypeId == dataFixedAssetProductTypeListView[i].fixedAssetProductTypeId){
	        								return '<span title=' + value +'>' + dataFixedAssetProductTypeListView[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
					 	},
	                    { text: '${StringUtil.wrapString(uiLabelMap.fromDate)}', width:150, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: false, columntype: 'datetimeinput'},
	                    { text: '${StringUtil.wrapString(uiLabelMap.thruDate)}', width:220, datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', columntype: 'datetimeinput',
	                    	validation: function (cell, value) {
                     			var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
                                var fromDate = data.fromDate;	                     			
                                if (!value)
                                   return true;
                                if (data.fromDate > value) {
                                    return { result: false, message: '${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}' };
                                }
                                return true;
                            },
						 	createeditor: function (row, column, editor) {
	                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd/MM/yyyy' });
	                     		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                     		if (!data.thruDate)
	                     		editor.jqxDateTimeInput('setDate', null);
	                     	}},
						{ text: '${uiLabelMap.comments}', width: '20%', datafield: 'comments'},
						{
		                      text: '${uiLabelMap.sequenceNum}', datafield: 'sequenceNum', width: 100, align: 'right', cellsalign: 'right', columntype: 'numberinput',
		                      createeditor: function (row, cellvalue, editor) {
		                          editor.jqxNumberInput({ decimalDigits: 0, digits: 3 });
		                      }
		                  },
		                  {
		                      text: '${uiLabelMap.Quantity}', datafield: 'quantity', width: 100, align: 'right', cellsalign: 'right', columntype: 'numberinput',
		                      createeditor: function (row, cellvalue, editor) {
		                          editor.jqxNumberInput({ decimalDigits: 0 });
		                      }
		                  },
	                    { text: '${StringUtil.wrapString(uiLabelMap.DAUom)}', datafield: 'quantityUomId', width: '10%', filtertype: 'checkedlist', cellclassname: cellclass, columntype: 'dropdownlist',
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataUomAndTypeListView.length; i++){
	        							if(data.quantityUomId == dataUomAndTypeListView[i].uomId){
	        								return '<span title=' + value +'>' + dataUomAndTypeListView[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
	    					createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(dataUomAndTypeListView,
				                {
				                    autoBind: true
				                });
				   				var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'uomId', valueMember : 'uomId', height: '21px',renderer: function (index, label, value) 
								{
									for(i=0;i < uniqueRecords2.length; i++){
										if(uniqueRecords2[i].statusId == value){
											return uniqueRecords2[i].description;
										}
									}
								    return value;
								}});
								widget.jqxDropDownList('checkAll');
				   			},			   			
				   				createeditor: function (row, column, editor) {
	                            editor.jqxDropDownList({source: dataUomAndTypeListView, displayMember:\"description\", valueMember: \"uomId\",
		                            renderer: function (index, label, value) {
					                    var datarecord = dataUomAndTypeListView[index];
					                    return datarecord.description;
					                  }
		                        });}
				   			},				
					"/>
	
	<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" addrefresh="true"	 deleterow="true"	
		url=params addColumns="fixedAssetId[${parameters.fixedAssetId}];productId;fixedAssetProductTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);sequenceNum;quantity;quantityUomId;comments"
		createUrl="jqxGeneralServicer?sname=addFixedAssetProduct&jqaction=C" 
		updateUrl="jqxGeneralServicer?sname=updateFixedAssetProduct&fixedAssetId=${parameters.fixedAssetId}&jqaction=U"
		editColumns="fixedAssetId[${parameters.fixedAssetId}];productId;fixedAssetProductTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);sequenceNum;quantity;quantityUomId;comments"
		removeUrl="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=removeFixedAssetProduct&jqaction=D"
		deleteColumn="fixedAssetId[${parameters.fixedAssetId}];productId;fixedAssetProductTypeId;fromDate(java.sql.Timestamp)"
		otherParams="productName:S-getProductName(productId{productId})<productName>"
		showlist="true"
	/>	
<#include "component://delys/webapp/delys/accounting/popup/popupAddFixedAssetsProducts.ftl"/>
