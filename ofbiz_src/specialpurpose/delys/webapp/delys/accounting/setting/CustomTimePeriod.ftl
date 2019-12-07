<script type="text/javascript" language="Javascript">

	<#assign periodTypeList = delegator.findList("PeriodType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("groupPeriodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "FISCAL_ACCOUNT"), null, null, null, false) />
	var dataPT = new Array();
	<#list periodTypeList as periodType>
		<#assign description = StringUtil.wrapString(periodType.get("description", locale)) />
		var row = {};
		row['description'] = "<span class='custom-style-word'>${description}</span>";
		row['periodTypeId'] = "${periodType.periodTypeId}";
		dataPT[${periodType_index}] = row;
	</#list>

	var dataOtp = new Array();
	dataOtp = [
		{
			'customTimePeriodId' : '',
			'periodName' : ''
		},
		<#list openTimePeriods as op>
			{
				'customTimePeriodId' : '${op.customTimePeriodId?if_exists}',
				'periodName' : 	"<span >[ ${op.customTimePeriodId?if_exists}]" + ":" + "${StringUtil.wrapString(op.get("periodName",locale)?if_exists)}" + ":" + "<span class='bg-trendcolor'>${op.fromDate?if_exists?string["dd/MM/yyyy"]}"+ " - " +  "${op.thruDate?if_exists?string["dd/MM/yyyy"]}</span>"
			},
		</#list>
	];
	
	var dataCtp = new Array();
	dataCtp = [
		<#list closedTimePeriods as op>
			{
				'customTimePeriodId' : '${op.customTimePeriodId?if_exists}',
				'periodName' : 	"<span class='custom-style-word'>[ ${op.customTimePeriodId?if_exists} ]" + ":" + "${StringUtil.wrapString(op.get("periodName",locale)?if_exists)}" + ":" + "${op.fromDate?if_exists}" + "-" +  "${op.thruDate?if_exists}</span>"
			},
		</#list>
	];
	
    var parentPeriodRenderer = function (row, column, value) {
        if (value.indexOf('#') != -1) {
            value = value.substring(0, value.indexOf('#'));
        }
        var fb = false;
        for(i=0;i<dataOtp.length;i++){
        	if(dataOtp[i].customTimePeriodId == value){
        		fb=true;
        		return "<span >" + dataOtp[i].periodName + "</span>";
        	}
        };
        for(i=0;i<dataCtp.length;i++){
        	if(dataCtp[i].customTimePeriodId == value){
        		fb=true;
        		return "<span" + dataCtp[i].periodName + "</span>";
        	}
        };
        return "<span class='custom-style-word'>" + value + "</span>";
    };
    
    var cellsrendererIsclose= function (row, columnfield, value, defaulthtml, columnproperties) {
    	var tmpData = $('#jqxgrid').jqxGrid('getrowdata', row);
    	if(tmpData.isClosed == 'N'){
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
    
    <#assign partiesList = delegator.findList("PartyAcctgPreference",  null, null, null, null, false) />  
	
	<#assign illength = partiesList.size()/>
	${illength} 
	<#assign partyIn = "" />
	<#if partiesList?has_content && partiesList?size gt 0>
		<#if partiesList?size gt 1>
			<#list 0..(illength - 2) as ii>					
					<#assign partyIn =partyIn + "\"" + StringUtil.wrapString(partiesList.get(ii).partyId?if_exists)  + "\"," />				
			</#list>
			<#assign partyIn =partyIn + "\"" + StringUtil.wrapString(partiesList.get(illength - 1).partyId?if_exists) + "\"" />
		<#else>	
			<#assign partyIn = "\"" + StringUtil.wrapString(partiesList.get(0).partyId?if_exists) + "\"" />
		</#if>
    <#else>
		<#assign partyIn=""/>	
	</#if>			
	${partyIn}	
		
	<#assign Parties = delegator.findList("PartyNameView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["company"]), null, null, null, false) />
	var partyData =  new Array();
	<#list Parties as item>
		var row = {};
		<#assign firstName = StringUtil.wrapString(item.firstName?if_exists)>
		<#assign middleName = StringUtil.wrapString(item.middleName?if_exists)>
		<#assign lastName = StringUtil.wrapString(item.lastName?if_exists)>
		<#assign groupName = StringUtil.wrapString(item.groupName?if_exists)>

		row['partyId'] = '${item.partyId?if_exists}';
		row['firstName'] = "${firstName}";
		row['middleName'] = "${middleName}";
		row['lastName'] = "${lastName}";
		row['groupName'] = "${groupName}";
		partyData[${item_index}] = row;
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
<#assign isOrganization = "true"/>
<script src="/delys/images/js/generalUtils.js"></script>
<#include "component://delys/webapp/delys/accounting/popup/popupGridPartyGeneralFilter.ftl"/>

<#assign dataField="[{ name: 'customTimePeriodId', type: 'string' },
					 { name: 'parentPeriodId', type: 'string' },
					 { name: 'periodTypeId', type: 'string' },
					 { name: 'organizationPartyId', type: 'string' },					 
					 { name: 'periodNum', type: 'number',other : 'Long'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'},
					 { name: 'periodName', type: 'string' }]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.CustomTimePeriodId}', datafield: 'customTimePeriodId', width: 100,  editable:false },
					 { text: '${uiLabelMap.accParentPeriodId}', datafield: 'parentPeriodId', width: 300, cellsrenderer:parentPeriodRenderer,columntype : 'dropdownlist',createeditor : function(row,column,editor){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 	editor.jqxDropDownList({displayMember : 'periodName',valueMember : 'customTimePeriodId',source : dataOtp,placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
					 }},
				 	 { text: '${uiLabelMap.OrganizationParty}', width:300, datafield: 'organizationPartyId',editable : false, filtertype: 'olbiusdropgrid', cellclassname: cellclass,
					 	cellsrenderer: function (row, column, value) {
							for(i = 0; i < partyData.length; i++){
								if(partyData[i].partyId == value){
									var result = '<a title=\"' + value + '\"' + ' style = \"margin-left: 10px\" ' +  ' href=\"' + '/partymgr/control/viewprofile?partyId=' + value + '\">' + partyData[i].firstName + '&nbsp' + partyData[i].middleName + '&nbsp' + partyData[i].lastName + '&nbsp' + partyData[i].groupName + '&nbsp' + '</a>';
									return result;
								}
							}
        					return '<a title= \"' + value  + '\"' +' style = \"margin-left: 10px\" href=' + '/partymgr/control/viewprofile?partyId=' + value + '>' + value + '</a>'
    					},
					 	createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(490);
			   			},				   			
				 	 },					 
					 { text: '${uiLabelMap.accPeriodTypeId}', width:150, datafield: 'periodTypeId', columntype: 'dropdownlist', filtertype: 'checkedlist', 
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        						for(i = 0 ; i < dataPT.length; i++){
        							if(data.periodTypeId == dataPT[i].periodTypeId){
        								return '<span class=\"custom-style-word\" title=' + value +'>' + dataPT[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span class=\"custom-style-word\" title=' + value +'>' + value + '</span>';
	    						},
	    					createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(dataPT,
				                {
				                    autoBind: true
				                });
				   				var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'periodTypeId', valueMember : 'periodTypeId', renderer: function (index, label, value) 
								{
									for(i=0;i < dataPT.length; i++){
										if(dataPT[i].periodTypeId == value){
											return dataPT[i].description;
										}
									}
								    return value;
								}});
				   			},
				   			createeditor: function (row, column, editor) {
	                            var sourceDPT =
					            {
					                localdata: dataPT,
					                datatype: \"array\"
					            };
					            var dataAdapterDPT = new $.jqx.dataAdapter(sourceDPT);
	                            editor.jqxDropDownList({source: dataAdapterDPT, displayMember:\"periodTypeId\", valueMember: \"periodTypeId\",
	                            renderer: function (index, label, value) {
				                    var datarecord = dataPT[index];
				                    return datarecord.description;
				                } 
	                        })},	    
					 },
                     { text: '${uiLabelMap.accPeriodNumber}', datafield: 'periodNum', width: 140 ,filtertype : 'number'},
                     { text: '${uiLabelMap.accStartDate}', datafield: 'fromDate', filtertype: 'range', columntype: 'template', width: 150, cellsformat: 'dd/MM/yyyy', 
                      	createeditor: function (row, column, editor) {
                      	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'yyyy/MM/dd',value : data.fromDate ?data.fromDate : null });
                     	}},
                     { text: '${uiLabelMap.accEndDate}', datafield: 'thruDate', filtertype: 'range', columntype: 'template',cellsformat: 'dd/MM/yyyy',  width: 150,
                      	createeditor: function (row, column, editor) {
                      	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'yyyy/MM/dd',value : data.fromDate ?data.fromDate : null });
                     	}},
                     { text: '${uiLabelMap.accPeriodName}', datafield: 'periodName',width : '17%'}
					 "/>
