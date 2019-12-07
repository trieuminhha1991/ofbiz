<div id="${dataToggleModalId}" class="modal hide fade modal-dialog" tabindex="-1" style="width: 80%;">
	<div class="modal-header no-padding ">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.EditPayrollTable}
		</div>
	</div>	
	<div class="modal-body no-padding">
		<div class="widget-body">	 
			<div class="widget-main">
				<div class="row-fluid">
					<form name="EditPayrollTable" id="EditPayrollTable" method = "post" action = "<@ofbizUrl>createPayrollTableRecord</@ofbizUrl>" class="form-horizontal">
						<div class="row-fluid">
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">
									${uiLabelMap.PayrollTableName}
								</label>
								<div class="controls">
									<input type="text" name="payrollTableName">
								</div>
							</div>						   	
					   		<div class="control-group no-left-margin">
					   			<label class="control-label asterisk">
									${uiLabelMap.formulaCode}
								</label>
								<div class="controls">
									<@htmlTemplate.renderPayrollFormula name="formulaList" id="formulaList" payrollFormulaList=parollFormula divId="jqxPayrollBox1" width="90%" height="200px"/>
								</div>
					   		</div>	
					   		<div class="control-group no-left-margin">
					   			<label class="control-label asterisk">
					   				${uiLabelMap.Department}
					   			</label>
					   			<div class="controls">
					   				<select name="departmentList" multiple="multiple" class="chzn-select">
										<#list directChildDepartment as department>
											<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, department.partyId, false)>
											<option value="${department.partyId}" <#if parameters.departmentList?exists && parameters.departmentList?contains(department.partyId)>selected="selected"</#if>>${partyName}</option>
										</#list> 			
									</select>
					   			</div>
					   		</div>	
					   		<div class="control-group no-left-margin">
					   			<label class="control-label asterisk">
					   				${uiLabelMap.CalcSalaryPeriod}
					   			</label>
					   			<div class="controls">
					   				<select name="periodTypeId">
										<#if parameters.periodTypeId?exists>
											<#assign periodTypeId = parameters.periodTypeId>
										<#else>
											<#assign periodTypeId = "MONTHLY">
										</#if>
										<#list periodTypes as period>
											<option value="${period.periodTypeId}" <#if periodTypeId==period.periodTypeId>selected="selected"</#if>>${period.description}</option>
										</#list>
									</select>
					   			</div>
					   		</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">
					   				${uiLabelMap.FromDate}
					   			</label>
					   			<div class="controls">
					   				<@htmlTemplate.renderDateTimeField name="fromDate" value="${parameters.fromDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					   			</div>
							</div>	
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">
					   				${uiLabelMap.ThruDate}
					   			</label>
					   			<div class="controls">
					   				<@htmlTemplate.renderDateTimeField name="thruDate" value="${parameters.thruDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="thruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					   			</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">
					   				&nbsp;
					   			</label>
					   			<div class="controls">
					   				<button class="btn btn-primary btn-small" type="button" id="btnSubmit">
									 	<i class = "icon-ok" ></i>
			          					${uiLabelMap.CommonCreate}
			      					</button>
					   			</div>
							</div>	
		 				</div>	
					</form>
				</div>
			</div><!--/widget-main-->
		</div> <!-- /widget-body-->
	</div>
	
</div>
<style type="text/css">
.modal-dialog {
	margin: 0;
    position: absolute;
    outline: none;
    left: 10%;
   }
.modal.fade.in{
	top: -4%;
}   
</style>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	jQuery(document).ready(function() {
		$('#${dataToggleModalId}').on('show.bs.modal', function () {
			$('.modal .modal-body').css('overflow-y', 'auto'); 
		    $('.modal .modal-body').css('max-height', $(window).height() * 0.85);
		});
		jQuery("#${createNewLinkId}").attr("data-toggle", "modal");
		jQuery("#${createNewLinkId}").attr("role", "button");
		jQuery("#${createNewLinkId}").attr("href", "#${dataToggleModalId}");
		
		jQuery("#btnSubmit").click(function(){
			//console.log($('#EditPayrollTable').jqxValidator('validate'));
			if(!validForm.valid() || !$('#EditPayrollTable').jqxValidator('validate')){
				return false;
			}
			$('#EditPayrollTable').submit();
		});
		
		$('#EditPayrollTable').jqxValidator({
			 rules: [{
		          input: '#jqxPayrollBox1',
		          message: '${uiLabelMap.CommonRequired}',
		          rule:  function () {
		        	  var items = $("#jqxPayrollBox1").jqxListBox('getSelectedItems');
		              if (!items.length){
		            	  //console.log("items:" + items);
		            	  return false;
		              }
		              return true;
		          }
		      },
		      ]
		});
		
		jQuery.validator.addMethod("greaterThan", function(value, element, params){
			//var fromDate = Date.parseExact(value,"yyyy-MM-dd");
			if (value){
				return Date.parseExact(value,"yyyy-MM-dd") >= Date.parseExact($(params).val(),"yyyy-MM-dd");
			} else{ 
				return true;
			}	
		}, 'Must be greater than');
		
		var validForm = $('#EditPayrollTable').validate({
			ignore : [],
			errorElement: 'span',
			errorClass: 'help-inline red-color',
			errorPlacement: function(error, element) {
				element.addClass("border-error");
	    		if (element.parent() != null ){   
					element.parent().find("button").addClass("button-border");     			
	    			error.appendTo(element.parent());
				}
	    	  },
	    	unhighlight: function(element, errorClass) {
	    		$(element).removeClass("border-error");
	    		$(element).parent().find("button").removeClass("button-border");
	    	},
			focusInvalid: false,
			rules: {
				payrollTableName: {
					required: true,
				},
				departmentList: {
					required: true,					
				},
				periodTypeId:{
					required: true,
				},
				fromDate: {
					required: true,
				},
				thruDate:{
					required: true,
					greaterThan: "#fromDate"
				}
			},

			messages: {
				payrollTableName: {
					required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
				},
				departmentList: {
					required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",					
				},
				periodTypeId:{
					required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
				},
				fromDate: {
					required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
				},
				thruDate:{
					greaterThan: "<span style='color:red;'>${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}</span>"
				}
			},

			invalidHandler: function (event, validator) { //display error alert on form submit   
				$('.alert-error', $('.login-form')).show();
			},

			highlight: function (e) {
				$(e).closest('.control-group').removeClass('info').addClass('error');
			},

			success: function (e) {
				$(e).closest('.control-group').removeClass('error').addClass('info');
				$(e).remove();
			},

			submitHandler: function (form) {				
				form.submit();
			},
			invalidHandler: function (form) {
			}
		});
	});
</script>