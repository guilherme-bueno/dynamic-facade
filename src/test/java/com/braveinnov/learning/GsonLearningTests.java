package com.braveinnov.learning;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.braveinnov.models.BasicResponse;
import com.google.gson.Gson;

public class GsonLearningTests {

  @Test
  public void test(){
    BasicResponse response = new BasicResponse();
    response.setMessage("Hello");
    response.setCode("200");
    Map<String,Object> headers = new HashMap<>();
    headers.put("key", "value 1");
    response.setData(headers);
    String content = new Gson().toJson(response);
    System.out.println(content);
  }
  
}
