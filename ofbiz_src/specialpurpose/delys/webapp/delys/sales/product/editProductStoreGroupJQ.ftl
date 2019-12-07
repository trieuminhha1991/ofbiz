<#assign listproductStoreGroupType = delegator.findList("ProductStoreGroupType", null, null, null, null, false)/>
<#assign listprimaryParentGroup = delegator.findList("ProductStoreGroup", null, null, null, null, false)/>
<#assign listprimaryParentGroupFocus = delegator.findList("ProductStoreGroup", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productStoreGroupId",Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS,"${parameters.productStoreGroupId?if_exists}"),null,null,null,false)/>
<#assign ele = listprimaryParentGroupFocus />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<script>
var productStoreGroupTypedata = [];
	<#if listproductStoreGroupType?exists>
		 productStoreGroupTypedata = [
			<#list listproductStoreGroupType as lpSGT>
				{
					productStoreGroupTypeId: "${lpSGT.productStoreGroupTypeId?if_exists}",
					description: "${lpSGT.description?if_exists}"
				},
			</#list>
		];
	<#else>
		 productStoreGroupTypedata = [];
	</#if>
		var sourceproductStoreGroupTypedata = {
			localdata : productStoreGroupTypedata,
			datatype : "array",
			datafield : [
		           {name: 'productStoreGroupTypeId'}
	         ]
	};
	var dataAdapterproductStoreGroupTypedata = new $.jqx.dataAdapter(sourceproductStoreGroupTypedata,{
		formatData: function(data){
			if($('#productStoreGroupTypeIdEdit').jqxComboBox('searchString') !== undefined){
				data.searchKey = $('#productStoreGroupTypeIdEdit').jqxComboBox('searchString');
				return data;
			}
		}
	});
	<#if listprimaryParentGroup?exists>
		var productStoreGroupdata = [
	 	    <#list listprimaryParentGroup as lpPG>
	 	    	{
	 	    		productStoreGroupId : "${lpPG.productStoreGroupId?if_exists}",
	 	    		description : "${lpPG.productStoreGroupName?if_exists}" + "[" + "${lpPG.productStoreGroupId?if_exists}" + "]",
	 	    	},
	 	    </#list>
	 	    ];
 		<#else>
			productStoreGroupdata =[];
	</#if>
	var src = (function(){
		for(var i=0; i<productStoreGroupdata.length;i++){
			if(productStoreGroupdata[i].productStoreGroupId == '${parameters.productStoreGroupId?if_exists}'){
				productStoreGroupdata.splice(i,1);
			}
		}
		return productStoreGroupdata;
	}())
	var sourceproductStoreGroupdata = {
		localdata : src,
		datatype : "array",
		datafield : [
            {name : "productStoreGroupId"}
         ]
	};
	var dataAdapterproductStoreGroupdata = new $.jqx.dataAdapter(sourceproductStoreGroupdata,{
		formatData : function(data){
			if($('#primaryGroupIdEdit').jqxComboBox('searchString') != undefined){
				data.searchKey = $('#primaryGroupIdEdit').jqxComboBox('searchString');
				return data;
			}
		}
	});
	<#if listprimaryParentGroupFocus?exists>
		var productStoreGroupdataFocus = [
          <#list listprimaryParentGroupFocus?if_exists as lpPGF>
          	{
          		productStoreGroupNameFocus : '${ele.get(0).productStoreGroupName}',
      			descriptionFocus : '${ele.get(0).description}',
          	},
          </#list>
      	]
		<#else> productStoreGroupdataFocus = [];
	</#if>
</script>
<div id ="noti"></div>
<div id ="EditProductStoreGroup">
<form  id="EditProductStoreGroupForm" name="EditProductStoreGroupForm" method ="post" action="<@ofbizUrl>updateProductStoreGroup</@ofbizUrl>">
	<div class="row-fluid  form-window-content">
		<div class="span12">
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class='span6 align-right asterisk'>
						${uiLabelMap.DAProductStoreGroupId}
					</div>
					<div class="span6">
						<div id="productStoreGroupIdEdit">${parameters.productStoreGroupId?if_exists}</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span6 align-right asterisk'>
						${uiLabelMap.DAType}
					</div>
					<div class="span6">
						<div id="productStoreGroupTypeIdEdit" name="productStoreGroupTypeIdEdit"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span6 align-right asterisk'>
						${uiLabelMap.DAPrimaryParentGroup}
					</div>
					<div class="span6">
						<div id="primaryGroupIdEdit" name="primaryGroupIdEdit"></div>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class='span3 align-right asterisk'>
						${uiLabelMap.DAName}
					</div>
					<div class ="span9">
						<input type="text" id="productStoreGroupNameEdit">	
					</div> 
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span3 align-right asterisk'>
						${uiLabelMap.DADescription}
					</div>
					<div class ="span9 span7editor">
						<div id="descriptionEdit"></div>
					</div>
				</div>
			</div>
		</div>	
	</div>
	</form>
	<div class="form-action" style="bottom:-21px;">
	<div class='row-fluid'>
		<div class="span12 margin-top10">
			<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
	</div>
</div>
<div id="notification" style="display:none">
	<div>edit success</div>
</div>
<style>
	#alterSave {
		margin-right : 174px !important;
	}
</style>
<script>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$('#notification').jqxNotification({template : 'success',autoClose : true,opacity : 1,appendContainer : '#noti'});
	$("#productStoreGroupTypeIdEdit").jqxComboBox({width : 248,height : 23, source: dataAdapterproductStoreGroupTypedata, displayMember: 'description', valueMember: 'productStoreGroupTypeId',selectedIndex: 0,autoDropDownHeight : true});
	$("#primaryGroupIdEdit").jqxComboBox({width : 248,height : 23, source: dataAdapterproductStoreGroupdata, displayMember: 'description', valueMember: 'productStoreGroupId', selectedIndex: null, autoDropDownHeight : false, dropDownHeight: 100});
	$("#productStoreGroupNameEdit").jqxInput({width :248, height :23, value: productStoreGroupdataFocus[0].productStoreGroupNameFocus });
	$("#descriptionEdit").jqxEditor({width : 248,height : 150, tools: 'bold italic underline | left center right'});
	$("#descriptionEdit").jqxEditor('val', productStoreGroupdataFocus[0].descriptionFocus);
	$("#EditProductStoreGroupForm").jqxValidator({
		rules : [
		         {
		        	 input : '#productStoreGroupNameEdit',
		        	 message : 'edit the groups name',
		        	 action :'change',
		        	 rule : 'required'
		         
		         }
         
         ]
	});
	$("#alterSave").click(function(){
		$("#EditProductStoreGroupForm").jqxValidator('validate');
	});
	$("#EditProductStoreGroupForm").on('validationSuccess', function(event){
		var row ={};
		row = {
				productStoreGroupId : '${parameters.productStoreGroupId?if_exists}',
				productStoreGroupTypeId : $('#productStoreGroupTypeIdEdit').val(),
				primaryParentGroupId : $('#primaryGroupIdEdit').val(),
				productStoreGroupName : $('#productStoreGroupNameEdit').val(),
				description : $('#descriptionEdit').val()	
		};
		$.ajax({
			type :"POST",
			url : "updateProductStoreGroup",
			data : row,
			success : function(){
				$("#notification").jqxNotification('open');
			}
		});
	});
</script>
