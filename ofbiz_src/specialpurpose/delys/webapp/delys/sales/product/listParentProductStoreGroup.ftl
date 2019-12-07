<#assign listproductStoreGroupType = delegator.findList("ProductStoreGroupType", null, null, null, null, false)/>
<#assign listprimaryParentGroup = delegator.findList("ProductStoreGroup", null, null, null, null, false)/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script>
	<#if listproductStoreGroupType?exists>
	var productStoreGroupTypedata = [
		<#list listproductStoreGroupType as lpSGT>
			{
				productStoreGroupTypeId: "${lpSGT.ProductStoreGroupTypeId?if_exists}",
				description: "${lpSGT.description?if_exists}"
			},
		</#list>
	];
	<#else>
	var productStoreGroupTypedata = [];
	</#if>
	<#if listprimaryParentGroup?exists>
		var productStoreGroupdata = [
		    <#list listprimaryParentGroup as lpPG>
		    	{
		    		productStoreGroupId : "${lpPG.productStoreGroupId?if_exists}",
		    		description : "${lpPG.productStoreGroupName?if_exists}" + "[" + "${lpPG.productStoreGroupId?if_exists}" + "]",
		    	},
		    </#list>
		    ];
			<#else> productStoreGroupdata =[];
	</#if>
	<#if listprimaryParentGroup?exists>
		var productStoreGroupdataComplete = [
		    <#list listprimaryParentGroup as lpPG>
		    	{
		    		productStoreGroupId : "${lpPG.productStoreGroupId?if_exists}",
		    		description : "${lpPG.productStoreGroupName?if_exists}" + "[" + "${lpPG.productStoreGroupId?if_exists}" + "]",
		    	},
		    </#list>
		    ];
			<#else> productStoreGroupdataComplete =[];
	</#if>
	var sourceproductStoreGroupdataComplete = {
			localdata : productStoreGroupdataComplete,
			datatype : "array",
			datafield : [
	             {name : 'productStoreGroupId'}
	         ]
	};
	var dataAdapterproductStoreGroupdataComplete = new $.jqx.dataAdapter(sourceproductStoreGroupdataComplete, {
		formatData : function(data){
			if($('#primaryGroupIdAddd').jqxComboBox('searchString') != undefined){
				data.searchKey = $('#primaryGroupIdAddd').jqxComboBox('searchString');
				return data;
			}
		}
	});
</script>
<#assign dataField = 
	"[
	 	{name : 'productStoreGroupId', type : 'String'},
		{name : 'productStoreGroupName', type :'String' },
	 	{name : 'productStoreGroupTypeId', type :'String'},
	 	{name : 'description', type : 'String'},
	 	{name : 'primaryParentGroupId', type :'String'}
	]"/>
