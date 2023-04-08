import static org.junit.Assert.*;

import com.digdes.school.Table;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTable {

    private Table table;

    @Before
    public void setUp() {
        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", 1L);
        row1.put("lastName", "Петров");
        row1.put("age", 30L);
        row1.put("cost", 5.4);
        row1.put("active", true);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("id", 2L);
        row2.put("lastName", "Иванов");
        row2.put("age", 25L);
        row2.put("cost", 4.3);
        row2.put("active", false);

        List<Map<String, Object>> data = new ArrayList<>();
        data.add(row1);
        data.add(row2);

        table = new Table(data);
    }

    @Test(expected = Exception.class)
    public void testWrongArgumentsRequest() throws Exception {
        String request = "INSERT 'lastName' = '' , 'id'=3, 'age'=40, 'active'=true";
        List<Map<String, Object>> result = table.execute(request);
    }

    @Test(expected = Exception.class)
    public void testWrongRequest() throws Exception {
        String request = "Hello World";
        List<Map<String, Object>> result = table.execute(request);
    }

    @Test
    public void testInsert() throws Exception {
        String request = "INSERT VALuES   'lastName' = 'new' , 'id'=3, 'age'=40, 'active'=true";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(1, result.size());
        assertEquals("new", table.data.get(2).get("lastName"));
        assertEquals(40L, table.data.get(2).get("age"));
        assertTrue((boolean) table.data.get(2).get("active"));
        assertEquals(3L, table.data.get(2).get("id"));
        assertEquals(4,  table.data.get(2).size());
    }

    @Test
    public void testInsertWithEmptyValues() throws Exception {
        String request = "INSERT VALuES   'lastName' = '' , 'id'=3, 'age'=40, 'active'=true";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(1, result.size());
        assertEquals("", table.data.get(2).get("lastName"));
    }

    @Test
    public void testInsertWithTrueCondition() throws Exception {
        String request = "INSERT VALuES   'lastName' = '' , 'id'=3, 'age'=40, 'active'=true WHERE 1!=1";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(1, result.size());
        assertEquals("", table.data.get(2).get("lastName"));
    }

    @Test
    public void testInsertWithCondition() throws Exception {
        String request = "INSERT VALuES   'lastName' = '' , 'id'=3, 'age'=40, 'active'=true WHERE 'id'=1";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(1, result.size());
        assertEquals("", table.data.get(2).get("lastName"));
    }

    @Test(expected = Exception.class)
    public void testInsertWithoutValues() throws Exception {
        String request = "INSERT";
        table.execute(request);
    }

    @Test
    public void testUpdate() throws Exception {
        String request = "UPDATE values 'lastName'='Кузнецов'";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(2, result.size());
        assertEquals(2, table.data.size());
        assertEquals("Кузнецов", result.get(0).get("lastName"));
        assertEquals("Кузнецов", table.data.get(0).get("lastName"));
        assertEquals("Кузнецов", result.get(1).get("lastName"));
        assertEquals("Кузнецов", table.data.get(1).get("lastName"));
    }

    @Test
    public void testUpdateWithCondition() throws Exception {
        String request = "UPDATE values 'lastName'='Кузнецов' WHERE 'id' = 1";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(1, result.size());
        assertEquals(2, table.data.size());
        assertEquals("Кузнецов", result.get(0).get("lastName"));
        assertEquals("Кузнецов", table.data.get(0).get("lastName"));
        assertNotEquals("Кузнецов", table.data.get(1).get("lastName"));
    }

    @Test(expected = Exception.class)
    public void testUpdateWithoutValues() throws Exception {
        String request = "UPDATE";
        table.execute(request);
    }

    @Test
    public void testSelectWithoutValues() throws Exception {
        String request = "SELECT";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(2, result.size());
        assertEquals(2, table.data.size());
    }

    @Test
    public void testSelectWithTrueCondition() throws Exception {
        String request = "SELECT VALUES WHERE 1 = 1 ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(2, result.size());
        assertEquals(2, table.data.size());
    }

    @Test
    public void testSelectWithCondition() throws Exception {
        String request = "SELECT VALUES WHERE 'id' = 1 ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(1, result.size());
        assertEquals(2, table.data.size());
    }

    @Test
    public void testDeleteWithoutValues() throws Exception {
        String request = "DELETE";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(2, result.size());
        assertEquals(0, table.data.size());
    }

    @Test
    public void testDeleteWithTrueCondition() throws Exception {
        String request = "DELETE VALUES WHERE 1 = 1 ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(2, result.size());
        assertEquals(0, table.data.size());
    }

    @Test
    public void testDeleteWithCondition() throws Exception {
        String request = "DELETE VALUES WHERE 'id' = 1 ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(1, result.size());
        assertEquals(1, table.data.size());
    }

    @Test
    public void testDeleteWithConditionMore() throws Exception {
        String request = "DELETE VALUES WHERE 'id' > 1 ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(1, result.size());
        assertEquals(1, table.data.size());
    }

    @Test
    public void testDeleteWithConditionLess() throws Exception {
        String request = "DELETE VALUES WHERE 'id' < 1 ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(0, result.size());
        assertEquals(2, table.data.size());
    }

    @Test
    public void testDeleteWithWrongLikeCondition() throws Exception {
        String request = "DELETE VALUES WHERE 'id' like 1 ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(0, result.size());
        assertEquals(2, table.data.size());
    }
    @Test
    public void testDeleteWithLikeCondition() throws Exception {
        String request = "DELETE VALUES WHERE 'lastName' like '%о%' ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(2, result.size());
        assertEquals(0, table.data.size());
    }

    @Test
    public void testDeleteWithLikeConditionEmptyResult() throws Exception {
        String request = "DELETE VALUES WHERE 'lastName' like '%О%' OR 'lastName' ilike '%kkkО%'";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(0, result.size());
        assertEquals(2, table.data.size());
    }

    @Test
    public void testDeleteWithILikeCondition() throws Exception {
        String request = "DELETE VALUES WHERE 'lastName' Ilike '%О%' ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(2, result.size());
        assertEquals(0, table.data.size());
    }

    @Test
    public void testDeleteWithILikeConditionEmptyResult() throws Exception {
        String request = "DELETE VALUES WHERE 'lastName' Ilike '%O%' AND 'lastName' Ilike '%3O%' ";
        List<Map<String, Object>> result = table.execute(request);
        assertEquals(0, result.size());
        assertEquals(2, table.data.size());
    }
}