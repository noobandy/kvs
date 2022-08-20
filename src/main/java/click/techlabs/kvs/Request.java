package click.techlabs.kvs;

public class Request {
    private Commands cmd;
    private String key;
    private String value;


    public Request() {
    }

    public Commands getCmd() {
        return cmd;
    }

    public void setCmd(Commands cmd) {
        this.cmd = cmd;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private boolean isValidCMD() {
        return cmd != null && cmd != Commands.UNKNOWN;
    }

    private boolean isValidInput(String input) {
        return input != null && !input.isEmpty();
    }

    public boolean isValid() {
        if (!isValidCMD()) {
            return false;
        }

        if (cmd == Commands.GET) {
            return isValidInput(key);
        }

        if (cmd == Commands.SET) {
            return isValidInput(key) && isValidInput(value);
        }

        return false;
    }
}
