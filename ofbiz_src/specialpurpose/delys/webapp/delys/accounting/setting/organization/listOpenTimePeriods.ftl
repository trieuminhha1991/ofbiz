<script type="text/javascript" language="Javascript">
	<#assign periodTypeListX = delegator.findList("PeriodType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("groupPeriodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "FISCAL_ACCOUNT"), null, null, null, false) />
	var dataPT = new Array();
	<#list periodTypeListX as periodType>
		<#assign description = StringUtil.wrapString(periodType.get("description", locale)) />
		var row = {};
		row['description'] = "<span class='custom-style-word'>${description}</span>";
		row['periodTypeId'] = "${periodType.periodTypeId}";
		dataPT[${periodType_index}] = row;
	</#list>
	
    var cellsrendererIsclose= function (row, columnfield, value, defaulthtml, columnproperties) {
    	var tmpData = $('#jqxgrid').jqxGrid('getrowdata', row);
    	if(tmpData.isClosed=='N'){
    		var tmpId = 'tmpIc' + tmpData.customTimePeriodId;
    		var html = '<input type="button" onclick="changeState('+row+')" style="opacity: 0.99; position: absolute; top: 0%; left: 0%; padding: 0px; margin-top: 2px; margin-left: 2px; width: 96px; height: 21px;" value="${StringUtil.wrapString(uiLabelMap.commonClose)}" hidefocus="true" id="' + tmpId + '" role="button" class="jqx-rc-all jqx-rc-all-base jqx-button jqx-button-base jqx-widget jqx-widget-base jqx-fill-state-pressed jqx-fill-state-pressed-base" aria-disabled="false">';
    		return html;
    	}else{
    		return "<span class='custom-style-word'>" + value + "</span>";
    	}
    }
    
    function changeState(rowIndex){
    	var tmpData = $('#jqxgrid').jqxGrid('getrowdata', rowIndex);
      	var data = 'columnList0' + '=' + 'customTimePeriodId'; 
		data = data + '&' + 'columnValues0' + '=' +  tmpData.customTimePeriodId;
		data += "&rl=1";
      	$.ajax({
            type: "POST",                        
            url: 'jqxGeneralServicer?&jqaction=U&sname=closeFinancialTimePeriod',
            data: data,
            success: function(odata, status, xhr) {
                // update command is executed.
                if(odata.responseMessage == "error"){
                	$('#jqxNotification').jqxNotification({ template: 'info'});
                	$('#jqxNotification').text(odata.results);
                	$('#jqxNotification').jqxNotification('open');
                }else{
                	$('#jqxgrid').jqxGrid('updatebounddata');
                	$('#container').empty();
                	$('#jqxNotification').jqxNotification({ template: 'info'});
                	$('#jqxNotification').text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
                	$('#jqxNotification').jqxNotification('open');
                }
            },
            error: function(arg1) {
            	alert(arg1);
            }
        });  
    }
</script>

	<#assign dataField="[{ name: 'customTimePeriodId', type: 'string' },
						 { name: 'parentPeriodId', type: 'string' },
						 { name: 'periodTypeId', type: 'string' },
						 { name: 'periodNum', type: 'number' ,other : 'Long'},
						 { name: 'fromDate', type: 'date',other : 'Timestamp'},
						 { name: 'thruDate', type: 'date',other : 'Timestamp'},
						 { name: 'periodName', type: 'string' },
						 { name: 'isClosed', type: 'string' }]
						"/>
