<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="span12">
				<div class='row-fluid' style="margin-bottom: -10px !important">
					<div class="span6">
                        <div class='row-fluid'>
                            <div class="span5" style="text-align: right;"><div>${uiLabelMap.BSSupervisor}</div></div>
                            <div class="span7"><div id="salessupConfirm" class="green-label"></div></div>
                        </div>
					</div>
					<div class="span6">
						<div class='row-fluid'>
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.BSSalesmanName}</div></div>
					 	    <div class="span7"><div id="salesmanConfirm" class="green-label"></div></div>
				   		</div>
					</div>
				</div>
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div id="jqxgridCustomerSelected" style="width: 100%"></div>
		</div>
	</div>
</div>
<script>

</script>
<#assign dataField="[
				{name: 'partyId', type: 'string'},
				{name: 'partyCode', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'distributorName', type: 'string'},
				{name: 'distributorId', type: 'string'},
				{name: 'distributorCode', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'salesmanName', type: 'string'},
				{name: 'salesmanId', type: 'string'},
				{name: 'postalAddressName', type: 'string'},
				{name: 'contactNumber', type: 'string'},
				{name: 'emailAddress', type: 'string'},
				{name: 'officeSiteName', type: 'string'},
				{name: 'latitude', type: 'number'},
				{name: 'longitude', type: 'number'},
				{name: 'geoPointId', type: 'string'},
				{name: 'preferredCurrencyUomId', type: 'string'}]"/>
<#assign columnlist = "
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false, width: '5%',
				    cellsrenderer: function (row, column, value) {
				        return '<div>' + (row + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'partyCode', width: '13%',},
				{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: 'fullName', width: '25%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'postalAddressName', sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: '15%', editable: true,
					cellsrenderer: function(row, colum, value){
						value?value=mapStatusItem[value]:value;
				        return '<span>' + value + '</span>';
					},
					createfilterwidget: function (column, htmlElement, editor) {
    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' ,
                            renderer: function (index, label, value) {
                            	if (index == 0) {
                            		return value;
								}
							    return mapStatusItem[value];
			                }
    		        	});
					}
				}
			"/>
<@jqGrid filtersimplemode="true" id="jqxgridCustomerSelected" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="false" clearfilteringbutton="false"
url="" initrowdetails = "false" rowdetailsheight="200" viewSize="10"
/>