<#assign columnlist ="
	{text : 'Id', dataField : 'productStoreGroupId', cellsrenderer : function(row,column,value){
		var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
		return \"<span><a href= '/delys/control/editProductStoreGroup?productStoreGroupId=\" + data.productStoreGroupId +\"'>\" + data.productStoreGroupId + \"</a></span>\";
	}  
	},
	{text : '${uiLabelMap.DAName}', dataField : 'productStoreGroupName', width : '30%'},
	{text : '${uiLabelMap.DAType}', dataField : 'productStoreGroupTypeId', width :'30%', cellsrenderer : function(row,column,value){
			var data = $(\'#jqxgrid\').jqxGrid(\"getrowdata\",row);
			for(var i=0; i < productStoreGroupTypedata.length ;i++){
				if(productStoreGroupTypedata[i].productStoreGroupTypeId == value){
					return '<span>'+ productStoreGroupTypedata[i].description +'</span>';
				}
			}
	}},
	{text : '${uiLabelMap.DADescription}', dataField : 'description', width : '30%'}
"/>
<@jqGrid id="jqxgrid" filterable ="true" editable = "false" clearfilteringbutton="true" alternativeAddPopup="alterpopupWindowCreate" columnlist=columnlist dataField=dataField
		 addrow="true" addType="popup" addrefresh="true"
		 url="jqxGeneralServicer?sname=JQGetListParentProductStoreGroup" mouseRightMenu="true" contextMenuId="contextMenu"
		 createUrl="jqxGeneralServicer?sname=createProductStoreGroup&jqaction=C" addColumns="productStoreGroupName;productStoreGroupTypeId;description;primaryParentGroupId"
/>
<div id="alterpopupWindowCreate1" style="display:none">
<div>CreateNewParentProductStoreGroup</div>
<div style="overflow: hidden;">
<form id="alterpopupWindowCreateform1" class="form-horizontal">
		<div class="row-fluid  form-window-content">
		<div class="span12">
			<div class="row-fluid margin-bottom10">
			
				<div class='span5 align-right asterisk'>
		        	${uiLabelMap.DAProductStoreGroupId}
		        </div>
				<div class="span7">
					<div id="productStoreGroupIdAdd"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				
				<div class='span5 align-right asterisk'>
	            	${uiLabelMap.DAType}
	            </div>
				<div class="span7">
					<div id="productStoreGroupTypeIdAddd1" name="productStoreGroupTypeIdAddd1"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
			
				<div class='span5 align-right asterisk'>
	            		${uiLabelMap.DAPrimaryParentGroup}
	            </div>
				<div class="span7">
					<div id="primaryGroupIdAddd1" name="primaryGroupIdAddd1"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span5 align-right asterisk'>
        			${uiLabelMap.DAName}
        		</div>
				<div class ="span7">
					<input type="text" id="productStoreGroupNameAddd1">	
				</div> 
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span5 align-right asterisk'>
    				${uiLabelMap.DADescription}
    			</div>
				<div class ="span7 span8editor">
					<textarea id="descriptionAddd1"></textarea>
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
</div>
<style>
.span8editor div.jqx-widget-header-olbius{
	background: #FFFFFF !important;
}

</style>

<div id="alterpopupWindowCreate" style="display:none">
	<div>${uiLabelMap.DACreateNewParentProductStoreGroup}</div>
	<div style="overflow: hidden;">
	<form id="alterpopupWindowCreateform" class="form-horizontal">
			<div class="row-fluid  form-window-content">
			<div class="span12">
				<div class="row-fluid margin-bottom10">
					
					<div class='span4 align-right asterisk'>
		            	${uiLabelMap.DAType}
		            </div>
					<div class="span8">
						<div id="productStoreGroupTypeIdAddd" name="productStoreGroupTypeId"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
				
					<div class='span4 align-right asterisk'>
		            		${uiLabelMap.DAPrimaryParentGroup}
		            </div>
					<div class="span8">
						<div id="primaryGroupIdAddd" name="primaryGroupIdAddd"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span4 align-right asterisk'>
	        			${uiLabelMap.DAName}
	        		</div>
					<div class ="span8">
						<input type="text" id="productStoreGroupNameAddd">	
					</div> 
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span4 align-right asterisk'>
	    				${uiLabelMap.DADescription}
	    			</div>
					<div class ="span8 span8editor">
						<input type="text" id="descriptionAddd">
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
</div>
<div id ="contextMenu" style ="display:none">
	<ul>
		<li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
		<li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
		<li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}</li>
		<li><i class="fa fa-edit"></i>${StringUtil.wrapString(uiLabelMap.DAEditStatus)}</li>
	</ul>
