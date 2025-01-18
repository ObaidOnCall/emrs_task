package io.hahn_software.emrs.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import io.hahn_software.emrs.entities.AbstractBaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DBUtiles {
    
    private DBUtiles () {}



    public static <T> Map<String, Object> convertToMap(T entity) {
        Map<String, Object> map = new HashMap<>();
    
        // Get declared fields and filter out Hibernate internal fields
        Arrays.stream(entity.getClass().getDeclaredFields())
            .filter(field -> !field.getName().startsWith("$$_"))  // Exclude Hibernate internal fields
            .forEach(field -> {
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    // Add to map only if the value is not null or 0 (for numeric types)
                    if (value != null && !(value instanceof Number && ((Number) value).intValue() == 0)) {
                        map.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    // Handle exception or log
                    e.printStackTrace();
                }
            });
        
        if (map.isEmpty()) {
            throw new IllegalArgumentException("No fields to update");
        }
    
        return map;
    }



    private static boolean isEntityWithNullId(Object obj) {
    
        try {
            // Check if the class is annotated as an entity (assuming a JPA Entity in jakarta package)
            if (obj.getClass().isAnnotationPresent(Entity.class)) {
                log.info("Object is not an entity: {}", obj.getClass().getName());
                

                Field idField = obj.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                
                Object idValue = idField.get(obj);
                
                if (idValue != null) {
                    return false;  // `id` field is non-null, so it's a valid entity with a valid ID
                } else {
                    log.info("Entity {} has a null id", obj.getClass().getName());
                }
                return true;
            }
    
            // Check for `id` field and validate if it is non-null
            
            
            
            
        } catch (NoSuchFieldException e) {
            log.warn("No id field found in entity {}", obj.getClass().getName(), e);
        } catch (IllegalAccessException e) {
            log.warn("Unable to access id field for entity {}", obj.getClass().getName(), e);
        }
    
        return false; // Either `id` is null or entity check failed
    }


    
    private static Set<String> getValidateFieldNames(Field[] fields) {
        
        // @ Validate field names to prevent injection and errors
        return Arrays.stream(fields)
            .map(Field::getName)
            .collect(Collectors.toSet());
    }

    // Build the JPQL query dynamically based on non-null fields
    public static Query buildJPQLQueryDynamicallyForUpdate(
            AbstractBaseEntity entity ,
            EntityManager em 
        ) {
        

        // Get the entity class
        Class<?> entityClass = entity.getClass();

        // Retrieve the entity name dynamically
        Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
        String entityName = (entityAnnotation != null && !entityAnnotation.name().isEmpty()) 
                            ? entityAnnotation.name() 
                            : entityClass.getSimpleName();

        StringBuilder jpql = new StringBuilder("UPDATE " + entityName + " c SET ");
        Map<String, Object> params = new HashMap<>();

        for (Map.Entry<String, Object> entry : DBUtiles.convertToMap(entity).entrySet()) {
            
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            
            // Check if the field is a valid field and if the value is not null
            if (DBUtiles.getValidateFieldNames(entity.getClass().getDeclaredFields()).contains(fieldName) && value != null) {
                
                if (DBUtiles.isEntityWithNullId(value)) {
                    continue;
                }
                
                jpql.append("c.").append(fieldName).append(" = :").append(fieldName).append(", ");
                params.put(fieldName, value);
            }
        }

        jpql.setLength(jpql.length() - 2);  // Remove last comma
        jpql.append(" WHERE c.id IN :Ids");


        log.info("JPQL {} : 📑" , jpql);


        //? Create the query with the base JPQL
        Query query = em.createQuery(jpql.toString());
    
        //$ Set parameters for non-null fields
        for (Map.Entry<String, Object> param : params.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }

        return query ;
    }
}