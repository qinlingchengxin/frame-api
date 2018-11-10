package net.ys.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩zip文件
 */
public class CompressZipFile {

    private File targetFile;

    public CompressZipFile(File target) {
        this.targetFile = target;
        if (this.targetFile.exists()) {
            this.targetFile.delete();
        }
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

    public static void main(String[] args) {
        File f = new File("E:/test/");
        new CompressZipFile(new File("E:/", System.currentTimeMillis() + ".zip")).zipFiles(f);
    }
}