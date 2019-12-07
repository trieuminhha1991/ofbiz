import com.olbius.baseecommerce.backend.ConfigProductServices;

	List<String> prodCatalogIds = ConfigProductServices.prodCatalogIds(delegator, userLogin, true);
	
	def catalogs = "";
	for (String s : prodCatalogIds) {
		catalogs += " " + s;
	}
	context.catalogs = catalogs;