package tbytekit.interceptors;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import java.util.Arrays;

public class PrintInterceptor {

  @AtEnter(
      inline = true,
      suppress = RuntimeException.class,
      suppressHandler = PrintExceptionSuppressHandler.class)
  public static void atEnter(
      @Binding.This Object object,
      @Binding.Class Object clazz,
      @Binding.Args Object[] args,
      @Binding.MethodName String methodName,
      @Binding.MethodDesc String methodDesc) {
    String full = ((Class) clazz).getName() + ":" + methodName;
    String line = String.format("atEnter %s %s", full, Arrays.deepToString(args));
    System.out.println(line);
  }

  @AtExit(inline = true)
  public static void atExit(
      @Binding.This Object object,
      @Binding.Class Object clazz,
      @Binding.MethodName String methodName,
      @Binding.Return Object returnObject) {

    String full = ((Class) clazz).getName() + ":" + methodName;
    String line = String.format("atExit %s %s", full, returnObject);
    System.out.println(line);
  }

  @AtExceptionExit(inline = true, onException = RuntimeException.class)
  public static void atExceptionExit(
      @Binding.Throwable RuntimeException ex,
      @Binding.Class Object clazz,
      @Binding.MethodName String methodName,
      @Binding.Field(name = "exceptionCount") int exceptionCount) {
    String full = ((Class) clazz).getName() + ":" + methodName;
    String line = String.format("atExceptionExit %s", full);
    System.out.println(line);
    System.out.println(
        "atExceptionExit, ex: " + ex.getMessage() + ", field exceptionCount: " + exceptionCount);
  }
}
