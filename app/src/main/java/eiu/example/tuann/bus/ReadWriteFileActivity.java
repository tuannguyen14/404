package eiu.example.tuann.bus;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by tuann on 7/12/2017.
 */

public class ReadWriteFileActivity extends AppCompatActivity {

    final static String path = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static String loadFile(Context context, String fileName, String folder) {
        String line = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(path + folder + fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        return line;
    }

    public static boolean saveToFile(String fileName, String data, String folder) {
        try {
            new File(path + folder).mkdir();
            File file = new File(path + folder + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());
            return true;
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        return false;
    }

    public static boolean deleteFile(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteFile(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static boolean isFolderEmpty() {
        File dir = new File(path);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children.length == 0) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
}
