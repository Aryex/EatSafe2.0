package ca.cmpt276.restauranthealthinspection.model.updater;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Source: https://stackoverflow.com/questions/42118924/android-retrofit-download-progress
 * Get progress in downloading files, does not work for now due to content length not being
 * transmitted by server reliably
 */

public class RestaurantsResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private DownloadListener downloadListener;
    private BufferedSource bufferedSource;

    public RestaurantsResponseBody(ResponseBody responseBody, DownloadListener downloadListener) {
        this.responseBody = responseBody;
        this.downloadListener = downloadListener;
    }
    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @NotNull
    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {

            long totalRead = 0L;

            @Override
            public long read(@NotNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                float percent;

                if (bytesRead != -1) {
                    totalRead += bytesRead;
                }

                if (bytesRead == -1) {
                    percent = 100f;
                } else {
                    //set percent between 0 and 100
                    percent = (((float) totalRead / contentLength()) * 100);

                    Log.d("test", totalRead + " and " + contentLength());
                }

                if (downloadListener != null) {
                    downloadListener.downloadUpdate((int) percent);
                }

                return bytesRead;
            }
        };
    }
}
