<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/delys/images/js/import/notify.js"></script>
<script>

var productArray = [
      					<#if productList?has_content>
      						<#list productList as productListData>
      						{
      							productId: "${productListData.productId?if_exists}",
      							internalName: "${StringUtil.wrapString(productListData.internalName?if_exists)}"
      						},
      						</#list>
      					</#if>
                             ];

var mainProductArray = [
                    <#if mainProductList?has_content>
                    <#list mainProductList as mainProductListData>
                    {
                    	productId: "${mainProductListData.productId?if_exists}",
                    	fromDate: "${mainProductListData.fromDate?if_exists}",
                    	thruDate: "${mainProductListData.thruDate?if_exists}"
                    },
                    </#list>
                    </#if>
                    ];

//for (var i = 0 ; i < productArray.length ; i++ )
//	{
//		for (var j = 0 ; j < mainProductArray.length ; j++ )
//			{
//			if(productArray[i].productId == mainProductArray[j].productId)
//				productArray.splice(i,1);
//			}
//	}


</script>
<#assign dataField="[{ name: 'mainProductId', type: 'string' },
						 { name: 'productId', type: 'string'},
						 { name: 'internalName', type: 'string'},
						 { name: 'fromDate', type: 'date', other : 'Timestamp'},
						 { name: 'thruDate', type: 'date', other : 'Timestamp'}
						 ]
						 "/>
						 
 <#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.No)}',sortable: false, filterable: false, editable: false,
			        groupable: false, draggable: false, resizable: false,
			        datafield: '', columntype: 'number', width: 50,
			        cellsrenderer: function (row, column, value) {
			            return \"<div style='margin-top: 3px; text-align: left;  '>\" + (value + 1)+  \"</div>\";} },
					 { text: '${uiLabelMap.accProductId}',  datafield: 'productId', width: 320, filterable : true, editable: false,},
					 { text: '${uiLabelMap.accProductName}', datafield: 'internalName',editable: false,},
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate',columntype: 'datetimeinput', width: '200', filtertype: 'range', filterable: true, cellsformat:'dd/MM/yyyy', cellsalign: 'right',
		           		 validation: function (cell, value) {
							 var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
							 if (value && data.thruDate && data.thruDate <= value) {
								 return { result: false, message: '${uiLabelMap.TimeBeginAfterTimeEnd}'};
							 }
							 else 
								 return true;
		           		 },
		           		 cellvaluechanging: function (row, datafield, columntype, oldvalue, newvalue) {
							 if(!newvalue){
								 $('#jqxgrid').jqxGrid('setcellvalue', row, datafield, 0);
								 return '';
							 }
						     return newvalue;
						 }
						 
					 },
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate',columntype: 'datetimeinput', width: '200', filtertype: 'range', filterable: true, cellsformat:'dd/MM/yyyy', cellsalign: 'right', 
						 validation: function (cell, value) {
							 var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
							 if (value && data.fromDate && data.fromDate >= value) {
								 return { result: false, message: '${uiLabelMap.TimeBeginAfterTimeEnd}'};
							 }
							 else 
								 return true;
		           		 },
						 cellvaluechanging: function (row, datafield, columntype, oldvalue, newvalue) {
							 if(!newvalue){
								 $('#jqxgrid').jqxGrid('setcellvalue', row, datafield, 0);
								 return '';
							 }
						     return newvalue;
						 }
					 },
					 "/>
					 
<@jqGrid 
filtersimplemode="true"
filterable="true"
dataField=dataField 
columnlist=columnlist
showtoolbar="true"  
addrow="true"
editable="true"
editmode="selectedcell"
deleterow="true"
clearfilteringbutton="true"
addType="popup"
alternativeAddPopup="alterpopupWindow" 
editColumns="mainProductId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" 
deleteColumn="mainProductId"
addColumns="productId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);" 
addrefresh="true"
removeUrl="jqxGeneralServicer?sname=jqDeleteMainProductConfig&jqaction=D"  
updateUrl="jqxGeneralServicer?sname=jqUpdateMainProductConfig&jqaction=U"
createUrl="jqxGeneralServicer?sname=jqCreateMainProductConfig&jqaction=C"
url="jqxGeneralServicer?sname=jqGetListMainProductConfig"
/>





<div id="alterpopupWindow" style="display : none;">
     <div>${uiLabelMap.addMainProductConfig}</div>
     <div style="overflow: hidden;">
         <form id="formAdd" class="form-horizontal">
         

     <div class="row-fluid no-left-margin">
         <label class="span4" style="text-align:right;"> ${uiLabelMap.accProductName} </label>
         <div class="span8" style="margin-bottom: 10px;">
         	<div id='productInternalName'></div>
         </div>
     </div>
         
              
         <div class="row-fluid no-left-margin">
             <label class="span4" style="text-align:right;"> ${uiLabelMap.accProductId} </label>
             <div class="span8" style="margin-bottom: 10px;">
             	<div id='productId'></div>
             </div>
         </div>
         
         
         
         <div class="row-fluid no-left-margin">
             <label class="span4" style="text-align:right;"> ${uiLabelMap.CommonFromDate} </label>
             <div class="span8" style="margin-bottom: 10px;">
             	<div id='fromDate'></div>
             </div>
         </div>	
         
         
         
         <div class="row-fluid no-left-margin">
             <label class="span4" style="text-align:right;"> ${uiLabelMap.CommonThruDate} </label>
             <div class="span8" style="margin-bottom: 10px;">
             	<div id='thruDate'></div>
             </div>
         </div>	
         
         
         
         <div class="control-group no-left-margin" style="float:right">
         <div class="" style="width:166px;margin:0 auto;">
          <button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius" style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
          <button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius  btn-danger" style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
         </div>
        </div>
         
         
         
             
             
          
             
             
         </form>
     </div>
