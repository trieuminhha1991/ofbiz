<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupNewProbReport" style="display: none">
			<div id="windowHeaderNewProbReport">
	            <span>
	               ${uiLabelMap.createProbReport}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentNewProbReport">
			    <div id='jqxTabs' style="position: relative;">
		            <ul>
		                <li>${uiLabelMap.SelfReview}</li>
		                <li>${uiLabelMap.JobStatus}</li>
		                <li>${uiLabelMap.JobFuture}</li>
		                <li>${uiLabelMap.JobPropsal}</li>
		            </ul>
		            <div id="SelfReview" style="margin:10px">
						<div id="jqxgridSelfReview"></div>
			            <div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
		                		<button type="button" class="btn btn-primary next btn-small" >${uiLabelMap.CommonNext} <i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div id="JobStatus">
		            	<div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="createJobStatus" id="createJobStatus">	
					            <div class="row-fluid" >
									<div class="span12">
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.education}:</label>  
											<div class="controls">
												<div id="education"></div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.completedJob}:</label>  
											<div class="controls">
												<div id="completedJob"></div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.advantageAndDisadvantage}:</label>  
											<div class="controls">
												<div id="advantageAndDisadvantage"></div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
		                <div class="row-fluid jqx-tabs-button-olbius">
			            	<div class="span12" style="text-align: right">
			            		<button type="button" class="btn btn-primary btn-success back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		               			<button type="button" class="btn btn-primary next btn-small">${uiLabelMap.CommonNext}<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div id="JobFuture" style="margin:10px">
		            	<div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="createJobFuture" id="createJobFuture">	
					            <div class="row-fluid" >
									<div class="span12">
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.futureJob}:</label>  
											<div class="controls">
												<div id="futureJob"></div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
		                <div class="row-fluid jqx-tabs-button-olbius">
			            	<div class="span12" style="text-align: right">
			            		<button type="button" class="btn btn-primary btn-success back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		               			<button type="button" class="btn btn-primary btn-small next">${uiLabelMap.CommonNext}<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div id="JobPropsal" style="margin:10px">
		            	<div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="createJobPropsal" id="createJobPropsal">	
					            <div class="row-fluid" >
									<div class="span12">
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.jobProposal}:</label>  
											<div class="controls">
												<div id="jobProposal"></div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.policyProposal}:</label>  
											<div class="controls">
												<div id="policyProposal"></div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.eduWishes}:</label>  
											<div class="controls">
												<div id="eduWishes"></div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
		                <div class="row-fluid jqx-tabs-button-olbius">
			            	<div class="span12" style="text-align: right">
			            		<button type="button" class="btn btn-primary btn-success back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		               			<button type="button" id='submit' class="btn btn-primary btn-small"><i class="icon-ok"></i>${uiLabelMap.CommonCreate}</button>
		                	</div>
	                	</div>
		            </div>
		        </div>
	        </div>
		</div>
	</div>
</div>