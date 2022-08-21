package click.techlabs.kvs.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class ResponseEncoder extends MessageToByteEncoder<Response> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) throws Exception {

        byte[] data = msg.getContent().getBytes(StandardCharsets.UTF_8);
        out.writeInt(data.length).writeBytes(data);

    }
}
