<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign dataField = "[{name: 'enumId', type: 'string'}, 
{name: 'description', type: 'string'},
{name: 'sequenceId', type: 'number'},

]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.salesChannelId)}', dataField: 'enumId', width: '25%', editable:false,}, 
{text: '${StringUtil.wrapString(uiLabelMap.salesChannelName)}', dataField: 'description'},
{text: '${StringUtil.wrapString(uiLabelMap.sequenceId)}', dataField: 'sequenceId', width: '25%', filterable: false,  columntype: 'numberinput', cellsalign: 'center',
	validation: function (cell, value) {
        if (value <= 0) {
            return { result: false, message: '${StringUtil.wrapString(uiLabelMap.sequenceIdGreaterThanZero)}' };
        }
        return true;
	},
	createeditor: function (row, cellvalue, editor) {
        editor.jqxNumberInput({ decimalDigits: 0, digits: 2, spinButtons: false, width: '12%'});
    }	
 },
"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup" mouseRightMenu="true" contextMenuId="contextMenu"
url="jqxGeneralServicer?sname=JQGetListSalesChannel" editable="true"
createUrl="jqxGeneralServicer?sname=createSalesChannelJQ&jqaction=C" addColumns="enumId;description"
updateUrl="jqxGeneralServicer?sname=editSalesChannelJQ&jqaction=U" editColumns="enumId;description;sequenceId"
/> 
<style>
	.line-height-25{
		line-height: 25px;
	}
	.m-bot-5{
		margin-bottom: 5px;
	}
</style>
<div id="alterpopupWindow1" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="SalesChannelForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.salesChannelId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="salesChannelIdAdd" />
						</div>
					</div>

					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.salesChannelName}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="salesChannelNameAdd" />
						</div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave1" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<div id='contextMenu' style="display: none">
	<ul>
		<li>
			${StringUtil.wrapString(uiLabelMap.DARefresh)}
		</li>
		<li>
			${StringUtil.wrapString(uiLabelMap.createGroupChannel)}
		</li>
	</ul>
</div>
  
<div id="popupCreateGroupChannel" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="CreateGroupChannelForm" class="form-horizontal">
			<input type="hidden" value="${parameters.enumId?if_exists}" id="enumId" name="enumId" />
			<div class="row-fluid no-left-margin">
				<div class="row-fluid no-left-margin">
					<label class="span5 align-right asterisk">${uiLabelMap.descriptionGroupChannel}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<input id="descriptionGroupChannel" />
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel2" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'></i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave2" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-pencil'></i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
  
