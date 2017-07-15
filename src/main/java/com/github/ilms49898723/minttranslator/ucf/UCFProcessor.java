package com.github.ilms49898723.minttranslator.ucf;

import javax.json.Json;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by littlebird on 2017/07/15.
 */
public class UCFProcessor {
    public void run(String filename) {
        try {
            JsonReader jsonReader = Json.createReader(new FileInputStream(filename));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
