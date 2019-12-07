<script type="text/javascript">
	var cellClassPosTerminalBank = function (row, columnfield, value) {
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			var now = new Date();
			if (data.thruDate != null && data.thruDate < now) {
				return "background-cancel";
			} else if (data.fromDate >= now) {
				return "background-prepare";
			}
 		}
    }
</script>
<#include "script/posTerminalBankScript.ftl"/>

<#assign dataField="[{ name: 'posTerminalId', type: 'string' },
               		 { name: 'terminalName', type: 'string' },
               		 { name: 'partyId', type: 'string' },
               		 { name: 'partyName', type: 'string' },
               		 { name: 'fromDate', type: 'date', other: 'Timestamp' },
               		 { name: 'thruDate', type: 'date', other: 'Timestamp' }
                ]"/>

<#assign columnlist=" {text: '${uiLabelMap.BPOSTerminalId}', dataField: 'posTerminalId', width: 100, pinned: 'true', cellClassName: cellClassPosTerminalBank},
					  {text: '${uiLabelMap.BPOSTerminalName}', dataField: 'terminalName', cellClassName: cellClassPosTerminalBank},
					  {text: '${uiLabelMap.BACCBankId}', dataField: 'partyId', width: 100, cellClassName: cellClassPosTerminalBank},
					  {text: '${uiLabelMap.BACCBankName}', dataField: 'partyName', cellClassName: cellClassPosTerminalBank},
					  {text: '${uiLabelMap.BACCFromDate}', dataField: 'fromDate', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss', cellClassName: cellClassPosTerminalBank},
					  {text: '${uiLabelMap.BACCThruDate}', dataField: 'thruDate', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss', cellClassName: cellClassPosTerminalBank},
					  {text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', dataField: 'status', width: 100, cellClassName: cellClassPosTerminalBank,
					   	cellsrenderer: function(row, column, value){
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							if (data != null && data.thruDate != null && data.thruDate != undefined) {
								var thruDate = new Date(data.thruDate);
								var nowDate = new Date();
								if (thruDate < nowDate) {
									return '<span title=\"${uiLabelMap.BSExpired}\">${uiLabelMap.BSExpired}</span>';
								}
							}
							return '<span></span>';
						}
					 }
					"/>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListPosTerminalBank" columnlist=columnlist dataField=dataField showtoolbar="true" 
		editable="false" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup" clearfilteringbutton="true" 
		deleterow="true" deleteColumn="posTerminalId;partyId;fromDate(java.sql.Timestamp)" removeUrl="jqxGeneralServicer?jqaction=D&sname=deletePosTerminalBank"
		addColumns="posTerminalId;partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" createUrl="jqxGeneralServicer?jqaction=C&sname=createPosTerminalBank" 
/>

<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.BACCCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCPOSTerminal}
    				</div>
    				<div class='span7'>
    					<div id="posTerminalId">
    						<div id="jqxgridPosTerminal"></div>
    						<input type="hidden" id="inputPosTerminalId"/>
    					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCBankName}
    				</div>
    				<div class='span7'>
    					<div id="bankId">
    						<div id="jqxgridBank"></div>
    						<input type="hidden" id="inputBankId"/>
    					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.BACCFromDate}
    				</div>
    				<div class='span7'>
    					<div id="fromDate">
    					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.BACCThruDate}
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
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>	
	</div>
</div>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/accresources/js/setting/addPosTerminalBank.js?v=0.0.2"></script>