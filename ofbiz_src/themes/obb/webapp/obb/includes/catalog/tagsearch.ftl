<#macro paginationControls>
    <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
      <#if (viewIndexMax?int > 0)>
            <ol>
		<#if viewIndex!=0>
	                <li class="previous">
		                <a class=" i-previous" href="<@ofbizUrl>tagsearch?SEARCH_STRING=${parameters.SEARCH_STRING}&keywordTypeId=${parameters.keywordTypeId}&statusId=${parameters.statusId}&hoz=${parameters.hoz}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex?int-11}&clearSearch=N&sortOrder=${sortOrder?if_exists}</@ofbizUrl>" title="Previous">
	                        <i class="fa fa-caret-left"></i>
	                    </a>
		            </li>
	            </#if>
                <#list 0..(viewIndexMax-1) as curViewNum>
			<li <#if viewIndex==curViewNum_index>class="current">
				${curViewNum_index+1}
				<#else>
				<a href="<@ofbizUrl>tagsearch?SEARCH_STRING=${parameters.SEARCH_STRING}&keywordTypeId=${parameters.keywordTypeId}&statusId=${parameters.statusId}&hoz=${parameters.hoz}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${curViewNum?int}&clearSearch=N&sortOrder=${sortOrder?if_exists}</@ofbizUrl>">${curViewNum + 1}</a>
				</#if>
			</li>
                </#list>
                <#if (viewIndex+1)!= viewIndexMax>
	                <li class="next">
		                <a class=" i-next" href="<@ofbizUrl>tagsearch?SEARCH_STRING=${parameters.SEARCH_STRING}&keywordTypeId=${parameters.keywordTypeId}&statusId=${parameters.statusId}&hoz=${parameters.hoz}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex?int+1}&clearSearch=N&sortOrder=${sortOrder?if_exists}</@ofbizUrl>" title="Next">
	                        <i class="fa fa-caret-right"></i>
	                    </a>
		            </li>
	            </#if>
            </ol>
    </#if>
