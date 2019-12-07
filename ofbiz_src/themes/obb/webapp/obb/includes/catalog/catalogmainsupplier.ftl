<#macro paginationControls>
    <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
      <#if (viewIndexMax?int > 0)>
            <ol>
		<#if viewIndex!=0>
	                <li class="previous">
		                <a class=" i-previous" href="javascript:void(0);" onclick="callDocumentByPaginate(~${viewSize}~${viewIndex?int-1}');" title="Previous">
	                        <i class="fa fa-caret-left"></i>
	                    </a>
		            </li>
	            </#if>
                <#list 0..(viewIndexMax-1) as curViewNum>
			<li <#if viewIndex==curViewNum_index>class="current">
				${curViewNum_index+1}
				<#else>
				><a href="javascript:void(0);" onclick="callDocumentByPaginate('${viewSize}~${curViewNum?int}');">${curViewNum + 1}</a>
				</#if>
			</li>
                </#list>
                <#if (viewIndex+1)!=viewIndexMax>
	                <li class="next">
		                <a class=" i-next" href="javascript:void(0);" onclick="callDocumentByPaginate('${viewSize}~${viewIndex?int+1}');" title="Next">
	                        <i class="fa fa-caret-right"></i>
	                    </a>
		            </li>
	            </#if>
            </ol>
    </#if>
</#macro>
<#assign viewIndex = context.viewIndex?if_exists?string/>
<#assign viewSize = context.viewSize?if_exists/>
<#assign viewSize = viewSize?number/>
<script type="text/javascript">
    function callDocumentByPaginate(info) {
	var str = info.split('~');
	var form = document.createElement("form");
	    form.setAttribute("method", "POST");
	    form.setAttribute("action", "<@ofbizUrl>supplierdetail</@ofbizUrl>");

        var hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "VIEW_SIZE");
        hiddenField.setAttribute("value", str[0]);
        form.appendChild(hiddenField);

        hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "VIEW_INDEX");
        hiddenField.setAttribute("value", str[1]);
        form.appendChild(hiddenField);

        hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "hoz");
		hiddenField.setAttribute("value", "Y");
		form.appendChild(hiddenField);

		hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "supId");
		hiddenField.setAttribute("value", ${parameters.supId});
		form.appendChild(hiddenField);

		hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "sortOrder");
		hiddenField.setAttribute("value", document.getElementById("sortOrderTMP").value);
		form.appendChild(hiddenField);

	    document.body.appendChild(form);
	    form.submit();
     }
