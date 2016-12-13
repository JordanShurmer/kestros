package com.kestros.properties;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ResourcePropertyTest {

    @Rule
    public final SlingContext context = new SlingContext();

    private Resource fullResource;

    @Before
    public void setUp() throws Exception {
        context.load().json("/testNodes.json", "/content/tests");
        fullResource = context.resourceResolver().resolve("/content/tests/fullResource");

    }

    @Test
    public void testStringSingleProperty() throws Exception {
        ResourceProperty<String> testMe;
        Optional<String> theOptional;
        String actualValue;

        testMe = new ResourceProperty<>("description");
        theOptional = testMe.fromResource(fullResource);
        assert theOptional != null : "fromResource should not return null";
        actualValue = theOptional.orElse("~~");
        assertEquals("A Resource with properties", actualValue);
    }

    @Test
    public void testEmptyStringProperty() throws Exception {
        ResourceProperty<String> testMe;
        Optional<String> theOptional;
        String actualValue;

        testMe = new ResourceProperty<>("nothing");
        theOptional = testMe.fromResource(fullResource);
        assert theOptional != null : "fromResource should not return null";
        actualValue = theOptional.orElse("~~");
        assertEquals("~~", actualValue);
    }

    @Test
    public void testStringMultiProperty() throws Exception {
        ResourceProperty<String> testMe;
        Optional<String> theOptional;
        String actualValue;

        testMe = new ResourceProperty<>("title", "legacyTitle");
        theOptional = testMe.fromResource(fullResource);
        assert theOptional != null : "fromResource should not return null";
        actualValue = theOptional.orElse("~~");
        assertEquals("First property found should be used", "Title Value", actualValue);

        testMe = new ResourceProperty<>("nothing", "title", "legacyTitle");
        theOptional = testMe.fromResource(fullResource);
        assert theOptional != null : "fromResource should not return null";
        actualValue = theOptional.orElse("~~");
        assertEquals("lookup order not working. should have found second property", "Title Value", actualValue);
    }

    @Test
    public void testCalendarSingleProperty() throws Exception {
        ResourceProperty<Calendar> testMe;
        Optional<Calendar> theOptional;
        Calendar actualValue, expected;
        Date expectedDate;

        //setup the expected value
        SimpleDateFormat calendarFormat = new SimpleDateFormat(JsonItemWriter.ECMA_DATE_FORMAT, JsonItemWriter.DATE_FORMAT_LOCALE);
        expectedDate = calendarFormat.parse("Fri Dec 09 2016 10:13:20 GMT-0500");
        expected = Calendar.getInstance();
        expected.setTime(expectedDate);


        //run the test and verify
        testMe = new ResourceProperty<>("aDate");
        theOptional = testMe.fromResource(fullResource);
        assert theOptional != null : "fromResource should not return null";
        actualValue = theOptional.orElse(null);
        assert actualValue != null : "Could not get Calendar from date property";
        assertEquals("Calendar property doesn't match expected.", expected, actualValue);
    }

    @Test
    public void testStringArrayProperty() throws Exception {
        ResourceProperty<List<String>> testMe;
        List<String> expected, actual;
        Optional<List<String>> theOptional;

        //set up the expected
        expected = new ArrayList<>();
        expected.add("value1");
        expected.add("value2");

        testMe = new ResourceProperty<>("twoValues");
        theOptional = testMe.fromResource(fullResource);
        assert theOptional != null : "fromResource should not return null";
        actual = theOptional.orElse(Collections.emptyList());
        assert !actual.isEmpty() : "Could not get List<String> from String array";
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

}