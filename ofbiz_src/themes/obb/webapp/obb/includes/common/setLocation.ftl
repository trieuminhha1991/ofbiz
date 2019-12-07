<div id="setLocation" class="modal fade" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog set-location">
		<div class="modal-content location-wrapper" >
			<div class="modal-body location-container">
				<div class="row">
					<div class="col-lg-12">
						<h2 class="location-title">${uiLabelMap.BSPleaseSelectYourArea}</h2>
					</div>
				</div>
				
				<div class="map-container">
					<div class="map-area northern">
						<div class='title'>${uiLabelMap.BSNorth}</div>
					</div>
					<div class="map-area southern">
						<div class='title'>${uiLabelMap.BSSouth}</div>
					</div>
				</div>
				
			</div>
		</div>
	</div>
</div>


<script>
	$(document).ready(function() {
		if (!CookieWorker.getByName("productStoreId")) {
//			$("#setLocation").modal('show');
			CookieWorker.setValue("productStoreId", "ECOMMERCE_01", 365);
		}
		$("#iSetLocation").click(function() {
			$("#setLocation").modal('show');
		});
		$(".northern").click(function() {
			$.post( "<@ofbizUrl>setNewLocation</@ofbizUrl>", { productStoreId: "ECOMMERCE_01" });
			CookieWorker.setValue("productStoreId", "ECOMMERCE_01", 365);
			location.reload();
		});
		$(".southern").click(function() {
			$.post( "<@ofbizUrl>setNewLocation</@ofbizUrl>", { productStoreId: "ECOMMERCE_02" });
			CookieWorker.setValue("productStoreId", "ECOMMERCE_02", 365);
			location.reload();
		});
	});
//	CookieWorker.getByName("productStoreId");
//	CookieWorker.setValue("productStoreId", "hoanm", 7)
//	CookieWorker.deleteByName("productStoreId")
	if (typeof (CookieWorker) == "undefined") {
		var CookieWorker = (function() {
			var setValue = function(cname, cvalue, exdays) {
				var d = new Date();
			    d.setTime(d.getTime() + (exdays*24*60*60*1000));
			    var expires = "expires="+d.toUTCString();
			    document.cookie = cname + "=" + cvalue + "; " + expires;
			};
			var getByName = function(cname) {
				var name = cname + "=";
			    var ca = document.cookie.split(';');
			    for(var i=0; i<ca.length; i++) {
			        var c = ca[i];
			        while (c.charAt(0)==' ') c = c.substring(1);
			        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
			    }
			    return "";
			};
			var deleteByName = function(cname) {
				document.cookie = cname + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
			}
			return {
				setValue: setValue,
				getByName: getByName,
				deleteByName: deleteByName
			};
		})();
	}
</script>