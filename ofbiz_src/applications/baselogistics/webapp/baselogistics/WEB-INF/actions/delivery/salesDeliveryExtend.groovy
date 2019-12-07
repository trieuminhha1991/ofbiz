import org.ofbiz.base.util.UtilProperties;

String allowViewPrice = UtilProperties.getPropertyValue("baselogistics.properties", "deliveryAndExport.pdf.allowViewPrice");

context.allowViewPrice = allowViewPrice;