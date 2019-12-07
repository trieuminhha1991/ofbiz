var Liability = (function() {
	var getData = function(partyId) {
		$.ajax({
			url : "getLiabilityParty",
			type : "POST",
			data : {
				partyId : partyId
			},
			success : function(res) {
				renderLiability(res);
			}
		});
		$.ajax({
			url : "getLoyaltyPoint",
			type : "POST",
			data : {
				partyId : partyId
			},
			success : function(res) {
				renderLoyaltyPoint(res);
			}
		});
		$.ajax({
			url : "getPartyClassificationGroup",
			type : "POST",
			data : {
				partyId : partyId
			},
			success : function(res) {
				renderPartyClassificationGroup(res);
			}
		});
	};
	var renderLiability = function(res) {
		$("#totalPayable").html(
				!isNaN(res.totalPayable) ? res.totalPayable : "");
		$("#totalReceivable").html(
				!isNaN(res.totalReceivable) ? res.totalReceivable : "");
		$("#totalLiability").html(
				!isNaN(res.totalLiability) ? res.totalLiability : "");
	};
	var renderLoyaltyPoint = function(res) {
		var x = !isNaN(res.loyaltyPoint) ? res.loyaltyPoint : "";
		$("#totalLoyalty").html(x);
	};
	var renderPartyClassificationGroup = function(res) {
		if (res) {
			$("#customerType").html(res.value);
		}
	};
	return {
		getData : getData
	};
})();
