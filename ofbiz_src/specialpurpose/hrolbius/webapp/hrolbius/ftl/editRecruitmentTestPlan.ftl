<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<div id="fuelux-wizard" class="row-fluid">
				<ul class="wizard-steps">
					<li data-target="#step1" style="min-width: 20%; max-width: 20%;" class="active">
						<span class="step">1</span> 
						<span class="title">${uiLabelMap.InitRecruitmentTestPlan}</span>
					</li>
					<li data-target="#step2" style="min-width: 20%; max-width: 20%;" class="step">
						<span class="step">2</span> 
						<span class="title">${uiLabelMap.Round1}</span>
					</li>
					<li data-target="#step3" style="min-width: 20%; max-width: 20%;" class="step">
						<span class="step">3</span> 
						<span class="title">${uiLabelMap.Round2}</span>
					</li>
					<li data-target="#step4" style="min-width: 20%; max-width: 20%;" class="step">
						<span class="step">4</span> <span class="title">${uiLabelMap.Round3}</span>
					</li>
					<li data-target="#step5" style="min-width: 20%; max-width: 20%;" class="step">
						<span class="step">5</span> 
						<span class="title">${uiLabelMap.OverView}</span>
					</li>
				</ul>
			</div>
			
			<hr>
			
			<div class="step-content row-fluid position-relative">
