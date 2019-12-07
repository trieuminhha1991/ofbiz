package com.olbius.bi.olap;

import com.olbius.bi.olap.chart.OlapChartInterface;
import com.olbius.bi.olap.chart.ReturnResultChart;
import com.olbius.bi.olap.chart.ReturnResultChartInterface;
import com.olbius.bi.olap.grid.OlapGridInterface;
import com.olbius.bi.olap.grid.OlapResultQueryEx;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.grid.export.OlapExport;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.system.ProcessServices;
import com.olbius.entity.cache.OlbiusCache;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.jdbc.SQLProcessor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builder sử dụng xây dựng truy vấn dữ liệu olap cho jqxGrid hoặc highcharts
 *
 * @author Nguyen Ha
 * @see AbstractOlap
 */
public abstract class OlbiusBuilder extends AbstractOlap {

    private static final OlbiusCache<Map<String, Object>> CACHE = new OlbiusCache<Map<String, Object>>() {
        @Override
        public Map<String, Object> loadCache(Delegator delegator, String key) throws Exception {
            return OlbiusBuilder.builder.execute(false);
        }
    };

    private static volatile OlbiusBuilder builder;

    public static Long getTime() {
        if (builder != null) {
            return builder.timestamp;
        } else {
            return (long) -1;
        }
    }

    private OlapResultInterface olapResult;

    private final List<String> params = new ArrayList<String>();

    protected Delegator delegator;

    private Long timestamp;

    private OlapExport export;

    /**
     * Constructor
     *
     * @param delegator Delegator truy cập dữ liệu
     */
    public OlbiusBuilder(Delegator delegator) {
        if (delegator != null) {
            this.delegator = delegator;
            super.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
        }
    }

    public void setDelegator(Delegator delegator) {
        this.delegator = delegator;
        super.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
    }

    public static void cleanCache(Delegator delegator) {
        CACHE.clean(delegator);
    }

    /**
     * Call addDataField(name, null, null);
     *
     * @see OlbiusBuilder#addDataField(String, String, ReturnResultCallback)
     */
    protected void addDataField(String name) {
        addDataField(name, null, null);
    }

    ;

    /**
     * Call addDataField(name, col, null);
     *
     * @see OlbiusBuilder#addDataField(String, String, ReturnResultCallback)
     */
    protected void addDataField(String name, String col) {
        addDataField(name, col, null);
    }

    /**
     * Khởi tạo dataField theo column được định nghĩa trong sql query và gán tên
     * cho nó
     *
     * @param name     tên được gán
     * @param col      tên column được query
     * @param callBack method xử lý dữ liệu được query
     */
    protected void addDataField(String name, String col, ReturnResultCallback<?> callBack) {
        ((OlapResultQueryEx) getOlapResult().getResultQuery()).addDataField(name, col, callBack);
    }

    /**
     * Khởi tạo dataField theo 1 nhóm các column được định nghĩa trong sql query
     * và gán tên cho nó
     *
     * @param name     tên được gán
     * @param callBack method xử lý nhóm dữ liệu được query
     */
    protected void addDataField(String name, ReturnResultCallback<?> callBack) {
        ((OlapResultQueryEx) getOlapResult().getResultQuery()).addDataField(name, callBack);
    }

    /**
     * Call addSeries(name, null)
     *
     * @see OlbiusBuilder#addSeries(String, ReturnResultCallback)
     */
    protected void addSeries(String name) {
        addSeries(name, null);
    }

    /**
     * Khởi tạo series theo column được định nghĩa trong sql query
     *
     * @param name     tên column được query
     * @param callback method xử lý dữ liệu được query
     */
    protected void addSeries(String name, ReturnResultCallback<?> callback) {
        ((ReturnResultChartInterface) getOlapResult().getResultQuery()).addSeries(name, callback);
    }

    /**
     * Call addXAxis(name, null)
     *
     * @see OlbiusBuilder#addXAxis(String, ReturnResultCallback)
     */
    protected void addXAxis(String name) {
        addXAxis(name, null);
    }

    /**
     * Khởi tạo xAxis theo column được định nghĩa trong sql query
     *
     * @param name     tên column được query
     * @param callback method xử lý dữ liệu được query
     */
    protected void addXAxis(String name, ReturnResultCallback<?> callback) {
        ((ReturnResultChartInterface) getOlapResult().getResultQuery()).addXAxis(name, callback);
    }

    /**
     * Call addYAxis(name, null)
     *
     * @see OlbiusBuilder#addYAxis(String, ReturnResultCallback)
     */
    protected void addYAxis(String name) {
        addYAxis(name, null);
    }

    /**
     * Khởi tạo yAxis theo column được định nghĩa trong sql query
     *
     * @param name     tên column được query
     * @param callback method xử lý dữ liệu được query
     */
    protected void addYAxis(String name, ReturnResultCallback<?> callback) {
        ((ReturnResultChartInterface) getOlapResult().getResultQuery()).addYAxis(name, callback);
    }

