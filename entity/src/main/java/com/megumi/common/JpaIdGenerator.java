package com.megumi.common;


import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * 2021/3/9
 *
 * @author miyabi
 * @since 1.0
 */
public class JpaIdGenerator implements IdentifierGenerator {
    private static final IdGenerator idGenerator = IdGenerator.getInstance();

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return idGenerator.getNextId();
    }
}
