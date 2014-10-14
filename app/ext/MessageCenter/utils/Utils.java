package ext.MessageCenter.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	private final static Logger logger = LoggerFactory
			.getLogger(Utils.class);
	
	public static byte[] signed64ToBytes(long value) {
		return toBytes(value, 8);
	}

	public static byte[] unsigned48ToBytes(long value) {
		return toBytes(value, 6);
	}

	public static byte[] unsigned32ToBytes(long value) {
		return toBytes(value, 4);
	}

	public static byte[] unsigned16ToBytes(int value) {
	    if (value > 65535) {
	        throw new RuntimeException("value " + value + " is too big. value(unsigned16) should <= 65535");
	    }
		return toBytes(value, 2);
	}

	public static byte[] unsigned8ToBytes(int value) {
		return toBytes(value, 1);
	}

	public static byte[] toBytes(long value, int len) {
		byte[] array = new byte[len];
		for (int i = 0; i < len; i++) {
			array[i] = (byte) (value >>> ((len - i - 1) * 8));
		}
		return array;
	}

	public static long fromBytes(byte[] bytes, int len) {
		long v = 0;
		for (int i = 0; i < len; i++) {
			v += (long) (bytes[i] & 255) << (len - i - 1) * 8;
		}
		return v;
	}

	public static short from8Unsigned(byte[] bytes) {
		return (short) fromBytes(bytes, 1);
	}

	public static int from16Unsigned(byte[] bytes) {
		return (int) fromBytes(bytes, 2);
	}

	public static long from32Unsigned(byte[] bytes) {
		return fromBytes(bytes, 4);
	}

	public static long from48Unsigned(byte[] bytes) {
		return fromBytes(bytes, 6);
	}

	public static long from64(byte[] bytes) {
		return fromBytes(bytes, 8);
	}

	public static int getUTF8StringLength(String str) {
		if (str == null) {
			return 0;
		}
		return str.getBytes(CharsetUtil.UTF_8).length;
	}
	

	public static void close(final ChannelHandlerContext ctx) {
		if (ctx.channel().isOpen() || ctx.channel().isActive()) {
			logger.debug("close channel {} ", ctx.channel().remoteAddress());
			ctx.close().addListener(new ChannelFutureListener() {
	
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					close(ctx);
				}
			});
		}
	}
	public static String convertBytesToUUID(byte[] bytes){
		ByteBuffer bb = ByteBuffer.wrap(bytes);   
		long firstLong = bb.getLong();
		long secondLong = bb.getLong();
		return new UUID(firstLong, secondLong).toString();
	}
	public static byte[] getIdAsByte(String messageId)
	{
	//	UUID uuid = UUID.randomUUID();
		UUID uuid = UUID.fromString(messageId);
	    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
	    bb.putLong(uuid.getMostSignificantBits());
	    bb.putLong(uuid.getLeastSignificantBits());
	    return bb.array();
	}
	
	
	public static void main(String[] args) {
		long l = 234567890123456L;
		byte[] bytes = unsigned48ToBytes(l);
		for (int i = 0; i < bytes.length; i++) {
			System.out.print(Integer.toBinaryString(((int)bytes[i] & 255)) + " ");
		}
		System.out.println();
		System.out.println(from48Unsigned(bytes));
		System.out.println(Byte.MAX_VALUE);
		System.out.println(Byte.MIN_VALUE);
//		11010101 01010110 10010111 11000100 00111010 11000000
//		11010101  1010110 10010111 11000100   111010 11000000 
		
		System.out.println(Integer.MAX_VALUE);
	}
	
	
}
