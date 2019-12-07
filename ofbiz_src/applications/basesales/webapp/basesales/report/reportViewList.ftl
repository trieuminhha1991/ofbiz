<style>
	.header {
		color: #fff;
		width: 100%;
		height: 50px;
		box-shadow: inset 0 0 5px #999;
	}
	.header-staff {
		background: linear-gradient(to right, #1C79BF, #00ADEF, #1C79BF);
	}
	.header-turnover {
		background: linear-gradient(to right, #F9B339, #FFDE17, #F9B339);
	}
	.header-synthesis {
		background: linear-gradient(to right, #09743C, #37B24A, #09743C);
	}
	.content-icon {
		width: 74px;
		background: #fff;
		height: 38px;
		color: #00ADEF;
		display: flex;
		box-shadow: inset 0 5px 10px -5px #999;
	}
	.content-icon > i {
		font-size: 25px;
		margin: 11px auto;
	}
	.arrow-x {
		height: 0px;
		width: 0px;
		border-top: 10px solid #fff;
		border-right: 37px solid transparent;
		border-left: 37px solid transparent;
		float: left;
	}
	.title {
		display: flex;
	}
	.title > div {
		margin: 10px 15px 10px 34px;
	}
	.content-link {
		line-height: 30px;
		text-indent: 10px;
		font-size: 15px;
	}
	ul, li {
		list-style: none;
		margin: 0;
	}
	.content-link-turnover:hover, .content-link-other:hover, .content-link-synthesis:hover {
		text-indent: 20px !important;
	}
	.content > ul > li > a {
		color: #000;
	}
	.widget-color-blue > .widget-header {
		background: #307ECC;
		border-color: #307ECC;
	}
	.pricing-box .widget-header {
		text-align: center;
		padding-left: 0;
	}
	.widget-header > .widget-title {
		line-height: 36px;
		padding: 0;
		margin: 0;
		display: inline;
		color: #fff;
	}
	.header1 {
		margin: 0 !important;
	}
	.header1 > div.widget-body {
		padding-top: 0 !important;
	}
	.main1 {
		padding: 25px 12px !important;
	}
	.body1 {
		margin-top: -10px !important;
		padding-top: 0 !important;
	}
	.main1 li {
		margin: 0 !important;
	}
	.icon-main {
		padding-right: 5px;
	}
	.btn-blue:focus, .btn-blue:hover {
		background-color: #307ECC !important;
		border-color: #307ECC !important;
	}
	.widget-color-green > .widget-header {
		background: #82AF6F;
		border-color: #82AF6F;
	}
	.btn-green:focus, .btn-green:hover {
		background-color: #82AF6F !important;
		border-color: #82AF6F !important;
	}
	.widget-color-grey {
		background-color: #9e9e9e !important;
	    border-color: #9e9e9e !important;
	}
	.widget-color-grey>.widget-header {
	    border-color: #aaa;
	    background: #848484;
	}
</style>
<div class="row-fluid">
	<div class="span4 pricing-box">
		<div class="widget-box widget-color-blue">
			<div class="widget-header header1">
				<h4 class="widget-title bigger lighter" style="font-weight: 500"><i class="fa fa-line-chart"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSMTurnover)}</h4>
			</div>
			<div class="widget-body body1">
				<div class="widget-main main1">
					<ul class="list-unstyled spaced2">
						<#if security.hasPermission("MTLPPSREPORT_VIEW", session)>
						<li>
							<a href='viewReportTurnoverProProstoMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${StringUtil.wrapString(uiLabelMap.BSByProductnStore)} </a>
						</li></#if>
						
						<#if security.hasPermission("MTLPCREPORT_VIEW", session)>
						<li>
							<a href='viewReportTurnoverProChaMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${StringUtil.wrapString(uiLabelMap.BSByProductnChannel)} </a>
						</li></#if>
						
						<#if security.hasPermission("SGREPORT_VIEW", session)>
						<li>
							<a href='viewReportGrowthMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${StringUtil.wrapString(uiLabelMap.BSGrowth)} </a>
						</li></#if>
						
						<#if security.hasPermission("CREPORT_VIEW", session)>
						<li>
							<a href='viewReportTurnoverCusMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${StringUtil.wrapString(uiLabelMap.BSByProductnCustomer)} </a>
						</li></#if>
						
						<#if security.hasPermission("PREPORT_VIEW", session)>
						<li>
							<a href='viewReportProRegMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${StringUtil.wrapString(uiLabelMap.BSByProductnRegion)} </a>
						</li></#if>
						
						<#if security.hasPermission("RSREPORT_VIEW", session)>
						<li>
							<a href='viewEvaluateReportRSMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${StringUtil.wrapString(uiLabelMap.BSByProductnState)} </a>
						</li></#if>
						
						<#if security.hasPermission("TUPPSSMREPORT_VIEW", session)>
						<li>
							<a href='viewReportPPSSMLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSRevenueProductProductStore}</a>
						</li></#if>
						
						<#if security.hasPermission("DISTRIBUTOR_ADMIN", session)>
						<li>
							<a href='viewReportProDisLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSTurnoverReport}</a>
						</li></#if>
						
						<#if security.hasPermission("TURBYCUSTTYPEREPORT_VIEW", session)>
						<li>
							<a href='viewRevenueByCusTypeReportLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSMRevenueByCustomerType}</a>
						</li></#if>
						
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div class="span4 pricing-box">
		<div class="widget-box widget-color-green">
			<div class="widget-header header1">
				<h4 class="widget-title bigger lighter" style="font-weight: 500"><i class="fa fa-history"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSSynthesis)}</h4>
			</div>
			<div class="widget-body body1">
				<div class="widget-main main1">
					<ul class="list-unstyled spaced2">
						<#if security.hasPermission("SREPORT_VIEW", session)>
						<li>
							<a href='viewReportSynTurStoMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSMByStore)} </a>
						</li></#if>
						
						<#if security.hasPermission("SCACEREPORT_VIEW", session)>
						<li>
							<a href='viewReportSynTurSaAdMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSBySaAd)} </a>
						</li></#if>
						
						<#if security.hasPermission("SSAEXREPORT_VIEW", session)>
						<li>
							<a href='viewReportSynTurSaExMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSBySaex)} </a>
						</li></#if>
						
						<#if security.hasPermission("SSAEXREPORT_VIEW", session)>
						<li>
							<a href='viewSynthesisRevenueReportLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSByLevel)} </a>
						</li>
						</#if>
						<#if security.hasPermission("SYNSMREPORT_VIEW", session)>
						<li>
							<a href='viewSynthesisReportSESMLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSTurnoverSynthesisBySalesExecutive}</a>
						</li>
						</#if>
						<#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>
							<li>
								<a href='distributorFacilityReport' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${uiLabelMap.InventoryReportTotal}</a>
							</li>
							<li>
								<a href='reportExportTotalDistributor' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BLExportedReport}</a>
							</li>
							<li>
								<a href='reportReceiveTotalDistributor' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BLReceivedReport}</a>
							</li>
						</#if>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div class="span4 pricing-box">
		<div class="widget-box widget-color-grey">
			<div class="widget-header header1">
				<h4 class="widget-title bigger lighter" style="font-weight: 500"><i class="fa fa-crosshairs" aria-hidden="true"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSOther)}</h4>
			</div>
			<div class="widget-body body1">
				<div class="widget-main main1">
					<ul class="list-unstyled spaced2">
						<#if security.hasPermission("TPREPORT_VIEW", session)>
						<li>
							<a href='viewReportTopProMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSTopProduct)} </a>
						</li></#if>
						<#if security.hasPermission("ROREPORT_VIEW", session)>
							<li>
								<a href='viewReportReturnProductReport' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.LogReturnProductReport)} </a>
							</li>
						</#if>
						<#if security.hasPermission("TTCREPORT_VIEW", session)>
						<li>
							<a href='viewEvaluateReportTTCMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSMTopRevenueByDistributor)} </a>
						</li></#if>
						
						<#if security.hasPermission("ESREPORT_VIEW", session)>
						<li>
							<a href='viewEvaluateEffectiveSalesReportMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSEffectiveSales)} </a>
						</li></#if>
						
						<#if security.hasPermission("ESREPORT_VIEW", session)>
						<li>
							<a href='viewEvaluateSpecialPromoReportMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSSpecialPromotion)} </a>
						</li></#if>
						
						<#if security.hasPermission("PARTYSALESMAN_VIEW", session)>
						<li>
							<a href='viewExhibitionPromotionDistributorLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSSpecialPromoReport)} </a>
						</li></#if>
						
						<#if security.hasPermission("TPSMREPORT_VIEW", session)>
						<li>
							<a href='viewReportTPSMLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSTopProduct}</a>
						</li></#if>
						
						<#if security.hasPermission("CUSSMREPORT_VIEW", session)>
						<li>
							<a href='viewReportCSMLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${uiLabelMap.BSTurnoverCustomer}</a>
						</li></#if>
						
						<#if security.hasPermission("PARTYSALESMAN_VIEW", session)></#if>
						<li>
							<a href='viewRouteHistoryReport' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSRouteHistory)} </a>
						</li>
						
						<#if security.hasPermission("PARTYSALESMAN_VIEW", session)></#if>
						<li>
							<a href='ListProductInventoryCustomer' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${uiLabelMap.BSInventoryReport}</a>
						</li>
						
						<#if security.hasPermission("DISTRIBUTOR_ADMIN", session)>
						<li>
							<a href='viewEvaluateEffectiveSalesDistributorReportMTLLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.BSEffectiveSales)} </a>
						</li>
						</#if>
						<#if security.hasEntityPermission("DISTRIBUTOR", "_ADMIN", session)>
						<li>
						<a href='getReturnProductDistributorReport' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${StringUtil.wrapString(uiLabelMap.LogReturnProductDistributorReport)} </a>
						</li>
						</#if>
						<li>
							<a href='viewNewAgencyLink' target="_blank" class="content-link content-link-turnover"> <i class="ace-icon fa fa-angle-right green icon-main"></i> ${uiLabelMap.BSMNewAgency}</a>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>