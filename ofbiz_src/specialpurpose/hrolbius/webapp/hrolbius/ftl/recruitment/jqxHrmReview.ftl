<div class="row-fluid" >
	<div class="span12">
	  <input type="hidden" id="probReviewId" value="${hrmReview?if_exists.probReviewId?if_exists}"/>
	  <ol type="I">
		  <li><h6><b>${uiLabelMap.proposal}</b></h6></li>
		  <div>
			 <div style='margin-top: 10px; display: inline-block' id='jqxHrmAccepted'><span>${uiLabelMap.prAccepted}</span></div>
	         <div style='margin-top: 10px; display: inline-block' id='jqxHrmRejected'><span>${uiLabelMap.prRejected}</span></div>
	         <div style='margin-top: 10px; display: inline-block' id='jqxHrmExtTime'><span>${uiLabelMap.prContinuedProbation}</span></div>
	      </div>
	      <div class="row-fluid" >
		      <div class="span12">
		      	<div class="span6">
			      	<div class="control-group no-left-margin">
						<label class="control-label" style="width: 170px !important">${uiLabelMap.extTime}</label>
						<div class="controls" style="margin-left: 230px !important">
							<div id="hrmExtTime"></div>
						</div>
					</div>
		      	</div>
		      	<div class="span6">
			      	<div class="control-group no-left-margin">
						<label class="control-label" style="width: 90px !important;">${uiLabelMap.comment}</label>
						<div class="controls" style="margin-left: 150px !important">
							<input id="hrmComment">
						</div>
					</div>
		      	</div>
		      </div>
	      </div>
      </ol>
	</div>
</div>
<script>
	isHrmReview = true;
	var isAccepted = true;
	var isRejected = false;
	var isExt = false;
	<#if hrmReview?if_exists.resultId?if_exists = 'RR_ACCEPTED'>
		isAccepted = true;
		isRejected = false;
		isExt = false;
	<#elseif hrmReview?if_exists.resultId?if_exists = 'RR_REJECTED'>
		isAccepted = false;
		isRejected = true;
		isExt = false;
	<#elseif hrmReview?if_exists.resultId?if_exists = 'RR_EXTTIME'>
		isAccepted = false;
		isRejected = false;
		isExt = true;
	</#if>
	$("#jqxHrmAccepted").jqxRadioButton({ width: 150, height: 25, checked: isAccepted});
    $("#jqxHrmRejected").jqxRadioButton({ width: 150, height: 25, checked: isRejected});
    $("#jqxHrmExtTime").jqxRadioButton({ width: 150, height: 25, checked: isExt});
    $("#hrmExtTime").jqxNumberInput({ width: '100px', height: '25px',  spinButtons: true, decimal: '${hrmReview?if_exists.extTime?if_exists}'});
    $("#hrmComment").val('${hrmReview?if_exists.comment?if_exists}');
</script>