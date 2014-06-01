package org.mdpnp.vista.mock;

import java.util.List;
import org.apache.camel.component.dataset.SimpleDataSet;

public class DataSetList<DataRecord> extends SimpleDataSet {

    List<DataRecord> dataList;

    public DataSetList(List<DataRecord> dataList) {
        this.dataList = dataList;
        this.setSize(dataList.size());
    }

    @Override
    protected DataRecord createMessageBody(long messageIndex) {
        if ((messageIndex > Integer.MAX_VALUE) || (messageIndex < 0)) {
            throw new IndexOutOfBoundsException("DataSetList.createMessageBody long argument cast to in for backing List interface");
        }
        int index = new Long(messageIndex).intValue();
        return dataList.get(index);
    }

    public List<DataRecord> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataRecord> dataList) {
        this.dataList = dataList;
    }

}