<script type="text/javascript">
	$('#alterpopupWindow1').jqxWindow({ width: 500, height : 180,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7 });
	$("#salesChannelIdAdd").jqxInput({placeHolder: "${StringUtil.wrapString(uiLabelMap.salesChannelId)}", height: 19, width: 200, minLength: 1 });
	$("#salesChannelNameAdd").jqxInput({placeHolder: "${StringUtil.wrapString(uiLabelMap.salesChannelName)}", height: 19, width: 200, minLength: 1 });
	
	 $('#SalesChannelForm').jqxValidator({
		   rules : [
		            {input: '#salesChannelIdAdd', message: '${StringUtil.wrapString(uiLabelMap.salesChannelIdNotEmpty)}', action: 'blur', rule: 
	        			function (input, commit) {
	        				var value = $(input).val();
							if(/^\s*$/.test(value)){
								return false;
							}
							return true;
						}
					},
					{input: '#salesChannelNameAdd', message: '${StringUtil.wrapString(uiLabelMap.salesChannelNameNotEmpty)}', action: 'blur', rule: 
	        			function (input, commit) {
	        				var value = $(input).val();
							if(/^\s*$/.test(value)){
								return false;
							}
							return true;
						}
					},
			]
		});
		
	$('#alterSave1').click(function(){
		$('#SalesChannelForm').jqxValidator('validate');
	});
	
	$('#SalesChannelForm').on('validationSuccess',function(){
		var row = {};
		row = {
				enumId : $('#salesChannelIdAdd').val(),
				description : $('#salesChannelIdAdd').val(),
		};
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		$("#jqxgrid").jqxGrid('clearSelection');                        
		$("#jqxgrid").jqxGrid('selectRow', 0);  
		$("#alterpopupWindow1").jqxWindow('close');
		$('#salesChannelIdAdd').val('');
		$('#salesChannelNameAdd').val('');
		setTimeout($("#jqxgrid").jqxGrid('updatebounddata'), 3000);
		
	});
	
	$('#alterCancel1').click(function(){
		$('#salesChannelIdAdd').val('');
		$('#salesChannelNameAdd').val('');
	});
	
	$('#alterpopupWindow1').on('close',function(){
		$('#SalesChannelForm').jqxValidator('hide');
		$('#jqxgrid').jqxGrid('refresh');
		
	});
	
	$("#contextMenu").jqxMenu({ width: 230, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
	    var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    var tmpKey = $.trim($(args).text());
	    if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
	    	$("#jqxgrid").jqxGrid('updatebounddata');
	    }  else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.createGroupChannel)}") {
	    		var wtmp = window;
	    	   var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    	   var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
	    	   var tmpwidth = $('#popupCreateGroupChannel').jqxWindow('width');
	    	   $('#popupCreateGroupChannel').jqxWindow({ width: 500, height : 135,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7 });
	    	   $("#popupCreateGroupChannel").jqxWindow('open');
	    	   $('#descriptionGroupChannel').jqxInput({width : '193px',height : '19px'});
	    		
	    	   $("#alterSave2").click(function () {
	    		   $('#CreateGroupChannelForm').jqxValidator('validate');
	    		   $('#CreateGroupChannelForm').jqxValidator({
	    			   rules : [
	    			            {input: '#descriptionGroupChannel', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'blur', rule: 
				        			function (input, commit) {
				        				var value = $(input).val();
										if(/^\s*$/.test(value)){
											return false;
										}
										return true;
									}
								},
						]
					});
	    				
	    		   $('#CreateGroupChannelForm').on('validationSuccess',function(){
	    			   var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    			   if (rowindex >= 0) {
	    				   // var data1 = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
	    				   // var row = {
			            		// enumId: data1.enumId,
			            		// description : $('#descriptionGroupChannel').val(),
	    				   // };
	    				   // var rowID = $('#jqxgrid').jqxGrid('getrowid', rowindex);
	    				   abc();
				           $('#CreateGroupChannelForm').jqxValidator('hide');
				           $("#popupCreateGroupChannel").jqxWindow('hide');
				           $("#popupCreateGroupChannel").jqxWindow('close');
	    			   }
				});
		    });
    	}
	});
	
	function abc(){
		var row = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		var cGroupChannel = new Array();
		for(var i = 0; i < row.length; i++){
			var data = $('#jqxgrid').jqxGrid('getrowdata', row[i]);
				var map = {};
				map['enumId'] = data.enumId;
				map['description'] = $('#descriptionGroupChannel').val();
				cGroupChannel[i] = map;
		}
		if (cGroupChannel.length <= 0){
			// $("#notifyMissInfoId").jqxNotification("open");
			return false;
		} else {
			cGroupChannel = JSON.stringify(cGroupChannel);
			jQuery.ajax({
		        url: "createGroupChannelJQ",
		        type: "POST",
		        async: true,
		        data: {
		        		'cGroupChannel': cGroupChannel,
	        		},
		        success: function(res) {
		        	// var newCustomerKeyId = res.newCustomerKeyId;
		        	// $("#notifyId").jqxNotification("open");
		        	$('#jqxgrid').jqxGrid('updatebounddata');
		        	$('#jqxgrid').jqxGrid('clearselection');
		        },
		        error: function(e){
		        }
		    });
		}
	}
	
</script>