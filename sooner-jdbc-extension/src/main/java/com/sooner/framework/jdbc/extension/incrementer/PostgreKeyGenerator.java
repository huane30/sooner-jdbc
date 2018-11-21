package com.sooner.framework.jdbc.extension.incrementer;

import com.sooner.framework.jdbc.core.incrementer.IKeyGenerator;

/**
 * <p>
 * Postgres Sequence
 * </p>
 *
 * @author Caratacus
 * @since 2017-06-12
 */
public class PostgreKeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        return "select nextval('" + incrementerName + "')";
    }
}
