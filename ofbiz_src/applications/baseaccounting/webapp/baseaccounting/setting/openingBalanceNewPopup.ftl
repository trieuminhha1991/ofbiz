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
		var init = function(){
			createWindow();
			initEvent();
		};
		
		var _getGlAccData = function(){
			var glAccData = new Array();
			<#list glAccountOrgList as item>
				var row = {};
				row['glAccountId'] = '${item.glAccountId}';
				row['accountName'] = '${StringUtil.wrapString(item.accountName)}';
				row['organizationPartyId'] = '${item.organizationPartyId}';
				<#assign accountType = Static["com.olbius.acc.utils.accounts.AccountUtils"].getAccountType(item.glAccountId, delegator) />
				row['accountType'] = '${accountType}';
				row['openingCrBalance'] = 0;
				row['openingDrBalance'] = 0;
				glAccData['${item_index}'] = row;
			</#list>
			var source = {
		            localdata: glAccData,
		            datatype: "array",
		            //Data fields 
		            datafields : [
		                  { name: 'glAccountId', type: 'string'},
		                  { name: 'organizationPartyId', type: 'string'},
		                  { name: 'accountType', type: 'string'},
		                  { name: 'accountName', type: 'string' },
		                  { name: 'openingCrBalance', type: 'number' },
              			  { name: 'openingDrBalance', type: 'number' },
                    ],
					updaterow: function (rowid, rowdata, commit) {
				        commit(true);
				    },
				    pagesize: 15
		        };
	        var dataAdapter = new $.jqx.dataAdapter(source);
			return dataAdapter;
		}
		
		var createWindow = function(){
			$("#newOpeningBalPopup").jqxWindow({
				width: '1200', height: 600, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#btnCancel"), modalOpacity: 0.7, theme: 'olbius',
				initContent: function () {
					//$('#customTimePeriodId').jqxDropDownList({source: customTimePeriods, width:'80%', valueMember: 'customTimePeriodId', displayMember: 'description' });
					
					//Create grid
				   	$("#glAccountGrid").jqxGrid({
			        	width: '100%',
			        	theme: 'olbius',
				   		virtualmode: false,
				   		showfilterrow: true,
				   		filterable: true,
				   		showtoolbar: false,
				   		source: _getGlAccData(),
				   		selectionmode: 'singlecell',
				   		editmode: 'selectedcell',
				   		pageable: true,
				   		columnsresize: true,
				   		sortable: false,
				        editable: true,
				        autoheight: true,
			            showaggregates: true,
			            showstatusbar: true,
			            statusbarheight: 50,
				        localization: getLocalization(),
				        columns : [
		                  { text: '${uiLabelMap.BACCGlAccountId}', datafield: 'glAccountId',  width: 200, editable: false},
		                  { text: '${uiLabelMap.BACCGlAccountName}', datafield: 'accountName', width: 200, editable: false},
		                  { text: '${uiLabelMap.BACCOpeningCrBalance}', dataField: 'openingCrBalance', filterable: false, columntype: 'numberinput',
		                	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
        						  return '<span>' + formatcurrency(value) + '</span>';
            				  },
            				  initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
            					  editor.jqxNumberInput({inputMode: 'advanced', decimalDigits: 0, digits: 12, max: 100000000000});
            				  },
            				  cellbeginedit: function (row, datafield, columntype) {
            					  var data = $('#glAccountGrid').jqxGrid('getrowdata', row);
            					  if (data['accountType'] == 'DEBIT') return false;
            				  },
            				  aggregates: [{'${uiLabelMap.BACCCrAmountTotal}': function (aggregatedValue, currentValue) {
            	                  return aggregatedValue + currentValue;
              				  	}
            				  }],
            				  aggregatesrenderer: function (aggregates) {
            		              var renderstring = "";
            		              $.each(aggregates, function (key, value) {
            		                  renderstring += '<div style="font-weight: 600; font-size: 105%; padding: 10px;">' + key + ': ' + formatcurrency(value) + '</div>';
            		              });
            		              return renderstring;
            		          }
		                  },
		                  { text: '${uiLabelMap.BACCOpeningDrBalance}', dataField: 'openingDrBalance', filterable: false, columntype: 'numberinput',
		                	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
        						  return '<span>' + formatcurrency(value) + '</span>';
            				  },
            				  initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
            					  editor.jqxNumberInput({inputMode: 'advanced', decimalDigits: 0, digits: 12, max: 100000000000});
            				  },
            				  cellbeginedit: function (row, datafield, columntype) {
            					  var data = $('#glAccountGrid').jqxGrid('getrowdata', row);
            					  if (data['accountType'] == 'CREDIT') return false;
            				  },
            				  aggregates: [{'${uiLabelMap.BACCDrAmountTotal}': function (aggregatedValue, currentValue) {
            	                  return aggregatedValue + currentValue;
              				  	}
            				  }],
            				  aggregatesrenderer: function (aggregates) {
            		              var renderstring = "";
            		              $.each(aggregates, function (key, value) {
            		                  renderstring += '<div style="font-weight: 600; font-size: 105%; padding: 10px;">' + key + ': ' + formatcurrency(value) + '</div>';
            		              });
            		              return renderstring;
            		          }
		                  },
		                ]
			    	});
				}
			});
		};
		var initEvent = function(){
			// update the edited row when the user clicks the 'Save' button.
			$("#btnSave").click(function () {
				var submitedData = {};
				$('#glAccountGrid').jqxGrid('removefilter', 'accountName', true);
				$('#glAccountGrid').jqxGrid('removefilter', 'glAccountId', true);
				var rows = $('#glAccountGrid').jqxGrid('getrows');
				var drData = $("#glAccountGrid").jqxGrid('getcolumnaggregateddata', 'openingDrBalance', ['sum']);
				var crData = $("#glAccountGrid").jqxGrid('getcolumnaggregateddata', 'openingCrBalance', ['sum']);
				if(drData.sum != crData.sum){
					accutils.confirm.confirm('${uiLabelMap.BACCDrNotEqualCr}', function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					return false;
				}
				submitedData['glAccountList'] = JSON.stringify(rows);
				submitedData['customTimePeriodId'] = $('#customTimePeriodId').val();
				$.ajax({
					  url: "createGlAccountBal",
					  type: "POST",
					  data: submitedData,
					  async: false,
					  success: function(data) {
						  if(data._ERROR_MESSAGE_LIST_ || data._ERROR_MESSAGE_){
							  if(data._ERROR_MESSAGE_LIST_ || data._ERROR_MESSAGE_){
									if(data._ERROR_MESSAGE_LIST_){
										accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_[0], function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
									}
									if(data._ERROR_MESSAGE_){
										accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
									}
								}
						  }else{
							  $("#newOpeningBalPopup").jqxWindow('close');
							  window.location.replace('<@ofbizUrl>SetupOpeningBalance?organizationPartyId=' + '${userLogin.lastOrg}' + '</@ofbizUrl>');
						  }
					  }
			  	});
			});
		};
		return {
			init: init,
		};
	}());
	OlbOpeningBalPop.init();
})
</script>