<!-- 				<form name="editRecruitmentTestPlan" id="editRecruitmentTestPlan" method = "post" action = "<@ofbizUrl>CreateRecruitmentTestPlan</@ofbizUrl>"> -->
					<div class="step-pane active" id="step1">
					<form id="formStep1" name="formStep1">
					   	<table cellspacing="0" id="tableStep1">
					   		<tbody>
					   			<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="jobRequestId">${uiLabelMap.JobRequestId}</label></td>
      								<td>
      									<@htmlTemplate.lookupField formName="formStep1" name="jobRequestId" id="jobRequestId" fieldFormName="LookupJobRequest"/>
										<!--<span class="tooltipob">${uiLabelMap.required}</span>-->
      								</td>
								</tr>
								
								<tr>
									<td>
										<label class="padding-bottom5 padding-right15 asterisk" for="name">
											${uiLabelMap.CommonName}
										</label>
									</td>
      								<td>
      									<input type="text" id="name" name="name"/>
      								</td>
								</tr>
								
								<tr>
									<td>
										<label class="padding-bottom5 padding-right15" for="description">
											${uiLabelMap.Description}
										</label>
									</td>
      								<td>
      									<input type="text" id="description" name="description"/>
      								</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="applicantIdList">
										${uiLabelMap.Applicant}</label></td>
      								<td >
      									<@htmlTemplate.renderComboxBox name="applicantIdList" id="applicantIdList" emplData=applicantList container="jqxComboBox1"/>
      								</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="fromDate">${uiLabelMap.FromDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="fromDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="thruDate">${uiLabelMap.ThruDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="thruDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="thruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
								</tr>
								
							</tbody>
						</table>
						</form>
				    </div>
					<div class="step-pane" id="step2">
						<form id="formStep2" name="formStep2">
						<table cellspacing="0" id="tableStep2">
					   		<tbody>
					   			
					   			<tr>
					   				<td>
					   					<label class="padding-bottom5 padding-right15">
					   						${uiLabelMap.SkipThisStep}
					   					</label>
				   					</td>
					   				<td>
					   					<label>
					   						<input id="skipStep2" type="checkbox" class="ace-switch ace-switch-4" name="skipCreateExam"/>
					   						<span class="lbl"></span>
					   					</label>
					   				</td>
					   			</tr>
					   			<tr>
									<td><label class="padding-bottom5 padding-right15" for="examName">${uiLabelMap.CommonName}</label></td>
      								<td>
      									<input type="text" id="examName" name="examName"/>
      								</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="examDescription">${uiLabelMap.Description}</label></td>
      								<td>
      									<input type="text" name="examDescription" id="examDescription" />
      								</td>
      							</tr>
					   			
					   			<tr>
									<td><label class="padding-bottom5 padding-right15" for="examTesterIdList">${uiLabelMap.RecruitmentTester}</label></td>
      								<td>
      									<@htmlTemplate.renderComboxBox name="examTesterIdList" id="examTesterIdList" emplData=employeeList container="jqxComboBox2"/>
      								</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="examFromDate">${uiLabelMap.FromDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="examFromDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
								</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="examThruDate">${uiLabelMap.ThruDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="examThruDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="thruDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
      							</tr>
      							
      							<tr>
									<td><label class="padding-bottom5 padding-right15" for="examLocation">${uiLabelMap.CommonLocation}</label></td>
      								<td>
      									<@htmlTemplate.lookupField formName="formStep2" name="examLocation" id="examLocation" fieldFormName="LookupPartyPostalAddress"/>
      								</td>
      							</tr>
      							
							</tbody>
						</table>
						</form>
					</div>
					<div class="step-pane" id="step3">
						<form id="formStep3" name="formStep3">
						<table cellspacing="0" id="tableStep3">
					   		<tbody>
					   			<#--
					   			<!-- <tr>
					   				<td>
					   					<label class="padding-bottom5 padding-right15">
					   						${uiLabelMap.SkipThisStep}
					   					</label>
				   					</td>
					   				<td>
					   					<label>
					   						<input id="skipStep3" type="checkbox" class="ace-switch ace-switch-4" name="skipCreateFirstIntv"/>
					   						<span class="lbl"></span>
					   					</label>
					   				</td>
					   			</tr> -->
					   			<tr>
					   				<td>
					   					<label class="padding-bottom5 padding-right15" for="copyPrevStep2">
											${uiLabelMap.CopyPrevStep}
										</label>
					   				</td>
					   				<td>
					   					<input name="copyPrevStep2" type="checkbox" id="copyPrevStep2"/>
					   					<span class="lbl"></span>
					   				</td>
					   			</tr>
					   			<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="firstIntvName">${uiLabelMap.CommonName}</label></td>
      								<td>
      									<input type="text" id="firstIntvName" name="firstIntvName"/>
      								</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="firstIntvDescription">${uiLabelMap.Description}</label></td>
      								<td>
      									<input type="text" name="firstIntvDescription" id="firstIntvDescription" />
      								</td>
      							</tr>
					   		
					   			<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="firstIntvTesterIdList">${uiLabelMap.RecruitmentInterviewer}</label></td>
      								<td>
      									<@htmlTemplate.renderComboxBox name="firstIntvTesterIdList" id="firstIntvTesterIdList" emplData=employeeList container="jqxComboBox3"/>
      								</td>
								</tr>
								
      							
								<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="firstIntvFromDate">${uiLabelMap.FromDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="firstIntvFromDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate2" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
								</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="firstIntvthruDate">${uiLabelMap.ThruDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="firstIntvThruDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="thruDate2" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
      							</tr>
      							
      							<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="firstIntvLocation">${uiLabelMap.CommonLocation}</label></td>
      								<td>
      									<@htmlTemplate.lookupField formName="formStep3" name="firstIntvLocation" id="firstIntvLocation" fieldFormName="LookupPartyPostalAddress"/>
      								</td>
      							</tr>
      							
							</tbody>
						</table>
						</form>
					</div>
					
					<div class="step-pane" id="step4">
						<form id="formStep4" name="formStep4">
						<table cellspacing="0" id="tableStep4">
					   		<tbody>
					   			<#--
					   			<!-- <tr>
					   				<td>
					   					<label class="padding-bottom5 padding-right15">
					   						${uiLabelMap.SkipThisStep}
					   					</label>
				   					</td>
					   				<td>
					   					<label>
					   						<input id="skipStep4" type="checkbox" class="ace-switch ace-switch-4" name="skipCreateSecondIntv"/>
					   						<span class="lbl"></span>
					   					</label>
					   				</td>
					   			</tr> -->
					   			<tr>
					   				<td>
					   					<label class="padding-bottom5 padding-right15" for="copyPrevStep">
											${uiLabelMap.CopyPrevStep}
										</label>
					   				</td>
					   				<td>
					   					<input name="copyPrevStep" type="checkbox" id="copyPrevStep"/>
					   					<span class="lbl"></span>
					   				</td>
					   			</tr>
					   			<tr>
									<td>
										<label class="padding-bottom5 padding-right15 asterisk" for="secondIntvName">
											${uiLabelMap.CommonName}
										</label>
									</td>
      								<td>
      									<input type="text" id="secondIntvName" name="secondIntvName"/>
      								</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="secondIntvDescription">${uiLabelMap.Description}</label></td>
      								<td>
      									<input type="text" name="secondIntvDescription" id="secondIntvDescription" />
      								</td>
      							</tr>
					   		
					   			<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="secondIntvTesterIdList">${uiLabelMap.RecruitmentInterviewer}</label></td>
      								<td>
      									<@htmlTemplate.renderComboxBox name="secondIntvTesterIdList" id="secondIntvTesterIdList" emplData=employeeList container="jqxComboBox4"/>
      								</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="secondIntvFromDate">${uiLabelMap.FromDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="secondIntvFromDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate3" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
								</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="secondIntvThruDate">${uiLabelMap.ThruDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="secondIntvThruDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="thruDate3" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
      							</tr>
      							
      							<tr>
									<td><label class="padding-bottom5 padding-right15 asterisk" for="secondIntvLocation">${uiLabelMap.CommonLocation}</label></td>
      								<td>
      									<@htmlTemplate.lookupField formName="formStep4" name="secondIntvLocation" id="secondIntvLocation" fieldFormName="LookupPartyPostalAddress"/>
      								</td>
      							</tr>
      							
							</tbody>
						</table>
						</form>
					</div>
					<div id="step5" class="step-pane">
						<table cellspacing="0" id="overView">
							<tbody>
								
							</tbody>
						</table>
					</div>					
<!-- 				</form> -->
			</div>
			
			<hr/>
					
			<div class="row-fluid wizard-actions">
				<button class="btn btn-prev btn-small"><i class="icon-arrow-left"></i> Prev</button>
				<button class="btn btn-success btn-next btn-small" data-last="Finish" id="btnNext">
					Next <i class="icon-arrow-right icon-on-right"></i>
				</button>
			</div>
		</div>

	</div><!--/widget-main-->
</div> <!-- /widget-body-->

<script type="text/javascript">
$(function() {
	var $validation = false;
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
		var step = info.step;
		
		if (step == 1){
			var validateComboBox1 = $('#formStep1').jqxValidator('validate');
			if((!$('#formStep1').valid())||(validateComboBox1 == false)) return false;	
		} 
		
		if (step == 2){
			if(!$('#formStep2').valid()) return false;	
		} 
		
		if (step == 3){
			var validateComboBox3 = $('#formStep3').jqxValidator('validate');
			if((!$('#formStep3').valid())||(validateComboBox3 == false)) return false;	
		} 
		
		if(step == 4){
			var validateComboBox4 = $('#formStep4').jqxValidator('validate');
			if((!$('#formStep4').valid())||(validateComboBox4 == false)) return false;	
			else overviewAllStep();
		}

// 		if(info.direction == "next"){
// 			var validateComboBox = $('#editRecruitmentTestPlan').jqxValidator('validate');
// 			if((!$('#editRecruitmentTestPlan').valid())||(validateComboBox == false)) return false;	
// 		}
	}).on('finished', function(e) {
// 		document.getElementById("editRecruitmentTestPlan").submit();
		
		var element1 = document.getElementById("tableStep1");
		var element2 = document.getElementById("tableStep2");
		var element3 = document.getElementById("tableStep3");
		var element4 = document.getElementById("tableStep4");
		method = "post"; 
		path = "<@ofbizUrl>CreateRecruitmentTestPlan</@ofbizUrl>";
	    var form = document.createElement("form");
	    form.setAttribute("method", method);
	    form.setAttribute("action", path);
	    form.appendChild(element1);
	    form.appendChild(element2);
	    form.appendChild(element3);
	    form.appendChild(element4);
	    document.body.appendChild(form);
	    form.submit();
	});
	//$("#editRecruitmentTestPlan").data("validator").settings.ignore = ":hidden, :disabled";
	$('#formStep1').jqxValidator({
      rules: [{
          input: '#jqxComboBox1',
          message: '${uiLabelMap.CommonRequired}',
          action: 'change',
          rule: function () {
              var item = $("#jqxComboBox1").jqxComboBox('getSelectedItem');
              if (!item) return false;
              return true;
          }
      },
      ]
  	});
	
	$('#formStep3').jqxValidator({
	      rules: [{
	          input: '#jqxComboBox3',
	          message: '${uiLabelMap.CommonRequired}',
	          action: 'change',
	          rule: function () {
	              var item = $("#jqxComboBox3").jqxComboBox('getSelectedItem');
	              if (!item) return false;
	              return true;
	          }
	      },
	      ]
	  	});
	
	$('#formStep4').jqxValidator({
	      rules: [{
	          input: '#jqxComboBox4',
	          message: '${uiLabelMap.CommonRequired}',
	          action: 'change',
	          rule: function () {
	              var item = $("#jqxComboBox4").jqxComboBox('getSelectedItem');
	              if (!item) return false;
	              return true;
	          }
	      },
	      ]
	  	});
	
		
	$.validator.addMethod('validateToday',function(value,element){
		if(value){
			var now = new Date();
			now.setHours(0,0,0,0);
			return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= now;
		}else{
			return true;
		}
	},'Greather than today');
	
	$.validator.addMethod('greatThan',function(value,element,params){
		if(value){
			if($(params).val()){
				return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
			}else{
				var now = new Date();
				return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= now;
			}
		}else{
			return true;
		}
	},'Greather than today');
	
	$.validator.addMethod("nospecialcharacter", function(value, element) {
		if(value){
			return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.]+$/i.test(value);
		} else 
			return true;
	}, "Letters, numbers, and underscores only please");
	
	$('#formStep1').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		focusInvalid: false,
		ignore : ":disabled, :hidden",
		rules: {
			name: {
				required: true,
			},
			jobRequestId: {
				required: true,
			},
			fromDate_i18n:{
				validateToday:true
			},
			thruDate_i18n:{
				greatThan:"#fromDate_i18n"
			},
			description:{
				nospecialcharacter: true
			}
		},

		messages: {
			name: {
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
			},
			jobRequestId: {
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
			},
			fromDate_i18n:{
				validateToday:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
			},
			thruDate_i18n:{
				greatThan:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}"
			},
			description:{
				nospecialcharacter:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
			}
		},
    	errorPlacement: function(error, element) {
    		if (element.parent() != null ){   
    			error.appendTo(element.parent());
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
		},
		invalidHandler: function (form) {
		}
	});
	
	$('#formStep2').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		focusInvalid: false,
		ignore : ":disabled, :hidden",
		rules: {
			examDescription:{
				nospecialcharacter: true
			},
			examFromDate_i18n:{
				validateToday: true
			},
			examThruDate_i18n:{
				greatThan: '#fromDate1_i18n'
			}
		},
		messages: {
			examDescription:{
				nospecialcharacter:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
			},
			examFromDate_i18n:{
				validateToday:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
			},
			examThruDate_i18n:{
				greatThan:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}"
			}
		},
    	errorPlacement: function(error, element) {
    		if (element.parent() != null ){   
    			error.appendTo(element.parent());
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
		},
		invalidHandler: function (form) {
		}
	});
	
	$('#formStep3').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		focusInvalid: false,
		ignore : ":disabled, :hidden",
		rules: {
			firstIntvName:{
				required: true,
			},
			firstIntvFromDate_i18n:{
				required: true,
				validateToday:true
			}, 
			firstIntvThruDate_i18n:{
				required: true,
				greatThan:"#fromDate2_i18n"
			},
			firstIntvLocation: {
				required: true
			},
			firstIntvDescription:{
				nospecialcharacter: true
			}
		},

		messages: {
			firstIntvName:{
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
			},
			firstIntvFromDate_i18n:{
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
				validateToday:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
			}, 
			firstIntvThruDate_i18n:{
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
				greatThan:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}"
			},
			firstIntvLocation:{
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>"
			},
			firstIntvDescription:{
				nospecialcharacter:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
			}
		},
    	errorPlacement: function(error, element) {
    		if (element.parent() != null ){   
    			error.appendTo(element.parent());
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
		},
		invalidHandler: function (form) {
		}
	}); 
	
	$('#formStep4').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		focusInvalid: false,
		ignore : ":disabled, :hidden",
		rules: {
			secondIntvName:{
				required: true,
			},
			secondIntvFromDate_i18n:{
				required: true,
				validateToday:true
			},
			secondIntvThruDate_i18n:{
				required: true,
				greatThan:"#fromDate3_i18n"
			},
			secondIntvLocation: {
				required: true
			},
			secondIntvDescription:{
				nospecialcharacter: true
			}
		},

		messages: {
			secondIntvName:{
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
			},
			secondIntvFromDate_i18n:{
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
				validateToday:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
			},
			secondIntvThruDate_i18n:{
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
				greatThan:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}"
			},
			secondIntvLocation:{
				required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>"
			},
			secondIntvDescription:{
				nospecialcharacter:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
			}
		},
    	errorPlacement: function(error, element) {
    		if (element.parent() != null ){   
    			error.appendTo(element.parent());
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
		},
		invalidHandler: function (form) {
		}
	}); 
	
	jQuery('#skipStep2').removeAttr('checked').on('click', function(){
		$validation = this.checked;
		if(this.checked) {
			disableFieldInStep2();
			jQuery('#btnNext').trigger("click");
		}else{
			enableFieldInStep2();
		}		
	});
	
	/* jQuery("input[name='jobRequestId']").on("lookupIdChange", function(){
		var id = $(this).val();
	});
	jQuery("#0_lookupId_jobRequestId").blur(function(){
		var id = $(this).val();
	}); */
	
	jQuery('#skipStep3').removeAttr('checked').on('click', function(){
		$validation = this.checked;
		if(this.checked) {
			disableFieldInStep3();
			jQuery('#btnNext').trigger("click");
		}else{
			enableFieldInStep3();
		}		
	});
	
	jQuery('#skipStep4').removeAttr('checked').on('click', function(){
		$validation = this.checked;
		if(this.checked) {
			disableFieldInStep4();
			jQuery('#btnNext').trigger("click");
		}else{
			enableFieldInStep4();
		}		
	});
	
	jQuery("#copyPrevStep").change(function(){
		if(this.checked){
			copyDataInStep3();
		}
	});

	jQuery("#copyPrevStep2").change(function() {
		/* Act on the event */
		if(this.checked){
			copyDataInStep2();
		}
	});
});

