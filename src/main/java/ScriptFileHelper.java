import junit.framework.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 *
 * ��ȡ�ű��ļ�,�ʺϸ��ֲ�ͬ��׺�ű�
 */
public class ScriptFileHelper {
    private final static String SCRIPT_HOME = "SCRIPT_HOME";
    private final static String SEPARATOR = System.getProperty("file.separator");
    private final static String JRUBY_SUFFIX = ".rb";

    /**
     * �����ļ���������Դ�ļ�
     * ���ҷ�Χ�����ȼ�:
     *     1. classesĿ¼�µ�fileName;
     *     2. ��������SCRIPT_HOME�µ�filaName��
     *     3. ��ǰ�����ռ���resourcesĿ¼�µ�filaName;
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
     * ����������ȡ�ű��ļ�
     * ���ҷ�Χ�����ȼ�:
     *     1. impl����Ŀ¼��ͬ���Ľű�;
     *     2. classesĿ¼�µ�fileName;
     */
    public static ScriptFile getScriptFileOnImpl(Class<?> impl, String suffix) {
        ScriptFile scriptFile = null;
        try {
            // ��ȡ��ǰ����ͬ�����ļ�
            String implName = impl.getSimpleName() + suffix;
            URL resource = impl.getResource(implName);
            if (resource == null) {
                // ��ȡclasses��ͬ�����ļ�
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
     * 1. ����ȫ·�����ؽű��ļ�
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
     * ��������,�ű�������,��ȡ�ű��ļ�
     * ���ҷ�Χ�����ȼ�:
     */
    public static ScriptFile getJrubyScriptFile(Class<?> impl, String engineName) {
        ScriptFile reslut = null;
        if (engineName.toUpperCase().equals(SuperScriptEngineFactory.JRUBY_ENGINE_NAME)) {
            reslut = getScriptFileOnImpl(impl, JRUBY_SUFFIX);
        }
        return reslut;
    }
}
