package com.example.albumgallery.utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class ScanQRCode {
    private final Context context;
    public ScanQRCode(Context context) {
        this.context = context;
    }
    public String decodeQRCode(Bitmap bitmap) {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if (barcodeDetector.isOperational() && bitmap != null) {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
            for (int index = 0; index < barcodes.size(); index++) {
                Barcode code = barcodes.valueAt(index);
                return code.displayValue;
            }
        }
        return null;
    }
}