<#assign columnlist="{ text: '${uiLabelMap.CustomTimePeriodId}', datafield: 'customTimePeriodId', width: 150},
					 { text: '${uiLabelMap.accParentPeriodId}', datafield: 'parentPeriodId', width: 300, cellsrenderer:parentPeriodRenderer},
					 { text: '${uiLabelMap.accPeriodTypeId}', width:150, datafield: 'periodTypeId', columntype: 'dropdownlist', filtertype: 'checkedlist', 
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataPT.length; i++){
	        							if(data.periodTypeId == dataPT[i].periodTypeId){
	        								return '<span title=' + value +'>' + dataPT[i].description + '</span>';
		        							}
		        						}
		        						
		        						return '<span title=' + value +'>' + value + '</span>';
		    						},
		    					createfilterwidget: function (column, columnElement, widget) {
					   				var filterBoxAdapter2 = new $.jqx.dataAdapter(dataPT,
					                {
					                    autoBind: true
					                });
					                var empty = {periodTypeId: '', description: 'Empty'};
					   				var uniqueRecords2 = filterBoxAdapter2.records;
					   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'description', valueMember : 'periodTypeId', renderer: function (index, label, value) 
									{
										for(i=0;i < dataPT.length; i++){
											if(dataPT[i].periodTypeId == value){
												return dataPT[i].description;
											}
										}
									    return value;
									}});
					   			}},	    
                     { text: '${uiLabelMap.accPeriodNumber}', datafield: 'periodNum', width: 150,filtertype : 'number' },
                     { text: '${uiLabelMap.accStartDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.accEndDate}', datafield: 'thruDate', filtertype: 'range',cellsformat: 'dd/MM/yyyy',  width: 150 },
                     { text: '${uiLabelMap.accPeriodName}', datafield: 'periodName'}                     
					 "/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListOpenTimePeriod" dataField=dataField columnlist=columnlist
		 addrow="true" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCustomTimePeriod" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true"
		 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=createCustomTimePeriod"
		 addColumns="periodName;periodNum(java.lang.Long);parentPeriodId;isClosed;periodTypeId;fromDate(java.sql.Date);thruDate(java.sql.Date);organizationPartyId[${parameters.organizationPartyId}]" clearfilteringbutton="true"
		 alternativeAddPopup="alterpopupWindow"
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=closeFinancialTimePeriod&jqaction=D"  
		 deleteColumn="customTimePeriodId" 	mouseRightMenu="true" contextMenuId="contextMenu" customTitleProperties="${uiLabelMap.AccountingOpenTimePeriods}"  
		 />

<div id='contextMenu' style="display:none;">
<ul>
    <li><i class="icon-ok open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
    <li><i class="icon-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.accIsClosed)}</li>
</ul>
</div>
<script type="text/javascript">
$.jqx.theme = 'olbius';  
theme = $.jqx.theme;
$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
$("#contextMenu").on('itemclick', function (event) {
	var args = event.args;
    var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
    
    var tmpKey = $.trim($(args).text());
    if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") 
    {
    	$("#jqxgrid").jqxGrid('updatebounddata');
    } 
    else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.accIsClosed)}") 
    {		
    	var tmpData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
      	var data = 'columnList0' + '=' + 'customTimePeriodId'; 
		data = data + '&' + 'columnValues0' + '=' +  tmpData.customTimePeriodId;
		data += "&rl=1";
      	$.ajax({
            type: "POST",                        
            url: 'jqxGeneralServicer?&jqaction=U&sname=closeFinancialTimePeriod',
            data: data,
            success: function(odata, status, xhr) 
            {
            	$('#jqxgrid').jqxGrid('updatebounddata');
                $('#jqxGridClosed').jqxGrid('updatebounddata');
                if(odata.results['responseMessage'] == "error")
                {
					Grid.updateGridMessage('jqxgrid', 'error', '${StringUtil.wrapString(uiLabelMap.closeTimesPeriodsError)}'); 
                }
                else
                {
                	Grid.updateGridMessage('jqxgrid', 'success', '${StringUtil.wrapString(uiLabelMap.closeTimesPeriodsSuccess)}'); 
                }
            },
            error: function(arg1) 
            {
            }
        });      	    	
    } 
    
});
</script>


