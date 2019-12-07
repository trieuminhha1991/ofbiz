<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupReportDetail" style="display: none">
			<div id="windowHeaderReportDetail">
	            <span>
	               ${uiLabelMap.reportDetail}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentReportDetail">
		        <div id="mainSplitterDT">
			        <div>
		                <div style="border: none;" id="feedExpanderDT">
		                    <div class="jqx-hideborder">${uiLabelMap.reportDetail}</div>
		                    <div class="jqx-hideborder jqx-hidescrollbars">
		                        <div class="jqx-hideborder" id='jqxTreeDT'>
		                            <ul>
                                        <li id="probReportItemDT"><span item-title="true">${uiLabelMap.ProbationaryReport}</span> </li>
                                        <li id="leaderReviewDT"><span item-title="true">${uiLabelMap.LeaderReview}</span></li>
                                        <li id="hrmReviewDT"><span item-title="true">${uiLabelMap.HrmReview}</span></li>
                                        <li id="ceoReviewDT"><span item-title="true">${uiLabelMap.CeoReview}</span></li>
		                            </ul>
		                        </div>
		                    </div>
		                </div>
		            </div>
		            <div class="splitter-panel">
			            <div class="basic-form form-horizontal" style="margin: 10px;">
		        			<form name="detailForm" id="detailForm">	
					            
							</form>
						</div>
		            </div>
		        </div>
                <div class="row-fluid jqx-splitter-button-olbius">
	            	<div class="span12" style="text-align: right">
               			<button type="button" class="btn btn-primary btn-small"><i class="icon-ok"></i>${uiLabelMap.CommonOk}</button>
                	</div>
            	</div>
	        </div>
		</div>
	</div>
</div>