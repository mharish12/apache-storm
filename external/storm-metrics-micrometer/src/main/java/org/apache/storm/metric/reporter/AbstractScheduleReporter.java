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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.storm.metric.reporter;

import org.apache.storm.metric.micrometer.persister.StormMetricsPersister;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduleReporter implements ScheduleReporter {
    private long interval;
    private TimeUnit timeUnit;
    private ScheduledExecutorService scheduler;
    private StormMetricsPersister stormMetricsPersister;

    @Override
    public void prepare(StormMetricsPersister stormMetricsPersister, Map<String, Object> daemonConf) {
        this.stormMetricsPersister = stormMetricsPersister;
    }

    @Override
    public void setReportingInterval(long interval, TimeUnit timeUnit) {
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    @Override
    public void start() {
        if (interval <= 0 || timeUnit == null) {
            throw new IllegalStateException("Reporting interval and time unit must be set before starting the reporter.");
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::report, 0, interval, timeUnit);
    }

    @Override
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * This method should be implemented by subclasses to define the reporting logic.
     */
    protected abstract void report();
}