</div>
<script>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function(event){
		var args = event.args;
		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var tmpkey = $.trim($(args).text());
		if(tmpkey == '${StringUtil.wrapString(uiLabelMap.DARefresh)}'){
			$("#jqxgrid").jqxGrid('updatebounddata');
		}
		else if(tmpkey == '${StringUtil.wrapString(uiLabelMap.DAViewDetail)}'){
			var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			if(data){
				var productStoreGroupId = data.productStoreGroupId;
				window.location.href =  'editProductStoreGroup?productStoreGroupId=' + productStoreGroupId;
			}
		}
		else if(tmpkey == '${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}'){
			var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			if(data){
				var productStoreGroupId = data.productStoreGroupId;
				window.open('editProductStoreGroup?productStoreGroupId=' + productStoreGroupId,'_blank');
			}
		}
		else if(tmpkey == '${StringUtil.wrapString(uiLabelMap.DAEditStatus)}'){
			var wtmp = window;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
			var tmpwidth = $('#alterpopupWindowCreate1').jqxWindow('width');
			$("#alterpopupWindowCreate1").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
			$("#alterpopupWindowCreate1").jqxWindow('open');
			$("#productStoreGroupIdAdd").html('<span>'+ data.productStoreGroupId +'</span>');
			var ProductStoreGroupdata = productStoreGroupdata;
			for(var i=0; i < ProductStoreGroupdata.length; i++){
				if(ProductStoreGroupdata[i].productStoreGroupId == data.productStoreGroupId){
					ProductStoreGroupdata.splice(i,1);
				}
			}
			var sourceproductStoreGroup = {
					localdata : ProductStoreGroupdata,
					datatype : "array",
					dataField :[
			            {name : 'productStoreGroupId'}
					]
			};
			var dataAdapterproductStore = new $.jqx.dataAdapter(sourceproductStoreGroup, {
				formatData : function(data){
					if($('#primaryGroupIdAddd1').jqxComboBox('searchString') != undefined){
						data.searchKey = $('#primaryGroupIdAddd1').jqxComboBox('searchString');
						return data;
					}
				}
			});
			$("#primaryGroupIdAddd1").jqxComboBox({width : 248,height : 23,source: dataAdapterproductStore,displayMember: 'description',valueMember: 'productStoreGroupId',selectedIndex: null,autoDropDownHeight : false, dropDownHeight: 100});
			$("#productStoreGroupNameAddd1").jqxInput({width : 243,height :19, value: data.productStoreGroupName});
			//$("#descriptionAddd1").jqxEditor({width : 248,height : 150, tools: 'bold italic underline | left center right'});   
			$("#descriptionAddd1").jqxEditor('val', data.description);
			$("#alterpopupWindowCreate1").jqxWindow({width :480,height :425,resizable: false,isModal: true,autoOpen: false,cancelButton: $('#alterCancel1'),modalOpacity : 0.8,theme : theme});
			$("#productStoreGroupTypeIdAddd1").jqxComboBox({width : 248,height : 23,source: dataAdapterproductStoreGroupType,displayMember: 'description',valueMember: 'productStoreGroupTypeId',selectedIndex: null,autoDropDownHeight : true});
			$("#descriptionAddd1").jqxEditor({width : 248,height : 150, tools: 'bold italic underline | left center right'});
		}
	});
	<#if listproductStoreGroupType?exists>
	var productStoreGroupTypedata = [
		<#list listproductStoreGroupType as lpSGT>
			{
				productStoreGroupTypeId: "${lpSGT.productStoreGroupTypeId?if_exists}",
				description: "${StringUtil.wrapString(lpSGT.description?if_exists)}"
				
			},
		</#list>
	];
	<#else>
		var productStoreGroupTypedata = [];
	</#if>
	var sourceproductStoreGroupType =
		{
			localdata: productStoreGroupTypedata,
			datatype: "array",
			datafield: [
			   {name: 'productStoreGroupTypeId'}
			]
		};
	var dataAdapterproductStoreGroupType = new $.jqx.dataAdapter(sourceproductStoreGroupType,{
			formatData: function(data){
				if($('#productStoreGroupTypeIdAddd').jqxComboBox('searchString') != undefined){
					data.searchKey = $('#productStoreGroupTypeIdAddd').jqxComboBox('searchString');
					return data;
				}
			}
	});
//	dataAdapter for alterpopupWindowCreate
	$("#alterpopupWindowCreate").jqxWindow({
		width :480,
		height :400	,
		resizable: false,
		isModal: true,
		autoOpen: false,
		cancelButton: $("#alterCancel"),
		modalOpacity : 0.8,
		theme : theme
	});
	$("#productStoreGroupTypeIdAddd").jqxComboBox({width : 248,height : 23,source: dataAdapterproductStoreGroupType,displayMember: 'description',valueMember: 'productStoreGroupTypeId',selectedIndex: null,autoDropDownHeight : true});
	$("#productStoreGroupNameAddd").jqxInput({width : 243,height :19,});
	$("#descriptionAddd").jqxEditor({width : 248,height : 150, tools: 'bold italic underline | left center right'});
	$("#primaryGroupIdAddd").jqxComboBox({width : 248,height : 23,source: productStoreGroupdataComplete,displayMember: 'description',valueMember: 'productStoreGroupId',selectedIndex: null,dropDownHeight : 100});
