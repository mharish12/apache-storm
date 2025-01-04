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

package org.apache.storm.metric;


import java.util.concurrent.Callable;

/**
 * Storm timer.
 */
public interface ITimer extends IStormMetric {

    /**
     * @param duration duration to be recorded.
     * @param unit     time unit.
     */
    default void update(long duration, java.util.concurrent.TimeUnit unit) {
        this.record(duration, unit);
    }

    /**
     * Updates the statistics kept by the timer with the specified amount.
     *
     * @param duration Duration of a single event being measured by this timer.
     *                 If the amount is less than 0 the value will be dropped.
     * @param unit     Time unit for the amount being recorded.
     */
    void record(long duration, java.util.concurrent.TimeUnit unit);

    /**
     * @return The number of times that stop has been called on this timer.
     */
    long getCount();

    /**
     * @return Context for time,
     */
    IContext time();

    /**
     * @param event event to be called after record.
     * @param <T>   generic type.
     * @return result of callable event.
     * @throws Exception if any exception from the event.
     */
    <T> T time(Callable<T> event) throws Exception;

    /**
     * Timer Context.
     */
    interface IContext extends AutoCloseable {
        /**
         * @return Elapsed time.
         */
        long stop();
    }

}
