<style>
	.freeSize {
		  width: 761px;
	}
	.inputSmall {
		width: 50px !important;
	}
	textarea {
	  resize: vertical;
	}
	h2 {
	    text-align: center;
	}
	.center {
		text-align: center;
	}
	body {
			-webkit-user-select: none;
	}
</style>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script>
	$(document).ready(function() {
		var productId = "${productId?if_exists}";
		if (productId != "") {
			$("#Product option[value='"+ productId +"']").attr('selected', 'selected');
			$( "#Product" ).trigger( "change" );
		}
		var expireDateProduct = "${expireDateProduct?if_exists}";
		if (expireDateProduct != "") {
			$("input[name='txtExpireDay']").val(expireDateProduct);
		}
	});
	function saveQualityPublication(jsonObject, url) {
		jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	
	        }
	    }).done(function() {
	    	var productId = $("select[name='Product']").val();
	    	var header = "${StringUtil.wrapString(uiLabelMap.ImportQuantityPublication)} " + "[" + productId + "] ${StringUtil.wrapString(uiLabelMap.ImportExpiring)}";
	    	createNotification(productId, "QA_QUALITY_MANAGER", header);
		});
	}
</script>
<form method="POST" action="CreateProductQuality.pdf">
<table class="table table-striped  table-bordered">
		<tr>
			<td colspan="4">
				<table>
					<tr>
						<td>${uiLabelMap.Number}</td>
						<td colspan="3"><input type="text" name="NumberQP"/></td>
					</tr>
					<tr>
						<td>${uiLabelMap.DVNK}: </td>
						<td colspan="3"><input type="text" placeholder="${uiLabelMap.DVNK}" class="freeSize" name="DVNK"/></td>
					</tr>
					<tr>
						<td>${uiLabelMap.Address}: </td>
						<td colspan="3"><input type="text" placeholder="${uiLabelMap.Address}  ${uiLabelMap.DVNK}" class="freeSize" name="AddressDVNK"/></td>
					</tr>
					<tr>
						<td>${uiLabelMap.Phone}: </td>
						<td><input type="text" name="txtPhone"/></td>
						<td>${uiLabelMap.Fax}: </td>
						<td><input type="text" name="txtFax"/></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr></tr>
		<tr>
			<td>${uiLabelMap.Product}: </td>
			<td>
			<select name="Product" id="Product" size="1">
				<option value="">${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</option>
				<#list listProducts as listProduct>
					<option value="${listProduct.productId?if_exists}">${listProduct.internalName?if_exists}</option>
				</#list>
			</select>
			</td>
			<td>${uiLabelMap.origin}: </td>
			<td><input type="text" placeholder="${uiLabelMap.origin}  ${uiLabelMap.Product}"  name="txtXuatXu" id="txtXuatXu"/></td>
		</tr>
		<tr>
			<td>${uiLabelMap.Manufacturer}: </td>
			<td><input type="text" placeholder="${uiLabelMap.Manufacturer}"  name="txtNhaSanXuat" id="txtNhaSanXuat"/></td>
			<td>${uiLabelMap.Address}: </td>
			<td><input type="text" placeholder="${uiLabelMap.Address}  ${uiLabelMap.Manufacturer}" name="ManufacturerAddress"/></td>
		</tr>
		<tr>
			<td>${uiLabelMap.Exporter}: </td>
			<td><input type="text" placeholder="${uiLabelMap.Exporter}" name="txtNhaXuatKhau" id="txtNhaXuatKhau"/></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td>${uiLabelMap.QCVN}: </td>
			<td colspan="3"><textarea name="QCVN" class="freeSize" rows="2" cols="150"></textarea></td>
		</tr>
		<tr>
			<td>${uiLabelMap.PTDGSPH}: </td>
			<td colspan="3"><textarea name="PTDGSPH" class="freeSize" rows="2" cols="150"></textarea></td>
		</tr>
		<tr>
			<td>${uiLabelMap.CamKet}: </td>
			<td colspan="3"><textarea name="CamKet" class="freeSize" rows="2" cols="150"></textarea></td>
		</tr>
		<tr>
			<td colspan="4"><h2>${uiLabelMap.TechnicalRequirements}</h2></td>
		</tr>
		<tr>
			<td colspan="2"><h3>${uiLabelMap.SensoryNorm}: </h3></td>
			<td colspan="2"><h3>${uiLabelMap.PhysicsNorm}: </h3></td>
		</tr>
		<tr>
			<td colspan="2">
				<table>
					<tr>
						<td>${uiLabelMap.No}</td>
						<td>${uiLabelMap.NormName}</td>
						<td>${uiLabelMap.publishLevel}</td>
					</tr>
					<tr>
						<td>1</td>
						<td>${uiLabelMap.StatusOutside}</td>
						<td><input type="text" name="txtStatusOutside" id="txtStatusOutside"/></td>
					</tr>
					<tr>
						<td>2</td>
						<td>${uiLabelMap.Color}</td>
						<td><input type="text" name="txtColor" id="txtColor"/></td>
					</tr>
					<tr>
						<td>3</td>
						<td>${uiLabelMap.Flavor}</td>
						<td><input type="text" name="txtFlavor" id="txtFlavor"/></td>
					</tr>
					<tr>
						<td>4</td>
						<td>${uiLabelMap.Impurities}</td>
						<td><input type="text" name="txtImpurities" id="txtImpurities"/></td>
					</tr>
				</table>
			</td>
			<td colspan="2">
				<table>
					<tr>
						<td>${uiLabelMap.No}</td>
						<td>${uiLabelMap.NormName}</td>
						<td>${uiLabelMap.Unit}</td>
						<td>${uiLabelMap.publishLevel}</td>
					</tr>
					<tr>
						<td>1</td>
						<td>${uiLabelMap.Calories}</td>
						<td><input class="inputSmall" type="text" name="txtCaloriesUnit" id="txtCaloriesUnit"/></td>
						<td><input type="text" name="txtCalories" id="txtCalories"/></td>
					</tr>
					<tr>
						<td>2</td>
						<td>${uiLabelMap.Protein}</td>
						<td><input class="inputSmall" type="text" name="txtProteinUnit" id="txtProteinUnit"/></td>
						<td><input type="text" name="txtProtein" id="txtProtein"/></td>
					</tr>
					<tr>
						<td>3</td>
						<td>${uiLabelMap.CarbonHydrat}</td>
						<td><input class="inputSmall" type="text" name="txtCarbonHydratUnit" id="txtCarbonHydratUnit"/></td>
						<td><input type="text" name="txtCarbonHydrat" id="txtCarbonHydrat"/></td>
					</tr>
					<tr>
						<td>4</td>
						<td>${uiLabelMap.Fat}</td>
						<td><input class="inputSmall" type="text" name="txtFatUnit" id="txtFatUnit"/></td>
						<td><input type="text" name="txtFat" id="txtFat"/></td>
					</tr>
					<tr>
						<td>4</td>
						<td>${uiLabelMap.Canxi}</td>
						<td><input class="inputSmall" type="text" name="txtCanxiUnit" id="txtCanxiUnit"/></td>
						<td><input type="text" name="txtCanxi" id="txtCanxi"/></td>
					</tr>
					<tr>
						<td>4</td>
						<td>${uiLabelMap.pH}</td>
						<td><input class="inputSmall" type="text" name="txtpHUnit" id="txtpHUnit"/></td>
						<td><input type="text" name="txtpH" id="txtpH"/></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="2"><h3>${uiLabelMap.MicroorganismNorm}: </h3></td>
			<td colspan="2"><h3>${uiLabelMap.HeavyMetals}: </h3></td>
		</tr>
		<tr>
			<td colspan="2">
				<table>
					<tr>
						<td>${uiLabelMap.No}</td>
						<td>${uiLabelMap.NormName}</td>
						<td>${uiLabelMap.Unit}</td>
						<td>${uiLabelMap.maxLevel}</td>
					</tr>
					<tr>
						<td>1</td>
						<td>${uiLabelMap.Lmonocytogenes}</td>
						<td><input class="inputSmall" type="text" name="txtLmonocytogenesUnit" id="txtLmonocytogenesUnit"/></td>
						<td><input type="text" name="txtLmonocytogenes" id="txtLmonocytogenes"/></td>
					</tr>
					<tr>
						<td>2</td>
						<td>${uiLabelMap.Salmonella}</td>
						<td><input class="inputSmall" type="text" name="txtSalmonellaUnit" id="txtSalmonellaUnit"/></td>
						<td><input type="text" name="txtSalmonella" id="txtSalmonella"/></td>
					</tr>
					<tr>
						<td>3</td>
						<td>${uiLabelMap.Staphylococci}</td>
						<td><input class="inputSmall" type="text" name="txtStaphylococciUnit" id="txtStaphylococciUnit"/></td>
						<td><input type="text" name="txtStaphylococci" id="txtStaphylococci"/></td>
					</tr>
					<tr>
						<td>4</td>
						<td>${uiLabelMap.Staphylococcal}</td>
						<td><input class="inputSmall" type="text" name="txtStaphylococcalUnit" id="txtFatUnit"/></td>
						<td><input type="text" name="txtStaphylococcal" id="txtFat"/></td>
					</tr>
				</table>
			</td>
			<td colspan="2">
				<table>
					<tr>
						<td>${uiLabelMap.MetalName}</td>
						<td>${uiLabelMap.Unit}</td>
						<td>${uiLabelMap.maxLevel}</td>
					</tr>
					<tr>
						<td>${uiLabelMap.Lead}</td>
						<td><input class="inputSmall" type="text" name="txtLeadUnit" id="txtLeadUnit"/></td>
						<td><input type="text" name="txtLead" id="txtLead"/></td>
					</tr>
					<tr>
						<td>${uiLabelMap.Antimoxy}</td>
						<td><input class="inputSmall" type="text" name="txtAntimoxyUnit" id="txtAntimoxyUnit"/></td>
						<td><input type="text" name="txtAntimoxy" id="txtAntimoxy"/></td>
					</tr>
					<tr>
						<td>${uiLabelMap.Arsen}</td>
						<td><input class="inputSmall" type="text" name="txtArsenUnit" id="txtArsenUnit"/></td>
						<td><input type="text" name="txtArsen" id="txtArsen"/></td>
					</tr>
					<tr>
						<td>${uiLabelMap.Cadimi}</td>
						<td><input class="inputSmall" type="text" name="txtCadimiUnit" id="txtCadimiUnit"/></td>
						<td><input type="text" name="txtCadimi" id="txtCadimi"/></td>
					</tr>
					<tr>
						<td>${uiLabelMap.Mercury}</td>
						<td><input class="inputSmall" type="text" name="txtMercuryUnit" id="txtCadimiUnit"/></td>
						<td><input type="text" name="txtMercury" id="txtCadimi"/></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="4"><h3>${uiLabelMap.UnwantedNorms}: </h3></td>
		</tr>
		<tr>
			<td colspan="4">
				<table>
					<tr>
						<td>${uiLabelMap.No}</td>
						<td>${uiLabelMap.NormName}</td>
						<td>${uiLabelMap.Unit}</td>
						<td>${uiLabelMap.maxLevel}</td>
					</tr>
					<tr>
						<td>1</td>
						<td>${uiLabelMap.Aflatoxin}</td>
						<td><input class="inputSmall" type="text" name="txtAflatoxinUnit" id="txtAflatoxinUnit"/></td>
						<td><input type="text" name="txtAflatoxin" id="txtAflatoxin"/></td>
					</tr>
					<tr>
						<td>2</td>
						<td>${uiLabelMap.Melamine}</td>
						<td><input class="inputSmall" type="text" name="txtMelamineUnit" id="txtMelamineUnit"/></td>
						<td><input type="text" name="txtMelamine" id="txtMelamine"/></td>
					</tr>
					<tr>
						<td>3</td>
						<td colspan="2"><textarea name="txtUnwantedNorms" class="freeSize" rows="2" cols="150"></textarea></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="4"><h2>${uiLabelMap.Components}: </h2></td>
		</tr>
		<tr>
			<td colspan="4"><textarea name="Components" placeholder="${uiLabelMap.Components}" class="freeSize" rows="2" cols="150"></textarea></td>
		</tr>
		<tr>
			<td colspan="4"><h2>${uiLabelMap.shelfLifeInstructionAndMaintain}: </h2></td>
		</tr>
		<tr>
			<td colspan="4"><h3>${uiLabelMap.shelfLife}: </h3></td>
		</tr>
		<tr>
			<td>${uiLabelMap.expireDay}: </td>
			<td><input type="number" name="txtExpireDay"/></td>
			<td colspan="2">${uiLabelMap.fromProductionDate}.</td>
		</tr>
		<tr>
			<td>${uiLabelMap.dateOfManufacture}: </td>
			<td colspan="3"><input type="text" name="dateOfManufacture"/></td>
		</tr>
		<tr>
			<td>${uiLabelMap.ExpireDate}: </td>
			<td colspan="3"><input type="text" name="ExpireDate"/></td>
		</tr>
		<tr>
			<td colspan="4"><h3>${uiLabelMap.Instruction}: </h3></td>
		</tr>
		<tr>
			<td colspan="4"><textarea name="Instruction" placeholder="${uiLabelMap.Instruction}" class="freeSize" rows="2" cols="150"></textarea></td>
		</tr>
		<tr>
			<td colspan="4"><h3>${uiLabelMap.Maintain}: </h3></td>
		</tr>
		<tr>
			<td colspan="4"><textarea name="Maintain" placeholder="${uiLabelMap.Maintain}" class="freeSize" rows="2" cols="150"></textarea></td>
		</tr>
		<tr>
			<td colspan="4"><h2>${uiLabelMap.PackagingMaterialAndPacking}: </h2></td>
		</tr>
		<tr>
			<td colspan="4">
			<table>
				<tr>
					<td colspan="4"><textarea name="PackagingMaterialAndPacking" placeholder="${uiLabelMap.PackagingMaterial}"  class="freeSize" rows="2" cols="150"></textarea></td>
				</tr>
				<tr>
					<td>${uiLabelMap.Packing}: </td>
					<td><input type="text" name="Packing" id="Packing"/></td>
					<td>${uiLabelMap.NetWeight} :</td>
					<td><input type="text" name="NetWeight" id="NetWeight"/></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td colspan="4"><h2>${uiLabelMap.OriginAndTraderImport}: </h2></td>
		</tr>
		<tr>
			<td>${uiLabelMap.Manufacturer}: </td>
			<td><input type="text" name="Manufacturer" id="Manufacturer"/></td>
			<td>${uiLabelMap.Address}:</td>
			<td><input type="text" name="AddressManufacturer" id="AddressManufacturer"/></td>
		</tr>
		<tr>
			<td>${uiLabelMap.Importer}: </td>
			<td><textarea name="Importer" rows="2" cols="50"></textarea></td>
			<td>${uiLabelMap.Address}: </td>
			<td><textarea name="AddressImporter" rows="2" cols="50"></textarea></td>
		</tr>
		<tr>
			<td colspan="4"><h2>${uiLabelMap.draftLabelOfProduct}: </h2></td>
		</tr>
		<tr>
			<td colspan="4">
				<table>
					<tr>
						<td><h4>${uiLabelMap.ProductName}: </h4></td>
						<td><input type="text" name="ProductName" id="ProductName"/></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.Components}: </h4></td>
						<td><textarea name="Components2" rows="2" cols="50"></textarea></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.NutritiousInfoIn100g}: </h4></td>
						<td><textarea name="NutritiousInfoIn100g" rows="2" cols="50"></textarea></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.dateOfManufacture}: </h4></td>
						<td><input type="text" name="dateOfManufacture2" /></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.ExpireDate}: </h4></td>
						<td><input type="text" name="shelfLife2" id="shelfLife2"/></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.Maintain}: </h4></td>
						<td><input type="text" name="txtMaintain" id="txtMaintain"/></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.Instruction}: </h4></td>
						<td><input type="text" name="txtInstruction" id="txtInstruction"/></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.NetWeight}: </h4></td>
						<td><input type="text" name="txtNetWeight" id="txtNetWeight"/></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.origin}: </h4></td>
						<td><input type="text" name="txtorigin" id="txtorigin"/></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.Exporter}: </h4></td>
						<td><input type="text" name="Exporter" id="Exporter"/></td>
					</tr>
					<tr>
						<td><h4>${uiLabelMap.Importer}: </h4></td>
						<td><textarea name="Importer2" rows="2" cols="50"></textarea></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="4"><h2>${uiLabelMap.PeriodicalSurveyPlan} </h2></td>
		</tr>
		<tr>
			<td colspan="4" >
				<table>
					<tr>
						<td>${uiLabelMap.organizationName}: </td>
						<td><input type="text" class="freeSize" name="organizationName" id="organizationName"/></td>
					</tr>
					<tr>
						<td>${uiLabelMap.Address}: </td>
						<td><textarea name="organizationAddress" class="freeSize" rows="2" cols="150"></textarea></td>
					</tr>
					<tr>
						<td colspan="2"><h3 class="center">${uiLabelMap.PeriodicalSurveyPlan} </h3></td>
					</tr>
					<tr class="center">
						<td>${uiLabelMap.Product}: </td>
						<td><input type="text" name="ProductName" id="ProductName"/></td>
					</tr>
					<tr>
						<td colspan="2">
								<table>
									<tr>
										<td>${uiLabelMap.No}</td>
										<td>${uiLabelMap.TechnologyApplying}</td>
										<td>${uiLabelMap.SurveyTest}</td>
										<td>${uiLabelMap.RePulish}</td>
									</tr>
									<tr>
										<td>01</td>
										<td><input type="text" name="TechnologyApplying" id="TechnologyApplying" /></td>
										<td><input type="text" name="SurveyTest" id="SurveyTest"/></td>
										<td><input type="text" name="RePulish" id="RePulish"/></td>
									</tr>
								</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="2"></td>
			<td colspan="2">
				<table>
					<tr>
						<td colspan="2"></td>
						<td><textarea name="NoiViet" rows="4">${uiLabelMap.NoiViet}</textarea></td>
					</tr>
					<tr>
						<td colspan="2"></td>
						<td><textarea name="ctDelys" rows="4">${uiLabelMap.ctDelys}</textarea></td>
					</tr>
				</table>
			</td>
			</tr>
			
			<tr>
				<td colspan="4"><h2>${uiLabelMap.BBCCHQ}: </h2></td>
			</tr>
			<tr>
				<td></td>
				<td><h3>${uiLabelMap.Product}: </h3></td>
				<td><input type="text" name="ProductName" id="ProductName"/></td>
				<td></td>
			</tr>
			<tr>
				<td colspan="4">
					<table>
					<tr>
						<td>1.  ${uiLabelMap.thtpt}: </td>
						<td><input type="text" name="thtpt"/></td>
					</tr>
					<tr>
						<td>1.  ${uiLabelMap.xdspcdkn}: </td>
						<td><input type="text" name="ProductName"/></td>
					</tr>
					<tr>
						<td>2. ${uiLabelMap.dgsphcsp}: </td>
						<td><textarea name="dgsphcsp" rows="4"></textarea></td>
					</tr>
					<tr>
						<td>3. ${uiLabelMap.xlkqdgsph}: </td>
						<td><textarea name="xlkqdgsph" rows="4"></textarea></td>
					</tr>
					<tr>
						<td>4. ${uiLabelMap.klvsph}: </td>
						<td><textarea name="klvsph" rows="4"></textarea></td>
					</tr>
				</table>
				</td>
			</tr>
	</table>
			<table>
				<tr>
					<td>${uiLabelMap.fromDateOfPubich}:</td>
					<td><div id="fromDate"></div></td>
				</tr>
				<tr>
					<td>${uiLabelMap.thruDateOfPubich}:</td>
					<td><div id="thruDate"></div></td>
				</tr>
			</table>
			<div id="myButton">
				<button class="btn btn-primary btn-small" onclick="btnCreateClick();return false;"><i class='icon-ok'></i>${uiLabelMap.Create}</button>
			</div>
</form>