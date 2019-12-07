<div id="routeoverview-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "routeoverview-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<#if routeInfo.statusId?exists>
			<div class="title-status" id="statusTitle">
			</div>
		</#if>
		<div><!--class="widget-main"-->
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
					<div class="row-fluid">
						<div class="span5">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BsRouteId}:</label>
								</div>
								<div class="div-inline-block">
									<span><i>${routeInfo.routeCode?if_exists}</i></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSRouteName}:</label>
								</div>
								<div class="div-inline-block">
									<span>${routeInfo.routeName?if_exists}</span>
								</div>
							</div>
                            <div class="row-fluid">
                                <div class="div-inline-block">
                                    <label>${uiLabelMap.BSDescription}:</label>
                                </div>
                                <div class="div-inline-block">
                                    <span>${routeInfo.description?if_exists}</span>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="div-inline-block">
                                    <label>${uiLabelMap.CreatedBy}:</label>
                                </div>
                                <div class="div-inline-block">
                                    <span>
                                        [${createdBy.partyCode?if_exists}] ${createdBy.lastName?if_exists} ${createdBy.middleName?if_exists} ${createdBy.firstName?if_exists}
                                    </span>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="div-inline-block">
                                    <label>${uiLabelMap.BSCreatedDate}:</label>
                                </div>
                                <div class="div-inline-block">
                                    <span>${routeInfo.createdDate?string("yyyy-MM-dd HH:mm:ss.SSS")}</span>
                                </div>
                            </div>
						</div><!--.span6-->
						<div class="span5">
                            <div class="row-fluid">
                                <div class="div-inline-block">
                                    <label>${uiLabelMap.BSSupervisor}:</label>
                                </div>
                                <div class="div-inline-block">
                                    <span>
                                        [${manager.partyCode?if_exists}] ${manager.lastName?if_exists} ${manager.middleName?if_exists} ${manager.firstName?if_exists}
                                    </span>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="div-inline-block">
                                    <label>${uiLabelMap.BSSalesExecutive}:</label>
                                </div>
                                <div class="div-inline-block">
                                    <span>
                                        [${routeInfo.salesmanCode?if_exists}] ${routeInfo.salesmanName?if_exists}
                                    </span>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="div-inline-block">
                                    <label>${uiLabelMap.BSScheduleDescription}:</label>
                                </div>
                                <div class="div-inline-block">
                                    <span id="info_scheduleRoute">
                                    </span>
                                </div>
                            </div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>