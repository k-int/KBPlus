/**
 * Copyright 2012 Trustees of the University of Pennsylvania
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.ole.docstore.model.bagit;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.filesystem.impl.ZipFileSystem;
import gov.loc.repository.bagit.utilities.FormatHelper;
import gov.loc.repository.bagit.utilities.SimpleResult;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author upennlib
 */
public class BagExtractor {

    public static final  BagFactory              bf                          = new BagFactory();
    public static final  Form                    FILENAME_NORMALIZATION_FORM = Form.NFC;
    private static final int                     BUFFER_SIZE                 = 2048;
    //  private static final String     DATA_DIR                    = "bag_dir"+File.separator+"data"+File.separator;
    //  private static final String DATA_DIR = "data"+File.separator;
    private static final String                  DATA_DIR                    = "data/";
    // private static final String DATA_DIR = "data";
    //private static final String     DATA_DIR                    = "bag_dir\\data";
    private static final org.apache.log4j.Logger LOG                         = org.apache.log4j.Logger
            .getLogger(BagExtractor.class);


    public static void main(String[] args) throws FormatHelper.UnknownFormatException {
        //File bagFile = new File( ClassLoader.getSystemResourceAsStream("edu/upennlib/bagexamples/bulkIngest-Work-Bib-Marc-20.xml").toString());

        //   File bagFile=new File("C:\\DOCUME~1\\nd6967\\LOCALS~1\\Temp\\bag.41486.zip");
        //        File bagFile = new File(
        //                "\\projects\\OLE-SVN-JUN\\ole-trunk\\DocumentStore\\documentstore-webapp\\src\\main\\webapp\\files\\bag.35624.zip");

        //  File bagFile = new File("C:\\Documents and Settings\\nd6967\\Local Settings\\Temp\\bag.28255.zip");
        File bagFile = new File("c://bujji//bag.8780079206618240355.zip");
        // File bagFile = new File("C:\\DOCUME~1\\nd6967\\LOCALS~1\\Temp\\bag.53798.zip");


        //C:\projects\OLE-SVN-JUN\ole-trunk\DocumentStore\documentstore-webapp\src\main\webapp\files
        if (bagFile.exists()) {

        }
        else {

        }

        try {
            System.out.println("extractBagContent " + BagExtractor.extractBagContent(bagFile.getAbsolutePath()));
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //        File output = File.createTempFile("bag.", ".output");
        //
        //        FileUtils.deleteQuietly(output);
        //        output.mkdirs();
        //
        //
        //        if (verifyBag(bagFile)) {
        //            extractBag(bagFile, output);
        //
        //        }
        //        else {
        //            throw new IOException("Bag verification failed.");
        //        }
    }

    /**
     * @param bagFilePath
     * @return
     * @throws IOException
     */
    public static String extractBagContent(String bagFilePath) throws IOException, FormatHelper.UnknownFormatException {
        LOG.info("BagExtractorrr extractBagContenttt ") ;
        System.out.println("BagExtractorrr extractBagContentrr ") ;
        LOG.info("DATA_DIRRRRRRRRRRRRRRRRReeeeeeee " + DATA_DIR);
        File bagFile = new File(bagFilePath);
        File output = null;
        output = File.createTempFile("bag.", ".output");
        FileUtils.deleteQuietly(output);
        output.mkdirs();
        //   extractBag(bagFile, output);
        if (verifyBag(bagFile)) {
            extractBag(bagFile, output);
        }
        else {
            throw new IOException("Bag verification faileddddddddddddddddddddddddddddd.");
        }

        return output.getAbsolutePath();
    }

    /**
     * @param bagFile
     * @return
     */
    public static boolean verifyBag(File bagFile) throws IOException, FormatHelper.UnknownFormatException {

        SimpleResult result = null;
        Bag bag;
        LOG.info("synchronized verifyBagggggg " + bagFile.getAbsolutePath());
        synchronized (bf) {
            LOG.info("synchronizeddddd");
            System.out.println("yes");
        //    try {
                Bag.Format format = FormatHelper.getFormat(bagFile);
                LOG.info("format " + format);
                //gov.loc.repository.bagit.filesystem.FileSystem fileFileSystem = new FileFileSystem(bagFile);
                gov.loc.repository.bagit.filesystem.FileSystem zipFileSystem = new ZipFileSystem(bagFile);
                //LOG.info("FileFileSystem "+fileFileSystem);

                Iterator ite = zipFileSystem.getRoot().listChildren().iterator();
                while (ite.hasNext()) {
                    FileSystemNode fs = (FileSystemNode) ite.next();
                    ZipFile zipFile = new ZipFile(bagFile);
                    Enumeration<ZipArchiveEntry> entryEnum = zipFile.getEntries();
                    while (entryEnum.hasMoreElements()) {
                        ZipArchiveEntry entry = entryEnum.nextElement();
                        String entryFilepath = entry.getName();
                        LOG.info("entryFilepath " + entryFilepath);
                        if (entryFilepath.endsWith("/")) {
                            LOG.info("in if entryFilepath.endsWith");
                            entryFilepath = entryFilepath.substring(0, entryFilepath.length() - 1);
                        }
                        else {
                            //entryFilepath = entryFilepath.substring(0, entryFilepath.length()-1);
                            LOG.info("in else entryFilepath.endsWith entryFilepath " + entryFilepath);
                        }
                        File parentFile = new File(entryFilepath).getParentFile();

                        List<String> parentPaths = new ArrayList<String>();
                        while (parentFile != null) {
                            parentPaths.add((parentFile.getPath()));
                            parentFile = parentFile.getParentFile();
                        }
                        LOG.info("parentPaths " + parentPaths);
                    }
                }

            //}
//            catch (FormatHelper.UnknownFormatException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }

            bag = bf.createBag(bagFile);
            LOG.info("synchronized AFTER createBag");
        }
        result = bag.verifyValid();
        LOG.info("result " + result.getMessages());
        LOG.info("isSuccess " + result.isSuccess());
        return result.isSuccess();
    }

    /**
     * @param bagFile
     * @param outputDir
     * @throws IOException
     */
    public static void extractBag(File bagFile, File outputDir) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(bagFile)));
        ZipEntry next;
        LOG.info("extractBag bagFile.getAbsolutePath " + bagFile.getAbsolutePath());
        LOG.info("extractBag outputDir.getAbsolutePath " + outputDir.getAbsolutePath());
        while ((next = zis.getNextEntry()) != null) {
            LOG.info("next.getName " + next.getName());
            System.out.println("next.getName " + next.getName());
            //String name = next.getName().replaceFirst("[^/]*/", "");
            String name = next.getName().replace('\\', '/').replaceFirst("[^/]*/", "");
            //System.out.println("replace nameunix "+nameunixreplace.replace('\\','/').replaceFirst("[^/]*/", ""));
            //   String name = next.getName();
            LOG.info("name " + name);
            LOG.info("normalize " + Normalizer
                    .normalize(name.substring(DATA_DIR.length()), FILENAME_NORMALIZATION_FORM));
            LOG.info("substring " + name.substring(DATA_DIR.length()));
            LOG.info("DATA_DIR " + DATA_DIR);
            if (name.startsWith(DATA_DIR)) {
                LOG.info("in if name.startsWith(DATA_DIR)");
                File localFile = new File(outputDir, Normalizer
                        .normalize(name.substring(DATA_DIR.length()), FILENAME_NORMALIZATION_FORM));
                if (next.isDirectory()) {
                    LOG.info("in if next.isDirectory");
                    if (!localFile.exists() && !localFile.mkdir()) {
                        throw new IOException("error creating local directories in output directory");
                    }
                }
                else {
                    LOG.info("in else next.isDirectory");
                    File parent = localFile.getParentFile();
                    if (!parent.exists() && !parent.mkdirs()) {
                        throw new IOException("error creating local directories in output directory");
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localFile));
                    int bytesRead;
                    while ((bytesRead = zis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                    bos.close();
                }
            }
        }
        zis.close();
    }


}
