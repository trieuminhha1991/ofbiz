<#assign localeStr = "VI" />

<#if locale = "en">
    <#assign localeStr = "EN" />
<#elseif locale= "en_US">
	<#assign localeStr = "EN" />
</#if>
<script>
	var facilityId = '${parameters.facilityId?if_exists}';
	var getLocalization = function () {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
	    localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
	    localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
	    localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
	    localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
	    localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
	    localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
	    localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
	    localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}";
	    localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
	    localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
	    localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	    localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.Groupsheaderstring)}";
	    return localizationobj;
	}
</script>
<div id="detailItems">
	<#assign dataField="[
					{ name: 'partyId', type: 'string'},
					{ name: 'firstName', type: 'string' },
					{ name: 'lastName', type: 'string'},
					{ name: 'middleName', type: 'string'},
	                { name: 'birthDate', type: 'date', other: 'Timestamp'},
					{ name: 'listEntryShipping', type: 'string' },
					{ name: 'listDeliveryEntry', type: 'string' },
					{ name: 'listDlvEntryMissed', type: 'string' },
					
				]"/>
	<#assign columnlist="
					{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.PartyId}', datafield: 'partyId', align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.FullName}', datafield: 'firstName', align: 'left', minwidth: 200,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridDeliverer').jqxGrid('getrowdata', row);
							var fullName;
							if (data.lastName){
								if (fullName){
									fullName = fullName + ' ' + data.lastName;
								} else {
									fullName = data.lastName;
								}
							}
							if (data.middleName){
								if (fullName){
									fullName = fullName + ' ' + data.middleName;
								} else {
									fullName = data.middleName;
								}		
							}
							if (data.firstName){
								if (fullName){
									fullName = fullName + ' ' + data.firstName;
								} else {
									fullName = data.firstName;
								}	
							}
							return '<span>' + fullName + '</span>';
						}
					},
					{ text: '${uiLabelMap.DeliveryEntryTotal}', datafield: 'listDeliveryEntry', align: 'left', width: 300, filterable: false,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridDeliverer').jqxGrid('getrowdata', row);
							var listDeliveryEntry = data.listDeliveryEntry;
							var desc;
							if (listDeliveryEntry.length > 0){
								for (var i = 0; i < listDeliveryEntry.length; i ++){
									if (desc){
										desc = desc + ' | <a href=deliveryEntryDetail?deliveryEntryId=' + listDeliveryEntry[i]+'>' + listDeliveryEntry[i] + '</a>';
									} else {
										desc = '(' + listDeliveryEntry.length + ') <a href=deliveryEntryDetail?deliveryEntryId=' + listDeliveryEntry[i]+'>' + listDeliveryEntry[i] + '</a>';
									}
								}
							} 
							if (desc){
								return '<span>' + desc + '</span>';
							} else {
								return '<span>' + 0 + '</span>';
							}
						}
					},
					{ text: '${uiLabelMap.EntryShippingTotal}', datafield: 'listEntryShipping', align: 'left', width: 300, filterable: false,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridDeliverer').jqxGrid('getrowdata', row);
							var listEntryShipping = data.listEntryShipping;
							var desc = '';
							for (var i = 0; i < listEntryShipping.length; i ++){
								if (desc){
									desc = desc + ' | <a href=deliveryEntryDetail?deliveryEntryId=' + listEntryShipping[i] +'>' + listEntryShipping[i] + '</a>';
								} else {
									desc ='(' + listEntryShipping.length + ') <a href=deliveryEntryDetail?deliveryEntryId=' + listEntryShipping[i] +'>' + listEntryShipping[i] + '</a>';
								}
							}
							if (desc){
								return '<span>' + desc + '</span>';
							} else {
								return '<span>' + 0 + '</span>';
							}
						}
					},
					{ text: '${uiLabelMap.EntryShippingMissed}', datafield: 'listDlvEntryMissed', align: 'left', width: 300, filterable: false,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridDeliverer').jqxGrid('getrowdata', row);
							var listDlvEntryMissed = data.listDlvEntryMissed;
							var desc = '';
							for (var i = 0; i < listDlvEntryMissed.length; i ++){
								if (desc){
									desc = desc + ' | <a href=deliveryEntryDetail?deliveryEntryId=' + listDlvEntryMissed[i] +'>' + listDlvEntryMissed[i] + '</a>';
								} else {
									desc ='(' + listDlvEntryMissed.length + ') <a href=deliveryEntryDetail?deliveryEntryId=' + listDlvEntryMissed[i] +'>' + listDlvEntryMissed[i] + '</a>';
								}
							}
							if (desc){
								return '<span>' + desc + '</span>';
							} else {
								return '<span>' + 0 + '</span>';
							}
						}
					},
					
				"/>
	
	<@jqGrid id="jqxgridDeliverer" filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true" addrow="false" alternativeAddPopup="alterpopupWindow"
		url="jqxGeneralServicer?sname=getDeliverers&roleTypeId=${parameters.roleTypeId?if_exists}" customTitleProperties="ListDeliverers" id="jqxgridDeliverer"
	/>
</div>
<div id="selectViewMethod" class="hide popup-bound">
	<div>${uiLabelMap.ViewAs}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid margin-top10">
				<div id="viewMethodId" style="width: 100%; margin-left: 10px" class="green-label"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="viewCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
        	<button id="viewSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="alterpopupWindow" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.AddNewDeliverer}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="overflow-x: hidden;">
	    	<div class="row-fluid margin-top10">
	    		<div class="span6">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
						</div>
						<div class="span7">
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
						</div>
						<div class="span7">
						</div>
					</div>
				</div>
				<div class="span6">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
						</div>
						<div class="span7">
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
						</div>
						<div class="span7">
						</div>
					</div>
				</div>
			</div>
		    <div class="form-action popup-footer">
	            <button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	            <button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	        </div>
	    </div>
	</div>
</div>
<script>
</script>