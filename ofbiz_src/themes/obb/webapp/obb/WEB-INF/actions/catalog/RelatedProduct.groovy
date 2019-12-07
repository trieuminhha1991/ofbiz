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

/*
 * NOTE: This script is also referenced by the webpos and ecommerce's screens and
 * should not contain order component's specific code.
 */

import java.util.Map;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

import javolution.util.FastList;
import javolution.util.FastSet;

import com.olbius.product.product.ProductUtils;
import com.olbius.obb.ConfigUtils;

productCategoryId = context.product.primaryProductCategoryId;
productId = context.productId;
contents = ProductUtils.getRelatedProduct(delegator, productCategoryId, productId, 0, ConfigUtils.RELATED_PRODUCT_PAGE_SIZE)

products = (List<Map<String, Object>>) contents.get("products");
context.relatedProducts = products;
