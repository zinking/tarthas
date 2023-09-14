package tbytekit;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.utils.AgentUtils;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.bytekit.utils.Decompiler;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import java.util.List;
import tbytekit.interceptors.PrintInterceptor;

public class ByteKit {
  static Object installed = AgentUtils.install();

  public static void inhanceWatch(Class<?> clazz, String method) {
    inhanceWatch(clazz, method, false);
  }

  public static void inhanceWatch(Class<?> clazz, String method, boolean displayEnhance) {
    DefaultInterceptorClassParser interceptorClassParser = new DefaultInterceptorClassParser();
    List<InterceptorProcessor> processors = interceptorClassParser.parse(PrintInterceptor.class);

    try {
      ClassNode classNode = AsmUtils.loadClass(clazz);
      for (MethodNode methodNode : classNode.methods) {
        if (methodNode.name.equals(method)) {
          MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
          for (InterceptorProcessor interceptor : processors) {
            interceptor.process(methodProcessor);
          }
          System.out.println(String.format("%s %s enhanced", clazz.getSimpleName(), method));
        }
      }

      byte[] bytes = AsmUtils.toBytes(classNode);
      if (displayEnhance) {
        System.out.println("enhancedWatch class:");
        System.out.println(Decompiler.decompile(bytes));
      }
      AgentUtils.reTransform(clazz, bytes);

    } catch (Exception e) {
      System.out.println("inhanceWatch: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
