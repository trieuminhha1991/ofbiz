<div id="editTrainingCostWindow" class="hide">
	<div>${uiLabelMap.TrainingCostAndProvider}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class="row-fluid">
  				<div class="span12">
  					<div class="span6 boder-all-profile" style="padding: 20px 10px 0 15px">
  						<span class="text-header">${uiLabelMap.CostEstimatedEmployee}</span>
  						<div class='row-fluid margin-bottom10'>
	  						<div class='span4 text-algin-right'>
	  							<label class="">${uiLabelMap.AmountEstimatedEmplPaid}</label>
	  						</div>
	  						<div class="span8">
	  							<div id="amountEmplPaid_Cost"></div>
	  						</div>
	  					</div>	
	  					<div class='row-fluid margin-bottom10'>
	  						<div class='span4 text-algin-right'>
	  							<label class="">${uiLabelMap.AmountCompanySupport}</label>
	  						</div>
	  						<div class="span8">
	  							<div id="amountCompanyPaid_Cost"></div>
	  						</div>
	  					</div>
  					</div>
  					<div class="span6 boder-all-profile" style="padding: 20px 10px 0 15px">
  						<span class="text-header">${uiLabelMap.TotalAmountEstimated}</span>
  						<div class='row-fluid margin-bottom10'>
	  						<div class='span4 text-algin-right'>
	  							<label class="">${uiLabelMap.CommonEstimatedNumber}</label>
	  						</div>
	  						<div class="span8">
	  							<div id="nbrEmplEstimated_Cost"></div>
	  						</div>
	  					</div>
  						<div class='row-fluid margin-bottom10'>
	  						<div class='span4 text-algin-right'>
	  							<label class="">${uiLabelMap.TotalAmountEstimated}</label>
	  						</div>
	  						<div class="span8">
	  							<div id="totalCostEstimated_Cost"></div>
	  						</div>
	  					</div>
  					</div>
  				</div>
  			</div>
  			<div class="row-fluid">
	  			<div class="span12 boder-all-profile"  style="margin-top: 15px; padding: 20px 10px 0 15px">
	  				<div class="span6">
	  					<div class='row-fluid margin-bottom10'>
		  					<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.TrainingProvider}</label>
							</div>  
							<div class="span8">
								<div id="trainingProvider_Cost"></div>
					   		</div>
		  				</div>
	  				</div>
	  				<div class="span6">
	  					<div class='row-fluid margin-bottom10'>
		  					<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.HRAddrContactShort}</label>
							</div>
							 <div class="span8">
							 	<input type="text" id="providerContact_Cost">
							 </div>
	  					</div>
	  				</div>
	  			</div>
  			</div>
  			<div class="row-fluid" style="margin-top: 15px">
  				<div class='row-fluid '>
 						<div class='span1 text-algin-right'>
 						</div>
 						<div class="span11">
 							<div id="isNotPublic_Cost"><label>${uiLabelMap.OnlyEmplInRegisterList}</label></div>
 						</div>
 					</div>
  				<div class='row-fluid margin-bottom10'>
  					<div class='span1 text-algin-right'>
					</div>  
					<div class="span11">
						<div id="isPublic_Cost"><label>${uiLabelMap.AllowAllEmployeeRegister}</label></div>
			   		</div>
  				</div>
  				<div class='row-fluid margin-bottom10'>
  					<div class='span1 text-algin-right'>
					</div>  
					<div class="span9" >
						<div id="allowCancelRegister_Cost" style="float: left; margin-left: 0 !important"><label>${uiLabelMap.AllowCancelRegisterBefore}</label></div>
						<div id="nbrDayBeforeStart_Cost" style="float: left;"></div>
						<div style="float: left; margin-left: 10px">${uiLabelMap.CommonDay}</div>
					</div>
  				</div>
  			</div>
  			<div class="row-fluid no-left-margin">
				<div id="loadingTrainingCourseCost" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerTrainingCourseCost"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditTrainingCourseCost">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditTrainingCourseCost">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/training/editTrainingCourseCost.js"></script>