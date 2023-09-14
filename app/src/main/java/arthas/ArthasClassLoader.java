package arthas;

import java.io.IOException;
import java.io.InputStream;

public class ArthasClassLoader extends ClassLoader {

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    if (!name.startsWith("com.taobao.arthas")) {
      return super.loadClass(name);
    }
    try {
      InputStream in = ArthasSession.class.getResourceAsStream(name);
      byte[] a = new byte[10000];
      int len = in.read(a);
      in.close();
      return defineClass(name, a, 0, len);
    } catch (IOException e) {
      throw new ClassNotFoundException();
    }
  }
}
