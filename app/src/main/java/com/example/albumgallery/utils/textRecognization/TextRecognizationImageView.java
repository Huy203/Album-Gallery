//package com.example.albumgallery.utils.textRecognization;
//
//import android.content.Context;
//import android.graphics.Rect;
//import android.util.Log;
//import android.view.MotionEvent;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TextRecognizationImageView extends androidx.appcompat.widget.AppCompatImageView {
//    private List<Rect> boundingBoxes;
//    private List<String> textRecognized;
//
//    public TextRecognizationImageView(Context context) {
//        super(context);
//        this.textRecognized = new ArrayList<>();
//        this.boundingBoxes = new ArrayList<>();
//    }
//
//    public List<Rect> getBoundingBoxes() {
//        return boundingBoxes;
//    }
//
//    public void setBoundingBoxes(List<Rect> boundingBoxes) {
//        this.boundingBoxes = boundingBoxes;
//    }
//
//    public void addBoundingBox(Rect boundingBox) {
//        this.boundingBoxes.add(boundingBox);
//    }
//
//    public List<String> getTextRecognized() {
//        return textRecognized;
//    }
//
//    public void setTextRecognized(List<String> textRecognized) {
//        this.textRecognized = textRecognized;
//    }
//
//    public void addTextRecognized(String textRecognized) {
//        this.textRecognized.add(textRecognized);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            int x = (int) event.getX();
//            int y = (int) event.getY();
//
//            for (int i = 0; i < boundingBoxes.size(); i++) {
//                Rect rect = boundingBoxes.get(i);
//                if (rect.contains(x, y)) {
//                    showContextMenu(textRecognized.get(i));
//                    return true;
//                }
//            }
//        }
//        return super.onTouchEvent(event);
//    }
//
//    private void showContextMenu(String recognizedText) {
//        Log.v("TextRecognization", "Recognized Text: " + recognizedText);
//        // Show a context menu with the actions you want to provide.
//        // Use the recognizedText in the actions of the context menu.
//    }
//}
