<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="span12">
				<h5 class="smaller green row header blue font-bold" style="">
					${uiLabelMap.GeneralInfo}
				</h5>
				<div class='row-fluid font-bold' style="margin-bottom: -10px !important">
					<div class="span6">
						<div class="row-fluid">
				    		<div class="span5" style="text-align: right;"><div>${uiLabelMap.LogShipper}</div></div>
					 	    <div class="span7"><div id="shipperPartyIdDT" class="green-label"></div></div>
						</div>
						<div class="row-fluid">
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.ShipCost}</div></div>
					 	    <div class="span7"><div id="tripCostDT" class="green-label"></div></div>
						</div>
						<div class="row-fluid">
				    		<div class="span5" style="text-align: right;"><div>${uiLabelMap.Description}</div></div>
					 	    <div class="span7"><div id="descriptionDT" class="green-label"></div></div>
						</div>
					</div>
					<div class="span6">
						
						<div class="row-fluid">
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.CostCustomerPaid}</div></div>
					 	    <div class="span7"><div id="costCustomerPaidDT" class="green-label"></div></div>
						</div>
						<div class='row-fluid'>
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.StartShipDate}</div></div>
							<div class="span7"><div class="green-label" id="estimatedTimeStartDT"></div></div>
					</div>
					<div class='row-fluid'>
						<div class="span5" style="text-align: right;"><div>${uiLabelMap.EndShipDate}</div></div>
							<div class="span7"><div class="green-label" id="estimatedTimeEndDT"></div></div>
					</div>
					</div>
				</div>
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid margin-top10">
		<div class="span12">
			<h5 class="smaller green row header blue font-bold" style="">
				${uiLabelMap.ListPacks}
			</h5>
			<div id="jqxgridPackSelected" style="width: 100%"></div>
		</div>
	</div>
</div>
<script>
</script>
<#assign dataField="[{ name: 'packId', type: 'string'},
	{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
	{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
	{ name: 'partyIdTo', type: 'string'},
	]"/>

<#assign columnlistPack="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},
	{ text: '${uiLabelMap.PackCode}', datafield: 'packId', align: 'left', pinned: true},
	{ text: '${uiLabelMap.BLProductStoreId}', datafield: 'partyIdTo', align: 'left'},
	{ text: '${uiLabelMap.ShipAfterDate}', datafield: 'shipBeforeDate', align: 'left',},
	{ text: '${uiLabelMap.ShipBeforeDate}', datafield: 'shipAfterDate', align: 'left', },
	"/>
<@jqGrid filtersimplemode="true" id="jqxgridPackSelected" filterable="false" dataField=dataField columnlist=columnlistPack editable="false" showtoolbar="false" clearfilteringbutton="false" initrowdetailsDetail=initrowdetails
	url="" editmode='click' initrowdetails = "false" selectionmode=""
/>
<script type="text/javascript" src="/logresources/js/trip/newTripConfirm.js"></script>
