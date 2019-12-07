import org.ofbiz.base.util.UtilValidate;
import com.olbius.baseecommerce.backend.ContentUtils;
import org.ofbiz.entity.util.EntityUtil;

def verticalBanners = ContentUtils.verticalBanners(delegator, "DPC", false);
if (UtilValidate.isNotEmpty(verticalBanners)) {
	def verticalBanner = EntityUtil.getFirst(verticalBanners);
	context.verticalBanner = verticalBanner;
}
def horizontalBanners = ContentUtils.horizontalBanners(delegator, "DPC", false);
if (UtilValidate.isNotEmpty(horizontalBanners)) {
	def horizontalBanner = EntityUtil.getFirst(horizontalBanners);
	context.horizontalBanner = horizontalBanner;
}