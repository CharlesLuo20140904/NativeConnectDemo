package com.yzt400.yzt.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public final class IoUtil {
	public final static ByteArrayOutputStream read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = -1;
		while ((i = in.read()) != -1) {
			out.write(i);
		}
		out.flush();
		return out;
	}

	public final static void close(OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}
		} catch (Exception e) {
		}
	}

	public final static void close(InputStream in) {
		try {
			if (in != null) {
				in.close();
			}
		} catch (Exception e) {
		}
	}

	public final static void close(Reader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (Exception e) {
		}
	}

	public final static void close(Writer writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
		}
	}

}
