package com.babylon.abodihin.hasan.nebuchadnezzarbabylonhistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


public class HttpConnection {
    // declare a variables to carry the url and method of the request
    private static String mURL;
    private static String method;
    // define a hash map which saves the parameters of the request in pairs KEY and VALUE.
    private static Map<String, String> parameters = new HashMap<>();
    // create a string builder to store the response from the web service.
    StringBuilder sb;

    // define method that receives the URL as string value and assign the value to mURL variable.
    public void setmURL(String mURL) {
        this.mURL = mURL;
    }

    // // define method that receives the method as string value and assign the value to method variable.
    public void setMethod(String method) {
        this.method = method;
    }

    // define method that receives two string values KEY and value and then put the result in the hash map.
    public void setParameters(String paramKey, String paramValue) {
        parameters.put(paramKey, paramValue);
    }

    // this method used to get the data from the web and return the result as string
    public String getData() {
        String params = "";
        URL url = null;
        // foreach loop which used to loop through all keys in the hash map.
        for (String key : parameters.keySet()) {
            // add "&" to params string only if the params length is grater than 0 which means do not add
            // the "&" at the beginning of the params string.
            if (params.length() > 0) {
                params = params + "&";
            }
            // get the value of the parameters from the parameter hash map using get method and by passing the KEy.
            String value = parameters.get(key);
            // separate the key and value by "=" and append the result to params string.
            params = params + key + "=" + value;
        }
        // we need to append the params to the URL if the method is GET
        // note that the URL should end with "?"
        if (method.equals("GET")) {
            mURL = mURL + params;
        }
        // surround the code with try catch block because an exception might through.
        try {
            // instantiate URL class and pass the URL string to it to create a URL object
            url = new URL(mURL);
            // open the connection and set the requested method.
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod(method);
            // check if the specified method is POST
            if (method.equals("POST")) {
                // get the out put stream.
                httpConnection.setDoInput(true);
                OutputStream out = httpConnection.getOutputStream();
                // create a stream writer.
                OutputStreamWriter writer = new OutputStreamWriter(out);
                // write the data (params) to the stream
                writer.write(params);
                // clear the writer and close the out put stream
                writer.flush();
                out.close();
            }
            // instantiate the String builder class to get a string builder which used to content all respond data.
            sb = new StringBuilder();
            // get the input stream
            InputStream in = httpConnection.getInputStream();
            // create a stream reader.
            InputStreamReader reader = new InputStreamReader(in);
            // create a buffer reader
            BufferedReader bin = new BufferedReader(reader);
            String dataLine;
            // create line from the buffer and check if it is not null then append the line to the string builder.
            while ((dataLine = bin.readLine()) != null) {
                sb.append(dataLine);
            }
            // close the input stream
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return the data from the string builder as string.
        return sb.toString();

    }
}
