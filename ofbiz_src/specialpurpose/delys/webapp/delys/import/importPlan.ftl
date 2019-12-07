<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<form action="resultPlanOfYear" id="hasPlan" method="post">
<input type="hidden" name="productPlanHeaderId">
</form>
<div id="myAlert" style="display: none">
	<span class="alert alert-info">${uiLabelMap.ProductPlanNotAvailable}!</span><br/><br/>
	<a style="color:#438eb9" href="createNewImportPlanForYear">${uiLabelMap.createImportPlan}</a>
</div>