<div id="monthly_li" class="tab-pane">
	<div id="containerNtf_jqxgrid_monthly" style="background-color: transparent; overflow: auto; width: 100%;"></div>
		<div id="jqxNotificationNtf_jqxgrid_monthly">
	    	<div id="notificationContentNtf_jqxgrid_monthly"></div>
		</div>
	<div class="widget-box transparent no-bottom-border">
		<div class="widget-body">
			<div class="row-fluid">
				<div class="span12">
					<div class="span4">
						<div class='row-fluid margin-bottom10'>
							<h4 style="line-hight:36px;padding:0px;margin:0px;float:left;text-align:left;color:#4383b4;margin-top:4px;">${StringUtil.wrapString(uiLabelMap.KPIListAssignForEmpl)}</h4>
						</div>
					</div>
					<div class="span8">
						<div class='row-fluid'>
							<div class="span12">
								<button class="btn-primary btn-mini btn pull-right" style="margin-right: 4px" id="searchResultDataKPI" type="button"><i class="icon-search"></i></button>
								<div id="year_to" class="pull-right" style="margin: 0 4px"></div>
								<div id="month_to" class="pull-right"></div>
								<label class="pull-right" style="margin: 4px 10px 0 10px">${uiLabelMap.HRCommonToLowercase}</label>
								<div id="year_from" class="pull-right" style="margin-left: 4px"></div>
								<div id="month_from" class="pull-right"></div>
								<label class="pull-right" style="margin: 4px 10px 0 10px">${uiLabelMap.HRCommonMonth}</label>
                                <#--<button class="btn-primary btn-mini btn pull-right" title="${uiLabelMap.HRApproveAcceptAll}"
										style="margin-right: 4px" id="btnAcceptAllMonth" type="button"><i class="fa fa-check"></i></button>-->
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="false" dataField=datafield columnlist=columnlist   
				editable="false" deleterow="false" id="jqxgrid_monthly"
				addrow="false" showlist="false" sortable="false" autorowheight="true"
				url="" jqGridMinimumLibEnable="false" />
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewResultDataKPIEmplMonth.js?v=0.0.1"></script>