package fr.polytech.jydet.lib;

import org.jfree.data.statistics.HistogramDataset;

public class ScalableXYDataset extends HistogramDataset {
    private int factor;
    public ScalableXYDataset(int factor){
        super();
        this.factor = factor;
    }

    @Override
    public Number getY(int series, int item) {
        return super.getY(series, item).doubleValue() / factor;
    }
}