function disableFieldInStep2(){
	jQuery("#copyPrevStep2").prop('disabled',true);
	jQuery("#examName").prop('disabled', true);
	jQuery("#examDescription").prop('disabled', true);
	jQuery("input[name^='examFromDate']").prop('disabled', true);
	jQuery("input[name^='examThruDate']").prop('disabled', true);
	jQuery("input[name='examLocation']").prop('disabled', true);
	jQuery("#jqxComboBox2").jqxComboBox({ disabled: true }); 
}

function enableFieldInStep2(){
	jQuery("#copyPrevStep2").prop('disabled',false);
	jQuery("#examName").prop('disabled', false);
	jQuery("#examDescription").prop('disabled', false);
	jQuery("input[name^='examFromDate']").prop('disabled', false);
	jQuery("input[name^='examThruDate']").prop('disabled', false);
	jQuery("input[name='examLocation']").prop('disabled', false);
	//jQuery("div[id*='jqxComboBox1']").prop('disabled', false);
	jQuery("#jqxComboBox2").jqxComboBox({ disabled: false }); 
}
function disableFieldInStep3(){
	jQuery("#firstIntvName").prop('disabled', true);
	jQuery("#firstIntvDescription").prop('disabled', true);
	jQuery("input[name^='firstIntvFromDate']").prop('disabled', true);
	jQuery("input[name^='firstIntvThruDate']").prop('disabled', true);
	jQuery("input[name='firstIntvLocation']").prop('disabled', true);
	jQuery("#jqxComboBox3").jqxComboBox({ disabled: true }); 
}

