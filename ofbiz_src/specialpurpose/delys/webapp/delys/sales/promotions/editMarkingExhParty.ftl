<#if productPromoPartyReg?exists>
<style>
	.profile-info-name{
		width: 15%;
	}
	.profile-info-value{
		margin-left: 16%;
	}
</style>			
<div class="widget-box">
	<div class="widget-header widget-header-blue widget-header-flat wi1dget-header-small header-color-blue2">
		<h6 class="lighter">${uiLabelMap.DelysExhibitedMarking}</h6>
	</div>
	
	<div class="widget-body">
		<div class="widget-body-inner">
			<div class="widget-main">
				<div class="row-fluid">
					<div class="span12">
						<div>
							<div id="user-profile-1" class="user-profile row-fluid">
							<!-- <div class="span3 center">
								<div>
									<span class="profile-picture">
										<img id="exhImg" src="${exhImgUrl?if_exists}" width="180px" height="250px">
									</span>
								</div>
							</div> -->
							<div class="span12">
								<div class="profile-user-info profile-user-info-striped">
									<form method="post" action="<@ofbizUrl>updateMarkingExh</@ofbizUrl>">
										<input type="hidden" name="partyId" value="${productPromoPartyReg.partyId}">
										<input type="hidden" name="productPromoId" value="${productPromoPartyReg.productPromoId}">
										
										<div class="profile-info-row">
											<div class="profile-info-name"> ${uiLabelMap.DelysPartyName} </div>
											<div class="profile-info-value">
												<span>
													${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, productPromoPartyReg.partyId, false)}
												</span>
											</div>
										</div>
									
										<div class="profile-info-row">
											<div class="profile-info-name">${uiLabelMap.PromotionNameDelys}</div>
											<div class="profile-info-value">
												${productPromo.promoName?if_exists}&nbsp;
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name">${uiLabelMap.PromotionContentDelys}</div>
											<div class="profile-info-value">
												${productPromo.promoText?if_exists}&nbsp;
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name">${uiLabelMap.CommonFromDate}</div>
											<div class="profile-info-value">
												${productPromo.fromDate?if_exists}&nbsp;
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name">${uiLabelMap.CommonThruDate}</div>
											<div class="profile-info-value">
												${productPromo.thruDate?if_exists}&nbsp;
											</div>
										</div>
										<#if productPromoRules?has_content>
											<div class="profile-info-row">
												<div class="profile-info-name">${uiLabelMap.DelysProductPromoCondition}</div>
												<div class="profile-info-value">
													<#list productPromoRules as rule>
														<#assign productPromoConds = rule.getRelated("ProductPromoCond", null, null, false)>
														<div class="row-fluid">
															<div class="span12"><p>${rule.ruleName}</p></div>
															<div class="span12">
																<#if productPromoConds?has_content>	
																<#list productPromoConds as productPromoCond>
																	<#if (productPromoCond.inputParamEnumId)?exists>
														     			<#assign inputParamEnum = productPromoCond.getRelatedOne("InputParamEnumeration", true)>
														     			<#if inputParamEnum?exists>
														     				${(inputParamEnum.get("description",locale))?if_exists}
														     			<#else>
														     				[${(productPromoCond.inputParamEnumId)?if_exists}]
														     			</#if> 
														     		</#if>
														     		
														     		<#if (productPromoCond.operatorEnumId)?exists>
							        									<#assign operatorEnum = productPromoCond.getRelatedOne("OperatorEnumeration", true)>
							        									<#if operatorEnum?exists>
							        										${(operatorEnum.get("description",locale))?if_exists}
							       										<#else>
							       											[${(productPromoCond.operatorEnumId)?if_exists}]
							     										</#if>
							        								</#if>	
							        								
							        								${(productPromoCond.condValue)?if_exists}
							        								<br/>
							        								<#assign condProductPromoCategories = productPromoCond.getRelated("ProductPromoCategory", null, null, false)>
														     		<#if condProductPromoCategories?has_content>
														     		  <#assign listCondCat = []>
																      <#list condProductPromoCategories as condProductPromoCategory>
																	       <#assign condProductCategory = condProductPromoCategory.getRelatedOne("ProductCategory", true)>
																	       <#assign condApplEnumeration = condProductPromoCategory.getRelatedOne("ApplEnumeration", true)>
																	       <#assign listCondCat = listCondCat + [condProductCategory.productCategoryId] >
																	       <#assign includeSubCategoriesCond = condProductPromoCategory.includeSubCategories?default("N")>
																	        ${(condProductCategory.get("description",locale))?if_exists} [${condProductPromoCategory.productCategoryId}]
														                  <#--- ${(condApplEnumeration.get("description",locale))?default(condProductPromoCategory.productPromoApplEnumId)}-->
														                  - ${uiLabelMap.DelysProductSubCats}? ${condProductPromoCategory.includeSubCategories?default("N")}
														                  <br/>
																      </#list>
																    </#if> 
																    <br/>
																    <#assign condProductPromoProducts = productPromoCond.getRelated("ProductPromoProduct", null, null, false)>
																      <#if condProductPromoProducts?has_content>
																	      <#assign productCondList = []>
																	      <#list condProductPromoProducts as condProductPromoProduct>
																	        <#assign condProduct = condProductPromoProduct.getRelatedOne("Product", true)?if_exists>
																	        <#assign condApplEnumeration = condProductPromoProduct.getRelatedOne("ApplEnumeration", true)>
																	       	<#assign productCondList = productCondList + [condProduct.productId]>
																	        ${(condProduct.internalName)?if_exists} [${condProductPromoProduct.productId}]
								                 							- ${(condApplEnumeration.get("description",locale))?default(condProductPromoProduct.productPromoApplEnumId)}
								                 							<br/>
																	      </#list>
																      </#if>
																      <#if productPromoCond.condExhibited?exists>
																      	${uiLabelMap.DelysExhibitedAt} ${productPromoCond.condExhibited}
																      </#if>
																      <#if productPromoCond.notes?exists>
																      	 (productPromoCond.notes)
																      </#if> 
																</#list>
																<#else>
																	${uiLabelMap.NoConditionApplyForRule}
																</#if>
															</div>
														</div>
													</#list>
												</div>
											</div>
										</#if>
										<div class="profile-info-row">
											<div class="profile-info-name">${uiLabelMap.DelysCurrentPartyMarking}</div>
											<div class="profile-info-value">
												${productPromoPartyReg.promoMarkingStatus?if_exists}
												<#if promoExhContentList?has_content>
													( ${promoExhContentList?size} ${uiLabelMap.DelysImageCaptured} - <a href="${exhImgUrl}" data-rel="colorbox">${uiLabelMap.clickToViewImage}</a> )
													<div style="display: none;">
														<#list promoExhContentList as content>
															<#if (content_index > 0)>
																<#if content.contentId?has_content>
																	<a href="/content/control/stream?contentId=${content.contentId}" data-rel="colorbox"></a>
																</#if>
															</#if>
														</#list>
													</div>
												</#if>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name">${uiLabelMap.DelysPartyMarking}</div>
											<div class="profile-info-value">										
												<#list exhMarkingSTT as status>
													<label style="margin-bottom: 20px">
														<input name="promoMarkingStatus" type="radio" value="${status.statusId}"
															<#if (productPromoPartyReg.promoMarkingStatus?exists && productPromoPartyReg.promoMarkingStatus == status.statusId)>checked="checked"</#if>/>
														<span class="lbl">${status.get("description", locale)?if_exists}</span>
													</label>						
												</#list>
												<button type="submit" class="btn btn-mini btn-info margin-top-nav-10" 
														style="margin-left:20px">
												<i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>
												<a href="<@ofbizUrl>exhibitedMarkingList</@ofbizUrl>" class="btn btn-mini btn-cancel margin-top-nav-10">
													${uiLabelMap.CommonCancel}
												</a>
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
	</div>
</div>		
<script type="text/javascript">
	var colorbox_params = {
		reposition:true,
		scalePhotos:true,
		scrolling:false,
		previous:'<i class="icon-arrow-left"></i>',
		next:'<i class="icon-arrow-right"></i>',
		close:'&times;',
		current:'{current} of {total}',
		maxWidth:'100%',
		maxHeight:'100%',
		photo:true,
		onOpen:function(){
			document.body.style.overflow = 'hidden';
		},
		onClosed:function(){
			document.body.style.overflow = 'auto';
		},
		onComplete:function(){
			$.colorbox.resize();
		}
	};

	$('a[data-rel="colorbox"]').colorbox(colorbox_params);
</script>
</#if>