    @Override
    public Map<String, Object> execute() {
        try {

            if (getParameter(OlapInterface.SERVICE_TIMESTAMP) != null) {
                timestamp = (long) getParameter(OlapInterface.SERVICE_TIMESTAMP);
            } else {
                Timestamp tmp = ProcessServices.getLastUpdated(delegator, (String) getParameter(OlapInterface.SERVICE));
                if (tmp != null) {
                    timestamp = tmp.getTime();
                }
            }

            if (timestamp == null) {
                return super.execute();
            }

            builder = this;
            return CACHE.get(delegator, this.toString());
        } catch (Exception e) {
            Debug.logError(e, getModule());
            return null;
        }

    }

    public Map<String, Object> execute(boolean cache) {
        if (!cache) {
            return super.execute();
        } else {
            return this.execute();
        }
    }

    @Override
    public Map<String, Object> execute(Map<String, ? extends Object> context) {
        super.putParameter(OlapInterface.SERVICE, context.get("service"));
        super.putParameter(OlapInterface.SERVICE_TIMESTAMP, context.get("serviceTimestamp"));
        return super.execute(context);
    }

    @Override
    public OlapResultInterface getOlapResult() {
        if (super.getOlapResult() == null) {
            return olapResult;
        }
        return super.getOlapResult();
    }

    @Override
    public void prepareResult() {
        prepareResultBuilder();
        if (getOlapResult() instanceof OlapGridInterface) {
            prepareResultGrid();
        }

        if (getOlapResult() instanceof OlapChartInterface) {
            prepareResultChart();
        }
    }

    /**
     * Được gọi trước 2 method prepareResultGrid, prepareResultChart
     */
    public void prepareResultBuilder() {
    }

    /**
     * Cấu hình Series, XAxis, YAxis cho highcharts
     * <p>
     * Sử dụng các method addSeries, addXAxis, addYAxis
     * <p>
     * Được gọi khi olapResultType implements OlapChartInterface
     *
     * @see OlbiusBuilder#setOlapResultType(Class)
     */
    public void prepareResultChart() {
    }

    /**
     * Cấu hình DataField cho jqxGrid
     * <p>
     * Sử dụng các method addDataField
     * <p>
     * Được gọi khi olapResultType implements OlapGridInterface
     *
     * @see OlbiusBuilder#setOlapResultType(Class)
     */
    public void prepareResultGrid() {
    }

    @Override
    public void putParameter(String key, Object value) {
        params.add(key);
        super.putParameter(key, value);
    }

    /**
     * Cấu hình kiểu dữ liệu trả về khi execute()
     *
     * @param type class extends OlapResultInterface
     * @see OlapInterface#execute()
     */
    public void setOlapResultType(Class<? extends OlapResultInterface> type) {
        try {
            OlapResultQueryInterface olapResultQuery = null;

            if (OlapGridInterface.class.isAssignableFrom(type)) {
                olapResultQuery = returnResultGrid();
                if (export != null) {
                    ((ReturnResultGrid) olapResultQuery).setOlapExport(export);
                }
            }

            if (OlapChartInterface.class.isAssignableFrom(type)) {
                olapResultQuery = returnResultChart();
            }

            olapResult = type.getConstructor(OlapInterface.class, OlapResultQueryInterface.class).newInstance(this, olapResultQuery);

        } catch (Exception e) {
            Debug.logError(e, getModule());
        }

    }

    protected OlapResultQueryInterface returnResultGrid() {
        return new ReturnResultGridEx();
    }

    protected OlapResultQueryInterface returnResultChart() {
        return new ReturnResultChart();
    }

    /**
     * Set seriesDefaultName, seriesDefaultName sẽ được sử dụng nếu không có
     * series được thêm vào
     * <p>
     * Mặc định seriesDefaultName là default
     *
     * @param seriesDefaultName tên seriesDefaultName
     */
    protected void setSeriesDefaultName(String seriesDefaultName) {
        ((ReturnResultChartInterface) getOlapResult().getResultQuery()).setSeriesDefaultName(seriesDefaultName);
    }

    @Override
    @Deprecated
    public void SQLProcessor(org.ofbiz.entity.jdbc.SQLProcessor processor) {
        super.SQLProcessor(processor);
    }

    @Override
    public String toString() {

        String s = "";

        s += this.getClass().toString();

        s += " : ";
        s += "timestamp ";
        s += this.timestamp;

        s += " : ";
        s += "from_date ";
        s += this.getFromDate() != null ? this.getFromDate().getTime() : "";

        s += " : ";
        s += "thru_date ";
        s += this.getThruDate() != null ? this.getThruDate().getTime() : "";

        s += " : ";
        s += "is_chart ";
        s += Boolean.toString(this.isChart());

        for (String key : params) {
            s += " : ";
            s += key + " ";
            if (this.getParameter(key) != null) {
                s += this.getParameter(key).toString();
            }
        }

        return s;
    }

    protected OlbiusQuery makeQuery() {
        return OlbiusQuery.make(getSQLProcessor());
    }

    protected long getSqlTime(Timestamp timestamp) {
        return timestamp.getTime() / 1000;
    }

    public OlapExport getExport() {
        return export;
    }

    public void setExport(OlapExport export) {
        this.export = export;
    }
}
