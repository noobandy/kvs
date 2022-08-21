package click.techlabs.kvs.db;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

public class DB implements Closeable {

    private static final String INDEX_FILE_NAME = "db.index";
    private static final String DATA_FILE_NAME = "db.data";

    private Index index;
    private RandomAccessFile dataFile;

    private boolean ready = false;

    public DB(String dbDir) throws DBException {
        File dbDirF = initDir(dbDir);
        dataFile = initDataFile(dbDirF);
        index = initIndex(dbDirF);
        ready = true;
    }

    private Index initIndex(File dbDirF) throws DBException {
        Path indexPath = Path.of(dbDirF.getAbsolutePath(), INDEX_FILE_NAME);
        File indexFile = indexPath.toFile();
        if (!(indexFile.exists() && indexFile.isFile())) {
            try {
                boolean created = indexFile.createNewFile();
                assert created == true;
            } catch (IOException e) {
                //TODO: LOG
                throw new DBException(e);
            }

        }

        Index index = new Index();
        index.init(indexFile);
        return index;
    }

    private RandomAccessFile initDataFile(File dbDirF) throws DBException {
        Path dataFilePath = Path.of(dbDirF.getAbsolutePath(), DATA_FILE_NAME);
        File dataFile = dataFilePath.toFile();
        if (!(dataFile.exists() && dataFile.isFile())) {
            try {
                boolean created = dataFile.createNewFile();
                assert created == true;
            } catch (IOException e) {
                //TODO: LOG
                throw new DBException(e);
            }

        }

        try {
            RandomAccessFile raf = new RandomAccessFile(dataFile, "rwd");
            raf.seek(raf.length());
            return raf;
        } catch (IOException e) {
            //TODO: LOG
            throw new DBException(e);
        }
    }

    private File initDir(String dbDir) throws DBException {
        Path dbPath = Path.of(URI.create(dbDir));
        File dbDirF = dbPath.toFile();
        if (dbDirF.exists() && dbDirF.isDirectory()) {
            return dbDirF;
        }

        //TODO: LOG
        throw new DBException("DB Directory does not exists");
    }

    public void set(String key, String value) throws DBException {
        assert ready;
        try {
            FilePointer fp  = write(value);
            index.insert(new IndexEntry(key, fp));
        } catch (IOException e) {
            // TODO: LOG
            new DBException(e);
        }

    }

    public Optional<String> get(String key) throws DBException {
        assert ready;
        Optional<IndexEntry> indexEntry = index.find(key);
        if (indexEntry.isPresent()) {
            try {
                return Optional.of(read(indexEntry.get().getFilePointer()));
            } catch (IOException e) {
                //TODO: LOG
                throw new DBException(e);
            }
        }

        return Optional.empty();
    }

    private String read(FilePointer filePointer) throws IOException {
        int dataSize = (int) (filePointer.getEnd() - filePointer.getStart());
        byte[] bytes = new byte[dataSize];
        dataFile.seek(filePointer.getStart());
        dataFile.readFully(bytes);

        return new String(bytes);
    }

    private FilePointer write(String value) throws IOException {
        byte[] valueData = value.getBytes();
        long start = dataFile.getFilePointer();
        long end = start+valueData.length;
        dataFile.write(valueData);
        return new FilePointer(start, end);
    }

    @Override
    public void close() throws IOException {
        ready = false;

        if (index != null) {
            index.close();
        }

        if (dataFile != null) {
            dataFile.close();
        }
    }
}
