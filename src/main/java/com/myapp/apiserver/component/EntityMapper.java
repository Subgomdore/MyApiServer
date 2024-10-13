package com.myapp.apiserver.component;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Component
public class EntityMapper {

    // 엔티티 리스트를 Map 리스트로 변환
    public <T> List<Map<String, Object>> convertToMapList(List<T> entities) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (T entity : entities) {
            resultList.add(convertToMap(entity));  // 각 엔티티를 Map으로 변환
        }
        return resultList;
    }

    // 엔티티 객체를 Map<String, Object>로 변환
    public <T> Map<String, Object> convertToMap(T entity) {
        Map<String, Object> resultMap = new HashMap<>();
        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);  // private 필드에도 접근 가능
            try {
                resultMap.put(field.getName(), field.get(entity));  // 필드 이름과 값을 Map에 저장
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return resultMap;
    }
}
