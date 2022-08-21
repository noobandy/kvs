package click.techlabs.kvs.db;

import java.util.Objects;

class IndexEntry implements Comparable<IndexEntry> {
    private String key;
    private FilePointer filePointer;

    public IndexEntry(String key, FilePointer filePointer) {
        this.key = key;
        this.filePointer = filePointer;
    }

    public String getKey() {
        return key;
    }

    public FilePointer getFilePointer() {
        return filePointer;
    }

    @Override
    public int compareTo(IndexEntry o) {
        return key.compareTo(o.key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexEntry that = (IndexEntry) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
