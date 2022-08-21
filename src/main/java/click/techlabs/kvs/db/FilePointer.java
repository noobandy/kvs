package click.techlabs.kvs.db;

class FilePointer {

    private long start;
    private long end;

    public FilePointer(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
}
