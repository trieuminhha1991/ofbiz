<div class="row-fluid">
	<div class="span12">
		<div id="sendEmailWindow" style="display: none;">
			<div id="windowHeaderSendEmail">
	            <span>
	               ${uiLabelMap.sendEmail}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentSendEmail">
			    <div id='jqxSendEmailTabs' style="position: relative;">
		            <ul>
		                <li>${uiLabelMap.SelectedAppl}</li>
		                <li>${uiLabelMap.EditEmail}</li>
		            </ul>
		            <div id="selectedAppl" style="margin:10px">
		            	<div id="jqxSelectedApplGrid"></div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
		                		<button type="button" class="btn btn-primary btn-small email-next" >${uiLabelMap.CommonNext} <i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
	            	<div id="jqxSendEmail">
		            	<div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="editEmail" id="editEmail">	
					            <div class="row-fluid" >
									<div class="span12">
										<div class="control-group no-left-margin">
											<label class="control-label asterisk" style="width: 100px !important;">${uiLabelMap.CommonTitle}:</label>  
											<div class="controls" style="margin-left: 115px !important;">
												<input id="titleEmail">
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label" style="width: 100px !important;">${uiLabelMap.CommonTemplate}:</label>  
											<div class="controls" style="margin-left: 115px !important;">
												<div id="templateEmail">
													<div id="jqxgridTemplateEmail"></div>
												</div>
											</div>
										</div>
										<div class="control-group no-left-margin margin-top30">
											<div class="controls" style="margin-left: 40px !important;">
												<textarea id="contentEmail"></textarea>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
			            	<div class="span12" style="text-align: right">
			            		<button type="button" class="btn btn-primary btn-success email-back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		               			<button type="button" id='emailSubmit' class="btn btn-primary btn-small"><i class="fa fa-paper-plane fa-2"></i>${uiLabelMap.sendEmail}</button>
		                	</div>
	                	</div>
	            	</div>
	            </div>
	        </div>
		</div>
	</div>
</div>