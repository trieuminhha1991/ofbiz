<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupReviewProbation" style="display: none">
			<div id="windowHeaderReviewProbation">
	            <span>
	               ${uiLabelMap.reviewProbation}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentReviewProbation">
		        <div id="mainSplitter">
			        <div>
		                <div style="border: none;" id="feedExpander">
		                    <div class="jqx-hideborder">${uiLabelMap.ReportAndReview}</div>
		                    <div class="jqx-hideborder jqx-hidescrollbars">
		                        <div class="jqx-hideborder" id='jqxTree'>
		                            <ul>
                                        <li id="probReportItem"><span item-title="true">${uiLabelMap.ProbationaryReport}</span> </li>
                                        <li id="leaderReview"><span item-title="true">${uiLabelMap.LeaderReview}</span></li>
                                        <li id="hrmReview"><span item-title="true">${uiLabelMap.HrmReview}</span></li>
                                        <li id="ceoReview"><span item-title="true">${uiLabelMap.CeoReview}</span></li>
		                            </ul>
		                        </div>
		                    </div>
		                </div>
		            </div>
		            <div class="splitter-panel">
			            <div class="basic-form form-horizontal" style="margin: 10px;">
		        			<form name="formSplitter" id="formSplitter">	
					            
							</form>
						</div>
		            </div>
		        </div>
                <div class="row-fluid jqx-splitter-button-olbius">
	            	<div class="span12" style="text-align: right">
	            		<button type="button" id="cancel" class="btn btn-primary btn-danger back btn-small"><i class="icon-trash"></i>${uiLabelMap.CommonCancel}</button>
               			<button type="button" id='review' class="btn btn-primary btn-small"><i class="icon-ok"></i>${uiLabelMap.CommonOk}</button>
                	</div>
            	</div>
	        </div>
		</div>
	</div>
</div>