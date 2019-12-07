<#include "component://delys/webapp/delys/marketing/marketingRequestHeader.ftl"/>

<div class="marketing-wrapper">
	<#include "component://delys/webapp/delys/marketing/trade/formSampling.ftl"/>
	<div class="tabbable tabs-shadow tabs-space margin-bottom8">
		<ul class="nav nav-tabs" id="myTab4">
			<li class="active">
				<a data-toggle="tab" href="#productTab">${uiLabelMap.Product}</a>
			</li>
			<li>
				<a data-toggle="tab" href="#costTab">${uiLabelMap.Cost}</a>
			</li>
		</ul>
		<div class="tab-content trade-content">
			<div id="productTab" class="tab-pane in active">
				<div class="row-fluid paddingtop-10">
					<div class="span6">
						<div class="row-fluid">
							<div class="span6">
								<label class="header-action"> ${uiLabelMap.chooseCity} </label>
							</div>
							<div class="span6" style="margin-left: 0px">
								<select id="chooseProvince" value="<#if info?exists && info.geoName?exists>${info.geoName}</#if>">
									<#if products?exists>
									<#list province as geo>
									<option value="${geo.geoId}">${geo.geoName}</option>
									</#list>
									</#if>
								</select>
							</div>
						</div>
					</div>
					<div class="span6 no-margin">
						<div class="row-fluid">
							<div class="span3">
								<label class="header-action"> ${uiLabelMap.chooseCounty} </label>
							</div>
							<div class="span5">
								<select id="district">

								</select>
							</div>
							<div class="span4">
								<button id="addAddress" class="btn btn-primary btn-small open-sans disabled" style="margin-left: 20px">
									${uiLabelMap.addPlace}
								</button>
							</div>
						</div>
					</div>
				</div>
				<div class="cost-form" id="addressContainer">
					<div class="row-al header-cost-form">
						<div class="col-al3 aligncenter">
							${uiLabelMap.District}
						</div>
						<div class='col-al9'>
							<div class='row-al'>
								<div class="col-al7 aligncenter borderleft">
									${uiLabelMap.Place}
								</div>
								<div class="col-al5 aligncenter borderleft">
									${uiLabelMap.Sup}
								</div>
							</div>
						</div>
					</div>
					<div id="address-form" class="form-content">
						<#assign loadingid = '1'>
						<#include "component://delys/webapp/delys/loading.ftl"/>
					</div>
				</div>
				<#include "component://delys/webapp/delys/marketing/productUom.ftl"/>
			</div>
			<div id="costTab" class="tab-pane">
				<#include "component://delys/webapp/delys/marketing/costList.ftl"/>
			</div>
		</div>
	</div>
	<div class="control-action">
		<button class="btn btn-primary" id="submit">
			<i class='fa fa-check'></i>&nbsp;${uiLabelMap.submit}
		</button>
		<button class="btn btn-success" id="reset">
			<i class='fa fa-refresh'></i>&nbsp;${uiLabelMap.reset}
		</button>
	</div>
</div>
