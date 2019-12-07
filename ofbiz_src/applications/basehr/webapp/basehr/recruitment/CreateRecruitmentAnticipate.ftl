<div id="addRecruitmentAnticipateWindow" class="hide">
	<div>${uiLabelMap.AddRecruitmentAnticipatePlan}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.OrganizationalUnit}</label>
							</div>
							<div class='span8'>
								<div id="dropDownButtonAddNew" class="pull-right">
									<div style="border: none;" id="jqxTreeAddNew">
									</div>
								</div>
							</div>					
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.HrCommonPosition}</label>
							</div>
							<div class="span8">
								<div id="emplPositionTypeAddNew"></div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 align-right'>
								<label class="asterisk">${uiLabelMap.HRCommonAnticipate}</label>
							</div>
							<div class="span7">
								<div id="yearAdd"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12 boder-all-profile" >
					<span class="text-header">${uiLabelMap.HRCommonQuantity}</span>
					<div class="row-fluid">
						<div class="span6">
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonJanuary}</label>
								</div>
								<div class="span8">
									<div id="month1" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonMarch}</label>
								</div>
								<div class="span8">
									<div id="month3" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonMay}</label>
								</div>
								<div class="span8">
									<div id="month5" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonJuly}</label>
								</div>
								<div class="span8">
									<div id="month7" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonSeptember}</label>
								</div>
								<div class="span8">
									<div id="month9" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonNovember}</label>
								</div>
								<div class="span8">
									<div id="month11" class="numberRecruitment"></div>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonFebruary}</label>
								</div>
								<div class="span8">
									<div id="month2" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonApril}</label>
								</div>
								<div class="span8">
									<div id="month4" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonJune}</label>
								</div>
								<div class="span8">
									<div id="month6" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonAugust}</label>
								</div>
								<div class="span8">
									<div id="month8" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonOctober}</label>
								</div>
								<div class="span8">
									<div id="month10" class="numberRecruitment"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonDecember}</label>
								</div>
								<div class="span8">
									<div id="month12" class="numberRecruitment"></div>
								</div>
							</div>
							
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAddNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAddNew"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCreate" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveCreate" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button type="button" class="btn btn-success form-action-button pull-right" id="saveAndContinue"><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/recruitment/CreateRecruitmentAnticipate.js"></script>
