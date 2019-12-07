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
	//.content-link-turnover:hover, .content-link-other:hover, .content-link-synthesis:hover {
	//	text-indent: 20px !important;
	//}
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
						<#if hasOlbEntityPermission("ETTDASHBOARD_CHART_PARTNER", "VIEW")>
						<li>
							<a href="dashboardChartPartner" target="_blank" class="content-link content-link-turnover">
								<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSDashboardChart}
							</a>
						</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_TOR_PRODSTORE_PARTNER", "VIEW")>
							<li>
								<a href="reportTorProductStore<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSPSAbbTurnOverSalesChannel}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_TORPRODUCT_PRODSTORE_PARTNER", "VIEW")>
							<li><#-- href="viewReportTurnoverProProstoLink" -->
								<a href="reportTorProductProdStore<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSPSTurnOverProductBySalesChannel}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_TORPRODUCT_SALESCHANNEL_PARTNER", "VIEW")>
							<li><#-- href="viewReportTurnoverProChaLink" -->
								<a href="reportTorProductSalesChannel<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSPSAbbTurnOverProductBySalesChannelType}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_TORPRODUCT_REGION_PARTNER", "VIEW")>
							<li><#-- href="viewReportProRegLink" -->
								<a href="reportTorProductRegion<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSAbbTurnoverProductByRegion}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_TORPRODUCT_CUSTOMER_PARTNER", "VIEW")>
							<li><#-- href="viewReportTurnoverCusLink" -->
								<a href="reportTorCustomer<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSTurnoverCustomer}
								</a>
							</li>
						</#if>
						
						<#if hasOlbEntityPermission("SGREPORT_SALES_REPORT_PARTNER", "VIEW")>
						<li>
						<a href="viewReportGrowthLink<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
						<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSGrowthTurnover}
						</a>
						</li>
						</#if>
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
						<#if hasOlbEntityPermission("REPORTSA_SYNTORPRODUCT_PRODSTORE_PARTNER", "VIEW")>
							<li><#-- href="viewReportSynTurStoLink" -->
								<a href="reportSynTorProductProdStore<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSPSTurnoverProductSynthesisByChannel}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_SYNTORSALESEMPL_PROD_PARTNER", "VIEW")>
							<li>
								<a href="reportSynTorSalesEmpl<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSTurnoverSynthesisByStaff}
								</a>
							</li>
							<li><#-- href="viewReportSynTurCaCeLink" -->
								<a href="reportSynTorSalesEmplProduct<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSTurnoverProductSynthesisByStaff}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_SYNTORSALESEXEC_PROD_PARTNER", "VIEW")>
							<li>
								<a href="reportSynTorSalesExec<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSTurnoverSynthesisBySalesExecutive}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_SYNTORSALESEXEC_BYPROD_PARTNER", "VIEW")>
							<li><#-- href="viewReportSynTurSaExLink" -->
								<a href="reportSynTorSalesExecProduct<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSTurnoverProductSynthesisBySalesExecutive}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_TOR_PRODSTORE_PARTNER", "VIEW")>
							<li>
								<a href="reportQtyOrderProductStore<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSPSQuantityOrderByProductStore}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_PERCKHTT_PRODSTORE_PARTNER", "VIEW")>
							<li>
								<a href="reportPercKhttProductStore<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSPSPercentOrderHasKhttCard}
								</a>
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
						<#if hasOlbEntityPermission("REPORTSA_OTHERTOR_SALESORDER_PARTNER", "VIEW")>
							<li><#-- href="salesDetailReport" -->
								<a href="reportOtherTorSalesOrder<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSReportSalesOrder}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_OTHERTOR_CUSTLOYALTY_PARTNER", "VIEW")>
							<li><#-- href="viewReportCustomerSatisfactionLink" -->
								<a href="reportTorProductCustomerLoyalty<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSCustomerSatisfaction}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("SSAEXREPORT_SALES_REPORT_PARTNER", "VIEW")>
							<li>
								<a href="reportOtherUsePromoCode<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSVoucherReport2}
								</a>
							</li>
						</#if>
						
						<#if hasOlbEntityPermission("REPORTSA_OTHERTOR_CATEGORY_PARTNER", "VIEW")>
							<li><#-- href="departmentSummaryReport" -->
								<a href="reportOtherTorCategory<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.ReportDepartmentSummaryMenu}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_OTHERTOR_PRODUCT_PARTNER", "VIEW")>
							<li><#-- href="itemSummaryReport" -->
								<a href="reportOtherTorProduct<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.ReportItemSummaryMenu}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_OTHERTOR_CUSTOMER_PARTNER", "VIEW")>
							<li><#-- href="customerSummaryReport" -->
								<a href="reportOtherTorCustomer<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.ReportCustomerSummaryMenu}
								</a>
							</li>
						</#if>
						<#--
						<#if hasOlbEntityPermission("POS_SALES_REPORT", "VIEW")>
							<li>
								<a href="detailCustomerReport" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.ReportDetailCustomerMenu}
								</a>
							</li>
							
							<li>
								<a href="returnReport<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.ReportReturnReport}
								</a>
							</li>
						</#if>
						-->
						<#if hasOlbEntityPermission("REPORTSA_TORRETURN_PRODSTORE_PARTNER", "VIEW")>
							<li>
								<a href="reportTorReturnByProductStore<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSPSAbbTurnOverReturnByProductStore}
								</a>
							</li>
						</#if>
						<#if hasOlbEntityPermission("REPORTSA_OTHERTOR_SALESDATA_PARTNER", "VIEW")>
							<li>
								<a href="reportStatisticSalesData<#if viewPartner?exists && viewPartner == "Y">?viewPartner=Y</#if>" target="_blank" class="content-link content-link-turnover">
									<i class="ace-icon fa fa-angle-right green icon-main"></i>${uiLabelMap.BSReportStatisticSalesData}
								</a>
							</li>
						</#if>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>