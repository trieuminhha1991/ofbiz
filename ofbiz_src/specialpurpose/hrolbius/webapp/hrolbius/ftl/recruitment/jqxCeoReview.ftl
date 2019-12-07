<div class="row-fluid" >
	<div class="span12">
	  <input type="hidden" id="probReviewId" value="${hrmReview?if_exists.probReviewId?if_exists}"/>
	  <ol type="I">
		  <li><h6><b>${uiLabelMap.proposal}</b></h6></li>
		  <div>
			 <div style='margin-top: 10px; display: inline-block' id='jqxCeoAccepted'><span>${uiLabelMap.prAccepted}</span></div>
	         <div style='margin-top: 10px; display: inline-block' id='jqxCeoRejected'><span>${uiLabelMap.prRejected}</span></div>
	         <div style='margin-top: 10px; display: inline-block' id='jqxCeoExtTime'><span>${uiLabelMap.prContinuedProbation}</span></div>
	      </div>
	      <div class="row-fluid" >
		      <div class="span12">
		      	<div class="span6">
			      	<div class="control-group no-left-margin">
						<label class="control-label" style="width: 170px !important">${uiLabelMap.extTime}</label>
						<div class="controls" style="margin-left: 230px !important">
							<div id="ceoExtTime"></div>
						</div>
					</div>
		      	</div>
		      	<div class="span6">
			      	<div class="control-group no-left-margin">
						<label class="control-label" style="width: 90px !important;">${uiLabelMap.comment}</label>
						<div class="controls" style="margin-left: 150px !important">
							<input id="ceoComment">
						</div>
					</div>
		      	</div>
		      </div>
	      </div>
      </ol>
	</div>
</div>
<script>
	isCeoReview = true;
	var isAccepted = true;
	var isRejected = false;
	var isExt = false;
	<#if ceoReview?if_exists.resultId?if_exists = 'RR_ACCEPTED'>
		isAccepted = true;
		isRejected = false;
		isExt = false;
	<#elseif ceoReview?if_exists.resultId?if_exists = 'RR_REJECTED'>
		isAccepted = false;
		isRejected = true;
		isExt = false;
	<#elseif ceoReview?if_exists.resultId?if_exists = 'RR_EXTTIME'>
		isAccepted = false;
		isRejected = false;
		isExt = true;
	</#if>
	$("#jqxCeoAccepted").jqxRadioButton({ width: 150, height: 25, checked: isAccepted});
    $("#jqxCeoRejected").jqxRadioButton({ width: 150, height: 25, checked: isRejected});
    $("#jqxCeoExtTime").jqxRadioButton({ width: 150, height: 25, checked: isExt});
    $("#ceoExtTime").jqxNumberInput({ width: '100px', height: '25px',  spinButtons: true, decimal: '${ceoReview?if_exists.extTime?if_exists}'});
    $("#ceoComment").jqxInput({});
    $("#ceoComment").val('${ceoReview?if_exists.comment?if_exists}');
</script>