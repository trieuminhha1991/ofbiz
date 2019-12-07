<script type="text/javascript">	
	<#assign customMethodList = delegator.findList("CustomMethod", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("customMethodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "DEPRECIATION_FORMULA"), null, null, null, false) />
	var dataCustomMethodView = new Array();
	var row = {};
	row['customMethodId'] = '';
	row['description'] = '';
	dataCustomMethodView[0] = row;	
	<#list customMethodList as customMethod >
		var row = {};
		row['customMethodId'] = '${customMethod.customMethodId?if_exists}';
		row['description'] = '${customMethod.get('description',locale)?if_exists}';
		dataCustomMethodView[${customMethod_index} + 1] = row;
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign params="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=listFixedAssetDepreciationMethodJqx">
<#assign dataField="[{ name: 'depreciationCustomMethodId', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'thruDate', type: 'date', other: 'Timestamp'}
				   ]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_depreciationCustomMethodId)}', datafield: 'depreciationCustomMethodId', editable: false,  width: '60%', cellclassname: cellclass, columntype: 'dropdownlist',
							 	cellsrenderer: function (row, column, value) {
									var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		        						for(i = 0 ; i < dataCustomMethodView.length; i++){
		        							if(data.depreciationCustomMethodId == dataCustomMethodView[i].customMethodId){
		        								return '<span title=' + value +'>' + dataCustomMethodView[i].description + '</span>';
		        							}
		        						}
		        						
		        						return '<span title=' + value +'>' + value + '</span>';
		    						},	    					   			
					   				createeditor: function (row, column, editor) {					   				
					   					editor.jqxDropDownList({source: dataCustomMethodView, displayMember:\"description\", valueMember: \"customMethodId\",
			                            renderer: function (index, label, value) {
						                    var datarecord = dataCustomMethodView[index];
						                    return datarecord.description;
						                  }
			                        });}
					   			},
	                    { text: '${StringUtil.wrapString(uiLabelMap.fromDate)}', width:'20%', datafield: 'fromDate', editable: false, cellsformat: 'dd/MM/yyyy', filtertype: 'range', columntype: 'datetimeinput',
						 	createeditor: function (row, column, editor) {
	                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd/MM/yyyy' });
	                     		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                     		if (!data.fromDate)
	                     		editor.jqxDateTimeInput('setDate', null);
	                     	}},
	                    { text: '${StringUtil.wrapString(uiLabelMap.thruDate)}', width:'20%', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', columntype: 'datetimeinput',
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
	                     	}}
					"/>
	
	<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" customTitleProperties="${uiLabelMap.PageTitleFixedAssetDepreciationMethod}"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" addrefresh="true"	 deleterow="true"	
		url=params addColumns="fixedAssetId[${parameters.fixedAssetId}];depreciationCustomMethodId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		createUrl="jqxGeneralServicer?sname=createFixedAssetDepMethod&jqaction=C" 
		updateUrl="jqxGeneralServicer?sname=updateFixedAssetDepMethod&fixedAssetId=${parameters.fixedAssetId}&jqaction=U"
		editColumns="fixedAssetId[${parameters.fixedAssetId}];depreciationCustomMethodId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		removeUrl="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=deleteFixedAssetDepMethod&jqaction=D"
		deleteColumn="fixedAssetId[${parameters.fixedAssetId}];depreciationCustomMethodId"
		showlist="true"
	/>	
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content' style="overflow: hidden;">
			<form id="formAdd">
			    <div class="span12">
			    	<div class="row-fluid">		
			    			<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right asterisk'>
			    					${uiLabelMap.FormFieldTitle_depreciationCustomMethodId}
			    				</div>
			    				<div class='span7'>
									<div id="depreciationCustomMethodIdAdd">
					 				</div>
			    				</div>
							</div>
							
							<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right'>
			    					${uiLabelMap.fromDate}
			    				</div>
			    				<div class='span7'>
			    					<div id="fromDateAdd">
					 				</div>
			    				</div>
							</div>
							<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right'>
			    					${uiLabelMap.thruDate}
			    				</div>
			    				<div class='span7'>
			    					<div id="thruDateAdd" >
			 						</div>
			    				</div>
							</div>
					</div>
				</div>		
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
    </div>
</div>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.culture.vi-VN.js"></script>
<script type="text/javascript" src="/delys/images/js/filterDate.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		
		var tmpLcl = '${locale}';
		if(tmpLcl=='vi'){
			tmpLcl = 'vi-VN';
		}else{
			tmpLcl = 'en-EN';
		}
var action = (function(){
		var initElement = function(){
			$("#thruDateAdd").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
			$("#fromDateAdd").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
			$("#thruDateAdd").jqxDateTimeInput("val",null);
			$("#fromDateAdd").jqxDateTimeInput("val",null);
			$("#depreciationCustomMethodIdAdd").jqxDropDownList({source: dataCustomMethodView, width: '208px', displayMember:"description", valueMember: "customMethodId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#alterpopupWindow").jqxWindow({
		        width: 450, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme         
		    });
			filterDate.init('fromDateAdd','thruDateAdd');
		}
	
	var clear = function(){
		$("#thruDateAdd").jqxDateTimeInput("val",null);
    	$("#fromDateAdd").jqxDateTimeInput("val",null);
		$("#depreciationCustomMethodIdAdd").jqxDropDownList('clearSelection');
		filterDate.resetDate();
	}
	
	var save = function(){
		if(!$('#formAdd').jqxValidator('validate')){
			return;		
		};
		var row;
        row = { 
    		thruDate:$('#thruDateAdd').jqxDateTimeInput('getDate'),
    		fromDate:$('#fromDateAdd').jqxDateTimeInput('getDate'),
    		depreciationCustomMethodId:$('#depreciationCustomMethodIdAdd').val(),
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        return true;
	}
	var bindEvent = function(){
		$("#alterpopupWindow").on('close',function(){
			clear();
		})
	    // update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
    		if(save()) $('#alterpopupWindow').jqxWindow('close');
	    });
	     $("#saveAndContinue").click(function () {
	    	save();
	    });
	}
	
	var initRules = function(){
	    $('#formAdd').jqxValidator({
	        rules: [
	                   { input: '#depreciationCustomMethodIdAdd', message: '${uiLabelMap.CommonRequired}', action: 'select,change', rule: 
	                	   function (input) {
	                	   		var val = $("#depreciationCustomMethodIdAdd").jqxDropDownList('val');
	                	   if(!val){
	                	  	 return false;
	                	   }
	                	   	return true;
	                	   }                            
	                   }
	               ]
	   		 });	    
	}

	return {
		init : function(){
			initElement();
			bindEvent();
			initRules();
		}
	}

}())	
	
$(document).ready(function () {
	action.init();
});

	    
	</script>