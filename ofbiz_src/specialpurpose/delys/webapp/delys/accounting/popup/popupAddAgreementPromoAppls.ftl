<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
	    		<div class='span12'>
	    			<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.accProductPromoId}
	    				</div>
	    				<div class='span7'>
							<div id="productPromoIdAdd">
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
	    					${uiLabelMap.SequenceId}
	    				</div>
	    				<div class='span7'>
	    					<div id="sequenceNumAdd">
	 						</div>
	    				</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
    </div>
</div>


<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
	//Create theme
	 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var popupAction = (function(){
		var initElement = function(){
				//Create Popup
				$("#alterpopupWindow").jqxWindow({
				        width: 600,height : 260, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
			    });
				//Create productPromoId
				$("#productPromoIdAdd").jqxDropDownList({ source: ppData, width: '215px', height: '25px', displayMember: "promoName", valueMember: "productPromoId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
				//Create FromDate
				$("#fromDateAdd").jqxDateTimeInput({width: '215px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});
				var date = new Date();
				$("#fromDateAdd").jqxDateTimeInput('setMinDate',new Date(date.getYear() + 1900,date.getMonth(),date.getDate()));
				//Create ThruDate
				$("#thruDateAdd").jqxDateTimeInput({width: '215px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});
				//Create sequenceNum
				$("#sequenceNumAdd").jqxNumberInput({width: '210px',height : '25px',digits : 15,spinButtons : false,min : 0 ,max  :999999999999999,decimalDigits : 0});
			}
			
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#productPromoIdAdd',message  : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'close,change',rule : function(){
						var val = $('#productPromoIdAdd').jqxDropDownList('val');
						if(!val) return false;
						return true;
					}},
					{input :'#thruDateAdd',message : '${StringUtil.wrapString(uiLabelMap.thruDateValidate?default(''))}',action : 'change,click',rule : function(){
						var from  = $('#fromDateAdd').jqxDateTimeInput('getDate');
						var thru  = $('#thruDateAdd').jqxDateTimeInput('getDate');
						if(!thru){
							return true;
						}else if(from >= thru){
							return false;
						}
						return true;
					}}
				]
			
			})
		}	
		var bindEvent = function(){
			$('#alterpopupWindow').on('open', function (event) {
				$("#thruDateAdd").jqxDateTimeInput('val', null);
				$("#sequenceNumAdd").jqxNumberInput('clear');	   	
				$('#productPromoIdAdd').jqxDropDownList('clearSelection');
			});
			 // update the edited row when the user clicks the 'Save' button.
		    $("#save").click(function () {
		    	save();
		    });
		    //update the edited row when the user clicks the 'SaveAndContinue' button and popup not close.
		    $("#saveAndContinue").click(function () {
		    	saveAndContinue();
		    });
		}
		var save = function(){
			if(!$('#formAdd').jqxValidator('validate')){return;};
			var row;
		    	var valThrudate = $('#thruDateAdd').val();
		    	if (valThrudate  && typeof(valThrudate) != 'undefined')
		    	{
		    		valThrudate = $('#thruDateAdd').jqxDateTimeInput('getDate').getTime();
		    	}
		    	    	
		        row = { 
		        		productPromoId:$('#productPromoIdAdd').val(), 
		        		fromDate:$('#fromDateAdd').jqxDateTimeInput('getDate').getTime(),
		        		thruDate : valThrudate,
		        		sequenceNum:$('#sequenceNumAdd').val()
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        $("#jqxgrid").jqxGrid('clearSelection');                        
		        $("#jqxgrid").jqxGrid('selectRow', 0);          		 
		        $("#alterpopupWindow").jqxWindow('close');
		}
		
		var saveAndContinue = function(){
			if(save && typeof(save) == 'function'){
				save();
				$("#alterpopupWindow").jqxWindow('open');
			}else return;
		}
		
		
		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			}
		}
	}());
	
	$(document).ready(function(){
		popupAction.init();
	})
</script>
