package ssixprojet.server.packet;

import java.net.URI;
import java.net.URISyntaxException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import ssixproject.controller.XAtlas;

public abstract class PacketHandler<D> extends Thread {

	private class PacketHandlerChannelHandler extends ChannelInboundHandlerAdapter {
		private WebSocketClientHandshaker handshaker;

		public PacketHandlerChannelHandler(WebSocketClientHandshaker handshaker) {
			this.handshaker = handshaker;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("Sending handshake...");

			handshaker.handshake(ctx.channel());
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			if (!handshaker.isHandshakeComplete()) {
				try {
					handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
					System.out.println("WebSocket Client connected!");
					onOpen();
				} catch (WebSocketHandshakeException e) {
					System.out.println("WebSocket Client failed to connect");
				}
				return;
			}

			if (msg instanceof WebSocketFrame) {
				if (msg instanceof BinaryWebSocketFrame) {
					channelRead0(ctx, (BinaryWebSocketFrame) msg);
				} else if (msg instanceof CloseWebSocketFrame) {
					ctx.close();
				} else {
					ctx.close();
					System.out.println("Unsupported WebSocketFrame: " + msg.getClass().getCanonicalName());
				}
			}
		}

		protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) {
			PacketServer<D> packet = packetManager.buildPacket(frame);
			if (packet == null) {
				return;
			}
			xAtlas.doAction(() -> {
				// System.out.println(packet);
				try {
					packet.handle(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
		}

	}

	private PacketManager<D> packetManager;
	protected final D data;
	private XAtlas xAtlas;
	private Channel channel;
	private URI uri;

	public PacketHandler(XAtlas xAtlas, PacketManager<D> packetManager, D data) {
		this.xAtlas = xAtlas;
		this.packetManager = packetManager;
		this.data = data;
		try {
			uri = new URI("ws://" + xAtlas.config.serverHost + ":" + xAtlas.config.serverPort + "/game");
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public D getData() {
		return data;
	}

	@Override
	public void run() {
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {

			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("httpcodec", new HttpClientCodec());
							WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri,
									WebSocketVersion.V13, null, true, HttpHeaders.EMPTY_HEADERS);

							ch.pipeline().addLast(new HttpObjectAggregator(8192));
							ch.pipeline().addLast("packethandler", new PacketHandlerChannelHandler(handshaker));
						}
					});

			// Start the client.
			while (true)
				try {
					ChannelFuture f = b.connect(xAtlas.config.serverHost, xAtlas.config.serverPort).sync();

					synchronized (PacketHandler.this) {
						channel = f.channel();
					}

					// Wait until the connection is closed.
					f.channel().closeFuture().sync();
					if (!xAtlas.isStarted())
						break;
					System.out.println("Reconnexion");
					Thread.sleep(5_000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

		} finally {
			workerGroup.shutdownGracefully();
		}
	}

	public abstract void onOpen();

	public synchronized void sendPacket(PacketClient packet) {
		if (channel == null)
			return;
		// System.out.println(packet);
		ByteBuf buffer = Unpooled.buffer(packet.getInitialSize() + 4);
		buffer.writeInt(packet.getPacketId());
		packet.write(buffer);
		BinaryWebSocketFrame frame = new BinaryWebSocketFrame(buffer);
		channel.writeAndFlush(frame);
	}

}
