package com.digdes.school;

import java.util.List;
import java.util.Map;

public class JavaSchoolStarter {
    private static Table table = new Table();

    //Дефолтный конструктор
    public JavaSchoolStarter() {
    }

    //На вход запрос, на выход результат выполнения запроса
    public List<Map<String, Object>> execute(String request) throws Exception {
        List<Map<String, Object>> result = table.execute(request);
        System.out.println(request);
        table.print();
        return result;
    }
}