</script>
<div class=" catalog-category-view categorypath-gifts-beauty-health-lotion-html category-lotion skin-default">
	<div id="jm-main">
		<div class="inner clearfix">
			<div id="jm-current-content" class="clearfix">
				<div class="page-title category-title">
			       <h3>Sản phẩm được cung cấp bởi ${supplier.firstName?if_exists} ${supplier.lastName?if_exists}</h3>
				</div>
				<script type="text/javascript">
					function changehoz(){
						tmpV = document.getElementById("tmphoz").value;
						if(tmpV=="Y"){
							document.getElementById("tmphoz").value = "N";
						}else{
							document.getElementById("tmphoz").value = "Y";
						}
						document.formFilter.submit();
					}
				</script>
				<div class="category-products">
					<div class="toolbar">
					<div class="view-mode" style="width:150px;padding-left:10px;padding-right:10px;">
						<label>${uiLabelMap.ObbViewAs}:</label>
							<#if (parameters.hoz?has_content && parameters.hoz=="N") || (context.hoz?has_content && context.hoz=="N")>
								<span class="ico-outer active"><i class="fa fa-th"></i></span>
								<a href="javascript:void(0);" onclick="changehoz();" title="List" class="list">
									<span class="ico-outer"><i class="fa fa-th-list"></i></span>
								</a>
							<#else>
								<a href="javascript:void(0);" onclick="changehoz()();" title="List" class="list">
									<span class="ico-outer"><i class="fa fa-th"></i></span>
								</a>
								<span class="ico-outer active"><i class="fa fa-th-list"></i></span>
							</#if>
						</div>
						<div class="pages" style="padding-left:10px;padding-right:10px;">
					        <label>${uiLabelMap.CommonPage}:</label>
					        <#if productCategoryMembers?has_content>
							<@paginationControls/>
			                </#if>
					    </div>
					    <form name="formFilter" id="formFilter" action="<@ofbizUrl>supplierdetail</@ofbizUrl>" method="post" style="margin:0px">
					        <input type="hidden" value="${viewIndex}" name="viewIndex"/>
					        <input type="hidden" value="${parameters.supId}" name="supId"/>
					        <div class="sort-by" style="width:190px;padding-left:10px;padding-right:10px;display:none;">
							<label>${uiLabelMap.CommonSortedBy}:</label>
						        <div class="select-box2" style="width:100px;">
						            <select name="sortOrder" onchange="javascript:document.formFilter.submit();">
								        <option value="" <#if sortOrder?has_content && sortOrder.equals("SortKeywordRelevancy")>selected="selected"</#if>>_Mặc định_</option>
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
					                <select name="viewSize" onchange="javascript:document.formFilter.submit();">
										<option value="8" <#if viewSize==8>selected="selected"</#if>>8</option>
										<option value="16" <#if viewSize==16>selected="selected"</#if>>16</option>
										<option value="20" <#if viewSize==20>selected="selected"</#if>>20</option>
										<option value="24" <#if viewSize==24>selected="selected"</#if>>24</option>
										<option value="32" <#if viewSize==32>selected="selected"</#if>>32</option>
										<option value="40" <#if viewSize==40>selected="selected"</#if>>40</option>
									</select>
					            </div>
						</div>
						<#if parameters.hoz?has_content>
							<input type="hidden" name="hoz" id="tmphoz" value="${parameters.hoz}"/>
							<#else>
								<input type="hidden" name="hoz" id="tmphoz" value="${context.hoz}"/>
							</#if>
					</form>
					</div>
					<#if context.listProduct?has_content>
				        <#if (parameters.hoz?has_content && parameters.hoz=="N") || (context.hoz?has_content && context.hoz=="N")>
					        <ul class="products-grid products-grid-special first last odd">
						        <#list context.listProduct as pids>
			                      ${setRequestAttribute("optProductId", pids.productId)}
			                      ${setRequestAttribute("productCategoryMember", pids)}
			                      ${setRequestAttribute("listIndex", pids_index)}
					  ${screens.render("component://obb/widget/CatalogScreens.xml#productsummarymini")}
						        </#list>
					        </ul>
				        <#else>
						<ol class="products-list" id="products-list">
						<#list context.listProduct as pids>
			                      ${setRequestAttribute("optProductId", pids.productId)}
			                      ${setRequestAttribute("productCategoryMember", pids)}
			                      ${setRequestAttribute("listIndex", pids_index)}
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
								<#if (parameters.hoz?has_content && parameters.hoz=="N") || (context.hoz?has_content && context.hoz=="N")>
									<span class="ico-outer active"><i class="fa fa-th"></i></span>
									<a href="javascript:void(0);" onclick="changehoz()();" title="List" class="list">
										<span class="ico-outer"><i class="fa fa-th-list"></i></span>
									</a>
								<#else>
									<a href="javascript:void(0);" onclick="changehoz()();" title="List" class="list">
										<span class="ico-outer"><i class="fa fa-th"></i></span>
									</a>
									<span class="ico-outer active"><i class="fa fa-th-list"></i></span>
								</#if>
							</div>
		                    <div class="pages" style="padding-left:10px;padding-right:10px;">
						        <label>${uiLabelMap.CommonPage}:</label>
						        <#if productCategoryMembers?has_content>
								<@paginationControls/>
							</#if>
						    </div>
					        <form name="formFilter2" action="<@ofbizUrl>supplierdetail</@ofbizUrl>" method="post" style="margin:0px">
						        <input type="hidden" value="${viewIndex}" name="viewIndex"/>
							<input type="hidden" value="${parameters.supId}" name="supId"/>
						        <div class="sort-by" style="width:190px;padding-left:10px;padding-right:10px;display:none;">
								<label>${uiLabelMap.CommonSortedBy}:</label>
							        <div class="select-box2" style="width:100px;">
							            <select name="sortOrder" onchange="javascript:document.formFilter2.submit();">
									        <option value="" <#if sortOrder?has_content && sortOrder.equals("SortKeywordRelevancy")>selected="selected"</#if>>_Mặc định_</option>
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
						                <select name="viewSize" onchange="javascript:document.formFilter2.submit();">
											<option value="8" <#if viewSize?int==8>selected="selected"</#if>>8</option>
											<option value="16" <#if viewSize?int==16>selected="selected"</#if>>16</option>
											<option value="20" <#if viewSize?int==20>selected="selected"</#if>>20</option>
											<option value="24" <#if viewSize?int==24>selected="selected"</#if>>24</option>
											<option value="32" <#if viewSize?int==32>selected="selected"</#if>>32</option>
											<option value="40" <#if viewSize?int==40>selected="selected"</#if>>40</option>
										</select>
						            </div>
							</div>
							<#if parameters.hoz?has_content>
								<input type="hidden" name="hoz" id="tmphoz" value="${parameters.hoz}"/>
								<#else>
									<input type="hidden" name="hoz" id="tmphoz" value="${context.hoz}"/>
								</#if>
						</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>