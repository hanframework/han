package org.hanframework.web.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;

/**
 * @author liuxin
 * @version Id: WebScoketConverter.java, v 0.1 2019-06-11 14:08
 */
public class WebSocketConverter {

    private static final byte OPCODE_CONT = 0x0;
    private static final byte OPCODE_TEXT = 0x1;
    private static final byte OPCODE_BINARY = 0x2;
    private static final byte OPCODE_CLOSE = 0x8;
    private static final byte OPCODE_PING = 0x9;
    private static final byte OPCODE_PONG = 0xA;


    public static boolean match(Object message) {
        return message instanceof WebSocketFrame;
    }


    public static void clearClientData(WebSocketSession webSocketSession) {
        webSocketSession.clearClientData();
    }

    public static WebSocketFrame buildWebSocketFrame(WebSocketSession webSocketSession) {
        WebSocketFrame wsf;
        WebSocketSession.Type type = webSocketSession.getType();
        clearClientData(webSocketSession);
        switch (type) {
            case TEXT:
                wsf = new TextWebSocketFrame(webSocketSession.content());
                break;
            case BYTE:
                byte[] data = webSocketSession.getData();
                ByteBuf buf = Unpooled.buffer(data.length);
                ByteBuf byteBuf = buf.writeBytes(data);
                wsf = new BinaryWebSocketFrame(byteBuf);
                break;
            case PING:
                wsf = new PingWebSocketFrame();
                break;
            case PONG:
                wsf = new PongWebSocketFrame();
                break;
            case CLOSE:
                wsf = new CloseWebSocketFrame();
                break;
            case CONT:
                byte[] bigData = webSocketSession.getData();
                ByteBuf bigDataBuf = Unpooled.buffer(bigData.length);
                ByteBuf bigByteBuf = bigDataBuf.writeBytes(bigData);
                wsf = new ContinuationWebSocketFrame(bigByteBuf);
                break;
            default:
                throw new UnsupportedOperationException("Cannot encode frame of type: " + type);
        }
        return wsf;
    }

    public static WebSocketSession buildWebSocketSession(Object msg) {
        //websocket还是属于request
        final FullHttpRequest req = (FullHttpRequest) msg;
        String websocketPath = req.uri();
        byte opcode;
        if (msg instanceof TextWebSocketFrame) {
            opcode = OPCODE_TEXT;
        } else if (msg instanceof PingWebSocketFrame) {
            opcode = OPCODE_PING;
        } else if (msg instanceof PongWebSocketFrame) {
            opcode = OPCODE_PONG;
        } else if (msg instanceof CloseWebSocketFrame) {
            opcode = OPCODE_CLOSE;
        } else if (msg instanceof BinaryWebSocketFrame) {
            opcode = OPCODE_BINARY;
        } else if (msg instanceof ContinuationWebSocketFrame) {
            opcode = OPCODE_CONT;
        } else {
            throw new UnsupportedOperationException("Cannot encode frame of type: " + msg.getClass().getName());
        }
        WebSocketFrame wsf = (WebSocketFrame) msg;
        WebSocketSession ws;
        byte[] data = new byte[wsf.content().readableBytes()];
        wsf.content().writeBytes(data);
        //丢弃已读取数据
        wsf.content().discardReadBytes();
        switch (opcode) {
            case OPCODE_BINARY:
                ws = new WebSocketSession(WebSocketSession.Type.BYTE, data, websocketPath);
                break;
            case OPCODE_TEXT:
                ws = new WebSocketSession(WebSocketSession.Type.TEXT, data, websocketPath);
                break;
            case OPCODE_CLOSE:
                ws = new WebSocketSession(WebSocketSession.Type.CLOSE, data, websocketPath);
                break;
            case OPCODE_PING:
                ws = new WebSocketSession(WebSocketSession.Type.PING, data, websocketPath);
                break;
            case OPCODE_PONG:
                ws = new WebSocketSession(WebSocketSession.Type.PONG, data, websocketPath);
                break;
            case OPCODE_CONT:
                ws = new WebSocketSession(WebSocketSession.Type.CONT, data, websocketPath);
                break;
            default:
                throw new UnsupportedOperationException("Cannot encode frame of type: " + msg.getClass().getName());
        }
        return ws;
    }
}
