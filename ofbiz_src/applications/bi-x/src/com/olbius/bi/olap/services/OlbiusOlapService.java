package com.olbius.bi.olap.services;

import com.olbius.bi.olap.OlapResultInterface;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.bi.olap.grid.export.OlapExport;
import com.olbius.service.OlbiusService;
import org.ofbiz.entity.Delegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import java.util.HashMap;
import java.util.Map;

public abstract class OlbiusOlapService extends OlbiusBuilder implements OlbiusService {

    public static final String GRID = "GRID";
    public static final String COLUMNCHART = "COLUMNCHART";
    public static final String LINECHART = "LINECHART";
    public static final String PIECHART = "PIECHART";

    protected static final Map<String, Class<? extends OlapResultInterface>> RESULT_TYPE = new HashMap<String, Class<? extends OlapResultInterface>>();

    static {
        RESULT_TYPE.put(GRID, OlapGrid.class);
        RESULT_TYPE.put(COLUMNCHART, OlapColumnChart.class);
        RESULT_TYPE.put(LINECHART, OlapLineChart.class);
        RESULT_TYPE.put(PIECHART, OlapPieChart.class);
    }

    public OlbiusOlapService() {
        super(null);
    }

    public OlbiusOlapService(Delegator delegator) {
        super(delegator);
    }

    @Override
    public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) {

        setDelegator(dctx.getDelegator());

        String type = (String) context.get("olapType");
        if (type != null) putParameter("olapType", type);

        Class<? extends OlapResultInterface> olap = getOlapResult(type);

        if (olap == null) {
            olap = RESULT_TYPE.get(type);
        }

        if (context.get("export") != null) {
            setExport((OlapExport) context.get("export"));
        }

        setOlapResultType(olap);

        prepareParameters(dctx, context);

        Map<String, Object> result = execute(context);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    public abstract void prepareParameters(DispatchContext dctx, Map<String, Object> context);

    protected Class<? extends OlapResultInterface> getOlapResult(String olapType) {
        return null;
    }

}
