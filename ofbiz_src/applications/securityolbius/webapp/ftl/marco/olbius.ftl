
<#include "olbiusPermission.ftl"/>
<#--<#function hasEntityPermission entity permssion>
    <#return Static["com.olbius.security.util.SecurityUtil"].getOlbiusSecurity(security).olbiusEntityPermission(session, permssion, entity)/>
</#function>-->

<#function mapTab label urlScreen>
    <#assign uuid = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
    <#return {"id": uuid, "label" : label, "url": urlScreen}/>
</#function>

<#function mapToString map>
    <#assign tmp = "{"/>
    <#assign flag = false/>
    <#list map?keys as key>
        <#if flag>
            <#assign tmp = tmp + ","/>
            <#assign flag = false/>
        </#if>
        <#if map[key]?is_string>
            <#assign tmp = tmp + "\'" + key + "\':\'" + map[key] +"\'"/>
        <#else>
            <#assign tmp = tmp + "\'" + key + "\':" + map[key]?string/>
        </#if>
        <#assign flag = true/>
    </#list>
    <#assign tmp = tmp + "}"/>
    <#return tmp/>
</#function>

<#function listToString list>
    <#assign tmp = "["/>
    <#assign flag = false/>
    <#list list as elm>
        <#if flag>
            <#assign tmp = tmp + ","/>
            <#assign flag = false/>
        </#if>
        <#if elm?is_hash>
            <#assign tmp = tmp + mapToString(elm)/>
        <#else >
            <#assign tmp = tmp + "\'" + elm + "\'"/>
        </#if>

        <#assign flag = true/>
    </#list>
    <#assign tmp = tmp + "]"/>
    <#return tmp/>
</#function>

<#macro olbiusTab config>

<div class="row-fluid">
    <div class="span12">
        <div class="widget-box transparent" id="recent-box">
            <div class="widget-header" style="border-bottom:none">
                <ul class="nav nav-tabs" id="recent-tab">
                    <#assign active = true/>
                    <#list config as map>
                        <#if active>
                        <li class="active">
                            <#assign active = false/>
                        <#else >
                        <li>
                        </#if>
                        <a data-toggle="tab" href="#${map["id"]}">${map["label"]}</a>
                    </li>
                    </#list>
                </ul>
            </div>
            <div class="widget-body" style="margin-top: -12px !important">
                <div class="widget-main padding-4">
                    <div class="tab-content overflow-visible" style="padding:8px 0">
                        <#assign active = true/>
                        <#list config as map>
                            <#if active>
                            <div id="${map["id"]}" class="tab-pane active">
                                <#assign active = false/>
                            <#else >
                            <div id="${map["id"]}" class="tab-pane">
                            </#if>
                        ${screens.render(map["url"])}
                        </div>
                        </#list>
                    </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</#macro>

<#function mapMenuConfig label icon func>
    <#assign uuid = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
    <#return {"id": uuid, "label": label, "icon": icon, "func" : func}/>
</#function>

<#macro olbiusFunc>

    <script type="text/javascript">
        (function (window) {
            if (!window.olbiusFunc) {

                var tmpFunc = {};

                function olbiusFunction() {
                }

                olbiusFunction.prototype = {

                    put: function (name, func) {
                        if (typeof func == 'function') {
                            tmpFunc[name] = func;
                        }
                    },
                    get: function (name) {
                        return tmpFunc[name];
                    }
                };

                window.olbiusFunc = new olbiusFunction;
            }
            if (!window.convertMap) {
                window.convertMap = function (obj) {
                    var txt = '{';
                    var flag = false;
                    for (var key in obj) {
                        if (flag) {
                            txt += ",\n";
                            flag = false;
                        }
                        txt += key;
                        txt += '=';
                        txt += obj[key];
                        flag = true;
                    }
                    txt += '}';
                    return txt;
                }
            }

            if (!window.createOrUpdateValue) {
                window.createOrUpdateValue = function (entity, obj, gridId) {
                    jQuery.ajax({
                        url: 'createOrUpdateValue',
                        async: false,
                        type: 'POST',
                        data: {
                            entity: entity,
                            value: window.convertMap(obj)
                        },
                        success: function () {
                            if(!gridId){
                                return;
                            }
                            $('#' + gridId).jqxGrid('updatebounddata');
                        }
                    });
                }
            }

            if (!window.removeValue) {
                window.removeValue = function (entity, obj, gridId) {
                    jQuery.ajax({
                        url: 'removeValue',
                        async: false,
                        type: 'POST',
                        data: {
                            entity: entity,
                            value: window.convertMap(obj)
                        },
                        success: function () {
                            if(!gridId){
                                return;
                            }
                            $('#' + gridId).jqxGrid('updatebounddata');
                        }
                    });
                }
            }

            if (!window.thruDateValue) {
                window.thruDateValue = function (entity, obj, gridId) {
                    jQuery.ajax({
                        url: 'thruDateValue',
                        async: false,
                        type: 'POST',
                        data: {
                            entity: entity,
                            value: window.convertMap(obj)
                        },
                        success: function () {
                            if(!gridId){
                                return;
                            }
                            $('#' + gridId).jqxGrid('updatebounddata');
                        }
                    });
                }
            }
        }(window));
    </script>

