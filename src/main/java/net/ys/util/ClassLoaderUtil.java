package net.ys.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ClassLoaderUtil extends ClassLoader {

    private static volatile ClassLoaderUtil instance;

    private ClassLoaderUtil() {
    }

    public static synchronized ClassLoaderUtil getInstance() {
        if (instance == null) {
            synchronized (ClassLoaderUtil.class) {
                if (instance == null) {
                    instance = new ClassLoaderUtil();
                }
            }
        }
        return instance;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] data = this.loadClassData(name);
        return this.defineClass(name, data, 0, data.length);
    }

    private byte[] loadClassData(String fullName) {
        try {
            String path = ClassLoaderUtil.class.getClassLoader().getResource("").getPath();
            fullName = fullName.replaceAll("\\.", "/");
            FileInputStream is = new FileInputStream(new File(path + fullName + ".class"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b;
            while ((b = is.read()) != -1) {
                bos.write(b);
            }
            is.close();
            return bos.toByteArray();
        } catch (Exception e) {
        }
        return null;
    }
}
