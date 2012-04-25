/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.smscserver.message.impl;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * A Specialized queue implementation to imitate an empty queue if the head of the queue is not ready to run.
 * 
 * @version $Rev$ $Date$
 */
public class IOSessionQueue extends PriorityBlockingQueue<MessagePoller> {

    private static final long serialVersionUID = -689568747117274583L;

    public IOSessionQueue() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public MessagePoller peek() {
        MessagePoller poller = super.peek();

        // if the head of the queue is in the future we have no work to do
        if ((poller == null) || (poller.getNextCheckTime() > System.currentTimeMillis())) {
            return null;
        }

        return poller;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public MessagePoller poll() {
        Runnable runnable = this.peek();

        if (runnable == null) {
            return null;
        }

        return super.poll();
    }
}
