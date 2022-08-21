package click.techlabs.kvs.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RequestDecoder extends ByteToMessageDecoder {
    private static final int CMD_FLAG_SIZE = 1;
    private static final int INT_SIZE = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < CMD_FLAG_SIZE + INT_SIZE) {
            return;
        }

        byte cmd = in.getByte(in.readerIndex());
        int keyLength = in.getInt(in.readerIndex() + CMD_FLAG_SIZE);

        if (in.readableBytes() < (CMD_FLAG_SIZE + INT_SIZE + keyLength)) {
            return;
        }

        if (cmd == Commands.SET.getFlag() && in.readableBytes() < (CMD_FLAG_SIZE + INT_SIZE + keyLength + INT_SIZE)) {
            return;
        }

        if (cmd == Commands.SET.getFlag()) {
            int valueLength = in.getInt(in.readerIndex() + CMD_FLAG_SIZE + INT_SIZE + keyLength);
            if (in.readableBytes() < (CMD_FLAG_SIZE + INT_SIZE + keyLength + INT_SIZE + valueLength)) {
                return;
            }
        }

        // cmd is either GET or SET, and we have all the data


        if (cmd == Commands.GET.getFlag()) {
            Request r = new Request();
            r.setCmd(Commands.fromProtocolFlag(in.readByte()));

            byte[] keyData = new byte[in.readInt()];
            in.readBytes(keyData);
            r.setKey(new String(keyData));

            out.add(r);
        }

        if (cmd == Commands.SET.getFlag()) {
            Request r = new Request();
            r.setCmd(Commands.fromProtocolFlag(in.readByte()));

            byte[] keyData = new byte[in.readInt()];
            in.readBytes(keyData);
            r.setKey(new String(keyData));

            byte[] valueData = new byte[in.readInt()];
            in.readBytes(valueData);
            r.setValue(new String(valueData));

            out.add(r);
        }

    }
}
