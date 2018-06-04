package com.example.victor.iot;

import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.example.victor.iot.MainActivity.INVENTORY_URL;

class ScheduledTask extends TimerTask {

    public void run() {
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            URL url = new URL(INVENTORY_URL);
            InputStream inputStream = url.openStream();
            Document document = db.parse(inputStream);

            List<String> epcList = new ArrayList<>();
            NodeList elements = document.getElementsByTagName("epc");
            if (elements != null) {
                for(int i = 0; i < elements.getLength(); ++i) {
                    epcList.add(elements.item(i).getFirstChild().getNodeValue());
                }
                //DeleteUserActivity.setDeleteUserScanList(epcList);
                System.out.println(epcList);
                DeleteUserActivity.deleteRfids(epcList);
            }
        }catch (Exception e){
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}

public class ScanInventoryTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Timer time = new Timer(); // Instantiate Timer Object
            ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
            time.schedule(st, 0, 3000); // Create Repetitively task for every 1 secs

        return null;
    }
}