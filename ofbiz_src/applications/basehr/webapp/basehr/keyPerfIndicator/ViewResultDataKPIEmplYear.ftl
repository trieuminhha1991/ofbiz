<div id="yearly_li" class="tab-pane">
	<div id="containerNtf_jqxgrid_yearly" style="background-color: transparent; overflow: auto; width: 100%;"></div>
		<div id="jqxNotificationNtf_jqxgrid_yearly">
	    	<div id="notificationContentNtf_jqxgrid_yearly"></div>
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
						<div class='row-fluid margin-bottom10'>
							<div class="span12">
								<div id="year_yearly" class="pull-right" style="margin-right: 4px"></div>
                                <#--<button class="btn-primary btn-mini btn pull-right" title="${uiLabelMap.HRApproveAcceptAll}"
                                        style="margin-right: 4px" id="btnAcceptAllYear" type="button"><i class="fa fa-check"></i></button>-->
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="false" dataField=datafield columnlist=columnlist   
				editable="false" deleterow="false" id="jqxgrid_yearly"
				addrow="false" showlist="false" sortable="false" autorowheight="true"
				url="" jqGridMinimumLibEnable="false" />
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewResultDataKPIEmplYear.js?v=0.0.1"></script>