import org.apache.commons.lang3.StringUtils;

import javax.script.*;
import java.io.*;


public class JrubyUtil {
    private LoadedScriptFile loaded = new LoadedScriptFile(
            SuperScriptEngineFactory.JRUBY_ENGINE_NAME);

    public Object getInstance(Class<?> impl) {
        return loaded.getInstance(
                ScriptFileHelper.getJrubyScriptFile(impl, SuperScriptEngineFactory.JRUBY_ENGINE_NAME), impl);
    }

    public Object getInstance(Class<?> impl, String fun) {
        return loaded.getInstance(
                ScriptFileHelper.getJrubyScriptFile(impl, SuperScriptEngineFactory.JRUBY_ENGINE_NAME), impl, fun);
    }
}

/**
 * 封装rs-223的实现,适用于各种脚本语言
 */
class SuperScriptEngine {
    protected ScriptEngine engine;

    /**
     *
     * @param name 所需要使用引擎名称
     */
    public SuperScriptEngine(String name) {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName(name);
        System.out.println(engine.getFactory().getEngineName());
    }

    /**
     *
     * @param in 脚本文件的文件流
     * @return   编译完成的脚本对象
     */
    public CompiledScript compiled(InputStream in) {
        CompiledScript compileScript = null;
        try {
            if (engine instanceof Compilable) {
                Compilable compile = (Compilable) engine;
                compileScript = compile.compile(
                        new InputStreamReader(in));
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return compileScript;
    }

    /**
     * @param impl	脚本所需实现的接口
     * @param in	脚本文件的文件流
     * @param fun	脚本中负责初始化的函数名
     * @return  	返回接口实现的对象
     * <p>fun函数最后必须返回实现的对象,如果为空则脚本结束的返回值必须返回对象</p>
     */
    public Object getInterface(Class<?> impl, InputStream in, String fun) {
        Object interfaceInstance = null;
        Object result = null;
        try {
            if (engine instanceof Invocable) {
                result = engine.eval(
                        new InputStreamReader(in));
                Invocable invocable = (Invocable) engine;
                if (StringUtils.isBlank(fun)) {
                    interfaceInstance = invocable.getInterface(result, impl);
                } else {
                    result = invocable.invokeFunction(fun);
                    interfaceInstance = invocable.getInterface(result, impl);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return interfaceInstance;
    }

    /**
     *
     * @param impl	脚本所需实现的接口
     * @param in	脚本文件的文件流
     * @return  返回接口实现的对象
     * <p>脚本中最后必须返回实现的对象</p>
     */
    public Object getInterface(Class<?> impl, InputStream in) {
        return getInterface(impl, in, "");
    }

    /**
     * @param impl			脚本所需实现的接口
     * @param compileScript	编译后的脚本对象
     * @param fun			脚本中负责初始化的函数名
     * @return  			返回接口实现的对象
     * <p>fun函数最后必须返回实现的对象,如果为空则脚本结束的返回值必须返回对象</p>
     */
    public Object getInterface(Class<?> impl, CompiledScript compileScript, String fun) {
        Object interfaceInstance = null;
        Object result = null;
        try {
            ScriptEngine engine = compileScript.getEngine();
            if (engine instanceof Invocable) {
                result = compileScript.eval();
                Invocable invocable = (Invocable) engine;
                if(StringUtils.isBlank(fun)) {
                    interfaceInstance = invocable.getInterface(result, impl);
                } else {
                    result = invocable.invokeFunction(fun);
                    interfaceInstance = invocable.getInterface(result, impl);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return interfaceInstance;
    }

    /**
     * @param  	脚本所需实现的接口
     * @param 	编译后的脚本对象
     * @return  返回接口实现的对象
     * <p>脚本中最后必须返回实现的对象</p>
     */
    public Object getInterface(Class<?> impl, CompiledScript compileScript) {
        return getInterface(impl, compileScript, null);
    }

    /**
     * @param str 文本形式的脚本代码
     * @return    执行后脚本的返回值
     */
    public Object eval(String str) {
        Object result = null;
        try {
            result = engine.eval(str);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param str     脚本代码
     * @param context 脚本引擎上下文
     * @return        执行后脚本的返回值
     */
    public Object eval(String str, ScriptContext context) {
        Object result = null;
        try {
            result = engine.eval(str, context);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param in 脚本文件流
     * @return   执行后脚本的返回值
     */
    public Object eval(InputStream in) {
        Object result = null;
        try {
            result = engine.eval(new InputStreamReader(in));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param in 		脚本文件流
     * @param context 	脚本引擎上下文
     * @return   		执行后脚本的返回值
     */
    public Object eval(InputStream in, ScriptContext context) {
        Object result = null;
        try {
            result = engine.eval(new InputStreamReader(in), context);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return result;
    }
}

/**
 * 封装rs-223的实现,适用于各种脚本语言,jruby引擎的实现
 */
class JrubySuperScriptEngine extends SuperScriptEngine {
    private static JrubySuperScriptEngine jrEngine;

    synchronized public static JrubySuperScriptEngine getInstance() {
        if (jrEngine == null) {
            System.setProperty("org.jruby.embed.compilemode", "jit");
            jrEngine = new JrubySuperScriptEngine("jruby");
        }
        return jrEngine;
    }

    public JrubySuperScriptEngine(String name) {
        super(name);
    }
}

/**
 * 脚本引擎工厂类,负责实例化脚本引擎对象
 */
class SuperScriptEngineFactory {
    public final static String JRUBY_ENGINE_NAME = "JRUBY";

    synchronized public static SuperScriptEngine getInstance(String engineName) {
        SuperScriptEngine instance = null;
        if (engineName.toUpperCase().equals(JRUBY_ENGINE_NAME)) {
            instance = JrubySuperScriptEngine.getInstance();
        }
        return instance;
    }
}

/**
 * 脚本文件封装
 */
class ScriptFile {
    /** 脚本文件名称  */
    protected String name;
    /** 脚本的hash值  */
    protected int hash;
    /** 脚本的路径       */
    protected String path;
    /** 脚本的文件流  */
    protected InputStream in;

    public ScriptFile(File file) throws FileNotFoundException {
        in	 = new FileInputStream(file);
        name = file.getName();
        path = file.getPath();
        hash = file.hashCode();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getHash() {
        return hash;
    }

    public InputStream getInputStream() {
        return in;
    }
}

/**
 * 脚本文件管理
 */
class LoadedScriptFile {
    public final static String DEFAULT_GET_INSTANCE = "getInstance";

    protected SuperScriptEngine engine;
    protected Class<?> impl;

    public LoadedScriptFile(String engineName) {
        this.engine = SuperScriptEngineFactory.getInstance(engineName);
    }

    public SuperScriptEngine getEngine() {
        return engine;
    }

    public Object getInstance(ScriptFile scriptFile, Class<?> impl, String fun) {
        Object instance = null;
        instance = engine.getInterface(impl,
                scriptFile.getInputStream(), fun);
        return instance;
    }

    public Object getInstance(ScriptFile scriptFile, Class<?> impl) {
        return getInstance(scriptFile, impl, "");
    }
}
