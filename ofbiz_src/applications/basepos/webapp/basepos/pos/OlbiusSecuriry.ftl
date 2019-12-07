<script>
	if (typeof (POSPermission) == "undefined") {
		var POSPermission = (function() {
			var OlbPermission = Object.freeze({
				POS_ORDER_CTRL_S_CREATE: Object.freeze(${hasOlbEntityPermission("POS_ORDER_CTRL_S", "CREATE")?string}),
				POS_ORDER_CTRL_H_VIEW: Object.freeze(${hasOlbEntityPermission("POS_ORDER_CTRL_H", "VIEW")?string}),
				POS_PROMOTION_CTRL_L_VIEW: Object.freeze(${hasOlbEntityPermission("POS_PROMOTION_CTRL_L", "VIEW")?string}),
				POS_PROMOTION_CTRL_C_CREATE: Object.freeze(${hasOlbEntityPermission("POS_PROMOTION_CTRL_C", "CREATE")?string}),
				POS_PAID_CTRL_I_CREATE: Object.freeze(${hasOlbEntityPermission("POS_PAID_CTRL_I", "CREATE")?string}),
				POS_PAID_CTRL_O_CREATE: Object.freeze(${hasOlbEntityPermission("POS_PAID_CTRL_O", "CREATE")?string}),
				POS_CUSTOMER_F7_CREATE: Object.freeze(${hasOlbEntityPermission("POS_CUSTOMER_F7", "CREATE")?string}),
				POS_RETURN_CTRL_ENTER_CREATE: Object.freeze(${hasOlbEntityPermission("POS_RETURN_CTRL_ENTER", "CREATE")?string}),
				POS_RETURN_CTRL_BACKSPACE_CREATE: Object.freeze(${hasOlbEntityPermission("POS_RETURN_CTRL_BACKSPACE", "CREATE")?string}),
			});
			var has = function(entity, permssion) {
				return OlbPermission[entity + "_" + permssion]?OlbPermission[entity + "_" + permssion]:false;
			};
			return {
				has: has
			};
		})();
	}
</script>