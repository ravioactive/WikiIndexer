package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.*;

/**
 * User: ravioactive
 */

public class Index {

    private INDEXFIELD indexType = null;
    private INDEXFIELD indexValueType = null;
    boolean inverted = false;

    private LocalDictionary fieldDictionary = null;

    Map<Integer, Map<Integer, GenericIndexEntry>> indexMapQueue;


    public Index(Dictionary dictionary, Properties props, INDEXFIELD indexType) {
        this(dictionary, props, indexType, INDEXFIELD.LINK, false);
    }

    public Index(Dictionary dictionary, Properties properties, INDEXFIELD keyType, INDEXFIELD valueType) {
        this(dictionary, properties, keyType, valueType, false);
    }

    public Index(Dictionary dictionary, Properties properties, INDEXFIELD keyType, INDEXFIELD valueType, boolean isForward) {
        indexType = keyType;
        indexValueType = valueType;
        inverted = !isForward;
        fieldDictionary = (LocalDictionary) dictionary;
        init(properties);
    }

    private void init(Properties properties) {
        if(fieldDictionary == null) {
            fieldDictionary = new LocalDictionary(properties, indexType);
        }
        indexMapQueue = new TreeMap<Integer, Map<Integer, GenericIndexEntry>>();
    }

    public int getDictionaryValue(String key) {
        return fieldDictionary.lookup(key);
    }

    private String getKeyForValue(int value) {
        return fieldDictionary.getKeyForValue(value);
    }

    public boolean addWithDictionaryLookup(String key, String value, int numOccurances) throws IndexerException {
        int dictionaryId = 0;
        if(indexType != INDEXFIELD.LINK) {
            dictionaryId = getDictionaryValue(key);
        } else {
            try {
                dictionaryId = Integer.parseInt(key);
            } catch (NumberFormatException nfe) {
                throw new IndexerException("Only integer parse-able string values allowed for link index");
            }
        }
        GenericIndexEntry entry = new GenericIndexEntry(dictionaryId, value, numOccurances, inverted);
        return add(entry);
    }

    public boolean addWithDictionaryLookup(String key, int valueId, int numOccurances) throws IndexerException {
        int dictionaryId = 0;
        if(indexType != INDEXFIELD.LINK) {
            dictionaryId = getDictionaryValue(key);
        } else {
            try {
                dictionaryId = Integer.parseInt(key);
            } catch (NumberFormatException nfe) {
                throw new IndexerException("Only integer parse-able string values allowed for link index");
            }
        }
        GenericIndexEntry entry = new GenericIndexEntry(dictionaryId, valueId, numOccurances, inverted);
        return add(entry);
    }

    public boolean addWithDictionaryLookup(int keyId, String value, int numOccurances) throws IndexerException {
        int dictionaryId = 0;
        if(indexType != INDEXFIELD.LINK) {
            dictionaryId = getDictionaryValue(String.valueOf(keyId));
        } else {
            dictionaryId = keyId;
        }

        GenericIndexEntry entry = new GenericIndexEntry(dictionaryId, value, numOccurances, inverted);
        return add(entry);
    }

    public boolean addWithDictionaryLookup(int keyId, int valueId, int numOccurances) throws IndexerException {
        int dictionaryId = 0;
        if(indexType != INDEXFIELD.LINK) {
            dictionaryId = getDictionaryValue(String.valueOf(keyId));
        } else {
            dictionaryId = keyId;
        }

        GenericIndexEntry entry = new GenericIndexEntry(dictionaryId, valueId, numOccurances, inverted);
        return add(entry);
    }

