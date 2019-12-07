<#include "script/listShippingTripAccmScript.ftl"/>

<div id="jqxNotificationSuccess" style="display: none;">
<div>
	${uiLabelMap.UpdateSuccessfully}.
</div>
</div>

<div>
${uiLabelMap.BLChooseAShipper}: <div id="shipperPartyId" class="green-label"></div>
</div>

<#assign dataField="[{ name: 'shippingTripId', type: 'string' },
					 { name: 'startDateTime', type: 'date', other: 'Timestamp'},
					 { name: 'finishedDateTime', type: 'date', other: 'Timestamp'},
					 { name: 'tripCost', type: 'number'},
					 { name: 'costCustomerPaid', type: 'number'},
					 { name: 'statusId', type: 'string'}
					 ]"/>
<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				        groupable: false, draggable: false, resizable: false,
				        datafield: '', columntype: 'number', width: 50,
				        cellsrenderer: function (row, column, value) {
				            return '<span style=margin:4px;>' + (value + 1) + '</span>';
				        }
					},
					{ text: '${uiLabelMap.DeliveryEntryCode}', datafield: 'shippingTripId', width: 300, editable: false, pinned: true,
						cellsrenderer: function(row, column, value){
							//var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return '<span><a href=\"shippingTripDetail?shippingTripId=' + value +'\">' + value + '</a></span>';
						}
				 	},
				 	{ text: '${uiLabelMap.BLDstartDateTime}', datafield: 'startDateTime', cellsalign: 'right', width: 200, cellsformat:'dd/MM/yyyy', editable: false, filtertype:'range'},
				 	{ text: '${uiLabelMap.BLDfinishedDateTime}', datafield: 'finishedDateTime', cellsalign: 'right', width: 200, cellsformat:'dd/MM/yyyy', editable: false, filtertype:'range'},
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: 200, editable: false, columntype: 'dropdownlist',
					 	cellsrenderer: function(row, column, value){
						 	for(var i = 0; i < statusDataDE.length; i++){
							 	if(statusDataDE[i].statusId == value){
								 	return '<span title=' + value + '>' + statusDataDE[i].description + '</span>';
							 	}
						 	}
						 	return value;
					 	}
				 	},
				 	{ text: '${uiLabelMap.BLDTripCost}', datafield: 'tripCost', width: 200, editable: false, },
				 	{ text: '${uiLabelMap.BLDCostCustomerPaid}', datafield: 'costCustomerPaid', width: 250, editable: false, },

 				"/>
 <div id="containerNotify" style="width: 100%; overflow: auto;">
 </div>
<div>
	<@jqGrid id="jqGridNewInvoice"
	filtersimplemode="false" dataField=dataField columnlist=columnlist sortable="true" sortdirection="desc"  clearfilteringbutton="true" clearfilteringbutton="true" showtoolbar="true"
	url="" selectionmode="checkbox"
	/>
</div>
<br>
<div>
	<button onclick="OlbShippingTripAccm.createPopupInvoice()" class='btn btn-small btn-success form-action-button pull-right' style="display:inline-block"><i class='fa-check'></i> ${uiLabelMap.BACCNewInvoice}</button>
</div>

<div id="popupCreateNewInvoice" style="display:none;">
    <div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.BPOSPageTitleCreateOrderToLog}</div>
    <div class="form-window-content" style="overflow: hidden;">
    	<div class="row-fluid" style="height: 100%">
                <div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container" style="display: block;" style="height: 5%">
    			<ul class="wizard-steps wizard-steps-square">
    				<li data-target="#step1" class="active" style="min-width: 50%; max-width: 50%;">
    					<span class="step">1. ${uiLabelMap.BACCEnterTheCommon}</span>
    				</li>
    				<li data-target="#step3" style="min-width: 50%; max-width: 50%;">
    					<span class="step">2. ${uiLabelMap.BACCConfirmInvoice}</span>
    				</li>
    			</ul>
    		</div>
            <div class="step-content row-fluid position-relative" id="step-container" style="overflow-y: scroll; overflow-x: hidden; height: 88%; margin-top: 5px;">
            	<div class="step-pane active" id="step1">
            		<div class="span12 boder-all-profile">
            		    <#include "invoiceShippingTripAccmInfo.ftl"/>
            		</div>
            	</div>
            	<div class="step-pane" id="step3">
            		<div class="span12">
            		    <#include "invoiceShippingTripAccmConfirm.ftl"/>
            		</div>
            	</div>
            </div>
    		<div class="form-action wizard-actions" style="height: 5%; margin-top: 50px;">
    			<button class="btn btn-next btn-success form-action-button pull-right" style="margin-top: 5px;" data-last="${StringUtil.wrapString(uiLabelMap.HRCommonCreateNew)}" id="btnNext">
    				${uiLabelMap.CommonNext}
    				<i class="icon-arrow-right icon-on-right"></i></button>
    				<button class="btn btn-prev form-action-button pull-right" style="margin-top: 5px;" id="btnPrev" disabled="disabled">
    					<i class="icon-arrow-left"></i>
    					${uiLabelMap.CommonPrevious}
    				</button>
    			</div>
    		</div>
    	</div>
    </div>
</div>