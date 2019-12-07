<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField="[{ name: 'targetHeaderId', type: 'string'},
					{ name: 'targetTypeId', type: 'string'},
					{ name: 'ofYear', type: 'string'},
					{ name: 'targetHeaderName', type: 'string'},
					{ name: 'createBy', type: 'string'},
					{ name: 'createDate', type: 'date', other: 'Timestamp'}
					]"/>
<#assign columnlist="
					{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.targetHeaderYear)}', datafield: 'ofYear', align: 'center', width: 250},
					{ text: '${StringUtil.wrapString(uiLabelMap.targetHeaderName)}', datafield: 'targetHeaderName', align: 'center' },
					{ text: '${StringUtil.wrapString(uiLabelMap.CreationDate)}', datafield: 'createDate', align: 'center', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${StringUtil.wrapString(uiLabelMap.CreateBy)}', datafield: 'createBy', align: 'center', width: 250 }
					"/>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" viewSize="15"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" editrefresh="true" addrefresh="true"
		url="jqxGeneralServicer?sname=JQGetListTargetCompany&targetTypeId=${parameters.targetTypeId?if_exists}"
		createUrl="jqxGeneralServicer?sname=createTargetHeader&jqaction=C"
		updateUrl="jqxGeneralServicer?sname=updateTargetHeader&jqaction=U"
		addColumns="targetTypeId;targetHeaderName;ofYear;createBy"
		editColumns="targetHeaderId;targetHeaderName;ofYear;createBy"
		contextMenuId="contextMenu" mouseRightMenu="true"
		/>

<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.AddNewProductSupplier}</div>
	<div style="overflow-y: hidden;">

		<div class="row-fluid">
 			<div class="span12">
	 			<div class="span4"><label class="text-right asterisk">${uiLabelMap.targetHeaderName}</label></div>
	 			<div class="span8"><input type="text" id="txtTargetHeaderName" /></div>
 			</div>
		</div>
		
		<div class="row-fluid">
			<div class="span12">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.targetHeaderYear}</label></div>
				<div class="span8"><input type="text" id="txtTargetHeaderYear" /></div>
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="span12">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.CreateBy}</label></div>
				<div class="span8"><input type="text" id="txtCreateBy" /></div>
			</div>
		</div>
		
 		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
 		<div class="row-fluid">
            <div class="span12 margin-top10">
            	<div class="span12">
            		<button id='alterCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.btnCancel}</button>
            		<button id='alterSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
        		</div>
            </div>
    	</div>
   </div>
</div>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id='viewDetails'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewDetails}</li>
	</ul>
</div>

<script>
	$("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 30, autoOpenPopup: false, mode: 'popup'});
	$("#jqxgrid").on('contextmenu', function () {
	    return false;
	});
	$("#viewDetails").on("click", function() {
		var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var data = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
        var targetHeaderId = data.targetHeaderId;
        var targetTypeId = data.targetTypeId;
        window.location.href = "EditTargetCompany?targetHeaderId=" + targetHeaderId + "&targetTypeId=" + targetTypeId;
	});
	
	$("#alterpopupWindow").jqxWindow({ theme:'olbius',
	    width: 500, maxWidth: 1000, height: 230, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
	});
	$("#alterSave").click(function () {
		if ($('#alterpopupWindow').jqxValidator('validate')) {
			var row = new Object();
			var targetTypeId = "${parameters.targetTypeId?if_exists}";
			row.targetTypeId = targetTypeId;
			row.targetHeaderName = $("#txtTargetHeaderName").val();
			row.ofYear = $("#txtTargetHeaderYear").val();
			row.createBy = $("#txtCreateBy").val();
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
            $("#jqxgrid").jqxGrid('clearSelection');
            $("#jqxgrid").jqxGrid('selectRow', 0);
            $("#alterpopupWindow").jqxWindow('close');
		}
	});
	$('#alterpopupWindow').jqxValidator({
        rules: [
                { input: '#txtTargetHeaderName', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
                { input: '#txtTargetHeaderYear', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
                { input: '#txtCreateBy', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' }
               ]
    });
</script>