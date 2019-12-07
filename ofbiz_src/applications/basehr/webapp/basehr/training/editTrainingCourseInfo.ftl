<div id="editTrainingInfoWindow" class="hide">
	<div>${uiLabelMap.EditTrainingCourse}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class="row-fluid">
	    		<div class="span12">
		    		<div class="span6">
		    			<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.TrainingCourseIdShort}</label>
							</div>  
							<div class="span7">
								<input type="text" id="trainingCourseCode">
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.HRRegisterStartDateShort}</label>
							</div>  
							<div class="span7">
								<div id="registerFromDate"></div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.HRCommonFromDate}</label>
							</div>  
							<div class="span7">
								<div id="fromDate"></div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.CommonLocation}</label>
							</div>  
							<div class="span7">
								<input type="text" id="location">
					   		</div>
						</div>
		    		</div>
		    		
		    		<div class="span6">
		    			<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.TrainingCourseNameShort}</label>
							</div>  
							<div class="span7">
								<input type="text" id="trainingCourseName">
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.HRRegisterEndDateShort}</label>
							</div>  
							<div class="span7">
								<div id="registerThruDate"></div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.HRCommonThruDate}</label>
							</div>  
							<div class="span7">
								<div id="thruDate"></div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.TrainingFormTypeId}</label>
							</div>  
							<div class="span7">
								<div id="trainingFormTypeId"></div>
					   		</div>
						</div>
		    		</div>
	    		</div>
    		</div>
    		<div class="row-fluid">
    			<div class="span12">
    				<div class='row-fluid margin-bottom10'>
						<div class='span2 text-algin-right' style="margin-left: 29px">
							<label class="">${uiLabelMap.CommonPurpose}</label>
						</div>  
						<div class="span9">
							<div id="trainingPurposeTypeId"></div>
				   		</div>
					</div>
    			</div>
    		</div>
    		<div class="row-fluid">
    			<div class="span12">
    				<div class='row-fluid margin-bottom10'>
    					<div class='span2 text-algin-right' style="margin-left: 29px">
		    				<label class="">${uiLabelMap.CommonCertificate}</label>
		    			</div>
		    			<div class="span9">
							<input type="text" id="certificate">
				   		</div>
    				</div>
    			</div>
    		</div>
    		<div class="row-fluid">
    			<div class="span12">
	    			<div class='row-fluid margin-bottom10'>
		    			<div class='span2 text-algin-right' style="margin-left: 29px">
		    				<label class="">${uiLabelMap.CommonDescription}</label>
		    			</div>
		    			<div class="span9">
		    				<textarea id="description"></textarea>
		    			</div>
	    			</div>
    			</div>
    		</div>
    		<div class="row-fluid no-left-margin">
				<div id="loadingTrainingCourseInfo" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerTrainingCourseInfo"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditTrainingCourseInfo">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditTrainingCourseInfo">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/training/editTrainingCourseInfo.js"></script>
