package cs455.overlay.wireformats;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class ByteEncoderTest {

	@Test
	public void testStringEncoding() throws IOException {
		String testString= "Zhopa";
		while (testString.length() < 1000)
			testString = testString +testString;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteEncoder.writeEncodedString(testString, bos);
		bos.flush();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		
		String result = ByteEncoder.readEncodedString(bis);
		assert result.equals(testString);
	}

}
