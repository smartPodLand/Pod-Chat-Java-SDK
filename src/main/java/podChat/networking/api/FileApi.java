package podChat.networking.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import podChat.mainmodel.FileUpload;
import podChat.model.FileImageUpload;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.ArrayList;

/**
 * Created By Khojasteh on 8/25/2019
 */
public interface FileApi {
    @Multipart
    @POST("nzh/uploadFile")
    Call<FileUpload> sendFile(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") RequestBody fileName);

    @Multipart
    @POST("nzh/uploadImage")
    Call<FileImageUpload> sendImageFile(
            @Part MultipartBody.Part image
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") RequestBody fileName);

    @POST("nzh/drive/uploadFileFromUrl")
    Call<FileUpload> uploadFileFromUrl(
            @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") String fileName
            , @Part("folderHash") String folderHash
            , @Part("metadata") String metadata
            , @Part("description") String description
            , @Part("isPublic") boolean isPublic
            , @Part("tags") ArrayList<String> tags
    );

    @GET("nzh/file/")
    Call<ResponseBody> getFile
            (@Query("fileId") int fileId
                    , @Query("downloadable") boolean downloadable
                    , @Query("hashCode") String hashCode);
}