</div>

<script>
// Create theme
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
$.jqx.theme = 'olbius';  
theme = $.jqx.theme;

$(document).ready(function () {
//    var source = productArray;
    $("#productInternalName").jqxComboBox({ source: productArray, displayMember: "internalName",
    	 valueMember : "productId", selectedIndex: -1, width: '230', height: '30'});
    $("#productId").jqxComboBox({ source: productArray, displayMember: "productId",
    	valueMember : "productId", selectedIndex: -1, width: '230', height: '30'});
    $("#fromDate").jqxDateTimeInput({width: '230', height: '30px'});
    $("#thruDate").jqxDateTimeInput({value: null,width: '230', height: '30px',allowNullDate: true});
	$('#formAdd').jqxValidator({
	    rules: [
	           { input: '#productId',  message: '${uiLabelMap.HrolbiusRequiredNotNull}', action: 'keyup, blur', rule: function(){
	        	var pId = $('#productId').val();
	        	var temp = 0;
	        	for(var i = 0 ; i < productArray.length ; i++)
	        		{
	        			if(pId == productArray[i].productId)
	        				{
	        				temp = 1;
	        				break;
	        				}
	        		}
	        	if( temp == 1) 
	        		return true; 
	        	else 
	        		return false;
	           } },
               { input: '#fromDate',message: '${uiLabelMap.HrolbiusRequiredNotNull}', action: 'keyup, blur', rule: function(){
            	   var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
	        	   var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
	        	   if(fromDate && thruDate && fromDate >= thruDate)
	        		   {
	        		   return false ;
	        		   }
	        	   else return true;
	           } },
               { input: '#thruDate',message: '${uiLabelMap.HrolbiusRequiredValueGreatherFromDate}', action: 'keyup, blur', rule: function(){
	        	   var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
	        	   var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
	        	   if(fromDate && thruDate && fromDate >= thruDate)
	        		   {
	        		   return false ;
	        		   }
	        	   else return true;
	           } },
	           ]});
	
	  $('#formAdd').on('validationError', function (event) {
	  });
	
	  $('#formAdd').on('validationSuccess', function (event) {
		     var row;
	          row = { 
	        	  productId:$('#productId').val(),
	              fromDate:convertDate($('#fromDate').val()),
	              thruDate:convertDate($('#thruDate').val())
	          };
	          $("#jqxgrid").jqxGrid('addRow', null, row, "first"); 
		      $("#jqxgrid").jqxGrid('clearSelection');                        
		      $("#jqxgrid").jqxGrid('selectRow', 0);
		      $("#alterpopupWindow").jqxWindow('close');
	    }); 
	});


	$('#productInternalName').on('change', function (event) {
		var currentDate = new Date();
	    var day = currentDate.getDate();
	    var month = currentDate.getMonth();
	    var year = currentDate.getFullYear();
	    $('#fromDate').jqxDateTimeInput('setDate', new Date(year, month, day));
	    $('#thruDate').jqxDateTimeInput('setDate', null);
		var index = $("#productInternalName").jqxComboBox('getSelectedIndex'); 
	    $("#productId").jqxComboBox({selectedIndex: index });
	    var tempValue = $("#productInternalName").jqxComboBox('getSelectedItem');
	    for(var i = 0 ; i< mainProductArray.length ; i++)
	    	{
	    	if (tempValue.value == mainProductArray[i].productId)
	    		{
	    		$('#fromDate').jqxDateTimeInput('setDate', mainProductArray[i].fromDate);
	    	    $('#thruDate').jqxDateTimeInput('setDate', mainProductArray[i].thruDate);
	    	    $("#productInternalName").notify("${StringUtil.wrapString(uiLabelMap.mainProductAvaiable)}", "info");
	    		}
	    	}
	});


	$('#productId').on('change', function (event) {
		var currentDate = new Date();
	    var day = currentDate.getDate();
	    var month = currentDate.getMonth();
	    var year = currentDate.getFullYear();
	    $('#fromDate').jqxDateTimeInput('setDate', new Date(year, month, day));
	    $('#thruDate').jqxDateTimeInput('setDate', null);
		var index = $("#productId").jqxComboBox('getSelectedIndex'); 
	    $("#productInternalName").jqxComboBox({selectedIndex: index }); 
	    var tempValue = $("#productInternalName").jqxComboBox('getSelectedItem');
	    for(var i = 0 ; i< mainProductArray.length ; i++)
	    	{
	    	if (tempValue.value == mainProductArray[i].productId)
	    		{
	    		$('#fromDate').jqxDateTimeInput('setDate', mainProductArray[i].fromDate);
	    	    $('#thruDate').jqxDateTimeInput('setDate', mainProductArray[i].thruDate);
	    	    $("#productInternalName").notify("${StringUtil.wrapString(uiLabelMap.mainProductAvaiable)}", "info");
	    		}
	    	}
	});
	
	


	$('#alterpopupWindow').jqxWindow({width: 480, height:280, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:'olbius'});       
    
	$("#alterSave").click(function () {
		
		
		
		  $('#formAdd').jqxValidator('validate');
		  }); 
                
function convertDate(date) {
	if (!date) {
		return null;
	}
	var thisDate = date.split("/");
	var d = new Date(thisDate[2] + "-" + thisDate[1] + "-" + thisDate[0]);
    var n = d.getTime();
    return n;
}
</script>



