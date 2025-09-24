package com.cf.tech.utils;

import com.cf.zsdk.uitl.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileUtil {

    public static boolean writeDataToFile(String pData, File pFile, boolean pIsAppend) {
        LogUtil.d("FileUtil.writeDataToFile == > call");
        if (!checkFile(pFile)) {
            LogUtil.d("file has something wrong fail to write file");
            return false;
        }
        LogUtil.d("file is ok begin to write file");
        try (FileOutputStream fos = new FileOutputStream(pFile, pIsAppend)) {
            fos.write(pData.getBytes(Charset.defaultCharset()));
            fos.flush();
            return true;
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        return false;
    }

    private static boolean checkFile(File pFile) {
        LogUtil.d("FileUtil.checkFile == > call");
        if (pFile == null) {
            LogUtil.d("file is null");
            return false;
        }
        File parentFile = pFile.getParentFile();
        if (parentFile != null) {
            if (!parentFile.exists()) {
                boolean mkdirs = parentFile.mkdirs();
                if (!mkdirs) {
                    LogUtil.d("create parent file fail");
                    return false;
                }
            }else {
                LogUtil.d( "parent file exit" );
            }
        }
        if (!pFile.exists()) {
            try {
                boolean newFile = pFile.createNewFile();
                if (!newFile) {
                    LogUtil.d("fail to create a new file");
                    return false;
                }
                LogUtil.d("succeed to create a new file");
                return true;
            } catch (IOException pE) {
                pE.printStackTrace();
                LogUtil.e("fail to create a new file");
            }
        }
        return false;
    }
}
