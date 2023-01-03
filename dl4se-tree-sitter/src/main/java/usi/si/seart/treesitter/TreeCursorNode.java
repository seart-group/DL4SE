package usi.si.seart.treesitter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TreeCursorNode {

  String type;
  String name;
  int startByte;
  int endByte;
  Point startPoint;
  Point endPoint;
  boolean isNamed;
}
