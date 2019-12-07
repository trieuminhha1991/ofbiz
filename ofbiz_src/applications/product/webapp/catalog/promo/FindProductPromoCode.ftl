<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#if productPromoId?exists>
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <h3 class="header smaller lighter blue">${uiLabelMap.ProductPromotionUploadSetOfPromotionCodes}</h3>
        </div>
        <div class="screenlet-body">
            <form method="post" action="<@ofbizUrl>createBulkProductPromoCode</@ofbizUrl>" enctype="multipart/form-data">
                <input type="hidden" name="productPromoId" value="${productPromoId}"/>
                <span>${uiLabelMap.ProductPromoUserEntered}:</span>
                    <select name="userEntered">
                        <option value="Y">${uiLabelMap.CommonY}</option>
                        <option value="N">${uiLabelMap.CommonN}</option>
                    </select>
                <span>${uiLabelMap.ProductPromotionReqEmailOrParty}:</span>
                    <select name="requireEmailOrParty">
                        <option value="N">${uiLabelMap.CommonN}</option>
                        <option value="Y">${uiLabelMap.CommonY}</option>
                    </select></br>
                <span>${uiLabelMap.ProductPromotionUseLimits}:&nbsp
                ${uiLabelMap.ProductPromotionPerCode}</span><input type="text" size="5" name="useLimitPerCode" />
                <span>${uiLabelMap.ProductPromotionPerCustomer}</span><input type="text" size="5" name="useLimitPerCustomer" />
                <div>
                	
                  <div style="width:30%;margin-left:12px;">
            		<input type="file" size="40" name="uploadedFile" id="uploadedFile"/>
            		<button class="btn btn-mini btn-primary" type="submit"><i class="icon-upload-alt"></i>${uiLabelMap.CommonUpload}</button>
                  </div>
                </div>
            </form>
        </div>
    </div>
    <script type="text/javascript">
  	function addadditionImages(){$('#uploadedFile').ace_file_input({
				no_file:'No File ...',
				btn_choose:'Choose',
				btn_change:'Change',
				droppable:false,
				onchange:null,
				thumbnail:false //| true | large
				//whitelist:'gif|png|jpg|jpeg'
				//blacklist:'exe|php'
				//onchange:''
				//
			});
			}
  </script>
    <br />
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <h3 class="header smaller lighter blue">${uiLabelMap.ProductPromotionAddSetOfPromotionCodes}</h3>
        </div>
        <div class="screenlet-body">
            <form method="post" action="<@ofbizUrl>createProductPromoCodeSet</@ofbizUrl>">
                <input type="hidden" name="productPromoId" value="${productPromoId}"/>
                <span>${uiLabelMap.CommonQuantity}:</span><input type="text" size="5" name="quantity" />
                <span>${uiLabelMap.ProductPromoCodeLength}:</span><input type="text" size="12" name="codeLength" />
                <span>${uiLabelMap.ProductPromoCodeLayout}:</span>
                    <select name="promoCodeLayout">
                        <option value="smart">${uiLabelMap.ProductPromoLayoutSmart}</option>
                        <option value="normal">${uiLabelMap.ProductPromoLayoutNormal}</option>
                        <option value="sequence">${uiLabelMap.ProductPromoLayoutSeqNum}</option>
                    </select>
                <span class="tooltipob">${uiLabelMap.ProductPromoCodeLayoutTooltip}</span>
                <br />
                <span>${uiLabelMap.ProductPromoUserEntered}:</span>
                    <select name="userEntered">
                        <option value="Y">${uiLabelMap.CommonY}</option>
                        <option value="N">${uiLabelMap.CommonN}</option>
                    </select>
                <span>${uiLabelMap.ProductPromotionReqEmailOrParty}:</span>
                    <select name="requireEmailOrParty">
                        <option value="N">${uiLabelMap.CommonN}</option>
                        <option value="Y">${uiLabelMap.CommonY}</option>
                    </select>
                <span>${uiLabelMap.ProductPromotionUseLimits}:
                ${uiLabelMap.ProductPromotionPerCode}</span><input type="text" size="5" name="useLimitPerCode" />
                <span>${uiLabelMap.ProductPromotionPerCustomer}</span><input type="text" size="5" name="useLimitPerCustomer" />
                <button class="btn btn-mini btn-primary" type="submit"><i class="icon-ok"></i>${uiLabelMap.CommonAdd}</button>
            </form>
        </div>
    </div>
</#if>