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
package org.eclipse.smarthome.binding.opencisterna.internal.model;

import org.apache.http.util.Asserts;

/**
 * {@link CisternaState} is used to deserialize the JSON returned from the Opencisterna sensor.
 *
 * @author Sven Trieflinger
 */
public class CisternState {

    private double level;
    private double quantity;

    /**
     * The fluid level reported by the sensor.
     *
     * Value is guaranteed to be between 0 and 1.
     *
     * @return The fluid level as a fraction of 1.
     */
    public double getLevel() {
        return level;
    }

    /**
     * Set the fluid level.
     *
     * <code>level</code> must
     *
     * @param level The fluid level. Must be between 0 and 1.
     */
    public void setLevel(double level) {
        Asserts.check(0 <= level && level <= 1, "level must be between 0 and 1");
        this.level = level;
    }

    /**
     * The fluid quantity reported by the cistern sensor.
     *
     * @return The quantity in m³.
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Set the fluid quanity.
     *
     * @param quantity The fluid quantity in m³. Must be non-negative.
     */
    public void setQuantity(double quantity) {
        Asserts.check(0 <= quantity, "quantity must be non-negative");
        this.quantity = quantity;
    }

}