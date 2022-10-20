package com.ivchenko.jsmdl;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Lists;
import com.ivchenko.jsmdl.servicehandler.ServiceHandler;
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
        return instance;
    }
}
