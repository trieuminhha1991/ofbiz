<div id="wdwNewAppendix" style="display:none;">
	<div id="wdwHeaderNewAppendix">
        <span>
           ${uiLabelMap.NewAppendix}
        </span>
    </div>
    <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="wdwContentNewAppendix">
	    <div id='jqxTabs'>
            <ul>
                <li>${uiLabelMap.AppendixInfo}</li>
                <li>${uiLabelMap.AppendixTerms}</li>
            </ul>
            <div id="tab1">
	            <div class="basic-form form-horizontal" style="margin-top: 10px">
					<form name="createNewAppendix" id="createNewAppendix">
						<div class="row-fluid" >
							<div class="span12">
								<div id="agreementText" class="margin-left10"></div>
							</div>
						</div>
					</form>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button type="button" class="btn btn-primary form-action-button pull-right next" >${uiLabelMap.CommonNext} <i class="icon-arrow-right"></i></button>
						</div>
					</div>
				</div>
			</div>
			<div id="tab2">
				<div class="basic-form form-horizontal" style="margin-top: 10px">
					<form name="createNewAppendixTerms" id="createNewAppendixTerms">
						<div class="row-fluid" >
							<div class="span12">
								<div id="jqxgridAppendixTerms"></div>
							</div>
						</div>
					</form>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button type="button" id="btnCreateAppendix" class="btn btn-primary form-action-button pull-right" ><i class="icon-ok"></i>${uiLabelMap.CommonOk}</button>
							<button type="button" class="btn btn-danger form-action-button pull-right back" ><i class="icon-arrow-left"></i>${uiLabelMap.CommonBack}</button>
						</div>
					</div>
				</div>
			</div>
        </div>
    </div>
</div>