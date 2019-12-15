package com.example.practice_thread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Main4Activity extends AppCompatActivity {

    private static String urlStr = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=61&gridy=125";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        new Thread() {
            public void run() {
                try {
                    getWeather();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getWeather() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        URL urlForHttp = new URL(urlStr);

        InputStream instream = getInputStreamUsingHTTP(urlForHttp);
        Document document = builder.parse(instream);

        if (document != null) {
            NodeList list = document.getElementsByTagName("data");

            for (int i = 0; i < list.getLength(); i++) {
                System.out.println("=========================");
                for (int k = 0; k < list.item(i).getChildNodes().getLength(); k++) {
                    if (list.item(i).getChildNodes().item(k).getNodeType() == Node.ELEMENT_NODE) {
                        System.out.print(k + ":" + list.item(i).getChildNodes().item(k).getNodeName() + "====>");
                        System.out.println(list.item(i).getChildNodes().item(k).getTextContent());
                    }
                }
            }
        }
    }

    private InputStream getInputStreamUsingHTTP(URL urlForHttp) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) urlForHttp.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);;
        conn.setAllowUserInteraction(false);
        int resCode = conn.getResponseCode();
        Log.d("TAG ::","Response code : " + resCode);
        InputStream instream = conn.getInputStream();
        return  instream;
    }


}