<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.accParentPeriodId}
    				</div>
    				<div class='span7'>
						<div id="parentPeriodId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accPeriodTypeId}
    				</div>
    				<div class='span7'>
						<div id="periodTypeId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.accPeriodNumber}
    				</div>
    				<div class='span7'>
						<div id="periodNum">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accPeriodName}
    				</div>
    				<div class='span7'>
						<input id="periodName">
 						</input>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accStartDate}
    				</div>
    				<div class='span7'>
						<div id="fromDate">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accEndDate}
    				</div>
    				<div class='span7'>
						<div id="thruDate">
 						</div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
    </div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var action = (function(){
		var initElement = function(){
			$('#periodTypeId').jqxDropDownList({dropDownWidth : 250,autoDropDownHeight : true,width  :250, source: dataPT, displayMember: "description", valueMember: "periodTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#parentPeriodId').jqxDropDownList({dropDownWidth : 250,width  :250,source: dataOOtp, displayMember: "periodName", valueMember: "customTimePeriodId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#fromDate").jqxDateTimeInput({width: '250', height: '25px',allowNullDate : true,value : null});
			$("#thruDate").jqxDateTimeInput({width: '250', height: '25px',allowNullDate : true,value : null});
			$("#periodName").jqxInput({width: '250px'});
			$("#periodNum").jqxNumberInput({ width: '250', height: '25px', inputMode: 'simple',spinButtons: true ,decimalDigits : 0});	
		    
		    $("#alterpopupWindow").jqxWindow({
		        width: 500,height : 350, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
		    var objDate = initDate();
			localStorage.setItem('objDate',JSON.stringify(objDate));
		}
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#periodTypeId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}},
					{input : '#periodName',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxInput('val');
						if(!val) return false;
						return true;
					}},
					{input : '#periodNum',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredFormat?default(''))}',action : 'valueChanged',rule : function(input){
						var val = input.jqxNumberInput('val');
						if(!val || val < 0) return false;
						return true;
					}},
					{input : '#fromDate',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDateTimeInput('val');
						if(!val) return false;
						return true;
					}},
					{input : '#thruDate',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDateTimeInput('val');
						if(!val) return false;
						return true;
					}}
				]
			})
			
		}
		
		var initDate = function(){
			return {
				min : $("#fromDate").jqxDateTimeInput('getMinDate'),
				max : $("#fromDate").jqxDateTimeInput('getMaxDate')
			}
		}
		
		var bindEvent = function(){
			// update the edited row when the user clicks the 'Save' button.
		    $("#save").click(function () {
		    	if(!$('#formAdd').jqxValidator('validate')){return;}
		    	var row;
		        row = { 
	        		fromDate:$('#fromDate').jqxDateTimeInput('getDate'),
	        		isClosed:"N",
	        		parentPeriodId:$('#parentPeriodId').val(),
	        		periodName:$('#periodName').val(),
	        		periodNum:$('#periodNum').val(),
	        		periodTypeId:$('#periodTypeId').val(),
	        		thruDate: $('#thruDate').jqxDateTimeInput('getDate'),            
	        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			   $("#jqxgrid").jqxGrid('clearSelection');                        
      			$("#jqxgrid").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    
		    $('#alterpopupWindow').on('close',function(){
		    	 $('#periodTypeId').jqxDropDownList('clearSelection');
		    	 $('#parentPeriodId').jqxDropDownList('clearSelection');
		    	 $("#fromDate").jqxDateTimeInput('val',null);
		    	 $("#thruDate").jqxDateTimeInput('val',null);
		    	 $("#periodName").jqxInput('val','');
		    	 $("#periodNum").jqxNumberInput('clear');
		    	 $('#formAdd').jqxValidator('hide');
		    	  if(localStorage.getItem('objDate')){
		        	var objDate = $.parseJSON(localStorage.getItem('objDate'));
		        	if(objDate){
		        		setMinMaxDate($('#fromDate'),objDate);
		        		setMinMaxDate($('#thruDate'),objDate);
		        	}
		        }
		    });
		    
		    var setMinMaxDate = function(object,date){
				object.jqxDateTimeInput('setMinDate',date.min);
				object.jqxDateTimeInput('setMaxDate',date.max);
			}
		    
		    $('#fromDate').on('change',function(){
			    var dateTmp = $('#fromDate').jqxDateTimeInput('getDate');
			    if(typeof(dateTmp) != 'undefined' && dateTmp && dateTmp != null){
			    	$('#thruDate').jqxDateTimeInput('setMinDate',new Date(dateTmp.getYear() +1900,dateTmp.getMonth(),dateTmp.getDate()));
			    }
		    });
		    
		     $('#thruDate').on('change',function(){
			    var dateTmp = $('#thruDate').jqxDateTimeInput('getDate');
			    if(typeof(dateTmp) != 'undefined' && dateTmp && dateTmp != null){
			    	$('#fromDate').jqxDateTimeInput('setMaxDate',new Date(dateTmp.getYear() +1900,dateTmp.getMonth(),dateTmp.getDate()));
			    }
		    });
		}
		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			}
		}
	}());
	
	$(document).ready(function(){
		action.init();
	});
</script>	
