package com.example.backend.jsonUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JsonUtilityTest {

  @Data
  @AllArgsConstructor
  static class Person{
    private String name;
    private int age;
  }
  /**
   * Tests readJson method with a simple class type.
   * Validates if the JSON string is correctly deserialized into a Person object with expected name and age.
   */
  @Test
  void testReadJsonWithClass(){
    try {
      String json = "{\"name\":\"John\", \"age\":30}";
      JsonUtility<Person> jsonUtility = new JsonUtility<>();

      Person person = jsonUtility.readJson(json, Person.class);

      assertEquals("John", person.getName());
      assertEquals(30, person.getAge());
    } catch (Exception e){
      fail();
    }

  }

  /**
   * Tests readJson method with a specified Type (List of Persons).
   * Verifies if the JSON array is accurately deserialized into a list of Person objects with correct attributes.
   */
  @Test
  void testReadJsonWithType(){
    try {
      String json = "[{\"name\":\"John\", \"age\":30}, {\"name\":\"Jane\", \"age\":25}]";
      Type listType = Types.newParameterizedType(List.class, Person.class);
      JsonUtility<List<Person>> jsonUtility = new JsonUtility<>();

      List<Person> people = jsonUtility.readJson(json, listType);

      assertEquals(2, people.size());
      assertEquals("John", people.get(0).getName());
      assertEquals(25, people.get(1).getAge());
    } catch (Exception e){
      fail();
    }
  }

  /**
   * Tests the serialize method.
   * Confirms if a Person object is correctly serialized into a JSON string with matching name and age values.
   */
  @Test
  void testSerialize(){
    try {
      Person person = new Person("Alice", 28);
      JsonUtility<Person> jsonUtility = new JsonUtility<>();

      String json = jsonUtility.serialize(person, Person.class);

      assertTrue(json.contains("\"name\":\"Alice\""));
      assertTrue(json.contains("\"age\":28"));
    } catch (Exception e){
      fail();
    }

  }

}
