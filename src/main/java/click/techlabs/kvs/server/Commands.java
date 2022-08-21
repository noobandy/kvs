package click.techlabs.kvs.server;

public enum Commands {


    GET((byte) 1), SET((byte) 2), UNKNOWN((byte) -1);
    private byte flag;

    private Commands(byte flag) {
        this.flag = flag;
    }


    public static Commands fromProtocolFlag(byte f) {
        switch (f) {
            case 1:
                return GET;
            case 2:
                return SET;
            default:
                return UNKNOWN;
        }
    }

    public byte getFlag() {
        return flag;
    }
}