</#macro>

<#macro olbiusGrid dataField columnlist url menuConfig=[] id="" rightClick="" customcontrol="">

    <#assign df = listToString(dataField)/>
    <#assign cl = listToString(columnlist)/>
    <#assign uuid = id/>
    <#assign contextMenuId="contextMenuId"/>
    <#if uuid == "">
        <#assign uuid = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
    </#if>
    <#if menuConfig??>
        <#assign contextMenuId="menu"+uuid/>
        <div id='menu${uuid}' style="display:none">
            <ul>
                <#assign i = 0/>
                <#list menuConfig as item>
                    <li id='${i}menuItem${uuid}'><i class='fa ${item["icon"]}'></i>${item["label"]}</li>
                    <#assign i = i + 1/>
                </#list>
            </ul>
        </div>
    </#if>

    <@jqGrid id=uuid url=url filterable="true" filtersimplemode="true"  clearfilteringbutton="true"
    dataField=df columnlist=cl mouseRightMenu="true" contextMenuId=contextMenuId jqGridMinimumLibEnable="false"
    customcontrol1=customcontrol/>

    <script type="text/javascript">
        $(function (window) {
            <#if menuConfig??>
                $('#menu${uuid}').jqxMenu({width: '200', autoOpenPopup: false, mode: 'popup'});
                $('#menu${uuid}').css('visibility', 'visible');
                $('#menu${uuid}').on('itemclick', function (event) {
                    var args = event.args;
                    var tmpId = $(args).attr('id');
                    <#assign i = 0/>
                    <#list menuConfig as item>
                        if (tmpId == '${i}menuItem${uuid}') {
                            var func = window.olbiusFunc.get('${item["func"]}');
                            if (func) {
                                var index = $('#' + '${uuid}').jqxGrid('getselectedrowindex');
                                var data = $('#' + '${uuid}').jqxGrid('getrowdata', index);
                                func('${uuid}', 'menu${uuid}', '${i}menuItem${uuid}', data);
                            }
                        }
                        <#assign i = i + 1/>
                    </#list>
                });
            </#if>
            $('#' + '${uuid}').on('rowclick', function (event) {
                var args = event.args;
                var index = args.rowindex;
                var data = $('#' + '${uuid}').jqxGrid('getrowdata', index);
                $('#' + '${uuid}').jqxGrid('selectrow', index);
                if (args.rightclick) {
                    var func = window.olbiusFunc.get('${rightClick}');
                    if (func) {
                        func('${uuid}', 'menu${uuid}', data);
                    }
                }
            });
        }(window));
    </script>
</#macro>

<#macro rowDropDownGrid label url dataField columnlist showDataField width="150", height="25" event="" field="" fieldRep="" value={}>
    <div class="row-fluid">
        <div class="span3" style="text-align: right; line-height: 27px">${label}</div>
        <div class="span9" style="text-align: left">
            <@dropDownGrid url=url dataField=dataField
                columnlist=columnlist showDataField=showDataField width=width height=height event=event field=field fieldRep=fieldRep value=value/>
        </div>
    </div>
</#macro>

