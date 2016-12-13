package com.kestros.properties;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A class used to pull values from resources.
 *
 * This will use the {@link Resource#getValueMap()} method to get the properties
 * of the resource.
 *
 * @param <PropertyType> the value from the resource's valuemap will be cast to T
 */
public class ResourceProperty<PropertyType> {

    private Logger LOG = LoggerFactory.getLogger(ResourceProperty.class);

    private List<String> propertyNames;

    /**
     * Create a new ResourceProperty which will pull data from the given list
     * of property names. When getting data the list of property names will be
     * checked in order and the first one that has a value will be used. When
     * setting data the {@link #setTo(Object)} method can be used to set
     * the value of the <strong>first</strong> propertyName given.
     *
     * @param propertyNames the property names, in order, to try to pull a value from
     */
    public ResourceProperty(String... propertyNames) {
        this.propertyNames = Arrays.asList(propertyNames);
    }

    /**
     * Use this method to extract a property from the given resource.
     *
     * <pre><code>
     *     ResourceProperty<String> titleProperty = new ResourceProperty<>("title", "legacyTitle");
     *     String title = titleProperty.fromResource(myResource);
     * </code></pre>
     *
     * @param resource the resource to pull the property from
     * @return A {@link Optional} that will contain the value if there was one
     */
    @Nonnull
    public Optional<PropertyType> fromResource(@Nonnull Resource resource) {
        Optional<PropertyType> valueFromResource;
        final ValueMap properties;

        properties = resource.getValueMap();
        try {

            //todo: add support for Calendar->Instant when Instant is requested
            //todo: add support for List when value is []
            //noinspection unchecked since we're catching ClassCastException
            valueFromResource = (Optional<PropertyType>) propertyNames.stream()
                    .map(properties::get)
                    .filter(Objects::nonNull)
                    .findFirst();

        } catch (ClassCastException ignored) {
            LOG.debug("ClassCastException when getting {} from {}: {}", propertyNames, resource, ignored);
            valueFromResource = Optional.empty();
        }

        return valueFromResource;
    }

    /**
     * Use this method to start setting (or removing) a property. It will
     * set the value to the <strong>first</strong> propertyName given in the constructor
     *
     * To set a property you simply chain a few method calls. This code will set
     * the property "title" to the value "New Title".
     *
     * <pre><code>
     *     ResourceProperty<String> titleProperty = new StringProperty("title", "altTitle");
     *     titleProperty.setTo("New Title").onResource(myResource);
     * </code></pre>
     *
     * To remove a property you can pass null as the value, or use the
     * convenient method {@link #removeFrom(Resource)}
     *
     * @param value the value to set this property to
     * @return a {@link PropertySetter} which can be used to set the property
     */
    @Nonnull
    public  PropertySetter<PropertyType> setTo(PropertyType value) {
        return new PropertySetter<>(this.propertyNames.stream().findFirst().orElse(""), value);
    }

    /**
     * A helper function to remove the first property given in the contructor.
     *
     * This is equivalent to
     * <pre><code>
     *     resourceProperty.setTo(null).onResource(resource);
     * </code></pre>
     *
     * @param resource the resource to remove this property from
     */
    public void removeFrom(@Nonnull Resource resource) {
        this.setTo(null).onResource(resource);
    }
}
