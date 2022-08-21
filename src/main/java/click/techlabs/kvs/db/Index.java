package click.techlabs.kvs.db;

import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

class Index implements Closeable {

    private static final String DELM = ":";

    private Map<String, IndexEntry> indexEntries = new TreeMap<>();
    private PrintWriter indexWriter;
    private boolean ready = false;

    public void init(File indexFile) throws DBException {
        try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(DELM);
                assert parts.length == 3;
                IndexEntry indexEntry = new IndexEntry(parts[0], new FilePointer(Long.parseLong(parts[1]), Long.parseLong(parts[2])));
                indexEntries.put(parts[0], indexEntry);

            }
        } catch (IOException e) {
            //TODO: LOG
            throw new DBException(e);
        }

        try {
            indexWriter = new PrintWriter(new FileWriter(indexFile, true));
        } catch (IOException e) {
            //TODO: LOG
            throw new DBException(e);
        }
        ready = true;
    }

    public void insert(IndexEntry indexEntry) {
        assert ready;
        indexEntries.put(indexEntry.getKey(), indexEntry);
        indexWriter.printf("%s:%d:%d%n", indexEntry.getKey(), indexEntry.getFilePointer().getStart(), indexEntry.getFilePointer().getEnd());
        indexWriter.flush();
    }

    public Optional<IndexEntry> find(String key) {
        assert ready;
        return Optional.ofNullable(indexEntries.get(key));
    }

    @Override
    public void close() throws IOException {
        ready = false;
        indexEntries.clear();
        if (indexWriter != null) {
            indexWriter.flush();
            indexWriter.close();
        }
    }
}
