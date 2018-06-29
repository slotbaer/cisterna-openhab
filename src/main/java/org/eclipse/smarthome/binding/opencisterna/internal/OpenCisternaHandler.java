/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.binding.opencisterna.internal;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.binding.opencisterna.internal.model.CisternState;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

/**
 * The {@link OpenCisternaHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Sven Trieflinger
 */
@NonNullByDefault
public class OpenCisternaHandler extends BaseThingHandler {

    static {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public <T> T readValue(@Nullable String value, @Nullable Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(@Nullable Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private final Logger logger = LoggerFactory.getLogger(OpenCisternaHandler.class);

    @Nullable
    private OpenCisternaConfiguration config;

    private ScheduledExecutorService ses = new ScheduledThreadPoolExecutor(1);

    @Nullable
    private ScheduledFuture<?> handle;

    public OpenCisternaHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            update();
        } else {
            logger.debug("The Open Cisterna binding is a read-only binding and cannot handle command {}", command);
        }
    }

    private void updateChannel(String channelId, CisternState cisternState) {
        if (isLinked(channelId)) {
            State state = null;
            switch (channelId) {
                case OpenCisternaBindingConstants.FLUID_LEVEL: {
                    state = new DecimalType(cisternState.getLevel());
                    break;
                }
                case OpenCisternaBindingConstants.FLUID_QUANTITY: {
                    state = new DecimalType(cisternState.getQuantity());
                    break;
                }
                default: {
                    logger.error("Unhandled channel encountered: {}", channelId);
                }
            }
            if (state != null) {
                logger.debug("Updating channel {} with state {}", channelId, state.toString());
                updateState(channelId, state);
            }
        }
    }

    private void updateStatusIfRequired(ThingStatus status) {
        if (getThing().getStatus() != status) {
            updateStatus(status);
        }
    }

    private void update() {
        try {
            URI endpoint = new URIBuilder(config.baseUrl).build().resolve("/cistern/state");
            logger.debug("Fetching cistern state from sensor at: {}", endpoint);
            HttpResponse<CisternState> response = Unirest.get(endpoint.toString()).header("accept", "application/json")
                    .asObject(CisternState.class);
            CisternState cs = response.getBody();
            for (Channel channel : getThing().getChannels()) {
                updateChannel(channel.getUID().getId(), cs);
            }
            updateStatusIfRequired(ThingStatus.ONLINE);
        } catch (Exception e) {
            logger.error("Fetching cistern state failed", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    @Override
    public void initialize() {
        logger.debug("Initializing handler");
        config = getConfigAs(OpenCisternaConfiguration.class);
        logger.info("Config is {}", config);

        if (StringUtils.trimToNull(config.baseUrl) == null) {
            String status = "Parameter 'baseUrl' must be configured";
            logger.debug("Disabling thing '{}': {}", getThing().getUID(), status);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, status);
            return;
        }
        if (config.refresh == null || config.refresh <= 0) {
            String status = "Parameter 'refresh' must be configured and strictly positive";
            logger.debug("Disabling thing '{}': {}", getThing().getUID(), status);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, status);
            return;
        }
        logger.info("Scheduling periodic updates with {} seconds interval", config.refresh);
        handle = ses.scheduleWithFixedDelay(() -> update(), 0, config.refresh, TimeUnit.SECONDS);
    }

    @Override
    public void dispose() {
        if (handle != null) {
            handle.cancel(false);
        }
        try {
            Unirest.shutdown();
        } catch (IOException ioe) {
            logger.warn("Unirest shutdown failed", ioe);
        }
        super.dispose();
    }
}
