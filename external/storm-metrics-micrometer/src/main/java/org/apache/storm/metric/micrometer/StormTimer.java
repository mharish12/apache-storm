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

package org.apache.storm.metric.micrometer;

import io.micrometer.core.instrument.Timer;
import org.apache.storm.metric.ITimer;

import java.time.Clock;
import java.time.temporal.ChronoField;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class StormTimer implements ITimer {
    private final Timer timer;
    private final Clock clock;

    public StormTimer(Timer timer) {
        this.timer = timer;
        this.clock = Clock.systemUTC();
    }

    @Override
    public void record(long duration, java.util.concurrent.TimeUnit unit) {
        timer.record(duration, unit);
    }

    @Override
    public long getCount() {
        return timer.count();
    }

    @Override
    public IContext time() {
        return new Context(this.timer, Clock.systemUTC());
    }

    @Override
    public <T> T time(Callable<T> event) throws Exception {
        long startTime = this.clock.millis();

        T result;
        try {
            result = event.call();
        } finally {
            this.update(this.clock.millis() - startTime, TimeUnit.MILLISECONDS);
        }

        return result;
    }

    public static class Context implements IContext, AutoCloseable {
        private final Timer timer;
        private final Clock clock;
        private final long startTime;

        Context(Timer timer, Clock clock) {
            this.timer = timer;
            this.clock = clock;
            this.startTime = clock.instant().getLong(ChronoField.NANO_OF_SECOND);
        }

        @Override
        public long stop() {
            long elapsed = this.clock.instant().getLong(ChronoField.NANO_OF_SECOND) - this.startTime;
            this.timer.record(elapsed, TimeUnit.NANOSECONDS);
            return elapsed;
        }

        public void close() {
            this.stop();
        }
    }
}
