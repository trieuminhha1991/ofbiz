<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
 <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.Owner}
    				</div>
    				<div class='span7'>
    					<div id="managerPartyId"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accRoleTypeId}
    				</div>
    				<div class='span7'>
    					<div id="roleTypeIdAdd">
	 					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.fromDate}
    				</div>
    				<div class='span7'>
    					<div id="fromDateAdd">
	 					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.thruDate}
    				</div>
    				<div class='span7'>
    					<div id="thruDateAdd">
	 					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_allocatedDate}
    				</div>
    				<div class='span7'>
    					<div id="allocatedDateAdd">
	 					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.CommonStatus}
    				</div>
    				<div class='span7'>
    					<div id="statusIdAdd">
	 					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.comments}
    				</div>
    				<div class='span7'>
    					<input id="commentsAdd">
	 					</input>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCategory" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinueCategory" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="saveCategory" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.culture.vi-VN.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<script type="text/javascript" src="/delys/images/js/filterDate.js"></script>
<script type="text/javascript">
	
$.jqx.theme = 'olbius';  
theme = $.jqx.theme;

var tmpLcl = '${locale}';
if(tmpLcl=='vi'){
	tmpLcl = 'vi-VN';
}else{
	tmpLcl = 'en-EN';
}

var action = (function(){
		 var initElement = function(){
		 	$("#comments").jqxInput({width: '202px', height: '22px'});	
			$("#thruDateAdd").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
			$("#fromDateAdd").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
			$("#allocatedDateAdd").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
			$("#thruDateAdd").jqxDateTimeInput("val",null);
			$("#fromDateAdd").jqxDateTimeInput("val",null);
			$("#allocatedDateAdd").jqxDateTimeInput("val",null);
			$("#roleTypeIdAdd").jqxDropDownList({source: dataRoleTypeListView,  filterable: true,  width: '208px', displayMember:"description",valueMember: "roleTypeId", placeHolder: "${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc)}"});
			$("#statusIdAdd").jqxDropDownList({source: dataStatusListView, autoDropDownHeight : true, width: '208px', displayMember:"description",valueMember: "statusId", placeHolder: "${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc)}"});
		 	$("#alterpopupWindow").jqxWindow({
	        	width: 470, height: 400, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelCategory"), modalOpacity: 0.7, theme:theme         
	    	});	
	    	initComBoBox();
	    	filterDate.init('fromDateAdd','thruDateAdd');
		 }
		 
		 var initComBoBox = function(){
		 		var sourceMNG =
		            {
		                datatype: "json",
		                datafields: [
		                    { name: 'partyId' },
		                    { name: 'firstName' },
		                    { name: 'middleName' },
		                    { name: 'lastName' },
		                    { name: 'fullName' },
		                    { name: 'groupName' }
		                ],
		                type: "POST",
		                root: "listParties",
		                contentType: 'application/x-www-form-urlencoded',
		                url: "fixedAssetManagerableList"
	            };
	            var dataAdapterMNG = new $.jqx.dataAdapter(sourceMNG,
	                {
	                    formatData: function (data) {
	                        if ($("#managerPartyId").jqxComboBox('searchString') != undefined) {
	                            data.searchKey = $("#managerPartyId").jqxComboBox('searchString');
	                            return data;
	                        }
	                    }
	                }
	            );
	            $("#managerPartyId").jqxComboBox(
	            {
	                width: 208,
	                placeHolder: "${StringUtil.wrapString(uiLabelMap.wmparty)}",
	                dropDownWidth: 300,
	                dropDownHeight : 300,
	                height: 25,
	                source: dataAdapterMNG,
	                remoteAutoComplete: true,
	                displayMember : "fullName",
	                valueMember: "partyId",
	                renderer: function (index, label, value) {
	                    var item = dataAdapterMNG.records[index];
	                    if (item != null) {
	                    	var label ="";
	                    	label += (item.groupName != null) ? item.groupName : "";
	                    	label += (item.fullName != null) ? item.fullName : "";
	                    	label += (item.firstName != null) ? item.lastName + " ": "";
	                    	label += (item.middleName != null) ? item.middleName + " " : "";
	                    	label += (item.lastName != null) ? item.firstName +" ": "";
	                        label += " (" + item.partyId + ")";
	                        return label;
	                    }
	                    return "";
	                },
	                renderSelectedItem: function(index, item)
	                {
	                    var item = dataAdapterMNG.records[index];
	                    if (item != null) {
	                        var label = "";
	                        label += (item.groupName != null) ? item.groupName : "";
	                    	label += (item.fullName != null) ? item.fullName : "";
	                    	label += (item.firstName != null) ? item.lastName + " ": "";
	                    	label += (item.middleName != null) ? item.middleName + " " : "";
	                    	label += (item.lastName != null) ? item.firstName +" ": "";
	                        return label;
	                    }
	                    return "";   
	                },
	                search: function (searchString) {
	                    dataAdapterMNG.dataBind();
	                }
	            });           
		 
		 }
		 
		 var initRules = function(){
		 		$('#alterpopupWindow').jqxValidator({
				        rules: [
				                   { input: '#managerPartyId', message: '${uiLabelMap.CommonRequired}', action: 'change,select', rule: function(input){
				                	   var val = $("#managerPartyId").jqxComboBox('val');
				                	   if(val=="" || !val){
				                	  	 return false;
				                	   }
				                	  	 return true;
				                	   }},
				                   { input: '#roleTypeIdAdd', message: '${uiLabelMap.CommonRequired}', action: 'select', rule: 
				                	   function (input) {
				                	   var val = $("#roleTypeIdAdd").jqxDropDownList('val');
				                	   if(val==""){
				                	   return false;
				                	   }
				                	   return true;
				                	   }                            
				                   }	                   
				               ]
				    });	    
				};	
				
		var save = function(){
			if(!$('#alterpopupWindow').jqxValidator('validate')){return;}
		    	var row;
		        row = { 
		        		thruDate:$('#thruDateAdd').jqxDateTimeInput('getDate'),
		        		fromDate:$('#fromDateAdd').jqxDateTimeInput('getDate'),
		        		allocatedDate:$('#allocatedDateAdd').jqxDateTimeInput('getDate'),
		        		comments:$('#commentsAdd').val(),
		        		roleTypeId:$('#roleTypeIdAdd').val(),
		        		statusId:$('#statusIdAdd').val(),	        		
		        		partyId: $('#managerPartyId').val(),
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		      return true;
		}
		
		var clear = function(){
			$("#thruDateAdd").jqxDateTimeInput("val",null);
	    	$("#fromDateAdd").jqxDateTimeInput("val",null);
	    	$("#allocatedDateAdd").jqxDateTimeInput("val",null);	    	
			$("#commentsAdd").val("");
			$("#statusIdAdd").jqxDropDownList('clearSelection');
			$('#roleTypeIdAdd').jqxDropDownList('clearSelection');
			$('#managerPartyId').jqxComboBox('clear');
			$('#formAdd').jqxValidator('hide');
			filterDate.resetDate();
		}
		
		var bindEvent = function(){
			$("#saveCategory").click(function () {
				if(save()) $("#alterpopupWindow").jqxWindow('close');
			});
			$("#saveAndContinueCategory").click(function () {
				if(save()) return;
			});
			
			$('#alterpopupWindow').on('close',function(){
				clear();
			})
			
		}
	
		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			}
		}
	}())
	
	$(document).ready(function(){
		action.init();
	})

	</script>