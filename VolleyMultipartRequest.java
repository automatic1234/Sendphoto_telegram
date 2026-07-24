package your.package.name;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public abstract class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String lineEnd = "\r\n";
    private final String twoHyphens = "--";

    public VolleyMultipartRequest(
            int method,
            String url,
            Response.Listener<NetworkResponse> listener,
            Response.ErrorListener errorListener) {

        super(method, url, errorListener);
        this.mListener = listener;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    protected abstract Map<String, DataPart> getByteData();

    @Override
    public byte[] getBody() throws AuthFailureError {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {

            Map<String, String> params = getParams();

            if (params != null && params.size() > 0) {
                textParse(bos, params);
            }

            Map<String, DataPart> data = getByteData();

            if (data != null && data.size() > 0) {
                dataParse(bos, data);
            }

            bos.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bos.toByteArray();


          private void textParse(ByteArrayOutputStream bos,
                           Map<String, String> params) throws IOException {

        try {

            for (Map.Entry<String, String> entry : params.entrySet()) {

                bos.write((twoHyphens + boundary + lineEnd).getBytes());

                bos.write(("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\""
                        + lineEnd).getBytes());

                bos.write(("Content-Type: text/plain; charset=UTF-8"
                        + lineEnd).getBytes());

                bos.write(lineEnd.getBytes());

                bos.write(entry.getValue().getBytes("UTF-8"));

                bos.write(lineEnd.getBytes());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dataParse(ByteArrayOutputStream bos,
                           Map<String, DataPart> data)
            throws IOException {

        try {

            for (Map.Entry<String, DataPart> entry : data.entrySet()) {

                DataPart dataFile = entry.getValue();

                bos.write((twoHyphens + boundary + lineEnd).getBytes());

                bos.write(("Content-Disposition: form-data; name=\""
                        + entry.getKey()
                        + "\"; filename=\""
                        + dataFile.getFileName()
                        + "\""
                        + lineEnd).getBytes());

                if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {

                    bos.write(("Content-Type: "
                            + dataFile.getType()
                            + lineEnd).getBytes());

                }

                bos.write(lineEnd.getBytes());

                bos.write(dataFile.getContent());

                bos.write(lineEnd.getBytes());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(
                response,
                HttpHeaderParser.parseCacheHeaders(response)
        );
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }
}

      
      
    }
