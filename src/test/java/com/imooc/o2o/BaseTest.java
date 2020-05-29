package com.imooc.o2o;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//用来配置spring和junt整合，junit启动加载springIOC容器
@RunWith(SpringJUnit4ClassRunner.class)//类似springboot的 @SpringBootTest
//告诉junit spring配置文件的位置,这两个注解类似@component
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml","classpath:spring/spring-redis.xml",})//类似@RunWith(SpringRunner.class)
public class BaseTest {
}
