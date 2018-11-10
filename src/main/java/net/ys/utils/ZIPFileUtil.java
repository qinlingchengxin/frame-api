package net.ys.utils;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

public class ZIPFileUtil {

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
}