function enableFieldInStep3(){
	jQuery("#firstIntvName").prop('disabled', false);
	jQuery("#firstIntvDescription").prop('disabled', false);
	jQuery("input[name^='firstIntvFromDate']").prop('disabled', false);
	jQuery("input[name^='firstIntvThruDate']").prop('disabled', false);
	jQuery("input[name='firstIntvLocation]").prop('disabled', false);
	jQuery("#jqxComboBox3").jqxComboBox({ disabled: false }); 
}

function disableFieldInStep4(){
	jQuery("#copyPrevStep").prop('disabled', true);
	jQuery("#secondIntvName").prop('disabled', true);
	jQuery("#secondIntvDescription").prop('disabled', true);
	jQuery("input[name^='secondIntvFromDate']").prop('disabled', true);
	jQuery("input[name^='secondIntvThruDate']").prop('disabled', true);
	jQuery("#jqxComboBox4").jqxComboBox({ disabled: true });
	jQuery("input[name='secondIntvLocation']").prop('disabled', true);
}

function enableFieldInStep4(){
	jQuery("#copyPrevStep").prop('disabled', false);
	jQuery("#secondIntvName").prop('disabled', false);
	jQuery("#secondIntvDescription").prop('disabled', false);
	jQuery("input[name^='secondIntvFromDate']").prop('disabled', false);
	jQuery("input[name^='secondIntvThruDate']").prop('disabled', false);
	jQuery("#jqxComboBox4").jqxComboBox({ disabled: false });
	jQuery("input[name='secondIntvLocation']").prop('disabled', false); 
}
function copyDataInStep2(){
	var examName= jQuery("#examName").val();
	var examDescription= jQuery("#examDescription").val();	
	var examFromDateDisp= jQuery("input[name^='examFromDate_']");
	var examFromDateSubm= jQuery("input[name='examFromDate']");
	var examThruDateDisp=jQuery("input[name^='examThruDate_']");
	var examThruDateSubm=jQuery("input[name='examThruDate']");
	if(examFromDateDisp.length>0){
		var fromDateDisp= jQuery(examFromDateDisp[0]).val();
	}
	if (examThruDateDisp.length>0) {

		var thruDateDisp= jQuery(examThruDateDisp[0]).val();
	}
	if(examFromDateSubm.length>0){
		var fromDateSubm= jQuery(examFromDateSubm[0]).val();
	}
	if (examThruDateSubm.length>0) {

		var thruDateSubm= jQuery(examThruDateSubm[0]).val();
	}


	var examLocation= jQuery("input[name='examLocation']").val();
	jQuery("#firstIntvName").val(examName);
	jQuery("#firstIntvDescription").val(examDescription);
	jQuery("input[name='firstIntvLocation']").val(examLocation);

	if(typeof fromDateDisp != "undefined"){
		firstIntvFromDateDisp = jQuery("input[name^='firstIntvFromDate_']");
		for(var i = 0; i < firstIntvFromDateDisp.length; i++){
			jQuery(firstIntvFromDateDisp[i]).val(fromDateDisp);
		}
	}

	if(typeof fromDateSubm != "undefined"){
		firstIntvFromDateSubm = jQuery("input[name='firstIntvFromDate']");
		for(var i = 0; i < firstIntvFromDateSubm.length; i++){
			jQuery(firstIntvFromDateSubm[i]).val(fromDateSubm);
		}
	}

	if(typeof thruDateDisp != "undefined"){
		firstIntvThruDateDisp = jQuery("input[name^='firstIntvThruDate_']");
		for(var i = 0; i < firstIntvThruDateDisp.length; i++){
			jQuery(firstIntvThruDateDisp[i]).val(thruDateDisp);
		}
	}

	if(typeof thruDateSubm != "undefined"){
		firstIntvThruDateSubm = jQuery("input[name='firstIntvThruDate']");
		for(var i = 0; i < firstIntvThruDateSubm.length; i++){
			jQuery(firstIntvThruDateSubm[i]).val(thruDateSubm);
		}
	}

	var selectedItemStep2 = $("#jqxComboBox2").jqxComboBox('getSelectedItems');
	for(var i = 0; i < selectedItemStep2.length; i++){
		var indexTemp = selectedItemStep2[i].index;
		$("#jqxComboBox3").jqxComboBox('selectIndex', indexTemp);
	}
};

