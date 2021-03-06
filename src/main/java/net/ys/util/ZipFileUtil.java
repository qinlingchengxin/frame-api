package net.ys.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileUtil {

    private File targetFile;

    public ZipFileUtil(File target) {
        this.targetFile = target;
        if (this.targetFile.exists()) {
            this.targetFile.delete();
        }
    }

    /**
     * 压缩文件/文件夹
     *
     * @param srcPathName 需要被压缩的文件/文件夹
     * @param endPathName 生成文件全路径
     */
    public static void compressFile(String srcPathName, String endPathName) {
        File srcDir = new File(srcPathName);

        if (!srcDir.exists()) {
            throw new RuntimeException("file not exist!");
        }

        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(new File(endPathName));
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(srcDir);
        zip.addFileset(fileSet);
        zip.execute();

    }

    /**
     * 压缩文件
     *
     * @param srcFile
     */
    public boolean zipFiles(File srcFile) {

        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(this.targetFile));

            if (srcFile.isFile()) {
                zipFile(srcFile, out, "");
            } else {
                File[] list = srcFile.listFiles();
                for (int i = 0; i < list.length; i++) {
                    compress(list[i], out, "");
                }
            }
            return true;
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * 压缩文件夹里的文件
     *
     * @param file
     * @param out
     * @param basedir
     */
    private void compress(File file, ZipOutputStream out, String basedir) {
        if (file.isDirectory()) {
            this.zipDirectory(file, out, basedir);
        } else {
            this.zipFile(file, out, basedir);
        }
    }

    /**
     * 压缩单个文件
     *
     * @param srcFile
     */
    public void zipFile(File srcFile, ZipOutputStream out, String basedir) {
        if (!srcFile.exists()) {
            return;
        }

        byte[] buf = new byte[1024];
        FileInputStream in = null;

        try {
            int len;
            in = new FileInputStream(srcFile);
            out.putNextEntry(new ZipEntry(basedir + srcFile.getName()));

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                if (out != null) {
                    out.closeEntry();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * 压缩文件夹
     *
     * @param dir
     * @param out
     * @param basedir
     */
    public void zipDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            compress(file, out, basedir + dir.getName() + "/");
        }
    }

    /**
     * 读取zip文件并拷贝到指定目录
     *
     * @param srcPath
     * @param desPath
     */
    public static void readZipFile(String srcPath, String desPath) {
        FileInputStream fileInputStream = null;
        ZipInputStream zin = null;
        ZipFile zf = null;
        try {
            zf = new ZipFile(srcPath);
            Charset gbk = Charset.forName("gbk");
            fileInputStream = new FileInputStream(srcPath);
            zin = new ZipInputStream(fileInputStream, gbk);
            ZipEntry ze;
            String tempPath;
            while ((ze = zin.getNextEntry()) != null) {
                tempPath = desPath + "/" + ze.toString();
                File file = new File(tempPath);
                if (tempPath.endsWith("/")) {
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    continue;
                }
                InputStream stream = zf.getInputStream(ze);
                FileUtils.copyInputStreamToFile(stream, file);
            }
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                IOUtils.closeQuietly(fileInputStream);
                if (zin != null) {
                    zin.closeEntry();
                    zin.close();
                }
                if (zf != null) {
                    zf.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) {
        File f = new File("E:/test/");
        new ZipFileUtil(new File("E:/", System.currentTimeMillis() + ".zip")).zipFiles(f);
    }
}
