<#assign currentCatalogId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request)/>
<div class="box">
	<div class="box-heading">
		${uiLabelMap.BigshopFilter}
	</div>

<#assign searchCategoryId = request.getAttribute("productCategoryId")?if_exists>
<#assign searchCategoryId2 = parameters.get("cat")?if_exists>
<#if !searchCategoryId?has_content>
	<#assign searchCategoryId = searchCategoryId2>
</#if>
<script type="text/javascript">
	var strarr = window.location.search.substring(1);
	var resultarr;
	var catid = "";
	var imin = -1;
	var imax = -2;
	var ipmax = -1;
	var ipmin = -1;
	var iFlagPrice = 0;
	String.prototype.replaceAt=function(index, character) {
	      return this.substr(0, index) + character + this.substr(index+character.length);
	   }
	function updateCheckedStatus(){
		if(strarr){
			//alert('test');
			resultarr = strarr.split("&");
		}else{
			resultarr = null;
		}
		if(resultarr){
			var atmp = resultarr[0].split("=");
			catid = atmp[1];
			var iindex = 0;
			for(var i = 1; i < resultarr.length;i++){
				atmp = resultarr[i].split("=");
				if(atmp[0].lastIndexOf("option") != -1){
					iindex  = atmp[1].lastIndexOf(":");
					if(iindex != -1){
						atmp[1] = atmp[1].substr(0, iindex) + "\:" + atmp[1].substr(iindex + 1,atmp[1].length);
					}
					if(document.getElementById(atmp[1])){
						document.getElementById(atmp[1]).selected = true;
					}
					continue;
				}
				if(atmp[0].lastIndexOf("minp") != -1){
					imin = parseInt(atmp[1]);
					iFlagPrice++;
					continue;
				}
				if(atmp[0].lastIndexOf("maxp") != -1){
					imax = parseInt(atmp[1]);
					iFlagPrice++;
					continue;
				}
				if(atmp[0].lastIndexOf("SORT") != -1){
					if(atmp[1] == "Y"){
						//$('input[value="Y"][name="sortAscending"]').checked = true;
						document.advtokeywordsearchform.sortAscending[0].checked = true;
					}else{
						//$('input[value="N"][name="sortAscending"]').checked = true;
						document.advtokeywordsearchform.sortAscending[1].checked = true;
					}
				}
				iindex = atmp[0].lastIndexOf("_");
				atmp[0] = atmp[0].substr(0, iindex) + "#" + atmp[0].substr(iindex + 1,atmp[0].length);
				if(document.getElementById(atmp[0])){
					document.getElementById(atmp[0]).checked = true;
				}
			}
		}else{
			catid = "${searchCategoryId}";
		}
		//alert(imin + '-' + imax);
		initSlider2();
		initslider();
		//alert(catid);
	}
	function processBeforeSubmit(elementid){
		if(!resultarr){
			document.advtokeywordsearchform.action = "<@ofbizUrl>keywordsearchcategory?cat=" + catid + "&"+ elementid + "=checked2";
			document.advtokeywordsearchform.action += addOption() + addSort();
			document.advtokeywordsearchform.action += "</@ofbizUrl>";
			document.advtokeywordsearchform.submit();
			return;
		}
		var bl = true;
		document.advtokeywordsearchform.action = "<@ofbizUrl>keywordsearchcategory?cat="+ catid;
		for (var i=1;i<resultarr.length;i++){
			var atmp = resultarr[i].split("=");
			if(atmp[0] == elementid){
				bl = false;
			}else{
				if(atmp[0] == "option"){
					continue;
				}
				if(atmp[0] == "SORT"){
					continue;
				}
				document.advtokeywordsearchform.action += "&" + resultarr[i];
			}
		}
		if(bl){
			document.advtokeywordsearchform.action += "&" + elementid + "=checked2";
		}else{
		}
		document.advtokeywordsearchform.action += addOption() + addSort();
		document.advtokeywordsearchform.action += "</@ofbizUrl>";
		document.advtokeywordsearchform.submit();
	}
	function submitPriceRange(){
		document.advtokeywordsearchform.action = "<@ofbizUrl>keywordsearchcategory?cat="+ catid;
		if(!resultarr){
			document.advtokeywordsearchform.action += "&minp=" + imin + "&maxp=" + imax;
		}else{
			for (var i=1;i<resultarr.length;i++){
				var atmp = resultarr[i].split("=");
				if(atmp[0] === "minp" || atmp[0] === "maxp"){
					// do nothing
				}else{
					document.advtokeywordsearchform.action += "&" + resultarr[i];
				}
			}
			document.advtokeywordsearchform.action += "&minp=" + imin + "&maxp=" + imax;
		}
		document.advtokeywordsearchform.action += "</@ofbizUrl>";
		//alert(imin + "" + imax);
		document.advtokeywordsearchform.submit();
	}
	function addSort(){
		return ( "&SORT=" + $('input[name="sortAscending"]:checked').val());
	}
	function addOption(){
		var e = document.getElementById("sortOrder");
		var strOption = e.options[e.selectedIndex].value;
		if(strOption){
			return ("&option=" + strOption);
		}else{
			return "";
		}
	}
	window.onload = updateCheckedStatus;
