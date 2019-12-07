<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/hrolbius/js/formatCurrency.js"></script>
<#assign taxAuthGeoId = Static["org.ofbiz.base.util.string.FlexibleStringExpander"].expandString(Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default"), null) />
<#assign taxAuthority = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findList("TaxAuthority", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("taxAuthGeoId", taxAuthGeoId), null, null, null, false))>

<script>
var periodTypeArray = [
   					<#if periodTypeList?has_content>
   						{
   							periodTypeId: "",
   							description: "${uiLabelMap.HrolbiusNullValue}"
   						},
   						<#list periodTypeList as periodType>
   						{
   							periodTypeId: "${periodType.periodTypeId?if_exists}",
   							description: "${StringUtil.wrapString(periodType.description?if_exists)}"
   						},
   						</#list>
   					</#if>
                          ];


var taxAuthorityRateTypeArray = [
     							<#if taxAuthorityRateTypeList?has_content>
     								{
     									taxAuthorityRateTypeId: "",
     									description: "${uiLabelMap.HrolbiusNullValue}"
     								},
     								<#list taxAuthorityRateTypeList as taxAuthorityRateType>
     								{
     									taxAuthorityRateTypeId: "${taxAuthorityRateType.taxAuthorityRateTypeId?if_exists}",
     									description: "${StringUtil.wrapString(taxAuthorityRateType.description?if_exists)}"
     								},
     								</#list>
     							</#if>
                                      ];

var mapTaxAuthorityRateType = {
	<#if taxAuthorityRateTypeList?has_content>
		<#list taxAuthorityRateTypeList as taxAuthorityRateType>
			"${taxAuthorityRateType.taxAuthorityRateTypeId?if_exists}": "${StringUtil.wrapString(taxAuthorityRateType.description?if_exists)}",
		</#list>
	</#if>
};

var mapPeriodType = {
		<#if periodTypeList?has_content>
			<#list periodTypeList as periodType>
				"${periodType.periodTypeId?if_exists}": "${StringUtil.wrapString(periodType.description?if_exists)}",
			</#list>
		</#if>
};

</script>

<#assign dataField="[{ name: 'taxAuthorityRateSeqId', type: 'string' },
						 { name: 'name', type: 'string'},
						 { name: 'taxAuthGeoId', type: 'string' },
						 { name: 'taxAuthPartyId', type: 'string' },
						 { name: 'taxAuthorityRateTypeId', type: 'string'},
						 { name: 'fromValue', type: 'string'},
						 { name: 'thruValue', type: 'string'},
						 { name: 'taxPercentage', type: 'string'},
						 { name: 'fromDate', type: 'date', other : 'Timestamp'},
						 { name: 'thruDate', type: 'date', other : 'Timestamp'},
						 { name: 'periodTypeId', type: 'string'}
						 ]
						 "/>
						 
