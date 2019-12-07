<div id="monthly_tab" class="tab-pane">
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
								<button class="btn-primary btn-mini btn pull-right" style="margin-right: 4px" id="searchApproveKPI" type="button"><i class="icon-search"></i></button>
								<div id="year_to" class="pull-right" style="margin: 0 4px"></div>
								<div id="month_to" class="pull-right"></div>
								<label class="pull-right" style="margin: 4px 10px 0 10px">${uiLabelMap.HRCommonToLowercase}</label>
								<div id="year_from" class="pull-right" style="margin-left: 4px"></div>
								<div id="month_from" class="pull-right"></div>
								<label class="pull-right" style="margin: 4px 10px 0 10px">${uiLabelMap.HRCommonMonth}</label>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="false" dataField=datafield columnlist=columnlist  
					clearfilteringbutton="true" editable="true" deleterow="false" id="jqxgrid_monthly" groupable="false"
					addrow="false" showlist="false" sortable="false" autorowheight="true"
					url="" jqGridMinimumLibEnable="false" selectionmode=selectionmode
					editColumns="criteriaId;fromDate(java.sql.Timestamp);result(java.math.BigDecimal);periodTypeId;dateReviewed(java.sql.Timestamp);partyId"
					updateUrl="jqxGeneralServicer?jqaction=U&sname=updateKPIForEmp"
				/>	
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/profile/profileViewListEmplKPIMonthly.js"></script>