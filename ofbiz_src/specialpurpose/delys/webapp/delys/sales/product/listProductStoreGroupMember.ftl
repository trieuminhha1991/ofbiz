<#assign listProductStore = delegator.findList("ProductStore",null,null,null,null,false)/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script>
	<#if listProductStore?exists>
		var ProductStoreData = [
              <#list listProductStore as lPS>
                {
                	storeName : "${lPS.storeName?if_exists}",
                	productStoreId : "${lPS.productStoreId?if_exists}",
                },
              </#list>
        ]
		<#else>
			ProductStoreData = [];
	</#if>
	var sourceProductStoreData = {
			localdata : ProductStoreData,
			datatype :"array",
			datafield: [
	            {name : "productStoreId", type : "String"},
	            {name : "storeName", type : "String"}
            ]
	};
	var dataAdapterProductStoreData = new $.jqx.dataAdapter(sourceProductStoreData);
</script>

<#assign dataField = "[
		{name : 'productStoreId', type : 'String'},
		{name : 'storeName', type : 'String'},
		{name :'fromDate', type :'date', other:'Timestamp'},
		{name : 'thruDate', type : 'date', other: 'Timestamp'},
		{name : 'productStoreGroupId', type : 'String'}
]"/>
<#assign columnlist = "
		{text :'${uiLabelMap.DANo}', cellsrenderer: function(row,columm,value){
				var data = $(\"#jqxgrid\").jqxGrid(\'getrowdata\',row);
				var index = data.uid + 1;
				return '<span>' + index + '</span>';
			}
		},
		{text : '${uiLabelMap.DAProductStoreId}', dataField: 'productStoreId', cellsrenderer : function(row,column,value){
			var data = $(\"#jqxgrid\").jqxGrid(\'getrowdata\',row);
			return \"<span><a href= '/delys/control/editProductStore?productStoreId=\" + data.productStoreId +\"'>\" + data.productStoreId + \"</a></span>\";
		}},
		{text : '${uiLabelMap.DAStoreName}', dataField: 'storeName'},
		{text : '${uiLabelMap.DACommonFromDate}',dataField : 'fromDate',cellsformat :'dd/MM/yyyy HH:mm:ss'},
		{text : '${uiLabelMap.DAToDate}', dataField :'thruDate',cellsformat :'dd/MM/yyyy hh:mm:ss'}
"/>																			
<@jqGrid filterable ="true" editable = "false" clearfilteringbutton="true" alternativeAddPopup="alterpopupWindowCreate" columnlist=columnlist dataField=dataField addrow="true" addType="popup" addrefresh="true" 
	url="jqxGeneralServicer?sname=JQGetListProductStoreGroupMember&productStoreGroupId=	" mouseRightMenu="true" contextMenuId="contextMenu"
	createUrl="jqxGeneralServicer?sname=createProductStoreGroupMember&jqaction=C" addColumns ="productStoreGroupId;productStoreId;fromDate(java.sql.Timestamp)"
	removeUrl="jqxGeneralServicer?sname=JQupdateProductStoreGroupMember&jqaction=D" deleteColumn="productStoreId;fromDate(java.sql.Timestamp);productStoreGroupId" deleterow="true"
/>
<div id="alterpopupWindowCreate" style="display:none">
<div>${uiLabelMap.DAAddToProductStoreGroupChild}</div>
	<div style="overflow: hidden;">
	<form id="alterpopupWindowCreateform" class="form-horizontal">
		<div class="row-fluid  form-window-content">
			<div class="span12">
				<div class="row-fluid margin-bottom10">
				
					<div class='span5 align-right asterisk'>
			        	${uiLabelMap.DAProductStoreId}
			        </div>
					<div class="span7">
						<div id="productStoreGroupMemberAdd">
							<div style="border-color: transparent;" id="productStoreGroupMemberAddGrid"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span5 align-right asterisk'>
		            	${uiLabelMap.DACommonFromDate}
		            </div>
					<div class="span7 span7edit">
						<div id="fromDateAdd" name="fromDateAdd"></div>
					</div>
				</div>
			</div>
		</div>
	</form>
	<div class="form-action">
		<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
	</div>
</div>
<div id="alterpopupWindowEdit" style="display:none">
<div>${uiLabelMap.DAAddToProductStoreGroupChild}</div>
	<div style="overflow: hidden;">
	<form id="alterpopupWindowEditform" class="form-horizontal">
		<div class="row-fluid  form-window-content">
			<div class="span12">
				<div class="row-fluid margin-bottom10">
					<div class='span5 align-right asterisk'>
			        	${uiLabelMap.DAProductStoreId}
			        </div>
					<div class="span7">
						<div id="productStoreGroupMemberEdit">
							<div style="border-color: transparent;" id="productStoreGroupMemberEditGrid"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span5 align-right asterisk'>
		            	${uiLabelMap.DACommonFromDate}
		            </div>
					<div class="span7 span7edit">
						<input type="text" id="fromDateEdit"/>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span5 align-right asterisk'>
		            	${uiLabelMap.DAToDate}
		            </div>
					<div class="span7 span7edit">
						<div id="thruDateEdit" name="thruDateEdit"></div>
					</div>
				</div>
			</div>
		</div>
	</form>
	<div class="form-action">
		<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button id="alterCancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="alterSave1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
	</div>
</div>
<div id="contextMenu" style="display:none">
	<ul>
		<li><i class="fa fa-edit"></i>${StringUtil.wrapString(uiLabelMap.DAEditStatus)}</li>
	</ul>
</div>
<style>
	#fromDateAdd > div > input{
		width : 221px !important;
	};
	#fromDateEdit > div > input{
		width : 221px !important;
	};
	#thruDateEdit > div > input{
		width : 221px !important;
	}
