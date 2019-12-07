<#include "component://obb/webapp/obb/includes/common/paginationJs.ftl"/>
<script type="text/javascript">
    function callDocumentByPaginate(info) {
		var str = info.split('~');
		var brand = document.getElementById("txtBrand").value;
		var form = document.createElement("form");
	    form.setAttribute("method", "POST");
	    form.setAttribute("action", "<@ofbizUrl>category/~category_id=" + str[0] + "</@ofbizUrl>");
        var hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "VIEW_SIZE");
        hiddenField.setAttribute("value", str[1]);
        form.appendChild(hiddenField);

        hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "VIEW_INDEX");
        hiddenField.setAttribute("value", str[2]);
        form.appendChild(hiddenField);

        hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "hoz");
		if(document.getElementById("hoz").value=="N"){
			hiddenField.setAttribute("value", "N");
		}else{
			hiddenField.setAttribute("value", "Y");
		}
		form.appendChild(hiddenField);

		hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "sortOrder");
		hiddenField.setAttribute("value", document.getElementById("sortOrderTMP").value);
		form.appendChild(hiddenField);
		
		if (brand) {
			hiddenField = document.createElement("input");
	        hiddenField.setAttribute("type", "hidden");
	        hiddenField.setAttribute("name", "brand");
			hiddenField.setAttribute("value", brand);
			form.appendChild(hiddenField);
		}
		
	    document.body.appendChild(form);
	    form.submit();
     }
</script>
<#if productCategory?exists>
	<#assign categoryName = categoryContentWrapper.get("CATEGORY_NAME")?if_exists/>
