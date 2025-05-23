package com.javarush.jira.profile.internal.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.MatcherFactory;
import com.javarush.jira.common.util.JsonUtil;
import com.javarush.jira.login.internal.web.UserTestData;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.internal.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ProfileRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = ProfileRestController.REST_URL;
    private static final MatcherFactory.Matcher<ProfileTo> PROFILE_TO_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(ProfileTo.class, "id");

    @Autowired
    private ProfileRepository repository;

    @Autowired
    private ProfileMapper profileMapper;

    @Test
    @WithAnonymousUser
    void get_WhenUnauthenticated_ReturnsUnauthorized() throws Exception {
        perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void get_WhenAuthenticated_ReturnsProfileUser() throws Exception {
        perform(get(REST_URL))
                .andExpectAll(status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        PROFILE_TO_MATCHER.contentJson(USER_PROFILE_TO));
    }

    @Test
    @WithUserDetails(value = UserTestData.GUEST_MAIL)
    void get_WhenAuthenticated_ReturnsProfileGuest() throws Exception {
        perform(get(REST_URL))
                .andExpectAll(status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        PROFILE_TO_MATCHER.contentJson(GUEST_PROFILE_EMPTY_TO));
    }

    @Test
    @WithAnonymousUser
    void update_WhenUnauthenticated_ReturnsUnauthorized() throws Exception {
        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void update_WithValidData_ReturnsNoContent() throws Exception {
        ProfileTo updatedTo = getUpdatedTo();
        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void update_WithInvalidData_Returns422() throws Exception {
        ProfileTo invalidTo = getInvalidTo();
        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalidTo)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void update_WithUnknownNotification_Returns422() throws Exception {
        ProfileTo withUnknownNotificationTo = getWithUnknownNotificationTo();
        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(withUnknownNotificationTo)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void update_WithUnknownContactTo_Returns422() throws Exception {
        ProfileTo withUnknownContactTo = getWithUnknownContactTo();
        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(withUnknownContactTo)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void update_WithContactHtmlUnsafeTo_Returns422() throws Exception {
        ProfileTo withContactHtmlUnsafeTo = getWithContactHtmlUnsafeTo();
        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(withContactHtmlUnsafeTo)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void update_WithNewDataProfile() throws Exception {
        long userId = UserTestData.USER_ID;
        Profile profileToExpected = getUpdated(userId);
        ProfileTo profileTo = profileMapper.toTo(profileToExpected);

        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(profileTo)))
                .andExpect(status().isNoContent()
                );

        Profile profileToActual = repository.findById(userId).orElseThrow();
        ProfileTestData.PROFILE_MATCHER.assertMatch(profileToActual, profileToExpected);
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void create_newProfile() throws Exception {
        ProfileTo newTo = getNewTo();

        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andExpect(status().isNoContent()
                );

        long userId = UserTestData.USER_ID;
        Profile profileToActual = repository.getExisted(userId);
        Profile profileToExpected = ProfileTestData.getNew(userId);

        ProfileTestData.PROFILE_MATCHER.assertMatch(profileToActual, profileToExpected);
    }
}