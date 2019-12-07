import com.olbius.product.catalog.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;

import javolution.util.FastList;

import com.olbius.baseecommerce.backend.ContentUtils;

catalogId = NewCatalogWorker.getCurrentCatalogId(request);
promoCat = NewCatalogWorker.getCatalogPromotionsCategoryId(request, catalogId);
request.setAttribute("productCategoryId", promoCat);

/* NOTE DEJ20070220 woah, this is doing weird stuff like always showing the last viewed category when going to the main page;
 * It appears this was done for to make it go back to the desired category after logging in, but this is NOT the place to do that,
 * and IMO this is an unacceptable side-effect.
 *
 * The whole thing should be re-thought, and should preferably NOT use a custom session variable or try to go through the main page.
 *
 * NOTE: see section commented out in Category.groovy for the other part of this.
 *
 * NOTE JLR 20070221 this should be done using the same method than in add to cart. I will do it like that and remove all this after.
 *
productCategoryId = session.getAttribute("productCategoryId");
if (!productCategoryId) {
    request.setAttribute("productCategoryId", promoCat);
} else {
    request.setAttribute("productCategoryId", productCategoryId);
}
*/
def mainSlide = ContentUtils.mainSlide(delegator, webSiteId, false);
context.mainSlide = mainSlide;

def verticalBanners = ContentUtils.verticalBanners(delegator, webSiteId, false);
context.verticalBanners = verticalBanners;
if(UtilValidate.isNotEmpty(verticalBanners)){
	context.verticalBanner = EntityUtil.getFirst(verticalBanners);
}
def horizontalBanners = ContentUtils.horizontalBanners(delegator, webSiteId, false);
context.horizontalBanners = horizontalBanners;
if(UtilValidate.isNotEmpty(horizontalBanners)){
	context.horizontalBanner = EntityUtil.getFirst(horizontalBanners);
}
def thumbs = [];
for(GenericValue slide : mainSlide){
	orgi = slide.originalImageUrl;
	if(UtilValidate.isNotEmpty(orgi)){
		thumbs.add(orgi);
	}
}
context.thumb = thumbs;
context.partnerBanners = ContentUtils.partnerBanners(delegator, webSiteId, false);

globalContext.productAppeared = FastList.newInstance();