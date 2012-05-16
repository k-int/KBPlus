package org.kuali.ole.web;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kuali.ole.docstore.model.bagit.BagCreator;
import org.kuali.ole.docstore.model.bagit.BagExtractor;
import org.kuali.ole.docstore.model.xmlpojo.ingest.Response;
import org.kuali.ole.docstore.model.xmlpojo.ingest.ResponseDocument;
import org.kuali.ole.docstore.model.xstream.ingest.KBPlusResponseHandler;
import org.kuali.ole.docstore.model.xstream.ingest.ResponseHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: nd6967
 * Date: 4/24/12
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class KBPlusTestServlet
        extends HttpServlet {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KBPlusTestServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            System.out.println("in KBPlusTestServlet wow ");
            LOG.info("innnnn KBPlusTestServlet doPost");
            PrintWriter out = null;
            Response response=null;
            String url = getProperty("kbplus.url");
            out = resp.getWriter();
            String documentsLocation = getProperty("kbplus.testcases.root.folder");
            LOG.info("innnnn KBPlusTestServlet doPost url " + url);
            LOG.info("innnnn KBPlusTestServlet doPost documentsLocation " + documentsLocation);
        //    String output=documentsLocation + File.separator + "output";
            out.println("<html>");
            out.println("<body>");
            out.println("<table border=\"1\">");
            out.println("<tr>");
            out.println("<td><b> Test Case   </b></td>");
             out.println("<td><b>  Message  </b></td>");
             out.println("<td><b>  Status  </b></td>");
            out.println("</tr>");



            File f = new File(documentsLocation);
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                System.out.println(i + " isDirectory " + files[i].isDirectory());
                if (files[i].isDirectory()) {
                    if (files[i].list().length != 0) {
                        if (getProperty(files[i].getName()) == null || getProperty(files[i].getName())
                                .equalsIgnoreCase("true")) {
                            KBPlusTest(url, files[i].getAbsolutePath());
                            String output=files[i].getAbsolutePath()+File.separator +"output";
                            response=prepareResponsePojo( output);
                            System.out.println("responseeeeeeee "+response);
                            LOG.info("responseeeeeeee "+response);
                            out.println("<tr>");
                            out.println("<td>"+files[i].getName()+"</td>");
                            out.println("<td>"+response.getMessage()+"</td>");
                            out.println("<td>"+response.getStatus()+"</td>");
                            out.println("</tr>");
                        }
                        else {
                            LOG.info("Skipped " + files[i].getAbsolutePath());
                            System.out.println("Skipped " + files[i].getAbsolutePath());
                        }
                    }else{
                        LOG.info(files[i].getAbsolutePath() + " is empty");
                        System.out.println(files[i].getAbsolutePath() + " is empty");
                    }
                }
            }
            out.println("</table>");
            out.println("</html>");
            out.println("</body>");

            //out.println("All test cases are completed");
        }
        catch (Exception e) {
            e.printStackTrace();
            LOG.info("Exceptionnnn", e);
        }


    }
    public Response prepareResponsePojo(String output) throws IOException {
        Response response=null;
        System.out.println("prepareResponsePojo output "+output);
        LOG.info("prepareResponsePojo output "+output);
        String responseXMLLocation=output+File.separator+"response.xml";
        System.out.println("prepareResponsePojo responseXMLLocation "+responseXMLLocation);
        LOG.info("prepareResponsePojo responseXMLLocation "+responseXMLLocation);
        File file=new File(responseXMLLocation);
        if(file.exists()){
        String responseXMLContent= readFile(file) ;
        System.out.println("prepareResponsePojo responseXMLContent "+responseXMLContent);
        LOG.info("prepareResponsePojo responseXMLContent "+responseXMLContent);
        response=new ResponseHandler().toObject(responseXMLContent);
        }
        return response;

    }

    /**
     * Method creates the bag from the test case folder then sends to the document store application and captures the response
     * @param url
     * @param documentsLocation
     * @throws Exception
     */
    private void KBPlusTest(String url, String documentsLocation) throws Exception {
        LOG.info("in KBPlusTest");
        String input = documentsLocation;
        LOG.info("in KBPlusTest input " + input);

        HttpPost httpPost = new HttpPost(url);
        HttpClient httpclient = new DefaultHttpClient();
        LOG.info("existsssssssss " + new File(input).exists());

        LOG.info("in KBPlusTest before create bag");
        File Location_Of_Created_Bag = BagCreator.createBag(new File(input));
        LOG.info("HttpUtilllllllllllllllll : Location_Of_Created_Bag " + Location_Of_Created_Bag);
        FileBody uploadFilePart = new FileBody(Location_Of_Created_Bag);  //the bag is then uploaded for ingesting
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("upload-file", uploadFilePart);
        httpPost.setEntity(reqEntity);
        //retrieve

        HttpResponse response = httpclient.execute(httpPost);
        createOutput(response, documentsLocation);

    }

    /**
     * Method prepares the response folder
     * @param response
     * @param documentsLocation
     * @throws Exception
     */
    private void createOutput(HttpResponse response, String documentsLocation) throws Exception {
        String output = documentsLocation + File.separator + "output";
        System.out.println("KBPlusTestServlet createOutput output "+output);
        LOG.info("KBPlusTestServlet createOutput output "+output);
        File outputLocation = new File(output);
        if (!outputLocation.exists()) {
            System.out.println("KBPlusTestServlet createOutput in if exists");
            LOG.info("KBPlusTestServlet createOutput in if exists");
            outputLocation.mkdir();
        }

        File responseLocation = File.createTempFile("response.", ".zip", outputLocation);
        byte[] buf = new byte[1028];
        InputStream is = response.getEntity().getContent();
        File file = new File(responseLocation.getAbsolutePath());

        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream fileInBuf = null;
        fileInBuf = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length;
        while ((length = fileInBuf.read(buf)) > 0) {
            baos.write(buf, 0, length);
        }
        fos.write(baos.toByteArray());
        fileInBuf.close();
        baos.close();
        is.close();
        fos.close();
        LOG.info("Response in bagit form is created at " + responseLocation.getAbsolutePath());
        // File responseFileExtractedLocation=BagCreator.createBag(responseLocation);
        String extractedResponseContents = BagExtractor.extractBagContent(responseLocation.getAbsolutePath());
        FileUtils.copyDirectory(new File(extractedResponseContents), new File(output));
        // return  BagExtractor.extractBagContent(responseLocation.getAbsolutePath())  ;
    }
        private String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param key
     * @return
     * @throws IOException
     */
    private String getProperty(String key) throws IOException {
        Properties properties = new Properties();
        String propsDir = System.getProperty("docstore.properties.home");
        //        String propsDir = File.separator + "opt" + File.separator + "docstore" + File.separator + "properties"
        //                          + File.separator + "documentstore.properties";
        //        String propsDir = File.separator+"home"+File.separator +"kbplus"+File.separator+ "opt" + File.separator + "docstore" + File.separator + "properties"
        //                          + File.separator + "documentstore.properties";
        propsDir = propsDir + File.separator + "documentstore.properties";
        System.out.println("propsDir " + propsDir);
        LOG.info("propsDir " + propsDir);
        FileInputStream fis = new FileInputStream(propsDir);
        properties.load(fis);
        String value = properties.getProperty(key);
        fis.close();
        return value;


    }
}
