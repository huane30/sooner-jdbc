package com.sooner.framework.jdbc.core.toolkit.condition;

import org.junit.Test;

public class WrapperTest {

    @Test
    public void name() {
        Wrapper<User> wrapper = new WrapperImpl<>();
        wrapper.eq("id", 123);
        System.out.println(wrapper.getSqlSeq());
    }

    private class User {

        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