    public boolean add(GenericIndexEntry entry) {
        boolean added = false;
        //int key = (inverted)?entry.getKey():entry.getValue();
        int key = entry.getKey();       //Calling code is taking care of inverting values on index's behalf.

        if(key>0) {
            Map<Integer, GenericIndexEntry> entryMap = null;
            if(!indexMapQueue.containsKey(key)) {
                entryMap = new TreeMap<Integer, GenericIndexEntry>();
            } else {
                entryMap = indexMapQueue.get(key);
            }

            /*if(inverted) {           //Calling code is taking care of inverting values on index's behalf.
                entryMap.put(entry.getValue(), entry);
            } else {
                entryMap.put(entry.getKey(), entry);
            }*/

            entryMap.put(entry.getValue(), entry);
            indexMapQueue.put(key, entryMap);
            added = true;
        }

        return added;
    }

    public boolean exists(int key) {
        return indexMapQueue.containsKey(key);
    }

    public boolean existsFor(int key, GenericIndexEntry entry) {
        boolean exists = false;
        if(exists(key)) {
            Map<Integer, GenericIndexEntry> entryMap = indexMapQueue.get(key);
            if(inverted) {
                if(entryMap.containsKey(entry.getValue())) {
                    exists = true;
                }
            } else {
                if(entryMap.containsKey(entry.getKey())) {
                    exists = true;
                }
            }
        }

        return exists;
    }

    public boolean remove(int key) {
        boolean removed = false;
        if(!exists(key)){
            removed = true;
        } else {
            indexMapQueue.remove(key);
            removed = true;
        }
        return removed;
    }

    public boolean removeFor(int key, GenericIndexEntry entry) {
        boolean removed = false;
        if(key>0) {
            if(!exists(key)) {
                removed = true;
            } else {
                Map<Integer, GenericIndexEntry> entryMap = indexMapQueue.get(key);

                /*if(inverted) {        //Calling code is taking care of inverting values on index's behalf.
                    if(entryMap.containsKey(entry.getValue())) {
                        entryMap.remove(entry.getValue());
                    }
                } else {
                    if(entryMap.containsKey(entry.getKey())) {
                        entryMap.remove(entry.getKey());
                    }
                }*/

                if(entryMap.containsKey(entry.getValue())) {
                    entryMap.remove(entry.getValue());
                }
            }
        }
        return removed;
    }

    public Map<String, Integer> getIndexCountFor(String key) {
        Map<String, Integer> postingsList = null;
        if(fieldDictionary.exists(key)) {
            int dictionaryValue = getDictionaryValue(key);
            if(exists(dictionaryValue)) {
                postingsList = new HashMap<String, Integer>();
                int valuesCount = indexMapQueue.get(dictionaryValue).size();
                postingsList.put(key, valuesCount);
            } else {
                System.err.println("Value present in dictionary but not in index. Something weird has happened here...");
            }
        }

        return postingsList;
    }

    public List<String> getTopK_Keys(int k) {
        List<String> topK = null;
        if(!indexMapQueue.isEmpty()) {
            Iterator<Integer> keysIterator = indexMapQueue.keySet().iterator();
            topK = new ArrayList<String>();
            if(indexMapQueue.size() <= k) {
                System.err.println("Index size is <= the no. of requested terms. Returning full index");
                while(keysIterator.hasNext()) {
                    String key = getKeyForValue(keysIterator.next());
                    topK.add(key);
                }
            } else {
                int i = 0;
                while(keysIterator.hasNext() && i<k) {
                    String key = getKeyForValue(keysIterator.next());
                    topK.add(key);
                    i++;
                }
            }

        }
        return topK;
    }

    public int getTotalEntries() {
        return indexMapQueue.size();
    }

    public LocalDictionary getFieldDictionary() {
        return fieldDictionary;
    }

    public INDEXFIELD getIndexType() {
        return indexType;
    }

    public INDEXFIELD getIndexValueType() {
        return indexValueType;
    }

    public INDEXFIELD getDictionaryType() {
        return indexType;
    }

    public boolean isInverted() {
        return inverted;
    }

    public Map<Integer, Map<Integer, GenericIndexEntry>> getIndexMapQueue() {
        return indexMapQueue;
    }
}
