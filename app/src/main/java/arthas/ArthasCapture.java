package arthas;

import java.util.ArrayList;
import java.util.List;

public class ArthasCapture {
  public static List<Object> capturedObjs = new ArrayList<>();

  public static String capture(Object obj) {
    capturedObjs.add(obj);
    return "1";
  }
}
