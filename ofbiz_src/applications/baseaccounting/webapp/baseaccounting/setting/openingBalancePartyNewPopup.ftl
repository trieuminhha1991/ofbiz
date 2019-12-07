<#--Begin Popup-->
<div id="newOpeningBalPopup" style="display:none">
	<div>${uiLabelMap.BACCNewOpeningBalance}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<#--<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right asterisk'>
					${uiLabelMap.BACCCustomTimePeriodId}
				</div>
				<div class='span7'>
					<div id="customTimePeriodId"></div>
				</div>
			</div>-->
			<div class="row-fluid">
				<div id="glAccountGrid"></div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="btnSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="btnCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function(){
	var OlbOpeningBalPop = (function(){
		var _object = {
				orgId : '${organizationPartyId?if_exists}',
				listGlAccountParty : new Array()
		}
		
		var init = function(){
			createWindow();
			initEvent();
		};
		
		var _getGlAccData = function(){
			var glAccData = new Array();
			<#list glAccountOrgList as item>
				var row = {};
				row['glAccountId'] = '${item.glAccountId?if_exists}';
				row['accountName'] = '${StringUtil.wrapString(item.accountName?if_exists)}';
				row['organizationPartyId'] = '${item.organizationPartyId?if_exists}';
				<#assign accountType = Static["com.olbius.acc.utils.UtilServices"].getAccountType(item.glAccountId, delegator) />
				row['accountType'] = '${accountType?if_exists}';
				glAccData['${item_index}'] = row;
			</#list>
			return glAccData;
		}
		
		var initrowdetails = function(index, parentElement, gridElement, datarecord){
				var grid = $($(parentElement).children()[0]);
				$(grid).attr('id','setBalanceParty' + index);
				
				var datafields = [
			                   { name: 'partyId', type: 'string'},
			                   { name: 'fullName', type: 'string'},
			                   { name: 'openingCrBalance', type: 'number' },
	              			  	{ name: 'openingDrBalance', type: 'number' }
		                   ];
				
				var columns = [
		                	{ text: '${uiLabelMap.CustomerId}', datafield: 'partyId', filterable: true,  width: 200, editable: false},
		                	{ text: '${uiLabelMap.Customer}', datafield: 'fullName', filterable: true,  width: 250, editable: false},
		                	{ text: '${uiLabelMap.BACCOpeningCrBalance}', datafield: 'openingCrBalance', filterable: false, columntype: 'numberinput',
			                	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			                		  if(!value)  return '<span>' + formatcurrency(0) + '</span>';
	        						  return '<span>' + formatcurrency(value) + '</span>';
	            				  },
	            				  initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
	            					  editor.jqxNumberInput({inputMode: 'advanced', decimalDigits: 0, digits: 12, max: 100000000000});
	            				  },
	            				  cellbeginedit: function (row, datafield, columntype) {
	            					  if (datarecord['accountType'] == 'DEBIT') return false;
	            				  },cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
	            					  var data = $(grid).jqxGrid('getrowdata',row);
	            						 if(datarecord && typeof datarecord !== 'undefined'){
		            							addValue(datarecord.glAccountId + '-' + data.partyId,datafield,newvalue);
		            						 }
            				    },validation: function (cell, value) {
           						 if (value < 0) {
        							 return { result: false, message: '${uiLabelMap.BACCBalanceRequired}'};
        						 }
        						 else 
        							 return true;
            				    	}
			                  },
			                  { text: '${uiLabelMap.BACCOpeningDrBalance}', datafield: 'openingDrBalance', filterable: false,columntype: 'numberinput',
			                	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			                		  if(!value)  return '<span>' + formatcurrency(0) + '</span>';
	        						  return '<span>' + formatcurrency(value) + '</span>';
	            				  },
	            				  initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
	            					  editor.jqxNumberInput({inputMode: 'advanced', decimalDigits: 0, digits: 12, max: 100000000000});
	            				  },
	            				  cellbeginedit: function (row, datafield, columntype) {
	            					  if (datarecord['accountType'] == 'CREDIT') return false;
	            				  },cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
	            					  var data = $(grid).jqxGrid('getrowdata',row);
	            						 if(datarecord && typeof datarecord !== 'undefined'){
	            							addValue(datarecord.glAccountId+ '-' + data.partyId,datafield,newvalue);
	            						 }
            				    },
	            				validation: function (cell, value) {
	            						 if (value < 0) {
	            							 return { result: false, message: '${uiLabelMap.BACCBalanceRequired}'};
	            						 }
	            						 else 
	            							 return true;
	            		      		 }
			                  }
	                ];
				
				Grid.initGrid({url : 'JQGetCustomersBySetupOpenBalanceParty',source : {pagesize:15,pagesizeoptions : [5,10,15],cache :false}, editable:true ,filterable: true,autoheight: true,width : '100%',selectionmode: 'singlecell',editmode: 'selectedcell'},
		   				datafields, columns, null, $(grid));
				
				$(grid).on('bindingcomplete',function(){
					 var dataGrid = $(grid).jqxGrid('getboundrows');
					    if(typeof dataGrid !== 'undefined' && dataGrid !== null && dataGrid.length > 0){
					    	$.each(dataGrid,function(index){
					    			var parent_each = this;
					    			$.each(_object.listGlAccountParty,function(){
					    				if((datarecord.glAccountId+ '-' +parent_each.partyId) == Object.keys(this)[0]){
					    					var objTmp = this['' + Object.keys(this)[0]];
					    					if(objTmp != null && typeof objTmp !== 'undefined' && objTmp.hasOwnProperty('openingCrBalance')){
					    						if(!isNaN(objTmp['openingCrBalance'])) $(grid).jqxGrid('setcellvalue',index,'openingCrBalance',objTmp['openingCrBalance']);
					    					}
				    						if(objTmp != null && typeof objTmp !== 'undefined' && objTmp.hasOwnProperty('openingDrBalance')){
				    							if(!isNaN(objTmp['openingDrBalance'])) $(grid).jqxGrid('setcellvalue',index,'openingDrBalance',objTmp['openingDrBalance']);
					    					}
						    			}
					    			});
					    	})
					    }
				})
		}
		
		
		var renderObj = function(obj,key,value){
				if(typeof obj === 'object') obj['' + key] = value;
				return obj;
			};
			
		var addValue = function(pattern,key,value){
			if(_object.listGlAccountParty.length == 0 ){
				_object.listGlAccountParty.push(renderObj(new Object(),pattern,renderObj(new Object(),key,value)));
			}else{
				var isContains = false;
				$.each(_object.listGlAccountParty,function(){	
					if(Object.keys(this)[0] == pattern){
						isContains = true;
						for(var k in this[pattern]){
							if(k == key){
								this[pattern][k] = value;
							}else renderObj(this[pattern],key,value);
						}
					}
				})
				
				if(!isContains){
					_object.listGlAccountParty.push(renderObj(new Object(),pattern,renderObj(new Object(),key,value)));
				}
			}
		}
		
		var createWindow = function(){
			$("#newOpeningBalPopup").jqxWindow({
				maxWidth : 1400,width: 1300,maxHeight : 800, height: 670, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#btnCancel"), modalOpacity: 0.7, theme: 'olbius',
				initContent: function () {
					//Create grid
				   	var columns = [
		                  { text: '${uiLabelMap.BACCGlAccountId}', datafield: 'glAccountId',  width: 200, editable: false},
		                  { text: '${uiLabelMap.BACCGlAccountName}', datafield: 'accountName', editable: false},
		                ];
				   	var datafields = [
					                  { name: 'glAccountId', type: 'string'},
					                  { name: 'organizationPartyId', type: 'string'},
					                  { name: 'accountType', type: 'string'},
					                  { name: 'accountName', type: 'string' }
			                    ];
				   	Grid.initGrid({source : {localdata : _getGlAccData(),pagesize : 15},editable:true,rowdetails:true,initrowdetails : initrowdetails,virtualmode: false,filterable : true, autoheight: true,width : '100%',selectionmode: 'singlecell',editmode: 'selectedcell',rowdetailstemplate: {rowdetailsheight: 500}},
				   				datafields, columns, null, $('#glAccountGrid'));
				}
			});
		};
		var initEvent = function(){
			// update the edited row when the user clicks the 'Save' button.
			$("#btnSave").click(function () {
				$.ajax({
					  url: "createGlAccountBalParty",
					  type: "POST",
					  data: {
						  orgId : _object.orgId,
						  listValue : JSON.stringify(_object.listGlAccountParty)
					  },
					  async: false,
					  success: function(data) {
						  if(data._ERROR_MESSAGE_LIST_ || data._ERROR_MESSAGE_ || data.responseMessage != "success"){
							  if(data.responseMessage) Grid.renderMessage('openingBalGrid',data.responseMessage,{template : 'error'});
							  else Grid.renderMessage('openingBalGrid','${StringUtil.wrapString(uiLabelMap.wgadderror)}',{template : 'error'});
						  }else{
							  Grid.renderMessage('openingBalGrid','${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}',{template : 'success'});
						  }
						  $('#openingBalGrid').jqxGrid('updatebounddata');
						  $('#glAccountGrid').jqxGrid('updatebounddata');
						  $("#newOpeningBalPopup").jqxWindow('close');
						  _object.listGlAccountParty = new Array();
					  }
			  	});
			});
			
		};
	
		return {
			init: init
		};
	}());
	OlbOpeningBalPop.init();
})
</script>