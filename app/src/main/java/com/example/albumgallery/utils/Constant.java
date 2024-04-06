package com.example.albumgallery.utils;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static final int REQUEST_CODE_PICK_MULTIPLE_IMAGES = 100;
    public static final int REQUEST_CODE_CAPTURE_IMAGE = 101;
    public static final int REQUEST_CODE_CAMERA = 1000;
    public static final int REQUEST_CODE_EDIT_IMAGE = 102;
    public static final int REQUEST_CODE_DETAIL_IMAGE = 103;


    public static final List<String> imageExtensions = new ArrayList<String>() {{
        add("jpg");
        add("jpeg");
        add("png");
        add("gif");
        add("bmp");
    }};
}
