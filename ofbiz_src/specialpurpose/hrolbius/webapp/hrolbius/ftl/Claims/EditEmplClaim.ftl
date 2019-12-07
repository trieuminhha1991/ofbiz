<div class="row-fluid">
	<form action="<@ofbizUrl>createEmplClaim</@ofbizUrl>" id="EditEmplClaim" class="basic-form form-horizontal" name="EditEmplClaim" method="post">
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">${uiLabelMap.EmplClaimType}</label>
			<div class="controls">
				<select name="claimTypeId" id="EditEmplClaim_claimTypeId">					
					<#list claimTypeList as claimType>
						<option value="${claimType.claimTypeId}">${claimType.description}</option>
					</#list>
				</select>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">${uiLabelMap.NotificationHeader}</label>
			<div class="controls">
				<input type="text" name="title" id="EditEmplClaim_title">
			</div>
		</div>
		<#assign observerIdList = Static["com.olbius.util.PartyUtil"].getAllManagerInOrg(delegator)>
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">${uiLabelMap.ClaimSettlement}</label>
			<div class="controls">
				<@htmlTemplate.renderComboxBox name="partyClaimSettlement" id="partyClaimSettlement" emplData=observerIdList container="jqxComboBox1" multiSelect="false"/>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.ObserverClaimList}</label>
			<div class="controls">
				<@htmlTemplate.renderComboxBox name="ObserverIdList" id="observerIdList" emplData=observerIdList container="jqxComboBox2"/>
			</div>
		</div>
		
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.CommonDescription}</label>
			<div class="controls">
				<textarea class="note-area no-resize" name="description" id="EditEmplClaim_description" autocomplete="off"></textarea>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">&nbsp;</label>
			<div class="controls">
				<button type="button" class="btn btn-small btn-primary" name="submitButton" id="submitBtn"><i class="icon-ok"></i>${uiLabelMap.CommonCreate}</button>
			</div>
		</div>
	</form>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	jQuery(document).ready( function() {
		CKEDITOR.replace('EditEmplClaim_description', {
		    height: '200px',
		    width: '600px',
		    skin: 'office2013'
		});
		jQuery("#submitBtn").click(function(){
			var validateComboBox1 = $('#EditEmplClaim').jqxValidator('validate');
			if(validateComboBox1 == false) return false;			
			jQuery('#EditEmplClaim').submit();
		});
		
		$('#EditEmplClaim').validate({
			errorElement: 'span',
			errorClass: 'help-inline red-color',
			focusInvalid: false,
			ignore : ":disabled, :hidden",
			rules: {
				claimTypeId: {
					required: true,
				},
				title: {
					required: true,
				},
				
			},

			messages: {
				claimTypeId: {
					required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
				},
				title: {
					required: "<span style='color:red'>${uiLabelMap.CommonRequired}</span>",
				},
				
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
				form.submit();
			},
			invalidHandler: function (form) {
			}
		});
		
		jQuery('#EditEmplClaim').jqxValidator({
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
	});
</script>