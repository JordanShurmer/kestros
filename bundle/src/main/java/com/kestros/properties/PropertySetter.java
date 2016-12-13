package com.kestros.properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class PropertySetter<T> {

    private final static Logger LOG = LoggerFactory.getLogger(PropertySetter.class);
    private String propertyName;
    private T value;

    PropertySetter(String propertyName, T value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    public void onResource(@Nonnull Resource resource) {
        final ModifiableValueMap properties;

        if (StringUtils.isNotEmpty(propertyName)) {

            properties = resource.adaptTo(ModifiableValueMap.class);
            if (properties != null) {
                if (value != null) {
                    LOG.debug("Setting {} = {} on {}", propertyName, value, resource.getPath());
                    properties.put(propertyName, value);
                } else {
                    LOG.debug("Removing {} from {}", propertyName, resource.getPath());
                    properties.remove(propertyName);
                }
            } else {
                LOG.warn("Unable to get ModifiableValueMap from resource {}", resource);
            }

        }
    }
}
