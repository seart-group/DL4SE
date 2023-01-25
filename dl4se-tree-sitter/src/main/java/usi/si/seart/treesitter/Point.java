package usi.si.seart.treesitter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EqualsAndHashCode
public class Point {

    int row;
    int column;

    @Override
    public String toString() {
        return row + ":" + column;
    }

    public boolean isOrigin() {
        return row == 0 && column == 0;
    }
}
