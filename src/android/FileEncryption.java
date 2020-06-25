package com.lateph.myapplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import org.apache.cordova.LOG;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaResourceApi;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.content.Context;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class encrypts and decrypts files using the Conceal encryption lib
 */
public class FileEncryption extends CordovaPlugin {

    private static final String TAG = "FileEncryption";

    public static final String ENCRYPT_ACTION = "encrypt";
    public static final String DECRYPT_ACTION = "decrypt";

//    private Context CONTEXT;
//    private Crypto CRYPTO;
//    private Entity ENTITY;
//
//    private OutputStream OUTPUT_STREAM;
//    private InputStream INPUT_STREAM;
//
//    private String FILE_NAME;
//    private Uri SOURCE_URI;
//    private File SOURCE_FILE;
//    private File TEMP_FILE;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        if (action.equals(ENCRYPT_ACTION) || action.equals(DECRYPT_ACTION)) {
            CordovaResourceApi resourceApi = webView.getResourceApi();

            String path = args.getString(0);
            String pass = args.getString(1);
            Uri normalizedPath = resourceApi.remapUri(Uri.parse(path));

            LOG.d(TAG, "normalizedPath: "+ normalizedPath.getPath().toString());

            this.cryptOp(normalizedPath.toString(), pass, action, callbackContext);

            return true;
        }

        return false;
    }

    private void cryptOp(String path, String password, String action, CallbackContext callbackContext) {
        // init crypto variables
//        this.initCrypto(path, password, callbackContext);

        // create output stream which encrypts the data as
        // it is written to it and writes out to the file
        try {
            if (action.equals(ENCRYPT_ACTION)) {
                // create encrypted output stream
                FileInputStream fis = new FileInputStream(path);
                FileOutputStream fos = new FileOutputStream(path.concat(".crypt"));
                byte[] key = ("GanTeng" + password).getBytes("UTF-8");
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                key = sha.digest(key);
                key = Arrays.copyOf(key,16);
                SecretKeySpec sks = new SecretKeySpec(key, "AES");

                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, sks);

                CipherOutputStream cos = new CipherOutputStream(fos, cipher);
                int b;
                byte[] d = new byte[8];
                while((b = fis.read(d)) != -1) {
                    cos.write(d, 0, b);
                }
                cos.flush();
                cos.close();
                fis.close();

                Uri uri = Uri.parse(path.concat(".crypt"));
                File file = new File(uri.getPath());
                callbackContext.success(file.getPath());

                // write to temp file
//                this.writeFile(INPUT_STREAM, cos, callbackContext);
            }
//            else if (action.equals(DECRYPT_ACTION)) {
//                // create decrypted input stream
//                InputStream decryptedInputStream = CRYPTO.getCipherInputStream(INPUT_STREAM, ENTITY);
//                // write to temp file
//                this.writeFile(decryptedInputStream, OUTPUT_STREAM, callbackContext);
//            }
//
//            // delete original file after write
//            boolean deleted = SOURCE_FILE.delete();
//            if (deleted) {
//                File src = TEMP_FILE;
//                File dst = new File(SOURCE_URI.getPath());
//
//                this.copyFile(src, dst);
//
//                callbackContext.success(dst.getPath());
//            } else {
//                callbackContext.error(1);
//            }
        }  catch (Exception e) {
            LOG.d(TAG, "initCrypto Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

//    private void initCrypto(String path, String password, CallbackContext callbackContext) {
//        if (path != null && path.length() > 0 && password != null && password.length() > 0) {
//            SOURCE_URI  = Uri.parse(path);
//            FILE_NAME = SOURCE_URI.getLastPathSegment();
//
//            CONTEXT = cordova.getActivity().getApplicationContext();
//
//            SOURCE_FILE = new File(SOURCE_URI.getPath());
//
//            try {
//                // initialize temp file
//                TEMP_FILE = File.createTempFile(FILE_NAME, null, CONTEXT.getExternalCacheDir());
//                // initialize output stream for temp file
//                OUTPUT_STREAM = new BufferedOutputStream(new FileOutputStream(TEMP_FILE));
//                // create input stream from source file
//                INPUT_STREAM = new FileInputStream(SOURCE_FILE);
//            } catch (FileNotFoundException e) {
//                LOG.d(TAG, "initCrypto FileNotFoundException: " + e.toString());
//                callbackContext.error(e.getMessage());
//                e.printStackTrace();
//            } catch (IOException e) {
//                LOG.d(TAG, "initCrypto IOException: " + e.toString());
//                callbackContext.error(e.getMessage());
//                e.printStackTrace();
//            }
//        } else {
//            LOG.d(TAG, "initCrypto error ");
//            callbackContext.error(2);
//        }
//    }
//
//    private void writeFile(InputStream inputStream, OutputStream outputStream, CallbackContext callbackContext) {
//        try {
//            // create new byte object with source file length
//            byte[] data = new byte[(int) SOURCE_FILE.length()];
//
//            // read contents of source file byte by byte
//            int buffer = 0;
//            while ((buffer = inputStream.read(data)) > 0) {
//                // write contents to encrypted output stream
//                outputStream.write(data, 0, buffer);
//                outputStream.flush();
//            }
//
//            LOG.d(TAG, "writeFile called ");
//
//            // close output stream
//            outputStream.close();
//            inputStream.close();
//        } catch (IOException e) {
//            LOG.d(TAG, "writeFile error: " + e.toString());
//            callbackContext.error(e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public void copyFile(File source, File dest) throws IOException {
//        InputStream in = new FileInputStream(source);
//        OutputStream out = new FileOutputStream(dest);
//
//        // Transfer bytes from in to out
//        byte[] buf = new byte[1024];
//        int len;
//        while ((len = in.read(buf)) > 0) {
//            out.write(buf, 0, len);
//        }
//        in.close();
//        out.close();
//
//        LOG.d(TAG, "copyFile called ");
//    }
}
