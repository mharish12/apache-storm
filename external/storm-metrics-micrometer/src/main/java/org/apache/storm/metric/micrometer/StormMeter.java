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

import io.micrometer.core.instrument.Counter;
import org.apache.storm.metric.ICounter;
import org.apache.storm.metric.IMeter;

public class StormMeter implements IMeter, ICounter {
    private final Counter counter;

    public StormMeter(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void mark() {
        counter.increment();
    }

    @Override
    public void inc() {
        counter.increment();
    }

    @Override
    public void inc(double count) {
        counter.increment(count);
    }

    @Override
    public void dec() {
        //NOOP for now
    }

    @Override
    public void dec(double count) {
        //NOP for now
    }

    @Override
    public double getCount() {
        return counter.count();
    }
}
