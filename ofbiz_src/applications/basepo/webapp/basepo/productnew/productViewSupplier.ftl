
<div id="prodsupplier-tab" class="tab-pane<#if activeTab?exists && activeTab == "prodsupplier-tab"> active</#if>">
	<div class="row-fluid">
		<div class="span12">
			<#assign UpdateSupplier = "false" />
			<#assign showtoolbarSupplier = "false" />
			<#include "productViewSupplierPrimary.ftl"/>
		</div>
	</div>
</div>

<@jqOlbCoreLib hasCore=false hasGrid=true/>
<script type="text/javascript">
	$(function(){
		OlbSupplierProd.init();
	});
	var OlbSupplierProd = (function(){
		var init = function(){
			
		};
		return {
			init: init
		};
	}());
</script>