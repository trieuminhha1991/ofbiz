<div class="row-fluid margin-bottom-10" id="UsingHistory">
	<div class="span12">
		<div class="widget-box">
			<div class="widget-header widget-header-blue widget-header-flat">
				<h4 class="smaller"> ${uiLabelMap.DAUsingHistory}</h4>
				<div class="widget-toolbar">
					<!-- <a class="blue" style="cursor:pointer" id="addProductUsingHistory">
							<i class="fa-plus"></i>&nbsp;</a> -->
					<a href="#memberUsingCollapse" role="button" data-toggle="collapse">
						<i class="fa-chevron-left"></i>
					</a>
				</div>
			</div>
			<div class="widget-body no-padding-top collapse" id="memberUsingCollapse">
				<div class="widget-main">
					<div class="row-fluid">
						<div class="span6">
							<div class="zone info-zone padding-top10" id="member" style="padding-left: 10px;">
								<#assign familyUrl="" />
								<#include "memberInformation.ftl"/>
							</div>
						</div>
						<div class="span6">
							<div class="zone info-zone" style="padding: 10px;padding-left: 0; padding-bottom:0">
								<#include "popup/claimingForm.ftl"/>
								<hr/>
								<#include "popup/memberUsing.ftl"/>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>