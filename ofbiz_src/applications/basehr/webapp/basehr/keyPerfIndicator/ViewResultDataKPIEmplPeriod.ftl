<div id="daily_li" class="tab-pane active">
	<div id="containerNtf" style="background-color: transparent; overflow: auto; width: 100%;"></div>
	<div id="jqxNotificationNtf">
    	<div id="notificationContentNtf"></div>
	</div>
	<div class="widget-box transparent no-bottom-border">
		<div class="widget-body">
			<div class="row-fluid">
				<div id="jqxNotification">
					<div id="noti_Content"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<h4 style="padding:0px;margin:0px;float:left;text-align:left;color:#4383b4;margin-top:10px;">${uiLabelMap.ApproveKPIEmpl}</h4>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10 '>
							<div class="span12" style="margin-right: 10px">
								<div id="dateTimeInput" class="pull-right" style="margin-top:7px;"></div>
                                <#--<button class="btn-primary btn-mini btn pull-right" title="${uiLabelMap.HRApproveAcceptAll}"
                                        style="margin-right: 4px; margin-top:7px; width: 26px; height: 23px" id="btnAcceptAllDaily" type="button"><i class="fa fa-check"></i></button>-->
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="false" dataField=datafield columnlist=columnlist   
				editable="false" deleterow="false" id="jqxgrid"
				addrow="false" showlist="false" sortable="false" autorowheight="true"
				url="" jqGridMinimumLibEnable="false" />
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewResultDataKPIEmpl.js?v=0.0.1"></script>