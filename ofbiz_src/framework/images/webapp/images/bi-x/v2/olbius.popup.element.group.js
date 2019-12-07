(function (window, olbius) {

    var groupLabel = {
        dayType: 'DAY',
        weekType: 'WEEK',
        monthType: 'MONTH',
        quarterType: 'QUARTER',
        yearType: 'YEAR',
        otherType: 'OTHER',
        groupType: 'Type',
        dateType: 'Date type',
        fromDate: 'From date',
        thruDate: 'Thru date',
        date: 'Date',
        month: 'Month',
        week: 'Week',
        quarter: 'Quarter',
        year: 'Year'
    };

    olbius.groupDateTimeLable = function (label) {
        groupLabel = $.extend(groupLabel, label);
    };

    olbius.addPopupConfigGroup('dateTime', {
        elements: function (params) {
        	var defaultIndex = 0;
        	var defaultDateTypeIndex = -1;
        	var defaultFromDate = new Date();
        	var defaultThruDate = new Date();
        	if (typeof params == 'object') {
        		if (typeof params.index != 'undefined') defaultIndex = params.index;
        		if (defaultIndex < 0 || defaultIndex > 5) defaultIndex = 0;
        		
        		if (typeof params.dateTypeIndex != 'undefined') defaultDateTypeIndex = params.dateTypeIndex;
        	}
        	var dateTypeDayIndex = 0;
        	var dateTypeWeekIndex = 1;
        	var dateTypeMonthIndex = 2;
        	var dateTypeQuarterIndex = 3;
        	var dateTypeYearIndex = 4;
        	var dateTypeOtherIndex = 0;
        	var isGetFromParam = false;
    		switch (defaultIndex) {
				case 1 : { //dateTypeWeek
					if (defaultDateTypeIndex < 0 || defaultDateTypeIndex > 1) defaultDateTypeIndex = dateTypeWeekIndex;
					else dateTypeWeekIndex = defaultDateTypeIndex;
					break;
				}
				case 2 : { //dateTypeMonth
					if (defaultDateTypeIndex < 0 || defaultDateTypeIndex > 2) defaultDateTypeIndex = dateTypeMonthIndex;
					else dateTypeMonthIndex = defaultDateTypeIndex;
					break;
				}
				case 3 : { //dateTypeQuarter
					if (defaultDateTypeIndex < 0 || defaultDateTypeIndex > 3) defaultDateTypeIndex = dateTypeQuarterIndex;
					else dateTypeQuarterIndex = defaultDateTypeIndex;
					break;
				}
				case 4 : { //dateTypeYear
					if (defaultDateTypeIndex < 0 || defaultDateTypeIndex > 4) defaultDateTypeIndex = dateTypeYearIndex;
					else dateTypeYearIndex = defaultDateTypeIndex;
					break;
				}
				case 5 : { //other
					if (defaultDateTypeIndex < 0 || defaultDateTypeIndex > 4) defaultDateTypeIndex = dateTypeOtherIndex;
					else dateTypeOtherIndex = defaultDateTypeIndex;
					isGetFromParam = true;
					break;
				}
				default: {
					break;
				}
			}
    		if (isGetFromParam) {
    			if (typeof params == 'object') {
					if (typeof params.fromDate != 'undefined') defaultFromDate = params.fromDate;
	        		if (typeof params.thruDate != 'undefined') defaultThruDate = params.thruDate;
				}
    		} else if (defaultDateTypeIndex > 0) {
    			var today = new Date();
    			if (defaultDateTypeIndex == 1) {
    				defaultFromDate = new Date(today.getTime() - (today.getDay() - 1) * 86400000);//WEEK
    			} else if (defaultDateTypeIndex == 2) {
    				defaultFromDate.setDate(1);//MONTH
    			} else if (defaultDateTypeIndex == 3) {
    				defaultFromDate.setMonth(parseInt(today.getMonth() / 3) * 3, 1);//QUARTER
    			} else if (defaultDateTypeIndex == 4) {
    				defaultFromDate.setMonth(0, 1);//YEAR
    			}
    		}
            return [
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'type',
                        label: groupLabel.groupType,
                        source: [{text: groupLabel.dayType, value: 'DAY'}, {
                            text: groupLabel.weekType,
                            value: 'WEEK'
                        }, {text: groupLabel.monthType, value: 'MONTH'}, {
                            text: groupLabel.quarterType,
                            value: 'QUARTER'
                        }, {
                            text: groupLabel.yearType,
                            value: 'YEAR'
                        }, {text: groupLabel.otherType, value: 'OTHER'}],
                        selectedIndex: defaultIndex
                    },
                    event: [
                        {
                            name: 'select',
                            action: function (p, e) {
                                var item = e.element.jqxDropDownList('getSelectedItem');
                                var value = item.value;
                                var date = new Date();
                                var wdate = new Date();
                                switch (value) {
                                    case 'DAY' :
                                    {
                                        p.group(e.group()).element('dateTypeDay').show();
                                        p.group(e.group()).element('dateTypeWeek').hide();
                                        p.group(e.group()).element('dateTypeMonth').hide();
                                        p.group(e.group()).element('dateTypeQuarter').hide();
                                        p.group(e.group()).element('dateTypeYear').hide();
                                        p.group(e.group()).element('dateTypeOther').hide();
                                        p.group(e.group()).element('fromDate').hide();
                                        p.group(e.group()).element('thruDate').hide();
                                        p.group(e.group()).element('fromDateView').show();
                                        p.group(e.group()).element('thruDateView').show();
                                        p.group(e.group()).element('fromDateView').element.jqxDateTimeInput('setDate', date);
                                        p.group(e.group()).element('thruDateView').element.jqxDateTimeInput('setDate', date);
                                        break;
                                    }
                                    case 'WEEK' :
                                    {
                                        wdate = new Date(date.getTime() - (date.getDay() - 1) * 86400000);
                                        p.group(e.group()).element('dateTypeDay').hide();
                                        p.group(e.group()).element('dateTypeWeek').show();
                                        p.group(e.group()).element('dateTypeMonth').hide();
                                        p.group(e.group()).element('dateTypeQuarter').hide();
                                        p.group(e.group()).element('dateTypeYear').hide();
                                        p.group(e.group()).element('dateTypeOther').hide();
                                        p.group(e.group()).element('fromDate').hide();
                                        p.group(e.group()).element('thruDate').hide();
                                        p.group(e.group()).element('fromDateView').show();
                                        p.group(e.group()).element('thruDateView').show();
                                        p.group(e.group()).element('fromDateView').element.jqxDateTimeInput('setDate', wdate);
                                        p.group(e.group()).element('thruDateView').element.jqxDateTimeInput('setDate', date);
                                        break;
                                    }
                                    case 'MONTH' :
                                    {
                                        wdate.setDate(1);
                                        p.group(e.group()).element('dateTypeDay').hide();
                                        p.group(e.group()).element('dateTypeWeek').hide();
                                        p.group(e.group()).element('dateTypeMonth').show();
                                        p.group(e.group()).element('dateTypeQuarter').hide();
                                        p.group(e.group()).element('dateTypeYear').hide();
                                        p.group(e.group()).element('dateTypeOther').hide();
                                        p.group(e.group()).element('fromDate').hide();
                                        p.group(e.group()).element('thruDate').hide();
                                        p.group(e.group()).element('fromDateView').show();
                                        p.group(e.group()).element('thruDateView').show();
                                        p.group(e.group()).element('fromDateView').element.jqxDateTimeInput('setDate', wdate);
                                        p.group(e.group()).element('thruDateView').element.jqxDateTimeInput('setDate', date);
                                        break;
                                    }
                                    case 'QUARTER' :
                                    {
                                        wdate.setMonth(parseInt(date.getMonth() / 3) * 3, 1);
                                        p.group(e.group()).element('dateTypeDay').hide();
                                        p.group(e.group()).element('dateTypeWeek').hide();
                                        p.group(e.group()).element('dateTypeMonth').hide();
                                        p.group(e.group()).element('dateTypeQuarter').show();
                                        p.group(e.group()).element('dateTypeYear').hide();
                                        p.group(e.group()).element('dateTypeOther').hide();
                                        p.group(e.group()).element('fromDate').hide();
                                        p.group(e.group()).element('thruDate').hide();
                                        p.group(e.group()).element('fromDateView').show();
                                        p.group(e.group()).element('thruDateView').show();
                                        p.group(e.group()).element('fromDateView').element.jqxDateTimeInput('setDate', wdate);
                                        p.group(e.group()).element('thruDateView').element.jqxDateTimeInput('setDate', date);
                                        break;
                                    }
                                    case 'YEAR' :
                                    {
                                        wdate.setMonth(0, 1);
                                        p.group(e.group()).element('dateTypeDay').hide();
                                        p.group(e.group()).element('dateTypeWeek').hide();
                                        p.group(e.group()).element('dateTypeMonth').hide();
                                        p.group(e.group()).element('dateTypeQuarter').hide();
                                        p.group(e.group()).element('dateTypeYear').show();
                                        p.group(e.group()).element('dateTypeOther').hide();
                                        p.group(e.group()).element('fromDate').hide();
                                        p.group(e.group()).element('thruDate').hide();
                                        p.group(e.group()).element('fromDateView').show();
                                        p.group(e.group()).element('thruDateView').show();
                                        p.group(e.group()).element('fromDateView').element.jqxDateTimeInput('setDate', wdate);
                                        p.group(e.group()).element('thruDateView').element.jqxDateTimeInput('setDate', date);
                                        break;
                                    }
                                    case 'OTHER' :
                                    {
                                    	p.group(e.group()).element('dateTypeDay').hide();
                                        p.group(e.group()).element('dateTypeWeek').hide();
                                        p.group(e.group()).element('dateTypeMonth').hide();
                                        p.group(e.group()).element('dateTypeQuarter').hide();
                                        p.group(e.group()).element('dateTypeYear').hide();
                                        p.group(e.group()).element('dateTypeOther').show();
                                        p.group(e.group()).element('fromDate').show();
                                        p.group(e.group()).element('thruDate').show();
                                        p.group(e.group()).element('fromDateView').hide();
                                        p.group(e.group()).element('thruDateView').hide();
                                        break;
                                    }
                                    default :
                                    {
                                        break
                                    }
                                }
                            }
                        }
                    ]
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'dateTypeDay',
                        label: groupLabel.dateType,
                        source: [{text: groupLabel.date, value: 'DAY'}],
                        selectedIndex: dateTypeDayIndex
                    },
                    hide: !(defaultIndex == 0)
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'dateTypeWeek',
                        label: groupLabel.dateType,
                        source: [{text: groupLabel.date, value: 'DAY'}, {
                            text: groupLabel.week,
                            value: 'WEEK'
                        }],
                        selectedIndex: dateTypeWeekIndex
                    },
                    hide: !(defaultIndex == 1)
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'dateTypeMonth',
                        label: groupLabel.dateType,
                        source: [{text: groupLabel.date, value: 'DAY'}, {
                            text: groupLabel.week,
                            value: 'WEEK'
                        }, {text: groupLabel.month, value: 'MONTH'}],
                        selectedIndex: dateTypeMonthIndex
                    },
                    hide: !(defaultIndex == 2)
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'dateTypeQuarter',
                        label: groupLabel.dateType,
                        source: [{text: groupLabel.date, value: 'DAY'}, {
                            text: groupLabel.week,
                            value: 'WEEK'
                        }, {text: groupLabel.month, value: 'MONTH'}, {text: groupLabel.quarter, value: 'QUARTER'}],
                        selectedIndex: dateTypeQuarterIndex
                    },
                    hide: !(defaultIndex == 3)
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'dateTypeYear',
                        label: groupLabel.dateType,
                        source: [{text: groupLabel.date, value: 'DAY'}, {
                            text: groupLabel.week,
                            value: 'WEEK'
                        }, {text: groupLabel.month, value: 'MONTH'}, {
                            text: groupLabel.quarter,
                            value: 'QUARTER'
                        }, {text: groupLabel.year, value: 'YEAR'}],
                        selectedIndex: dateTypeYearIndex
                    },
                    hide: !(defaultIndex == 4)
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'dateTypeOther',
                        label: groupLabel.dateType,
                        source: [{text: groupLabel.date, value: 'DAY'}, {
                            text: groupLabel.week,
                            value: 'WEEK'
                        }, {text: groupLabel.month, value: 'MONTH'}, {
                            text: groupLabel.quarter,
                            value: 'QUARTER'
                        }, {text: groupLabel.year, value: 'YEAR'}],
                        selectedIndex: dateTypeOtherIndex
                    },
                    hide: !(defaultIndex == 5)
                },
                {
                    action: 'jqxDateTimeInput',
                    params: {
                        id: 'fromDate',
                        label: groupLabel.fromDate,
                        value: defaultFromDate
                    },
                    hide: !(defaultIndex == 5)
                },
                {
                    action: 'jqxDateTimeInput',
                    params: {
                        id: 'thruDate',
                        label: groupLabel.thruDate,
                        value: defaultThruDate
                    },
                    hide: !(defaultIndex == 5)
                },
                {
                    action: 'jqxDateTimeInput',
                    params: {
                        id: 'fromDateView',
                        label: groupLabel.fromDate,
                        value: defaultFromDate,
                        disabled: true
                    },
                	hide: defaultIndex == 5
                },
                {
                    action: 'jqxDateTimeInput',
                    params: {
                        id: 'thruDateView',
                        label: groupLabel.thruDate,
                        value: defaultThruDate,
                        disabled: true
                    },
                	hide: defaultIndex == 5
                }
            ]
        },
        val: function (id, popup) {
            var type = popup.group(id).val('type');
            switch (type) {
                case 'DAY' :
                {
                    return {
                        fromDate: popup.group(id).val('fromDateView'),
                        thruDate: popup.group(id).val('thruDateView'),
                        dateType: 'DAY'
                    }
                }
                case 'WEEK' :
                {
                    return {
                        fromDate: popup.group(id).val('fromDateView'),
                        thruDate: popup.group(id).val('thruDateView'),
                        dateType: popup.group(id).val('dateTypeWeek')
                    }
                }
                case 'MONTH' :
                {
                    return {
                        fromDate: popup.group(id).val('fromDateView'),
                        thruDate: popup.group(id).val('thruDateView'),
                        dateType: popup.group(id).val('dateTypeMonth')
                    }
                }
                case 'QUARTER' :
                {
                    return {
                        fromDate: popup.group(id).val('fromDateView'),
                        thruDate: popup.group(id).val('thruDateView'),
                        dateType: popup.group(id).val('dateTypeQuarter')
                    }
                }
                case 'YEAR' :
                {
                    return {
                        fromDate: popup.group(id).val('fromDateView'),
                        thruDate: popup.group(id).val('thruDateView'),
                        dateType: popup.group(id).val('dateTypeYear')
                    }
                }
                case 'OTHER' :
                {
                    return {
                        fromDate: popup.group(id).val('fromDate'),
                        thruDate: popup.group(id).val('thruDate'),
                        dateType: popup.group(id).val('dateTypeOther')
                    }
                }
                default:
                {
                    return {};
                }
            }
        }
    });

}(window, OlbiusUtil));