function copyDataInStep3(){
	var firstIntvName = jQuery("#firstIntvName").val();
	var firstIntvDescription = jQuery("#firstIntvDescription").val();
	var fromDateArrDisp = jQuery("input[name^='firstIntvFromDate_']");
	var fromDateArrSubm = jQuery("input[name='firstIntvFromDate']");
	
	if(fromDateArrDisp.length > 0){
		var fromDateDisp = jQuery(fromDateArrDisp[0]).val();
	}
	if(fromDateArrSubm.length > 0){
		fromDateSubm = jQuery(fromDateArrSubm[0]).val();
	}
	var thruDateArrDisp = jQuery("input[name^='firstIntvThruDate_']");
	var thruDateArrSubm = jQuery("input[name='firstIntvThruDate']");
	
	if(thruDateArrDisp.length > 0){
		var thruDateDisp = jQuery(thruDateArrDisp[0]).val();
	}
	
	if(thruDateArrSubm.length > 0){
		thruDateSubm = jQuery(thruDateArrSubm[0]).val();
	}
	
	var firstIntvLocation = jQuery("input[name='firstIntvLocation']").val();
	
	jQuery("#secondIntvName").val(firstIntvName);
	jQuery("#secondIntvDescription").val(firstIntvDescription);
	
	if(typeof fromDateDisp != "undefined"){
		secondFromDateArrDisp = jQuery("input[name^='secondIntvFromDate_']");
		for(var i = 0; i < secondFromDateArrDisp.length; i++){
			jQuery(secondFromDateArrDisp[i]).val(fromDateDisp);
		}
	}
	if(typeof fromDateSubm != "undefined"){
		secondFromDateArrSubm = jQuery("input[name='secondIntvFromDate']");
		for(var i = 0; i < secondFromDateArrSubm.length; i++){
			jQuery(secondFromDateArrSubm[i]).val(fromDateSubm);
		}
	}
	
	if(typeof thruDateDisp != "undefined"){
		secondThruDateArrDis = jQuery("input[name^='secondIntvThruDate_']");
		for(var i = 0; i < secondThruDateArrDis.length; i++){
			jQuery(secondThruDateArrDis[i]).val(thruDateDisp);
		}
	}
	
	if(typeof  thruDateSubm != "undefined"){
		secondThruDateArrSubm = jQuery("input[name='secondIntvThruDate']");
		for(var i = 0; i < secondThruDateArrSubm.length; i++){
			jQuery(secondThruDateArrSubm[i]).val(thruDateSubm);
		}
	}
	
	jQuery("input[name='secondIntvLocation']").val(firstIntvLocation);
	
	var selectedItemStep3 = $("#jqxComboBox3").jqxComboBox('getSelectedItems');
	for(var i = 0; i < selectedItemStep3.length; i++){
		var indexTemp = selectedItemStep3[i].index;
		$("#jqxComboBox4").jqxComboBox('selectIndex', indexTemp);
	}
}