</#if>
<div class="catalog-category-view categorypath-gifts-beauty-health-lotion-html category-lotion skin-default">
	<div id="jm-main">
		<div class="inner clearfix">
			<div id="jm-current-content" class="clearfix">
				<div class="page-title category-title">
			        <h3>${categoryName?if_exists}</h3>
				</div>
				<script type="text/javascript">
					jQuery.noConflict();
					function changeList(){
						if(document.getElementById("hoz").value =="Y"){
							document.getElementById("hoz").value = "N";
						}else{
							document.getElementById("hoz").value = "Y";
						}
						var formtmp = document.getElementById('formFilter');

				        hiddenField = document.createElement("input");
				        hiddenField.setAttribute("type", "hidden");
				        hiddenField.setAttribute("name", "VIEW_INDEX");
				        hiddenField.setAttribute("value", ${viewIndex});
				        formtmp.appendChild(hiddenField);

						formtmp.submit();
					}
				</script>
				<div class="category-products">
					<div class="toolbar">
					
					<div class="row">
						<div class='col-lg-6 col-md-5 col-sm-7 col-xs-12'>
							<ul class="pagination-sm pagination-obl"></ul>
						</div>
						<div class='col-lg-6 col-md-7 col-sm-5 col-xs-12'>
						<form name="formFilter" id="formFilter" action="<@ofbizUrl>category/~category_id=${productCategoryId}</@ofbizUrl>" method="post" style="margin:0px">
							<div class="row">
							<div class='col-lg-6 col-md-6 col-sm-6 col-xs-0'>
							<div class="limiter">
								<div class="select-box2">
									<select name="viewSize" id="viewSizeTMP" onchange="javascript:form.submit();">
										<option value="8" <#if viewSize?int==8>selected="selected"</#if>>8</option>
										<option value="16" <#if viewSize?int==16>selected="selected"</#if>>16</option>
										<option value="20" <#if viewSize?int==20>selected="selected"</#if>>20</option>
										<option value="24" <#if viewSize?int==24>selected="selected"</#if>>24</option>
										<option value="32" <#if viewSize?int==32>selected="selected"</#if>>32</option>
										<option value="40" <#if viewSize?int==40>selected="selected"</#if>>40</option>
									</select>
								</div>
								<label>${uiLabelMap.CommonShow}:</label>
							</div>
							<#if parameters.hoz?has_content && parameters.hoz=="Y">
							<input type="hidden" name="hoz" id="hoz" value="Y"/>
							<#else>
							<input type="hidden" name="hoz" id="hoz" value="N"/>
							</#if>
						</div>
						
						<div class='col-lg-6 col-md-6 col-sm-6 col-xs-12 col-block-mobile hide' id="sortOrder-hidden-mobile">
							<input type="hidden" value="${viewIndex}" name="viewIndex"/>
							<input type="hidden" value="${(parameters.brand)?if_exists}" id="txtBrand" name="brand"/>
					        <div class="sort-by">
						        <div class="select-box2">
						            <select name="sortOrder" id="sortOrderTMP" onchange="javascript:form.submit();">
								        <option value="" <#if sortOrder?has_content && sortOrder.equals("SortKeywordRelevancy")>selected="selected"</#if>>_Mặc định_</option>
								        <option value="SortProductField:productName" <#if sortOrder?has_content && sortOrder.equals("SortProductField:productName")>selected="selected"</#if>>${uiLabelMap.ProductProductName}</option>
								        <option value="SortProductField:totalQuantityOrdered" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalQuantityOrdered")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByOrders}</option>
								        <option value="SortProductField:totalTimesViewed" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalTimesViewed")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByViews}</option>
								        <option value="SortProductField:averageCustomerRating" <#if sortOrder?has_content && sortOrder.equals("SortProductField:averageCustomerRating")>selected="selected"</#if>>${uiLabelMap.ProductCustomerRating}</option>
								        <option value="SortProductPrice:DEFAULT_PRICE" <#if sortOrder?has_content && sortOrder.equals("SortProductPrice:DEFAULT_PRICE")>selected="selected" disabled="disabled"</#if>>${uiLabelMap.ProductDefaultPrice}</option>
							        </select>
								</div>
								<label>${uiLabelMap.CommonSortedBy}:</label>
							</div>
						</div>
						</div>
						</form>
					</div>
					</div>
				</div>
					<#--if productCategoryLinkScreen?has_content && productCategoryLinks?has_content>
					    <div class="productcategorylink-container">
						    <div class="widget-body margin-top-nav-4">
						        <#list productCategoryLinks as productCategoryLink>
						            ${setRequestAttribute("productCategoryLink",productCategoryLink)}
						            ${screens.render(productCategoryLinkScreen)}
						        </#list>
						    </div>
					     </div>
					</#if -->

					<#if productIds?has_content>
						<#if (context.hoz?has_content && context.hoz=="Y") || (parameters.hoz?has_content && parameters.hoz=="Y")>
					        <ul class="products-grid products-grid-special first last odd">
						        <#list productIds as proId>
			                      ${setRequestAttribute("optProductId", proId)}
			                      ${setRequestAttribute("listIndex", proId_index)}
			                      ${screens.render("component://obb/widget/CatalogScreens.xml#productsummarymini")}
						        </#list>
					        </ul>
				        <#else>
						<ul class="products-grid" id="products-list">
				        	<#list productIds as proId>
			                      ${setRequestAttribute("optProductId", proId)}
			                      ${setRequestAttribute("listIndex", proId_index)}
			                      ${screens.render("component://obb/widget/CatalogScreens.xml#productsummarymini")}
					        </#list>
				        </ul>
                        </#if>
					<#else>
					    <div class="row" style="margin-top: 60px;">
							<div class="alert alert-info">${uiLabelMap.ProductNoProductsInThisCategory}</div>
						</div>
					</#if>
					<div class="toolbar-bottom">
					<div class="toolbar">
						<div class="row">
							<div class='col-lg-6 col-md-5 col-sm-7 col-xs-12'>
								<ul class="pagination-sm pagination-obl"></ul>
							</div>
							<div class='col-lg-6 col-md-7 col-sm-5 col-xs-12'>
							<form name="formFilter2" action="<@ofbizUrl>category/~category_id=${productCategoryId}</@ofbizUrl>" method="post" style="margin:0px">
							<div class="row">	
								<div class='col-lg-6 col-md-6 col-sm-6 col-xs-0'>
								<div class="limiter">
						            <div class="select-box2">
						                <select name="viewSize" onchange="javascript:form.submit();">
											<option value="8" <#if viewSize?int==8>selected="selected"</#if>>8</option>
											<option value="16" <#if viewSize?int==16>selected="selected"</#if>>16</option>
											<option value="20" <#if viewSize?int==20>selected="selected"</#if>>20</option>
											<option value="24" <#if viewSize?int==24>selected="selected"</#if>>24</option>
											<option value="32" <#if viewSize?int==32>selected="selected"</#if>>32</option>
											<option value="40" <#if viewSize?int==40>selected="selected"</#if>>40</option>
										</select>
						            </div>
						            <label>${uiLabelMap.CommonShow}:</label>
					            </div>
							</div>
							<div class='col-lg-6 col-md-6 col-sm-6 col-xs-12 col-block-mobile'>
								<input type="hidden" value="${viewIndex}" name="viewIndex"/>
								<input type="hidden" value="${(parameters.brand)?if_exists}" name="brand"/>
									<div class="row"><div class="col-xs-12">
									<a id="displayOptions" data-toggle="collapse" data-target="#displayOptionsContainer" class="right pointer hide"><div>${uiLabelMap.BEDisplayOptions}</div></a>
									</div></div>
									<div id="displayOptionsContainer" class="sort-by">
								        <div class="select-box2">
								            <select name="sortOrder" onchange="javascript:form.submit();">
										        <option value="" <#if sortOrder?has_content && sortOrder.equals("SortKeywordRelevancy")>selected="selected"</#if>>_Mặc định_</option>
										        <option value="SortProductField:productName" <#if sortOrder?has_content && sortOrder.equals("SortProductField:productName")>selected="selected"</#if>>${uiLabelMap.ProductProductName}</option>
										        <option value="SortProductField:totalQuantityOrdered" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalQuantityOrdered")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByOrders}</option>
										        <option value="SortProductField:totalTimesViewed" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalTimesViewed")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByViews}</option>
										        <option value="SortProductField:averageCustomerRating" <#if sortOrder?has_content && sortOrder.equals("SortProductField:averageCustomerRating")>selected="selected"</#if>>${uiLabelMap.ProductCustomerRating}</option>
										        <option value="SortProductPrice:DEFAULT_PRICE" <#if sortOrder?has_content && sortOrder.equals("SortProductPrice:DEFAULT_PRICE")>selected="selected"</#if>>${uiLabelMap.ProductDefaultPrice}</option>
									        </select>
										</div>
										<label>${uiLabelMap.CommonSortedBy}:</label>
									</div>
							</div>
							</div>
							</form>
						</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>