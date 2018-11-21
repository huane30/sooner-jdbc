package com.sooner.framework.jdbc.core.toolkit;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;


/**
 * 测试 bean utils
 *
 * @author HCL
 * 2018/7/24 17:44
 */
public class BeanUtilsTest {

    /**
     * 测试 beanToMap
     */
    @Test
    public void beanMapConvertTest() {
        Map<String, Object> map = BeanUtils.beanToMap(new User() {{
            setId(123);
            setName("baomidou");
        }});
        assertEquals(2, map.keySet().size());
        assertEquals(123, map.get("id"));
        assertEquals("baomidou", map.get("name"));
        // 测试反向转换过程
        User user = BeanUtils.mapToBean(map, User.class);
        assertEquals(123, user.getId());
        assertEquals("baomidou", user.getName());

    }

    /**
     * 自定义实体类用于测试
     */
    public static class User {

        private String name;
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

}