function overviewAllStep(){
	var tempTR;
	trArray = new Array();
	jQuery("#overView > tbody").html("");

	//=====================================step 1 data=================================================
	jobRequestId = "";
	jobRequest = jQuery("input[name='jobRequestId']");
	if(jobRequest.length > 0){
		jobRequestId = jQuery(jobRequest[0]).val();
	}
	tempTR="<tr><td width='25%' style='padding: 20px; border-radius: 10px;border: 1px solid #eee;text-align: center;'><label class='headerFtl' >${uiLabelMap.InitRecruitmentTestPlan}</label></td>";

	tempTR+="<td width='25%' style='padding: 20px; border-radius: 10px;border: 1px solid #eee;'><label class='headerFtl' >${uiLabelMap.Round1}</td>";

	tempTR+="<td width='25%' style='padding: 20px; border-radius: 10px;border: 1px solid #eee;text-align: center;'><label class='headerFtl' >${uiLabelMap.Round2}</td>";

	tempTR+="<td width='25%' style='padding: 20px; border-radius: 10px;border: 1px solid #eee;text-align: center;'><label class='headerFtl' >${uiLabelMap.Round3}</td>";

	tempTR+="</tr>";
	tempTR+="<tr><td style='padding: 20px; border-radius: 10px;border: 1px solid #eee;'><table>"
	tempTR += "<tr>";
	tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.JobRequestId}</label></td>";
	tempTR += "<td style='color:red;'>"+ jobRequestId +"</td>";
	tempTR += "</tr>";
	//trArray.push(tempTR);
	
	tempTR += "<tr>";
	tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.CommonName}</label></td>";
	tempTR += "<td style='color:red;'>" + jQuery("#name").val() + "</td>";
	tempTR += "</tr>";
	//trArray.push(tempTR);
	
	tempTR += "<tr>";
	tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.Description}</label></td>";
	tempTR += "<td style='color:red;'>"+ jQuery("#description").val() +"</td>";
	tempTR += "</tr>";
	//trArray.push(tempTR);
	
	tempTR += "<tr>";
	tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.FromDate}</label></td>";

	tempTR += "<td style='color:red;'>" + jQuery("input[name^='fromDate_']").val() +"</td>";
	tempTR += "</tr>";
	//trArray.push(tempTR);
	
	tempTR += "<tr>";
	tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.ThruDate}</label></td>";
	tempTR += "<td style='color:red;'>" + jQuery("input[name^='thruDate_']").val() +"</td>";
	tempTR += "</tr></table></td>";
	//trArray.push(tempTR);

	//========================================step 2 data================================
	if(!jQuery("#skipStep2").attr("checked")){
		tempTR+="<td style='padding: 20px; border-radius: 10px;border: 1px solid #eee;'><table>";
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.RecruitmentTester}</td>";
		items2 = jQuery("#jqxComboBox2").jqxComboBox("getSelectedItems");
		selectedItems2 = "";
		$.each(items2, function (index) {
	        selectedItems2 += this.label;
	        if (items2.length - 1 != index) {
	            selectedItems2 += ", ";
	        }
	    });
		tempTR += "<td style='color:red;'>" + selectedItems2 +"</td>";
		tempTR += "</tr>";
		//trArray.push(tempTR);
		
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.FromDate}</td>";

		tempTR += "<td style='color:red;'>" + jQuery("input[name^='examFromDate_']").val() + "</td>";
		tempTR += "</tr>";
		//trArray.push(tempTR);
		
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.ThruDate}</td>";

		tempTR += "<td style='color:red;'>" + jQuery("input[name^='examThruDate_']").val() + "</td>";
		tempTR += "</tr>";
		//trArray.push(tempTR);
		
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.CommonLocation}</td>";
		tempTR += "<td style='color:red;'>" + jQuery("input[name='examLocation']").val() + "</td>";
		tempTR += "</tr></table></td>";
		//trArray.push(tempTR);

	}else{
		tempTR+="<td style='padding: 20px; border-radius: 10px;border: 1px solid #eee;'><table>";
		tempTR += "<tr><td><label style='color:red;'>${uiLabelMap.hrolbiusNoData}</label></td></tr>";
		tempTR += "</tr></table></td>";

	}	
	
	//================================step 3 data=======================================
	if(!jQuery("#skipStep3").attr("checked")){
		tempTR += "<td style='padding: 20px; border-radius: 10px;border: 1px solid #eee;'><table><tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.RecruitmentInterviewer}</td>";
		items3 = jQuery("#jqxComboBox3").jqxComboBox("getSelectedItems");
		selectedItems3 = "";
		$.each(items3, function (index) {
	        selectedItems3 += this.label;
	        if (items3.length - 1 != index) {
	            selectedItems3 += ", ";
	        }
	    });
		tempTR += "<td style='color:red;'>" + selectedItems3 +"</td>";
		tempTR += "</tr>";
		//trArray.push(tempTR);
		
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.FromDate}</td>";

		tempTR += "<td style='color:red;'>" + jQuery("input[name^='firstIntvFromDate_']").val() + "</td>";
		tempTR += "</tr>";
		//trArray.push(tempTR);
		
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.ThruDate}</td>";

		tempTR += "<td style='color:red;'>" + jQuery("input[name^='firstIntvThruDate_']").val() + "</td>";
		tempTR += "</tr>";
		//trArray.push(tempTR);
		
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.CommonLocation}</td>";
		tempTR += "<td style='color:red;'>" + jQuery("input[name^='firstIntvLocation']").val() + "</td>";
		tempTR += "</tr></table></td>";
		//trArray.push(tempTR);	

	}else{
		tempTR+="<td style='padding: 20px; border-radius: 10px;border: 1px solid #eee;'><table>";
		tempTR += "<tr><td><label style='color:red;'>${uiLabelMap.hrolbiusNoData}</label></td></tr>";
		tempTR += "</tr></table></td>";

	}	
	//========================step 4===================================================
	if(!jQuery("#skipStep4").attr("checked")){
		tempTR += "<td style='padding: 20px; border-radius: 10px;border: 1px solid #eee;'><table><tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.RecruitmentInterviewer}</td>";
		items4 = jQuery("#jqxComboBox4").jqxComboBox("getSelectedItems");
		selectedItems4 = "";
		$.each(items4, function (index) {
	        selectedItems4 += this.label;
	        if (items4.length - 1 != index) {
	            selectedItems4 += ", ";
	        }
	    });
		tempTR += "<td style='color:red;'>" + selectedItems4 +"</td>";
		tempTR += "</tr>";
		//trArray.push(tempTR);
		
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.FromDate}</td>";
		tempTR += "<td style='color:red;'>" + jQuery("input[name^='secondIntvFromDate_']").val() + "</td>";
		tempTR += "</tr>";
		//trArray.push(tempTR);
		
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.ThruDate}</td>";

		tempTR += "<td style='color:red;'>" + jQuery("input[name^='secondIntvThruDate_']").val() + "</td>";
		tempTR += "</tr>";
		//trArray.push(tempTR);
		
		tempTR += "<tr>";
		tempTR += "<td><label class='padding-bottom5 padding-right15'>${uiLabelMap.CommonLocation}</td>";
		tempTR += "<td style='color:red;'>" + jQuery("input[name='secondIntvLocation']").val() + "</td>";
		tempTR += "</tr></table></td></tr>";
		trArray.push(tempTR);
	}else{
			tempTR+="<td style='padding: 20px; border-radius: 10px;border: 1px solid #eee;'><table>";
			tempTR += "<tr><td><label style='color:red;'>${uiLabelMap.hrolbiusNoData}</label></td></tr>";
			tempTR += "</tr></table></td>"
			tempTR+="</tr>";
			trArray.push(tempTR);
	}
	
	for(var i = 0; i < trArray.length; i++){
		jQuery("#overView > tbody:last").append(trArray[i]);	
	}
	
}
</script>