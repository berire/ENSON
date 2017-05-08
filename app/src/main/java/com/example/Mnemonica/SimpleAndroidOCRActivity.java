package com.example.Mnemonica;
import java.io.ByteArrayOutputStream;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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

import static java.lang.Math.abs;

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
    public ArrayList<Word> lectures=new ArrayList<>();

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

        String ou_path=_path;
        String ou_path2=_path;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 4;

        int dstWidth=1000;
        int dstHeight=1000;
        try
        {
            int inWidth = 0;
            int inHeight = 0;
            InputStream in = new FileInputStream(_path);
            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options1);
            in.close();
            in = null;
            // save width and height
            inWidth = options1.outWidth;
            inHeight = options1.outHeight;
            // decode full image pre-resized
            in = new FileInputStream(_path);
            Bitmap roughBitmap = BitmapFactory.decodeStream(in);
            roughBitmap=resize(roughBitmap, inWidth*2, inHeight*2);///YENİİİİİİİİİİİİİİİİİİİİİİİİİİİİİİİİ
            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            ///////////
            try
            {
                Toast.makeText(SimpleAndroidOCRActivity.this, "BITMAP RESIZED",
                        Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                FileOutputStream out = new FileOutputStream(ou_path);
                roughBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
                roughBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
                byte[] byteArray = stream.toByteArray();

                ////////////////////////////////////
                /*PlanarYUVLuminanceSource pl= new PlanarYUVLuminanceSource(byteArray, (int)inWidth, (int)inHeight,(int) inRect.left, (int)inRect.top,
                        (int)inRect.width(), (int)inRect.height(), false);
                Bitmap xl=pl.renderCroppedGreyscaleBitmap();
                bitmap=xl;*/







            }
            catch (Exception e)
            {
                Log.e("Image", e.getMessage(), e);
                Toast.makeText(SimpleAndroidOCRActivity.this, "here",
                        Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e)
        {
            Log.e("Image", e.getMessage(), e);
        }

        bitmap = BitmapFactory.decodeFile(ou_path, options);

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
            if(slp.size()>=4)
            {
                aword.setStartx(Integer.parseInt(slp.get(0)));
                aword.setStarty(Integer.parseInt(slp.get(1)));
                aword.setEndx(Integer.parseInt(slp.get(2)));
                aword.setEndy(Integer.parseInt(slp.get(3)));
            }else if(slp.size()==3){
                aword.setStartx(Integer.parseInt(slp.get(0)));
                aword.setStarty(Integer.parseInt(slp.get(1)));
                aword.setEndx(Integer.parseInt(slp.get(2)));
                aword.setEndy(-1);
            }
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
        for(int i=0;i<ALL.size()-1;i++)
        {
            if(ALL.get(i).getcontext()!=null)
                {
                    cnt=0;
                    while(cnt<code.length)
                    {
                        if(ALL.get(i).getcontext().equals(code[cnt]) || ALL.get(i).getcontext().equals(code[cnt].toLowerCase()) || ALL.get(i).getcontext().contains(code[cnt]) || ALL.get(i).getcontext().contains(code[cnt].toLowerCase()))
                    {
                        Word alecture=new Word();
                        alecture.setStartx(ALL.get(i).getStartx());
                        alecture.setStarty(ALL.get(i).getStarty());
                        alecture.setEndx(ALL.get(i+1).getEndx());
                        alecture.setEndy(ALL.get(i+1).getEndy());
                        alecture.setcontext(ALL.get(i).getcontext()+ALL.get(i+1).getcontext());
                        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        String aLEC=ALL.get(i).getcontext()+" "+ALL.get(i+1).getcontext();
                        Lectures.add(aLEC);
                        lectures.add(alecture);

                    }
                    cnt++;
                    }
                }
        }

        Set<String> hs = new HashSet<>();
        hs.addAll(Lectures);
        Lectures.clear();
        Lectures.addAll(hs);

        String ap="";
        int smolx=1000000;
        Word firstX=null;

        for(int g=0;g<lectures.size();g++)
        {
            if(lectures.get(g).getStartx()<smolx)
            {
                smolx=lectures.get(g).getStartx();
                firstX=lectures.get(g);
            }
        }

        Word firstY=firstX;
        int w=Math.abs(firstX.getEndx()-firstX.getStartx());
        int h=Math.abs(firstY.getEndy()-firstY.getStarty());

        for(int x=0;x<lectures.size();x++)
        {
            double distX=Math.abs(firstX.getStartx()-lectures.get(x).getStartx());
            double distY=(150/100)*Math.abs(firstY.getStarty()-lectures.get(x).getStarty());

            if(distX==0*w)
            {
                lectures.get(x).setDay("Monday");
                if(distY==0)
                {
                    lectures.get(x).sethour(8);
                }
                if(distY>0*h&&distY<=1*h)
                {
                    lectures.get(x).sethour(9);
                }
                if(distY>1*h&&distY<=2*h)
                {
                    lectures.get(x).sethour(10);
                }
                if(distY>2*h && distY<=3*h)
                {
                    lectures.get(x).sethour(11);
                }
                if(distY>3*h&&distY<=4*h)
                {
                    lectures.get(x).sethour(12);
                }
                if(distY>4*h && distY<=5*h)
                {
                    lectures.get(x).sethour(13);
                }
                if(distY>5*h&&distY<=6*h)
                {
                    lectures.get(x).sethour(14);
                }
                if(distY>6*h&&distY<=7*h)
                {
                    lectures.get(x).sethour(15);
                }
                if(distY>7*h&&distY<=8*h)
                {
                    lectures.get(x).sethour(16);
                }
                if(distY>8*h)
                {
                    lectures.get(x).sethour(17);
                }
            }

            if(distX>0*w && distX<=1*w)
            {
                lectures.get(x).setDay("Monday");
                if(distY==0)
                {
                    lectures.get(x).sethour(8);
                }
                if(distY>0*h&&distY<=1*h)
                {
                    lectures.get(x).sethour(9);
                }
                if(distY>1*h&&distY<=2*h)
                {
                    lectures.get(x).sethour(10);
                }
                if(distY>2*h && distY<=3*h)
                {
                    lectures.get(x).sethour(11);
                }
                if(distY>3*h&&distY<=4*h)
                {
                    lectures.get(x).sethour(12);
                }
                if(distY>4*h && distY<=5*h)
                {
                    lectures.get(x).sethour(13);
                }
                if(distY>5*h&&distY<=6*h)
                {
                    lectures.get(x).sethour(14);
                }
                if(distY>6*h&&distY<=7*h)
                {
                    lectures.get(x).sethour(15);
                }
                if(distY>7*h&&distY<=8*h)
                {
                    lectures.get(x).sethour(16);
                }
                if(distY>8*h)
                {
                    lectures.get(x).sethour(17);
                }
            }

            if(distX>1*w && distX<=2*w)
            {
                lectures.get(x).setDay("Tuesday");
                if(distY==0)
                {
                    lectures.get(x).sethour(8);
                }
                if(distY>0*h&&distY<=1*h)
                {
                    lectures.get(x).sethour(9);
                }
                if(distY>1*h&&distY<=2*h)
                {
                    lectures.get(x).sethour(10);
                }
                if(distY>2*h && distY<=3*h)
                {
                    lectures.get(x).sethour(11);
                }
                if(distY>3*h&&distY<=4*h)
                {
                    lectures.get(x).sethour(12);
                }
                if(distY>4*h && distY<=5*h)
                {
                    lectures.get(x).sethour(13);
                }
                if(distY>5*h&&distY<=6*h)
                {
                    lectures.get(x).sethour(14);
                }
                if(distY>6*h&&distY<=7*h)
                {
                    lectures.get(x).sethour(15);
                }
                if(distY>7*h&&distY<=8*h)
                {
                    lectures.get(x).sethour(16);
                }
                if(distY>8*h)
                {
                    lectures.get(x).sethour(17);
                }
            }

            if(distX>2*w && distX<=3*w)
            {
                lectures.get(x).setDay("Wednesday");
                if(distY==0)
                {
                    lectures.get(x).sethour(8);
                }
                if(distY>0*h&&distY<=1*h)
                {
                    lectures.get(x).sethour(9);
                }
                if(distY>1*h&&distY<=2*h)
                {
                    lectures.get(x).sethour(10);
                }
                if(distY>2*h && distY<=3*h)
                {
                    lectures.get(x).sethour(11);
                }
                if(distY>3*h&&distY<=4*h)
                {
                    lectures.get(x).sethour(12);
                }
                if(distY>4*h && distY<=5*h)
                {
                    lectures.get(x).sethour(13);
                }
                if(distY>5*h&&distY<=6*h)
                {
                    lectures.get(x).sethour(14);
                }
                if(distY>6*h&&distY<=7*h)
                {
                    lectures.get(x).sethour(15);
                }
                if(distY>7*h&&distY<=8*h)
                {
                    lectures.get(x).sethour(16);
                }
                if(distY>8*h)
                {
                    lectures.get(x).sethour(17);
                }
            }
            if(distX>3*w && distX<=4*w)
            {
                lectures.get(x).setDay("Thursday");
                if(distY==0)
                {
                    lectures.get(x).sethour(8);
                }
                if(distY>0*h&&distY<=1*h)
                {
                    lectures.get(x).sethour(9);
                }
                if(distY>1*h&&distY<=2*h)
                {
                    lectures.get(x).sethour(10);
                }
                if(distY>2*h && distY<=3*h)
                {
                    lectures.get(x).sethour(11);
                }
                if(distY>3*h&&distY<=4*h)
                {
                    lectures.get(x).sethour(12);
                }
                if(distY>4*h && distY<=5*h)
                {
                    lectures.get(x).sethour(13);
                }
                if(distY>5*h&&distY<=6*h)
                {
                    lectures.get(x).sethour(14);
                }
                if(distY>6*h&&distY<=7*h)
                {
                    lectures.get(x).sethour(15);
                }
                if(distY>7*h&&distY<=8*h)
                {
                    lectures.get(x).sethour(16);
                }
                if(distY>8*h)
                {
                    lectures.get(x).sethour(17);
                }
            }
            if(distX>4*w)
            {
                lectures.get(x).setDay("Friday");
                if(distY==0)
                {
                    lectures.get(x).sethour(8);
                }
                if(distY>0*h&&distY<=1*h)
                {
                    lectures.get(x).sethour(9);
                }
                if(distY>1*h&&distY<=2*h)
                {
                    lectures.get(x).sethour(10);
                }
                if(distY>2*h && distY<=3*h)
                {
                    lectures.get(x).sethour(11);
                }
                if(distY>3*h&&distY<=4*h)
                {
                    lectures.get(x).sethour(12);
                }
                if(distY>4*h && distY<=5*h)
                {
                    lectures.get(x).sethour(13);
                }
                if(distY>5*h&&distY<=6*h)
                {
                    lectures.get(x).sethour(14);
                }
                if(distY>6*h&&distY<=7*h)
                {
                    lectures.get(x).sethour(15);
                }
                if(distY>7*h&&distY<=8*h)
                {
                    lectures.get(x).sethour(16);
                }
                if(distY>8*h)
                {
                    lectures.get(x).sethour(17);
                }
            }
        }


        for(int i=0;i<lectures.size();i++)
        {
            if(lectures.get(i)!=null)
            {
                ap=ap+ "A LECTURE: " + lectures.get(i).getcontext()+" Hour: "+ lectures.get(i).gethour()+ " Day: "+lectures.get(i).getDay();
            }
        }

        Toast.makeText(SimpleAndroidOCRActivity.this, "Word firstX: " +firstX.getcontext()+
                        "Word firstY: "+firstY.getcontext(),
                Toast.LENGTH_SHORT).show();

        _field.setText(ap);

        LectureApprove.lectures=lectures;

        Intent intent = new Intent(SimpleAndroidOCRActivity.this,LectureApprove.class);
        startActivity(intent);

        // CS,ENG,GE,MATH,MBG,TURK,HIST,HUM,PHYS,EEE,IE,FRE
        // 13 485 29 497   startx, starty, endx, endy
        //<span class='ocrx_word' id='word_1_114' title='bbox 13 485 29 497; x_wconf 95'><strong>16</strong></span>
        // Log.v(TAG, "OCRED TEXT: " + recognizedText);
        /*if ( recognizedText.length() != 0 ) {
            _field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
            _field.setSelection(_field.getText().toString().length());
        }*/

    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


    public Bitmap resize(Bitmap img, int newWidth, int newHeight) {
        Bitmap bmap = img.copy(img.getConfig(), true);

        double nWidthFactor = (double) img.getWidth() / (double) newWidth;
        double nHeightFactor = (double) img.getHeight() / (double) newHeight;

        double fx, fy, nx, ny;
        int cx, cy, fr_x, fr_y;
        int color1;
        int color2;
        int color3;
        int color4;
        byte nRed, nGreen, nBlue;

        byte bp1, bp2;

        for (int x = 0; x < bmap.getWidth(); ++x) {
            for (int y = 0; y < bmap.getHeight(); ++y) {

                fr_x = (int) Math.floor(x * nWidthFactor);
                fr_y = (int) Math.floor(y * nHeightFactor);
                cx = fr_x + 1;
                if (cx >= img.getWidth())
                    cx = fr_x;
                cy = fr_y + 1;
                if (cy >= img.getHeight())
                    cy = fr_y;
                fx = x * nWidthFactor - fr_x;
                fy = y * nHeightFactor - fr_y;
                nx = 1.0 - fx;
                ny = 1.0 - fy;

                color1 = img.getPixel(fr_x, fr_y);
                color2 = img.getPixel(cx, fr_y);
                color3 = img.getPixel(fr_x, cy);
                color4 = img.getPixel(cx, cy);

                // Blue
                bp1 = (byte) (nx * Color.blue(color1) + fx * Color.blue(color2));
                bp2 = (byte) (nx * Color.blue(color3) + fx * Color.blue(color4));
                nBlue = (byte) (ny * (double) (bp1) + fy * (double) (bp2));

                // Green
                bp1 = (byte) (nx * Color.green(color1) + fx * Color.green(color2));
                bp2 = (byte) (nx * Color.green(color3) + fx * Color.green(color4));
                nGreen = (byte) (ny * (double) (bp1) + fy * (double) (bp2));

                // Red
                bp1 = (byte) (nx * Color.red(color1) + fx * Color.red(color2));
                bp2 = (byte) (nx * Color.red(color3) + fx * Color.red(color4));
                nRed = (byte) (ny * (double) (bp1) + fy * (double) (bp2));

                bmap.setPixel(x, y, Color.argb(255, nRed, nGreen, nBlue));
            }
        }

        bmap = toGrayscale(bmap);
        return bmap;
    }
    // RemoveNoise
    private Bitmap removeNoise(Bitmap bmap) {
        for (int x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                if (Color.red(pixel) < 162 && Color.green(pixel) < 162 && Color.blue(pixel) < 162) {
                    bmap.setPixel(x, y, Color.BLACK);
                }
            }
        }
        for (int x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                if (Color.red(pixel) > 162 && Color.green(pixel) > 162 && Color.blue(pixel) > 162) {
                    bmap.setPixel(x, y, Color.WHITE);
                }
            }
        }
        return bmap;
    }
    // www.Gaut.am was here
    // Thanks for reading!


}
