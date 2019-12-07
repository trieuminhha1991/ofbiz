<script>
	var reasonContacted = [<#if reasonContacted?exists><#list reasonContacted as reason>{enumTypeId: "${reason.enumTypeId}", description: "${StringUtil.wrapString(reason.description)?default("")}"},</#list></#if>];
	var reasonUnContacted = [<#if reasonUnContacted?exists><#list reasonUnContacted as reason>{enumTypeId: "${reason.enumTypeId}", description: "${StringUtil.wrapString(reason.description)?default("")}"},</#list></#if>];
	var reasonType = _.union(reasonContacted, reasonUnContacted);
</script>

<style>
	.table-border {
		border: 1px solid #ccc;
		border-collapse: collapse;
		padding: 10px 0;
	}
</style>


<div id="notifyAssign" class="notify-assign">&nbsp;</div>
<div class="row-fluid">
	<div class="span12" id="assignmentZone">
		<div class="condition-zone">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<div class="span4">
							<div class="row-fluid margin-bottom10">
								<div class="span5">
									<label class="text-right">${uiLabelMap.FromDataSource}&nbsp;</label>
								</div>
								<div class="span7">
									<div id="DataSource"></div>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span5">
									<label class="text-right">${uiLabelMap.RecentlyCommunicate}&nbsp;</label>
								</div>
								<div class="span7">
									<div class="pull-left" id="entryFrom"></div>
									<div class="pull-left" id="entryTo" style="margin-left: 10px;"></div>
									<!-- <div id="RecentlyCommunicate" class="pull-left">&nbsp;</div>
									<input class="pull-left margin-left10 margin-bottom0" type="number" id="RecentlyCommunicateInput"/> -->
								</div>
							</div>
						</div>
						<div class="span4">
							<div class="row-fluid">
								<div class="span3">
									<label class="align-right">${uiLabelMap.AgeRange}&nbsp;</label>
								</div>
								<div class="span9">
									<div class="pull-left" id="AgeRangeFrom"></div>
									<i class="fa fa-long-arrow-right pull-left margin-left15" style="margin-top: 5px;">&nbsp;</i>
									<div class="pull-left" id="AgeRangeTo"></div>
								</div>
							</div>
							<div class="row-fluid margin-top10">
								<div class="span3">
									<label class="text-right">${uiLabelMap.CallResultEnum}&nbsp;</label>
								</div>
								<div class="span9 relative jqxdropdown">
									<div class="clear-dropdown"><i class="fa fa-remove">&nbsp;</i></div>
									<div id="ResultEnumId">&nbsp;</div>
								</div>
							</div>
						</div>
						<div class="span4">
							<div class="row-fluid">
								<div class="span4">
									<label class="text-right">${uiLabelMap.Region}&nbsp;</label>
								</div>
								<div class="span8 jqxdropdown">
									<div class="clear-dropdown"><i class="fa fa-remove">&nbsp;</i></div>
									<div id="Region"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span12">
							<button class="btn btn-primary form-action-button pull-right" id="searchCustomer">
								<i class="fa fa-search"></i>&nbsp;${uiLabelMap.searchContact}
							</button>
							<div class="pull-right margin-right15 notice-term" id="NoticeTerm" style="display: none">
								<i class="asterisk"></i>&nbsp;${uiLabelMap.NoticeCannotChange}
							</div>
						</div>
					</div>
				</div>
			</div>
			<hr class="margin-top15" style="margin-bottom: 15px;"/>
			<div class="row-fluid"  style="margin-bottom: 15px;">
				<div class="span8">
					<div class="row-fluid">
						<div class="span3">
							<label class="text-right">${uiLabelMap.PersonCommunicate}&nbsp;</label>
						</div>
						<div class="span9">
							<div id="Employee" class=" pull-left"></div>
						</div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span12">
							<button class="btn btn-primary form-action-button pull-right" id="autoAssign">
								<i class="fa fa-pie-chart"></i>&nbsp;${uiLabelMap.AutoAssign}
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="condition-zone margin-top10">
			<#include "listContact.ftl" />
		</div>
	</div>
</div>
