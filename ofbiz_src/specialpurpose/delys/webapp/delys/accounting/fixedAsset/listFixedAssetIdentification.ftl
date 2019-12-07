<script type="text/javascript">	
	<#assign fixedAssetIdentTypeList = delegator.findList("FixedAssetIdentType", null, null, null, null, false) />
	var dataFixedAssetIdentTypeListView = new Array();
	<#list fixedAssetIdentTypeList as fixedAssetIdentType>
		var row = {};
		row['fixedAssetIdentTypeId'] = '${fixedAssetIdentType.fixedAssetIdentTypeId?if_exists}';
		row['description'] = '${fixedAssetIdentType.get('description',locale)?if_exists}';
		dataFixedAssetIdentTypeListView[${fixedAssetIdentType_index}] = row;
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

<#assign params="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=listFixedAssetIdentificationJqx">
<#assign dataField="[
					 { name: 'fixedAssetIdentTypeId', type: 'string'},					
					 { name: 'idValue', type: 'string'}					 
				   ]"/>
<#assign columnlist="
					 { text: '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetIdentType)}', datafield: 'fixedAssetIdentTypeId',filtertype : 'checkedlist', width: '50%', editable: false, filterable: true, cellclassname: cellclass,
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataFixedAssetIdentTypeListView.length; i++){
	        							if(data.fixedAssetIdentTypeId == dataFixedAssetIdentTypeListView[i].fixedAssetIdentTypeId){
	        								return '<span title=' + value +'>' + dataFixedAssetIdentTypeListView[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},createfilterwidget : function(row,cellValue,widget){
	    							var filter = new $.jqx.dataAdapter(dataFixedAssetIdentTypeListView,{autoBind : true});
	    							var records = filter.records;
	    							records.splice(0,0,'${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}');
    								widget.jqxDropDownList({displayMember : 'description',valueMember : 'fixedAssetIdentTypeId',source : records});
    							}
					 	},	                    
						{ text: '${uiLabelMap.AccountingFixedAssetIdentIdValue}', width: '50%', datafield: 'idValue'}
					"/>
	
	<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" addrefresh="true"	 deleterow="true"	
		url=params addColumns="fixedAssetId[${parameters.fixedAssetId}];fixedAssetIdentTypeId;idValue"
		createUrl="jqxGeneralServicer?sname=createFixedAssetIdent&jqaction=C" 
		updateUrl="jqxGeneralServicer?sname=updateFixedAssetIdent&fixedAssetId=${parameters.fixedAssetId}&jqaction=U"
		editColumns="fixedAssetId[${parameters.fixedAssetId}];fixedAssetIdentTypeId;idValue"
		removeUrl="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=removeFixedAssetIdent&jqaction=D"
		deleteColumn="fixedAssetId[${parameters.fixedAssetId}];fixedAssetIdentTypeId"
		showlist="true"
	/>	
	 <div id="alterpopupWindow" style="display:none;">
	    <div>${uiLabelMap.accCreateNew}</div>
	    <div style="overflow: hidden;">
			<div class='row-fluid form-window-content'>
				<form id="formAdd">
	    			<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.AccountingFixedAssetIdentType}
	    				</div>
	    				<div class='span7'>
	    					<div id="fixedAssetIdentTypeIdAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.AccountingFixedAssetIdentIdValue}
	    				</div>
	    				<div class='span7'>
							<input id="idValueAdd" ></input>
							
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
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
				$("#idValueAdd").jqxInput({width: '246px', height: '20px'});	
				$("#fixedAssetIdentTypeIdAdd").jqxDropDownList({source: dataFixedAssetIdentTypeListView,filterable : true, width: '250px',height : '25px', displayMember:"description",valueMember: "fixedAssetIdentTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			}
			
			var initRules  = function(){
				$('#formAdd').jqxValidator({
					rules  : [
						{input : '#fixedAssetIdentTypeIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting)?default('')}',action : 'change,close',rule  :function(input){
							var val = input.jqxDropDownList('val');
							if(!val) return false;
							return true;
						} }	
					]
				})	
				
									
			}
			var bindEvent = function(){
				$('#alterpopupWindow').on('close', function (event) {
						$("#idValueAdd").val('');
						$("#fixedAssetIdentTypeIdAdd").jqxDropDownList('clearSelection');
					});
				    
					$("#alterpopupWindow").jqxWindow({
				        width: 470, height: 200, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme         
				    });
					var save = function(){
							var row;
					        row = { 
					        		idValue:$('#idValueAdd').val(),		        		
					        		fixedAssetIdentTypeId:$('#fixedAssetIdentTypeIdAdd').val()
					        	  };
			        	  $("#jqxgrid").jqxGrid('addRow', null, row, "first");
					}
				
				    // update the edited row when the user clicks the 'Save' button.
				    $("#save").click(function () {
				    	if(!$('#formAdd').jqxValidator('validate')){return;}
					    	save();
					        $("#alterpopupWindow").jqxWindow('close');
				    });
				    
				    $("#saveAndContinue").click(function () {
				    	if(!$('#formAdd').jqxValidator('validate')){return;}
					    	save();
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
	$(document).ready(function(){
		action.init();
	})		
	      
	</script>