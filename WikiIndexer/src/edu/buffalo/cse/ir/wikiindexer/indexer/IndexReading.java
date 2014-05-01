package edu.buffalo.cse.ir.wikiindexer.indexer;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;
import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Date: 10/6/13
 * Time: 1:17 PM
 */

public class IndexReading {

    private static boolean validateProps(Properties props) {
		/* Validate size */
        System.out.println("props != null : "+(props != null));
        System.out.println("props.entrySet().size() : "+props.entrySet().size());
        System.out.println("IndexerConstants.NUM_PROPERTIES : "+ IndexerConstants.NUM_PROPERTIES);
        System.out.println("props.entrySet().size() == IndexerConstants.NUM_PROPERTIES : "+(props.entrySet().size() == IndexerConstants.NUM_PROPERTIES));
        if (props != null && props.entrySet().size() == IndexerConstants.NUM_PROPERTIES) {
			/* Get all required properties and ensure they have been set */
            Field[] flds = IndexerConstants.class.getDeclaredFields();
            boolean valid = true;
            Object key;

            for (Field f : flds) {
                System.out.println("Processing property "+f.getName());
                if (f.isAnnotationPresent(IndexerConstants.RequiredConstant.class) ) {
                    try {
                        key = f.get(null);
                        if (!props.containsKey(key) || props.get(key) == null) {
                            System.err.println("The required property " + f.getName() + " is not set");
                            valid = false;
                        }
                    } catch (IllegalArgumentException e) {

                    } catch (IllegalAccessException e) {

                    }
                }
            }

            return valid;
        }

        return false;
    }

    private static Properties loadProperties(String filename) {

        try {
            Properties props = FileUtil.loadProperties(filename);
            if (validateProps(props)) {
                return props;
            } else {
                System.err.println("Some properties were either not loaded or recognized. Please refer to the manual for more details");
                return null;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to open or load the specified file: " + filename);
        } catch (IOException e) {
            System.err.println("Error while reading properties from the specified file: " + filename);
        }

        return null;
    }

    public static void main(String[] args) {
        String filename = args[0];
        Properties properties = loadProperties(filename);
        IndexReader indexReader = new IndexReader(properties, INDEXFIELD.TERM);
        try {
            indexReader.init();
            int totalKeyTerms = indexReader.getTotalKeyTerms();
            System.out.println("totalKeyTerms: " + totalKeyTerms);
            int totalValueTerms = indexReader.getTotalValueTerms();
            System.out.println("totalValueTerms: "+totalValueTerms);
            /*Map<String, Integer> postings = indexReader.getPostings("Johnpacklambert");*/
            /*Map<String, Integer> postings = indexReader.getPostings("songwriters");*/
            Map<String, Integer> postings = indexReader.getPostings("I");
            Iterator<Map.Entry<String, Integer>> entryIterator = postings.entrySet().iterator();
            while(entryIterator.hasNext()) {
                Map.Entry<String, Integer> entry = entryIterator.next();
                String key = entry.getKey();
                System.out.println("Key: "+key);
                Integer value = entry.getValue();
                System.out.println("Value: "+value);
            }

            Collection<String> topK = indexReader.getTopK(5);
            Iterator<String> topKIterator = topK.iterator();
            while(topKIterator.hasNext()) {
                String entry = topKIterator.next();
                System.out.println("Top Entry: "+entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
