<script>
if (typeof (AdministrationConfig) == "undefined") {
	var AdministrationConfig = (function() {
		var self = Object.freeze({
			SecurityPermission: Object.freeze({
				array :
				[
					"ADMIN",
					"CREATE",
					"INVOKE",
					"UPDATE",
					"VIEW",
					"APPROVE",
					"NOTE",
					"ACTION_CANCEL",
					"ACTION_APPROVE",
					"ACTION_HOLD",
					"ACTION_QUICKSHIP",
					"ACTION_COMPLETE",
					"DLV_ENTRY_REQ_VIEW",
					"VEHICLE_VIEW",
					"SHIPPER_VIEW",
				]
			})
		});
		return self;
	})();
}
</script>