</style>
<script>
	var formatDate = function(val){
		   var date = new Date(val);
		   var newFormat;
		   if(date){
		    newFormat = date.format('yyyy-mm-dd HH:MM:ss');
		   }
		   return newFormat;
	};
//	function
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#alterpopupWindowCreate").jqxWindow({width :480,height :235,resizable: false,isModal: true,autoOpen: false,cancelButton: $("#alterCancel"),modalOpacity : 0.8,theme : theme});
	$("#productStoreGroupMemberAdd").jqxDropDownButton({width: 248, height: 23});
	$("#productStoreGroupMemberAddGrid").jqxGrid({width:480, height:350,source:dataAdapterProductStoreData,pageable: true,columnsresize: true,filterable: true,showfilterrow: true,
		columns : [
           {text:'${uiLabelMap.DAProductStoreId}', datafield :'productStoreId'},
           {text:'${uiLabelMap.DAStoreName}', datafield : 'storeName'}
       ],
	});
	$("#productStoreGroupMemberAddGrid").on('rowselect', function(event){
		var args = event.args;
		var data = $('#productStoreGroupMemberAddGrid').jqxGrid('getrowdata', args.rowindex);
		var dropdownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + data.productStoreId + '</div>';
		$("#productStoreGroupMemberAdd").jqxDropDownButton('setContent', dropdownContent);
		return;
	});
	$("#fromDateAdd").jqxDateTimeInput({width: 248, height: 23, theme: theme,allowNullDate: true, value: null,formatString : 'dd/MM/yyyy HH:mm:ss'});
	$("#alterpopupWindowCreateform").jqxValidator({
		rules : [
		         {
		        	 input : '#productStoreGroupMemberAdd',
		        	 message : '${uiLabelMap.DAchooseProductStoreId}',
		        	 action: 'change,close,keyup',
		        	 rule : function(input,commit){
		        		 if(!$('#productStoreGroupMemberAdd').val()){
		        			 return false;
		        		 }
		        		 return true;
		        	 }
		         },
		         {
		        	 input : '#fromDateAdd',
		        	 message : '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreatherOrEqualToDay)}',
		        	 action : 'change,close,keyup',
		        	 rule : function (input, commit) {
								var now = new Date();
//								now.setHours(0,0,0,0);
				        		if($(input).jqxDateTimeInput('getDate') < now){
				        			return false;
				        		}
				        		return true;
			    			}
		         }
         ]
	});
	$("#alterSave").click(function(){
		$('#alterpopupWindowCreateform').jqxValidator('validate');
	});
	$("#alterpopupWindowCreateform").on('validationSuccess', function(event){
		var row={};
		row ={
				productStoreGroupId : '${parameters.productStoreGroupId?if_exists}',
				productStoreId : $("#productStoreGroupMemberAdd").val(),
				fromDate : $("#fromDateAdd").jqxDateTimeInput('getDate')
				};
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindowCreate").jqxWindow('close');
	});
