<script type="text/javascript">
	<#assign itlength = product.size()/>
    <#if product?size gt 0>
	    <#assign vaProduct="var vaProduct = ['" + StringUtil.wrapString(product.get(0).productId?if_exists) + "'"/>
		<#assign vaProductValue="var vaProductValue = ['" + StringUtil.wrapString(product.get(0).productName?if_exists) + " [" + StringUtil.wrapString(product.get(0).productId?if_exists) +"]" + "'"/>
		<#if product?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign vaProduct=vaProduct + ",'" + StringUtil.wrapString(product.get(i).productId?if_exists) + "'"/>
				<#assign vaProductValue=vaProductValue + ",\"" + StringUtil.wrapString(product.get(i).productName?if_exists) + " [" + StringUtil.wrapString(product.get(i).productId?if_exists) +"]" + "\""/>
			</#list>
		</#if>
		<#assign vaProduct=vaProduct + "];"/>
		<#assign vaProductValue=vaProductValue + "];"/>
	<#else>
    	<#assign vaProduct="var vaProduct = [];"/>
    	<#assign vaProductValue="var vaProductValue = [];"/>
    </#if>
	${vaProduct}
	${vaProductValue}	
	var dataProduct = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["productId"] = vaProduct[i];
        row["productName"] = vaProductValue[i];
        dataProduct[i] = row;
    }
    <#assign itlength = productCategory.size()/>
    <#if productCategory?size gt 0>
	    <#assign vaProdCate="var vaProdCate = ['" + StringUtil.wrapString(productCategory.get(0).productCategoryId?if_exists) + "'"/>
		<#assign vaProdCateValue="var vaProdCateValue = [\"" + StringUtil.wrapString(productCategory.get(0).categoryName?if_exists) + " [" + StringUtil.wrapString(productCategory.get(0).productCategoryId?if_exists) + "]" + "\""/>
		<#if productCategory?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign vaProdCate=vaProdCate + ",'" + StringUtil.wrapString(productCategory.get(i).productCategoryId?if_exists) + "'"/>
				<#assign vaProdCateValue=vaProdCateValue + ",\"" + StringUtil.wrapString(productCategory.get(i).categoryName?if_exists) + " [" +StringUtil.wrapString(productCategory.get(i).productCategoryId?if_exists) +"]" + "\"" />
			</#list>
		</#if>
		<#assign vaProdCate=vaProdCate + "];"/>
		<#assign vaProdCateValue=vaProdCateValue + "];"/>
	<#else>
    	<#assign vaProdCate="var vaProdCate = [];"/>
    	<#assign vaProdCateValue="var vaProdCateValue = [];"/>
    </#if>
	${vaProdCate}
	${vaProdCateValue}	
	var dataProdCate = new Array();
	for (var i = 0; i < ${itlength}; i++) {
        var row = {};
        row["productCategoryId"] = vaProdCate[i];
        row["categoryName"] = vaProdCateValue[i];
        dataProdCate[i] = row;
    }
</script>
<script type="text/javascript">
	var linkProductrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < vaProduct.length; i++){
        	if(vaProduct[i] == data.productId){
        		return "<span>" + vaProductValue[i] + "</span>";
        	}
        }
        return "";
    }
    var linkProdCaterenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < vaProdCate.length; i++){
        	if(vaProdCate[i] == data.productCategoryId){
        		return "<span>" + vaProdCateValue[i] + "</span>";
        	}
        }
        return "";
    }
</script>
<#assign dataField="[{ name: 'productCategoryId', type: 'string' },
					 { name: 'productId', type: 'string'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'},
					 { name: 'comments', type: 'string'}]
					 "/>

<#assign columnlist="{ text: '${uiLabelMap.accProductCategory}', width: 350, datafield: 'productCategoryId', cellsrenderer:linkProdCaterenderer, editable : false},
					 { text: '${uiLabelMap.ProductListProduct}', width: 350, datafield: 'productId', cellsrenderer:linkProductrenderer,  editable : false },
					 { text: '${uiLabelMap.fromDate}', width: 200, datafield: 'fromDate', columntype: 'template',  editable : false,
                     cellsformat:'dd-MM-yyyy hh:mm:ss'                    	                      
                     },
					 { text: '${uiLabelMap.accThruDate}', width: 200, datafield: 'thruDate', columntype: 'template',
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '200px', height: '25px',  formatString: 'dd-MM-yyyy hh:mm:ss' });
	                     	 editor.on('valuechanged', function (event) 
							{  
						    		var jsDate = event.args.date; 
							});
                     	},                     	                                                
                      cellsformat:'dd-MM-yyyy hh:mm:ss'                     	  
                     },					 	
					 { text: '${uiLabelMap.accComments}', width: 200, datafield: 'comments'}
					 "/>
	
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" deleterow="true" editable="true"
		 editmode="singlecell" url="jqxGeneralServicer?sname=JQGetListTaxProductCategoryMember" 
		 createUrl="jqxGeneralServicer?sname=safeAddProductToCategory&jqaction=C" addColumns="productCategoryId;productId;fromDate(java.sql.Timestamp)"
		 removeUrl="jqxGeneralServicer?sname=removeProductFromCategory&jqaction=D" deleteColumn="productCategoryId;productId;fromDate(java.sql.Timestamp)"
		 updateUrl="jqxGeneralServicer?sname=updateProductToCategory&jqaction=U" editColumns="productCategoryId;productId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);comments"
		 />
		 
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accProductCategory}:</td>
	 			<td align="left"><div id="productCategoryIdPop"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.ProductListProduct}:</td>
	 			<td align="left"><div id="productIdPop"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.fromDate}:</td>
	 			<td align="left"><div id="fromDatePop"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accComments}:</td>
	 			<td align="left"><input id="commentsPop"></div></td>
    	 	</tr> 	 	    	 	
            <tr>
                <td align="right"></td>
                <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
	 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
    var addsourceProdCate =
    {
        localdata: dataProdCate,
        datatype: "array"
    };
    var addDataAdapterProdCate = new $.jqx.dataAdapter(addsourceProdCate);
    $('#productCategoryIdPop').jqxDropDownList({ selectedIndex: 0,  source: addDataAdapterProdCate, displayMember: "categoryName", valueMember: "productCategoryId"});
    
    var sourceProduct =
    {
        localdata: dataProduct,
        datatype: "array"
    };
    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
    $('#productIdPop').jqxDropDownList({ selectedIndex: 0,  source: dataAdapterProduct, displayMember: "productName", valueMember: "productId"});   
	
	$("#fromDatePop").jqxDateTimeInput({width: '250px', height: '25px'});
	$("#commentsPop").jqxInput({width: '250px', height: '25px'});
	    
    $("#alterpopupWindow").jqxWindow({
        width: 500, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7           
    });
    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { 
        		productCategoryId:$('#productCategoryIdPop').val(),
        		productId:$('#productIdPop').val() ,
        		fromDate:"Date(" + $('#fromDatePop').jqxDateTimeInput('getDate').getTime() + ")",
        		comments:$('#commentsPop').val()        
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });       
</script>