</#macro>
<div class=" catalog-category-view categorypath-gifts-beauty-health-lotion-html category-lotion skin-default">
	<div id="jm-main">
		<div class="inner clearfix">
			<div id="jm-current-content" class="clearfix">
				<div class="page-title category-title">
			        <h3>${uiLabelMap.ProductProductTaggedWith} "${parameters.SEARCH_STRING}"</h3>
			        <#if !productIds?has_content>
					  <h2>&nbsp;${uiLabelMap.ProductNoResultsFound}.</h2>
					</#if>
					<div class="category-products">
						<div class="toolbar">
						<div class="view-mode" style="width:150px;padding-left:10px;padding-right:10px;">
						<label>${uiLabelMap.ObbViewAs}:</label>
						<#if parameters.hoz?has_content && parameters.hoz=="Y">
									<span class="ico-outer active"><i class="fa fa-th"></i></span>
									<a href="<@ofbizUrl>tagsearch?SEARCH_STRING=${parameters.SEARCH_STRING}&keywordTypeId=${parameters.keywordTypeId}&statusId=${parameters.statusId}&hoz=N&sortOrder=${sortOrder?if_exists}</@ofbizUrl>" title="List" class="list">
										<span class="ico-outer"><i class="fa fa-th-list"></i></span>
									</a>
								<#else>
									<a href="<@ofbizUrl>tagsearch?SEARCH_STRING=${parameters.SEARCH_STRING}&keywordTypeId=${parameters.keywordTypeId}&statusId=${parameters.statusId}&hoz=Y&sortOrder=${sortOrder?if_exists}</@ofbizUrl>" title="List" class="list">
										<span class="ico-outer"><i class="fa fa-th"></i></span>
									</a>
									<span class="ico-outer active"><i class="fa fa-th-list"></i></span>
								</#if>
							</div>
							<div class="pages" style="padding-left:10px;padding-right:10px;">
						        <label>${uiLabelMap.CommonPage}:</label>
						        <#if productIds?has_content>
								<@paginationControls/>
				                </#if>
						    </div>
						    <form name="formFilter" id="formFilter" action="<@ofbizUrl>tagsearch</@ofbizUrl>" method="post" style="margin:0px">
						        <input type="hidden" value="${viewIndex}" name="viewIndex"/>
						        <input type="hidden" value="N" name="clearSearch"/>
						        <input type="hidden" value="${hoz}" name="hoz"/>
						        <input type="hidden" value="${parameters.SEARCH_STRING}" name="SEARCH_STRING"/>
						        <input type="hidden" value="${parameters.keywordTypeId}" name="keywordTypeId"/>
						        <input type="hidden" value="${parameters.statusId}" name="statusId"/>
							<div class="sort-by" style="width:190px;padding-left:10px;padding-right:10px;">
								<label>${uiLabelMap.CommonSortedBy}:</label>
							        <div class="select-box2" style="width:100px;">
							            <select name="sortOrder" onchange="javascript:form.submit();">
									        <option value="" <#if sortOrder?has_content && sortOrder.equals("SortKeywordRelevancy")>selected="selected"</#if>>${uiLabelMap.ObbDefault}</option>
									        <option value="SortProductField:productName" <#if sortOrder?has_content && sortOrder.equals("SortProductField:productName")>selected="selected"</#if>>${uiLabelMap.ProductProductName}</option>
									        <option value="SortProductField:totalQuantityOrdered" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalQuantityOrdered")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByOrders}</option>
									        <option value="SortProductField:totalTimesViewed" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalTimesViewed")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByViews}</option>
									        <option value="SortProductField:averageCustomerRating" <#if sortOrder?has_content && sortOrder.equals("SortProductField:averageCustomerRating")>selected="selected"</#if>>${uiLabelMap.ProductCustomerRating}</option>
									        <option value="SortProductPrice:DEFAULT_PRICE" <#if sortOrder?has_content && sortOrder.equals("SortProductPrice:DEFAULT_PRICE")>selected="selected"</#if>>${uiLabelMap.ProductDefaultPrice}</option>
								        </select>
								</div>
							</div>
					        <div class="limiter" style="width:120px;padding-left:10px;padding-right:10px;">
					            <label>${uiLabelMap.CommonShow}:</label>
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
							</div>
						</form>
						</div>

					<#if productIds?has_content>
						<#if parameters.hoz?has_content && parameters.hoz=="Y">
					        <ul class="products-grid products-grid-special first last odd">
						        <#list productIds as proId>
			                      ${setRequestAttribute("optProductId", proId)}
			                      ${setRequestAttribute("listIndex", proId_index)}
					  ${screens.render("component://obb/widget/CatalogScreens.xml#productsummarymini")}
						        </#list>
					        </ul>
				        <#else>
						<ol class="products-list" id="products-list">
					<#list productIds as proId>
			                      ${setRequestAttribute("optProductId", proId)}
			                      ${setRequestAttribute("listIndex", proId_index)}
					  ${screens.render("component://obb/widget/CatalogScreens.xml#productsummarymini2")}
						        </#list>
					</ol>
                        </#if>
					<#else>
					    <div style="width:722px;">
						<p class="alert alert-info">${uiLabelMap.ProductNoProductsInThisCategory}</p>
					</div>
					</#if>
					<div class="toolbar-bottom">
					<div class="toolbar">
						<div class="view-mode" style="width:150px;padding-left:10px;padding-right:10px;">
						<label>${uiLabelMap.ObbViewAs}:</label>
						<#if parameters.hoz?has_content && parameters.hoz=="Y">
									<span class="ico-outer active"><i class="fa fa-th"></i></span>
									<a href="<@ofbizUrl>tagsearch?SEARCH_STRING=${parameters.SEARCH_STRING}&keywordTypeId=${parameters.keywordTypeId}&statusId=${parameters.statusId}&hoz=N&sortOrder=${sortOrder?if_exists}</@ofbizUrl>" title="List" class="list">
										<span class="ico-outer"><i class="fa fa-th-list"></i></span>
									</a>
								<#else>
									<a href="<@ofbizUrl>tagsearch?SEARCH_STRING=${parameters.SEARCH_STRING}&keywordTypeId=${parameters.keywordTypeId}&statusId=${parameters.statusId}&hoz=Y&sortOrder=${sortOrder?if_exists}</@ofbizUrl>" title="List" class="list">
										<span class="ico-outer"><i class="fa fa-th"></i></span>
									</a>
									<span class="ico-outer active"><i class="fa fa-th-list"></i></span>
								</#if>
							</div>
		                    <div class="pages" style="padding-left:10px;padding-right:10px;">
						        <label>${uiLabelMap.CommonPage}:</label>
						        <#if productIds?has_content>
								<@paginationControls/>
							</#if>
						    </div>
					        <form name="formFilter2" action="<@ofbizUrl>tagsearch</@ofbizUrl>" method="post" style="margin:0px">
						        <input type="hidden" value="${viewIndex}" name="viewIndex"/>
						        <input type="hidden" value="N" name="clearSearch"/>
						        <input type="hidden" value="${hoz}" name="hoz"/>
						        <input type="hidden" value="${parameters.SEARCH_STRING}" name="SEARCH_STRING"/>
						        <input type="hidden" value="${parameters.keywordTypeId}" name="keywordTypeId"/>
						        <input type="hidden" value="${parameters.statusId}" name="statusId"/>
						        <div class="sort-by" style="width:190px;padding-left:10px;padding-right:10px;">
								<label>${uiLabelMap.CommonSortedBy}:</label>
							        <div class="select-box2" style="width:100px;">
							            <select name="sortOrder" onchange="javascript:form.submit();">
									        <option value="" <#if sortOrder?has_content && sortOrder.equals("SortKeywordRelevancy")>selected="selected"</#if>>${uiLabelMap.ObbDefault}</option>
									        <option value="SortProductField:productName" <#if sortOrder?has_content && sortOrder.equals("SortProductField:productName")>selected="selected"</#if>>${uiLabelMap.ProductProductName}</option>
									        <option value="SortProductField:totalQuantityOrdered" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalQuantityOrdered")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByOrders}</option>
									        <option value="SortProductField:totalTimesViewed" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalTimesViewed")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByViews}</option>
									        <option value="SortProductField:averageCustomerRating" <#if sortOrder?has_content && sortOrder.equals("SortProductField:averageCustomerRating")>selected="selected"</#if>>${uiLabelMap.ProductCustomerRating}</option>
									        <option value="SortProductPrice:DEFAULT_PRICE" <#if sortOrder?has_content && sortOrder.equals("SortProductPrice:DEFAULT_PRICE")>selected="selected"</#if>>${uiLabelMap.ProductDefaultPrice}</option>
								        </select>
								</div>
							</div>
						        <div class="limiter" style="width:120px;padding-left:10px;padding-right:10px;">
						            <label>${uiLabelMap.CommonShow}:</label>
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
							</div>
						</form>
						</div>
				</div>
				</div>
			</div>
		</div>
	</div>
</div>