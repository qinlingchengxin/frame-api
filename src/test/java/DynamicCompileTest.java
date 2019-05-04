import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;

public class DynamicCompileTest {
    public static void main(String[] args) throws Exception {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager javaFileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable it = javaFileManager.getJavaFileObjects(new File("E:/test/User.java"));
        CompilationTask task = javaCompiler.getTask(new FileWriter("E:/test/User.class"), javaFileManager, null, null, null, it);
        task.call();
        javaFileManager.close();
    }
}