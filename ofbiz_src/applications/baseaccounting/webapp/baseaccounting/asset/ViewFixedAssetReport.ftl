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
		height: 200px !important;
		overflow-y: scroll;
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
<#include "script/ViewFixedAssetReportScript.ftl"/>
<div class="row-fluid">
	<div class="span6 pricing-box">
		<div class="widget-box widget-color-blue">
			<div class="widget-header header1">
				<h4 class="widget-title bigger lighter" style="font-weight: 500"><i class="icon-list"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BACCReportList)}</h4>
			</div>
			<div class="widget-body body1">
				<div class="widget-main main1">
					<ul class="list-unstyled spaced2">
						<li>
							<a href='javascript:void(0)' class="content-link content-link-turnover" id="reportS09DNNBtn">
								<i class="ace-icon fa fa-angle-right green icon-main"></i>${StringUtil.wrapString(uiLabelMap.S09DNNTSCD)}
							</a>
						</li>
						<li>
							<a href='javascript:void(0)' class="content-link content-link-turnover" id="reportS11DNNBtn">
								<i class="ace-icon fa fa-angle-right green icon-main"></i>${StringUtil.wrapString(uiLabelMap.S11DNNTSCD)}
							</a>
						</li>
						<li>
							<a href='javascript:void(0)' class="content-link content-link-turnover" id="reportFADepBtn">
								<i class="ace-icon fa fa-angle-right green icon-main"></i>${StringUtil.wrapString(uiLabelMap.BACCFADepPeriodsReport)}
							</a>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>	
</div>
<#include "ViewReportS09DNN.ftl"/>
<#include "ViewReportS11DNN.ftl"/>
<#include "ViewReportFADep.ftl"/>