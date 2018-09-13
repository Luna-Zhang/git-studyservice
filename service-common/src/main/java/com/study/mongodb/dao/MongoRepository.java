package com.study.mongodb.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.study.anaotations.Column;
import com.study.anaotations.Table;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public abstract class MongoRepository<T> {

    @Autowired
    public MongoTemplate mongoTemplate;

    public void insert(T t) {
        DBObject dBObject = new BasicDBObject();
        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    dBObject.put(columnName, object);
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        this.mongoTemplate.insert(dBObject, tableName);
    }

    @SuppressWarnings("unchecked")
    public List<T> find(T t) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    Criteria criteria = new Criteria(columnName);
                    criteria.is(object);
                    query.addCriteria(criteria);
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        List<T> userList = (List<T>) this.mongoTemplate.find(query, clazz,
                tableName);
        return userList;
    }

    @SuppressWarnings("unchecked")
    public List<T> findOrderBySort(T t,Sort sort) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    Criteria criteria = new Criteria(columnName);
                    criteria.is(object);
                    query.addCriteria(criteria);
                }
            }
            if (null != sort) {
                query.with(sort);
            }

        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        List<T> userList = (List<T>) this.mongoTemplate.find(query, clazz,
                tableName);
        return userList;
    }

    private String getTableName(Class<?> clazz) {
        String tableName = "";
        Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            tableName = table.value();
        } else {
            tableName = clazz.getSimpleName();
        }
        return tableName;
    }

    private Field[] getFields(Class<?> clazz) {
        Field[] beanFields = clazz.getDeclaredFields();
        Class<?> beanSuperClass = clazz.getSuperclass();
        Field[] beanSuperFields = beanSuperClass.getDeclaredFields();
        return ArrayUtils.addAll(beanFields, beanSuperFields);
    }

    private boolean isStatic(int modifiers) {
        return ((modifiers & Modifier.STATIC) != 0);
    }

    private static String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        String columnName = "";
        if (column != null) {
            columnName = column.value();
        }
        if (StringUtils.isEmpty(columnName)) {
            return field.getName();
        }
        return columnName;
    }

    //设置修改条件
//      public void update(T t) {
//        Query query = new Query();
//        Criteria criteria = new Criteria("_id");
//        criteria.is();
//        query.addCriteria(criteria);
//        this.mongoTemplate.updateFirst(query, update, "user");
//    }


    /**
     * 根据userUuid修改
     * @param t
     */
    public void update(T t){
        Update update=new Update();
        Query query=new Query();
        DBObject dBObject = new BasicDBObject();
        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        boolean flag = false;
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    dBObject.put(columnName, object);
                    if(columnName.equals("uuid")||columnName.equals("id")){
                        Criteria criteria = new Criteria(columnName);
                        criteria.is(object);
                        query.addCriteria(criteria);
                        flag = true;
                    }
                    else {
                        update.set(columnName,object);
                    }
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        if(flag){
            this.mongoTemplate.updateFirst(query,update,tableName);
        }
    }

    /**
     * 根据userUuid修改
     * @param t
     */
    public void updateByOrderNo(T t){
        Update update=new Update();
        Query query=new Query();
        DBObject dBObject = new BasicDBObject();
        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        boolean flag = false;
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    dBObject.put(columnName, object);
                    if(columnName.equals("orderNo")){
                        Criteria criteria = new Criteria(columnName);
                        criteria.is(object);
                        query.addCriteria(criteria);
                        flag = true;
                    }else {
                        update.set(columnName,object);
                    }
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        if(flag) {
            this.mongoTemplate.updateFirst(query, update, tableName);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> customFind(T t,List<Criteria> criteriaList) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);

        for(Criteria item:criteriaList){
            query.addCriteria(item);
        }

        List<T> result = (List<T>) this.mongoTemplate.find(query, clazz,tableName);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<T> customFind2(T t) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);

//        for(Criteria item:criteriaList){
//            query.addCriteria(item);
//        }

        query.with(new Sort(Sort.Direction.DESC,"_id")).limit(20);

        List<T> result = (List<T>) this.mongoTemplate.find(query, clazz,tableName);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<T> customFindByPage(T t, Sort sort, List<Criteria> criteriaList, Integer pageStart, Integer pageSize) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);

        for(Criteria item:criteriaList){
            query.addCriteria(item).skip(pageStart).limit(pageSize).with(sort);
        }

        List<T> result = (List<T>) this.mongoTemplate.find(query, clazz,tableName);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<T> customFindByPage(T t,Sort sort,Integer pageStart,Integer pageSize) {
        Query query = new Query();

        Class<?> clazz = t.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = getFields(clazz);
        try {
            for (Field field : fields) {
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                String columnName = getColumnName(field);
                field.setAccessible(true);
                Object object = field.get(t);
                if (object != null) {
                    Criteria criteria = new Criteria(columnName);
                    criteria.is(object);
                    query.addCriteria(criteria);
                }
            }
        } catch (Exception e) {
            new RuntimeException("mongo dal exceptoin" + e);
        }
        query.with(sort).skip(pageStart).limit(pageSize);

        List<T> result = (List<T>) this.mongoTemplate.find(query, clazz,tableName);
        return result;
    }
}
