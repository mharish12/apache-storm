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

/**
 * Counter.
 */
public interface ICounter extends IStormMetric {

    /**
     * Increment by one.
     */
    void inc();

    /**
     * Increment by count.
     * @param count - count to be incremented.
     */
    void inc(double count);

    /**
     * Decrement by one.
     */
    void dec();

    /**
     * Decrement by count.
     * @param count - count to be decremented.
     */
    void dec(double count);

    /**
     * Method to get the current count.
     * @return current count.
     */
    double getCount();
}
