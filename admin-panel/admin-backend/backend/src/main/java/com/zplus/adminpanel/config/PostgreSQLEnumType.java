package com.zplus.adminpanel.config;

import com.zplus.adminpanel.entity.RegistrationStatus;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;

/**
 * Custom Hibernate UserType for PostgreSQL enum mapping
 */
public class PostgreSQLEnumType implements UserType<RegistrationStatus> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<RegistrationStatus> returnedClass() {
        return RegistrationStatus.class;
    }

    @Override
    public boolean equals(RegistrationStatus x, RegistrationStatus y) {
        return x == y;
    }

    @Override
    public int hashCode(RegistrationStatus x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public RegistrationStatus nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        return value == null ? null : RegistrationStatus.valueOf(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, RegistrationStatus value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public RegistrationStatus deepCopy(RegistrationStatus value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(RegistrationStatus value) {
        return value;
    }

    @Override
    public RegistrationStatus assemble(Serializable cached, Object owner) {
        return (RegistrationStatus) cached;
    }
}
