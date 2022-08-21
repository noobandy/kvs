package click.techlabs.kvs.server;


import click.techlabs.kvs.db.DB;
import click.techlabs.kvs.db.DBException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends ChannelInboundHandlerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    private DB db;

    public RequestHandler(DB db) {
        this.db = db;
    }

    public Response handle(Request r) throws DBException {
        LOGGER.info("Processing a new request");
        String content = null;
        if (r.isValid()) {

            switch (r.getCmd()) {
                case SET:
                    LOGGER.info("Processing request command {} with key {} and value {} ", r.getCmd(), r.getKey(), r.getValue());
                    db.set(r.getKey(), r.getValue());
                    break;
                case GET:
                    LOGGER.info("Processing request command {} with key {} ", r.getCmd(), r.getKey());
                    content = db.get(r.getKey()).orElse(null);
            }
        } else {
            LOGGER.error("Bad Request {}", r);
            content = "Bad Request";
        }

        Response response = new Response();
        response.setContent(content == null ? "" : content);
        LOGGER.info("Returning response {} ", response.getContent());
        return response;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            Request r = (Request) msg;
            Response response = handle(r);
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        LOGGER.info("Response written successfully");
                    } else {
                        LOGGER.error("Error writing response");
                    }
                }
            });
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }
}
