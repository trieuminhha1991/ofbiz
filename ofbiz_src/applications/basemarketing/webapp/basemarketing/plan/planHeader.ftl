<div class="widget-box">
	<div class="widget-header widget-header-blue widget-header-flat">
		<h4 class="smaller"> ${uiLabelMap.marketingPlanHeader}</h4>
	</div>
	<div class="widget-body no-padding-top">
		<div class="widget-main">
			<div class="tabbable tabs-left">
				<ul class="nav nav-tabs" id="planHeaderTab">
					<li class="active">
						<a data-toggle="tab" href="#BasicInfo">
							<i class="fa fa-edit"></i>
							${uiLabelMap.BasicInfo}
						</a>
					</li>
					<li>
						<a data-toggle="tab" href="#summaryTab">
							<i class="fa fa-pencil"></i>
							${uiLabelMap.summary}
						</a>
					</li>
					<li>
						<a data-toggle="tab" href="#visionTab">
							<i class="fa fa-bullseye"></i>
							${uiLabelMap.vision}
						</a>
					</li>

					<li>
						<a data-toggle="tab" href="#missionTab">
							<i class="fa fa-globe"></i>
							${uiLabelMap.mission}
						</a>
					</li>
					<li>
						<a data-toggle="tab" href="#messagingTab">
							<i class="fa fa-comment-o"></i>
							${uiLabelMap.messaging}
						</a>
					</li>
				</ul>

				<div class="tab-content no-right-padding">
					<div id="BasicInfo" class="tab-pane active">
						<div class='row-fluid'>
							<div class='span6'>
								<div class='row-fluid margin-bottom10'>
									<div class='span5'>
										<label class='pull-right'>${uiLabelMap.marketingPlanId}</label>
									</div>
									<div class='span7'>
                                        <div class="row-fluid">
                                            <div class="span10">
                                                <input style="padding: 0 5px;flex: 1" id="marketingPlanId" value="${plan?if_exists.code?if_exists}"  data-value="${plan?if_exists.code?if_exists}" class="no-space"/>
                                            </div>
                                            <div class="span2" style=" text-align: right;">
                                                <button class="grid-action-button icon-edit" style="text-align: right;" type="button"
                                                        title="${StringUtil.wrapString(uiLabelMap.MKAutoId)}"
                                                        onclick="Planning.getAutoMarketingPlanId($('#marketingPlanId'))">
                                                </button>
                                            </div>
                                        </div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span5'>
										<label class='pull-right asterisk'>${uiLabelMap.planName}</label>
									</div>
									<div class='span7'>
										<input style="padding: 0 5px;" id="name" value="${plan?if_exists.name?if_exists}"  data-value="${plan?if_exists.name?if_exists}"/>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span5'>
										<label class='pull-right'>${uiLabelMap.planTypeId}</label>
									</div>
									<div class='span7'>
										<div id="marketingTypeId" data-value="${plan?if_exists.marketingTypeId?if_exists}"></div>
									</div>
								</div>
								<!--<div class='row-fluid margin-bottom10'>
									<div class='span5'>
										<label class='pull-right'>${uiLabelMap.budgetId}</label>
									</div>
									<div class='span7'>
										<#assign dataFieldSearch="[{ name: 'budgetId', type: 'string'},
																	{ name: 'comments', type: 'string'},
																	]"/>
										<@jqxCombobox id="budgetId" url="getBudgetMarketing"  datafields=dataFieldSearch root="results" height="25px"
													placeHolder="${StringUtil.wrapString(uiLabelMap.SearchBudget)}"
													dropDownWidth="100%" customLoadFunction="true"
													/>
									</div>
								</div>-->
								<div class='row-fluid margin-bottom10'>
									<div class='span5'>
										<label class='pull-right'>
											<#if parentPlan?exists>
												<a href="EditMarketingPlan?id=${parameters.parentPlanId}" target="_blank">${uiLabelMap.parentPlanId}</a>
											<#else>
												${uiLabelMap.parentPlanId}
											</#if>
										</label>
									</div>
									<div class='span7'>
										<#assign dataFieldPlan="[{ name: 'marketingPlanId', type: 'string'},
																	{ name: 'code', type: 'string'},
																	{ name: 'name', type: 'string'},
																	]"/>
										<@jqxCombobox id="ParentPlan" url="getAllPlanMarketing"  datafields=dataFieldPlan root="results" height="25px"
													placeHolder="${StringUtil.wrapString(uiLabelMap.SearchPlan)}"
													valueMember="marketingPlanId" displayMember="code" data="{marketingPlanId:1000}" filterable="true"
													width="98%" dropDownWidth="100%" customLoadFunction="true"
													remoteAutoComplete="true" autoDropDownHeight="true" value="${parentPlan?if_exists.code?if_exists}"/>
									</div>
								</div>
							</div>
							<div class='span6'>
								<div class='row-fluid margin-bottom10'>
									<div class='span5'>
										<label class='pull-right'>${uiLabelMap.managerPartyId}</label>
									</div>
									<div class='span7'>
										<#assign dataFieldManager="[{ name: 'firstName', type: 'string'},
																{ name: 'lastName', type: 'string'},
																{ name: 'middleName', type: 'string'},
																{ name: 'partyId', type: 'string'},
																]"/>
										<@jqxCombobox id="PartyManager" url="getMarketingEmployee"  datafields=dataFieldManager value="${plan?if_exists.partyId?if_exists}"
													root="results" height="25px" filterable="false" valueMember="partyId" displayMember="lastName"
													placeHolder="${StringUtil.wrapString(uiLabelMap.SearchManager)}" renderSelectedItem="Planning.renderPartyManagerSelected"
													width="98%" dropDownWidth="100%" customLoadFunction="true" renderer="Planning.renderPartyManager"/>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span5'>
										<label class='pull-right'>${uiLabelMap.statusId}</label>
									</div>
									<div class='span7'>
										<div id="statusId" data-value="${plan?if_exists.statusId?if_exists}"></div>
									</div>
								</div>

								<div class='row-fluid margin-bottom10'>
									<div class='span5'>
										<label class='pull-right'>${uiLabelMap.fromDate}</label>
									</div>
									<div class='span7'>
										<div id="fromDate" data-value="${plan?if_exists.fromDate?if_exists}"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span5'>
										<label class='pull-right'>${uiLabelMap.thruDate}</label>
									</div>
									<div class='span7'>
										<div id="thruDate" data-value="${plan?if_exists.thruDate?if_exists}"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div id="visionTab" class="tab-pane">
						<div class='row-fluid'>
							<div class='span12'>
								<div id="vision" data-value="${vision?if_exists.description?if_exists}" data-id="${vision?if_exists.contentId?if_exists}"></div>
							</div>
						</div>
					</div>

					<div id="missionTab" class="tab-pane">
						<div class='row-fluid'>
							<div class='span12'>
								<div id="mission" data-value="${vision?if_exists.description?if_exists}" data-id="${vision?if_exists.contentId?if_exists}"></div>
							</div>
						</div>
					</div>

					<div id="messagingTab" class="tab-pane">
						<div class='row-fluid'>
							<div class='span12'>
								<div id="messaging" data-value="${messaging?if_exists.description?if_exists}" data-id="${messaging?if_exists.contentId?if_exists}"></div>
							</div>
						</div>
					</div>

					<div id="summaryTab" class="tab-pane">
						<div class='row-fluid'>
							<div class='span12'>
								<div id="summary" data-value="${plan?if_exists.description?if_exists}"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>