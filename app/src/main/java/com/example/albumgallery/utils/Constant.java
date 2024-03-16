package com.example.albumgallery.utils;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static final int REQUEST_CODE_PICK_MULTIPLE_IMAGES = 100;
    public static final List<String> imageExtensions = new ArrayList<String>() {{
        add("jpg");
        add("jpeg");
        add("png");
        add("gif");
        add("bmp");
    }};
}