<#macro rowDateTimeInput label url dataField columnlist showDataField width="150", height="25" event="">
    <div class="row-fluid">
        <div class="span3" style="text-align: right; line-height: 27px">${label}</div>
        <div class="span9" style="text-align: left">
            <#assign uuid = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
            <div id='${uuid}'></div>
            <script type="text/javascript">
                $(function (window) {
                    var input = $("#"+"${uuid}");
                    input.jqxDateTimeInput({
                        formatString: "dd/MM/yyyy HH:mm:ss",
                        showTimeButton: true,
                        width: '${width}px',
                        height: '${height}px',
                        value: null
                    });
                    <#if event?has_content>
                        input.on('valueChanged', function (event) {
                            window.olbiusFunc.get('${event}')(input, event.args.date);
                        });
                    </#if>
                }(window));
            </script>
        </div>
    </div>
</#macro>

<#macro rowDateTimeInput label width="150", height="25" event="" field="">
    <div class="row-fluid">
        <div class="span3" style="text-align: right; line-height: 27px">${label}</div>
        <div class="span9" style="text-align: left">
            <#assign uuid = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
            <div id='${uuid}'></div>
            <script type="text/javascript">
                $(function (window) {
                    var input = $("#"+"${uuid}");
                    input.jqxDateTimeInput({
                        formatString: "dd/MM/yyyy HH:mm:ss",
                        showTimeButton: true,
                        width: '${width}px',
                        height: '${height}px',
                        value: null
                    });
                    <#if event?has_content && field?has_content>
                        input.on('valueChanged', function (event) {
                            if(event.args.date == null) {
                                return;
                            }
                            window.olbiusFunc.get('${event}')('${field}', event.args.date.getTime().toString(),function() {
                                input.val(null);
                            });
                        });
                    </#if>
                }(window));
            </script>
        </div>
    </div>
</#macro>

<#macro rowInput label width="150", height="25" event="" field="" value="" disabled=false>
    <div class="row-fluid">
        <div class="span3" style="text-align: right; line-height: 27px">${label}</div>
        <div class="span9" style="text-align: left">
            <#assign uuid = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
            <input type='text' id='${uuid}' <#if disabled>readonly</#if> title='${label}'/>
            <script type="text/javascript">
                $(function (window) {
                    var input = $("#"+"${uuid}");
                    input.jqxInput({
                        width: '${width}px',
                        height: '${height}px',
                        theme: 'olbius'
                    });
                    <#if value?has_content>
                        input.val('${StringUtil.wrapString(value)}');
                    </#if>
                    <#if event?has_content && field?has_content>
                        input.on('change', function (event) {
                            var value = input.val();
                            if(value == null || value == '${value!}') {
                                return;
                            }
                            window.olbiusFunc.get('${event}')('${field}', value, function() {
                                input.val('${value!}');
                            });
                        });
                    </#if>
                }(window));
            </script>
        </div>
    </div>
</#macro>

<#macro rowDropDownList label source width="150", height="25" event="" field="">
    <div class="row-fluid">
        <div class="span3" style="text-align: right; line-height: 27px">${label}</div>
        <div class="span9" style="text-align: left">
            <#assign uuid = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
            <div id='${uuid}'></div>
            <script type="text/javascript">
                $(function (window) {
                    var list = $("#"+"${uuid}");
                    list.jqxDropDownList({
                        source : ${listToString(source)},
                        width: '${width}px',
                        height: '${height}px',
                        theme: 'olbius'
                    });
                    <#if event?has_content && field?has_content>
                        list.on('select', function (event) {
                            var args = event.args;
                            if (args) {
                                var item = args.item;
                                var value = item.value;
                                window.olbiusFunc.get('${event}')('${field}', value, function() {
                                    list.jqxDropDownList('clearSelection');
                                });
                            }
                        });
                    </#if>
                }(window));
            </script>
        </div>
    </div>
</#macro>

