/*
 * Copyright 2022 Ivchenko Anton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivchenko.jsmdl;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Lists;
import com.ivchenko.jsmdl.servicehandler.ServiceHandler;
import com.ivchenko.jsmdl.servicehandler.TikTokServiceHandler;
import com.ivchenko.jsmdl.servicehandler.TwitterServiceHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@Getter @ToString
public class JsmdlConfig {
    // Request factory to generate all http requests inside Jsmdl instance that uses this config
    private HttpRequestFactory requestFactory;
    // List of registered service handlers for Jsmdl instance that uses this config
    private List<ServiceHandler<?>> serviceHandlers;

    private JsmdlConfig() { }

    private JsmdlConfig(
            @NonNull HttpRequestFactory requestFactory,
            @NonNull List<ServiceHandler<?>> serviceHandlers
    ) {
        this.requestFactory = requestFactory;
        this.serviceHandlers = serviceHandlers;
    }

    public boolean addServiceHandler(ServiceHandler<?> serviceHandler) {
        return this.serviceHandlers.add(serviceHandler);
    }

    private boolean removeServiceHandler(ServiceHandler<?> serviceHandler) {
        return this.serviceHandlers.remove(serviceHandler);
    }

    private boolean removeServiceHandlersOfClass(Class<? extends ServiceHandler<?>> serviceHandlerClass) {
        return this.serviceHandlers.removeIf(el -> el.getClass().equals(serviceHandlerClass));
    }

    public static JsmdlConfig buildDefault() {
        JsmdlConfig instance = new JsmdlConfig(
                new NetHttpTransport().createRequestFactory(),
                Lists.newArrayList()
        );
        instance.addServiceHandler(new TwitterServiceHandler(instance.requestFactory));
        instance.addServiceHandler(new TikTokServiceHandler(instance.requestFactory));
        return instance;
    }
}
