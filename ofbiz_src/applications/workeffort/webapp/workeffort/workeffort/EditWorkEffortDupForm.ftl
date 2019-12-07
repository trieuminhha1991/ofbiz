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
        <form action="<@ofbizUrl>DuplicateWorkEffort</@ofbizUrl>" method="post">
            <input type="hidden" name="oldWorkEffortId" value="${workEffortId?if_exists}"/>
            <div>
                <span class="label1">${uiLabelMap.ProductDuplicateRemoveSelectedWithNewId}</span>
                <input type="text" size="20" maxlength="20" name="workEffortId" class="margin-top8"/>&nbsp;
                <button type="submit" class="btn btn-primary btn-small" name="searchButton"><i class="icon-copy"></i>${uiLabelMap.CommonDuplicate}!</button>
            </div>
            <div>
                <span class="label1">${uiLabelMap.CommonDuplicate}</span>
                <label class="margin-left10" style="margin-top: 10px;">
					<input type="checkbox" name="duplicateWorkEffortAssignmentRates" value="Y" checked="checked"/><span class="lbl">&nbsp;${uiLabelMap.FormFieldTitle_rate}</span>
				</label>
                <label class="margin-left10">
					<input type="checkbox" name="duplicateWorkEffortAssocs" value="Y" checked="checked"/><span class="lbl">&nbsp;${uiLabelMap.WorkEffortAssoc}</span>
				</label>
                <label class="margin-left10">
					<input type="checkbox" name="duplicateWorkEffortContents" value="Y" checked="checked"/><span class="lbl">&nbsp;${uiLabelMap.ProductContent}</span>
				</label>
                <label class="margin-left10">
					<input type="checkbox" name="duplicateWorkEffortNotes" value="Y" checked="checked"/><span class="lbl">&nbsp;${uiLabelMap.WorkEffortNotes}</span>
				</label>
            </div>
            <div>
                <span class="label1">${uiLabelMap.CommonRemove}</span>
                <label class="margin-left10" style="margin-top: 10px;">
					<input type="checkbox" name="removeWorkEffortAssignmentRates" value="Y"/><span class="lbl">&nbsp;${uiLabelMap.FormFieldTitle_rate}</span>
				</label>
                <label class="margin-left10">
					<input type="checkbox" name="removeWorkEffortAssocs" value="Y"/><span class="lbl">&nbsp;${uiLabelMap.WorkEffortAssoc}</span>
				</label>
                <label class="margin-left10">
					<input type="checkbox" name="removeWorkEffortContents" value="Y"/><span class="lbl">&nbsp;${uiLabelMap.ProductContent}</span>
				</label>
                <label class="margin-left10">
					<input type="checkbox" name="removeWorkEffortNotes" value="Y"/><span class="lbl">&nbsp;${uiLabelMap.WorkEffortNotes}</span>
				</label>
            </div>
        </form>