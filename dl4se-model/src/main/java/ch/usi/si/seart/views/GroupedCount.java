package ch.usi.si.seart.views;

import java.util.Map;

public interface GroupedCount<K> extends Map.Entry<K, Long> {

    K getKey();
    Long getCount();

    @Override
    default Long getValue() {
        return getCount();
    }

    @Override
    default Long setValue(Long value) {
        throw new UnsupportedOperationException();
    }
}
