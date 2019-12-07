<#assign getAlGlAccount="getAll"/>
<div id="alterpopupWindow" style="display:none;" >
    <div>${uiLabelMap.accAssign}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
    		<div class='span12'>
				<div class='row-fluid margin-top10'>
					<div class='span4 align-right asterisk'>
						${uiLabelMap.FormFieldTitle_glAccountId}:
					</div>
					<div class='span8'>
						<div id="glAccountId2">
							<div id="jqxgridGlAccount"></div>
						</div>
					</div>
				</div>    			
    		</div>
    	</div>
        <div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
    </div>
</div>
<script src="../images/js/generalUtils.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	var updateListGL = function(){
		$('#jqxgridGlAccount').jqxGrid('updatebounddata');
	}
	
	var initDropDown = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getListGLAccountChart&organizationPartyId=${parameters.organizationPartyId}',autoshowloadelement : true,width : 400,filterable : true,dropdown : {width : '250',dropDownHorizontalAlignment : true}},
			[
				{name : 'glAccountId',type : 'string'},
				{name : 'accountCode',type : 'string'},				
				{name : 'accountName',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.accountCode}',datafield : 'accountCode',width : '30%'},
				{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
			]
			, null, grid,dropdown,'glAccountId');
		}
	
	var action = (function(){
		var initElement = function(){
			//$('#glAccountId2').jqxDropDownList({theme:theme, width: 250, height: 24, dropDownWidth: 400,  source: dataGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			initDropDown($('#glAccountId2'),$('#jqxgridGlAccount'));
			$("#alterpopupWindow").jqxWindow({
		        width: 470, height: 150, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
		    });
				    
		    // update the edited row when the user clicks the 'Save' button.
		    $('#alterpopupWindow').jqxValidator({
		       rules: [
		           { 
				       input: '#glAccountId2', 
		           	   message: '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}', action: 'change,close, blur', 
		               rule: function (input, commit) {
		                   var val = input.jqxDropDownButton('val');
		                   if(!val) return false;
		                   return true;
		               } 
		           }
		       ]
		    });
		    
		}
		
		var save = function(){
			if($('#alterpopupWindow').jqxValidator('validate')){
			    		var row;
				        row = {
				        		glAccountId: $('#glAccountId2').val(),
				        		organizationPartyId  : '${parameters.organizationPartyId?if_exists}'
				        	  };
					   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
				       $("#alterpopupWindow").jqxWindow('close');
			    	}else{
			    		return;
			    	}
		}
		
		var bindEvent = function(){
			 $('#alterpopupWindow').on('close', function (event) {
			    	$('#alterpopupWindow').jqxValidator('hide');
			    });
		    $("#alterSave").click(function () {
			    	save();
			    });
			 $('#alterpopupWindow').on('close',function(){
			 	 $('#glAccountId2').jqxDropDownButton('val','');
			 	 $('#jqxgridGlAccount').jqxGrid('clearSelection');
			 })   
		};
		
		return {
			init : function(){
				initElement();
				bindEvent();
			}
		}
	}());  
	$(document).ready(function(){
		action.init();	
	})
</script>   