<#macro dropDownGrid url dataField columnlist showDataField width="150", height="25" event="" field="" fieldRep="" value={}>
    <#assign uuid = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
    <#assign df = listToString(dataField)/>
    <#assign cl = listToString(columnlist)/>
    <div id="dropDown${uuid}">
        <div style="border-color: transparent;" id="dropDownGrid${uuid}">
        </div>
    </div>
    <script type="text/javascript">
        $(function (window) {
            var grid = $("#dropDownGrid${uuid}");
            var dropDown = $("#dropDown${uuid}");
            var source =
            {
                id: 'Id',
                data: {
                    noConditionFind: 'N',
                    conditionsFind: 'N',
                    dictionaryColumns: '',
                    otherCondition: ''
                },
                dataUrl: '${url}',
                beforeprocessing: function (data) {
                    var res = {};
                    res.totalrecords = data.TotalRows;
                    res.records = data.results;
                    if (parseInt(data.TotalRows) <= 0) {
                        grid.jqxGrid({
                            height: "",
                            autoheight: "true"
                        });
                    } else {
                        grid.jqxGrid({
                            height: "500",
                            autoheight: true
                        });
                    }
                    return res;
                },
                sortcolumn: '',
                sortdirection: 'asc',
                pagenum: 0,
                pagesize: 15,
                entityName: '',
                root: 'results',
                datafields: ${df}
            };
            var config = {
                source: source,
                filterable: true,
                editable: false,
                clearfilteringbutton: true,
                showdefaultloadelement:true,
                autoshowloadelement:true,
                pagesizeoptions: ['5', '10', '15', '20', '25', '30', '50', '100'],
                pageable: true,
                autoheight: true,
                columnsresize: true,
                showfilterrow: true
            };

            Grid.initGrid(config, ${df}, ${cl}, null, grid);

            dropDown.jqxDropDownButton({
                width: ${width}, height: ${height}, theme: 'olbius'
            });

            function setContent(row) {
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">';

                <#assign flag = false/>
                <#list showDataField as show>
                    <#if flag>
                        dropDownContent+= '-';
                        <#assign flag = false/>
                    </#if>
                    dropDownContent += row.${show};
                    <#assign flag = true/>
                </#list>

                dropDownContent += '</div>';

                dropDown.jqxDropDownButton('setContent', dropDownContent);
            }

            <#if value?has_content>
                setContent(${mapToString(value)});
            </#if>

            grid.on('rowselect', function (event) {
                var args = event.args;
                var row = grid.jqxGrid('getrowdata', args.rowindex);

                setContent(row);

                dropDown.jqxDropDownButton('close');

                <#if event?has_content && field?has_content>
                    <#assign tmp = field/>
                    <#if fieldRep?has_content>
                        <#assign tmp = fieldRep/>
                    </#if>
                    window.olbiusFunc.get('${event}')('${tmp}', row.${field}, function(){
                        <#if value?has_content>
                            setContent(${mapToString(value)});
                        <#else>
                            dropDown.jqxDropDownButton('setContent', '');
                        </#if>
                        grid.jqxGrid('clearselection');
                        grid.jqxGrid('updatebounddata');
                        grid.jqxGrid('gotopage', 0);
                    });
                </#if>

            });
        }(window));

    </script>
</#macro>

<#macro rowButton labelOk="OK" labelCancel="CANCEL" iconOK="fa-check" iconCancel="fa-times" eventOk="" eventCancel="">
    <div class="row-fluid" style="margin-top: 20px">
        <div class="span3"></div>
        <div class="span9" style="text-align: left">
            <#assign uuid = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
            <button id="buttonOk${uuid}" type="button" class="btn btn-primary form-action-button pull-left">
                <i class="${iconOK}"></i>${labelOk}
            </button>
            <button id="buttonCancel${uuid}" type="button" class="btn btn-danger form-action-button pull-left" style="margin-left:20px;">
                <i class="${iconCancel}"></i>${labelCancel}
            </button>
            <#if eventOk?has_content || eventCancel?has_content>
                <script type="text/javascript">
                    $(function (window) {
                    <#if eventOk?has_content>
                        $('#buttonOk${uuid}').on('click',function(){
                            window.olbiusFunc.get('${eventOk}')($('#buttonOk${uuid}'));
                        });
                    </#if>
                    <#if eventCancel?has_content>
                        $('#buttonCancel${uuid}').on('click',function(){
                            window.olbiusFunc.get('${eventCancel}')($('#buttonCancel${uuid}'));
                        });
                    </#if>
                    }(window));
                </script>
            </#if>

        </div>
    </div>
</#macro>