//	$("#alterSave").jqxButton({
//		theme : theme
//	});
//	$("#alterCancel").jqxButton({
//		theme : theme
//	});
	$("#alterpopupWindowCreateform").jqxValidator({
		rules : [
		         {
		        	 input : '#productStoreGroupTypeIdAddd',
		        	 message : 'choose a selection',
		        	 action : 'change',
		        	 rule : function(input,commit){
		        		 if(!$('#productStoreGroupTypeIdAddd').jqxComboBox('getSelectedItem')){
		        			 return false;
		        		 }
		        		 return true;
		        	 }
		         },
		         {
		        	 input : '#productStoreGroupNameAddd',
		        	 message : 'fill a name',
		        	 action : 'change',
		        	 rule : 'required'
		         }
		         ]
	
	});
	$("#alterSave").click(function(){
		$('#alterpopupWindowCreateform').jqxValidator('validate');
	});
	$("#alterpopupWindowCreateform").on('validationSuccess', function(event){
//		var Items1 = $('#productStoreGroupTypeId').jqxComboBox('getSelectedItem');
//		var descriptionproductStoreGroupTypeAdd = Items1.label;
//		var productStoreGroupTypeIdAdd = Items1.value;
		var description = $('#descriptionAddd').val();
		var descriptionvalue = description.substring(5,description.length-6);
		var row = {};
		row = {
//				productStoreGroupName : $('#productStoreGroupName').val(),
				productStoreGroupName : $('#productStoreGroupNameAddd').val(),
//				ProductStoreGroupTypeId : productStoreGroupTypeIdAdd,
				productStoreGroupTypeId : $('#productStoreGroupTypeIdAddd').val(),
				primaryParentGroupId : $('#primaryGroupIdAddd').val(),
//				description : $('#description').val(),
				description : descriptionvalue,
//				ProductStoreGroupTypeId : $('#productStoreGroupTypeId').val()
		};
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindowCreate").jqxWindow('close');
	});
	$("#alterpopupWindowCreate").on('close', function(){
		$('#productStoreGroupTypeIdAddd').jqxComboBox('selectIndex', null);
		$('#primaryGroupIdAddd').jqxComboBox('selectIndex',null);
		$('#productStoreGroupNameAddd').jqxInput('val', null);
		$('#descriptionAddd').jqxEditor('val', null)
	});
//	done alterpopupWindowCreate
	$("#alterpopupWindowCreateform1").jqxValidator({
		rules : [
	         {
	        	 input: '#productStoreGroupTypeIdAddd1',
        		 message : 'choose a generic',
        		 action : 'change',
        		 rule: function(){
        			 if(!$('#productStoreGroupTypeIdAddd1').val()) return false;
        			 return true;
        		 }
	         },
	         {
	        	input: '#primaryGroupIdAddd1',
	        	message : 'choose a group',
	        	action : 'change',
	        	rule : function(){
	        		if(!$('#primaryGroupIdAddd1').val()) return false;
	        		return true;
	        	}
	         },
	         {
	        	 input : '#productStoreGroupNameAddd1',
	        	 message : 'fill a name',
	        	 action : 'change',
	        	 rule: 'required'
	         },
	         {
	        	 input : '#descriptionAddd1',
	        	 message : 'describe the group',
	        	 action : 'change',
	        	 rule : function(){
	        		 if(!$('#descriptionAddd1').val()) return false;
	        		 return true;
	        	 }
	         }
        ]
	});
	$("#alterSave1").click(function(){
		$('#alterpopupWindowCreateform1').jqxValidator('validate');
	});
	$("#alterpopupWindowCreateform1").on('validationSuccess', function(event){
		var row ={};
		var x = document.getElementById("productStoreGroupIdAdd").innerText;
		row = {
				productStoreGroupId : x,
				productStoreGroupTypeId : $('#productStoreGroupTypeIdAddd1').val(),
				primaryParentGroupId : $('#primaryGroupIdAddd1').val(),
				productStoreGroupName : $('#productStoreGroupNameAddd1').val(),
				description : $('#descriptionAddd1').val()
		};
		$.ajax({
			type : "POST",
			url : "updateProductStoreGroup",
			data : row,
			success : function(data, status, xhr){
				if(data.responseMessage == "error"){
					$('#jqxgrid').jqxGrid('updatebounddata');
					$('#container').empty();
					$('#jqxNotification').jqxNotification({ template: 'error'});
                    $("#notificationContent").text(data.errorMessage);
                    $("#jqxNotification").jqxNotification('open');
				}
				else {
					$('#container').empty();
                    $('#jqxNotification').jqxNotification({ template: 'success'});
                    $("#notificationContent").text('updateSuccess');
                    $("#jqxNotification").jqxNotification('open');
                    $('#jqxgrid').jqxGrid('updatebounddata');
				}
			}
//			error : function(){
////				
//			};
		});
		$("#alterpopupWindowCreate1").jqxWindow('close');
	});
	$("#alterpopupWindowCreate1").on('close', function(){
		$("#productStoreGroupTypeIdAddd1").jqxComboBox('val',null);
		$("#primaryGroupIdAddd1").jqxComboBox('val',null);
		$("#productStoreGroupNameAddd1").jqxInput('val',null);
		$("#descriptionAddd1").jqxEditor('val',null);
	})
	
</script>