<#assign columnlist="{ text: '${uiLabelMap.HrolbiusTaxAuthorityRateSeqId}', datafield: 'taxAuthorityRateSeqId', width: 100, hidden: true},
					 { text: '${uiLabelMap.firstName}', datafield: 'name', width: 100, filterable : true},
					 { text: '${uiLabelMap.taxAuthGeoId}', datafield: 'taxAuthGeoId', width: 150, hidden: true},
					 { text: '${uiLabelMap.taxAuthPartyId}', datafield: 'taxAuthPartyId', width: 100, hidden: true},
					 { text: '${uiLabelMap.taxAuthorityRateTypeId}', datafield: 'taxAuthorityRateTypeId', width: 160 , filterable : true ,filtertype : 'checkedlist',columntype: 'dropdownlist',
						 cellsrenderer : function(row, column, value){
							value?value=mapTaxAuthorityRateType[value]:value='';
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + value + '</div>';
						},
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						        editor.jqxDropDownList({source: taxAuthorityRateTypeArray ,valueMember: 'taxAuthorityRateTypeId', displayMember: 'description' });
					    },
					    createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(taxAuthorityRateTypeArray), displayMember: 'description', valueMember: 'taxAuthorityRateTypeId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
	                            	for(var i = 0; i < taxAuthorityRateTypeArray.length; i++){
										if(value == taxAuthorityRateTypeArray[i].taxAuthorityRateTypeId){
											return taxAuthorityRateTypeArray[i].description; 
										}
									}
				                } });
	    		        	editor.jqxDropDownList('checkAll');
	                    }
					 },
					 { text: '${uiLabelMap.HrolbiusFromValue}', datafield: 'fromValue', width: 120, filterable : true, cellsalign: 'right',columntype: 'numberinput' ,inputMode: 'simple',
						 
						 cellsrenderer: function (row, column, value) {
			 		 		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			 		 		 if (data && data.fromValue){
			 		 		 	return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(data.fromValue) + \"</div>\";
			 		 		 }
		 		 		 },
						 
						 validation: function (cell, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
							if(value <0)
								 return { result: false, message: '${uiLabelMap.HrolbiusCannotBeNegativeValue}'};
							else
								{
								if(value < data.thruValue)
									return true;
								else
									{
									if(data.thruValue == 0 && value > 0 )
										{
											return true;
										}
									else
										return { result: false, message: '${uiLabelMap.HrolbiusCannotGreaterThanEndValue}'};
									}
								}
                 	} },
					 { text: '${uiLabelMap.HrolbiusThruValue}', datafield: 'thruValue', width: 120, cellsalign: 'right', filterable : true ,columntype: 'numberinput' ,inputMode: 'simple', 	
                 		cellsrenderer: function (row, column, value) {
			 		 		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			 		 		 if (value>0  && data.thruValue){
			 		 		 	return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(data.thruValue) + \"</div>\";
			 		 		 }
			 		 		 else 
			 		 			return '';
		 		 		 }, 
                 		
                 		validation: function (cell, value) {
						 var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
						 
						 if(value > data.fromValue) 
							 return true;
						 else
							 {
							 if( value ==0 )
								 return true;
							 else
								 return { result: false, message: '${uiLabelMap.HrolbiusRequiredValueGreatherValue}'};
							 }
						 
                 	} },
					 { text: '${uiLabelMap.FormFieldTitle_taxPercentage}', datafield: 'taxPercentage', width: 100, filterable : true, cellsalign: 'right', columntype: 'numberinput' ,inputMode: 'simple', spinButtons: true, 	
                 	 validation: function (cell, value) {
	                      	if (value < 0 || value >100) {
	                         	return { result: false, message: '${uiLabelMap.HrolbiusTaxRule}'};
	                     	}
	                     	return true;
	                } },
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate',columntype: 'datetimeinput', width: '193', filtertype: 'range', filterable: true, cellsformat:'dd/MM/yyyy', cellsalign: 'right',
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
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate',columntype: 'datetimeinput', width: '193', filtertype: 'range', filterable: true, cellsformat:'dd/MM/yyyy', cellsalign: 'right', 
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
					 { text: '${uiLabelMap.FormFieldTitle_periodTypeId}', datafield: 'periodTypeId', width: 130 , filterable : true ,filtertype : 'checkedlist',columntype: 'dropdownlist',
						 cellsrenderer : function(row, column, value){
							value?value=mapPeriodType[value]:value='';
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + value + '</div>';
						},
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						        editor.jqxDropDownList({source: periodTypeArray ,valueMember: 'periodTypeId', displayMember: 'description' });
					    },
					    createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(periodTypeArray), displayMember: 'description', valueMember: 'periodTypeId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
	                            	for(var i = 0; i < periodTypeArray.length; i++){
										if(value == periodTypeArray[i].periodTypeId){
											return periodTypeArray[i].description; 
										}
									}
				                } });
	    		        	editor.jqxDropDownList('checkAll');
	                    }
					 }
					 
					 
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
defaultSortColumn="name" 
addType="popup"
alternativeAddPopup="alterpopupWindow" 
addColumns="taxAuthGeoId;taxAuthPartyId;taxAuthorityRateTypeId;name;fromValue;thruValue;taxPercentage;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);periodTypeId" addrefresh="true"
editColumns="taxAuthorityRateSeqId;taxAuthGeoId;taxAuthPartyId;taxAuthorityRateTypeId;name;fromValue;thruValue;taxPercentage;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);periodTypeId" 
deleteColumn="taxAuthorityRateSeqId" 

