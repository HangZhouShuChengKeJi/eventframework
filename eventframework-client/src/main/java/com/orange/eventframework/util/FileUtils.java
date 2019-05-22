package com.orange.eventframework.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author maomao
 * @date 2019/5/22
 */
public class FileUtils {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final long MAX_STATIC_FILE_SIZE = 20971520L;
    public static final int BUFFER_SIZE = 20480;

    public FileUtils() {
    }

    public static String getFileContent(String basePath, String fileName) throws IOException {
        return getFileContent(getFile(basePath, fileName));
    }

    public static void deleteQuietly(String... file) {
        String[] var1 = file;
        int var2 = file.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String f = var1[var3];
            if (StringUtils.isNotBlank(f)) {
                File ff = new File(f);
                if (ff != null) {
                    ff.delete();
                }
            }
        }

    }

    public static void deleteQuietly(File... file) {
        File[] var1 = file;
        int var2 = file.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            File f = var1[var3];
            if (f != null) {
                f.delete();
            }
        }

    }

    public static String getFileContent(File file) throws IOException {
        List<String> list = getFileLineList(file);
        if (null == list) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder(80);

            for(int i = 0; i < list.size(); ++i) {
                sb.append((String)list.get(i)).append(LINE_SEPARATOR);
            }

            return sb.toString();
        }
    }

    public static List<String> getFileLineList(String basePath, String fileName) throws IOException {
        return getFileLineList(getFile(basePath, fileName));
    }

    public static List<String> getFileLineList(File file) throws IOException {
        if (null != file && file.exists() && file.isFile()) {
            if (file.length() > 20971520L) {
                return null;
            } else {
                List<String> list = new ArrayList();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = null;

                while((line = br.readLine()) != null) {
                    list.add(line);
                }

                close((Reader)br);
                return list;
            }
        } else {
            return null;
        }
    }

    public static boolean writeFile(String basePath, String fileName, List<String> lines, boolean append) throws IOException {
        StringBuilder sBuilder = new StringBuilder(1000);
        if (CollectionUtils.isNotEmpty(lines)) {
            for(int i = 0; i < lines.size(); ++i) {
                sBuilder.append((String)lines.get(i)).append(LINE_SEPARATOR);
            }
        }

        return writeFile(basePath, fileName, sBuilder.toString(), append);
    }

    public static boolean writeFile(String basePath, String fileName, String content, boolean append) throws IOException {
        if (null != content && (long)content.length() <= 20971520L) {
            if (!createFolder(basePath)) {
                return false;
            } else {
                File file = createFile(basePath, fileName);
                if (null == file) {
                    return false;
                } else {
                    BufferedWriter write = new BufferedWriter(new FileWriter(file, append));
                    write.write(content);
                    write.flush();
                    close((Writer)write);
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public static File getFile(String basePath, String fileName) {
        if (null != fileName && fileName.length() != 0) {
            File file;
            if (null != basePath && basePath.length() != 0) {
                file = new File(basePath, fileName);
            } else {
                file = new File(fileName);
            }

            return file;
        } else {
            return null;
        }
    }

    public static File createFile(String basePath, String fileName) {
        File file = getFile(basePath, fileName);
        if (file.exists()) {
            return !file.isFile() ? null : file;
        } else {
            try {
                return !file.createNewFile() ? null : file;
            } catch (IOException var4) {
                return null;
            }
        }
    }

    public static boolean createFolder(String path) {
        if (null != path && path.length() != 0) {
            File folder = new File(path);
            if (folder.exists()) {
                if (folder.isDirectory()) {
                    return true;
                }

                if (!folder.renameTo(new File(folder.getParent(), folder.getName() + "." + System.currentTimeMillis()))) {
                    return false;
                }

                folder = new File(path);
            }

            return folder.mkdirs();
        } else {
            return false;
        }
    }

    public static boolean isFileAndExist(File srcFile) {
        return null != srcFile && srcFile.exists() && srcFile.isFile();
    }

    public static void close(Reader reader) throws IOException {
        if (null != reader) {
            reader.close();
        }

    }

    public static void close(InputStream reader) throws IOException {
        if (null != reader) {
            reader.close();
        }

    }

    public static void close(Writer writer) throws IOException {
        if (null != writer) {
            writer.close();
        }

    }

    public static void close(OutputStream writer) throws IOException {
        if (null != writer) {
            writer.close();
        }

    }

    public static int createFile(InputStream stream, String path, String filename) throws IOException {
        FileOutputStream fs = new FileOutputStream(new File(path, filename));
        byte[] buffer = new byte[1048576];
        int bytesum = 0;
        boolean var6 = false;

        int byteread;
        while((byteread = stream.read(buffer)) != -1) {
            bytesum += byteread;
            fs.write(buffer, 0, byteread);
            fs.flush();
        }

        close((OutputStream)fs);
        close(stream);
        return bytesum;
    }

    public static void makeSureFileExists(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            f.mkdirs();
        }

    }
}

