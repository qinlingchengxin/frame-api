package net.ys.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类相关的工具类
 */
public class ClassUtil {

    public static void main(String[] args) throws Exception {
        List<Class> classes = ClassUtil.getClasses("net.ys.controller");
        for (Class clazz : classes) {
            System.out.println(clazz.getName());
        }
    }

    /**
     * 取得某个接口下所有实现这个接口的类
     *
     * @param c
     * @return
     */
    public static List<Class> getAllClassByInterface(Class c) {
        List<Class> returnClassList = null;

        if (c.isInterface()) {
            String packageName = c.getPackage().getName();// 获取当前的包名
            List<Class> allClass = getClasses(packageName);// 获取当前包下以及子包下所以的类
            if (allClass != null) {
                returnClassList = new ArrayList<Class>();
                for (Class classes : allClass) {
                    if (c.isAssignableFrom(classes)) { // 判断是否是同一个接口
                        if (!c.equals(classes)) {// 本身不加入进去
                            returnClassList.add(classes);
                        }
                    }
                }
            }
        }
        return returnClassList;
    }

    /**
     * 取得某一类所在包的所有类名 不含迭代
     *
     * @param classLocation
     * @param packageName
     * @return
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        String[] packagePathSplit = packageName.split("[.]");//将packageName分解
        String realClassLocation = classLocation;
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++) {
            realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
        }
        File packageDir = new File(realClassLocation);
        if (packageDir.isDirectory()) {
            String[] allClassName = packageDir.list();
            return allClassName;
        }
        return null;
    }

    /**
     * 从包package中获取所有的Class
     */
    public static List<Class> getClasses(String packageName) {

        List<Class> classes = new ArrayList<Class>();//第一个class类的集合
        boolean recursive = true;//是否循环迭代
        String packageDirName = packageName.replace('.', '/'); //获取包的名字 并进行替换
        Enumeration<URL> dirs; //定义一个枚举的集合 并进行循环来处理这个目录下的things
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");//获取包的物理路径
                    //以文件的方式扫描整个包下的文件 并添加到集合中  
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    //如果是jar包文件   
                    //定义一个JarFile  
                    JarFile jar;
                    try {
                        //获取jar  
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        //从此jar包 得到一个枚举类  
                        Enumeration<JarEntry> entries = jar.entries();
                        //同样的进行循环迭代  
                        while (entries.hasMoreElements()) {
                            //获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件  
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            //如果是以/开头的  
                            if (name.charAt(0) == '/') {
                                //获取后面的字符串  
                                name = name.substring(1);
                            }
                            //如果前半部分和定义的包名相同  
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                //如果以"/"结尾 是一个包  
                                if (idx != -1) {
                                    //获取包名 把"/"替换成"."  
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                //如果可以迭代下去 并且是一个包  
                                if ((idx != -1) || recursive) {
                                    //如果是一个.class文件 而且不是目录  
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        //去掉后面的".class" 获取真正的类名  
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            //添加到classes  
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class> classes) {
        //获取此包的目录 建立一个File  
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回  
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录  
        File[] dirFiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件  
        for (File file : dirFiles) {
            //如果是目录 则继续扫描  
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名  
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    //添加到集合中去  
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}  