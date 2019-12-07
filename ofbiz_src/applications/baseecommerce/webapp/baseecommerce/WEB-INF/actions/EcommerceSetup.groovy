/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import com.olbius.product.catalog.NewCatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.webapp.control.*;
import org.ofbiz.webapp.website.WebSiteWorker;
import com.olbius.product.product.ProductEvents;
import com.olbius.product.category.*;
import javolution.util.FastMap;
import com.olbius.baseecommerce.backend.ContentUtils;
import com.olbius.baseecommerce.resources.VisualThemeUtils;

String productStoreId = ProductEvents.getProductStoreId(request);
productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
//Process theme
themeResources = VisualThemeUtils.getVisualThemeResources(delegator, productStore.visualThemeId, layoutSettings, locale);
context.themeResources = themeResources;

prodCatalog = NewCatalogWorker.getProdCatalog(request);
if (prodCatalog) {
    catalogStyleSheet = prodCatalog.styleSheet;
    if (catalogStyleSheet) globalContext.catalogStyleSheet = catalogStyleSheet;
    catalogHeaderLogo = prodCatalog.headerLogo;
    if (catalogHeaderLogo) globalContext.catalogHeaderLogo = catalogHeaderLogo;
}

globalContext.productStore = productStore;
globalContext.checkLoginUrl = LoginWorker.makeLoginUrl(request, "checkLogin");
globalContext.catalogQuickaddUse = NewCatalogWorker.getCatalogQuickaddUse(request);
cart = session.getAttribute("shoppingCart");
context.shoppingCart = cart;
context.webSiteId = WebSiteWorker.getWebSiteId(request);

uri = request.getScheme() + "://" +
		request.getServerName() +
		("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) +
		request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

context.currentUrl = uri;

device = request.getHeader("User-Agent").toLowerCase();
model = "desktop";
if(device.indexOf("mobile") != -1){
	model = "mobile";
}
context.device = model;

context.mainTitle = ContentUtils.getContent(delegator, "MAIN_TITLE", true);
context.mainDescription = ContentUtils.getContent(delegator, "MAIN_DESCRIPTION", true);
context.mainKeywords = ContentUtils.getContent(delegator, "MAIN_KEYWORDS", true);
context.mainSitename = ContentUtils.getContent(delegator, "MAIN_SITENAME", true);
context.aboutUs = ContentUtils.getContent(delegator, "MAIN_ABOUT_US", true);

context.fbType = ContentUtils.getContent(delegator, "FB_TYPE", true);
context.fbAddress = ContentUtils.getContent(delegator, "FB_STREET_ADDRESS", true);
context.fbLocality = ContentUtils.getContent(delegator, "FB_LOCALITY", true);
context.fbCountry = ContentUtils.getContent(delegator, "FB_COUNTRY_NAME", true);
//
//ctx = FastMap.newInstance();
//system = delegator.findOne("UserLogin", ["userLoginId" : "system"], true);
//ctx.put("userLogin", system);
//dispatcher.runSync("synchronizeListProduct", ctx);
//ctx.put("productId", "OLB120011");
//dispatcher.runSync("synchronizeProduct", ctx);