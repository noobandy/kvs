package click.techlabs.kvs.server;


import click.techlabs.kvs.db.DB;
import click.techlabs.kvs.db.DBException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Server {

    private final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private final int port;
    private Channel channel;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private boolean running = false;
    private DB db;

    public Server(int port) {
        this.port = port;
    }

    public void start(ChannelFutureListener onStart) throws DBException {
        running = true;
        //TODO: Externalize
        db = new DB("file:/Users/name/Downloads/kvs/src/main/resources/kvs-db");
        ServerBootstrap sb = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        sb.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RequestDecoder(), new ResponseEncoder(), new RequestHandler(db));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 10)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture f = sb.bind(port).addListener(onStart);
        channel = f.channel();


    }

    public void stop(final ChannelFutureListener onStop) throws IOException {
        if (running) {
            running = false;
            db.close();
            channel.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                    channel = null;
                    bossGroup = null;
                    workerGroup = null;
                    onStop.operationComplete(future);
                }
            });

        } else {
            throw new RuntimeException("Server not running or stop in progress");
        }


    }
}
