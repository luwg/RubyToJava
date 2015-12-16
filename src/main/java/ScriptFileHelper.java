import junit.framework.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 *
 * 读取脚本文件,适合各种不同后缀脚本
 */
public class ScriptFileHelper {
    private final static String SCRIPT_HOME = "SCRIPT_HOME";
    private final static String SEPARATOR = System.getProperty("file.separator");
    private final static String JRUBY_SUFFIX = ".rb";

    /**
     * 根据文件名读入资源文件
     * 查找范围及优先级:
     *     1. classes目录下的fileName;
     *     2. 环境变量SCRIPT_HOME下的filaName；
     *     3. 当前工作空间下resources目录下的filaName;
     */
    public static ScriptFile getScriptFileOnFileName(String fileName) {
        ScriptFile scriptFile = null;
        try {
            URL resource = ScriptFileHelper.class.getResource("/" + fileName);
            if (resource != null) {
                scriptFile = new ScriptFile(new File(resource.toURI()));
            }
            if (scriptFile == null) {
                String[] paths = {
                        System.getProperty(SCRIPT_HOME) + fileName,
                        System.getProperty("user.dir") + SEPARATOR + "scriptfiles" + SEPARATOR + fileName};
                for (String path : paths) {
                    System.out.println(path);
                    if ((new File(path)).isFile()) {
                        scriptFile = new ScriptFile(new File(path));
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getStackTrace());
        } catch (URISyntaxException e) {
            System.out.println(e.getStackTrace());
        }
        Assert.assertNotNull(scriptFile);
        return scriptFile;
    }

    /**
     * 根据类名读取脚本文件
     * 查找范围及优先级:
     *     1. impl所在目录下同名的脚本;
     *     2. classes目录下的fileName;
     */
    public static ScriptFile getScriptFileOnImpl(Class<?> impl, String suffix) {
        ScriptFile scriptFile = null;
        try {
            // 读取当前包下同名的文件
            String implName = impl.getSimpleName() + suffix;
            URL resource = impl.getResource(implName);
            if (resource == null) {
                // 读取classes下同名的文件
                resource = impl.getResource("/" + implName);
            }
            if (resource != null) {
                scriptFile = new ScriptFile(new File(resource.toURI()));
            }
        } catch (URISyntaxException e) {
            System.out.println(e.getStackTrace());
        } catch (FileNotFoundException e) {
            System.out.println(e.getStackTrace());
        }
        Assert.assertNotNull(scriptFile);
        return scriptFile;
    }

    /**
     * 1. 根据全路径加载脚本文件
     */
    public static ScriptFile getScriptFileOnFullPath(String fullPath) {
        ScriptFile scriptFile = null;
        try {
            if ((new File(fullPath)).isFile()) {
                scriptFile = new ScriptFile(new File(fullPath));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(scriptFile);
        return scriptFile;
    }

    /**
     * 根据类名,脚本引擎名,读取脚本文件
     * 查找范围及优先级:
     */
    public static ScriptFile getJrubyScriptFile(Class<?> impl, String engineName) {
        ScriptFile reslut = null;
        if (engineName.toUpperCase().equals(SuperScriptEngineFactory.JRUBY_ENGINE_NAME)) {
            reslut = getScriptFileOnImpl(impl, JRUBY_SUFFIX);
        }
        return reslut;
    }
}
