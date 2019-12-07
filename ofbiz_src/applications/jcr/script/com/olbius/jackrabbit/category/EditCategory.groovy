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

import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*

if (productCategory) {
    context.productCategoryType = productCategory.getRelatedOne("ProductCategoryType", false);
}

primaryParentCategory = null;
primParentCatIdParam = request.getParameter("primaryParentCategoryId");
if (productCategory) {
    primaryParentCategory = productCategory.getRelatedOne("PrimaryParentProductCategory", false);
} else if (primParentCatIdParam) {
    primaryParentCategory = delegator.findOne("ProductCategory", [productCategoryId : primParentCatIdParam], false);
}
context.primaryParentCategory = primaryParentCategory;


// make the image file formats
imageFilenameFormat = UtilProperties.getPropertyValue("catalog", "image.filename.format");
imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("catalog", "image.server.path"), context);
imageUrlPrefix = UtilProperties.getPropertyValue("catalog", "image.url.prefix");
context.imageFilenameFormat = imageFilenameFormat;
context.imageServerPath = imageServerPath;
context.imageUrlPrefix = imageUrlPrefix;

filenameExpander = FlexibleStringExpander.getInstance(imageFilenameFormat);
context.imageNameCategory = imageUrlPrefix + "/" + filenameExpander.expandString([location : "categories", type : "category", id : productCategoryId]);
context.imageNameLinkOne  = imageUrlPrefix + "/" + filenameExpander.expandString([location : "categories", type : "linkOne", id : productCategoryId]);
context.imageNameLinkTwo  = imageUrlPrefix + "/" + filenameExpander.expandString([location : "categories", type : "linkTwo", id : productCategoryId]);

