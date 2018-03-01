package com.omkar.bmccarparkinguser.Activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.omkar.bmccarparkinguser.Helpers.Encryption;
import com.omkar.bmccarparkinguser.R;

import net.glxn.qrgen.android.QRCode;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class QRCodeActivity extends AppCompatActivity {

    ImageView iv_qr_code;
    String tokenData = "";
    Encryption encryption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        iv_qr_code = findViewById(R.id.iv_qr_code);
        encryption = Encryption.getDefault("Key", "random", new byte[16]);
        tokenData = getIntent().getStringExtra("tokenData");
        String EncreptedData = encryption.encryptOrNull(tokenData);
        Bitmap myBitmap = QRCode.from(EncreptedData).withSize(300,300).withColor(Color.RED,Color.WHITE).bitmap();
        iv_qr_code.setImageBitmap(myBitmap);
    }
}
