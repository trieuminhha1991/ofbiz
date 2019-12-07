<#assign productId = '${product.productId?if_exists}'/>
<#assign quantityUomId = '${product.quantityUomId?if_exists}'/>
<#assign listSupplier = delegator.findByAnd("PartyRoleDetailAndPartyDetail",Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId","SUPPLIER"),null,false) !>
<style>
	.custom{
		color : #037c07 !important;
	}

</style>
<div class="row-fluid"> 
	<div class="span12" style="font-size:14px !important;">
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid  margin-bottom10">
					<div class="span5 align-right">
						${uiLabelMap.ProductId}
					</div>
					<div class="span7 custom custom">
						${productId?if_exists?default('')}
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5  align-right">
						${uiLabelMap.ProductName}
					</div>
					<div class="span7 custom">
						${product.productName?if_exists?default('')}
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.BrandName}
					</div>
					<div class="span7 custom">
						${product.brandName?if_exists?default('')}
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.ProductLastModifiedBy}
					</div>
					<div class="span7 custom">
						[${product.lastModifiedByUserLogin?if_exists}] ${uiLabelMap.CommonOn?if_exists} ${product.lastModifiedDate?if_exists}
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.CommonDescription}
					</div>
					<div class="span7 custom">
						${product.description?if_exists?default('')}
					</div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class="span5  align-right">
						${uiLabelMap.QuantityUomId}
					</div>
					<div class="span7 custom">
						${quantityUomId?if_exists?default('')}
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5 align-right">
						${uiLabelMap.ProductProductWeight}
					</div>
					<div class="span7 custom">
						${product.productWeight?if_exists?default('')}
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5 align-right">
						${uiLabelMap.WeightUomId}
					</div>
					<div class="span7 custom">
						${product.weightUomId?if_exists?default('')}
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5 align-right">
						${uiLabelMap.CommonCreatedBy}
					</div>
					<div class="span7 custom">
						[${product.createdByUserLogin?if_exists?default('')}] ${uiLabelMap.CommonOn?if_exists?default('')} ${product.createdDate?if_exists?default('')}
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
