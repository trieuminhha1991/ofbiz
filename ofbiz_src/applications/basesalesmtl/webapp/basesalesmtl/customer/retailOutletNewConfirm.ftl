<div class="step-pane" id="confirmInfo">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="row-fluid">
			<div class="span4">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSAgentId}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_partyCode"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSAgentName}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_groupName"></span>
					</div>
				</div>
				
				<div class="row-fluid margin-top10">
					<div class="logo-company">
						<img id="wn_desc_logoImage" width="300px" src="/salesmtlresources/logo/LOGO_demo.png"/>
					</div>
				</div>
			</div><!--.span4-->
            <div class="span4">
                <div class="row-fluid">
                    <div class="div-inline-block">
                        <label>${uiLabelMap.BSDistributorId}:</label>
                    </div>
                    <div class="div-inline-block">
                        <span id="wn_desc_distributorId"></span>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="div-inline-block">
                        <label>${uiLabelMap.BSSalesman}:</label>
                    </div>
                    <div class="div-inline-block">
                        <span id="wn_desc_salesmanId"></span>
                    </div>
                </div>
				<#if !parameters.partyId?exists>
				<div class="row-fluid">
                    <div class="div-inline-block">
                        <label>${uiLabelMap.BSRoute}:</label>
                    </div>
                    <div class="div-inline-block">
                        <span id="wn_desc_routeId"></span>
                    </div>
                </div>
                </#if>
                <div class="row-fluid">
                    <div class="div-inline-block">
                        <label>${uiLabelMap.BSVisitFrequency}:</label>
                    </div>
                    <div class="div-inline-block">
                        <span id="wn_desc_visitFrequencyTypeId"></span>
                    </div>
                </div>
            </div><!--.span4-->
			<div class="span4">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.FormFieldTitle_officeSiteName}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_officeSiteName"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.PartyTaxAuthInfos}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_taxAuthInfos"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSCurrencyUomId}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_currencyUomId"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.EmailAddress}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_infoString"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.PhoneNumber}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_contactNumber"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSAddress}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_addressFullName"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSDescription}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_comment"></span>
					</div>
				</div>
			</div>
		</div><!--.row-fluid-->
		<#-- representative info -->
		<div class="row-fluid" style="border-top:1px solid #eee">
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label class="orange" style="text-transform: uppercase;"><b>${uiLabelMap.BERepresentative}</b></label>
					</div>
					<div class="div-inline-block">
						<span></span>
					</div>
				</div>
			</div><!--.span6-->
			<div class="span6">
			</div>
		</div>
		<div class="row-fluid">
			<div class="span4">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.FullName}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_ownFullName"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.DmsPartyBirthDate}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_ownBirthDate"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.DmsPartyGender}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_ownGender"></span>
					</div>
				</div>
			</div><!--.span4-->
			<div class="span4">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.PhoneNumber}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_ownPhoneNumber"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.EmailAddress}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_ownEmailAddress"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSAddress}:</label>
					</div>
					<div class="div-inline-block">
						<span id="wn_desc_ownAddress"></span>
					</div>
				</div>
			</div><!--.span4-->
		</div><!--.row-fluid-->
	</div>
</div>

<script type="text/javascript">
	var OlbOutletNewConfirm = (function(){
		var setValue = function(data){
			$("#wn_desc_partyCode").text(data.partyCode);
			$("#wn_desc_groupName").text(data.groupName);
			$("#wn_desc_logoImage").text(data.logoImage);
			$("#wn_desc_officeSiteName").text(data.officeSiteName);
			$("#wn_desc_taxAuthInfos").text(data.taxAuthInfos);
			$("#wn_desc_currencyUomId").text(data.currencyUomId);
            $.each(visitFrequencyTypes, function (_, value) {
                if (value.visitFrequencyTypeId === data.visitFrequencyTypeId) {
                    $("#wn_desc_visitFrequencyTypeId").text(value.description);
                    return;
                }
            });
			$("#wn_desc_infoString").text(data.infoString);
			$("#wn_desc_contactNumber").text(data.contactNumber);
			$("#wn_desc_addressFullName").text(data.addressFullName);
			$("#wn_desc_comment").text(data.comments);
			
			$("#wn_desc_distributorId").text(data.distributorDesc);
			$("#wn_desc_salesmanId").text(data.salesmanDesc);
			$("#wn_desc_routeId").text(data.routeDesc);
			
			var representativeInfos = data.representativeInfos;
			if (!_.isEmpty(representativeInfos)) {
				var own_isUsePrimaryAddress = representativeInfos.isUsePrimaryAddress;
				if (own_isUsePrimaryAddress) {
					$("#wn_desc_ownAddress").text("<${StringUtil.wrapString(uiLabelMap.BSUsePrimaryAddress)}>");
					$("#wn_desc_ownAddress").css("font-style", "italic");
					$("#wn_desc_ownAddress").css("font-weight", "normal");
				} else {
					$("#wn_desc_ownAddress").text(representativeInfos.addressFullName);
					$("#wn_desc_ownAddress").css("font-style", "normal");
					$("#wn_desc_ownAddress").css("font-weight", "bold");
				}
				
				$("#wn_desc_ownFullName").text(representativeInfos.partyFullName);
				$("#wn_desc_ownGender").text(representativeInfos.genderDesc);
				$("#wn_desc_ownBirthDate").text(representativeInfos.birthDateDesc);
				$("#wn_desc_ownPhoneNumber").text(representativeInfos.contactNumber);
				$("#wn_desc_ownEmailAddress").text(representativeInfos.infoString);
			}
			
			// FileReader support
			var filesLogo = $('#logoImageUrl').prop('files');
			if (!_.isEmpty(filesLogo)) {
				var fileLogo = filesLogo[0];
				if (FileReader && fileLogo) {
			        var fr = new FileReader();
			        fr.onload = function () {
			            document.getElementById("wn_desc_logoImage").src = fr.result;
			        }
			        fr.readAsDataURL(fileLogo);
			    }
			}
		};
		return {
			setValue: setValue,
		};
	}());
</script>
