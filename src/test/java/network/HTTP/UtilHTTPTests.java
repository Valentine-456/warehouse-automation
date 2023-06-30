package network.HTTP;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class UtilHTTPTests {

    @Test
    public void testQueryToMapWithSimpleQueryString() {
        String query = "key1=value1&key2=value2";
        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "value1");
        expected.put("key2", "value2");

        Map<String, String> result = UtilHTTP.queryToMap(query);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testQueryToMapWithUrlEncodedQueryString() {
        String query = "key1=value%201&key2=value%202";
        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "value 1");
        expected.put("key2", "value 2");

        Map<String, String> result = UtilHTTP.queryToMap(query);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testQueryToMapWithEmptyValue() {
        String query = "key1=&key2=value2";
        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "");
        expected.put("key2", "value2");

        Map<String, String> result = UtilHTTP.queryToMap(query);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testQueryToMapWithMultipleValuesForSameKey() {
        String query = "key1=value1&key1=value2";
        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "value2");

        Map<String, String> result = UtilHTTP.queryToMap(query);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testQueryToMapWithSpecialCharacters() {
        String query = "key1=value%20%26%203";
        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "value & 3");

        Map<String, String> result = UtilHTTP.queryToMap(query);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testQueryToMapWithNullQuery() {
        String query = null;
        Map<String, String> expected = new HashMap<>();

        Map<String, String> result = UtilHTTP.queryToMap(query);

        Assert.assertEquals(expected, result);
    }
}