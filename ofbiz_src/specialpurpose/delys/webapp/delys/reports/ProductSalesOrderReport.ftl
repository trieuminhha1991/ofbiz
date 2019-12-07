
<@jqGridMinimumLib/>

<div id="jqxPanel" style="width:100%;">
	<table style="margin:0 auto;margin-top:10px;width:100%;position:relative;">
		<!--<tr>
			<td><div style="width:100px;">${uiLabelMap.BiOlbiusProductId}</div></td> 
			<td>
				<div><input type="text"  id="productId"> </div>
			</td>
		</tr>-->
		<tr>
			<td width="30">${uiLabelMap.BiOlbiusFromDate}</td>
			<td>
				<div id="fromDate" style="display:inline-block; vertical-align:top"></div>
			 	<button name="clear" value="clear" id="clear1" style="display:inline-block; border-radius:8px;"> Clear</button>
			</td>
		</tr>
		<tr>
			<td width="30">${uiLabelMap.BiOlbiusThruDate}</td>
			<td>
		        <div id="thruDate" style="display:inline-block; vertical-align:top"></div>
		        <button name="clear" value="clear" id="clear2" style="display:inline-block; border-radius:8px;"> Clear</button>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
		       <input type="button" value="${uiLabelMap.commonSearch}" id='jqxButton' />
		    </td>
		</tr>
	</table>
</div>	 	

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var alterData = new Object();
	$("#fromDate").jqxDateTimeInput({ width: '175px', height: '25px',  formatString: 'dd/MM/yyyy', theme:theme});
	$("#fromDate").jqxDateTimeInput('val','');
	$("#thruDate").jqxDateTimeInput({ width: '175px', height: '25px',  formatString: 'dd/MM/yyyy ', theme:theme});
	$("#thruDate").jqxDateTimeInput('val','');
	$("#jqxButton").jqxButton({ width: '154', height: '30', theme:theme});
	//$("#productId").jqxComboBox({source: products, multiSelect: true , width: 350, height: 25, displayMember: "internalName", valueMember: "productId"});

	//$.jqx
</script>	
<style type="text/css">
	#jqxPanel td{
			padding:5px;
	}	 
</style>	

<#assign  dataField="[{name:'productId',type:'String'},{name:'internalName', type:'String'}, {name:'quantity', type:'number'},{name:'productType', type:'String'},{name:'extTaxAmount',type:'number'},{name:'extGrossAmount',type:'number'},{name:'productStoreId',type:'String'}]"/>
<#assign columnlist="{ 	text: '${uiLabelMap.BiOlbiusProductId}', dataField: 'productId', width: 150},
						{text:'${uiLabelMap.BiOlbiusInternalName}',dataField:'internalName'},
						{text:'${uiLabelMap.BiOlbiusProductType}',dataField:'productType'},
						{text:'${uiLabelMap.BiOlbiusProductStoreId}',dataField:'productStoreId'},
						{text:'${uiLabelMap.BiOlbiusQuantity}', dataField:'quantity'},
						{text:'${uiLabelMap.BiOlbiusExtTaxAmount}', dataField:'extTaxAmount',cellsrenderer: function(row, column, value) {
					 		var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
					 		var str = \"<div style='text-align:right; width:95%; margin-left:0!important'>\";
							str += formatcurrency(value,data.currencyUom);
							str += \"</div>\";
							return str;
					 	}},
						{text:'${uiLabelMap.BiOlbiusExtGrossAmount}', dataField:'extGrossAmount',cellsrenderer: function(row, column, value) {
					 		var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
					 		var str = \"<div style='text-align:right; width:95%; margin-left:0!important'>\";
							str += formatcurrency(value,data.currencyUom);
							str += \"</div>\";
							return str;
					 	}}" />
                        
<script type="text/javascript">
	
	$("#jqxButton").on('click', function () {
		alterData= new Object();
		alterData.pagenum = "0";
	    alterData.pagesize = "20";
	    alterData.noConditionFind = "Y";
	    alterData.conditionsFind = "N";
	    //alterData.fromDate="";
	    //alterData.salesProductList="";
	    // /alterData.thruDate="";
        if($('#fromDate').val() != null && $('#fromDate').val()){
        	alterData.fromDate = $('#fromDate').val();
    	}
    	if($('#thruDate').val() != null && $('#thruDate').val()){
        	alterData.thruDate = $('#thruDate').val();
        }
        //var selectedItems = $('#productId').jqxComboBox('getSelectedItems');
       // var salesProductList= new Array();
        //if(selectedItems != null && selectedItems){
        	//for(i=0; i < selectedItems.length;i++){
        	//	salesProductList[i] = selectedItems[i].value;
        	//}
      // }else{
        //	salesProductList = [];
        //}
        //alterData.salesProductList=salesProductList;
        if($("#productId").val()!=null && $("#productId").val()){
        	alterData.search= $('#productId').val();
        	//console.log(alterData);
        }else{
        	alterData.search=null;
        }
		$('#jqxgrid').jqxGrid('updatebounddata');
    });
       $(window).bind('beforeunload',function(){
       
      	 <#if session.getAttribute("thruDate")?has_content>
      	 	      	 	<#assign tr= session.setAttribute("thruDate","") />
 		</#if>
 		
 		 <#if session.getAttribute("fromDate")?has_content>
      	 	      	 	<#assign tf= session.setAttribute("fromDate","") />
 		</#if>
	});     
	$("#clear1").on('click',function(){
		$("#fromDate").jqxDateTimeInput('val','');
		 <#if session.getAttribute("fromDate")?has_content>
      	 	      	 	<#assign td= session.setAttribute("fromDate","") />
 		</#if>
	});
	$("#clear2").on('click',function(){
		$("#thruDate").jqxDateTimeInput('val','');
		<#if session.getAttribute("thruDate")?has_content>
      	 	      	 	<#assign tu= session.setAttribute("thruDate","") />
 		</#if>
	});          
</script>                  
                        
  <@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" selectionmode="multiplerowsextended"
		 showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" addrefresh="true" editrefresh="true"
		 url="jqxGeneralServicer?sname=jqxBiSalesOrderProduct&hasrequest=Y"
		 /> 
		 

