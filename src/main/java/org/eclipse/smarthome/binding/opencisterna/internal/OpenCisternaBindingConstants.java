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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link OpenCisternaBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Sven Trieflinger
 */
@NonNullByDefault
public class OpenCisternaBindingConstants {

    private static final String BINDING_ID = "opencisterna";

    // List of all Open Cisterna Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_CISTERN = new ThingTypeUID(BINDING_ID, "cistern");

    // List of all Open Cisterna Channel ids
    public static final String FLUID_LEVEL = "fluidLevel";
    public static final String FLUID_QUANTITY = "fluidQuantity";

}