<@jqGrid url="jqxGeneralServicer?sname=JQListCustomTimePeriod" dataField=dataField columnlist=columnlist editmode="selectedcell" addrefresh="true"
		 addrow="true" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCustomTimePeriod"   updaterow="true"
		 addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" editable="true"
		 editColumns="customTimePeriodId;parentPeriodId;periodTypeId;periodName;fromDate(java.sql.Date);periodNum(java.lang.Long);isClosed" createUrl="jqxGeneralServicer?jqaction=C&sname=createCustomTimePeriod"
	     deleterow="true" removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteCustomTimePeriod" deleteColumn="customTimePeriodId"
		 addColumns="periodName;periodNum(java.lang.Long);parentPeriodId;isClosed;periodTypeId;fromDate(java.sql.Date);thruDate(java.sql.Date);organizationPartyId" clearfilteringbutton="true"
		 alternativeAddPopup="alterpopupWindow"
		 />

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
	 <div id="alterpopupWindow" style="display:none;">
	    <div>${uiLabelMap.accCreateNew}</div>
	    <div style="overflow: hidden;">
			<div class='row-fluid form-window-content'>
				<form id="formAdd">
	    			<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.accParentPeriodId}
	    				</div>
	    				<div class='span7'>
	    					<div id="parentPeriodId"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.DAFormFieldTitle_organizationPartyId}
	    				</div>
	    				<div class='span7'>
	    					<div id="orgPartyId">
			 					<div id="jqxOrgPartyIdGridId"></div>
			 				</div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.accPeriodTypeId}
	    				</div>
	    				<div class='span7'>
	    					<div id="periodTypeIdAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.accPeriodNumber}
	    				</div>
	    				<div class='span7'>
	    					<div id="periodNum"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.accPeriodName}
	    				</div>
	    				<div class='span7'>
	    					<input id="periodNameAdd"></input>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.accStartDate}
	    				</div>
	    				<div class='span7'>
	    					<div id="fromDate"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.accEndDate}
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
<#include "component://delys/webapp/delys/accounting/initGeneralDropdown.ftl"/>
<script src="/delys/images/js/filterDate.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var action = (function(){
		var initElement = function(){
				initOrganizationSelect($('#orgPartyId'),$("#jqxOrgPartyIdGridId"),{dropDownHorizontalAlignment : true,wgrid : 480});   
			    $('#periodTypeIdAdd').jqxDropDownList({width : 250,height : 25,  source: dataPT, displayMember: "description", valueMember: "periodTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			    $('#parentPeriodId').jqxDropDownList({width : 250,height : 25 , source: dataOtp, displayMember: "periodName", valueMember: "customTimePeriodId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
				$("#fromDate").jqxDateTimeInput({width: '250px', height: '25px',allowNullDate : true,value : null});
				$("#thruDate").jqxDateTimeInput({width: '250px', height: '25px',allowNullDate : true,value : null});
				$("#periodNameAdd").jqxInput({width: 245,height : 25});
				$("#periodNum").jqxNumberInput({width: 250,height : 25,min : 0,max : 999999999999,digits : 17,decimalDigits : 0,spinButtons : true});
				initWindow();
			}
			
			var initWindow = function(){
				$("#alterpopupWindow").jqxWindow({
				        width: 580, height : 380,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
				    });
			}
			
			var initRules = function(){
				$('#formAdd').jqxValidator({
					rules : [
						{input : '#fromDate',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,blur',rule : function(input){
							var val  = input.jqxDateTimeInput('val');
							if(!val) return false;
							return true;
						}},
						{input : '#thruDate',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,blur',rule : function(input){
							var val  = input.jqxDateTimeInput('val');
							if(!val) return false;
							return true;
						}},
						{input : '#orgPartyId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
							var val  = input.val();
							if(!val) return false;
							return true;
						}},
						{input : '#periodTypeIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
							var val  = input.val();
							if(!val) return false;
							return true;
						}},
						{input : '#periodNameAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,blur',rule : function(input){
							var val  = input.val();
							if(!val) return false;
							return true;
						}}
						
					] 
				})	
				initFilterDate();
			}
			
			var initFilterDate = function(){
				filterDate.init('fromDate','thruDate');
			};
			var bindEvent = function(){
				$("#save").click(function () {
					if(save())  $("#alterpopupWindow").jqxWindow('close');
    			});
			    $('#saveAndContinue').click(function(){
			    	save();
			    })
			    
			    $("#alterpopupWindow").on('close',function(){
			    	clear();
			    })
			}
			
			var clear = function(){
				$('#orgPartyId').val('');
				$('#periodTypeIdAdd').jqxDropDownList('clearSelection');
				$('#parentPeriodId').jqxDropDownList('clearSelection');
				$("#fromDate").jqxDateTimeInput('value',null);
				$("#thruDate").jqxDateTimeInput('value',null);
				$("#periodNameAdd").jqxInput('val','');
				$("#periodNum").jqxNumberInput('clear');
				$('#formAdd').jqxValidator('hide');
				$('#jqxOrgPartyIdGridId').jqxGrid('clearSelection');
				filterDate.resetDate();
			}
			
			var save  = function(){
				if(!$('#formAdd').jqxValidator('validate')) { return;	}
					var row = getData();	
				   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			        // select the first row and clear the selection.
			        $("#jqxgrid").jqxGrid('clearSelection');                        
			        $("#jqxgrid").jqxGrid('selectRow', 0);  
			    
				return true;
			}
			
			var getData = function(){
					var row;
			        row = { 
			        		fromDate:$('#fromDate').jqxDateTimeInput('getDate'),
			        		parentPeriodId:$('#parentPeriodId').val(),
			        		organizationPartyId:$('#orgPartyId').val(),
			        		periodName:$('#periodNameAdd').val(),
			        		periodNum:$('#periodNum').val(),
			        		periodTypeId:$('#periodTypeIdAdd').val(),
			        		thruDate: $('#thruDate').jqxDateTimeInput('getDate'),            
			        	  };
			        	  return row;
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
