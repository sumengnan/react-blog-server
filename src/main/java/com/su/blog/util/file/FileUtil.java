package com.su.blog.util.file;

import com.su.blog.util.exception.AppRuntimeException;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: FileUtil
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/15 22:30
 * @Version:
 */
@Slf4j
public class FileUtil {


    //逐行读取文件内容返回内容列表
    public static List<String> readLine(String path) {
        List<String> list = new ArrayList<>();
        try {
            //@Cleanup lombok用法 自动关闭IO
            @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    list.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    //读取文件内容并返回
    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        //new File对象
        File file = new File(fileName);
        //获取文件长度
        Long filelength = file.length();
        //获取同长度的字节数组
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(filecontent);
            return new String(filecontent, encoding);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //向文件里写入内容
    public static void saveAsFileWriter(String path, String content) {
        File file = new File(path);
        //如果目录不存在 创建目录
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fileWriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fileWriter = new FileWriter(path, false);
            fileWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        } else if (file.delete()) {
            return true;
        }
        return false;
    }

    public static void export(HttpServletResponse response, String filePath, String fileName) {
        InputStream in = null;
        OutputStream out = null;
        response.setContentType("application/md;charset=UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            in = new FileInputStream(filePath);
            int len = 0;
            byte[] bytes = new byte[1024];
            response.setCharacterEncoding("UTF-8");
            out = response.getOutputStream();
            while ((len = in.read(bytes)) > 0) {
                out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("UnsupportedEncodingException：" + filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("文件未找到：" + filePath);
        } catch (IOException e) {
            log.error("IOException" + filePath);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                throw new AppRuntimeException("失败");
            }
        }
    }
}
