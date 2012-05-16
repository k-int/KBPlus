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

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.PreBag;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author upennlib
 */
public class BagCreator {
    public static final BagFactory bf = new BagFactory();
     private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BagCreator.class);

    public static void main(String[] args) throws IOException {
        //  File input = new File(args[0]);
        //File input=new File("C://InterviewQuestions.txt");
        File input = new File("C:\\projects\\KB+\\documents\\save\\input");
        // File input=new File("\\java\\drivers\\ojdbc14.jar");
        //File input=new File("org\\kuali\\ole\\bulkhandler\\nar.xml");
        BagCreator bagCreator = new BagCreator();
        File file=BagCreator.createBag(input);
        System.out.println("file "+file.getAbsolutePath());
        bagCreator.test();
        System.out.println("");
    }

    public void test() {
        URL resource = getClass().getResource("/org/kuali/ole/repository/requestKBPlus.xml");

    }

    /**
     *
     * @param bagSource
     * @return
     * @throws IOException
     */
    public static File createBag(File bagSource) throws IOException {
        LOG.info("in createBag ");
        File tempDir = File.createTempFile("bag.", ".dir");
         LOG.info("in createBag tempDir "+tempDir);
        FileUtils.deleteQuietly(tempDir);
        LOG.info("in createBag after deleteQuietly");

        File bagDir = new File(tempDir, "bag_dir");
        bagDir.mkdirs();
        LOG.info("in createBag bagDir "+bagDir.getAbsolutePath());


        FileUtils.copyDirectory(bagSource, bagDir);
        PreBag preBag;
        synchronized (bf) {
            preBag = bf.createPreBag(bagDir);
        }
        preBag.makeBagInPlace(BagFactory.Version.V0_96, false);

        File zipFile = zipDirectory(tempDir);
        FileUtils.deleteQuietly(tempDir);
        LOG.info("in createBag bagDir after deleteQuietly again ");
        LOG.info("zipFile "+zipFile.getAbsolutePath());

        return zipFile;
    }

    /**
     *
     * @param directory
     * @return
     * @throws IOException
     */
    private static File zipDirectory(File directory) throws IOException {
        File testZip = File.createTempFile("bag.", ".zip");
        String path = directory.getAbsolutePath();
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(testZip));

        ArrayList<File> fileList = getFileList(directory);
        for (File file : fileList) {
            ZipEntry ze = new ZipEntry(file.getAbsolutePath().substring(path.length() + 1));
            zos.putNextEntry(ze);

            FileInputStream fis = new FileInputStream(file);
            IOUtils.copy(fis, zos);
            fis.close();

            zos.closeEntry();


        }

        zos.close();
        return testZip;
    }

    /**
     *
     * @param file
     * @return
     */
    private static ArrayList<File> getFileList(File file) {
        ArrayList<File> fileList = new ArrayList<File>();
        if (file.isFile()) {
            fileList.add(file);
        }
        else if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                fileList.addAll(getFileList(innerFile));
            }
        }
        return fileList;
    }

}
