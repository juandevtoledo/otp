package com.lulobank.otp.starter.adapters.out.pushnotifications;

import com.lulobank.otp.sdk.dto.events.NewPushMessageNotification;
import com.lulobank.otp.services.ports.out.PushNotificationService;
import com.lulobank.otp.starter.adapters.out.pushnotifications.dto.FirebaseResponse;
import com.lulobank.otp.starter.adapters.out.pushnotifications.dto.Result;
import io.vavr.API;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.json.JSONObject;
import org.owasp.encoder.Encode;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.util.HashMap;
import java.util.Map;

import static com.lulobank.otp.sdk.dto.notifications.DeviceOs.ANDROID;
import static com.lulobank.otp.sdk.dto.notifications.DeviceOs.IOS;
import static io.vavr.API.$;
import static io.vavr.API.Case;

@Slf4j
public class PushNotificationsServiceAdapter implements PushNotificationService {

    private static final String TO = "to";
    private static final String NOTIFICATION = "notification";
    private static final String DATA = "data";
    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String ID_NOTIFICATION = "idNotification";
    private static final String ID_CLIENT = "idClient";
    private static final String ACTION = "action";
    private static final String CLICK_ACTION = "click_action";
    private static final String CONTENT_AVAILABLE = "content-available";
    private static final String MUTABLE_CONTENT = "mutable-content";
    private static final String PRIORITY = "priority";

    private final IFirebaseService service;
    private Map<String, String> headers;

    public PushNotificationsServiceAdapter(String firebaseUrlPush, String firebaseApiKey) {
        Retrofit retrofit = buildRetrofit(firebaseUrlPush);
        this.service = retrofit.create(IFirebaseService.class);
        headers = new HashMap<>();
        headers.put("Authorization", "key=" + firebaseApiKey);
    }

    @Override
    public void sendPushNotification(NewPushMessageNotification messageNotification) {
        Call<FirebaseResponse> call = this.service.sendPushNotification(
                headers, generateNotificationMessage(messageNotification));
        Try.of(call::execute)
                .onSuccess(response -> handleResultFirebase(response, messageNotification))
                .onFailure(e -> log.error("Error sending push notification. Error: {}, {}", e.getMessage(), e));
    }

    private void handleResultFirebase(Response<FirebaseResponse> firebaseResponse,
                                      NewPushMessageNotification messageNotification) {

        Option.of(firebaseResponse)
                .filter(Response::isSuccessful)
                .map(Response::body)
                .peek(response -> {
                    if (Boolean.TRUE.equals(response.getSuccess())) {
                        log.info("Push notification sent to client {}",  Encode.forJava(messageNotification.getIdClient()));
                    } else {
                        log.error("Push notification not sent to client {}, Error {}", Encode.forJava(messageNotification.getIdClient()),
                                response.getResults().stream().findFirst().map(Result::getError).orElse("Unknown error."));
                    }
                })
                .onEmpty(() -> log.error("Error firebase service. Code {}", firebaseResponse.code()));

    }

    private String generateNotificationMessage(NewPushMessageNotification messageNotification) {
        return API.Match(messageNotification.getOsDevice().toUpperCase(LocaleUtils.toLocale("es_CO"))).of(
                Case($(ANDROID.name()), () -> getMessageANDROID(messageNotification)),
                Case($(IOS.name()), () -> getMessageIOS(messageNotification))
        );
    }

    private String getMessageIOS(NewPushMessageNotification messageNotification) {

        JSONObject gcmData = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();

        gcmData.put(TO, messageNotification.getArnSns());

        notification.put(TITLE, messageNotification.getTitle());
        notification.put(BODY, messageNotification.getMessageBody());
        notification.put(CLICK_ACTION, messageNotification.getAction());

        data.put(ID_NOTIFICATION, messageNotification.getIdNotification());
        data.put(ID_CLIENT, messageNotification.getIdClient());

        gcmData.put(NOTIFICATION, notification);
        gcmData.put(DATA, data);

        gcmData.put(CONTENT_AVAILABLE, true);
        gcmData.put(MUTABLE_CONTENT, true);
        gcmData.put(PRIORITY, "high");


        return gcmData.toString();

    }

    private String getMessageANDROID(NewPushMessageNotification messageNotification) {
        JSONObject gcmData = new JSONObject();
        gcmData.put(TO, messageNotification.getArnSns());
        JSONObject data = new JSONObject();
        data.put(ACTION, messageNotification.getAction());
        data.put(TITLE, messageNotification.getTitle());
        data.put(BODY, messageNotification.getMessageBody());
        data.put(ID_CLIENT, messageNotification.getIdClient());
        data.put(ID_NOTIFICATION, messageNotification.getIdNotification());
        gcmData.put(DATA, data);


        return gcmData.toString();
    }

    private Retrofit buildRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }


    private interface IFirebaseService {
        @POST("fcm/send")
        @Headers({"Content-Type: application/json"})
        Call<FirebaseResponse> sendPushNotification(@HeaderMap Map<String, String> headers, @Body String body);
    }

}
