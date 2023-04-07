package com.digdes.school;


import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String... args){
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {
            List<Map<String,Object>> result0 = starter.execute("INSERT VALuES   'lastName' = '' , 'id'=3, 'age'=40, 'active'=true");
            //Вставка строки в коллекцию
            //List<Map<String,Object>> result0_5 = starter.execute("INSERT VALuES   'lastName' = 'Федоров' , 'id'=, 'age'=null, 'active'=true");

            List<Map<String,Object>> result1 = starter.execute("INSERT VALuES   'lastName' = 'Федоров' , 'id'=3, 'age'=null, 'active'=true");
            //Изменение значения которое выше записывали
            List<Map<String,Object>> result1_5 = starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1");

            List<Map<String,Object>> result2 = starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 5= 5 AND 'id'=3");
            //Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
            List<Map<String,Object>> result3 = starter.execute("SELECT");
            starter.execute("DELETE");

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
