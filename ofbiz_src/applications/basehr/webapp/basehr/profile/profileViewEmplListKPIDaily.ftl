<div id="daily_tab" class="tab-pane active">
	<div class="widget-box transparent no-bottom-border">
		<div class="widget-body">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<h4 style="line-hight:36px;padding:0px;margin:0px;float:left;text-align:left;color:#4383b4;margin-top:4px;">${StringUtil.wrapString(uiLabelMap.KPIListAssignForEmpl)}</h4>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div id="dateTimeInput_daily" class="pull-right"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="false" dataField=datafield columnlist=columnlist   
					clearfilteringbutton="true" editable="true" deleterow="false" id="jqxgrid_daily"
					addrow="false" showlist="false" sortable="false" autorowheight="true"
					url="" jqGridMinimumLibEnable="false" selectionmode=selectionmode
					editColumns="criteriaId;fromDate(java.sql.Timestamp);result(java.math.BigDecimal);periodTypeId;dateReviewed(java.sql.Timestamp);partyId"
					updateUrl="jqxGeneralServicer?jqaction=U&sname=updateKPIForEmp"
				/>	
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/profile/profileViewListEmplKPIDaily.js"></script>