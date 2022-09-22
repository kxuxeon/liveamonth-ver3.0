package teamproject.lam_server.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamproject.lam_server.domain.member.dto.editor.PasswordEditor;
import teamproject.lam_server.domain.member.dto.editor.ProfileEditor;
import teamproject.lam_server.domain.member.dto.request.*;
import teamproject.lam_server.domain.member.dto.response.FormCheckResponse;
import teamproject.lam_server.domain.member.dto.response.FindIdResponse;
import teamproject.lam_server.domain.member.dto.response.MemberProfileResponse;
import teamproject.lam_server.domain.member.dto.response.SimpleProfileResponse;
import teamproject.lam_server.domain.member.service.MemberService;
import teamproject.lam_server.global.dto.CustomResponse;
import teamproject.lam_server.global.dto.PostIdResponse;

import javax.validation.Valid;

import static teamproject.lam_server.global.constants.ResponseMessage.*;
import static teamproject.lam_server.util.JwtUtil.extractAccessToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberApiController {
    private final MemberService memberService;

    /**
     * presentation layer::home, sign up
     * -> user sign up
     */
    @PostMapping("/sign-up")
    public ResponseEntity<?> joinUser(@Valid @RequestBody SignUpRequest request) {
        PostIdResponse result = memberService.signUp(request);
        return CustomResponse.success(CREATED_MEMBER, result);
    }

    @PostMapping("/reconfirm")
    public ResponseEntity<?> reconfirm(
            @Valid @RequestBody ReconfirmRequest request,
            @RequestHeader(value = "Authorization") String accessTokenRequest) {
        FormCheckResponse result = memberService.reconfirm(extractAccessToken(accessTokenRequest), request);
        return CustomResponse.success(RECONFIRM, result);
    }

    /**
     * presentation layer::login
     * -> find user login id
     */
    @PostMapping("/find-id")
    public ResponseEntity<?> findLoginId(@Valid @RequestBody FindIdRequest request) {
        FindIdResponse result = memberService.findLoginId(request);
        return CustomResponse.success(FIND_MEMBER_LOGIN_ID, result);
    }

    /**
     * presentation layer::login
     * -> find user password
     */
    @PostMapping("/find-pw")
    public ResponseEntity<?> findPassword(@Valid @RequestBody FindPasswordRequest request) {
        memberService.findPassword(request);
        return CustomResponse.success(FIND_MEMBER_PASSWORD);
    }

    /**
     * presentation layer::my page
     * -> modify user information
     */
    @PatchMapping("/profile")
    public ResponseEntity<?> editProfile(
            @RequestHeader(value = "Authorization") String accessTokenRequest,
            @Valid @RequestBody ProfileEditor request) {
        memberService.editProfile(extractAccessToken(accessTokenRequest), request);
        return CustomResponse.success(UPDATE_MEMBER);
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestHeader(value = "Authorization") String accessTokenRequest,
            @Valid @RequestBody PasswordEditor request) {
        memberService.changePassword(extractAccessToken(accessTokenRequest), request);
        return CustomResponse.success(UPDATE_MEMBER);
    }

    /**
     * presentation layer::my page
     * -> drop user(customer)
     */
    @PostMapping("/drop")
    public ResponseEntity<?> dropUser(@RequestHeader(value = "Authorization") String accessTokenRequest) {
        memberService.dropUser(extractAccessToken(accessTokenRequest));
        return CustomResponse.success(DROP_MEMBER);
    }

    /**
     * presentation layer::sign up
     * -> login id duplicate check
     */
    @GetMapping("/exists/loginId/{loginId}")
    public ResponseEntity<?> duplicateCheckLoginId(@PathVariable String loginId) {
        FormCheckResponse result = memberService.checkDuplicateLoginId(loginId);
        return CustomResponse.success(DUPLICATE_CHECK, result);
    }

    /**
     * presentation layer::sign up
     * -> email(id & domain) duplicate check
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<?> duplicateCheckEmail(@PathVariable String email) {
        FormCheckResponse result = memberService.checkDuplicateEmail(email);
        return CustomResponse.success(DUPLICATE_CHECK, result);
    }

    /**
     * presentation layer::sign up
     * -> nickname duplicate check
     */
    @GetMapping("/exists/nickname/{nickname}")
    public ResponseEntity<?> duplicateCheckNickname(@PathVariable String nickname) {
        FormCheckResponse result = memberService.checkDuplicateNickname(nickname);
        return CustomResponse.success(DUPLICATE_CHECK, result);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getMember(@RequestHeader("Authorization") String token) {
        MemberProfileResponse result = memberService.getMember(extractAccessToken(token));
        return CustomResponse.success(READ_MEMBER, result);
    }

    @GetMapping("/profile/simple")
    public ResponseEntity<?> getSimpleProfile(@RequestHeader("Authorization") String token) {
        SimpleProfileResponse result = memberService.getSimpleProfile(extractAccessToken(token));
        return CustomResponse.success(READ_MEMBER, result);
    }

//    @PostMapping("/editProfileImage")
//    public String editProfileImage(@SessionAttribute(name = SessionConstants.LOGIN_USER, required = false) User loginUser, @RequestPart(FILE_NAME) MultipartFile mFile) throws Exception {
//        log.info("fileNAme = {}",mFile.getOriginalFilename());
//        if (loginUser.getImage() != null) {
//            log.info("UserImageName = {}",loginUser.getImage());
//            uploader.delete(PROFILE_IMAGE_DIR + loginUser.getImage());
//        }
//        String saveName = uploader.uploadProfile(loginUser.getLoginId(), PROFILE_IMAGE_DIR, mFile.getOriginalFilename(), mFile.getBytes());
//        userWebService.editUserImage(loginUser.getId(), saveName);
//
//        return RE_DIRECT_DIR+MY_PAGE;
//    }

}
