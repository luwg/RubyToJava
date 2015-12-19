import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine engine = manager.getEngineByName("ruby");

        engine.eval(new BufferedReader(new FileReader("ruby/TempConverter.rb")));

        Invocable invocable = (Invocable) engine;
        Object tempconverter = invocable.invokeFunction("getTempConverter");

        double degreesCelsius = (Double) invocable.invokeMethod(tempconverter,"f2c", 98.6);
        System.out.println(degreesCelsius);

        double degreesFahrenheit = (Double) invocable.invokeMethod(tempconverter,"c2f",100.0);
        System.out.println(degreesFahrenheit);

/*            JrubyUtil jruby = new JrubyUtil();
            Object result = jruby.getInstance(Message.class);
            System.out.println(((Message)result).sayHello());*/
    }
}