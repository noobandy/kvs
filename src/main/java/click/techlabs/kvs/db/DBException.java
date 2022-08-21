package click.techlabs.kvs.db;

public class DBException extends Exception {

    public DBException(Exception e) {
        super(e);
    }

    public DBException(String message) {
        super(message);
    }
}
