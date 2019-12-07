<div id="jm-col1" class="col-left sidebar">
	<div class="inner clearfix">
		<div class="block block-account">
		    <div class="block-title">
		        <strong><span>${uiLabelMap.BEAccountManagement}</span></strong>
		    </div>
		    <div class="block-content">
		        <ul>
	                <li<#if selectItem?has_content && selectItem == "dashboard"> class="current"><strong>${uiLabelMap.BEGeneralInformation}</strong><#else>><a href="<@ofbizUrl>dashboard</@ofbizUrl>">${uiLabelMap.BEGeneralInformation}</a></#if></li>
	                <li<#if selectItem?has_content && selectItem == "editperson"> class="current"><strong>${uiLabelMap.BEProfile}</strong><#else>><a href="<@ofbizUrl>editperson</@ofbizUrl>">${uiLabelMap.BEProfile}</a></#if></li>
	                <li<#if selectItem?has_content && selectItem =="messages"> class="current"><strong>${uiLabelMap.BEMessage}</strong><#else>><a href="<@ofbizUrl>messagelist</@ofbizUrl>">${uiLabelMap.BEMessage}</a></#if></li>
                <!--   <li<#if selectItem?has_content && selectItem =="editcontactmechnosave"> class="current"><strong>${uiLabelMap.BEAddressManagement}</strong><#else>><a href="<@ofbizUrl>addressbook</@ofbizUrl>">${uiLabelMap.BEAddressManagement}</a></#if></li>
	                <li><a href="/">Quotes</a></li>
	                <li><a href="#">Requests</a></li>
	                <li><a href="#">Shopping List</a></li>
	                <li><a href="#">My Product Reviews</a></li>
	                <li><a href="#">My Tags</a></li>
	                <li><a href="#">My Wishlist</a></li>
	                <li><a href="#">My Applications</a></li>
	                <li><a href="#">Newsletter Subscriptions</a></li>
	                <li class="last<#if selectItem=="viewcompare"> current</#if>"><a href="">View compare</a></li> -->
                </ul>
		</div>
		</div>
		<div class="block block-list block-compare" style="display:none;">
		    <div class="block-title">
		        <strong><span>Compare Products</span></strong>
		    </div>
		    <div class="block-content">
	            <p class="empty">You have no items to compare.</p>
		</div>
		</div>
	</div>
</div>