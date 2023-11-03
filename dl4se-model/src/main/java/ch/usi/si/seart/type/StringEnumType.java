package ch.usi.si.seart.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.EnumType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class StringEnumType extends EnumType<Enum<?>> {
    @Override
    public void nullSafeSet(
            PreparedStatement statement, Object value, int index, SharedSessionContractImplementor contractImplementor
    ) throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, Types.OTHER);
        } else {
            statement.setObject(index, value.toString(), Types.OTHER);
        }
    }
}
