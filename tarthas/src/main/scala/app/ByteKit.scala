package app

import tbytekit.interceptors.PrintInterceptor
import com.alibaba.bytekit.asm.MethodProcessor
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser
import com.alibaba.bytekit.utils.{AgentUtils, AsmUtils, Decompiler}

import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters

class Sample {
  private var exceptionCount = 0

  def hello(str: String, exception: Boolean): String = {
    if (exception) {
      exceptionCount += 1
      throw new RuntimeException("test exception, str: " + str)
    }
    "hello " + str
  }
}

object ByteKitDemoScala {

  @throws[Exception]
  def main(args: Array[String]): Unit = {
    AgentUtils.install
    val sample = new Sample
    val t = new Thread(new Runnable() {
      override def run(): Unit = {
        for (i <- 0 until 100) {
          try {
            TimeUnit.SECONDS.sleep(3)
            val result = sample.hello("" + i, (i % 3) == 0)
            println("call hello result: " + result)
          } catch {
            case e: Throwable =>
              println("call hello exception: " + e.getMessage)
          }
        }
      }
    })

    t.start()

    val interceptorClassParser = new DefaultInterceptorClassParser
    val processors = JavaConverters.asScalaIterator(
      interceptorClassParser.parse(classOf[PrintInterceptor]).iterator()
    )
    val classNode = AsmUtils.loadClass(classOf[Sample])
    println("before transform")
    val beforeBytes = AsmUtils.toBytes(classNode)
    println(Decompiler.decompile(beforeBytes))

    val methods = JavaConverters.asScalaIterator(classNode.methods.iterator())
    for (methodNode <- methods) {
      if (methodNode.name.equals("hello")) {
        val methodProcessor = new MethodProcessor(classNode, methodNode)
        for (interceptor <- processors) {
          interceptor.process(methodProcessor)
        }
      }
    }

    println("after transform")
    val bytes = AsmUtils.toBytes(classNode)
    println(Decompiler.decompile(bytes))
    TimeUnit.SECONDS.sleep(5)
    AgentUtils.reTransform(classOf[Sample], bytes)
    System.in.read
  }

}
