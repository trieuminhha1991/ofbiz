<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<div class="step-content row-fluid position-relative">
				<form name="createEmployment" id="createEmployment" method="post" action="<@ofbizUrl>createEmploymentDirect</@ofbizUrl>">
					<div class="step-pane active" id="step1">
					   	<table cellspacing="0">
					   		<tbody>
					   			<tr>
									<td>
										<input type="hidden" name="roleTypeIdFrom" id="roleTypeIdFrom" value="INTERNAL_ORGANIZATIO"/>
										<input type="hidden" name="roleTypeIdTo" id="roleTypeIdTo" value="EMPLOYEE"/>
									</td>
								</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15 margin-left30" for="partyIdFrom">${uiLabelMap.HumanResEmploymentPartyIdFrom}</label></td>
									<td>
										 <#if (orgInternal?has_content)>
                							<#list orgInternal as root>
                								<#if root.partyId==parameters.partyId>
													<label><h4><i>${root.groupName}<i></h4></label>
												</#if>
											</#list>
										</#if>
										<input name="partyIdFrom" type="hidden" value="${parameters.partyId}"/>
									</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15 margin-left30" for="partyIdTo">${uiLabelMap.HumanResEmployeePartyIdTo}</label></td>
									<td>
										<@htmlTemplate.lookupField formName="createEmployment" name="partyIdTo" id="partyIdTo" fieldFormName="LookupEmployeeNew" value="${parameters.parentOrgId?if_exists}"/>
									</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15 margin-left30" for="fromDate">${uiLabelMap.CommonFromDate}</label></td>
									<td><@htmlTemplate.renderDateTimeField name="fromDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
								</tr>
								<tr>
									<td></td>
									<td>
										<button class="btn btn-success btn-small" type="submit">
											 <i class="icon-ok"></i>
	              							${uiLabelMap.CommonSubmit}
	          							</button>
          							</td>
								</tr>
							</tbody>
						</table>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$.validator.addMethod('validateToDay',function(value,element){
		var now = new Date();
		now.setHours(0,0,0,0);
		return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss")>=now;
	},'Greather than today');
	
	$('#createEmployment').validate({
		errorElement: 'div',
		errorClass: "help-inline red-color",
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
		rules:{
			fromDate_i18n:{
				required:true,
				validateToDay:true
			},
			partyIdTo:{
				required:true
			}
		},
		messages:{
			fromDate_i18n:{
				required:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotNull)}',
				validateToDay:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}'
			},
			partyIdTo:{
				required:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotNull)}'
			}
		}
	});	
</script>