package podChat.networking.api;


import podChat.mainmodel.SearchContactVO;
import podChat.mainmodel.UpdateContact;
import podChat.model.AddContacts;
import podChat.model.ContactRemove;
import podChat.model.Contacts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.ArrayList;

public interface ContactApi {

    @POST("nzh/addContacts")
    @FormUrlEncoded
    Call<Response<Contacts>> addContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber);

    @POST("nzh/addContacts")
    @FormUrlEncoded
    Call<Response<AddContacts>> addContacts(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") ArrayList<String> firstName
            , @Field("lastName") ArrayList<String> lastName
            , @Field("email") ArrayList<String> email
            , @Field("uniqueId") ArrayList<String> uniqueId
            , @Field("cellphoneNumber") ArrayList<String> cellphoneNumber);

    @POST("nzh/removeContacts")
    @FormUrlEncoded
    Call<Response<ContactRemove>> removeContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long userId);

    @POST("nzh/updateContacts")
    @FormUrlEncoded
    Call<Response<UpdateContact>> updateContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long id
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber);

    @GET("nzh/listContacts")
    Call<Response<SearchContactVO>> searchContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Query("id") String id
            , @Query("firstName") String firstName
            , @Query("lastName") String lastName
            , @Query("email") String email
            , @Query("uniqueId") String uniqueId
            , @Query("offset") String offset
            , @Query("size") String size
            , @Query("typeCode") String typeCode
            , @Query("q") String query
            , @Query("cellphoneNumber") String cellphoneNumber);
}