url="jqxGeneralServicer?sname=JQgetListTaxAuthorityRatePayroll"
removeUrl="jqxGeneralServicer?sname=deleteTaxAuthorityRatePayroll&jqaction=D"  
updateUrl="jqxGeneralServicer?sname=UpdateTaxAuthorityRatePayroll&jqaction=U"
createUrl="jqxGeneralServicer?sname=createTaxAuthorityRatePayroll&jqaction=C"
/>





<div id="alterpopupWindow" style="display : none;">
     <div>${uiLabelMap.HrolbiusAddTaxAuthorityRatePayroll}</div>
     <div style="overflow: hidden;">
         <form id="formAdd" class="form-horizontal">
         

         <div class="row-fluid no-left-margin">
             <label class="span4" style="text-align:right;"> ${uiLabelMap.firstName} </label>
             <div class="span8" style="margin-bottom: 10px;">
              <input type="text" id="name"/>
             </div>
         </div>
         
              <input type="hidden" id="taxAuthGeoId" value="${taxAuthGeoId?if_exists}" />
              <input type="hidden" id="taxAuthPartyId" value="${taxAuthority.taxAuthPartyId?if_exists}" />
              
         <div class="row-fluid no-left-margin">
             <label class="span4" style="text-align:right;"> ${uiLabelMap.taxAuthorityRateTypeId} </label>
             <div class="span8" style="margin-bottom: 10px;">
             	<div id='taxAuthorityRateTypeId'></div>
             </div>
         </div>
         
         
         <div class="row-fluid no-left-margin">
             <label class="span4" style="text-align:right;"> ${uiLabelMap.HrolbiusFromValue} </label>
             <div class="span8" style="margin-bottom: 10px;">
             	<input type="text" id="fromValue"/>
             </div>
         </div>
         
        
         
         <div class="row-fluid no-left-margin">
             <label class="span4" style="text-align:right;"> ${uiLabelMap.HrolbiusThruValue} </label>
             <div class="span8" style="margin-bottom: 10px;">
             	<input type="text" id="thruValue"/>
             </div>
         </div>
         
         
         <div class="row-fluid no-left-margin">
             <label class="span4" style="text-align:right;"> ${uiLabelMap.FormFieldTitle_taxPercentage}</label>
             <div class="span8" style="margin-bottom: 10px;">
             	<div id='taxPercentage'></div>
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
         
         <div class="row-fluid no-left-margin">
         <label class="span4" style="text-align:right;"> ${uiLabelMap.FormFieldTitle_periodTypeId} </label>
         <div class="span8" style="margin-bottom: 10px;">
         	<div id='periodTypeId'></div>
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
    var source = taxAuthorityRateTypeArray;
    $("#name").jqxInput({ width: '193px', height: '25px'});
    $("#taxAuthPartyId").jqxInput({ width: '193px', height: '25px'});
    $("#taxAuthGeoId").jqxInput({ width: '193px', height: '25px'});
    $("#fromValue").jqxInput({ width: '193px', height: '25px'});
    $("#thruValue").jqxInput({ width: '193px', height: '25px'});
    $("#taxAuthorityRateTypeId").jqxDropDownList({ source: taxAuthorityRateTypeArray,displayMember: "description",
    	 valueMember : "taxAuthorityRateTypeId", selectedIndex: 7, width: '198', height: '30'});
    $("#fromDate").jqxDateTimeInput({width: '198px', height: '30px'});
    var source = periodTypeArray;
    $("#periodTypeId").jqxDropDownList({ source: periodTypeArray,displayMember: "description",
    	 valueMember : "periodTypeId", selectedIndex: 4, width: '198', height: '30'});
    $("#thruDate").jqxDateTimeInput({value: null,width: '198px', height: '30px',allowNullDate: true});
    $("#taxPercentage").jqxNumberInput({ width: '198px', height: '25px', digits: 3, inputMode: 'simple', symbolPosition: 'left', spinButtons: true });      
	$('#formAdd').jqxValidator({
	    rules: [
	           { input: '#name',  message: '${uiLabelMap.HrolbiusRequiredNotNull}', action: 'keyup, blur', rule: 'required' },
	           { input: '#taxAuthPartyId', message: '${uiLabelMap.HrolbiusRequiredNotNull}', action: 'keyup, blur', rule: 'required' },
	           { input: '#taxAuthGeoId',message: '${uiLabelMap.HrolbiusRequiredNotNull}', action: 'keyup, blur', rule: 'required' },
	           { input: '#taxPercentage',message: '${uiLabelMap.HrolbiusTaxRule}', action: 'keyup, blur', rule: function(){
	        	   var taxPercentage = $('#taxPercentage').val();
	        	   if(taxPercentage < 0 || taxPercentage>100) return false;
	        	   else return true;
	           } },
	           { input: '#fromValue',message: '${uiLabelMap.HrolbiusInvalidValue}', action: 'keyup, blur', rule: function(){
	        	   var fromValueRaw = document.getElementById('fromValue').value;
	        	   if(fromValueRaw < 0 ) return false;
	        	   if(isNaN(fromValueRaw) || !fromValueRaw || 0 === fromValueRaw.length ) return false;
	        	   else return true;
	           } },
	           { input: '#thruValue',message: '${uiLabelMap.HrolbiusInvalidValue}', action: 'keyup, blur', rule: function(){
            	   var fromValueRaw = document.getElementById('fromValue').value;
            	   var toValueRaw = document.getElementById('thruValue').value;
            	   var fromValue = parseInt(fromValueRaw);
            	   var toValue = parseInt(toValueRaw);
            	   if(isNaN(fromValueRaw) || isNaN(toValueRaw)) return false;
            	   else{
            	   if((0 === toValueRaw.length) && fromValue) return true;
            	   else
            		   {
		            	   if(+fromValue >= +toValue){
		            	   return false;
		            	   } else
		            	   return true;
            		   }}
               }},
               { input: '#fromDate',message: '${uiLabelMap.HrolbiusRequiredNotNull}', action: 'keyup, blur', rule: function(){
            	   var fromDate = $('#fromDate').val();
	        	   var thruDate = $('#thruDate').val();
	        	   if(fromDate && thruDate && fromDate >= thruDate)
	        		   {
	        		   return false ;
	        		   }
	        	   else return true;
	           } },
               { input: '#thruDate',message: '${uiLabelMap.HrolbiusRequiredValueGreatherFromDate}', action: 'keyup, blur', rule: function(){
            	   var fromDate = $('#fromDate').val();
	        	   var thruDate = $('#thruDate').val();
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
	        	  taxAuthorityRateSeqId:$('#taxAuthorityRateSeqId').val(), 
	        	  taxAuthGeoId:$('#taxAuthGeoId').val(),
	        	  taxAuthPartyId:$('#taxAuthPartyId').val(),
	        	  taxAuthorityRateTypeId:$('#taxAuthorityRateTypeId').val(),
	              name:$('#name').val(),
	              fromValue:$('#fromValue').val(),
	              thruValue:$('#thruValue').val(),
	              taxPercentage:$('#taxPercentage').val(),
	              fromDate:convertDate($('#fromDate').val()),
	              thruDate:convertDate($('#thruDate').val()),
	              periodTypeId:$('#periodTypeId').val()
	          };
	          $("#jqxgrid").jqxGrid('addRow', null, row, "first"); 
		      $("#jqxgrid").jqxGrid('clearSelection');                        
		      $("#jqxgrid").jqxGrid('selectRow', 0);  
		      $("#alterpopupWindow").jqxWindow('close');
	    }); 
	});
	$('#alterpopupWindow').jqxWindow({width: 480, height:430, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:'olbius'});       
    
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



