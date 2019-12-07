
<i class="ibefore-icon-angle-right"></i><a href="getImportPlans?customTimePeriodId=${customTimePeriodId?if_exists}&productPlanName=${productPlanName?if_exists}" style="cursor: pointer;"> ${productPlanName?if_exists}</a>
<i class="ibefore-icon-angle-right"></i><a style="cursor: pointer;" onclick="productPlanIdClick(${productPlanId?if_exists})"> ${internalPartyId?if_exists}</a>
<i class="ibefore-icon-angle-right"></i><a> ${uiLabelMap.AddNewProduct}</a>

<script type="text/javascript">
function productPlanIdClick(productPlanId) {
				var form = document.createElement("form");
				form.setAttribute('method', "post");
				form.setAttribute('action', "resultPlanOfYear");
				var input = document.createElement("input");
				input.setAttribute('name', "productPlanHeaderId");
				input.setAttribute('value', productPlanId);
				input.setAttribute('type', "hidden");
				form.appendChild(input);
				document.getElementsByTagName('body')[0].appendChild(form);
				form.submit();
			}
</script>