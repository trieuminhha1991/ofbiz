<script type="text/javascript" language="Javascript">
    var luData = new Array();
    luData = [
    <#if listUom?exists && listUom?has_content>
    	<#list listUom as e>
    	{
    		'uomId' : '${e.uomId?if_exists}',
    		'description' : "${StringUtil.wrapString(e.get("description",locale)?default(""))}"
    	},
    	</#list>
    </#if>
    ]
    
    var leData = new Array();
    leData = [
    <#if listEnum?exists && listEnum?has_content>
    	<#list listEnum as e>
    	{
    		'enumId' : '${e.enumId?if_exists}',
    		'description' : "${StringUtil.wrapString(e.get("description",locale)?default(""))}"
    	},
    	</#list>
    </#if>
    ]
</script>

<#assign dataField="[{ name: 'uomId', type: 'string' },	
					 { name: 'uomIdTo', type: 'string' },
					 { name: 'uomIdDes', type: 'string' },
					 { name: 'uomIdToDes', type: 'string' },
					 { name: 'purposeEnumId', type: 'string' },
					 { name: 'conversionFactor', type: 'number' ,other : 'Double'},
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp' }
					]"/>
<#assign columnlist="{ text: '${uiLabelMap.accFromUomId}', datafield: 'uomIdDes', width: 200},
					 { text: '${uiLabelMap.accToUomId}', datafield: 'uomIdToDes', width: 200},
                     { text: '${uiLabelMap.accConversionFactor}', datafield: 'conversionFactor', width: 140,filtertype : 'number' },
                     { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.accThruDate}', datafield: 'thruDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.CommonPurpose}', datafield: 'purposeEnumId', filtertype : 'checkedlist',cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i = 0; i < leData.length;i++){
                        		if(leData[i].enumId == value){
                        			return \"<span>\" + leData[i].description + \"</span>\";
                        		}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        },createfilterwidget : function(column,columnElement,widget){
				    	var filterBoxAdapter = new $.jqx.dataAdapter(leData,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'enumId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
											{
												for(i = 0;i < leData.length; i++){
													if(leData[i].enumId == value){
														return leData[i].description;
													}
												}
											    return value;
											}});
				    }
                     }
					 "/>
<@jqGrid addrow="true" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQListConversions" dataField=dataField columnlist=columnlist
		 createUrl="jqxGeneralServicer?sname=updateFXConversion&jqaction=C" addColumns="uomId;uomIdTo;purposeEnumId;conversionFactor;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 alternativeAddPopup="alterpopupWindow"
		 />
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accFromUomId}
    				</div>
    				<div class='span7'>
    					<div id="uomId"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk' >
    					${uiLabelMap.accToUomId}
    				</div>
    				<div class='span7'>
						<div id="uomIdTo"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.CommonPurpose}
    				</div>
    				<div class='span7'>
    					<div id="purposeEnumId"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accConversionFactor}
    				</div>
    				<div class='span7'>
    					<div id="conversionFactor"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.fromDate}
    				</div>
    				<div class='span7'>
    					<div id="fromDate"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.accThruDate}
    				</div>
    				<div class='span7'>
    					<div id="thruDate"></div>
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script src="/delys/images/js/filterDate.js"></script>
<script type="text/javascript">
    $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var action = (function(){
		var initElement = function(){
			$('#uomId').jqxDropDownList({width: '250', height: '25px',filterable: true,source: luData, displayMember: "description", valueMember: "uomId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#uomIdTo').jqxDropDownList({ width: '250', height: '25px',filterable :true,  source: luData, displayMember: "description", valueMember: "uomId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#purposeEnumId').jqxDropDownList({width: '250', height: '25px', autoDropDownHeight : true, source: leData, displayMember: "description", valueMember: "enumId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#fromDate").jqxDateTimeInput({width: '250', height: '25px',allowNullDate : true,value : null});
			$("#thruDate").jqxDateTimeInput({width: '250', height: '25px',allowNullDate : true,value : null});
			$("#conversionFactor").jqxNumberInput({width: '250',height : 25,digits : 15,min  : 0,max : 9999999,decimalDigits : 2});
			initjqxWindow();
			filterDate.init('fromDate','thruDate');
		}
		var initjqxWindow = function(){
			$("#alterpopupWindow").jqxWindow({
		        width: 530,height :350,  resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme: theme          
		    });
		}
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#conversionFactor',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting)?default('')}',action : 'change,close,blur',rule : function(input){
						var val = input.jqxNumberInput('val');
						if(!val) return false;
						return true;
					}},{input : '#uomId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting)?default('')}',action : 'change,close,blur',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}},
					{input : '#uomIdTo',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting)?default('')}',action : 'change,close,blur',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}}
				]
			})
		}
		var save = function(){
			if(!$('#formAdd').jqxValidator('validate')){return;};
				var row;
			        row = { 
			        		fromDate: $('#fromDate').jqxDateTimeInput('getDate'),
			        		uomId:$('#uomId').val(),
			        		uomIdTo:$('#uomIdTo').val(),
			        		purposeEnumId:$('#purposeEnumId').jqxDropDownList('val'),
			        		conversionFactor:$('#conversionFactor').val(),
			        		thruDate:  $('#thruDate').jqxDateTimeInput('getDate')              
			        	  };
				   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			        // select the first row and clear the selection.
			        $("#jqxgrid").jqxGrid('clearSelection');                        
			        $("#jqxgrid").jqxGrid('selectRow', 0);  
			        return true;
			}
		
		var clear = function(){
			$('#fromDate').jqxDateTimeInput('val',null);
			$('#thruDate').jqxDateTimeInput('val',null);
			$('#conversionFactor').jqxNumberInput('clear');
			$('#uomId').jqxDropDownList('clearSelection');
			$('#uomIdTo').jqxDropDownList('clearSelection');
			$('#purposeEnumId').jqxDropDownList('clearSelection');
			filterDate.resetDate();
		};
		
		var bindEvent = function(){
			$("#save").click(function () {
		    	if(save())  $("#alterpopupWindow").jqxWindow('close');
		    });
		    
		    $("#saveAndContinue").click(function () {
		    	save();
		    });
		    $("#alterpopupWindow").on('close',function(){
		    	clear();
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
	});
    
</script>