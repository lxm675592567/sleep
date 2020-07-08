package com.hife.base;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


public abstract class BaseMapper {
    @Autowired
    protected MongoTemplate mongoTemplate;

    private static final int FIRST_PAGE_NUM = 1;

    private static final String ID = "_id";


    /**
     * 分页查询，直接返回集合类型的结果.
     *
     * @see BaseMapper#pageQuery(BasicQuery,
     * Class, Function, Integer, Integer,
     * String, Sort.Order...)
     */
    protected <T> PageResult<T> pageQuery(BasicQuery query, Class<T> entityClass, Integer pageSize,
                                          Integer pageNum, Sort.Order... orders) {
        return pageQuery(query, entityClass, Function.identity(), pageSize, pageNum, null, orders);
    }

    /**
     * 分页查询，不考虑条件分页，直接使用skip-limit来分页.
     *
     * @see BaseMapper#pageQuery(BasicQuery,
     * Class, Function, Integer, Integer,
     * String, Sort.Order...)
     */
    protected <T, R> PageResult<R> pageQuery(BasicQuery query, Class<T> entityClass, Function<T, R> mapper,
                                             Integer pageSize, Integer pageNum, Sort.Order... orders) {
        return pageQuery(query, entityClass, mapper, pageSize, pageNum, null, orders);
    }

    /**
     * 分页查询.
     *
     * @param query       Mongo Query对象，构造你自己的查询条件.
     * @param entityClass Mongo collection定义的entity class，用来确定查询哪个集合.
     * @param mapper      映射器，你从db查出来的list的元素类型是entityClass, 如果你想要转换成另一个对象，比如去掉敏感字段等，可以使用mapper来决定如何转换.
     * @param pageSize    分页的大小.
     * @param pageNum     当前页.
     * @param lastId      条件分页参数, 区别于skip-limit，采用find(_id>lastId).limit分页.
     *                    如果不跳页，像朋友圈，微博这样下拉刷新的分页需求，需要传递上一页的最后一条记录的ObjectId。 如果是null，则返回pageNum那一页.
     * @param orders      排序条件
     * @param <T>         collection定义的class类型.
     * @param <R>         最终返回时，展现给页面时的一条记录的类型。
     * @return PageResult，一个封装page信息的对象.
     */
    protected <T, R> PageResult<R> pageQuery(BasicQuery query, Class<T> entityClass, Function<T, R> mapper,
                                             Integer pageSize, Integer pageNum, String lastId, Sort.Order... orders) {
        // 分页逻辑
        long total = mongoTemplate.count(query, entityClass);
        final Integer pages = (int) Math.ceil(total / (double) pageSize);
        if (pageNum <= 0 || pageNum > pages) {
            pageNum = FIRST_PAGE_NUM;
        }
        final Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(lastId)) {
            if (pageNum != FIRST_PAGE_NUM) {
                criteria.and(ID).gt(new ObjectId(lastId));
            }
            query.limit(pageSize);
        } else {
            int skip = pageSize * (pageNum - 1);
            query.skip(skip).limit(pageSize);
        }
        Sort sort = new Sort(Sort.Direction.ASC, ID);

        if (orders != null && orders.length > 0) {
            sort = Sort.by(orders);
        }

        final List<T> entityList = mongoTemplate
                .find(query.addCriteria(criteria)
                                .with(sort),
                        entityClass);

        final PageResult<R> pageResult = new PageResult<>();
        PageResult.Page page = new PageResult.Page()
                .setTotal(total)
                .setPages(pages)
                .setPageSize(pageSize)
                .setPageNum(pageNum);
        pageResult.setList(entityList.stream()
                .map(mapper)
                .collect(Collectors.toList()))
                .setPage(page);
        return pageResult;
    }

    /**
     * 批量保存
     *
     * @param list        要存入的集合
     * @param entityClass Mongo collection定义的entity class 用来确定查询哪个集合.
     * @param <T>         泛型
     */
    protected <T> void saveBatch(List<T> list, Class<T> entityClass) {
        if (list == null || list.isEmpty() || entityClass == null) {
            return;
        }
        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, entityClass);
        for (T t : list) {
            bulkOperations.insert(t);
        }
        bulkOperations.execute();
    }

    protected <T> T updateFirst(T t, Criteria criteria) {
        Class<?> clazz = t.getClass();

        Field[] fields = clazz.getDeclaredFields();

        Update update = new Update();

        Arrays.stream(fields)
                //.map(Field::getName)
                .forEach(field -> {
                    String name = field.getName();
                    Object value = getFieldValueByName(name, t);

                    org.springframework.data.mongodb.core.mapping.Field fieldAnnotation
                            = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
                    if (Objects.nonNull(fieldAnnotation)) {
                        name = fieldAnnotation.value();
                    }

                    if (Objects.nonNull(value)) {
                        update.set(name, value);
                    }
                });
        this.mongoTemplate.updateFirst(new Query(criteria), update, clazz);

        return t;
    }

    protected <T> T upsert(T t, Criteria criteria) {
        Class<?> clazz = t.getClass();

        Field[] fields = clazz.getDeclaredFields();

        Update update = new Update();

        Arrays.stream(fields)
                // .map(Field::getName)
                .forEach(field -> {
                    String name = field.getName();
                    Object value = getFieldValueByName(name, t);

                    org.springframework.data.mongodb.core.mapping.Field fieldAnnotation
                            = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
                    if (Objects.nonNull(fieldAnnotation)) {
                        name = fieldAnnotation.value();
                    }

                    if (Objects.nonNull(value)) {
                        update.set(name, value);
                    }
                });
        this.mongoTemplate.upsert(new Query(criteria), update, clazz);

        return t;
    }

    /**
     * 根据属性名获取属性值
     */
    private <T> Object getFieldValueByName(String fieldName, T t) {
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String getter = "get" + firstLetter + fieldName.substring(1);
        try {
            Method method = t.getClass().getMethod(getter);
            return method.invoke(t);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
