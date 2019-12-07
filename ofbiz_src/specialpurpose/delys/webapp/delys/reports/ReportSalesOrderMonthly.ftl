<@jqGridMinimumLib/>
<style>
[id^="columntablejqxgrid"], [id*=" columntablejqxgrid"] {
	z-index:10 !important;
 	min-height:25px!important;
}
.jqx-grid-column-header-olbius,.jqx-widget-header-olbius {
	min-height:25px!important;
}

.jqx-grid-header {
	min-height: 50px !important;
}
</style>
<div id="screenlet_1_col" class="widget-body-inner">
	<div class="widget-body">
		<form class="form-horizontal" name="getInforDepartment" id="getInforDepartment">
			<div class="row-fluid">
				<div class="control-group no-left-margin ">
					<label class="">
						<label for="getInforDepartment_chanelsales" class="control-label" id="getInforDepartment_chanelsales_title">${uiLabelMap.DASalesChannel}</label>
					</label>
					<div class="controls">
						<span class="ui-widget">
							<select name="chanelsales" id="getInforDepartment_chanelsales" size="1">
								<option  value ="DELYS_SALESSUP_GT">${uiLabelMap.DAGTChannel}</option>
								<option  value ="DELYS_SALESSUP_MT">${uiLabelMap.DAMTChannel}</option>
							</select>
						</span>
					</div>
				</div>
				
				
				<div class="control-group no-left-margin ">
					<label class="">
						<label for="getInforDepartment_department" class="control-label" id="getInforDepartment_department_title">${uiLabelMap.OlapDepartment}</label>
					</label>
					<div class="controls">
						<span class="ui-widget">
							<select name="department" id="getInforDepartment_department" size="1">
							</select>
						</span>
					</div>
				</div>
				
				<div class="control-group no-left-margin span12">
					<label class="">
						<label for="getInforDepartment_department" class="control-label" id="getInforDepartment_department_title">${uiLabelMap.CommonMonth}</label>
					</label>
					<div class="controls">
						<input type="text" style="margin-bottom:0px;" name="month" class="input-mini" id="month" />
						<label style="display: inline;">${uiLabelMap.CommonYear}</label>
						<input type="text" style="margin-bottom:0px;"class="input-mini" id="year" name="year"/>
					</div>					
				</div>
				<div class="control-group no-left-margin ">
					  <label class="">
					&nbsp;  </label>
					  <div class="controls">
					    <button id="staticButton" type="button" class="btn btn-small btn-primary" name="submitButton" ><i class="icon-ok"></i>${uiLabelMap.DAViewSalesStatement}</button>
					  
					  </div>
				  </div>
			</div>
		</form>
	</div>
