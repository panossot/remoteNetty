package org.jboss.additional.testsuite.jdkall.present.others;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.junit.Test;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/others/src/main/java","modules/testcases/jdkAll/Eap7/others/src/main/java","modules/testcases/jdkAll/Eap72x/others/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/others/src/main/java"})
public class NettyHeaderTest {

    @Test
    public void testNettyHeader() {
        String requestStr = "POST / HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Connection: close\r\n" +
                "Content-Length: 5\r\n" +
                "Content-Length:\r\n" +
                "\t6\r\n\r\n" +
                "123";
        EmbeddedChannel channel = new EmbeddedChannel(new HttpRequestDecoder());
        assertTrue(channel.writeInbound(Unpooled.copiedBuffer(requestStr, CharsetUtil.US_ASCII)));

        HttpRequest request = channel.readInbound();
        assertTrue(request.decoderResult().isFailure());
        assertTrue(request.decoderResult().cause() instanceof IllegalArgumentException);

        String requestStr2 = "POST / HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Connection: close\r\n" +
                "Content-Length: 5\r\n" +
                "Transfer-Encoding: chunked\r\n\r\n" +
                "0\r\n\r\n";

        assertTrue(channel.writeInbound(Unpooled.copiedBuffer(requestStr2, CharsetUtil.US_ASCII)));

        HttpRequest request2 = channel.readInbound();
        assertTrue(request2.decoderResult().isFailure());
        assertTrue(request2.decoderResult().cause() instanceof IllegalArgumentException);

        assertFalse(channel.finish());
    }

}
