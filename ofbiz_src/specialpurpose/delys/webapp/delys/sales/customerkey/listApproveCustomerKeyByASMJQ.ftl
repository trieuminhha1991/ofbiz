<div class="row-fluid" id="container"></div>
<#assign dataField = "[{name: 'partyIdFrom', type: 'string'}, 
{name: 'partyId', type: 'string'},
{name: 'fullName', type: 'string'},
{name: 'relStatusId', type:'string'},
{name: 'fromDate', type: 'date', other: 'Date'},
{name: 'thruDate', type: 'date', other: 'Date'}

]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.partyIdFrom)}', dataField: 'partyIdFrom', width: '20%', editable: false,hidden: true}, 
{text: '${StringUtil.wrapString(uiLabelMap.partyId)}', dataField: 'partyId', width: '16%'},
{text: '${StringUtil.wrapString(uiLabelMap.fullName)}', dataField: 'fullName', width: '20%'},
{text: '${StringUtil.wrapString(uiLabelMap.statusId)}', dataField: 'relStatusId', width: '20%'},
{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', dataField: 'fromDate', cellsformat: 'd', width: '20%', filtertype:'range', columntype: 'datetimeinput',
	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		editor.jqxDateTimeInput({ });
	}
},
{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', dataField: 'thruDate', cellsformat: 'd', width: '20%', filtertype:'range', columntype: 'datetimeinput',
	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		editor.jqxDateTimeInput({ });
	}
},
"/>

<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="false" addType="popup"
url="jqxGeneralServicer?sname=JQGetListCustomersKeyByAsm" mouseRightMenu="false"  selectionmode="checkbox" viewSize="10" pagesizeoptions="['5', '15', '30', '50']" 
/> 

<div class="form-action" style="margin: 10px 10px 0 0;">
	<div class='row-fluid'>
		<div class="span12 margin-top10">
			<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.DANotApprove}
			</button>
			<button type="button" id="alterApprove" class='btn btn-primary form-action-button pull-right'>
				<i class='fa-check'></i> ${uiLabelMap.DAApprove}
			</button>
		</div>
	</div>
</div>
<div id="notifyMissInfoId" style="display: none;">
	<div>
		${uiLabelMap.approveCancel1}
	</div>
</div>
<div id="notifyId" style="display: none;">
	<div>
		${uiLabelMap.approveSuccessfully}
	</div>
</div>
<div id="notifyCancel" style="display: none;">
	<div>
		${uiLabelMap.cancelCustomerKey}
	</div>
</div>
<script type="text/javascript">
	$("#alterApprove").click(function(){
		approvedCustomerKeys();
	});
	
	$("#alterCancel1").click(function(){
		cancelCustomerKey();
	});
	
	$("#notifyCancel").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#container", autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 5000 }); 
	$("#notifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#container", autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 5000 }); 
	$("#notifyMissInfoId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#container", autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 5000 }); 

	function approvedCustomerKeys(){
		var row = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		var listAppCustomerKeys = new Array();
		
		for(var i = 0; i < row.length; i++){
			var data = $('#jqxgrid').jqxGrid('getrowdata', row[i]);
//			console.log(data);
				var map = {};
				map['roleTypeIdFrom'] = 'SUPPLIER';
				map['roleTypeIdTo'] = 'DELYS_CUSTOMER_GT';
				map['fromDate'] = data.fromDate.getTime();
				map['partyIdTo'] = data.partyId;
				map['partyIdFrom'] = 'company';
				listAppCustomerKeys[i] = map;
		}
		if (listAppCustomerKeys.length <= 0){
			$("#notifyMissInfoId").jqxNotification("open");
			return false;
		} else {
			listAppCustomerKeys = JSON.stringify(listAppCustomerKeys);
			jQuery.ajax({
		        url: "approveCustomerKeyByAsm",
		        type: "POST",
		        async: true,
		        data: {
		        		'listAppCustomerKeys': listAppCustomerKeys,
		        		},
		        success: function(res) {
		        	var newCustomerKeyId = res.newCustomerKeyId;
		        	$("#notifyId").jqxNotification("open");
		        	$('#jqxgrid').jqxGrid('updatebounddata');
		        	$('#jqxgrid').jqxGrid('clearselection');
		        },
		        error: function(e){
		        }
		    });
		}
	}
	function cancelCustomerKey(){
		var row = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		var listAppCustomerKeys2 = new Array();
		
		for(var i = 0; i < row.length; i++){
			var data = $('#jqxgrid').jqxGrid('getrowdata', row[i]);
//			console.log(data);
				var map = {};
				map['roleTypeIdFrom'] = 'SUPPLIER';
				map['roleTypeIdTo'] = 'DELYS_CUSTOMER_GT';
				map['fromDate'] = data.fromDate.getTime();
				map['partyIdTo'] = data.partyId;
				map['partyIdFrom'] = 'company';
				listAppCustomerKeys2[i] = map;
		}
		if (listAppCustomerKeys2.length <= 0){
			$("#notifyMissInfoId").jqxNotification("open");
			return false;
		} else {
			listAppCustomerKeys2 = JSON.stringify(listAppCustomerKeys2);
			jQuery.ajax({
		        url: "cancelCustomerKey",
		        type: "POST",
		        async: true,
		        data: {
		        		'listAppCustomerKeys2': listAppCustomerKeys2,
		        		},
		        success: function(res) {
		        	var newCustomerKeyId = res.newCustomerKeyId;
		        	$("#notifyCancel").jqxNotification("open");
		        	$('#jqxgrid').jqxGrid('updatebounddata');
		        	$('#jqxgrid').jqxGrid('clearselection');
		        },
		        error: function(e){
		        }
		    });
		}
	}
</script>