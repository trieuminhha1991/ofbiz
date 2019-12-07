<div class="row-fluid">
	<div class="span12">
		<div id="scoreInterviewWindow" style="display: none;">
			<div id="windowHeaderScoreInterview">
	            <span>
	               ${uiLabelMap.scoreInterview}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentScoreInterview">
			    <div id='jqxScoreInterviewTabs' style="position: relative;">
		            <ul>
		                <li>${uiLabelMap.scoreDetail}</li>
		                <li>${uiLabelMap.generalScore}</li>
		                <li>${uiLabelMap.interviewerProposal}</li>
		            </ul>
		            <div id="scoreDetail" style="margin:10px">
			            <div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="scoreDetail" id="scoreDetail">
			        			<div class="row-fluid" >
				        			<div class="span12">
					        			<div class="span6">
						        			<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.FullName}:</label>  
												<div class="controls">
													<div id="invFullname"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.Gender}:</label>  
												<div class="controls">
													<div id="invGender"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.BirthDate}:</label>  
												<div class="controls">
													<div id="invBirthDate"></div>
												</div>
											</div>
					        			</div>
					        			<div class="span6">
						        			<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.Position}:</label>  
												<div class="controls">
													<div id="invEmplPositionType"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.Department}:</label>  
												<div class="controls">
													<div id="invPartyId"></div>
												</div>
											</div>
					        			</div>
				        			</div>
			        			</div>
					            <div class="row-fluid" style="margin-top: 30px">
									<div class="span12">
										<div class="span5" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 40px;">
											<div class="title-border">
												<span>${uiLabelMap.exterior}</span>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.face}:</label>  
												<div class="controls">
													<div id="face" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.figure}:</label>  
												<div class="controls">
													<div id="figure" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.voice}:</label>  
												<div class="controls">
													<div id="voice" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.communication}:</label>  
												<div class="controls">
													<div id="communication" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.confidence}:</label>  
												<div class="controls">
													<div id="confidence" ></div>
												</div>
											</div>
										</div>
										<div class="span5" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 40px;">
											<div class="title-border">
												<span>${uiLabelMap.mindMethod}</span>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.circumstance}:</label>  
												<div class="controls">
													<div id="circumstance" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.agility}:</label>  
												<div class="controls">
													<div id="agility"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.logic}:</label>  
												<div class="controls">
													<div id="logic" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.answer}:</label>  
												<div class="controls">
													<div id="answer" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.honest}:</label>  
												<div class="controls">
													<div id="honest" ></div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="row-fluid" style="margin-top: 30px">
									<div class="span12">
										<div class="span5" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 40px;">
											<div class="title-border">
												<span>${uiLabelMap.correspondent}</span>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.experience}:</label>  
												<div class="controls">
													<div id="experience" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.expertise}:</label>  
												<div class="controls">
													<div id="expertise" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.jobRequirable}:</label>  
												<div class="controls">
													<div id="jobRequirable" ></div>
												</div>
											</div>
										</div>
										<div class="span5" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 40px;">
											<div class="title-border">
												<span>${uiLabelMap.familyBackground}</span>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.parent}:</label>  
												<div class="controls">
													<div id="parentBackgroundId"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.sibling}:</label>  
												<div class="controls">
													<div id="siblingBackgroundId"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.spouses}:</label>  
												<div class="controls">
													<div id="spousesBackgroundId"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.child}:</label>  
												<div class="controls">
													<div id="childBackgroundId"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="row-fluid" style="margin-top: 30px">
									<div class="span12">
										<div class="span10" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 40px; width: 915px;">
											<div class="title-border">
												<span>${uiLabelMap.diffScore}</span>
											</div>
											<div class="span5" >
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.workChangeId}:</label>  
													<div class="controls">
														<div id="workChangeId"></div>
													</div>
												</div>
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.uniCertificateId}:</label>  
													<div class="controls">
														<div id="uniCertificateId"></div>
													</div>
												</div>
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.itCertificateId}:</label>  
													<div class="controls">
														<div id="itCertificateId"></div>
													</div>
												</div>
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.engCertificateId}:</label>  
													<div class="controls">
														<div id="engCertificateId"></div>
													</div>
												</div>
											</div>
											<div class="span5" style="margin-left: 80px;">
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.teamWorkId}:</label>  
													<div class="controls">
														<div id="teamWorkId"></div>
													</div>
												</div>
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.aloneWorkId}:</label>  
													<div class="controls">
														<div id="aloneWorkId"></div>
													</div>
												</div>
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.currentSal}:</label>  
													<div class="controls">
														<div id="currentSal"></div>
													</div>
												</div>
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.proposeSal}:</label>  
													<div class="controls">
														<div id="proposeSal"></div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
		                		<button type="button" class="btn btn-primary score-next btn-small" >${uiLabelMap.CommonNext} <i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div id="generalScore">
			            <div class="basic-form form-horizontal" style="margin-top: 10px">
			    			<form name="interviewerProposal" id="interviewerProposal">
					            <div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.generalScore}:</label>  
									<div class="controls">
										<div id="generalRate"></div>
									</div>
								</div>
							</form>
						</div>
						<div class="row-fluid jqx-tabs-button-olbius">
			            	<div class="span12" style="text-align: right">
			            		<button type="button" class="btn btn-primary btn-success score-back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		               			<button type="button" class="btn btn-primary score-next btn-small"></i>${uiLabelMap.CommonNext}<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
	            	<div id="interviewerProposal">
		            	<div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="interviewerProposal" id="interviewerProposal">
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.propose}:</label>  
									<div class="controls">
										<div id="propose"></div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.resultId}:</label>  
									<div class="controls">
										<div id="resultId" ></div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.nextRound}:</label>  
									<div class="controls">
										<div id="isNextRound" style="margin-left: -3px !important;"></div>
									</div>
								</div>
							</form>
						</div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
			            	<div class="span12" style="text-align: right">
			            		<button type="button" class="btn btn-primary btn-success score-back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		               			<button type="button" id='scoreSubmit' class="btn btn-primary btn-small"><i class="icon-ok"></i>${uiLabelMap.CommonCreate}</button>
		                	</div>
	                	</div>
	            	</div>
	            </div>
	        </div>
		</div>
	</div>
</div>