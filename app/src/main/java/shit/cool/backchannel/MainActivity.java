package shit.cool.backchannel;


import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.w3c.dom.Text;

import java.util.List;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class MainActivity extends AppCompatActivity {

    RelativeLayout screen;
    View view;
    Toolbar toolbar;
    SimpleArcLoader smallerarc;
    SimpleArcLoader inenrArc;
    SimpleArcLoader outerArc;
    TextView displayText;
    ImageView imageView;

    String code;
    int currentPane = 0;


    private static final String TAG = MainActivity.class.getSimpleName();
    private CompoundBarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
            }
            //Added preview of scanned barcodef
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

            Integer num = Integer.parseInt(result.toString().substring(0, 2));
            displayText.setText(String.valueOf(num));


            if (num >= currentPane) {
                currentPane = num + 1;
                Log.d("result", result.toString());
                code += result.toString().substring(3);
                displayText.setText("Completed");
            } else {
                //displayText.setText("Scanning");
            }


        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

            //Log.d("resultPoints", resultPoints.toString());

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Back Channel");

        getSupportActionBar().setElevation(0);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        barcodeView = (CompoundBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("");
        barcodeView.pause();

        displayText = (TextView) findViewById(R.id.displayText);
        imageView = (ImageView) findViewById(R.id.barcodePreview);
        screen = (RelativeLayout) findViewById(R.id.screen);
        view = (View) findViewById(R.id.enableButton);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("DOWN", "DOWN");
                        code = "";
                        //screen.setVisibility(View.GONE);
                        smallerarc.setVisibility(View.VISIBLE);
                        inenrArc.setVisibility(View.VISIBLE);
                        outerArc.setVisibility(View.VISIBLE);
                        barcodeView.resume();
                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d("UP", code);
                        displayText.setText(code);
                        currentPane = 0;
                        imageView.setImageBitmap(StringToBitMap(code));
                        //screen.setVisibility(View.VISIBLE);
                        smallerarc.setVisibility(View.GONE);
                        inenrArc.setVisibility(View.GONE);
                        outerArc.setVisibility(View.GONE);
                        barcodeView.pause();
                        return true;
                }

                return false;
            }

        });


        ArcConfiguration configuration = new ArcConfiguration(this);
        configuration.setLoaderStyle(SimpleArcLoader.STYLE.COMPLETE_ARC);
        int[] array = {Color.parseColor("#27ae60"), Color.parseColor("#212121"), Color.parseColor("#27ae60"), Color.parseColor("#212121")};
        configuration.setColors(array);


        smallerarc = (SimpleArcLoader) findViewById(R.id.smallerarc);
        inenrArc = (SimpleArcLoader) findViewById(R.id.innerArc);
        outerArc = (SimpleArcLoader) findViewById(R.id.outerArc);

        configuration.setAnimationSpeed(SimpleArcLoader.SPEED_FAST);
        smallerarc.refreshArcLoaderDrawable(configuration);

        configuration.setAnimationSpeed(SimpleArcLoader.SPEED_MEDIUM);
        inenrArc.refreshArcLoaderDrawable(configuration);

        configuration.setAnimationSpeed(SimpleArcLoader.SPEED_SLOW);
        outerArc.refreshArcLoaderDrawable(configuration);
        //Added preview of scanned barcode
        //ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
        //imageView.setImageBitmap(encodeToQrCode("ON Purpose bababy toooh yeah oh babay yeah this is sooo great like what", 500, 500));


    }

    public static Bitmap encodeToQrCode(String text, int width, int height) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }


    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
