package click.techlabs.kvs.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestEncoder extends MessageToByteEncoder<Request> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {
        byte cmdFlag = msg.getCmd().getFlag();
        byte[] keyData = msg.getKey().getBytes();

        out.writeByte(cmdFlag).writeInt(keyData.length).writeBytes(keyData);
        if (msg.getCmd() == Commands.SET) {
            byte[] valueData = msg.getValue().getBytes();
            out.writeInt(valueData.length).writeBytes(valueData);
        }

    }
}
