package org.apache.storm.metric.micrometer;


import org.apache.storm.metric.IGauge;

public abstract class DerivativeGauge<F, T> implements IGauge<T> {
    private final IGauge<F> base;

    protected DerivativeGauge(IGauge<F> base) {
        this.base = base;
    }

    public T getValue() {
        return this.transform(this.base.getValue());
    }

    protected abstract T transform(F value);
}