</div>
	<#assign currentYear = currentDateTime.get(Static["java.util.Calendar"].YEAR)>
	<#assign currentMonth = currentDateTime.get(Static["java.util.Calendar"].MONTH) + 1>
	<#assign minYear = currentYear-15>
	<#assign maxYear = currentYear+15>
	<#assign columngroups = "
  		 { text: '${uiLabelMap.AccountingFixedAssetProducts}', align: 'center', name: 'Product' }" />
	<#assign dataField= "[{name:'saleman',type:'String'},{name:'totalOrder',type:'String'},">
	<#assign columnlist="{text: 'STT', sortable: false, filterable: false, editable: false,
	                      groupable: false, draggable: false, resizable: false,
	                      datafield: '', columntype: 'number', width: 50,
	                      cellsrenderer: function (row, column, value) {
	                          return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
                      	}
                  },{text: '${uiLabelMap.EmployeeName}', datafield: 'saleman',width:150, cellsalign: 'left', editable: false,
                  			aggregates: [{ '<b>${uiLabelMap.Total}</b>': 
			                   function (aggregatedValue, currentValue, column, record) {
			                    return aggregatedValue + currentValue ;
			               		 }
          					 }],
							aggregatesrenderer: 
	        					function (aggregates, column, element, summaryData1){
		                           var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%;'>\";
		                             $.each(aggregates, function (key, value) {
		                                renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: center; overflow: hidden;\">' 
		                                 + '<b>${uiLabelMap.Total}<\\/b>' + '<br>' 
		                                 + \"&nbsp;\" + '</div>';
		                               });                          
		                             renderstring += \"</div>\";
		                            return renderstring; 
	                           }
                  
                  },
				    {text:'${uiLabelMap.OrderOrderNumber}',datafield:'totalOrder',width:150,cellsalign: 'center', filterable:false,editable: false,
						 	aggregates: [{ '<b></b>': 
			                   function (aggregatedValue, currentValue, column, record) {
			                    return aggregatedValue + currentValue ;
			               		 }
          					 }],
							aggregatesrenderer: 
	        					function (aggregates, column, element, summaryData1){
		                           var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%;'>\";
		                             $.each(aggregates, function (key, value) {
		                                renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: center; overflow: hidden;\">' 
		                                 + value  
		                                 + \"&nbsp;\" + '</div>';
		                               });                          
		                             renderstring += \"</div>\";
		                            return renderstring; 
	                           }
						 }"
	 />
	 
	<#list productList as item>
		<#assign dataField=dataField+"{name:'${item.productId}',type:'String'},"/>
		<#if item.internalName?has_content>
			<#assign name=item.internalName/>
		<#else>
			<#assign name=item.productId/>	
		</#if>
		<#assign columnlist=columnlist+",{text:'${name}',columngroup:'Product',width:120,datafield:'${item.productId}',cellsalign: 'center',filterable:false, editable: false,
							aggregates: [{ '<b></b>': 
				                   function (aggregatedValue, currentValue, column, record) {
				                    return aggregatedValue + currentValue ;
				               		 }
	          					 }],
							aggregatesrenderer: 
	        					function (aggregates, column, element, summaryData1){
		                           var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%;'>\";
		                             $.each(aggregates, function (key, value) {
		                                renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: center; overflow: hidden;\">' 
		                                 + value  
		                                 + \"&nbsp;\" + '</div>';
		                               });                          
		                             renderstring += \"</div>\";
		                            return renderstring; 
	                           }
		}"/>
	</#list>
	<#assign columnlist=columnlist+",{text:'${uiLabelMap.totalAmount}',datafield:'grandTotal',width:150,cellsalign: 'center', editable: false,filterable:false,cellsrenderer: function(row, column, value) {
					 		var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
					 		var str = \"<div style='text-align:right; width:95%; margin-left:0!important'>\";
							str += formatcurrency(value,data.currencyUom);
							str += \"</div>\";
							return str;
					 	},
						 	aggregates: [{ '<b></b>': 
				                   function (aggregatedValue, currentValue, column, record) {
				                    return aggregatedValue + currentValue ;
				               		 }
	          					 }],
							aggregatesrenderer: 
	        					function (aggregates, column, element, summaryData1){
		                           var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%;'>\";
		                             $.each(aggregates, function (key, value) {
		                                renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' 
		                                 + formatcurrency(value)
		                                 + \"&nbsp;\" + '</div>';
		                               });                          
		                             renderstring += \"</div>\";
		                            return renderstring; 
	                           }
					 	}" />
	<#assign dataField=dataField+"{name:'grandTotal',type:'String'}]" />
<script type="text/javascript">
	$(document).ready(function (){
		jQuery('#month').ace_spinner({value:${currentMonth},min:1,max:12,step:1, icon_up:'icon-caret-up', icon_down:'icon-caret-down'});
		jQuery('#year').ace_spinner({value:${currentYear},min:${minYear},max:${maxYear},step:1, icon_up:'icon-caret-up', icon_down:'icon-caret-down'});
	});
	
	var alterData= new Object();
	$("#staticButton").on('click', function () {
		alterData.pagenum = "0";
	    alterData.pagesize = "20";
	    alterData.noConditionFind = "Y";
	    alterData.conditionsFind = "N";
        if($('#getInforDepartment_chanelsales').val() != null && $('#getInforDepartment_chanelsales').val()){
        	alterData.chanelsales = $('#getInforDepartment_chanelsales').val();
    	}
    	if($('#getInforDepartment_department').val() != null && $('#getInforDepartment_department').val()){
        	alterData.department = $('#getInforDepartment_department').val();
        }
        if($("#month").val()!=null && $("#month").val()){
        	alterData.month= $('#month').val();
        	text= "${uiLabelMap.CommonMonth}";
        	$("#toolbarcontainer>h4").empty();
        	$("#toolbarcontainer>h4").append("${uiLabelMap.BiSalesOrderReport}"+" "+text.toLowerCase()+" "+$('#month').val());
        }
        
         if($("#year").val()!=null && $("#year").val()){
        	alterData.year= $('#year').val();
        	text= "${uiLabelMap.CommonYear}";
        	$("#toolbarcontainer>h4").append(" "+text.toLowerCase()+" "+$('#year').val());
        }
		$('#jqxgrid').jqxGrid('updatebounddata');
		alterData= new Object();
		
		<#if session.getAttribute("department")?has_content>
      	 	      	 	<#assign tr= session.setAttribute("department","") />
 		</#if>
 		<#if session.getAttribute("month")?has_content>
      	 	      	 	<#assign tr= session.setAttribute("month","") />
 		</#if>
 		<#if session.getAttribute("year")?has_content>
      	 	      	 	<#assign tr= session.setAttribute("year","") />
 		</#if>
    });
    
</script>
<@jqGrid clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField columngrouplist = columngroups
		viewSize="25" showtoolbar="true" sortdirection="desc" filtersimplemode="true" showstatusbar="false" 
		url="jqxGeneralServicer?sname=JQxSalesOrderMonthly&hasrequest=Y" showstatusbar="true"/>

