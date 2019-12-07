<#assign showProductCode = Static['org.ofbiz.base.util.UtilProperties'].getPropertyValue('administration.properties', 'report.show.productCode') />
<#assign showProductName = Static['org.ofbiz.base.util.UtilProperties'].getPropertyValue('administration.properties', 'report.show.productName') />
<script type="text/javascript">
if (typeof (OlbiusConfig) == "undefined") {
	var OlbiusConfig = (function() {
		var self = new Object();
		var current = new Date();
		self.report = Object.freeze({
			show: Object.freeze({
				productCode : "${showProductCode}"=="Y"?true:false,
				productName : "${showProductName}"=="Y"?true:false
			}),
			time: Object.freeze({
				current  :  Object.freeze({
							year: current.getFullYear(),
							month: current.getMonth(),
							date: current.getDate()
						})
			}),
			lang: Object.freeze({
				decimalPoint: locale=="vi"?",":".",
				thousandsSep: locale=="vi"?".":","
			})
		})
		return self;
	})();
}
$(document).ready(function() {
	if (typeof (Highcharts) != "undefined") {
		if (Highcharts) {
			Highcharts.setOptions({
			    lang: OlbiusConfig.report.lang
			});
		}
	}
});
</script>