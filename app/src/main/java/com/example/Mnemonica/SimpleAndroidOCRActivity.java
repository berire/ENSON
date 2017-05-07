package com.example.Mnemonica;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class SimpleAndroidOCRActivity extends Activity {
    public static final String PACKAGE_NAME = "com.datumdroid.android.ocr.simple";
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";

    // You should have the trained data file in assets folder
    // You can get them at:
    // https://github.com/tesseract-ocr/tessdata
    public static final String lang = "eng";
    public String recognizedText;

    private static final String TAG = "SimpleAndroidOCR.java";

    protected Button _buttonT;
    protected Button _buttonG;
    protected Button _buttonO;

    protected ImageView _image;
    protected EditText _field;
    protected String _path;
    protected boolean _taken;
    Bitmap bitmap;
    Bitmap bitmapA;
    protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phototext);

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }


        _image = (ImageView) findViewById(R.id.image11);
        _field = (EditText) findViewById(R.id.field);

        _buttonT = (Button) findViewById(R.id.button_take);
        _buttonT.setOnClickListener(new ButtonClickHandler());

        _buttonG = (Button) findViewById(R.id.button_gal);
        _buttonG.setOnClickListener(new ButtonClickHandler());

        _path = DATA_PATH + "/ocr.jpg";
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.button_gal:
                    FromCard();
                    break;
                case R.id.button_take:
                    FromCamera();
                    break;
            }

        }
    }

    // Simple android photo capture:
    // http://labs.makemachine.net/2010/03/simple-android-photo-capture/


    public void FromCamera() {
        startCameraActivity();
    }

    public void FromCard() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 2);
    }


    protected void startCameraActivity() {
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*Log.i(TAG, "resultCode: " + resultCode);

        if (resultCode == -1) {
            onPhotoTaken();
        } else {
            Log.v(TAG, "User cancelled");
        }*/

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bitmapA = BitmapFactory.decodeFile(picturePath);
            _image.setImageBitmap(bitmapA);
            CNVR(bitmapA);

        } else {

            switch (resultCode) {
                case 0:
                    Log.i("SonaSys", "User cancelled");
                    break;
                case -1:
                    onPhotoTaken();
                    break;

            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SimpleAndroidOCRActivity.PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(SimpleAndroidOCRActivity.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

    protected void onPhotoTaken() {
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        bitmap = BitmapFactory.decodeFile(_path, options);

        try {
            ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        _image.setImageBitmap( bitmap );
        // Cycle done.

        CNVR(bitmap);
    }

    public void CNVR(Bitmap bm)
    {
        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bm);
        String anothertext=baseApi.getUTF8Text();
        recognizedText = baseApi.getHOCRText(0);

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.


        ArrayList<String> words=new ArrayList<>();
        Pattern pattern = Pattern.compile("<span class='ocrx_word'(.*?)</span>");
        Matcher matcher = pattern.matcher(recognizedText);
        while (matcher.find()) {

            words.add(matcher.group());
        }
        ArrayList<String> sizes=new ArrayList<>();
        ArrayList<String> cntxs=new ArrayList<>();
        for(int i=0;i<words.size();i++)
        {
            Pattern pattern1 = Pattern.compile("title='bbox(.*?);");
            Matcher matcher1 = pattern1.matcher(words.get(i));
            while (matcher1.find()) {

                sizes.add(matcher1.group());
            }

            Pattern pattern2 = Pattern.compile("<strong>(.*?)</strong>");
            Matcher matcher2 = pattern2.matcher(words.get(i));
            while (matcher2.find()) {

                cntxs.add(matcher2.group());
            }
        }

        for(int i=0;i<sizes.size();i++)
        {
            String rm=sizes.get(i).replaceAll("title='bbox","");
            rm=rm.replaceAll(";","");
            sizes.set(i,rm);
        }

        for(int i=0;i<cntxs.size();i++)
        {
            String rm=cntxs.get(i).replaceAll("</strong>","");
            rm=rm.replaceAll("<strong>","");
            cntxs.set(i,rm);
        }


        ArrayList<Word> ALL=new ArrayList<>();
        for(int i=0;i<words.size();i++)
        {
            Word aword=new Word();
            String[] splited = sizes.get(i).split("\\s+");
            ArrayList<String> slp=new ArrayList<>();
            int cv=0;
            while(cv<splited.length)
            {
                slp.add(splited[cv]);
                cv++;
            }
            for(int g=0;g<slp.size();g++)
            {
                if(slp.get(g).length()==0 || slp.get(g).length()==1)
                {
                    slp.remove(g);
                }

            }
            aword.setStartx(Integer.parseInt(slp.get(0)));
            aword.setStarty(Integer.parseInt(slp.get(1)));
            aword.setEndx(Integer.parseInt(slp.get(2)));
            aword.setEndy(Integer.parseInt(slp.get(3)));
            if(i<cntxs.size())
                aword.setcontext(cntxs.get(i));
            ALL.add(aword);
        }

        //KELIMELERI DUZELTME

       for(int i=0;i<ALL.size();i++)
        {
            if(ALL.get(i).getcontext()!=null && ALL.get(i)!=null )
            {
                if ( lang.equalsIgnoreCase("eng") ) {

                    String str=ALL.get(i).getcontext().replaceAll("[^a-zA-Z0-9]+", " ");

                    if(str!=null)
                        ALL.get(i).setcontext(str);
                }

                ALL.get(i).setcontext(ALL.get(i).getcontext().trim());
            }
        }



        ArrayList<String> Lectures=new ArrayList<>();

        String [] code={"CS","ENG","GE","MATH","MBG","TURK","HIST","HUM","PHYS","EEE","IE","FRE"};

        int cnt=0;
        for(int i=0,g=0;i<ALL.size()-1&&g<code.length;i++)
        {
            if(ALL.get(i).getcontext()!=null)
                {
                    if(ALL.get(i).getcontext().equals(code[cnt]) || ALL.get(i).getcontext().equals(code[cnt].toLowerCase()) || ALL.get(i).getcontext().contains(code[cnt]) || ALL.get(i).getcontext().contains(code[cnt].toLowerCase()))
                    {
                        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        String aLEC=ALL.get(i).getcontext()+" "+ALL.get(i+1).getcontext();
                        Lectures.add(aLEC);
                    }
                }
                g++;
        }

        /*Set<String> hs = new HashSet<>();
        hs.addAll(Lectures);
        Lectures.clear();
        Lectures.addAll(hs);*/

        String ap="EMPTY";
        for(int g=0;g<ALL.size();g++)
        {

            if(Lectures.get(g)!=null)
            {
                ap=ap+ "A LECTURE: " + Lectures.get(g)+" ";
            }
        }


        Toast.makeText(SimpleAndroidOCRActivity.this, ap,
                Toast.LENGTH_SHORT).show();

        _field.setText(ap);




        // CS,ENG,GE,MATH,MBG,TURK,HIST,HUM,PHYS,EEE,IE,FRE



        // 13 485 29 497   startx, starty, endx, endy
        //<span class='ocrx_word' id='word_1_114' title='bbox 13 485 29 497; x_wconf 95'><strong>16</strong></span>
        // Log.v(TAG, "OCRED TEXT: " + recognizedText);





        /*if ( recognizedText.length() != 0 ) {
            _field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
            _field.setSelection(_field.getText().toString().length());
        }*/






    }



    // www.Gaut.am was here
    // Thanks for reading!


}