//	done creatform
//	start edit form
	$("#contextMenu").jqxMenu({width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function(event){
		var args = event.args;
		var tmpkey = $.trim($(args).text());
		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
		var fromDate = formatDate(data.fromDate);
		if(tmpkey == '${StringUtil.wrapString(uiLabelMap.DAEditStatus)}'){
			$("#alterpopupWindowEdit").jqxWindow('open');
			$("#alterpopupWindowEdit").jqxWindow({width :480,height :300,resizable: false,isModal: true,autoOpen: false,cancelButton: $("#alterCancel1"),modalOpacity : 0.8,theme : theme});
			$("#productStoreGroupMemberEdit").html('<div>'+data.productStoreId+'</div>');
			$("#fromDateEdit").jqxInput({width: 243, height: 19, disabled :true});
			$("#fromDateEdit").jqxInput('val', fromDate);
			$("#thruDateEdit").jqxDateTimeInput({width: 248, height: 23, theme: theme,allowNullDate: true, value: null,formatString : 'dd/mm/yyyy hh:mm:ss'});
		}
	});
	$("#alterpopupWindowEditform").jqxValidator({
		rules : [
		         {
		        	 input : '#thruDateEdit',
		        	 message : '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreatherOrEqualToDay)}',
		        	 action : 'keyup,change,close',
		        	 rule : function(input,commit){
				        		 var now = new Date();
		//							now.setHours(0,0,0,0);
				        		if($(input).jqxDateTimeInput('getDate') < now){
				        			return false;
				        		}
				        		return true;
		        	 }
		         },
		         {
		        	 input : '#thruDateEdit',
		        	 message : '${StringUtil.wrapString(uiLabelMap.DArequiredValueGreaterThanFromDate)}',
		        	 action : 'keyup,change,close',
		        	 rule : function(input,commit){
		        		 		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		        		 		var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
		        		 		if($(input).jqxDateTimeInput('getDate') <= data.fromDate){
		        		 			return false;
		        		 		}
		        		 		return true;
		        	 }
		         }
         ]
	});
	$("#alterSave1").click(function(){
		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
		if($('#alterpopupWindowEditform').jqxValidator('validate')){
			var thruDateUpdate,fromDate;
			if($('#thruDateEdit').jqxDateTimeInput('getDate') && typeof($('#thruDateEdit').jqxDateTimeInput('getDate')) != 'undefined'){
				thruDateUpdate = formatDate($('#thruDateEdit').jqxDateTimeInput('getDate').getTime());
			}
			if(data.fromDate && typeof(data.fromDate) != 'undefined'){
				fromDate = formatDate(data.fromDate);
			}
			var row ={};
			row = {
				fromDate : fromDate,
				productStoreId : data.productStoreId,
				productStoreGroupId : '${parameters.productStoreGroupId?if_exists}',
				thruDateUpdate : thruDateUpdate	 
			};
			$.ajax({
				type : "POST",
				url : "updateProductStoreMember",
				data : row,
				success : function(data, status, xhr){
					if(data._ERROR_MESSAGE_LIST_){
						$('#jqxgrid').jqxGrid('updatebounddata');
						$('#container').empty();
						$('#jqxNotification').jqxNotification({ template: 'error'});
	                    $("#notificationContent").text(data._ERROR_MESSAGE_LIST_);
	                    $("#jqxNotification").jqxNotification('open');
					}else {
						$('#container').empty();
	                    $('#jqxNotification').jqxNotification({ template: 'success'});
	                    $("#notificationContent").text('updateSuccess');
	                    $("#jqxNotification").jqxNotification('open');
	                    $('#jqxgrid').jqxGrid('updatebounddata');
					}
					
				}
			});
			$("#alterpopupWindowEdit").jqxWindow('close');
		}
	});
	<!-- -->
</script>