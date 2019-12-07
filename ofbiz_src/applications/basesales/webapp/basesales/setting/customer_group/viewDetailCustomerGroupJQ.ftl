
<#assign dataField = "[{name: 'partyIdTo', type: 'string'}, 
{name: 'partyIdFrom', type: 'string'},
{name: 'groupName', type: 'string'},
{name: 'fromDate', type: 'date', other: 'Timestamp'},
{name: 'thruDate', type: 'date', other: 'Timestamp'},
]"/>

<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DAPartyId)}', dataField: 'partyIdTo', width: '20%', editable: false,}, 
{text: '${StringUtil.wrapString(uiLabelMap.DAPartyIdFrom)}', dataField: 'partyIdFrom', hidden: true},
{text: '${StringUtil.wrapString(uiLabelMap.DAGroupName)}', dataField: 'groupName', width: '30%'},
{text: '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', dataField: 'fromDate', width: '25%', cellsformat:'dd/MM/yyyy', filtertype: 'range'},
{text: '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', dataField: 'thruDate', width: '25%', cellsformat:'dd/MM/yyyy', filtertype: 'range'},
"/>

<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup" mouseRightMenu="true" contextMenuId="contextMenu" 
url="jqxGeneralServicer?sname=getListCustomerGroupDetail&partyFrom=${parameters.partyId}&hasrequest=Y"
/> 

<div id="contextMenu" style="display:none">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa-pencil open-sans"></i>${StringUtil.wrapString(uiLabelMap.DACancel)}</li>
	</ul>
</div>

<div id="popupDeleteMember" style="display : none;">
<div>
	${uiLabelMap.CommonDelete}
</div>
<div style="overflow: hidden;">
	<form id="DeleteMemberForm" class="form-horizontal">
		<input type="hidden" value="${parameters.roleTypeId?if_exists}" id="roleTypeId" name="roleTypeId">
		<label class="">${uiLabelMap.DAAreYouSureDelete}</label>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button type="button" id="alterCancel3" class='btn btn-danger form-action-button pull-right'>
						<i class='fa-remove'></i> ${uiLabelMap.Cancel}
					</button>
					<button type="button" id="alterSave3" class='btn btn-primary form-action-button pull-right'>
						<i class='fa-pencil'></i> ${uiLabelMap.OK}
					</button>
				</div>
			</div>
		</div>
	</form>
</div>
</div>

<script>
	var windowDeleteMember =  $('#popupDeleteMember').jqxWindow({ width: 320, height : 120,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel3"), modalOpacity: 0.7 });
	var deleteSuccess = "${StringUtil.wrapString(uiLabelMap.DADeleteSuccess)}";
	
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
	    var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    var tmpKey = $.trim($(args).text());
	    if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
	    	$("#jqxgrid").jqxGrid('updatebounddata');
	    }else if (tmpKey == '${StringUtil.wrapString(uiLabelMap.DACancel)}') {
			var wtmp = window;
		   	var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
		   	var tmpwidth = $('#popupDeleteMember').jqxWindow('width');
    	   	windowDeleteMember.jqxWindow('open');
		}
	});
	
	$('#alterSave3').click(function () {
		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	   	if (rowindex >= 0) {
		   	deleteCG();
           	$('#popupDeleteMember').jqxWindow('hide');
           	$('#popupDeleteMember').jqxWindow('close');
	   	}
    });
	
	function deleteCG(){
		var row = $("#jqxgrid").jqxGrid('getselectedrowindexes');
		var success = deleteSuccess;
		var cMemberr = new Array();
			var data2 = $("#jqxgrid").jqxGrid('getrowdata', row);
			var map = {};
			map['partyIdFrom'] = data2.partyIdFrom;
			map['partyIdTo'] = data2.partyIdTo;
			map['fromDate'] = data2.fromDate.getTime();
			if(!data2.thruDate){
				map['thruDate'] = data2.fromDate.getTime();
			}else{
				map['thruDate'] = data2.thruDate.getTime();
			}
			cMemberr = map;
		if (cMemberr.length <= 0){
			return false;
		} else {
			cMemberr = JSON.stringify(cMemberr);
			jQuery.ajax({
		        url: 'deleteCG',
		        type: 'POST',
		        async: true,
		        data: {
		        		'cMemberr': cMemberr,
	        		},
		        success: function(res) {
		        	var message = '';
					var template = '';
					if(res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_){
						if(res._ERROR_MESSAGE_LIST_){
							message += res._ERROR_MESSAGE_LIST_;
						}
						if(res._ERROR_MESSAGE_){
							message += res._ERROR_MESSAGE_;
						}
						template = 'error';
					}else{
						message = success;
						template = 'success';
						$("#jqxgrid").jqxGrid('updatebounddata');
		        		$("#jqxgrid").jqxGrid('clearselection');
					}
					updateGridMessage('jqxgrid', template ,message);
		        },
		        error: function(e){
		        	console.log(e);
		        }
		    });
		}
	}
</script>