</script>

<form name="advtokeywordsearchform" method="post" action="<@ofbizUrl>keywordsearchcategory?cat=${searchCategoryId}</@ofbizUrl>">
  <input type="hidden" name="VIEW_SIZE" value="10"/>
  <input type="hidden" name="PAGING" value="Y"/>
    <#if searchCategory?has_content>
        <input type="hidden" name="SEARCH_CATEGORY_ID" value="${searchCategoryId?if_exists}"/>
        <input type="hidden" name="SEARCH_SUB_CATEGORIES" value="Y"/>
        <input type="hidden" name="SEARCH_OPERATOR" value="OR"/>
    </#if>
    <#list productFeatureTypeIdsOrdered as productFeatureTypeId>
      <#assign findPftMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureTypeId", productFeatureTypeId)>
      <#assign productFeatureType = delegator.findOne("ProductFeatureType", findPftMap, true)>
      <#assign productFeatures = productFeaturesByTypeMap[productFeatureTypeId]>
         <#if productFeatureTypeId == "PRICE">
	<#else>
	 <div class="filterleft">${(productFeatureType.get("description",locale))?if_exists}</div>
	        <#list productFeatures as productFeature>
	          <#--  <select name="pft_${productFeatureTypeId}_#_">
	              <option value="">- ${uiLabelMap.CommonSelectAny} -</option>

	              <option value="${productFeature.productFeatureId}">${productFeature.description?default(productFeature.productFeatureId)}</option>

	            </select> -->
	            <label><input style="margin-left:10px;" type="checkbox" onclick="processBeforeSubmit('pft_${productFeatureTypeId}__${productFeature_index}');" name="pft_${productFeatureTypeId}_#${productFeature_index}" id="pft_${productFeatureTypeId}_#${productFeature_index}" value="${productFeature.productFeatureId}">
			${productFeature.description?default(productFeature.productFeatureId)}
	            </label>
	            <br /></#list>
       </#if>
    </#list>
    <#list productFeatureTypeIdsOrdered as productFeatureTypeId>
      <#assign findPftMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureTypeId", productFeatureTypeId)>
      <#assign productFeatureType = delegator.findOne("ProductFeatureType", findPftMap, true)>
      <#assign productFeatures = productFeaturesByTypeMap[productFeatureTypeId]>

         <#if productFeatureTypeId == "PRICE">
		<div class="filterleft">${(productFeatureType.get("description",locale))?if_exists}</div>
		  <input type="hidden" class="sliderValue" data-index="0" value="10" name="MINPRICE"/>
			  <input type="hidden" class="sliderValue" data-index="1" value="90" name="MAXPRICE"/>
			  <div name="lbforprice" class="marginbottom5">
				${uiLabelMap.BigshopPriceRange}:
			  </div>
		  <div id="slider" class="marginleft15 marginright30"></div>
		  <script type="text/javascript">
			function initSlider2(){
				  <#list productFeatures as productFeature>
					<#if productFeature_index == 0>
						if(imin < 0 || iFlagPrice < 2){
							imin = ${productFeature.description?default(productFeature.productFeatureId)};
							}
							ipmin = ${productFeature.description?default(productFeature.productFeatureId)};
							$('[name="MINPRICE"]').val(imin);
					<#else>
						if(imax < 0 || iFlagPrice < 2){
							imax = ${productFeature.description?default(productFeature.productFeatureId)};
						}
						ipmax = ${productFeature.description?default(productFeature.productFeatureId)};
						$('[name="MAXPRICE"]').val(imax);
					</#if>
				  </#list>
				  $('[name="lbforprice"]').html("${uiLabelMap.BigshopPriceRange}: " + imin + "USD - " + imax + "USD");
			  }
		  </script>
	<#else>
       </#if>
    </#list>
      <br /><div class="filterleft">${uiLabelMap.ProductSortedBy}</div>
          <select name="sortOrder" id="sortOrder" class="selectBox" onchange="processBeforeSubmit('option');">
            <option id="SortKeywordRelevancy" value="SortKeywordRelevancy">${uiLabelMap.ProductKeywordRelevancy}</option>
            <option id="SortProductField:productName" value="SortProductField:productName">${uiLabelMap.ProductProductName}</option>
            <option id="SortProductField:totalQuantityOrdered" value="SortProductField:totalQuantityOrdered">${uiLabelMap.ProductPopularityByOrders}</option>
            <option id="SortProductField:totalTimesViewed" value="SortProductField:totalTimesViewed">${uiLabelMap.ProductPopularityByViews}</option>
            <option id="SortProductField:averageCustomerRating" value="SortProductField:averageCustomerRating">${uiLabelMap.ProductCustomerRating}</option>
            <option id="SortProductPrice:LIST_PRICE" value="SortProductPrice:LIST_PRICE">${uiLabelMap.ProductListPrice}</option>
            <option id="SortProductPrice:DEFAULT_PRICE" value="SortProductPrice:DEFAULT_PRICE">${uiLabelMap.ProductDefaultPrice}</option>
            <#if productFeatureTypes?exists && productFeatureTypes?has_content>
              <#list productFeatureTypes as productFeatureType>
                <option id="SortProductFeature:${productFeatureType.productFeatureTypeId}" value="SortProductFeature:${productFeatureType.productFeatureTypeId}">${productFeatureType.description?default(productFeatureType.productFeatureTypeId)}</option>
              </#list>
            </#if>
          </select>
          <br /><div class="margintop5"></div>
          <label>${uiLabelMap.EcommerceLowToHigh} <input type="radio" name="sortAscending" value="Y" checked="checked" onchange="processBeforeSubmit('SORT');"/></label>
          <label>${uiLabelMap.EcommerceHighToLow} <input type="radio" name="sortAscending" value="N" onchange="processBeforeSubmit('SORT');"/></label>

</form>
</div>
<script type="text/javascript">
function initslider(){
	$("#slider").slider({
	range:true,
    min: ipmin,
    minRange:10,
    max: ipmax,
    step: 1,
    values: [imin, imax],
    slide: function(event, ui) {
        for (var i = 0; i < ui.values.length; ++i) {
            $("input.sliderValue[data-index=" + i + "]").val(ui.values[i]);
        }
        if(ui.values[1] - ui.values[0] < 6){
			return false;
		}
		imin = ui.values[0];
		imax = ui.values[1];
		$('[name="MINPRICE"]').val(imin);
		$('[name="MAXPRICE"]').val(imax);
		$('[name="lbforprice"]').html("${uiLabelMap.BigshopPriceRange}: " + imin + "USD - " + imax + "USD");
		submitPriceRange();
    }
});

$("input.sliderValue").change(function() {
    var $this = $(this);
    $("#slider").slider("values", $this.data("index"), $this.val());
});
}
</script>