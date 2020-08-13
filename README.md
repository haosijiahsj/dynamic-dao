# dynamic-dao
java, simple-orm, reflect
## 概述
基于sping-jdbc
## 配置方式
* 使用注解方式
```java
@Configuration
public class DynamicDaoConfig {
    @Bean
    public InjectDaoBeanPostProcessor injectDaoBeanPostProcessor() {
        return new InjectDaoBeanPostProcessor();
    }
}
```
* 使用xml方式
```java
<bean class="com.husj.dynamicdao.InjectDaoBeanPostProcessor" />
```
* 使用方式  
dao层在接口上使用注解@Save @Update @BatchUpdate @Query  
service层在dao接口字段使用@DynamicDao注解
## 使用示例
1. @Save  
* 使用?占位符
```java
@Save("INSERT INTO table_name(column1, column2, column3) VALUES (?, ?, ?)")
int save(int column1, int column2, String column3);
```
* 使用具名占位符  
```java
@Save("INSERT INTO table_name(column1, column2, column3) VALUES (:1, :2, :3)")
int save(Map<String, Object> map);

@Save("INSERT INTO table_name(column1, column2, column3) VALUES (:1, :2, :3)")
int save(@Param("1") int a, @Param("2") int b, @Param("3") int c);
```
* 返回主键（默认返回成功条数）
```java
@Save(value = "INSERT INTO table_name(column1, column2, column3) VALUES (?, ?, ?)", returnKey = true)
int save(int column1, int column2, String column3);

@Save(value = "INSERT INTO table_name(column1, column2, column3) VALUES (:1, :2, :3)", returnKey = true)
int save(Map<String, Object> map);
```
* 实体类使用JPA注解
```java
@Save
int save(Entity entity);
```
2. @Update
* 使用?占位符
```java
@Update("UPDATE table_name SET column1 = ?, column2 = ? WHERE id = ?")
int update(String column1, String column2, int id);
```
* 使用具名占位符  
```java
@Update(value = "UPDATE table_name SET column1 = :2, column2 = :3 WHERE id = :1")
int update(Map<String, Object> map);
```
* 实体类使用JPA注解（此方式默认使用实体类中的@id字段作为WHERE条件）
```java
@Update
int update(Entity entity);
```
3. @Query  
* 返回List<Map<String, Object>>
```java
@Query("select * from table_name where id = ?")
List<Map<String, Object>> query(int id);

@Query("select * from table_name where id = :id")
List<Map<String, Object>> query(Map<String, Object> map);
```
* 返回值封装到对象中（返回列的名称要与实体类中字段名相同，不区分大小写）
```java
@Query("select id, name, sex from table_name where id = ?")
List<Entity> query(int id);

@Query("select id, name, sex from table_name where id = :id")
List<Entity> query(Map<String, Object> map);
```
* 动态查询（根据传入map中该值是否是null来决定是否拼接该条件，operator拼接到该条件前面，没有命名占位符的条件会直接拼接）
```java
@Query("select id, name, sex from table_name where id = ?")
@Conditions({
    @Condition(value = "sex = :sex"),
    @Condition(value = "name LIKE :name", operator = Operator.OR)
    @Condition(value = "tel IS NOT NULL")
})
List<Entity> query(Map<String, Object> map);
```
* 分页查询（不返回页数条数相关信息）
```java
@Query("select id, name, sex from table_name where id = :id")
List<Entity> query(Map<String, Object> paramMap, PageParam pageParam);
```
* 分页查询（返回页数条数相关信息）
```java
@Query("select id, name, sex from table_name")
@Conditions({
    @Condition(value = "sex = :sex"),
    @Condition(value = "name LIKE :name", operator = Operator.OR)
    @Condition(value = "tel IS NOT NULL")
})
PageWrapper<Entity> query(Map<String, Object> paramMap, PageParam pageParam);
```
