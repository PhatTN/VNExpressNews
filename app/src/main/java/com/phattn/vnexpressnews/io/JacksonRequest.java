package com.phattn.vnexpressnews.io;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.phattn.vnexpressnews.exceptions.InternalServerErrorException;
import com.phattn.vnexpressnews.exceptions.JacksonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.phattn.vnexpressnews.util.LogUtils.LOGE;
import static com.phattn.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * A custom class of {@link Request} to parse network response
 * into Java Object by Jackson Lib
 */
public class JacksonRequest<T> extends Request<T> {
    private static final String TAG = makeLogTag(JacksonRequest.class);

    private final Map<String, String> mHeaders;
    private final Response.Listener<T> mListener;
    private final JSONHandler mHandler;

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Make a GET request and return a parsed object from JSON
     *
     * @param url URL of the request to make
     * @param headers Map of request headers
     * @param listener is a listener for the success response
     * @param errorListener is a listener for the error response
     */
    public JacksonRequest(String url, JSONHandler handler, Map<String, String> headers,
                          Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        mHandler = handler;
        mHeaders = headers;
        mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            return Response.success((T) mHandler.process(json),
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            LOGE(TAG, "Create new string with unsupported encoding error.\n" + e.getMessage());
            return Response.error(new ParseError(e));
        } catch (JacksonProcessingException e) {
            LOGE(TAG, "Cannot deserialize json result to Java Object. Error at: " + e.getLocation());
            return Response.error(new ParseError(response));
        } catch (InternalServerErrorException e) {
            LOGE(TAG, "Error when trying to map JSON to Java Object.\n" + e.getMessage());
            return Response.error(new ServerError(response));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
